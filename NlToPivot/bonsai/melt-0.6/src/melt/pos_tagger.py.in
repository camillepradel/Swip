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

''' PosTagger class definition '''


import os
import sys
import re
import math
import tempfile
import codecs
import operator
import time
from melt.corpus_reader import BrownReader
from melt.mytoken import Token
from melt.classifier import MaxEntClassifier
from melt.instance import Instance
from melt.utils import serialize, unserialize



class POSTagger:


    def __init__(self):
        self.tag_dict = {}
        self.lex_dict = {}
        self.classifier = MaxEntClassifier()
        self.cache = {} 
        return


    def load_model(self,model_path):
        try:
            self.classifier.load( model_path )
        except Exception,e:
            sys.exit("Error: Failure load POS model from %s (%s)" %(model_path,e))
        return


    def train_model(self,_file, model_path, prior_prec=1, feat_options={}):
        print >> sys.stderr, "Generating training data...",
        train_inst_file = self.generate_training_data( _file,
                                                       feat_options=feat_options)
        print  >> sys.stderr, "done." 
        print >> sys.stderr, "Training POS model..."
        self.classifier.train_megam( train_inst_file,
                                     prior_prec=prior_prec)
        print >> sys.stderr, "Dumping model in %s" %model_path
        self.classifier.dump( model_path )
        print >> sys.stderr, "Clean-up data file..."
        os.unlink(train_inst_file)
        print  >> sys.stderr, "done." 
        return
    

    def generate_training_data(self, infile, feat_options={}, encoding='utf-8'):
        data_file_name = tempfile.mktemp()
        data_file = codecs.open(data_file_name,'w',encoding)
        inst_ct = 0
        for s in BrownReader(infile):
            # build token list for each sentence (urgh! FIXME)
            tokens = []
            for wd,tag in s:
                token = Token( string=wd, pos=tag )
                token.label = token.pos # label is POS tag
                tokens.append( token )
            # create training instance for each token
            for i in range(len(tokens)):
                inst_ct += 1
                os.write(0, "%s" %"\b"*len(str(inst_ct))+str(inst_ct))
                inst = Instance( label=tokens[i].label,\
                                 index=i, tokens=tokens,\
                                 feat_selection=feat_options,\
                                 lex_dict=self.lex_dict,\
                                 tag_dict=self.tag_dict,\
                                 cache=self.cache )
                inst.get_features()
                print >> data_file, inst.__str__()
                # print >> sys.stderr, inst.__str__().encode('utf8')
        os.write(0,'\n')
        data_file.close()
        return data_file_name
    


    def tag_token_sequence(self, tokens, feat_options={}, beam_size=3):
        ''' N-best breath search for the best tag sequence for each sentence'''
        # maintain N-best sequences of tagged tokens
        sequences = [([],0.0)]  # log prob.
        for i,token in enumerate(tokens):
            n_best_sequences = []
            # cache static features
            cached_inst = Instance( label=tokens[i].label,
                                    index=i, tokens=tokens,
                                    feat_selection=feat_options,
                                    lex_dict=self.lex_dict,
                                    tag_dict=self.tag_dict,
                                    cache=self.cache )
            cached_inst.get_static_features()
            # get possible tags: union of tags found in tag_dict and
            # lex_dict
            wd = token.string
            legit_tags1 = self.tag_dict.get(wd,{})
            legit_tags2 = {} # self.lex_dict.get(wd,{}) 
            for j,seq in enumerate(sequences):
                seq_j,log_pr_j = sequences[j]
                tokens_j = seq_j+tokens[i:] # tokens with previous labels
                # classify token
                inst = Instance( label=tokens[i].label,
                                 index=i, tokens=tokens_j, 
                                 feat_selection=feat_options,
                                 lex_dict=self.lex_dict,
                                 tag_dict=self.tag_dict,
                                 cache=self.cache )
                inst.fv = cached_inst.fv[:]
                inst.get_sequential_features()
                label_pr_distrib = self.classifier.class_distribution(inst.fv)
                # extend sequence j with current token
                for (cl,pr) in label_pr_distrib:
                    # make sure that cl is a legal tag
                    if legit_tags1 or legit_tags2:
                        if (cl not in legit_tags1) and (cl not in legit_tags2):
                            continue
                    labelled_token = Token(string=token.string,pos=token.pos,\
                                           comment=token.comment,\
                                           label=cl,label_pr_distrib=label_pr_distrib)
                    n_best_sequences.append((seq_j+[labelled_token],log_pr_j+math.log(pr)))
            # sort sequences
            n_best_sequences.sort( key=operator.itemgetter(1) )
            # keep N best
            sequences = n_best_sequences[-beam_size:]
        # return sequence with highest prob. 
        best_sequence = sequences[-1][0]
        # print >> sys.stderr, "Best tok seq:", [(t.string,t.label) for t in best_sequence]
        return best_sequence


    def apply(self, infile, outfile, handle_comments, feat_options={}, beam_size=3):
        print >> sys.stderr, "POS Tagging..."
        t0 = time.time()
        # process sentences
        s_ct = 0
        if (handle_comments):
            comment_re = re.compile(r'^{.*} ')
            split_re = re.compile(r'(?<!\}) ')
            token_re = re.compile(r'(?:{[^}]*} *)?[^ ]+')
        else:
            split_re = re.compile(r' ')
            token_re = re.compile(r'[^ ]+')
        for line in infile:
            line = line.strip()
            wds = []
#            wds = split_re.split(line)
            result = token_re.match(line)
            while (result):
                wds.append( result.group() )
                line = token_re.sub("",line,1)
                line = line.strip()
                result = token_re.match(line)
            tokens = []
            if (handle_comments):
                for wd in wds:
                    result = comment_re.match(wd)
                    if (result):
                        wd = comment_re.sub("",wd)
                        token = Token( string=wd, comment=result.group() )
                    else:
                        comment = ""
                        token = Token( string=wd )
                    tokens.append( token )
            else:
                for wd in wds:
                    token = Token( string=wd )
                    tokens.append( token )
            tagged_tokens = self.tag_token_sequence( tokens,
                                                     feat_options=feat_options,
                                                     beam_size=beam_size )
            tagged_sent = " ".join( [tok.__str__() for tok in tagged_tokens] )
            print >> outfile, tagged_sent
        print >> sys.stderr, "done in %s sec." %(time.time()-t0)
        return


    def load_tag_dictionary(self, filepath ):
        print >> sys.stderr, "Loading tag dictionary...",
        self.tag_dict = unserialize( filepath )
        print >> sys.stderr, "done."
        return


    def load_lexicon(self, filepath ):
        print >> sys.stderr, "Loading external lexicon...",
        self.lex_dict = unserialize( filepath )
        print >> sys.stderr, "done."
        return
    
    


    
    
