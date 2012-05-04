#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

# A command line interface for functional role labelling

import os
import sys
from optparse import OptionParser
from FTree import *
from FexprReader import *
from FunctionalLabelling import *

#Management of param files

param_dir = ".flbl"
param_pref = 'function'

def create_param_dir():
    path = '/'
    if os.environ.has_key('HOME'):
        path = os.environ['HOME']
    else:
        print ('Warning no HOME detected storing parameters in current directory !')
        path = os.getcwd()
    try:
        os.mkdir(path+"/"+param_dir)
    except OSError:
        pass
    return path+"/"+param_dir
        
def get_param_dir():
    path = '/'
    if os.environ.has_key('HOME'):
        path = os.environ['HOME']
    else:
        print ('Warning no HOME grabbing parameters from current directory !')
        path = os.getcwd()
    return path+"/"+param_dir


#classifier code is the code selecting which classifier to train
#scheme code customises training for a given evaluation scheme

def train(classfiercode,schemecode='gr'):
    # Classifier code and scheme code are not really integrated now : defaults to Decision trees and gr
    ptable = None
    if schemecode == "gr": #this is not taken into account for now
        ptable =  ak_sym4_table()
        cotable = cohead_table()
    else :
        print "Unknown scheme, aborting training"
        sys.exit(1)
    learner = FunctionLearner(create_param_dir()+'/'+param_pref)
    print "Creating data tables..."
    parser=get_lex_yacc()
    f = sys.stdin
    result = parser.parse(f.read())
    f.close()
    for tree in result:
        if not tree.is_parse_failure():
            tree = tree.remove_dummy_root()
            tree.decorate_tree(ptable,cotable)
            learner.train_labeller(tree)
    print "Computing parameters..."
    learner.save_tree_model()


def label(classfiercode='tree',schemecode='gr',outdir=None,outformat="parc700"):
    ptable = None
    ctable = None
    cotable = None
    if schemecode == "gr": #this is not really taken into account for now
        ptable =  ak_sym4_table()
        ctable =  symset4_gr_catchup()
        cotable = cohead_table()
    else :
        print "Unknown scheme, aborting training"
        sys.exit(1)
    if outdir <> None:
        try:
            os.mkdir(outdir)
        except OSError:
          pass
    labeller = FunctionLabeller(ptable,ctable,get_param_dir()+'/'+param_pref)
    parser=get_lex_yacc()
    f = sys.stdin
    result = parser.parse(f.read())
    f.close()
    idx = 0
    for tree in result:
        if not tree.is_parse_failure():
            tree = tree.remove_dummy_root()
            tree.decorate_tree(ptable,cotable)
            labeller.label_functions(tree)
        depgraph = tree.dependency_graph(fmapper)
        depgraph.set_id('test-'+ str(idx))
        if outdir <> None:
            fout = open(outdir+"/"+'F'+str(idx)+".fdsc","w")
            fout.write(depgraph.print_parc700())
            fout.close()
        else:
            if outformat == "parc700":
                print depgraph.print_parc700()
            if outformat == "tree":
                print tree.display_tree_prolog('test-'+ str(idx))
            if outformat == "all":          
                print depgraph.print_parc700()
                print tree.display_tree_prolog('test-'+ str(idx))
        idx+=1


# Main Program
##################
# Command Line interface
usage = """Functional Role labelling system

           %prog [options] FILE

           where FILE is a Penn Treebank file to be processed where Functional tags are not annotated (when testing) or suffixed to the symbols with '_' as concatenation operator in case of training

           If no FILE is specified, data is read from STDIN. The labelled corpus is printed out on STDOUT unless specified otherwise by an option

           Comments and improvements are welcome, please send them to bcrabbe@linguist.jussieu.fr"""

parser=OptionParser(usage=usage)
parser.add_option("--train",dest="train",action="store_true",default=False,help="This option sets the labeller in training mode")
parser.add_option("--target",dest="tget",default="gr",help="This options selects which labelling scheme the labeller uses. Possible values Grammatical Functions (gr), or Easy (easy))")
parser.add_option("--classifier",dest="classifier",default="dtree",help="This option selects the classifier to be used for learning/classifying. Possible values are bayes,dtree,knn,perceptron,svm [only dtrees available for now]")
parser.add_option("--format",dest="format",default="parc700",help="This option selects the format used for displaying the results on STDOUT. Possible values are trees, parc700, all")
parser.add_option("--path",dest="path",help="This option sets the location of the parsed files. This is a directory and not a specific path that has to be referenced here.")

(opts,args) = parser.parse_args()

trainflag = opts.train
target = opts.tget
classifier = opts.classifier
path = opts.path
format = opts.format
if trainflag:
    train(classifier,schemecode=target)
else:
    if path <> None:
        label(classifier,target,outdir=path)
    else:
        label(classifier,target,outformat=format)
        
