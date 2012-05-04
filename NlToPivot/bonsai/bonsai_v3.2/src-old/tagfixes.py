#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import sys
#This is a class that converts abstract analytical tags to atomic symbols and vice-versa
class TagFixer:
    def __init__(self):
	self.analytic_2_pos = {}
	self.pos_2_analytic = {}

    #all the fields have to be filled up when a field is irrelevant put a '*' in it.
    def add_pos_mapping(self,cat,subcat,mood,tense,pers,num,gen,pos_code):
	self.analytic_2_pos[cat+'+'+subcat+'+'+mood+'+'+tense+'+'+pers+'+'+num+'+'+gen] = pos_code
	self.pos_2_analytic[pos_code] = (cat,subcat,mood,tense,pers,num,gen,pos_code)

    def map_pos(self,cat,subcat,mood,tense,pers,num,gen):#this is error prone (redo later if needed)
	if self.analytic_2_pos.has_key(cat+'+'+subcat+'+'+mood+'+'+tense+'+'+pers+'+'+num+'+'+gen):    #try perfect match
	    return  self.analytic_2_pos[cat+'+'+subcat+'+'+mood+'+'+tense+'+'+pers+'+'+num+'+'+gen]
	elif self.analytic_2_pos.has_key(cat+'+'+subcat+'+'+mood+'+'+tense+'+'+'*'+'+'+'*'+'+'+'*'):   #apply morphological mask
	    return  self.analytic_2_pos[cat+'+'+subcat+'+'+mood+'+'+tense+'+'+'*'+'+'+'*'+'+'+'*']
	elif self.analytic_2_pos.has_key(cat+'+'+subcat+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*'):      #apply 2ndmorphological mask
	    return  self.analytic_2_pos[cat+'+'+subcat+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*']
	elif self.analytic_2_pos.has_key(cat+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*'):         #apply subcat mask
	    return  self.analytic_2_pos[cat+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*'+'+'+'*']
	else:
	    sys.stderr.write('Mapping failed : ('+cat+'+'+subcat+'+'+mood+'+'+tense+'+'+pers+'+'+num+'+'+gen+')\n')
	    return cat+'&' # '&' is an error code to indicate that sth went wrong

    def unmap_pos(self,pos_code):
	if self.pos_2_analytic.has_key(pos_code):
	    return self.pos_2_analytic[pos_code]
	else:
	    sys.stderr.write('Mapping failed : unknown code ('+pos_code+')\n')
	    return None
#####EOClass

#Returns a tagfixer that will convert an analytic representation into tagset 2
def get_tagset2_fixer():
    tf = TagFixer()
    tf.add_pos_mapping('V','*','participe','passe','*','*','*','VPP')
    tf.add_pos_mapping('V','*','participe','present','*','*','*','VPR')
    tf.add_pos_mapping('V','*','infinitif','*','*','*','*','VINF')
    tf.add_pos_mapping('V','*','indicatif','present','*','*','*','VP')
    tf.add_pos_mapping('V','*','indicatif','futur','*','*','*','VF')
    tf.add_pos_mapping('V','*','imperatif','present','*','*','*','VIMP')
    tf.add_pos_mapping('V','*','indicatif','imparfait','*','*','*','VIPF')
    tf.add_pos_mapping('V','*','indicatif','passe-simple','*','*','*','VPAST')
    tf.add_pos_mapping('V','*','subjonctif','imparfait','*','*','*','VSIPF')
    tf.add_pos_mapping('V','*','subjonctif','present','*','*','*','VSP')
    tf.add_pos_mapping('V','*','indicatif','conditionnel','*','*','*','VC')
    tf.add_pos_mapping('V','*','indicatif','present','3','s','*','VP3SG')
    tf.add_pos_mapping('V','*','indicatif','present','3','p','*','VP3PL')
    tf.add_pos_mapping('N','C','*','*','*','*','*','NC')
    tf.add_pos_mapping('N','P','*','*','*','*','*','NPP')
    tf.add_pos_mapping('N','card','*','*','*','*','*','NC') #to remap ? unclear to me for now  (Problem related to mapping compounds)
    tf.add_pos_mapping('N','*','*','*','*','*','*','NC') #to remap ? unclear to me for now  (Problem related to mapping compounds)
    tf.add_pos_mapping('A','*','*','*','*','*','*','ADJ')
    tf.add_pos_mapping('A','int','*','*','*','*','*','ADJWH')
    tf.add_pos_mapping('C','C','*','*','*','*','*','CC')
    tf.add_pos_mapping('C','S','*','*','*','*','*','CS')
    tf.add_pos_mapping('C','*','*','*','*','*','*','CS')#Unsure whether this is correct (Problem related to mapping compounds)
    tf.add_pos_mapping('P','*','*','*','*','*','*','P')
    tf.add_pos_mapping('P+D','*','*','*','*','*','*','P+D')
    tf.add_pos_mapping('P+PRO','*','*','*','*','*','*','P+PRO')
    tf.add_pos_mapping('ADV','*','*','*','*','*','*','ADV')
    tf.add_pos_mapping('ADV','int','*','*','*','*','*','ADVWH')
#   tf.add_pos_mapping('ADV','neg','*','*','*','*','*','ADVNEG')
    tf.add_pos_mapping('D','*','*','*','*','*','*','DET')
#   tf.add_pos_mapping('D','def','*','*','*','*','*','DDEF')
#   tf.add_pos_mapping('D','dem','*','*','*','*','*','DDEM')
#   tf.add_pos_mapping('D','poss','*','*','*','*','*','DPOSS')   
    tf.add_pos_mapping('D','int','*','*','*','*','*','DETWH')
    tf.add_pos_mapping('CL','*','*','*','*','*','*','CL')
    tf.add_pos_mapping('CL','suj','*','*','*','*','*','CLS')
    tf.add_pos_mapping('CL','obj','*','*','*','*','*','CLO')
    tf.add_pos_mapping('CL','refl','*','*','*','*','*','CLR')
    tf.add_pos_mapping('PRO','*','*','*','*','*','*','PRO')
    tf.add_pos_mapping('PRO','rel','*','*','*','*','*','PROREL')
    tf.add_pos_mapping('PRO','int','*','*','*','*','*','PROWH')
    #tf.add_pos_mapping('PRO','pers','*','*','*','*','*','PROPER')
    #tf.add_pos_mapping('PRO','poss','*','*','*','*','*','PROPOSS')
    #tf.add_pos_mapping('PRO','indef','*','*','*','*','*','PROIND')
    tf.add_pos_mapping('I','*','*','*','*','*','*','I')
    tf.add_pos_mapping('ET','*','*','*','*','*','*','ET')
    tf.add_pos_mapping('PONCT','*','*','*','*','*','*','PONCT')
    tf.add_pos_mapping('PREF','*','*','*','*','*','*','PREF')
    return tf

    
def get_tagset4_fixer():
    tf = TagFixer()
    tf.add_pos_mapping('V','*','*','*','*','*','*','VUNK')
    tf.add_pos_mapping('V','*','participe','passe','*','*','*','VPP')
    tf.add_pos_mapping('V','*','participe','present','*','*','*','VPR')
    tf.add_pos_mapping('V','*','infinitif','*','*','*','*','VINF')
    tf.add_pos_mapping('V','*','indicatif','present','*','*','*','V')
    tf.add_pos_mapping('V','*','indicatif','futur','*','*','*','V')
    tf.add_pos_mapping('V','*','imperatif','present','*','*','*','VIMP')
    tf.add_pos_mapping('V','*','indicatif','imparfait','*','*','*','V')
    tf.add_pos_mapping('V','*','indicatif','passe-simple','*','*','*','V')
    tf.add_pos_mapping('V','*','subjonctif','imparfait','*','*','*','VS')
    tf.add_pos_mapping('V','*','subjonctif','present','*','*','*','VS')
    tf.add_pos_mapping('V','*','indicatif','conditionnel','*','*','*','V')
    tf.add_pos_mapping('V','*','indicatif','present','3','s','*','V')
    tf.add_pos_mapping('V','*','indicatif','present','3','p','*','V')
    tf.add_pos_mapping('N','C','*','*','*','*','*','NC')
    tf.add_pos_mapping('N','P','*','*','*','*','*','NPP')
    tf.add_pos_mapping('N','card','*','*','*','*','*','NC') #to remap ? unclear to me for now  (Problem related to mapping compounds)
    tf.add_pos_mapping('N','*','*','*','*','*','*','NC') #to remap ? unclear to me for now  (Problem related to mapping compounds)
    tf.add_pos_mapping('A','*','*','*','*','*','*','ADJ')
    tf.add_pos_mapping('A','int','*','*','*','*','*','ADJWH')
    tf.add_pos_mapping('C','C','*','*','*','*','*','CC')
    tf.add_pos_mapping('C','S','*','*','*','*','*','CS')
    tf.add_pos_mapping('C','*','*','*','*','*','*','CS')#Unsure whether this is correct (Problem related to mapping compounds)
    tf.add_pos_mapping('P','*','*','*','*','*','*','P')
    tf.add_pos_mapping('P+D','*','*','*','*','*','*','P+D')
    tf.add_pos_mapping('P+PRO','*','*','*','*','*','*','P+PRO')
    tf.add_pos_mapping('ADV','*','*','*','*','*','*','ADV')
    tf.add_pos_mapping('ADV','int','*','*','*','*','*','ADVWH')
#   tf.add_pos_mapping('ADV','neg','*','*','*','*','*','ADVNEG')
    tf.add_pos_mapping('D','*','*','*','*','*','*','DET')
#   tf.add_pos_mapping('D','def','*','*','*','*','*','DDEF')
#   tf.add_pos_mapping('D','dem','*','*','*','*','*','DDEM')
#   tf.add_pos_mapping('D','poss','*','*','*','*','*','DPOSS')   
    tf.add_pos_mapping('D','int','*','*','*','*','*','DETWH')
    tf.add_pos_mapping('CL','*','*','*','*','*','*','CL')
    tf.add_pos_mapping('CL','suj','*','*','*','*','*','CLS')
    tf.add_pos_mapping('CL','obj','*','*','*','*','*','CLO')
    tf.add_pos_mapping('CL','refl','*','*','*','*','*','CLR')
    tf.add_pos_mapping('PRO','*','*','*','*','*','*','PRO')
    tf.add_pos_mapping('PRO','rel','*','*','*','*','*','PROREL')
    tf.add_pos_mapping('PRO','int','*','*','*','*','*','PROWH')
    #tf.add_pos_mapping('PRO','pers','*','*','*','*','*','PROPER')
    #tf.add_pos_mapping('PRO','poss','*','*','*','*','*','PROPOSS')
    #tf.add_pos_mapping('PRO','indef','*','*','*','*','*','PROIND')
    tf.add_pos_mapping('I','*','*','*','*','*','*','I')
    tf.add_pos_mapping('ET','*','*','*','*','*','*','ET')
    tf.add_pos_mapping('PONCT','*','*','*','*','*','*','PONCT')
    tf.add_pos_mapping('PREF','*','*','*','*','*','*','PREF')
    return tf


def get_tagsetA_fixer():
    tf = get_tagset4_fixer()
    tf.add_pos_mapping('D','poss','*','*','*','*','*','DPOSS')
    tf.add_pos_mapping('D','dem','*','*','*','*','*','DDEM')
    tf.add_pos_mapping('D','def','*','*','*','*','*','DDEF')
    tf.add_pos_mapping('P+D','def','*','*','*','*','*','P+DDEF')
    return tf

# marie :
# treebank4 tagset, adapted to MFT corpus
# common nouns are NNC and not NC (as NC already a non terminal)
# + ADVne mapped to ADV
def get_tagset4mft_fixer():
    tf = get_tagset4_fixer()
    tf.add_pos_mapping('ADVne','*','*','*','*','*','*','ADV')
    tf.add_pos_mapping('N','C','*','*','*','*','*','NNC')
    return tf

# marie :
# treebank4 tagset, plus card Determiners retagged as Adj
def get_tagset6_fixer():
    tf = get_tagset4_fixer()
    tf.add_pos_mapping('D','card','*','*','*','*','*','AC')
    return tf

# marie : 
# tagset as specified for MFT
def get_MFTtagset_fixer():
    tf = TagFixer()
    tf.add_pos_mapping('V','*','*','*','*','*','*','VUNK')
    tf.add_pos_mapping('V','*','participe','passe','*','*','*','V_part')
    tf.add_pos_mapping('V','*','participe','present','*','*','*','V_part')
    tf.add_pos_mapping('V','*','infinitif','*','*','*','*','V_inf')
    tf.add_pos_mapping('V','*','indicatif','present','*','*','*','V_finite')
    tf.add_pos_mapping('V','*','indicatif','futur','*','*','*','V_finite')
    tf.add_pos_mapping('V','*','imperatif','present','*','*','*','V_finite')
    tf.add_pos_mapping('V','*','indicatif','imparfait','*','*','*','V_finite')
    tf.add_pos_mapping('V','*','indicatif','passe-simple','*','*','*','V_finite')
    tf.add_pos_mapping('V','*','subjonctif','imparfait','*','*','*','V_finite')
    tf.add_pos_mapping('V','*','subjonctif','present','*','*','*','V_finite')
    tf.add_pos_mapping('V','*','indicatif','conditionnel','*','*','*','V_finite')
    tf.add_pos_mapping('V','*','indicatif','present','3','s','*','V_finite')
    tf.add_pos_mapping('V','*','indicatif','present','3','p','*','V_finite')
    tf.add_pos_mapping('N','C','*','*','*','*','*','N')
    tf.add_pos_mapping('N','P','*','*','*','*','*','N')
    tf.add_pos_mapping('N','card','*','*','*','*','*','N_card')
    tf.add_pos_mapping('N','*','*','*','*','*','*','N') 
    tf.add_pos_mapping('A','*','*','*','*','*','*','A')
    tf.add_pos_mapping('A','int','*','*','*','*','*','A_int')
    tf.add_pos_mapping('C','C','*','*','*','*','*','C_C')
    tf.add_pos_mapping('C','S','*','*','*','*','*','C_S')
    tf.add_pos_mapping('C','*','*','*','*','*','*','C_S')#Unsure whether this is correct (Problem related to mapping compounds)
    tf.add_pos_mapping('P','*','*','*','*','*','*','P')
    tf.add_pos_mapping('P+D','*','*','*','*','*','*','P+D')
    tf.add_pos_mapping('P+PRO','*','*','*','*','*','*','P+PRO')
    tf.add_pos_mapping('ADV','*','*','*','*','*','*','ADV')
    tf.add_pos_mapping('ADV','int','*','*','*','*','*','ADV_int')
    tf.add_pos_mapping('ADV','neg','*','*','*','*','*','ADVne')
    tf.add_pos_mapping('D','*','*','*','*','*','*','D')
#   tf.add_pos_mapping('D','def','*','*','*','*','*','DDEF')
#   tf.add_pos_mapping('D','dem','*','*','*','*','*','DDEM')
#   tf.add_pos_mapping('D','poss','*','*','*','*','*','DPOSS')   
    tf.add_pos_mapping('D','int','*','*','*','*','*','D_int')
    tf.add_pos_mapping('CL','*','*','*','*','*','*','CL')
    tf.add_pos_mapping('CL','suj','*','*','*','*','*','CL')
    tf.add_pos_mapping('CL','obj','*','*','*','*','*','CL')
    tf.add_pos_mapping('CL','refl','*','*','*','*','*','CL')
    tf.add_pos_mapping('PRO','*','*','*','*','*','*','PRO')
    tf.add_pos_mapping('PRO','rel','*','*','*','*','*','PRO_rel')
    tf.add_pos_mapping('PRO','int','*','*','*','*','*','PRO_int')
    #tf.add_pos_mapping('PRO','pers','*','*','*','*','*','PROPER')
    #tf.add_pos_mapping('PRO','poss','*','*','*','*','*','PROPOSS')
    #tf.add_pos_mapping('PRO','indef','*','*','*','*','*','PROIND')
    tf.add_pos_mapping('I','*','*','*','*','*','*','I')
    tf.add_pos_mapping('ET','*','*','*','*','*','*','ET')
    tf.add_pos_mapping('PONCT','*','*','*','*','*','*','PONCT')
    tf.add_pos_mapping('PREF','*','*','*','*','*','*','PREF')
    return tf

def get_tagset5_fixer():
    tf = TagFixer()
    tf.add_pos_mapping('V','*','participe','passe','*','*','*','VPP')
    tf.add_pos_mapping('V','*','participe','present','*','*','*','VPR')
    tf.add_pos_mapping('V','*','infinitif','*','*','*','*','VINF')
    tf.add_pos_mapping('V','*','indicatif','present','*','*','*','V')
    tf.add_pos_mapping('V','*','indicatif','present','3','*','*','V3')
#    tf.add_pos_mapping('V','*','indicatif','present','3','p','*','V3P')
#    tf.add_pos_mapping('V','*','indicatif','present','3','s','*','V3S')
    tf.add_pos_mapping('V','*','indicatif','futur','*','*','*','V')
    tf.add_pos_mapping('V','*','indicatif','futur','3','*','*','V3')
#    tf.add_pos_mapping('V','*','indicatif','futur','3','p','*','V3P')
#    tf.add_pos_mapping('V','*','indicatif','futur','3','s','*','V3S')
    tf.add_pos_mapping('V','*','imperatif','present','*','*','*','VIMP')
    tf.add_pos_mapping('V','*','indicatif','imparfait','*','*','*','V')
    tf.add_pos_mapping('V','*','indicatif','imparfait','3','*','*','V3')
#    tf.add_pos_mapping('V','*','indicatif','imparfait','3','p','*','V3P')
#    tf.add_pos_mapping('V','*','indicatif','imparfait','3','s','*','V3S')
    tf.add_pos_mapping('V','*','indicatif','passe-simple','*','*','*','V')
    tf.add_pos_mapping('V','*','indicatif','passe-simple','3','*','*','V3')
#    tf.add_pos_mapping('V','*','indicatif','passe-simple','3','p','*','V3P')
#    tf.add_pos_mapping('V','*','indicatif','passe-simple','3','s','*','V3S')
    tf.add_pos_mapping('V','*','subjonctif','imparfait','*','*','*','VS')
    tf.add_pos_mapping('V','*','subjonctif','imparfait','3','*','*','VS3')
    tf.add_pos_mapping('V','*','subjonctif','present','*','*','*','VS')
    tf.add_pos_mapping('V','*','subjonctif','present','3','p','*','VS3')
#    tf.add_pos_mapping('V','*','subjonctif','present','3','p','*','VS3P')
#    tf.add_pos_mapping('V','*','subjonctif','present','3','s','*','VS3S')
    tf.add_pos_mapping('V','*','indicatif','conditionnel','*','*','*','V')
    tf.add_pos_mapping('V','*','indicatif','conditionnel','3','*','*','V3')
#    tf.add_pos_mapping('V','*','indicatif','conditionnel','3','p','*','V3P')
#    tf.add_pos_mapping('V','*','indicatif','conditionnel','3','s','*','V3S')
    tf.add_pos_mapping('N','C','*','*','*','*','*','NC')
    tf.add_pos_mapping('N','C','*','*','*','*','*','NC')
    tf.add_pos_mapping('N','P','*','*','*','*','*','NPP')
    tf.add_pos_mapping('N','card','*','*','*','*','*','NC') #to remap ? unclear to me for now  (Problem related to mapping compounds)
    tf.add_pos_mapping('N','*','*','*','*','*','*','NC') #to remap ? unclear to me for now  (Problem related to mapping compounds)
    tf.add_pos_mapping('A','*','*','*','*','*','*','ADJ')
    tf.add_pos_mapping('A','int','*','*','*','*','*','ADJWH')
    tf.add_pos_mapping('C','C','*','*','*','*','*','CC')
    tf.add_pos_mapping('C','S','*','*','*','*','*','CS')
    tf.add_pos_mapping('C','*','*','*','*','*','*','CS')#Unsure whether this is correct (Problem related to mapping compounds)
    tf.add_pos_mapping('P','*','*','*','*','*','*','P')
#    tf.add_pos_mapping('P','de','*','*','*','*','*','DE')# if splitde option set, de gets special tag
#    tf.add_pos_mapping('DE','*','*','*','*','*','*','DE')# if splitde option set, de gets special tag
    tf.add_pos_mapping('P+D','*','*','*','*','*','*','P+D')
    tf.add_pos_mapping('P+PRO','*','*','*','*','*','*','P+PRO')
    tf.add_pos_mapping('ADV','*','*','*','*','*','*','ADV')
    tf.add_pos_mapping('ADV','int','*','*','*','*','*','ADVWH')
#   tf.add_pos_mapping('ADV','neg','*','*','*','*','*','ADVNEG')
    tf.add_pos_mapping('D','*','*','*','*','*','*','DET')
#   tf.add_pos_mapping('D','def','*','*','*','*','*','DDEF')
#   tf.add_pos_mapping('D','dem','*','*','*','*','*','DDEM')
#   tf.add_pos_mapping('D','poss','*','*','*','*','*','DPOSS')   
    tf.add_pos_mapping('D','int','*','*','*','*','*','DETWH')
    tf.add_pos_mapping('CL','*','*','*','*','*','*','CL')
    tf.add_pos_mapping('CL','suj','*','*','*','*','*','CLS')
    tf.add_pos_mapping('CL','obj','*','*','*','*','*','CLO')
    tf.add_pos_mapping('CL','refl','*','*','*','*','*','CLR')
    tf.add_pos_mapping('PRO','*','*','*','*','*','*','PRO')
    tf.add_pos_mapping('PRO','rel','*','*','*','*','*','PROREL')
    tf.add_pos_mapping('PRO','int','*','*','*','*','*','PROWH')
    #tf.add_pos_mapping('PRO','pers','*','*','*','*','*','PROPER')
    #tf.add_pos_mapping('PRO','poss','*','*','*','*','*','PROPOSS')
    #tf.add_pos_mapping('PRO','indef','*','*','*','*','*','PROIND')
    tf.add_pos_mapping('I','*','*','*','*','*','*','I')
    tf.add_pos_mapping('ET','*','*','*','*','*','*','ET')
    tf.add_pos_mapping('PONCT','*','*','*','*','*','*','PONCT')
    tf.add_pos_mapping('PREF','*','*','*','*','*','*','PREF')
    return tf
