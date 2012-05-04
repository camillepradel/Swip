#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import sys
from DepEval import *
from depparser import *
from optparse import OptionParser
from collections import defaultdict

usage = """This program reads a dependency treebank from STDIN
           If a gold treebank is given, 
           -- the program either extracts/or load dependencies from gold file
           -- and compares input treebank with gold treebank
           %prog [options]
"""

parser=OptionParser(usage=usage)
parser.add_option("--testformat", dest="testformat", default='conll', help='Format of input test treebank. Either "pivot" or "conll". Default=conll')
parser.add_option("--gold", dest="gold", default=None, help='Gold file')
parser.add_option("--goldformat", dest="goldformat", default='conll', help='Format of gold file.  Either "pivot" or "conll". Default=conll')
parser.add_option("--ignoretags", dest="ignoretags", default='', help='list of tags to ignore, separated by ":" : dependencies where the dependent has such a tag in gold are ignored. Default=\'\'. Use --ignoretags=\'PONCT\' to ignore ponctuation dependents.')
parser.add_option("--ignorelabels", dest="ignorelabels", default='ponct', help='list of labels to ignore, separated by ":" : dependencies having such label in gold are ignored. Default=ponct')

parser.add_option("--cutoff", dest="cutoff_length", default=40, help='At the end of evaluation, statistics for sentences of length <= cutoff are also shown. Default=40')
parser.add_option("--labeled", action="store_true", dest="labeled", default=False, help='Compute also labeled evaluation. Default False => only unlabeled')
parser.add_option("--vocab", dest="vocab", default=None, help='Vocabulary file : known at training, tabulated format')
parser.add_option("--vocabformcol", dest="vocabformcol", default=0, help='Column number (starting at 0) for the word form in the tabulated vocab file. Default=0')
parser.add_option("--trace",action="store_true",dest="trace",default=False,help="Triggers trace output for each compared dependency")

(opts,args) = parser.parse_args()
gold_file = str(opts.gold) if opts.gold <> None else None
vocab_file = str(opts.vocab) if opts.vocab <> None else None
vocab_formcol = int(opts.vocabformcol)
test_format = str(opts.testformat)
gold_format = str(opts.goldformat)
trace = bool(opts.trace)
labeled = bool(opts.labeled)
cutoff_length = int(opts.cutoff_length)
ignorelabels = [x for x in str(opts.ignorelabels).split(':') if x <> '']
ignoretags = [x for x in str(opts.ignoretags).split(':') if x <> '']

# if a the known vocabulary is given
vocab = None
if vocab_file <> None:
    vocab = defaultdict(int)
    vocab_stream = open(vocab_file)
    line = vocab_stream.readline()
    while line <> '':
        line = line[0:-1]
        cols = line.split('\t')
        vocab[cols[vocab_formcol]] += 1
        line = vocab_stream.readline()

depeval = DepEval(skip_labels = ignorelabels,
                  skip_dependent_pos = ignoretags,
                  compute_labeled_also = labeled,
                  trace_stream = sys.stdout if trace else None,
                  vocab = vocab)
if cutoff_length <> None and cutoff_length > 0:
    depeval_cutoff = DepEval(skip_labels=ignorelabels,
                             skip_dependent_pos=ignoretags,
                             compute_labeled_also=labeled,
                             trace_stream=None,# sys.stdout if trace else None,
                             name='len<='+str(cutoff_length))


test_stream = sys.stdin
gold_stream = open(gold_file)
gold_depparses = []
if gold_format == 'conll':
    method = read_depparses_conll_stream
else:
    method = read_depparses_pivot_stream
for gold_depparse in method(gold_stream):
    gold_depparses.append(gold_depparse)

if test_format == 'conll':
    method = read_depparses_conll_stream
else:
    method = read_depparses_pivot_stream
i = -1
for test_depparse in method(sys.stdin):
    #print test_depparse.to_string_pivot()
#    print test_depparse.depgraph.to_string_conll()
    # TODO : pourquoi ça marche pas ça...
    #gold_depparse = read_depparses_pivot_stream(gold_stream).next()
    i = i + 1
    gold_depparse = gold_depparses[i]

    depeval.update_attachment_scores(gold_depparse.depgraph, 
                                     test_depparse.depgraph, 
                                     gold_depparse.sentid)

    depeval.update_fmeasures(gold_depparse.depgraph,
                             test_depparse.depgraph)
    if cutoff_length > 0 and len(test_depparse.depgraph.lexnodes.keys()) <= cutoff_length:
        depeval_cutoff.update_attachment_scores(gold_depparse.depgraph, 
                                                test_depparse.depgraph, 
                                                gold_depparse.sentid)
        
        depeval_cutoff.update_fmeasures(gold_depparse.depgraph,
                                        test_depparse.depgraph)
        

depeval.write(sys.stdout)
if cutoff_length > 0:
    depeval_cutoff.write(sys.stdout)
