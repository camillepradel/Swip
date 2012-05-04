#!/usr/bin/env python -O
# -*- coding: iso-8859-1 -*-
#
# EasyReader
#
# Author : Francois Guerin
# Date : Sept 2008
# Objectif:
# Lecture d'un fichier XML au format Easy
# - renvoi des informations de chunking et des relations
# - renvoi du rawtext

import sys
import re
import string
import os
from xml.dom import minidom
from xml.dom.minidom import parse, parseString
from chunks import *
from dgraph import *

class EasyReader:

	# Lecture du rawtext des fichiers xml dans un dossier
	# Renvoi d'une liste associant un nom de fichier à l'ensemble du rawtext de ce fichier
    def get_sentences_dir(self,dirname):
		files = os.listdir(dirname)
		sentences_per_file={}
		for file in files:
			if re.match("^.*\.xml$",file):
				sys.stderr.write("Parsing input file "+file+'\n')
				instream = open(os.path.join(dirname,file))
				try:
					sentences_per_file[file]=self.get_sentences(instream)
				except Exception:
					print "# Probleme dans le parsing du fichier: "+file
				instream.close()
		return sentences_per_file


	# Lecture du rawtext dans flux xml
	# Renvoi de l'ensemble du rawtext de ce flux
    def get_sentences(self,instream):
		sentences=""
		xmldoc = minidom.parse(instream)
		sentlist = xmldoc.getElementsByTagName('E')
		for sent in sentlist:
			sentences += self.get_sentences_aux(sent)+"\n"
		return sentences.rstrip()
    
    # Si le noeud xml en paramatre est du text "type=3",
    # renvoi de ce texte (avec conversion de caractères), 
    # sinon application de la méthode sur chaque sous-noeud
    
    def get_sentences_aux(self,node):
		if node.nodeType == 3:
			if not re.match("^(\n|\t| |\r)+$",node.data):
				mot = node.data
				mot = mot.lstrip()
				mot = mot.rstrip()
				mot = re.sub(" ","_",mot)
				mot = re.sub("\(","--LBR--",mot)
				mot = re.sub("\)","--RBR--",mot)
				if re.match("^ *_ *$",mot):
					mot = "-"
				return mot+" "
			else:
				return ""
		elif node.nodeType == 1:
			string=""
			for n in node.childNodes:
				string += self.get_sentences_aux(n)
			return string
   
   # Affichage du flux xml     
    def print_xml(self,instream):
		xmldoc = minidom.parse(instream)
		print xmldoc.toxml()

	# Lecture des fichiers xml dans un dossier
	# Renvoi de la liste de couples 'chunks,dépendances' par fichier
    def read_dir_xml(self,dirname):
		files = os.listdir(dirname)
		chunksdeps_per_file={}
		for file in files:
			if re.match("^.*\.xml$",file):
				sys.stderr.write("Parsing input file "+file+'\n')
				instream = open(os.path.join(dirname,file))
				chunksdeps_per_file[file]=self.read_xml(instream)
				instream.close()
		return chunksdeps_per_file		

	# Lecture du flux xml donné en parametre
	# Renvoi de la liste de couples 'chunks,dépendances'
    def read_xml(self,instream):
        xmldoc = minidom.parse(instream)
        sentlist = xmldoc.getElementsByTagName('E')
        chunksdeps={}
        for sent in sentlist:
        	id_sent = sent.attributes['id'].value
        	#print ">"+str(id_sent)
        	id_sent = int(re.match("^E(\d+)$",id_sent).group(1))
        	cs = Chunks(id_sent)
        	dg = DependencyGraph()
        	for node in sent.childNodes:
        		if node.nodeName == "Groupe":
        			cs = self.read_xml_Groupe(node,cs)
        		elif node.nodeName == "F":
         			cs = self.read_xml_F(node,cs)    	
        		else:
        			pass
			cs.set_heads()
        	dg = self.read_xml_relations(sent,dg,cs)
        	chunksdeps[id_sent]={}
        	chunksdeps[id_sent]['chunks']=cs
        	chunksdeps[id_sent]['deps']=dg
       	return chunksdeps
       	
    def get_triples_xml(self,file):
    	instream = open(file,"r")
    	chunksdeps = self.read_xml(instream)
    	instream.close()
    	depbank = {}
    	cpt = 0
    	chunksdeps_sorted = chunksdeps.keys()
    	chunksdeps_sorted.sort()
    	for i in chunksdeps_sorted:
    		depbank[cpt] = DepSentence(i,file,"EASY","_",chunksdeps[i]['deps'])
    		#Ajout de l'information du type de chunk pour chaque sommet apparaissant en relation
    		for edge in depbank[cpt].depforest.edges:
    			edge.orig.chunk=chunksdeps[i]['chunks'].tokens[edge.orig.idx].chunk
    			edge.dest.chunk=chunksdeps[i]['chunks'].tokens[edge.dest.idx].chunk
    		cpt += 1
    	return depbank

    def write_easy2pivot(self,fichier,chunksdeps):
		out = open(re.search("^(.+)\.xml",fichier).group(1)+".piv","w")
		liste=chunksdeps.keys()
		liste.sort()
		for i in liste:
			ds=DepSentence(i,re.search("^.*/([^/]+.xml)$",fichier).group(1),"EASY","_",chunksdeps[i]['deps'])
			sentence2pivot='sentence(\n'
			sentence2pivot += ds.id2str()
			sentence2pivot += ds.date2str()
			sentence2pivot += ds.validator2str()
			sentence2pivot += 'sentence_form('+chunksdeps[i]['chunks'].get_sentence()+')\n'
			sentence2pivot += ds.surfdeps2str()
			sentence2pivot += ds.feats2str(False)
			sentence2pivot += ')'
			out.write(sentence2pivot)
			out.write("\n\n")
		out.close()

	# Le noeud étant de tag <Group>,
	# créer le chunk correspondant,
	# lire chacune des formes,
	# créer les tokens correspondants,
	# et les ajouter au chunk précédemment créé
    def read_xml_Groupe(self,group,chunks):
		chunk = group.attributes['type'].value
		groupe = group.attributes['id'].value
		g=re.search("^E\d+G(\d+)$",groupe)
		groupe=g.group(1)			
		formlist = group. getElementsByTagName('F')
		for f in formlist:
			id = f.attributes['id'].value
			i=re.search("^E\d+F(\d+)$",id)
			id=i.group(1)
			form = f.firstChild.data
			try:
				form = str(form)
			except UnicodeEncodeError:
				form = form.encode('latin1')
			t = Token(form,chunk,int(groupe),0)
			chunks.add_token(int(id),t)
		return chunks
		pass

	# Le noeud étant de tag <F>,
	# créer le token correspondant
	# et l'ajouter à la liste de chunks
    def read_xml_F(self,f,chunks):
		id = f.attributes['id'].value
		i=re.search("^E\d+F(\d+)$",id)
		id=i.group(1)
		form = f.firstChild.data
		try:
			form = str(form)
		except UnicodeEncodeError:
			form = form.encode('latin1')
		t = Token(form,"",None,1)
		chunks.add_token(int(id),t)
		return chunks
		pass
	
	# Le noeud étant de tag <relations>,
	# lire le type de la relation (COORD,ATB,MOD,etc. )
	# et appliquer la fonction associée.
	# La relation ainsi créée sera ajoutée à la liste de dépendances.
    def read_xml_relations(self,sent,depgraph,chunks):
    	relations = []
    	relationlist = sent.getElementsByTagName('relation')
    	for relation in relationlist:
    	    if re.match("^COORD$",relation.attributes['type'].value):
    	    	depgraph = self. read_xml_relations_coord(relation,depgraph,chunks)
    	    	pass
    	    elif re.match("^ATB",relation.attributes['type'].value):
    	    	depgraph = self.read_xml_atb(relation,depgraph,chunks)
    	    	pass
    	    elif re.match("^MOD-",relation.attributes['type'].value):
    	    	depgraph = self.read_xml_modifieurs(relation,depgraph,chunks)
    	    	pass
    	    elif re.match("^JUXT|APPOS$",relation.attributes['type'].value):
    	    	depgraph = self. read_xml_app_juxt(relation,depgraph,chunks)
    	    	pass
    	    else:
    	    	depgraph = self.read_xml_verbes(relation,depgraph,chunks)
    	    	pass    			
    	return depgraph
    
    # NOTE:
    # Si, dans une relation, un élément est de type chunk,
    # nous renvoyons la tete de chunk à la place.
    # Nous avons ainsi uniquement des relations entre formes,
	# et non des relations mixtes (formes/chunks )
    
    # La relation est de type COORD:
    # capter les éléments x="coord-g",y="coord-d" et c="coordonnant"
    # puis créer les triplets cooord1(c,x) et coord2(c,y)
    def read_xml_relations_coord(self,relation,depgraph,chunks):
    	gouv = -1
    	dep = -1
    	coord = -1
    	for node in relation.childNodes:
   			if node.nodeType == 1:
				if node.nodeName == "coord-g":
					gouv = node.attributes['xlink:href'].value
					if not gouv == "vide":
						g = re.search("^E\d+(F|G)(\d+)$",gouv)
						type = g.group(1)
						gouv = int(g.group(2))
						if type =="G":
							gouv = int(chunks.get_head(int(gouv)))
					pass
				elif node.nodeName == "coord-d":
					dep = node.attributes['xlink:href'].value
					d = re.search("^E\d+(F|G)(\d+)$",dep)
					type = d.group(1)
					dep = int(d.group(2))
					if type =="G":
						dep = int(chunks.get_head(int(dep)))
					pass
				elif node.nodeName == "coordonnant":
					coord = node.attributes['xlink:href'].value
					c = re.search("^E\d+(F|G)(\d+)$",coord)
					type = c.group(1)
					coord = int(c.group(2))
					if type =="G":
						coord = int(chunks.get_head(int(coord)))
					pass
   			pass
    	coordVertex=DepVertex(chunks.tokens[coord].forme, coord)
    	# Dans le cas ou il nya pas de coordonnant gauche
    	if not gouv == "vide":	
    		gouvVertex=DepVertex(chunks.tokens[gouv].forme, gouv)
    		depgraph.add_edge(coordVertex,"coord1",gouvVertex)
    		
    	depVertex=DepVertex(chunks.tokens[dep].forme, dep)
    	depgraph.add_edge(coordVertex,"coord2",depVertex)
    	return depgraph
    	pass
    
    # La relation implique un élément verbal:
    # capter les éléments v="verbe" et x="..." (sauf "a_propager")
    # et créer le triplet correspondant: ---(v,x )
    def read_xml_verbes(self,relation,depgraph,chunks):
    	gouv = -1
    	dep = -1
    	label = ""
    	if relation.attributes['type'].value == "SUJ-V":
    		label = "suj_v"
    	elif relation.attributes['type'].value == "COD-V":
    		label = "cod_v"
    	elif relation.attributes['type'].value == "CPL-V":
    		label = "cpl_v"
    	elif relation.attributes['type'].value == "AUX-V":
    		label = "aux_v"
    	elif relation.attributes['type'].value == "COMP":
    		label = "comp"
    	else:
    		print "#TYPE INATTENDU: "+relation.attributes['types'].value	
    	for node in relation.childNodes:
   			if node.nodeType == 1:
				if node.nodeName == "verbe":
					gouv = node.attributes['xlink:href'].value
					g = re.search("^E\d+(F|G)(\d+)$",gouv)
					type = g.group(1)
					gouv = int(g.group(2))
					if type =="G":
						gouv = int(chunks.get_head(int(gouv)))
					pass
				elif node.nodeName != "a-propager":
					dep = node.attributes['xlink:href'].value
					d = re.search("^E\d+(F|G)(\d+)$",dep)
					type = d.group(1)
					dep = int(d.group(2))
					if type =="G":
						dep = int(chunks.get_head(int(dep)))
					pass
   			pass
    	gouvVertex=DepVertex(chunks.tokens[gouv].forme, gouv)
    	depVertex=DepVertex(chunks.tokens[dep].forme, dep)
    	depgraph.add_edge(gouvVertex,label,depVertex)
    	return depgraph
    	
    # La relation est de type ATB:
    # capter les éléments v="verbe" et a="attribut" et x="valeur"
    # puis créer le triplet ats(v,a) si x=sujet, ato(v,a) si x=objet
	# sinon, ats(v,a) si x=indef
	# (nous avons choisi de mettre la relation attribut du sujet par défaut, étant plus courante )
    def read_xml_atb(self,relation,depgraph,chunks):
    	gouv = -1
    	dep = -1
    	label = ""
    	for node in relation.childNodes:
   			if node.nodeType == 1:
				if node.nodeName == "verbe":
					gouv = node.attributes['xlink:href'].value
					g = re.search("^E\d+(F|G)(\d+)$",gouv)
					type = g.group(1)
					gouv = int(g.group(2))
					if type =="G":
						gouv = int(chunks.get_head(int(gouv)))
					pass
				elif node.nodeName == "attribut":
					dep = node.attributes['xlink:href'].value
					d = re.search("^E\d+(F|G)(\d+)$",dep)
					type = d.group(1)
					dep = int(d.group(2))
					if type =="G":
						dep = int(chunks.get_head(int(dep)))
					pass
				else:
					if node.attributes['valeur'].value =="sujet":
						label = "ats"
					elif node.attributes['valeur'].value == "objet":
						label = "ato"
					else:
						label = "ats"
   			pass
    	gouvVertex=DepVertex(chunks.tokens[gouv].forme, gouv)
    	depVertex=DepVertex(chunks.tokens[dep].forme, dep)
    	depgraph.add_edge(gouvVertex,label,depVertex)
    	return depgraph
    
    # La relation est de type MOD_*:
    # capter les éléments m="modifieur" et x="..."
    # puis créer le triplet mod_*(x,m), en fonction de la valeur de x.
    def read_xml_modifieurs(self,relation,depgraph,chunks):
    	gouv = -1
    	dep = -1
    	label = ""
    	for node in relation.childNodes:
   			if node.nodeType == 1:
				if re.match("^adjectif|adverbe|nom|verbe|preposition$",node.nodeName):
					gouv = node.attributes['xlink:href'].value
					g = re.search("^E\d+(F|G)(\d+)$",gouv)
					type = g.group(1)
					gouv = int(g.group(2))
					if type =="G":
						gouv = int(chunks.get_head(int(gouv)))
					
					if node.nodeName == "adjectif":
						label = "mod_a"
					elif node.nodeName == "adverbe":
						label = "mod_r"
					elif node.nodeName == "nom":
						label = "mod_n"
					elif node.nodeName == "verbe":
						label = "mod_v"
					elif node.nodeName == "preposition":
						label = "mod_p"
					pass
				elif node.nodeName == "modifieur":
					dep = node.attributes['xlink:href'].value
					d = re.search("^E\d+(F|G)(\d+)$",dep)
					type = d.group(1)
					dep = int(d.group(2))
					if type =="G":
						dep = int(chunks.get_head(int(dep)))
					pass
   			pass
    	gouvVertex=DepVertex(chunks.tokens[gouv].forme, gouv)
    	depVertex=DepVertex(chunks.tokens[dep].forme, dep)
    	depgraph.add_edge(gouvVertex,label,depVertex)
    	return depgraph    	
    	pass
    
	# La relation est de type APP/JUXT
    # capter les éléments p="premier" et x="suivant/appose"
    # puis créer le triplet ***(p,x), en fonction du type de x
    def read_xml_app_juxt(self,relation,depgraph,chunks):
    	gouv = -1
    	dep = -1
    	label = ""
    	for node in relation.childNodes:
   			if node.nodeType == 1:
				if node.nodeName == "premier":
					gouv = node.attributes['xlink:href'].value
					g = re.search("^E\d+(F|G)(\d+)$",gouv)
					type = g.group(1)
					gouv = int(g.group(2))
					if type =="G":
						gouv = int(chunks.get_head(int(gouv)))
					pass
				elif node.nodeName == "suivant" or node.nodeName == "appose":
					dep = node.attributes['xlink:href'].value
					d = re.search("^E\d+(F|G)(\d+)$",dep)
					type = d.group(1)
					dep = int(d.group(2))
					if type =="G":
						dep = int(chunks.get_head(int(dep)))
					
					if node.nodeName == "suivant":
						label = "juxt"
					elif node.nodeName == "appose":
						label = "app"
   			pass
    	gouvVertex=DepVertex(chunks.tokens[gouv].forme, gouv)
    	depVertex=DepVertex(chunks.tokens[dep].forme, dep)
    	depgraph.add_edge(gouvVertex,label,depVertex)
    	return depgraph
    	pass
    	


#fichier = sys.argv[1]
#instream = open(fichier)
#
#er = EasyReader()
#print er.get_sentences(fichier).encode('latin1')
#cd = er.read_xml(instream)
#er.write_easy2pivot("/Users/fguerin/data/new_ref/general_lemonde_normalise.xml",cd)
