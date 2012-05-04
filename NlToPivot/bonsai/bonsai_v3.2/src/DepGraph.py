#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
# Marie Candito : 
# Dependency Graph meant to be used for surface dependency trees, and in the future, for deeper dependencies ...
# -- Conversion of a richly annotated phrase-structure tree (LabelledTree) into a DepGraph
# -- printing methods
# -- Inference of underspecified dependency labels
"""
from parser_constants import *
from LabelledTree import remove_dummy_root

# linear index for dummy root of the graph
DUMMY_ROOT_LIDX = -1
# name of the dependency between nodes and dummy root
DUMMY_DEP_LABEL = 'root'
# string for the dependency name when underspecified
UNK_DEP_LABEL = 'dep'
# name of the dependency when the correct governor is unknown
UNK_GOV_DEP_LABEL = 'missinghead'

# symbol encoding for pivot format
def encode_const_metasymbols(form):
    return form.replace(',','-C-').replace('(','-RRB-').replace(')','-LRB-').replace('~','-T-')

def decode_const_metasymbols(form):
    return form.replace('-C-',',').replace('-RRB-','(').replace('-LRB-',')').replace('-T-','~')
    # backward compatibility....
#    return form.replace('-C-',',').replace('-RRB-','(').replace('-LRB-',')').replace('-T-','~').replace('<LBR>','(').replace('<RBR>',')')

verbs = set(['V', 'VIMP', 'VINF', 'VPP', 'VPR', 'VS'])
                
class LexicalNode:
    def __init__(self, form, lidx, cat=None, features=None):
        """ Lexical node, identified by lidx (linear index)
        CAUTION : two LexicalNodes with same lidx are considered equal
        Members:
        * form : string for the word / token / whatever
        * lidx : identifier of the node (currently the linear index of the node)
        * cat : morpho-syntactic category, default = None

        Additional members can be added via set_feature

        @param form
        @param lidx
        @param lemma default=None
        @param cat default=None
        @param features : default=None, dictionary for additional member/value pairs

        Additional Members, when built from LabelledTree:
        * coarsecat : the coarse grained part of speech ("basecat")
        * spine : list of cats that this node projects in the LabelledTree
        * lemma : if set from the LabelledTree
        
        """
        self.lidx = lidx
        self.form = form
        self.cat = cat

        if features <> None:
            self.__dict__.update(features)

    def __eq__(self, other):
        return isinstance(other, LexicalNode) and self.lidx == other.lidx

    def __ne__(self, other):
        return not isinstance(other, LexicalNode) or self.lidx <> other.lidx

    def get_feature_value(self,attr):
        """ Returns the value of the given attribute, if defined,
        otherwise returns None 
        """
        if attr in self.__dict__:
            return self.__dict__[attr]
        return None

    def set_feature(self,attr, value):
        """ Sets the value of the given attribute """
        self.__dict__[attr] = value

    # For LexicalNodes containing spine
    def add_to_spine(self,label):
        if 'spine' not in self.__dict__ or self.spine == None:
            self.spine = []
        self.spine.append(label)

    def maximal_projection(self):
        return self.spine[-1]

    def first_projection(self):
        if len(self.spine) > 1:
            return self.spine[1]
        else:
            return None

    def x_projection(self,x):
        if len(self.spine) > x:
            return self.spine[x]
        else:
            return None
    ## Printing Functions

    def __str__(self):
        return self.to_string_pivot()

    def feature_value_to_string(self,attr,dummy=None,normalize=False):
        a = self.get_feature_value(attr)
        if dummy <> None and a == None:
            return dummy
        if normalize:
            if isinstance(a, str):
                return encode_const_metasymbols(a)
            return encode_const_metasymbols(str(a))
        return str(a)

    def feature_to_string_pivot(self,attr):
        if attr == 'cat':
            pattr = 'pos'
        else: pattr = attr
        return pattr+'('+self.to_string_pivot()+','+self.feature_value_to_string(attr,normalize=True)+')'

    def to_string_pivot(self):
        return encode_const_metasymbols(self.form)+'~'+str(self.lidx)

    def to_string_conll(self):
        """ builds the string for the conll format : the first columns for information on the node (not the dependency) """
        # conll ID : the lidx + 1 ...
        vals = [ str(self.get_feature_value('lidx') + 1) ]
        # conll FORM, LEMMA, CPOSTAG, POSTAG
        vals.extend([ self.feature_value_to_string(x,dummy='_')
                      for x in ['form','lemma','coarsecat','cat']])
        vals.append('_')            # conll FEATS 
        return '\t'.join(vals)
        
DUMMY_ROOT = LexicalNode('null',DUMMY_ROOT_LIDX)

# a dependency
class Dep:

    def __init__(self, governor, dependent, label=None):
        """ A dependency
        @param governor : the governor (or head) of the dependency
        @type governor : LexicalNode
        @param dependent : the dependent of the dependency
        @type dependent : LexicalNode
        @param label : the label of the dependency
        @type label : string
        """
        self.label = label
        self.governor = governor
        self.dependent = dependent

    def __eq__(self, other):
        return isinstance(other,Dep) and self.label == other.label and self.governor == other.governor and self.dependent == other.dependent

    def __ne__(self, other):
        return not isinstance(other, Dep) or self.label <> other.label or self.governor <> other.governor or self.dependent <> other.dependent

    def __cmp__(self, other):
        """ !!! CAUTION : comparisons on the dependent lidx only """
        return cmp(self.dependent.lidx, other.dependent.lidx)

    def unlabeled_eq(self, other):
        return self.governor == other.governor and self.dependent == other.dependent

    def is_unlabelled(self):
        return self.label == UNK_DEP_LABEL or self.label == None

    def __str__(self):
        return self.to_string_pivot()

    def label_to_string(self):
        if self.label == None:
            return UNK_DEP_LABEL
        return self.label.lower()

    def to_string_pivot(self):
        return self.label_to_string()+'('+self.governor.to_string_pivot()+','+self.dependent.to_string_pivot()+')'

    def to_string_trace(self):
        """ For tracing purposes : a tab separated string for the dependency, including categories of governor and dependent """
        gov_cat = self.governor.cat if self.governor.cat <> None else str(None)
        return '\t'.join( [self.label_to_string(),
                           '(',
                           gov_cat+'~'+self.governor.to_string_pivot(),
                           ',',
                           self.dependent.cat+'~'+self.dependent.to_string_pivot(),
                           ')'] )
                           

    def to_string_conll(self):
        """ one line of the conll format : a line for one token and its dependency to its governor """
        a = self.dependent.to_string_conll()
        # conll HEAD,DEPREL
        a = a + '\t' + str(self.governor.lidx + 1) + '\t' + self.label_to_string()
        # conll PHEAD, PDEPREL
        a = a + '\t_\t_'
        return a

class DepGraph:
    """
    A dependency graph
    Restrictions : 
    = a directed acyclic graph (no way to add dependencies that create a cycle)
    A dummy node may appear in dependencies, as governor only,
    and it does not appear as node of the graph
    The dummy node has by convention lidx = DUMMY_ROOT_LIDX
    (A VOIR SI ON GARDE COMME CA OU PAS)
    FIELDS:
    lexnodes : a dictionary mapping lidx to LexicalNodes
    deps : a list of Dep
    
    dependent2deps : a dictionary mapping dependent lidx to the list of dependencies with such dependent
    governor2deps : a dictionary mapping governor lidx to the list of dependencies with such governor
    """
    def __init__(self):
        self.lexnodes = {} # dict : key=lidx, val = lexnode
        self.deps = []

        self.dependent2deps = {}
        self.governor2deps = {}

    def add_lexnode(self,lexnode,gov_lidx=None,label=None):
        """ Adds a lexical node to the graph,
        if a governor lidx is supplied (and <> None), also adds the relevant dependency between this node
        and the governor
        @param lexnode : the LexicalNode
        @param gov_lidx : the lidx of its governor (default=None)
        @param label : the label of the dependency between lexnode and the supplied governor
        """
        self.lexnodes[lexnode.lidx] = lexnode
        if gov_lidx <> None:
            # todo report error if not in self.lexnodes
            g = self.get_lexnode(gov_lidx)
            if g <> None:
                self.add_dep( Dep(g,lexnode,label) )

    def add_dep(self, dep):
        """ Adds a Dep in the graph, and updates the indexes accordingly 
        NOTE : the nodes of the Dep are not required to be in the graph, and are not added
        NOTE : if dependent is the dummy node (lidx=DUMMY_ROOT_LIDX) : the dep is not added
        NOTE : the dep is not added if it creates a cycle
        @param : a dependency
        @type : Dep
        """
        gov_lidx = dep.governor.lidx
        dep_lidx = dep.dependent.lidx
        # block dependent dummy
        if dep_lidx == DUMMY_ROOT_LIDX:
            return

        # block cycles
        if gov_lidx in self.get_reachable_nodes(dep_lidx):
            return

        self.deps.append(dep)
        if gov_lidx in self.governor2deps:
            self.governor2deps[gov_lidx].append(dep)
        else:
            self.governor2deps[gov_lidx] = [dep]
        if dep_lidx in self.dependent2deps: 
            self.dependent2deps[dep_lidx].append(dep)
        else:
            self.dependent2deps[dep_lidx] = [dep]

    def add_dep_from_lidx(self, gov_lidx, dep_lidx, label):
        """ Adds a dependency between two nodes identified by their lidx
        The nodes must be in the graph already
        """
        gov = self.get_lexnode(gov_lidx)
        dep = self.get_lexnode(dep_lidx)
        if gov <> None and dep <> None:
            self.add_dep(Dep(gov, dep, label))

# repris de Enrique (do_passage_conv)
    def remove_dep(self, dep):
        if self.deps.count(dep) < 1:
            return

        dptdeplist = self.dependent2deps[dep.dependent.lidx]
        govdeplist = self.governor2deps[dep.governor.lidx]
        dptdeplist.remove(dep)
        govdeplist.remove(dep)
        if len(dptdeplist) == 0:
            del self.dependent2deps[dep.dependent.lidx]
        else:
            self.dependent2deps[dep.dependent.lidx] = dptdeplist
        if len(govdeplist) == 0:
            del self.governor2deps[dep.governor.lidx]
        else:
            self.governor2deps[dep.governor.lidx] = govdeplist
        self.deps.remove(dep)
        
    def add_labelledtree(self, tree, up_gov_lidx=DUMMY_ROOT_LIDX, updeplabel=DUMMY_DEP_LABEL, robust=True):
        """ extracts nodes and dependencies from the input LabelledTree,
        and adds them to the graph
        The dependency root is added as dependent of a node whose lidx is @param up_gov_lidx, with @param updeplabel as dependency label
        @param tree: input LabelledTree
        @param up_gov_lidx: linear index of the governor
        @type up_gov_lidx: int
        @param updeplabel:
        @type updeplabel: string
        """
        #print "ADD_LABELLEDTREE", up_gov_lidx, "label=",updeplabel
        if tree.is_tag():
            lexnode = labellednode_2_graphnode(tree)
            self.add_lexnode(lexnode, up_gov_lidx, label=updeplabel)
            #print "LEXNODE:", lexnode.form
            return lexnode
        else:
            hidx = get_head_child_idx(tree,robust)
            # if we know which child is the head
            if hidx <> -1:
                # this head is added as dependent of upgov, with label updeplabel
                # governor of this subtree is now this head
                #print "HEAD of", tree.label, "is",tree.children[hidx].label
                lex_head = self.add_labelledtree(tree.children[hidx], up_gov_lidx, updeplabel, robust)
                # spine construction
                lex_head.add_to_spine(tree.label)
            else:
                # TODO : remonter erreur si pas de tete
                # if head is unknown at that level, all children will be added as dependent of the dummy root
                lex_head = DUMMY_ROOT 
                #print "UNK_GOV_DEP_LABEL for", tree.label
            for i,child in enumerate(tree.children):
                if i <> hidx:
                    # if head is dummy at this level
                    # (either because head index is unknown at that level (hidx==-1), or head is unknown deeper)
                    # then label is UNK_GOV_DEP_LABEL
                    # (example : (Ssub (CS que) (VN (A A A)))
                    # head of Ssub = VN, but head of VN unknown, then dependent "que" receives UNK_GOV_DEP_LABEL
                    if lex_head == DUMMY_ROOT: 
                        deplabel = UNK_GOV_DEP_LABEL
                    # get deplabel for this child, unless head is unknown
                    else:
                        deplabel = None # erase previous
                        if child.has_function() and child.fct <> '':
                            deplabel = child.fct.lower()
                    dependent = self.add_labelledtree(tree.children[i], lex_head.lidx, deplabel, robust)
            return lex_head
     
    def fix_vpcoord(self):
        """ For DepGraph built from LabelledTree only. Method that fixes the cases of a COORD dominating a VN : dependents of the V have been wrongly assigned the CC as governor. Works either with labeled or unlabeled deptree."""
        # for any V node with a spine indicating a COORD that immediately dominates a VN or a VPP
        # set all other dependents of its governor as dep of this v node
        wronggovlidxs = set([])
        for v in [self.get_lexnode(l) for l in self.lexnodes.keys() if self.lexnodes[l].coarsecat == 'V']:
            if v.maximal_projection() in ['VN', 'VPP']:
            #print "v node, lidx=", v.lidx, "spine=", v.spine
                c = self.get_first_governor(v.lidx)
                if c.lidx <> DUMMY_ROOT_LIDX and 'COORD' in c.spine:
            #if len(v.spine) > 2 and (v.spine[2] == 'COORD' or v.spine[1] == 'COORD'):
                # add the gov to the list of wrong governors
                    wronggovlidxs.add(c.lidx)
                #print "wronggovs:", wronggovlidxs

        # now iterate on the wrong govs, and get the last verbal dependent only
        for clidx in wronggovlidxs:
            c = self.get_lexnode(clidx)
            # get the last verbal dependent of c
            verbaldeps = [v for v in self.get_dependents(c.lidx) if v.coarsecat == 'V' and v.maximal_projection() in ['VN','VPP'] and c.lidx < v.lidx]
            if verbaldeps <> []:
                v = verbaldeps[-1]
                otherdependencies = [d for d in self.get_dep_by_governor(c.lidx) if d.dependent.lidx <> v.lidx and d.dependent.lidx > c.lidx]
                for dep in otherdependencies:
                    self.add_dep( Dep(v, dep.dependent, dep.label) )
                    self.remove_dep(dep)
                
    def infer_deplabels(self):
        """ Inference of dependency labels using heuristics 
        For any unlabelled dependency in the graph, try to infer it according to heuristics 
        @precondition: LexicalNodes have their coarsecat attribute set"""
        for dep in self.deps:
            label = self.infer_deplabel(dep)
            if label <> None:
                dep.label = label

    # A DEPLACER AUTRE MODULE SPECIFIQUE?        
    def infer_deplabel(self, dep):
        """ Inference of the label of the given dependency 
        Returns the infered label
        @param : input dependency
        @type: Dep
        @author = Mathieu Falco, Marie Candito
        """
        # hack for subject clitics that do not appear as subject!!!
        if dep.dependent.cat == 'CLS': return 'suj'
        # nothing to do if label already specified
        if not dep.is_unlabelled():
            return dep.label 
        dep_pos = dep.dependent.coarsecat
        gov_pos = dep.governor.coarsecat
        nb_of_dependents = len(self.get_dep_by_governor(dep.governor.lidx))
        # test on the dependent's cat
        if dep_pos == 'D': return 'det'
        if dep_pos == 'PREF' : return 'mod'
        # marie : relation coord : pour gérer d'un coup tous les cas, y compris des erreurs de tagging sur le coordonnant,
        #         on recherche simplement les dépendants dont la projection maximale est COORD
        if dep.dependent.maximal_projection() == 'COORD': return 'coord'
        # les coord étant déjà gérés supra, les PONCT restant ne sont pas des coordonnants
        if dep_pos == 'PONCT': return 'ponct'
        # if fine-grained cat is available
        if dep.dependent.cat == 'CC': return 'coord'
        # MOD_REL(GVT, X) si X a Srel comme tête de son constituant
        # marie : on sort les cas COORD dominant Srel
        if dep.dependent.maximal_projection() == 'Srel' and dep.governor.first_projection() <> 'COORD': return 'mod_rel'

        # test on both governor and dependent cat
        if dep_pos <> 'PONCT' and gov_pos == 'PONCT': return 'dep_coord'
        # Traitement des conjonctions en gouverneur
        if gov_pos == 'C':
            # coordination : DEP_COORD(C, dep) si le constituant au-dessus du C est COORD dans le spine (spine)
            if dep.governor.first_projection() == 'COORD':
                # marie : debug à la hache : pb des modifieurs "contre la monnaie mais(C) AUSSI(adv) contre ...
                # si dependant adverbial, on ne le code en dep_coord que s'il est le seul dep
                if dep_pos == 'ADV' and nb_of_dependents > 1: return 'mod'
                return 'dep_coord'
            # subordination : OBJ(C, dep) sinon
            return 'obj'

        # rem : cas des CS dépendant de CC (et donc dep_coord) traités supra
        if dep.dependent.cat == 'CS': 
            if gov_pos == 'P':
                return 'obj'
            elif dep.dependent.form not in ['que','qu\'']: 
                return 'mod'
            else:
                return UNK_DEP_LABEL # (eg. "le fait QUE Paul soit parti => QUE added as dependent of "fait")
        # si fine-grained cat non disponible
        if dep_pos == 'C':
            if dep.dependent.first_projection() == 'COORD': return 'coord'
            return 'comp' # should rather be DEP here (eg. "le fait QUE Paul soit parti => QUE added as dependent of "fait")

        # GOUVERNEUR ADJ or ADV
        if gov_pos in ['A','ADV']:
            # marie : insuffisant : il faut changer de gouverneur aussi ...
            # marie : TODO : TESTER presence d'un adv comparatif!
            # arg(A, C) "plus importante que"
            # arg(A, C) "plus faiblement que"
            if dep_pos == 'C' and dep.dependent.first_projection() <> 'COORD':
                return 'arg_a'
            if dep_pos == 'ADV': return 'mod'

        # GOUVERNEUR PRO
        if gov_pos == 'PRO':
            if dep_pos in ['A','ADV','N']: return 'mod'

        # GOUVERNEUR PREP
        if gov_pos in ['P', 'P+D']:
            # OBJ(P ou P+D, N ou PRO)
            # marie : to handle cases of VP obj of P 
            # (appearing only if original constituent trees are modified)
            # if dep_pos in ['N', 'PRO']:            dependent.deplabel = 'OBJ'
            if dep_pos in ['N','PRO','V','A']: return 'obj'
            # ARG(P ou P+D, P ou P+D)
            # marie debug : un P dépendant de P : si seul dépendant = OBJ (de chez moi), sinon = ARG
            if dep_pos in ['P','P+D']:
                if nb_of_dependents == 1: return 'obj'
                return 'arg'
            # marie : ADV dépendant de P
            # -> OBJ si seul dépendant ("depuis longtemps"), MOD sinon
            # TODO: gestion des cas "pour encore longtemps" "pour longtemps encore"
            if dep_pos == 'ADV':
                if nb_of_dependents == 1: return 'obj'
                return 'mod'

        # GOUVERNEUR N
        if gov_pos == 'N':
            if dep_pos in ['P','P+D']:
                # marie : "peu de surprises" => "de" est taggé Prep, mais deplabel = DET
                # repéré par : la prep ne projette pas de PP
                if dep.dependent.maximal_projection() <> 'PP': return 'det'
                # sinon : sous-specification (mod ou arg) : on ne gere pas la sous-cat du nom
                # DEP(N, P ou P+D)
                return UNK_DEP_LABEL
            # to comply to input where prepositional PPs are within VPinf
            if dep_pos == 'V':
                if dep.dependent.maximal_projection() in [ 'Ssub', 'VPinf']:
                    return 'dep'
                else : return 'mod'
            # MOD(N, A, ADV, N)
            if dep_pos in ['A', 'ADV', 'N']: return 'mod'

        # GOUVERNEUR V
        if gov_pos == 'V':
            # MOD(V, A ou N ou ADV)
            if dep_pos in ['A', 'N', 'ADV']: return 'mod'
            # Traitement du cas AUX(V,V)
            if dep_pos == 'V':
                if dep.dependent.maximal_projection() == 'Ssub': # cas de fonction manquante dans le treebank
                    return 'mod'
                # AUX_TPS(V, V) si le dépendant est une forme conjuguée de l'auxiliaire avoir
                if dep.dependent.form in GramForms['avoir']: return 'aux_tps'
                # AUX_TPS(V, V) si le dépendant est une forme du passif à un temps composé
                if dep.dependent.form in ['été']: return 'aux_pass'
                # AUX_CAUS(V, V) si le dépendant est une forme conjuguée de "faire"
                if dep.dependent.form in GramForms['faire']: return 'aux_caus'
                if dep.dependent.form in GramForms['etre']:
                    #if tete.lemma in liste_etre_pp:
                    if dep.governor.form in GramForms['etre_pp']: return 'aux_tps'
                    # si présence d'un clitique pronominal alors AUX_TPS(V, V) sinon AUX_PASS(V, V)
                    for dependent in self.get_dependents(dep.governor.lidx):
                        if dependent.form in ['se','s\'']:
                            return 'aux_tps'
                    return 'aux_pass'
                # cases of VPpart starting with a prep, modifying a V => always modifiers
                if dep.dependent.cat == 'VPR': return 'mod'
            # COMP(V, C ou P) si le C ou le P n'a pas de dépendants
            if dep_pos in ['C','P'] and len(self.get_dep_by_governor(dep.dependent.lidx)) == 0:
                # Problem with the compound : "si bien que"
                return 'comp'
            # AFF(V, CL)
            if dep_pos == 'CL': return 'aff'
            # Marie : hack : les dépendants de participes passés non traités par functional role labelling
            # => à la hâche
            if dep_pos in ['P', 'P+D']:
                if dep.dependent.form == 'par' : return 'p_obj'
                if dep.dependent.form in ['de', 'du', 'des', 'd'] : return 'de_obj'
                if dep.dependent.form in ['à', 'au', 'aux'] : return 'a_obj'
                return 'mod'
            if dep_pos in verbs:
                # look for a prep before the verb, without any dep
                for deplidx in sorted([d.lidx for d in self.get_dependents(dep.dependent.lidx) if d.lidx < dep.dependent.lidx]):
                    depv = self.get_lexnode(deplidx)
                    if depv.cat == 'P' and not(self.has_dependents(depv.lidx)):
                        if depv.form == 'par' : return 'p_obj'
                        if depv.form in ['de', 'du', 'des', 'd'] : return 'de_obj'
                        if depv.form in ['à', 'au', 'aux'] : return 'a_obj'
                        return 'mod'
                        
                    
        # Marie : défaut
        return UNK_DEP_LABEL
                
            
    def sorted_lidx(self):
        """ Returns the sorted list of linear indexes of the nodes that are in the graph, excluding dummy nodes """
        return sorted(self.lexnodes.keys())

    def sorted_dependent_lidx(self):
        """ Returns the sorted list of linear indexes of the nodes that dependent of something in the graph
        """
        return sorted(self.dependent2deps.keys())

    def get_lexnode(self,lidx):
        """ Returns the node in the graph, having the given lidx 
        Works for the dummy root too"""
        if lidx in self.lexnodes:
            return self.lexnodes[lidx]
        if lidx == DUMMY_ROOT_LIDX:
            return DUMMY_ROOT
        return None

    def get_dep_by_dependent(self,dep_lidx):
        """ Returns the dependencies that have a given dependent
        @param dep_lidx: linear index of dependent lexical node
        @type dep_lidx: integer
        @return: list of Dep (empty list if none exist)"""
        if dep_lidx in self.dependent2deps:
            return self.dependent2deps[dep_lidx]
        return []

    def get_dep_by_governor(self,gov_lidx):
        """ Returns the dependencies that have a given governor
        @param gov_lidx: linear index of governor lexical node
        @type gov_lidx: integer
        @return: list of Dep (empty list if none exist)"""
        if gov_lidx in self.governor2deps:
            return self.governor2deps[gov_lidx]
        return []

    def get_first_dep_by_dependent(self,dep_lidx):
        """ Returns the first dependency that has a given dependent 
        @param dep_lidx: linear index of dependent lexical node
        @type dep_lidx: integer
        @return: first Dep if exists, None otherwise"""
        a = self.get_dep_by_dependent(dep_lidx)
        if a <> []:
            return a[0]
        return None


    def get_governors(self,dep_lidx):
        """ Returns the governors of a given dependent
        (for dependency trees : at most one gov)
        @param dep_lidx: linear index of dependent lexical node
        @type dep_lidx: integer
        @return: List of governors
        @rtype: list of LexicalNode
        """
        return map(lambda x: x.governor, self.get_dep_by_dependent(dep_lidx))

    def get_first_governor(self,dep_lidx):
        """ Returns the first governor of a given dependent
        @param dep_lidx: linear index of dependent lexical node
        @type dep_lidx: integer
        @return: first governor, if it exists, None otherwise
        @rtype: LexicalNode
        """
        dep = self.get_first_dep_by_dependent(dep_lidx)
        if dep <> None:
            return dep.governor
        return None


    def get_dependents(self,gov_lidx):
        """ Returns the dependents of a given governor
        @param gov_lidx: linear index of dependent lexical node
        @type gov_lidx: integer
        @rtype: list of LexicalNode (maybe empty)
        """
        return map(lambda x: x.dependent, self.get_dep_by_governor(gov_lidx))

    def get_dependencies(self):
        """ Returns the dependencies of the DepGraph
        @rtype: list of Dep
        """
        return self.deps

    def sorted_dependencies(self):
        """ Returns the dependencies, sorted by the default sort for Dep: increasing lidx of dependent """
        return sorted(self.deps)

    def get_such_dependency(self, gov_lidx, dep_lidx):
        """ Returns the dependency between governor lidx and dependent lidx if it exists. Returns None otherwise"""
        for dep in self.get_dep_by_dependent(dep_lidx):
            if dep.governor.lidx == gov_lidx:
                return dep
        return None

    def get_dummy_root_lidx(self):
        return DUMMY_ROOT_LIDX

    def get_roots(self):
        """ The roots are the nodes without governor 
        or that only have DUMMY_ROOT as governor
        @return : List of LexicalNodes """
        return [x for x in self.lexnodes.values() if x.lidx not in self.dependent2deps or self.get_governors(x.lidx) == [DUMMY_ROOT]]

    def get_first_root(self):
        r = self.get_roots()
        if r <> []:
            return r[0]
        return None
    
    def get_reachable_nodes(self, lidx):
        """ Computes the nodes reachable from a node
        @param lidx : linear id of node
        @param reachables : recursively constructed set of reachable nodes (lidx of nodes)
        @type reachables : set of lidx

        @precondition : the graph is acyclic ...
        """ 
        reachables = set([])
        self.___get_reachable_nodes(lidx, reachables)
        return reachables
    

    def ___get_reachable_nodes(self, lidx, reachables):
        """ Recursive internal function to compute reachable nodes
        """
        for node in self.get_dependents(lidx):
            reachables.add(node.lidx)
            self.___get_reachable_nodes(node.lidx, reachables)
            
    def get_node_projection(self,lidx):
        """ Returns the projection of a node : the sequence of lidx dominated by the node """
        return sorted([lidx] + [x.lidx for x in self.get_reachable_nodes(lidx)])

    # Checks

    def is_empty(self):
        return (len(self.lexnodes.keys()) <= 0)

    def has_dependents(self,gov_lidx):
        return len(self.get_dependents(gov_lidx)) > 0


    def is_segment_contiguous(self, lidxs):
        """ Returns True if the set of lidxs contains a contiguous sequence of lidxs
        @param lidxs : set of lidxs
        """
        local = sorted(lidxs.copy())
        prev = local.pop(0)
        while local:
            next = local.pop(0)
            if next > prev + 1:
                return False
            prev = next
        return True
        
    def is_tree(self):
        """ Checks whether the graph is a tree 
        i.e. no node has several governors, and only one node hasn't any governor
        """
        a = [x for x in self.dependent2deps.keys() if len(self.get_governors(x)) > 1]
        return a == [] and len(self.get_roots()) == 1

    def is_projective(self, trace=False):
        """ Checks whether the graph is projective : no node has a projection corresponding to a discontinuous sequence of linear indexes """
        for root in self.get_roots():
            reachables = set([])
            a = self.___is_node_projective(root.lidx, reachables, trace)
            if not a:
                return False
        return True

    def ___is_node_projective(self, lidx, reachables, trace=False):
        """ Recursive internal function to check projectivity of the node """
        #print "___is_node_projective: ", lidx, "==> ", reachables
        # check for all dependents
        for node in self.get_dependents(lidx):
            subreachables = set([])
            if not self.___is_node_projective(node.lidx, subreachables, trace):
                if trace:
                    print "Non projective node :"+str(node.lidx)
                return False
            reachables.update(subreachables)
        # add the starting node itself
        reachables.add(lidx)
        # check for new reachables
        if self.is_segment_contiguous(reachables):
            #print "=TRUE==> ", reachables
            return True
        #print "=FALSE==> ", reachables
        if trace:
            print "=>Non projective node :"+str(lidx)+str(self.get_lexnode(lidx).form)
        return False
            
    # Printing functions

    def __str__(self):
        return self.to_string_pivot()

    def get_yield_str(self, attr='form',separator=' ',normalize=True):
        """ Returns a string made of the nodes form, separated by separator """
        return separator.join([self.get_lexnode(x).feature_value_to_string(attr,normalize=True) for x in self.sorted_dependent_lidx()])

    def to_string_pivot(self, features=['cat']):
        lidxs = self.sorted_dependent_lidx() # consider only nodes that are dependent of something in the graph
        res = 'sentence_form('+self.get_yield_str(normalize=True)+')\n'
        res = res + 'surf_deps(\n'
        res = res + '\n'.join([ self.get_first_dep_by_dependent(lidx).to_string_pivot() for lidx in lidxs ])+'\n'
        res = res + ')\n' # close surf_deps
        res = res + 'features(\n'
        if features <> None:
            for attr in features:
                res = res + '\n'.join([ self.get_lexnode(lidx=lidx).feature_to_string_pivot(attr) for lidx in lidxs ])+'\n'
        res = res + ')\n' # close features
        return res

    def to_string_conll(self):
        s = '\n'.join(map(lambda x: self.get_first_dep_by_dependent(x).to_string_conll(), self.sorted_dependent_lidx()))
        if s:
            return s+'\n'
        return s

class DepParse:
    def __init__(self, sentid, depgraph, parseid=1, status=None,features=None):
        """ Structure to store a depgraph, along with its meta information :
        @param sentid : identifier of the sentence : either a string identifier from a treebank, or a rank if comes from a parsed file
        @type sentid : string

        @param parseid : parse rank for this sentence (default=1)
        @type parseid : integer

        @param status : (TODO) status of the parse : gold, autofromfunc, autofrommrg, autofromraw ... default=None
        @type : either None or string
        
        @param depgraph : the dependency graph
        @type depgraph : DepGraph

        @param features : Additional fields 
        @type features : either None, or dict mapping field / value
        """
        # meta information
        if isinstance(sentid, str):
            self.sentid = sentid
        else:
            self.sentid = str(sentid)
        self.parseid = parseid
        self.status = status

        # additional features
        if features <> None:
            self.__dict__.update(features)

        # the dependency graph
        self.depgraph = depgraph


    def get_feature_value(self,attr):
        """ Returns the value of the given attribute, if defined,
        otherwise returns None 
        """
        if attr in self.__dict__:
            return self.__dict__[attr]
        return None

    # Printing functions 
    def __str__(self):
        return self.to_string_pivot()

    def feature_value_to_string(self,attr,dummy=None,normalize=False):
        a = self.get_feature_value(attr)
        if dummy <> None and a == None:
            return dummy
        if normalize:
            if isinstance(a, str):
                return encode_const_metasymbols(a)
            return encode_const_metasymbols(str(a))
        return str(a)


    def to_string_pivot(self, features=['cat']):
        """ returns a string for the parse (meta-information + depgraph) in 'pivot' format """
        res = 'sentence(\n'
        for attr in ['sentid','date','validators']:
            if attr in self.__dict__:
                res = res + attr + '(' + self.feature_value_to_string(attr,normalize=True) + ')\n'
        res = res + self.depgraph.to_string_pivot(features)
        return res + ')\n' # close sentence



# a deplacer
def get_head_child_idx(tree, robust=True):
    """ Returns the rank of the head child of a given labelled tree """
    if tree.children == None:
        return -1
    for i, child in enumerate(tree.children):
        if child.isHead():
            return i
    # robustness : if only one child : that's the head ....
    # (TODO: to move in LabelledTree)
    if robust and len(tree.children) == 1:
        return 0
    return -1

def labellednode_2_graphnode(node):
    """ Builds a graph node (LexicalNode) from a preterminal node of a LabelledTree """
    features = {'coarsecat':node.basecat,
                'spine': [node.label]
                }
    if 'lemma' in node.__dict__:
        features['lemma'] = node.lemma
    return LexicalNode(form = node.get_form(),
                       lidx = node.children[0].idx,
                       cat = node.label,
                       features = features)


def labelledtree_2_depgraph(tree, tagset, headrules):
    """ Builds a DepGraph given an annotated phrase-structure tree 
    @param tree: input tree
    @type tree: LabelledTree
    @return: dependency tree, encoded in a DepGraph
    @rtype: DepGraph
    @precondition: The input tree has nodes annotated for basecat, heads and linear indexes 
    """
    depgraph = DepGraph()
    if not tree.is_parse_failure():
        remove_dummy_root(tree)
        tree.annotate_all(tagset, headrules)
        depgraph.add_labelledtree(tree)
        # special handling of vp coordination
        depgraph.fix_vpcoord()
        # if parse_failure : graph is empty 
        # TODO : is_parse_failure method
    return depgraph

def demo():
    import fastptbparser
    import depparser
    from LabelledTree import *

    line = "( (SENT (NP-SUJ (NPP Lyonnaise_-_Dumez)) (VN (V vient)) (PP-DE_OBJ (P d') (VPinf (VN (VINF hispaniser)) (NP-OBJ (DET sa) (NC filiale) (AP (ADJ espagnole)))) (COORD (CC et) (PP (P d') (VPinf (VN (VINF étendre)) (NP-OBJ (DET ses) (NC participations)) (PP-MOD (P en) (NP (NPP Espagne)))))))))"

    tree = fastptbparser.parse_line(line, parse_symbols=True)

    # Conversion
    depgraph = labelledtree_2_depgraph(tree, ftb4_fixer(), ftb_symset4())

    # Inference of dependency labels
    depgraph.infer_deplabels()

    print "DEPGRAPH PIVOT"
    print str(depgraph)
    print "DEPGRAPH CONLL"
    print depgraph.to_string_conll()

    print "Governor of dependent with lidx=4"
    g = depgraph.get_first_governor(4)
    if g <> None:
        print g.to_string_pivot()

    print "Dependents of a governor with lidx=1"
    print '\n'.join(map(lambda x: x.to_string_pivot(), depgraph.get_dependents(1)))

    print "Dependencies of a governor whith lidx=2"
    print '\n'.join(map(lambda x: x.to_string_pivot(), depgraph.get_dep_by_governor(2)))

    print "Depgraph has also spine information"
    r = depgraph.get_lexnode(1)
    print r, "SPINE: ",r.spine

    # reload from string : a non-projective example
    depparse_str = """
sentence(
id(ftb_1,flmf7aa1ep.xml)
date(00-00-00)
validators(M. Bidule)
sentence_form(Angie a déjà été informée de la situation)
surf_deps(
suj(informée~4,Angie~0)
aux_tps(été~3,a~1)
mod(informée~4,déjà~2)
aux_pass(informée~4,été~3)
root(null~-1,informée~4)
de_obj(informée~4,de~5)
det(situation~7,la~6)
obj(de~5,situation~7)
)
features(
pos(Angie~0,NPP)
pos(a~1,V)
pos(déjà~2,ADV)
pos(été~3,VPP)
pos(informée~4,VPP)
pos(de~5,P)
pos(la~6,DET)
pos(situation~7,NC)))
"""

    depparse = depparser.read_depparse_pivot(depparse_str)
    print "\nRELOADED (non projective example) :"
    print str(depparse)

    print "IS TREE?", depparse.depgraph.is_tree()
    print "ROOTS' LIDX?", ' '.join([str(x.lidx) for x in depparse.depgraph.get_roots()])

    print "IS PROJECTIVE?", depparse.depgraph.is_projective()    

    depgraph2 = depparse.depgraph #labelledtree_2_depgraph(remove_dummy_root(tree))
    if depgraph.deps[1] in depgraph2.deps:
        print "elle y est bien", depgraph.deps[1]
    if depgraph.deps[1] not in depparse.depgraph.deps:
        print "elle y est pas et c'est normal", depgraph.deps[1]
        
    
if __name__ == "__main__":
      demo()
