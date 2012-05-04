#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# Author : Mathieu-Henri Falco
# Date : mars 2008

import os, pickle
import PennTreeBankReader
import XmlReader
import re
from LabelledTree import *
import PropagTable
from DependencyTree import *
from dgraph import *
from easy_chunking import *
from easy_conversion import *
import copy

import datetime
from random import *
import sexpr, sys
import os.path
#import codecs

from optparse import OptionParser
# Conversion : constituent trees (either xml source files / parsed ptb files)
# to depsentences
def Conversion(input_format, flmethod=None, flpara=None, outopts=None):
    depgraphs = []
    ptable = sym4_table()

    #---------------------------------------------------------
    # reading, and conversion to depgraphs
    #---------------------------------------------------------
    if input_format == 'xml':
        #output_dir = xml_file
        # Taken from the ftp2ptb file (tranform_treebank), can be interfaced later in a better way (just testing now)
        instream = sys.stdin
        reader = XmlReader.XmlReader()  
        ftb_list = reader.read_xml(instream)
        for tree in ftb_list:
            depgraph = srctree2depgraph(tree, ptable, outopts)
            depgraphs.append( (tree.treenum, depgraph))
    elif input_format == 'ptb':
        #sys.stdin = codecs.getreader('latin1') (sys.stdin)
        #output_dir = ptb_file
        # Taken from the PennTreebankReader file, can be interfaced later in a better way (just testing now)
        instream =  sys.stdin #open(ptb_file, 'r') 
        reader = PennTreeBankReader.PtbReader()
        labelledtreelist = reader.read_mrg(instream)
        # functional labeling, dependency heuristics, conversion to dep graphs
        depgraphs = parsedtrees2depgraphs(labelledtreelist, ptable, flmethod, flpara, outopts)
    else:
        print 'input file format unknown'
        exit()

    #---------------------------------------------------------
    # various ouputs
    #---------------------------------------------------------
    
    # Si l'option --easydeps est activée
    if easydeps:
    	print " -rawtext:\t"+fichier_in
        # Marie : permettre de lancer la conversion easy à partir de stades intermédiaires
        #         ==> le fichier_in n'est pas forcément un raw ou un txt !!!!
    	#fichier_add = re.search("^(.+?)\.(raw|txt)$",fichier_in).group(1)+'.add'
    	fichier_add = re.search("^(.+?)(\.[^\.]+)?$",fichier_in).group(1)+'.add'
    	association={}
    	# lecture d 'une fichier .add (association d'index d'énoncé avec un compteur incrémental)
    	if os.path.isfile(fichier_add):
    		print " -fichier add:\t"+fichier_add
	    	desc_add = open(fichier_add,"r")
	        cpt=1
	        for line in desc_add.readlines():
				association[cpt]=int(re.search("^E(\d+)$",line).group(1).rstrip())
				cpt += 1
    	else:
	    	print "# le fichier .add n'existe pas !"
    	
        #filename = os.path.join(output_dir,fichier_in)
        filename = fichier_in
        # Création du document xml résultat.
        # debug marie : chemin peut ne pas contenir de / ...
        # split chemin / nom de fichier
        (pathname, basenameext) = os.path.split(filename)
        # nom de fichier sans extension
        basename = re.search("^(.+?)\.([^\.]+)$",basenameext).group(1)
        print " -sortie xml:\t"+os.path.join(pathname, basename+'.xml')
        fichier_xml = open(os.path.join(pathname, basename+'.xml'), 'w')
        fichier_xml.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
        fichier_xml.write("<!DOCTYPE DOCUMENT SYSTEM \"easy.dtd\">\n")
        fichier_xml.write("<DOCUMENT fichier=\""+basename+'.xml'+"\" id=\"reference\" date=\"_DATE_\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n")

    	
    	# Fichier de debug (AVANT-APRES)
        # marie debug = os.path.join(output_dir,"statgram_vs_stateasy_"+re.search("^.*/([^/]+?)\.(raw|txt)$",filename).group(1)+".txt")
        debug = os.path.join(output_dir,"statgram_vs_stateasy_"+basename+'.txt')
        desc = open(debug,'w')
    
    gener_posandlemma = (input_format == u'xml') 

    for (treenum, depgraph) in depgraphs:

        if parc700:
            # Generate a Parc700 file from the dgraph
            ds = DepSentence(treenum,'parsed',validator,datetime.date.today(),depgraph)
            write_output_file(output_dir,treenum,ds.sentence2pivot(True))
            
        # Si l'option --easydeps est activée
        if easydeps:
            if association.has_key(treenum):
        		no_enonce = association[treenum]
            else:
        		no_enonce = treenum
            # marie : appauvrissement du tagset pour conserver telles quelles les heuristiques de François
            depgraph.set_poor_tags()
            ds = DepSentence(no_enonce,fichier_in,validator,datetime.date.today(),depgraph)
            # Ecriture dans le fichier de debug
            desc.write("\nE"+str(no_enonce)+" phrase +++++++++++++++++++++++++++++++++++++\n")
            desc.write(ds.sentence_form())
            desc.write("E"+str(no_enonce)+" statgram ------------------------------------\n")
            desc.write(ds.depforest.triples2string(True))
            
            #print "CHUNKS EASY"
            fichier_xml.write("<E id=\"E"+str(no_enonce)+"\">\n")
            ds.lower_labels()
            liste = set_chunks(ds)
            chunks = get_chunks(ds,liste)
            fichier_xml.write(sortie_xml_chunk(chunks))

            #print "DEPENDANCES EASY"
            dseasy = copy.deepcopy(ds)
            dseasy = applications_regles(dseasy)
            fichier_xml.write(sortie_xml_dependances(dseasy,ROOT_INDEX = -1))
            fichier_xml.write("</E>\n")

			# Ecriture dans le fichier de debug
            desc.write("E"+str(no_enonce)+" statgram+easy ------------------------------------\n")
            desc.write(dseasy.depforest.triples2string(True))

        if parcPivot:
            # Generate from the dgraph a ParcPivot file with or without POS and lemma 
            ds = DepSentence(treenum,'parsed',validator,re.split('-',datetime.date.today().__str__()),depgraph)
            write_output_file(output_dir,treenum,ds.sentence2pivot(out_lemma=True))
        if dep_out:
            pass
            #print depgraph.triples2string(sorted=True) 
        if dot_out:
            write_dot_file(output_dir,treenum,ds.depforest.graph2dot())
            
    # Si l'option --easydeps est activée        
    if easydeps:
        fichier_xml.write("</DOCUMENT>\n")

    if png_out:
        do_pngs(output_dir)

#Make an output directory if needed...
def do_output_dir(output_dir):
    if not os.path.isdir(output_dir):
        os.mkdir(output_dir)

#Writes the content of a parse in a file in output dir. The content parameter is a string to be written in the file
def write_output_file(output_dir,treenum,content):
    filename = os.path.join(output_dir,'P_'+str(treenum)+'.piv') 
    outstream = open(filename,'w')
    try:
        outstream.write(content)
    except UnicodeEncodeError:
        outstream.write(content.encode('iso-8859-15'))
    outstream.close()

def write_dot_file(output_dir,treenum,content):
    filename = os.path.join(output_dir,'P_'+str(treenum)+'.dot') 
    outstream = open(filename,'w')
    try:
        outstream.write(content)
    except UnicodeEncodeError:
        outstream.write(content.encode('iso-8859-15'))
    outstream.close()

#Requires to have a recent version of dot in the user path
#Takes a bunch of time...
def do_pngs(output_dir):
    files = os.listdir(output_dir)
    for file in files:
        if re.match("^.*\.dot$",file):
            os.system('dot -T png -Gcharset=latin1 ' + os.path.join(output_dir,file) + ' > ' +  os.path.join(output_dir,re.sub('dot$','png',file)))


# Command Line interface
# python Const2dep.py --xml ftb.xml --parc700 (ou --ParcPivot ou les deux)
# python Const2dep.py --ptb ftb.ptb --parc700 (ou --ParcPivot ou les deux. PTB avec tagset appauvri)

usage = """
    Extracteur de dépendances.
                        
    %prog [options]
    """

parser = OptionParser()
parser.add_option("--gestion_comp", dest="gestion_comp", default=False, help="Applique la fonction Gestion_comp() dans DependencyTree")
parser.add_option("--tags", action="store_true", dest="pos", default=False, help="Prints part of speech tagged text on <stdout>")
parser.add_option("--ctree", action="store_true", dest="ctree", default=False, help="Prints constituent tree on <stdout>")
parser.add_option("--deps", action="store_true", dest="deps", default=False, help="Prints dependency triples on <stdout>")
parser.add_option("--ptbdeps", action="store_true", dest="ptbdeps", default=False, help="Prints dependency in PTB-style on <stdout>")
parser.add_option("--input-format", dest="inf", default='xml', help="Input format (xml or ptb)")
parser.add_option("--output-dir", dest="out_dir", default='', help="Output dir")
parser.add_option("--method", dest="meth", default='svm', help="Specifies the classifier family used for labelling predicate-argument dependencies. Possible values are 'svm' for Support Vector Machines or 'tree' for Decision Trees.")
parser.add_option("--para", dest="para", default=None, help="Specifies the path of the classifier param files.")
parser.add_option("--parc700", action="store_true", dest="parc700", default=False, help="Generate a Parc700 text file")
parser.add_option("--parcPivot", action="store_true", dest="parcPivot", default=False, help="Generate a ParcPivot text file")
parser.add_option("--distance", action="store_true", dest="long_distance_mark", default=False, help="Write \'?\' in the ftb_function if it might be a long distance dependency")
parser.add_option("--graphic", dest="graphic_generation", action="store_true", default=False, help="Generate a dot file representing each dependency graph")
parser.add_option("--png", dest="png", action="store_true", default=False, help="Generates a PNG file from the dependency graph")

parser.add_option("--validator", dest="validator", default='validator', help="Annotator's name written in the Parc file (validator by default)")
parser.add_option("--easydeps", action="store_true", dest="easydeps", default=False, help="Generate a EASY xml file")
parser.add_option("--fichier", dest="fichier_in", default='', help="Fichier in")


(options, args) = parser.parse_args()

#Program options

output_dir = str(options.out_dir)
in_format =  str(options.inf)
fichier_in = str(options.fichier_in)


#ptb_file = str(options.ptb_file)
parc700 = bool(options.parc700)
parcPivot = bool(options.parcPivot)
long_distance_mark = bool(options.long_distance_mark)
dot_out = bool(options.graphic_generation)
png_out = bool(options.png)
if png_out:
    dot_out = True
validator = str(options.validator)
para = str(options.para)
pos_out =  bool(options.pos)
ctree_out =  bool(options.ctree)
dep_out =  bool(options.deps)
ptbdep_out =  bool(options.ptbdeps)
gestion_comp =  bool(options.gestion_comp)
easydeps = bool(options.easydeps)
outopts = []
if ctree_out: outopts.append('ctree')
if pos_out: outopts.append('pos')
if dep_out: outopts.append('dep')

if in_format <> '' and para == None:
    sys.stderr.write('No parameter file provided for classification : aborting. Please type help for further informations.\n')
    sys.exit(1)

method = str(options.meth)
if not (method == 'svm' or method == 'tree' or method == 'none'):
    sys.stderr.write('Classification method not supported, please type help for further informations.\n')
    sys.exit(1)

head_table = PropagTable.sym4_table()
if output_dir:
    do_output_dir(output_dir)
    #sys.exit()
    

#Main program
#ptb_file = 'pb.ptb'
#ptb_file = 'ftb_3.mrg'
#xml_file = 'ftb.xml'
if (in_format == 'xml' or in_format == 'ptb'):
    Conversion(in_format, flmethod=method, flpara=para, outopts=outopts)
else:
    sys.stderr.write('Invalid input format ('+in_format+'). Please type help for further informations.\n') 
