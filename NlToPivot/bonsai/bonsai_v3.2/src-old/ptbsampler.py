#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

# Penn Treebank sampler
# Samples trees from the PennTreebank
# Used to compare Parsing on the PTB w/ parsing on French

from LabelledTree import *
from PennTreeBankReader import *
from XmlReader import XmlReader
from tagfixes import *
import gc


def numeric_compare(a, b):
    x = int(a)
    y = int(b)
    if x>y:
	return 1
    elif x==y:
      return 0
    else: # x<y
      return -1

def printout_ptb_corpus(treelist,stream):
    format = 'flat'
    for tree in treelist:
        tree.set_print_options(False,False,False,False)
        if format == 'flat':
            stream.write(tree.printf()+'\n')
        if format == 'indented':
            stream.write(tree.pprint()+'\n\n')
        if format == 'latex':
            stream.write(tree.print_latex()+'\n\n')

def printout_pos_corpus(treelist,stream):
    for tree in treelist:
        tree.set_print_options(False,False,False,False)
        nodes = tree.pos_yield()
        stream.write(print_pos_list_brown(nodes)+'\n')

def printout_raw_corpus(treelist,stream):
    for tree in treelist:
        tree.set_print_options(False,False,False,False)
        nodes = tree.tree_yield()
        stream.write(print_node_list_brown(nodes)+'\n')

inputfile1 = sys.argv[1]
dev = []
test = []
reader = PtbReader()
treebank = []
sel_idxes = []
input = sys.stdin
sel_idxes = input.readlines()
sel_idxes.sort(cmp=numeric_compare)
print sel_idxes
files = os.listdir(inputfile1)
i = 0
for file in files:
    src_treebank = reader.read_dir_mrg(os.path.join(inputfile1,file))
    for tree in src_treebank:
	if (len(sel_idxes) > 0 and i == int(sel_idxes[0])):
	    sys.stderr.write('*')
	    sel_idxes = sel_idxes[1:]
	    tree.remove_traces_ptb()
	    tree.remove_ptb_annotations()
	    tree = tree.add_dummy_root()   
	    treebank.append(tree)
	i = i + 1


(treebank,dev,test) = reader.build_eval_data(treebank)

eval_file = 'ptb'
traintreefile = eval_file+'_1.mrg'
gstream = open(traintreefile,'w')
printout_ptb_corpus(treebank,gstream)
gstream.close()
trainposfile= eval_file+'_1.pos'
pstream = open(trainposfile,'w')
printout_pos_corpus(treebank,pstream)
posdevfile = eval_file+'_2.pos'
treedevfile = eval_file+'_2.mrg'
gstream = open(treedevfile,'w')
pstream = open(posdevfile,'w')
printout_ptb_corpus(dev,gstream)
printout_pos_corpus(dev,pstream)
gstream.close()
pstream.close()

rawtestfile = eval_file+'_3.raw'
postestfile = eval_file+'_3.pos'
treetestfile = eval_file+'_3.mrg'
gstream = open(treetestfile,'w')
rstream = open(rawtestfile,'w')
pstream = open(postestfile,'w')
printout_ptb_corpus(test,gstream)
printout_pos_corpus(test,pstream)
printout_raw_corpus(test,rstream)
gstream.close()
rstream.close()
pstream.close()
