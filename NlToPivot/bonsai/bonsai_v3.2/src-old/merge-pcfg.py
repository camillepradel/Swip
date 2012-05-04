#!/usr/bin/env python

#Merges raw text with Lncky output

import sys
import re
from PennTreeBankReader import *


def unmarkovise_treebank(tbank):
    new_tbank = []
    for tree in tbank:
        #if tree == "parse_failure.":
        #    tree = parse_failure()
        tree.unmarkovize()
        tree.unannotate_parent()
        #print tree.printf()
        new_tbank.append(tree)
    return new_tbank

def set_dummy_root(tbank):
    for tree in tbank:
        tree.label =""


in_raw = open(sys.argv[1])
in_parsed = open(sys.argv[2])

raw_lines = in_raw.readlines()
parsed_lines = in_parsed.read()
parsed_lines = re.sub("parse_failure.","(())",parsed_lines)

reader = PtbReader()
treelist = reader.parse_treebank(parsed_lines)
treelist = unmarkovise_treebank(treelist)
set_dummy_root(treelist)

raw_len = len(raw_lines)
parse_len = len(treelist)

#print raw_len,parse_len
if raw_len<>parse_len:
    print "Files lengths do not match: aborting merge"
    sys.exit(1)

#merge
for i in range(raw_len):
    toks = re.split("\s+",raw_lines[i])
    nodelist = []
    for tok in toks:
        nodelist.append(LabelledTree(tok))
    treelist[i].append_yield(nodelist[:-1]) # The minus 1 is hacky should be removed
    

#print
for tree in treelist:
    print tree.printf()#.encode('iso-8859-1')
