#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

from LoadLexicon import Lefff
from optparse import OptionParser

# Marie Candito

usage = """
           Tool to "desinflect" words, according to a lexicon (Lefff) : inflected forms are changed into a more canonical forms, by getting rid of feminine and plural morphemes, and also getting rid of some tense/mood marking. The obtained forms are valid forms. "Desinflection" is performed only if it keeps the ambiguities of the original form.
   Example :
   'mangées' => 'mangé'
   'cheminées' => ambiguous between 'cheminer', Vpart, fem plural and 'cheminée' N plural
               => changed into 'cheminée', which has the same ambiguity
           %prog [options] [TOKENIZED_FILE_TO_DESINFLECT]
   Reads STDIN if no file is specified
   Prints output to SDOUT : same as input with some words desinflected
"""
parser=OptionParser(usage=usage.decode('iso-8859-1'))
parser.add_option("--lefffloc",dest="lefffloc",help="Lefff location : either a .lex lefff file to load, or a directory, in which all .lex files are to load",metavar='LEFFF FILE_OR_DIR')
parser.add_option("--inputformat",dest="inputformat",default='raw',help="Input format: either 'raw' for raw tokenized text, or 'ptb' for bracketed penn treebank format. Default = raw")
parser.add_option("--outtable", dest="outtable",default='',help="If set to non-empty string, dumps the list of word/disinflected word pairs, without repetition. Default = ''")
parser.add_option("--clusternum",action="store_true", dest="clusternum",default=False,help="If set, changes any numerical expression ([0-9\.,]+) into NUMEXPR. Default = False") 
parser.add_option("--ignoreP2",action="store_true", dest="ignoreP2",default=True,help="If set, forms corresponding to second persons verbs are ignored unless specific second person pronoun found. Default = True")
parser.add_option("--lowerfirstword",action="store_true", dest="lowerfirstword",default=True,help="If set, try to lower first word if unknown. Default = True")

(opts,args) = parser.parse_args()
lefffloc = str(opts.lefffloc)
inputformat = str(opts.inputformat)
outtable = str(opts.outtable)
clusternum = bool(opts.clusternum)
ignoreP2 = bool(opts.ignoreP2)
lowerfirstword = bool(opts.lowerfirstword)
inputstream = sys.stdin
if (len(args) > 0):
     inputstream = open(args[0])

lefff = Lefff(lefffloc, clusternum, ignoreP2, lowerfirstword)
#lefff.read_dir_or_file(lefffloc)

line = inputstream.readline()
while line:
    line = line[0:-1]
    if inputformat == 'raw':
        tokens = line.split(' ')
        for i, token in enumerate(tokens):
            # if token is not first word, or token is all in low cap
            if i > 0 or token.lower()==token:
                # try to get an already disinflected form
                if token in lefff.form2dflform:
                    ntoken = lefff.form2dflform[token]
                else:
                    ntoken = lefff.desinflect(token,isnotfirst=i)
                    lefff.form2dflform[token] = ntoken
            # if token is the first word, and token is capitalized 
            else:
                # try whether the low cap token is known already
                l = token.lower()
                if l in lefff.form2dflform:
                    ntoken = lefff.form2dflform[l]
                # otherwise just compute the desinflect form, but don't store it:
                else:
                    ntoken = lefff.desinflect(token,isnotfirst=i)
                
            sys.stdout.write(ntoken+' ')
	sys.stdout.write('\n')
    elif inputformat == 'ptb':
        tokens = re.split('([\)\( ])', line)
        isnotfirst = 0
        # for each token in ptb format, except the last one
        for i, token in enumerate(tokens[0:-1]):
            # terminal symbols are those followed by ')', and not ')' themselves
            if token <> ')' and tokens[i+1] == ')':
                ntoken = lefff.desinflect(token,isnotfirst)
                isnotfirst = 1 
                sys.stdout.write(' '+ntoken)
            elif token == '(':
                sys.stdout.write(' (')
            elif token <> ' ':
                sys.stdout.write(token)
        print tokens[-1]
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


                
   
