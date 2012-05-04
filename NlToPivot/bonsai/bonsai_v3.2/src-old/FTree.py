#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import sys
import re
from dgraph import *

class FTree:
    def __init__(self,nodelabel):
        self.cat = nodelabel
        self.id = -1
        self.word = "None"
        self.type = "None"
        self.features = {}
        self.children = []

    #primitives
    def add_child(self, childnode):
        self.children.append(childnode)

    def is_leaf(self):
        return len(self.children) == 0

    def is_preterminal(self):
        return len(self.children) == 1 and self.children[0].is_leaf()

    def is_head(self):
        return self.type == "H"

    def is_cohead(self):
        return self.features.has_key('cohead')

    def is_parse_failure(self):
        return self.cat == "**FAILURE**"

    def remove_dummy_root(self):
        return self.children[0]


    #Composition of features as Features union (and not unification)
    def compose_features(self,f2):
        if len(f2.keys()) > 0:
            for key in f2.keys():
                if self.features.has_key(key):
                    self.features[key] = self.features[key]+"/"+f2[key] #terrifying hack.. requires to think way more about this
                else:
                    self.features[key] = f2[key]

    #Propagates words and indexes
    def head_propagation(self):
        for child in self.children:
            if not child.is_leaf():
                child.head_propagation()
                if child.is_head():
                    self.word = child.word
                    self.id = child.id
                    self.compose_features(child.features)
                if self.cat== "PP" and child.is_cohead():
                    self.features['coheadcat'] = child.features['headcat']
                    self.features['coheadword'] = child.word
                    
    def annotate_heads(self,ptable,cotable):
       self.type="H"
       self.annotate_heads_rec(ptable,cotable)

    def annotate_heads_rec(self,ptable,cotable):
        if not self.is_preterminal():
            head = ptable.annotate_head(self,self.children)
            cohead = cotable.annotate_cohead(self,self.children)
            for child in self.children:
                child.annotate_heads_rec(ptable,cotable)

    def annotate_yield(self):
        self.annotate_yield_rec(0)
        
    def annotate_yield_rec(self,idx):
        local_idx = idx
        for child in self.children:
            if child.is_leaf():
                self.word = child.cat
                self.id = local_idx
                self.features['headcat'] = self.yield_simplify_cat(self.cat)
                wh = self.yield_annotate_wh(self.cat)
                if wh <> '-':
                    self.features['wh'] = wh
                local_idx = local_idx + 1
            else:
                local_idx = child.annotate_yield_rec(local_idx)
        return local_idx


    def get_yield(self):
        if self.is_leaf():
            return [self]
        else:
            theyield = []
            for elt in self.children:
                theyield = theyield + elt.get_yield()
            return theyield
    
    def display_yield(self):
        theyield = self.get_yield()
        res = ''
        if len(theyield) > 0:
            res = theyield[0].cat
            for elt in theyield[1:]:
                res += " "+elt.cat
        return res

    #Experiments for dealing with amalgamates:non-conclusive
    def yield_simplify_cat(self, incat):
        if incat == 'P+D':
            return 'P'
        return incat

    def yield_annotate_wh(self,incat):
        if  incat in ["DETWH","PROWH","ADVWH","ADJWH"]:
            return "+"
        elif incat == "PROREL":
            return "rel"
        return "-"

    def yield_simplify_word(self,inword):
        if inword == 'au':
            return 'à'
        if inword == 'aux':
            return 'à'
        if inword == 'du':
            return 'de'
        return inword

    #Moves the functional tags from the VN to the clitics
    def clitics_downwards(self):
        if not self.is_leaf():
            if self.cat == "VN":
                if self.type <> "None":
                    flist = re.split("/",self.type)
                    for child in self.children:
                        if child.cat in ["CLS","CLO","CLR","CL"] and len(flist) > 0:
                            child.type = flist[0]
                            flist = flist[1:]
                        child.clitics_downwards()
                else:
                    for elt in self.children:
                        elt.clitics_downwards()
            else:
                for elt in self.children:
                    elt.clitics_downwards()

    def decorate_tree(self,ptable,cotable):
        self.clitics_downwards()
        self.annotate_yield()
        self.annotate_heads(ptable,cotable)
        self.head_propagation()


    #Functions for grabbing the features for the classifiers : call them on the node to be classified
    # returns a tuple:(cat,word)
    def get_head_features(self,rhs):
        head = self.get_head(rhs)
        if head <> None:
            if head.features.has_key('headcat'): 
                return (head.cat,head.features['headcat'])
            else:
                return (head.cat,"None")
        else:
            return ("None","None")
        

    def get_head(self,rhs):
        for elt in rhs:
            if elt.type == "H":
                return elt
        return None

    def get_cohead_features(self):
        coword = "None"
        cocat = "None"
        if self.features.has_key('coheadword'):
            coword = self.features['coheadword']
        if self.features.has_key('coheadcat'):
             cocat = self.features['coheadcat']
        return (cocat,coword)

    def get_self_features(self):
        return (self.cat,self.word)
    
    def get_lhs_features(self,mothernode):
        return (mothernode.cat,mothernode.word)

    #Head node Distance (distance on the surface string between the 2 heads)
    def get_head_distance(self,rhs):
        head = self.get_head(rhs)
        if head <> None:
            return self.id-head.id
        else:
            return 0

    def get_left_corner_distance(self,rhs):
        head = self.get_head(rhs)
        if head <> None:
            return self.left_corner()-head.left_corner()
        else :
            return 0

    def left_corner(self):
        if self.is_leaf():
            return self.id
        else:
            return self.children[0].left_corner()

    #Graph extraction

    def node2vertex(self):
        return DepVertex(self.word,self.id)

    def dependency_graph(self,fmapper):
        dg = None
        if self.is_parse_failure():
            dg = graph_failure()
        else:
            root = self.node2vertex()
            dg = DependencyGraph(root)
            self.dep_graph_rec(dg,fmapper)
        return dg

    def dep_graph_rec(self,dg,fmapper):
        if not self.is_preterminal():
            orig = self.node2vertex()
            for child in self.children:
                dest = child.node2vertex()
                if not child.is_head():
                    if not child.cat == "PONCT":
                        dg.add_edge(orig,fmapper.map(child.cat,child.type),dest)
                child.dep_graph_rec(dg,fmapper)

    #Display and pretty printing
    def display_treef(self):
        if not self.is_leaf():
            res = "("+self.display_node()
            for elt in self.children:
                res = res + elt.display_treef()
            return res + ")"
        else:
            return " "+self.cat
    
    def display_tree(self):
        return self.display_rec("")

    def display_rec(self,indent):
        if self.is_parse_failure():
             return '(())'  
        elif self.is_leaf():
           return self.cat
        else:
            indent = indent + '  '
            str = '('+self.display_node()
            if (not self.is_leaf()) and (len(self.children) == 1):
                 str = str + '  ' + self.children[0].display_rec('')+')'
            else:
                str = str + '\n'
                for child in self.children:
                    str = str + indent + child.display_rec(indent+'  ')+'\n'
                str = str[:-1]+')'
            return str

    def display_tree_prolog(self,idx):
        res = 'sentence(\n'
        res = res + "id("+str(idx)+")\ndate("+str(datetime.date.today())+")\nvalidators(automatically parsed)\n"
        if self.is_parse_failure():
            res = res + "sentence_form(PARSE_FAILURE)\n"
            res = res + 'structure(\n'
        else:
            res = res + "sentence_form("+self.display_yield()+")\n"
            res = res + "structure(\n"
            res += self.display_tree()
        return res+"\n))"
            
    def display_node(self):
        word = self.word
        fstr = ''
        if word == "," :
            word = re.sub(",","<C>",word)
        if len(self.features.keys()) > 0:
            f = self.features.keys()            
            fstr = ','+f[0] + ' = ' + self.features[f[0]]
            for elt in f[1:]:
                fstr = fstr + "," + elt + ' = ' + self.features[elt]
        return self.cat+"["+self.type+","+word+","+str(self.id)+fstr+"]"

def parse_failure():
    return FTree("**FAILURE**")
                




