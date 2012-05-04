#! /usr/bin/env python -OO
# -*- coding: iso-8859-1 -*-

from LabelledTree import *
  
"""
This module reads in Penn treebank style trees from character streams.
It performs faster than other parsers in the project but is restricted to file formats where there is exactly one parse tree per input line. It also provides less feedback (almost no feedback) in case of parse errors.

@author : Benoit Crabbé
"""

def parse_line(line,parse_symbols=False):
    """
    Parses a penn treebank style line of the form ( (X (Y a b) ... )) or (X (Y a b) ... )

    @param line: the line to be parsed
    @type line:string
    @param parse_symbols: a flag indicating whether to split symbols into functions, categories and features.
    @type parse_symbols: boolean 
    @return : a parse tree or None if the line cannot be parsed
    >>> test1 = parse_line('(())') # This is the parse failure code
    >>> print test1.do_flat_string()
    (())
    >>> test2 = parse_line('((S (NP (D the) (N cat)) (VP (V eats) (NP (D the) (N mouse))) ))')
    >>> print test2.do_flat_string()
    ( (S (NP (D the) (N cat)) (VP (V eats) (NP (D the) (N mouse)))))
    >>> test3 = parse_line('(S (NP (D the) (N cat)) (VP (V eats) (NP (D the) (N mouse))) )')
    >>> print test3.do_flat_string()
    (S (NP (D the) (N cat)) (VP (V eats) (NP (D the) (N mouse))))
    """
    tokens = tokenise_line(line)
    tree = None
    idx=0
    if tokens[0:4] == ['(','(',')',')']:
        return parse_failure()
    elif tokens[0:2] == ['(','(']:
        idx = 2
        (tree,idx) = parse_tree(tokens,idx,parse_symbols=parse_symbols)
        idx += 1
        root = LabelledTree('')
        root.add_child(tree)
        tree = root
    elif tokens[0] == '(':
        idx = 1
        (tree,idx) = parse_tree(tokens,idx,parse_symbols=parse_symbols)
    if  idx == len(tokens):
        return tree
    else:
        print tokens
        print '<parse failure>'
        return None

def parse_tree(toklist,idx,parse_symbols=False):
    """
    Parses a tree from a list of tokens of the form ['(','X','(','Y','a','b',')','c',')']

    @param toklist: the list of tokens to be parsed
    @type toklist: list of strings
    @param parse_symbols: a flag indicating whether to split symbols into functions, categories and features.
    @type parse_symbols: boolean 
    @return : a parse tree or None if the list cannot be parsed
    """
    if parse_symbols:#the symbol analysis is dead time consuming...
        #the symbol analysis is dead time consuming...
        (cat,fct,avm) = ___parse_symbol(toklist[idx])
        tree = LabelledTree (cat)
        tree.set_funvalue(fct)
        tree.set_features(avm)
    else:
        tree = LabelledTree(decode_const_metasymbols(toklist[idx]))
    idx += 1
    lgth = len(toklist)
    while toklist[idx] <> ')':
        if idx > lgth:
            print '<parse failure>'
            return None
        elif toklist[idx] == '(':
            (ctree,idx) = parse_tree(toklist,idx+1,parse_symbols=parse_symbols)
            tree.add_child(ctree)
        else:
            tree.add_child(LabelledTree(decode_const_metasymbols(toklist[idx])))
            idx += 1
    return (tree,idx+1)


def tokenise_line(line):
    """
    Tokenises a line from a raw input string 

    @param line: the line to be parsed
    @type line: string
    @return : a list of strings where each element of the list is a token
    """
    return line.replace('(',' ( ').replace(')',' ) ').split()


def ___parse_symbol(symbol,sep='-'):
    """
    This splits a node in a couple of n symbols (n=1,2 or 3) if there is the character sep in the label.
    If sep is not in the label than the second element of the string is None.

    The parse symbol function handles nodes formats of the form
    label(-function)?(-[att=val,...,att=val])?

    @param symbol: the label to split
    @type symbol :string
    @return : a tuple made of a label and optionally a function and optionnally an avm as a dictionary
    @rtype: a tuple of strings
    """
    res = symbol.split(sep)
    if len(res) == 3:
        (lbl,fun,feats) = res
        avlist = feats[1:-1].split(',')
        avm = dict(map(lambda f: map(lambda x: decode_const_metasymbols(x,ext=True),tuple(f.split('='))),avlist))
        return (decode_const_metasymbols(lbl),fun,avm)
    elif len(res) == 2:
        if '[' in res[1]:
            (lbl,feats) = res
            avlist = feats[1:-1].split(',')
            avm = dict(map(lambda f:  map(lambda x: decode_const_metasymbols(x,ext=True),tuple(f.split('='))),avlist))
            return (decode_const_metasymbols(lbl),None,avm)
        else:
            return (decode_const_metasymbols(res[0]),res[1],{})
    else:
        return (decode_const_metasymbols(res[0]),None,{})


def read_treebank(instream,parse_symbols=False):   
    """
    Reads a list of trees (a treebank) from a character input stream. 
    The function reads exhaustively the input stream until the whole input is consumed.
    This function is considerably slower than successive calls to next_tree(instream)
    or than the treebank(instream) function defined below

    @param instream: the stream to read data from
    @type instream: input character stream
    @param parse_symbols: a flag indicating whether to split symbols into functions, categories and features.
    @type parse_symbols: boolean 
    @return : a list of trees (a treebank)
    """
    tbank = []
    lines = f.readlines(1500)
    while lines:
        for xline in lines:
            xtree = parse_line(xline,parse_symbols=parse_symbols)
            tbank.append(xtree)
        lines = f.readlines(1500)
    return tbank


def treebank(instream,parse_symbols=False):
    """
    Reads a treebank from a character input stream. 
    The function reads exhaustively the input stream until the whole input is consumed.
    This function is a generator variant (considerably faster) of read_treebank(instream)

    @param instream: the stream to read data from
    @type instream: input character stream
    @param parse_symbols: a flag indicating whether to split symbols into functions, categories and features.
    @type parse_symbols: boolean 
    @return : a LabelledTree generator (the treebank)
    """
    lines = f.readlines(1500)
    while lines:
        for xline in lines:
            xtree = parse_line(xline,parse_symbols=parse_symbols)
            yield xtree
        lines = f.readlines(1500)


def next_tree(instream,parse_symbols=False):
    """
    Provides the next parse tree from the input stream or None if the end of the stream has been reached

    @param instream: the stream to read data from
    @type instream: input character stream
    @param parse_symbols: a flag indicating whether to split symbols into functions, categories and features.
    @type parse_symbols: boolean 
    @return : a tree or None if the end of the stream has been reached
    """
    xline = instream.readline()
    if not xline:
        return None
    else:
        return parse_line(xline,parse_symbols=parse_symbols)

if __name__ == "__main__":
    """ 
    This is the module self test procedure used for non regression purposes
    """
    #Perform module self tests
    import doctest
    doctest.testmod()
    print 
    print
    #This measures the module speed and behaviour of the module
    import cProfile
    print "<profiling>"
    print
    def speed_test():
        #f = open("/Users/Benoit/statgram/corpus/treebank1+mergeC/ftb_1.mrg")
        f = open("test.full")
        xtree = next_tree(f,parse_symbols=True)
        idx = 0
        while xtree <> None:
            idx += 1
            print idx,':',xtree
            xtree = next_tree(f)
        f.close()
    speed_test()
    #cProfile.run('speed_test()')
