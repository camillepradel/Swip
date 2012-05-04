#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# Auteur : GUERIN Francois
# Date : Septembre 2008 - Janvier 2009
#
# Description: conversion des triplets de dépendances au format Pivot
# en triplets de dépendances au format Easy
# La liste de triplets va passer par une série de fonctions,
# qui sont chacunes associées à un phénomène linguistique majeure.
# L'ordre utilisé est important car une fonction s'appuie sur des relations pivot ou sur des relations Easy données
# et en crée de nouvelles.
# Ces différentes fonctions permettent d'ajouter/modifier/supprimer des catégories/labels/relations/structures

import re
import os
import sys

from PivotReader import *
from dgraph import *

#############
# AFFICHAGE #
#############

#Eviter les problemes d'encodage
def stredge(edge):
	v_orig = edge.orig.label+"~"+str(edge.orig.idx)
	if edge.orig.feats.has_key('pos'):
		v_orig += "/"+edge.orig.feats['pos']
	v_dest = edge.dest.label+"~"+str(edge.dest.idx)
	if edge.dest.feats.has_key('pos'):
		v_dest += "/"+edge.dest.feats['pos']
	return edge.label+"("+ v_orig +","+ v_dest +")"

# Affichage du dictionnaire de traduction (fichier spécifié)
def affiche_dico_trad():
	i=0
	for cat in trad_directe.keys():
		for fonc in trad_directe[cat]:
			print "\t["+cat+"]["+fonc+"]="+trad_directe[cat][fonc]
			i=i+1

# Renvoi du xml correspondant aux relations de dépendances trouvées
def sortie_xml_dependances(dsentence,ROOT_INDEX=0):
	sortie_xml=""
	sortie_xml+="<relations>\n"
	
	nedges=set([])
	nedges_coord={}
	for edge in dsentence.depforest.edges:
		if edge.label == "coord1":
			if not nedges_coord.has_key(edge.orig.idx):
				nedges_coord[edge.orig.idx]={}
			if not nedges_coord[edge.orig.idx].has_key('coord1'):
				nedges_coord[edge.orig.idx]['coord1']=[]	
			nedges_coord[edge.orig.idx]['coord1'].append(edge.dest.idx)
		elif edge.label == "coord2":
			if not nedges_coord.has_key(edge.orig.idx):
				nedges_coord[edge.orig.idx]={}
			if not nedges_coord[edge.orig.idx].has_key('coord2'):
				nedges_coord[edge.orig.idx]['coord2']=[]	
			nedges_coord[edge.orig.idx]['coord2'].append(edge.dest.idx)
		else:
			nedges.add(edge)
	
	nb_id=1
	
	for ne in nedges:
		if ne.orig.idx != -1 and ne.dest.idx != -1:
			if ne.label == "suj_v":
				fonction="SUJ-V"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"verbe",ne.orig.idx,"sujet",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "aux_v":
				fonction="AUX-V"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"verbe",ne.orig.idx,"auxiliaire",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "cod_v":
				fonction="COD-V"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"verbe",ne.orig.idx,"cod",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "cpl_v":
				fonction="CPL-V"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"verbe",ne.orig.idx,"complement",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "mod_v":
				fonction="MOD-V"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"verbe",ne.orig.idx,"modifieur",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "comp":
				fonction="COMP"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"verbe",ne.orig.idx,"complementeur",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "ato":
				fonction="ATB-SO"
				sortie_xml += relation_atb(nb_id,fonction,dsentence.id,ne.orig.idx,ne.dest.idx,"objet",ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "ats":
				fonction="ATB-SO"
				sortie_xml += relation_atb(nb_id,fonction,dsentence.id,ne.orig.idx,ne.dest.idx,"sujet",ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "mod_n":
				fonction="MOD-N"
				sortie_xml += relation_modn(nb_id,fonction,dsentence.id,"nom",ne.orig.idx,"modifieur",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "mod_a":
				fonction="MOD-A"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"adjectif",ne.orig.idx,"modifieur",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "mod_r":
				fonction="MOD-R"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"adverbe",ne.orig.idx,"modifieur",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "mod_p":
				fonction="MOD-P"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"preposition",ne.orig.idx,"modifieur",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "app":
				fonction="APPOS"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"premier",ne.orig.idx,"appose",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label == "juxt":
				fonction="JUXT"
				sortie_xml += relation_generique(nb_id,fonction,dsentence.id,"premier",ne.orig.idx,"suivant",ne.dest.idx,ROOT_INDEX=ROOT_INDEX)
			elif ne.label in ['null','ponct','head']:
				pass
			else:
				print ("\t# (conversion) RELATION INCONNUE: "+ne.label+","+ne.orig.feats['pos']+":"+str(dsentence.id))
				#print ("\t[("+str(ne.orig.label+"/"+ne.orig.feats['pos']+","+ne.dest.label+"/"+ne.dest.feats['pos'])+"):"+str(dsentence.id)+"]").encode('latin1')
			nb_id = nb_id+1
	
	fonction = "COORD"
	for nec in nedges_coord.keys():
		if nedges_coord[nec].has_key('coord2'):
			for c2 in nedges_coord[nec]['coord2']:
				if nedges_coord[nec].has_key('coord1'):
					for c1 in nedges_coord[nec]['coord1']:
						sortie_xml += relation_coord(nb_id,fonction,dsentence.id,nec,c1,c2,ROOT_INDEX=ROOT_INDEX)
						nb_id = nb_id+1
				else:
					sortie_xml += relation_coord(nb_id,fonction,dsentence.id,nec,"vide",c2,ROOT_INDEX=ROOT_INDEX)
					nb_id = nb_id+1					

	sortie_xml+="</relations>\n"	
	#print sortie_xml
	return sortie_xml


# Renvoi du xml correspondant aux relations génériques (ie. dont la structure n'est pas spéciale)
def relation_generique(rel_id,fonction,enonce,gouv_type,gouv_id,dep_type,dep_id,ROOT_INDEX=0):
	string="<relation xlink:type=\"extended\" type=\""+fonction+"\" id=\"E"+str(enonce)+"R"+str(rel_id)+"\">\n"
	string+="\t<"+dep_type+" xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(dep_id - ROOT_INDEX)+"\"/>\n"
	string+="\t<"+gouv_type+" xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(gouv_id - ROOT_INDEX)+"\"/>\n"
	string+="</relation>\n"
	return string
	
# Renvoi du cml correspondant à la relation MOD_N
def relation_modn(rel_id,fonction,enonce,gouv_type,gouv_id,dep_type,dep_id,ROOT_INDEX=0):
	string="<relation xlink:type=\"extended\" type=\""+fonction+"\" id=\"E"+str(enonce)+"R"+str(rel_id)+"\">\n"
	string+="\t<"+dep_type+" xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(dep_id - ROOT_INDEX)+"\"/>\n"
	string+="\t<"+gouv_type+" xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(gouv_id - ROOT_INDEX)+"\"/>\n"
	string+="\t<a-propager booleen=\"faux\"/>\n"
	string+="</relation>\n"
	return string

# Renvoi du xml correspondant à la relation COORD (fusion de coord1 et coord2)
def relation_coord(rel_id,fonction,enonce,coord_id,gauche_id,droit_id,ROOT_INDEX=0):
	string="<relation xlink:type=\"extended\" type=\""+fonction+"\" id=\"E"+str(enonce)+"R"+str(rel_id)+"\">\n"
	string+="\t<coordonnant xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(coord_id - ROOT_INDEX)+"\"/>\n"
	if gauche_id == "vide":
		string+="\t<coord-g xlink:type=\"locator\" xlink:href=\""+str(gauche_id)+"\"/>\n"
	else:
		string+="\t<coord-g xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(gauche_id - ROOT_INDEX)+"\"/>\n"
	string+="\t<coord-d xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(droit_id - ROOT_INDEX)+"\"/>\n"
	string+="</relation>\n"
	return string

# Renvoi du xml correspondant à la relation ATB (sujet ou objet)
def relation_atb(rel_id,fonction,enonce,gouv_id,dep_id,attribution,ROOT_INDEX=0):		
	string="<relation xlink:type=\"extended\" type=\""+fonction+"\" id=\"E"+str(enonce)+"R"+str(rel_id)+"\">\n"
	string+="\t<attribut xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(dep_id - ROOT_INDEX)+"\"/>\n"
	string+="\t<verbe xlink:type=\"locator\" xlink:href=\"E"+str(enonce)+"F"+str(gouv_id - ROOT_INDEX)+"\"/>\n"
	string+="\t<s-o valeur=\""+attribution+"\"/>"
	string+="</relation>\n"
	return string		

#########################
# FICHIER DE TRADUCTION #
#########################

# liste associative des traductions directes entre categories
trad_directe={}

# Lecture du fichier de traduction spécifié
# et Remplissage de la liste ci-dessus
def lecture_fichier_trad(fichier):
	trad = open(fichier,"r")
	for line in trad.readlines():
		line=line
		if re.match("([^\t#]+)\t+([^\t]+)\t+([^\t]+)\n?$",line):
			f=re.search("([^\t]+)\t+([^\t]+)\t+([^\t]+)\n?$",line)
			cat = f.group(1)
			anc_fonc = f.group(2)
			nv_fonc = f.group(3)
			if not trad_directe.has_key(cat):
				trad_directe[cat]={}
			trad_directe[cat][anc_fonc]=nv_fonc.rstrip()

# Traduction d'une fonction pivot en fonction easy
# si le gouverneur est '*', qqsoit la fonction pivot, on aura la fonction easy correspondante
# si le gouverneur apparait dans le fichier de traduction, renvoi de la fonction easy
# sinon, si le gouverneur est '+', renvoi la fonction easy
# sinon renvoi de la fonction pivot
def traduction_directe(gov_cat,fonc):
	if trad_directe.has_key('*') and trad_directe['*'].has_key(fonc):
		return trad_directe['*'][fonc]
	elif trad_directe.has_key(gov_cat):
		if trad_directe[gov_cat].has_key('*'):
			return trad_directe[gov_cat]['*']
		elif trad_directe[gov_cat].has_key(fonc):
			return trad_directe[gov_cat][fonc]
		elif trad_directe[gov_cat].has_key('+'):
			return trad_directe[gov_cat]['+']	
		else:
			return fonc
	else:
		return fonc

########################
# REGLES D'EQUIVALENCE #
########################

# Cette méthode permet de modifier un dgraph au format pivot
# en le rapprochant d'une annotation à la easy:
# - les petites relations sont supprimées (det,ponct,...)
# - les prepositions sont remontées
# - certaines relations sont enrichies par la catégorie du gouverneur
def easy_like(dsentence):
	nedges=set([])
	dsentence=prepositions(dsentence)
	for edge in dsentence.depforest.edges:
		if edge.label in ['suj']:
			edge.label = "suj_v"
			nedges.add(edge)
		elif edge.label in ['aux_tps','aux_caus','aux_pass']:
			edge.label = "aux_v"
			nedges.add(edge)
		elif edge.label in ['ats','ato']:
			edge.label = "atb"
			nedges.add(edge)
		elif edge.label in ['coord']:
			edge.label = "coord1"
			nedges.add(edge)
		elif edge.label in ['dep_coord']:
			edge.label = "coord2"
			nedges.add(edge)
		elif edge.label in ['mod','mod_rel'] and edge.orig.feats['pos'] == 'V':
			edge.label = "mod_v"
			nedges.add(edge)
			#print "MODV "+stredge(edge)
		elif edge.label == 'obj' and edge.orig.feats['pos'] == 'V':
			edge.label = "cod_v"
			nedges.add(edge)
			#print "COD "+stredge(edge)
		elif re.match("^.+obj$",edge.label) and edge.orig.feats['pos'] == 'V':
			edge.label = "cpl_v"
			nedges.add(edge)
			#print "CPL "+stredge(edge)
		elif edge.label in ['mod','mod_rel','dep'] and edge.orig.feats['pos'] in ['N','PRO']:
			edge.label = "mod_n"
			nedges.add(edge)
			#print "MODN "+stredge(edge)
		elif edge.label in ['mod','dep'] and edge.orig.feats['pos'] in ['P','P+D']:
			edge.label = "mod_p"
			nedges.add(edge)
			#print "MODP "+stredge(edge)
		elif edge.label in ['mod','dep'] and edge.orig.feats['pos'] in ['ADV']:
			edge.label = "mod_r"
			nedges.add(edge)
			#print "MODR "+stredge(edge)
		elif edge.label in ['mod','dep'] and edge.orig.feats['pos'] in ['A']:
			edge.label = "mod_a"
			nedges.add(edge)
			#print "MODA "+stredge(edge)
		elif edge.label in ['obj'] and edge.orig.feats['pos'] in ['C']:
			edge.label = "comp"
			nedges.add(edge)
			#print "COMP "+stredge(edge)
		else:
			#print "-----------: "+stredge(edge)
			pass
	dsentence.depforest.edges = nedges
	return dsentence

# La coordination:
# (1) Recensement des dépendances 'coord' et 'dep_coord' en fonction de la conjonction impliquée
# (2) si le gouverneur du coordonné gauche est un verbe, recensement de ses dépendants
# (3) Ajout des arcs 'coord1' et 'coord2'
# (4) Rattachement des dépendants de verbe (auxiliaires, sujets, etc.) sur la conjonction sils sont partagés par les deux vrbes coordonnés
def coordinations(dsentence):
	nedges = set([])
	liste_coord={}
	#1)
	for edge in dsentence.depforest.edges:
		if edge.label == "dep_coord":
			if not liste_coord.has_key(edge.orig.idx):
				liste_coord[edge.orig.idx]={}
				liste_coord[edge.orig.idx]['dep_coord']=[]
				liste_coord[edge.orig.idx]['coord']=[]
			liste_coord[edge.orig.idx]['dep_coord'].append(edge.dest)
			
		elif edge.label == "coord":
			if not liste_coord.has_key(edge.dest.idx):
				liste_coord[edge.dest.idx]={}
				liste_coord[edge.dest.idx]['dep_coord']=[]
				liste_coord[edge.dest.idx]['coord']=[]
			liste_coord[edge.dest.idx]['coord'].append(edge.orig)
	
		else:
			nedges.add(edge)
	#2)
	liste_verbe={}
	for edge in dsentence.depforest.edges:
		if edge.orig.est_categorie("^V$"):
			if not liste_verbe.has_key(edge.orig.idx):
				liste_verbe[edge.orig.idx]={}				
			if edge.label == "aux_caus":
				liste_verbe[edge.orig.idx]['aux_caus']=edge.dest
			elif edge.label == "aux_pass":
				liste_verbe[edge.orig.idx]['aux_pass']=edge.dest
			elif edge.label == "aux_tps":
				liste_verbe[edge.orig.idx]['aux_tps']=edge.dest		
			elif edge.label == "suj":
				if not liste_verbe[edge.orig.idx].has_key('suj'):
					liste_verbe[edge.orig.idx]['suj']=[]
				liste_verbe[edge.orig.idx]['suj'].append(edge.dest)
			elif re.match("^mod(_v)?$",edge.label) and edge.dest.label.lower() in ["ni","ne","n'"]:
				liste_verbe[edge.orig.idx]['neg']=edge.dest
	nnedges = set([])
	#3)
	for e in liste_coord.keys():
		for e1 in liste_coord[e]['coord']:
				new_edge1 = DepEdge(dsentence.get_vertex(e),"coord1",e1)
				nedges.add(new_edge1)
		for e2 in liste_coord[e]['dep_coord']:
				new_edge2 = DepEdge(dsentence.get_vertex(e),"coord2",e2)
				nedges.add(new_edge2)
	#4)
	for ne in nedges:
		if not (ne.label == "coord1" or ne.label == "coord2"):
			for e in liste_coord.keys():
				for e1 in liste_coord[e]['coord']:
					if ne.dest.idx == e1.idx:
						ne=DepEdge(ne.orig, ne.label, dsentence.get_vertex(e))
					if ne.orig.idx == e1.idx and e1.est_categorie("^V$"):
						for e2 in liste_coord[e]['dep_coord']:
							if liste_verbe.has_key(e2.idx) and liste_verbe.has_key(e1.idx):
								if not liste_verbe[e2.idx].has_key('aux_caus') and liste_verbe[e1.idx].has_key('aux_caus'):
									if ne.label == "aux_caus":
										ne=DepEdge(dsentence.get_vertex(e),"aux_caus",ne.dest)
								if not liste_verbe[e2.idx].has_key('aux_pass') and liste_verbe[e1.idx].has_key('aux_pass'):
									if ne.label == "aux_pass":
										ne=DepEdge(dsentence.get_vertex(e),"aux_pass",ne.dest)
								if not liste_verbe[e2.idx].has_key('aux_tps') and liste_verbe[e1.idx].has_key('aux_tps'):
									if ne.label == "aux_tps":
										ne=DepEdge(dsentence.get_vertex(e),"aux_tps",ne.dest)
								if not liste_verbe[e2.idx].has_key('suj') and liste_verbe[e1.idx].has_key('suj'):
									if ne.label == "suj":
										for s in liste_verbe[e1.idx]['suj']:
											if s.idx == ne.dest.idx:
												ne=DepEdge(dsentence.get_vertex(e),"suj",ne.dest)
								if not liste_verbe[e2.idx].has_key('neg') and liste_verbe[e1.idx].has_key('neg'):
									if ne.label == "mod" and ne.dest.label.lower() in ["ni","ne","n'"]:
										ne=DepEdge(dsentence.get_vertex(e),"mod",ne.dest)
		nnedges.add(ne)			
	dsentence.depforest.edges = nnedges
	return dsentence
	

# Suppression de la ponctuation (toutes les relations 'ponct')
def ponctuation(dsentence):
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if edge.label == "ponct":
			pass
		else:
			nedges.add(edge)
	dsentence.depforest.edges = nedges
	return dsentence


# Listes d'affixes selon les fonctions syntaxiques qu'ils peuvent entretenir
liste_aff2sujet = ["je","j'","tu","il","elle","ils","elles","on"]
liste_aff2cod = ["le","la","les","l\'"]
liste_aff2coi = ["lui","y","en"]
#cas ambigues:
liste_aff2suj_cod_coi = ["nous","vous"]
liste_aff2cod_coi = ["me","m'","te","t'","moi","toi"]
#avec tiret
liste_aff2sujet.append(["-je","-tu","-il","-t-il","-elle","-t-elle","-ils","-t-ils","-elles","-t-elles","-on","-t-on"])
liste_aff2cod.append(["-le","-la","-les"])
liste_aff2coi.append(["-lui","-y","-en"])
#cas ambigues:
liste_aff2suj_cod_coi.append(["-nous","-vous"])
liste_aff2cod_coi.append(["-moi","-toi"])

# Les affixes:
# Dans les relations 'aff':
# suppression de la relation si l'affixe est "se|s'"
# si l'affixe appartient à une des listes non-ambigues, mettre la relation indiquée
def affixes(dsentence):
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if edge.label == "aff":
			if re.match("^se|s\'|s'",edge.dest.label.lower()):
				pass
			elif edge.dest.label.lower() in liste_aff2sujet:
				edge=DepEdge(edge.orig,"suj_v",edge.dest)
				nedges.add(edge)
			elif edge.dest.label.lower() in liste_aff2cod:
				edge=DepEdge(edge.orig,"cod_v",edge.dest)
				nedges.add(edge)
			elif edge.dest.label.lower() in liste_aff2coi:
				edge=DepEdge(edge.orig,"cpl_v",edge.dest)
				nedges.add(edge)
			else:
				nedges.add(edge)
		else:
			nedges.add(edge)
	dsentence.depforest.edges = nedges
	return dsentence

# Les affixes ambigus:
# si l'affixe appartient à des listes ambigues,
# recenser la présence de relations 'sujet' et 'cod' pour chaque verbe
# et ajouter la relation en fonction de leur présence et des choix donnés par la liste
def affixes_ambigus(dsentence):
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if edge.label == "aff":
			sujet=False
			cod=False
			for e in dsentence.depforest.get_deps(edge.orig.idx):
				if e.label == "suj_v":
					sujet=True
				if e.label == "cod_v":
					cod=True
			if edge.dest.label.lower() in liste_aff2suj_cod_coi:
				if sujet:
					if cod:
						edge.label="cpl_v"
					else:
						edge.label="cod_v"
				else:
					edge.label="suj_v"
			elif edge.dest.label.lower() in liste_aff2cod_coi:
				if cod:
					edge.label="cpl_v"
				else:
					edge.label="cod_v"
			else:
				edge.label="cpl_v"			
		nedges.add(edge)
	dsentence.depforest.edges = nedges
	return dsentence


# liste des lemmes de determinants dont il faut retirer la relation "det"
# pour les autres, la relation "det" devient "mod_n"
liste_det_a_supprimer = ["le","la","les","l'","un","une","des","de","d\'","son","sa","ses","mon","ma","mes","ton","ta","tes","notre","nos","votre","vos","leur","leurs","ce","cette","ces"]

# Les déterminants
# suppression des déterminants apparaissant dans la liste ci-dessus
# sinon garder les autres
def determinants(dsentence):
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if edge.label == "det" and edge.dest.label.lower() in liste_det_a_supprimer:
			pass
		elif edge.label == "det":
			edge.label = "mod_n"
			nedges.add(edge)
		else:
			nedges.add(edge)
	dsentence.depforest.edges = nedges
	return dsentence

# Les auxiliaires:
# chainage des sujets et des auxiliaires des verbes
# ex: suj -> aux_tps -> aux_pass -> verbe
# (au lieu de: sujet -> verbe, aux_tps -> verbe, aux_pass -> verbe)
# (1) Recensement des dépendants de chaque verbe
# (2) chainage des dépendants en fonction de leur distribution par verbe
def auxiliaires(dsentence):
	nnedges = set([])
	liste_verbes={}
	#1)
	for edge in dsentence.depforest.edges:
		if edge.label == "aux_tps":
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			liste_verbes[edge.orig.idx]['tps']=edge.dest.idx
		elif edge.label == "aux_pass":
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			liste_verbes[edge.orig.idx]['pass']=edge.dest.idx
		elif edge.label == "aux_caus":
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			liste_verbes[edge.orig.idx]['caus']=edge.dest.idx
		elif edge.label == "suj":
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			if not liste_verbes[edge.orig.idx].has_key('suj'):
				liste_verbes[edge.orig.idx]['suj']=[]
			liste_verbes[edge.orig.idx]['suj'].append(edge.dest.idx)
		elif edge.label == "mod" and edge.dest.label.lower() in ["ni","ne","n'"]:
			if not liste_verbes.has_key(edge.orig.idx):
				liste_verbes[edge.orig.idx]={}
			liste_verbes[edge.orig.idx]['neg']=edge.dest.idx
		else:
			nnedges.add(edge)
	
	#print str(liste_verbes)
	
	#si le verbe a des auxiliaires, et qu'il est dépendant d'un autre élément
	#son premier auxiliaires deviendra dépendant à sa place
	nedges = set([])		
	for edge in nnedges:
		if edge.dest.est_categorie("^V$") and liste_verbes.has_key(edge.dest.idx):
			liste_verbes[edge.dest.idx]['gouv']=edge.orig.idx
			liste_verbes[edge.dest.idx]['fonc']=edge.label
		else:
			nedges.add(edge)
		
	#2)
	for v in liste_verbes.keys():
		if liste_verbes[v].has_key('tps') and liste_verbes[v].has_key('pass') and liste_verbes[v].has_key('caus'):
			# "TPS + PASS + CAUS"
			if liste_verbes[v].has_key('suj'):
				# "SUJET"
				for s in liste_verbes[v]['suj']:
					e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['tps']),"suj",dsentence.get_vertex(s))
					nedges.add(e1)
							
			if liste_verbes[v].has_key('neg'):
				# "NEGATION"
				e0 = DepEdge(dsentence.get_vertex(liste_verbes[v]['tps']),"mod",dsentence.get_vertex(liste_verbe[v]['neg']))
				nedges.add(e0)				
			e2 = DepEdge(dsentence.get_vertex(liste_verbes[v]['pass']),"aux_v",dsentence.get_vertex(liste_verbes[v]['tps']))
			nedges.add(e2)
			e3 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"aux_v",dsentence.get_vertex(liste_verbes[v]['pass']))
			nedges.add(e3)
			e4 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"cod_v",dsentence.get_vertex(v))
			nedges.add(e4)

		elif liste_verbes[v].has_key('tps') and liste_verbes[v].has_key('pass'):
			# "TPS + PASS"
			if liste_verbes[v].has_key('suj'):
				# "SUJET"
				for s in liste_verbes[v]['suj']:
					e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['tps']),"suj",dsentence.get_vertex(s))
					nedges.add(e1)
					
			if liste_verbes[v].has_key('gouv'):
				e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['gouv']),liste_verbes[v]['fonc'],dsentence.get_vertex(liste_verbes[v]['tps']))
				nedges.add(e1)
				
			if liste_verbes[v].has_key('neg'):
				# "NEGATION"
				e0 = DepEdge(dsentence.get_vertex(liste_verbes[v]['tps']),"mod",dsentence.get_vertex(liste_verbe[v]['neg']))
				nedges.add(e0)				
			e2 = DepEdge(dsentence.get_vertex(liste_verbes[v]['pass']),"aux_v",dsentence.get_vertex(liste_verbes[v]['tps']))
			nedges.add(e2)
			e3 = DepEdge(dsentence.get_vertex(v),"aux_v",dsentence.get_vertex(liste_verbes[v]['pass']))
			nedges.add(e3)
			
		elif liste_verbes[v].has_key('tps') and liste_verbes[v].has_key('caus'):
			# "TPS + CAUS"
			if liste_verbes[v].has_key('suj'):
				# "SUJET"
				for s in liste_verbes[v]['suj']:
					e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['tps']),"suj",dsentence.get_vertex(s))
					nedges.add(e1)

			if liste_verbes[v].has_key('gouv'):
				e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['gouv']),liste_verbes[v]['fonc'],dsentence.get_vertex(liste_verbes[v]['tps']))
				nedges.add(e1)
			
			if liste_verbes[v].has_key('neg'):
				# "NEGATION"
				e0 = DepEdge(dsentence.get_vertex(liste_verbes[v]['tps']),"mod",dsentence.get_vertex(liste_verbe[v]['neg']))
				nedges.add(e0)					
			e2 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"aux_v",dsentence.get_vertex(liste_verbes[v]['tps']))
			nedges.add(e2)
			e3 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"cod_v",dsentence.get_vertex(v))
			nedges.add(e3)

		elif liste_verbes[v].has_key('pass') and liste_verbes[v].has_key('caus'):
			# "PASS + CAUS"
			if liste_verbes[v].has_key('suj'):
				# "SUJET"
				for s in liste_verbes[v]['suj']:
					e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['pass']),"suj",dsentence.get_vertex(s))
					nedges.add(e1)

			if liste_verbes[v].has_key('gouv'):
				e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['gouv']),liste_verbes[v]['fonc'],dsentence.get_vertex(liste_verbes[v]['pass']))
				nedges.add(e1)
			
			if liste_verbes[v].has_key('neg'):
				# "NEGATION"
				e0 = DepEdge(dsentence.get_vertex(liste_verbes[v]['pass']),"mod",dsentence.get_vertex(liste_verbe[v]['neg']))
				nedges.add(e0)					
			e2 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"aux_v",dsentence.get_vertex(liste_verbes[v]['pass']))
			nedges.add(e2)
			e3 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"cod_v",dsentence.get_vertex(v))
			nedges.add(e3)
					
		elif liste_verbes[v].has_key('tps'):
			# "TPS"
			if liste_verbes[v].has_key('suj'):
				# "SUJET"	
				for s in liste_verbes[v]['suj']:
					e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['tps']),"suj",dsentence.get_vertex(s))
					nedges.add(e1)

			if liste_verbes[v].has_key('gouv'):
				e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['gouv']),liste_verbes[v]['fonc'],dsentence.get_vertex(liste_verbes[v]['tps']))
				nedges.add(e1)
			
			if liste_verbes[v].has_key('neg'):
				# "NEGATION"
				e0 = DepEdge(dsentence.get_vertex(liste_verbes[v]['tps']),"mod",dsentence.get_vertex(liste_verbes[v]['neg']))
				nedges.add(e0)						
			e2 = DepEdge(dsentence.get_vertex(v),"aux_v",dsentence.get_vertex(liste_verbes[v]['tps']))
			nedges.add(e2)
				
		elif liste_verbes[v].has_key('pass'):
			# "PASS"
			if liste_verbes[v].has_key('suj'):
				# "SUJET"
				for s in liste_verbes[v]['suj']:
					e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['pass']),"suj",dsentence.get_vertex(s))
					nedges.add(e1)	

			if liste_verbes[v].has_key('gouv'):
				e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['gouv']),liste_verbes[v]['fonc'],dsentence.get_vertex(liste_verbes[v]['pass']))
				nedges.add(e1)
					
			if liste_verbes[v].has_key('neg'):
				# "NEGATION"
				e0 = DepEdge(dsentence.get_vertex(liste_verbes[v]['pass']),"mod",dsentence.get_vertex(liste_verbes[v]['neg']))
				nedges.add(e0)	
			e2 = DepEdge(dsentence.get_vertex(v),"aux_v",dsentence.get_vertex(liste_verbes[v]['pass']))
			nedges.add(e2)
			
		elif liste_verbes[v].has_key('caus'):
			# "CAUS"
			if liste_verbes[v].has_key('suj'):
				# "SUJET"
				for s in liste_verbes[v]['suj']:
					e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"suj",dsentence.get_vertex(s))
					nedges.add(e1)		

			if liste_verbes[v].has_key('gouv'):
				e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['gouv']),liste_verbes[v]['fonc'],dsentence.get_vertex(liste_verbes[v]['caus']))
				nedges.add(e1)
			
			if liste_verbes[v].has_key('neg'):
				# "NEGATION"
				e0 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"mod",dsentence.get_vertex(liste_verbes[v]['neg']))
				nedges.add(e0)	
			e2 = DepEdge(dsentence.get_vertex(liste_verbes[v]['caus']),"cod_v",dsentence.get_vertex(v))
			nedges.add(e2)
			
		else:
			if liste_verbes[v].has_key('suj'):	
				# "SUJET"
				for s in liste_verbes[v]['suj']:
					e4 = DepEdge(dsentence.get_vertex(v),"suj",dsentence.get_vertex(s))
					nedges.add(e4)
			if liste_verbes[v].has_key('neg'):
				e0 = DepEdge(dsentence.get_vertex(v),"mod",dsentence.get_vertex(liste_verbes[v]['neg']))
				nedges.add(e0)
				
			if liste_verbes[v].has_key('gouv'):
				e1 = DepEdge(dsentence.get_vertex(liste_verbes[v]['gouv']),liste_verbes[v]['fonc'],dsentence.get_vertex(v))
				nedges.add(e1)	
	
	dsentence.depforest.edges = nedges
	return dsentence

# Liste des prépositions pour lesquels la remontée du sujet est possible:
# V en Vant : suj(V,x) -> suj(Vant,x)
# V pour Ver : suj(V,x) -> suj(Ver,x)
liste_prep_remontee=["en","pour"]
	
# La remontée des sujets:
# - si un verbe est dépendant d'un autre, et qu'ils sont reliés par une préposition dans la liste ci-dessus
# ajouter une relation 'sujet' entre le second verbe et le sujet du premier verbe
# - si un verbe est objet d'un autre, ajouter une relation 'sujet' entre le second verbe et le sujet du premier verbe
def remontee_sujets2(dsentence):
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if edge.label == "obj" and edge.orig.est_categorie("^V$") and edge.dest.est_categorie("^V$"):
			sujet_edges=set([])		
			for e in dsentence.depforest.get_deps(edge.orig.idx):
				if e.label == "suj":
					sujet_edges.add(e)	
			for sujet_edge in sujet_edges:
				rem_sujet_edge = DepEdge(edge.dest,"suj",sujet_edge.dest)
				nedges.add(rem_sujet_edge)
		elif edge.orig.est_categorie("^V$") and edge.dest.est_categorie("^P(\+D)?$"):
			if edge.dest.label in liste_prep_remontee:
				sujet_edges=set([])
				for e in dsentence.depforest.get_deps(edge.orig.idx):
					if e.label == "suj":
						sujet_edges.add(e)
				prep_deps = dsentence.depforest.get_deps(edge.dest.idx)
				for pd in prep_deps:
					if pd.label == "obj" and pd.dest.est_categorie("^V$"):
						for sujet_edge in sujet_edges:
							rem_sujet_edge = DepEdge(pd.dest,"suj",sujet_edge.dest)
							nedges.add(rem_sujet_edge)	

		nedges.add(edge)
	dsentence.depforest.edges = nedges
	return dsentence
	pass

# La transcatégorisation:
# Les ADJECTIFS qui sont dans une relation avec un déterminant (A gouverneur de D) sont transformés en NOMS
# que ce soit dans les relations
# ou dans les sommets
def transcategorisation(dsentence):
	liste_adj2nom = []
	for edge in dsentence.depforest.edges:
		if (edge.label == "det" or edge.dest.est_categorie("^D$")) and edge.orig.est_categorie("^A$"):
			liste_adj2nom.append(edge.orig.idx)
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if edge.orig.idx in liste_adj2nom:
			vertex = DepVertex(edge.orig.label,edge.orig.idx)
			vertex.feats['pos']="N"
			edge = DepEdge(vertex,edge.label,edge.dest)
		if edge.dest.idx in liste_adj2nom:
			vertex = DepVertex(edge.dest.label,edge.dest.idx)
			vertex.feats['pos']="N"
			edge = DepEdge(edge.orig,edge.label,vertex)
		nedges.add(edge)
	dsentence.depforest.edges = nedges
	
	nvertices = set([])
	for vert in dsentence.depforest.vertices:
		if vert.idx in liste_adj2nom:
			vert.feats['pos']="N"
		nvertices.add(vert)
	dsentence.depforest.vertices = nvertices
	
	return dsentence
	pass

# Listes de prépositions (à enrichir)
# ces prépositions permettraient d'identifier les adverbes coorespondants à des mots composés dont le premier mot est une préposition
# dans easy, "à_terme" => cpl_v et non mod_v (car présence de la préposition)
# A ENRICHIR? A LAISSER TOMBER?

liste_prepositions =["à","de","en","pour","avant","après","sur","contre","par"]

# Les adverbes composés:
# Les adverbes composés avec une préposition en premier membre entretiennent une relation 'cpl_v' et non 'mod_n'
# avec leurs verbes
def is_adv_compound(adverbe):
	if re.match("^.+_.+$",adverbe):
		if re.search("^(.+)_(.+)$",adverbe).group(1).lower() in liste_prepositions:
			return True
		else:
			return False
	else:
		return False

# Listes de déterminants (D) correspondants à des prépositions
liste_det2prep = ["du","de","des","d'"]

# Listes de compléménteurs (C) qui entretiennet une relation 'cpl_v' avec leur verbe
# (car ils correspondent à un complémént oblique effacé)
liste_comp_pleins = ["où","pourquoi","comment","combien"]

# Listes de complémenteurs (C) qui entretiennent une relation 'cod_v' avec leur verbe
liste_comp_cod = ["que","si","qu'","s'"]

# Les compléments de verbe:
# Traitement du complément de verbe en fonction:
# - du type de la relation
# - de la catégorie du dépendant: P, ADV, V, N et le reste
def objets_verbaux4(dsentence):
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if edge.orig.est_categorie("^V$"):
			if re.match("^obj$",edge.label):
				edge.label = "cod_v"
			elif re.match("^mod|arg|none|unk$",edge.label):
				if edge.dest.est_categorie("^P(\+(D|PRO))?$"):
					edge.label = "cpl_v"
				elif edge.dest.est_categorie("^ADV$") and is_adv_compound(edge.dest.label):
					edge.label = "cpl_v"
				elif edge.dest.label.lower() in liste_comp_pleins:
					edge.label = "cpl_v"						
				else:
					edge.label = "mod_v"
			elif re.match("^.*obj.*$",edge.label):
				edge.label = "cpl_v"
			elif re.match("^dep$",edge.label):
				if edge.dest.est_categorie("^C$") and edge.dest.label in liste_comp_cod:
					edge.label = "cod_v"
				elif edge.dest.est_categorie("^V$") and (edge.dest.idx - edge.orig.idx) in [1,2,3]:
					edge.label = "cod_v"
				elif edge.dest.est_categorie("^P(\+D)?$"):
					edge.label = "cpl_v"
				else:
					edge.label = "mod_v"
		nedges.add(edge)
	dsentence.depforest.edges = nedges	
	return dsentence

# La remontée des prépositions (OU PLUTOT LA DESCENTE):
# dans eays, nous avons: _fonc(x,PREP) + obj(PREP,y) => _fonc(x,y)
# (1) recensement des gouverneurs et dépendants de prépositions
# (2) Pour chaque prépositions:
#	-remontée
#	-ou reconstruction si la remontée n'est pas possible
def prepositions(dsentence):
	nedges = set([])
	liste_prep={}
	#1)
	for edge in dsentence.depforest.edges:
		if edge.orig.est_categorie("^P(\+.*)?$") or edge.dest.est_categorie("^P(\+.*)?$"):
			if edge.orig.est_categorie("^P(\+.*)?$") and edge.label == "obj":
				if not liste_prep.has_key(edge.orig.idx):
					liste_prep[edge.orig.idx]={}
				if not liste_prep[edge.orig.idx].has_key('obj'):
					liste_prep[edge.orig.idx]['obj']=[]
				liste_prep[edge.orig.idx]['obj'].append(edge.dest)
			if edge.orig.est_categorie("^P(\+.*)?$") and edge.label == "arg":
				if not liste_prep.has_key(edge.orig.idx):
					liste_prep[edge.orig.idx]={}
				if not liste_prep[edge.orig.idx].has_key('arg'):
					liste_prep[edge.orig.idx]['arg']=[]
				liste_prep[edge.orig.idx]['arg'].append(edge.dest)
			if edge.dest.est_categorie("^P(\+.*)?$"):
				if not liste_prep.has_key(edge.dest.idx):
					liste_prep[edge.dest.idx]={}
				liste_prep[edge.dest.idx]['gouv']=edge.orig
				liste_prep[edge.dest.idx]['label']=edge.label
		else:
			nedges.add(edge)

	liste_prep_to_remove=[]
	for p in liste_prep.keys():
		if liste_prep[p].has_key('arg'):
			for arg in liste_prep[p]['arg']:
				#arg=liste_prep[p]['arg']
				if liste_prep[p].has_key('obj'):
					if liste_prep.has_key(arg.idx) and liste_prep[arg.idx].has_key('obj'):
						for obj_p in liste_prep[p]['obj']:
							for obj_arg in liste_prep[arg.idx]['obj']:
								e1=DepEdge(obj_p,"mod",obj_arg)
								nedges.add(e1)
						e2=DepEdge(liste_prep[p]['gouv'],liste_prep[p]['label'],obj_p)
						nedges.add(e2)
						liste_prep_to_remove.append(p)
						liste_prep_to_remove.append(arg.idx)					
				else:
					if liste_prep.has_key(arg.idx) and liste_prep[arg.idx].has_key('obj'):
						for obj_arg in liste_prep[arg.idx]['obj']:
							e1=DepEdge(liste_prep[p]['gouv'],liste_prep[p]['label'],obj_arg)
							e2=DepEdge(obj_arg,"comp",dsentence.get_vertex(p))
							nedges.add(e1)
							nedges.add(e2)
						liste_prep_to_remove.append(p)
						liste_prep_to_remove.append(arg.idx)
	for p in liste_prep_to_remove:
		if p in liste_prep:
			del liste_prep[p]
					
	#2)
	for p in liste_prep.keys():
		#reconstruction
		if liste_prep[p].has_key('obj') and liste_prep[p].has_key('gouv'):
			for obj_p in liste_prep[p]['obj']:
				label = liste_prep[p]['label']
				e=DepEdge(liste_prep[p]['gouv'],label,obj_p)
				nedges.add(e)
		else:
			#reconstruction
			if liste_prep[p].has_key('obj'):
				for obj_p in liste_prep[p]['obj']:
					e=DepEdge(dsentence.get_vertex(p),"obj",obj_p)
					nedges.add(e)
			if liste_prep[p].has_key('gouv'):
				e=DepEdge(liste_prep[p]['gouv'],liste_prep[p]['label'],dsentence.get_vertex(p))
				nedges.add(e)
	dsentence.depforest.edges = nedges
	return dsentence

# La remontée des complémenteurs
# (fonction à l'image de la remontée des prépositions)	
def complementeurs2(dsentence):
	nedges = set([])
	liste_comp={}
	#1)
	for edge in dsentence.depforest.edges:
		if edge.orig.est_categorie("^C$") and edge.label == "obj":
			if not liste_comp.has_key(edge.orig.idx):
				liste_comp[edge.orig.idx]={}
			liste_comp[edge.orig.idx]['obj']=edge.dest
		elif edge.dest.est_categorie("^C$"):
			if not liste_comp.has_key(edge.dest.idx):
				liste_comp[edge.dest.idx]={}
			liste_comp[edge.dest.idx]['gouv']=edge.orig
			liste_comp[edge.dest.idx]['label']=edge.label
		else:
			nedges.add(edge)
	
	#2)
	for c in liste_comp.keys():
		#remontée
		if liste_comp[c].has_key('obj') and liste_comp[c].has_key('gouv'):
			label = liste_comp[c]['label']		
			e1=DepEdge(liste_comp[c]['gouv'],label,liste_comp[c]['obj'])
			e2=DepEdge(liste_comp[c]['obj'],"comp",dsentence.get_vertex(c))
			nedges.add(e1)
			nedges.add(e2)
		else:
			#reconstruction
			if liste_comp[c].has_key('obj'):
				#e3=DepEdge(dsentence.get_vertex(c),"obj",liste_comp[c]['obj'])
				e3=DepEdge(liste_comp[c]['obj'],"comp",dsentence.get_vertex(c))
				nedges.add(e3)
				
			if liste_comp[c].has_key('gouv'):
				e4=DepEdge(liste_comp[c]['gouv'],liste_comp[c]['label'],dsentence.get_vertex(c))
				nedges.add(e4)
			
	dsentence.depforest.edges = nedges
	return dsentence

# Les adverbes de négation:
# Suppression des relations mettant en jeu les adverbes "ni,ne,n'"
def adv_negation(dsentence):
	nedges = set([])
	liste_neg={}
	for edge in dsentence.depforest.edges:
		if re.match("^mod(_v)?$",edge.label) and edge.dest.est_categorie("ADV$") and re.match("^(n'|ne|ni)$",edge.dest.label.lower()):
			liste_neg[edge.dest.idx]=edge.orig.idx
			pass
		else:
			nedges.add(edge)	
	nnedges=set([])	
	dsentence.depforest.edges = nedges
	return dsentence

# Les appositions:
# ATTENTION RELATION NON-ANNOTEE DANS LE PIVOT
# Pour chaque relation où le dépendant et le gouverneur sont nominaux,
# sil existe une et unique ponctuation de séparation
# mettre une relation 'app'
def appositions(dsentence):
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if edge.orig.est_categorie("^N|PRO|P\+PRO$") and edge.dest.est_categorie("^N|PRO|P\+PRO$") and re.match("^none|dep|arg|mod$",edge.label):
			nb_ponc = 0
			for v in dsentence.depforest.vertices:
				if v.idx > min(edge.orig.idx,edge.dest.idx) and  v.idx < max (edge.orig.idx,edge.dest.idx):
					if v.label in ["--LBR--","<LBR>","<C>",",",":","<D>","-","[","("]:
						nb_ponc += 1						
			if nb_ponc == 1:
				edge=DepEdge(edge.orig,"app",edge.dest)
		nedges.add(edge)
	dsentence.depforest.edges = nedges
	return dsentence

# Les juxtapositions:
# ATTENTION RELATION NON-ANNOTEE DANS LE PIVOT
# Pour chaque relation où le dépendant et le gouverneur sont verbaux,
# si une (ou plusieurs) ponctuation(s) les sépare,
# mettre une relation 'juxt'
def juxtapositions2(dsentence):
	nedges = set([])
	dsentence.depforest.sort_dest()
	for edge in dsentence.depforest.edges:
		if edge.orig.est_categorie("^V$") and edge.dest.est_categorie("^V$") and re.match("^none|dep|arg|mod|unk$",edge.label):
			ponct=False
			for v in dsentence.depforest.vertices:
				if v.feats.has_key('pos'):
					if v.feats['pos'].lower()== "ponct" and v.idx > min(edge.orig.idx,edge.dest.idx) and  v.idx < max (edge.orig.idx,edge.dest.idx):
						ponct=True
			if ponct:
				edge=DepEdge(edge.orig,"juxt",edge.dest)							
		nedges.add(edge)
			
	dsentence.depforest.edges = nedges
	return dsentence

# Les compléments des attributs du sujet adjectivaux:
def attributs(dsentence):
	taille_initiale=len(dsentence.depforest.edges)
	liste_vb = []
	# Recensement des verbes
	for edge in dsentence.depforest.edges:
		if edge.label == "ats" and edge.orig.est_categorie("^V$") and edge.dest.est_categorie("^A$"):
			liste_vb.append(edge.orig.idx)
	liste_ats={}
	nedges=set([])

	# Pour chaque verbe, recencement de ses dépendants "ats", "*-obj" et "mod"
	for edge in dsentence.depforest.edges:
		if edge.orig.idx in liste_vb:
			for vb in liste_vb:
				if not liste_ats.has_key(vb):
					liste_ats[vb]={}
			if edge.label == "ats" and edge.dest.est_categorie("^A$"):
				liste_ats[vb]['adj']=edge.dest.idx
				nedges.add(edge)
			elif edge.label in ['de_obj','p_obj','mod','obj'] and edge.dest.est_categorie("^P(\+D)?$"):
				if not liste_ats[vb].has_key('args'):
					liste_ats[vb]['args']=[]
				toc={}
				toc['fonc']=edge.label
				toc['prep']=edge.dest.idx
				liste_ats[vb]['args'].append(toc)
			else:
				nedges.add(edge)
		else:
			nedges.add(edge)
	# Pour chaque verbe concerné, rattachement du dépendant à l'attribut.
	for vb in liste_ats.keys():
		if liste_ats[vb].has_key('adj') and liste_ats[vb].has_key('args'):
			for arg in liste_ats[vb]['args']:
				if arg['prep'] > liste_ats[vb]['adj']:
					ne = DepEdge(dsentence.get_vertex(liste_ats[vb]['adj']),"dep",dsentence.get_vertex(arg['prep']))
					nedges.add(ne)
				else:
					ne = DepEdge(dsentence.get_vertex(vb),arg['fonc'],dsentence.get_vertex(arg['prep']))
					nedges.add(ne)
		elif liste_ats[vb].has_key('args'):
			for arg in liste_ats[vb]['args']:
				ne = DepEdge(dsentence.get_vertex(vb),arg['fonc'],dsentence.get_vertex(arg['prep']))
				nedges.add(ne)
	
	if not taille_initiale == len(nedges):
		print "|> probleme: nombre d'arcs dans <attributs()>!"
		sys.exit()
	dsentence.depforest.edges = nedges
	return dsentence

# Listes de conjonctions 'vides' utilisées dans les comparatives
liste_conj_comp = ["que","qu'"]

# Les comparatives:
# (1) Recensement des dépendants et gouverneurs des conjonctions listées ci-dessus
# 	pouvant correspondre à une comparative
# (2) Remontée des éléments du dépendant sur la gouverneur et ajout d'une relation 'comp'
#	sinon, reconstruction de la relation gouvernante, mais ajout 'quand meme' de la relation 'comp'
def comparatives(dsentence):
	nedges = set([])
	liste_conj={}
	#1)
	for edge in dsentence.depforest.edges:
		if edge.label == "obj" and edge.orig.est_categorie("^C$") and edge.orig.label.lower() in liste_conj_comp:
			if not liste_conj.has_key(edge.orig.idx):
				liste_conj[edge.orig.idx]={}
			liste_conj[edge.orig.idx]['comp']=edge.dest
			liste_conj[edge.orig.idx]['conj']=edge.orig
		elif re.match("^mod(_.)?|arg_a|none$",edge.label) and edge.dest.est_categorie("^C$") and edge.dest.label.lower() in liste_conj_comp:
			if not liste_conj.has_key(edge.orig.idx):
				liste_conj[edge.dest.idx]={}
			liste_conj[edge.dest.idx]['adj']=edge.orig		
			liste_conj[edge.dest.idx]['fonc']=edge.label
			liste_conj[edge.dest.idx]['conj']=edge.dest	
		else:	
			nedges.add(edge)
	#2)
	for c in liste_conj.keys():
		if liste_conj[c].has_key('adj') and liste_conj[c].has_key('comp'):
			e1= DepEdge(liste_conj[c]['adj'],"mod",liste_conj[c]['comp'])
			e2= DepEdge(liste_conj[c]['comp'],"comp",liste_conj[c]['conj'])
			nedges.add(e1)
			nedges.add(e2)
		elif liste_conj[c].has_key('adj'):
			e0= DepEdge(liste_conj[c]['adj'],liste_conj[c]['fonc'],liste_conj[c]['conj'])
			nedges.add(e0)
		elif liste_conj[c].has_key('comp'):
			#e0= DepEdge(liste_conj[c]['conj'],"obj",liste_conj[c]['comp'])
			e0= DepEdge(liste_conj[c]['comp'],"comp",liste_conj[c]['conj'])
			nedges.add(e0)
		else:
			sys.exit()
	dsentence.depforest.edges = nedges
	return dsentence

# Traduction des labels de relations dans la liste de dépendances
def traduction(dsentence):
	nedges = set([])
	for edge in dsentence.depforest.edges:
		if not edge.label == "head" and not edge.orig.idx == -1:
			edge=DepEdge(edge.orig, traduction_directe(edge.orig.get_categorie(),edge.label),edge.dest)
			nedges.add(edge)
		else:
			pass
	dsentence.depforest.edges = nedges
	return dsentence
	
# Application des règles
def applications_regles(dsentence):
	ds=dsentence
	#print "--------------------------------------------------"
	ds=transcategorisation(ds)
	ds=juxtapositions2(ds)
	ds=appositions(ds)
	ds=affixes(ds)	
	ds=attributs(ds)
	ds=objets_verbaux4(dsentence)	
	ds=coordinations(dsentence)
	ds=ponctuation(ds)
	ds=determinants(ds)
	ds=auxiliaires(ds)
	ds=prepositions(ds)
	ds=adv_negation(ds)
	ds=complementeurs2(ds)
	lecture_fichier_trad("../src/traduction.txt")
	ds=traduction(ds)
	ds=affixes_ambigus(ds)
	
	return ds

# Application d'aucunes règles.
def applications_aucune_regles(dsentence):
	return dsentence	

#affiche_dico_trad()


