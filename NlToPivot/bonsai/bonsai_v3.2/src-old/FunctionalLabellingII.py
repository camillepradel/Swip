#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

###############################################################################
#This is a revamp of the FunctionalLabeller extended to manage 
# a modelling of subcategorisation frames 
#This class is meant to *REPLACE* FunctionalLabelling : both cannot be used simultaneously
###############################################################################

import sys
import math
from copy import deepcopy
from sexpr import read_treebank
import pickle
#from fsvm import *
from PropagTable import *
from LabelledTree import *
#from svm_features import *
from megam_classifier import MegamClassifier


#JUNK! to be moved away from this file later on
copula_wordset = set(['parais','paraissaient','paraissais','paraissait','paraissant','paraisse','paraissent','paraisses','paraissez','paraissiez','paraissions','paraissons','paraît','paraîtra','paraîtrai','paraîtraient','paraîtrais','paraîtrait','paraîtras','paraître','paraîtrez','paraîtriez','paraîtrions','paraîtrons','paraîtront','paru','parue','parues','parurent','parus','parusse','parussent','parusses','parussiez','parussions','parut','parûmes','parût','parûtes','apparais','apparaissaient','apparaissais','apparaissait','apparaissant','apparaisse','apparaissent','apparaisses','apparaissez','apparaissiez','apparaissions','apparaissons','apparaît','apparaîtra','apparaîtrai','apparaîtraient','apparaîtrais','apparaîtrait','apparaîtras','apparaître','apparaîtrez','apparaîtriez','apparaîtrions','apparaîtrons','apparaîtront','apparu','apparue','apparues','apparurent','apparus','apparusse','apparussent','apparusses','apparussiez','apparussions','apparut','apparûmes','apparût','apparûtes','es','est','furent','fus','fusse','fussent','fusses','fussiez','fussions','fut','fûmes','fût','fûtes','sera','serai','seraient','serais','serait','seras','serez','seriez','serions','serons','seront','soient','sois','soit','sommes','sont','soyez','soyons','suis','étaient','étais','était','étant','étiez','étions','été','êtes','être','resta','restai','restaient','restais','restait','restant','restas','restasse','restassent','restasses','restassiez','restassions','reste','restent','rester','restera','resterai','resteraient','resterais','resterait','resteras','resterez','resteriez','resterions','resterons','resteront','restes','restez','restiez','restions','restons','restâmes','restât','restâtes','restèrent','resté','restée','restées','restés','demeura','demeurai','demeuraient','demeurais','demeurait','demeurant','demeuras','demeurasse','demeurassent','demeurasses','demeurassiez','demeurassions','demeure','demeurent','demeurer','demeurera','demeurerai','demeureraient','demeurerais','demeurerait','demeureras','demeurerez','demeureriez','demeurerions','demeurerons','demeureront','demeures','demeurez','demeuriez','demeurions','demeurons','demeurâmes','demeurât','demeurâtes','demeurèrent','demeuré','demeurée','demeurées','demeurés','sembla','semblai','semblaient','semblais','semblait','semblant','semblas','semblasse','semblassent','semblasses','semblassiez','semblassions','semble','semblent','sembler','semblera','semblerai','sembleraient','semblerais','semblerait','sembleras','semblerez','sembleriez','semblerions','semblerons','sembleront','sembles','semblez','sembliez','semblions','semblons','semblâmes','semblât','semblâtes','semblèrent','semblé','semblée','semblées','semblés','devenaient','devenais','devenait','devenant','devenez','deveniez','devenions','devenir','devenons','devenu','devenue','devenues','devenus','deviendra','deviendrai','deviendraient','deviendrais','deviendrait','deviendras','deviendrez','deviendriez','deviendrions','deviendrons','deviendront','devienne','deviennent','deviennes','deviens','devient','devinrent','devins','devinsse','devinssent','devinsses','devinssiez','devinssions','devint','devînmes','devînt','devîntes','redevenaient','redevenais','redevenait','redevenant','redevenez','redeveniez','redevenions','redevenir','redevenons','redevenu','redevenue','redevenues','redevenus','redeviendra','redeviendrai','redeviendraient','redeviendrais','redeviendrait','redeviendras','redeviendrez','redeviendriez','redeviendrions','redeviendrons','redeviendront','redevienne','redeviennent','redeviennes','redeviens','redevient','redevinrent','redevins','redevinsse','redevinssent','redevinsses','redevinssiez','redevinssions','redevint','redevînmes','redevînt','redevîntes'])

# This class is responsible for :
# (1) Extracting data tables from the treebank
# (2) Building a classification model from the data and saving it.
class ModelGenerator:
  
    def __init__(self,data_file,features):
        """
        Params : 
        data_file , the file where to store the data lines
        features , a dictionary where each attribute is mapped to a type
        """      
        self.data_table = DataTable(data_file,features) 
        self.features = features

    def extract_data_table(self,treebank,subcat_sep=True):
        """
        This function appends lines to the data_table.

        Params:
        treebank , a chunk of a treebank whose extracted data has to be appended to the data table
        subcat_sep, a boolean flag indicating whether subcat chunks should be separated by blank lines or not
        """
        for tree in treebank:
            self.annotate_tree(tree)
            self.extract_data_from_tree(tree,subcat=subcat_sep)

    def annotate_tree(self,tree):
        """
        Adds extra annotations to the tree, like head, wh etc. features
        
        Params:
        tree , the tree to annotate
        """
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

    def extract_data_from_tree(self,tree,subcat=True):
        """
        Returns a list of list of data_lines
        each such elt of the list is list of datalines encoding a subcat frame

        Each node below a S (labelled or not in the treebank) triggers the generation of a dataline
        CL nodes below a VN are also appended (preserving the linear order) since these are the clitics bearing functions
         
        Params:
        tree , the tree to extract the data from
        subcat_sep, a boolean flag indicating whether subcat chunks should be separated by blank lines or not
        """
        if not tree.is_terminal_sym():
            if  find_head(tree).label == "VN": # tree.label in ["SENT","Sinf","Sint","Srel","VPinf","VPpart"]: #or find_head(tree) == "VN":
                extracted_data = False
                for child in tree.children:
                    # marie : (try) : don't label nodes with None functional tag
                    if child.is_terminal_sym() or child.label == 'COORD':
                        continue
                    if child.label == 'VN':
                        # marie debug : track when the only dependents are clitics
                        extracted_clitics = self.do_clitics(child)
                        if extracted_clitics: extracted_data = True
                    elif not child.head:
                        feats = grab_features(tree,child,self.features.keys())
                        self.data_table.add_line(feats,child.funlabel)
                        extracted_data=True
                if subcat and extracted_data:
                    self.data_table.add_blank_line()
            
            for child in tree.children:
                self.extract_data_from_tree(child,subcat)
               
    def do_clitics(self,tree):
        """
        This actually grabs data lines for every node under VN 
        """
        # marie : returns True (False) if clitics do (don't) exist
        cl = False
        for child in tree.children:
            if child.label in ['CL','CLS',"CLO","CLR"] and not child.head:
                feats = grab_features(tree,child,self.features.keys())
                self.data_table.add_line(feats,child.funlabel)
                cl = True
        return cl

   
    def smooth_data(self,stemmer_pref_length=4,unk_threshold=2):
        """
        This smoothes the data by stemming lexicons and by clustering hapaxes 
        """
        self.data_table.skim_openlists(stemmer_pref=stemmer_pref_length,unk_threshold=unk_threshold)
        

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
        
def grab_features(lhs,node,featlist):
        vallist = [ ]
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

        # feats = build_feature_map().keys()

#         for i in range(len(vallist)):
#             print feats[i], vallist[i]
#         print "\n"

#         if len(vallist) != 20:
#             print >> sys.stderr, vallist

        # print len(vallist)

        return vallist



class DataTable:
    """
    This class stores data extracted from trees and produces outputs out of the box.
    Each data line is a pointwise list of features extracted from a node
    Data lines are supposed to be sequentially ordered according to the linear order of the nodes in the trees
    An (optional) blank data line separates chunks of data, such a data chunk is the list of arguments of a predicate given in linear order, e.g.:
    (S
        (NP-SUJ
                  ...)
        (VN
           (CL-OBJ
                   le)
           (CL-A-OBJ
                  lui)
        (ADV 
                  ...)
        (PP-MOD
                  ...)
    )
    yields 5 datalines of the form
    [val1,...,valn, SUJ]
    [val1,...,valn, OBJ]
    [val1,...,valn, A-OBJ]
    [val1,...,valn, NONE]    
    [val1,...,valn, MOD]

    Additionally this class may also preprocess the data_lines to perform some smoothing
    Typically there is some handling here for unknown words
    """
    def __init__(self,data_file,data_types):
        self.data_lines = []
        self.datafile = data_file
        self.data_types = data_types
        self.stemmer = Stemmer()
        self.classes = set([])

    def add_blank_line(self):
        """ 
        The blank line is used to separate subcat frames from each other 
        """
        self.data_lines.append([])
    
    def add_line(self,valuevector,category):
        """
        Adds a line of data to the table

        Params:
        valuevector : the vector of values for every feature
        category : the class to which this value vector maps to
        """
        if category == None:
            category = "None"
        valuevector.append(category)
        self.data_lines.append(valuevector) 
        self.classes.add(category)

    def skim_openlists(self,stemmer_pref=4,unk_threshold=2):
        """
        This method manages unkknown words
        (replaces low frequency words with a specific token)
        Params:
        stemmer_pref, the length of word prefix kept by the stemmer
        unk_threshold, the number of occurrences of a word under which it is considered as hapax
        """
        attlist = self.data_types.keys()
        for i in range(len(attlist)):
            type = self.data_types[attlist[i]]
            if type == 'openlist':
                wordlist = []
                for line in self.data_lines:
                    if line <> []:
                        line[i] = self.stemmer.stem(line[i],stem_len=stemmer_pref)
                        wordlist.append(line[i])
                lexicon = self.build_lexicon(wordlist,threshold=unk_threshold)
                for line in self.data_lines:
                    if line <> []:
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

    def skim_data(self):
        """
        This method removes unfrequent data_lines from the data tables that are thought to be too sparse
        """
        pass

    def save_table(self):
        """
        Prints the current state of the table in file self.data_file
        """
        f = open(self.data_file,'w')
        for line in self.data_lines:
            if line <> []:
                f.write(','.join(line)+'\n')
            else:
                f.write('\n')
        f.close()

    def dump_table(self):
        """
        Prints the current state of the table on <STDOUT>
        """
        for line in self.data_lines:
            if line <> []:
                print ','.join(line)
            else:
                print


  #Returns the set of values stored in a column of the table
    def valset(self,colidx):
        vals = set([])
        for line in self.data_lines:
            if len(line) > 0:
                vals.add(line[colidx])
        return vals

    #Functions for generating binary data tables            
    def generate_binary_data(self):
        (datatable,int2cl,str2bin_dics) = self.generate_svm_data()
        self.write_svm_data(datatable,int2cl,str2bin_dics)
        return datatable

    def generate_svm_data(self):
        #1) Codes the whole stuff on integers
        (cl2int,int2cl) = classes2int(list(self.classes))
        str2bin_dics = []
        attlist = self.data_types.keys()
        for i in range(len(attlist)):
            if (self.data_types[attlist[i]] == 'list' or self.data_types[attlist[i]] == 'openlist'):
                vals = self.valset(i)
                str2bin_dics.append(binarize_set(list(vals)))
            else:
                str2bin_dics.append({})

        #2) Generate data table
        datatable = []
        for line in self.data_lines:
            if len(line) > 0:
                row = []
                #print line[len(attlist)]
                #print len(attlist)
                row.append(cl2int[line[len(attlist)]]) #appends the class code in 1st posn
                for idx in range(len(attlist)):          #appends values according to their type
                    if self.data_types[attlist[idx]] == 'int':
                    #out.write(' '+str(idx)+':'+str(line[j]))
                        row.append(line[idx])
                    else:
                        binvec = str2bin_dics[idx][line[idx]]
                        row = row + binvec
                datatable.append(row)
        return (datatable, int2cl,str2bin_dics)

                 
    def write_svm_data(self,datatable,int2cl,str2bin_dics):
        out = open(self.datafile+".data","w")
        for row in datatable:
            out.write(str(row[0]))
            for idx in range(1,len(row)):
                out.write(' '+str(idx)+':'+str(row[idx]))
            out.write('\n')
        out.close()
        binout = open(self.datafile+".bin.svm","w")
        pickle.dump((int2cl,str2bin_dics),binout)
        binout.close()

class Stemmer:
    def __init__(self):
        pass

    def stem(self,word,stem_len=4):
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
            if len(word) > stem_len:
                return word[0:stem_len].lower()+'*'
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

#Function for turning a feature vector into a binary vector
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

class FunctionLabeller:

    def __init__(self,datapath,features):
        #self.diclist = self.load_dict(datapath)
        self.stemmer = Stemmer()
        self.features= features
        self.classifier = MegamClassifier( modelfile=datapath+".megam")
        return

    def label_tree(self,root):
        decorate_tree(root)
        self.label_functions(root)

    def label_functions(self,root):
        if not root.is_terminal_sym():
            if find_head(root).label == "VN": # root.label in ["SENT","Sinf","Ssub","Sint","Srel","VN","VPinf","VPpart"] : #
                dep_node_list= []
                for child in root.children:
                    if child.funlabel == None:
                        if child.label == "VN":
                            dep_node_list += self.do_clitics(child)
                        # marie : skip nodes that are supposed to be None
                        #elif not child.head:
                        elif not child.head and not(child.is_terminal_sym() or child.label == 'COORD'):
                            dep_node_list.append(child)
                lines_seq = []
                for elt in dep_node_list:
                    featvector = grab_features(root,elt,self.features.keys())
                    # call stemmer simply to escape punctuation marks etc.
                    for i in xrange(len(featvector)):
                        featname = self.features.keys()[i]
                        if featname.endswith("WORD"):
                            featvector[i] = self.stemmer.stem(featvector[i],stem_len=100)
                    lines_seq.append(featvector)
                funlabelsequence = self.label_sequence(lines_seq,self.features.keys()) # return list de classes avec ordre préservé class[i] = dep[i]
                for i in range(len(dep_node_list)):
                    if funlabelsequence[i] == "None":
                        funlabelsequence[i] = None
                        
                    dep_node_list[i].funlabel = funlabelsequence[i]
            for child in root.children:
                self.label_functions(child)


    def do_clitics(self,tree):
        """
        This actually grabs data lines for every node under VN 
        """
        for child in tree.children:
            dep_node_list = []
            if child.label in ['CL','CLS','CLO','CLR'] and not child.head:
                dep_node_list.append(child)
            return dep_node_list



    def label_sequence( self, seq_list, feat_names ):
        labels = []
        # call constructor Sequence 
        seq = Sequence( seq_list, feat_names )
        # pointwise classification of dependent
        for i in range( len(seq.dependents) ):
            inst = PointwiseInstance( seq.dependents, i )
            # classify 
            cl = self.classifier.get_best_label( inst.feature_vector() )
            # store label for each dependent
            labels.append( cl )
        return labels



   #  def label_sequence( self, seq_list, feat_names, beamsize=3 ):
#         # call constructor Sequence 
#         seq = Sequence( seq_list, feat_names )
#         dependents = seq.dependents
#         # maintain N-best sequences of dependent assignments
#         sequences = [([],0.0)]  # log prob.
#         for i in range( len(dependents) ):
#             n_best_sequences = []
#             # compute static features (these are cached)
#             cached_inst = PointwiseInstance( dependents, i )
#             for j in range( len(sequences) ):
#                 seq_j,log_pr_j = sequences[j]
#                 deps_j = seq_j+dependents[i:]
#                 # add sequential features
#                 inst = PointwiseInstance( deps_j, i )
#                 inst.fv = deepcopy(cached_inst.fv)
#                 inst.add_sequential_features( deps_j, i )
#                 # get pr distrib for different classes
#                 label_pr_distrib = self.classifier.get_label_probs(inst.feature_vector())
#                 # extend sequence with dependent i
#                 for (cl,pr) in label_pr_distrib:
#                     dep = deepcopy(dependents[i])
#                     dep.cl = cl
#                     n_best_sequences.append((seq_j+[dep],log_pr_j+math.log(pr)))
#             # sort sequences
#             n_best_sequences.sort(lambda x,y:cmp(x[1],y[1]))
#             # keep N best
#             sequences = n_best_sequences[-beamsize:]
#         # best sequence is sequence with highest prob. 
#         best_sequence = sequences[-1][0]
#         # return labels for best_sequence
#         return [d.cl for d in best_sequence]



    def load_dict(self,datapath):
        f = open(datapath+".dict")
        diclist = pickle.load(f)
        f.close()
        return diclist



class Sequence:

    def __init__(self, items, feature_names):
        self.dependents = [Dependent( item, feature_names ) for item in items]
        return

    def __str__(self):
        return "\n".join( [d.__str__() for d in self.dependents] ).encode('Latin-1')




class Dependent:

    def __init__(self, elts, feature_names):
        self.fv = dict( zip(feature_names,elts) )
        self.cl = "?"
        return

    def __str__(self):
        return ",".join( self.fv.values() )



class Instance:

    def add_feature(self, name, value):
        self.fv[name] = value
        return

    def feature_vector(self):
        return ['%s=%s' %(f,v) for (f,v) in self.fv.items()]





class PointwiseInstance( Instance ):

    def __init__(self, dependents, index):
        self.compute_static_features( dependents, index )
        return


    def compute_static_features(self, dependents, index):
        # features from benoit's extraction
        self.fv = dependents[index].fv
        ####### discretization
        self.add_feature( 'HEAD_DIST', int(round(float(self.fv['HEAD_DIST'])/3)) )
        # self.add_feature( 'PREC_HEAD', int(self.fv['HEAD_DIST']) < 0 )
        self.add_feature( 'DEP_YIELD_LEN', int(round(float(self.fv['DEP_YIELD_LEN'])/5)) )
        ####### new positional features
        self.add_feature( 'DEP_RANK', index )
        # TODO: add many more features 
        # ...
        return


    def add_sequential_features(self,dependents, index):
        p_cl = 'Out'
        pp_cl = 'Out'
        prev_dependents = dependents[:index]
        if len(prev_dependents) > 0:
            p_cl = prev_dependents[index-1].cl
        if len(prev_dependents) > 1:
            pp_cl = prev_dependents[index-2].cl
        self.add_feature( 'pp_cl', pp_cl )
        self.add_feature( 'p_cl', p_cl )
        self.add_feature( 'pp_cl&p_cl', pp_cl+'&'+p_cl )
        return
    

    
        
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



if __name__ == "__main__":
#     m = ModelGenerator('/Users/Benoit/test',build_feature_map())
     m = ModelGenerator('/Users/mcandito/test',build_feature_map())
     tbank = read_treebank(sys.stdin)
     m.extract_data_table(tbank,subcat_sep=True)
     # rm smoothing for maxent data 
     # m.smooth_data(stemmer_pref_length=100,unk_threshold=0)
     m.smooth_data(stemmer_pref_length=4,unk_threshold=2)
     dump_features(build_feature_map())
     m.data_table.dump_table()
     m.data_table.generate_binary_data()


# Test classif
#if __name__ == "__main__":
#    instream = sys.stdin
#    # reader = PtbReader()
#    treebank = read_treebank(instream)
#    fl = FunctionLabeller("./ftb1.data.pointwise",build_feature_map())
#    for tree in treebank:
#        decorate_tree(tree)
#        fl.label_functions(tree)
#        print tree.pprint(feats = {'funlabel':{}})
