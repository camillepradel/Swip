#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# sampler
# Author : Francois Guerin
# Date : Sept 2008
# Objectif:
# dans le ftb, separer les phrases annotees par Mathieu, des autres phrases.
# pour toutes  ces autres phrases, les mettre au format PTB
# (arbre avec categorie principale et fonction)

import re
import sys
import os
from optparse import OptionParser
from PivotReader import *
from XmlReader import XmlReader
from tagfixes import *

import locale
import datetime

locale.setlocale(locale.LC_ALL, 'fr_FR')
debut = datetime.datetime.now()

dico_fichier_sentlist={}
treebank=[]

# creer_dico( liste d'arbres )
#	a partir d'une liste d'arbres au format pivot,
#	cree un dictionnaire de la forme {id_file -> [id_sent]}
#	avec id_file = nom du fichier (champ "xml" dans la racine de l'arbre)
#	et id_sent = numero de la phrase (champ "ident" dans la racine de l'arbre)
def creer_dico(ptreelist):
	nb_elem=0
	for pt in ptreelist:
		id_file = pt.xml
		if not dico_fichier_sentlist.has_key(id_file):
			dico_fichier_sentlist[id_file]=[]

		id_sent = pt.ident
		print "> "+id_sent
		i=re.search("^[^\d]*([\d]+)$",id_sent)
		num=i.group(1)
		dico_fichier_sentlist[id_file].append(num)
		nb_elem=nb_elem+1
	#print ">>> "+str(nb_elem)+" elements dans le dictionnaire"

# lire_pivot( adresse de fichier )
#	lecture d'un fichier au format pivot 		
def lire_pivot(fichier):
	reader = PReader()	
	ptree = reader.parse_pivot(fichier)
	#print str(ptree)

# lire_dir_pivot( adresse de dossier )
#	lecture de fichiers au format pivot dans un dossier
def lire_dir_pivot(dossier):
	reader = PReader()
	print "----------------------------\n| Lecture du dossier pivot |\n----------------------------"
	ptreelist = reader.parse_dir_pivot(dossier)
	print ">>> "+str(len(ptreelist))+" arbres dans le dossier: "+dossier
	creer_dico(ptreelist)

# lire_ftb ( adresse de fichier )
#	lecture d'un fichier contenant des arbres au format ftb,
#	seuls les arbres dont l'identifiant n'apparait pas dans la liste
#	seront retournés
def lire_ftb(fichier):
	reader = XmlReader()
	#remettre toujours liste vide else) si ca plante
	if dico_fichier_sentlist.has_key(fichier):
		treelist = reader.read_xml_nopivot(dico_fichier_sentlist[fichier])
	else:
		treelist = reader.read_xml_nopivot(fichier,[])
	print ">>> "+str(len(treelist))+" arbres dans le fichier: "+fichier

# lire_dir_ftb( adresse de dossier )
#	lecture de fichiers au format ftb contenus dans un dossier
#	pour chaque fichier, seuls les arbres dont l'identifiant n'apparait pas dans le dictionnaire
#	seront retournés
def lire_dir_ftb(dossier):
	reader = XmlReader()
	print "--------------------------\n| Lecture du dossier ftb |\n--------------------------"
	treebank = reader.read_dir_xml_nopivot(dossier,dico_fichier_sentlist)
	print ">>> "+str(reader.nb_trees)+" arbres dans le dossier: "+dossier
	print ">>>  - "+str(len(treebank))+" arbres napparaissent pas dans le dossier-pivot"
	print ">>>  - "+str(reader.skipped)+" arbres apparaissent dans le dossier-pivot"
	return treebank

# afficher_dico()
#	affichage du dictionnaire
#	(juste pour verification)
def affiche_dico():
	for k in dico_fichier_sentlist.keys():
		liste=dico_fichier_sentlist[k]
		for l in sort(liste):
			print "FILE: "+k+ " - SENT: "+l

# from_ftb_to_ptb( adresse de dossier )
#	transforme une liste d'arbre ftb en arbres ptb
#	et les imprime chacun dans des fichiers "ftb_sent<id_sent>_<id_xml>"			
def from_ftb_to_ptb(dossier):
	print "----------------------------------------\n| Transformation des arbres ftb en ptb |\n----------------------------------------"
	ptb_file_pref = dossier+"/ptb_"
	print ">>> nombre de fichier-arbres à creer: "+str(len(treebank))
	nb_ok=0
	transfo_treebank = transform_treebank(treebank)
	for tree in transfo_treebank:
#	for tree in treebank:
		
		#mets le nom du fichier et de la phrase de ce dernier (originaire du ftb) dans le dummy root
		#tree.label = "sent"+tree.treenum+"_"+tree.file
		try:
			dest = open(ptb_file_pref+"sent"+tree.treenum+"_"+tree.file,'w')
			dest.write(get_ptbflat(tree))
			dest.close()
			nb_ok = nb_ok + 1
		except:
			pass
	print ">>> nombre de fichier-arbres crees: "+str(nb_ok)


# get_ftbflat_corpus( liste d'abres ptb )
#	renvoie une présentation plate des arbres en argument
def get_ptbflat_corpus(treelist):
	s=""
	for tree in treelist:
		s+= get_ptbflat(tree)
	return s

# get_ftbflat( arbre ptb )
#	renvoie une représentation plate de l'abre en argument
def get_ptbflat(tree):
	tree.set_print_options(True,True,True,True)
	return tree.printf()+'\n'	


# transform_treebank( liste d'arbres )
#	renvoie un arbre modifié selon les fonctions:
# --ptb-strict --symset4 --merge-cpd --merge-num --clitics-down


#j'ai copie cette fonction a partir du fichier ftb2ptb
#car je navais pas envie de faire un import
#jai uniquement gardé les variables correspondant aux options indiquées
# (options indiquées par benoit)

	#Warning this transformation process is destructive (will do a copy version later on)
def transform_treebank(treelist):
	newtreebank = []
	for tree in treelist:
		#if ptb_strict :
		num = tree.treenum
		file = tree.file
		tree = tree.add_dummy_root()
		#if mergenums:
		tree.merge_num()
		#if mergecpd:
		tree.merge_cpds()
		#splitde= False, mergecpd= True, markcpt= False, markcpd= False
		tagopts = {'splitde':False,
                     #'splitaux':splitaux,
                     'mergecpd':True,
                     'markcpt':False,
                     'markcpd':False
                     }
		#la fonction provient du module tagfixes, mais quand je l'importe ca ne marche pas
		tagfixer=get_tagset4_fixer()
		tree.tagset2_terminals(tagfixer,tagopts)
		tree.propagate_rel()
		tree.propagate_mood()
          # main cat kept as label
          # compound or component marking, depending ont the tagopts
		tree.treenum = num
		tree.file = file
		newtreebank.append(tree)
	return newtreebank
	
# rmdir ne peut supprimer un dossier non-vide
# donc jai trouve ca sur le net:
#import os, dircache
#
#def recursive_delete(dirname):
#	files = dircache.listdir(dirname)
#	for file in files:
#		path = os.path.join (dirname, file)
#		if os.path.isdir(path):
#			recursive_delete(path)
#		else:
#			print 'Removing file: "%s"' % path
#			retval = os.unlink(path)
#			
#	print 'Removing directory:', dirname
#	os.rmdir(dirname)

# Main Program
##################
# Command Line interface
usage = """
           %prog --dossier-pivot <dossier> --dossier-ftb <dossier> --dest-ptb <dossier>

   %prog --help .
   """

parser=OptionParser(usage=usage)
parser.add_option("--dossier-pivot",dest="dossier_pivot",default=None,help="Dossier des phrases annotees manuellement en format PIVOT",metavar='VALUE')
parser.add_option("--fichier-pivot",dest="fichier_pivot",default=None,help="Fichier contenant une phrase annotee manuellement en format PIVOT",metavar='VALUE')
parser.add_option("--dossier-ftb",dest="dossier_ftb",default=None,help="Dossier contenant le french-tree-bank",metavar='VALUE')
parser.add_option("--fichier-ftb",dest="fichier_ftb",default=None,help="Fichier contenant du french-tree-bank",metavar='VALUE')
parser.add_option("--dest-ptb",dest="dest_ptb",default=None,help="Dossier contenant les fichiers ftb transformés en ptb (à raison dun arbre (plat) par fichier - si il existe il sera efface",metavar='VALUE')


(opts,args) = parser.parse_args()

#OPTIONS
#print opts
dossier_pivot = opts.dossier_pivot
fichier_pivot = opts.fichier_pivot
dossier_ftb = opts.dossier_ftb
fichier_ftb = opts.fichier_ftb
dest_ptb = opts.dest_ptb

#Lecture de fichier
if fichier_pivot <> None:
	if os.path.isfile(fichier_pivot):
		lire_pivot(fichier_pivot)
	else:
		print "(pivot) "+fichier_pivot+" nest pas un fichier!"
		sys.exit()
else:
	pass
		
if fichier_ftb <> None:	
	if os.path.isfile(fichier_ftb):
		lire_ftb(fichier_ftb)
	else:
		print "(ftb) "+fichier_ftb+" nest pas un fichier!"
		sys.exit()
else:
	pass

#Lecture de dossier
if dossier_pivot <> None:
	if os.path.isdir(dossier_pivot):
		lire_dir_pivot(dossier_pivot)
	else:
		print "(pivot) "+dossier_pivot+" nest pas un dossier!"
		sys.exit()
else:
	pass
	
if dossier_ftb:
	if os.path.isdir(dossier_ftb):
		treebank=lire_dir_ftb(dossier_ftb)
	else:
		print "(ftb) "+dossier_ftb+" nest pas un dossier!"
		sys.exit()
else:
	pass

if dest_ptb:
	if os.path.isdir(dest_ptb):
		print "\n! "+dest_ptb+" existe: il sera efface, puis recree!"
		os.rmdir(dest_ptb)
		os.mkdir(dest_ptb)
	else:
		print "\n! "+dest_ptb+" nexiste pas: il sera cree!"
		os.mkdir(dest_ptb)
	from_ftb_to_ptb(dest_ptb)
else:
	pass

fin = datetime.datetime.now()
print "\nDebut: "+debut.strftime("%A %d %B %Y, %H:%M")
print "Fin: "+fin.strftime("%A %d %B %Y, %H:%M")
