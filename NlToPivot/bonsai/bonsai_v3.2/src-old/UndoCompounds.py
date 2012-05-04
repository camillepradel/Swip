#/usr/bin/python
#coding: latin-1

from LabelledTree import LabelledTree
from LoadLexicon import Lefff
from ListCompounds import RegularCompoundPatterns
from optparse import OptionParser
from PennTreeBankReader import *

class SetCompounds:
    """ Systematically make compounds, according to a given list of allowed compounds (STILL TODO)
        Undo compounds that 
        (i) have a known regular pattern,
        (ii) and aren't in the allowed compounds list"""
    def __init__(self, lexcats=['N', 'P', 'V', 'A', 'ADV', 'C', 'D','PRO'], cmpdfile=None, lefffdir=None, subcatfile=None):
        # lexical categories (to spot compounds)
        self.lexcats = lexcats
        # lefff : used to infer the subcat attribute for components of compounds
        if lefffdir <> None:
            self.lefff = Lefff(lefffdir)
        else:
            self.lefff = None
        # allowed list of compounds
        self.allowed_cmpds = {}

        if cmpdfile <> None:
            self.load_cmpdfile(cmpdfile)

        # known subcats
        # dict : key = form, cal = dic: key2=cat, val=subcat
        self.form2cat2subcat = {}
        if subcatfile <> None:
            self.load_subcatfile(subcatfile)

        # new compounds to systematically recognize
        # (limited to the cases where the form spans a whole component)
            # search for a VN, with yield='il y a', and replace everything under the VN by (V il_y_a)
        self.new_compounds = [('il y a', 'VN', 'V')] 

    def load_cmpdfile(self, cmpdfile):
        try:
            instream = open(cmpdfile)
        except IOError:
            sys.stderr.write("Impossible to open "+cmpdfile)
            return
        for l in instream.readlines():
            # get rid of new line
            l = l.rstrip()
            self.allowed_cmpds[l] = 1

    def load_subcatfile(self, subcatfile):
        try:
            instream = open(subcatfile)
        except IOError:
            sys.stderr.write("Impossible to open "+subcatfile)
            return
        for l in instream.readlines():
            #print ':',l,':'
            # get rid of new line
            l = l.rstrip()
            if not l: continue
            # ???? le split ne passe pas
            m = re.match('([^\t]+)\t([^\t]+)\t(.*)$',l) 
            if m <> None:
                cat = m.group(1)
                subcat = m.group(2)
                form = m.group(3)
            #(cat, subcat, form) = re.split('\t', l, 2)
                if form in self.form2cat2subcat:
                    self.form2cat2subcat[form][cat] = subcat
                else:
                    self.form2cat2subcat[form] = { cat : subcat }

    def is_compound(self,node):
        if (node.label in self.lexcats) and not(node.is_leaf()) and len(node.get_children()) > 1:
            return True
        return False

    def has_compound_child(self,tree):
        if tree.has_children():
            for x in tree.get_children():
                if self.is_compound(x): return True
        return False

    def children_labels(self,tree):
        "Returns a space-separated string of the children labels"
        if not tree.has_children(): return ""
        return " ".join([ x.label for x in tree.get_children()])

    def labels(self, nodes):
        "Returns a space-separated string of the labels of the nodelist"
        return " ".join([x.label for x in nodes])

    def undo_compound(self, node):
        """If compound is not in allowed_cmpds, and has a known pattern, undo the compound :
        returns a list of nodes that should replace the compound node"""
        if not(self.is_compound(node)): return [node]
        cat = node.label
        # nothing to do if cat has no regular pattern
        if not cat in RegularCompoundPatterns: return [node]

        # nothing to do if this compound is allowed
        cmpd_str = self.children_labels(node)
        if cmpd_str in self.allowed_cmpds:
            return [node]
        for pattern in RegularCompoundPatterns[cat]:
            if re.match(pattern+'$', cmpd_str):
                # guess the subcat information for these new nodes
                # (cf. they're missing on compound components)
                for component in node.get_children():
                    self.guess_subcat(component)
                print "trace:PATTERN:", cmpd_str
                print "trace:BEFORE: ", node.printf()#.encode('iso-8859-1')
                if cat == 'N':
                    (n,tail) = self.make_NP(node.get_children(), beforehead=True)
                elif self.isP(node):
                    n = self.make_PP(node.get_children())
                elif cat == 'V':
                    n = self.make_VP(node.get_children())
                print "trace:AFTER: ",n.printf()#.encode('iso-8859-1')
                print "trace:UNDONE:",node.get_compound_form()
                return n.get_children()
        return [node]

    def make_AP(self, nodes, cmpd_str, beforehead=True):
        if beforehead and cmpd_str == 'A':
            # for preverbal bare adjectives
            return nodes[0]
        ap = LabelledTree('AP')
        ap.add_child(nodes[0])
        if cmpd_str == 'A C A':
            coord = LabelledTree('COORD')
            coord.add_child(nodes[1])
            a2 = self.make_AP([nodes[2]], 'A', beforehead=False)
            coord.add_child(a2)
            ap.add_child(coord)
        return ap

    def isP(self,node):
        return node.label in ['P','P+D']

    def make_NP(self, nodes, beforehead=True):
        np = LabelledTree('NP')
        tail = None
        while nodes <> []:
            if nodes[0].label in ['D','N','ET']:
                np.add_child(nodes[0])
                if nodes[0].label == 'N':
                    beforehead = False
                nodes = nodes[1:]
            elif nodes[0].label == 'A':
                if len(nodes) > 2 and nodes[1].label == 'C' and nodes[2].label == 'A':
                    ap = self.make_AP(nodes[0:3],'A C A',beforehead)
                    np.add_child(ap)
                    nodes = nodes[3:]
                else:
                    np.add_child(self.make_AP([nodes[0]], 'A', beforehead))
                    nodes = nodes[1:]
            # if a prep is encountered
            # => treat all remaining nodes as a whole PP
            # (cf. closest attachment preferred)
            # (unhandled case : N1 (P N2) others : where others attaches to N1)
            elif self.isP(nodes[0]):
                pp = self.make_PP(nodes)
                np.add_child(pp)
                nodes = []
            elif nodes[0].label == 'C':
                (coord, type) = self.make_COORD(nodes)
                nodes = []
                if type == 'PP':
                    tail = coord
                else:
                    np.add_child(coord)
        return [np, tail]

    def make_PP(self, nodes):
        pp = LabelledTree('PP')
        # prep is supposed to be the first node
        pp.add_child(nodes[0])
        [np, tail] = self.make_NP(nodes[1:])
        pp.add_child(np)
        if tail <> None:
            pp.add_child(tail)
        return pp

    def make_VP(self, nodes):
        vp = LabelledTree('VP')
        # V is supposed to be the first node
        vp.add_child(nodes[0])
        if self.isP(nodes[1]):
            vp.add_child(self.make_PP(nodes[1:]))
        else:
            # sinon on bactracke
            vp.children = nodes
        return vp

    # coord node, for either NP conjunct or PP conjunct
    # (AP handled separately)
    def make_COORD(self, nodes):
        coord = LabelledTree('COORD')
        # conjunction is supposed to be the first node
        coord.add_child(nodes[0])
        # if C P ... => coordination of PPs
        if self.isP(nodes[1]):
            pp = self.make_PP(nodes[1:])
            coord.add_child(pp)
            type = 'PP'
        # otherwise = coordination of NPs (APs handled differently)
        else:
            np = self.make_NP(nodes[1:])
            coord.add_child(np)
            type = 'NP'
        return [coord, type]

    def undo_compounds(self, tree):
        "Walk the whole tree, undoing some compounds"
        if not tree.has_children(): return

        # iterate on children
        # (allowing the remaining children list to change during this loop)
        #for child in tree.children:
        j = 0
        while j < len(tree.get_children()):
            child = tree.children[j]
            j = j+1
            if not child.has_children():
                continue
            if self.has_compound_child(child):
                new = []
                # iterate on grand children
                # (allowing the children list to change during this loop)
                for i,gchild in enumerate(child.get_children()):
                    if self.is_compound(gchild):
                        # returns either a list containing the compound ( not undone)
                        # or a list of several nodes : the compound node (gchild) should be replaced by this list
                        n = self.undo_compound(gchild)
                    #print "n:",n
                        # si child=VN, et si compound V, alors les freres du V doivent etre sortis du VN:
                        # (VN (V (V mettre) (P en) (N place))) ==> (VN (V mettre)) (PP (P en) (NP (N place)))
                        if len(n) > 1 and child.label == 'VN' and gchild.label == 'V':
                            new = new + [ n[0] ]
                            tree.children = tree.children[0:j] + n[1:] + tree.children[j:]
                        else:
                            new = new + n
                    else:
                        new.append(gchild)
                child.children = new
        # iterate on children
        # (allowing the children list to change during this loop)
        for child in tree.get_children():
            self.undo_compounds(child)
            
    def guess_subcat(self,node):
        """Guess and add subcat information for that node
        (used for nodes that are components of a compound that is undone here"""
        cat = node.label
        # no subcats for these
        if cat in ['ET','P','P+D']: return

        # verbs : get at least mood and tense
        if cat == 'V':
            morphs = self.lefff.get_morphs(node.get_form())
            if morphs <> None:
                for morph in morphs:
                    feats = self.lefff.get_features(morph)
                    if 'mood' in feats:
                        # !! susceptible de changer ici !!
                        print "trace:VERBAL FEATS: "+' '.join( [str(x[0])+':'+str(x[1]) for x in feats.items()] )
            return

        subcat = '' 
        form = node.get_form()

        # if known in the treebank (i.e. in the subcatfile)
        if form in self.form2cat2subcat and cat in self.form2cat2subcat[form]:
            subcat = self.form2cat2subcat[form][cat]

        elif re.match('[0-9,]+$',form):
            subcat = 'int'
        elif cat == 'N':
            # if lower case string => common noun
            if form.islower():
                subcat = 'C'
            # else, if known in lefff, as such => proper noun
            elif self.lefff <> None:
                if self.lefff.form_is_known(form):
                    subcat = 'P'
                # else, if the lowered form is known => common noun
                elif self.lefff.form_is_known(form.lower()):
                    subcat = 'C'
            else:
                subcat = 'P?'
        elif cat == 'A':
            subcat = 'qual?'
        node.set_feature(subcat)
        if subcat == '':
            print "trace:UNKNOWN subcat for :", form, ' ', cat
        else:
            print "trace:SUBCAT:", form, ' ', cat, ' ', subcat

    def do_compounds(self,tree):
        """ systematically recognizes new_compounds """
        if not tree.has_children():
            return
        if len(tree.get_children()) > 1:
            for (form, nt_cat, pos) in self.new_compounds:
                if tree.label == nt_cat:
                    y = tree.tree_yield_str()
                    if y.lower() == form:
                        # replace children with a compound, of specified cat
                        n = LabelledTree(pos)
                        n.add_child(LabelledTree(y.replace(' ','_')))
                        tree.children = [n]
                        return 
        for child in tree.get_children():
            self.do_compounds(child)

usage = """
"""                    
parser=OptionParser(usage=usage)
parser.add_option("--lefffloc",dest="lefffloc",default=None,help="Lefff location : either a .lex lefff file to load, or a directory, in which all .lex files are to load",metavar='LEFFF_FILE_OR_DIR')
parser.add_option("--subcatfile",dest="subcatfile",default=None,help="File containing known subcats : triples cat, subcat, form, separated by a tab", metavar="SUBCATFILE")
(opts,args) = parser.parse_args()
#ARGS
if (len(args) > 0):
    ftbloc = args[0]
    ftbstream = open(ftbloc)
else:
    ftbstream = sys.stdin
#OPTIONS
lefffloc = str(opts.lefffloc)
subcatfile = str(opts.subcatfile)

reader = PtbReader()
trees = reader.read_mrg(ftbstream)
setter = SetCompounds(lefffdir=lefffloc, subcatfile=subcatfile)
ntrees = []
for tree in trees:
    setter.undo_compounds(tree)
    setter.do_compounds(tree)
    print tree.printf()

