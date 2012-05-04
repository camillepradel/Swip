# -*- coding: utf-8 -*-

''' POS tagging feature extraction '''

import sys
import re

"""
TODO: caching lex. feature computation
"""


# regexes for word form feature computation
number = re.compile("\d")
hyphen = re.compile("\-")
upper = re.compile("[A-Z]")
allcaps = re.compile("^[A-Z]+$")



class Instance:

    def __init__(self, index, tokens, label=None, lex_dict={}, tag_dict={},
                 feat_selection={}, cache={}):
        self.label = label
        self.fv = []
        self.feat_selection = feat_selection
        # token 
        self.token = tokens[index]
        self.index = index
        self.word = self.token.string
        # lexicons
        self.lex_dict = lex_dict
        self.tag_dict = tag_dict  
        self.cache = cache ## TODO
        # contexts
        win = feat_selection.get('win',2) # TODO: different wins for different types of contexts
        self.context_window = win
        self.set_contexts( tokens, index, win )
        return


    def set_contexts(self, toks, idx, win):
        lconx = toks[:idx][-win:]
        rconx = toks[idx+1:][:win]
        self.left_wds = [tok.string for tok in lconx]
        if len(self.left_wds) < win:
            self.left_wds = ["<s>"] + self.left_wds
        self.left_labels = [tok.label for tok in lconx]
        self.right_wds = [tok.string for tok in rconx]
        if len(self.right_wds) < win:
            self.right_wds += ["</s>"] 
        self.lex_left_tags = {}
        self.lex_right_tags = {}
        if self.lex_dict:
            self.lex_left_tags = ["|".join(self.lex_dict.get(tok.string,{"unk":1}).keys())
                                  for tok in lconx if tok is not None]
            self.lex_right_tags = ["|".join(self.lex_dict.get(tok.string,{"unk":1}).keys())
                                   for tok in rconx if tok is not None]
        if self.tag_dict:
            self.train_left_tags = ["|".join(self.tag_dict.get(tok.string,{"unk":1}).keys())
                                    for tok in lconx if tok is not None]
            self.train_right_tags = ["|".join(self.tag_dict.get(tok.string,{"unk":1}).keys())
                                    for tok in rconx if tok is not None]
        return


    def add(self,name,value):
        f = u'%s=%s' %(name,value)
        self.fv.append( f )
        return f


    def add_cached_feats(self,features):
        self.fv.extend(features)
        return


    def __str__(self):                                                     
        return u'%s\t%s' %(self.label,u" ".join(self.fv))


    def get_features(self):
        self.get_static_features()
        self.get_sequential_features()
        return
    

    def get_sequential_features(self):
        ''' features based on preceding tagging decisions '''
        prev_labels = self.left_labels
        for n in range(1,self.context_window+1):
            if len(prev_labels) >= n:
                # unigram for each position
                if n == 1:
                    unigram = prev_labels[-n]
                else:
                    unigram = prev_labels[-n:-n+1][0]
                self.add('ptag-%s' %n, unigram)
                if n > 1:
                    # ngrams where 1 < n < window 
                    ngram = prev_labels[:n]
                    self.add('ptagS-%s' %n, "#".join(ngram))
        return


    def get_static_features(self):
        ''' features that can be computed independently from previous
        decisions'''
        self.get_word_features()
        self.get_conx_features()
        if self.lex_dict:
            self.add_lexicon_features()
        # NOTE: features for tag dict currently turned off
        # if self.tag_dict: 
        #     self.add_tag_dict_features()
        return


    def get_word_features(self, ln=5):
        ''' features computed based on word form: word form itself,
        prefix/suffix-es of length ln: 0 < n < 5, and certain regex
        patterns'''
        word = self.word
        index = self.index
        # word string-based features
        if word in self.cache:
            # if wd has been seen, use cache
            self.add_cached_features(self.cache[word])
        else:
            # word string
            self.add('wd',word)
            # suffix/prefix
            wd_ln = len(word)
            for i in range(1,ln):
                if wd_ln >= i:
                    self.add('pref%i' %i, word[:i])
                    self.add('suff%i' %i, word[-i:])
        # regex-based features
        self.add( 'nb', number.search(word) != None )
        self.add( 'hyph', hyphen.search(word) != None )
        uc = upper.search(word)
        self.add( 'uc', uc != None)
        self.add( 'niuc', uc != None and index > 0)
        self.add( 'auc', allcaps.match(word) != None)
        return



    def get_conx_features(self):
        ''' ngrams word forms in left and right contexts '''
        win = self.context_window
        lwds = self.left_wds
        rwds = self.right_wds
        # left/right contexts: ONLY UNIGRAMS FOR NOW
        for n in range(1,win+1):
            # LHS
            if len(lwds) >= n:
                # unigram
                if n == 1:
                    left_unigram = lwds[-n] 
                else:
                    left_unigram = lwds[-n:-n+1][0]
                self.add('wd-%s' %n, left_unigram)
                # ngram
                # if n > 1:
                #    left_ngram = lwds[-n:]
                #    self.add('wdS-%s' %n, "#".join(left_ngram))
            # RHS
            if len(rwds) >= n:
                # unigram
                right_unigram = rwds[n-1:n][0]
                self.add('wd+%s' %n, right_unigram)
                # ngram
                # if n > 1:
                #    right_ngram = rwds[:n]
                #    self.add('wdS+%s' %n, "#".join(right_ngram))
        # surronding contexts
        # if win % 2 == 0:
        #     win /= 2
        #     for n in range(1,win+1):
        #         surr_ngram = lwds[-n:] + rwds[:n]
        #         if len(surr_ngram) == 2*n:
        #             self.add('surr_wds-%s' %n, "#".join(surr_ngram))

        return



    def _add_lex_features(self, dico, ltags, rtags, feat_suffix): # for lex name
        lex_wd_feats = self.feat_selection.get('lex_wd',0)
        lex_lhs_feats = self.feat_selection.get('lex_lhs',0)
        lex_rhs_feats = self.feat_selection.get('lex_rhs',0)
        if lex_wd_feats:
            # ------------------------------------------------------------
            # current word
            # ------------------------------------------------------------
            word = self.word
            lex_tags = dico.get(word,{})
            if not lex_tags and self.index == 0:
                # try lc'ed version for sent initial words
                lex_tags = dico.get(word.lower(),{})
            if len(lex_tags) == 0:
                self.add('%s' %feat_suffix, "unk")
            elif len(lex_tags) == 1:
                # unique tag
                self.add('%s-u' %feat_suffix, lex_tags.keys()[0])
            else:
                # disjunctive tag
                self.add('%s-disj' %feat_suffix,"|".join(lex_tags))
                # individual tags in disjunction
                for t in lex_tags:
                    self.add('%s-in' %feat_suffix,t)

        # left and right contexts
        win = self.context_window
        for n in range(1,win+1):
            # ------------------------------------------------------------
            # LHS
            # ------------------------------------------------------------
            if lex_lhs_feats:
                if len(ltags) >= n:
                    # unigram
                    if n == 1:
                        left_unigram = ltags[-n]
                    else:
                        left_unigram = ltags[-n:-n+1][0]
                    self.add('%s-%s' %(feat_suffix,n), left_unigram)
                    # ngram
                    if n > 1:
                        left_ngram = ltags[-n:]
                        self.add('%sS-%s' %(feat_suffix,n), "#".join(left_ngram))
            # ------------------------------------------------------------
            # RHS
            # ------------------------------------------------------------
            if lex_rhs_feats:
                if len(rtags) >= n:
                    # unigram
                    right_unigram = rtags[n-1:n][0]
                    self.add('%s+%s' %(feat_suffix,n), right_unigram)
                    # ngram
                    if n > 1:
                        right_ngram = rtags[:n]
                        self.add('%sS+%s' %(feat_suffix,n), "#".join(right_ngram))
                        
        # surronding contexts
        if lex_lhs_feats and lex_rhs_feats:
            if win % 2 == 0:
                win /= 2
                for n in range(1,win+1):
                    surr_ngram = ltags[-n:] + rtags[:n]
                    if len(surr_ngram) == 2*n:
                        self.add('%s-surr-%s' %(feat_suffix,n), "#".join(surr_ngram))        
        return


    def add_lexicon_features(self):
        lex = self.lex_dict
        l_tags = self.lex_left_tags
        r_tags = self.lex_right_tags
        self._add_lex_features( lex, l_tags, r_tags, feat_suffix='lex')
        return


    def add_tag_dict_features(self):
        lex = self.tag_dict
        l_tags = self.train_left_tags
        r_tags = self.train_right_tags
        self._add_lex_features( lex, l_tags, r_tags, feat_suffix='tdict' )
        return














