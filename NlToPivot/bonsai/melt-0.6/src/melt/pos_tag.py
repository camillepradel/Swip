#!/usr/bin/env python

#################################################################################
## Copyright (C) 2009 Pascal Denis and Benoit Sagot
## 
## This library is free software; you can redistribute it and#or
## modify it under the terms of the GNU Lesser General Public
## License as published by the Free Software Foundation; either
## version 3.0 of the License, or (at your option) any later version.
## 
## This library is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
## Lesser General Public License for more details.
## 
## You should have received a copy of the GNU Lesser General Public
## License along with this library; if not, write to the Free Software
## Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#################################################################################

import sys
import os
import codecs
import optparse 
from melt.pos_tagger import POSTagger
from melt.result_sink import AccuracySink, compare_files

# Import Psyco if available
try:
    import psyco
    psyco.full()
except ImportError:
    pass


##################### I/O ##################################
usage = "usage: %prog [options] <input_file>"
parser = optparse.OptionParser(usage=usage)
parser.add_option("-m", "--model", action="store", help="model directory", default="./data")
parser.add_option("-t", "--train", action="store", help="training data file")
parser.add_option("-b", "--beam_size", action="store", help="set beam size", type=int, default=3)
parser.add_option("-e", "--encoding", action="store", help="language encoding (default: utf-8)", default="utf-8")
parser.add_option("-p", "--prior_prec", action="store", help="set precision of gaussian prior", type=int, default=1)
parser.add_option("-d", "--tag_dict", action="store", help="read in tag dictionary", default='')
parser.add_option("-l", "--lexicon", action="store", help="read in Lexicon DB", default='')
parser.add_option("-o", "--output_file", action="store", help="output file", default='')
parser.add_option("-g", "--gold_file", action="store", help="reference file")
parser.add_option("-c", "--handle_comments", action="store_true", help="handle SxPipe-like comments while tagging", dest="handle_comments", default=False)
parser.add_option("-f", "--feature_selection", action="store", help="feature selection options", type=str, default="")
(options, args) = parser.parse_args()

infile = codecs.getreader(options.encoding)(sys.stdin)
if len(args) > 0:
    infile = codecs.open( args[0], "r", options.encoding )
outfile = codecs.getwriter(options.encoding)(sys.stdout)
if options.output_file:
    outfile = codecs.open( options.output_file, "w", options.encoding )

# extra options dict for feature selection
feat_select_options = { 
    # default values
    'win':2, # context window size
    'lex_wd':1, # lefff current word features
    'lex_lhs':1, # lefff LHS context features
    'lex_rhs':1, # lefff RHS context features
    } 
if options.feature_selection:
    for f_opt in options.feature_selection.split(","):
        name, value = f_opt.split("=")
        feat_select_options[name] = eval(value)


############## create tagger ##################################
pos_tagger = POSTagger()

# read-in tag dictionary from file
if options.tag_dict:
    pos_tagger.load_tag_dictionary( options.tag_dict )
else:
    print >> sys.stderr, "Warning: No tag dictionary provided"
    
# read-in Lexicon
if options.lexicon:
    pos_tagger.load_lexicon( options.lexicon )
else:
    print >> sys.stderr, "Warning: External lexicon not provided"
    
# induce/load model
if options.train:
    # make sure model directory exists, if not create it
    if not os.path.isdir(options.model):
        os.makedirs(options.model)
    pos_tagger.train_model( options.train,
                            model_path=options.model,
                            prior_prec=options.prior_prec,
                            feat_options=feat_select_options)
elif options.model:
    pos_tagger.load_model( options.model )
else:
    sys.exit("Error: please provide model file (-m) or training file (-t)")


############## apply tagger ##################################
pos_tagger.apply( infile,
                  outfile,
                  handle_comments=options.handle_comments,
                  beam_size=options.beam_size,
                  feat_options=feat_select_options )


############## file closing ##################################
infile.close()
outfile.close()

############## eval ##################################
if options.gold_file and options.output_file:
    sink = AccuracySink()
    compare_files( options.gold_file, options.output_file, sink )
    print "Acc: %s (%s/%s)" %(sink.score(),sink.correct,sink.total)
