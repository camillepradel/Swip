#!/usr/bin/env python

import sys
import re
# This script converts raw text or tnt to bikel input format

def raw_2_tnt(lines):
    for line in lines:
        toks = re.split('\s+',line)
        for tok in toks[:-1]:
            out.write(tok+'\n')
        out.write('\n')

def tnt_2_bikel(lines):
    out.write('(') 
    for line in lines[:-1]:
        if re.match('^%%.*$',line):
            pass
        elif re.match('^\s*$',line):
            out.write(')\n(')
        else:
            toks = re.split('\s*',line)
            out.write('('+toks[0]+' ('+toks[1]+'))')

    line = lines[len(lines)-1]
    toks = re.split('\s+',line)
    out.write('('+toks[0]+' ('+toks[1]+'))')

#def raw_2_bikel(lines):
    
instream=sys.stdin
lines = instream.readlines()
out=sys.stdout
tnt_2_bikel(lines)
#raw_2_tnt(lines)
