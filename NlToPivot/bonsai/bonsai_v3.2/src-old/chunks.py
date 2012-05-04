#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#

# Représentation en chunks d'un énoncé.

# 1 liste de tokens:
# chaque token comprend :
# - une forme
# - le chunk dans lequel il apparait (GN,GP,...)
# - l'indice du chunk dans la phrase
# - si le token est la tete du chunk (0/1)

# 1 liste de groupes:
# chaque groupe comprend:
# - le type du chunk (GN,GP,...)
# - la liste des indices des tokens dans la phrase, qui constituent ce groupe
# - l'indice de la tete du token de ce groupe


import re
import os
import sys

# Liste des clitiques post-verbaux que l'on peut renconctrer en francais
clitiques_post_verbaux=["je","tu","il","elle","on","nous","vous","ils","elles","moi","toi","lui","leur","le","la","les","y","en"]
clitiques_post_verbaux.extend(["-je","-tu","-il","-elle","-on","-nous","-vous","-ils","-elles","-moi","-toi","-lui","-leur","-le","-la","-les","-y","-en"])
clitiques_post_verbaux.extend(["-t-il","-t-elle","-t-on","-t-ils","-t-elles"])

# Creation de chunks à la easy
class Chunks:
	
	# L'objet 'Chunks':
	# id: l'identifiant de la phrase
	# tokens: liste des tokens
	# groupes: listes des groupes (chunks )
	def __init__(self,id_sent):
		self.id_sent=id_sent
		self.tokens={}
		self.groupes={}
	
	def __str__(self):
		string=""
		for t in self.tokens.keys():
			string+="TOKEN: "+str(t)+" -> "+str(self.tokens[t])+"\n"
		for g in self.groupes.keys():
			string+="GROUPE: "+str(g)+" -> "+str(self.groupes[g])+"\n"
		return string
	
	
	# Ajout du token en parametre:
	# à la liste des tokens
	# au groupe correspondant, en ajoutant l'information de tete
	def add_token(self,index,token):
		self.tokens[index]=token
		if not self.groupes.has_key(token.groupe):
			self.groupes[token.groupe]=Groupe(token.chunk)
		if token.tete == 1:
			self.groupes[token.groupe].add_token(index,index)
		else:
			tete = self.groupes[token.groupe].head
			self.groupes[token.groupe].add_token(index,tete)
	
	def get_const(self,id_group):
		const=""
		tokens = self.groupes[id_group].liste
		for t in tokens:
			if t == self.groupes[id_group].head:
				const+= "#"+self.tokens[t].forme+"# "
			else:
				const+= self.tokens[t].forme+" "
		return const
	
	# Renvoi de la phrase
	def get_sentence(self):
		sentence=""
		for t in self.tokens.keys():
			sentence+= self.tokens[t].forme+" "
		return sentence
		
	# Désigne la tete dans chaque groupe et parmi les tokens,
	# si les tetes n'ont pas été données.
	def set_heads(self):
		for g in self.groupes.keys():
			if len(self.groupes[g].liste) == 1:
				tete = self.groupes[g].liste[0]
				self.groupes[g].head = tete
				self.tokens[tete].tete=1
			else:
				max = -1
				for l in self.groupes[g].liste:
					if l > max:
						# si le groupe est NV, et quilya des clitiques post-verbaux,
						# ils ne comptent pas comme derniere forme
						# (et donc comme tete )
						if self.groupes[g].chunk != "NV" or (self.groupes[g].chunk == "NV" and not self.tokens[l].forme in clitiques_post_verbaux):
							max=l
				self.groupes[g].head = max
				self.tokens[max].tete=1
	
	# Renvoi du chunk par son indice			
	def get_chunk(self,index):
		if self.tokens.has_key(index):
			return self.tokens[index].chunk+str(self.tokens[index].groupe)
		else:
			return None
	
	# Renvoi de la tete du groupe donné en indice		
	def get_head(self,index):
		if self.groupes.has_key(index):
			return self.groupes[index].head
		else:
			return None
		
class Token:
	
	# L'objet 'Tokens':
	# une forme, un chunk, un identifiant de groupe, et un "booleen" de tete
	def __init__(self,forme,chunk,groupe,tete):
		self.forme = forme
		self.chunk = chunk
		self.groupe = groupe
		self.tete = tete
		
	def __str__(self):
		return "("+self.forme+": "+self.chunk+", "+str(self.groupe)+", head:"+str(self.tete)+")"
		
class Groupe:

	# L'objet 'Groupe':
	# un chunk, une liste d'identifiants de tokens et l'indice de la tete parmi cette liste
	def __init__(self,chunk):
		self.chunk = chunk
		self.liste=[]
		self.head=-1
		
	def __str__(self):
		string=""
		string += "("+self.chunk+": ["
		for i in self.liste:
			string += str(i)+", "
		string += "], head:"+str(self.head)+")"
		return string
	
	# Ajout du token et de l'information de tete donnés en parametre	
	def add_token(self,id_token,head):
		self.liste.append(id_token)
		if head > -1:
			self.head = head
