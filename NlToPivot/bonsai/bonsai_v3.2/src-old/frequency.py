#!/usr/bin/python

# A utility class for computing some basic statistics on the treebank
from LabelledTree import LabelledTree
from LabelledTree import print_cfg_rule
from XmlReader import XmlReader
from PennTreeBankReader import *

import sys
class FrequencyTable:
      def __init__(self,subcat=False,traces=False):
      	  self.type_dico = {}
          self.fun_dico = {}
          self.tag_dico = {}
          self.sym_dico = {}
          self.pcfg_dico = {}
          self.pcfg_detail = {}
          self.comp_count = 0
          self.num_tok = 0
          self.num_rules = 0
          self.num_fun = 0
          self.num_sym = 0
          self.num_tags = 0
          self.num_sents = 0
          self.subcat = subcat
          self.traces = traces

      def do_counts(self,treebank):
            self.num_sents = len(treebank)
            for tree in treebank:
                  tree.set_print_options(self.subcat,False,False,False)
                  if self.traces:
                        tree.remove_traces()
                  self.process_tree(tree)
                  rules = tree.cfg_rules_strict()
                  #rules = tree.cfg_rules()
                  for rule in rules:
                        self.add_rule_count(print_cfg_rule(rule))
                        if not self.pcfg_detail.has_key(rule[0].label):
                              self.pcfg_detail[rule[0].label] = set([])
                        self.pcfg_detail[rule[0].label].add(print_cfg_rule(rule))                              
      def add_rule_count(self,rule):
            self.num_rules = self.num_rules + 1
            if  self.pcfg_dico.has_key(rule):
                  self.pcfg_dico[rule] = self.pcfg_dico[rule] + 1
                  return False
            else:
                  self.pcfg_dico[rule] = 1
                  return True

      def add_sym_count(self,sym):
            self.num_sym = self.num_sym + 1
            if  self.sym_dico.has_key(sym):
                  self.sym_dico[sym] = self.sym_dico[sym] + 1
            else:
                  self.sym_dico[sym] = 1

      def add_type_count(self,type):
            self.num_tok = self.num_tok + 1
            if  self.type_dico.has_key(type):
                  self.type_dico[type] = self.type_dico[type] + 1
            else:
                  self.type_dico[type] = 1

#      def_build_node... see labelled tree

      def add_tag_count(self,tag):
            self.num_tags = self.num_tags + 1
            if self.tag_dico.has_key(tag):
                  self.tag_dico[tag] = self.tag_dico[tag] + 1
            else:
                  self.tag_dico[tag] = 1

      def add_fun_count(self,fun):
            self.num_fun = self.num_fun + 1
            if self.fun_dico.has_key(fun):
                  self.fun_dico[fun] = self.fun_dico[fun] + 1
            else:
                  self.fun_dico[fun] = 1
      
      def revert_dico(self,dico):
            new_dico = {}
            keys = dico.keys()
            for key in keys:
                  val = dico[key]
                  if new_dico.has_key(val):
                        new_dico[val].append(key)
                  else:
                        new_dico[val] = [key]
            return new_dico

      def process_tree(self,tree):            
            if tree.is_leaf(): 
                  self.add_type_count(tree.label)
            else:
                  if len(tree.children) == 1 and tree.children[0].is_leaf():
                        self.add_tag_count(tree.print_node())
                  else:
                        self.add_sym_count(tree.print_node())
                  if tree.funlabel <> None:
                        self.add_fun_count(tree.funlabel)
                  if tree.compound and tree.children[0].is_leaf():
                        self.comp_count = self.comp_count + 1
                  for child in tree.children:
                        self.process_tree(child)

      def get_sorted_rev_dico(self,adico):
            sorted_rev_dico = []
            dico = self.revert_dico(adico)
            list = dico.keys()
            list.sort(reverse=True)
            for elt in list:
                  vals  = dico[elt]
                  for val in vals:
                       sorted_rev_dico.append( (elt,val) )
            return sorted_rev_dico


      def print_stat_table(self,dico,max_length=-1):
            dico = self.revert_dico(dico)
            list = dico.keys()
            list.sort(reverse=True)
            res = ''
            max = max_length
            if len(list) < max_length or max_length == -1:
                  max = len(list)
            for elt in range(0,max):
                  vals  = dico[list[elt]]
                  for val in vals:
                        res = res + str(list[elt]) +'\t' + val+'\n'
            return res

      def print_generic_stats(self):
            res =''
            res = res + 'Token counts : '+str(self.num_tok)+'\n'
            res = res + 'Type counts : '+ str(len(self.type_dico))+'\n'
            res = res + 'Symbol counts : '+ str(self.num_sym)+'\n'
            res = res + 'Tag counts : '+ str(self.num_tags)+'\n'
            res = res + 'Function counts : '+ str(self.num_fun)+'\n'
            res = res + 'Context Free Grammar rules counts : '+ str(self.num_rules)+'\n'
            res = res + 'Context Free Grammar rules type counts : '+ str(len(self.pcfg_dico.keys()))+'\n'
            res = res + 'Sentence counts : '+ str(self.num_sents)+'\n'
            cpd_ratio = float(self.comp_count) / float(self.num_tok) * float(100)
            res = res + 'Compound tokens : '+ str(self.comp_count)+' ('+str(cpd_ratio)+'%) \n'
            return res

      def print_type_list(self,max_length=-1):
            #str = "Type counts\n-------------------\n"
            str =""
            str = str + self.print_stat_table(self.type_dico,max_length)
            return str

      def print_sym_list(self,max_length=-1):
            #str = "Non terminal symbols counts\n-------------------\n"
            str =""
            str = str + self.print_stat_table(self.sym_dico,max_length)
            return str

      def print_tag_list(self,max_length=-1):
            #str = "Tag counts\n-------------------\n"
            str =""
            str = str + self.print_stat_table(self.tag_dico,max_length)
            return str

      def print_fun_list(self,max_length=-1):
            #str = "Function counts\n-------------------\n"
            str =""
            str = str + self.print_stat_table(self.fun_dico,max_length)
            return str

      def print_rules_list(self,max_length=-1):
            #str = "PCFG counts\n-------------------\n"
            str =""
            str = str + self.print_stat_table(self.pcfg_dico,max_length)
            return str

      #experimental hack
      def print_detailed_dispersion(self):
            res  =''
            for key in self.sym_dico:
                  if self.pcfg_detail.has_key(key):
                        res += key +' n:'+str(len(self.pcfg_detail[key]))+'   c:'+str(self.sym_dico[key])+'  d:'+str(float(len(self.pcfg_detail[key]))/float(self.sym_dico[key]))+'\n'
                        #for rule in pcfg_detail[key]:
                        #     res+=print_cfg_rule(rule)+'\n'
            return res


#Tests
# reader = XmlReader()
# treebank = reader.read_dir_xml(sys.argv[1])
# stats = FrequencyTable()
# stats.do_counts(treebank)
# print stats.print_generic_stats()
# print stats.print_type_list(20).encode('iso-8859-1')
# print stats.print_sym_list().encode('iso-8859-1')
# print stats.print_tag_list().encode('iso-8859-1')
# print stats.print_fun_list().encode('iso-8859-1')
# print stats.print_rules_list(40).encode('iso-8859-1')

#reader = PtbReader()
#stream  = sys.stdin.read()
#(fflag,treebank) = reader.parse_treebank(stream)
#stats = FrequencyTable()
#stats.do_counts(treebank)
#print stats.print_generic_stats()
#print stats.print_type_list(20)#.encode('iso-8859-1')
#print stats.print_sym_list()#.encode('iso-8859-1')
#print stats.print_tag_list()#.encode('iso-8859-1')
#print stats.print_fun_list()#.encode('iso-8859-1')
#print stats.print_rules_list()#.encode('iso-8859-1')
