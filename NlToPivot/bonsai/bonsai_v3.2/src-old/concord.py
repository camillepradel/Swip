#!/usr/bin/env python -0
# -*- coding: iso-8859-1 -*-
import re

# This is an elementary tgrep made for statistical parsing and grammatical exploration purposes.
# It performs basic matches on a pcfg or on a treebank

class Tgrep:
    def __init__(self,treebank):
        self.set_treebank(treebank)

    #The rule is supposed to be a list or a tuple
    #The function returns the indexes of the trees matched in the treebank
    def match_treebank(self, rule):
        res = []
        for i in range(len(self.treebank)):
            if self.match_tree(self.treebank[i],rule):
                res.append(i)
        return res

    def set_treebank(self,treebank):
        self.treebank = treebank

    #The function returns a boolean indicating if the tree is matched by the rule
    def match_tree(self,tree,rule):
        rules = tree.cfg_rules_strict()
        for r in rules:
            srule = []
            for s in r:
                srule.append(s.label)
            if self.equals(rule,srule):
                return True
            
        return False

    #The function returns the subset of the treebank that is matched
    def search_treebank(self,rule):
        idxes = self.match_treebank(rule)
        treelist = []
        for idx in idxes:
            treelist.append(self.treebank[idx])
        return treelist

    #Tests if two rules are equal
    def equals(self,ruleA,ruleB):
        if(len(ruleA) == len(ruleB)):
            for i in range(len(ruleA)):
                if not ruleA[i] == ruleB[i]:
                    return False
            return True
        return False

    #Match w/ an lhs symbol
    def smatch_treebank(self,sym):
        res = []
        for i in range(len(self.treebank)):
            if self.smatch_tree(self.treebank[i],sym):
                res.append(i)
        return res

     #Match w/ an lhs symbol
    def smatch_tree(self,tree,sym):
        symbols = tree.tree_nt_sym_nodes()
        for s in symbols:
            if s.label == sym:
                return True
        return False

    #Match w/ an lhs symbol
    def ssearch_treebank(self,sym):
        idxes = self.smatch_treebank(sym)
        treelist = []
        for idx in idxes:
            treelist.append(self.treebank[idx])
        return treelist
