#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

from triplesp import *
from dgraph import *
import os
from optparse import OptionParser
from GraphModif import *

# Main Program
##################
# Command Line interface
usage = """
           Reads pivot-files, check syntax, and print them out as .piv and .dot files
           %prog [options] FILE|DIR
           where FILE is either a pivot-format file, or a directory containing pivot-format files
"""

parser=OptionParser(usage=usage)
parser.add_option("--outdir",dest="outdir",default='readprint',help="Outdir for re-written piv and .dot files")
parser.add_option("--extension",dest="extension",default='piv',help="Extension of files to take as input (default = piv)")
parser.add_option("--labelmodifs",dest="labelmodifs",default=None,help="If set to P7107LABELMODIFS, labels are mapped accordingly (see GraphModif.py)")
(opts,args) = parser.parse_args()

input = None
if (len(args) > 0):
     input = args[0]
outdir = str(opts.outdir)
extension = str(opts.extension)
labelmodifs = None
if opts.labelmodifs <> None:
     labelmodifs = str(opts.labelmodifs)
     labelmodifier = eval(labelmodifs)

glist = []
if input <> None:
     if not os.path.isdir(outdir):
          os.mkdir(outdir)
     if os.path.isdir(input):
          depsentlist = parse_triples_dir(input,extension)
     else:
          depsentlist = parse_triples_file(input)
else:
    print usage


for depsent in depsentlist:
     # if label modifications required
     if labelmodifs <> None:
          depsent.depforest = labelmodifier.apply_modifs(depsent.depforest)
     outpiv = open(os.path.join(outdir, depsent.id+'.'+depsent.file+'.piv'), 'w')
     outdot = open(os.path.join(outdir, depsent.id+'.'+depsent.file+'.dot'), 'w')
    
     #for elt in depsent:
     outpiv.write(depsent.sentence2pivot(out_lemma=True))
     outdot.write(depsent.depforest.graph2dot())
 


 
