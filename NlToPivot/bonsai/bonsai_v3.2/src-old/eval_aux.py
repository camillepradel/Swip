#!/usr/bin/env python -O
# -*- coding: iso-8859-1 -*-
#

# Fichier auxiliaire regroupant des classes et des méthodes
# pour le script d'évaluation "super_eval"

# class Lexique:   lexique apparaissant dans un (ou plusieurs) LabelledTree (selon l'énoncé et son existence dans le corpus d'apprentissage)
# class Forme : lexique apparaissand dans un (ou plusieurs) LabelledTree
# class Counter : calcul des scores (précision, rappel et fscore)
# class Triplet : représentation interne de triplets
# class Triplets : représentation de listes de triplets (et création de listes à partir de LabelledTrees)
# class Matrice : représentation de matrice de confusion (matrices NxN)
# class Eval-parsing : évaluation du parsing (tagging et brackets)
# class Eval_tagging : évaluation du tagging
# class Eval_brackets : évaluation des constituants (PARSEVAL-like)
# class Eval_funlabels : évaluation de l'assignation de labels fonctionnels
# class Eval_deps : évaluation des heuristiques et de la conversion en dépendances
# class Comp_funlabelling : comparaison des étapes de traitement entre le RAW et le FUN
# class Comp_dependances1 : comparaison des étapes de traitement entre le PTB et le PIVOT
# class Comp_dependances2 : comparaison des étapes de traitement entre le RAW et le PIVOT
# class Methodes : Traitement des fichiers utiles pour l'évaluation demandée.

import re
import os
import sys

from steps import *

# Recensement du lexique apparaissant dans un fichier
# (l'information d'apparition dans le corpus d'apprentissage peut être ajoutée)
class Lexique:

	def __init__(self):
		# le lid de la forme dans un énoncé (auto-incrémental)
		self.lid=-1
		# table associant à chaque énoncé, une liste de lid associé chacun à 'mot' et 'tag'
		# (la forme et la Pos de la forme dans l'énoncé en question)
		self.table={}
		# table associant à chaque forme rencontré un booléen (s'il est dans le corpus d'apprentissage ou non)
		self.inconnus={}
		self.words_per_sent={}
	
	# Reset du lid (nouvel énoncé)	
	def reset_lid(self):
		self.lid=-1
	
	# Ajout dans l'énoncé "id_sent", au lid courant, de "mot" et de "tag"	
	def add_tag(self,id_sent,mot,tag):
		if not self.table.has_key(id_sent):
			self.table[id_sent]={}
			self.reset_lid()
			self.words_per_sent[id_sent]=set([])
		self.lid+=1
		self.table[id_sent][self.lid]={}
		self.table[id_sent][self.lid]['mot']=mot
		self.table[id_sent][self.lid]['tag']=tag
		self.words_per_sent[id_sent].add(mot)
		self.inconnus[mot]=False
				
	def __str__(self):
		string=""
		for id_sent in self.table.keys():
			for lid in self.table[id_sent].keys():
				mot = self.table[id_sent][lid]['mot']
				tag = self.table[id_sent][lid]['tag']
				if not self.inconnus[mot]:
					string += "x\t"
				else:
					string += " \t"
				string += str(id_sent)+"\t"+str(lid)+"\t"+str(mot)+"\t"+str(tag)+"\n"
		return string
	
	# Renvoie le mot dans l'énoncé "id_sent" à l'index "lid"	
	def mot(self,id_sent,lid):
		return self.table[id_sent][lid]['mot']

	# Renvoie tag dans l'énoncé "id_sent" à l'index "lid"
	def tag(self,id_sent,lid):
		return self.table[id_sent][lid]['tag']	
	
	# A partir d'une liste de LabelledTree, remplit l'objet courant
	def fill_tags_list(self,labelledtree_list):
		for i in range(len(labelledtree_list)):
			self.reset_lid()
			lb=labelledtree_list[i]
			self.fill_tags(i,lb)
	
	# A partir d'un LabelledTree, remplit les informations pour l'énoncé "index"		
	def fill_tags(self,index,labelledtree):
		if labelledtree.is_terminal_sym():
			mot = labelledtree.children[0].label
			label = labelledtree.label
			self.add_tag(index,mot,label)
		else:
			for child in labelledtree.children:
				self.fill_tags(index,child)
	
	# A partir d'un objet Formes, remplit la table "inconnus"
	def set_inconnus(self,formes):
		for mot in self.inconnus.keys():
			if not formes.has_form(mot):
				self.inconnus[mot]=True
			else:
				self.inconnus[mot]=False		

# Recensement des formes apparaissant dans un arbre de type LabelledTree
class Formes:
	
	def __init__(self):
		self.liste=set([])
		
	def __str__(self):
		return str(self.liste)			
	
	# Ajoute une forme à la liste courante
	def add_forme(self,forme):
		self.liste.add(forme)
	
	# A partir d'une liste de LabelledTree, ajoute à la liste courante les formes contenues dans ces derniers	
	def get_formes_list(self, labelledtree_list):
		for lb in labelledtree_list:
			self.get_formes(lb)
	
	# A partir d'un LabelledTree, ajoute à la liste courant les formes contenus dans ce dernier.
	def get_formes(self,labelledtree):
		if labelledtree.is_terminal_sym():
			self.add_forme(labelledtree.children[0].label)
		else:
			for child in labelledtree.children:
				self.get_formes(child)
	
	# Dit si une forme apparait dans la liste courante.			
	def has_form(self,forme):
		if forme in self.liste:
			return True
		else:
			return False		

# Mesures					
class Counter:

    def __init__(self):
        self.fc = 0
        self.found = 0
        self.correct = 0
    
    # Ajoutes aux comptes courantes les comptes en argument   
    def add_counts(self,fc,found,correct):
        self.fc += fc
        self.found += found
        self.correct += correct
    
    # Divise les comptes courants par "number"+1 (éviter division par zéro)
    def divide_counts_by(self,number):
    	self.fc = float(self.fc)/(number +1)
    	self.found = float(self.found)/(number +1)
    	self.correct = float(self.correct)/(number +1)

	# Renvoie la précision
    def prec(self):
        if self.correct + self.found == 0:
    		return 1
        elif self.found == 0:
            return 0
        return float(self.fc)/float(self.found)

	# Renvoie le rappel
    def rec(self):
    	if self.correct + self.found == 0:
    		return 1
        elif self.correct == 0:
            return 0
        return float(self.fc)/float(self.correct)

	# Renvoie le fscore
    def fscore(self):
        if self.prec() + self.rec() == 0:
            return 0
        return  2 * (self.prec() * self.rec()) / (self.prec() + self.rec()) 

	# Renvoie les scores sous forme de triplets
    def scores(self):
        return (self.prec(),self.rec(),self.fscore())

	# Renvoie les scores sous formes de lignes csv (avec ; comme séparateur)
    def as_csv(self):
		return ";".join(re.split("\t",str(self)))

    def __str__(self):
    	string = "%.2f\t%.2f\t%.2f\t" % self.scores()
    	return string

# Représentation des triplets		
class Triplet:

	def __init__(self,x,y,z):
		self.x = x
		self.y = y
		self.z = z
		
	def __str__(self):
		return str(self.y)+"("+str(self.x)+","+str(self.z)+")"

# Représentation des listes de triplets		
class Triplets:

	def __init__(self):
		self.liste=set([])
	
	# Ajoute un triplet à la liste courante	
	def add_triplet(self,triplet):
		self.liste.add(triplet)
		
	def __str__(self):
		string =""
		for t in self.liste:
			string += str(t)+"\n"
		return string
	
	# Dit si un triplet apparait dans la liste courante	
	def has_triplet(self,triplet):
		for t in self.liste:
			if t.x == triplet.x and t.y == triplet.y and t.z == triplet.z:
				return True
				break
		return False

	# Fonctions de parcours de d'arbres (labelledtree)
	# pour extraire différents types de triplets

	# Peres et fils: 
	# le pere correspond au chemin entre le noeud-pere
	# le label est le numéro du noeud-fils parmi les fils du noeud-pere
	# le fils correspond au label du noeud-fils
	# ex: (SENT_1_PP_2_NP,1,NPP)
	def tree2spines(self,labelledtree,ancestor=""):
		if labelledtree.is_terminal_sym():
			pass
		else:
			cpt = 0
			for child in labelledtree.children:
				cpt += 1
				if ancestor:
					a_name = ancestor+"_"+str(cpt)+"_"+labelledtree.label
					t = Triplet(a_name,cpt,child.label)
					self.add_triplet(t)
					self.tree2spines(child,a_name)
				else:
					self.tree2spines(child,labelledtree.label)
	
	# Indexs de projection
	# le label du noeud courant et les indexs des spans qu'il projette
	# ex: (0,SENT,75)
	def tree2widthTriplets(self,labelledtree):
		if labelledtree.is_terminal_sym():
			pass
		else:
			label = labelledtree.label
			if not label:
				label = "_"
			t = Triplet(labelledtree.start_lid, label, labelledtree.end_lid)
			self.add_triplet(t)
			for child in labelledtree.children:
				self.tree2funTriplets_with_cat(child)

	# Labels fonctionnels et projections
	# le label fonctionnels du noeud courant (si label présent) et les ids max qu'il projette
	# ex: (0,SUJ,10)
	def tree2funTriplets(self,node):
		if node.is_terminal_sym():
			pass
		else:
			if node.funlabel <> None:
				fun = node.funlabel
				t = Triplet(node.start_lid,fun,node.end_lid)
				self.add_triplet(t)
			for child in node.children:
				self.tree2funTriplets_with_cat(child)

	# Labels fonctionnels et catégories syntaxiques
	# Tous les noeuds de l'arbre (catégorie syntaxique + label fonctionnel si présent + ids max projetés)
	# ex: (0,NP-SUJ,10)
	def tree2funTriplets_with_cat(self,node):
		if node.is_terminal_sym():
			pass
		else:
			fun = ""
			if node.funlabel <> None:
				fun += "-"+node.funlabel
			label = node.label
			if not label:
				label = "_"
			t = Triplet(node.start_lid,label+fun,node.end_lid)
			self.add_triplet(t)
			for child in node.children:
				self.tree2funTriplets_with_cat(child)



# Représentation des matrices
# (en réalité, nous avons plutot une sorte de liste d'adjacence,
# l'apparence de matrice NxN se fait à l'affichage)
class Matrice:

	def __init__(self):
		self.matrice = {}
	
	# Ajoute à la matrice courante +1 à l'intersection de la ligne "reference" et de la colonne "hypothese"	
	def add_count(self,reference,hypothese):
		if not self.matrice.has_key(reference):
			self.matrice[reference]={}		
		if not self.matrice[reference].has_key(hypothese):
			self.matrice[reference][hypothese]=0
		self.matrice[reference][hypothese]+=1
	
	def __str__(self):
		return self.tostring("\t")
	
	# Représentation de la matrice sous forme de string
	def tostring(self,sep=" "):
		entete=sep
		string=""
		dots = set([])
		for k in self.matrice.keys():
			dots.add(k)
			for j in self.matrice[k].keys():
				dots.add(j)
		dots=list(dots)
		dots.sort()
		for k in dots:
			entete+=k+sep
			if self.matrice.has_key(k):
				string+=k+sep
				for j in dots:
					if self.matrice[k].has_key(j):
						string+=str(self.matrice[k][j])+sep
					else:
						string+="0"+sep
				string+="\n"
			else:
				string+=k+sep
				for j in dots:
					string+="0"+sep
				string+="\n"
		entete+="\n"
		return entete+string
		pass
	
	# Renvoie une représentation de la matrice sous forme csv (avec ; comme séparateur)	
	def as_csv(self):
		return self.tostring(sep=";")
	
	# Ecrit la matrice dans le fichier indiqué en argument	
	def write_matrice(self,file):
		f = open(file,"w")
		f.write(self.as_csv())
		f.close()

# Evaluation du parsing
# (regroupant le tagging et le brackettage)
class Eval_parsing:
	
	def __init__(self,eval_tagging,eval_brackets):
		self.eval_tagging = eval_tagging
		self.eval_brackets = eval_brackets
		
	def __str__(self):
		string = "\t|\t\t\t\tTAGS\t\t\t\t\t|\tPARSEVAL\n"
		string += "\t|\tCONNUS\t\t|\tINCONNUS\t|\tTOTAL\t\t|\n"
		string += "SENT\tPREC\tREC\tFSCORE\tPREC\tREC\tFSC\tPREC\tREC\tFSCORE\tPREC\tREC\tFSC\n"
		string +="========================================================================================================\n"
		for id_sent in self.eval_tagging.counts_per_sent_all.keys():
			string += str(id_sent)+"\t"
			string += str(self.eval_tagging.counts_per_sent_connus[id_sent])
			string += str(self.eval_tagging.counts_per_sent_inconnus[id_sent])
			string += str(self.eval_tagging.counts_per_sent_all[id_sent])
			string += str(self.eval_brackets.counts_per_sent[id_sent])
			string += "\n"
		string +="========================================================================================================\n"
		string += "TOTAL \t"
		string += str(self.eval_tagging.counts_total_connus)
		string += str(self.eval_tagging.counts_total_inconnus)
		string += str(self.eval_tagging.counts_total_all)
		string += str(self.eval_brackets.counts_total)
		string += "\n\n"
		string += "En fonction du nombre de PP:\n"
		for i in self.eval_brackets.counts_nb_PP.keys():
			string += "\t"+str(i)+" -> "+str(self.eval_brackets.counts_nb_PP[i]['scores'])+"\t/"+str(self.eval_brackets.counts_nb_PP[i]['nb'])+"\n"
		string += "En fonction du nombre de COORD:\n"
		for i in self.eval_brackets.counts_nb_COORD.keys():
			string += "\t"+str(i)+" -> "+str(self.eval_brackets.counts_nb_COORD[i]['scores'])+"\t/"+str(self.eval_brackets.counts_nb_COORD[i]['nb'])+"\n"
		string += "En fonction du nombre de SREL:\n"
		for i in self.eval_brackets.counts_nb_SREL.keys():
			string += "\t"+str(i)+" -> "+str(self.eval_brackets.counts_nb_SREL[i]['scores'])+"\t/"+str(self.eval_brackets.counts_nb_SREL[i]['nb'])+"\n"		
		string += "En fonction du nombre de mots dans la phrase:\n"
		for i in self.eval_brackets.counts_nb_mots.keys():
			string += "\t"+str(i)+" -> "+str(self.eval_brackets.counts_nb_mots[i]['scores'])+"\t/"+str(self.eval_brackets.counts_nb_mots[i]['nb'])+"\n"
		string += "En fonction du nombre de mots inconnus dans la phrase:\n"
		for i in self.eval_brackets.counts_inconnus.keys():
			string += "\t"+str(i)+" -> "+str(self.eval_brackets.counts_inconnus[i]['scores'])+"\t/"+str(self.eval_brackets.counts_inconnus[i]['nb'])+"\n"
		return string	

# Evaluation du tagging
# (mots inconnus vs mots connus à l'apprentissage)
# Parcours des formes du lexique de <test>, énoncé par énoncé:
# si le meme tag apparait pour la meme forme dans le meme énoncé dans le lexique du <gold>, PERTINENTS_RETOURNES ++
# Pour chaque forme, par énoncé, du <test>: RETOURNES ++
# Pour chaque forme, par énoncé, du <gold>: PERTINENTS ++
class Eval_tagging:

	def __init__(self):
		self.counts_per_sent_connus = {}
		self.counts_per_sent_inconnus = {}
		self.counts_per_sent_all = {}
		self.counts_total_connus = Counter()
		self.counts_total_inconnus = Counter()
		self.counts_total_all = Counter()
		self.matrice = Matrice()
		self.counts_mots_inconnus = {}
		
	def __str__(self):
		string = "\t|CONNUS\t\t\t|\tINCONNUS\t|\tTOTAL\n"
		string += "SENT\tPREC\tREC\tFSC\tPREC\tREC\tFSCORE\tPREC\tREC\tFSC\n"
		string +="================================================================================\n"
		for id_sent in self.counts_per_sent_all.keys():
			string += str(id_sent)+"\t"
			string += str(self.counts_per_sent_connus[id_sent])
			string += str(self.counts_per_sent_inconnus[id_sent])
			string += str(self.counts_per_sent_all[id_sent])
			string += "\n"
		string +="================================================================================\n"
		string += "TOTAL \t"
		string += str(self.counts_total_connus)
		string += str(self.counts_total_inconnus)
		string += str(self.counts_total_total_all)
		string += "\n"		
		return string

	#
	# Fonctions de comptage
	#
	def add_found(self,id_sent,connu=False):
		if not self.counts_per_sent_all.has_key(id_sent):
			self.counts_per_sent_connus[id_sent] = Counter()
			self.counts_per_sent_inconnus[id_sent] = Counter()
			self.counts_per_sent_all[id_sent] = Counter()
		if connu:
			self.counts_per_sent_connus[id_sent].found += 1
			self.counts_total_connus.found += 1
		else:
			self.counts_per_sent_inconnus[id_sent].found += 1
			self.counts_total_inconnus.found += 1
		self.counts_per_sent_all[id_sent].found += 1
		self.counts_total_all.found += 1
	
	def add_correct(self,id_sent,connu=False):
		if not self.counts_per_sent_all.has_key(id_sent):
			self.counts_per_sent_connus[id_sent] = Counter()
			self.counts_per_sent_inconnus[id_sent] = Counter()
			self.counts_per_sent_all[id_sent] = Counter()
		if connu:
			self.counts_per_sent_connus[id_sent].correct += 1
			self.counts_total_connus.correct += 1
		else:
			self.counts_per_sent_inconnus[id_sent].correct += 1
			self.counts_total_inconnus.correct += 1
		self.counts_per_sent_all[id_sent].correct += 1
		self.counts_total_all.correct += 1
		
	def add_fc(self,id_sent,connu=False):
		if not self.counts_per_sent_all.has_key(id_sent):
			self.counts_per_sent_connus[id_sent] = Counter()
			self.counts_per_sent_inconnus[id_sent] = Counter()
			self.counts_per_sent_all[id_sent] = Counter()
		if connu:
			self.counts_per_sent_connus[id_sent].fc += 1
			self.counts_total_connus.fc += 1
		else:
			self.counts_per_sent_inconnus[id_sent].fc += 1
			self.counts_total_inconnus.fc += 1
		self.counts_per_sent_all[id_sent].fc += 1
		self.counts_total_all.fc += 1

	def eval(self,test_lexique,gold_lexique):
		if len(test_lexique.table.keys()) == len(gold_lexique.table.keys()):
			for id_sent in range(len(test_lexique.table.keys())):
				for lid in test_lexique.table[id_sent].keys():
					tmot= test_lexique.mot(id_sent,lid)
					ttag= test_lexique.tag(id_sent,lid)
					gmot= gold_lexique.mot(id_sent,lid)
					gtag= gold_lexique.tag(id_sent,lid)
					connu = test_lexique.inconnus[tmot]
					self.add_correct(id_sent,connu)
					self.add_found(id_sent,connu)
					if ttag == gtag:
						self.add_fc(id_sent,connu)
					self.matrice.add_count(gtag,ttag)
		else:
			print "# TEST et GOLD n'ont pas le meme nombre de phrases! (test:"+str(len(test_lexique.table.keys()))+" vs gold:"+str(len(gold_lexique.table.keys()))+")"		

# Evaluation du type PARSEVAL (plus contrainte que parseval)
# Les arbres de <test> et de <gold> sont transformés en triplets.
# Par énoncé,
#	Pour chaque triplet de <test> apparaissant dans <gold>, PERTINENTS_RETOURNES ++
#	Pour chaque triplet de <test>, RETOURNES ++
#	Pour chaque triplet de <gold>, PERTINENTS ++
# A cela, nous ajoutons des informations par rapport aux nombres de COORD, de SREL, de PP et de mots inconnus
class Eval_brackets:
	
	def __init__(self):
		self.counts_per_sent = {}
		self.counts_total=Counter()
		self.counts_nb_PP={}
		self.counts_nb_COORD={}
		self.counts_nb_SREL={}
		self.counts_nb_mots={}
		self.counts_inconnus={}
		self.matrice=Matrice()
	
	#
	# Fonctions de comptage
	#	
	def add_found(self,id_sent):
		if not self.counts_per_sent.has_key(id_sent):
			self.counts_per_sent[id_sent] = Counter()
		self.counts_per_sent[id_sent].found += 1
		self.counts_total.found += 1

	def add_correct(self,id_sent):
		if not self.counts_per_sent.has_key(id_sent):
			self.counts_per_sent[id_sent] = Counter()
		self.counts_per_sent[id_sent].correct += 1
		self.counts_total.correct += 1
		
	def add_fc(self,id_sent):
		if not self.counts_per_sent.has_key(id_sent):
			self.counts_per_sent[id_sent] = Counter()
		self.counts_per_sent[id_sent].fc += 1
		self.counts_total.fc += 1
		
	def eval(self,test_labelledtree_list,gold_labelledtree_list,train_form):
		if len(test_labelledtree_list) == len(gold_labelledtree_list):
			for id_sent in range(len(test_labelledtree_list)):
				self.eval_sent(id_sent,test_labelledtree_list[id_sent],gold_labelledtree_list[id_sent],train_form)	
		else:
			print "# TEST et GOLD n'ont pas le meme nombre de phrases! (test:"+str(len(test_labelledtree_list))+" vs gold:"+str(len(gold_labelledtree_list))+")"
		
	def eval_sent(self,id_sent,test_labelledtree,gold_labelledtree,train_form):
		#print "("+str(id_sent)+")--------------------------------"
		test_triplets = Triplets()
		gold_triplets = Triplets()
		test_labelledtree.width_annotate()
		gold_labelledtree.width_annotate()		
		test_triplets.tree2widthTriplets(test_labelledtree)
		gold_triplets.tree2widthTriplets(gold_labelledtree)
		for g in gold_triplets.liste:
			self.add_correct(id_sent)
			if not test_triplets.has_triplet(g):
				#print "t/g: "+str(g)
				pass
		for t in test_triplets.liste:
			self.add_found(id_sent)
			if gold_triplets.has_triplet(t):
				self.add_fc(id_sent)
				#print " OK: "+str(t)
			else:
				#print "g/t: "+str(t)
				pass
		for g in gold_triplets.liste:
			for f in gold_triplets.liste:
				if f.x == g.x and f.z == g.z:
					self.matrice.add_count(g.y,f.y)
		self.count_PP(id_sent,gold_triplets)
		self.count_COORD(id_sent,gold_triplets)
		self.count_SREL(id_sent,gold_triplets)
		self.count_inconnus(id_sent,gold_labelledtree,train_form)
		self.count_sent_length(id_sent,gold_triplets)
	
	#
	# Compteurs spéciaux pour différents types de phénomènes
	#	
	def count_PP(self,id_sent,gold_triplets):
		cpt=0
		for g in gold_triplets.liste:
			if g.y == 'PP':
				cpt+=1
		if not self.counts_nb_PP.has_key(cpt):
			self.counts_nb_PP[cpt]={}
			self.counts_nb_PP[cpt]['nb']=0
			self.counts_nb_PP[cpt]['scores']=Counter()
		self.counts_nb_PP[cpt]['nb']+=1
		self.counts_nb_PP[cpt]['scores'].fc += self.counts_per_sent[id_sent].fc
		self.counts_nb_PP[cpt]['scores'].found += self.counts_per_sent[id_sent].found
		self.counts_nb_PP[cpt]['scores'].correct += self.counts_per_sent[id_sent].correct

	def count_COORD(self,id_sent,gold_triplets):
		cpt=0
		for g in gold_triplets.liste:
			if g.y == 'COORD':
				cpt+=1
		if not self.counts_nb_COORD.has_key(cpt):
			self.counts_nb_COORD[cpt]={}
			self.counts_nb_COORD[cpt]['nb']=0
			self.counts_nb_COORD[cpt]['scores']=Counter()
		self.counts_nb_COORD[cpt]['nb']+=1	
		self.counts_nb_COORD[cpt]['scores'].fc += self.counts_per_sent[id_sent].fc
		self.counts_nb_COORD[cpt]['scores'].found += self.counts_per_sent[id_sent].found
		self.counts_nb_COORD[cpt]['scores'].correct += self.counts_per_sent[id_sent].correct

	def count_SREL(self,id_sent,gold_triplets):
		cpt=0
		for g in gold_triplets.liste:
			if g.y == 'Srel':
				cpt+=1
		if not self.counts_nb_SREL.has_key(cpt):
			self.counts_nb_SREL[cpt]={}
			self.counts_nb_SREL[cpt]['nb']=0
			self.counts_nb_SREL[cpt]['scores']=Counter()
		self.counts_nb_SREL[cpt]['nb']+=1	
		self.counts_nb_SREL[cpt]['scores'].fc += self.counts_per_sent[id_sent].fc
		self.counts_nb_SREL[cpt]['scores'].found += self.counts_per_sent[id_sent].found
		self.counts_nb_SREL[cpt]['scores'].correct += self.counts_per_sent[id_sent].correct
	
	def count_inconnus(self,id_sent,gold_labelledtree,train_form):
		cpt=0
		words = Formes()
		words.get_formes(gold_labelledtree)
		for w in words.liste:
			if train_form.has_form(w):
				cpt += 1
		if not self.counts_inconnus.has_key(cpt):
			self.counts_inconnus[cpt]={}
			self.counts_inconnus[cpt]['nb']=0
			self.counts_inconnus[cpt]['scores']=Counter()
		self.counts_inconnus[cpt]['nb']+=1
		self.counts_inconnus[cpt]['scores'].fc += self.counts_per_sent[id_sent].fc
		self.counts_inconnus[cpt]['scores'].found += self.counts_per_sent[id_sent].found
		self.counts_inconnus[cpt]['scores'].correct += self.counts_per_sent[id_sent].correct		

			
	def count_sent_length(self,id_sent,gold_triplets):
		length = -1
		for g in gold_triplets.liste:
			if g.y == 'SENT':
				length = int(g.z)+1
				break
		if not self.counts_nb_mots.has_key(length):
			self.counts_nb_mots[length]={}
			self.counts_nb_mots[length]['nb']=0
			self.counts_nb_mots[length]['scores']=Counter()
		self.counts_nb_mots[length]['nb']+=1
		self.counts_nb_mots[length]['scores'].fc += self.counts_per_sent[id_sent].fc
		self.counts_nb_mots[length]['scores'].found += self.counts_per_sent[id_sent].found
		self.counts_nb_mots[length]['scores'].correct += self.counts_per_sent[id_sent].correct		
				
	def __str__(self):
		string = "SENT\tPREC\tREC\tFSC\n"
		string +="================================\n"
		for id_sent in self.counts_per_sent.keys():
			string += str(id_sent)+"\t"
			string += str(self.counts_per_sent[id_sent])+"\n"
		string +="================================\n"
		string += "TOTAL \t"
		string += str(self.counts_total)+"\n\n"		
		return string		

# Evaluation du Labelling fonctionnel
# <test> sont parsés (listes d'objets "LabelledTree")
# Les arbres de <test> et de <gold> sont transformés en triplet.
# Par énoncé,
#	Pour chaque triplet de <test> apparaissant dans <gold>, PERTINENTS_RETOURNES ++
#	Pour chaque triplet de <test>, RETOURNES ++
#	Pour chaque triplet de <gold>, PERTINENTS ++
class Eval_funlabels:
	
	def __init__(self):
		self.counts_per_sent = {}
		self.counts_total=Counter()
		self.matrice = Matrice()
		self.gold_funlabels_per_sent={}
		self.test_funlabels_per_sent={}
		self.same_funlabels_per_sent={}
	
	#
	# Fonctions d'ajouts de comptes
	#
	def add_found(self,id_sent):
		if not self.counts_per_sent.has_key(id_sent):
			self.counts_per_sent[id_sent] = Counter()
		self.counts_per_sent[id_sent].found += 1
		self.counts_total.found += 1

	def add_correct(self,id_sent):
		if not self.counts_per_sent.has_key(id_sent):
			self.counts_per_sent[id_sent] = Counter()
		self.counts_per_sent[id_sent].correct += 1
		self.counts_total.correct += 1
		
	def add_fc(self,id_sent):
		if not self.counts_per_sent.has_key(id_sent):
			self.counts_per_sent[id_sent] = Counter()
		self.counts_per_sent[id_sent].fc += 1
		self.counts_total.fc += 1
		
	def add_nb_gold_funlabels(self,id_sent,triplets):
		if not self.gold_funlabels_per_sent.has_key(id_sent):
			self.gold_funlabels_per_sent[id_sent]=len(triplets.liste)

	def add_nb_test_funlabels(self,id_sent, triplets):
		if not self.test_funlabels_per_sent.has_key(id_sent):
			self.test_funlabels_per_sent[id_sent]=len(triplets.liste)
						
	def add_nb_same_funlabels(self,id_sent):
		if not self.same_funlabels_per_sent.has_key(id_sent):
			self.same_funlabels_per_sent[id_sent]=0
		self.same_funlabels_per_sent[id_sent]+=1

	def init_sent(self,id_sent):
		self.counts_per_sent[id_sent]=Counter()
		self.gold_funlabels_per_sent[id_sent]=0
		self.test_funlabels_per_sent[id_sent]=0
		self.same_funlabels_per_sent[id_sent]=0
		
	def eval(self,test_labelledtree_list,gold_labelledtree_list):
		if len(test_labelledtree_list) == len(gold_labelledtree_list):
			for id_sent in range(len(test_labelledtree_list)):
				self.eval_sent(id_sent,test_labelledtree_list[id_sent],gold_labelledtree_list[id_sent])	
		else:
			print "# TEST et GOLD n'ont pas le meme nombre de phrases! (test:"+str(len(test_labelledtree_list))+" vs gold:"+str(len(gold_labelledtree_list))+")"
		
	def eval_sent(self,id_sent,test_labelledtree,gold_labelledtree):
		#print "("+str(id_sent)+")--------------------------------"
		self.init_sent(id_sent)
		test_triplets = Triplets()
		gold_triplets = Triplets()
		test_labelledtree.width_annotate()
		gold_labelledtree.width_annotate()
		test_triplets.tree2funTriplets_with_cat(test_labelledtree)
		gold_triplets.tree2funTriplets_with_cat(gold_labelledtree)
		self.add_nb_test_funlabels(id_sent,test_triplets)
		self.add_nb_gold_funlabels(id_sent,gold_triplets)
		for g in gold_triplets.liste:
			existe_ge=False
			self.add_correct(id_sent)
			if not test_triplets.has_triplet(g):
				#print "g/t: "+str(g)
				pass
			for t in test_triplets.liste:
				if t.x == g.x and t.z == g.z:
					existe_ge=True
			if not existe_ge:
				self.matrice.add_count(g.y,"___")
		for t in test_triplets.liste:
			existe_te=False
			self.add_found(id_sent)
			if gold_triplets.has_triplet(t):
				self.add_fc(id_sent)
				self.add_nb_same_funlabels(id_sent)
				#print " OK: "+str(t)
			else:
				#print "t/g: "+str(t)
				pass
			for g in gold_triplets.liste:
				if t.x == g.x and t.z == g.z:
					self.matrice.add_count(g.y,t.y)
					existe_te=True
			if not existe_te:
				self.matrice.add_count("___",t.y)
				
	def __str__(self):
		string = "\t| FUNCTIONNAL LABELLING\n"
		string += "SENT\tPREC\tREC\tFSC\n"
		string +="==========================================\n"
		for id_sent in self.counts_per_sent.keys():
			string += str(id_sent)+"\t"
			nbt=self.test_funlabels_per_sent[id_sent]
			nbg=self.gold_funlabels_per_sent[id_sent]
			nbs=0
			if self.same_funlabels_per_sent.has_key(id_sent):
				nbs=self.same_funlabels_per_sent[id_sent]
			string += str(self.counts_per_sent[id_sent])+"\n"
		string +="==========================================\n"
		string += "TOTAL \t"
		string += str(self.counts_total)+"\n"
		return string

# Evaluation des triplets de dépendances
# (que ce soit pivot ou easy)
# <test> et <goldpivot> sont parsés (liste d'objets "Dgraph")
# Par énoncé,
#	Pour chaque triplet de <test> apparaissant dans <gold>, PERTINENTS_RETOURNES ++
#	Pour chaque triplet de <test>, RETOURNES ++
#	Pour chaque triplet de <gold>, PERTINENTS ++
class Eval_deps:

	def __init__(self):
		self.counts_per_sent_labelled = {}
		self.counts_per_sent_unlabelled = {}
		self.counts_total_labelled = Counter()
		self.counts_total_unlabelled = Counter()
		self.matrice = Matrice()
		
	def __str__(self):
		string = "\t|\t HEURISTIQUES/DEPENDANCES\n"
		string += "\t|LABELLED\t\t|\tUNLABELLED\n"
		string += "SENT\tPREC\tREC\tFSC\tPREC\tREC\tFSCORE\n"
		string +="==========================================================\n"
		for id_sent in self.counts_per_sent_labelled.keys():
			string += str(id_sent)+"\t"
			string += str(self.counts_per_sent_labelled[id_sent])
			string += str(self.counts_per_sent_unlabelled[id_sent])
			string += "\n"
		string +="===========================================================\n"
		string += "TOTAL \t"
		string += str(self.counts_total_labelled)
		string += str(self.counts_total_unlabelled)
		string += "\n"		
		return string

	#
	# Fonction de comptage
	#
	def add_found_labelled(self,id_sent):
		if not self.counts_per_sent_labelled.has_key(id_sent):
			self.counts_per_sent_labelled[id_sent] = Counter()
		self.counts_per_sent_labelled[id_sent].found += 1
		self.counts_total_labelled.found += 1

	def add_correct_labelled(self,id_sent):
		if not self.counts_per_sent_labelled.has_key(id_sent):
			self.counts_per_sent_labelled[id_sent] = Counter()
		self.counts_per_sent_labelled[id_sent].correct += 1
		self.counts_total_labelled.correct += 1
		
	def add_fc_labelled(self,id_sent):
		if not self.counts_per_sent_labelled.has_key(id_sent):
			self.counts_per_sent_labelled[id_sent] = Counter()
		self.counts_per_sent_labelled[id_sent].fc += 1
		self.counts_total_labelled.fc += 1

	def add_found_unlabelled(self,id_sent):
		if not self.counts_per_sent_unlabelled.has_key(id_sent):
			self.counts_per_sent_unlabelled[id_sent] = Counter()
		self.counts_per_sent_unlabelled[id_sent].found += 1
		self.counts_total_unlabelled.found += 1

	def add_correct_unlabelled(self,id_sent):
		if not self.counts_per_sent_unlabelled.has_key(id_sent):
			self.counts_per_sent_unlabelled[id_sent] = Counter()
		self.counts_per_sent_unlabelled[id_sent].correct += 1
		self.counts_total_unlabelled.correct += 1
		
	def add_fc_unlabelled(self,id_sent):
		if not self.counts_per_sent_unlabelled.has_key(id_sent):
			self.counts_per_sent_unlabelled[id_sent] = Counter()
		self.counts_per_sent_unlabelled[id_sent].fc += 1
		self.counts_total_unlabelled.fc += 1

	def eval(self, test_deps_list, gold_deps_list):
		if len(test_deps_list) == len(gold_deps_list):
			for i in range(len(gold_deps_list)):
				for g in range(len(gold_deps_list[i].edges)):
					self.add_correct_labelled(i)
					self.add_correct_unlabelled(i)
				for t in range(len(test_deps_list[i].edges)):
					self.add_found_labelled(i)
					self.add_found_unlabelled(i)
				for te in test_deps_list[i].edges:
					exist_te=False
					for ge in gold_deps_list[i].edges:
						if te.orig.idx == ge.orig.idx and te.label.lower() == ge.label.lower() and te.dest.idx == ge.dest.idx:
							self.add_fc_labelled(i)
						if te.orig.idx == ge.orig.idx and te.dest.idx == ge.dest.idx:
							self.add_fc_unlabelled(i)
							self.matrice.add_count(ge.label.lower(),te.label.lower())
							exist_te=True
					if not exist_te:
							self.matrice.add_count("___",te.label.lower())
				for ge in gold_deps_list[i].edges:
					exist_ge=False
					for te in test_deps_list[i].edges:
						if te.orig.idx == ge.orig.idx and te.dest.idx == ge.dest.idx:
							exist_ge=True
					if not exist_ge:
						self.matrice.add_count(ge.label.lower(),"___")
		else:
			print "# TEST et GOLD n'ont pas le meme nombre de phrases! (test:"+str(len(test_deps_list))+" vs gold:"+str(len(gold_deps_list))+")"

# Comparaison des différents étapes précédant le labelling fonctionnel
class Comp_funlabelling:

	def __init__(self,eval_parsing,eval_funlabels1,eval_funlabels2):
		self.eval_parsing = eval_parsing
		self.eval_funlabels1 = eval_funlabels1
		self.eval_funlabels2 = eval_funlabels2
		
	def __str__(self):
		string = "\t|\t\t\t\tTAGS\t\t\t\t\t|\tPARSEVAL\t"+"| du RAW au FUN\t\t"+"| du GOLDFTB au FUN\n"
		string += "\t|\tCONNUS\t\t|\tINCONNUS\t|\tTOTAL\t\t|"+"\t\t\t| FUN LABELLING\t"+"\t| FUN LABELLING\n"
		string += "SENT\t| PREC\tREC\tFSC\t| PREC\tREC\tFS\t| PREC\tREC\tFSC\t| PREC\tREC\tFSC"+"\t| PREC\tREC\tFSC"+"\t| PREC\tREC\tFSC\n"
		string +="====================================================================================================================================================\n"
		for id_sent in self.eval_parsing.eval_tagging.counts_per_sent_all.keys():
			string += str(id_sent)+"\t"
			string += "| "+str(self.eval_parsing.eval_tagging.counts_per_sent_connus[id_sent])
			string += "| "+str(self.eval_parsing.eval_tagging.counts_per_sent_inconnus[id_sent])
			string += "| "+str(self.eval_parsing.eval_tagging.counts_per_sent_all[id_sent])
			string += "| "+str(self.eval_parsing.eval_brackets.counts_per_sent[id_sent])
			string += "| "+str(self.eval_funlabels1.counts_per_sent[id_sent])
			string += "| "+str(self.eval_funlabels2.counts_per_sent[id_sent])
			string += "\n"
		string +="====================================================================================================================================================\n"
		string += "TOTAL \t"
		string += "| "+str(self.eval_parsing.eval_tagging.counts_total_connus)
		string += "| "+str(self.eval_parsing.eval_tagging.counts_total_inconnus)
		string += "| "+str(self.eval_parsing.eval_tagging.counts_total_all)
		string += "| "+str(self.eval_parsing.eval_brackets.counts_total)
		string += "| "+str(self.eval_funlabels1.counts_total)
		string += "| "+str(self.eval_funlabels2.counts_total)
		string += "\n"
		string += "SENT\t| PREC\tREC\tFSC\t| PREC\tREC\tFS\t| PREC\tREC\tFSC\t| PREC\tREC\tFSC"+"\t| PREC\tREC\tFSC"+"\t| PREC\tREC\tFSC\n"
		string += "\n"
		
		return string

# Comparaison des différents étapes précédant le la construction des dépendances
# (à partir du labelling fonctionnel)
class Comp_dependances1:
	
	def __init__(self,eval_funlabels,eval_deps1,eval_deps2):
		self.eval_funlabels = eval_funlabels
		self.eval_deps1 = eval_deps1
		self.eval_deps2 = eval_deps2
		
	def __str__(self):
		string = "\t| FUNCTIONNAL LABELLING\t"+"| \t\t\tdu PTB au PIV\t\t"+"| \t\tdu FUN au PIV\n"
		string += "\t\t\t\t| LABELLED\t\t| UNLABELLED\t\t| LABELLED\t\t| UNLABELLED\n"
		string += "SENT\t| PREC\tREC\tFSC\t"+"| PREC\tREC\tFSC\t"+"| PREC\tREC\tFSC\t"+"| PREC\tREC\tFSC\t"+"| PREC\tREC\tFSC\n"
		string +="===========================================================================================================================\n"
		for id_sent in self.eval_funlabels.counts_per_sent.keys():
			string += str(id_sent)+"\t"
			string += "| "+str(self.eval_funlabels.counts_per_sent[id_sent])
			string += "| "+str(self.eval_deps1.counts_per_sent_labelled[id_sent])
			string += "| "+str(self.eval_deps1.counts_per_sent_unlabelled[id_sent])
			string += "| "+str(self.eval_deps2.counts_per_sent_labelled[id_sent])
			string += "| "+str(self.eval_deps2.counts_per_sent_unlabelled[id_sent])
			string += "\n"
		string +="===========================================================================================================================\n"
		string += "TOTAL \t"
		string += "| "+str(self.eval_funlabels.counts_total)
		string += "| "+str(self.eval_deps1.counts_total_labelled)
		string += "| "+str(self.eval_deps1.counts_total_unlabelled)
		string += "| "+str(self.eval_deps2.counts_total_labelled)
		string += "| "+str(self.eval_deps2.counts_total_unlabelled)
		string += "\n"		
		string += "SENT\t| PREC\tREC\tFSC\t"+"| PREC\tREC\tFSC\t"+"| PREC\tREC\tFSC\t"+"| PREC\tREC\tFSC\t"+"| PREC\tREC\tFSC\n"
		return string		

# Comparaison des différents étapes précédant le la construction des dépendances
# (à partir du fichier raw)
class Comp_dependances2:
	
	def __init__(self,eval_parsing,eval_funlabels1,eval_funlabels2,eval_deps1,eval_deps2,eval_deps3):
		self.eval_parsing = eval_parsing
		self.eval_funlabels1 = eval_funlabels1
		self.eval_funlabels2 = eval_funlabels2
		self.eval_deps1 = eval_deps1
		self.eval_deps2 = eval_deps2
		self.eval_deps3 = eval_deps3
		
	def __str__(self):
		string = "\t|\t\t\t\tTAGS\t\t\t\t\t|\tPARSEVAL\t"+"| \tdu RAW au FUN\t"+"| du GOLDFTB au FUN\t"+"| \t\tdu RAW au PIVOT\t\t\t"+"| \t\tdu GOLDPTB au PIVOT\t\t"+"| \t\tdu GOLDFUN au PIVOT"+"\n"
		string += "\t|\tCONNUS\t\t|\tINCONNUS\t|\tTOTAL\t\t|"+"\t\t\t|\t\t"+"\t|\t\t"+"\t| LABELLED\t"+"\t| UNLABELLED\t"+"\t| LABELLED\t"+"\t| UNLABELLED\t"+"\t| LABELLED\t"+"\t| UNLABELLED\t"+"\n"
		string += "SENT\t| PREC\tREC\tFSC\t| PREC\tREC\tFS\t| PREC\tREC\tFSC\t| PREC\tREC\tFSC"+"\t| PREC\tREC\tFSC\t| PREC\tREC\tFS\t| PREC\tREC\tFSC\t| PREC\tREC\tFSC\t| PREC\tREC\tFSC\t| PREC\tREC\tFSC"+"\t| PREC\tREC\tFSC"+"\t| PREC\tREC\tFSC\n"
		string +="====================================================================================================================================================\n"
		for id_sent in self.eval_parsing.eval_tagging.counts_per_sent_all.keys():
			string += str(id_sent)+"\t"
			string += "| "+str(self.eval_parsing.eval_tagging.counts_per_sent_connus[id_sent])
			string += "| "+str(self.eval_parsing.eval_tagging.counts_per_sent_inconnus[id_sent])
			string += "| "+str(self.eval_parsing.eval_tagging.counts_per_sent_all[id_sent])
			string += "| "+str(self.eval_parsing.eval_brackets.counts_per_sent[id_sent])
			string += "| "+str(self.eval_funlabels1.counts_per_sent[id_sent])
			string += "| "+str(self.eval_funlabels2.counts_per_sent[id_sent])
			string += "| "+str(self.eval_deps1.counts_per_sent_labelled[id_sent])
			string += "| "+str(self.eval_deps1.counts_per_sent_unlabelled[id_sent])
			string += "| "+str(self.eval_deps2.counts_per_sent_labelled[id_sent])
			string += "| "+str(self.eval_deps2.counts_per_sent_unlabelled[id_sent])
			string += "| "+str(self.eval_deps3.counts_per_sent_labelled[id_sent])
			string += "| "+str(self.eval_deps3.counts_per_sent_unlabelled[id_sent])
			string += "\n"
		string +="====================================================================================================================================================\n"
		string += "TOTAL \t"
		string += "| "+str(self.eval_parsing.eval_tagging.counts_total_connus)
		string += "| "+str(self.eval_parsing.eval_tagging.counts_total_inconnus)
		string += "| "+str(self.eval_parsing.eval_tagging.counts_total_all)
		string += "| "+str(self.eval_parsing.eval_brackets.counts_total)
		string += "| "+str(self.eval_funlabels1.counts_total)
		string += "| "+str(self.eval_funlabels2.counts_total)
		string += "| "+str(self.eval_deps1.counts_total_labelled)
		string += "| "+str(self.eval_deps1.counts_total_unlabelled)
		string += "| "+str(self.eval_deps2.counts_total_labelled)
		string += "| "+str(self.eval_deps2.counts_total_unlabelled)
		string += "| "+str(self.eval_deps3.counts_total_labelled)
		string += "| "+str(self.eval_deps3.counts_total_unlabelled)	
		string += "\n"
		string += "\n"
		return string		

class Methodes:
##########################
# FONCTIONS D'EVALUATION #
##########################

# Les fonctions ci-dessous correspondent aux classes implémentées plus haut.
# Les arguments utilisés sont les fichiers utiles à l'évaluation

# Par exemple, pour la premiere fonction, nous obtenos comme evaluation:
# DU RAW AU FUN:
# eb: raw2ptb vs gold_ftb
# ef1: raw2ptb->fun vs gold_fun
# ef2: gold_ftb->fun vs gold_fun

	# Du parsing au funlabelling
	# Entre le <raw> et le <fun>
	def comp_funlabelling(self,raw2ptb_file,goldftb_file,train,raw2ptb2fun_file,goldptb2fun_file,goldfun_file,matrice):
		print "# Lecture du fichier ["+ raw2ptb_file +"]"
		test_labelledtree_list = file2labelledtree_list(raw2ptb_file)
		print "# Lecture du fichier ["+ goldftb_file +"]"
		gold_labelledtree_list = file2labelledtree_list(goldftb_file)
		print "# Lecture du fichier ["+ train +"]"
		train_labelledtree_list = file2labelledtree_list(train)
		print "# Construction des lexiques."
		test_lex = Lexique()
		test_lex.fill_tags_list(test_labelledtree_list) 
		gold_lex = Lexique()
		gold_lex.fill_tags_list(gold_labelledtree_list) 	
		train_form = Formes()
		train_form.get_formes_list(train_labelledtree_list)	
		test_lex.set_inconnus(train_form)
		print "# Evaluation du parsing:"
		print " -tagging."
		et = Eval_tagging()
		et.eval(test_lex,gold_lex)
		print " -brackets"	
		eb = Eval_brackets()
		eb.eval(test_labelledtree_list,gold_labelledtree_list,train_form)
		ep = Eval_parsing(et,eb)
		
		print "# Lecture du fichier ["+ raw2ptb2fun_file +"]"
		raw2ptb2fun_file_labelledtree_list = file2labelledtree_list(raw2ptb2fun_file)
		print "# Lecture du fichier ["+ goldptb2fun_file +"]"
		goldptb2fun_labelledtree_list = file2labelledtree_list(goldptb2fun_file)
		print "# Lecture du fichier ["+ goldfun_file +"]"
		goldfun_labelledtree_list = file2labelledtree_list(goldfun_file)
		print "# Evaluation du funlabelling."
		ef1 = Eval_funlabels()
		ef1.eval(raw2ptb2fun_file_labelledtree_list,goldfun_labelledtree_list)
		print "# Evaluation du funlabelling."
		ef2 = Eval_funlabels()
		ef2.eval(goldptb2fun_labelledtree_list,goldfun_labelledtree_list)			
		
		cf = Comp_funlabelling(ep,ef1,ef2)
		print cf
		if matrice:
			et.matrice.write_matrice("tagging_"+matrice)
			eb.matrice.write_matrice("brackets_"+matrice)
			ef1.matrice.write_matrice("raw2ptb2fun_"+matrice)
			ef2.matrice.write_matrice("goldptfb2fun_"+matrice)
		pass
	
	# Du funlabelling au pivot
	# Entre le <mrg> et le <pivot>
	def comp_dependances1(self,goldptb2fun_file,goldfun_file,goldptb2fun2pivot,goldfun2pivot,goldpivot,matrice,fine=False,easy=False):
		print "# Lecture du fichier ["+ goldptb2fun_file +"]"	
		goldptb2fun_labelledtree_list = file2labelledtree_list(goldptb2fun_file)
		print "# Lecture du fichier ["+ goldfun_file +"]"
		goldfun_labelledtree_list = file2labelledtree_list(goldfun_file)
		print "# Evaluation du funlabelling."
		ef = Eval_funlabels()	
		ef.eval(goldptb2fun_labelledtree_list, goldfun_labelledtree_list)
		
		print "# Lecture du dossier ["+ goldptb2fun2pivot +"]"
		goldptb2fun2pivot_deps_list = dirpivot2dgraph_list(goldptb2fun2pivot,fine=fine,easy=easy)
		print "# Lecture du dossier ["+ goldfun2pivot +"]"
		goldfun2pivot_deps_list = dirpivot2dgraph_list(goldfun2pivot,fine=fine,easy=easy)
		print "# Lecture du dossier ["+ goldpivot +"]"
		goldpivot_deps_list = dirpivot2dgraph_list(goldpivot,fine=fine,easy=easy)
		
		print "# Evaluation des dépendances pivot."
		ed1 = Eval_deps()
		ed1.eval(goldptb2fun2pivot_deps_list, goldpivot_deps_list)
		print "# Evaluation des dépendances pivot."	
		ed2 = Eval_deps()
		ed2.eval(goldfun2pivot_deps_list, goldpivot_deps_list)
		
		cd = Comp_dependances1(ef,ed1,ed2)
		print cd
		
		if matrice:
			ef.matrice.write_matrice("goldptfb2fun_"+matrice)
			ed1.matrice.write_matrice("goldptb2fun2pivot_"+matrice)
			ed2.matrice.write_matrice("goldfun2pivot_"+matrice)
		pass
	
	# Du parsing au pivot
	# Entre le <raw> et le <pivot>
	def comp_dependances2(self,raw2ptb_file,goldptb_file,train,raw2ptb2fun_file,goldptb2fun_file,goldfun_file,raw2ptb2fun2pivot,goldptb2fun2pivot,goldfun2pivot,goldpivot,matrice,fine=False,easy=False):
		print "# Lecture du fichier ["+ raw2ptb_file +"]"
		test_labelledtree_list = file2labelledtree_list(raw2ptb_file)
		print "# Lecture du fichier ["+ goldptb_file +"]"
		gold_labelledtree_list = file2labelledtree_list(goldptb_file)
		print "# Lecture du fichier ["+ train +"]"
		train_labelledtree_list = file2labelledtree_list(train)
		print "# Construction des lexiques."
		test_lex = Lexique()
		test_lex.fill_tags_list(test_labelledtree_list) 
		gold_lex = Lexique()
		gold_lex.fill_tags_list(gold_labelledtree_list) 	
		train_form = Formes()
		train_form.get_formes_list(train_labelledtree_list)	
		test_lex.set_inconnus(train_form)
		print "# Evaluation du parsing:"
		print " -tagging."
		et = Eval_tagging()
		et.eval(test_lex,gold_lex)
		print " -brackets."
		eb = Eval_brackets()
		eb.eval(test_labelledtree_list,gold_labelledtree_list,train_form)
		ep = Eval_parsing(et,eb)
		
		print "# Lecture du fichier ["+ raw2ptb2fun_file +"]"
		raw2ptb2fun_labelledtree_list = file2labelledtree_list(raw2ptb2fun_file)
		print "# Lecture du fichier ["+ goldptb2fun_file +"]"
		goldptb2fun_labelledtree_list = file2labelledtree_list(goldptb2fun_file)
		print "# Lecture du dossier ["+ goldfun_file +"]"
		goldfun_labelledtree_list = file2labelledtree_list(goldfun_file)
	
		print "# Evaluation du funlabelling."
		ef1 = Eval_funlabels()
		ef1.eval(raw2ptb2fun_labelledtree_list, goldfun_labelledtree_list)
		print "# Evaluation du funlabelling."
		ef2 = Eval_funlabels()
		ef2.eval(goldptb2fun_labelledtree_list, goldfun_labelledtree_list)
		
		print "# Lecture du dossier ["+ raw2ptb2fun2pivot +"]"
		raw2ptb2fun2pivot_deps_list = dirpivot2dgraph_list(raw2ptb2fun2pivot,fine=fine,easy=easy)
		print "# Lecture du dossier ["+ goldptb2fun2pivot +"]"
		goldptb2fun2pivot_deps_list = dirpivot2dgraph_list(goldptb2fun2pivot,fine=fine,easy=easy)
		print "# Lecture du dossier ["+ goldfun2pivot +"]"
		goldfun2pivot_deps_list = dirpivot2dgraph_list(goldfun2pivot,fine=fine,easy=easy)
		print "# Lecture du dossier ["+ goldpivot +"]"
		goldpivot_deps_list = dirpivot2dgraph_list(goldpivot,fine=fine,easy=easy)
	
		print "# Evaluation des dépendances pivot."
		ed1 = Eval_deps()
		ed1.eval(raw2ptb2fun2pivot_deps_list, goldpivot_deps_list)
		print "# Evaluation des dépendances pivot."
		ed2 = Eval_deps()
		ed2.eval(goldptb2fun2pivot_deps_list, goldpivot_deps_list)
		print "# Evaluation des dépendances pivot."
		ed3 = Eval_deps()
		ed3.eval(goldfun2pivot_deps_list, goldpivot_deps_list)	
		cd = Comp_dependances2(ep,ef1,ef2,ed1,ed2,ed3)
		print cd
		
		if matrice:
			et.matrice.write_matrice("tagging_"+matrice)
			eb.matrice.write_matrice("brackets_"+matrice)
			ef1.matrice.write_matrice("raw2ptb2fun_"+matrice)
			ef2.matrice.write_matrice("goldptfb2fun_"+matrice)
			ed1.matrice.write_matrice("raw2ptb2fun2pivot_"+matrice)
			ed2.matrice.write_matrice("goldptb2fun2pivot_"+matrice)
			ed3.matrice.write_matrice("goldfun2pivot_"+matrice)
		pass
	
	# Le parsing
	# Entre le <raw> et le <mrg>	
	def eval_parsing(self,raw2ptb_file,gold_ftb,train,matrice):
		print "# Lecture du fichier ["+ raw2ptb_file +"]"
		test_labelledtree_list = file2labelledtree_list(raw2ptb_file,noponct=True)
		print "# Lecture du fichier ["+ gold_ftb +"]"
		gold_labelledtree_list = file2labelledtree_list(gold_ftb,noponct=True)
		print "# Lecture du fichier ["+ train +"]"
		train_labelledtree_list = file2labelledtree_list(train)
		print "# Construction des lexiques."
		test_lex = Lexique()
		test_lex.fill_tags_list(test_labelledtree_list)
		gold_lex = Lexique()
		gold_lex.fill_tags_list(gold_labelledtree_list) 
		train_form = Formes()
		train_form.get_formes_list(train_labelledtree_list)	
		test_lex.set_inconnus(train_form)
		print "# Evaluation du parsing:"
		print " -tagging."
		et = Eval_tagging()
		et.eval(test_lex,gold_lex)
		print " -brackets"
		eb = Eval_brackets()
		eb.eval(test_labelledtree_list,gold_labelledtree_list,train_form)
		ep = Eval_parsing(et,eb)
		print ep
		
		if matrice:
			et.matrice.write_matrice("tagging_"+matrice)
			eb.matrice.write_matrice("brackets_"+matrice)
		pass
	
	# L'ajout de labels fonctionnels
	# Entre le <mrg> et le <fun>		
	def eval_funlabelling(self,ftb2fun_file,goldfun_file,matrice):
		print "# Lecture du fichier ["+ftb2fun_file+"]"
		test_labelledtree_list = file2labelledtree_list(ftb2fun_file)
		print "# Lecture du fichier ["+goldfun_file+"]"
		gold_labelledtree_list = file2labelledtree_list(goldfun_file)
		ef = Eval_funlabels()
		ef.eval(test_labelledtree_list,gold_labelledtree_list)
		print ef
		
		if matrice:
			ef.matrice.write_matrice("goldptb2fun_"+matrice)
		pass
	
	# L'application des heuristiques et la conversion en dépendances pivot
	# Entre le <fun> et le <pivot>
	def eval_dependances(self,fun2pivot_dir,gold_pivot,matrice,fine=False,easy=False):
		print "# Lecture du dossier ["+fun2pivot_dir+"]"
		fun2pivot_deps_list = dirpivot2dgraph_list(fun2pivot_dir,fine=fine,easy=easy)
		print "# Lecture du dossier ["+gold_pivot+"]"
		gold_deps_list = dirpivot2dgraph_list(gold_pivot,fine=fine,easy=easy)
		print "# Evaluation des dependances pivot."
		ed = Eval_deps()
		ed.eval(fun2pivot_deps_list, gold_deps_list)
		print ed
		
		if matrice:
			ed.matrice.write_matrice("goldfun2pivot_"+matrice)
		pass

	# La conversion des dépendances PIVOT aux dépendances EASY
	# Entre le <pivot> et <easy>
	def eval_easy(self,pivot2easy_file,gold_easy,matrice,fichier_add=None):
		print "# Lecture du fichier ["+pivot2easy_file+"]"
		test_easy_deps_list = easy_file2dgraph_list(pivot2easy_file,add=fichier_add)
		print "# Lecture du fichier ["+gold_easy+"]"
		gold_easy_deps_list = easy_file2dgraph_list(gold_easy,add=fichier_add)
		print "# Evaluation des relations easy"
		ed = Eval_deps()
		ed.eval(test_easy_deps_list,gold_easy_deps_list)
		print ed
		
		if matrice:
			ed.matrice.write_matrice("pivot2easy_"+matrice)
		pass