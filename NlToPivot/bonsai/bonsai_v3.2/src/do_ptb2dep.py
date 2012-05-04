#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

# Marie Candito 

import sys
import fastptbparser
from LabelledTree import *
from DepGraph import *
from optparse import OptionParser

usage = """This program reads from STDIN a treebank, in ptb-like format
           It extracts either labeled or unlabeled dependencies,
           If labeled dep are asked, 
           it launches functional role labeling if necessary (if input trees have no functional information)
           and infers the dependency labels that remain underspecified

           %prog [options]
"""

parser=OptionParser(usage=usage)
#parser.add_option("--intrees", dest="intrees", default=None, help='If set to None, input trees are read from stdin. If set to a directory, input trees are read from xml files in this directory. If set to a file, input trees are read from this file, in the format specified by --informat')
parser.add_option("--headrules", dest="headrules", default='ftb_symset4', help='Head rules. Default=ftb_symset4')
parser.add_option("--tagset", dest="tagset", default='ftb4', help='Tagset. Default=ftb4')
parser.add_option("--outformat", dest="outformat", default='conll', help='Output format for dependency trees. Either "pivot" or "conll". Default="conll"')
parser.add_option("--informat", dest="informat", default='ptbfunc', help='Input format of the phrase-structure trees. Either "ptb" for ptb-like bracketed trees, without functional annotations, or "ptbfunc" for ptb-like trees with functional tags (e.g. NP-SUJ). Default="ptbfunc"')
parser.add_option("--readtreeids", action="store_true", dest="readtreeids", default=False, help='If set, the input trees are preceded by an identifier (each line contains an id, a tab, the bracketed tree. Default=False')
parser.add_option("--labeled", action="store_true", dest="labeled", default=False, help='Either produce labeled or unlabeled dependencies (default False => unlabeled)')

(opts,args) = parser.parse_args()
#ARGS
#OPTIONS
in_format = str(opts.informat)
out_format = str(opts.outformat)
headrules = str(opts.headrules)
tagset = str(opts.tagset)
labeled = bool(opts.labeled)
readtreeids = bool(opts.readtreeids)

# TODO : convert string to method
if headrules == 'ftb_symset4' : headrules = ftb_symset4()
tagfixer = eval(tagset+'_fixer')()

# read lines containing on each line
#   either one bracketed tree per line
#   or a couple id / bracketed
instream = sys.stdin
line = instream.readline()
treeid = 0
# whether to decode functional annotations in node symbols (NP-SUJ)
parse_symbols = (in_format == 'ptbfunc')

while line:
    line = line.strip()
    if readtreeids:
        (treeid, line) = line.split('\t',1)
    else:
        treeid = treeid+1
    if not line:
        print ''
        continue
    # read bracketed tree
    tree = fastptbparser.parse_line(line, parse_symbols=parse_symbols)
            
    # head annotation and conversion
    depparse = DepParse(treeid,
                        labelledtree_2_depgraph(tree, tagfixer, headrules))

    # if labeled dependencies are required
    if labeled:
        #if in_format == 'ptb':
            #TODO HERE : call functional role labelling
            #print "FRL"

        # Inference of remaining underspecified dependency labels
        depparse.depgraph.infer_deplabels()

    if out_format == 'pivot':
        print str(depparse)
    else:
        print depparse.depgraph.to_string_conll()
    line = instream.readline()


    

