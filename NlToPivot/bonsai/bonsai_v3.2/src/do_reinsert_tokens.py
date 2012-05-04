#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

from optparse import OptionParser
from parser_constants import *
import sys
import re

# Marie Candito

usage = """
   Script to reinsert tokens in a parse where terminal symbols were alterated

   """+sys.argv[0]+""" [options] TOKENIZED_FILE

   Reads from STDIN either PTB-style parsed text, or tabulated text representing the parsed version of TOKENIZED_FILE, but where terminal symbols may have been alterated
   Prints to SDOUT the input where, the terminal symbols were substituted with the corresponding token in TOKENIZED_FILE
"""
parser=OptionParser(usage=usage.decode('iso-8859-1'))
(opts,args) = parser.parse_args()
if (len(args) > 0):
     tokenized_file = args[0]
else:
     exit(usage+'\n   Missing TOKENIZED_FILE argument!\n')

if (len(args) > 1):
     parsed_file = args[1]
     parsed_stream = open(parsed_file)
else:
     parsed_stream = sys.stdin

tokenized_stream = open(tokenized_file)
parsed_line = parsed_stream.readline()

while parsed_line:
    parsed_line = parsed_line[0:-1]
    tokenized_line = tokenized_stream.readline()
    if not tokenized_line:
        # TODO!!
        exit("Wrong number of lines for "+tokenized_file)
    tokenized_line = tokenized_line[0:-1]
    # TEMPO : due to a bug (now corrected) in tokenization, empty lines may occur in tokenized file, but not in parsed file => skip empty lines
    while len(tokenized_line.strip(' ')) == 0:
         tokenized_line = tokenized_stream.readline()
         if not tokenized_line:
              # TODO!!
              exit("Wrong number of lines for "+tokenized_file)
         tokenized_line = tokenized_line[0:-1]
         
    tokenized_line = encode_const_metasymbols(tokenized_line) # parentheses ...
    parsed_tokens = re.split('([\)\( ])', parsed_line)
    tokens = re.split(' ', tokenized_line)
    token_rank = 0
    # for each parsed_token in ptb format, except the last one
    for i, parsed_token in enumerate(parsed_tokens[0:-1]):
        if parsed_token == '': continue
        # terminal symbols are those followed by ')', and not ')' themselves
        if parsed_token <> ')' and parsed_tokens[i+1] == ')':
            if token_rank > len(tokens) - 1:
                # TODO : gérer le cas où l'on n'a pas assez de tokens dans tokenized_line
                exit("Not enough tokens in :"+tokenized_line+": Could not make substitution in parsed line :"+parsed_line)
            parsed_token = tokens[token_rank]
            token_rank = token_rank + 1
        sys.stdout.write(parsed_token)

    print parsed_tokens[-1]
    parsed_line = parsed_stream.readline()

# TODO : vérifier les cas où c'est parsed_line qui est trop petit, en dehors du cas (())

