#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-

import sys
from triplesp import *
from dgraph import *

# EvalF : for evaluation of dependency relations
# This takes as input a pivot corpus
# This allows to generate a raw corpus from the pivot

#encodes only the types to simplify. I map the types in the ref to the types of the parser
def simple_type_map():
    map = {}
    map['a_obj#'] = 'a_obj'
    map['aff_moyen'] = 'aff'
    map['arg_comp'] = 'arg'
    map['arg_cons'] = 'arg'
    map['arg_a'] = 'arg'
    map['de_obj#'] = 'de_obj'
    map['mod_comp'] = 'mod'
    map['mod_app'] = 'mod'
    map['mod#'] = 'mod'
    map['mod_cleft'] = 'mod'
    map['mod_loc'] = 'mod'
    map['mod_quant'] = 'mod'
    map['p_obj_agt'] = 'p_obj'
    map['p_obj_loc'] = 'p_obj'
    map['suj_impers'] = 'suj'
    return map

def pred_arg_rels():
    return set(['suj','obj','a_obj','aff','ats','ato','de_obj','mod','obj','p_obj'])


class ConfMatrix:
    def __init__(self):
        self.matrix = {}

    def add_error(self,ref,test):
        if self.matrix.has_key(ref):
            self.matrix[ref].append(test)
        else:
            self.matrix[ref] = []
            self.matrix[ref].append(test)

#Reimplementation as a sparse Matrix (ref,test) = val
    def add_error(self,ref,test):
        if self.matrix.has_key((ref,test)):
            self.matrix[(ref,test)] +=1
        else:
            self.matrix[(ref,test)] = 0

#     def do_row(self):
#         row = []
        
#     for elt in self.matrix.keys():
#         elt[0]=rowname
#         for 
    

    def do_row(self,key,max_num_cols=-1):
        row = self.matrix[key]
        row.sort()
        clist = []
        prev = row[0]
        c = 1  
        for elt in row[1:]:
            if elt == prev:
                c += 1
            else:
                clist.append((c,prev))
                c=1
            prev = elt
        clist.append((c,prev))
        clist.sort(reverse=True)
        if max_num_cols > 0:
            return clist[:max_num_cols]
        else:
            return clist
        

    def display_matrix(self,max_num_cols=-1):
        for elt in sorted(self.matrix.keys(),reverse=True):
            row = self.do_row(elt,max_num_cols)
            self.display_row(elt,row)

    def display_row(self,lbl, row):
        res = lbl+' ==>\t\t'
        for elt in row:
            res+=str(elt[1])+':'+str(elt[0])+'\t'
        print res

class Counter:

    def __init__(self,name):
        self.name = name
        self.fc = 0
        self.found = 0
        self.correct = 0
        
    def add_counts(self,fc,found,correct):
        self.fc += fc
        self.found += found
        self.correct += correct

    def prec(self):
        if self.found == 0:
            return 0
        return float(self.fc)/float(self.found)

    def rec(self):
        if self.correct == 0:
            return 0
        return float(self.fc)/float(self.correct)

    def fscore(self):
        if self.prec() + self.rec() == 0:
            return 0
        return  2 * (self.prec() * self.rec()) / (self.prec() + self.rec()) 

    def scores(self):
        return (self.prec(),self.rec(),self.fscore())

    def __str__(self):
        res =  self.name+'\t\t'
        for elt in self.scores():
            res+=str(elt)+'\t'
        return res


class EvalF:

    def __init__(self,pred_arg=False):
        self.tmap = simple_type_map()
        self.pred_arg = pred_arg
        self.pred_arg_set= pred_arg_rels()
        self.typec = {}
        self.confprec = ConfMatrix()

    def add_found_correct(self,typename):
        if self.typec.has_key(typename):
            self.typec[typename].fc += 1
        else:
            self.typec[typename] = Counter(typename)
            self.typec[typename].fc += 1

    def add_correct(self,typename):
        if self.typec.has_key(typename):
            self.typec[typename].correct += 1
        else:
            self.typec[typename] = Counter(typename)
            self.typec[typename].correct += 1

    def add_found(self,typename):
        if self.typec.has_key(typename):
            self.typec[typename].found += 1
        else:
            self.typec[typename] = Counter(typename)
            self.typec[typename].found += 1

    def eval(self,gold_dir,test_dir,typed=True,ignore_punct=True):
        gbank = parse_triples_dir(gold_dir,sorted=True,prefix="ftb_",extension=".flmf7aa1ep.cat.xml.piv")
        tbank = parse_triples_dir(test_dir,sorted=True,prefix="P_",extension=".piv")
        gc = self.compareDepBanks(gbank,tbank,typed,ignore_punct)
        self.display_summary(gc,typed)
        self.display_detailed_table()
        print
        self.confprec.display_matrix()

    def compareDepBanks(self,gold,test,typed=True,ignore_punct=True):
        if len(gold) <> len(test):
            sys.stderr.write("Error depbanks have not the same length, aborting...")
            sys.exit()

        gc = Counter('Global')
        for i in range(len(gold)):
            (fc,f,c) = self.compare_sentences(gold[i].depforest.edges,test[i].depforest.edges,typed,ignore_punct)
            gc.add_counts(fc,f,c)
        return gc

    def display_summary(self,counter, typed=False):
        print
        print
        if typed:
            print "Typed evaluation results"
        else:
            print "Untyped evaluation results"
        print "----------------------------"
        print "Precision : "+str(counter.prec())
        print "Recall : "+ str(counter.rec())
        print "F1-Score : "+str(counter.fscore())
        print   

    def display_detailed_table(self):
        print 'Relname\t\tPrec\t\tRec\t\tF-Score'
        for elt in sorted(self.typec.keys(),reverse=True):
            print self.typec[elt]

    def compare_sentences(self,gold_edges,test_edges,typed=True,ignore_punct=True,diagnostic=True):
        eval_gold = self.edge_set(gold_edges,typed,ignore_punct)
        eval_test = self.edge_set(test_edges,typed,ignore_punct)
        if diagnostic:
            for elt in eval_test:
                self.add_found(elt.label)
                if elt in eval_gold:
                    self.add_found_correct(elt.label)
                else:
                    for ref in eval_gold:
                        if elt.orig == ref.orig and elt.dest == ref.dest:
                            self.confprec.add_error(ref.label,elt.label)
                            break
                    self.confprec.add_error(ref.label,'<struct>')
            for elt in eval_gold:
                self.add_correct(elt.label)

        nfc = len(eval_test.intersection(eval_gold))
        nfound = len(eval_test)
        ncorrect = len(eval_gold)
        return (nfc,nfound,ncorrect)

    #Filters out useless edges for this eval and returns an edge set suitable for this eval (removes punct, edge selection and performs mapping)
    def edge_set(self,edgelist,typed,ignore_punct):
        eset = set([])
        for elt in edgelist:
            if elt.label <> 'ponct' or not ignore_punct:
                if elt.label <> 'head':
                    edge = self.map_edge(elt,typed)
                    if self.pred_arg and edge.label in self.pred_arg_set:
                        eset.add(edge)
                    elif not self.pred_arg :
                        eset.add(edge)
        return eset
    
    def map_edge(self,edge,typed=True):
        """
        >>> e = EvalF()
        >>> e.map_edge(DepEdge(DepVertex("petit",1),"mod#",DepVertex("enfant",2))) #doctest: +ELLIPSIS
        <dgraph.DepEdge instance at 0x...>
        """
        label = edge.label
        if self.tmap.has_key(label):
            label = self.tmap[label]
        if not typed:
            label = "na"
        return DepEdge(edge.orig,label,edge.dest)
    
    def compare_edges(self,egold,etest,typed=True):
        if self.map_edge(egold,typed) == self.map_edge(etest,typed):
            return True
        return False


#Generates a ready to parse evaluation data from a dependency bank in pivot format
def generate_raw(depbank):
    for sent in depbank:
        print sent.sentence_form(decode=True)

#returns the list of labels found in a depbank
def typelist(depbank):
    types = set([])
    for sent in depbank:
        for elt in sent.depforest.edges:
            types.add(elt.label)
    return types


def display_typelist(typelist):
    """
    Displays a list of relations, one per line
    >>> display_typelist(['OBJ','A_OBJ','SUJ','P_OBJ'])
    A_OBJ
    OBJ
    P_OBJ
    SUJ
    """
    for elt in sorted(list(typelist)):
        print elt


def _test():
    import doctest
    doctest.testmod()

if __name__ == "__main__":
    _test()


usage = """
           %prog [options] GOLD_FILE TEST_FILE

           This is a dependency evaluation module for evaluation of dependency parsing output 

           where FILE are P7-107 files to be
	   processed. This is supposed to be encoded in P7-107 format


   %prog --help .
"""

parser=OptionParser(usage=usage)
parser.add_option("--generate-test",dest="raw_test",action="store_true", default=False,help="This generates on <STDOUT> a corpus of raw sentences ready to parse from a reference corpus then exits",metavar='VALUE')
parser.add_option("--ref",dest="refdir",help="This specifies the directory of the reference corpus",metavar='VALUE')
parser.add_option("--test",dest="tdir",help="This specifies the directory of the test corpus",metavar='VALUE')
parser.add_option("--untyped",dest="typed",default=True, action="store_false", help="This specifies if the evaluation is untyped or not",metavar='VALUE')
parser.add_option("--pa",dest="pa",default=False, action="store_true", help="This restricts the evaluation to predicate argument dependency relations",metavar='VALUE')
parser.add_option("--ignore-punct",dest="punct",default=False, action="store_true", help="This specifies if the punctuation has to be taken into account or not",metavar='VALUE')
parser.add_option("--typelist",dest="typelist",default=False, action="store_true", help="This generates on <STDOUT> the list of types found in a reference corpus then exits",metavar='VALUE')

(opts,args) = parser.parse_args()

if opts.raw_test:
    depbank = parse_triples_dir(opts.refdir,sorted=True,prefix="ftb_",extension=".flmf7aa1ep.cat.xml.piv")
    generate_raw(depbank)
    sys.exit()
if opts.typelist:
    depbank = parse_triples_dir(opts.refdir)
    t = typelist(depbank)
    display_typelist(t)
    sys.exit()

evalmachine = EvalF(opts.pa)
evalmachine.eval(opts.refdir,opts.tdir,opts.typed,opts.punct)
