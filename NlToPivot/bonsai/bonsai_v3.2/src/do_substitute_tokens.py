#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

from optparse import OptionParser
from TokenClassifier import *
import sys

# Marie Candito

usage = """
   Tool to substitute tokens according to a given mapping (eg. a clustering)
   The mapping is read from a file with 3 colums :
   -- first column = the form to output
   -- second column = the form that should be replaced
   -- third column = the nb of occ of this form in the resource used for clustering

   """+sys.argv[0]+""" [options] MAPPING_FILE

   Reads from STDIN either tokenized text, or PTB-style parsed text, or tabulated text
   Prints to SDOUT the input where, the tokens were substituted with new forms (depending on the MAPPING_FILE provided, and the suffixes defined in TokenClassifier)
"""
parser=OptionParser(usage=usage.decode('iso-8859-1'))
parser.add_option("--inputformat", dest="inputformat", default='tok', help="Input format: either 'tok' for tokenized text, or 'ptb' for bracketed penn treebank format, or 'tabulated' for conll-like format : one line per token. TOKEN-RANK defines the column rank for the token itself. Default = tok")
parser.add_option("--token-rank",dest="token_rank",default=1,help="Column number (starting at 0) for the token column in the input, in case of tabulated input/output format. Default=1")
parser.add_option("--token-type",dest="token_type",default='deflected',help="Type of token ('deflected' for desinflected forms, 'catlemma' for terminals made of cat+lemma, 'catlemma2' for same terminals but cluster mapped to lemmas only. Default = 'deflected'")
parser.add_option("--append", action="store_true", dest="append", default=False, help="Boolean pertaining for tabulated input/output format : if set, the new computed form is not output in place of the original form, but is written as an extra column. Default=False")
parser.add_option("--no-cap-info", action="store_true", dest="no_cap_info", default=False, help="If set, disables the duplication of clusters for cluster+cap letter / cluster+noncap letter. Default=False (cap used)")
parser.add_option("--ldelim",dest="ldelim",default='#',help="Left prefix added to the new tokens. Default='-K'")
parser.add_option("--rdelim",dest="rdelim",default='#',help="Right prefix added to the new tokens. Default='K-'")
parser.add_option("--cit",dest="cit",default=0,help="Use this to take coarser clusters : Cluster id truncation : if set to positive value, only this nb of characters are kept for cluster ID. Default=0")
parser.add_option("--cnboccmin",dest="cnboccmin",default=0,help="If set to positive value, only recover a cluster the number of occ of the form is >= this value. Default=0")

(opts,args) = parser.parse_args()
inputformat = str(opts.inputformat)
token_rank = int(opts.token_rank)
token_type = str(opts.token_type)
append = bool(opts.append)
use_cap_info = not (bool(opts.no_cap_info))
ldelim = str(opts.ldelim)
rdelim = str(opts.rdelim)
clusteridtruncation = int(opts.cit)
clusternboccmin = int(opts.cnboccmin)

val2_col = None
if append:
     val2_col = 2 # to get number of occ in the cluster file
removecatpref = False
if token_type == 'catlemma2':
     removecatpref = True
elif token_type == 'catlemmaenrique':
     removecatpref = 'enrique'
     token_type = 'catlemma'

if (len(args) > 0):
     mapping_file = args[0]
else:
     exit(usage+'\n   Missing MAPPING_FILE argument!\n')

inputstream = sys.stdin

clues_method_str = 'compile_clues_for_'+token_type+'_tokens'
# use this to ensable suffixation of clusters 
tokenClassifier = TokenClassifier(first_clustering_file=mapping_file, 
                                  use_cap_info=use_cap_info, 
                                  ldelim=ldelim, 
                                  rdelim=rdelim, 
                                  clusteridtruncation=clusteridtruncation,
                                  clusternboccmin=clusternboccmin,
                                  val2_col=val2_col,
                                  clues_method_str=clues_method_str)
# suffixation of clusters disabled :
#tokenClassifier = TokenClassifier(first_clustering_file=mapping_file,ldelim=ldelim, rdelim=rdelim,clues_method_str='')

line = inputstream.readline()
while line:
    line = line[0:-1]

    if inputformat == 'tok':
        tokens = map(lambda x: tokenClassifier.classify_token(x, removecatpref), line.split(' '))
	sys.stdout.write(' '.join(tokens) + '\n')

    elif inputformat == 'ptb':
        tokens = re.split('([\)\( ])', line)
        # for each token in ptb format, except the last one
        for i, token in enumerate(tokens[0:-1]):
            if token == '': continue
            # terminal symbols are those followed by ')', and not ')' themselves
            if token <> ')' and tokens[i+1] == ')':
                 token = tokenClassifier.classify_token(token, removecatpref)
            sys.stdout.write(token)

        print tokens[-1]
    elif inputformat == 'tabulated':
         cols = line.split('\t')
         if len(cols) > token_rank:
              ntoken = tokenClassifier.classify_token(cols[token_rank], removecatpref)
              # output as extra column
              if append:
                   print line+'\t'+ntoken
              # or replace the token_rank column
              else:
                   print '\t'.join(cols[:token_rank] + [ntoken] + cols[token_rank+1:])
         else:
              print line
    line = inputstream.readline()


                
   
