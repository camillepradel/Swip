#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# manual2dot
# Author : Francois Guerin
# Date : Sept 2008
# Objectif:
# Transformer les fichiers txt d'annotation manuelle en fichier piv et dot

import re
import sys
import os
from optparse import OptionParser
from PivotReader import *

# Main Program
##################
# Command Line interface
usage = """
           %prog --dossier-txt <dossier> --dossier-dot <dossier> [--out-lemma]

   %prog --help .
   """

parser=OptionParser(usage=usage)
parser.add_option("--dossier-txt",dest="dossier_txt",default=None,help="Dossier des phrases annotees manuellement en fichier .txt",metavar='VALUE')
parser.add_option("--dossier-dot",dest="dossier_dot",default=None,help="Dossier de sortie pour les fichiers .piv et .dot",metavar='VALUE')
parser.add_option("--out-lemma",dest="out_lemma",action="store_true",default=False,help="Impression de l'information lemma dans les fichiers .piv",metavar='VALUE')

(opts,args) = parser.parse_args()

#OPTIONS
#print opts
dossier_txt = str(opts.dossier_txt)
dossier_dot = str(opts.dossier_dot)
out_lemma = bool(opts.out_lemma)


#Lecture de dossier
if dossier_txt <> None:
	if os.path.isdir(dossier_txt):
		pass
	else:
		print "(pivot) "+dossier_txt+" nest pas un dossier!"
else:
	exit
	
if dossier_dot:
	if os.path.isdir(dossier_dot):
		pass
	else:
		print "\n! "+ dossier_dot +" nexiste pas: il sera cree!"
		os.mkdir(dossier_dot)
	#ecriture des fichiers dans le dossier
else:
	exit

reader = PReader()
print "Lecture du dossier .txt:"
ptreelist = reader.parse_dir_pivot(dossier_txt)
print "Transformation et Ecriture en .piv et .dot"

for pt in ptreelist:
	ds = pt.PTree2DepSentence()
	filename = os.path.join(dossier_dot,'P_'+str(ds.id)) 
	fichier_Pivot = open(filename+'.piv', 'w')
	fichier_Pivot.write(ds.sentence2pivot(out_lemma))	
	fichier_Dot = open(filename+'.dot', 'w')
	fichier_Dot.write(ds.depforest.graph2dot(['pos']))