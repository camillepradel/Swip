#!/usr/bin/env python

import sys
import re
from LabelledTree import *

#This generates eval results and confusion matrix from a tagged-file

#Extracts a POS vector from a list of lines
def pos_vector(lines):
    vec = []
    for line in lines:
        if not re.match('^(%%.*|\s*)$',line):
            (flex,pos) =  re.split('\t+',line)
            vec.append(pos[:-1])
    return vec

def pos_vector_from_treebank(treebank):
    vec = []
    for tree in treebank:
        tree.

class ConfusionMatrix:
    def __init__(self):
        self.code = -1
        self.dictionary = {}
        self.rev_dictionary = {}
        self.matrix = {}

    def encode(self,pos):
        if self.dictionary.has_key(pos):
            return self.dictionary[pos]
        else:
            self.code = self.code + 1
            self.dictionary[pos] = self.code
            self.rev_dictionary[self.code] = pos     
            return self.code

    def decode(self,code):
        return self.rev_dictionary[code]

    def add_error_count(self,gold,test):
        gcode = self.encode(gold)
        tcode = self.encode(test)
        counts = self.matrix.get((gcode,tcode),0)
        counts = counts + 1
        self.matrix[(gcode,tcode)] = counts

    def print_matrix(self):
        res =''
        #do header
        for code in self.rev_dictionary.keys():
            res = res + '\t' + self.decode(code)
        res = res + '\t' + 'TOTAL'
        #do body
        for i in self.rev_dictionary.keys():
            res = res + '\n'+ self.decode(i)
            total = 0
            for j in self.rev_dictionary.keys():
                c = self.matrix.get((i,j),0)
                total = total + c
                res = res + '\t' + str(c)
            res= res + '\t'+str(total)
        return res

gstream = open(sys.argv[1])
tstream = open(sys.argv[2])
glines = gstream.readlines()
tlines = tstream.readlines()
gold = pos_vector(glines)
test = pos_vector(tlines)
lex = lexical_vector(glines)
if len(gold) <> len(test) :
    print "Files do not match. Aborting..."
    sys.exit(1)

identity = 0
matrix = ConfusionMatrix()
for i in range(len(gold)):
    if gold[i] == test[i]:
        identity = identity + 1
    else : 
        matrix.add_error_count(gold[i],test[i])

errors = len(gold) - identity
accuracy = float(identity)/float(len(gold))
error_rate = float(errors)/float(len(gold))

print str(identity)+'/'+str(len(gold))+ ' tokens correctly tagged (accurracy = '+str(accuracy)+' %)'
print str(errors) +'/'+str(len(gold)) + ' tokens incorrectly tagged (error rate = '+str(error_rate)+' %)'

print matrix.print_matrix()
