#!/usr/bin/env python -0
# -*- coding: iso-8859-1 -*-

# This reader reads-in PTB-like s-expressions
# and returns a list of trees. 
# The parser ensures that the input is syntactically correct and emits some basic error messages
# The parser should be improved: in some cases the parser will crash when the input is syntactically incorrect

import sys
import os
from LabelledTree import *
from random import *
import sexpr

wsp = [' ','\n','\t','\r','\f']
lpar =  '('
rpar = ')'
non_sym = wsp + [lpar] + [rpar]

#Boolean that says whether the input is made of dummy rooted trees or standard trees

class SParserException(Exception):
    def __init__(self,value):
	self.msg = value
    def __str__(self):
	return self.msg
    def set_line_num(self,num):
	self.linenum = num

class PtbReader:
    def __init__(self,drparser=True):
        self.drparser = drparser
	pass
    
    # Builds a config for eval analog to that of the French treebank
    def build_eval_data(self,treebank,ratio=0.10):
        length = len(treebank)
        size = float(length)
        shuffle(treebank)
        max_train = int(float(size)*0.8)
        max_dev = int(float(size)*0.9)
        train = []
        dev = []
        test = []
        i = 0
        while i < len(treebank):
            if i < max_train:
                train.append(treebank[i])
            elif i < max_dev:
                dev.append(treebank[i])
            else:
                test.append(treebank[i])
            i = i + 1
        return (train,dev,test)


    def parse_treebank(self,buff):
        parser = sexpr.get_lex_yacc()
        return parser.parse(buff)

    #Deprec method (should be removed)
    def parse_treebankDeprec(self,buff):
	buffer = buff
	treenum = 0
	idx = 0
	treelist = []
	(flag,buffer,idx) = self.scan_wsp(buffer,idx)
	while(idx < len(buffer) and flag):
	    try:
                la = self.lookahead(4,buffer,idx)#Detects parse failures returned by parsers encoded as (())
                if la == "(())":
                    #print "skipped invalid tree"
                    tree = parse_failure()
                    treelist.append(tree)
                    treenum = treenum + 1
                    idx = idx + 4
                else:
                    if self.drparser:
                        (flag,buffer,idx) = self.scan_lpar(buffer,idx)
                        (flag,buffer,idx) = self.scan_wsp(buffer,idx)
                    (flag,buffer,idx,tree) = self.parse_tree(buffer,idx,LabelledTree('ROOT'))
                    if self.drparser:
                        (flag,buffer,idx) = self.scan_wsp(buffer,idx)
                        (flag,buffer,idx) = self.scan_rpar(buffer,idx)
                    tree = tree.remove_dummy_root()
                    treelist.append(tree)
                    treenum = treenum + 1
	    except SParserException, e :
#		strnum = str(treenum)
#		e.set_line_num(treenum)
#		sys.stderr.write(e.msg+' (tree n° '+e.linenum+')\n')
		sys.stderr.write(e.msg+'\n')
		sys.exit(1)
	    (flag,buffer,idx) = self.scan_wsp(buffer,idx)
	return (flag,treelist)

    def parse_tree(self,str,idx,root):
	flag = True
	lookedupchar = self.lookahead_1(str,idx)
	if lookedupchar == '(':
	    (flag,buff,idx) = self.scan_lpar(str,idx)
	    (flag,buff,idx) = self.scan_wsp(buff,idx)
	    (flag,buff,idx,child) = self.scan_sym(buff,idx)
	    if not flag:#DEBUG CODE
		e = SParserException("Malformed node (no label specified). Parsing aborted !\n")
		raise e
	    root.add_child(child) #SEM ACTION
	    while (idx < len(buff) and flag) :
		(flag,buff,idx) = self.scan_wsp(buff,idx)
		(flag,buff,idx,stuff) = self.parse_tree(buff,idx,child)	    
	    (flag,buff,idx) = self.scan_wsp(buff,idx)
	    (flag,buff,idx) = self.scan_rpar(buff,idx)
	    if not flag:#DEBUG CODE
		e = SParserException("Unbalanced tree : ')' missing. Parsing aborted !\n")
		raise e
	    return (flag,buff,idx,root)
	elif lookedupchar == ')':
	    return (False,str,idx,None)
	else:
	    (flag,buff,idx,child) = self.scan_sym(str,idx)
	    root.add_child(child) #SEM ACTION
	    return (flag,buff,idx,root)
     
    def lookahead_1(self,str,idx):
	if idx < len(str):
	    return str[idx]
	else:
	    return ''

    def lookahead(self,ln,str,idx):
        if idx + ln < len(str):
            return str[idx:idx+ln]
        else :
            return ''

    def scan_wsp(self,str,idx):
	buff = str
	while (idx < len(buff) and  buff[idx] in wsp):
	    idx = idx + 1
	return (True,buff,idx)

    def scan_sym(self,str,idx):
	buff = str
	sym = ''
	while (idx < len(buff)  and  buff[idx] not in non_sym):
	    sym = sym + buff[idx]
	    idx = idx + 1
	if len(sym) > 0:
	    return (True, buff,idx,LabelledTree(sym))
	else:
	    return (False,buff,idx,None)

    def scan_lpar(self,str,idx):
	buff = str
	if len(buff) > 0 and buff[idx] == lpar:
	    idx = idx + 1
	    return (True,buff,idx)
	else:
	    return (False,str,idx)

    def scan_rpar(self,str,idx):
	buff = str
	if idx < len(str) and str[idx] == rpar:
	    idx = idx +1
	    return (True,buff,idx)
	else:
	    return (False,str,idx)

    #Reads in a full mrg file
    def read_mrg(self,instream):
        filestr = instream.read()
        treelist = self.parse_treebank(filestr)
        return treelist

    #Reads all the mrg files in a directory
    def read_dir_mrg(self,dirname):
        files = os.listdir(dirname)
        treebank = []
        for file in files:
            if re.match("^.*\.mrg$",file):
                sys.stderr.write("Parsing input file "+file+'\n')
                instream = open(os.path.join(dirname,file))
                treebank  = treebank + self.read_mrg(instream)
                instream.close()
	return treebank
    
# instream = sys.stdin
# str  = instream.read()
# reader = PtbReader()
# treelist = reader.parse_treebank(str)
# for tree in treelist:
#     #tree = tree.remove_dummy_root()
#     tree = tree.add_dummy_root()
#     print tree.printf()#.encode('iso-8859-1')
#     #print print_node_list_brown(tree.tree_yield())

