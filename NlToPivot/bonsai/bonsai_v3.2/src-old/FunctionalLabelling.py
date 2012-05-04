#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import sys
from PennTreeBankReader import *
import pickle
from dgraph import *
#from FTree import *
#from bayes import *
#from dtree import *
from fsvm import *
from PropagTable import *

############################################
# Auxiliary functions for annotating trees #
############################################
# Designed for symset_4  #
##########################

copula_wordset = set(['parais','paraissaient','paraissais','paraissait','paraissant','paraisse','paraissent','paraisses','paraissez','paraissiez','paraissions','paraissons','paraît','paraîtra','paraîtrai','paraîtraient','paraîtrais','paraîtrait','paraîtras','paraître','paraîtrez','paraîtriez','paraîtrions','paraîtrons','paraîtront','paru','parue','parues','parurent','parus','parusse','parussent','parusses','parussiez','parussions','parut','parûmes','parût','parûtes','apparais','apparaissaient','apparaissais','apparaissait','apparaissant','apparaisse','apparaissent','apparaisses','apparaissez','apparaissiez','apparaissions','apparaissons','apparaît','apparaîtra','apparaîtrai','apparaîtraient','apparaîtrais','apparaîtrait','apparaîtras','apparaître','apparaîtrez','apparaîtriez','apparaîtrions','apparaîtrons','apparaîtront','apparu','apparue','apparues','apparurent','apparus','apparusse','apparussent','apparusses','apparussiez','apparussions','apparut','apparûmes','apparût','apparûtes','es','est','furent','fus','fusse','fussent','fusses','fussiez','fussions','fut','fûmes','fût','fûtes','sera','serai','seraient','serais','serait','seras','serez','seriez','serions','serons','seront','soient','sois','soit','sommes','sont','soyez','soyons','suis','étaient','étais','était','étant','étiez','étions','été','êtes','être','resta','restai','restaient','restais','restait','restant','restas','restasse','restassent','restasses','restassiez','restassions','reste','restent','rester','restera','resterai','resteraient','resterais','resterait','resteras','resterez','resteriez','resterions','resterons','resteront','restes','restez','restiez','restions','restons','restâmes','restât','restâtes','restèrent','resté','restée','restées','restés','demeura','demeurai','demeuraient','demeurais','demeurait','demeurant','demeuras','demeurasse','demeurassent','demeurasses','demeurassiez','demeurassions','demeure','demeurent','demeurer','demeurera','demeurerai','demeureraient','demeurerais','demeurerait','demeureras','demeurerez','demeureriez','demeurerions','demeurerons','demeureront','demeures','demeurez','demeuriez','demeurions','demeurons','demeurâmes','demeurât','demeurâtes','demeurèrent','demeuré','demeurée','demeurées','demeurés','sembla','semblai','semblaient','semblais','semblait','semblant','semblas','semblasse','semblassent','semblasses','semblassiez','semblassions','semble','semblent','sembler','semblera','semblerai','sembleraient','semblerais','semblerait','sembleras','semblerez','sembleriez','semblerions','semblerons','sembleront','sembles','semblez','sembliez','semblions','semblons','semblâmes','semblât','semblâtes','semblèrent','semblé','semblée','semblées','semblés','devenaient','devenais','devenait','devenant','devenez','deveniez','devenions','devenir','devenons','devenu','devenue','devenues','devenus','deviendra','deviendrai','deviendraient','deviendrais','deviendrait','deviendras','deviendrez','deviendriez','deviendrions','deviendrons','deviendront','devienne','deviennent','deviennes','deviens','devient','devinrent','devins','devinsse','devinssent','devinsses','devinssiez','devinssions','devint','devînmes','devînt','devîntes','redevenaient','redevenais','redevenait','redevenant','redevenez','redeveniez','redevenions','redevenir','redevenons','redevenu','redevenue','redevenues','redevenus','redeviendra','redeviendrai','redeviendraient','redeviendrais','redeviendrait','redeviendras','redeviendrez','redeviendriez','redeviendrions','redeviendrons','redeviendront','redevienne','redeviennent','redeviennes','redeviens','redevient','redevinrent','redevins','redevinsse','redevinssent','redevinsses','redevinssiez','redevinssions','redevint','redevînmes','redevînt','redevîntes'])



def decorate_tree(tree):
    #tree.remove_functions()
    ptable = sym4_table()
    tree.clitics_downwards()
    tree.head_annotate(ptable)
    tree.ylength_annotate()
    tree.vcls_annotate()
    tree.int_mark_annotate()
    tree.wh_annotate()
    tree.rel_annotate()
    tree.cohead_annotate()
    tree.conjsub_annotate()
    tree.passive_annotate()
    tree.mood_annotate()
    tree.copula_annotate(copula_wordset)
    #tree.fine_types_annotate()
    #tree.head_annotateII(ptable)
    #tree.head_propagation()
    #tree.annotate_yield()
    #print tree.printf()
    
def find_head(tree):
    default_head = None
    for child in tree.children:
        if child.head:
            return child
        else:
            default_head = child
    return default_head

def find_head_preterminal(tree):
    if tree.is_terminal_sym():
        return tree
    else:
        head = find_head(tree)
        while not head.is_terminal_sym():
            head = find_head(head)
        return head
        

#Experiments for dealing with amalgamates : non-conclusive
def yield_simplify_cat(self, incat):
    if incat == 'P+D':
        return 'P'
    return incat

def yield_annotate_wh(self,incat):
    if  incat in ["DETWH","PROWH","ADVWH","ADJWH"]:
        return "+"
    elif incat == "PROREL":
        return "rel"
    return "-"

def yield_simplify_word(self,inword):
    if inword == 'au':
        return 'à'
    if inword == 'aux':
        return 'à'
    if inword == 'du':
        return 'de'
    return inword
    
def yield_length(node):
	length=0
	if node.children != None:
		for child in node.children:
			length += yield_length(child)
		return length
	else:
		return 1

#######################################################################################################
# This class is responsible for grabbing the data in the treebank, saving it and building classifiers #
#######################################################################################################

class FunctionLearner:

    def __init__(self,data_file,featurelist,featuretypes):
        self.data_file = data_file
        self.featurelist = featurelist
        self.featuretypes = featuretypes
        
    def learn_bayes_model(self,treebank):
        self.data_table = QuinlanDataTable(self.data_file,self.featurelist,self.featuretypes)
        self.train_labeller_treebank(treebank)
        self.data_table.generate_data_all()
        learner = BayesLearner(self.data_file)
        learner.save_model()

    def learn_tree_model(self,treebank):
        self.data_table = QuinlanDataTable(self.data_file,self.featurelist,self.featuretypes)
        self.train_labeller_treebank(treebank)
        self.data_table.generate_data_all(True)
        learner = TreeLearner(self.data_file)

    def learn_joint_model(self,treebank):
        self.data_table = QuinlanDataTable(self.data_file,self.featurelist,self.featuretypes)
        stemmer = Stemmer()
        for tree in treebank:
            decorate_tree(tree)
            #print tree.pprint(feats = {'funlabel':{}})
            self.generate_joint_parameters(tree,stemmer)
        self.data_table.generate_data_all(True)
        #learner = TreeLearner(self.data_file)

    def learn_svm_model(self,treebank,grid=False,log2c=7,log2g=-9):
        self.data_table = QuinlanDataTable(self.data_file,self.featurelist,self.featuretypes)
        self.train_labeller_treebank(treebank)
        #datatable = self.data_table.generate_data_all(True)
        datatable = self.data_table.generate_binary_data()
        learner = SVMLearner(self.data_file,datatable,grid,log2c,log2g)
        
#    def learn_svm_model(self,treebank):
#        self.data_table = QuinlanDataTable(self.data_file,self.featurelist,self.featuretypes)
#        self.train_labeller_treebank(treebank)
#        datatable = self.data_table.generate_binary_data()
#        learner = SVMLearner(self.data_file,datatable)

    def train_labeller_treebank(self,treebank):
        for tree in treebank:
            #print tree.pprint()#.encode('latin1')
            decorate_tree(tree)
            # self.train_labeller_tree(tree)
        sys.exit()
        
    def train_labeller_tree(self,tree):
        if not tree.is_terminal_sym():
            for child in tree.children:
                if not child.head:
                    if tree.label in ["SENT","Sinf","Ssub","Sint","Srel","VN","VPinf","VPpart"] or find_head(tree) == "VN":
                        feats = grab_features(tree,child,self.data_table.colnames)
                        if child.funlabel == None:
                            self.data_table.add_data_line(feats,"None")
                        else:
                            self.data_table.add_data_line(feats,child.funlabel)
                self.train_labeller_tree(child)


    #Added feature generation for joint model experimentation
    def generate_joint_parameters(self,tree,stemmer):
        if not tree.is_terminal_sym():
            has_emitted = False
            for child in tree.children:
                if not child.head:
                    if tree.label in ["SENT","Sinf","Ssub","Sint","Srel","VN","VPinf","VPpart"] or find_head(tree) == "VN":
                        feats = grab_features(tree,child,self.data_table.colnames)
                        if child.funlabel == None:
                            self.data_table.add_data_line(feats,"None")
                            print ",".join(self.generate_join_values(feats,stemmer))
                        else:
                            self.data_table.add_data_line(feats,child.funlabel)
                            print ",".join(self.generate_join_values(feats,stemmer))
                        has_emitted = True
                if child.label == "VN":
                    for grandchild in child.children:
                        if not grandchild.head:
                            feats = grab_features(child,grandchild,self.data_table.colnames)
                            if grandchild.funlabel == None:
                                self.data_table.add_data_line(feats,"None")
                                print ",".join(self.generate_join_values(feats,stemmer))
                            else:
                                self.data_table.add_data_line(feats,grandchild.funlabel)
                                print ",".join(self.generate_join_values(feats,stemmer))
                            has_emitted = True
            if has_emitted:
                print(" ")
            for child in tree.children:
                if child.label <> "VN":
                    self.generate_joint_parameters(child,stemmer)
        
    def generate_join_values(self,vallist,stemmer):
        r = []
        for i in range(len(self.data_table.coltypes)):
            if self.data_table.coltypes[i] == "openlist":
                r.append(stemmer.stem(vallist[i]))
            else:
                r.append(vallist[i])
        r.append(vallist[len(vallist)-1])
        return r

def grab_features(lhs,node,featlist):
    vallist = []
    head = find_head(lhs)
    for feat in featlist:
        if feat == 'LHS_CAT':
            vallist.append(lhs.label)
        elif feat == 'LHS_WORD':
            if lhs.word == None:
                vallist.append("<UNK>")
            else:
                vallist.append(lhs.word)
        elif feat == 'DEP_CAT' or feat == 'CAT':
			vallist.append(node.label)
        elif feat == 'DEP_HEAD_CAT':
            headpt = find_head_preterminal(node)
            if headpt <> None:
                vallist.append(headpt.label)
            else:
                vallist.append('<NONE>')                
        elif feat == 'DEP_WORD' or feat == 'WORD':
            if node.word == None:
                vallist.append("<UNK>")
            else:
                vallist.append(node.word)
        elif feat == 'LEFT_SIBLING_CAT':
            left = node.left_sibling(lhs)
            if left <> None:
                vallist.append(left.label)
            else:
                vallist.append('<STOP>')
        elif feat == 'RIGHT_SIBLING_CAT':
            right = node.right_sibling(lhs)
            if right <> None:
                vallist.append(right.label)
            else:
                vallist.append('<STOP>')
        elif feat == 'HEAD_CAT':
            headnode = find_head_preterminal(head)
            if headnode.label <> None:
                vallist.append(headnode.label)
            else:
                vallist.append(head.label)
        elif feat == 'HEAD_WORD':
            if head.word == None:
                vallist.append("<UNK>")
            else:
                vallist.append(head.word)
        elif feat =='DEP_YIELD_LEN':
            vallist.append(str(len(node.tree_yield())))
        elif feat == 'HEAD_DIST':
            vallist.append(str(node.lid - head.lid))
       #meme chose que dep-yield-len:
        elif feat == 'YLENGTH':
        	vallist.append(str(node.ylength))
        elif feat == 'COHEAD_CAT':
        	if node.cohead_cat == None:
        		vallist.append("<NONE>")
        	else:
        		vallist.append(str(node.cohead_cat))
        elif feat == 'COHEAD_WORD':
        	if node.cohead_word == None:
        		vallist.append("<UNK>")
        	else:
        		vallist.append(str(node.cohead_word))
       	elif feat == 'CSUB':
       		if node.csub == None:
       			vallist.append("<NONE>")
       		else:
       			vallist.append(str(node.csub))
       	elif feat == 'PASSIVE':
       		if head.passive == None:
       			vallist.append("<NONE>")
       		else:
       			vallist.append(str(head.passive))
       	elif feat == 'INT_MARK':
       		vallist.append(str(lhs.int_mark))
       	elif feat == 'REL':
       		vallist.append(str(lhs.rel))
       	elif feat == 'WH':
       		vallist.append(str(lhs.wh))
       	elif feat == 'VCLS':
       		vallist.append(str(head.vcls))
       	elif feat == 'MOOD':
       		if head.mood == None:
       			vallist.append("<NONE>")
       		else:
       			vallist.append(str(head.mood))
        elif feat == 'COP': #COPULE (for detecting ATS functions)
                vallist.append(head.cop)
        else:
            sys.stderr.write('Fatal error (!) : undefined feature -> '+feat+'\n')
            sys.exit(1)
    #print str(vallist)
    return vallist

class QuinlanDataTable:

    #Manages C.4.5 like data tables with enhanced functionalities for NL features
    #coltypes are one of int(eger), list, openlist
    #coltypes and colnames must have the same length 
    def __init__(self,datafile, colnames,coltypes):
        self.colnames = colnames
        self.coltypes = coltypes
        self.datalines = []
        self.stemmer = Stemmer()
        self.data_file = datafile

    def add_data_line(self,features,type):
        features.append(type)
        self.datalines.append(features)

    def skim_openlists(self):
        for i in range(len(self.coltypes)):
            if (self.coltypes[i] == 'openlist'):
                wordlist = []
                for line in self.datalines:
                    line[i] = self.stemmer.stem(line[i])
                    wordlist.append(line[i])
                lexicon = self.build_lexicon(wordlist)
                for line in self.datalines:
                    if not line[i] in lexicon:
                        line[i] = self.stemmer.hapaxify(line[i])

    #Gets an high frequency word list (without hapaxes and near hapaxes)
    def build_lexicon(self,wordlist,threshold=2):
        wordlist.sort()
        skim_wl = []
        prev = wordlist[0]
        count = 1
        for word in wordlist[1:]:
            if word == prev:
                count += 1
            else:
                if count >= threshold:
                    skim_wl.append(prev)
                count = 1
            prev = word
        if count >= threshold:
            skim_wl.append(prev)
        return skim_wl

    #Returns the lists of possible values for each attribute
    def lexiconlist(self):
        lexlist = []
        for i in range(len(self.colnames)-1):
            lex = self.valset(i)
            lexlist.append(lex)
        return lexlist

    #Returns the set of values stored in a column of the table
    def valset(self,colidx):
        vals = set([])
        for line in self.datalines:
            vals.add(line[colidx])
        return vals

    #Generates c45's name file
    #The param 'c45' will manage number lists in such a way that it maximises c45 performances
    def generate_names(self, c45=False):
        out = open(self.data_file+".names","w")
        classes = self.valset(len(self.colnames))
        out.write(",".join(classes)+".\n\n")
        for i in range(len(self.colnames)):
            if self.coltypes[i] == 'int' and c45:
                out.write(self.colnames[i]+' : continuous.\n')
            else:
                vals = self.valset(i)
                out.write(self.colnames[i]+' : '+','.join(vals)+'.\n')
        out.close()

    #Generates c45's data file in quinlan's format
    def generate_learning_data(self):
        out = open(self.data_file+".data","w")
        for line in self.datalines:
            out.write(",".join(line)+"\n")
        out.close()
    
    #Dumps the dictionaries
    def generate_dictionaries(self):
        f = open(self.data_file+".dict","w")
        pickle.dump(tuple(self.lexiconlist()),f)
        f.close()

    #Saves the whole stuff (Warning files may be Huge...)
    def generate_data_all(self,c45=False):
        self.skim_openlists()
        self.generate_learning_data()
        self.generate_names(c45)
        self.generate_dictionaries()

    #Saves the whole stuff (Warning files may be Huge...)
    def generate_binary_data(self):
        self.skim_openlists()
        (datatable,int2cl,str2bin_dics) = self.generate_svm_data()
        self.write_svm_data(datatable,int2cl,str2bin_dics)
        self.generate_names(False)
        self.generate_dictionaries()
        return datatable
        
    def generate_svm_data(self):
        #1) Code the whole stuff on integers
        classes = self.valset(len(self.colnames)) 
        (cl2int,int2cl) = classes2int(classes)
        str2bin_dics = []
        for i in range(len(self.colnames)):
            if (self.coltypes[i] == 'list' or self.coltypes[i] == 'openlist'):
                vals = self.valset(i)
                str2bin_dics.append(binarize_set(list(vals)))
            else:
                str2bin_dics.append({})
        #2) Generate data table
        datatable = []
        for line in self.datalines:
            row = []
            row.append(cl2int[line[len(self.colnames)]])
            for idx in range(len(self.coltypes)):
                if self.coltypes[idx] == 'int':
                    #out.write(' '+str(idx)+':'+str(line[j]))
                    row.append(line[idx])
                else:
                    binvec = str2bin_dics[idx][line[idx]]
                    row = row + binvec
            datatable.append(row)
        return (datatable, int2cl,str2bin_dics)
                 
    def write_svm_data(self,datatable,int2cl,str2bin_dics):
        out = open(self.data_file+".data","w")
        for row in datatable:
            out.write(str(row[0]))
            for idx in range(1,len(row)):
                out.write(' '+str(idx)+':'+str(row[idx]))
            out.write('\n')
        out.close()
        binout = open(self.data_file+".bin.svm","w")
        pickle.dump((int2cl,str2bin_dics),binout)
        binout.close()

class FunctionLabeller:
    def __init__(self,datapath,featnames,feattypes,method='svm'):
        self.diclist = self.load_dict(datapath)
        self.method = method
        if self.method == 'svm':
            (self.int2cl,self.str2bin_dic) = self.loadBinaryRsrc(datapath)
            self.classifier = SVMClassifier(datapath)
        elif self.method == 'tree':
            self.classifier = TreeClassifier(datapath)
        else:
            sys.stderr.write('Fatal error in Labelling : unknown method...\n')
        self.stemmer = Stemmer()
        self.featnames = featnames
        self.feattypes = feattypes

    def load_dict(self,datapath):
        f = open(datapath+".dict")
        diclist = pickle.load(f)
        f.close()
        return diclist

    def loadBinaryRsrc(self,datapath):
        binin = open(datapath+".bin.svm")
        (int2cl,str2bin_dics) = pickle.load(binin)
        binin.close()
        return (int2cl,str2bin_dics)

    def label_treebank(self,treebank):
        for tree in treebank:
            self.label_tree(tree)
#            print tree.pprint(feats ={'head':{},'funlabel':{},'cohead':{},'word':{}})#,'word':{}})#".encode('iso-8859-1')


    def label_tree(self,root):
        decorate_tree(root)
        self.label_functions(root)

    #Performs the labelling (incl. decoding tasks)
    def label_functions(self,root):
        if not root.is_terminal_sym():
            for child in root.children:
                if child.funlabel == None and not child.head:
                    if root.label in ["SENT","Sinf","Ssub","Sint","Srel","VN","VPinf","VPpart"] or find_head(root) == "VN":
                         featvals = grab_features(root,child,self.featnames)
                         child.funlabel = self.label_one(featvals)
                         if child.funlabel == "None":
                             child.funlabel = None
                    else:
                        child.funlabel = None
                self.label_functions(child)

    def label_one_binary(self,featvals):
        fvec = binarize_features(featvals,self.feattypes,self.str2bin_dic)
        int_class = self.classifier.classify(fvec)
        return self.int2cl[int_class]

    def label_one(self,featvals):
        #featvals = self.apply_vpp_hack(featvals)     
        #Normalize + check consistency of the feat values vector
        for i in range(len(self.featnames)):
            if self.feattypes[i] == 'list' and not featvals[i] in self.diclist[i]:
                print 'aborted',featvals[i]
                return None #we abort when we meet a value unseen at training
            if self.feattypes[i] == 'openlist':
                featvals[i] = self.stemmer.smooth(featvals[i],self.diclist[i])
        if self.method == 'svm':
            return self.label_one_binary(featvals)
        else:
            return self.classifier.classify(featvals)

    #This is a **fragile** hack that simulates a regular passive context for VPP > VPpart in the French Treebank (since it is not annotated)
    def apply_vpp_hack(self,featvals,HEAD_CAT_IDX=11,LHS_CAT_IDX=7,PASSIVE_IDX=5,MOOD_IDX=4,COP_IDX=13):
        #print featvals 
        #print featvals[LHS_CAT_IDX],featvals[HEAD_CAT_IDX],featvals[HEAD_CAT_IDX+1]
#        if ( (featvals[LHS_CAT_IDX] == "SENT" and featvals[HEAD_CAT_IDX] == "VPP" and featvals[PASSIVE_IDX] == "True")):
            #print featvals, "<<>>"
        if (featvals[LHS_CAT_IDX] == "VPpart" and featvals[HEAD_CAT_IDX] == "VPP"):
            #print featvals, "<<"
            featvals[LHS_CAT_IDX] = "SENT"
            featvals[MOOD_IDX] = "indicatif"
            featvals[PASSIVE_IDX] = "True"
            featvals[COP_IDX] = "f"
            #print featvals, ">>"
        return featvals

class Stemmer:
    def __init__(self):
        pass

    def stem(self,word):
        if word == ",":
            return "<VIRG>"
        elif word == ":" :
            return "<COL>"
        elif word == "<UNK>":
            return "<UNK>"
        elif word == "au":
            return "à@"
        elif word == "aux":
            return "à@"
        elif word == "Au":
            return "à@"
        elif word == "Aux":
            return "à@"
        elif word == "Du": #for the purpose of functional labelling, this is safe, since we label only PPs in argument posn 
            return "de@"
        elif word == "du":
            return "de@"
        elif word == "D'":
            return "de@"
        elif word == "d'":
            return "de@"     #seems hopeless for "des" (and not very frequent anyway)
        elif re.match("([0-9]|[.,-])+",word):
            return "<NUM>"
        else :
            word = re.sub(",","",word)
            if len(word) > 4:
                return word[0:4].lower()+'*'
            else:
                return word.lower()+'@'

    #Manages words at training-time (when learning)
    def hapaxify(self,word):
        #could do more fine grained stuff later on...
        if re.match("^.*_.*$",word):
            return "<UNK-C>"
        else:
            return "<UNK>"
        
    #Manages words at run-time (before classification)
    def smooth(self,word,dict):
        w = self.stem(word)
        if w in dict:
            return w
        else:
            return self.hapaxify(w)

#BINARIZATION SECTION
########################

#Function for binarizing a feature vector into a binary vector
def binarize_features(fvec,ftypes,str2bin_dic):
    row = []
    for idx in range(len(ftypes)):
        if ftypes[idx] == 'int':
            row.append(fvec[idx])
        else:
            binvec = str2bin_dic[idx][fvec[idx]]
            row = row + binvec
    return row

#Generic functions for mapping classes to integers
def classes2int(class_set):
    i = 1
    class2int = {}
    int2class = {}
    for elt in class_set:
        class2int[elt] = i
        int2class[i] = elt
        i = i + 1
    return (class2int,int2class)

#Generic functions for binarizing discrete features -> returns a dictionary : featname --> binary translation
def binarize_set(val_set):
    nbits = num_bits(len(val_set))
    nam2bin = {}
    for i in range(len(val_set)):
        nam2bin[val_set[i]] = int2bin(i,nbits)
    return nam2bin

def int2bin(num, nbits):
    return [((num >> i) & 1) for i in range(nbits - 1,-1, -1)]


#Returns the number of bits needed to encode a positive or null number
def num_bits(n):
    if n <= 0:
        return 1
    x = 1
    c = 0
    while x <= n:
        c = c + 1
        x=x*2
    return c

# #Test 
# # print "test"
instream = sys.stdin
reader = PtbReader()
treebank = reader.parse_treebank(instream.read())

# flearner = FunctionLearner('foo3',['WH','REL','INT_MARK','VCLS','MOOD','PASSIVE','CSUB','LHS_CAT','DEP_CAT','DEP_WORD','DEP_HEAD_CAT','HEAD_CAT','HEAD_WORD','COP','LEFT_SIBLING_CAT','RIGHT_SIBLING_CAT','COHEAD_CAT','COHEAD_WORD','DEP_YIELD_LEN','HEAD_DIST'],['list','list','list','list','list','list','list','list','list','openlist','list','list','openlist','list','list','list','list','openlist','int','int'])

flearner = FunctionLearner('foo3',['CSUB','LHS_CAT','DEP_CAT','DEP_WORD','DEP_HEAD_CAT','HEAD_CAT','HEAD_WORD','LEFT_SIBLING_CAT','RIGHT_SIBLING_CAT','COHEAD_CAT','COHEAD_WORD','DEP_YIELD_LEN','HEAD_DIST'],['list','list','list','openlist','list','list','openlist','list','list','list','list','int','int'])



flearner.learn_svm_model(treebank)
# #
#flabeller = FunctionLabeller('../parser/params/foo9',['WH','REL','INT_MARK','VCLS','MOOD','PASSIVE','CSUB','LHS_CAT','DEP_CAT','DEP_WORD','DEP_HEAD_CAT','HEAD_CAT','HEAD_WORD','COP','LEFT_SIBLING_CAT','RIGHT_SIBLING_CAT','COHEAD_CAT','COHEAD_WORD','DEP_YIELD_LEN','HEAD_DIST'],['list','list','list','list','list','list','list','list','list','openlist','list','list','openlist','list','list','list','list','openlist','int','int'])
#flabeller.label_treebank(treebank)
# #for tree in treebank:
#     #decorate_tree(tree)
#     #print tree.pprint(feats = {'cohead_word':{},'conj_sub':{},'cohead_cat':{},'funlabel':{}, 'word':{},'label':{},'head':{},'switch':{}})#.encode('latin1')
# flearner.learn_joint_model(treebank)

# # flearner.learn_svm_model(treebank)
# #flearner.learn_joint_model(treebank)
# # for tree in treebank:
# #     decorate_tree(tree)
# #     print tree.pprint(feats = {'cohead_word':{},'conj_sub':{},'cohead_cat':{},'word':{},'label':{},'head':{},'switch':{}})#.encode('latin1')
# #     flearner.generate_joint_parameters(tree)
    

# # flabeller = FunctionLabeller('foo3',['CSUB','LHS_CAT','DEP_CAT','DEP_WORD','DEP_HEAD_CAT','HEAD_CAT','HEAD_WORD','LEFT_SIBLING_CAT','RIGHT_SIBLING_CAT','COHEAD_CAT','COHEAD_WORD','DEP_YIELD_LEN','HEAD_DIST'],['list','list','list','openlist','list','list','openlist','list','list','list','list','int','int',])
# # flabeller.label_treebank(treebank)
# #    print tree.pprint(feats = { 'head':{},'word':{},'cohead_cat':{},'cohead_word':{},'csub':{},'passive':{},'inv_subj':{},'mood':{}})
# #    print tree.pprint(feats = { 'wh':{},'rel':{},'vcls':{},'int_mark':{},'mood':{},'passive':{}})
# #    print tree.pprint(feats = {'mood':{}})>>>>>>> .r205

if __name__ == "__main__":
	instream = open(sys.argv[1])
	reader = PtbReader()
	treebank = reader.parse_treebank(instream.read())
	for tree in treebank:
		decorate_tree(tree)
		print tree.pprint(feats = {'fine_type':{}})

   
