#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# ftb-wc
# Author : Benoît Crabbé
# Date : Sept 2007

import re
import sys
import os
from optparse import OptionParser
from XmlReader import XmlReader
from PennTreeBankReader import *
from LabelledTree import *
from frequency import FrequencyTable

#AUX functions

# Main Program
##################
# Command Line interface
usage = """This is a tool for getting stats about the treebank
           %prog [options] FILE

           where FILE is the French Treebank XML file to be processed. 
If the FILE is a directory, all the files suffixed by '.xml' are read from that directory.
If no FILE is specified, data is read from STDIN. The extracted corpus is printed out on STDOUT.

This tool outputs some statistics on the treebank (frequencies...). Select relevant options to tell the tool which stats you want to get.

Comments and improvements are welcome, please send them to bcrabbe@linguist.jussieu.fr"""

parser=OptionParser(usage=usage)
parser.add_option("--source",dest="source",default='xml',help="This option lets you specify in which format the treebank is encoded (xml or penn)",metavar='VALUE')

parser.add_option("--target",dest="format",default='raw',help="This option allows to specify the target format in which the stats will be generated. Possible VALUE are 'plain', 'html' or 'latex'.",metavar='VALUE')
parser.add_option("--all",action="store_true",dest="all",default=False,help="This option causes the tool to output all the stats available regardless other options.")
parser.add_option("--raw",action="store_true",dest="raw",default=False,help="This option causes the tool to output stats for raw segmented text.")
parser.add_option("--pos",action="store_true",dest="pos",default=False,help="This option causes the tool to output stats for POS")
parser.add_option("--sym",action="store_true",dest="sym",default=False,help="This option causes the tool to output stats for non terminal symbols")
parser.add_option("--subcat",action="store_true",dest="subcat",default=False,help="If the option is set, counts on complex categories (cat + subcat) are generated.")
parser.add_option("--function",action="store_true",dest="fun",default=False,help="If the option is set stats for functions are emitted.")
parser.add_option("--pcfg",action="store_true",dest="pcfg",default=False,help="If the option is set stats for PCFG rules are emitted.")
parser.add_option("--lncky",action="store_true",dest="lncky",default=False,help="If the option is set, stats for PCFG rules compatible with Mark Johnson's LNCKY parser are emitted.")
parser.add_option("--top",dest="top",default = -1,help="An integer indicating the max number of lines to output for any statistical table")
parser.add_option("--explicit-traces",action="store_true",dest="traces",default=False,help="If the option is set traces in the treebank are kept for counting. Otherwise traces are ignored.")


(opts,args) = parser.parse_args()
#ARGS
instream = ""
input_file = None
if (len(args) > 0):
     input_file = args[0]

#OPTIONS
source =  str(opts.source)
format = str(opts.format)
all = bool(opts.all)
raw = bool(opts.raw)
pos = bool(opts.pos)
sym = bool(opts.sym)
subcat = bool(opts.subcat)
fun = bool(opts.fun)
pcfg = bool(opts.pcfg)
top = int(opts.top)
traces = bool(opts.traces)
lncky = bool(opts.lncky)

if source == 'xml':
     reader = XmlReader()
     if input_file <> None :
          if os.path.isdir(input_file):
               treebank =  reader.read_dir_xml(input_file)
          else:
               instream = open(input_file)
               treebank = reader.read_xml(instream)
     else:
          treebank = reader.read_xml(sys.stdin)
elif source=='penn':
     stream  = sys.stdin.read()
     reader = PtbReader(drparser=True)
     treebank = reader.parse_treebank(stream)
else :
     print "invalid source format\n"
     sys.exit(1)


freq = FrequencyTable(subcat,not traces)

if all:
     raw = True
     sym = True
     pos = True
     fun = True
     pcfg = True

if lncky:
     for tree in treebank:
          tree.label = 'ROOT'

freq.do_counts(treebank)

if all:
     print freq.print_generic_stats()

if raw:
     if all:
          print "Type counts\n-------------------\n"
     print freq.print_type_list(top)#.encode('iso-8859-1')
if sym:
     if all:
          print "Non terminal symbols counts\n-------------------\n"
     print freq.print_sym_list(top)#.encode('iso-8859-1')
if pos:
     if all:
          print  "Tag counts\n-------------------\n"
     print freq.print_tag_list(top)#.encode('iso-8859-1')
if fun:
     if all:
          print "Function counts\n-------------------\n"
     print freq.print_fun_list(top)#.encode('iso-8859-1')
if pcfg or lncky :
     if all:
          print "PCFG counts\n-------------------\n"
     if lncky:
          print str(len(treebank))+' ROOT --> SENT'#dummy root (quick hack to remove asap)
     print freq.print_rules_list(top)#.encode('iso-8859-1')
     print 
     print freq.print_detailed_dispersion()
