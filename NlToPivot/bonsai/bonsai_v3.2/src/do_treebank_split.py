#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

# Marie Candito 

import sys
import os
import re
from optparse import OptionParser

usage = """This program reads from STDIN a treebank, 
           and splits it into training/dev/test files
           The treebank may either be with one sentence per line (Default), or with one token per line, with sentences separated by a blank line (like in conll-format)

           %prog [options]
"""

parser=OptionParser(usage=usage)
parser.add_option("--inputformat", dest="inputformat", default='onesentenceperline', help="Input format: either 'onesentenceperline' for any format where each line stands for a sentence, or 'tabulated' for conll-like format : one line per token. Default = onesentenceperline")
parser.add_option("--column", dest="column", default='1', help='Column number where the relevant trees are. Default 1')
parser.add_option("--outdir", dest="outdir", default='.', help='Output dir for the splitted files. Created if it does not exist. Default=.')
parser.add_option("--outprefix", dest="outprefix", default='ftb_', help='Prefix for the output splitted filenames. Default=ftb_')
parser.add_option("--outextension", dest="outextension", default='mrg', help='Extension for the output splitted filenames. Default=mrg')
#parser.add_option("--suffixes", dest="suffixes", default='1/2/3', help='Suffixes for the output files. Separated by \'/\'. Default 1/2/3')
#parser.add_option("--proportions", dest="proportions", default='80/10/10', help='Percentages, separated by / for thes output files. Should have the same number of / as in --suffixes. Default=80/10/10.')
parser.add_option("--cc", action="store_true",dest="cc", default=False, help='If set, overrides all other options, and splits accordingly to the "historic" train/dev/test split of Crabbe/Candito 08 paper. Default=False')
parser.add_option("--ccdebug", dest="ccdebug", default=0, help='If set to a positive value, overrides all other options, and splits with ftb_3=historic, historic ftb-2 is dicarded, and the historic ftb_1 is split into ftb_2 and ftb_1, with ccdebug representing the % for ftb_2. Default=0')
parser.add_option("--xfold",dest="xfold",default=0,help="If set to positive integer X, outputs X files. If --complementary is set, each file receives (X - 1) / X of the whole corpus. Otherwise, each file receives 1 / X of the whole corpus. Available with inputformat= onesentenceperline only", metavar="X")
parser.add_option("--complementary", action="store_true",dest="complementary", default=False, help="See --xfold option")

(opts,args) = parser.parse_args()
#ARGS : no args
#OPTIONS
column = int(opts.column)
inputformat = str(opts.inputformat)
outdir = str(opts.outdir)
outextension = str(opts.outextension)
outprefix = str(opts.outprefix)
#suffixes = str(opts.suffixes)
#proportions = str(opts.proportions)
cc = bool(opts.cc)
ccdebug = int(opts.ccdebug)
complementary = bool(opts.complementary)
xfold = int(opts.xfold)

if not (cc or ccdebug > 0) and xfold > 0 and not inputformat == 'onesentenceperline':
    exit('xfold option available only for inputformat = onesentenceperline')
if not (cc or ccdebug > 0) and not xfold > 0:
    exit('specify either --cc option or set xfold to a positive integer')

# output dir
if not os.path.isdir(outdir):
    try:
        os.mkdir(outdir)
    except OSError: exit('Could not create '+outdir)


#if not cc:
#    p = proportions.split('/')
#    if len(s) <> len(p) : 
#        exit('suffixes and proportions should have the same number of elements!')


instream = sys.stdin

if cc or ccdebug > 0:
    # output files
    s = ['1','2','3','2t','4']
    outstreams = []
    for suffix in s:
        name = os.path.join( outdir, outprefix + suffix + '.' + outextension )
        outstreams.append(open(name, 'w'))
    if ccdebug > 0:
        rk2 = (1235 * 2) + (9881 * ccdebug / 100)
    sentencenum = 1
    line = instream.readline()
    while line:
        line = line[0:-1]
        if cc:
            if sentencenum < 1236: rk = 2 # ftb_3
            elif sentencenum > 2470 and sentencenum < 12352 : rk = 0 # ftb_1
            elif sentencenum > 12351 : rk = 4 # extra sentences in FTB-2010 !!!
            else: rk = 1 # ftb_2
        else: #ccdebug : the historic ftb_2 is now ftb_2t, old ftb_1 is now (988sent)=ftb_2 + remaining=ftb_1
            if sentencenum < 1236: rk = 2       # ftb_3
            elif sentencenum < 2471: rk = 3     # ftb_2t (true dev set)
            elif sentencenum < rk2 + 1 : rk = 1 # ftb_2 (EM tuning for BKY)
            elif sentencenum < 12352 : rk = 0   # ftb_1 (training for BKY)
            else: rk = 4 # extra sentences in FTB-2010 !!!

        if inputformat == 'onesentenceperline':
#        line = line.strip()
            cols = line.split('\t')
            if len(cols) < column - 1 :
                exit('Cannot read line', sentencenum, ': could not find column', column)
            if rk > -1: outstreams[rk].write(cols[column -1]+'\n')
            sentencenum += 1
        elif inputformat == 'tabulated':
            if len(line) == 0:
                sentencenum = sentencenum + 1
            if rk > -1 : outstreams[rk].write(line+'\n')
        line = instream.readline()
# if xfold required : first read all treebank
elif xfold > 0:
    lines = instream.readlines()
    # prepare the folds
    length = len(lines)
    size = float(length) / float(xfold)
    intsize = int(size)
    folds = []
    for i in range(xfold):
        folds.append(lines[i*intsize:(i+1)*intsize])
    # if length of treebank is not a multiple of the number of folds,
    # there are some extra lines, to add at the end of the last fold
    for line in lines[(i+1)*intsize:len(lines)]:
        folds[len(folds) - 1].append(line)

    outstreams = []
    for i in range(xfold):
        name = os.path.join(outdir, outprefix + 'x' +str(xfold)+'-'+str(i)+'.'+outextension)
        outstreams.append(open(name,'w'))
    for i in range(xfold):
        # if complementary : each file receives the (X-1)/X remaining part of the treebank
        if complementary:
            for j in range(0,i)+range(i+1,xfold):
                fold = folds[j]
                for line in fold:
                    outstreams[i].write(line)
        # otherwise : each file receives one 1/X part of the treebank
        else:
            for line in folds[i]:
                outstreams[i].write(line)
            
    
