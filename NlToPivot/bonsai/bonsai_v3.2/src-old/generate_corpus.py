#!/usr/bin/env python -O
# -*- coding: iso-8859-1 -*-
#

# Generer dans un dossier, les différentes versions du corpus de test en fonction des étapes
# (parsing, funlabelling, applications des heuristiques et dépendances)
# Il faut spécifier TOUS les arguments: l'adresse du parser, des parametres, du raw et des différents gold

# Exemples de versions:
#-raw
#-du raw au ptb
#-du raw au fun
#-du gold ptb au fun
#-etc.

# Aurait surement besoin de refactoring plus bas...
# car je fais toujours le meme traitement...

# Pour Easy,
# - il faudrait annoter le gold pivot en easy
# - je convertis le pivot en easy 1) dans un fichier xml et 2) dans un dossier comprenant chaque énoncé danns un fichier distinct au format pivot et annoté easy

import re
import os
import sys
import eval_aux
from svm_features import *
from optparse import OptionParser
from steps import *
from EasyReader import *

# Main Program
##################
# Command Line interface
usage = """	
           %prog --dir <dossier> -- parser <parser> --params <params> --method <method> --grammaire <grammaire> --train <fichier> --raw <fichier> --gold_ftb <fichier> --gold_fun <fichier> --gold_pivot <fichier>
           
           [TOUS LES ARGUMENTS SONT OBLIGATOIRES]

   %prog --help .
   """

parser=OptionParser(usage=usage)
parser.add_option("--dir",dest="out_dir",default=None,metavar='<dossier>',help="Dossier ou seront stockes les differentes etapes du traitement")
parser.add_option("--parser",dest="parser",default=None,metavar='<parser>',help="Parser de Berkeley")
parser.add_option("--params",dest="params",default=None,metavar='<params>',help="Parametres svm (ex: ../parser/params/foo9)")
parser.add_option("--method",dest="method",default="svm",metavar='<method>',help="La methode de labelling utilisee (svm)")
parser.add_option("--grammaire",dest="grammaire",default=None,metavar='<grammaire>',help="Grammaire utilisee par le parser")
parser.add_option("--train",dest="train_file",default=None,metavar='<fichier>',help="Fichier ftb d'apprentissage au format ptb sans les labels fonctionnels predicatifs")
parser.add_option("--raw",dest="raw_file",default=None,metavar='<fichier>',help="Fichier raw (tokenise et segmente)")
parser.add_option("--gold_ftb",dest="gold_ftb",default=None,metavar='<fichier>',help="Fichier gold ftb au format ptb sans les labels fonctionnels predicatifs")
parser.add_option("--gold_fun",dest="gold_fun",default=None,metavar='<fichier>',help="Fichier gold ftb au format ptb avec les labels fonctionnels predicatifs")
parser.add_option("--gold_pivot",dest="gold_pivot",default=None,metavar='<dossier>',help="Dossier contenant les fichiers gold pivot")
(opts,args) = parser.parse_args()

#OPTIONS
#print opts

out_dir = str(opts.out_dir)
parser = str(opts.parser)
params = str(opts.params)
methode = str(opts.method)
grammaire = str(opts.grammaire)
train_file = str(opts.train_file)
raw_file = str(opts.raw_file)
gold_ftb = str(opts.gold_ftb)
gold_fun = str(opts.gold_fun)
gold_pivot = str(opts.gold_pivot)

# Affichage des arguments
print "-------------------------------"
print "  DIR : "+out_dir
print "PARSER: "+parser
print "PARAMS: "+params
print "METHOD: "+methode
print "GRAMM : "+grammaire
print "TRAIN : "+train_file
print "  RAW : "+raw_file
print "  FTB : "+gold_ftb
print "  FUN : "+gold_fun
print "PIVOT : "+gold_pivot
print "-------------------------------\n"

# Si le dossier existe, on le vide
# sinon, on le crée
# Alors faites attention!
if os.path.isdir(out_dir):
	print "# Réinitialisation du dossier ["+out_dir+"]"
	os.system("rm -Rf "+out_dir+"/*")
elif not os.path.isdir(out_dir):
	print "# Creation du dossier ["+out_dir+"]"
	os.system("mkdir "+out_dir)

#########
# TRAIN #
#########
if os.path.isfile(train_file):
	print "\nTRAIN:"
	print "# Copie du fichier ["+train_file+"] dans le dossier ["+out_dir+"]"
	os.system("cp "+train_file+" "+out_dir+"/p7-107.train.ptb")
else:
	print "! le fichier ["+train_file+"] n'existe pas !"

#######
# RAW #
#######
if os.path.isfile(raw_file):
	print "\nRAW:"
	print "# Copie du fichier ["+raw_file+"] dans le dossier ["+out_dir+"]"
	os.system("cp "+raw_file+" "+out_dir+"/p7-107.raw")
	
	#raw->ftb
	temporaire = out_dir+"/p7-107.raw2ptb.ptb"
	print "# Parsing du fichier ["+raw_file+"]"
	parsing(raw_file,parser,grammaire,temporaire)
	print "# Impression dans le fichier ["+temporaire+"]"
	
	#ftb->fun
	print "# Lecture du fichier ["+temporaire+"]"
	test_labelledtree_list = file2labelledtree_list(temporaire,tagfixes=False)
	print "# Ajout des labels fonctionnels"
	funlabelling_labelledtree_list(test_labelledtree_list,methode,params,SVM_features.names,SVM_features.types)
	file = out_dir+"/p7-107.raw2ptb2fun.fun"
	print "# Impression dans le fichier ["+file+"]"
	f = open(file,"w")
	for t in test_labelledtree_list:
		f.write(t.printf()+"\n")
			
	#fun->pivot		
	print "# Applications des heuristiques"
	test_deptree_list = apply_heuristiques_deptree_list(test_labelledtree_list)
	test_deps_list = deptree_list2dgraph_list(test_deptree_list)
	dir_piv = out_dir+"/p7-107.raw2ptb2fun2pivot/"
	if not os.path.isdir(dir_piv):
		print "# Creation du dossier ["+dir_piv+"]"
		os.system("mkdir "+dir_piv)
	cpt=0
	print "# Impression des fichiers pivot dans le dossier ["+dir_piv+"]"
	for dg in test_deps_list:
		ds = DepSentence(cpt,file,"AUTO","DATE",test_deps_list[dg])
		g = open(dir_piv+"ftb_"+str(cpt)+".piv","w")
		string = "sentence(\n"
		string += ds.id2str()
		string += ds.date2str()
		string += ds.validator2str()
		string += ds.sentence_form()
		string += ds.surfdeps2str()
		string += ds.feats2str(True)
		string += ")"
		g.write(string)
		cpt+=1

	os.system("mkdir "+out_dir+"/p7-107.raw2ptb2fun2pivot2easy/")	
	easy_file = out_dir+"/p7-107.raw2ptb2fun2pivot2easy.xml"
	print "# Conversion du dossier pivot ["+ dir_piv +"] en fichier easy ["+easy_file+"]"
	ef = open(easy_file,"w")
	dsentence_list = dgraph_list2dsentence_list(test_deps_list)
	easy_list = dsentence_list2easy_list(dsentence_list)
	xml = easy_list2string(easy_list,'fichier',ROOT_INDEX=-1)	
	ef.write(xml)
	
	dir_easy = out_dir+"/p7-107.raw2ptb2fun2pivot2easy/"
	if not os.path.isdir(dir_easy):
		print "# Creation du dossier ["+dir_easy+"]"
		os.system("mkdir "+dir_easy)
	cpt=0
	print "# Impression des fichiers easy dans le dossier ["+dir_easy+"]"	
	ds_list = easy_list2dsentence_list(easy_list)
	for i in ds_list.keys():
		ds = ds_list[i]
		g = open(dir_easy+"ftb_"+str(cpt)+".piv","w")
		string = "sentence(\n"
		string += ds.id2str()
		string += ds.date2str()
		string += ds.validator2str()
		string += ds.sentence_form()
		string += ds.surfdeps2str()
		string += ds.feats2str(True)
		string += ")"
		g.write(string)
		g.close()
		cpt+=1	
else:
	print "! le fichier ["+raw_file+"] n'existe pas !"

############
# GOLD FTB #
############
if os.path.isfile(gold_ftb):
	print "\nGOLD FTB:"
	print "# Copie du fichier ["+gold_ftb+"] dans le dossier ["+out_dir+"]"
	os.system("cp "+gold_ftb+" "+out_dir+"/p7-107.goldptb.ptb")
	
	temporaire = out_dir+"/p7-107.goldptb.ptb"	
	#ftb->fun
	print "# Lecture du fichier ["+temporaire+"]"
	test_labelledtree_list = file2labelledtree_list(temporaire,tagfixes=False)
	print "# Ajout des labels fonctionnels"
	funlabelling_labelledtree_list(test_labelledtree_list,methode,params,SVM_features.names,SVM_features.types)
	file = out_dir+"/p7-107.goldptb2fun.fun"
	print "# Impression dans le fichier ["+file+"]"
	f = open(file,"w")
	for t in test_labelledtree_list:
		f.write(t.printf()+"\n")
			
	#fun->pivot		
	print "# Applications des heuristiques"
	test_deptree_list = apply_heuristiques_deptree_list(test_labelledtree_list)
	test_deps_list = deptree_list2dgraph_list(test_deptree_list)
	dir_piv = out_dir+"/p7-107.goldptb2fun2pivot/"
	if not os.path.isdir(dir_piv):
		print "# Creation du dossier ["+dir_piv+"]"
		os.system("mkdir "+dir_piv)
	cpt=0
	print "# Impression des fichiers pivot dans le dossier ["+dir_piv+"]"
	for dg in test_deps_list:
		ds = DepSentence(cpt,file,"AUTO","DATE",test_deps_list[dg])
		g = open(dir_piv+"ftb_"+str(cpt)+".piv","w")
		string = "sentence(\n"
		string += ds.id2str()
		string += ds.date2str()
		string += ds.validator2str()
		string += ds.sentence_form()
		string += ds.surfdeps2str()
		string += ds.feats2str(True)
		string += ")"
		g.write(string)
		cpt+=1

	os.system("mkdir "+out_dir+"/p7-107.goldptb2fun2pivot2easy/")
	easy_file = out_dir+"/p7-107.goldptb2fun2pivot2easy.xml"
	print "# Conversion du dossier pivot ["+ dir_piv +"] en fichier easy ["+easy_file+"]"
	ef = open(easy_file,"w")
	dsentence_list = dgraph_list2dsentence_list(test_deps_list)
	easy_list = dsentence_list2easy_list(dsentence_list)
	xml = easy_list2string(easy_list,'fichier',ROOT_INDEX=-1)	
	ef.write(xml)

	dir_easy = out_dir+"/p7-107.goldptb2fun2pivot2easy/"
	if not os.path.isdir(dir_easy):
		print "# Creation du dossier ["+dir_easy+"]"
		os.system("mkdir "+dir_easy)
	cpt=0
	print "# Impression des fichiers easy dans le dossier ["+dir_easy+"]"	
	ds_list = easy_list2dsentence_list(easy_list)
	for i in ds_list.keys():
		ds = ds_list[i]
		g = open(dir_easy+"ftb_"+str(cpt)+".piv","w")
		string = "sentence(\n"
		string += ds.id2str()
		string += ds.date2str()
		string += ds.validator2str()
		string += ds.sentence_form()
		string += ds.surfdeps2str()
		string += ds.feats2str(True)
		string += ")"
		g.write(string)
		g.close()
		cpt+=1	
else:
	print "! le fichier  ["+gold_ftb+"] n'existe pas !"

############
# GOLD FUN #
############
if os.path.isfile(gold_fun):
	print "\nGOLD FUN:"
	print "# Copie du fichier ["+gold_fun+"] dans le dossier ["+out_dir+"]"
	os.system("cp "+gold_fun+" "+out_dir+"/p7-107.goldfun.fun")
	
	temporaire = out_dir+"/p7-107.goldfun.fun"
	file = temporaire 
	#ftb->fun
	print "# Lecture du fichier ["+temporaire+"]"
	test_labelledtree_list = file2labelledtree_list(temporaire)
	
	#fun->pivot		
	print "# Applications des heuristiques"
	test_deptree_list = apply_heuristiques_deptree_list(test_labelledtree_list)
	test_deps_list = deptree_list2dgraph_list(test_deptree_list)
	dir_piv = out_dir+"/p7-107.goldfun2pivot/"
	if not os.path.isdir(dir_piv):
		print "# Creation du dossier ["+dir_piv+"]"
		os.system("mkdir "+dir_piv)
	cpt=0
	print "# Impression des fichiers pivot dans le dossier ["+dir_piv+"]"
	for dg in test_deps_list:
		ds = DepSentence(cpt,file,"AUTO","DATE",test_deps_list[dg])
		g = open(dir_piv+"ftb_"+str(cpt)+".piv","w")
		string = "sentence(\n"
		string += ds.id2str()
		string += ds.date2str()
		string += ds.validator2str()
		string += ds.sentence_form()
		string += ds.surfdeps2str()
		string += ds.feats2str(True)
		string += ")"
		g.write(string)
		cpt+=1

	os.system("mkdir "+out_dir+"/p7-107.goldfun2pivot2easy/")
	easy_file = out_dir+"/p7-107.goldfun2pivot2easy.xml"
	print "# Conversion du dossier pivot ["+ dir_piv +"] en fichier easy ["+easy_file+"]"
	ef = open(easy_file,"w")
	dsentence_list = dgraph_list2dsentence_list(test_deps_list)
	easy_list = dsentence_list2easy_list(dsentence_list)
	xml = easy_list2string(easy_list,'fichier',ROOT_INDEX=-1)	
	ef.write(xml)

	dir_easy = out_dir+"/p7-107.goldfun2pivot2easy/"
	if not os.path.isdir(dir_easy):
		print "# Creation du dossier ["+dir_easy+"]"
		os.system("mkdir "+dir_easy)
	cpt=0
	print "# Impression des fichiers easy dans le dossier ["+dir_easy+"]"	
	ds_list = easy_list2dsentence_list(easy_list)
	for i in ds_list.keys():
		ds = ds_list[i]
		g = open(dir_easy+"ftb_"+str(cpt)+".piv","w")
		string = "sentence(\n"
		string += ds.id2str()
		string += ds.date2str()
		string += ds.validator2str()
		string += ds.sentence_form()
		string += ds.surfdeps2str()
		string += ds.feats2str(True)
		string += ")"
		g.write(string)
		g.close()
		cpt+=1	
else:
	print "! le fichier  ["+gold_fun+"] n'existe pas !"

#########
# PIVOT #
#########
if os.path.isdir(gold_pivot):
	print "\nPIVOT:"
	print "# Copie du dossier ["+gold_pivot+"] dans le dossier ["+out_dir+"]"
	newdossier = out_dir+"/p7-107.goldpivot/"
	dir_piv = newdossier
	if os.path.isdir(newdossier):
		os.system("rm -Rf "+newdossier)
	os.system("mkdir "+newdossier)
	cmd_cp = "cp -R "+gold_pivot+"/*.piv "+newdossier
	os.system( cmd_cp )
	dgraph_list = dirpivot2dgraph_list(newdossier)
	os.system("mkdir "+out_dir+"/p7-107.goldpivot2easy/")
	easy_file = out_dir+"/p7-107.goldpivot2easy.xml"
	print "# Conversion du dossier pivot ["+ dir_piv +"] en fichier easy ["+easy_file+"]"
	ef = open(easy_file,"w")
	dsentence_list = dgraph_list2dsentence_list(dgraph_list)
	easy_list = dsentence_list2easy_list(dsentence_list)
	xml = easy_list2string(easy_list,'fichier',ROOT_INDEX=-1)	
	ef.write(xml)

	dir_easy = out_dir+"/p7-107.goldpivot2easy/"
	if not os.path.isdir(dir_easy):
		print "# Creation du dossier ["+dir_easy+"]"
		os.system("mkdir "+dir_easy)
	cpt=0
	print "# Impression des fichiers easy dans le dossier ["+dir_easy+"]"	
	ds_list = easy_list2dsentence_list(easy_list)
	for i in ds_list.keys():
		ds = ds_list[i]
		g = open(dir_easy+"ftb_"+str(cpt)+".piv","w")
		string = "sentence(\n"
		string += ds.id2str()
		string += ds.date2str()
		string += ds.validator2str()
		string += ds.sentence_form()
		string += ds.surfdeps2str()
		string += ds.feats2str(True)
		string += ")"
		g.write(string)
		g.close()
		cpt+=1
else:
	print "! le dossier  ["+gold_pivot+"] n'existe pas !"