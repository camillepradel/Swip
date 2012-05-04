#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#

# Modularisation des traitements (lecture,conversion,écriture)
# - lecture de fichiers/dossiers et extractions de leurs données (arbres ou structures de données)
# - conversion d'abres ou de structures de données

import re
import os
import sys

import PennTreeBankReader
from LabelledTree import *
from FunctionalLabelling import *
from DependencyTree import *
from PivotReader import *
from EasyReader import *
from easy_conversion import *
from easy_chunking import *

#Pour la conversion (simplification) de certaines relations
dico={}
dico['unk']='dep'
dico['mod_cleft']='mod'
dico['mod#']='mod'
dico['p_obj_agt']='p_obj'
dico['arg_comp']='arg'
dico['arg_cons']='arg'
dico['arg_a']='arg'

# implémentée quand il y avait des problemes encode/decode...		
def codec(string):
	try:
		return string
	except UnicodeEncodeError:
		return string.encode('latin1')
	except UnicodeDecodeError:
		return string.decode('latin1')

# Parse d'un fichier en raw texte (déja tokénisé) à l'aide d'un parser, d'une grammaire, et du nom du fichier résultat
def parsing(file,parser,grammaire,temp):
	parse_test = "cat "+file+"| java -Xmx2048m -jar "+parser+" -gr "+grammaire
	os.system(parse_test+" > "+temp)

# Lecture d'un fichier au format ptb et Création d'une liste d'objets labelledtree
def file2labelledtree_list(file,noponct=False,tagfixes=True):
	stream = open(file)
	reader = PennTreeBankReader.PtbReader()
	labelledtree_list = reader.read_mrg(stream)
	stream.close()
	fixer = Tagfixer()
	if not tagfixes:
		print "\t> Tagfixes OFF"
	if noponct:
		print "\t> Suppression de la ponctuation ON"
	for lb in labelledtree_list:
		decorate_tree(lb)
		if tagfixes:
			lb.tagfixes(fixer)
		if noponct:
			lb.suppr_PONCT()
	return labelledtree_list

# Labelling fonctionnel d'une liste d'objets labelledtree (arguments: méthode et traits utilisés)
def funlabelling_labelledtree_list(liste,funmethod,funparams,svm_names,svm_types):
	ptable = sym4_table()
	treenum = 0
	fixer = Tagfixer()	
	flabeller =  FunctionLabeller(funparams,svm_names,svm_types,method=funmethod)  	
	for tree in liste:	
	    treenum += 1
	    funlabelling_labelledtree(tree,treenum,flabeller,ptable,fixer)

# Labelling fonctionnel d'un objet labelledtree
def funlabelling_labelledtree(tree,num,flabeller,ptable,fixer):
    if tree.label == '':
        tree = tree.children[0]
    tree.clitics_downwards()
    tree.head_annotate(ptable,0,True)	
    if flabeller <> None:
        flabeller.label_tree(tree)	
    nodes = LabelledTree.tree_nodes(tree)
    for node in nodes:
        if node.is_terminal_sym():
            node.label = fixer.map(node.label)
        node.set_Treenum(num)

# Lecture d'un dossier contenant des fichiers au format pivot et transformation de ceux-ci en objets dgraph
# ATTENTION: j'utilise ici le lecteur de fichiers-pivot utilisant des expressions regulieres!!!
# L'argument "fine_types" permet de spécifier si l'on souhaite avoir la catégorie du gouverneur avec le label
def dirpivot2dgraph_list(directory,fine=False,easy=False):
	pr = PReader()
	ptree_list = pr.parse_dir_pivot(directory)
	dsentences ={}
	cpt=0
	for pt in ptree_list:
		ds = pt.PTree2DepSentence()
		ds.depforest.translate_labels(dico)
		if fine:
			ds.fine_types()
		if easy:
			ds=easy_like(ds)
		dsentences[cpt]=ds.depforest
		cpt+=1		
	return dsentences

# Lecture d'un fichier pivot et renvoi du dgraph correspondant	
def pivot_file2dgraph(fichier,fine_types=False):
	pr = PReader()
	ptree = pr.kiddy_parse_pivot(fichier)
	ds = ptree.PTree2DepSentence()
	ds.depforest.translate_labels(dico)
	if fine_types:
		ds.fine_types()
	return ds

# Application des heuristiques de MFalco sur une liste de Deptree
def apply_heuristiques_deptree_list(liste):
	deptree_list = {}
	cpt=0
	for tree in liste:
		deptree_list[cpt] = apply_heuristiques_deptree(tree)
		cpt+=1
	return deptree_list

# Application des heuristiques sur un Deptree
def apply_heuristiques_deptree(tree):
	ptable = sym4_table()
	fixer = Tagfixer()
	
	nodes = LabelledTree.tree_nodes(tree)
	for node in nodes:
		if node.is_terminal_sym():
			node.label = fixer.map(node.label)
		node.set_Treenum(0)
	
	deptree = LabTree_2_EnhancedDepTree(tree, gestion_comp = False,long_distance_mark = False,head_table = ptable)
	return deptree

# Conversion d'une liste de ptree en une liste de dgraph
def ptree_list2dgraph_list(liste):
	dgraph_list={}
	cpt=0
	for pt in liste:
		ds = liste[pt].PTree2DepSentence()
		dgraph_list[cpt]=ds.depforest
		dgraph_list[cpt].translate_labels(dico)
		cpt+=1

# Conversion d'une liste de deptree en une liste de dgraph			
def deptree_list2dgraph_list(liste):
	dgraph_list={}
	cpt=0
	for i in liste.keys():
		tree = liste[i]
		dgraph_list[cpt] = deptree2dgraph(tree)
		cpt+=1
	return dgraph_list

# Conversion d'un deptree en un dgraph		
def deptree2dgraph(deptree):
    dg = deptree.DependencyTree2Dgraph()
    dg.FIX_missinghead()
    dg.translate_labels(dico)
    return dg
  
# Conversion d'une liste de dgraph en une liste de depsentence 
def dgraph_list2dsentence_list(liste,file="FILE",validator="VALIDATOR",date="DATE"):
	dsentence_list={}
	for dg in liste.keys():
		ds = DepSentence(dg,file,validator,date,liste[dg])
		ds.lower_labels()
		dsentence_list[dg] = ds
	return dsentence_list

#Objets easy:
# 'chunks': objet chunks
# 'deps': objet depsentence
 
# Conversion d'une liste de depsentence en une liste d'objets easy   
def dsentence_list2easy_list(liste):
	easy_chunksdeps={}
	for ds in liste:
		dsent = liste[ds]
		liste_chunks = set_chunks(dsent)
		chunks = get_chunks(dsent,liste_chunks)
		easy_chunksdeps[ds]={}
		easy_chunksdeps[ds]['chunks'] = chunks
		dsent = applications_regles(dsent)
		easy_chunksdeps[ds]['deps'] = dsent
	return easy_chunksdeps

# Extraction des DepSentence d'une liste d'objets easy	
def easy_list2dsentence_list(liste):
	ds_list={}
	for e in liste.keys():
		ds_list[e] = liste[e]['deps']
	return ds_list

# Conversion d'une liste d'objets easy en chaines de caracteres		
def easy_list2string(easy_list,file, ROOT_INDEX=0):
	string = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
	string += "<!DOCTYPE DOCUMENT SYSTEM \"easy.dtd\">\n"
	string += "<DOCUMENT fichier=\""+file+'.xml'+"\" id=\"corpus\" date=\"_DATE_\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"	
	for e in easy_list:
		string += "<E id=\"E"+str(e)+"\">\n"
		string += sortie_xml_chunk(easy_list[e]['chunks'])
		string += sortie_xml_dependances(easy_list[e]['deps'], ROOT_INDEX=ROOT_INDEX)
		string += "</E>\n"
	string += "</DOCUMENT>\n"
	return string

# Lecture d'un fichier easy et renvoi de la liste de dgraph correspondante	
def easy_file2dgraph_list(file,add=None):
	dgraph_list={}
	er = EasyReader()
	chunksdeps = er.read_xml(file)
	for i in chunksdeps.keys():
		dgraph_list[i] = chunksdeps[i]['deps']
	if add:
		dgraph_list = normalize_dgraph_list(dgraph_list,add)
	return dgraph_list

# Lecture d'un ficher .add (listes des index d'énoncé) et normalization d'une liste d'objet Dgraph	
def normalize_dgraph_list(dgraph_list,fichier_add=None):
	if fichier_add:
		fadd = open(fichier_add)
		association={}
		cpt=0
		lines = fadd.readlines()
		# Association des indexs d'enonce avec un compteur incrémental
		for l in lines:
			i = int(re.search("^E(\d+)$",l).group(1))
			association[i] = cpt
			cpt+=1
		new_dgraph_list={}
		# Normalisation de la liste de dgraph
		for dg in dgraph_list.keys():
			new_dgraph_list[ association[dg] ] = dgraph_list[dg]
		return new_dgraph_list
	else:
		return dgraph_list
	
# Conversion du contenu d'un fichier xml easy en une liste de chaines de caractères
def easy_file2ds_string_list(file,date="DATE",validators="VALIDATOR"):
	ds_string_list=""
	er = EasyReader()
	chunksdeps = er.read_xml(file)
	ds_string_list={}
	for i in chunksdeps.keys():
		sentence = ""
		dg = chunksdeps[i]['deps']
		ds_string_list[i] = "sentence(\n"
		ds_string_list[i] += "id("+str(i)+")\n"
		ds_string_list[i] += "date("+date+")\n"
		ds_string_list[i] += "validators("+validators+")\n"
		ds_string_list[i] += "sentence_form("
		for t in chunksdeps[i]['chunks'].tokens.keys():
			token = chunksdeps[i]['chunks'].tokens[t]
			sentence += token.forme + " "
		ds_string_list[i] += sentence
		ds_string_list[i] += ")\n"
		ds_string_list[i] += "surf_deps(\n"
		ds_string_list[i] += dg.triples2string(True)
		ds_string_list[i] += ")\n"
		ds_string_list[i] += "features(\n"
		ds_string_list[i] += ")\n"
		ds_string_list[i] += ")\n"
		print ds_string_list[i]