#!/usr/bin/env python

import sys
import os
from math import *
import operator
import codecs


class MegamClassifier :
    

    def __init__( self, modelfile=None ):
        self.LABELS = "***NAMEDLABELSIDS***"
        self.BIAS = "**BIAS**"
        self.modelfile = modelfile
        self.classes = []
        self.bias_weights = []
        self.weights = {}
        if self.modelfile:
            print >> sys.stderr, "Reading model file: %s" %self.modelfile
            self.read_model( modelfile )
        return


    def train_model( self, datafile, prior=1 ):
        self.modelfile = datafile+'.megam'
        cmd = 'megam.opt -nc -repeat 4 -lambda %s multiclass %s > %s' %(prior,datafile,self.modelfile)
        os.system( cmd )
        return


    def test_model( self, datafile, prior=1 ):
        cmd = 'megam.opt -nc -predict %s multiclass %s' %(modelfile,datafile)
        os.system( cmd )
        return
    
    
    def read_model(self, modelfile, lang='iso-8859-1' ):
        """the modelfile is a sequence of whitespace-separated lines
        the first field is a string feature label
        subsequence fields are the weight for that feature for class.
        The first line is a map of class *names* to *field positions*
        for example:
                ***NAMEDLABELSIDS***    O       B-Misc  I-Misc
        """
        ct = 1
        for l in codecs.open( modelfile, 'r', lang ):
            l = l.strip()
            print >> sys.stderr, '\r'*len(str(ct))+str(ct),
            ct += 1
            parts = l.split()
            feature = parts[0]  # first field is the feature name
            if feature == self.LABELS :
                self.classes = map( str, parts[1:] )
            elif feature == self.BIAS :
                self.bias_weights = map( float, parts[1:] )
            else :
                self.weights[feature] = map( float, parts[1:] )  # next are the weights -- convert all to floats
        print >> sys.stderr
        return


    def get_best_label(self, event):
        """for an 'event', which is a list of feature values,
        we sum the exponential model weights to get a probability
        for labelling each class
        """
        # print "event: %s" % event
        event_weights = self.bias_weights
        for f in event :
            try:
                fweights = self.weights[f]  # a list of weights
                event_weights = map( sum, zip( event_weights, fweights ))
            except Exception, e:
                # print >> sys.stderr, "Dereferenced unknown feature: %s" % f
                pass
        best_weight = max( event_weights )
        return self.classes[event_weights.index(best_weight)]


    def get_label_probs(self, event):
        """for an 'event', which is a list of feature values,
        we sum the exponential model weights to get a probability
        for labelling each class
        """
        # print "event: %s" % event
        event_weights = self.bias_weights
        for f in event :
            try:
                fweights = self.weights[f]  # a list of weights
                event_weights = map( sum, zip( event_weights, fweights ))
            except Exception, e:
                # print >> sys.stderr, "Dereferenced unknown feature: %s" % f
                pass
        scores = [ exp(ew) for ew in event_weights ]
        z = sum( scores )
        probs = [ s/z for s in scores ]
        classprobs = [ (self.classes[i], probs[i]) for i in range(len(probs))]
        return classprobs
    







