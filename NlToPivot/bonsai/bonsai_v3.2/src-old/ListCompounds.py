#/usr/bin/python
#coding: latin-1

from LabelledTree import LabelledTree
from XmlReader import XmlReader
from PennTreeBankReader import *
from optparse import OptionParser

from operator import itemgetter

# Marie : print out a list of compounds given a xml treebank, with occurrences, and internal composition

# cmpd stands for compound
# cmpt stands for component of compound

# Syntactically regular internal composition of compounds
RegularCompoundPatterns = { # a N, maybe with Det, Adj, and PPs
                     #" Organisation_de_coopération_et_de_développement_économique "
                     #" Institut_de_formation_des_agents_de_voyages"
                     #" pomme de terre "
                     # "marché monétaire et obligataire"
                     #"	Bureau_de_recherches_géologiques_et_minières"
                     'N': [ '(D )?(A )*N( A( C A)?)*( P(\+D)?( D)?( A)* N( A( C A)?)*(( C)? P(\+D)?( D)?( A)* N( A)*)*)?',
                            'N( N)+', # Air_France, maison_mère ... 
                            '(N|ET)( ET)+' ], # Wall Street, Zenith Data Structures,
                     
                     'V': [ 'V P(\+D)? (D )?N', # mettre_en_place, mettre en cause, prendre en compte ...
                            # INHIB: cf. pas si régulier..., et diff de savoir où rattacher le PP suivant 
                            # 'V (D )?(A )?N'   , #faire face, faire appel
                            ], 
                     }
# Regular forms
RegularForms = { 'REG-DASH': '^[^_\-]+(_-_[^_-]+)+$',  # form is made of components separated by dashes
                 'REG-NUM' : '^[0-9,_\.]+$', # form is a numerical expression
                 }

class Compounds:
    def __init__(self,lexcats=['N', 'P','P+D','P+PRO', 'V', 'A', 'ADV', 'C', 'D','PRO','ET']):
        # key= compound form ('pomme_de_terre')
        # val = dico : cat -> composition -> # occ
        #              N -> N P N -> 28
        self.cmpds = {}
        # total number of occ for each coumpound form
        self.cmpds_total_occ = {}

        self.lexcats = lexcats

    # a more reliable definition of a compound
    def is_compound(self,node):
        if (node.label in self.lexcats) and not(node.is_leaf()) and len(node.children) > 1:
            return True
        return False

    # recursively track compounds in the tree, and add them to the Compounds dictionary
    def add_cmpds_of_a_tree(self, tree):
        if self.is_compound(tree):
            form = tree.get_compound_form()
            composition = ' '.join( [x.label for x in tree.children] )
            cat = tree.label
            self.add_cmpd(form, composition, cat)
            lform = form.lower()
            if lform <> form:
                self.add_cmpd(lform, composition, cat)
        elif tree.children <> None:
            for child in tree.children:
                self.add_cmpds_of_a_tree(child)

    # form= 'pomme_de_terre', composition='N P N', cat='N'
    # => add count for this compound 
    def add_cmpd(self,form, composition, cat):
        if form in self.cmpds:
            self.cmpds_total_occ[form] = self.cmpds_total_occ[form] + 1
            if cat in self.cmpds[form]:
                if composition in self.cmpds[form][cat]:
                    self.cmpds[form][cat][composition] = self.cmpds[form][cat][composition] + 1
                else:
                    #try:
                    #    print "# several compositions for form+cat :"+form.encode('iso-8859-15')+" "+cat
                    #except UnicodeDecodeError:
                    #    #print "# several compositions for form+cat :"+form+" "+cat
                    self.cmpds[form][cat][composition] = 1
            else:
                #print "# several cats for form :"+form.encode('iso-8859-15')
                self.cmpds[form][cat] = { composition : 1 }
        else:
            self.cmpds[form] = { cat : { composition : 1 } }
            self.cmpds_total_occ[form] = 1

    # dans le cas d'un composé avec majuscule(s)
    # si la version minusculisée existe, on reverse toutes les occs vers le minus
    def suppress_cap_pairs(self):
        forms = self.cmpds.keys()
        for form in forms:
            lform = form.lower()
            # si formes avec et sans majuscule
            if lform <> form and lform in self.cmpds:
                #print "test pour "+lform.encode('iso-8859-15')+" ("+str(self.cmpds_total_occ[lform])+") et "+form.encode('iso-8859-15')+" ("+str(self.cmpds_total_occ[form])+")"
                # si meme nb d'occ, alors par construction, toutes les occs proviennent de la version majuscule
                if self.cmpds_total_occ[form] == self.cmpds_total_occ[lform]:
                    #print "suppression "+lform.encode('iso-8859-15')
                    del self.cmpds_total_occ[lform]
                    del self.cmpds[lform]
                # sinon, on ne garde que la version minusculisée, qui comprend les occs des autres versions...
                else:
                    #print "suppression "+form.encode('iso-8859-15')
                    del self.cmpds_total_occ[form]
                    del self.cmpds[form]

    def dump_cmpds(self, flag_regular_patterns=False):
        print "#"+'\t'.join(['CAT', 'FORM', 'TOTAL_OCC_FOR_FORM', 'INTERNAL COMP', 'OCC for this form+cat+composition', 'IS_THIS_PATTERN_REGULAR'])
        for item in sorted(self.cmpds_total_occ.items(), lambda x,y: cmp(y[1],x[1])):
            form = item[0]
            for cat in sorted(self.cmpds[form].keys()):
                for item_comp in sorted(self.cmpds[form][cat].items(), lambda x,y: cmp(y[1],x[1])):
                    comp = item_comp[0]
                    occ = item_comp[1]
                    regular = ''
                    # check whether the internal composition is syntactically regular
                    if flag_regular_patterns and cat in RegularCompoundPatterns:
                        for pattern in RegularCompoundPatterns[cat]:
                            if re.match(pattern, comp):
                                regular = 'REG-'+cat
                                break
                    # check whether the form of the compound is regular (nums or dash-separated...)
                    if regular == '':
                        for flag in RegularForms:
                            if re.match(RegularForms[flag], form):
                                regular = flag
                                break
                    total_occ = self.cmpds_total_occ[form]
                    print '\t'.join([ cat, form, str(total_occ), comp, str(occ), regular ]).encode('iso-8859-15')
            
#-----------------------------------------
# main
#-----------------------------------------

# parser=OptionParser(usage="tbd")
# (opts,args) = parser.parse_args()

# cmpds = Compounds()

# instream = ""
# input_file = None
# if (len(args) > 0):
#     input_file = args[0]

# # read xml format
# reader = XmlReader()
# if input_file <> None : 
#     if os.path.isdir(input_file):
#         treebank =  reader.read_dir_xml(input_file)
#     else:
#         instream = open(input_file)
#         treebank = reader.read_xml(instream)
# else:
#     treebank = reader.read_xml(sys.stdin)

# for tree in treebank:
#     tree.remove_traces()
#     cmpds.add_cmpds_of_a_tree(tree)

# cmpds.suppress_cap_pairs()
# cmpds.dump_cmpds(flag_regular_patterns=True)
