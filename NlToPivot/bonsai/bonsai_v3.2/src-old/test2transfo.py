#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#

import re
import os
import sys

from PivotReader import *
from dgraph import DepEdge

fichier = sys.argv[1]


##############
# SORTIE XML #
##############

def sortie_easy_xml_dependances(dgraph):
	s="<relations>\n"

###############
# MANIP EDGES #
###############	

def get_deps(index,edges):
	nedges = set([])
	for ne in edges:
		if str(ne.orig.idx) == str(index) and ne.label != "ponct":
			nedges.add(ne)
	return nedges
	
# ne plus utiliser des indices dans les listes et les dico dans les methodes apres
# plutot utiliser l'objet depvertex directement
# et donc nutiliser get_vertex que dans les cas relous
def get_vertex(idx,vertices):
	for v in vertices:
		if str(v.idx) == str(idx):
			return v
	
#########
# PRINT #
#########

def printl(dgraph):
	s="\nsurf_deps(\n"
	for edge in dgraph.edges:
		s+="\t"+edge.label+"("+edge.orig.label+"~"+edge.orig.subcat+"~"+str(edge.orig.idx)+","+edge.dest.label+"~"+edge.dest.subcat+"~"+str(edge.dest.idx)+")\n"    
	s+=")"
	print s

def str_vertex(vertex):
	return vertex.label+"~"+vertex.subcat+"~"+str(vertex.idx)
	
def str_edge(edge):
	return edge.label+"("+str_vertex(edge.orig)+","+str_vertex(edge.dest)+")"

##########
# CHUNKS #
##########

liste_chunk = {}

def affiche_liste_chunks(dgraph):
	liste=[]
	for v in dgraph.vertices:
		liste.append(int(v.idx))
	liste.sort()
	for i in liste:
		i=str(i)
		if liste_chunk.has_key(i):			
			print "["+str_vertex(get_vertex(i,dgraph.vertices))+"] \t chunk:"+liste_chunk[i]['chunk']+" \t head:"+str(liste_chunk[i]['head'])+" \t neg:"+str(liste_chunk[i]['neg'])
		else:
			print "["+str_vertex(get_vertex(i,dgraph.vertices))+"]"

chunks={}

def affiche_chunks(dgraph):
	liste=[]
	for v in dgraph.vertices:
		liste.append(int(v.idx))
	liste.sort()
	for i in liste:
		i=str(i)
		if chunks.has_key(i):
			print str_vertex(get_vertex(i,dgraph.vertices))+" \t>\t "+str(chunks[i])
		else:
			pass
	
def build_chunks(dgraph):
	liste=[]
	for v in dgraph.vertices:
		liste.append(int(v.idx))
	liste.sort()
	chunk="G"
	nb_chunk=0
	treenum = dgraph.treenum
	liste_chunk[str(-1)]={}
	liste_chunk[str(-1)]['head']=0
	sortie_xml=""
	for li in liste:
		li=str(li)
		if int(li) != -1:
			if liste_chunk.has_key(li):
				print "====================="+li
				print str_vertex(get_vertex(li,dgraph.vertices))+"\nanc:"+chunk+"	- nv:"+liste_chunk[li]['chunk']+"	- hd:"+str(liste_chunk[li]['head'])

				if liste_chunk[li]['chunk'] !="":
					if (not liste_chunk[li]['chunk'] == "_" and not chunk == liste_chunk[li]['chunk']): 
						print ">changement de cat:"+str(nb_chunk)+" -> "+str(nb_chunk+1)
						nb_chunk = nb_chunk+1
						if int(li) != 0:
							sortie_xml+="</Groupe>\n"
						sortie_xml+="<Groupe type=\""+liste_chunk[li]['chunk']+"\" id=\"E"+str(treenum)+"G"+str(nb_chunk)+"\">\n"
					elif (liste_chunk[str(int(li)-1)]['head']==2 and liste_chunk[li]['head']!=2):
						print ">apres tete-clit:"+str(nb_chunk)+" -> "+str(nb_chunk+1)
						nb_chunk = nb_chunk+1
						if int(li) != 0:
							sortie_xml+="</Groupe>\n"
						sortie_xml+="<Groupe type=\""+liste_chunk[li]['chunk']+"\" id=\"E"+str(treenum)+"G"+str(nb_chunk)+"\">\n"
					elif (liste_chunk[str(int(li)-1)]['head']==1 and liste_chunk[li]['head']!=2):
						print ">apres tete:"+str(nb_chunk)+" -> "+str(nb_chunk+1)
						nb_chunk = nb_chunk+1
						if int(li) != 0:
							sortie_xml+="</Groupe>\n"
						sortie_xml+="<Groupe type=\""+liste_chunk[li]['chunk']+"\" id=\"E"+str(treenum)+"G"+str(nb_chunk)+"\">\n"
											
					print ">affectation:"+str(nb_chunk)
					chunks[li]=nb_chunk
					chunk=liste_chunk[li]['chunk']						
				else:
					chunks[li]=""
					sortie_xml+="</Groupe>\n"
				sortie_xml+="\t<F id=\"E"+str(treenum)+"F"+str(li)+"\">"+get_vertex(li,dgraph.vertices).label+"</F>\n"

			else:
				chunk="G"
	sortie_xml+="</Groupe>"
	print sortie_xml
	
def set_chunk_head(index,chunk,head):
	if not liste_chunk.has_key(index):
		print "=ajoute("+str(index)+","+chunk+","+str(head)+")"
		liste_chunk[index]={}
		liste_chunk[index]['neg']=-1	
	else:
		print "=modification: ("+str(index)+","+liste_chunk[index]['chunk']+","+str(liste_chunk[index]['head'])+") > ("+str(index)+","+chunk+","+str(head)+")"
		pass
	liste_chunk[index]['chunk']=chunk	
	liste_chunk[index]['head']=head

def set_chunk_prep(idx_obj,idx_prep):
	print "=preposition: ("+idx_obj+","+idx_prep+")"
	liste_chunk[idx_obj]['prep']=idx_prep

def set_neg(index,neg):
	print "=negation: ("+index+","+neg+")"
	liste_chunk[index]['neg']=neg
	

def set_chunks(dgraph):
	print "#SET_CHUNKS"
	deps_root = get_deps(-1,dgraph.edges)
	for dp in deps_root:
		print "RACINE: "+str_vertex(dp.dest)+"\n"
		set_chunks_aux(dp,dgraph.edges,'')
	

def set_chunks_aux(edge,edges,ident):
	print ident+"PERE: "+str_vertex(edge.orig)
	print ident+"FILS: "+str_vertex(edge.dest)
	
	cat2chunks(edge,ident,edges)
	
	fils_deps = get_deps(edge.dest.idx,edges)
	for fd in fils_deps:
		set_chunks_aux(fd,edges,'\t'+ident)

def cat2chunks(edge,ident,all_edges):
	gouv = edge.orig
	dep = edge.dest
	print ident+"  > "+str_edge(edge)
	print ident+"  >> CAT: "+dep.subcat
	if re.match("^N$",dep.subcat):
		cat2chunks_N(edge,ident)
	elif re.match("^PRO$",dep.subcat):
		cat2chunks_PRO(edge,ident)
	elif re.match("^D$",dep.subcat):
		cat2chunks_D(edge,ident)
	elif re.match("^A$",dep.subcat):
		cat2chunks_A(edge,ident)
	elif re.match("^ADV$",dep.subcat) and dep.lemma != "ne":
		cat2chunks_ADV(edge,ident,all_edges)
	elif re.match("^ADV$",dep.subcat) and dep.lemma == "ne":
		cat2chunks_ADVne(edge,ident)
	elif re.match("^V$",dep.subcat):
		cat2chunks_V(edge,ident,all_edges)
	elif re.match("^CL$",dep.subcat):
		cat2chunks_CL(edge,ident)
	elif re.match("^C$",dep.subcat):
		cat2chunks_C(edge,ident)
	elif re.match("^I$",dep.subcat):
		cat2chunks_I(edge,ident)
	elif re.match("^P(\+.*)?$",dep.subcat):
		cat2chunks_P(edge,ident)
	elif re.match("^ET$",dep.subcat):
		cat2chunks_ET(edge,ident)
	else:
		print "# CATEGORIE INCONNUE #"

def est_prep(node):
	if re.match("^P(\+.*)?$",node.subcat):
		return True
	else:
		return False

def est_nom(node):
	if re.match("^N$",node.subcat):
		return True
	else:
		return False

def est_verbe(node):
	if re.match("^V$",node.subcat):
		return True
	else:
		return False
		
def est_adjectif(node):
	if re.match("^A$",node.subcat):
		return True
	else:
		return False
		
def est_adverbe(node):
	if re.match("^ADV$",node.subcat):
		return True
	else:
		return False

def cat2chunks_N(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"GN",1)
	else:
		if est_prep(gouv):
			set_chunk_head(dep.idx,"GP",1)
			set_chunk_head(gouv.idx,"GP",0)
			set_chunk_prep(dep.idx,gouv.idx)
		else:
			set_chunk_head(dep.idx,"GP",1)		

def cat2chunks_PRO(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"GN",1)
	else:
		if est_prep(gouv):
			set_chunk_head(dep.idx,"GP",1)
			set_chunk_head(gouv.idx,"GP",0)
			set_chunk_prep(dep.idx,gouv.idx)
		else:
			set_chunk_head(dep.idx,"GN",1)	
	
def cat2chunks_D(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"GN",0)
	else:
		if est_prep(gouv):
			set_chunk_head(dep.idx,"GP",1)
			set_chunk_head(gouv.idx,"GP",0)
			set_chunk_prep(dep.idx,gouv.idx)
		elif est_adjectif(gouv):
			set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
			set_chunk_head(gouv.idx,liste_chunk[gouv.idx]['chunk'],1) # le Det fait de son pere ADJ une tete
		elif est_nom(gouv):
			set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
		else:
			print "# CATEGORIE: "+dep.idx+ " inattendue (cat2chunks_D) #"
			pass
	
def cat2chunks_A(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"GA",1)
	else:
		if est_prep(gouv):
			set_chunk_head(dep.idx,"GP",1)
			set_chunk_head(gouv.idx,"GP",0)
			set_chunk_prep(dep.idx,gouv.idx)
		elif est_nom(gouv):
			if dep.idx < gouv.idx:
				set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
			else:
				set_chunk_head(dep.idx,"GA",1)
		elif est_verbe(gouv):
			set_chunk_head(dep.idx,"GA",1)
		else:
			print "# CATEGORIE: "+dep.idx+ " inattendue (cat2chunks_A) #"
			pass			
	
def cat2chunks_ADV(edge,ident,all_edges):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"GR",1)
	else:
		if est_verbe(gouv):
			if liste_chunk[gouv.idx]['chunk']=="GP":
				if dep.idx > liste_chunk[gouv.idx]['prep'] and dep.idx < gouv.idx:
					if liste_chunk[gouv.idx].has_key('neg'):
						set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
					else:
						set_chunk_head(dep.idx,"GR",1)
						set_chunk_head(liste_chunk[gouv.idx]['prep'],"G",0)
				else:
					set_chunk_head(dep.idx,"GR",1)
			else:
				if liste_chunk[gouv.idx].has_key('neg'):
					set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
				else:
					set_chunk_head(dep.idx,"GR",1)
					set_chunk_head(liste_chunk[gouv.idx]['prep'],"G",0)				
		elif est_adjectif(gouv):
			if liste_chunk[gouv.idx]['chunk']=="GP":
				set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
			else:
					transcat=False
					edges=get_deps(gouv.idx,all_edges)
					for e in edges:
						if re.match("D$",e.dest.subcat):
							transcat=True
							print "#transcat"
					if transcat:
						set_chunk_head(dep.idx,"GN",0)
						set_chunk_head(gouv.idx,"GN",1)
					else:
						set_chunk_head(dep.idx,"GR",1)
		elif est_prep(gouv):
			set_chunk_head(dep.idx,"GR",1)
		else:
			print "# CATEGORIE: "+dep.idx+ " inattendue (cat2chunks_ADV) #"
				
	
def cat2chunks_ADVne(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"GR",1)
	else:
		set-chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
		
def cat2chunks_V(edge,ident,all_edges):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"NV",1)
	else:
		edges=get_deps(dep.idx,all_edges)
		for e in edges:
			if est_adverbe(e.dest) and e.dest.lemma=="ne":
				set_neg(dep.idx,e.dest.idx)
		if est_prep(gouv):
			set_chunk_head(dep.idx,"PV",1)
			set_chunk_head(gouv.idx,"PV",0)
			set_chunk_prep(dep.idx,gouv.idx)
		else:
			set_chunk_head(dep.idx,"NV",1)	
		

def cat2chunks_CL(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"NV",2)
	else:
		if est_verbe(gouv):
			if dep.idx < gouv.idx:
				set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
			else:
				set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],2)
		else:
			print "# CATEGORIE: "+dep.idx+ " inattendue (cat2chunks_CL) #"

def cat2chunks_C(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	set_chunk_head(dep.idx,"",1)
	
def cat2chunks_I(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	set_chunk_head(dep.idx,"GR",1)
	
def cat2chunks_P(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	
def cat2chunks_ET(edge,ident):
	gouv = edge.orig
	dep = edge.dest

##################################
# TRANSFO DE GRAPH: PIVOT > EASY #
##################################

# liste associative des traductions directes entre categories
trad_directe={}

def lecture_fichier_trad(fichier):
	trad = open(fichier,"r")
	for line in trad.readlines():
		line=line
		if re.match("([^\t]+)\t+([^\t]+)\t+([^\t]+)\n?$",line):
			f=re.search("([^\t]+)\t+([^\t]+)\t+([^\t]+)\n?$",line)
			cat = f.group(1)
			anc_fonc = f.group(2)
			nv_fonc = f.group(3)
			if not trad_directe.has_key(cat):
				trad_directe[cat]={}
			trad_directe[cat][anc_fonc]=nv_fonc.rstrip()

def affiche_dico_trad():
	i=0
	for cat in trad_directe.keys():
		for fonc in trad_directe[cat]:
			print "("+str(i)+") ["+cat+"]["+fonc+"]="+trad_directe[cat][fonc]
			i=i+1

def traduction_directe(gov_cat,fonc):
	if trad_directe.has_key(gov_cat):
		if trad_directe[gov_cat].has_key(fonc):
			return trad_directe[gov_cat][fonc]
		else:
			return fonc
	else:
		return fonc

# La coordination: la conjonction devient la tete d'un noeud ayant deux fils: les deux groupes coordonnes
def coordinations(dgraph):
	print "====Coordination==="
	nedges = set([])
	liste_coord={}
	print ">>> Debut: "+str(len(dgraph.edges))+" arcs"
	for edge in dgraph.edges:
		if edge.label == "dep_coord":
			print "	-----DEPCOORD:"+edge.dest.lemma
			if not liste_coord.has_key(edge.orig.idx):
				liste_coord[edge.orig.idx]={}
				liste_coord[edge.orig.idx]['dep_coord']=[]
				liste_coord[edge.orig.idx]['coord']=[]
				print "	NEW["+edge.orig.idx+"]"
			liste_coord[edge.orig.idx]['dep_coord'].append(edge.dest)
			print "	AJOUT(dep_coord)["+edge.orig.idx+"]:"+edge.dest.lemma
			
		elif edge.label == "coord":
			print "	-----COORD:"+edge.orig.lemma
			if not liste_coord.has_key(edge.dest.idx):
				liste_coord[edge.dest.idx]={}
				liste_coord[edge.dest.idx]['dep_coord']=[]
				liste_coord[edge.dest.idx]['coord']=[]
				print "	NEW["+edge.dest.idx+"]"
			liste_coord[edge.dest.idx]['coord'].append(edge.orig)
			print "	AJOUT(coord)["+edge.dest.idx+"]:"+edge.orig.lemma
	
		else:
			nedges.add(edge)
	print ">>> Apres parcours: "+str(len(nedges))+" arcs"
	
	liste_verbe={}
	for edge in dgraph.edges:
		if edge.orig.subcat == "V":
			if not liste_verbe.has_key(edge.orig.idx):
				liste_verbe[edge.orig.idx]={}
				
		if edge.label == "aux_caus":
			liste_verbe[edge.orig.idx]['aux_caus']=edge.dest
		elif edge.label == "aux_pass":
			liste_verbe[edge.orig.idx]['aux_pass']=edge.dest
		elif edge.label == "aux_tps":
			liste_verbe[edge.orig.idx]['aux_tps']=edge.dest		
		elif edge.label == "suj":
			liste_verbe[edge.orig.idx]['suj']=edge.dest
	for lv in liste_verbe.keys():
		print "="+lv
		for lv2 in liste_verbe[lv]:
			print "==="+lv2	
	
	
	nnedges = set([])
	
	#si verbe, faire une boucle pour relever: sujet,tps,pass et caus

	for e in liste_coord.keys():
		for e1 in liste_coord[e]['coord']:
			for e2 in liste_coord[e]['dep_coord']:
				print "coord("+get_vertex(e,dgraph.vertices).lemma+","+e1.lemma+","+e2.lemma+")"
				new_edge1 = DepEdge(get_vertex(e,dgraph.vertices),"coord1",e1)
				new_edge2 = DepEdge(get_vertex(e,dgraph.vertices),"coord2",e2)
				nedges.add(new_edge1)
				nedges.add(new_edge2)
				for ne in nedges:
					#AJOUT DE LA RELATION COMP
					if re.match("^P",ne.orig.subcat) and ne.orig.idx == e1.idx:
						edge_comp = DepEdge(e2,"comp",ne.orig)
						print "> ajout comp!"
						nnedges.add(edge_comp)

	print ">Propagation"
	for ne in nedges:
		print "["+ne.label
		if not (ne.label == "coord1" or ne.label == "coord2"):
			for e in liste_coord.keys():
				print "	["+e
				for e1 in liste_coord[e]['coord']:
					print "		["+e1.idx
					#Propagation: partout ou e1 est dependant, mettre la conj a la place
					if ne.dest.idx == e1.idx:
						print "			[propagation coord1-dependant"
						ne=DepEdge(ne.orig, ne.label, get_vertex(e,dgraph.vertices))
					#Si e1 est un verbe, rechercher ses auxiliaires et son sujet (SIL NEN A PAS) et les faire porter sur la conj
					elif ne.orig.idx == e1.idx and e1.subcat == "V":
						for e2 in liste_coord[e]['dep_coord']:
							print "		["+e2.idx
							print "		("+ne.label+" "+ne.orig.label+" "+ne.dest.label+","+get_vertex(e,dgraph.vertices).label+","+e1.label+","+e2.label+")"
							if not liste_verbe[e2.idx].has_key('aux_caus') and liste_verbe[e1.idx].has_key('aux_caus'):
								if ne.label == "aux_caus":
									print e2.label+" na pas de aux_caus"
									ne=DepEdge(get_vertex(e,dgraph.vertices),"aux_caus",ne.dest)
							if not liste_verbe[e2.idx].has_key('aux_pass') and liste_verbe[e1.idx].has_key('aux_pass'):
								if ne.label == "aux_pass":
									print e2.label+" na pas de aux_pass"
									ne=DepEdge(get_vertex(e,dgraph.vertices),"aux_pass",ne.dest)
							if not liste_verbe[e2.idx].has_key('aux_tps') and liste_verbe[e1.idx].has_key('aux_tps'):
								if ne.label == "aux_tps":
									print e2.label+" na pas de aux_tps"
									ne=DepEdge(get_vertex(e,dgraph.vertices),"aux_tps",ne.dest)
							if not liste_verbe[e2.idx].has_key('suj') and liste_verbe[e1.idx].has_key('suj'):
								if ne.label == "suj":
									print e2.label+" na pas de suj"
									ne=DepEdge(get_vertex(e,dgraph.vertices),"suj",ne.dest)

		nnedges.add(ne)		
				
	print ">>> Apres ajout: "+str(len(nnedges))+" arcs"
	
	dgraph.edges = nnedges
	print ">>> Apres affectation: "+str(len(dgraph.edges))+" arcs"
	return dgraph

# suppression de la ponctuation
def ponctuation(dgraph):
	print "====Ponctuation==="
	nedges = set([])
	print ">>> Debut: "+str(len(dgraph.edges))+" arcs"
	for edge in dgraph.edges:
		if edge.label == "ponct":
			pass
		else:
			nedges.add(edge)
	print ">>> Fin: "+str(len(nedges))+" arcs"
	dgraph.edges = nedges
	return dgraph

# suppression des affixes
def affixes(dgraph):
	print "====Affixes==="
	nedges = set([])
	print ">>> Debut: "+str(len(dgraph.edges))+" arcs"
	for edge in dgraph.edges:
		if edge.label == "aff":
			pass
		else:
			nedges.add(edge)
	print ">>> Fin: "+str(len(nedges))+" arcs"
	dgraph.edges = nedges
	return dgraph


# liste des lemmes de determinants dont il faut retirer la relation "det"
# pour les autres, la relation "det" devient "mod_n"
liste_det_a_supprimer = ["le","un","son"]

# suppression des determinants
# attention il faudra garder les choses commes les numerals, tous, etc...
def determinants(dgraph):
	print "====Determinant==="
	nedges = set([])
	print ">>> Debut: "+str(len(dgraph.edges))+" arcs"
	for edge in dgraph.edges:
		if edge.label == "det" and edge.dest.lemma in liste_det_a_supprimer:
			pass
		elif edge.label == "det":
			edge.label = "mod_n"
			print "DET A GARDER:"+edge.dest.label
			nedges.add(edge)
		else:
			nedges.add(edge)
	print ">>> Fin: "+str(len(nedges))+" arcs"
	dgraph.edges = nedges
	return dgraph

# chainage des sequences sujet -> aux_tps -> aux_pass|aux_caus -> verbe
# au lieu d'avoir chacun de ces elements relie directement a la tete verbale
def auxiliaires(dgraph):
	print "=====Auxiliaires====="
	nedges = set([])
	liste_verbes={}
	print ">>> Debut: "+str(len(dgraph.edges))+" arcs"
	for edge in dgraph.edges:
		if edge.label == "aux_tps":
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			liste_verbes[edge.orig.idx]['tps']=edge.dest.idx
			print ">tps:"+edge.orig.idx
		elif edge.label == "aux_pass":
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			liste_verbes[edge.orig.idx]['pass']=edge.dest.idx
			print ">pass:"+edge.orig.idx
		elif edge.label == "aux_caus":
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			liste_verbes[edge.orig.idx]['caus']=edge.dest.idx
			print ">caus:"+edge.orig.idx	
		elif edge.label == "suj":
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			liste_verbes[edge.orig.idx]['suj']=edge.dest.idx
			print ">suj:"+edge.orig.idx
		else:
			nedges.add(edge)
	
	print ">>> Apres parcours: "+str(len(nedges))+" arcs"

	for v in liste_verbes.keys():
		if liste_verbes[v].has_key('suj'):
			if liste_verbes[v].has_key('tps') and liste_verbes[v].has_key('pass'):
				print "SUJ + TPS_PASS"
				e1 = DepEdge(get_vertex(liste_verbes[v]['tps'],dgraph.vertices),"suj",get_vertex(liste_verbes[v]['suj'],dgraph.vertices))
				nedges.add(e1)
				e2 = DepEdge(get_vertex(liste_verbes[v]['pass'],dgraph.vertices),"aux_v",get_vertex(liste_verbes[v]['tps'],dgraph.vertices))
				nedges.add(e2)
				e3 = DepEdge(get_vertex(v,dgraph.vertices),"aux_v",get_vertex(liste_verbes[v]['tps'],dgraph.vertices))
				nedges.add(e3)
			elif liste_verbes[v].has_key('tps') and liste_verbes[v].has_key('caus'):
				print "SUJ + TPS_CAUS"
				e1 = DepEdge(get_vertex(liste_verbes[v]['tps'],dgraph.vertices),"suj",get_vertex(liste_verbes[v]['suj'],dgraph.vertices))
				nedges.add(e1)
				e2 = DepEdge(get_vertex(liste_verbes[v]['caus'],dgraph.vertices),"aux_v",get_vertex(liste_verbes[v]['tps'],dgraph.vertices))
				nedges.add(e2)
				e3 = DepEdge(get_vertex(v,dgraph.vertices),"aux_v",get_vertex(liste_verbes[v]['caus'],dgraph.vertices))
				nedges.add(e3)		
			elif liste_verbes[v].has_key('tps'):
				print "SUJ + TPS"
				e1 = DepEdge(get_vertex(liste_verbes[v]['tps'],dgraph.vertices),"suj",get_vertex(liste_verbes[v]['suj'],dgraph.vertices))
				nedges.add(e1)
				e2 = DepEdge(get_vertex(v,dgraph.vertices),"aux_v",get_vertex(liste_verbes[v]['tps'],dgraph.vertices))
				nedges.add(e2)	
			elif liste_verbes[v].has_key('pass'):
				print "SUJ + PASS"
				e1 = DepEdge(get_vertex(liste_verbes[v]['pass'],dgraph.vertices),"suj",get_vertex(liste_verbes[v]['tps'],dgraph.vertices))
				nedges.add(e1)
				e2 = DepEdge(get_vertex(v,dgraph.vertices),"aux_v",get_vertex(liste_verbes[v]['pass'],dgraph.vertices))
				nedges.add(e2)
			else:
				print "SUJET"
				e4 = DepEdge(get_vertex(v,dgraph.vertices),"suj",get_vertex(liste_verbes[v]['suj'],dgraph.vertices))
				nedges.add(e4)		
	
	print ">>> Apres propagation: "+str(len(nedges))+" arcs"
	dgraph.edges = nedges
	return dgraph

# remontee des prepositions:
# elles ne sont pas notees dans EASY, donc _fonc(x,PREP) + obj(PREP,y) => _fonc(x,y)
def remontee_prepositions(dgraph):
	print "=====Remontee des prepositions====="
	nedges = set([])
	print ">>> Debut: "+str(len(dgraph.edges))+" arcs"
	liste_prep={}
	for edge in dgraph.edges:
		print "["+edge.label
		if re.match("^P",edge.orig.subcat):
			if not liste_prep.has_key(edge.orig.idx):
				liste_prep[edge.orig.idx]={}
			liste_prep[edge.orig.idx]['obj']=edge.dest
			print "	[obj>"+edge.orig.idx
		elif re.match("^P",edge.dest.subcat) and not re.match("^comp",edge.label):
			if not liste_prep.has_key(edge.dest.idx):
				liste_prep[edge.dest.idx]={}
			liste_prep[edge.dest.idx]['gouv']=edge.orig
			liste_prep[edge.dest.idx]['label']=edge.label
			print "	[gouv/label>"+edge.dest.idx
		else:
			nedges.add(edge)
	print ">>> Apres parcours: "+str(len(nedges))+" arcs"
	
	liste_comp=[]
	
	for p in liste_prep.keys():
		print ">"+p
		if liste_prep[p].has_key('obj') and liste_prep[p].has_key('gouv'):
			print "remontee"
			e=DepEdge(liste_prep[p]['gouv'],liste_prep[p]['label'],liste_prep[p]['obj'])
			nedges.add(e)
			#TRAITEMENT DU CAS "de toutletemps changer"
			if liste_prep[p]['obj'].subcat == "V":
				for ne in nedges:
					if ne.orig.idx == liste_prep[p]['obj'].idx and ne.dest.subcat != "CL" and ne.dest.idx > p and ne.dest.idx < liste_prep[p]['obj'].idx:
						edge_comp = DepEdge(liste_prep[p]['obj'],"comp",get_vertex(p,dgraph.vertices))
						print "+comp"+ne.label
						liste_comp.append(edge_comp)
			#fin
		else:
			print "fausse remontee"
			if liste_prep[p].has_key('obj'):
				nedges.add(DepEdge(get_vertex(p,dgraph.vertices),"obj",liste_prep[p]['obj']))
			if liste_prep[p].has_key('gouv'):
				nedges.add(DepEdge(liste_prep[p]['gouv'],liste_prep[p]['label'],get_vertex(p,dgraph.vertices)))
	print ">>> Fin: "+str(len(nedges))+" arcs"
	
	for e in liste_comp:
		nedges.add(e)
		print "-ajout:comp("+e.orig.label+","+e.dest.label+")"
	
	dgraph.edges = nedges
	return dgraph

def complementeurs(dgraph):
	print "=====Traitement des complementeurs====="
	nedges = set([])
	print ">>> Debut: "+str(len(dgraph.edges))+" arcs"
	liste_comp={}
	for edge in dgraph.edges:
		print "["+edge.label
		if re.match("^Cs",edge.orig.subcat):
			if not liste_comp.has_key(edge.orig.idx):
				liste_comp[edge.orig.idx]={}
			liste_comp[edge.orig.idx]['obj']=edge.dest
			print "	[obj>"+edge.orig.idx
		elif re.match("^Cs",edge.dest.subcat):
			if not liste_comp.has_key(edge.dest.idx):
				liste_comp[edge.dest.idx]={}
			liste_comp[edge.dest.idx]['gouv']=edge.orig
			liste_comp[edge.dest.idx]['label']=edge.label
			print "	[gouv/label>"+edge.dest.idx
		else:
			nedges.add(edge)
	print ">>> Apres parcours: "+str(len(nedges))+" arcs"
	
	for c in liste_comp.keys():
		print ">"+c
		if liste_comp[c].has_key('obj') and liste_comp[c].has_key('_fonc'):
			print "traitement"
			e1=DepEdge(liste_comp[c]['gouv'], liste_comp[c]['label'], liste_comp[c]['obj'])
			nedges.add(e1)
			e2=DepEdge(liste_comp[c]['obj'], "comp", get_vertex(c,dgraph.vertices))
			nedges.add(e2)
		else:
			print "faux traitement"
			if liste_comp[c].has_key('obj'):
				nedges.add(DepEdge(get_vertex(c,dgraph.vertices),"obj", liste_comp[c]['obj']))
			if liste_comp[c].has_key('gouv'):
				nedges.add(DepEdge(liste_comp[c]['gouv'], liste_comp[c]['label'],get_vertex(c,dgraph.vertices)))
	print ">>> Fin: "+str(len(nedges))+" arcs"
	dgraph.edges = nedges
	return dgraph

def adv_negation(dgraph):
	print "====ADV NEGATION==="
	nedges = set([])
	print ">>> Debut: "+str(len(dgraph.edges))+" arcs"
	liste_neg={}
	for edge in dgraph.edges:
		if edge.label == "mod" and edge.dest.subcat =="ADV" and re.match("^(n'|ne)$",edge.dest.label):
			liste_neg[edge.dest.idx]=edge.orig.idx
			pass
		else:
			nedges.add(edge)
	print ">>> Apres parcours: "+str(len(nedges))+" arcs"
	
	nnedges=set([])
	
	for ne in nedges:
		if ne.dest.subcat == "ADV":
			inter=0
			for adv_ne in liste_neg.keys():
				if ne.dest.idx > adv_ne and ne.dest.idx < liste_neg[adv_ne]:
					inter=1
			if inter == 1:
				pass
			else:
				nnedges.add(ne)
		else:
			nnedges.add(ne)
	print ">>> Fin: "+str(len(nnedges))+" arcs"
	
	dgraph.edges = nnedges
	return dgraph


################
# MAIN PROGRAM #
################


reader = PReader()	
ptree = reader.parse_pivot(fichier)
lecture_fichier_trad("traduction.txt")
affiche_dico_trad()
print "--------------------------PIVOT--------------------------\n"
dg = ptree.PTree2DependencyGraph()
printl(dg)

#print "--------------------------MODIFICATIONS--------------------------\n"
#coordinations(dg)
#printl(dg)
#determinants(dg)
#printl(dg)
#auxiliaires(dg)
#printl(dg)
#affixes(dg)
#printl(dg)
#adv_negation(dg)
#printl(dg)
#remontee_prepositions(dg)
#printl(dg)
#complementeurs(dg)
#print "--------------------------EASY--------------------------\n"
#printl(dg)
	
print "------------"
#set_chunks(dg)
#affiche_liste_chunks(dg)
#build_chunks(dg)
#affiche_chunks(dg)

