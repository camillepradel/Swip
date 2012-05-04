#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

from optparse import OptionParser

import sys
from LoadLexicon import Lefff
from LoadLexicon import load_serialized_lefff

usage = """
   Script to insert predicted lemmas in tagged text (tagged with FTB4 tagset), or CONLL format text
   If input is in CONLL format, predicted morpho features will also replace the feature column
   """+sys.argv[0]+""" [options] LEFFFDIR [TAGGED_FILE]

   Reads from TAGGED_FILE if it is specified, or from STDIN otherwise
   Prints to SDOUT the input where lemmas are predicted, and depending on the format, morphological features are predicted
"""
parser=OptionParser(usage=usage.decode('iso-8859-1'))
parser.add_option("--inputformat", dest="inputformat", default='onesentenceperline', help="Input format: either 'onesentenceperline' if each line stands for a sentence (use fieldsep and tokensep), or 'conll' tabulated format, or the more general 'onetokenperline' format : one line per token. Default = onesentenceperline")
parser.add_option("--fieldsep", dest="fieldsep", default='^^', help='String that separates token, lemma and part-of-speech. Irrelevant if INPUTFORMAT=conll. Default=^^')
parser.add_option("--tokensep", dest="tokensep", default=' ', help='For onesentenceperline format only : String that separates the token/lemma/cat triples. Default is a single space')
parser.add_option("--coldef", dest="coldef", default='0.1.2.3', help='For onetokenperline format only : list of the column ranks for form, lemma, fine-grained cat and morpho features respectively. Default=0.1.2.3')
parser.add_option("--usepriorlemma", action="store_true", dest="usepriorlemma", default=False, help='Whether to use the lemma present in input as default value in output or not; Default=False')
parser.add_option("--serializedlex", action="store_true", dest="serializedlex", default=False, help='Whether the lefff is in serialized version')


(opts,args) = parser.parse_args()
tagged_file = None
if (len(args) > 0):
    lefffdir = args[0]
else:
    exit(usage+'\nMissing LEFFFDIR argument!\n')
if (len(args) > 1):
     tagged_file = args[1]
fieldsep = str(opts.fieldsep)
if fieldsep == '\\t':
    fieldsep = '\t'

tokensep = str(opts.tokensep)
inputformat = str(opts.inputformat).lower()
coldefstr = str(opts.coldef)
usepriorlemma = bool(opts.usepriorlemma)
serializedlex = bool(opts.serializedlex)

if inputformat == 'conll':
    coldefstr='1.2.4.5'
    fieldsep = '\t'
    inputformat = 'onetokenperline'

if inputformat == 'onetokenperline':
    coldef = coldefstr.split('.')
    colform = int(coldef[0])
    collemma = int(coldef[1])
    colftb4cat = int(coldef[2])
    if len(coldef) > 3: 
        colfeats = int(coldef[3])
    else:
        colfeats == None

tagged_stream = sys.stdin if tagged_file == None else open(tagged_file)

# load the lefff lexicon
lefff = Lefff(lefffdir, serialized_input=serializedlex)

#lefff = load_serialized_lefff(lefffdir)

line = tagged_stream.readline()
isfirst = True
fieldseps = fieldsep + fieldsep + fieldsep
while line <> '':
    line = line[0:-1]
    if inputformat == 'onesentenceperline':
        ltoks = line.split(tokensep)

# morfette :premier token vide...        for tok in ltoks[1:]:
        for tok in ltoks:
            if len(tok) == 0: continue
            # on split le token pour récupérer forme, lemme et catégorie
            # ou bien seulement forme et catégorie
            (forml, ftbcat) = tok.rsplit(fieldsep,1)
            if forml == fieldsep:
                form = forml
            elif forml == fieldseps:
                form = fieldsep
                priorlemma = fieldsep
            else:
                fields = tok.split(fieldsep)
                if len(fields) == 3:
                    (form, priorlemma, ftb4cat) = fields
                elif len(fields) == 2 and not(usepriorlemma):
                    (form,ftb4cat) = fields
                    priorlemma = None
            # if len > 3 : something's wrong with the separator...: just print the triple as it is
                else:
                    sys.stdout.write(tokensep+tok)
                    continue
            (lemma, feats,clemma) = lefff.get_lefff_lemma_and_feats(form, ftb4cat, priorlemma=priorlemma, isfirst=isfirst, usepriorlemma=usepriorlemma)
            if lemma == None:lemma=clemma
            if isfirst and ftb4cat <> 'PONCT':
                isfirst = False
            sys.stdout.write(fieldsep.join([form, lemma, ftb4cat]) + tokensep)
        print ''
        isfirst = True
    # else : onetokenperline format
    else:
        if line == '':
            isfirst = True
            print
        else:
            cols = line.split(fieldsep)
            form = cols[colform]
            priorlemma = cols[collemma]
            ftb4cat = cols[colftb4cat]
            if colfeats <> None and len(cols) > colfeats:
                priorfeats = cols[colfeats]
            (lemma, feats,clemma) = lefff.get_lefff_lemma_and_feats(form, ftb4cat, priorlemma=priorlemma, isfirst=isfirst,usepriorlemma=usepriorlemma)
            if lemma == None:lemma=clemma
            if isfirst and ftb4cat <> 'PONCT':
                isfirst = False
            cols[collemma] = lemma

        # if features are also asked
            if colfeats <> None:
                if len(cols) > colfeats:
                    cols[colfeats] = lefff.formatfeats(feats)
                else:
                    cols.append(lefff.formatfeats(feats))
            print '\t'.join(cols)
    line = tagged_stream.readline()

# input morfette ^^ : cat EP20000.tcs.morfette | python /Users/mcandito/Documents/OUTILS/probparse/statgram/src2/do_lefff_lemmatisation.py --usepriorlemma ~/Documents/OUTILS/lefff
# input conll :cat ../data/ftb/6l/ftb4+mc+undocpd+fct+structmod110908+autotagged_jk_2.dep_conll| python do_lefff_lemmatisation.py --inputformat=onetokenperline --fieldsep=\\t --coldef=1.2.4.5 ../../../lefff > ../data/ftb/6l/ftb4+mc+undocpd+fct+structmod110908+autotag_jk+autolf_2.dep_conll
# input conll :cat ../data/ftb/6l/ftb4+mc+undocpd+fct+structmod110908+autotagged_jk_2.dep_conll| python do_lefff_lemmatisation.py --inputformat=conll ../../../lefff > ../data/ftb/6l/ftb4+mc+undocpd+fct+structmod110908+autotag_jk+autolf_2.dep_conll
