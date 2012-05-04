#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-
# Lecture des fichiers au format pivot

import sys
import ply.lex as lex
import ply.yacc as yacc
from dgraph import *
import re
from optparse import OptionParser

class Lexer:
      def __init__(self):
            pass

tokens = (
	'SENT','ID','DATE','VALID','SENTFORM',
	'SURFDEPS','FEATS','LPAR','RPAR','COMMA','TOK','MINUS'
	)

states = (
   ('string','exclusive'),
)
	
#TOKENS

reserved = {
      'sentence':'SENT',
      'id':'ID',
      'date':'DATE',
      'validators':'VALID',
     # 'sentence_form':'SENTFORM',
      'surf_deps':'SURFDEPS',
      'features':'FEATS'
}

t_LPAR=            r'\('
t_RPAR=            r'\)'
t_TOK =            r'[^\s\t\n\)\(\-,]+'
t_COMMA =          r','
t_MINUS =          r'-'
t_ignore =         ' \t\f'
t_string_TOK =     r'[^\s\t\n\)\(]+'
t_string_LPAR =    r'\('
t_string_ignore =  ' \t\f'

def t_begin_string(t):
    r'sentence_form'
    #print '>>>>>>>>STRING '
    t.type = 'SENTFORM'
    t.lexer.begin('string')   
    return t

def t_reserved(t):
      # marie : dependency names may end with a #
      # r'[a-z_]+'
      r'[a-z_\#]+'
      t.type = reserved.get(t.value,'TOK')
      return t

def t_string_end(t):
    r'\)'
    #print '>>>>>>>>INIT'
    t.type = 'RPAR'
    t.lexer.begin('INITIAL')
    return t


#NEW-LINES
def t_newline(t):
      r'[\n\r]+'
      t.lexer.lineno += len(t.value)


def lex_debug(text):
      lexer = lex.lex()
      lexer.input(text)
      while 1:
            tok = lexer.token()
            if not tok: break
            print tok

#ERRORS
def t_error(t):
      print "Line %d Illegal character '%s' , ligne " % (t.lineno, t.value[0])
      #t.lexer.skip(1)
      
lex.lex(debug=0)

start = 'sentencebank'

def p_sentencebank_nt(t):
      '''sentencebank : sentence sentencebank'''
      t[0]= t[1] + t[2]
	
def p_sentencebank_t(t):
 	'''sentencebank : sentence'''
 	t[0]=t[1]

def p_sentence(t):
	'''sentence : SENT LPAR ident date valid sentence_form surf_deps feats RPAR'''
        depgraph = t[7]
        for feature in t[8]:
              depgraph.set_feature(feature[0],feature[1],feature[2])
        (id,file)= t[3]
        ds = DepSentence(id,file,t[5],t[4],depgraph)
        t[0] = [ds]

def p_sentence_short(t):
	'''sentence : SENT LPAR ident date valid sentence_form surf_deps RPAR'''
        depgraph = t[7]
        (id,file)= t[3]
        ds = DepSentence(id,file,t[5],t[4],depgraph)
        t[0] = [ds]

def p_id(p):
	'''ident : ID LPAR toklist COMMA toklist RPAR'''
	p[0]= (p[3],p[5])

def p_date(p):
	'''date : DATE LPAR TOK MINUS TOK MINUS TOK RPAR'''
	p[0] = (p[3],p[5],p[7])

def p_validator(p):
	'''valid : VALID LPAR validlist RPAR'''
	p[0] = p[3]

def p_valid_list(p):
      '''validlist : TOK COMMA validlist'''
      p[0] = [str(p[1])] + p[3]

def p_valid_list_end(p):
      '''validlist : TOK'''
      p[0] = [str(p[1])]
      
def p_sentence_form(p):
	'''sentence_form : SENTFORM LPAR toklist RPAR'''
	p[0] = p[3]

def p_sentence_form_empty(p):
	'''sentence_form : SENTFORM LPAR RPAR'''
	p[0] = ''
	
def p_sentence_nt(p):
      ''' toklist : tok toklist'''
      p[0] = str(p[1])+p[2]

def p_sentence_t(p):
      '''toklist : tok'''
      p[0] = str(p[1])

def p_tokt(p):
      '''tok : TOK'''
      p[0] = p[1]

def p_toki(p):
      '''tok : ID'''
      p[0] = p[1]

def p_tokd(p):
      '''tok : DATE'''
      p[0] = p[1]


# def p_tokc(p):
#       '''tok : COMMA'''
#       p[0] = p[1]

# def p_tokm(p):
#       '''tok : MINUS'''
#       p[0] = p[1]

# def p_toki(p):
#       '''tok : INT'''
#       p[0] = p[1]

def p_surf_deps(p):
	'''surf_deps : SURFDEPS LPAR deplist RPAR'''
        depgraph = DependencyGraph()
        for edge in p[3]:
              depgraph.add_edgeq(edge)
	p[0] = depgraph

def p_surf_deps_empty(p):
	'''surf_deps : SURFDEPS LPAR RPAR'''
        depgraph = DependencyGraph()
	p[0] = depgraph
	
def p_deps(p):
	'''deps : TOK LPAR triple_item COMMA triple_item RPAR'''
        ed = DepEdge(p[3],p[1],p[5])
	p[0] = ed

def p_deps_bis(p):
	'''deps : TOK LPAR triple_item COMMA feat_item RPAR'''
	p[0] = (p[3],p[1],p[5])

def p_mbr_dep(p):
      '''triple_item : toklist MINUS TOK'''
      v = DepVertex(p[1],int(p[3]))
      p[0] = v

def p_mbr_dep_neg(p):
      '''triple_item : toklist MINUS MINUS TOK'''
      v = DepVertex(p[1], - int(p[4]))
      p[0] = v

def p_mbr_cat(p):
      '''feat_item : toklist'''
      p[0] = p[1]
      
def p_deplist_nt(p):	
      '''deplist : deps deplist'''
      p[0] = [p[1]] + p[2]

def p_deplist_t(p):	
      '''deplist : deps'''
      p[0] = [p[1]]
	
def p_feats(p):
	'''feats : FEATS LPAR deplist RPAR '''
	p[0] = p[3] 

def p_feats_empty(p):
	'''feats : FEATS LPAR RPAR '''
	p[0] = [] 

def p_error(t):
      print "Syntax error at line num "+str(t.lineno)+" at token '"+ t.value+"'"
	
#AUX FUNCTIONS
#finds out the root in an edge list if it exists
def find_root(edgelist):
      vset = set([])#vertices set
      inset = set([])#vertices with 1 incoming edge set
      for edge in edgelist:
            vset.add(edge.orig)
            inset.add(edge.dest)
      roots = vset.difference(inset)
      if len(roots) <> 1:
            print 'WARNING : Read inconsistent dep graph : no unique root, choosing arbitrary one.'
      if len(roots) == 0:
            print 'Unrooted graph (cyclic graph detected) : aborting parsing.'
            sys.exit(1)
      else:
            return roots.pop()

yacc.yacc(tabmodule="triplestab")

# Returns a list of DepSentence from a parc700 or pivot file
def parse_triples_file(filename):
      instream = open(filename)
      m = re.match("^(.*)\.piv$",filename)
      if m:
            filename = m.group(1)
      instr = re.sub('~','-',instream.read())
      # marie : do not get rid of the dummy head(null, root) relation
      #instr = re.sub('head\(null[^)]+\)\n','',instr)

      #print instr
      #lex_debug(instr)
      #lex.lex()
      r  = yacc.parse(instr)
      return r

# Returns a list of DepSentence from all files in dirname with given extension
def parse_triples_dir(dirname, prefix="P_", extension='.piv',sorted=False):
      files = os.listdir(dirname)
      if sorted:
            files = numeric_sort(files,prefix,extension)
      glist = []
      for file in files:
            if re.match("^.*"+extension+"$",file):
                  #sys.stderr.write("Parsing input file "+file+'\n')
                  filename = os.path.join(dirname,file)
                  g = parse_triples_file(filename)
                  glist = glist + g
      return glist

#sorts out parser generated files by numeric order
def numeric_sort(filelist,prefix="P_",extension=".piv"):
      file_idxes = []
      for filelt in filelist:
            if re.match("^.*"+extension+"$",filelt):
                  match = re.search("([0-9]+)",filelt)
                  idx = match.group(1)
                  file_idxes.append(int(idx))
      file_idxes.sort()
      sorted_files = []
      for elt in file_idxes:
            sorted_files.append(prefix+str(elt)+extension)
      return sorted_files
