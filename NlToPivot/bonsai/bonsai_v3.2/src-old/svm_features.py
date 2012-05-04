#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#

# Listes des traits SVM utilisés par le labeller fonctionnel
# Pour les utiliser:
# ex: print SVM_features.names

class SVM_features:
	names = ['WH','REL','INT_MARK','VCLS','MOOD','PASSIVE','CSUB','LHS_CAT','DEP_CAT','DEP_WORD','DEP_HEAD_CAT','HEAD_CAT','HEAD_WORD','COP','LEFT_SIBLING_CAT','RIGHT_SIBLING_CAT','COHEAD_CAT','COHEAD_WORD','DEP_YIELD_LEN','HEAD_DIST']
	types = ['list','list','list','list','list','list','list','list','list','openlist','list','list','openlist','list','list','list','list','openlist','int','int'] 

names = ['WH','REL','INT_MARK','VCLS','MOOD','PASSIVE','CSUB','LHS_CAT','DEP_CAT','DEP_WORD','DEP_HEAD_CAT','HEAD_CAT','HEAD_WORD','COP','LEFT_SIBLING_CAT','RIGHT_SIBLING_CAT','COHEAD_CAT','COHEAD_WORD','DEP_YIELD_LEN','HEAD_DIST']
types = ['list','list','list','list','list','list','list','list','list','openlist','list','list','openlist','list','list','list','list','openlist','int','int'] 


def build_feature_map():
	fmap = {}
	for i in range(len(names)):
		fmap[names[i]] = types[i]
	return fmap
def dump_features(fmap):
	print '#', ','.join(fmap.keys())
		
