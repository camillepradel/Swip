#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-
#
# This module reads a french treebank XML File
# and stores it into a list of LabelledTree data structure
#

import sys
import re
import string
import os
from random import *
from LabelledTree import LabelledTree
from tagfixes import *
from xml.dom import minidom
from xml.dom.minidom import parse, parseString

#########################
#CONSTANTS
#Trace symbol
TRACE = "--t--"
#TRACE = ""
#########################

class XmlReader:
    def __init__(self):
        self.treebank = []
	self.filename = '<unknown file>'
	self.treenum = 0
	self.skipped = 0
	self.nb_trees = 0


#
#FRANCOIS
#
# ne pas garder les arbres du ftb, sils apparaissent dans le corpus pivot
# les enonces du corpus pivot sont passes à ces fonctions par l'intermediaire d'un dictionnaire
# ayant pour clé le nom du fichier et pour liste de valeurs, les numeros de phrase (SENT)
#

	# read_xml_nopivot ( self , flux , liste d'identifiants de phrase , nom de fichier )
	#	parse les arbres ftb contenus dans le flux
	#	mais renvoie uniquement ceux qui n'apparaissent pas dans la liste
    def read_xml_nopivot(self,instream,liste_pivot,file):
        treelist = []
        dom = parse(instream)
        sentlist = dom.getElementsByTagName('SENT')
        self.nb_trees += len(sentlist)
        nb_omis = 0
        for sent in sentlist:
			if sent.attributes.has_key('nb'):
				self.treenum =  sent.attributes['nb'].value
			else:
				pass
			tree = self.readTree(sent)
			if not tree.treenum in liste_pivot:
				tree.file=file
				treelist.append(tree)
			else:
				self.skipped = self.skipped + 1
				nb_omis = nb_omis +1
				pass
        if nb_omis > 0:
        	print "\t>>> nombre d'arbres omis: "+str(nb_omis)
        return treelist

	# read_dir_xml_nopivot( self , dossier , dictionnaire {id_xml -> [id_sent] } )
	#	pour chaque fichier du dossier, applique la fonction read_xml_pivot
	#	avec pour liste, la valeur associée au fichier dans le dictionnaire
    def read_dir_xml_nopivot(self,dirname,dico_pivot):
        files = os.listdir(dirname)
        treebank = []
        liste_assfile=[]
        nb_file=1
        nb_files=0
        for file in files:
            if re.match("^.*\.xml$",file):
            	nb_files = nb_files + 1
        
        for file in files:
            liste_assfile=[]
            if re.match("^.*\.xml$",file):
				sys.stderr.write("["+str(nb_file)+"/"+str(nb_files)+"] Parsing du fichier: "+file+'\n')
				nb_file=nb_file + 1
				self.filename = file
				instream = open(os.path.join(dirname,file))
				if dico_pivot.has_key(file):
					liste_assfile = dico_pivot[file]
				tliste = self.read_xml_nopivot(instream,liste_assfile,file)
				if len(tliste) > 0:
					treebank  = treebank + tliste
				instream.close()
        return treebank
        
#
#(fin) FRANCOIS
#       
    def read_xml(self,instream):
        treelist = []
        dom = parse(instream)
        sentlist = dom.getElementsByTagName('SENT')
        for sent in sentlist:
	    if sent.attributes.has_key('nb'):
		self.treenum =  sent.attributes['nb'].value
	    else:
		pass
	    tree = self.readTree(sent)
            treelist.append(tree)
        return treelist

    #Reads all the xml files in a directory
    def read_dir_xml(self,dirname):
        files = os.listdir(dirname)
        treebank = []
        for file in files:
            if re.match("^.*\.xml$",file):
                sys.stderr.write("Parsing input file "+file+'\n')
		self.filename = file
                instream = open(os.path.join(dirname,file))
                treebank  = treebank + self.read_xml(instream)
                instream.close()
        return treebank
    
    #Returns a training, a test and a gold standard corpus for evaluation purpose
    #The ratio given as optional parameter defines the proportion of the test/gold corpora wrt total size of the available data
    #The function returns a tuple (training, test, gold)
    def build_eval_data(self,treebank,ratio=0.10):
        length = len(treebank)
        size = float(length) * float(ratio)
        shuffle(treebank)
        test = treebank[0:int(size)]
        devtest = treebank[int(size):int(size)*2]
        train = treebank[int(size)*2:]
        #A bit dangerous : you need to clone that stuff at some point don't you ? (not for now since the printin functions do not alter the data)
        return (train,devtest,test)

 #Returns a training, a test and a gold standard corpus for evaluation purpose
 #The ratio given as optional parameter defines the proportion of the test/gold corpora wrt total size of the available data
    #The function returns a tuple (training, test, gold)
    def build_std_eval_data(self,treebank,ratio=0.10):
        length = len(treebank)
        size = float(length) * float(ratio)
        test = treebank[0:int(size)]
        devtest = treebank[int(size):int(size)*2]
        train = treebank[int(size)*2:]
        #A bit dangerous : you need to clone that stuff at some point don't you ? (not for now since the printin functions do not alter the data)
        return (train,devtest,test)

    def build_xfold_eval_data(self,treebank,x):
        length = len(treebank)
        size = float(length) / float(x)
        intsize = int(size)
        folds = []
        for i in range(x):
            folds.append(treebank[i*intsize:(i+1)*intsize])
        return folds

 #Function responsible for building syntactic trees
    def readTree(self,root,num=-1):
	rootc = self.doNode(root)
	if self.is_trace(root) :#Adding explicit traces
	    rootc.add_child(LabelledTree(TRACE))
	elif self.is_partitive(root):
	    self.do_partitive(rootc,root)
	elif root.hasChildNodes():
	    for node in root.childNodes:
		if node.nodeType == node.ELEMENT_NODE:
		    # AJOUT mhc : sometimes compound attribute is missing -> catint is another clue
		    if node.attributes.has_key('catint') and not rootc.is_compound():
			rootc.set_compound_true()
			sys.stderr.write("Missing compound attribute!\n")
		    rootc.add_child(self.readTree(node))
		elif node.nodeType == node.TEXT_NODE and not re.match('^\s*$',node.nodeValue):
		    match = re.match('^\s*(\S+)\s*$',node.nodeValue)
		    if match <> None:
			# marie: ?? A VOIR pb encodage different des attr. (lemma) et de la forme #
                        rootc.add_child(LabelledTree(match.group(1)))
		    else:
			match = re.match('^\s*((\S|[0-9]\s)+)\s*$',node.nodeValue)#Fixes a weirdness in Natalie's encoding
			if match <> None:
			    num = match.group(1)
			    num = re.sub('\s','_',num)
			    rootc.add_child(LabelledTree(num))
			else:
			    sys.stderr.write('Error while reading node (empty node) '+node.nodeValue)
	return rootc



 #    #Function responsible for building syntactic trees
#     def readTree(self,root):
#         rootc = self.doNode(root)
#         if self.is_trace(root) :#Adding explicit traces
#             rootc.add_child(LabelledTree(TRACE))
#         elif self.is_partitive(root):
#             self.do_partitive(rootc,root)
#         elif root.hasChildNodes():
#             for node in root.childNodes:
#                 if node.nodeType == node.ELEMENT_NODE:
#                     rootc.add_child(self.readTree(node))
#                 elif node.nodeType == node.TEXT_NODE and not re.match('^\s*$',node.nodeValue):
#                     match = re.match('^\s*(\S+)\s*$',node.nodeValue)
#                     if match <> None:
# 			#print match.group(1).encode("iso-8859-1")
#                         rootc.add_child(LabelledTree(match.group(1)))
#                     else:
#                         sys.stderr.write('Error while reading node (empty node)')
#         return rootc

    def normalise_wsp(self, label):
        m = re.match('^(\s*)(\S+)(\s*)$',label)
        if m <> None:
            return m.group(2)
        else:
            return label

    #Function responsible for grabbing all the relevant information in the xml for building a node
    def doNode(self,xmlnode):
        node = None
        if xmlnode.nodeName == 'w':                          # extracting the categories of words (terminals)
            if xmlnode.attributes.has_key('cat'):
                node = LabelledTree(self.normalise_wsp(xmlnode.attributes['cat'].value))    
                if xmlnode.attributes.has_key('compound'):
                    node.set_compound_true()
            elif xmlnode.attributes.has_key('catint'):
                 node = LabelledTree(self.normalise_wsp(xmlnode.attributes['catint'].value))
                 node.set_compound_true()
            if  xmlnode.attributes.has_key('subcat'):
                val = self.normalise_wsp(xmlnode.attributes['subcat'].value)
                if val <> '':
                     node.set_feature(val)
            # AJOUT marie : lemma useful!
            if  xmlnode.attributes.has_key('lemma'):
                #val = self.normalise_wsp(unicode(xmlnode.attributes['lemma'].value))
                val = self.normalise_wsp(xmlnode.attributes['lemma'].value)
                if val <> '':
                    node.set_lemma(val)
            node = self.do_morphology(xmlnode,node)
        else:                                                 #default non terminal nodes
             node = LabelledTree(xmlnode.nodeName)
             if xmlnode.attributes.has_key('fct'):
                 # marie : '-' systematically turned into '_' in functional tags
                 #node.set_function(self.normalise_wsp(xmlnode.attributes['fct'].value))
                 # marie : correction of a few erroneous functional tag form : case error, and missing _
                 fun = re.sub('-','_',xmlnode.attributes['fct'].value)
                 fun = string.upper(fun)
                 fun = re.sub('DEOBJ','DE_OBJ',fun)
                 fun = re.sub('AOBJ','A_OBJ',fun)
                 #node.set_function(self.normalise_wsp(re.sub('-','_',xmlnode.attributes['fct'].value)))
                 node.set_function(fun)
        # mathieu : mark the SENTnb in the treenum attribute for each node
        node.set_Treenum(self.treenum)         
        # specific to MFT
        if xmlnode.attributes.has_key('type'):
            node.type = xmlnode.attributes['type'].value

        return node

    #Handles morphology
    def do_morphology(self,xmlnode, node):
	if xmlnode.attributes.has_key('cat') and xmlnode.attributes['cat'].value == 'V':
	    mph = self.normalise_wsp(xmlnode.attributes['mph'].value)
	    if len(mph) == 1 :
		if mph == 'W':
		    node.set_Vmorphology('infinitif','*','*','*')
		elif mph == 'G':
		    node.set_Vmorphology('participe','present','*','*')
		else:
		    sys.stderr.write("Error badly converted morphology...("+mph+')')
	    else:
		moodcode=mph[0]
		pers=mph[1]
		num=mph[2]
		if moodcode == 'K':
		    gen = pers
		    #TODO
		    node.set_VPPmorphology('participe','passe',num,gen)
		elif moodcode == 'P':
		    node.set_Vmorphology('indicatif','present',pers,num)
		elif moodcode == 'I':
		    node.set_Vmorphology('indicatif','imparfait',pers,num)
		elif moodcode == 'J':
		    node.set_Vmorphology('indicatif','passe-simple',pers,num)
		elif moodcode == 'F':
		    node.set_Vmorphology('indicatif','futur',pers,num)
		elif moodcode == 'T':
		    node.set_Vmorphology('subjonctif','imparfait',pers,num)
		elif moodcode == 'C':
		    node.set_Vmorphology('indicatif','conditionnel',pers,num)
		elif moodcode == 'S':
		    node.set_Vmorphology('subjonctif','present',pers,num)
		elif moodcode == 'Y':
		    node.set_Vmorphology('imperatif','present',pers,num)
		else:
		    sys.stderr.write("Error badly converted morphology...("+mph+')')
	#Add morphology for other categories
	elif xmlnode.attributes.has_key('cat') and (xmlnode.attributes['cat'].value == 'A' or xmlnode.attributes['cat'].value == 'N' or xmlnode.attributes['cat'].value == 'D' or xmlnode.attributes['cat'].value == 'PRO' or xmlnode.attributes['cat'].value == 'CL'):
	    pers = '*'
	    gen = '*'
	    num = '*'
# marie : pb : il existe cas ou pas de mph
            if xmlnode.attributes.has_key('mph'): 
                mph = self.normalise_wsp(xmlnode.attributes['mph'].value)
                for c in mph:
                    if c in ['m','f']:
                        gen = c
                    elif c in ['s','p']:
                        num = c
                    elif c in ['1','2','3']:
                        pers = c
	    node.set_non_Vmorphology(gen,num,pers)
	return node
	
    #Special routines for building partitive nodes in the French Treebank (does a sound segmentation)
    # partitives are : du ; de la ; de l' ; de; d'
    def do_partitive(self,node,xmlnode):
        node.set_compound_true()
        val = ''
        if xmlnode.hasChildNodes():
            for xmlchild in xmlnode.childNodes:
                if xmlchild.nodeType == xmlchild.TEXT_NODE and not re.match('^\s*$',xmlchild.nodeValue):
                    val = (xmlchild.nodeValue)       
            match = re.match("^\s*([Dd]e)\s+(l(a|'))\s*$",val)
            if match <> None:
                childone = LabelledTree("P")
                childone.set_compound_true()
                childtwo = LabelledTree("D")
                childtwo.set_feature("def")
                childtwo.set_compound_true()
                grandchildone = LabelledTree(match.group(1))
                grandchildtwo = LabelledTree(match.group(2))
                node.add_child(childone)
                node.add_child(childtwo)
                childone.add_child(grandchildone)
                childtwo.add_child(grandchildtwo)
            else:
                match = re.match("^\s*([Dd]([eu]|'))\s*$",val)
                if match <> None :
                    childone = LabelledTree(match.group(1))
                    node.add_child(childone)
                else: 
                    sys.stderr.write("error while reading partitive(" +val+")\n")
           

    #Detects if a node is a partitive (apparently nodes of form <w cat = "D" ... subcat = "part">...</w>)
    def is_partitive(self,xmlnode):
        if xmlnode.nodeName == 'w':
            if xmlnode.attributes.has_key('cat') and xmlnode.attributes['cat'].value == 'D' and xmlnode.attributes.has_key('subcat')  and xmlnode.attributes['subcat'].value == 'part':
                return True
        return False

    #Detects if a node is a trace (apparently nodes of the form <w cat = "D" [...] ></w> and of the form <w cat = "P" [...] ></w>)
    def is_trace(self,xmlnode):
        if xmlnode.nodeName == 'w':
            if (xmlnode.attributes.has_key('cat')) and (xmlnode.attributes['cat'].value == 'P' or xmlnode.attributes['cat'].value == 'D') and (not xmlnode.hasChildNodes() or self.has_whitespace_children_only(xmlnode)) :
                return True
            elif (xmlnode.attributes.has_key('catint')) and (xmlnode.attributes['catint'].value == 'P' or xmlnode.attributes['catint'].value == 'D') and (not xmlnode.hasChildNodes() or self.has_whitespace_children_only(xmlnode)) :
                return True
        return False

    def has_whitespace_children_only(self,xmlnode):
        if xmlnode.hasChildNodes():
            for child in xmlnode.childNodes:
                if child.nodeType == child.TEXT_NODE:
                    if not re.match("^\s*$",child.nodeValue):
                        return False                    
                else:
                    return False
        else:
            return False
        return True

#reader = XmlReader()
#treebank = reader.read_xml(sys.stdin)
#tf = get_tagset2_fixer()

#for tree in treebank:
 #   print '*******************************'
 #   print tree.pprint().encode('iso-8859-1')
#    print "-------------------------------"
#    tree.remove_traces()
#    tree.merge_num()
#    tree.merge_cpds()
#     tree = tree.sbar_transform()
#     tree.infer_cpd_subcats()
#    tree.tagset2_terminals(tf)
#    tree.propagate_mood()
  #  print tree.pprint().encode('iso-8859-1')

#Tests
#sys.setdefaultencoding('iso-8859-1') 
#reader = XmlReader()
#treebank = reader.read_xml(sys.stdin)
#for tree in treebank:
#     print '*******************************'
     #print tree.pprint().encode('iso-8859-1')
#     print "-------------------------------"
#     tree.remove_traces()
#     print tree.pprint().encode('iso-8859-1')
#    # poslist = tree.pos_yield()
# i = 0
#     #for i in range(0,len(poslist)-1,2):
#     #    print (poslist[i].print_node()+'\t'+poslist[i+1].print_node()).encode('iso-8859-1')

#tlist = []
#for tree in treebank :
#    gtree = nltk.bracket_parse(tree.pprint().encode('iso-8859-1'))
#    tlist.append(gtree)
#draw_trees(tlist)
#print treebank[0].printf().encode('iso-8859-1')
#tree1 = nltk.bracket_parse('(SENT (PONCT  ") (NP-SUJ (D  Le) (N  texte)) (VN-A_OBJ (CL  me) (V  parait)) (AP-ATS (ADV  assez) (A  mauvais)) (ADV (P  dans) (D  l) (N  ensemble)) (PONCT  .))')
#tree2 = nltk.bracket_parse('(SENT (NP-SUJ (N (A Lyonnaise) (PONCT -) (N Dumez))) (VN (V vient)) (VPinf-DE_OBJ (P d) (VN (V hispaniser)) (NP-OBJ (D sa) (N filiale) (AP (A espagnole))) (COORD (C et) (VPinf (P d) (VN (V étendre)) (NP-OBJ (D ses) (N participations)) (PP-MOD (P en) (NP (N Espagne)))))) (PONCT ,) (VPpart-MOD (ADV tout) (P en) (VN (V resserrant)) (NP-OBJ (D ses) (N liens) (PP (P avec) (NP (D la) (N Caixa) (PONCT ,) (NP (A première) (N (N caisse) (P d) (N épargne)) (AP (A espagnole)) (COORD (C et) (NP (D l) (PRO un) (PP (P des) (NP (D --t--) (A premiers) (N établissements) (AP (A financiers)) (PP (P de) (NP (D la) (N péninsule) (AP (A ibérique))))))))) (PONCT ,) (Srel (PP-A_OBJ (P à) (NP (PRO laquelle))) (VN-SUJ (CL elle) (V est) (V liée)) (PP-MOD (P depuis) (ADV longtemps)) (PP-MOD (P dans) (NP (D la) (N Société) (AP (A générale)) (PP (P des) (NP (D --t--) (N eaux))) (PP (P de) (NP (N Barcelone))) (PONCT --LBR--) (NP (N SGAB)) (PONCT --RBR--) (PONCT ,) (NP (A premier) (N groupe) (AP (A espagnol)) (PP (P de) (NP (N services)))))))))) (PONCT --LBR--) (Sint-MOD (NP-SUJ (D la) (N Caixa)) (VN (V détient)) (ADV aussi) (NP-OBJ (D 2) (N %) (PP (P du) (NP (D --t--) (N capital) (PP (P de) (NP (N (A Lyonnaise) (PONCT -) (N Dumez)))))))) (PONCT --RBR--)) (PONCT .))')
#tree3 = nltk.bracket_parse(treebank[2].printf().encode('iso-8859-1'))
#print tree1
#ttrees = (tree1,tree2)
#draw_trees(tree1,tree2)
#tree1.draw()
#tree2.draw()

