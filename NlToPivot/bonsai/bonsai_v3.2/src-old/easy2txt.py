#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# easy2txt
# Author : Francois Guerin
# Date : Sept 2008
# Objectif:
# Recuperer le rawtext des fichiers xml d'easy en fichiers texte.

import re
import sys
import os
from optparse import OptionParser
from EasyReader import *

# Main Program
##################
# Command Line interface
usage = """
           %prog --dossier-easy <dossier> --dossier-txt <dossier> [--out-lemma]

   %prog --help .
   """

parser=OptionParser(usage=usage)
parser.add_option("--dossier-easy",dest="dossier_easy",default=None,help="Dossier contenant des fichiers xml easy",metavar='<dossier>')
parser.add_option("--dossier-txt",dest="dossier_txt",default=None,help="Dossier de sortie pour les fichiers txt",metavar='<dossier>')

(opts,args) = parser.parse_args()

#OPTIONS
#print opts
dossier_easy = str(opts.dossier_easy)
dossier_txt = str(opts.dossier_txt)

# Verification de l'existence des objet donnés en argument.
if dossier_easy <> None:
	if os.path.isdir(dossier_easy):
		pass
	else:
		print "\n# "+dossier_easy+" nest pas un dossier!"
		sys.exit()
else:
	sys.exit()
	
if dossier_txt:
	if os.path.isdir(dossier_txt):
		pass
	else:
		print "\n# "+ dossier_txt +" nexiste pas: il sera cree!"
		os.mkdir(dossier_txt)
else:
	sys.exit()

reader = EasyReader()
print "Lecture du dossier easy:"
sentences_per_file = reader.get_sentences_dir(dossier_easy)
print "Transformation en .txt"

# Ecriture des fichiers texte correspondant.
for file in sentences_per_file.keys():
	filetxt = re.search("(.*?)\.xml$",file).group(1) + ".txt"
	filename = os.path.join(dossier_txt,filetxt) 
	fichier = open(filename, 'w')
	# marie : désolée ça passe pas chez moi...
	try:
		fichier.write(sentences_per_file[file])
	except UnicodeEncodeError:
		fichier.write(sentences_per_file[file].encode('iso-8859-15'))
