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
from PennTreeBankReader import *
from LabelledTree import *
from tagfixes import *
from ProcessCompounds import *
#from Dep import *

#AUX functions

#Warning this transformation process is destructive (will do a copy version later on)
def transform_treebank(treelist):
     newtreebank = []
     for tree in treelist:
          if not traces :
               tree.remove_traces()
          if ptb_strict :
               tree = tree.add_dummy_root()
          if mergenums:
               tree.merge_num()
          if clitics and fun :
               tree.clitics_downwards()
          if compound_subcat_file <> None:
               processcompounds.undo_compounds(tree)
          if mergecpd:
               tree.merge_cpds()
          tagopts = {'splitde':splitde,
                     #'splitaux':splitaux,
                     'mergecpd':mergecpd,
                     'markcpt':markcpt,
                     'markcpd':markcpd,
                     'usemfttype':usemfttype
                     }
          if symset2 or symset4 or symset4mft or symset6 or simulate_mft_tagset or symsetadj:
               tree.tagset2_terminals(tagfixer,tagopts)
               if simulate_mft_tagset:
                    tree.propagate_as_in_MFT()
          elif symset3:
               #tree = tree.sbar_transform()
               #tree.raise_preps()
               #tree.raise_punct()
               tree.propagate_rel()
               tree.propagate_mood()
          # main cat kept as label
          # compound or component marking, depending ont the tagopts
          else:
               tree.tagset2_terminals(None,tagopts)
          if symsetadj:
               tree.propagate_det()
          if parent_transform:
               tree = tree.remove_dummy_root()
               tree.parent_transform()
               tree = tree.add_dummy_root()
          if add_lemma_level:
               tree.add_lemma_level()
          if lemmatize:
               tree.changelex(lex='lemma', addtag=False)
          elif lemtok:
               tree.lemtok()
          elif lemtag:
               tree.changelex(lex='lemma', addtag=True)
          elif formtag:
               tree.changelex(lex='form', addtag=True)
          elif tagonly:
               tree.changelex(lex='none', addtag=True)
          if phrase_brackets:
               tree.phrase_brackets()
          newtreebank.append(tree)
     return newtreebank

def printout_ptb_corpus(treelist,stream):
     for tree in treelist:
          tree.set_print_options(subcat,fun,cpd,mph)
          if format == 'flat':
               # marie : hack to handle xmlinput and ptbinput (TODO : encodings!)
			   # modif par djame : encoding latin1 avant écriture fichier
               try:
				foobar = tree.printf()+'\n'
				stream.write(foobar.encode('iso-8859-1'))
               except UnicodeDecodeError:
                    stream.write(foobar.encode('iso-8859-1'))

          # marie : printing in ptb style, with lemma if defined
          elif format == 'flatlemma':
               stream.write(tree.printf({'lemma':{}})+'\n')
          elif format == 'indented':
               stream.write(tree.pprint()+'\n\n')
          elif format == 'latex':
               stream.write(tree.print_latex()+'\n\n')

def printout_pos_corpus(treelist,stream):
     for tree in treelist:
          tree.set_print_options(subcat,fun,cpd,mph)
          nodes = tree.pos_yield()
     # marie : hack to handle xmlinput and ptbinput (TODO : encodings!)
	 # modif par djame : encoding latin1 avant écriture fichier
          try:
			foobar = print_pos_list_brown(nodes)+'\n'
			stream.write(foobar.encode('iso-8859-1'))
          except UnicodeDecodeError:
               stream.write(foobar.encode('iso-8859-1'))

def printout_raw_corpus(treelist,stream):
     for tree in treelist:
          tree.set_print_options(subcat,fun,cpd,mph)
          nodes = tree.tree_yield()
     # marie : hack to handle xmlinput and ptbinput (TODO : encodings!)
     # modif par djame : encoding latin1 avant écriture fichier
          try:
			foobar = print_node_list_brown(nodes)+'\n'
			stream.write(foobar.encode('iso-8859-1'))
          except UnicodeDecodeError:
               stream.write(foobar.encode('iso-8859-1'))

def change_terminals_in_treebank(treebank, treebank_from):
     newtreebank = []
     for i,tree in enumerate(treebank):
          tree_from = treebank_from[i]
          new_yield = tree_from.tree_yield()
          newtreebank.append(change_terminals_in_tree(tree,new_yield)[0])
     return newtreebank

def change_terminals_in_tree(tree, new_yield):
#     print "CHANGE"
#     stream = sys.stdout
#     try:
#          stream.write(tree.printf().encode('iso-8859-1')+'\n')
#          stream.write(tree_from.printf().encode('iso-8859-1')+'\n')
#     except UnicodeDecodeError:
#          stream.write(tree.printf()+'\n')
#          stream.write(tree_from.printf()+'\n')
     
     if tree.is_leaf():
          return [new_yield[0], new_yield[1:]]
     else:
          nchildren = []
          for child in tree.children:
               new = change_terminals_in_tree(child, new_yield)
               newchild = new[0]
               new_yield = new[1]
               nchildren.append(newchild)
          tree.children = nchildren
     return [tree, new_yield]
                            

# Main Program
##################
# Command Line interface
usage = """
           %prog [options] FILE

           where FILE is the French Treebank XML file to be processed. 
If the FILE is a directory, all the files suffixed by '.xml' are read from that directory.
If no FILE is specified, data is read from STDIN. The extracted corpus is printed out on STDOUT.

This tool extracts a Penn treebank compliant corpus from the French TreeBank. For more informations type :

   %prog --help .

Comments and improvements are welcome, please send them to bcrabbe@linguist.jussieu.fr"""

parser=OptionParser(usage=usage)
parser.add_option("--target",dest="format",default='indented',help="This option allows to specify the target format in which the corpus will be generated. Possible VALUEs are 'indented', 'flat', 'flatlemma' or 'latex'.",metavar='VALUE')
parser.add_option("--raw",action="store_true",dest="raw",default=False,help="This option causes the tool to output only raw segmented text. All other options are ignored.")
parser.add_option("--pos",action="store_true",dest="pos",default=False,help="This option causes the tool to output POS tagged text formatted in the PTB fashion.")
parser.add_option("--subcat",action="store_true",dest="subcat",default=False,help="If the option is set to true complex categories are generated; if set to false only main categories are generated")
parser.add_option("--function",action="store_true",dest="fun",default=False,help="If the option is set, functional information is appended to categories. If set to false, functional information is ignored")
parser.add_option("--morph",action="store_true",dest="mph",default=False,help="If the option is set, morphological information is appended to the tags")
parser.add_option("--parent-transform",action="store_true",dest="parent",default=False,help="Applies Johnson's parent transformation to the trees extracted")
parser.add_option("--add-lemma-level",action="store_true",dest="add_lemma_level",default=False,help="Add intermediate node between terminal and pre-terminal symbol, with label=pre-terminal + lemma")
parser.add_option("--lemmatize",action="store_true",dest="lemmatize",default=False,help="replaces terminal symbols (words) with their lemma")
parser.add_option("--lemtok",action="store_true",dest="lemtok",default=False,help="outputs a string of the form 'lemma/token' as terminal symbols")
parser.add_option("--lemtag",action="store_true",dest="lemtag",default=False,help="replaces terminal symbols (words) with label=pre-terminal + lemma")
parser.add_option("--formtag",action="store_true",dest="formtag",default=False,help="replaces terminal symbols (words) with label=pre-terminal + word")
parser.add_option("--tagonly",action="store_true",dest="tagonly",default=False,help="replaces terminal symbols (words) with label=pre-terminal")
parser.add_option("--phrase-brackets",action="store_true",dest="phrase_brackets",default=False,help="Add a phrase for material within brackets")
# obsolete ?
parser.add_option("--compound",action="store_true",dest="cpd",default=False,help="If the option is set, ditto tags will be generated where categories are appended with a -C mark indicating that the token is part of a compound")
parser.add_option("--explicit-traces",action="store_true",dest="traces",default=False,help="If the option is set traces in the treebank are explicitly generated. Otherwise traces are removed and encoded in POS tags.")
parser.add_option("--ptb-strict",action="store_true",dest="ptb",default=False,help="If the option is set a dummy root is added to every tree. Used for full Penn TreeBank compliance.")
parser.add_option("--symset2",action="store_true",dest="symset2",default=False,help="If the option is set the treebank is generated with symset2. This also cancels any --explicit-traces,--compound and --subcat options and forces the mergenum option")
parser.add_option("--symset4",action="store_true",dest="symset4",default=False,help="Same as symset 2, except for verb tags : mood is used, but neither tense nor person")
parser.add_option("--symset4mft",action="store_true",dest="symset4mft",default=False,help="Same as symset 4, adapted to MFT input corpus")
parser.add_option("--symset6",action="store_true",dest="symset6",default=False,help="Same as symset 4, except for card determiners : retagged as Adj")
parser.add_option("--symsetadj",action="store_true",dest="symsetadj",default=False,help="Same as symset 4 + functions and NP split in NP NPposs NPdef NPdem")
parser.add_option("--splitde",action="store_true",dest="splitde",default=False,help="Pertains onl if symset4 or symset2 : uses specific tag for 'de' d' prepositions")
parser.add_option("--symset3",action="store_true",dest="symset3",default=False,help="If the option is set the treebank is generated as with symset2. It adds some further transformations on non-terminals")
parser.add_option("--merge-cpd",action="store_true",dest="mcpd",default=False,help="If the option is set cpds are merged to yield a single token.")
parser.add_option("--mark-cpd",action="store_true",dest="markcpd",default=False,help="If the option is set cpds are marked with * suffix")
parser.add_option("--mark-cpt",action="store_true",dest="markcpt",default=False,help="If the option is set components of cpds are marked with * suffix")
parser.add_option("--merge-num",action="store_true",dest="mnum",default=False,help="If the option is set digital numbers are merged to yield a single token.")
parser.add_option("--eval",dest="eval_file",default=None,help="This option will cause the extractor to output (1) a training file for training a parser on STDOUT (2) both two raw text and two pos tagged files together with the corresponding gold corpora for development tests and for final evaluation.  The test, devtest and training corpora are chosen with a proportion that is 10% test 10% devtest and 80% training. The corpora will have the name given to the parameter with different suffixes '.dev.pos' and 'dev.gld' for devtest and '.test.raw', '.test.pos' and '.test.gld' for gold.")
parser.add_option("--cross-eval",action="store_true",dest="cross",default=False,help="If the option is set the training, development and test corpora are chosen randomly (as used in cross evaluations) while with the eval flag the training dev and test sets are chosen following the canonical order of sentences in the corpus.")
parser.add_option("--xfold",dest="xfold",default=0,help="If set to positive integer X, and if eval_file is defined, then X directories are created named eval_file-1 eval_file-2 ..., for the corresponding X-fold division of the treebank into train/dev/test sets", metavar="X")
parser.add_option("--compound_subcat_file",default=None, help="If set to a file, inferred subcats for compound components are loaded from this file, and added to nodes when relevant")
parser.add_option("--dependencies",action="store_true", dest="dependencies", default=False, help="Triggers head annotation and dependencies extraction")
parser.add_option("--clitics-down",action="store_true", dest="clitics", default=False, help="Puts the functional annotations on the VN on the clitics")
parser.add_option("--ptbinput",action="store_true",dest="ptbinput",default=False,help="To read input corpus in ptb format")
parser.add_option("--usemfttype",action="store_true",dest="usemfttype",default=False,help="Pertains only for MFT input : compute node labels as specified for mft (not just main category)")
parser.add_option("--simulate_mft_tagset",action="store_true",dest="simulate_mft_tagset",default=False,help="For FTB input, generate the mft tagset")
parser.add_option("--getterminalsfromfile",dest="getterminalsfromfile",default=None,help="Replace terminal symbols in ptb trees with terminal symbols obtained from this other ptb file")


(opts,args) = parser.parse_args()
#ARGS
instream = ""
input_file = None
if (len(args) > 0):
     input_file = args[0]

#OPTIONS
#print opts
format = str(opts.format)
raw = bool(opts.raw)
pos = bool(opts.pos)
subcat = bool(opts.subcat)
fun = bool(opts.fun)
cpd = bool(opts.cpd)
mph = bool(opts.mph)
traces = bool(opts.traces)
mergenums = bool(opts.mnum)
eval_file = None
parent_transform = bool(opts.parent)
add_lemma_level = bool(opts.add_lemma_level)
lemmatize = bool(opts.lemmatize)
lemtag = bool(opts.lemtag)
lemtok = bool(opts.lemtok)
formtag = bool(opts.formtag)
tagonly = bool(opts.tagonly)
phrase_brackets = bool(opts.phrase_brackets)
ptb_strict = bool(opts.ptb)
symset2 = bool(opts.symset2)
symset3 = bool(opts.symset3)
symset4 = bool(opts.symset4)
symset6 = bool(opts.symset6)
symsetadj = bool(opts.symsetadj)
symset4mft = bool(opts.symset4mft)
splitde = bool(opts.splitde)
tagfixer = None
cross = bool(opts.cross)
xfold = int(opts.xfold)
mergecpd = bool(opts.mcpd)
markcpd = bool(opts.markcpd)
markcpt = bool(opts.markcpt)
dependencies = bool(opts.dependencies)
clitics = bool(opts.clitics)
ptbinput = bool(opts.ptbinput)
usemfttype = bool(opts.usemfttype)
simulate_mft_tagset = bool(opts.simulate_mft_tagset)
if opts.getterminalsfromfile <> None :
     getterminalsfromfile = str(opts.getterminalsfromfile)
     ptbinput = True
else:
     getterminalsfromfile = None

if mergecpd:
     cpd = False
     markcpd = False
     markcpt = False
if symset4 or symset4mft or symset6 or simulate_mft_tagset or symsetadj:
     traces=False
     cpd=False
     subcat=False
     mergenums=True
     if symset4:
          tagfixer = get_tagset4_fixer()
     elif symset4mft:
          tagixer = get_tagset4mft_fixer()
     elif symset6:
          tagfixer = get_tagset6_fixer()
     elif symsetadj:
          tagfixer = get_tagsetA_fixer()
     else:
          tagfixer = get_MFTtagset_fixer()
if symset3:
     symset2 = True
if symset2:
     traces = False
     cpd=False
     subcat=False
     mergenums = True
     tagfixer = get_tagset2_fixer()

if opts.eval_file <> None :
     eval_file = str(opts.eval_file)

compound_subcat_file = None
if opts.compound_subcat_file <> None:
     compound_subcat_file = str(opts.compound_subcat_file)
     processcompounds = ProcessCompounds(file=compound_subcat_file)

#Reads in the whole stuff
treebank = []
dev = []
test = []
reader = XmlReader() # needed even for ptbinput (build_eval_data method)
if ptbinput:
     ptbreader = PtbReader()
     stream = None
     if input_file <> None :
          stream = open(input_file)
     else:
          stream = sys.stdin
     treebank = ptbreader.read_mrg(stream)
     if getterminalsfromfile <> None:
          ptbreader_from = PtbReader()
          treebank_from = ptbreader_from.read_mrg(open(getterminalsfromfile))
          ntreebank = change_terminals_in_treebank(treebank, treebank_from)
          printout_ptb_corpus(ntreebank,sys.stdout)
          exit()
else:
     if input_file <> None : 
          if os.path.isdir(input_file):
               treebank =  reader.read_dir_xml(input_file)
          else:
               instream = open(input_file)
               treebank = reader.read_xml(instream)
     else:
          treebank = reader.read_xml(sys.stdin)

#Perform transformations
treebank = transform_treebank(treebank)

#Check for eval mode
if eval_file <> None and not xfold:
     if cross:
          (treebank,dev,test) = reader.build_eval_data(treebank)
     else:
          (treebank,dev,test) = reader.build_std_eval_data(treebank)
     #Prints the additional dev and test files if eval mode is on
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
# marie candito : added xfold case
elif xfold > 0 :
     folds = reader.build_xfold_eval_data(treebank,xfold)
     for i in range(xfold):
          outdir = eval_file+'-'+str(xfold)+'-'+str(i+1)
          os.mkdir(outdir)
          streams = []
          for j in [0,1,2]:
               streams.append({})
               for type in ['mrg','raw']:
                    name = os.path.join(outdir,'ftb_'+str(j+1)+'.'+type)
                    streams[j][type] = open(name,'w')
          # test set = i-th fold
          printout_ptb_corpus(folds[i], streams[2]['mrg'])
          printout_raw_corpus(folds[i], streams[2]['raw'])
          already = [i]
          if i==0:
               # dev set = last fold
               printout_ptb_corpus(folds[-1], streams[1]['mrg'])
               printout_raw_corpus(folds[-1], streams[1]['raw'])
               already.append(len(folds)-1)
          else:
               # dev set = (i-1)-th fold
               printout_ptb_corpus(folds[i-1], streams[1]['mrg'])
               printout_raw_corpus(folds[i-1], streams[1]['raw'])
               already.append(i-1)
          # training set = other folds
          for j in range(xfold):
               if j not in already:
                    printout_ptb_corpus(folds[j], streams[0]['mrg'])
                    printout_raw_corpus(folds[j], streams[0]['raw'])
          for j in [0,1,2]:
               for type in ['mrg','raw']:
                    streams[j][type].close()
     # end here if xfold required
     exit()
          
if dependencies:
    # head propagation table
     #headtable = ak_sym4_table()
     headtable = sym4_table()
     deplists = treebank2dep(treebank, headtable, trace=True)
     # intégré à la trace ... print_deplists(deplists, 'withcat')
     headtable.stats()

#Prints the corpus (or the training treebank)
elif raw :
     printout_raw_corpus(treebank,sys.stdout)
elif pos:
     printout_pos_corpus(treebank,sys.stdout)
else:
     printout_ptb_corpus(treebank,sys.stdout)

          
