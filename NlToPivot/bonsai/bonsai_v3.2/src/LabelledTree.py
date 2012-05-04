#!/usr/bin/env python -OO
# -*- coding: iso-8859-15 -*-

from dgraph import *
from parser_constants import *
from utils import *

#Constant methods

def parse_failure():
      """
      This returns a special tree encoding a parse_failure.

      @return : a labelled tree encoding a parse failure
      @rtype : a Labelled tree
      """
      return LabelledTree(None)


def add_dummy_root(tree):
      """
      This adds a node rooted with an empty string as immediate ancestor of the actual root of this tree
      This is used to add a root compliant with PennTreebank style format

      @return : The dummy root of this tree being the new pointer on this tree
      @rtype: a LabelledTree
      """
      return LabelledTree('',children=[tree])


def remove_dummy_root(tree):
      """
      This removes a node rooted with an empty string as immediate ancestor of the actual root of this tree
      This is used to remove the dummy root encoded in PennTreebank style format
      
      @return : a pointer on the child of the dummy root or the root itself if it was not a dummy root
      @rtype: a LabelledTree
      """
      if len(tree.children) == 1 and tree.label == '':
	    return tree.first_child()
      else:
	    return tree

def tree_diff(treea,treeb,ignore_root=False,ignore_leaves=False):
      """
      This performs a basic diff on two trees.
      The function returns 3 sets of Parseval-style triples of the form(Label,leftindex,rightindex)
      * The first set is the set of shared triples
      * The second set is the set of triples where a differs from b
      * The third set is the set of triples where b differs from a
      
      @param treea: a tree to be compared
      @type treea: LabelledTree
      @param treeb: a tree to be compared
      @type tree: LabelledTree
      @param ignore_root:flag. set to true ignore roots for comparisons
      @type ignore_root: boolean
      @param ignore_leaves : set to true, ignore leaves for comparisons
      @type ignore_leaves : boolean
      @return: a tuple of sets of triples (shared, a diffs, b diffs)
      @rtype: a tuple of sets of triples
      """

      triplesa = set(treea.triples(ignore_root=ignore_root,ignore_leaves=ignore_leaves))
      triplesb = set(treeb.triples(ignore_root=ignore_root,ignore_leaves=ignore_leaves))
      ta_inter_tb = triplesa.intersection(triplesb)
      ta_minus_tb = triplesa.difference(triplesb)
      tb_minus_ta = triplesb.difference(triplesa)
      return (ta_inter_tb,ta_minus_tb,tb_minus_ta)


class TreebankTree:
      """
      This class represents a tree as ussed in a treebank.
      This encapsulates a LabelledTree and associates it with meta information such as 
      its identifier in the treebank (and possibly its annotator or the date it has been annotated)
      """
      def __init__(self,tree,identifier=-1):
            """
            This creates a Treebank Tree with specified identifier

            @param tree : the tree annotated
            @type tree : LabelledTree
            @param identifier: the identifier of the tree in the treebank
            @type identifier: integer
            """
            self.tree = tree
            self.identifier= identifier

      def do_xml_string(self):
            """
            This returns a Treebank Tree encoded in French Treebank XML
            
            @return: a multiline string encoding this tree in XML
            @rtype: string
            """
            return self.tree.do_xml_string(identifier=self.identifier,root=True)

      def __str__(self):
            """
            This returns a Treebank Tree encoded in French Treebank XML
            
            @return: a multiline string encoding this tree in XML
            @rtype: string
            """
            return self.do_xml_string()

      def to_db_string(self, colsep='\t',identifier=True,display_functions=False,display_features=False):
            """
            This does a string representation of this tree in database format.
            that is one tree per line with tab separated fields (by default)

            @param colsep: the column separating char
            @type colsep: char
            @param identifier: boolean indicating whether the sentence identifier has to be output
            @type identifier: boolean
            """
            columns=[self.tree.do_flat_string(sep=',',display_functions=display_functions,display_features=display_features,untyped=True)]
            if identifier:
                  columns = [str(self.identifier)]+ columns
            return colsep.join(columns)


class LabelledTree:
      """
      This class implements ordered n-ary branching labelled trees as used in Constituent Parsing
      Trees may be optionally decorated with atomic features, <attribute:value> couples
      The class supposes that leaves have no direct siblings.

      Fields :
      * LABEL
      * CHILDREN
      * FEATURES
      Opts Fields (= introduced by some functions otherwise are absent from the internal dictionary)
      * IDX       (introduced by index_yield)
      * HEAD_IDX  (introduced by head_annotate)
      * HEAD_WORD (introduced by head_annotate)
      * IS_HEAD   (introduced by head_annotate)
      * BASECAT   (introduced by do_base_cat)
      * FCT       (introduced by set_funvalue)
      * COMPOUND  (introduced by XMLreader)
      * GAP       (todo ?)
      * POINTER   (todo ?)
      @author : Benoit Crabbé
      """
      def __init__(self,label,children=None,features=None):
            """
            This creates a new LabelledTree node with a label, some children and some features.
            By convention the leaf node label is a word, non leaf nodes' labels are Grammatical categories
            By convention features encode grammatical (sub-)categories, 
            metafeatures like a tree index are encoded as additional fields by relevant methods
            
            @param label: a String being the label of that node
            @type label: String
            @param children: the children of this tree ordered linearly
            @type children: list of Labelled Trees
            @param features: a dictionary of attribute value couples 
            @type features: a dictionary <String:String>
            """
            self.label = label
            self.children = children
            if features == None:
                  self.features = {}
            else:
                  self.features = features

      def is_parse_failure(self):
            """
            Indicates whether this tree is a parse failure or a parse success

            @return: True if the tree is a parse failure False otherwise
            @rtype: boolean
            """
            return self.label == None

      def isHead(self):
            """
            Returns true if this node is marked as head, false otherwise
            
            @return: a boolean indicating whether this node is a head
            @rtype:boolean
            """
            return self.__dict__.has_key('is_head') and self.is_head == True

      def is_leaf(self):
            """
            Indicates if this tree node is a leaf or a non leaf node

            @return: True if the tree is a leaf False otherwise
            @rtype: boolean
            """
            return self.children == None

      def is_tag(self):
            """
            Indicates if this tree node has the structural position of a PoS Tag 

            @return : True if this tree has only one child being a leaf, false otherwise
            @rtype:boolean
            """
            return self.children <> None and len(self.children) == 1 and self.children[0].is_leaf()



      def first_child(self):
            """
            Returns the leftmost child of this tree immediately dominated by this tree

            @return : The leftmost child of this tree or this tree if this tree is a leaf
            @rtype: LabelledTree
            """
            if self.is_leaf() or self.children == []:
                  return self
            else:
                  return self.children[0]
            
      def last_child(self):
            """
            Returns the rightmost child of this tree immediately dominated by this tree

            @return : The rightmost child of this tree or this tree if this tree is a leaf
            @rtype: LabelledTree
            """
            if self.is_leaf() or self.children == []:
                  return self
            else:
                  return self.children[-1]

      def left_corner(self):
            """
            Returns the leftmost leaf of the overall tree below this node. 
            This is the reflexive, transitive leftcorner relation

            @return: The leftmost desendant of this node being a leaf of the tree or this tree if it is a leaf
            @rtype: LabelledTree
            """
            plc = None 
            clc = self
            while clc <> plc:
                  plc = clc
                  clc = clc.first_child()
            return clc

      def right_corner(self):
            """
            Returns the rightmost leaf of the overall tree below this node. 
            This is the reflexive, transitive rightcorner relation

            @return: The leftmost descendant of this node being a leaf of the tree or this tree if it is a leaf
            @rtype: LabelledTree
            """
            prc = None 
            crc = self
            while crc <> prc:
                  prc = crc
                  crc = crc.last_child()
            return crc

      def add_child(self,child):
            """
            This appends a child immediately dominated by this node at the rightmost position of existing children

            @param child: the child to be added
            @type child: LabelledTree
            """
            if self.is_leaf():
                  self.children = [child]
            else:
                  self.children.append(child)

      def add_children(self,childlist):
            """
            This is furcation. The children in the list are added as rightmost children
            of this node in the order specified in the list.

            @param childlist: the list of children to be added
            @type childlist: a list of LabelledTree
            """
            if self.is_leaf():
                  self.children = childlist
            else:
                  self.children.extend(childlist)
      

      def get_funvalue(self,dummyval = None):
            """
            This returns the function value associated to this node if there is one,
            otherwise returns the dummy value

            @param dummyval: a dummy value to return if there is no function associated to this node
            @type dummyval: string
            @return: the function of this node
            @rtype: string
            """
            if self.has_function():
                  return self.fct
            else:
                  return dummyval


      def set_funvalue(self, funval=None):
            """ 
            This sets an attribute 'fct' with value funval.

            @param funval: a string being a function name
            @type funval: a string
            """
            self.fct = funval


      def has_function(self):
            """
            This returns true if this tree has a non null fct attribute, false otherwise

            @return: true if this tree has a non null fct attribute, false otherwise
            @rtype: a boolean
            """

            return self.__dict__.has_key('fct') and self.fct <> None
      

      def has_basecat(self):
            """
            This returns true if this tree has a non null basecat attribute, false otherwise

            @return: true if this tree has a non null basecat attribute, false otherwise
            @rtype: a boolean
            """

            return self.__dict__.has_key('basecat') and self.basecat <> None


      def set_basecat(self, basecat=None):
            """ 
            Base cat is a category without features encoded in its symbol
            
            @param basecat : a string being an atomic base category
            @type basecat: string
            """
            self.basecat = basecat

      def tree_yield(self):
            """
            This returns the yield of the tree below this node.

            @return: the yield of this tree
            @rtype: a list of LabelledTree
            """
            if self.is_leaf():
                  return [self]
            else:
                  cyield = []
                  for child in self.children:
                        cyield.extend(child.tree_yield())
                  return cyield
      # marie : a ajouter
      def tree_yield_str(self,sep=' '):
            """
            Returns the yield of the tree, as a <sep>-separated string of its terminals"""
            return sep.join([x.label for x in self.tree_yield()])

      def tag_yield(self):
            """
            This returns the yield of the tree below this node.
            Here the yield is made of couples (POS tags,wordform)
            
            @return: the yield of this tree or empty list if the tree is a leaf
            @rtype: a list of couples (as 2-tuples of LabelledTree)
            """
            if self.is_leaf():
                  return []
            if self.is_tag():
                  return [(self,self.first_child())]
            else:
                  cyield = []
                  for child in self.children:
                        cyield.extend(child.tag_yield())
                  return cyield
  
      def is_compound(tree):
            """
            This indicates if a Labelled tree node has been annotated as a compound
            
            @return : a boolean indicating if this node has a compound flag
            @rtype: boolean
            """
            return tree.__dict__.has_key('compound') and self.compound

      def append_yield(self,node_list):
            """
            This appends a new yield to this tree if the node list has the same size as the actual yield
            otherwise does nothing. Typically used to append the words under the PoS tags.
            
            @param node_list: the new yield to be appended
            @type node_list: a list of equal size to the size of the yield of this tree
            @return a flag indicating whether the yield is appended or not
            @rtype : boolean
            """
            cyield = self.tree_yield()
            if len(node_list) == len(cyield):
                  for i,node in enumerate(cyield):
                        node.add_child(node_list[i])
                  return True
            return False

      def has_pointer(self):
            """
            Indicates if this node hqs a pointer to another node.
            Only gap nodes have pointers directed on their fillers

            @return : true if there is a non null pointer to another node, false otherwise
            @rtype: boolean
            """
            return self.__dict__.has_key('pointer') and self.pointer <> None

      def is_gap(self):
            """
            Indicates if this node is a gap

            @return: true if this node has a gap variable set to true, false otherwise
            @rtype: boolean
            """
            return self.__dict__.has_key('gap') and self.gap

      def index_leaves(self):
            """
            This explicitly indexes the leaf nodes of the trees according to their index wrt the sentence positions
            For a sequence of leaves w0...wN , the leaves are indexed from 0 to N according to the linear order
            The non leaf nodes are not indexed.
            This internally adds the field 'idx' to each leaf node
            """
            cyield = self.tree_yield()
            for i in range(len(cyield)):
                  cyield[i].idx = i

      def triples(self,ignore_root=False,ignore_leaves=False,root=True):
            """
            This recursively turns a tree into a list of triples (3-tuples)
            Each triple is of the form (label,lidx,ridx) where lidx and ridx are computed as for typical parsing purposes. 
            These triples can then be used for performing tree diffs and related operations.

            @param ignore_root: if set to true, the function does not emit a triple for the root of this tree
            @type ignore_root: boolean
            @param ignore_leaves: if set to true the function does not emit triples for the leaves
            @type ignore_leaves: boolean
            @return: a list of triples
            @rtype: a liste of (3)tuples
            """
            if root:
                  #ensures that leaf nodes are properly indexed according to the current root/state of the tree
                  self.index_leaves()
                  
            if self.is_leaf():
                  if not ignore_leaves:
                        return [(self.label,self.idx,self.idx+1)]
                  else:
                        return []
            else:
                  if ignore_root:
                        tlist = []
                        ignore_root = False
                  else:
                        tlist = [(self.label,self.left_corner().idx,self.right_corner().idx+1)] #...bulky isn't it ?
                  for child in self.children:
                        tlist.extend(child.triples(ignore_root=ignore_root,ignore_leaves=ignore_leaves,root=False)) 
                  return tlist

      def get_form(self,dummy_val=None):
            """  
            Returns the word form of a node known to be a preterminal (father of a leaf)
            """
            if self.is_tag():
                  return self.children[0].label
            else:
                  return dummy_val

      def head_node(self):
            """
            This returns the leaf node being the head of a given node
            
            @return : the head of this constituent
            @rtype: LabelledTree
            """
            ctree = self
            while not ctree.is_tag():
                  ctree = ctree.head_child()
            return ctree.first_child()

      def head_tag(self):
            """
            This returns the node being the preterminal above a given node's head word
      
            @return : the headnode's preterminal node of this constituent or None if this node is a leaf
            @rtype: LabelledTree
            """
            if self.is_tag():
                  return self
            elif self.is_leaf():
                  return None
            else:
                  tagtree = self
                  lookahead = tagtree.head_child()
                  while not lookahead == None:
			tagtree = lookahead 
			lookahead = lookahead.head_child()
                  return tagtree

      def head_child(self):
            """
            Returns the immediate child of this node marked as head
            
            @return : The immediate child marked as head or None if there is none (the node is a leaf)
            @rtype : Labelled Tree
            """
            if self.is_tag():
                  return None
            else:
                  for child in self.children:
			if child.isHead():
                              return child

      def annotate_all(self,tagfixer,propagation_table):
            """
            This performs a sequential annotation of the trees by:
            (1) indexing the leaves
            (2) Decomposes labels into basecats and features
            (3) Marking heads
            (4) Annotating functions
            @todo: finish it off -> functional annotation
            """
            self.index_leaves()
            self.do_base_cats(tagfixer)
            self.___head_annotate(propagation_table)

      def do_base_cats(self,tagfixer):
            """
            This populates a tree by recursively setting its basecat and features from the tree labels using a map specified by tagfixer.
            
            @param tagfixer: a tagfixer used for the analysis of labels
            @type tagfixer: TagFixer
            """
            if not self.is_leaf():
                  for child in self.children:
                        child.do_base_cats(tagfixer)
                  (self.basecat,features) = tagfixer.map_tag(self.label)
                  self.set_features(dict(features))


      def set_features(self,featsb):
            """
            This merges the feature structures dictionary featsb with the current feature dictionary to yield a single one.
            
            @warning: this is NOT unification !!! (this is a crappy merge instead :-)
            The function basically does the union of the two dictionaries.
            In case of value conflicts, the value of featsb take precedence over current feature values .
            In other words, this is a non monotonic crappy merge !
            
            @param featsb: a feature structure
            @type featsb : a dictionary of attribute/values
            @return : the merge of those 2 feature structures
            @rtype : a dictionary of attribute/values
            """
            self.features = dict(self.features.items() + featsb.items())


      def set_feature(self,attribute,value):
            """
            This attaches a feature with the given attribute and value to the node.
            In case the feature is already present with a conflicting value the current value replaces the former !

            @param attribute: the attribute of the feature
            @type attribute: String
            @param value: the value of the feature
            @type value: String 
            """
            self.features[attribute] = value
      
      def has_such_features(self,featsb):
            """
            This verifies whether the node bears each feature defined in the dictionary featsb
            @param featsb: a feature structure
            @type featsb : a dictionary of attribute/values
            @return : whether such features are satisfied
            @rtype : boolean
            """
            for attr in featsb:
                  if not(attr in self.features and self.features[attr] == featsb[attr]):
                        return False
            return True

      def ___head_annotate(self,ptable,root=True):
            """
            This recursively head annotates the tree using the specified propagation table.
            This specifically adds : 
            * a boolean flag 'is_head' to every non terminal node. The root is a head as well
            * a 'head_word' field to every non terminal node being the wordform of the head
            * an 'head_idx' field to every non terminal node, being the index of the head on the input
            The function ensures that every non terminal as a child annotated as head.
            
            @precondition: this function assumes that the leaves have been properly indexed
            @param ptable: the propagation table used for marking heads
            @param ptable: PropagationTable
            """
            if self.is_tag():
                  leaf = self.first_child()
                  self.head_word = leaf.label
                  self.head_idx = leaf.idx 
            else:
                  for child in self.children:
                        child.is_head = False
                        child.___head_annotate(ptable,root=False)
                  idx = ptable.head_index(self,self.children)
                  # marie debug : do not mark anything if head is unknown at that level
                  if idx <> None:
                        self.children[idx].is_head = True
                        self.head_word = self.children[idx].head_word
                        self.head_idx = self.children[idx].head_idx
                  else:
                        self.head_idx = None
                        self.head_word = None
                  if root:
                        self.is_head = True

      def do_node_string(self,feats=None,sep='/',typedef=None,dummy_val="*",display_features=False,display_functions=False,untyped=False):
            """
            This returns a string representation of a node.
            If the node has features the features *values* are appended to the node label. 
            in an order specified by typedef or randomly if typedef equals None.

            @param feats: a mask specifying which feature values should be printed out. 
            If feats is set to None, all relevant features are printed out 
            @type feats: a list of Strings (being relevant attributes for which values are to be printed)
            @param sep:the symbol used to separate the feature values fields  
            @type sep: a string (typically a char)
            @param typedef: a list of attributes specifying which features are to be outputted in which order
            @type typedef: a list of strings
            @param dummy_val: a value used for features with no known value
            @type dummy_val: string
            @param untyped : display the feature attributes as well for instanciated features. Features are displayed in square brackets as attribute=value pairs
            @type untyped:boolean
            @param display_features:flag indicating whether to output features or not
            @type display_features:boolean
            @return : the string representation of this node
            @rtype : string
            """
            lbl = encode_const_metasymbols(self.label)            
            if self.isHead():
                  lbl = '*'+lbl+'*'
            elif display_functions and self.has_function():
                  lbl += '-'+ self.fct
            if display_features and self.features <> {}:
                  if typedef <> None:
                        flist = typedef
                  else:
                        flist = self.features.keys()
                  if feats <> None:
                        flist  = filter(lambda x: x in feats,flist)
                  if untyped:
                        vals = [attribute+'='+ encode_const_metasymbols(self.feature_value(attribute,dummy_val),ext=True) for attribute in flist if self.has_feature(attribute)]
                        return lbl+'-['+sep.join(vals)+']'
                  else:
                        vals = map(lambda x: encode_const_metasymbols(self.feature_value(x,dummy_val)),flist)
                        return sep.join([lbl]+vals)
            else:
                  return lbl
                         
      def feature_value(self,attribute,dummy_val=None):
            """
            Maps an attribute to its value. Returns the dummy val if the feature is not specified for this node.
            
            @param attribute: the feature's attribute
            @type:string
            @param dummy_val : a default value if this node does not specify anything for the attribute
            @type dummy_val: string
            @return: the feature value or the dummy_val if this node does not specify anything for the attribute
            @rtype : a string
            """
            if self.features.has_key(attribute):
                  return self.features[attribute]
            else:
                  return dummy_val

      def has_feature(self,attribute):
            """
            This returns true if this attribute has a non null value, false otherwise
            
            @param attribute: the attribute for which we want to know whether it has a value
            @type attribute: string
            @return: true if this attribute has a non null value, false otherwise
            @rtype:boolean
            """
            return self.features.has_key(attribute) and self.features[attribute] <> None
            

      def dependents(self,treeness=True,projective=True):
        """
        Provides the list of immediate dependents of this node head following their linear order.
        The dependencies extracted can be restricted to two forms:
        * To ensure the dependency graph is a tree, set the treeness param to true (ignores control like links). 
        * To ensure the dependency graph is a projective tree, set the projective param to true (ignores filler-gap links)
        The latter implies the former.
        Note that these restrictions do guarantee a formal specification, not that the resulting dependencies are linguistically sound !!

        @param treeness: ensures that the extracted dependencies have a tree structure (no cycles)
        @type treeness: boolean
        @param projective: ensures that the extracted dependencies are strictly projective
        @type projective:boolean
        @return a list of LabelledTree being leaves of the global tree
        @rtype : a list of Labelled Tree
        """
        if self.is_tag():
            return []
        else:
            deplist = []
            for child in self.children:
                if child.isHead():
                    deplist.extend(child.dependents())
                elif projective or not child.is_gap():
                    if not treeness and child.has_pointer():
                        deplist.append(child.pointer.head_node())   
                    else:
                        deplist.append(child.head_node())
            return deplist

      def build_dependency_graph(self,treeness=True,projective=True):
        """
        This turns a LabelledTree into a dependency graph by reading off the dependencies.
        
        @param treeness: ensures that the extracted dependencies have a tree structure (no cycles)
        @type treeness: boolean
        @param projective: ensures that the extracted dependencies are strictly projective
        @type projective:boolean
        @return: a labelled Dependency Graph
        @rtype: a DependencyGraph
        """
        dg = DependencyGraph()
        self.___build_dg(dg,treeness=treeness,projective=projective)
        return dg


      def ___build_dg(self,dg,treeness=True,projective=True):
        """ 
        This is the inner recursive backbone of the prev func.

        @param treeness: ensures that the extracted dependencies have a tree structure (no cycles)
        @type treeness: boolean
        @param projective: ensures that the extracted dependencies are strictly projective
        @type projective:boolean
        """
        if not self.is_tag():
            for child in self.children:
                if child.isHead():
                    child.___build_dg(dg,treeness=treeness,projective=projective)
                elif projective or not child.gap:
                    if not treeness and child.has_pointer():
                        gov = DepVertex(self.head_word,self.head_idx)
                        dep = DepVertex(child.pointer.head_word,child.pointer.head_idx)
                        dg.add_edge(gov,child.pointer.fct,dep)
                        child.pointer.___build_dg(dg,treeness=treeness,projective=projective) # !! introduces potential infinite loops (check this out later on...)
                    else:
                        gov = DepVertex(self.head_word,self.head_idx)
                        dep = DepVertex(child.head_word,child.head_idx)
                        dg.add_edge(gov,child.fct,dep)
                        child.___build_dg(dg,treeness=treeness,projective=projective)

      def __str__(self):
            """
            This pretty prints a PennTreebank style representation of this tree encoded as an indented multiline string.
            Each line of the string ends up by a token.
            Note that parse failures are encoded as '(())'
            return self.pprint(padding=0)
            """
            return self.do_pretty_string()


      def do_pretty_string(self,padding=0,failure_code='(())',display_functions=False,display_features=False,lc=True):
            """
            This pretty prints a PennTreebank style representation of this tree encoded as a indented multiline string.
            Each line of the string ends up by a token.
            Note that parse failures are encoded as '(())' unless an alternative code is specified as parameter

            @param: failure_code: a code used to display parse_failures
            @type failure_code: String
            @param:sep: indicates which char to use for appending additional non terminal symbols features to the label at printout
            @type: a string (usually a single char)
            @param:sep: indicates which char to use for appending additional terminal symbols features to the label at printout
            @type: a string (usually a single char)
            @return: a String representation of this tree
            @rtype: a one line String
            """            
            if self.is_parse_failure():
                  return failure_code
            elif self.is_leaf():
                  return self.do_node_string()
            else:
                  str_node = self.do_node_string(display_functions=display_functions,display_features=display_features)
                  if lc:
                        res = ['(',str_node,' ']
                  else:
                        res = ['\n '.ljust(padding+1)+'(',str_node,' ']
                  padding += len(str_node)+2  
                  res.append(self.first_child().do_pretty_string(padding=padding,display_functions=display_functions,display_features=display_features,lc=True))
                  for child in self.children[1:]:
                        res.append(child.do_pretty_string(padding=padding,display_functions=display_functions,display_features=display_features,lc=False))
                  res.append(')')
                  return ''.join(res)


      def do_flat_string(self,failure_code='(())',display_functions=False,display_features=False,feats=None,sep='/',untyped=True):
            """
            This returns a PennTreebank style representation of this tree encoded as a flat one line string.
            This function outputs a format compliant with that of evalb and of standard parsers
            Note that parse failures are encoded as '(())' unless an alternative code is specified as parameter

            @param: failure_code: a code used to display parse_failures
            @type failure_code: String
            @param:sep: indicates which char to use for appending features at printout
            @type: a string (usually a single char)
            @return: a String representation of this tree
            @rtype: a one line String
            """            
            if self.is_parse_failure():
                  return failure_code  
            elif self.is_leaf():
                  return self.do_node_string()
            else:
                  return '(' + self.do_node_string(sep=sep,display_functions=display_functions,display_features=display_features,feats=feats,untyped=untyped) + ' ' + ' '.join(map(lambda child: child.do_flat_string(sep=sep,display_functions=display_functions,display_features=display_features,feats=feats,untyped=untyped),self.children))+')'


      def do_tag_string(self,failure_code='(())',wordsep=" ",tagsep='@',fsep='/',feats=None,tag_word=False,wordtypedef=None,dummy_val="-",display_features=False):
            """
            This pretty prints the POS tagged version of a phrase rooted by this tree.
            In case the parse tree is a parse failure it ouptuts the failure code instead
            
            @param wordsep: the string used to separate tokens
            @type wordsep: string
            @param tagsep: the string used to separate the tag and the word (usually a single char)
            @type tagsep: string
            @param fsep: the string used to separate the fields in a single symbol (usually a single char separating the fields of a tag)
            @type fsep: string
            @param tag_word: flag setting the word/tag order. set to true, the function will print the tag before the word, set to false it prints the word before the tag 
            @type tag_word: boolean
            @return: a pretty printed pos tagged string
            @rtype: String
            """
            if self.is_parse_failure():
                  return failure_code()
            else:
                  taglist =  self.tag_yield() 
                  if tag_word:
                        return wordsep.join(map(lambda x: x[0].do_node_string(sep=fsep,feats=feats,display_features=display_features,typedef=wordtypedef,dummy_val=dummy_val) + tagsep + x[1].do_node_string(sep=fsep,feats=feats,display_features=display_features,typedef=wordtypedef,dummy_val=dummy_val) ,taglist))
                  else:
                        return wordsep.join(map(lambda x: x[1].do_node_string(sep=fsep,feats=feats,display_features=display_features,typedef=wordtypedef,dummy_val=dummy_val) + tagsep + x[0].do_node_string(sep=fsep,feats=feats,display_features=display_features,typedef=wordtypedef,dummy_val=dummy_val) ,taglist))


      def do_xml_node(self,opening_tag=True):
            """
            This returns a French Treebank XML like representation of this node.
            
            @param opening_tag: if set to true, generates an XML starting tag, set to false it generates an XML end tag
            @type opening_tag: boolean
            @return : a string being the xml tag representation of this node
            @rtype: string
            """
            if self.has_basecat():
                  cat = self.basecat
            else:
                  cat = self.label
            if self.is_tag():
                  if opening_tag and len(self.features) > 0:
                        return '<w cat="'+cat + '" '+' '.join(map(lambda attribute:attribute+'="'+self.features[attribute]+'"' ,self.features.keys()))+'>'
                  elif opening_tag and len(self.features) == 0:
                        return '<w cat="'+cat + '">'
                  else:
                        return '</w>'
            else:
                  if opening_tag:
                        nodestr = '<'+cat
                        if self.has_function():
                              nodestr += ' fct="'+self.fct+'"'
                        if len(self.features) > 0:
                              return nodestr + ' '+' '.join(map(lambda attribute:attribute+'="'+self.features[attribute]+'"' ,self.features.keys()))+'>'
                        elif len(self.features) == 0:
                              return nodestr+'>'
                  else:
                        return '</'+cat + '>'
                  

      def do_xml_string(self,index=0,padding=0,root=False):
            """
            This returns a French Treebank like representation of this tree.

            @return : a string encoding the tree in french treebank xml format
            @rtype: a string
            """
            if self.is_leaf():
                  return self.label
            if self.is_tag():
                  return ''.ljust(padding)+self.do_xml_node()+ self.first_child().do_xml_string() + self.do_xml_node(opening_tag=False)
            else:
                  if root:
                        res = ''.ljust(padding)+'<'+self.label+ ' nb="' +str(index)+ '">\n'
                  else:
                        res = ''.ljust(padding)+self.do_xml_node()+'\n'

                  return res + '\n'.join(map(lambda child : child.do_xml_string(padding=padding+2),self.children)) + '\n'+''.ljust(padding)+'</'+self.label+'>'


      def do_sentence_string(self,wordsep=" "):
            """
            This pretty prints the sentence being the yield of this tree.
            
            @param wordsep: the string used to separate the tokens
            @type wordsep: string
            @return: the sentence being the yield of this tree
            @rtype: string
            """
            if self.is_parse_failure():
                  return failure_code()
            else:
                  cyield = self.tree_yield()
                  return wordsep.join(map(lambda x:x.label,cyield))



class PropagationTable:
    """
    This class is the head propagation/percolation table used as heuristic for annotating the heads of the trees.
    @see: Magerman 94, Collins 99, Arun 2005, Dybro-Johansen 2004.
    """
    def __init__(self):
        """
        This creates a new empty table that ensures to find a head for every rule.
        """
        self.prules = {}
        self.add_mapping("default",["*"],"R")

    def head_index(self,lhs,rhs):
        """
        This returns the index of the headnode in the right hand side of this rule.
        
        @param lhs: The left hand side symbol of the rule 
        @type lhs: The right hand side symbol of the rule
        @param: rhs: The right hand side of a grammatical rule
        @type rhs: a tuple of LabelledTree
        @return: the index of the head in the rule
        @rtype:integer
        """
        lhs = lhs.label
        if lhs not in self.prules:
            lhs = 'default'
        propag_rules = self.prules[lhs]
        for prule in propag_rules:
            (symbols,direction) = prule
            scanindex = range(len(rhs))
            if direction == "R": 
                scanindex.reverse()
            for index in scanindex:
                if (rhs[index].label in symbols or '*' in symbols):
                    return index
        # marie debug : control what happens if not found
        return None
    def add_mapping(self,lhs,value,direction):
        """
        This adds a mapping to the current propagation table.
        The order in which the mappings are added to the table is relevant.
        The first added mapping has priority over the next one (and so forth)
        
        @param lhs: a left hand side symbol 
        @type lhs: String
        @param value: a symbol to search for in the right hand side of a rule
        @type value: String
        @param direction: Says whether the search for this symbol starts leftwards or rightwards
        @type: a string being either "L" or "R"
        """
        if not self.prules.has_key(lhs):
            self.prules[lhs] = []
        self.prules[lhs].append((value,direction))


    def add_aliases(self, symbol, aliaslist):
        """
        Adds an alias to a given grammatical symbol. 
        This allows to easily extend a propagation table defined for a coarse symbol set
        to a propagation table defined for a finer grained symbol set
        
        @todo: this alias function is unsound since the lhs symbols are not duplicated
        @warning: this also supposes to add aliases after all mapping rules have been added
        
        @param symbol: the symbol being aliased
        @type symbol: String
        @param aliaslist: a list of alias for the symbol
        @type aliaslist: a list of Strings
        """
        for lhs in self.prules.keys():
            rules = self.prules[lhs]
            for rule in rules:
                (rhs,direction) = rule
                if symbol in rhs:
                    rhs.extend(aliaslist)


def ftb_symset4():
    """
    This returns a standard propagation table for French for a tagset dubbed symset4 
    being the best performing tagset on those treebanks.
    The table has been set up both on :
    * The French Treebank (FTB) 
    * The Modified French Treebank (MFT)       
    This also works for the base FTB/MFT tagsets where symbols are only the raw categories
    
    @return: a Propagation Table for French
    @rtype: PropagationTable
    """
    ptable = PropagationTable()
    ptable.add_mapping("AP",["A"],"R")
    ptable.add_mapping("AP",["N"],"R")
    ptable.add_mapping("AP",["V"],"R")
    ptable.add_mapping("AP",["COORD"],"L")
    ptable.add_mapping("AP",["PREF"],"L")
    # marie : a few cases of AP = AdP + AP
    ptable.add_mapping("AP",["AP"],"L")
    ptable.add_mapping("AP",["ADV"],"L")
    ptable.add_mapping("AdP",["ADV"],"R")
    ptable.add_mapping("AdP",["P"],"L")
    ptable.add_mapping("AdP",["P+D"],"L")
    ptable.add_mapping("AdP",["D"],"L")
    ptable.add_mapping("AdP",["C"],"L")
    ptable.add_mapping("AdP",["COORD"],"L")
#    # marie : for cases of sentences starting with a coord (present in original FTB as COORD)
#    ptable.add_mapping("COORD",["VN"],"L")
    ptable.add_mapping("COORD",["C"],"L")
    ptable.add_mapping("COORD",["PONCT"],"L")
    # marie debug : in case of missing conjunction or comma, the first node is generally the head (not the last)
    #ptable.add_mapping("COORD",["*"],"R") 
    ptable.add_mapping("COORD",["*"],"L") 
    ptable.add_mapping("NP",["N"],"L")
    ptable.add_mapping("NP",["P+PRO"],"L")
    ptable.add_mapping("NP",["PRO"],"R")
    ptable.add_mapping("NP",["A"],"R")
    ptable.add_mapping("NP",["ADV"],"R")
    ptable.add_mapping("NP",["NP"],"L")
    # marie debug : en fait la regle ("NP",["*"],"R") prend parfois vraiment n'importe quoi, dont des PONCT...
    ptable.add_mapping("NP",["ET"],"R")
    ptable.add_mapping("NP",["AP"],"L")
    ptable.add_mapping("NP",["VPP"],"L")
    ptable.add_mapping("NP",["CL"],"L")
    ptable.add_mapping("NP",["I"],"L")
    ptable.add_mapping("NP",["D"],"R")
    ptable.add_mapping("NP",["*"],"R")
    ptable.add_mapping("PP",['P'],"R")
    ptable.add_mapping("PP",[ 'P+D'],"R")
    ptable.add_mapping("PP",[ 'CL'],"R")
    ptable.add_mapping("PP",[ 'A'],"R")
    ptable.add_mapping("PP",[ 'ADV'],"R")
    ptable.add_mapping("PP",[ 'V'],"R")
    ptable.add_mapping("PP",[ 'N'],"R")
    ptable.add_mapping("PP",[ 'PRO'],"R")
    ptable.add_mapping("PP",[ 'NP'],"R")
    ptable.add_mapping("PP",['COORD'],"L")
    ptable.add_mapping("SENT",["VN"],"L")
    ptable.add_mapping("SENT",["V"],"L")
    ptable.add_mapping("SENT",["NP"],"L")
    ptable.add_mapping("SENT",["Srel"],"L")
    ptable.add_mapping("SENT",["Ssub"],"L")
    ptable.add_mapping("SENT",["Sint"],"L")
    ptable.add_mapping("SENT",["VPinf"],"L")
    ptable.add_mapping("SENT",["VPpart"],"L")
    ptable.add_mapping("SENT",["COORD"],"L")
    ptable.add_mapping("SENT",[ "PP"],"L")
    ptable.add_mapping("SENT",[ "AdP"],"L")
    ptable.add_mapping("SENT",[ "AP"],"L")
    ptable.add_mapping("SENT",[ "I"],"L")
    ptable.add_mapping("SENT",[ "N"],"L")
    # marie 
    ptable.add_mapping("SENT",[ "ADV"],"L")
    ptable.add_mapping("SENT",[ "PONCT"],"L")
    ptable.add_mapping("Sint",["VN"],"L")
    ptable.add_mapping("Sint",["V"],"L")
    ptable.add_mapping("Sint",["*"],"R")
    ptable.add_mapping("Srel",["VN"],"L")
    ptable.add_mapping("Srel",["V"],"L")
    ptable.add_mapping("Srel",["NP"],"L")
    ptable.add_mapping("Srel",["COORD"],"L")
    ptable.add_mapping("Ssub",["VN"],"L")
    ptable.add_mapping("Ssub",["V"],"L")
    ptable.add_mapping("Ssub",[ "C"],"L")
    ptable.add_mapping("Ssub",["*"],"R")
    ptable.add_mapping("VN",["V"],"R")
    # marie : numerous cases of tagging errors N instead of V
    ptable.add_mapping("VN",["N"],"R")
    ptable.add_mapping("VN",["VPinf"],"R")
    
    ptable.add_mapping("VPinf",["VN"],"L")
    ptable.add_mapping("VPinf",["V"],"L")
    ptable.add_mapping("VPinf",["*"],"R")
    ptable.add_mapping("VPpart",["VN"],"L")
    ptable.add_mapping("VPpart",["V"],"L")
    ptable.add_mapping("VPpart",["AP"],"L")
    ptable.add_mapping("VPpart",["*"],"R")
    ptable.add_aliases("V",["VIMP","VINF","VS","VPP","VPR"])
    ptable.add_aliases("N",["NC","NPP","NNC"])
    ptable.add_aliases("C",["CS","CC"])
    ptable.add_aliases("CL",["CLS","CLO","CLR"])
    ptable.add_aliases("A",["ADJ","ADJWH"])
    ptable.add_aliases("ADV",["ADVWH"])
    ptable.add_aliases("PRO",["PROWH","PROREL"])
    ptable.add_aliases("D",["DET","DETWH"])
    ptable.add_mapping("NC",["*"],"L")
    ptable.add_mapping("VP",["VN"],"L")
    return ptable

def demo():
      """
      This performs a demo of the main functionalities implemented in this module.
      It may be used as a starting point for developers wishing to reuse the functionalities of this module
      """
      import fastptbparser as parser

      tree = parser.parse_line('(S (NP (D the) (N cat)) (VP (V sleeps) (PP (P on) (NP (D the) (N mat))))(ADVP (ADV really))(PONCT .))')
      print "<A sentence>"
      print tree.do_sentence_string()
      print 
      print "<The PoS tags in Brown format>"
      print tree.do_tag_string(tagsep="/")
      print
      print "<The PoS tags in Database format>"
      print tree.do_tag_string(tagsep="\t",wordsep="\n")
      print
      print "<A parse tree (PTB style, one line flat)>"
      print tree.do_flat_string()
      print
      print "<A parse tree with dummy root (PTB style, multiline indented)>"
      tree = add_dummy_root(tree)
      print tree
      print
      print "<A parse tree with head annotation (PTB style, multiline indented)>"
      tree = parser.parse_line("(SENT (NP-SUJ (D le) (NC chat) (ADJ noir) (PP (P de) (NP (D la) (NC voisine)))) (VN (V dort)) (PP-MOD (P sur) (NP (D le) (NC paillasson))))",parse_symbols=True)
      tree.annotate_all(ftb4_fixer(),ftb_symset4())
      print tree
      print
      print "<A parse tree in French Treebank XML like format>"
      print TreebankTree(tree,index=10).do_xml_string()
      print
      print "<Dependency triples extracted from the tree>"
      dg = tree.build_dependency_graph()
      print dg.triples2string(sorted=True)
      print
      print "<A diff illustrated>" 
      treea = parser.parse_line('(S (NP (D the) (N cat)) (VP (V sleeps) (PP (P on) (NP (D the) (N mat)))))')
      treeb = parser.parse_line('(S (NP (D the) (N cat)) (VP (V sleeps)) (PP (P on) (NP (D the) (N mat))))')
      print 'A:',treea.do_flat_string()
      print 'B:',treeb.do_flat_string()
      print
      (common,aspec,bspec) = tree_diff(treea,treeb)
      print 'Intersection :',', '.join(map(lambda x:x[0]+'|'+str(x[1])+'|'+str(x[2]),common))
      print 'A diffs :', ', '.join(map(lambda x:x[0]+'|'+str(x[1])+'|'+str(x[2]),aspec))
      print 'B diffs :',', '.join(map(lambda x:x[0]+'|'+str(x[1])+'|'+str(x[2]),bspec))
      

if __name__ == "__main__":
      demo()
