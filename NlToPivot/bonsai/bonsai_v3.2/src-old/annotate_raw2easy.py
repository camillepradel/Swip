#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#

import re
import os
import sys

from chunks import *
from EasyReader import *
from optparse import OptionParser
from steps import *
from easy_chunking import *
from easy_conversion import *

# Generation des fichiers à enrichir
def generate_base(dossier,fichier):		
	f = open(fichier)
	lignes = f.readlines()
	cpt=0
	for ligne in lignes:
		generate_base_per_sent(dossier,cpt,ligne)
		cpt+=1
	pass

# Génération d'un fichier à enrichir
def generate_base_per_sent(dossier,compteur,sentence):
	tokens = re.split(" ",sentence)
	fichier = "annotation_"+str(compteur)+".ann"
	f = open(dossier+"/"+fichier,"w")
	f.write("sentence("+sentence.rstrip()+")\n")
	index=1
	for token in tokens:
		token = token.rstrip()
		f.write(token+" F"+str(index)+"\n")
		index+=1
	f.close()
	pass

# Concaténation de tous les fichiers .xml du dossier
# et création du fichier xml easy final
def compile_base(dossier,fichier_out="pivot2easy.xml"):
	fichiers = os.listdir(dossier)
	fichiers_xml = []
	for fichier in fichiers:
		if re.match("^annotation_\d+\.xml",fichier):
			fichiers_xml.append(fichier)
	fichiers_xml.sort()
	cpt=0
	lines=[]
	for fichier in fichiers_xml:
		f = open(dossier+"/"+fichier)
		lines.extend(f.readlines())
		f.close()
		cpt+=1
	out = open(dossier+"/"+fichier_out,"w")
	out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
	out.write("<!DOCTYPE DOCUMENT SYSTEM \"easy.dtd\">\n")
	out.write("<DOCUMENT fichier=\""+fichier_out+"\" id=\"pivot annoté en easy\" date=\"_DATE_\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n")
	for line in lines:
		out.write(line)
	out.write("</DOCUMENT>\n")
	out.close()
	print "# "+str(cpt)+" fichiers ont été mergés dans "+fichier_out
	pass

# Obkjet 'Enonce_easy'
class Enonce_easy:

	# Initialisation à partir d'un numéro d'énoncé
	def __init__(self,id_sent,date="DATE",validator="VALIDATOR"):
		self.id_sent=id_sent
		self.chunks = Chunks(id_sent)
		self.deps = DepSentence(id_sent,"annotation_"+str(id_sent)+".xml",validator,date,DependencyGraph())
	
	# Représentation sous forme de chaine de caractères.	
	def __str__(self):
		string = ""
		string += str(self.chunks)+"\n"
		string += self.deps.depforest.triples2string(True)
		return string		
	
	# Compilation d'un fichier	
	def compile_file(self,dossier):
		fichier = dossier+"/annotation_"+str(self.id_sent)+".ann"
		self.compile_file_aux(fichier)
	
	# Compilation d'un fichier	
	def compile_file_aux(self,fichier):
		if re.match("^.+/[^\d/]+_\d+\.ann$",fichier):
			f = open(fichier)
			lignes = f.readlines()
			sentence=""
			print "FICHIER :"+fichier
			index = 1
			print "ID_SENT: "+str(self.id_sent)
			sentence
			ts = []
			ds = []
			# Lecture des lignes
			for ligne in lignes:
				if re.match("^sentence\(.+\)$",ligne):
					sentence = re.search("^sentence\((.+)\)$",ligne).group(1)
				elif re.match(".+\(.+,.+\)$",ligne):
					ds.append(ligne)
				elif re.match("^.+=.+$",ligne) or re.match("^[\t\s\n]*[^\t\n]+[\t\s\n]*\n$",ligne):
					ts.append(ligne)
				else:
					pass
			f.close()
			
			# Pour les lignes se rapportant aux formes et groupes			
			id_groupe=1
			print "GROUPES\n----------"
			for ligne in ts:	
				if re.match("^.+=.+$",ligne):
					try:
						tokens = re.search("^[\t\s\n]*(.+) F\d+[\t\s\n]*=.+$",ligne).group(1)
						chunk = re.search("^.+=[\t\s\n]*([A-Z]+)\d+[\t\s\n]*$",ligne).group(1)
						groupe = int(re.search("^.+=[\t\s\n]*[A-Z]+(\d+)[\t\s\n]*$",ligne).group(1))
					except AttributeError:
						print "# Malformation de la ligne: "+str(ligne)
						sys.exit()
					liste_tokens = re.split(" ",tokens)
					#liste_tokens = liste_tokens[:-1]
					if groupe != id_groupe:
						print "# Erreur pour le groupe "+chunk+str(groupe)+" ! (groupe attendu:"+str(id_groupe)+")"
						sys.exit()

					nliste_tokens=[]
					bool_tok=True		
					#print "-------------"				
					#print liste_tokens
					for lt in liste_tokens:
						if bool_tok:
							nliste_tokens.append(lt)
							bool_tok=False
						else:
							bool_tok=True
					#print nliste_tokens
					#print "=============="

					print "\t"+chunk+str(groupe)+"\t",
					for token in nliste_tokens:
						t = Token(token,chunk,id_groupe,0)
						self.chunks.add_token(index,t)
						print token+"[F"+str(index)+"] ",
						index += 1
					print
					id_groupe+=1	

				elif re.match("^[\t\s\n]*[^\t\n]+[\t\s\n]*\n$",ligne):
					token = re.search("^[\t\s\n]*([^\t\s\n]+) F\d+[\t\s\n]*\n$",ligne).group(1)
					print "\t<none>\t"+token+"[F"+str(index)+"]"
					t = Token(token,"",0,0)
					self.chunks.add_token(index,t)
					index += 1
			# Affectation des tetes pour chaque chunk
			self.chunks.set_heads()
			
			# Pour les les lignes se rapportant aux relations
			print "\nRELATIONS\n------------"			

			if len(ds) == 0:
				print "\t#PAS DE RELATION POUR: "+str(self.id_sent)
				sys.exit()
			
			dep_vus=[]
			
			for ligne in ds:
				try:
					triplet = re.search("^([A-Za-z_0-9]+)\(([A-Za-z]+)(\d+),([A-Za-z]+)(\d+)\)$",ligne)
					label = triplet.group(1).lower()
					gouv = triplet.group(2).upper()
					id_gouv = int(triplet.group(3))
					dep = triplet.group(4).upper()
					id_dep = int(triplet.group(5))
					if dep+str(id_dep) in dep_vus:
						print "# PROBLEME! Le dépendant "+dep+str(id_dep)+" a déja été utilisé!"
						sys.exit()
					else:
						dep_vus.append(dep+str(id_dep))
				except:
					print "# Erreur: "+ligne
					sys.exit()
				if not label in ["suj_v","aux_v","cod_v","cpl_v","mod_v","mod_n","mod_a","mod_r","mod_p","ats","ato","comp","coord1","coord2","juxt","app"]:
					print "# Mauvais label de relation: "+label
					sys.exit()	
				print "\t"+label+"(",
				if gouv == "F":
					gouv_vertex = DepVertex(self.chunks.tokens[id_gouv].forme,id_gouv)
					print gouv+str(id_gouv)+": "+self.chunks.tokens[id_gouv].forme+" ,",
				else:
					head = self.chunks.groupes[id_gouv].head
					gouv_vertex = DepVertex(self.chunks.tokens[head].forme,head)
					print  gouv+str(id_gouv)+": "+self.chunks.get_const(id_gouv)+",",
				if dep == "F":
					dep_vertex = DepVertex(self.chunks.tokens[id_dep].forme,id_dep)
					print dep+str(id_dep)+": "+self.chunks.tokens[id_dep].forme,
				else:
					head = self.chunks.groupes[id_dep].head
					dep_vertex = DepVertex(self.chunks.tokens[head].forme,head)
					print  dep+str(id_dep)+": "+self.chunks.get_const(id_dep),
				print ")"
				self.deps.depforest.add_edge(gouv_vertex,label,dep_vertex)
		pass
	
	# Ecriture du fichier xml	
	def write_xml(self,dossier):
		print "\n# Ecriture dans le fichier "+dossier+"/annotation_"+str(self.id_sent)+".xml"
		f = open(dossier+"/annotation_"+str(self.id_sent)+".xml","w")
		f.write("<E id=\""+str(self.id_sent)+"\">\n")
		f.write(sortie_xml_chunk(self.chunks))
		f.write(sortie_xml_dependances(self.deps,ROOT_INDEX = 0))
		f.write("</E>\n")

# Main Program
##################
# Command Line interface
usage = """	
           %prog 

   %prog --help .
   """

parser=OptionParser(usage=usage)
parser.add_option("--dir",dest="out_dir",default=None,metavar='<dossier>',help="")
parser.add_option("--raw",dest="rawtext",default=None,metavar='<fichier>',help="")
parser.add_option("--easy",dest="easy",default=-1,metavar='<entier>',help="")
parser.add_option("--base",dest="base",action="store_true",default=None,help="")
parser.add_option("--compile",dest="compile",action="store_true",default=None,help="")
(opts,args) = parser.parse_args()


out_dir = opts.out_dir
rawtext = opts.rawtext
easy=int(opts.easy)
base = bool(opts.base)
compile = bool(opts.compile)

if not os.path.isdir(out_dir) or not os.path.isfile(rawtext):
	print "# Veuillez remplir les options --dir et --raw !"
	sys.exit()
	
if base:
	generate_base(out_dir,rawtext)
elif easy != -1:
	ee = Enonce_easy(easy)
	ee.compile_file(out_dir)
	ee.write_xml(out_dir)
elif compile:
	compile_base(out_dir)
	pass
else:
	print "# Veuillez indiquer UNE option --check ou --easy!"
	