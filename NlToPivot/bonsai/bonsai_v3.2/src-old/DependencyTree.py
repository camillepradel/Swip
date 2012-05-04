#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# Author : Mathieu-Henri Falco
# modified Marie Candito
# Date : sept 2008

from sys import exit
import re, os, codecs
from LabelledTree import *
from dgraph import *
from FunctionalLabellingII import *
from svm_features import *

# to be integrated in a better way later
from PropagTable import sym4_table

import codecs


#----------- changement de tagset---------------
class Tagfixer:

    def __init__(self):
        self.dico = self.symset4tosymset1()
        
    def symset4tosymset1(self):
        dico = {}
        dico['DET'] = 'D'
        dico['V'] = 'V'
        dico['VIMP'] = 'V'
        dico['VINF'] = 'V'
        dico['VS'] = 'V'
        dico['VPP'] = 'V'
        dico['VPR'] = 'V'
        dico['NPP'] = 'N'
        dico['NC'] = 'N'
        dico['CS'] = 'C'
        dico['CC'] = 'C'
        dico['CLS'] = 'CL'
        dico['CLO'] = 'CL'
        dico['CLR'] = 'CL'
        dico['P'] = 'P'
        dico['P+D'] = 'P+D'
        dico['P+PRO'] = 'P+PRO'
        dico['I'] = 'I'
        dico['PONCT'] = 'PONCT'
        dico['ET'] = 'ET'
        dico['DET'] = 'D'
        dico['ADJWH'] = 'A'
        dico['ADJ'] = 'A'
        dico['ADV'] = 'ADV'
        dico['ADVWH'] = 'ADV'
        dico['PROWH'] = 'PRO'
        dico['PROREL'] = 'PRO'
        dico['PRO'] = 'PRO'
        dico['DETWH'] = 'D'
        return dico

    def map(self,sym):
        if self.dico.has_key(sym):
            return self.dico[sym]
        else:
            #marie : todo : gestion des tagsets differents
            #sys.stderr.write('Impossible to convert symbol ('+sym+')\n')
            return sym

Fixer = Tagfixer() 

#head_table = sym4_table()

# marie : 
# A dependency tree : 
#  a word(= a lexical head) 
#  -- its dependents(= list of DependencyTree instances)
#  -- a dependency label (this node is a dependent of type deplabel 
#                         with respect to his governor)
class DependencyTree:
    def __init__(self, word, pos, deplabel=None, 
                 treenum=0, lid=-1, lemma=None):
        self.word = word
        self.pos = pos           # part-of-speech
        self.deplabel = deplabel # dependency label, whose dependent is this word
        self.lid = lid           # id of word in sentence, according to linear order
        self.lemma = lemma
        self.spine = [pos]       # list of categories projected by the word (the pos is last in spine)
        self.dependents = []     # dependents of this word
        self.treenum = treenum
        
    def add_dependent(self, dep):
        self.dependents.append(dep)

    def remove_dependent(self, dep):
    	#FRANCOIS: ca fait une erreur si le dep nest pas dans la liste...
    	if dep in self.dependents:
        	self.dependents.remove(dep)

    def add_to_spine(self,cat):
        self.spine.insert(0,cat)

    def is_unknown(self):
        return self.word == 'MISSINGHEAD'

    def id_unique(self):
        return (self.word + str(self.lid))

    def is_leaf(self):
        return (len(self.dependents) == 0)

    # Dependency-tree -> string
    # for now : ptb format only
    def tostring(self, format ='ptb'):
        node = self.pos + '~' + self.word + '~' + str(self.lid)
        if self.is_leaf():
            return node
        out = '('+node
        for dep in sorted(self.dependents,
                          lambda x,y: cmp(x.lid,y.lid)):
            out += ' ' + dep.tostring(format)
        return out+')'

    #mathieu
    # Affiche l'arbre de dépendance dans la console
    def Parcours(self):
        noeuds_file = []
        noeuds_marques = []
        noeuds_file.append(self)
        #root = True
        while len(noeuds_file) <> 0:
            tete_de_file = noeuds_file.pop(0)
            #if root:
            #    tete_de_file = (tete_de_file, None)
            #    root = False
            print ((tete_de_file.word) + ' ' + str(tete_de_file.pos))
            print '\tspine (spine)'
            for spine_constituant in tete_de_file.spine:
                print ('\t' + str(spine_constituant))
                       
            print '\t\tDependents'
            for dependent in tete_de_file.dependents:
                print ('\t\t' + str(dependent.pos) + ' ' + str(dependent.deplabel))#.encode('iso-8859-15')))
      
            for dependent in tete_de_file.dependents:
                if dependent not in noeuds_marques:
                    noeuds_marques.append(dependent)
                    noeuds_file.append(dependent)

    # mathieu
    # Permet le recensement de tous les treenum ayant déjà une fonction marquée dans le treebank
    # une liste par fonction : A_OBJ, DE_OBJ, P_OBJ, OBJ, ATO, ATS, SUJ, MOD
    def Deja_marque_dans_treebank(self, function, treenum):
        if function == 'A_OBJ': 
            liste_A_OBJ_deja_marques_dans_FTB.append(treenum)
        elif function == 'DE_OBJ':
            liste_DE_OBJ_deja_marques_dans_FTB.append(treenum)
        elif function == 'P_OBJ':
            liste_P_OBJ_deja_marques_dans_FTB.append(treenum)
        elif function == 'OBJ':
            liste_OBJ_deja_marques_dans_FTB.append(treenum)
        elif function == 'ATS':
            liste_ATS_deja_marques_dans_FTB.append(treenum)
        elif function == 'ATO':
            liste_ATO_deja_marques_dans_FTB.append(treenum)
        elif function == 'SUJ':
            liste_SUJ_deja_marques_dans_FTB.append(treenum)
        elif function == 'MOD':
            liste_MOD_deja_marques_dans_FTB.append(treenum)
   
    # FRANCOIS
    # *hack* : coordination de VP finis
    # Si une conjonction C a pour dépendant un V,
    # alors tous les dépendants de C (hormis le V) deviennent dépendants de V
    # (et ne sont donc plus dépendants de C)
    def hack_coord_vn(self):
    	coord_vn = None
    	if Fixer.map(self.pos) == 'C':
    		for dependent in self.dependents:
    			if Fixer.map(dependent.pos) == 'V':
    				coord_vn = dependent
    				break
    	deps_to_remove = []
    	for dependent in self.dependents:
                # marie debug : cas de dépendant COORD : doivent rester dep de la C (ex. phrase 116)
                #  le tout est à revoir parce que foireux... 
    		if Fixer.map(dependent.pos) <> 'V' and dependent.pos not in ['C','CC'] and coord_vn <> None:
    			coord_vn.add_dependent(dependent)
    			deps_to_remove.append(dependent)
    		dependent.hack_coord_vn()
    	for dtr in deps_to_remove:
    		self.remove_dependent(dtr)
    
    # mathieu
    # Heuristiques permettant de rajouter les dépendances non marquées dans le treebank
    # Parcours en largeur de l'arbre de dépendances
    # marie : simplification : recursivite, parcours profondeur d'abord
    # marie : adaptation : le tagset n'est plus apauvri en amont, par contre les heuristiques travaillent sur apauvrissement dynamique (Fixer.map)
    def Ajout_fonctions_heuristiques(self):
        #print self.Parcours()
        #marie
        tete = self
        pos_tete = Fixer.map(tete.pos)
        for dependent in tete.dependents:
            dependent.Ajout_fonctions_heuristiques()
            pos_dep = Fixer.map(dependent.pos)
            # Si la fonction est déjà marquée dans le treebank, 
            # on n'archive juste son treenum dans la liste appropriée
            if dependent.deplabel <> None:
                self.Deja_marque_dans_treebank(dependent.deplabel, self.treenum)
            else:
                # marie : debug d'effet d'écrasements d'une heuristique par une autre
                #        REGLE A SUIVRE : une relation assignée ne doit pas etre écrasée
                #        => ajout de quelques "continue" quand nécessaire
                # Première série de test : uniquement sur les dépendants    
                # DET(GVT, D)
                if pos_dep == 'D':
                    dependent.deplabel = 'DET'
                    continue

                if pos_dep == "PREF":
                    dependent.deplabel = 'MOD'
                # PONCT(GVT, PONCT) ou COORD selon que le constituant au-dessus du PONCT est COORD ou non 
                # dans le chemin_constituant (spine)
                if pos_dep == 'PONCT':
                    # marie : plus précisément (?), un dep PONCT porte une relation PONCT
                    # sauf s'il joue le role d'un coordonnant (essentiellement la virgule)
                    # => le test est "si le PONCT est dans un COORD et a des dépendants", 
                    #                 alors on met la relation COORD
                    if len(dependent.spine) > 1:
                        if Fixer.map(dependent.spine[-2]) <> 'COORD' or len(dependent.dependents) == 0:
                            dependent.deplabel = 'PONCT'
                        else:
                            dependent.deplabel = 'COORD'    
                    else:
                        dependent.deplabel = 'PONCT'
                    continue
                    # COORD(GVT, C) si le constituant au-dessus du PONCT est COORD
                    # (permet d'éviter de marquer les conjonctions de subordination comme COORD)
                if pos_dep == 'C' and len(dependent.spine) > 1:
                    if Fixer.map(dependent.spine[-2]) == 'COORD':
                        dependent.deplabel = 'COORD'
                        continue
                    # MOD_REL(GVT, X) si X a Srel comme tête de son constituant
                    # marie : on sort les cas COORD dominant Srel
                if Fixer.map(dependent.spine[0]) == 'Srel' and Fixer.map(tete.spine[0]) <> 'COORD':
                    dependent.deplabel = 'MOD_REL'
                    continue
                # Deuxième série de test : sur le gouverneur et le dépendant
                    # DEP_COORD(PONCT, différent de PONCT)
                if pos_tete == 'PONCT' and pos_dep <> 'PONCT': 
                    dependent.deplabel = 'DEP_COORD'
                    # Traitement des conjonctions en gouverneur
                elif pos_tete == 'C':
                    # coordination : DEP_COORD(C, dep) si le constituant au-dessus du C est COORD dans le spine (spine)
                    if Fixer.map(tete.spine[-2]) == 'COORD':
                        # marie : debug à la hache : pb des modifieurs "contre la monnaie mais(C) AUSSI(adv) contre ...
                        # si dependant adverbial, on ne le code en dep_coord que s'il est le seul dep
                        if pos_dep == 'ADV':
                            if len(tete.dependents) > 1:  dependent.deplabel = 'MOD'
                            else: dependent.deplabel = 'DEP_COORD'
                        else:
                            dependent.deplabel = 'DEP_COORD'
                        # subordination : OBJ(C, dep) sinon
                    else:
                        dependent.deplabel = 'OBJ'
                    # Traitement si tête adjectif ou adverbe
                elif pos_tete in ['A','ADV']:
                    # marie : insuffisant : il faut changer de gouverneur aussi ...
                    # marie : TODO : TESTER presence d'un adv comparatif!
                    # arg(A, C) "plus importante que"
                    # arg(A, C) "plus faiblement que"
                    if pos_dep == 'C' and len(dependent.spine) > 1:
                        if Fixer.map(dependent.spine[-2]) <> 'COORD':
                            dependent.deplabel = 'ARG_A' #'COMP'
                            continue
                        # mod(A,ADV)
                    elif pos_dep == 'ADV':
                        dependent.deplabel = 'MOD'
                        continue
                    # Traitement si PRO est la tête 
                elif pos_tete == 'PRO':
                    # MOD(PRO, A ou ADV ou N)
                    if pos_dep in ['A', 'ADV', 'N']:
                        dependent.deplabel = 'MOD'
                        continue
                # Traitement de la préposition en gouverneur
                elif pos_tete in ['P', 'P+D']:
                    # OBJ(P ou P+D, N ou PRO)
                    # marie : to handle cases of VP obj of P 
                    # (appearing only if original constituent trees are modified)
                    # if pos_dep in ['N', 'PRO']:            dependent.deplabel = 'OBJ'
                    if pos_dep in ['N', 'PRO', 'V', 'A']:
                        dependent.deplabel = 'OBJ'
                        continue
                    # ARG(P ou P+D, P ou P+D)
                    # marie debug : un P dépendant de P : si seul dépendant = OBJ (de chez moi), sinon = ARG
                    elif pos_dep in ['P', 'P+D']:
                        if len(tete.dependents) == 1:  dependent.deplabel = 'OBJ'
                        else:                          dependent.deplabel = 'ARG'
                        continue
                    # marie : ADV dépendant de P
                    # -> OBJ si seul dépendant ("depuis longtemps"), MOD sinon
                    # TODO: gestion cas "pour encore longtemps" "pour longtemps encore"
                    elif pos_dep == 'ADV':
                        if len(tete.dependents) == 1:  dependent.deplabel = 'OBJ'
                        else:                                  dependent.deplabel = 'MOD'
                        continue
                # Traitement du N en gouverneur
                elif pos_tete == 'N':
                    # DEP(N, P ou P+D)
                    if pos_dep in ['P', 'P+D']:
                        # marie : hack (tempo?) : quelques N fréquents sous-catégorisant un de_obj ...
                        #if tete.word in ['%','pourcentage','milliards','milliard','million','millions'] and dependent.word in ['de','d\'', 'du', 'des']:
                        #    dependent.deplabel = 'DE_OBJ'
                        #else:
                        # marie : "peu de surprises" => "de" est taggé Prep, mais deplabel = DET
                        # repéré par : la prep n'a pas de PP
                        if len(dependent.spine) == 1:
                            dependent.deplabel = 'DET'
                        else:
                            dependent.deplabel = 'DEP'
                        continue
                    # MOD(N, A, ADV, N ou V)
                    elif pos_dep in ['A', 'ADV', 'N','V']:
                        dependent.deplabel = 'MOD'
                        continue
                # Traitement du V en gouverneur
                elif pos_tete == 'V':
                    # MOD(V, A ou N ou ADV)
                    if pos_dep in ['A', 'N', 'ADV']:      dependent.deplabel = 'MOD'
                    # Traitement du cas AUX(V,V)
                    elif pos_dep == 'V':
                        # AUX_TPS(V, V) si le dépendant est une forme conjuguée de l'auxiliaire avoir
                        if dependent.word in liste_avoir:
                            # if dependent.lemma == 'avoir':
                            dependent.deplabel = 'AUX_TPS'
                        # AUX_TPS(V, V) si le dépendant est une forme du passif à un temps composé ou à l'infinitif  
                        elif dependent.word in ['été', 'être']:
                            dependent.deplabel = 'AUX_PASS'
                        # AUX_CAUS(V, V) si le dépendant est une forme conjuguée de "faire"
                        #elif dependent.lemma == 'faire':
                        elif dependent.word in liste_faire:
                            dependent.deplabel = 'AUX_CAUS'
                        #elif dependent.lemma == 'être':
                        elif dependent.word in liste_etre:
                            #if tete.lemma in liste_etre_pp:
                            if tete.word in liste_etre_pp:
                                dependent.deplabel = 'AUX_TPS'
                            # si présence d'un clitique pronominale alors AUX_TPS(V, V) sinon AUX_PASS(V, V)
                            else:
                                presence_aff = False
                                for dependent_ter in tete.dependents:
                                    if dependent_ter.word in ['se','s\'']:
                                        presence_aff = True
                                        continue
                                if presence_aff:
                                    dependent.deplabel = 'AUX_TPS'
                                else:
                                    dependent.deplabel = 'AUX_PASS'
                                continue
                    # COMP(V, C ou P) si le C ou le P n'a pas de dépendants
                    elif (pos_dep in ['C','P']) and \
                            len (dependent.dependents) == 0:
                        # Problem with the compound : "si bien que"
                        #for
                        dependent.deplabel = 'COMP'
                    # AFF(V, CL)
                    elif pos_dep == 'CL':
                        dependent.deplabel = 'AFF'
                    # Marie : hack : les dépendants de participes passés non traités par functional role labelling
                    # => à la hâche
                    elif pos_dep in ['P', 'P+D']:
                        if dependent.word == 'par' : dependent.deplabel = 'P_OBJ'
                        elif dependent.word in ['de', 'du', 'des', 'd'] : dependent.deplabel = 'DE_OBJ'
                        elif dependent.word in ['à', 'au', 'aux'] : dependent.deplabel = 'A_OBJ'
                        else: dependent.deplabel = 'MOD'
            # Marie : défaut = DEP
            if dependent.deplabel == None:
                dependent.deplabel = "DEP"
        
        # Gestion du comp :
        # Situation : "La société vient d'hispaniser sa filiale tout en resserrant ses liens"
        #            OBJ(vient,hispaniser) COMP(resserrant,en) MOD(resserrant, tout) 
        # Etape 1 : OBJ(vient,en)
        # Etape 2 : OBJ(en, resserrant)
        # Etape 3 : MOD(en, tout)
        #
        #             XXX(gvt1,dep1) COMP(dep1,dep2)
        # Etape 1 : relier le dep1 en dependant de dep2. OBJ(dep2,dep1)
        # Etape 2 : relier le gouverneur 1 à dep2. XXX(gvt1,dep2)
        # Etape 3 : si des dépendants de dep1 sont situées avant dep2 dans l'ordre linétaire de la phrase alors ils sont
        #            ajoutées comme dépendants de dep1.
    def Gestion_comp(self):
        noeuds_file = []
        noeuds_marques = []
        noeuds_file.append(self)        
        while len(noeuds_file) <> 0:
            tete_de_file = noeuds_file.pop(0)
            
            for dependent in tete_de_file.dependents:
                for dependent_ter in dependent.dependents:
                    if dependent_ter.deplabel == 'COMP':
                        dependent_ter.deplabel = dependent.deplabel
                        tete_de_file.dependents[tete_de_file.dependents.index(dependent)] = dependent_ter[0]
                        dependent.deplabel = 'OBJ'
                        dependent_ter[0].dependents.append(dependent, 'OBJ')
                        dependent.remove_dependent(dependent_ter)
                        for dependent_quater in dependent.dependents:
                            if dependent_quater.lid < dependent_ter.lid:
                                dependent_ter.dependents.insert(0, dependent_quater)
                                dependent.remove_dependent(dependent_quater)
                        # Stop the FOR because compound not merge like "comme si de" will crash the programm otherwise
                        # exemple : SENTnb : 
                        break
                        
            
            for dependent_bis in tete_de_file.dependents:                              
                if dependent_bis not in noeuds_file:
                    noeuds_file.append(dependent_bis)
      
      # Gestion du aux : si présence d'un AUX_TPS dans les dépendants alors fouille pour voir s'il y a un AUX_PASS.
      # AUX_TPS(vu,a) AUX_PASS(vu,été)  => AUX_PASS(vu,été) AUX_TPS(été,a)
      # TO_DO AUX_CAUS...
    def Gestion_aux(self):
        noeuds_file = []
        noeuds_marques = []
        noeuds_file.append(self)        
        
        while len(noeuds_file) <> 0:
            tete_de_file = noeuds_file.pop(0)
            
            for dependent in tete_de_file.dependents:
                if dependent.deplabel == 'AUX_TPS':
                    for dependent_ter in tete_de_file.dependents:
                        if dependent_ter.deplabel == 'AUX_PASS':
                            dependent_ter.add_dependent(dependent)
                            tete_de_file.remove_dependent(dependent)
                # Replace all the COMPUND function by a Nonetype one 
                #if dependent.deplabel == 'COMPOUND':
                    #dependent.deplabel = None
                    #tete_de_file[0].dependents[tete_de_file[0].dependents.index(dependent)] = (dependent[0], None) 
            
            for dependent_bis in tete_de_file.dependents:                              
                if dependent_bis not in noeuds_file:
                    noeuds_file.append(dependent_bis)            

    # Suppression des prépositions sémantiquement vides pour A_OBJ et DE_OBJ
    def Suppression_semantiques_vides(self, function):
        if function == 'A_OBJ':
            liste_prepositions = ['à', 'au', 'aux']
        elif function == 'DE_OBJ':
            liste_prepositions = ['de', 'du', 'd\'', 'des']
        noeuds_file = []
        noeuds_marques = []
        noeuds_file.append(self)

        while len(noeuds_file) <> 0:
            tete_de_file = noeuds_file.pop(0)
            
            marque = False     
            for fille_externe in tete_de_file.dependents:
                if fille_externe.deplabel == function:
                    if fille_externe.word in liste_prepositions:
                        if len(fille_externe.dependents) == 1:
                            fille_externe.dependents[0].deplabel = fille_externe.deplabel
                            tete_de_file.dependents[tete_de_file.dependents.index(fille_externe)] = fille_externe.dependents[0]
                        else:
                            # il y a plusieurs dépendants donc il faut gérer le rattachement
                            trouve = False
                            for fille_externe_bis in fille_externe.dependents:
                                if not trouve:
                                # Si plusieurs dependents alors le nom est choisi comme nouvelle tête parmi les candidats
                                    if Fixer.map(fille_externe_bis.pos) == 'N':
                                        if fille_externe_bis.deplabel == None:
                                            fille_externe_bis.deplabel = fille_externe.deplabel
                                            fille_externe.remove_dependent(fille_externe_bis)
                                            fille_externe_bis.dependents += fille_externe.dependents
                                            tete_de_file.dependents[tete_de_file.dependents.index(fille_externe)] = fille_externe_bis       
                                        else:
                                            print 'Robustesse à revoir : fonction déjà marquée dans Suppression_semantiques_vides_a'
                                            exit()                                 
                                        trouve = True                          
                    else: 
                        for dependent in fille_externe.dependents:
                            if dependent.word in liste_prepositions and len(dependent.dependents) == 0: 
                                fille_externe.remove_dependent(dependent)
                                    
            for fille in tete_de_file.dependents:
                if fille not in noeuds_marques:
                    noeuds_marques.append(fille)
                    noeuds_file.append(fille)
                 

 # calcul de la position du dernier mot dans l'ordre linéaire de la phrase (LID).
    def LID_max(self):
        noeuds_file = [self]
        noeuds_marques = []
        maximum = -1
        
        while len(noeuds_file) <> 0:
            tete_de_file = noeuds_file.pop(0)
            if tete_de_file.lid > maximum:
                maximum = tete_de_file.lid
            for dependent in tete_de_file.dependents:
                if dependent.lid > maximum:
                    maximum = dependent.lid
            
            for dependent in tete_de_file.dependents:
                if dependent not in noeuds_marques:
                    noeuds_marques.append(dependent)
                    noeuds_file.append(dependent)
                    
        return maximum

# r = DepVertex("dort",3)
# dg = DependencyGraph(r)
# v1 = DepVertex("le",1)
# v2 = DepVertex("chat",2)
# v3 = DepVertex("sur",4)
# v4 = DepVertex("le",5)
# v5 = DepVertex("paillasson",6)
# dg.add_edge(r,"subj",v2)
# dg.add_edge(v2,"det",v1)
# #dg.add_edge(v2,"foo",v4)
# #dg.add_edge(v2,"foo",v5)
# dg.add_edge(r,"mod",v5)
# dg.add_edge(v5,"prep",v3)
# dg.add_edge(v5,"det",v4)
    def DependencyTree2Dgraph(self):
        noeuds_file = []
        noeuds_marques = []
        noeuds_file.append(self)
        v = []
        
        for i in range(0, self.LID_max() + 1):
            v.append(i)
        root = True
        
        while len(noeuds_file) <> 0:
            tete_de_file = noeuds_file.pop(0)
            if root:
                r = DepVertex(self.word, self.lid)
                r.set_feature('pos',self.pos)
                r.set_feature('lemma',self.lemma)
                dg = DependencyGraph()
                # special dependency for the root of the dep tree
                h = DepVertex('null', -1)
                dg.add_edge(h, 'head' ,r)
                for dependent in tete_de_file.dependents:
                    w = DepVertex(dependent.word, dependent.lid)
                    w.set_feature('pos',dependent.pos)
                    w.set_feature('lemma',dependent.lemma)    
                    v[dependent.lid] = w
                    if dependent.deplabel == None:
                        dg.add_edge(r, str(dependent.deplabel), v[dependent.lid])
                    else:
                        dg.add_edge(r, dependent.deplabel, v[dependent.lid])
                        
                for dependent in tete_de_file.dependents:
                    if dependent not in noeuds_marques:
                        noeuds_marques.append(dependent)
                        noeuds_file.append(dependent)
                root = False
            else :
                for dependent in tete_de_file.dependents:
                    w = DepVertex(dependent.word, dependent.lid)
                    w.set_feature('pos',dependent.pos)
                    w.set_feature('lemma',dependent.lemma)    
                    v[dependent.lid] = w
                    if dependent.deplabel == None:
                        dg.add_edge(v[tete_de_file.lid], str(dependent.deplabel), v[dependent.lid])
                    else:
                        dg.add_edge(v[tete_de_file.lid], dependent.deplabel, v[dependent.lid])

                for dependent in tete_de_file.dependents:
                    if dependent not in noeuds_marques:
                        noeuds_marques.append(dependent)
                        noeuds_file.append(dependent)
        return dg

    def Possible_long_distance_relation(self):
       noeuds_file = []
       noeuds_marques = []
       noeuds_file.append(self)
       liste_tete_du_pronoms_relatifs = []
       root = True

       while len(noeuds_file) <> 0:
           tete_de_file = noeuds_file.pop(0)
           if root:
               tete_de_file = (tete_de_file, None)
               root = False
                
           for dependent in tete_de_file.dependents:
               if dependent.deplabel in ['A_OBJ','DE_OBJ','P_OBJ','OBJ','MOD','ATS','ATO'] and tete_de_file.lid > dependent.lid:
                   dependent.deplabel = dependent.deplabel + '?'               
                                         
           for fille in tete_de_file.dependents:
               if fille not in noeuds_marques:
                   noeuds_marques.append(fille)
                   noeuds_file.append(fille)
       
#####################################
# Fonctions utilitaires
#####################################

# marie : 
# LabelledTree (not head-annotated) to DependencyTree
def LabTree_2_DepTree(labtree, head_table, deplabel='head'):
    labtree.head_annotate(head_table)
    return HLabTree_2_DepTree(labtree, deplabel)

# marie :
# LabelledTree (head-annotated) to DependencyTree
def HLabTree_2_DepTree(labtree, deplabel='head'):

    # hack : treenum inconsistently assigned/not assigned to LabelledTree instances
    # input labelledtree is (ahhhh!) with or without treenum
    treenum = 0
    if 'treenum' in labtree.__dict__ and labtree.__dict__['treenum'] <> None:
        treenum = labtree.treenum

    if labtree.is_terminal_sym():
        return DependencyTree(labtree.word, labtree.label, deplabel,
                              treenum, labtree.lid, labtree.lemma)
    headindex = labtree.headindex
    # if head is known
    if headindex <> None:
        headdeptree = HLabTree_2_DepTree(labtree.children[headindex], deplabel)
    else:
        # if head is unknown : for all the lexical heads of the children, the governor is unknown
        # a dummy node is added that will serve as governor for the current subtree
        headdeptree = DependencyTree('MISSINGHEAD','MISSINGHEAD','MISSINGHEAD',treenum)
    # spine construction
    headdeptree.add_to_spine(labtree.label)

    # iteration on all children, even if head unknown
    for i in range(len(labtree.children)):
        # if child is not the head, it corresponds to a dependent
        if i <> headindex:
            child = labtree.children[i]
            deplabel = child.funlabel # possibly None
            # recursively build the childdeptree from child
            dep = HLabTree_2_DepTree(child,deplabel)
            # add this child dep tree to the head dep tree
            headdeptree.add_dependent(dep)
    return headdeptree


# LabelledTree 
# -> Plain DependencyTree according to head rules
# -> Enhanced DependencyTree according to dependency labels heuristics
#def From_const_to_dep(labelledtree, gestion_comp, long_distance_mark):
def LabTree_2_EnhancedDepTree(labelledtree, gestion_comp, long_distance_mark, head_table=None):

    # Build a DependencyTree from a LabelledTree

    # if LabelledTree not yet head_annotated:
    if head_table <> None:
        # according to the sym4_table in the PropagTable file and from the build LabelledTree
        # (Uses the sym4_table imported to find the head, see the head_table global variable below)
        depTree = LabTree_2_DepTree(labelledtree, head_table)
    else:
        depTree = HLabTree_2_DepTree(labelledtree)
        
    # Remplace the treebank function 'XXX' by 'XXX?' if there is a possible long distance relation. Use the LID attribute 
    # from the governor and the dependent : if the dependent is before the governor in the sentence, the question mark is inserted. 
    # Useful if an annotator has to valid the function : a question mark indicates that he has to investigate this relation.
    # Not needed if the Parc files will be tested with pickles : the question mark will make silence (MOD? <> MOD).
    if long_distance_mark:
        depTree.Possible_long_distance_relation()
        
    # Add missing functions according to heuristics
    depTree.hack_coord_vn()
    depTree.Ajout_fonctions_heuristiques()
    # Adjust all the COMP and AUX function
    if gestion_comp:
    	depTree.Gestion_comp()
    depTree.Gestion_aux()

    return depTree

# Transforme une liste d'enfant en n-uplet séparé par un espace.
# Permet d'utiliser une partie droite de régle en clé de dictionnaire
# Used by PropagTable.recensement
def Liste_to_nuplet(liste):
    first = False
    nuplet = ''
    for enfant in liste:
        if not first:
            nuplet += str(enfant.label)
            first = True
        else:
            nuplet += (' ' + enfant.label)
    return nuplet

def Get_sentence_string(nt_nodes):
    sentence = ''
    #nt_nodes = LabelledTree.tree_nt_nodes(tree)
    #print nt_nodes
    for node in nt_nodes:
        for child in node.children:
            if child.is_leaf():
                if child.label == '"':
                    sentence += (' ' + '\\"')
                else:
                    sentence += (' ' + child.label)
    return sentence







    

#####################################
# Variables globales
#####################################

# Utilisé par pour AUX_TPS dans le cas None(V,V)
liste_avoir = ['avoir','ai', 'as', 'a', 'avons', 'avez', 'ont', 'avais', 'avait', 'avions', 'aviez', 'avaient', 
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
                 'entré', 'entrée', 'entrés', 'entrées', 'intervenu', 'intervenue', 'intervenus', 'intervenues',  
                 'mort', 'morte', 'morts', 'mortes', 'né', 'née', 'nés', 'nées', 
                 'parti', 'partie', 'partis', 'parties', 'parvenu', 'parvenue', 'parvenus', 'parvenues',  
                 'apparu', 'apparue', 'apparus', 'apparues', 'apparu', 'apparue', 'apparus', 'apparues',
                 'redevenu', 'redevenue', 'redevenus', 'redevenues', 'reparti', 'repartie', 'repartis', 'reparties' 
                 'resté', 'restée', 'restés', 'restées', 'retombé', 'retombée', 'retombés', 'retombées', 
                 'revenu', 'revenue',  'revenus',  'revenues', 'tombé', 'tombée', 'tombés', 'tombées', 
                 'venu', 'venue',  'venus',  'venues']

liste_A_OBJ_deja_marques_dans_FTB = []
liste_DE_OBJ_deja_marques_dans_FTB = []
liste_P_OBJ_deja_marques_dans_FTB = []
liste_OBJ_deja_marques_dans_FTB = []
liste_ATS_deja_marques_dans_FTB = []
liste_ATO_deja_marques_dans_FTB = []
liste_SUJ_deja_marques_dans_FTB = []
liste_MOD_deja_marques_dans_FTB = []



def tracetree(tree, deptree, outopts):
    if outopts <> None:
        if 'pos' in outopts:
            taglist = tree.pos_yield()
            print print_pos_list_brown(taglist)    
        if 'ctree' in outopts:
            # indented : print tree.pprint()
            # flat ptb output
            print tree.printf()
        #ptb-style output for dependency tree
        if 'ptbdep' in outopts:
            print deptree.tostring('ptb')
#------ Conversion Labelled trees vers DependencyGraph -----
# en passant par DependencyTree, application heurisitques



# input = labelled tree read from xml source files
# applies functional labeling
# applies dependency heuristics
# output = dependencygraph
def srctree2depgraph(tree, ptable, outopts=None):
    tree.remove_traces()
    tree.clitics_downwards()
    tree.merge_num()
    tree.merge_cpds()
        
    deptree = LabTree_2_EnhancedDepTree(tree, gestion_comp = False, 
                                        long_distance_mark = False, 
                                        head_table = ptable)
    tracetree(tree, deptree, outopts)

    depgraph = deptree.DependencyTree2Dgraph()
    # marie : tri des deps selon l'ordre linÃ©aire des dÃ©pendants
    depgraph.sort_dest()
    if outopts <> None and 'dep' in outopts:
        print depgraph.triples2string() 

    return depgraph

# input = labelled tree read from parsed files
# applies functional labeling
# applies dependency heuristics
# output = dependencygraph
def parsedtree2depgraph(tree, ptable, flabeller=None, fixer=None, treenum=0, outopts=None):
    # Remove the dummy root
    # marie : only if necessary (in case of parse-failure, missing dummy)
    if tree.label == '':
        tree = tree.children[0]

    #Functional labelling
    if flabeller <> None:
        # inclut un marquage des tetes
        flabeller.label_tree(tree)
    else:
        tree.head_annotate(ptable)
        tree.clitics_downwards() # todo : sortir clitics_downwards de label_tree

    # modif marie : plus d'apauvrissement brutal du tagset
    # fonctionnement des tables de propag modifié pour que les alias ne modifient pas les assignations de tete
    nodes = LabelledTree.tree_nodes(tree)
    for node in nodes:
        #if node.is_terminal_sym():
        #    node.label = fixer.map(node.label)
        node.set_Treenum(treenum)

    # build a DependencyTree and applies heuristics to enhance it
    deptree = LabTree_2_EnhancedDepTree(tree, gestion_comp = False, 
                                        long_distance_mark = False, 
                                        head_table = None) 
                                        #head_table = ptable) # inutile cf. plus d'apauvrissement
    tracetree(tree, deptree, outopts)

    depgraph = deptree.DependencyTree2Dgraph()
    # marie : tri des deps selon l'ordre linÃ©aire des dÃ©pendants
    depgraph.sort_dest()
    if outopts <> None and 'dep' in outopts:
        print depgraph.triples2string() 

    return depgraph


# input = tree list read from parsed files
# output = treenum / depgraphs tuple list (with functional labeling and heuristics applied)
def parsedtrees2depgraphs(labelledtreelist,ptable,flmethod=None,flpara=None, outopts=None):
    depgraphs = []
    treenum = 0
    fixer = Tagfixer()
    flabeller = None
    if flmethod <> None and flmethod <> 'none':
        #flabeller =  FunctionLabeller(flpara, ['LHS_CAT','DEP_CAT','DEP_WORD','DEP_HEAD_CAT','HEAD_CAT','HEAD_WORD','LEFT_SIBLING_CAT','RIGHT_SIBLING_CAT','COHEAD_CAT','COHEAD_WORD','DEP_YIELD_LEN','HEAD_DIST'], ['list','list','openlist','list','list','openlist','list','list','list','openlist','int','int'],method=flmethod)
        #FRANCOIS
        flabeller =  FunctionLabeller(flpara,build_feature_map())   
    for tree in labelledtreelist:
        treenum += 1
        depgraphs.append( (treenum, 
                           parsedtree2depgraph(tree, ptable, flabeller, fixer, treenum, outopts))
                          )
    return depgraphs
