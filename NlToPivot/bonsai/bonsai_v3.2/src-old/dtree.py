#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# dtree.py
#
# A Decision Tree Classifier built as a wrapper over R. Quinlan's c.4.5 
# original algorithm
#
# Author : Benoit Crabbé
# Date : Feb 2007

import pyC45 
import classifier_generic
from data_tables import *

class TreeLearner:
    def __init__(self,path_to_data):
        pyC45.build_tree(path_to_data)
        #read_data_names(path_to_data+".names")
        #read_data(path_to_data+".data")
    def show_data_names(self):
        pass

    def show_data(self):
        pass

    #Uses Quinlan's eval procedure
    def eval_tree(self):
        pass

#To be used statically, the behaviour if several instances of the classifier are used together is prone to crash the whole process (due to Quinlan's implementation)
class TreeClassifier:
    def __init__(self,path_to_tree): 
        self.path_to_tree = path_to_tree
        pyC45.init_classifier(path_to_tree)
#        dt = DataTable(path_to_tree)
#        self.classes = dt.classes
#        self.attributes = dt.attributes
        
    def show_tree(self):
        pyC45.show_tree()

    def show_data_names(self):
        pass 

    def classify(self,case_descr):
        case_descr.append('SUJ')#appends a dummy class to satisfy Quinlan's implementation requirements)
        return pyC45.classify(case_descr)

    def load_test(self):
        return classifier_generic.load_test(self,self.path_to_tree)

    def run_eval(self,testtable,verbose=False):
        classifier_generic.run_eval(self,testtable,verbose)

#Tests
########
#flearn = TreeLearner("/Users/Benoit/tools/dtrees/Data/functions")
#fclass = TreeClassifier("/Users/Benoit/tools/dtrees/Data/functions")

#classifier = TreeClassifier("/Users/Benoit/tools/dtrees/Data/functions")
#table = classifier.load_test()
#classifier.run_eval(table,verbose=True)

