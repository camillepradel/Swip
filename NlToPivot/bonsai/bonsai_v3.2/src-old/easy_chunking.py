#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
#
# easy_chunking
#
# Author : Francois Guerin
# Date : Sept 2008
# Objectif:
# crée la structure en chunks à partir d'un objet DepSentence

# Méthode:
# à chaque token de l'énoncé, nous associons le type de chunk
# dans lequel il apparaitre.
# Les types de chunks sont GN,GP,NV,PV,GR,GA et <None>
# Nous parcourons le graphe de dépendances à partir de la racine en profondeur
# Pour cela, nous nous appuyons sur deux catégories fortes: N et V.
# Ainsi, lorsque nous rencontrons un N, nous lui affectons le chunk GN, un V, le chunk NV.
# Pour les autres catégories, il faudra aussi regarder les catégories
# qui leur sont dépendantes:
# si nous rencontrons une préposition et qu'elle gouverne un nom ou un adjectif, nous avons un GP, si elle gouverne un V, nous avons un PV
# (si elle gouverne autre chose, nous lui associons le chunk <None>)
# etc.

# DU FAIT DES CHANGEMENTS DANS LA GRAMMAIRE
# LES REGLES SONT PEUT_ETRE A REVOIR...

# MAIS CE NEST PAS GRAVE... CE NEST PAS LE PLUS IMPORTANT!
# SEULE L'EVALUATION DES DEPENDANCES NOUS IMPORTE
# LA CONSTRUCTION DES CHUNKS CEST POUR CA LA DECO

import re
import os
import sys

from PivotReader import *
from dgraph import *
from chunks import *

#############
# VARIABLES #
#############

# Association de diverses infos à chaque tokens
# tq le type de chunk
# et l'information de tete
liste_chunk = {}


#############
# AFFICHAGE #
#############

def str_vertex(vertex):
	v=vertex.label+"~"+str(vertex.idx)
	return v
				
def str_edge(edge):
	return edge.label+"("+str_vertex(edge.orig)+","+str_vertex(edge.dest)+")"


# Affichage de la liste de chunks (non rangée et non traitée)
def affiche_liste_chunks(dsentence):
	liste=[]
	for v in dsentence.depforest.vertices:
		liste.append(int(v.idx))
	liste.sort()
	for i in liste:
		if liste_chunk.has_key(i):			
			print ("["+dsentence.get_vertex(i).label+"]\t\tchunk:"+liste_chunk[i]['chunk']+" \t head:"+str(liste_chunk[i]['head'])+" \t neg:"+str(liste_chunk[i]['neg']))
		else:
			print ("["+str_vertex(dsentence.get_vertex(i))+"]")

##############
# TRAITEMENT #
##############

# Traitement de la liste de chunks pour récupérer un objet 'Chunks'
def get_chunks(dsentence,liste_chunk):
	liste=[]
	for v in dsentence.depforest.vertices:
		liste.append(int(v.idx))
	liste.sort()
	chunk="G"
	nb_chunk=0
	treenum = dsentence.id
	liste_chunk[-1]={}
	liste_chunk[-1]['head']=0
	cs = Chunks(treenum)
	for li in liste:
		if li != -1:
			if liste_chunk.has_key(li):
				if liste_chunk[li]['chunk'] !="":
					if (not liste_chunk[li]['chunk'] == "_" and not chunk == liste_chunk[li]['chunk']): 
						nb_chunk = nb_chunk+1

					elif (liste_chunk[li-1]['head']==2 and liste_chunk[li]['head']!=2):
						nb_chunk = nb_chunk+1

					elif (liste_chunk[li-1]['head']==1 and liste_chunk[li]['head']!=2):
						nb_chunk = nb_chunk+1
							
					chunk=liste_chunk[li]['chunk']						

				token = dsentence.get_vertex(li)
				#token.label = dsentence.decode_pivot_metachars(token.label)
				if liste_chunk[li].has_key('chunk'):
					if liste_chunk[li]['chunk'] != "":
						cs.add_token(li+1,Token(token.label,liste_chunk[li]['chunk'],nb_chunk,liste_chunk[li]['head']))
					else:
						cs.add_token(li+1,Token(token.label,"",None,liste_chunk[li]['head']))
				else:
					cs.add_token(li+1,Token(token.label,"",nb_chunk,liste_chunk[li]['head']))
			else:
				chunk="G"
	return cs

# Traduction des caracteres interdits en rawtext dans le xml
def normalize(string):
	string = re.sub('<','&lt;',string)
	string = re.sub('>','&gt;',string)
	string = re.sub('&','&amp;',string)
	return string

# Renvoi du xml correspondant à l'objets 'Chunks' créé	
def sortie_xml_chunk(chunks):
	sortie_xml=""
	tokens = chunks.tokens.keys()
	tokens_vus = []
	for t in tokens:
		if not t in tokens_vus:
			for g in chunks.groupes.keys():
				if t in chunks.groupes[g].liste and not chunks.groupes[g].chunk:
					sortie_xml += "\t<F id=\"E"+str(chunks.id_sent)+"F"+str(t)+"\">"
					sortie_xml += normalize(chunks.tokens[t].forme)
					sortie_xml += "</F>\n"
					tokens_vus.append(t)	
				elif t in chunks.groupes[g].liste and chunks.groupes[g].chunk != None:
					sortie_xml+="<Groupe type=\""+chunks.groupes[g].chunk+"\" id=\"E"+str(chunks.id_sent)+"G"+str(g)+"\">\n"
					for tg in chunks.groupes[g].liste:
						sortie_xml+="\t<F id=\"E"+str(chunks.id_sent)+"F"+str(tg)+"\">"
						sortie_xml += normalize(chunks.tokens[tg].forme)
						sortie_xml += "</F>\n"
						tokens_vus.append(tg)
					sortie_xml+="</Groupe>\n"
				else:
					pass
		else:
			pass
	return sortie_xml

#####################
# FONCTIONS DE TEST #
#####################

# Fonctions de raccourcis pour retrouver certaines catégories syntaxiques

def est_prep(node):
	return node.est_categorie("^P(\+.*)?$")

def est_nom(node):
	return node.est_categorie("^N$") or node.est_categorie("^PRO$")

def est_adjectif(node):
	return node.est_categorie("^A$")

def est_verbe(node):
	return node.est_categorie("^V$")

def est_adverbe(node):
	return node.est_categorie("^ADV$")

def est_det(node):
	return node.est_categorie("^D$") or node.est_categorie("^P\+D$")

###########################
# FONCTIONS DAFFECTATION #
###########################

# Ajout de l'information de tete donné en parametre à un chunk donné 
def set_chunk_head(index,chunk,head):
	if not liste_chunk.has_key(index):
		liste_chunk[index]={}
		liste_chunk[index]['neg']=-1	
	else:
		pass
	liste_chunk[index]['chunk']=chunk	
	liste_chunk[index]['head']=head

# Ajout de l'information de prépositon à un chunk donné
# idx_prep: position de la preposition dominant idx_obj
def set_chunk_prep(idx_obj,idx_prep):
	liste_chunk[idx_obj]['prep']=idx_prep

# Ajout de l'information négation à un chunk donné
# neg: position de l'adverbe de negation dependant du verbe idx_vb
def set_neg(idx_vb,idx_neg):
	if not liste_chunk.has_key(idx_vb):
		liste_chunk[idx_vb]={}
	liste_chunk[idx_vb]['neg']=idx_neg

#####################################
# FONCTIONS DE PARCOURS DU GRAPHE #
#####################################

# Création d'une liste de chunks en fonction d'un objet 'DepSentence'
def set_chunks(dsentence):
	deps_root = dsentence.depforest.get_roots()
	for root in deps_root:
		edges = dsentence.depforest.get_deps(root.idx)
		for e in edges:
			set_chunks_aux(e,dsentence,'',[])
	return liste_chunk
	
# Modification de la liste de chunks en fonction d'une relation (si le dépendant n'a pas encore été vu)
def set_chunks_aux(edge,dsentence,ident,vus):
	if not edge.dest.idx in vus:
		vus.append(edge.dest.idx)
	
		cat2chunks(edge,ident,dsentence)
		
		fils_deps = dsentence.depforest.get_deps(edge.dest.idx)
		for fd in fils_deps:
			set_chunks_aux(fd,dsentence,'\t'+ident,vus)
		
# Modification de la liste de chunks en fonction de la catégorie du dépendant dans une relation donnée
def cat2chunks(edge,ident,dsentence):
	gouv = edge.orig
	dep = edge.dest
	if dep.feats.has_key('pos'):
		if dep.est_categorie("^N$"):
			cat2chunks_N(edge,ident)
		elif dep.est_categorie("^PRO|PREF$"):
			cat2chunks_PRO(edge,ident)
		elif dep.est_categorie("^D$"):
			cat2chunks_D(edge,ident)
		elif dep.est_categorie("^A$"):
			cat2chunks_A(edge,ident)
		elif dep.est_categorie("^ADV$") and not re.match("^(n'|ne|ni)$",dep.label.lower()):
			cat2chunks_ADV(edge,ident,dsentence.depforest)
		elif dep.est_categorie("^ADV$") and re.match("^(n'|ne|ni)$",dep.label.lower()):
			cat2chunks_ADVne(edge,ident)
		elif dep.est_categorie("^V$"):
			cat2chunks_V(edge,ident,dsentence.depforest)
		elif dep.est_categorie("^CL$"):
			cat2chunks_CL(edge,ident)
		elif dep.est_categorie("^C$"):
			cat2chunks_C(edge,ident)
		elif dep.est_categorie("^I$"):
			cat2chunks_I(edge,ident)
		elif dep.est_categorie("^P(\+.*)?$"):
			cat2chunks_P(edge,ident)
		elif dep.est_categorie("^ET$"):
			cat2chunks_ET(edge,ident)
		elif dep.est_categorie("^PONCT$"):
			cat2chunks_PONCT(edge,ident)
		elif dep.label in ['MISSINGHEAD']:
			print "\t# (chunking) MISSING HEAD: "+str(dsentence.id)
			pass
		elif dep.label in ["Failure"]:
			print "\t# (chunking) PARSING FAILURE: "+str(dsentence.id)
			pass
		else:
			print ("\t# CATEGORIE INCONNUE: "+dep.feats['pos']+"\t"+str_edge(edge)+":"+str(dsentence.id))
	else:
		print ("\t# PAS DE CATEGORIE POUR: "+str_vertex(dep)+"\t"+str_edge(edge)+":"+str(dsentence.id))

# FONCTIONS SUIVANTES:
# assignation d'un type de chunk au dépendant de l'arc donné en argument,
# en fonction de sa Part-of-speech.

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
			set_chunk_head(dep.idx,"GN",1)		

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
		elif est_adjectif(gouv) or est_verbe(gouv):
			if liste_chunk[gouv.idx]['chunk']=="GP" or liste_chunk[gouv.idx]['chunk']=="PV":
				set_chunk_head(dep.idx,"GP",0)
				set_chunk_head(gouv.idx,"GP",1)
			else:
				set_chunk_head(dep.idx,"GN",0)
				set_chunk_head(gouv.idx,"GN",1)
		elif est_nom(gouv):
			set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
		else:
			set_chunk_head(dep.idx,"GN",1)
			#print "# CATEGORIE: "+str_vertex(dep)+" [ "+str_edge(edge)+"] inattendue (cat2chunks_D) #"
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
			set_chunk_head(dep.idx,"GA",1)
			#print "# CATEGORIE: "+str_vertex(dep)+" [ "+str_edge(edge)+"] inattendue (cat2chunks_A) #"
			pass			
	
def cat2chunks_ADV(edge,ident,depforest):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"GR",1)
	else:
		if est_verbe(gouv):
			if liste_chunk[gouv.idx]['chunk']=="PV":
				if dep.idx > liste_chunk[gouv.idx]['prep'] and dep.idx < gouv.idx:
					if liste_chunk[gouv.idx]['neg'] > 0:
						set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
					else:
						set_chunk_head(dep.idx,"GR",1)
						set_chunk_head(liste_chunk[gouv.idx]['prep'],"",0)
				else:
					set_chunk_head(dep.idx,"GR",1)
			else:
				if liste_chunk[gouv.idx]['neg'] > 0:
					set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
				else:
					set_chunk_head(dep.idx,"GR",1)
					#set_chunk_head(liste_chunk[gouv.idx]['prep'],"G",0)				
		elif est_adjectif(gouv):
			if liste_chunk[gouv.idx]['chunk']=="GP":
				set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
			else:
					transcat=False
					edges= depforest.get_deps(gouv.idx)
					for e in edges:
						if e.dest.est_categorie("^D$") or e.dest.est_categorie("^P\+D$"):
							transcat=True
					if transcat:
						set_chunk_head(dep.idx,"GN",0)
						set_chunk_head(gouv.idx,"GN",1)
					else:
						set_chunk_head(dep.idx,"GR",1)
		elif est_prep(gouv):
			set_chunk_head(dep.idx,"GR",1)
		else:
			set_chunk_head(dep.idx,"GR",1)
				
	
def cat2chunks_ADVne(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"GR",1)
	else:
		set_chunk_head(dep.idx,liste_chunk[gouv.idx]['chunk'],0)
		
def cat2chunks_V(edge,ident,depforest):
	gouv = edge.orig
	dep = edge.dest
	if gouv.idx < 0:
		set_chunk_head(dep.idx,"NV",1)
	else:
	
		edges=depforest.get_deps(dep.idx)
	
		if est_prep(gouv):
			set_chunk_head(dep.idx,"PV",1)
			set_chunk_head(gouv.idx,"PV",0)
			set_chunk_prep(dep.idx,gouv.idx)
	
		else:
			avec_comp=False
			comp = -1
			for e in edges:
				if est_prep(e.dest) and e.label == "comp":
					avec_comp=True
					comp = e.dest
			if avec_comp:
				set_chunk_head(dep.idx,"PV",1)
				set_chunk_head(comp.idx,"PV",0)
				set_chunk_prep(dep.idx,comp.idx)
			else:
				set_chunk_head(dep.idx,"NV",1)
				
		for e in edges:
			if est_adverbe(e.dest) and re.match("^(n'|ne|ni)$",e.dest.label.lower()):
				set_neg(dep.idx,e.dest.idx)
		
#AJOUTER LE CAS OU UN FILS EST UNE PREP AVEC LABEL COMPLEMENTEUR:
#RECHERCHER CE FILS ET METTRE PV


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
			set_chunk_head(dep.idx,"NV",1)
			#print "# CATEGORIE: "+str_vertex(dep)+" [ "+str_edge(edge)+"] inattendue (cat2chunks_CL) #"

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
	if not liste_chunk.has_key(dep.idx):
		set_chunk_head(dep.idx,"",1)
	
def cat2chunks_ET(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	if not liste_chunk.has_key(dep.idx):
		set_chunk_head(dep.idx,"GN",1)

def cat2chunks_PONCT(edge,ident):
	gouv = edge.orig
	dep = edge.dest
	set_chunk_head(dep.idx,"",1)
	
def chunking(depsentence):
	set_chunks(depsentence)
	return sortie_xml_chunk(depsentence)	
	