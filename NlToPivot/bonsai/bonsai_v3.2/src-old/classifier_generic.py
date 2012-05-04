#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
#  classifier_generic.py
#
# Generic functions for classifiers evaluation
# Author : Benoit Crabbé
# Date : Feb 2007
#
import re

def load_test(classifier,path_to_model,suffix='.test'):
    f = open(path_to_model+suffix)
    test = []
    lines = f.readlines()
    for line in lines:
        line = line[:-1]#chop
        if (not re.match("^\s*$",line)):
            toks = re.split("\s*,\s*",line)            
            line = toks
            test.append(line)
    f.close()
    return test


def run_eval(classifier,testtable,verbose=False):
    total = len(testtable)
    succ = 0
    classtotals = {}
    succclass = {}
    missclass = {}
    print 
    print "*** EVAL CLASSIFIER RESULTS *** "
    print
    if verbose:
        print "Input Vector            ==> Guess/Gold"
    print "-----------------------------------------"
    for row in testtable:
        goldtarget = row[-1]
        testvector = row[:-1]
        if goldtarget in classtotals.keys():
            classtotals[goldtarget] = classtotals[goldtarget] + 1
        else:
            classtotals[goldtarget] = 1

        guess = str(classifier.classify(testvector))
        pguess = guess
        if str(guess) == str(goldtarget):
            succ = succ + 1
            if goldtarget in succclass.keys():
                succclass[goldtarget] = succclass[goldtarget] + 1
            else:
                succclass[goldtarget] =  1
        else:
            pguess = pguess+'*'
            if goldtarget in missclass:
                missclass[goldtarget].append(guess)
            else :
                missclass[goldtarget] = [guess]
        if (verbose):
            print ",".join(testvector) + " ==> " + pguess+"/"+goldtarget
    acc = float(succ)/float(total)
    if verbose:
        print "-----------------------------------------"
        print "Overall Accuracy : "+str(acc)
        print "-----------------------------------------"
        print "Details : Class ==> Accy (Most Commonly Misguessed Class)"
        print 
        for key in classifier.classes:
            keysucc = 0
            if key in succclass.keys():
                keysucc = float(succclass[key])
            keytotal = float(classtotals[key])
            keyacc = keysucc/keytotal
            mce = ""
            if keyacc < 1:
                missclass[key].sort()
                max_length = 1
                length = 1
                prec = missclass[key][0]
                mce = prec
                for elt in missclass[key][1:]:
                    if elt == prec:
                        length = length + 1
                    else:
                        if length > max_length:
                            max_length = length
                            mce = prec
                            length = 1
                    prec = elt
                if length > max_length:
                    mce = prec
                mce = " (Most Common Error : "+ mce+" instead of "+key+")"
            print key +" ==> "+ str(keyacc) + mce
        print "-----------------------------------------"
        return acc
