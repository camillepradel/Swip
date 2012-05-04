#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

import re
import sys
import string
# This is a class for managing syntactic trees, more specifically trees from the FTB

# Global Variables
#########################
global LBR 
LBR = '--LBR--'
#Right Bracket substitution symbol
global RBR
RBR  = '--RBR--'
#Traces symbol
global TRACE
TRACE = '--t--'
global BIN_SEP
BIN_SEP='@'
global BIN_CAT
BIN_CAT ='^'
#########################


	
def affine(label):
	if label <> None and re.match("^.+$",label):
		return re.search("^(.).*$",label).group(1)
	else:
		return label

# A classical labelled linearily ordered tree as used in NL Syntax
class LabelledTree:
    def __init__(self,label,children=None):
        self.label = label       #the cat attribute
        self.children = children #pointers to children
        self.subcat = None       #subcat attribute
        self.lemma = None       #lemma attribute
        self.funlabel = None     #function attribute
        self.compound = False    #compound mark        
        #THE 3 following attr. should be removed
        self.CPLX_CAT = False    #GLOBAL constant for printing subcat attribute
        self.FUN_CAT = True      #GLOBAL constant for printing function attribute
        self.CPD_CAT = False     #GLOBAL constant for printing compound attribute
        self.MPH_CAT = False     #GLOBAL constant for printing compound attribute
        self.morphology = {}   #att/val dict. 
        self.head = False
        # marie : index of the child that is the head
        self.headindex = None
        # marie : for preterminal nodes : rank in sentence according to linear order
        self.lid = -1
        # head word
        self.word = None
        self.type = None

    def set_Treenum(self, treenum):
        self.treenum = treenum
        
    def set_VPPmorphology(self,mood,tense,num,gen):
        if mood <> '*':
            self.morphology['mood'] = mood
	if tense <> '*':
            self.morphology['tense'] = tense
        if num <> '*':
            self.morphology['num'] = num
        if gen <> '*':
            self.morphology['gen'] = gen

    def set_Vmorphology(self,mood,tense,pers,num):
        if mood <> '*':
            self.morphology['mood'] = mood
        if tense <> '*':
            self.morphology['tense'] = tense
        if num <> '*':
            self.morphology['num'] = num
        if pers <> '*':
            self.morphology['pers'] = pers

    def set_non_Vmorphology(self,gen,num,pers):
        if gen <> '*':
            self.morphology['gen'] = gen
        if num <> '*':
            self.morphology['num'] = num
        if pers <> '*':
            self.morphology['pers'] = pers


    def get_morphology(self,feature):
        if self.morphology.has_key(feature):
            return self.morphology[feature]
        else:
            return '*'

    #generates a canonical symbol for describing morphological info 
    def gen_full_morpho_symbol(self):
        sym = ''
        if self.morphology.has_key('mood'):
            sym = sym + self.morphology['mood']
        if self.morphology.has_key('tense'):
            sym = sym + self.morphology['tense']
        if self.morphology.has_key('pers'):
            sym = sym + self.morphology['pers']
        if self.morphology.has_key('gen'):
            sym = sym + self.morphology['gen']
        if self.morphology.has_key('num'):
            sym = sym + self.morphology['num']
        return sym

    #A Method that controls the way the tree is going to be printed. 
    #The parameters are boolean indicating wether a given field has to be outputted
    #The first parameter controls whether the subcat field has to be printed out
    #The second parameter controls whether the function field has to be printed out
    #The third parameter controls whether the compounds field has to be printed out
    #NB : This sucks a little bit (requires a full tree traversal) since I didn't find how to specify global class params in python analog to java static stuff)
    def set_print_options(self,cplx_bool,fun_bool,comp_bool,mph_bool):
        self.CPLX_CAT = cplx_bool
        self.FUN_CAT = fun_bool
        self.CPD_CAT = comp_bool
        self.MPH_CAT = mph_bool
        if not self.is_leaf():
            for child in self.children:
                child.set_print_options(cplx_bool,fun_bool,comp_bool,mph_bool)

    #Returns true if this tree is a parse failure, false otherwise
    def is_parse_failure(self):
	return self.label == "ROOT-FAILURE" or self.label == 'ROOT_FAILURE'

    # An additional field to store an additional feature like the FTB subcat
    def set_feature(self,subcat):
	self.subcat = subcat

     # An additional field to store an additional feature like the FTB lemma
    def set_lemma(self,lemma):
	self.lemma = lemma

    # An additional field to store an additional feature like the FTB function
    def set_function(self,function):
	self.funlabel = function

    # A bit that indicates wether this node has been marked as compound
    def set_compound_true(self):
	self.compound = True

    # A bit that indicates wether this node has been marked as compound
    def set_compound_false(self):
	self.compound = False

    # true for compound or compound's component
    def is_compound(self):
	return self.compound

    def is_component(self):
        return self.compound and self.children[0].is_leaf()

    #Returns true is this tree has no children
    def is_leaf(self):
       if self.children == None:
           return True
       return False

    #This function says true if the tree is a pos tag, false otherwise
    def is_terminal_sym(self):
        if not self.is_leaf() and len(self.children) == 1 and self.children[0].is_leaf():
            return True
        else:
            return False        

    #Adds a new node to the tree as the next rightmost child of a tree
    def add_child(self,tree):
        if (self.children == None):
            self.children = []
        self.children.append(tree)
    #Cloning method
    #TODO

    #####################################################################
    #Reading off stuff in the tree (yield, node list etc.)
    #####################################################################
        
    #Returns the immediate children of a tree as a list
    def get_children(self):
        return self.children

    def has_children(self):
        return self.children <> None

    # get the word form of a preterminal node known to be a simple word, or a component of a compound
    def get_form(self):
        return self.children[0].label
    # get the word form of a preterminal node known to be a compound
    def get_compound_form(self):
        return '_'.join( [ x.get_form() for x in self.children ] )

    #Returns the list of nodes of the tree as given by a prefix walk
    def tree_nodes(self):
        cur_nodes = [self]
        if not self.is_leaf():
            for child in self.children:
                cur_nodes = cur_nodes + child.tree_nodes()
        return cur_nodes
    
    #Returns the list of non terminal nodes of the tree as given by a prefix walk
    def tree_nt_nodes(self):
        cur_nodes = []
        if not self.is_leaf():
            cur_nodes.append(self)
            for child in self.children:
                cur_nodes = cur_nodes + child.tree_nt_nodes()
        return cur_nodes

    #Returns the list of non terminal nodes of the trees (POS are considered as terminals in this case)
    def tree_nt_sym_nodes(self):
        cur_nodes = []
        if not (self.is_leaf() or (len(self.children) == 1 and  self.children[0].is_leaf())):
            cur_nodes.append(self)
            for child in self.children:
                cur_nodes = cur_nodes + child.tree_nt_sym_nodes()
        return cur_nodes

    #Returns the list of cfg rules from the tree as given by a prefix walk
    #each rule is represented as a tuple with the lhs symbol being indexed in 0
    def cfg_rules(self):
        cur_rules = []
        if not self.is_leaf():
            rule = [self] + self.children
            cur_rules.append(rule)
            for child in self.children:
                cur_rules = cur_rules + child.cfg_rules()
        return cur_rules

    #Returns the list of cfg rules from the tree as given by a prefix walk
    #each rule is represented as a tuple with the lhs symbol being indexed in 0
    #Here the terminals are excluded from the rules (as done in practice in parsing : terminals are handled by the tagger)
    def cfg_rules_strict(self):
        cur_rules = []
        if not (self.is_leaf() or (len(self.children) == 1 and self.children[0].is_leaf())):
            rule = [self] + self.children
            cur_rules.append(rule)
            for child in self.children:
                    cur_rules = cur_rules + child.cfg_rules_strict()
        return cur_rules

    #Returns the yield of a tree as a list of nodes
    def tree_yield(self):
        cur_yield = []
        for child in self.children:
            if child.is_leaf():
                cur_yield.append(child)
            else:
                cur_yield = cur_yield + child.tree_yield()
        return cur_yield

    def tree_yield_str(self):
        """Returns the yield of the tree, as a space-separated stringof its terminals"""
	return ' '.join([x.label for x in self.tree_yield()])

    #Returns a list of word/pos tags pairs where even indexed items are words and odd indexed items are pos
    def pos_yield(self):
        cur_yield = []
        if self.children <> None:
            for child in self.children:
                if child.is_leaf():
                    cur_yield.append(child)
                    cur_yield.append(self)
                else:
                    cur_yield = cur_yield + child.pos_yield()
        return cur_yield

    #returns the immediate left sibling of a node if it exists, None otherwise
    def left_sibling(self,parent):
        prevchild = None
        for child in parent.children:
            if child == self:
                return prevchild
            prevchild = child
        return None      

    #Returns the immediate right sibling of a node if it exists, None otherwise
    def right_sibling(self,parent):
        for i in range(len(parent.children)):
            if parent.children[i] == self and i+1 < len(parent.children):
                return parent.children[i+1]
        return None

    def append_yield_on_error(self,list):
	for node in list:
	    self.add_child(node)

    def append_yield(self,list):
	tyield = self.tree_yield()
	if len(tyield) <> len(list):
	    sys.stderr.write("---------------------------------------------------------\n")
	    sys.stderr.write("Warning yields do not match, inconsistent output expected\n")
	    sys.stderr.write(">> "+ print_node_list_raw(' ',list)+'\n')
	    sys.stderr.write(">> "+ print_node_list_raw(' ',tyield)+'\n')
	    self = parse_failure()
	    #self.append_yield_on_error(list)
	else:
            #sys.stderr.write("Normal Match\n")
	    for i in range(len(tyield)):
		tyield[i].add_child(list[i])
		
    ########################
    # marie : reading annotations on node labels
    # if tree looks like (S (NP{feat1=toto@ feat2=titi} (D la) (N paix)) ...)
    # then the node bearing NP{feat1=toto}{feat2=titi} label is changed into NP,
    #  with features node.feat1 and node.feat2 set to 'toto' and 'titi'
    def read_annotations(self):
        m = re.compile("^(.+)\{([^\{\}]+)}$")
        l = (self.label)
        match = m.search(l)
        while match <> None:
            l = match.group(1)
            feats = match.group(2)
            for feat in feats.split('@'):
                r = re.compile('^ *(.*?) *= *(.*?) *$').search(feat)
                if r <> None:
                    attr = r.group(1)
                    val = r.group(2)
                    self.__dict__[attr] = val
            match = m.search(l)
        self.label = l
        if self.children <> None:
            for child in self.children:
                child.read_annotations()

# marie: count of lemma occurrences
# countdic = a dictionary , key = form, 
#                           value = {lemma1:nb of occ of form with lemma1, 
#                                   lemma2:nb of occ of form with lemma2, 
    def lemmacount(self, countdic):	
        if self.is_terminal_sym():
            form = self.children[0].label
            lemma = self.lemma
            tag = self.label
            if form in countdic:
                if tag in countdic[form]:
                    countdic[form][tag][lemma] = countdic[form][tag].setdefault(lemma, 0) + 1
                else:
                    countdic[form][tag] = { lemma : 1 }
            else:
                countdic[form] = { tag : { lemma : 1 } }
        elif self.children <> None:
            for child in self.children:
                child.lemmacount(countdic)

    ########################
    # Tree transforms
    ########################    
    #Remove traces  : this is a trace removal transformation. This breaks in two cases :  (1) traces concerning empty determiners following prepositions (like au, des) and (2) traces concerning empty prepositions preceding a pronoun (like auxquels)
   
    def remove_traces(self):
        reg = self.remove_tracesr(False)
        #if reg:
	    #print "---------------------------------"
            #print "Error during trace removals right"
	   #  print self.pprint().encode('iso-8859-1')
# 	    print "---------------------------------"
           
        reg = self.remove_tracesl(False)
        #if reg:
	   # print "---------------------------------"
            #print "Error during trace removals left"
      #       print self.pprint().encode('iso-8859-1')
# 	    print "---------------------------------"

# Traces concerning empty determiners following prepositions, empty determiners are removed and the preposition tag of the immediately preceding preposition (P) is substituted by the new tag 'P+D'. Hence no information is lost.
#The algorithm proceeds by a postfix traversal of the tree keeping an implicit register indicating when a determiner has been removed and when to replace the preposition tag.
 
    def remove_tracesr(self,register):
        if self.is_leaf():
            return (register,None)
        else:
            reg = register
            new_children = []
            self.children.reverse()
	    detsubcat = None
            for child in self.children:
                if child.label == 'D' and (not child.is_leaf()) and child.children[0].label == TRACE:
                    reg = True
		    detsubcat = child.subcat
                elif child.label == "P" and (not child.is_leaf()) and child.children[0].is_leaf() and reg:
                    child.label = "P+D"
		    if detsubcat <> None:
			    child.subcat = detsubcat
                    reg =  False
		    detsubcat=None
                    new_children.append(child)
                else:
                    (reg,detsubcat) = child.remove_tracesr(reg)
                    new_children.append(child)
            new_children.reverse()
            self.children=new_children
            return (reg,detsubcat)

# Traces concerning empty prepositions preceding pronouns, empty prepositions are removed and the pronount tag of the immediately following pronoun (PRO) is substituted by the new tag 'P+PRO'. Hence no information is lost.
 #The algorithm proceeds by a prefix traversal of the tree keeping an implicit register indicating when a preposition has been removed and when to replace the pronoun tag.
    def remove_tracesl(self,register):
        if self.is_leaf():
            return register
        else:
            reg = register
            new_children = []
            for child in self.children:
                if child.label == 'P' and (not child.is_leaf()) and child.children[0].label == TRACE:
                    reg = True
                elif child.label == "PRO" and (not child.is_leaf()) and child.children[0].is_leaf() and reg:
                    child.label = "P+PRO"
                    reg =  False
                    new_children.append(child)
                else:
                    reg = child.remove_tracesl(reg)
                    new_children.append(child)
            self.children=new_children
            return reg

    #Remove traces from the Penn TreeBank
    def remove_traces_ptb(self):
        if self.is_leaf():
            return False
        else:
            new_children = []
            remove_all = True
            for child in self.children:
                if child.label <> '-NONE-':
                    remove_me = child.remove_traces_ptb()
                    if not remove_me:
                        remove_all = False
                        new_children.append(child)
                    elif remove_me and len(self.children) == 1:
                        return True
                elif child.label == '-NONE-'and len(self.children) == 1:
                    return True
            if remove_all:
                return True
            else:
                self.children = new_children
                return False
   
    def remove_ptb_annotations(self):
        if self.is_leaf():
            pass
        else:
            self.label = re.sub('[-=][^-=]+','',self.label)
            for child in self.children:
                child.remove_ptb_annotations()


    # ADDs an empty root at the top of the tree
    def add_dummy_root(self):
        root =  LabelledTree('')
        root.add_child(self)
        return root

    # ADDs an empty root at the top of the tree
    def add_dummy_rootII(self):
        root =  LabelledTree('Start')
        root.add_child(self)
        return root

    # Remove the dummy root at the top of the tree
    def remove_dummy_root(self):
        if len(self.children) == 1 :
	    return self.children[0]
	else:
	    return self

    #BINARIZATION SECTION
    ############################

    def h_markovize_left(self,order=2):
	    if not self.is_leaf():
		    if len(self.children) > 2:
			    horizontal_hist = [self.children[-1]]
			    for i in range(len(self.children)-2,0,-1):
				    horizontal_hist = [self.children[-2]] + horizontal_hist
				    newnode = LabelledTree(self.label+'@'+join_node_list('+',horizontal_hist[:order]),self.children[i:])
				    self.children = self.children[:i] + [newnode]
			    
		    for child in self.children:
			    child.h_markovize_left(order)

    def unmarkovize(self):
	    if not self.is_leaf():
		    newchildren = []
		    for child in self.children:
			    if not '@' in child.label:
				    newchildren.append(child)
				    child.unmarkovize()
			    else:
				    newchildren = newchildren + child.unmarkovize()
		    self.children = newchildren
		    return newchildren
	    #return []

    #(Johnson's) PARENT TRANSFORMATION 
    #####################################
    #Call this one on the root of the tree
    #Johnson extended
    def parent_transform_ext(self,order=100):
        self.parent_transform_extR([],order=order)

    def parent_transform_extR(self,parent_labels,order=100):
        if not self.is_leaf():
            for child in self.children:
                child.parent_transform_extR([self.label] + parent_labels[:order],order=order)
            if len(parent_labels)>0: 
		    if order > 0:			    
			    self.label = self.label + '^' + '^'.join(parent_labels[:order])

    def unannotate_parent(self):
	    if not self.is_leaf():
		    for child in self.children:
			    child.label = child.label.split('^')[0]
			    child.unannotate_parent()



    #Johnson base
    def parent_transform(self):
        self.parent_transformR(None)

    def parent_transformR(self,parent_label):
        if not self.is_leaf() and not (len(self.children)==1 and self.children[0].is_leaf()):
            for child in self.children:
                child.parent_transformR(self.label)
            if parent_label <> None:
                self.label = self.label + '^' + parent_label

    # add intermediate node between terminal and pre-terminal symbol
    # with label = pre-term + lemma
    def add_lemma_level(self):
        if self.is_terminal_sym():
            if self.label in ['V','VPP','VS','VIMP']:
                if self.lemma <> None:
                    lemma = self.lemma
#                    print "lemma : ", lemma.decode('latin-1').encode('latin-1')
                else:
                    lemma = self.children[0].label
#                    print "back off to label : ", lemma.encode('latin-1')
                lemma = '_'.join(lemma.split())
                new = LabelledTree(self.label + '_' + lemma)
                new.add_child(self.children[0])
                self.children = [new]
        else:
            for child in self.children:
                child.add_lemma_level()

    # replaces terminal symbols (words)
    # with label = pre-terminal + lemma
    # or with label = pre-terminal + word
    # or with label = lemma only
    # or with label = pre-terminal only
    def changelex(self, lex, addtag):
        if self.is_terminal_sym():
            if self.label <> 'PONCT':
                if lex == 'lemma':
                    if self.lemma <> None:
                        lemma = self.lemma
                    else:
                        lemma = self.children[0].label
                    lemma = '_'.join(lemma.split())
                #print "lemma : ", lemma.decode('latin-1').encode('latin-1')
                    if addtag:
                        self.children[0].label = self.label + '_' + lemma
                    else:
                        self.children[0].label = lemma
                elif lex == 'form':
                    self.children[0].label = self.label + '_' + self.children[0].label
                else:
                    self.children[0].label = self.label
        else:
            for child in self.children:
                child.changelex(lex, addtag)


    #lemtok 
    def lemtok(self):
	    if self.is_terminal_sym():
		    if self.lemma <> None:
			    self.lemma = re.sub('\s+','_',self.lemma)
			    self.children[0].label = self.lemma+'@'+self.children[0].label
	    else:
		    for child in self.children:
			    child.lemtok()



    # returns the index of the first node having its child labeled chlabel
    def label_index(self, nodelist, chlabel):
        for i,node in enumerate(nodelist):
            if not node.is_leaf():
                for child in node.children:
                    if child.label == chlabel: return i
        return -1

    # (X ... Y LBR Z ... T RBR ...) ==> (X ... Y BR (LBR Z ... T RBR) ...) 
    def phrase_brackets(self):
        if not self.is_leaf():
            # first ponct (
            i = self.label_index(self.children, '(')
            if i > -1:
                newchildren = self.phrase_one_bracket_pair(i, self.children)
                self.children = newchildren
            else:
                for child in self.children:
                    child.phrase_brackets()

    def phrase_one_bracket_pair(self, i, remaining_children):
        newchildren = []
        # children before ( are unchanged
        for child in remaining_children[:i]:
            child.phrase_brackets()
            newchildren.append(child)
        br = LabelledTree("BR")
        # ( as first child of br
        br.add_child(remaining_children[i])
        after_children = remaining_children[i+1:]
        k = self.label_index(after_children, ')')
        if k == -1:
            sys.stderr.write('Missing --RBR--')
        for l in after_children[0:k+1]:
            l.phrase_brackets()
            br.add_child(l)
        newchildren.append(br)
        j = self.label_index(after_children[k+1:], '(')
        # there exists a ( later on
        if j > -1:
            end = self.phrase_one_bracket_pair(j, after_children[k+1:])
            newchildren = newchildren + end
        else:
            for c in after_children[k+1:]:
                c.phrase_brackets()
                newchildren.append(c)
        return newchildren
                                               
               
    # POS TAGS ALTERATION (= LEXICAL CHANNEL W/ LEXICON) 
    ######################################################

    def propagate_det(self):
	    if not self.is_leaf():
		    if self.label == 'NP':
			    for child in self.children:
				    if child.label=='DDEF':
					    self.label = 'NPdef'
				    if child.label=='DDEM':
					    self.label = 'NPdem'
				    if child.label=='DPOSS':
					    self.label = 'NPposs'
		    if self.label == 'PP':
			    if len(self.children)>1:
				    if self.children[0].label == "P+DDEF" and self.children[1].label=="NP":
					    self.children[1].label="NPdef"
		    for child in self.children:
			    child.propagate_det()

    #RAISE PUNCT (// Penn Treebank)
    #################################
    def raise_punct(self):
        if not self.is_leaf() and self.label == "PONCT":
            if len(self.children) == 1:
                self.label = self.children[0].label
        elif not self.is_leaf():
            for child in self.children:
                child.raise_punct()

    # A + DE INSERTION                                                
    #RAISE PREPS
    def raise_preps(self):
        if not self.is_leaf() and self.label == "P" and len(self.children) == 1 and self.children[0].is_leaf():
           if self.children[0].label == "de" or self.children[0].label == "De" or self.children[0].label == "DE":
               self.label="PDE"
           elif self.children[0].label == "A" or self.children[0].label == "a":
               self.label="PA"
        if not self.is_leaf():
            for child in self.children:
                child.raise_preps()

    #MERGE COMPOUNDS
    def merge_cpds(self):
        if not self.is_leaf() and self.compound :
            tyield = self.tree_yield()
            self.children = []
            self.add_child(LabelledTree(print_node_list_raw('_',tyield)))   
            # marie : they're not compounds anymore ...
            self.set_compound_false()
        elif not self.is_leaf():
            for child in self.children:
                child.merge_cpds()

    #MERGE DIGITAL NUMBERS
    def merge_num(self):
        if self.compound and (self.label == 'D' or self.label == 'A' or self.label=='PRO') :
            bfr = ''
            merged = False
	    if not child.is_leaf():
		    for child in self.children:
			    if not child.is_leaf() and (child.label == 'D' or child.label == 'A' or  child.label == 'PRO') and len(child.children) == 1 and re.match('[0-9]+',child.children[0].label):
				    bfr = bfr+child.children[0].label
				    merged = True
			    elif not child.is_leaf() and child.label == 'PONCT' and len(child.children) == 1 :
				    bfr = bfr+child.children[0].label
		    if merged:
			    self.children = []
			    self.children.append(LabelledTree(bfr))
			    self.compound = False
        
		    elif not self.is_leaf():
			    for child in self.children:
				    child.merge_num()

    #PROPAGATES a rel feature on NP and PPs dominating a PROREL tag and below a Srel node
    def propagate_rel(self):
        if not self.is_leaf():
            if self.is_terminal_sym():
                if self.label == 'PROREL':
                    return 'rel'
                else:
                    return None
            else:
                feat = None
                for child in self.children:
                    nfeat = child.propagate_rel()
                    if nfeat <> None: #SOLVES PROPAG CONFLICTS
                        feat = nfeat
                if feat <> None and (self.label == "NP" or self.label == 'PP'):
                    self.label = self.label+feat
                    return feat
                    #return None#STOP
                elif feat <> None and self.label == "SBAR":#STOP PROPAGATION
                    self.label = self.label+feat
                    return None
                else:                           #STOP PROPAGATION 
                    return None
        else:
            return None
            

    #MOOD FEATURE PROPAGATION (FINITE - PART and INFINITIVE)
    def propagate_mood(self):
        if not self.is_leaf():
            if self.is_terminal_sym(): 
                if self.label == "VINF":
                    return "inf"
                elif self.label == "VPP" or self.label == "VPR":
                    return "part"
                elif re.match("V.*",self.label): 
                    return "fin"
                else: 
                    return None          
            else:
                feat = None
                for child in self.children:
                    nfeat = child.propagate_mood()
                    if nfeat <> None and feat <> None and feat <> nfeat: #Solves conflicts
                        if (feat == "inf") and (nfeat == "part"):
                            feat = "inf"
                        elif (feat == "fin") and (nfeat == "part"):
                            feat = "fin"
                        elif (feat == "fin") and (nfeat == "inf"):
                            feat = "inf"
                        else:
                            sys.stderr.write("Warning conflicting mood features : performing random propagation : "+feat+"--"+nfeat+"\n")
                            feat = nfeat
                    elif nfeat <> None:
                        feat = nfeat
                #PROPAG stuff
                if feat <> None and self.label == "VN" :#PROPAGATE 
                    self.label = self.label+feat
                    return feat
                elif feat <> None and self.label == "S"+feat :#PROPAGATE
                    #return feat
                    return None
                elif feat <> None and self.label == "SBAR":#STOP PROPAGATION 
                    self.label = "SBAR"+feat
                    return None
                elif feat <> None and self.label == "SENT":#STOP PROPAGATION 
                    #self.label = "S"+feat
                    return None
                elif feat == None and self.label == "SENT":#STOP PROPAGATION 
                    #self.label = "Sav"
                    return None
                else:
                    return None 

    #SBAR Insertion
    #for Ssub
    def ssub_2_sbar(self):
        if self.is_leaf():
            return self
        elif self.label == 'Ssub' and self.children[0].label == 'CS':
            sbar = LabelledTree('SBAR')
            sbar.add_child(self.children[0])
            sfin = LabelledTree('Sfin')
            sbar.add_child(sfin)
            for child in self.children[1:]:
                sfin.add_child(child.ssub_2_sbar())
            return sbar            
        else :
            new_children = []
            for child in self.children:
                new_children.append(child.ssub_2_sbar())
            self.children=new_children
            return self
 
    #For VPinf
    def vpinf_2_sbar(self):
        if self.is_leaf():
            return self
        elif self.label == 'VPinf':
            sbar = LabelledTree('SBAR')
            if self.children[0].label == 'P':
                sbar.add_child(self.children[0])
            #else:
             #   trace = LabelledTree("--t--")
              #  trace_cat = LabelledTree("P")
               # sbar.add_child(trace_cat)
                #trace_cat.add_child(trace)

            sinf =  LabelledTree('Sinf')
            sbar.add_child(sinf)
            #emptynp = LabelledTree('NP')
            #trace = LabelledTree(TRACE)
            #sinf.add_child(emptynp)
            #emptynp.add_child(trace)
            if self.children[0].label == 'P':
                for child in self.children[1:]:
                    sinf.add_child(child.vpinf_2_sbar())
            else:
                for child in self.children:
                    sinf.add_child(child.vpinf_2_sbar())
            return sbar
        else:
             new_children = []
             for child in self.children:
                 new_children.append(child.vpinf_2_sbar())
             self.children=new_children
             return self

    #For Srel
    def srel_2_sbar(self):
         if self.is_leaf():
            return self
         elif self.label == 'Srel':
             sbar = LabelledTree('SBAR')
             sbar.add_child(self.children[0])
             sfin =  LabelledTree('Sfin')
             sbar.add_child(sfin)
             for child in self.children[1:]:
                 sfin.add_child(child.srel_2_sbar())
             return sbar
         else:
             new_children = []
             for child in self.children:
                 new_children.append(child.srel_2_sbar())
             self.children=new_children
             return self

    #Remains to be done for wh questions (unclear to me for now)
    #I did for PROWH words, 
    #Remains WHNP and WHADV
    #def wh_2_sbar(self)
   
    def wh_2_sbarq(self):
	if self.is_leaf():
	    return self
	elif self.isWh() and self.label == 'SENT':
	    sbarq = LabelledTree('SBARQ')
	    sbarq.add_child(self.children[0])
	    if self.children[0].label == 'NP':
		self.children[0].label='NPQ'
	    sq = LabelledTree('SQ')
	    sbarq.add_child(sq)
	    for child in self.children[1:]:
		sq.add_child(child.wh_2_sbarq())
	    return sbarq
	else:
	    new_children=[]
	    for child in self.children:
		new_children.append(child.wh_2_sbarq())
	    self.children=new_children
	    return self


    def isWh(self):
	if self.subcat == 'inter' or self.subcat == 'int':
	    return True
	else:
	    return False

    def propagate_wh(self):
	if not self.is_leaf():
	    if self.label == 'SENT':
		for child in self.children:
		    child.propagate_wh()
	    if self.children[0].isWh() and self.label == 'NP':
		self.subcat = 'inter'
	    if self.children[0].isWh() and self.label == 'ADV':
		self.subcat = 'inter'
	    if self.label == 'SENT' and self.children[0].isWh():
		self.subcat='inter'

    def wh_transform(self):
	self.propagate_wh()
	tree = self.wh_2_sbarq()
	return tree

    def sbar_transform(self):
        tree = self.ssub_2_sbar()
        #tree = tree.vpinf_2_sbar()
        tree = tree.srel_2_sbar()
        tree = tree.wh_transform()
        return tree

    # propagation of mode to VNs , and int/rel to NPs
    # as done in MFT
    # CAUTION : supposes MFT tagset for ppreterminals already set
    def propagate_as_in_MFT(self):
        if self.is_terminal_sym():
            return None
        if self.label == 'VN':
            # propagate the suffix of first V-like node
            for child in self.children:
                s = re.search('^V_(.*)$', child.label)
                if s:
                    suffix = s.group(1)
                    self.label = self.label + '_' + suffix
                    break
        elif self.label == 'NP':
            # propagate the first _int or _rel suffix
            for child in self.children:
                s = re.search('_(int|rel)$', child.label)
                if s:
                    suffix = s.group(1)
                    self.label = self.label + '_' + suffix
                    break
        for child in self.children:
            child.propagate_as_in_MFT()


    #Compound Words heuristics
    #Compound words have no subcats. I infer them crudely here
    def infer_cpd_subcats(self):
        if (not self.is_leaf()) and self.compound and len(self.children)==1 and self.children[0].is_leaf():
            #infer for nouns
	    if self.label == 'N':
                firstletter = self.children[0].label[0:1]
                if firstletter.isupper():
                    self.subcat='P'
                else:
                    self.subcat='C'
	    elif self.label =='C':	
		word = self.children[0].label[0:1]
		if word == 'et' or  word == 'mais' or  word == 'ni' or  word == 'car' or  word == 'or':  #problÃšme encodage word == 'oÃ¹'.encode("iso-8859-1")
		    self.subcat = 'C'
		elif word == 'que' or  word == "qu'" :
		    self.subcat = 'S'
		else:
		    self.subcat = 'S'
	    elif self.label == 'V':#very approximative heuristic
		suffix = self.children[0].label[-2:]
		if suffix == 'er' or suffix == 'ir' or suffix == 're' :
		    self.morphology=('infinitif','*','*','*')
		if suffix == 'nt' :
		    self.morphology=('participe','present','*','*')
		else :# specifically here : lots of past participles in compounds...
		    self.morphology=('indicatif','present','*','*')
        elif not self.is_leaf():
            for child in self.children:
                child. infer_cpd_subcats()
        else:
            pass

    #Terminal Relabelling functions
    def tagset2_terminals(self,tagfixer,opts):
	if (not self.is_leaf()) and len(self.children)==1 and self.children[0].is_leaf():
            if opts.has_key('splitde') and opts['splitde'] and self.label == 'P' and self.lemma == 'de':
                self.label = 'DE'
            if opts.has_key('splitaux') and opts['splitaux']:
                if self.lemma == 'être':
                    self.label = 'ETR'
                elif self.lemma == 'avoir':
                    self.label = 'AVO'
	    self.label = self.fixtags(tagfixer,opts)
	elif not self.is_leaf() and self.compound:
            self.label =  self.fixtags(tagfixer,opts)
            for child in self.children:
		child.tagset2_terminals(tagfixer,opts)
	elif not self.is_leaf():
            if opts['usemfttype'] and self.type <> None:
                self.label = self.label + '_' + self.type
	    for child in self.children:
		child.tagset2_terminals(tagfixer,opts)
	else:
	    pass

    def fixtags(self,tagfixer,opts):
        # MFT only : concatenate 'type' attribute to label
        if opts['usemfttype']:
            newlabel = self.label
            if self.type <> None:
                newlabel = newlabel + '_' + self.type
            # Natalie's tagset : use subcat attribute if val=card/rel/int, or if label is V or C
            elif self.subcat <> None:
                if self.subcat in ['card','rel','int'] or self.label in ['V','C']:
                    newlabel = newlabel + '_' + self.subcat
            return newlabel
	subc = self.subcat
## pour les composÃ©s, ils n'ont pas de subcat
## mais ils ont une morpho -> appliquer calcul normal
#        if self.compound and self.subcat == None: #Abort compounds (catint) bad stuff : does not work as is with preps heading compound preps 
## * suffix on the component
##            return self.label+'*'
#            return self.label
	if self.subcat == None:
	    subc = '*'
        # marie : HACK : to process MFT corpus, an use same processing as FTB 
        #         => erase the subcat information for Verbs
        if self.label == 'V':
            subc = '*'
        if self.is_component():
            if opts['markcpt']:
                return self.label+'*'
            # no existing morphology for component
            return self.label
        newlabel = self.label
        if not tagfixer == None:
#        sys.stderr.write(str(self.morphology)+'\n')
            if len(self.morphology) > 0:
                newlabel = tagfixer.map_pos(self.label,subc,self.get_morphology('mood'),self.get_morphology('tense'),self.get_morphology('pers'),self.get_morphology("num"),self.get_morphology('gen'))  
            else:
                newlabel = tagfixer.map_pos(self.label,subc,'*','*','*','*','*') 
        if self.is_compound() and opts['markcpd']: # and not a component
            return newlabel+'+'
        return newlabel


    # Marie : head marking methods

    # builds a tuple (lhs, cat of child 1, cat of child 2 ...) from a node
    # (used as key in dictionary : cfrule -> index of head child)
    def node_to_rule(self):
        if self.children == None: return []
        if self.is_terminal_sym(): return []
        rule = [x.label for x in self.children]
        rule.insert(0, self.label)
        return tuple(rule)
            
    def mark_as_head(self):
        self.head = True

    #    stores in node the index of the child that is the head
    #    and marks this child as head too
    def mark_headchild(self, headindex):
        self.headindex = headindex
        self.children[headindex].mark_as_head()

    def set_lid(self,lid):
        self.lid = lid

    def get_lid(self):
        return self.lid

    def set_word(self,word):
        self.word = word

    def get_word(self):
        return self.word
        
    #FRANCOIS:
    # ajoute à chaque noeud, un champ 'ylength': le nombre de terminaux qu'il projette
    def ylength_annotate(self):
    	if self.is_terminal_sym():
    		self.ylength=1
    		return 1
    	else:
    		ylen=0
    		for child in self.children:
    			ylen += child.ylength_annotate()
    		self.ylength=ylen
    		return ylen

	#FRANCOIS:
	# ajoute un champ 'int_mark' à tous les noeuds:
	# True: si un fils du noeud courant est de type PONCT ?
	# False: sinon
    def int_mark_annotate(self):
    	self.int_mark = False
    	if self.is_leaf():
    		pass
    	else:
    		if self == None:
    			pass
    	   	else:
    	   		for child in self.children:
    	   			if self.label in ['SENT','Ssub','Srel','Sint','VPinf','VPpart']:
    	   				if child.label == 'PONCT' and child.word == '?':
    	   					self. int_mark=True
    	   			child.int_mark_annotate()
  
  	#FRANCOIS:
  	# ajoute une champ 'rel' à tous les noeuds:
  	# True: si un descendant du noeud courant (sans dépasser les frontieres S* et V*) est de type PROREL (sans être de la forme 'qui')
  	# (ie seuls les pronoms relatifs non-sujets peuvent impliquer une inversion du sujet dans la relative)
  	# False: sinon
    def rel_annotate(self):
    	self.rel = False
    	if self.is_leaf():
    		pass
    	else:
    		if self == None:
    			pass
    	   	else:
    	   		frontieres = ['SENT','Ssub','Srel','Sint','VPinf','VPpart','VN']
    	   		for child in self.children:
    	   			if self.label in ['SENT','Ssub','Srel','Sint','VPinf','VPpart']:
    	   				if re.match("^NP$",child.label) or re.match("^PP.*$",child.label):
	    	   					if child.find_label(['PROREL'],['qui'],frontieres):
	    	   						self.rel=True
    	   			child.rel_annotate()

	#FRANCOIS:
	# ajoute un champ 'vcls' (Verbe CLitique Sujet) à tous les noeuds:
	# True: si le noeud courant est de type VN et a pour fils, un noeud V apparaissant un noeud CLS
	# Faux: sinons
    def vcls_annotate(self):
    	self.vcls = False
    	if self.is_leaf():
    		pass
    	else:
    		if self == None:
    			pass
    	   	else:
    	   		cpt=0
    	   		pos_cls=0
    	   		pos_v=0
    	   		for child in self.children:
    	   			if self.label in ['VN']:
    	   				cpt+=1
    	   				if child.label in ['V','VIMP','VINF','VS','VPR']:
    	   					pos_v=cpt
    	   				elif child.label == "CLS":
    	   					pos_cls=cpt
    	   			child.vcls_annotate()
    	   		if pos_v < pos_cls:
    	   			self.vcls = True

	#FRANCOIS:
	# ajoute un champ 'wh' à tous les noeuds:
	# True: si un descendant du noeud courant (sans dépasser les frontieres S* et V*) est de type --WH
	# False: sinon
    def wh_annotate(self):
    	self.wh = False
    	if self.is_leaf():
    		pass
    	else:
    		if self == None:
    			pass
    	   	else:
    	   		frontieres = ['SENT','Ssub','Srel','Sint','VPinf','VPpart','VN']
    	   		for child in self.children:
    	   			if self.label in ['SENT','Ssub','Srel','Sint','VPinf','VPpart']:
	    	   			if child.find_label(['DETWH','PROWH','ADVWH','ADJWH'],[],frontieres):
    	   					self.wh = True
    	   			child.wh_annotate()
  
    	   			
	#FRANCOIS:
	# ajoute aux noeud VN, un champ 'passive':
	# 1: cest du passif (une forme de 'être' et un participe passé)
	# 0: sinon
	# Pour chaque noeud VN, si l'auxiliaire être (modulo son mode) et un participe passé apparaissent, nous avons du passif
    def passive_annotate(self):
        liste_etre_ind = ["suis","es","est","sommes","êtes","sont","étais","était","étions","étiez","étaient"]
        liste_etre_ind.extend(["Êtes","Étais","Était","Étions","Étiez","Étaient"])
        liste_etre_ind.extend(["fus","fut","fûmes","fûtes","furent","serai","seras","sera","serons","serez","seront"])
        liste_etre_cond = ["serais","serait","serions","seriez","seraient"]
        liste_etre_inf = ["être","Être"]
        liste_etre_ppart = ["été","Été"]
        liste_etre_ppres = ["étant","Étant"]
        liste_etre_subj = ["sois","soit","soyons","soyez","soient"]
        liste_etre_subj.extend(["fusse","fusses","fût","fussions","fussiez","fussent"])
        liste_etre_imp = ["sois","soyons","soyez"]
        liste_verbes_auxEtre = ["allé", "allée", "allés", "allées", 
        		"apparu", "apparue", "apparus", "apparues", 
                 "arrivé", "arrivée", "arrivés", "arrivées", 
                 "décédé", "décédée", "décédés", "décédées",  
                 "demeuré", "demeurée", "demeurés", "demeurées", 
                 "devenu", "devenue", "devenus", "devenues",  
                 "entré", "entrée", "entrés", "entrées", 
                 "intervenu", "intervenue", "intervenus", "intervenues",  
                 "mort", "morte", "morts", "mortes", "né", "née", "nés", "nées", 
                 "parti", "partie", "partis", "parties", 
                 "parvenu", "parvenue", "parvenus", "parvenues",  
                 "redevenu", "redevenue", "redevenus", "redevenues", 
                 "reparti", "repartie", "repartis", "reparties" 
                 "resté", "restée", "restés", "restées", 
                 "retombé", "retombée", "retombés", "retombées", 
                 "revenu", "revenue",  "revenus",  "revenues", 
                 "tombé", "tombée", "tombés", "tombées", 
                 "venu", "venue",  "venus",  "venues"]
        if not self.label:
        	self.passive = None
        	for child in self.children:
        		child.passive_annotate()
    	elif self.is_leaf():
    		self.passive = None
    	else:
    		if self == None:
    			pass
    		elif self.label == "VN":
   	   			aux_passif = False
   	   			participe = False
   	   			reflechi = False
   	   			for child in self.children:
   	   				child.passive_annotate()  	   				
   	   				if child.label in ["V"] and child.children[0].label.lower() in liste_etre_ind or child.children[0].label.lower() in liste_etre_cond:
   	   					aux_passif = True
   	   				elif child.label in ["VIMP"] and child.children[0].label.lower() in liste_etre_imp:
   	   					aux_passif = True
   	   				elif child.label in ["VINF"] and child.children[0].label.lower() in liste_etre_inf:
   	   					aux_passif = True
   	   				elif child.label in ["VS"] and child.children[0].label.lower() in liste_etre_subj:
   	   					aux_passif = True
   	   				elif child.label in ["VPP"] and child.children[0].label.lower() in liste_etre_ppart:
   	   					aux_passif = True
   	   				elif child.label in ["VPR"] and child.children[0].label.lower() in liste_etre_ppres:
   	   					aux_passif = True
   	   				elif child.label in ["VPP"] and not child.children[0].label.lower() in liste_etre_ppart and not child.children[0].label.lower() in liste_verbes_auxEtre:
   	   					participe = True
   	   				elif child.label in ["CLR"]:
   	   					reflechi = True
   	   			if aux_passif and participe and not reflechi:
   	   				self.passive = True
   	   			else:
   	   				self.passive = False
    		else:
   	   			self.passive = None
   	   			for child in self.children:
   	   				child.passive_annotate()

	#FRANCOIS:
	# enlève toutes les fonctions
    def remove_functions(self):
		self.funlabel = None
		if self.is_leaf():
			pass
		else:
			print "="+str(self.label)
			if re.match("^.+-.+$",self.label):
				self.label = re.search("^([^-]+)(-.+)?$",self.label).group(1)
				pass
			for child in self.children:
				child.remove_functions()

	# dit si l'un des descendants du noeud courant a l'un des labels 'labels', dont la forme n'apparait pas dans 'forbidden_word' dans la limite des frontieres 'bounds'
    def find_label(self,labels,forbidden_words,bounds):
    	found = False
    	if self.children == None:
    		return False
    	else:
	    	for child in self.children:
	    		if not child.label in bounds:
	    			found = found or (child.label in labels and not child.word.lower() in forbidden_words) or child.find_label(labels,forbidden_words,bounds)
	    	return found
	
	# renvoie le label de la tete du noeud courant	
    def find_head(self):
	    default_head = None
	    for child in self.children:
	        if child.head:
	            return child
	    return default_head

    
    #FRANCOIS:
    # ajoute à chaque noeud, un champ 'cohead':
    # - Pour les PP: P est la tete principal, son complément est la cotete
    # - Pour les VPinf (qui ne sont pas dominés par un PP): la cotete est son fils-tete
    def cohead_annotate(self):
    	if not self.label:
    		self.cohead_word=None
    		self.cohead_cat=None
    		for child in self.children:
    			child.cohead_annotate()
    	elif self.is_leaf():
    		self.cohead_cat = None
    		self.cohead_word = None
    	else:
    		if self == None:
				pass
    		elif re.match("^PP$",self.label):
    			dep_heads = []
    			dep_lexs = []
    			for child in self.children:
    				if not child.head and not re.match("^P(\+D)?$",child.label):
    					dep_heads.append(child.label)
    					dep_lexs.append(child.word)
    					child.in_pp = True
    				elif re.match("^P(\+D)?$",child.label) and not child.head:
    					#si bizarrement la préposition nest pas notée comme tete dans le PP:
    					child.head = True
    					self.word = child.word
    					pass
    			if len(dep_heads) > 0:
					self.cohead_cat = dep_heads[0]
					self.cohead_word = dep_lexs[0]
    			else:
					self.cohead_cat = None
					self.cohead_word = None
    		elif re.match("^VPinf$",self.label) and not self.__dict__.has_key('in_pp'):
    	   		# Pour chaque VPinf qui n'ont pas comme mère un PP (champ 'in_pp' existant)
   	   			# si bizarrement le VN nest pas noté comme tete dans le VPinf:
    	   		if self.find_head() == None:
    	   			for child in self.children:
    	   				if re.match("^VN$",child.label):
    	   					child.head = True
    	   					self.word = child.word
    	   		for child in self.children:
    	   			if re.match("^P(\+D)?$",child.label):
						child.head=True
						self.word = child.word
						for child2 in self.children:
							if re.match("^VN$",child2.label) and child2.head:
								child2.head=False
								self.cohead_cat = child2.label
								self.cohead_word = child2.word
    	   	elif re.match("^Ssub$",self.label):
    	   		if self.find_head() == None:
    	   			for child in self.children:
    	   				if re.match("^VN$",child.label):
    	   					child.head = True
    	   					self.word = child.word
    	   		for child in self.children:
    	   			if re.match("^CS$",child.label):
						child.head=True
						self.word = child.word
						for child2 in self.children:
							if re.match("^VN$",child2.label) and child2.head:
								child2.head=False
								self.cohead_cat = child2.label
								self.cohead_word = child2.word   		
    	   	else:
    			self.cohead_cat = None
    			self.cohead_word = None
    		for child in self.children:
    			child.cohead_cat = None
    			child.cohead_word = None
    			child.cohead_annotate()

	
	#FRANCOIS:
	# ajoute à chaque noeud un champ 'conjsub':
	# False, si CS est une conjonction vide (que,si)
	# True, si elle ne lest pas
	# None, pour les autres.	
    def conjsub_annotate(self):
    	liste_cs_vides = ["que","qu'","si","s'"]
    	if not self.label:
    		self.csub = None
    		for child in self.children:
    			child.conjsub_annotate()
    	elif self.is_leaf():
    		self.csub = None
    	else:
    		self.csub = None
    		if self == None:
    			self.csub = None
    			pass
    		elif re.match("^Ssub$",self.label):
    	   		for child in self.children:
    				if re.match("^CS$",child.label):
    					if child.word.lower() in liste_cs_vides:
    						self.csub=False
    					else:
    						self.csub=True
    					break
    		else:
    			self.csub=None
    		for child in self.children:
    			child.conjsub_annotate()
    #FRANCOIS:
    # ajoute à chaque noeud de type 'S' ou 'V', un champ 'mood':
    # Les 'V' propagent leur mood vers leur mère (atention priorité des moods au sein des VN)
    # Les 'S' ne propagent pas leur mood, sauf s'ils ont pour mère un COORD
    # Les 'S' et 'COORD' , si elles nont pas de mood, recoivent le mood 'nominal'			
    def mood_annotate(self):
    	if not self.label:
    		self.mood=None
    		for child in self.children:
    			child.mood_annotate()
     	elif self.is_leaf():
     		self.mood=None
     	elif self.is_terminal_sym():
    		if self.label == "V":
    			self.mood="indicatif"
    		elif self.label == "VS":
    			self.mood = "subjonctif"
    		elif self.label == "VPP":
    			self.mood = "part_past"
    		elif self.label == "VPR":
    			self.mood = "part_pres"
    		elif self.label == "VINF":
    			self.mood = "infinitif"
    		elif self.label == "VIMP":
    			self.mood = "impératif"
    		else:
    			self.mood = None
    		self.children[0].mood=None
    		#print indent+">TERM:"+self.label+" --> "+str(self.mood)
    		return self.mood
    	else:
    		if self == None:
    			pass
    		# Pour les labels de type S* ou V* pouvant porter l'information mood
    		elif self.label in ['SENT','Ssub','Srel','Sint','VPinf','VPpart','VN']:
    			#print indent+">SV*:"+self.label+"("+self.word+")"
    			moods = []
    			mood_vn = None
    			for child in self.children:
    				m = child.mood_annotate()
    				if child.label == "VN":
    					mood_vn = m
    				elif m != None:
    					moods.append(m)
    			if self.label == "VN":
    				for m in ["indicatif","infinitif","subjonctif","impératif","nominal","part_pres","part_past"]:
    					if m in moods:
    						moods=[m]
    						break
    			elif mood_vn <> None:
    				moods = [mood_vn]

    			if len(moods) == 1:
    				self.mood = moods[0]
    			elif len(moods) == 0:
    				self.mood = 'nominal'
    			else:
    				for m in ["part_past","part_pres","nominal","impératif","subjonctif","infinitif","indicatif"]:
    					if m in moods:
    						self.mood = m
    						break
    			# Si le label est de type S*, l'information mood ne remonte pas			
    			if self.label in ['SENT','Ssub','Srel','Sint']:
    				#print " --> "+str(None)
    				return None
    			# Si le label est de type V*, l'information mood peut remonter
    			else:
    				#print indent+" --> "+str(self.mood)
    				return self.mood
    		# Pour le label COORD qui peut porter l'information mood quand il coordonne des propositions ou des groupes verbaux
    		# (le type de ce qu'il coordonne est inféré à partir du type de son coordonnant droit - l'un de ses fils)		
    		elif self.label in ["COORD"]:
    			#print indent+">COORD:"+self.label
    			moods=[]
    			for child in self.children:
    				child.mood_annotate()
    				if child.label in ['SENT','Ssub','Srel','Sint','VPinf','VPpart','VN']:
    					#print child.label+"-"+str(child.mood)
    					moods.append(child.mood)
    			if len(moods) == 0:
    				self.mood = "nominal"
    			elif len(moods) == 1:
    				self.mood = moods[0]
    			else:
    				for m in ["indicatif","infinitif","subjonctif","impératif","nominal","part_pres","part_past"]:
    					if m in moods:
    						self.mood=m
    						break
    			#print indent+" --> "+self.mood
    			return self.mood
    		else:
    			#print indent+">LABEL:"+self.label+" --> "+str(None)
    			for child in self.children:
    				child.mood_annotate()
    			self.mood = None
    			return None

    def copula_annotate(self,copula_wordlist):
        if not self.is_leaf():
            self.cop = 'n'
            for child in self.children:
                child.copula_annotate(copula_wordlist)
            if self.label == "VN" :
                head = None
                for child in self.children:
                    if child.head:
                        head = child
                if head <> None:
                    if head.word in copula_wordlist:
                        head.cop = 't'
                        self.cop = 't'
                    else:
                        self.cop = 'f'
                        head.cop = 'f'
           
    		   			
    # Recursively annotate heads, according to a given head rule table
    # i.e. marks head=True on head node, headindex=i on a node, if the ith daughter is its head
    # If decorate==True : marks word=the lexical head on each node
    # AND : also marks linearid : rank in sentence according to linear order
    def head_annotate(self, headtable, lid=0, decorate=True):

        if self.is_terminal_sym(): 
            self.set_lid(lid)
            if decorate:
                self.set_word(self.children[0].label)
            return lid + 1

        for child in self.children:
            lid = child.head_annotate(headtable, lid, decorate)

        cf_rule = self.node_to_rule()
        i = headtable.get_child_index(cf_rule)
        if i <> None:
            self.mark_headchild(i)
            if decorate:
                self.set_word(self.children[i].get_word())
                self.set_lid(self.children[i].get_lid())
        else:
            pass
#           sys.stderr.write("WARNING:Missing head for rule: " + cf_rule_tostring(cf_rule) + '\n')
#           sys.stderr.write("WARNING:Missing head for tree: ")
            #sys.stdout.write(self.printf().encode('iso-8859-1')+'\n')
#           sys.stderr.write(":: "+self.printf()+'\n')
        return lid

    #Moves the functional annotations on the clitics down to the VN.
    def clitics_downwards(self):
        if not self.is_leaf():
            if self.label == "VN":
                if self.funlabel <> None:
                    flist = re.split("/",self.funlabel)
                    self.funlabel=None
                    for child in self.children:
                        if child.label in ["CLS","CLO","CLR","CL"] and len(flist) > 0:
                            child.funlabel = flist[0]
                            flist = flist[1:]
                        child.clitics_downwards()
                else:
                    for elt in self.children:
                        elt.clitics_downwards()
            else:
                for elt in self.children:
                    elt.clitics_downwards()

	
	#FRANCOIS:
	# pour chaque noeud, ajoute un champ start_lid et end_lid, correspondants aux feuilles maximales projetées par ce noeud
    def width_annotate(self,lid=-1):
    	if self.is_terminal_sym():
    		lid+=1
    		self.start_lid = str(lid)
    		self.end_lid = str(lid)
    	else:
			for child in self.children:
				child.width_annotate(lid)
				lid = int(child.end_lid)
			self.start_lid = self.children[0].start_lid
			self.end_lid = self.children[-1].end_lid
	
	#FRANCOIS:
	# suppression de tous les symboles PONCT de l'arbre
	def suppr_PONCT(self):
		if self.is_terminal_sym():
			pass
		else:
			new_children=[]	
			for child in self.children:
				if child.label == 'PONCT':
					pass
				elif child.children[0].is_leaf() and child.children[0].label in [',','.','!','?','_','-',' ',';','(',')','[',']','{','}','--RBR--','--LBR--','<C>','<D>']:
					print "-- Attention: la ponctuation:\""+child.children[0].label+"\" a été prise pour un\""+child.label+"\" !"
					pass
				else:
					new_children.append(child)
					child.suppr_PONCT()
			self.children=new_children		
	
	#FRANCOIS:
	# application de tagfixes sur tous les préterminaux de l'arbre
    def tagfixes(self,tagfix):
		if self.is_terminal_sym():
			self.label = tagfix.map(self.label)
		else:
			for child in self.children:
				child.tagfixes(tagfix)
			
	#FRANCOIS:
	# pour chaque noeud, ajoute un champ 'fine_type'
	# correspondant à la catégorie de sa tete
    def fine_types_annotate(self):
		self.fine_type=None
		if self.is_terminal_sym():
			self.fine_type=affine(self.label)
			return affine(self.label)
		else:
			fine_type = None
			for child in self.children:
				child.fine_types_annotate()
				if child.head:
					fine_type = affine(child.label)
			self.fine_type=affine(fine_type)
			return affine(fine_type)

    def find_head(tree):
        default_head = None
        for child in tree.children:
            if child.head:
                return child
            else:
                default_head = child
        return default_head

    ########################
    #Printing Functions
    ########################


    #Pretty prints the tree (PTB flat)
    #Actually returns a string
    def printf(self, feats={}):
        if self.is_parse_failure():
          return '(())'  
        elif self.is_leaf():
           return self.print_node(feats)
        else:
            str = '('+self.print_node(feats)
            for child in self.children:
#                try:
                str = str + ' '+ child.printf(feats)
                #except UnicodeDecodeError:
                #    str = str + ' '+ child.printf(feats).encode('iso-8859-1')                    
            str = str + ')'
            return str
        
    #Pretty prints the tree (PTB indented)
    #Actually returns a string
    def pprint(self,feats={}):
        return self.printr('',feats)
    
    def printr(self,indent,feats={}):
        if self.is_parse_failure():
             return '(())'  
        elif self.is_leaf():
           return self.print_node(feats)
        else:
            indent = indent + '  '
            str = '('+self.print_node(feats)
            if (not self.is_leaf()) and (len(self.children) == 1):# and (self.children[0].is_leaf()) : #flattening preterminal nodes to flatten the overall tree
                 str = str + '  ' + self.children[0].printr('',feats)+')'
            else:
                str = str + '\n'
                for child in self.children:
                    str = str + indent + child.printr(indent+'  ',feats)+'\n'
                str = str[:-1]+')'
            return str

    #Pretty prints the tree (Indented for use with Avery Andrews Latex tree drawing package)
    #Actually returns a string
    def print_latex(self):
        return self.printrl('')
    
    def printrl(self,indent):
        if self.is_leaf():
           return self.print_node_latex()
        else:
            indent = indent + '  '
            str = self.print_node_latex()+'\n'
            for child in self.children:
                str = str + indent + child.printrl(indent+'  ')+'\n'
            str = str[:-1]
            return str
	
    #Returns a string with the label of the node
    def print_node_raw(self):
	return self.label

    #Returns a string with the label and features of the node if it exists
    def print_node_ext(self,sep):
	if (self.subcat<> None) and self.CPLX_CAT:
	    return self.label+sep+self.subcat
	return self.label

    #Returns a string with the label and features of the node if it exists
    def print_full_node(self,isep,esep):
	if (self.funlabel <> None) and self.FUN_CAT:
            # marie : don't change '-' into '_' in function names
            # cf. is already done while reading
	    #return self.print_node_ext(isep)+esep+re.sub('-','_',self.funlabel)
            return self.print_node_ext(isep)+esep+self.funlabel
	return self.print_node_ext(isep)

    #Returns a string with the label and features of the node if it exists
    def print_compounds(self,isep,esep):
	if (self.compound == True) and self.CPD_CAT:
	    return self.print_full_node(isep,esep)+esep+'C'
	return self.print_full_node(isep,esep)
 
   #Returns a string with the morphology of the node if it exists
    def print_morphology(self,isep,esep):
        if self.MPH_CAT:
            mph = self.gen_full_morpho_symbol()
            if mph <> '':
                return self.print_compounds(isep,esep)+esep+mph
        return self.print_compounds(isep,esep)

    #Returns a string with the label and features of the node if it exists
    # marie : feats is a dict of dict
    # example : feats = {'lemma':{}} ===> each node having a non-None lemma attribute appears as 
    def print_node(self, feats={}):
        # marie : separator between cat and function is now '-'
        # => thus the 'esep' is '-'
	#res = self.print_morphology('_','_')
        res = self.print_morphology('_','-')
        #replaces parentheses in the input by other symbols so that parentheses are well defined metacharachters
	res = re.sub('\(',LBR,res)
	res = re.sub('\)',RBR,res)
        # marie : outputs features if required
        for feat in sorted(feats.keys()):
            if feat in self.__dict__ and self.__dict__[feat] <> None:
                val = self.__dict__[feat]
                # if only a specific value is searched for:
                if 'val' in feats[feat]:
                    searchedval = feats[feat]['val']
                    if val == searchedval:
                        if not (isinstance(val, str) or isinstance(val, unicode)):
                            val = str(val)
                        val = val.replace('(', 'LBR')
                        val = val.replace(')', 'LBR')
                        res = res + '{'+feat+'='+val+'}'
                else:
                    if (isinstance(val, str) or isinstance(val, unicode)):
                        val = str(val)
                    val = val.replace('(', 'LBR')
                    val = val.replace(')', 'LBR')
                    res = res + '{'+feat+'='+val+'}'

	return res

    def print_node_latex(self):
	res = self.print_compounds('_','-')
          #puts math symbols in the input in latex math mode
	res = re.sub('_','$_$',res)
	return res

######################################################################
#Returns a string from a list of nodes where all the nodes labels are separated by the charachter sep 
def print_node_list_raw(sep,list):
    if list <> None :
	str = list[0].print_node()
	for node in list[1:]:
	    str = str + sep + node.print_node()
	return str
    return None

#Returns a string from a list of nodes where all the nodes labels are separated by the charachter sep 
def join_node_list(sep,list):
    if list <> None and len(list) > 0 :
	str = list[0].print_node()
	for node in list[1:]:
	    str = str + sep + node.print_node()
	return str
    return ''


#Returns a string from a list of nodes where all the nodes labels are separated by a whitespace
def print_node_list_brown(list):
    return print_node_list_raw(' ',list)

#Returns a string from a list of nodes where all the nodes labels are separated by a newline  
def print_node_list_ims(list):
    return print_node_list_raw('\n',list)

#Returns a string from a list of nodes where all the nodes labels are separated by a newline  
def print_node_list_tnt(list):
    return print_node_list_raw('\n',list)+'\n'

#Prints a cfg rule
def print_cfg_rule(rule):
    prule = rule[0].print_node()+ " --> "
    for node in rule[1:]:
        prule = prule + " " + node.print_node()
    return prule
        
#Prints a list of rules (each rule on a separate line)
def print_cfg_rule_list(list):
    str = ""
    for rule in list:
        str = str + print_cfg_rule(rule) + "\n"
    return str[:-1]

#Prints words and POS following the IMS convention
def print_pos_list_ims(list):
    str = "<s>"
    for i in range(0,len(list)-1,2):
        str = str + list[i].print_node()+'\t'+list[i+1].print_node()+"</s>\n<s>"
    return str[:-3]

def print_pos_list_tnt(list):
    str = ""
    for i in range(0,len(list)-1,2):
        str = str + list[i].print_node()+'\t'+list[i+1].print_node()+"\n"
    return str

#Prints words and POS following the Brown convention
def print_pos_list_brown(list):
    str = ""
    for i in range(0,len(list)-1,2):
        str = str + list[i].print_node()+'/'+list[i+1].print_node()+" "
    return str

#Returns a tree that represents a parse failure
def parse_failure():
    root =  LabelledTree("ROOT_FAILURE")
    root.add_child(LabelledTree('Failure'))
    return root

# pretty print of rule in tuple format (as output from node_to_rule)
def cf_rule_tostring(cf_rule):
    return cf_rule[0] + ' --> ' + ' '.join(cf_rule[1:])

#Tests
# b = LabelledTree('NP')
# b.set_feature('def')
# b.set_function('subj')
# e = LabelledTree("John")
# b.add_child(e)
# c = LabelledTree('VP')
# c.set_compound_true()
# c.add_child(LabelledTree('V'))
# c.add_child(LabelledTree('NP'))
# a = LabelledTree('S',[b,c])
# a.add_child(LabelledTree('PP'))
# #
# # y = a.cfg_rules()
# # print print_cfg_rule_list(y)
# print a.pprint()
# a.binarize_left()
# print a.pprint()



#
# y = a.tree_yield()
# z = a.pos_yield()
# print print_node_list_ptb(y)
# print print_node_list_ims(y)
# print print_node_list_ptb(z)
# print print_node_list_ims(z)
# print a.pprint()
# print a.printf()
# print a.print_latex()


#FRANCOIS
#b = LabelledTree('NP')
#print b.pprint()
#b.set_feature('def')
#b.set_function('subj')
#e = LabelledTree("John")
#b.add_child(e)
#c = LabelledTree('VP')
#c.set_compound_true()
#c.add_child(LabelledTree('V'))
#c.add_child(LabelledTree('NP'))
#a = LabelledTree('S',[b,c])
#a.add_child(LabelledTree('PP'))
#print a.pprint()
#print "-------------"
#z = LabelledTree('S',[c,b])
#z.add_child(LabelledTree('PP'))
#print z.pprint())
