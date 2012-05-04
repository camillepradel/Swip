#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import re

def load_mapping(mapping_file, sep='\t', key_col=0, val_col=1, threshold_col=-1, minocc=0, val2_col=None):
     """ Loads mapping_file, splits line using sep, and maps the key to the value, the key is taken at rank key_col, the value is taken at rank val_col (ranks start at 0) 
     The last value for a given key is retained in case of multiple values for one key.
     @return dictionary containing the mapping
     """
     mapping = {}
     instream = open(mapping_file)
     line = instream.readline()
     while line:
         line = line[0:-1]
         cols = line.split(sep)
         if len(cols) >= max(key_col, val_col):
              # skip if a threshold is specified, and is not reached
              if threshold_col > -1 and minocc > 0 and len(cols) >= threshold_col and int(cols[threshold_col]) < minocc:
                   line = instream.readline()
                   continue
              # !!!!hack tempo!!!! pour charger les clusters avec forme de type  "au_-_dessous_de" et pas "au-dessous_de"
              # !!!! il faudra changer le tokeniseur pour produire les "au_-_dessous_de" (traitement du tiret)
              key2 = None
              if cols[key_col].find('-') > -1:
                   key2 = cols[key_col].replace('-','_-_')
              if val2_col <> None and val2_col < len(cols):
                   mapping[cols[key_col]] = [ cols[val_col], cols[val2_col] ]
                   if key2 <> None:
                        mapping[key2] = [ cols[val_col], cols[val2_col] ]

              else:
                   mapping[cols[key_col]] = cols[val_col]
                   if key2 <> None:
                        mapping[key2] = cols[val_col]

         line = instream.readline()
     return mapping

class TagFixer:
      """
      A TagFixer is a class mapping a conflated symbol like VINF to a base category V and (optionally) a feature structure
      e.g. VINF-> basecat=V, mood=inf, NC --> basecat=N, subcat=common
      """
      def __init__(self):
            """
            Creates an empty tag fixer with a type definition specified by attlist
            """
            self.mappings = {}

      def set_tagmap(self,orig_tag,dest_tag,list_of_features=[]):
            """
            This sets a mapping from a conflated tag to its analytical decomposition.
            e.g. 'VINF' --> 'V', ('mood','inf')
            
            @param orig_tag: the tag to analyse
            @type orig_tag: a string
            @param dest_tag: a base category
            @type dest_tag: a string
            @param list_of_features: an optional list of couples (2-tuples) of the form (attribute,value)
            @type list_of_features: a list of 2-tuples of strings
            """
            self.mappings[orig_tag] = (dest_tag,list_of_features)
		
      def map_tag(self,tag):
            """
            This returns a couple of the form (basecat,list_of_features) being the analytical decomposition of a tag.
            
            @param tag: the tag from which to derive the analytical form
            @type tag: string
            @return:a couple (basecat,list_of_features) where list of features is None if there are no features. In case there is no mapping for tag, returns the tag itself with an empty list of features
            @rtype: a couple(string,list of couples of strings)
            """
            if self.mappings.has_key(tag):
                  return self.mappings[tag]
            else:
                  return (tag,[])

      # marie : construction du mapping inverse, pour la conversion basecat + features => complexcat
      def build_inverse_mapping(self):
            """ Builds the inverse mapping : from basecat to complexcat + features
            in order to facilitate the computation of complexcat from basecat + features
            The method sets the dictionary inverse_mappings
            key = basecat, 
            value = list of pairs : (complexcat, dictionary of features)
                    the list of pairs is sorted, with decreasing order of number of features (so that (ADJINT, {subcat:1}) appears before (ADJ, {}))
                    """
            self.inverse_mappings = {}
            for complextag in self.mappings:
                  (basecat, list_of_features) = self.map_tag(complextag)
                  dfeats = dict(list_of_features)
                  if basecat in self.inverse_mappings:
                        self.inverse_mappings[basecat].append( (complextag, dfeats) )
                        # sort the feature dictionaries with decreasing order of keys
                        s = sorted(self.inverse_mappings[basecat], 
                                   lambda x,y: cmp(len(y[1].keys()),len(x[1].keys())))
                        self.inverse_mappings[basecat] = s
                  else:
                        self.inverse_mappings[basecat] = [ (complextag, dfeats) ]
            
def ftb4_fixer():

	tfixer = TagFixer()
	tfixer.set_tagmap('V','V',[('mood','ind')])
	tfixer.set_tagmap('VINF','V',[('mood','inf')])
	tfixer.set_tagmap('VIMP','V',[('mood','imp')])
	tfixer.set_tagmap('VS','V',[('mood','subj')])
	tfixer.set_tagmap('VPP','V',[('mood','part'),('tense','past')])
	tfixer.set_tagmap('VPR','V',[('mood','part'),('tense','pst')])

	# marie à valider : pb cf. NC est la cat par défaut pour subcat différente de p (subcat = C ou subcat = card)
        # => introduire disjonction sur les traits, avec une valeur prioritaire
        #tfixer.set_tagmap('NC','N',[('subcat','c')])
        tfixer.set_tagmap('NC','N',[])
	tfixer.set_tagmap('NPP','N',[('subcat','p')])

        # marie debug (?) for the subcat attr, keep the values as in original FTB
	#tfixer.set_tagmap('CS','C',[('subcat','sub')])
	#tfixer.set_tagmap('CC','C',[('subcat','coord')])
	tfixer.set_tagmap('CS','C',[('subcat','s')])
	tfixer.set_tagmap('CC','C',[('subcat','c')])

        # marie debug (?) for the subcat attr, keep the values as in original FTB
	#tfixer.set_tagmap('CLS','CL',[('subcat','subj')])
        tfixer.set_tagmap('CLS','CL',[('subcat','suj')])
	tfixer.set_tagmap('CLO','CL',[('subcat','obj')])
	tfixer.set_tagmap('CLR','CL',[('subcat','refl')])
	tfixer.set_tagmap('CL','CL',[('subcat','')])
	
	tfixer.set_tagmap('ADJWH','A',[('subcat','int')])
	tfixer.set_tagmap('ADJ','A',[])

	tfixer.set_tagmap('ADVWH','ADV',[('subcat','int')])
	tfixer.set_tagmap('ADV','ADV',[])
	
	tfixer.set_tagmap('PROREL','PRO',[('subcat','rel')])
	tfixer.set_tagmap('PROWH','PRO',[('subcat','int')])
	tfixer.set_tagmap('PRO','PRO',[])

       	tfixer.set_tagmap('DET','D',[])
	tfixer.set_tagmap('DETWH','D',[('subcat','int')])

	tfixer.set_tagmap('P+D','P+D',[('subcat','def')])
	tfixer.set_tagmap('P+PRO','P+PRO',[('subcat','rel')])
	return tfixer

