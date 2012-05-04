# S-Expressions parser
# Parses a Penn Treebank-like format
# Includes both a lexer and a parser
# Source file for PLY : http://www.dabeaz.com/ply/
# Handles parse failures encoded as : (())
# Handles standard s-expression tree encoding + Penn Treebank specific oddities
# Benoit Crabbe (Feb. 2008) 
################################################################################
import sys
import re
import ply.lex as lex
import ply.yacc as yacc
from LabelledTree import *

tokens = ('LPAR','RPAR','SYMBOL','PARSE_FAILURE')

#WHITESPACE
t_ignore =  ' \t\f'
#COMMENTS
t_ignore_COMMENT = r';;.*'

#NEW-LINES
def t_newline(t):
      r'[\n\r]+'
      t.lexer.lineno += len(t.value)

#ERRORS
def t_error(t):
      print "Line %d Illegal character '%s' , ligne " % (t.lineno, t.value[0])
      t.lexer.skip(1)

#Token definitions
####################
t_PARSE_FAILURE   = r'\(\(\)\)'
t_LPAR            = r'\('
t_RPAR            = r'\)'
t_SYMBOL          = r'[^\)\(\s]+'

#Parser definitions
######################
def p_treebank_nt(p):
      '''treebank : treebank tree '''
      p[1].append(p[2])
      p[0] = p[1]

def p_treebank_t(p):
      '''treebank : tree '''
      p[0] = [p[1]]

def p_tree_failure(p):
      '''tree : PARSE_FAILURE '''
      p[0] = parse_failure()
                 
def p_tree_ptb(p):
      '''tree : LPAR s_expression RPAR'''
      p[0]= LabelledTree("") 
      p[0].add_child(p[2])

def p_tree(p):
      '''tree : s_expression '''
      p[0] = p[1]

def p_s_expression_list_nt(p):
      '''s_expression_list :  s_expression_list s_expression '''
      p[1].append(p[2])
      p[0]=p[1]

def p_s_expression_list_t(p):
      '''s_expression_list :  s_expression '''
      p[0] = [p[1]]

def p_s_expression_nt(p):
      '''s_expression : LPAR SYMBOL s_expression_list RPAR'''
      if re.match('^.+-.+$',p[2]):
            # marie : allow '-' in function name => split with maxsplit=1
            #(cat,func) = re.split('-',p[2])
            (cat,func) = re.split('-',p[2],1)
            p[0] = LabelledTree(cat)
            p[0].funlabel = func
      else:
            p[0] = LabelledTree(p[2])
      p[0].children = p[3]

def p_s_expression_trace(p):
      '''s_expression : LPAR SYMBOL RPAR'''
      if re.match('^.+-.+$',p[2]):
            # marie : allow '-' in function name => split with maxsplit=1
            #(cat,func) = re.split('-',p[2])
            (cat,func) = re.split('-',p[2],1)
            p[0] = LabelledTree(cat)
            p[0].funlabel = func
      else:
            p[0] = LabelledTree(p[2])
      p[0].children = LabelledTree(TRACE)
                     
def p_s_expression_t(p):
      '''s_expression : SYMBOL '''
      p[0] = LabelledTree(p[1])
 
def p_error(t):
      print "Syntax error at '%s'" % t.value

#Main Method to be called from external modules
#Returns a parser to be called with p.parse(string)
def get_lex_yacc():
      lex.lex(optimize=1)
      p = yacc.yacc(optimize=1,tabmodule="sexprtab")
      return p

def read_treebank(instream):
      parser=get_lex_yacc()
      tbank = parser.parse(instream.read())
      return tbank


# f = sys.stdin
# #result = yacc.parse('(()) (S (NP Det N) (VP V NP))   ( (S (NP Det N) (VP V NP)) ) (())')
# f.close()
# #print result
# for tree in result:
#       print tree.pprint()
