#!/usr/bin/env python -O
# -*- coding: iso-8859-1 -*-
#
# Auteur: GUERIN Francois
# Date: Décembre 2008

# Evaluation des différentes étapes du traitement de parsing et de conversion (en dépendances et easy)
# Les fichiers d'entrée sont les fichiers générés par le script "generate_corpus.py"
# (l'emplacement du dossier est indiqué par l'option --corpus
# L'étape à évaluer doit etre indiquée par l'une des options --evaluation / --comparaison
# --evaluation: pour évaluer une étape unique
#		(prend ses valeurs dans: raw2ptb, ptb2fun, fun2pivot, pivot2easy)
# --comparaison: pour évaluer deux ou plusieurs étapes consécutives
#		(prend ses valeurs dans: raw2fun, raw2pivot, raw2easy, ptb2pivot, ptb2easy, fun2easy)


import re
import os
import sys
import eval_aux
from svm_features import *
from optparse import OptionParser
from steps import *
from eval_aux import *

RAW = "p7-107.raw"
TRAIN = "p7-107.train.ptb"
RAW2PTB = "p7-107.raw2ptb.ptb"
GOLDPTB = "p7-107.goldptb.ptb"
RAW2PTB2FUN = "p7-107.raw2ptb2fun.fun"
GOLDPTB2FUN = "p7-107.goldptb2fun.fun"
GOLDFUN = "p7-107.goldfun.fun"
RAW2PTB2FUN2PIVOT = "p7-107.raw2ptb2fun2pivot/"
GOLDPTB2FUN2PIVOT = "p7-107.goldptb2fun2pivot/"
GOLDFUN2PIVOT = "p7-107.goldfun2pivot/"
GOLDPIVOT = "p7-107.goldpivot/"
RAW2PTB2FUN2PIVOT2EASY = "p7-107.raw2ptb2fun2pivot2easy/"
GOLDPTB2FUN2PIVOT2EASY = "p7-107.goldptb2fun2pivot2easy/"
GOLDFUN2PIVOT2EASY = "p7-107.goldfun2pivot2easy/"
GOLDPIVOT2EASY = "p7-107.goldpivot2easy/"
GOLDEASY = "p7-107.goldeasy/"
 

#Ajouter conversion easy
liste_evaluations = set(['raw2ptb','ptb2fun','fun2pivot','pivot2easy'])
liste_conversions = set(['raw2fun','raw2pivot','ptb2pivot','raw2easy','ptb2easy','fun2easy'])

# Main Program
##################
# Command Line interface
usage = """
           %prog ( --evaluation <type> OR --comparaison <type> ) --corpus <dossier> [--matrice] [--fine] [--easylike]

   %prog --help .
   """

parser=OptionParser(usage=usage)
parser.add_option("--evaluation",dest="evaluation",default=None,help="",metavar='<type>',help='Type d\'evaluation choisie pour une etape donnee')
parser.add_option("--comparaison",dest="comparaison",default=None,help="",metavar='<type>',help='Type d\'evaluation choisie pour une sequence d\'etapes')
parser.add_option("--corpus",dest="corpus",default=None,help="",metavar='<dossier>',help='Emplacement du corpus step-by-step')
parser.add_option("--matrice",dest="matrice",default=None,help="",metavar='<nom>',help='Suffixe utilise pour les noms de matrices de confusions generees')
parser.add_option("--fine", action="store_true", dest="fine_types", default=False, help='Affine le type des relations avec la categorie du gouverneur')
parser.add_option("--easylike", action="store_true", dest="easylike", default=False, help='Filtre les relations de dependances pour simuler des relations easy')

(opts,args) = parser.parse_args()

#OPTIONS
#print opts

evaluation = str(opts.evaluation)
comparaison = str(opts.comparaison)
corpus = str(opts.corpus)
matrice = opts.matrice
fine_types = bool(opts.fine_types)
easylike = bool(opts.easylike)

print "-------------------------------"
print "EVALUA: "+ evaluation
print "COMPAR: "+ comparaison
print "CORPUS: "+ corpus
print "MATRIX: "+ str(matrice)
print "  FINE: "+ str(fine_types)
print "  EASY: "+ str(easylike)
print "-------------------------------\n"

if fine_types and easylike:
	print "! Options --fine et --easylike incompatibles !"
	sys.exit()

if not os.path.isdir(corpus):
	print "# Le dossier ["+corpus+"] n'existe pas!"
	sys.exit()

M = Methodes()

# ajouter conversion easy
if comparaison <> "None":
	if comparaison in liste_conversions:
		if comparaison == 'raw2fun':
			M.comp_funlabelling(corpus+RAW2PTB,corpus+GOLDPTB,corpus+TRAIN,corpus+RAW2PTB2FUN,corpus+GOLDPTB2FUN,corpus+GOLDFUN,matrice)
		elif comparaison == 'ptb2pivot':
			M.comp_dependances1(corpus+GOLDPTB2FUN,corpus+GOLDFUN,corpus+GOLDPTB2FUN2PIVOT,corpus+GOLDFUN2PIVOT,corpus+GOLDPIVOT,matrice,fine=fine_types,easy=easylike)
		elif comparaison == 'raw2pivot':
			M.comp_dependances2(corpus+RAW2PTB,corpus+GOLDPTB,corpus+TRAIN,corpus+RAW2PTB2FUN,corpus+GOLDPTB2FUN,corpus+GOLDFUN,corpus+RAW2PTB2FUN2PIVOT,corpus+GOLDPTB2FUN2PIVOT,corpus+GOLDFUN2PIVOT,corpus+GOLDPIVOT,matrice,fine=fine_types,easy=easylike)
		else:
			print "# comparaison="+comparaison+" n'a pas encore été implémentée!" 
		pass
	else:
		print "# comparaison="+comparaison+" est inconnue!"
		sys.exit()
		
elif evaluation <> "None":
	if evaluation in liste_evaluations:
		if evaluation == 'raw2ptb':
			M.eval_parsing(corpus+RAW2PTB,corpus+GOLDPTB,corpus+TRAIN,matrice)				
		elif evaluation == 'ptb2fun':
			M.eval_funlabelling(corpus+GOLDPTB2FUN,corpus+GOLDFUN,matrice)
		elif evaluation == 'fun2pivot':
			M.eval_dependances(corpus+GOLDFUN2PIVOT,corpus+GOLDPIVOT,matrice,fine=fine_types,easy=easylike)
		elif evaluation == 'pivot2easy':
			M.eval_dependances(corpus+GOLDPIVOT2EASY,corpus+GOLDEASY,matrice)
		else:
			print "# evaluation="+evaluation+" n'a pas encore été implémentée!"
	else:
		print "# evaluation="+evaluation+" est inconnue!"
		sys.exit()
