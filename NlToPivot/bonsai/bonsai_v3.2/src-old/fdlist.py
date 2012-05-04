#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

from triplesp import *
from dgraph import *
from random import *

#Self training of Func Labelling

# Rules
# ----------
# * if X => y
# * => y

# e.g. 

# (1st form)
# if headcat = V and depcat = N and dist < 0 then:
#      => SUJ

# (2nd form)
#      => MOD
     
# where X is a  conjunction of boolean valued features of the form : 

# 1st order features :
# * dep cat = c
# * head_cat = c
# * dist (< | >) n 
# * headword in set S
# 2nd order features ?
# * co-headword = w

# Each rule has a weight (or score)

# Classifier (Decision list)
# -----------------------------------
# * Several rules may match the same pattern
# * Rules are ordered wrt their weight
# * First matching rule fires

# * Can measure confidence rate for each rule :

#          #Fires and correct
# CR = --------------------------
#                    #Fires

# * Can also measure firing rate for each rule

# Learner
# --------------
# Learn rules : 
# for each rule template
#     generate all possible rules given the data specifications
#     -> CR < data counts

# Train on the data
# loop until convergence or n times:
#        train
#        label and evaluate

#

# TEMPLATES
# -----------------------
# if depcat = X then
#   Label

# if depcat = X and headcat = Y then 
#   Label

# if depcat = X and headcat = Y and depword in set S then
#   Label


class RuleTemplate:

	def __init__(self,depcats=[],headcats=[],depwords=[],labels=[]):
		self.labels = set(labels)		
		depcats.append(None)
		headcats.append(None)
		depwords.append(None)
		self.depcat = set(depcats)
		self.headcat = set(headcats)
		self.depword = depwords
			
		
	def generateRules(self):
		rules = []
		for depc in self.depcat:
			for headc in self.headcat:
				for depw in self.depword:
					for labc in self.labels:
						r = Rule(depc,headc,depw,labc)
						rules.append(r)			
		return rules




class Rule:
	def __init__(self,depcat=None,headcat=None,depword=None,label=None):
		self.depcat = depcat
		self.headcat = headcat
		self.depword = depword
		self.label = label
		self.firingc= 0
		self.confc = 0


        def is_default(self):
             return self.depcat==None and self.headcat==None and self.depword==None

	def __str__(self):
		res = ""
		if self.depcat <> None:
			res += "depcat="+self.depcat+" "
		if self.headcat <> None:
			res += "headcat="+self.headcat+" "
		if self.depword <> None:
			setsummary = ""
			idx = 0
			res += "depwords=set("+",".join(self.depword[0:2])+"...)"
		res += " => "+self.label + " \t("+str(self.confc)+"/"+str(self.firingc)+")"
		return res

        def cond_acc(self):
             if self.firingc <> 0:
                  return float(self.confc) / float(self.firingc) 
             return 0

	def resetcounts(self,firingc=0,confc=0):	
		self.firingc = firingc
		self.confc = confc
                				
	def match(self, edge):
		#print edge, edge.orig.feats, edge.dest.feats
		#print self

		if not(self.depcat == None or self.depcat == edge.dest.feats['pos']):
			return False
		if not(self.headcat == None or self.headcat == edge.orig.feats['pos']):
			return False
		if not(self.depword == None or  edge.dest.label in self.depword):
			return False
		#print ">>> rule matched <<< "
		return True

	def apply(self, edge, ref_edge=None):
		if self.match(edge):
			self.firingc += 1
			if ref_edge <> None:
				if ref_edge.label == self.label:
					self.confc += 1
			return self.label
		else:
			return None


#TODO : LEARN THE INITIAL WEIGHTS OF THE RULES ON LABELED DATA!

class FuncDecList:

     def __init__(self,tripleslist):
          self.traindata = tripleslist
          self.rules = []
	  self.train()

     def rank_rules(self):
          self.rules.sort(lambda x,y: cmp(x.cond_acc(), y.cond_acc()),reverse=True)

     #shortlist is a flag for displaying or removing rules that do not fire at all
     def print_rules(self,shortlist=False):
          for r in self.rules:
               if not(r.firingc == 0 and shortlist):
                    print r

     def train(self):
	     (catset,relset) = self.extractfeatvals()
	     rt = RuleTemplate(depcats=list(catset),headcats=list(catset),depwords=gram_word_lists(), labels=list(relset))
	     self.rules = rt.generateRules()
	     #shuffle(self.rules)
	     self.init_weights()
	     self.print_rules(shortlist=False)
	     self.train_loop()

     #Provides initial weights (cond acc) to the rules
     def init_weights(self):
	     for triple in self.traindata:
		     for rule in self.rules:
			     rule.apply(triple,triple)				  
	     self.rank_rules()
	     #self.print_rules(shortlist=True)
	     #self.trim_rules()
	    
 
     def reset_weights(self):
	     for rule in self.rules:
		     rule.resetcounts()	

     def classify_one(self,edge,ref_edge=None):
	     for rule in self.rules:
		     lbl = rule.apply(edge,ref_edge)
		     if lbl <> None:
			     return lbl
     
     def train_loop(self):
	     prev_acc = -1
	     idx = 1
	     while self.cond_accurracy() > prev_acc:
		     prev_acc = self.cond_accurracy()
		     self.reset_weights()
		     for triple in self.traindata:
			     self.classify_one(triple,triple)
		     self.rank_rules()
		     self.trim_rules()
		     print "\n\nIteration "+str(idx)+": condAcc:\t"+str(self.cond_accurracy()) + " Firing rate: \t" + str(self.firing_rate())
		     self.print_rules(shortlist=True)
		     idx += 1

     #Removes rules that do not fire in a single run
     def trim_rules(self):
	     rlist = []
	     for rule in self.rules:
		     if rule.firingc > 0:
			     rlist.append(rule)
	     self.rules = rlist

     def extractfeatvals(self):
	     relset = set([])
	     catset = set([])
	     for triple in self.traindata:
		     headcat = triple.orig.feats['pos']
		     depcat = triple.dest.feats['pos']
		     #headword = triple.orig.label
		     #depword = triple.dest.label
		     rel = triple.label
		     relset.add(rel)
		     catset.add(headcat)
		     catset.add(depcat)
	     return (catset,relset)
		     
     def cond_accurracy(self):
          numc = 0
          numf = 0
          for r in self.rules:
               numc += r.confc
               numf += r.firingc
	  if numf == 0:
		  return 0
          return float(numc)/float(numf)

     def firing_rate(self):
          numfires = 0
          for r in self.rules:
               numfires += r.firingc
          return float(numfires)/float(len(self.traindata))


def gram_word_lists():
	liste_avoir = ['ai', 'as', 'a', 'avons', 'avez', 'ont', 'avais', 'avait', 'avions', 'aviez', 'avaient', 
               'aurai', 'auras', 'aura', 'aurons', 'aurez', 'auront', 
               'eus', 'eut', 'eûmes', 'eûtes', 'eurent', 'aurais', 'aurait', 'aurions', 'auriez', 'auriont' 
               'aie', 'aies', 'ait', 'ayons', 'ayez', 'aient', 'eusse', 'eusses', 'eût', 'eussions', 'eussiez', 'eussent', 
               'ayant']

	liste_etre = ['suis', 'es', 'est', 'sommes', 'êtes', 'sont', 'étais', 'était', 'étions', 'étiez', 'étaient', 
              'serai', 'seras', 'sera', 'serons', 'serez', 'seront', 'fus', 'fut', 'fûmes', 'fûtes', 'furent', 
              'sois', 'soit', 'soyons', 'soyez', 'soient', 'fusse', 'fusses', 'fût', 'fussions', 'fussiez', 'fussent', 
               'serais', 'serait', 'serions', 'seriez', 'seraient' 
               'étant']

	liste_faire = ['fais', 'fait', 'faisons', 'faites', 'font', 'faisais', 'faisait', 'faisions', 'faisiez', 'faisaient', 
               'fis', 'fit', 'fîmes', 'fîtes', 'firent', 'ferai', 'feras', 'fera', 'ferons', 'ferez', 'feront', 
               'fasse', 'fasses', 'fassions', 'fassiez', 'fassent', 'fisse', 'fisses', 'fissions', 'fissiez', 'fissent',
               'ferais', 'ferait', 'ferions', 'feriez', 'feraient',
               'faisant', 
               'faire']

	liste_etre_pp = ['allé', 'allée', 'allés", allées', 'apparu', 'apparue', 'apparus', 'apparues', 
                 'arrivé', 'arrivée', 'arrivés', 'arrivées', 'décédé', 'décédée', 'décédés', 'décédées',  
                 'demeuré', 'demeurée', 'demeurés', 'demeurées', 'devenu', 'devenue', 'devenus', 'devenues',  
                 'entré', 'entrée', 'entrés', 'entrées', 'interven', 'intervenue', 'intervenus', 'intervenues',  
                 'mort', 'morte', 'morts', 'mortes', 'né', 'née', 'nés', 'nées', 
                 'parti', 'partie', 'partis', 'parties', 'parvenu', 'parvenue', 'parvenus', 'parvenues',  
                 'appar', 'apparue', 'apparus', 'apparues', 'appar', 'apparue', 'apparus', 'apparues',
                 'redevenu', 'redevenue', 'redevenus', 'redevenues', 'reparti', 'repartie', 'repartis', 'reparties' 
                 'resté', 'restée', 'restés', 'restées', 'retombé', 'retombée', 'retombés', 'retombées', 
                 'revenu', 'revenue',  'revenus',  'revenues', 'tombé', 'tombée', 'tombés', 'tombées', 
                 'venu', 'venue',  'venus',  'venues']
	liste_aux_pass = ['été', 'être']
	liste_reflechis= ['se',"s'"]
	liste_de_words = ['de',"d'", 'du', 'des']
	liste_de_obj_nom = ['%','pourcentage','milliards','milliard','million','millions']
	return [liste_aux_pass,liste_de_words,liste_reflechis,liste_de_obj_nom,liste_avoir,liste_etre,liste_faire,liste_etre_pp]

def remove_pred_arg_triples(tripleslist):
	res = []
	for t in tripleslist:
		if not (t.label in ['suj','obj','a_obj','a_obj#','p_obj','de_obj','de_obj#','ats','ato'] and t.orig.feats['pos'] == 'V'):
			res.append(t)
	return res

dgraphlist = parse_triples_dir('/Users/Benoit/Desktop/p7-107-tocorrect-regenerated')
tripleslist = []
for graph in dgraphlist:
	tripleslist = tripleslist + list(graph.depforest.edges)
decisionlist = FuncDecList(remove_pred_arg_triples(tripleslist))



