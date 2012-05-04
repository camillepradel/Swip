#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# ftb2pos
# Author : Benoit Crabbé
# Date : Sept 2007

import re
import sys
import os
from optparse import OptionParser
from XmlReader import XmlReader
from LabelledTree import *

#AUX functions
def printout_pos_corpus(treelist,stream):
     for tree in treelist:
          tree.set_print_options(subcat,fun,cpd)
          if not traces :
               tree.remove_traces()
          nodes = tree.pos_yield()
          if format == "ims":
               stream.write(print_pos_list_ims(nodes)+'\n')
          elif format == "brown":
               stream.write(print_pos_list_brown(nodes)+'\n')
          elif format == "tnt":
               stream.write(print_pos_list_tnt(nodes)+'\n')
          else:
               sys.stderr.write("ERROR : Unknown target format...")

def printout_raw_corpus(treelist,stream):
     for tree in treelist:
          tree.set_print_options(subcat,fun,cpd)
          if not traces :
               tree.remove_traces()
          nodes = tree.tree_yield()
          if format == "ims":
               stream.write(print_node_list_ims(nodes)+'\n')
          elif format == "brown":
               stream.write(print_node_list_brown(nodes)+'\n')
          elif format == "tnt":
               stream.write(print_node_list_tnt(nodes)+'\n')
          else:
               sys.stderr.write("ERROR : Unknown target format...")

# Main Program
##################
# Command Line interface
usage = """
           %prog [options] FILE

           where FILE is the French Treebank XML file to be processed. 
If the FILE is a directory, all the files suffixed by '.xml' are read from the directory.
If no FILE is specified, data is read from STDIN. The extracted corpus is printed out on STDOUT.

This tool extracts a POS tagged corpora from the French TreeBank. For more informations type :

   %prog --help .

Comments and improvements are welcome, please send them to bcrabbe@linguist.jussieu.fr"""

parser=OptionParser(usage=usage)
parser.add_option("--target",dest="format",default='tnt',help="This option allows to specify the target format in which the corpus will be generated. Possible VALUE are 'tnt' 'ims' and 'brown'.",metavar='VALUE')
parser.add_option("--raw",action="store_true",dest="raw",default=False,help="This option causes the tool to output only raw segmented text. All other options except target are ignored.")
parser.add_option("--subcat",action="store_true",dest="subcat",default=False,help="If the option is set to true complex categories are generated; if set to false only main categories are generated")
parser.add_option("--function",action="store_true",dest="fun",default=False,help="If the option is set, functional information is appended to categories. If set to false, functional information is ignored")
parser.add_option("--compound",action="store_true",dest="cpd",default=False,help="If the option is set, ditto tags will be generated where categories are appended with a --C mark indicating that the token is part of a compound")
parser.add_option("--explicit-traces",action="store_true",dest="traces",default=False,help="If the option is set traces in the treebank are explicitly generated. Otherwise traces are removed and encoded in POS tags.")
parser.add_option("--eval",dest="eval_file",default=None,help="This option will cause the extractor to output (1) a training file for training a tagger on STDOUT, and (2) a test file and the corresponding gold corpora for evaluation. The test and gold corpora are chosen randomly. The expected proportion is 10% test and 90% training. The corpora will have the name given to the parameter with different suffixes 'tst' for test and 'gld' for gold.")

(opts,args) = parser.parse_args()
#ARGS
instream = ""
input_file = None
if (len(args) > 0):
     input_file = args[0]

#OPTIONS
format = str(opts.format)
raw = bool(opts.raw)
subcat = bool(opts.subcat)
fun = bool(opts.fun)
cpd = bool(opts.cpd)
traces = bool(opts.traces)
eval_file = None
if opts.eval_file <> None :
     eval_file = str(opts.eval_file)

#Reads in the whole stuff
treebank = []
test = []
gold = []
reader = XmlReader()
if input_file <> None : 
     if os.path.isdir(input_file):
          treebank =  reader.read_dir_xml(input_file)
     else:
          instream = open(input_file)
          treebank = reader.read_xml(instream)
else:
     treebank = reader.read_xml(sys.stdin)

#Check for eval mode
if eval_file <> None :
     (treebank,test,gold) = reader.build_eval_data(treebank)

#Prints the corpus (or the training treebank)
if raw :
     printout_raw_corpus(treebank,sys.stdout)
else:
     printout_pos_corpus(treebank,sys.stdout)

#Prints the additional eval files if eval mode is on
if eval_file <> None :
     testfile = eval_file+'.tst'
     goldfile = eval_file+'.gld'
     gstream = open(goldfile,'w')
     tstream = open(testfile,'w')
     printout_pos_corpus(gold,gstream)
     printout_raw_corpus(test,tstream)
     gstream.close()
     tstream.close()
