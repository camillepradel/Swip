#!/usr/bin/env python
# -*- coding: utf-8 -*-

import utils
import re

class TokenClassifier:

    def __init__(self,
                 first_clustering_file=None, 
                 use_cap_info=True,
                 clues_method_str='compile_clues_for_deflected_tokens',
                 ldelim='K-',
                 rdelim='-K',
                 clusteridtruncation=0,
                 clusternboccmin=0,
                 val2_col=None
                 ):
         """ Class to store clues to classify strings (tokens).
         donttouch : set of tokens 
         clues : a list of pairs : a regexp, and a string representing the class to classify the token into
         use_cap_info : boolean indicating whether to duplicate classes for tokens with or without upper first letter
         clusteridtruncation : if set to positive value, take only this number of characters for the cluster id (used to model cluster granularity)
         clusternboccmin : if set to positive value, record only mappings for forms appearing more than this number of occ in the unsupervised resource
         """
         # tokens that should not be classified
         self.donttouch = set(['--LBR--','--RBR--','','-RRB-','-LRB-', '<RBR>','<LBR>','(',')',',',';','.','?','!','\'', '"','-','...',':','[',']','{','}','/','=','<','>','«','»'])

         # first level of clustering, if a mapping file is provided
         self.token2cluster = {}
         if first_clustering_file <> None:
             self.token2cluster = utils.load_mapping(first_clustering_file, 
                                                     sep='\t', 
                                                     key_col=1, 
                                                     val_col=0,
                                                     threshold_col=2,
                                                     minocc=clusternboccmin,
                                                     val2_col=val2_col)
         self.ldelim = ldelim
         self.rdelim = rdelim

         self.clusteridtruncation = clusteridtruncation
         self.clusternboccmin = clusternboccmin

         self.unkcluster = 'UNKC_'
         self.val2_col = val2_col # hack pour le nb d'occ dans fichier de clustering...
         # second level of clustering, based on suffixes and cap info
         self.use_cap_info = use_cap_info
         self.clues = []
         if clues_method_str <> '':
             clues_method = eval('self.'+clues_method_str)
             clues_method()

         # special mix of coarse and fine-grained pos (enrique's clusters...)
         self.ftb4cat2enriquecat = {
             'V':'V',
             'VINF':'V',
             'VIMP':'V',
             'VS':'V',
             'VPP':'V',
             'VPR':'V',
             'NC':'NC',
             'NPP':'NPP',
             'CS':'CS',
             'CC':'CC',
             'CLS':'CLS',
             'CLO':'CLO',
             'CLR':'CLR',
             'CL':'CL',
             'ADJ':'A',
             'ADJWH':'A',
             'ADV':'ADV',
             'ADVWH':'ADV',
             'PRO':'PRO',
             'PROREL':'PRO',
             'PROWH':'PRO',
             'DET':'D',
             'DETWH':'D',
             'P':'P',
             'ET':'ET',
             'I':'I',
             'PONCT':'PONCT',
             'P+D':'P',
             'P+PRO':'P',
             'PREF':'PREF'
             }

    def compile_clues_for_deflected_tokens(self):
         """ very small nb of classes, with a few suffixes, used to distinguish clusters/or OOV words according to suffix info """
        # the order of clues is relevant
         self.clues.append( (re.compile("^[0-9][0-9\.\-\,_\/]*$"), 'num') )
         self.clues.append( (re.compile("(mille|cent)_|(quar|cinqu|soix|sept|ott|non)ante|dix|vingt|trente"), 'numalpha') )
         self.clues.append( (re.compile("_que$"), '_que') )
        # compounds ending with a prep (=> prepositions) example : à_la_place_de
         self.clues.append( (re.compile("_(des?|du|en|aux?|à)$"), '_prep') )
        # compounds starting with a prep, and not ending with a prep (=> adverbs) example : de_même
         self.clues.append( (re.compile("^(au|en|par|de|sans|à)_"), 'prep_') )
         self.clues.append( (re.compile("ez$|^avez_"), '_ez') )
         self.clues.append( (re.compile("ant$"), '_ant') )
         self.clues.append( (re.compile("r$"), '_r') )
#         self.clues.append( (re.compile("é$"), '_é') )
         # more portable without any accents...
         self.clues.append( (re.compile("é$"), '_ea') )
         

    def compile_clues_for_catlemma_tokens(self):
         """ used as suffixes to append to cluster ids, when token are made of cat:lemma """
        # the order of clues is relevant
         self.clues.append( (re.compile("\:[0-9][0-9\.\-\,_\/]*$"), 'num') )
         self.clues.append( (re.compile("\:((un|deux|trois|quatre|cinq|six|sept|huit|neuf|dix|onze|douze|treize|quatorze|quinze|seize|vingts?|trente|quarante|cinquante|soixante|cents?|milles?|millions?|milliards?)(?:[\-_ ](?:et[_ -])?)?)+$", re.I), 'numalpha') )

         # otherwise: capture the tag that starts the token (1 = capture number 1)
         self.clues.append( (re.compile("^([A-Z]+)"), 1) )

    def compile_clues_for_catlemma2_tokens(self):
         """ used as suffixes to append to cluster ids, when tokens are made of lemma """
        # the order of clues is relevant
         self.clues.append( (re.compile("\:[0-9][0-9\.\-\,_\/]*$"), 'num') )
         self.clues.append( (re.compile("\:((un|deux|trois|quatre|cinq|six|sept|huit|neuf|dix|onze|douze|treize|quatorze|quinze|seize|vingts?|trente|quarante|cinquante|soixante|cents?|milles?|millions?|milliards?)(?:[\-_ ](?:et[_ -])?)?)+$", re.I), 'numalpha') )

         # !!! hack to map tagged mood info to suffixes compiled as suffix in BKY French jar............
         self.clues.append( (re.compile("_que$"), '_ique') )
         # compounds ending with a prep (=> prepositions) example : à_la_place_de
         self.clues.append( (re.compile("_(des?|du|en|aux?|à)$"), '_eront') )
         # compounds starting with a prep, and not ending with a prep (=> adverbs) example : de_même
         self.clues.append( (re.compile("^(au|en|par|de|sans|à)_"), '_erait') )
         # prefixes ending with '-' (neo- afro- ...)
         self.clues.append( (re.compile("\-$"), '_issons') )

         # !!! hack to map tagged mood info to suffixes compiled as suffix in BKY French jar............
         self.clues.append( (re.compile("^VINF"), '_aient') )
         self.clues.append( (re.compile("^VPR"), '_ant') )
         self.clues.append( (re.compile("^V\:"), '_eriez') )
         self.clues.append( (re.compile("^VS\:"), '_eriez') )
         # for other parts-of-speech : keep track of 'r' only
         self.clues.append( (re.compile("r$"), '_r') )


    def classify_token(self,token,removecatpref=False):
        """ method to classify a token according to the clustering mapping and according to self.clues """
        if token in self.donttouch :
            if self.val2_col <> None:
                token = token + '\t_'
            return token 

#        # (temporary) hack : added this compound, but not in the clusters...
#        if token == 'en_ce_qui_concerne':
#            return self.ldelim + '1100111111111111110'+self.rdelim

        tokenkey = token
        if removecatpref == True:
            r = token.find(':')
            if r > -1:
                cat = token[:r]
                tokenkey = token[r+1:]
        elif removecatpref == 'enrique':
            r = token.find(':')
            if r > -1:
                cat = token[:r]
                if cat in self.ftb4cat2enriquecat:
                    c = self.ftb4cat2enriquecat[cat]
                else:
                    c = cat
                tokenkey = c + ':' + token[r+1:]
            

        # the cluster
        occ = 0
        if tokenkey in self.token2cluster:
            if self.val2_col <> None:
                cluster = self.token2cluster[tokenkey][0]
                occ = self.token2cluster[tokenkey][1]
            else:
                cluster = self.token2cluster[tokenkey]
            if self.clusteridtruncation > 0:
                cluster = cluster[:self.clusteridtruncation]
            ntoken = self.ldelim + cluster + self.rdelim
        else:
            ntoken = self.unkcluster

        # the suffix for the cluster
        for (regexp, theclass) in self.clues:
            m = regexp.search(token)
            if m:
                if isinstance(theclass, int):
                    if m.group(theclass):
                        theclass = m.group(theclass)
                # for numalpha, don't use the cluster anyway (TODO:clusterise with numalpha as one type!!)
                if theclass == 'numalpha' and token.lower() <> 'un':
                    ntoken = theclass
                else:
                    ntoken = ntoken+theclass
                break
        if self.use_cap_info and tokenkey[0].isupper():
            ntoken = 'C'+ntoken
        if self.val2_col <> None:
            ntoken = ntoken + '\t' + str(occ)
        return ntoken
        
