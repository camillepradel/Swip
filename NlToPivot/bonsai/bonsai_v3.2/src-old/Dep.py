#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

from PropagTable import *
from DependencyTree import *
from triplesp import *

# Marie

# Information on a head
class HeadInfos:

    def __init__(self, form, id=0, lid=-1, lemma=None, spine=[]):
        self.spine = spine # list of categories projected by the lexical form
        self.lid = lid
        self.lemma = lemma
        self.form = form
        self.id = id

    def add_to_spine(self,label):
        self.spine.append(label)

    # assigns part-of-speech if supplied
    # returns part-of-speech
    def tag(self, tag=None):
        if tag <> None:
            self.spine[0:1] = tag
        if self.spine <> []:
            return self.spine[0]  
        else:
            return None

    def is_unknown(self):
        return self.form == 'MISSINGHEAD'

    def tostring(self, mode='parc700'):
       # if self.lemma <> None:
        l = self.form
        #else:
        #l = self.form
        l = re.sub(',', 'COMMA', l)
        if mode == 'parc700':
            return l + '~' + str(self.id)
        if mode == 'withcat':
            if self.spine <> []:
                if self.spine[0] == None : self.spine[0] = "MISSINGPOS"
                return '~'.join((self.spine[0], l, str(self.lid)))
            else:
                return '~'.join((l, str(self.lid)))

# a dependency
class Dep:

    def __init__(self, deptype, governor, dependent, wronggov=None, wronglab=None):
        self.name = deptype
        self.governor = governor
        self.dependent = dependent
        # for traces in comparison between gold and test dependencies
        # the wrongly assigned governor or label
        self.wronggov = wronggov
        self.wronglab = wronglab

        if self.name == None:
            self.name = 'DEP'
    def tostring(self, mode='parc700'):
        
        str = self.name + '\t(\t' + self.governor.tostring(mode) + '\t,\t' + self.dependent.tostring(mode) + '\t)\t'
        if self.wronggov <> None:
            str += self.wronggov.tostring(mode)
        if self.wronglab <> None:
            return str + '\t' + self.wronglab
        return str

# returns 0 otherwise if same dependency
# returns -1 if dependency has unknown governor in the test file
# returns -2 otherwise
# opts : 'labeled' (default = False)
#        'tagged'  (default = False)
def compare_Dep(gold, test, opts={}):
    compare_label = 0
    if 'labeled' in opts and opts['labeled']:
        if gold.name <> test.name: 
            compare_label = -1
    if test.governor.is_unknown():
        return (compare_label, -1)
    if compare_HeadInfos(gold.dependent, test.dependent, opts) <> 0 : 
        return (compare_label, -2)
    if compare_HeadInfos(gold.governor, test.governor, opts) <> 0 :
        return (compare_label, -2)
    return (compare_label, 0)

def compare_HeadInfos(gold, test, opts={}):
    if gold.lid <> test.lid: return -1
    if 'tagged' in opts and opts['tagged']:
        if gold.tag() <> test.tag(): return -1
    if gold.form <> test.form:
        sys.stderr.write('PB: should be the same word and it\'s not!'+gold.tostring()+' / '+test.tostring()+'\n')
        return -1
    return 0

class DepList:

    def __init__(self, id=None,tree=None, headtable=None, dgraph=None, trace=False):
        self.deps = []
        self.id = id

        if dgraph == None and (tree == None or headtable == None): return

        if dgraph <> None:
            self.depgraph2deplist(dgraph)
        else:
            # annotates a tree with head flags, and builds list of surface dependencies
            # if dummy node at root
            if tree.label == '':
                tree = tree.children[0]
            tree.clitics_downwards()
            tree.head_annotate(headtable)
            # TODO here : branch functional labelling ...
            if trace:
                printing_opts = {'head':{'val':True}, 'headindex':{}}
                try:
                    sys.stdout.write(tree.printf(printing_opts)+'\n')
                except UnicodeDecodeError:
                    sys.stdout.write(tree.printf(printing_opts)+'\n')
            # now LabelledTree --> DependencyTree --> DepList
            # instead of LabelledTree --> DepList
            # TODO : replace DepList with DependencyGraph...
            # self.htree2deplist(tree, 0)
            depTree = HLabTree_2_DepTree(tree)
            self.deptree2deplist(depTree)
        self.sort()

    # sort of dependency list, according to linear order of dependent
    def sort(self):
        self.deps = sorted(self.deps, 
                           lambda x,y: cmp(x.dependent.lid, y.dependent.lid))

    def add_dep(self, dep):
        self.deps.append(dep)

    # trivial conversion DependencyTree -> DepList:
    # => enables to reuse the DepList metrics available
    def deptree2deplist(self, deptree):
        deptree.spine.reverse()
        head_infos = HeadInfos(deptree.word, deptree.lid, deptree.lid, deptree.lemma, deptree.spine)
        for dep in deptree.dependents:
            dep_infos = self.deptree2deplist(dep)
            self.add_dep( Dep(dep.deplabel, head_infos, dep_infos) )
        return head_infos

    # trivial conversion DependencyGraph -> DepList:
    # => enables to reuse the DepList metrics available
    #TODO : get rid of DepList...
    def depgraph2deplist(self, depgraph):
        for edge in depgraph.edges:
            head = edge.orig
            dep = edge.dest
            head_infos = HeadInfos(head.label, head.idx, head.idx, head.get_feature('lemma'), [head.get_feature('pos')])
            if 'pos' in dep.feats : 
                p = [dep.feats['pos']]
            else:
                p = ['MISSING_POS']
            dep_infos = HeadInfos(dep.label, dep.idx, 
                                  dep.idx, 
                                  dep.get_feature('lemma'),
                                  p) 
            self.add_dep( Dep(edge.label, head_infos, dep_infos) )
            
    # LabelledTree -> DepList
    # (obsolete : now LabelledTree -> DepTree -> DepList)
    # Builds list of surface dependencies, according to head annotation
    # rem: a little complex as it does not suppose that lexical heads are available on each node ...
    def htree2deplist(self, tree, id):
        if tree.is_terminal_sym():
            head_infos = HeadInfos(tree.children[0].label,
                                   id,
                                   tree.lid,
                                   tree.lemma,
                                   [tree.label])
            # if no dep already found : this is the head of the whole sentence
            if self.deps == []:
                dummy_head = HeadInfos(form='*', id=-1)
                self.add_dep(Dep('HEAD', dummy_head, head_infos))
            return (id + 1, head_infos)
        # if head is known
        if tree.headindex <> None:
            h = tree.headindex
            (id, head_infos) = self.htree2deplist(tree.children[h], id)
            # on rajoute la catégorie courante dans la liste des projections de la tete
            head_infos.add_to_spine(tree.label)
        else:
            # if head unknown : for all the lexical heads of the children, the governor is unknown
            # a dummy node is added
            # that will serve as governor of the current subtree
            head_infos = HeadInfos('UNK', id)
        # iteration on all children, even if head unknown
        for i in range(len(tree.children)):
            if i <> tree.headindex:
                child = tree.children[i]
                (id, dep_infos) = self.htree2deplist(child, id)
                if not dep_infos.is_unknown():
                   # TODO here : gérer les cas "incertains", dependances à distance

                    # if node has a function label
                    if child.funlabel <> None:
                        deptype = child.funlabel  
                    else:
                        deptype = 'DEP'
                    # if tag of dependent contains the function label
                    m = re.match('([^_]+)_(.+)$', dep_infos.tag())
                    if m <> None:
                        dep_infos.tag(m.group(1))
                        deptype = m.group(2)
                    self.add_dep( Dep(deptype, head_infos, dep_infos) )
        return (id, head_infos)

    def tostring(self, mode='parc700'):
        #print ' '.join([x.dependent.form for x in self.deps]).encode('iso-8859-1')
        s = ''
        for dep in self.deps:
            s = s + str(self.id) + '\t' + dep.tostring(mode) + '\n'
        return s

    # horreur
    def robustprint(self, tostrarg):
        try:
            print self.tostring(tostrarg)
        except UnicodeDecodeError:
            print self.tostring(tostrarg)

# List of deplists...
def print_deplists(deplists, mode):
    for deplist in deplists:
        deplist.robustprint(mode)
        #print deplist.tostring(mode)
#        print deplist.tostring(mode).encode('iso-8859-1')




