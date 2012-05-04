#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

"""
Parser that builds dependency graphs :
input format is either:
- pivot
- conll (STILL TODO)

@author : Marie Candito
"""

from DepGraph import *
import sys

def read_depparses_conll_stream(stream):
    """ Reads the given stream, and parses depgraphs one by one
    @param stream : input stream in conll format
    @return : a generator over <DepParse> instances
    """
    line = stream.readline()
    sentence = []
    sentnum = 1
    while line:
        line = line[0:-1]
        line = line.strip(' \t')
        #print ':'+line+':\n'
        if len(line) > 0:
            sentence.append(line)
        else:
            yield read_depparse_conll(sentence, sentnum)
            sentnum = sentnum + 1
            sentence = []
        line = stream.readline()

def read_depparse_conll(sentence, sentnum=-1):
    """ Builds a DepParse out of list of lines : each line is in conll format, the whole lines form a sentence
    @precondition: the sentence argument contains exactly the lines corresponding to one depgraph in conll format
    """
    depgraph = DepGraph()
    for line in sentence:
        cols = line.split('\t')
        dep_lidx = int(cols[0]) -1
        gov_lidx = int(cols[6]) -1
        dep_form = cols[1]
        dep_lemma = cols[2]
        dep_label = cols[7]
        coarsecat = cols[3]
        cat = cols[4]
        if dep_lemma == '_': dep_lemma = None
        # add governor, though form unknown yet
        if gov_lidx <> DUMMY_ROOT_LIDX and gov_lidx not in depgraph.lexnodes:
            governor = LexicalNode('', gov_lidx)
            depgraph.add_lexnode(governor)
        if dep_lidx not in depgraph.lexnodes and dep_lidx <> DUMMY_ROOT_LIDX:
            dependent = LexicalNode(dep_form, dep_lidx, cat=cat, features={'coarsecat':coarsecat, 'lemma':dep_lemma})
            depgraph.add_lexnode(dependent, gov_lidx, dep_label)
        else:
            # update features
            d = depgraph.get_lexnode(dep_lidx) 
            d.set_feature('form',dep_form)
            d.set_feature('coarsecat',coarsecat)
            d.set_feature('cat',cat)
            d.set_feature('lemma',dep_lemma)
            depgraph.add_dep_from_lidx(gov_lidx, dep_lidx, dep_label)

    return DepParse(sentid=sentnum, depgraph=depgraph)

def read_depparse_pivot(s):
    """ Builds a DepParse out of a string in pivot format 
    @precondition: the string contains exactly one depgraph in pivot format
    CAUTION : intended to be fast (no re), but supposes nice coherent input ...
    """
    s = s.strip()
    if s.startswith('sentence('):
        s = s[9:]
    else:
        sys.stderr.write('Pivot String should start with "sentence("')
        return None
    is_in_deps = False
    is_in_features = False
    depgraph = DepGraph()
    meta = {}
    while s <> '':
        s = s.strip()
        if s[0] == ')':
            s = s[1:]
            if is_in_features: is_in_features = False
            elif is_in_deps: is_in_deps = False
            elif s <> '':
                sys.stderr.write('Reading pivot string : unexpected additional material :'+s)
                return None
            continue
        l = s.find('(')
        if l == -1 :
            sys.stderr.write('Reading pivot string : unexpected format : cannot find any (:'+s)
            return None
        label = s[0:l]
        # backward compatibility :
        if label == 'id': label = 'sentid'
        s = s[l+1:]
        if label == 'surf_deps':
            is_in_deps = True
            continue
        if label == 'features':
            is_in_features = True
            continue
        # in any case, match following ')'
        r = s.find(')') # )
        # meta information of the parse (id, validators ...)
        if not(is_in_features) and not(is_in_deps):
            meta[label] = s[0:r].strip()
        # read either a dependency, or a feature over lexical nodes
        else:
            c = s.find(',') # comma
            # TODO : what if unexpected format?
            (gov_form,gov_lidx) = s[0:c].split('~',1)
            gov_lidx = int(gov_lidx)
            if is_in_deps:
                (dep_form,dep_lidx) = s[c+1:r].split('~',1)
                dep_lidx = int(dep_lidx)
                # backward compatibility : to read older versions of pivot, with MISSINGHEAD~-1 nodes
                if gov_form == 'MISSINGHEAD':
                    label = UNK_GOV_DEP_LABEL
                # backward compatibility : 
                if label == 'head' and gov_lidx == DUMMY_ROOT_LIDX:
                    label = DUMMY_DEP_LABEL
                # caution : connectedness not ensured here
                # add governor before adding dependent, and dependency
                if gov_lidx <> DUMMY_ROOT_LIDX and gov_lidx not in depgraph.lexnodes:
                    governor = LexicalNode(gov_form, gov_lidx)
                    depgraph.add_lexnode(governor)
                if dep_lidx not in depgraph.lexnodes and dep_lidx <> DUMMY_ROOT_LIDX:
                    dependent = LexicalNode(dep_form, dep_lidx)
                    depgraph.add_lexnode(dependent, gov_lidx, label)
                else:
                    depgraph.add_dep_from_lidx(gov_lidx, dep_lidx, label)
            elif is_in_features:
                value = s[c+1:r]
                if label == 'pos': label = 'cat'
                dep = depgraph.get_lexnode(gov_lidx)
                if dep <> None:
                    dep.set_feature(label, value)
        s = s[r+1:]
    if 'sentid' not in meta:
        return None
    sentid = meta['sentid']
    del meta['sentid']
    return DepParse(sentid = sentid, depgraph = depgraph, features = meta)

def read_depparses_pivot_stream(stream):
    """ Reads the given stream, and parses depgraphs one by one
    @param stream : input stream in pivot format
    @return : a generator over <DepParse> instances
    """
    line = stream.readline()
    b = line.find('sentence(')
    if b == -1: return #[]
    line = line[b:]
    depparse_str = ''
    res = []
    while line:
        line = line.strip()
        b = line.find('sentence(')
        if b == -1:
            depparse_str = depparse_str + line
        else:
            depparse_str = depparse_str + line[0:b]
            if depparse_str <> '':
                yield read_depparse_pivot(depparse_str)
            depparse_str = line[b:]
        line = stream.readline()
    # don't forget the last one ...
    if depparse_str <> '':
        yield read_depparse_pivot(depparse_str)
                
if __name__ == "__main__":
    import sys
    import cProfile
    def pivot_2_conll():
        for depparse in read_depparses_pivot_stream(sys.stdin):
            print depparse.depgraph.to_string_conll()
    def pivot_2_pivot():
        for depparse in read_depparses_pivot_stream(sys.stdin):
            if depparse <> None:
                print depparse.to_string_pivot()
    def conll_2_pivot():
        for depparse in read_depparses_conll_stream(sys.stdin):
            if depparse <> None:
                print depparse.to_string_pivot()
    #pivot_2_conll()
    #pivot_2_pivot()
    conll_2_pivot()
    #cProfile.run('pivot_2_conll()')
