#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# bayes.py
#
# A Bayesian Classifier implementing the common naive Bayès algorithm
#
# Author : Benoit Crabbé
# Date : Feb 2007
# The smooth token is coded as -SMOOTH-
#(dunno how to code constants in Python (gasp)
#

from data_tables import *
from math import *
import classifier_generic
import pickle
import sys
import re

class BayesLearner:

    def __init__(self,path_to_data):
        self.data = DataTable(path_to_data)
        print "$"
        self.priors = self.do_priors()
        print "$$"
        self.likelihood = self.do_likelihood()
        print "$$$"
        self.path_to_data = path_to_data

    def save_model(self):
        f = open(self.path_to_data+".bayes","w")
        pickle.dump((self.priors,self.likelihood,self.data.classes,self.data.attributes),f)
        f.close()

    #Functions used for building the probabilistic model
    def do_priors(self,method="mle"):
        priors = {}
        if (method == 'mle'):
            targetclasses = self.data.classes
            for target in targetclasses:
                priors[target] = self.logestimate_max_likelihood(self.data.class_counts(target),self.data.datasize)
            # NO smoothing for priors since we do maxlikelihood !
            priors['-SMOOTH-'] = -1e308 #Approx -Infty, don't know anything better in python
            return priors
        elif (method =='laplace'):
            targetclasses = self.data.classes
            for target in targetclasses:
                priors[target] = self.logestimate_laplace(self.data.class_counts(target),self.data.datasize,self.data.datasize)
            priors['-SMOOTH-'] = self.logestimate_laplace(1,self.data.datasize,self.data.datasize)
            return priors
        else:
            return None

    def do_likelihood(self,method='laplace'):
        likelihood = {}
        if (method=='laplace'):
            for key in self.data.counts:
                if (self.is_likelihood_count(key)): #avoids conflation with prior counts
                    (target,attribute,value) = key
                    likelihood[(attribute,value,target)] = self.logestimate_laplace(self.data.att_val_counts(target,attribute,value),self.data.class_counts(target) ,self.data.att_counts(attribute,target)+1)
            #Add smoothing values (check : logprob seems a bit high)
            for target in self.data.classes:
                for attribute in self.data.attributes:
                    likelihood[(attribute,'-SMOOTH-',target)] = self.logestimate_laplace(1,self.data.class_counts(target),self.data.att_counts(attribute,target)+1)
            return likelihood
        elif(method=='mle'):
            #does the MLE here (no smoothing)
            for key in self.data.counts:
                if (self.is_likelihood_count(key)): #avoids conflation with prior counts
                    (target,attribute,value) = key
                    likelihood[(attribute,value,target)] = self.logestimate_max_likelihood(self.data.att_val_counts(target,attribute,value),self.data.class_counts(target))
            #The NO smoothing parameters for MLE
            for target in self.data.classes:
                for attribute in self.data.attributes:
                    likelihood[(attribute,'-SMOOTH-',target)] =  -1e308 #Approx -Infty, don't know anything better
            return likelihood
        else:
            return None

    #Performs a test to check whether probabilities estimated for an attribute given a class sum to 1
    def run_test(self, classname,attname,source='log'):
        print "Test "+attname+" :"
        sum = 0
        for elt in self.likelihood:
            (att,val,target) = elt
            if att == attname and target == classname:
                if (source == 'std'):
                    print str(self.likelihood[elt])+"\tP("+att+"["+val+"]|"+target+")"
                    sum = sum + (self.likelihood[elt])
                elif (source == 'log'):
                    print str(exp(self.likelihood[elt]))+"\tP("+att+"["+val+"]|"+target+")"
                    sum = sum + exp(self.likelihood[elt])
        print '----------'
        print sum

    #Checks whether the count is a prior count or a likelihood count
    def is_likelihood_count(self,ktuple):
        return ktuple[0] <> '-CLASS-'

    #DISPLAY
    ##########
    def show_class_names(self):
        print "Class Names :"
        print '----------------'
        for elt in self.data.classes:
            print " "+elt
            
    def show_priors(self,method='laplace'):
        print 'Priors :'
        print '-----------------------'
        for elt in self.priors.keys():
            print float(self.priors[elt]),elt

    def show_likelihood(self):
        print 'Likelihood :'
        print '--------------'
        for elt in self.likelihood.keys():
            (att,val,target) = elt
            print str(self.likelihood[elt])+"\tP("+att+"["+val+"]|"+target+")"

    #Google 'python perceptron' to get the web page explaining this odds stuff (Dan Klein's page).
    def show_odds():
        pass

    #Performs a MaxLikelihood estimation
    def estimate_max_likelihood(self,valuecounts,classcounts):
        return  float(valuecounts) / float(classcounts) 
       
    def logestimate_max_likelihood(self,valuecounts,classcounts):
        res = self.estimate_max_likelihood(valuecounts,classcounts)
        if res == 0:
            return -1e308 #didn't find a constant for returning -Infty :(
        else:
            return log(res)

    #Performs a Laplace estimation
    def estimate_laplace(self,valuecounts,classcounts,valtypescounts):
        return float(valuecounts + 1) / float(classcounts + valtypescounts)      
    def logestimate_laplace(self,valuecounts,classcounts,valtypescounts):
        return log(self.estimate_laplace(valuecounts,classcounts,valtypescounts))

class BayesClassifier:

    def __init__(self,path_to_data):   
        self.path_to_data = path_to_data
        (self.logpriors,self.loglikelihood,self.classes,self.attributes) = self.load_model(path_to_data)

    #Loads in the prob model
    def load_model(self,filename):
        f = open(filename+".bayes")
        (logpriors,loglikelihood,classes,attributes) = pickle.load(f)
        f.close()
        return (logpriors,loglikelihood,classes,attributes)

    def classify(self,case_descr):
        if len(case_descr) <> len (self.attributes):
            sys.stderr.write('Cannot classify : data incompatible with model\n')
            return 0
        argmax = self.classes[0]
        max = -1e308  #Neg infty
        for target in self.classes:
            logprod = self.logpriors[target]#prior logprob
            i = 0
            for i in range(len(self.attributes)):
                att = self.attributes[i]
                val = case_descr[i]
                if (att,val,target) in self.loglikelihood.keys():
                    logprod = logprod + self.loglikelihood[(att,val,target)] # = P(value|class)
                else:
                    logprod = logprod + self.loglikelihood[(att,'-SMOOTH-',target)]
            if logprod > max :
                max = logprod
                argmax = target
        return argmax


    def load_test(self):
        return classifier_generic.load_test(self,self.path_to_data)

    def run_eval(self,testtable,verbose=False):
        classifier_generic.run_eval(self,testtable,verbose)



#learner = BayesLearner("/Users/Benoit/tools/dtrees/Data/functions")
#learner.show_class_names()
#learner.show_priors()
#learner.show_likelihood()
#print learner.data.counts
#learner.save_model()
#classifier = BayesClassifier("/Users/Benoit/tools/dtrees/Data/functions")
#Perform an eval
#table = classifier.load_test()
#classifier.run_eval(table,verbose=True)
