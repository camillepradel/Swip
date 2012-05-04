#/usr/bin/python
#coding: latin-1

from LabelledTree import LabelledTree
from LabelledTree import print_cfg_rule
from XmlReader import XmlReader
from PennTreeBankReader import *
from optparse import OptionParser

from operator import itemgetter

# a class to infer various information on compounds
# currently only subcat information ...
class ProcessCompounds:
    def __init__(self, lexcats=['N', 'P', 'V', 'A', 'ADV', 'C', 'D','PRO'], treebank=[], file=None):
        self.lexcats = lexcats
        self.simples = {}
        self.components = {}
        self.treebank = treebank
        self.subcats = {}

        # mode for computing the subcat inferences
        if file is None:
            for tree in self.treebank:
                self.w2cat(tree)
            self.infer_subcats()
            self.print_infered_subcats()
        # otherwise : loads the subcat inferences from file
        else:
            instream = open(file)
            for l in instream.readlines():
                line = l.split('\t')
                # skipping lines where no infered subcat available
                if line[2] == '': continue
                if not(self.subcats.has_key(line[0])):
                    self.subcats[line[0]] = { line[1]:line[2] }
                else:
                    self.subcats[line[0]][line[1]] = line[2]
                
    def is_compound(self,node):
        if (node.label in self.lexcats) and not(node.is_leaf()) and len(node.children) > 1:
            return True
        return False

    def is_simple_word(self,node):
        if (node.label in self.lexcats) and not(node.is_leaf()) and len(node.children) == 1:
            return True
        return False

    # comptages et stockage
    # dans self.simples : forme simple + cat -> nb d'occ par sous-cat
    # dans self.components : forme composant de composés + cat -> nb d'occ
    def w2cat(self,tree):
        # composé 
        if self.is_compound(tree):
            # components = dictionnaire
            # clé = forme de composant + cat 
            # valeur = nb
            for component in tree.children:
                if len(component.children) > 1: 
                    print "Erreur : composé bizarre"
                    print tree.printf()
                form = component.children[0].label
                cat = component.label
                if cat == 'ET': continue
                key = form + '\t' + cat
                self.components[key] = self.components.setdefault(key, 0) + 1
        # mot simple
        # self.simples = dictionnaire
        # forme+cat -> subcat -> nb occ
        elif self.is_simple_word(tree):
            key = "%s\t%s" % (tree.children[0].label , tree.label)
            # la sous-cat
            subcat = tree.subcat
            if subcat is None: subcat = '*'
            if self.simples.has_key(key):
                self.simples[key][subcat] = self.simples[key].setdefault(subcat,0) + 1
            else:
                self.simples[key] = { subcat : 1 }
        elif not(tree.is_leaf()):
            for child in tree.children:
                self.w2cat(child)

    # si couple (forme modifiée+cat) est connu comme mot simple,
    # avec sous-cat non ambigue
    # alors on l'applique à la forme de départ
    def guess_if_modified_form_is_known(self,form, mod_form, occ):
        if mod_form in self.simples and len(self.simples[mod_form].keys()) ==1:
            self.infered_subcats[form] = [ self.simples[mod_form].keys()[0], 'GUESSED', occ ]
            return True
        return False

    def infer_subcats(self):
# pour chaque composant de composé
# repérage sous-cat : manquante / ambigue / non ambigue
        infered_subcats = {}
        self.infered_subcats = infered_subcats
        components = self.components
        simples = self.simples
        #for cmpt in sorted(components.keys(), key = str):
        for cmpt in components.keys():
            occ = components[cmpt]

            # si couple (forme composant + cat) est connu comme mot simple
            if cmpt in simples:
                # si une seule sous-cat possible
                if len(simples[cmpt].keys()) == 1:
                    infered_subcats[cmpt] = [ simples[cmpt].keys()[0], 'UNAMBIG', occ]
                # si ambiguité de sous-cat : stockage des nb d'occ pour se faire une idée
                else:
                    # tri des sous-cat possibles par nb d'occ décroissant
                    subcats = sorted(simples[cmpt].items(), key = itemgetter(1), reverse=True)
                    comment = ' / '.join( [ x + ' = ' + `simples[cmpt][x]` for x in simples[cmpt].keys()] )
                    # si gros écart entre la premiere souscat et la 2eme
                    if subcats[0][1] > (10 * subcats[1][1]):
                        infered_subcats[cmpt] = [subcats[0][0], 'AMBIGUESSED', occ, comment]
                    else:
                        # sinon, on ne devine pas
                        infered_subcats[cmpt] = ['', 'AMBIGTOGUESS', occ, comment]

            # couple inconnu (forme composant + cat)
            # -> prédiction par heuristiques
            else:
                heuristiq = False
                # nombres en chiffres
                if re.match('[0-9]+\t', cmpt):
                    infered_subcats[cmpt] = ['card', 'GUESSED', occ]
                    heuristiq = True
                # dé-capitalisation
                elif cmpt[0:1].isupper():
                    lcmpt = cmpt[0].lower() + cmpt[1:]
                    # si la forme décapitalisée a une sous-cat non ambigue ds les mots simples
                    heuristiq = self.guess_if_modified_form_is_known(cmpt, lcmpt, occ)
                elif cmpt.find('s\t') > -1:
                    a = cmpt.find('s\t')
                    lcmpt = cmpt[0:a] + '\t' + cmpt[a+2:]
                    heuristiq = self.guess_if_modified_form_is_known(cmpt, lcmpt, occ)
                # si aucune heuristique n'a fonctionné
                # on se rabat sur les valeurs par défaut
                if not(heuristiq):
                    # nom initiale majuscule, seconde minuscule
                    if re.match('[A-Z].*N$', cmpt) and cmpt[1].islower():
                        infered_subcats[cmpt] = ['P', 'DEFAULT', occ]
                    # nom commençant par minuscule
                    elif re.match('[a-zéèê].*N$', cmpt):
                        infered_subcats[cmpt] = ['C', 'DEFAULT', occ]
                    else:
                        infered_subcats[cmpt] = ['', 'MISSING', occ]

    def print_infered_subcats(self):
        "Tri et ecriture des sous cats inferees"
# infered_subcats contient
# dictionnaire
# clé = forme du composant de composé + '=' + cat
# valeur = [ sous-cat devinee, 
#            type(AMBIG, UNAMBIG, DEFAULT ...), 
#            nb d'occ du composant,
#            eventuelles infos supplémentaires

        # Tri de infered_subcats
        ##tri alpa des clés
        #for cmpt in sorted(infered_subcats.keys()):
        ##tri alpha par type
        #for item in sorted(infered_subcats.items(), key = lambda x: x[1][1]):
        ##tri alpha par type, et si égalité, tri numérique décroissant sur nb d'occ
        for item in sorted(self.infered_subcats.items(), lambda x,y: cmp(x[1][1], y[1][1]) or cmp(y[1][2], x[1][2])):
            cmpt = item[0]
            print cmpt +"\t",
            print "\t".join( [str(x) for x in self.infered_subcats[cmpt] ])

    def has_compound_child(self,tree):
        if tree.children <> None:
            for x in tree.children:
                if self.is_compound(x): return True
        return False

    def undo_compounds(self, tree):
        "Walk the whole tree, undoing some compounds"
        if tree.children == None: return

        # compound node is replaced by a list of nodes
        if self.has_compound_child(tree):
            new = tree.children
            for i,child in enumerate(tree.children):
                if self.is_compound(child):
                    n = self.undo_compound(child)
                    if n <> []:
                        new = new[:i] + n + new[i+1:]
            tree.children = new
        # iterate on children
        for child in tree.children:
            self.undo_compounds(child)
                    
    def inject_subcats_to_compound(self, tree):
        """Adds subcat to the compound's components when available
           also unset the compound mark"""
        if self.is_compound(tree):
            for component in tree.children:
                component.set_compound_false()
                form = component.children[0].label
                cat = component.label
                if form in self.subcats and cat in self.subcats[form]:
                    component.set_feature(self.subcats[form][cat])

    def children_labels(self,tree):
        "Returns a space-separated string of the children labels"
        if tree.children == None: return ""
        return " ".join([ x.label for x in tree.children])

    def undo_compound(self, tree):
        if not(self.is_compound(tree)): return
        cstr = self.children_labels(tree)
        # N (N0 A1) -> N0 (AP (A1))
        # N (N0 A1 A2) -> N0 (AP (A1)) (AP (A2))
        if cstr == 'N A' or cstr == 'N A A':
            self.inject_subcats_to_compound(tree)
            a1 = LabelledTree("AP")
            a1.add_child(tree.children[1])
            if cstr == 'N A A':
                a2 = LabelledTree("AP")
                a2.add_child(tree.children[2])
                return [tree.children[0], a1, a2]
            return [tree.children[0], a1]
        # N (A0 N1) -> (AP (A1)) N1
        if cstr == 'A N':
            self.inject_subcats_to_compound(tree)
#            n = LabelledTree("AP")
#            n.add_child(tree.children[0])
            return [tree.children[0], tree.children[1]]
        # N (N0 P1 N2) -> N0 (PP (P1 (NP N2)))
        # N (N0 A1 P2 N3) -> N0 (AP (A1) (PP (P2 (NP N3)))
        elif cstr in ['N P N', 'N P+D N', 'N P D N', 'N A P N', 'N A P+D N', 'N A P D N'] :
            self.inject_subcats_to_compound(tree)
            has_adj = tree.children[1].label == 'A'
            np_i = 2
            if has_adj: np_i = 3
            n = LabelledTree("NP")
            n.add_child(tree.children[np_i])
            if len(tree.children) == np_i + 2:
                n.add_child(tree.children[np_i + 1])
            p = LabelledTree("PP")
            p.add_child(tree.children[np_i - 1])
            p.add_child(n)
            if has_adj:
                a = LabelledTree("AP")
                a.add_child(tree.children[1])
                return [tree.children[0], a, p]
            return [tree.children[0], p]
        else:
            return []
        

#-----------------------------------------
# main
#-----------------------------------------

# parser=OptionParser(usage="tbd")
# parser.add_option("--compute", action="store_true", default=True, help="If true, the program will compute and print to stdout subcats for components of compounds")
# parser.add_option("--subcatfile", help="Use this option to specify the infered subcat file, that is to be loaded - overrides the compute option")

# (opts,args) = parser.parse_args()

# # loading mode
# if opts.subcatfile:
#     infer = ProcessCompounds(file=opts.subcatfile)
# # else : compute mode
# else:
# #ARGS
#     instream = ""
#     input_file = None
#     if (len(args) > 0):
#         input_file = args[0]

# # read xml format
#     reader = XmlReader()
#     if input_file <> None : 
#         if os.path.isdir(input_file):
#             treebank =  reader.read_dir_xml(input_file)
#         else:
#             instream = open(input_file)
#             treebank = reader.read_xml(instream)
#     else:
#         treebank = reader.read_xml(sys.stdin)

#     infer = EnrichCompounds(treebank=treebank)




