#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

from LoadLexicon import Lefff
from LoadLexicon import load_serialized_lefff

from optparse import OptionParser
import sys
import re

# Marie Candito

usage = """
   Tool to "desinflect" words, according to a lexicon (Lefff) : inflected forms are changed into more canonical forms, by getting rid of feminine and plural morphemes, and also getting rid of some tense/mood marking. The obtained forms are valid forms. "Desinflection" is performed only if it keeps the ambiguities of the original form.
   Example :
   'mangées'   => changed into 'mangé'
   'cheminées' => ambiguous between 'cheminer' (V, ppart, fem, plural) and 'cheminée' (N, plural)
               => changed into 'cheminée', which has the same ambiguity
   The first token of each line may be lowered if known in the lexicon as a low cap form
   """+sys.argv[0]+""" [options] LEFFF_FILE_OR_DIR

   Loads the LEFFF_FILE_OR_DIR file, or all .lex files in LEFFF_FILE_OR_DIR (takes a few seconds...)
   Reads from STDIN either tokenized text or PTB-style parsed text,
   Prints to SDOUT the input with some words desinflected
"""
parser=OptionParser(usage=usage.decode('iso-8859-1'))
parser.add_option("--inputformat", dest="inputformat", default='tok', help="Input format: either 'tok' for tokenized text, or 'ptb' for bracketed penn treebank format, or 'tabulated' for conll-like format : one line per token. TOKEN-RANK defines the column rank for the token itself. Default = tok")
parser.add_option("--token-rank",dest="token_rank",default=1,help="Column number (starting at 0) for the token column in the input, in case of tabulated input/output format. Default=1")
parser.add_option("--append", action="store_true", dest="append", default=False, help="Boolean pertaining for tabulated input/output format : if set, the new computed form is not output in place of the original form, but is written as a extra column. Default=False")
parser.add_option("--outtable", dest="outtable",default='',help="If set to non-empty string, dumps in outtable the list of word/disinflected word pairs, without repetition. Default = ''")
parser.add_option("--clusternum",action="store_true", dest="clusternum",default=False,help="If set, changes any numerical expression ([0-9\.,]+) into NUMEXPR. Default = False") 
parser.add_option("--ignoreP2",action="store_true", dest="ignoreP2",default=True,help="If set, forms corresponding to second persons verbs are ignored unless specific second person pronoun found. Default = True")
parser.add_option("--lowerfirstword",action="store_true", dest="lowerfirstword",default=True,help="If set, try to lower first word if unknown. Default = True")
parser.add_option("--freezetokennumber",action="store_true", dest="freezetokennumber",default=True,help="If set, never change the number of tokens (which may occur when a token is desinflected to a lefff form which is a compound - eg. JO => Jeux Olympiques). Default = True")
parser.add_option("--serializedlex", action="store_true", dest="serializedlex", default=False, help='Whether the lefff is in serialized version')

(opts,args) = parser.parse_args()
inputformat = str(opts.inputformat)
token_rank = int(opts.token_rank)
append = bool(opts.append)
outtable = str(opts.outtable)
clusternum = bool(opts.clusternum)
ignoreP2 = bool(opts.ignoreP2)
lowerfirstword = bool(opts.lowerfirstword)
freezetokennumber = bool(opts.freezetokennumber)
serializedlex = bool(opts.serializedlex)

if (len(args) > 0):
     lefffloc = args[0]
else:
     exit(usage+'\n   Missing LEFFF_FILE_OR_DIR argument!\n')

lefff = Lefff(lefffloc, clusternum, ignoreP2, lowerfirstword, serialized_input=serializedlex)

inputstream = sys.stdin
line = inputstream.readline()
isnotfirst = 0
while line:
    line = line[0:-1]
    if inputformat == 'tok':
        tokens = line.split(' ')
        isnotfirst = 0
        for token in tokens:
             ntoken = lefff.get_desinflected_form(token, isnotfirst)
             # the next token won't be considered the first of the sentence, unless only punctuations were encountered
             if isnotfirst == 0 and not (lefff.is_ponct_form(token)):
                  isnotfirst = 1
                  
             # backtrack if desinflected form contains space
             if freezetokennumber and ntoken.find(' ') > -1:
                  ntoken = token
             # demain relancer le run avec un .inhib sur les .txt qui sont bien passés....
             sys.stdout.write(ntoken+' ')
	sys.stdout.write('\n')
    elif inputformat == 'ptb':
        tokens = re.split('([\)\( ])', line)
        isnotfirst = 0
        # for each token in ptb format, except the last one
        for i, token in enumerate(tokens[0:-1]):
             if token == '':
                  continue
            # terminal symbols are those followed by ')', and not ')' themselves
             if token <> ')' and tokens[i+1] == ')':
                  token = lefff.get_desinflected_form(token,isnotfirst)
                  isnotfirst = 1 
                   # backtrack if desinflected form contains space
                  if freezetokennumber and token.find(' ') > -1:
                       token = token[i]
             sys.stdout.write(token)
                  
        print tokens[-1]
    elif inputformat == 'tabulated':
         if line == '':
              isnotfirst = 0
              print line
         else:
              cols = line.split('\t')
              if len(cols) > token_rank:
                   ntoken = lefff.get_desinflected_form(cols[token_rank], isnotfirst)
                   # backtrack if desinflected form contains space
                   if freezetokennumber and ntoken.find(' ') > -1:
                        ntoken = cols[token_rank]
                   # output as extra column
                   if append:
                        print line+'\t'+ntoken
                   # or replace the token_rank column
                   else:
                        print '\t'.join(cols[:token_rank] + [ntoken] + cols[token_rank+1:])
              else:
                   print line
              isnotfirst = 1

    line = inputstream.readline()

if outtable <> '':
    lefff.dump_form2dflform(outtable)

# test
#mywords = 'son ta tes leur vos votre dites sera est connaîtrai seriez fallait faut pleuvra puissions mangées amenées cheminées cheminements répertoriées capitalistes institutrices'
#mys = re.split(' ', mywords)
#print mywords
#for word in mys:
#    print "avant ", word
#    print "apres ", lefff.desinflect(word)


                
   
