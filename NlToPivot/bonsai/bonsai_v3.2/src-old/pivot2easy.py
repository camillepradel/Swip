#! /sw/bin/python -O
# -*- coding: iso-8859-15 -*-
#
# manual2dot
# Author : Francois Guerin
# Date : Sept 2008
# Objectif:
# Transformer les fichiers txt d'annotation manuelle en fichiers xml easy

import re
import sys
import os
from optparse import OptionParser
from PivotReader import *
from easy_chunking import *
from easy_conversion import *

# Main Program
##################
# Command Line interface
usage = """
           %prog --dossier-txt <dossier> --dossier-xml <dossier>

   %prog --help .
   """

parser=OptionParser(usage=usage)
parser.add_option("--dossier-txt",dest="dossier_txt",default=None,help="Dossier des phrases annotees manuellement en fichier .txt",metavar='<dossier>')
parser.add_option("--dossier-xml",dest="dossier_xml",default=None,help="Dossier de sortie pour le fichier xml",metavar='<dossier>')
parser.add_option("--sortie-xml",dest="sortie_xml",default="Statgram",help="nom du fichier de sortie xml",metavar='<fichier>')

(opts,args) = parser.parse_args()

#OPTIONS
dossier_txt = str(opts.dossier_txt)
dossier_xml = str(opts.dossier_xml)
sortie_xml = str(opts.sortie_xml)


#Lecture de dossier
if dossier_txt <> None:
	if os.path.isdir(dossier_txt):
		pass
	else:
		print "(pivot) "+dossier_txt+" nest pas un dossier!"
		sys.exit()
else:
	exit
	
if dossier_xml:
	if os.path.isdir(dossier_xml):
		pass
	else:
		print "\n! "+ dossier_xml +" nexiste pas: il sera cree!"
		os.mkdir(dossier_xml)
	#ecriture des fichiers dans le dossier
else:
	exit

reader = PReader()
print "Lecture du dossier .txt:"
ptreelist = reader.parse_dir_pivot(dossier_txt)
print "Transformation et Ecriture en .xml"

filename = os.path.join(dossier_xml,sortie_xml+'.txt')
# Ecriture du fichier xml correspondant à ces fichiers PIVOT
fichier_xml = open(re.search("^(.+?)\.(raw|txt|piv)$",filename).group(1)+'.xml', 'w')
fichier_xml.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
fichier_xml.write("<!DOCTYPE DOCUMENT SYSTEM \"easy.dtd\">\n")
fichier_xml.write("<DOCUMENT fichier=\""+"Statgram"+"\" id=\"reference\" date=\"_DATE_\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n")

for pt in ptreelist:
	ds = pt.PTree2DepSentence()
	fichier_xml.write("<E id=\"E"+str(ds.id)+"\">\n")
	liste = set_chunks(ds)
	chunks = get_chunks(ds,liste)
	fichier_xml.write(sortie_xml_chunk(chunks))
	ds = applications_regles(ds)
	fichier_xml.write(sortie_xml_dependances(ds, ROOT_INDEX = -1))
	fichier_xml.write("</E>")
	#print ds.sentence2pivot(True)

fichier_xml.write("</DOCUMENT>")
