#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# PivotReader
#
# Author : Francois Guerin
# Date : Sept 2008
# Objectif:
# lire un fichier au format pivot (celui de MFalco)
# et insérer les données dans un arbre

import re
import os
import sys
from dgraph import *

class PReader:

	def parse_dir_pivot(self,dirname):
		#print "[PivotReader] (PReader) parse_dir_pivot("+str(dirname)+")"
		ptreelist=[]
		files = os.listdir(dirname)
		nb_fic=0
		sorted_files={}
		for file in files:
			if re.match("^.*\.(txt|piv)$",file):
				id=re.search("^[a-zA-Z]+_(\d+)[^\d]+",file).group(1)
				sorted_files[int(id)]=file
		for file in sorted(sorted_files.keys()):		
				nb_fic=nb_fic+1
				#sys.stderr.write("--------------------------------------\n>Parsing input file "+file+'\n')
				instream = os.path.join(dirname,sorted_files[file])
				ptreelist.append(self.parse_pivot(instream))
		#print ">>> "+str(nb_fic)+" fichiers dans le dossier: "+dirname
		return ptreelist

	# string > parser du string et mise en memoire dans un objet ptree
	def kiddy_parse_pivot(self,instream):
		#print "[PivotReader] (PReader) kiddy_parse_pivot("+str(instream)+")"
		ptree = PTree()
		lines = open(instream,'r')
		sentence = False
		surf_deps = False
		features = False
		
		for line in lines.readlines():
			#line=line.decode('latin1')
		
		
			#print "LIGNE: "+line.rstrip()
			#sentence
			if not sentence and re.match("^sentence\($",line):
				sentence = True
			#id
			elif sentence and re.match("^id\((.*?),(.*?)\)$",line):
				f=re.search("^id\((.*?),(.*?)\)$",line)
				ptree.set_idxml(f.group(1),f.group(2))
			#date
			elif sentence and re.match("^date\((.*?)\)$",line):
				f=re.search("^date\((.*?)\)$",line)
				ptree.set_date(f.group(1))
			#validators
			elif sentence and re.match("^validators\((.*?)\)$",line):
				f=re.search("^validators\((.*?)\)$",line)
				ptree.set_validator(f.group(1))
			#sentence_form
			elif sentence and re.match("^sentence_form\((.*?)\)$",line):
				f=re.search("^sentence_form\((.*?)\)$",line)
				ptree.set_sentence_form(f.group(1))
			#surf_deps
			elif sentence and re.match("^surf_deps\($",line):
				sentence = False
				surf_deps = True
			#deps
			elif surf_deps and re.match("^(.*?)\((.*?)~(.*?),(.*?)~(.*?)\)$",line):
				f=re.search("^(.*?)\((.*?)~(.*?),(.*?)~(.*?)\)$",line)
				ptree.add_surf_deps(Triple(f.group(1),f.group(3),f.group(5)))
				ptree.add_feat_form(f.group(3),f.group(2))
				ptree.add_feat_form(f.group(5),f.group(4))
			#root
			elif surf_deps and re.match("^head\((.*?),(.*?)~(.*?)\)$",line):
#				f=re.search("^head\(null,(.*?)~(.*?)\)$",line)
#				ptree.set_root(f.group(2))
				pass
			#features
			elif surf_deps and re.match("^features\($",line):
				surf_deps = False
				features = True
			#lemma
			elif features and re.match("^lemma\((.*.)~(.*?),(.*?)\)$",line):
				f=re.search("^lemma\((.*?)~(.*?),(.*?)\)$",line)
				ptree.add_feat_lemma(f.group(2),f.group(3))
			#pos
			elif features and re.match("^pos\((.*.)~(.*?),(.*?)\)$",line):
				f=re.search("^pos\((.*?)~(.*?),(.*?)\)$",line)
				ptree.add_feat_pos(f.group(2),f.group(3))
				ptree.add_feat_form(f.group(2),f.group(1))
			elif re.match("^\)+$",line):
				pass
			else:
				print "\n! Erreur dans le parsing:\n	>"+line
				exit
				return PTree()				
		
		#print str(ptree)
		lines.close()
		#print "PTREE["+ptree.ident+"]["+ptree.xml+"]"
		return ptree
			
	# string > parser du string et mise en memoire dans un objet ptree
	def parse_pivot(self,instream):
		#print "[PivotReader] (PReader) parse_pivot("+str(instream)+")"
		return self.kiddy_parse_pivot(instream)
		

class Triple:

	def __init__(self,fonction,gouverneur,dependant):
		self.fonction=fonction
		self.gouverneur=gouverneur
		self.dependant=dependant

	def __repr__(self):
		s=self.fonction+"("+self.gouverneur+","+self.dependant+")"
		return s

class TripleListe:

	def __init__(self):
		self.liste=[]
		
	def __repr__(self):
		s=""
		liste=self.liste
		for l in liste:
			s+="\n"+str(l)
		return s
		
	def add_triple(self,triple):
		self.liste.append(triple)

class PTree:

	def __init__(self,ident=None):
		self.ident=ident
		self.xml=None
		self.date=None
		self.validator=None
		self.sentence_form=None
		self.surf_deps=TripleListe()
#		self.root = -1
		self.lexique={}
	
	def __repr__(self):
		s="ARBRE:\n"
		if self.ident:
			s+="	ID: "+self.ident+"\n"
		if self.xml:
			s+="	XML: "+self.xml+"\n"
		if self.date:
			s+="	DATE: "+self.date+"\n"
		if self.validator:
			s+="	VALIDATORS: "+self.validator+"\n"
		if self.sentence_form:
			s+="	SENTENCE_FORM: "+self.sentence_form+"\n"
		if self.surf_deps:
			s+="	SURF_DEPS:\n"+str(self.surf_deps)+"\n"
			#s+="head(null,"+self.lexique[self.root]['form']+"~"+self.root+")\n"
		if self.lexique:
			s+="	LEXIQUE:\n"
			for i in self.lexique.keys():
				s+="("+i+") "
				if self.lexique[i].has_key('form'):
					s+=" FORM:"+self.lexique[i]['form']		
				if self.lexique[i].has_key('pos'):
					s+=" POS:"+self.lexique[i]['pos']
				if self.lexique[i].has_key('lemma'):
					s+=" LEMMA:"+self.lexique[i]['lemma']
				s+="\n"
		return s
	
#	def set_root(self,id_root):
#		self.root=id_root
	
	def set_idxml(self,ident,xml):
		self.ident=ident
		self.xml=xml
	
	def set_date(self,date):
		self.date=date
	
	def set_validator(self,valid):
		self.validator=valid
		
	def set_sentence_form(self,sentence):
		self.sentence_form=sentence
	
	def init_surf_deps(self):
		self.surf_deps=TripleListe()
			
	def add_surf_deps(self,triple):
		self.surf_deps.add_triple(triple)
		
	def add_feat_pos(self,index,pos):
		if not self.lexique.has_key(index):
			self.lexique[index]={}
		self.lexique[index]['pos']=pos
	
	def add_feat_form(self,index,form):
		if not self.lexique.has_key(index):
			self.lexique[index]={}
		self.lexique[index]['form']=form
							
	def add_feat_lemma(self,index,lemma):
		if not self.lexique.has_key(index):
			self.lexique[index]={}
		self.lexique[index]['lemma']=lemma
	
	# PTree2DependencyGraph()
	#	transforme l'arbre pivot en un graphe de dépendances
	def PTree2DepSentence(self):
		dg = DependencyGraph()
		dico_vertex ={}
		for i in self.lexique.keys():
			dico_vertex[i]=DepVertex(self.lexique[i]['form'],int(i))
			dico_vertex[i].feats={}
			if self.lexique[i].has_key('pos'):
				dico_vertex[i].feats['pos'] = self.lexique[i]['pos']
			if self.lexique[i].has_key('lemma'):
				dico_vertex[i].feats['lemma'] = self.lexique[i]['lemma']
		for tr in self.surf_deps.liste:
			dg.add_edge( dico_vertex[tr.gouverneur] , tr.fonction , dico_vertex[tr.dependant] )
		# vérification de l'existence de dummy_roots (sinon création)
		has_roots = False
		for tr in self.surf_deps.liste:
			if dico_vertex[tr.gouverneur].idx == -1:
				has_roots = True
				break
		if not has_roots:
			dg.add_dummy_root()
		# création de la DepSentence
		ds = DepSentence(self.ident,self.xml,self.validator,self.date,dg)
		return ds
