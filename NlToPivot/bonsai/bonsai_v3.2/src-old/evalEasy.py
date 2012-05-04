#!/usr/bin/env python -O
# -*- coding: iso-8859-1 -*-
#
# EasyReader
#
# Author : Francois Guerin
# Date : Sept 2008
# Objectif:
# Lecture de deux fichiers XML au format easy
# (ou de deux dossiers contenant ce type de fichiers)
# et évaluation de l'entité prise comme référence par rapport à l'entité prise comme hypothese


import sys
import re
import string
import os
from xml.dom import minidom
from xml.dom.minidom import parse, parseString
from chunks import *
from dgraph import *
from optparse import OptionParser
from EasyReader import *


# Informations sur le fichier dans lequel sont tirés les chunks et des dépendances de l'objet en argument.
def informations(chunksdeps_per_file,dossier):
	nb_fichiers=0
	nb_sentences=0
	file_sentences={}
	for file in chunksdeps_per_file.keys():
		nb_sent_per_file=0
		nb_fichiers = nb_fichiers+1
		for sent in chunksdeps_per_file[file]:
			nb_sentences = nb_sentences+1
			nb_sent_per_file = nb_sent_per_file+1		
		file_sentences[file]=nb_sent_per_file
	
	print "---------------------------"
	if os.path.isdir(dossier):
		print "DOSSIER: "+dossier
		print " - Nombre de fichiers: "+str(nb_fichiers)
	elif os.path.isfile(dossier):
		print "FICHIER: "+dossier
	else:
		print "SOURCE INCONNUE: "+dossier
	print " - Nombre d'enonces: "+str(nb_sentences)
	for file in file_sentences.keys():
		print "\t"+file+" : "+str(file_sentences[file])+" enonces"

# Calcul du FSCORE
def fscore(precision,rappel):
	#print "-fscore(P:"+str(precision)+",R:"+str(rappel)+")=",
	if precision + rappel > 0:
		f=float(2*precision*rappel)/(precision+rappel)
		#print str(f)
		return f
	else:
		#print "0"
		return 0

# Calcul de la PRECISION
# (cas spécial: si les nombres de retournés et de pertinents sont égaux à 0, la précision sera de 1)
def precision(pertinents_retournes,retournes,pertinents):
	#print "-precision(PR:"+str(pertinents_retournes)+",R:"+str(retournes)+")=",
	if retournes < 0.1 and pertinents < 0.1:
		#print "100"
		return 100
	elif retournes < 0.1 or pertinents < 0.1:
		#print "0"
		return 0
	else:
		p=float(pertinents_retournes)/retournes * 100
		#print str(p)
		return p

# Calcul du Rappel
# (cas spécial: si les nombres de pertinents et de retournés sont égaux à 0, le rappel sera de 1)
def rappel(pertinents_retournes,pertinents,retournes):
	#print "-rappel(PR:"+str(pertinents_retournes)+",P:"+str(pertinents)+")=",
	if retournes < 0.1 and pertinents < 0.1:
		#print "100"
		return 100
	elif retournes < 0.1 or pertinents < 0.1:
		#print "0"
		return 0
	else:
		r=float(pertinents_retournes)/pertinents * 100
		#print str(r)
		return r 

# Comparaison des deux jeux de données (la référence et l'hypothese)
# Pour chaque phrase, les triplets totalement similaires, similaires sauf pour la relation, ou similaires modulo l'inversion des arguments seront gardés en mémoire.
def comparaison(cd_reference,cd_construit):
	compare={}
	for file in cd_reference.keys():
		compare[file]={}
		liste_ref = cd_reference[file].keys()
		liste_ref.sort()
		liste_cons = cd_construit[file].keys()
		liste_cons.sort()
		debug = "1_"+file+".txt"
		desc = open(debug,'w')
		cpt = 0
		while cpt < len(cd_reference[file].keys()):
#			print "------------------------------------------------------"
#			print cd_reference[file][liste_ref[cpt]]['chunks'].get_sentence()
#			print cd_construit[file][liste_cons[cpt]]['chunks'].get_sentence()
			deps_ref = cd_reference[file][liste_ref[cpt]]['deps']
			deps_cons = cd_construit[file][liste_cons[cpt]]['deps']
			desc.write("\nE"+str(liste_cons[cpt])+" reference +++++++++++++++++++++++++++++++++++++\n")
			desc.write(deps_ref.triples2string(True))
			desc.write("E"+str(liste_cons[cpt])+" construit -------------------------------------\n")
			desc.write(deps_cons.triples2string(True))
			
			compare[file][cpt]={}
			compare[file][cpt]['sentence_form']=cd_reference[file][liste_ref[cpt]]['chunks'].get_sentence()
			compare[file][cpt]['000']=[]
			compare[file][cpt]['_00']=[]
			compare[file][cpt]['100']=[]
			compare[file][cpt]['111_ref']=set([])
			compare[file][cpt]['111_cons']=set([])
			compare[file][cpt]['inv']=[]
			compare[file][cpt]['110']=set([])
			compare[file][cpt]['010']=set([])

			#verifier la tokenization:
			if len(cd_reference[file][liste_ref[cpt]]['chunks'].tokens.keys()) == len(cd_construit[file][liste_cons[cpt]]['chunks'].tokens.keys()):
				compare[file][cpt]['sentence']=0
			else:
				compare[file][cpt]['sentence']=1
				print "SENT:"+str(cpt)
				print "REF:"+str(len(cd_reference[file][liste_ref[cpt]]['chunks'].tokens.keys()))
				print "CONS:"+str(len(cd_construit[file][liste_cons[cpt]]['chunks'].tokens.keys()))
				
			liste_ok=set([])
			
			compare[file][cpt]['total_ref']=float(len(deps_ref.edges))
			compare[file][cpt]['total_cons']=float(len(deps_cons.edges))
			for dref in deps_ref.edges:
				for dcons in deps_cons.edges:
					if dref.dest.idx == dcons.dest.idx:
						if dref.orig.idx == dcons.orig.idx:						
							compare[file][cpt]['_00'].append((dref,dcons))
							liste_ok.add(dref)
							liste_ok.add(dcons)
							
							if dref.label == dcons.label:
								compare[file][cpt]['000'].append((dref,dcons))
								liste_ok.add(dref)
								liste_ok.add(dcons)
								
							else:
								compare[file][cpt]['100'].append((dref,dcons))
								liste_ok.add(dref)
								liste_ok.add(dcons)
						else:
							if dref.label == dcons.label:
								compare[file][cpt]['010'].add((dref,dcons))
								liste_ok.add(dref)
								liste_ok.add(dcons)
								
							else:
								compare[file][cpt]['110'].add((dref,dcons))
								liste_ok.add(dref)
								liste_ok.add(dcons)
									
					elif dref.orig.idx == dcons.dest.idx and dref.dest.idx == dcons.orig.idx:
							compare[file][cpt]['inv'].append((dref,dcons))
							
					else:
						if not dref in liste_ok:
							compare[file][cpt]['111_ref'].add(dref)
						if not dcons in liste_ok:
							compare[file][cpt]['111_cons'].add(dcons)
			cpt = cpt + 1
	return compare
			
# Affichage de la comparaison effectuée ci-dessus			
def affiche_comparaison(compare):
	for file in compare.keys():
		print "FICHIER: "+file
		pourcent_rappel=0
		prc_anonyme_rappel=0
		pourcent_precision=0
		prc_anonyme_precision=0
		
		nb_label_ok=0
		nb_anonyme_ok=0
		nb_relations_ref=0
		nb_relations_cons=0
		
		nb_rel_ref = 0
		nb_rel_cons = 0
		bad_token = 0
		parsing_failure_sentences = []
		
		for sent in compare[file].keys():
		
			nb_label_ok += len(compare[file][sent]['000'])
			nb_anonyme_ok += len(compare[file][sent]['_00'])
			nb_relations_ref += compare[file][sent]['total_ref']
			nb_relations_cons += compare[file][sent]['total_cons']
					
			if compare[file][sent]['sentence'] == 1:
				print "\t* ",
				bad_token += 1
				parsing_failure_sentences.append(sent)
			else:
				print "\t  ",
		
			if compare[file][sent]['total_ref'] > 0 and compare[file][sent]['total_cons'] > 0:
			
				print "ENONCE "+str(sent) +":"
				print "\t"+compare[file][sent]['sentence_form']+"\n"
				rappel_labellise =  rappel(len(compare[file][sent]['000']),compare[file][sent]['total_ref'],compare[file][sent]['total_cons'])
				print "\t\trappel: "+str(rappel_labellise)+" % labellise"
				pourcent_rappel += rappel_labellise

				precision_labellise = precision(len(compare[file][sent]['000']),compare[file][sent]['total_cons'],compare[file][sent]['total_ref'])
				print "\t\tprecis: "+str(precision_labellise)+" % labellise"
				pourcent_precision += precision_labellise
				
				fscore_labellise = fscore(rappel_labellise,precision_labellise)
				print "\t\tFSCORE: "+str(fscore_labellise)+" % labellise"				
				
				rappel_anonyme = rappel(len(compare[file][sent]['_00']),compare[file][sent]['total_ref'],compare[file][sent]['total_cons'])
				print "\t\trappel: "+str(rappel_anonyme)+" % anonyme"
				prc_anonyme_rappel += rappel_anonyme

				precision_anonyme = precision(len(compare[file][sent]['_00']),compare[file][sent]['total_cons'],compare[file][sent]['total_ref'])
				print "\t\tprecis: "+str(precision_anonyme)+" % anonyme"
				prc_anonyme_precision += precision_anonyme
		
				fscore_anonyme = fscore(rappel_anonyme,precision_anonyme)
				print "\t\tFSCORE: "+str(fscore_anonyme)+" % anonyme"	
				
				print "\t\t Sur "+str(compare[file][sent]['total_ref'])+" relations ref ("+str(compare[file][sent]['total_cons'])+" cons):"
				print "\t\t\t - "+str(len(compare[file][sent]['000']))+" ok (labellise)"
				print "\t\t\t - "+str(len(compare[file][sent]['_00']))+" ok (anonyme)"
				
				if len(compare[file][sent]['000']) > 0:
					print "\t\t\t\t-dont les relations qui ont ete trouvees:"
				for (u,v) in compare[file][sent]['000']:
					print "\t\t\t\t REF/CONS: "+str(u.print_parcPivot())				
								
				if len(compare[file][sent]['100']) > 0:
					print "\t\t\t\t-dont les relations qui n'ont pas le meme label:"
				
				for (u,v) in compare[file][sent]['100']:
					print "\t\t\t\t REF: "+str(u.print_parcPivot())+"\t-\tCONS: "+str(v.print_parcPivot())
				
				if len(compare[file][sent]['inv']) > 0:
					print "\t\t\t-Les relations inversees sont:"
					for (u,v) in compare[file][sent]['inv']:
						print "\t\t\t\t REF: "+str(u.print_parcPivot())+"\t-\tCONS: "+str(v.print_parcPivot())

				if len(compare[file][sent]['010']) > 0:
					print "\t\t\t-Les relations ou le gouverneur est different mais le label est le meme:"
					for (u,v) in compare[file][sent]['010']:
						print "\t\t\t\t REF: "+str(u.print_parcPivot())+"\t-\tCONS: "+str(v.print_parcPivot())

				if len(compare[file][sent]['110']) > 0:
					print "\t\t\t-Les relations ou les gouverneurs et les labels sont differents:"
					for (u,v) in compare[file][sent]['110']:
						print "\t\t\t\t REF: "+str(u.print_parcPivot())+"\t-\tCONS: "+str(v.print_parcPivot())

				nb_rel_ref = nb_rel_ref + compare[file][sent]['total_ref']
				nb_rel_cons = nb_rel_cons + compare[file][sent]['total_cons']
				
				
			else:
				if compare[file][sent]['total_cons'] > 0:
					print "ENONCE "+str(sent) +" : "+str(float(0))+" %"
					pourcent_rappel += 0
					prc_anonyme_rappel += 0
					pourcent_precision += 0
					prc_anonyme_precision += 0
				else:
					print "ENONCE "+str(sent) +" : "+str(float(100))+" %"
					pourcent_rappel += 100
					prc_anonyme_rappel += 100
					pourcent_precision += 100
					prc_anonyme_precision += 100
		
		print "\nMOYENNE:"
		rappel_labellise_final = float(pourcent_rappel)/float(len(compare[file].keys()))
		precision_labellise_final = float(pourcent_precision)/float(len(compare[file].keys()))					
		print "---> rappel: "+str(rappel_labellise_final)+" % labellise"
		print "---> precis: "+str(precision_labellise_final)+" % labellise"
		print "---> FSCORE: "+str(fscore(rappel_labellise_final, precision_labellise_final))+" % labellise"


		rappel_anonyme_final = float(prc_anonyme_rappel)/float(len(compare[file].keys()))
		precision_anonyme_final = float(prc_anonyme_precision)/float(len(compare[file].keys()))
		print "---> rappel: "+str(rappel_anonyme_final)+" % anonyme"
		print "---> precis: "+str(precision_anonyme_final)+" % anonyme"
		print "---> FSCORE: "+str(fscore(rappel_anonyme_final, precision_anonyme_final))+" % anonyme"

		print "\t\t sur une moyenne de "+str(float(nb_rel_ref)/float(len(compare[file].keys())))+" relations par enonce (ref)."
		print "\t\t sur une moyenne de "+str(float(nb_rel_cons)/float(len(compare[file].keys())))+" relations par enonce (cons)."
		
		print "\nTT ENONCE CONFONDU:"
		rappel_labellise_final = rappel(nb_label_ok,nb_relations_ref,nb_relations_cons)
		precision_labellise_final = precision(nb_label_ok,nb_relations_cons,nb_relations_ref)				
		print "---> rappel: "+str(rappel_labellise_final)+" % labellise"
		print "---> precis: "+str(precision_labellise_final)+" % labellise"
		print "---> FSCORE: "+str(fscore(rappel_labellise_final, precision_labellise_final))+" % labellise"


		rappel_anonyme_final = rappel(nb_anonyme_ok,nb_relations_ref,nb_relations_cons)
		precision_anonyme_final = precision(nb_anonyme_ok,nb_relations_cons,nb_relations_ref)
		print "---> rappel: "+str(rappel_anonyme_final)+" % anonyme"
		print "---> precis: "+str(precision_anonyme_final)+" % anonyme"
		print "---> FSCORE: "+str(fscore(rappel_anonyme_final, precision_anonyme_final))+" % anonyme"		
		
		print "\nPARSING FAILURE: "+str(bad_token)
		for pfs in parsing_failure_sentences:
			print "\tSENT: "+str(pfs)
			
		
		print "\n# MOYENNE: moyenne des mesures par énoncé.\n# TT ENONCE CONFONDU: mesures totales (~vraie eval d'Easy)"

liste_relations_easy = ["suj_v","aux_v","cod_v","cpl_v","mod_v","mod_n","mod_a","mod_r","mod_p","ats","ato","comp","coord1","coord2","juxt","app"]

# Comparaison des relations syntaxiques Easy dans les deux jeux de données
# une première table recense les résultats pour chaque énoncé
# une deuxième table recense les résultats pour le jeu de données entier
# une troisième table recense les résultats utiles à l'établissement d'une matrice de confusion sur les relations easy
def comparaison_categorie(cd_reference,cd_construit):
	compare={}
	compare2={}
	confusion={}
		
#	for rel in liste_relations_easy:
#		print rel
	
	for file in cd_reference.keys():
		compare[file]={}
		compare2[file]={}
		confusion[file]={}
		debug = "2_"+file+".txt"
		desc = open(debug,'w')
		liste_ref = cd_reference[file].keys()
		liste_ref.sort()
		liste_cons = cd_construit[file].keys()
		liste_cons.sort()
		
		for rel in liste_relations_easy:
			compare2[file][rel]={}
			compare2[file][rel]['000']=0
			compare2[file][rel]['_00']=0
			compare2[file][rel]['total_ref']=0
			compare2[file][rel]['total_cons']=0			

		for rel in liste_relations_easy:
			confusion[file][rel]={}
			if not confusion[file].has_key('total'):
				confusion[file]['total']={}
			confusion[file]['total'][rel]=0
			if not confusion[file].has_key('inconnu'):
				confusion[file]['inconnu']={}
			confusion[file]['inconnu'][rel]=0
			for rel2 in liste_relations_easy:
				confusion[file][rel][rel2]=0
				
		cpt = 0
		while cpt < len(cd_reference[file].keys()):
			deps_ref = cd_reference[file][liste_ref[cpt]]['deps']
			deps_cons = cd_construit[file][liste_cons[cpt]]['deps']
			
			desc.write("\nE"+str(liste_cons[cpt])+" phrase +++++++++++++++++++++++++++++++++++++\n")
			desc.write(cd_reference[file][liste_ref[cpt]]['chunks'].get_sentence()+"\n")
			desc.write("E"+str(liste_cons[cpt])+" reference -------------------------------------\n")
			desc.write(deps_ref.triples2string(True))
			desc.write("E"+str(liste_cons[cpt])+" construit -------------------------------------\n")
			desc.write(deps_cons.triples2string(True))
			
			compare[file][cpt]={}
			for rel in liste_relations_easy:
				compare[file][cpt][rel]={}
				compare[file][cpt][rel]['000']=[]
				compare[file][cpt][rel]['_00']=[]
				compare[file][cpt][rel]['100']=[]
				compare[file][cpt][rel]['total_ref']=0
				compare[file][cpt][rel]['total_cons']=0				

			for dref in deps_ref.edges:
				compare[file][cpt][dref.label]['total_ref'] += 1
				compare2[file][dref.label]['total_ref'] += 1
				
			for dcons in deps_cons.edges:
				compare[file][cpt][dcons.label]['total_cons'] += 1
				compare2[file][dcons.label]['total_cons'] += 1

			for dref in deps_ref.edges:
				confusion[file]['total'][dref.label] += 1
				inconnu=True
				for dcons in deps_cons.edges:
					if dref.orig.idx == dcons.orig.idx:
						if dref.dest.idx == dcons.dest.idx:
							inconnu= inconnu and False
							compare[file][cpt][dref.label]['_00'].append((dref,dcons))
							compare2[file][dref.label]['_00'] += 1
							confusion[file][dref.label][dcons.label] += 1
							if dref.label == dcons.label:
								compare[file][cpt][dref.label]['000'].append((dref,dcons))
								compare2[file][dref.label]['000'] += 1
							else:
								compare[file][cpt][dref.label]['100'].append((dref,dcons))
				if inconnu:
					confusion[file]['inconnu'][dref.label] += 1	
			cpt = cpt + 1
	return compare,compare2,confusion
	
# Affichage dans la sortie standard de la comparaison effectuée ci-dessus
# (Evaluation des relations Easy sur le jeu de donnée entier)
def affiche_comparaison_categorie(compare):

	for file in compare.keys():
		#print "FICHIER: "+file
		final={}
		for cpt in compare[file].keys():
			print "\tENONCE: "+str(cpt)
			for rel in compare[file][cpt].keys():
				if not final.has_key(rel):
					final[rel]={}
					final[rel]['label']={}
					final[rel]['label']['p']=0
					final[rel]['label']['r']=0
					final[rel]['label']['f']=0
					final[rel]['anon']={}
					final[rel]['anon']['p']=0
					final[rel]['anon']['r']=0
					final[rel]['anon']['f']=0
				print "\t\tRELATION: "+rel
				print "\t\t\tLABELLISE: ",
				p=precision(len(compare[file][cpt][rel]['000']),compare[file][cpt][rel]['total_cons'],compare[file][cpt][rel]['total_ref'])
				r=rappel(len(compare[file][cpt][rel]['000']),compare[file][cpt][rel]['total_ref'],compare[file][cpt][rel]['total_cons'])
				f=fscore(p,r)
				print "P="+str(p)+ "R="+str(r)+" F="+str(f)
				final[rel]['label']['p'] += p
				final[rel]['label']['r'] += r
				final[rel]['label']['f'] += f
				
				print "\t\t\tANONYME: ",
				p=precision(len(compare[file][cpt][rel]['_00']),compare[file][cpt][rel]['total_cons'],compare[file][cpt][rel]['total_ref'])
				r=rappel(len(compare[file][cpt][rel]['_00']),compare[file][cpt][rel]['total_ref'],compare[file][cpt][rel]['total_cons'])
				f=fscore(p,r)
				print "P="+str(p)+ "R="+str(r)+" F="+str(f)
				final[rel]['anon']['p'] += p
				final[rel]['anon']['r'] += r
				final[rel]['anon']['f'] += f
		
		print "\tTOTAL:"
		for rel in final:
			print "\t\tREL: "+rel
			print "\t\t\tLABELLISE:",
			print "P="+str(final[rel]['label']['p']/len(compare[file].keys())),
			print " R="+str(final[rel]['label']['r']/len(compare[file].keys())),
			print " F="+str(final[rel]['label']['f']/len(compare[file].keys()))
			print "\t\t\tANONYME:",
			print "P="+str(final[rel]['anon']['p']/len(compare[file].keys())),
			print " R="+str(final[rel]['anon']['r']/len(compare[file].keys())),
			print " F="+str(final[rel]['anon']['f']/len(compare[file].keys()))			
			pass

# Affichage dans la sortie standard de la comparaison effectuée ci-dessus
# (Evaluation des relations Easy pour chaque énoncé dans le jeu de donnée entier)
def affiche_comparaison_categorie2(compare):

	for file in compare.keys():
		print "FICHIER: "+file
		for rel in compare[file].keys():
			print "\t-----------------------"
			print "\tREL: "+rel
			print "\t\t -dans la reference: "+str(compare[file][rel]['total_ref'])
			print "\t\t -renvoyés par le parser: "+str(compare[file][rel]['total_cons'])
			print "\t\t -OK en labelé: "+str(compare[file][rel]['000'])
			print "\t\t -Ok en anonyme: "+str(compare[file][rel]['_00'])

			p=precision(compare[file][rel]['000'],compare[file][rel]['total_cons'],compare[file][rel]['total_ref'])
			r=rappel(compare[file][rel]['000'],compare[file][rel]['total_ref'],compare[file][rel]['total_cons'])
			f=fscore(p,r)
			print "\t\tLABELLISE: P="+str(p)+" R="+str(r)+" F="+str(f)
			
			p=precision(compare[file][rel]['_00'],compare[file][rel]['total_cons'],compare[file][rel]['total_ref'])
			r=rappel(compare[file][rel]['_00'],compare[file][rel]['total_ref'],compare[file][rel]['total_cons'])
			f=fscore(p,r)
			print "\t\tANONYME: P="+str(p)+" R="+str(r)+" F="+str(f)

# Affichage dans la sortie standard de la table de confusion créée ci-dessus
def affiche_matrice(compare):

	for file in compare.keys():
		print "FICHIER: "+file
		for rel in liste_relations_easy:
			print "\tREL: "+rel,
			max1=0
			arg1="_"
			max2=0
			arg2="_"
			max3=0
			arg3="_"			
			for rel2 in liste_relations_easy:
				if confusion[file][rel][rel2] > max1:
					max3=max2
					arg3=arg2
					max2=max1
					arg2=arg1
					max1=confusion[file][rel][rel2]
					arg1=rel2
				elif confusion[file][rel][rel2] > max2:
					max3=max2
					arg3=arg2
					max2=confusion[file][rel][rel2]
					arg2=rel2
				elif confusion[file][rel][rel2] > max3:
					max3=confusion[file][rel][rel2]
					arg3=rel2
										
			print " -> ",
			if max1 > 0:
				print arg1+":"+str(max1),
				if max2 > 0:
					print "\t- "+arg2+":"+str(max2),
					if max3 > 0:
						print "\t- "+arg3+":"+str(max3),
			else:
				print "(rien)",
			label_ok = confusion[file]['total'][rel] - confusion[file]['inconnu'][rel]
			print " sur "+str(label_ok)+" ("+str(confusion[file]['total'][rel])+" dans la reference)"
			
# Main Program
##################
# Command Line interface
usage = """
           %prog --reference <dossier> --construit <dossier>
           
   %prog --help .
   """

parser=OptionParser(usage=usage)
parser.add_option("--reference",dest="reference",default=None,help="Dossier contenant les fichiers easy xml de reference",metavar='VALUE')
parser.add_option("--construit",dest="construit",default=None,help="Dossier contenant les fichiers easy xml construits",metavar='VALUE')
parser.add_option("--confusion",dest="eval_confusion",action="store_true",default=False,help="Evaluation avec matrices de confusion",metavar='VALUE')
parser.add_option("--eval-enonce",dest="eval_enonce",action="store_true",default=False,help="Evaluation par enonce",metavar='VALUE')
parser.add_option("--eval-cat-enonce",dest="eval_cat_enonce",action="store_true",default=False,help="Evaluation des categories par enonce",metavar='VALUE')
parser.add_option("--eval-cat",dest="eval_cat",action="store_true",default=False,help="Evaluation des caterogies",metavar='VALUE')

parser.add_option("--fine", action="store_true", dest="fine_types", default=False, help='VALUE')


(opts,args) = parser.parse_args()

#OPTIONS
#print opts
reference = str(opts.reference)
construit = str(opts.construit)

eval_confusion = bool(opts.eval_confusion)
eval_enonce = bool(opts.eval_enonce)
eval_cat_enonce = bool(opts.eval_cat_enonce)
eval_cat = bool(opts.eval_cat)

########
# MAIN #
########

print "--------------------------------------------------------------"
print "EVAL_ENONCE\t=\t"+str(eval_enonce)
print "EVAL_CAT-ENONCE\t=\t"+str(eval_cat_enonce)
print "EVAL_CAT\t=\t"+str(eval_cat)
print "EVAL_CONFUSION\t=\t"+str(eval_confusion)
print "--------------------------------------------------------------"

if reference <> None:
	if os.path.isdir(reference):	
		print "Parsing du dossier reference: "+reference
		easyReader_reference = EasyReader()
		chunksdeps_per_file_ref = easyReader_reference.read_dir_xml(reference)	
		pass
	elif os.path.isfile(reference):
		print "Parsing du fichier reference: "+reference
		reference_file = re.search("/?([^/]+)$",reference).group(1)
		easyReader_reference = EasyReader()
		chunksdeps_per_file_ref = {}
		chunksdeps_per_file_ref[reference_file] = easyReader_reference.read_xml(reference)	
		pass
	else:
		print "\n# "+reference+" nest pas un dossier!"
		sys.exit()
else:
	sys.exit()
	
if construit:
	if os.path.isdir(construit):
		print "Parsing du dossier construction: "+construit
		easyReader_construit = EasyReader()
		chunksdeps_per_file_cons = easyReader_construit.read_dir_xml(construit)
		pass
	elif os.path.isfile(construit):
		print "Parsing du fichier construction: "+construit
		construit_file = re.search("/?([^/]+)$",construit).group(1)
		easyReader_construit = EasyReader()
		chunksdeps_per_file_cons = {}
		chunksdeps_per_file_cons[construit_file] = easyReader_construit.read_xml(construit)
		pass
	else:
		print "\n# "+ construit +" nexiste pas: il sera cree!"
		os.mkdir(construit)
else:
	sys.exit()

informations(chunksdeps_per_file_ref,reference)
informations(chunksdeps_per_file_cons,construit)

if eval_enonce:
	print ""
	compare=comparaison(chunksdeps_per_file_ref, chunksdeps_per_file_cons)
	affiche_comparaison(compare)

if eval_cat_enonce or eval_confusion or eval_cat:
	print ""
	(compare,compare2,confusion)=comparaison_categorie(chunksdeps_per_file_ref, chunksdeps_per_file_cons)
	if eval_cat_enonce:
		affiche_comparaison_categorie(compare)
	if eval_cat:
		affiche_comparaison_categorie2(compare2)
	if eval_confusion:
		affiche_matrice(confusion)
