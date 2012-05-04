#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import sys
from optparse import OptionParser
from PennTreeBankReader import *
from FunctionalLabelling import *
from svm_features import *

default_features = SVM_features.names
default_types = SVM_features.types

#default_features = ['WH','REL','INT_MARK','VCLS','MOOD','PASSIVE','CSUB','LHS_CAT','DEP_CAT','DEP_WORD','DEP_HEAD_CAT','HEAD_CAT','HEAD_WORD','COP','LEFT_SIBLING_CAT','RIGHT_SIBLING_CAT','COHEAD_CAT','COHEAD_WORD','DEP_YIELD_LEN','HEAD_DIST']
#default_types =   ['list','list','list','list','list','list','list','list','list','openlist','list','list','openlist','list','list','list','list','openlist','int','int']

default_log2c = 7
default_log2g = -9

def display_info(modelname,featlist,feattype,gridsearch):
    print "I will train an SVM-RBF model that will be stored as "+modelname
    if gridsearch:
        print "I will perform a Grid search to find out the RBF kernel parameters !!!"  
    else:
        print "I will train the model with RBF parameters set at log2gamma (gamma)="+str(default_log2g)+" ("+str(2**default_log2g)+") log2c (c) ="+str(default_log2c)+"("+str(2**default_log2c)+")"
    print "I use the following Feature list for training..."
    for i in range(len(featlist)):
        print featlist[i]+"\t["+feattype[i]+"]"
    print
    print "Please be patient..."

usage = """
           %prog [options] FILE

           where FILE is the French Treebank file to be
	   processed. This is supposed to be encoded in PTB format
	   with functional annotations included. 
           File may be send in Pipeline via <STDIN>

           This currently performs a training procedure with SVM.

   %prog --help .
"""

parser=OptionParser(usage=usage)
parser.add_option("--std-features",dest="svm_param",action="store_true", default=True,help="This option selects a standard set of features used by the classifier",metavar='VALUE')
parser.add_option("--model-name",dest="name", default='foo',help="This option sets the prefix of the parameter generated files",metavar='VALUE')
parser.add_option("--svm-default-RBF",dest="rbf_params",default=True,action="store_true",help="This option sets the C and Gamma parameters of the SVM RBF kernel used for labelling predicate argument dependencies to features that have empirically work in most cases. This is recommended if you want to save some training time.",metavar='VALUE')
parser.add_option("--search-svm-params",dest="svm_grid",action="store_true", default=False,help="This option triggers a grid search for SVM-RBF C and Gamma parameters. This actually triggers an extremely time consuming computation that may span over several days...",metavar='VALUE')

(opts,args) = parser.parse_args()

name = str(opts.name)
std_feats = bool(opts.svm_param)
svm_grid = bool(opts.svm_grid)
rbf_param = bool(opts.rbf_params)

features = default_features
types = default_types
#if std_feats:
display_info(name,features,types,svm_grid)

instream = sys.stdin
reader = PtbReader()
treebank = reader.parse_treebank(instream.read())
flearner = FunctionLearner(name,features,types)

if svm_grid:
    flearner.learn_svm_model(treebank,grid=True)
else:
    flearner.learn_svm_model(treebank,log2c=default_log2c,log2g=default_log2g)

#flearner.learn_tree_model(treebank)
