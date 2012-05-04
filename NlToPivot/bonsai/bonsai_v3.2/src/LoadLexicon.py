#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import re 
import os
# cjson much faster than pickle, cPickle, json to reload the lefff structures...
# and a little faster than simplejson
import cjson
#import simplejson as json
#import json
#import pickle
#import cPickle as pickle

from utils import TagFixer
from utils import ftb4_fixer

# Marie : lexicon loading
# Lefff only for now

HackedDfl = {'des':'des',
             'À':'à',
             # PB TODO : for a few forms, various masc singular forms share the same lemma
             # and thus could be clustered
             # e.g. fou / fol, beau / bel
             # ce / cet : not to cluster, because 'ce' is ambiguous
             # and also ... Monsieur / monsieur / M. / Mr. => manually clustered here
             'Monsieur':'monsieur',
             'M.':'monsieur',
             'Mr.':'monsieur',
             'bel':'beau',
             '|':'/',
             'n°':'numéro',
             'aux':'au',
             'Aux':'au',# pb with 'au', contracted in lefff
             'AUX':'au',
             'AU':'au',
             'Au':'au'             
             }

class Lefff:

    def __init__(self, lefffloc, clusternum=False, ignoreP2=True, lowerfirstword=True, serialized_input=False):
        self.form2morph = {} # key=form, val = set of lemma+morphcode+cat
        self.morph2form = {} # key1=lemma, key2=morphcode, val=form
        # dictionary for already encountered forms -> disinflected forms
        self.form2dflform = {}
        # dictionary for already encountered form+cat -> lemmas + feats
        self.form2lemmafeats = {}
        # reg exp for numerical expresssions
        self.numre = re.compile("^[0-9][0-9\.\-\,_\/]*$")
        self.clusternum = clusternum
        # poncts
        self.poncts = set(['...','..','(',')','[',']','{','}','!',';','?','"','<','>',"'",'Â«','Â»',' ','&','Â§','$','%','#','=']) # lefff turns = it into "égale"...
        # whether to ignore second person verb forms
        self.ignoreP2 = ignoreP2
        # whether to try lowering first word of sentence, if unknown in dict
        self.lowerfirstword = lowerfirstword

        self.alphanumre = re.compile("^((un|deux|trois|quatre|cinq|six|sept|huit|neuf|dix|onze|douze|treize|quatorze|quinze|seize|vingts?|trente|quarante|cinquante|soixante|cents?|milles?|millions?|milliards?)(?:[\-_ ](?:et[_ -])?)?)+$", re.I)

        # ordinals, unambiguous
        self.ordinalre = re.compile("^(1er|[0-9IVX]+(e|[eè]mes?))$|((^second|^derni(er|ère)|ième)s?)$")
        # ambiguous ordinals
        self.ambigordinalre = re.compile("^(premi(er|ère)|seconde)s?$")

        # tenses should match with those used in TagFixer
        self.moods_and_tenses = {'P': ('ind','pst'),
                                 'I': ('ind','impft'),
                                 'J': ('ind','past'),
                                 'F': ('ind','fut'),
                                 'S': ('subj','pst'),
                                 'T': ('subj','past'),
                                 'K': ('part','past'),
                                 'G': ('part','pst'),
                                 'Y': ('imp','pst'),
                                 'C': ('ind','cond'),
                                 'W': ('inf',None),
                                 }
        self.lefffcat2cat = {        
            'adj':'A',
            'adjPref':'PREF',
            'adv':'ADV',
            'advPref':'PREF',
            'advm':'ADV',
            'advneg':'ADV',
            'advp':'ADV',
            'auxAvoir':'V',
            'auxEtre':'V',
            'caimp':'PRO',
            'ce':'CL',
            #'cf':'',
            #'cfi':'',
            'cla':'CL',
            'clar':'CL',
            'cld':'CL',
            'cldr':'CL',
            'clg':'CL',
            'cll':'CL',
            'cln':'CL',
            'clneg':'ADV',
            'clr':'CL',
            'coo':'C',
            'csu':'C',
            'det':'D',
            'ilimp':'CL',
            'nc':'N',
            'np':'N',
            'parentf':'',
            'parento':'',
            'poncts':'PONCT',
            'ponctw':'PONCT',
            'prel':'PRO',
            'prep':'P',
            #'pres':'',
            'pri':'ADV',
            'pro':'PRO',
            'que':'C',
            'que_restr':'C',
            #'sbound':'',
            #'suffAdj':'',
            'v':'V'
            }
        # mapping from ftb4cat to ftbmincat + features
        self.fixer = ftb4_fixer() 
        self.lemmastoplist = set(['ilimp','çaimp','ccl'])

        self.somelemmas = {
            ('qu\''):'que',
            ('au'):'à', # cat P+D recalls that it was au and not à
            ('aux'):'à',
            ('du'):'de', # a voir si pb...
            ('des'):'de',
            ('y'):('y',{'pers':'3'}),
            ('en'):('en',{'pers':'3'}),
            ('se'):('se',{'pers':'3'}), # pb ambiguity in lefff : se fem and se masc
            ('s\'','CS'):'si',
            ('s\'','CLR'):('se',{'pers':'3'}),
            ('me','CLO'):'clad', # ambiguity dative/ acc
            ('te','CLO'):'clad', # ambiguity dative/ acc
            ('nous','CLO'):'clad', # ambiguity dative/ acc
            ('vous','CLO'):'clad', # ambiguity dative/ acc
            ('%'):('%',{'gender':'m','subcat':'c'}), # pour être cohérent avec le ftb
            ('M.'):('M.',{'gender':'m','number':'s','subcat':'c'}), # on ne veut pas Monsieur (?)
            ('MM.'):('MM.',{'gender':'m','number':'p','subcat':'c'}),
            ('À'):'à',
            # missing as such in lefff (contracted)
            ('auxquels'):'auquel',
            ('auxquelles'):'auquel',
            ('desquels'):'duquel',
            ('desquelles'):'duquel',

            }
        self.somesubcats = {
            'aucun':'neg',
            'guère':'neg',
            'jamais':'neg',
            'ne':'neg',
            'ne pas':'neg',
            'non':'neg',
            'non pas':'neg',
            'non plus':'neg',
            'nul':'neg',
            'nulle part':'neg',
            'pas':'neg',
            'pas du tout':'neg',
            'personne':'neg',
            'plus':'neg',
            'point':'neg',
            'rien':'neg',
            'son':'poss',
            'sien':'poss',
            'vôtre':'poss',
            'nôtre':'poss',
            'lui':'pers',
            'quelqu\'un':'ind',
            'quelque':'ind',
            'quelque_chose':'ind',
            'quelques':'ind',
            'quiconque':'ind',
            'aucun':'ind',
            'autre':'ind',
            'autrui':'ind',
            'certains':'ind',
            'chacun':'ind',
            'tout':'ind',
            'un':'ind',
            'le':'def',
            '.':'s',
            '!':'s',
            '?':'s',
            '...':'s',
            'celui-ci':'dem',
            'celui-là':'dem',
            'celui':'dem',
            'ça':'dem',
            'cela':'dem',
            'ceci':'dem',
            'ce':'dem' # on doit filtrer le cas ce=clitique...
            }

        self.defaultsubcats = {
            'PONCT':'w',
            'NC':'c', # NC may be also subcat=card ...
            'ADJ':'qual',
            }

        self.serialized_input = serialized_input

        # read from .lex lefff files
        if not self.serialized_input:
            self.read_dir_or_file(lefffloc)
        # or from json dump
        else:
            f = open(lefffloc+'.form2morph.json','r')

            ## self.form2morph = json.load(f)

            # arg! I'd like to load utf-8 strings... but cjson won't!
            #l = f.readline().decode('unicode-escape').encode('utf-8')
            self.form2morph = cjson.decode(f.readline())
            f.close()
            f = open(lefffloc+'.morph2form.json','r')
            #self.morph2form = json.load(f)
            self.morph2form = cjson.decode(f.readline())
            f.close()
            
    def is_ponct_form(self, form):
        return form in self.poncts

    # loads .lex file or all .lex files in dir
    def read_dir_or_file(self, dir_or_file):
        if os.path.isdir(dir_or_file):
            for file in os.listdir(dir_or_file):
                if re.search("\.lex$", file) <> None and file <> 'uw.lex':
                    stream = open(os.path.join(dir_or_file, file))
                    self.add_words(stream)
                    stream.close()
        else:
            stream = open(dir_or_file)
            self.add_words(stream)

    def add_words(self, stream):
        for line in stream.readlines():
            self.add_word(line)

    # a line for *.lex lefff files
    def add_word(self, line, skipifprio=True):
        #(form, prio, cat, stuff, lemma, isdefault, morphcode) = re.split('\t',line)
        (form, prio, cat, stuff, lemma, isdefault, morphcode, rest) = re.split('\t',line)
        # attention:format du lefff différent : le morph code n'est plus en fin de ligne
        # caramba! Ã§a marche pas
        #morphcode.replace('\n','')
        #morphcode = morphcode[0:-1]
        # skip unlikely form/cat associations
        if skipifprio and prio=='50':
            return
        # skip strange forms (eg : 'le__det')
        if re.search('__', form):
            return
        # skip second person present verb forms, if specified
        if self.ignoreP2 and morphcode in ['P2s', 'PS2s']:
            return

        m = re.match('^(.+?)\??(__+[a-z]+)?_+[0-9]+$', lemma)
        if m <> None:
            lemma = m.group(1)
        key = lemma+'+'+morphcode+'+'+ self.get_cat(cat)
        if form not in self.form2morph:
            self.form2morph[form] = set([ key ])
        else:
            self.form2morph[form].add(key)

        if lemma not in self.morph2form:
            self.morph2form[lemma] = { morphcode : form }
        else:
            self.morph2form[lemma][morphcode] = form

    # get a form from lemma+morphcode+cat
    def from_morph2form(self, lemma, morphcode):
        # very ugly : all the code uses utf-8 strings..., but cjson will load unicode strings only...
        if self.serialized_input:
#            if lemma in self.morph2form:
#                if morphcode in self.morph2form[lemma]:
#                    return self.morph2form[lemma][morphcode].encode('utf-8')
#                return None
            ulemma = lemma.decode('utf-8')
            if ulemma in self.morph2form and morphcode in self.morph2form[ulemma]:
                return self.morph2form[ulemma][morphcode].encode('utf-8')
        else:
            if lemma in self.morph2form and morphcode in self.morph2form[lemma]:
                return self.morph2form[lemma][morphcode]
        return None

    def get_cat(self, lefffcat):
        if lefffcat in self.lefffcat2cat:
            return self.lefffcat2cat[lefffcat]
        return 'unk'

    def get_morphs(self, form):
        # very ugly : all the code uses utf-8 strings..., but cjson will load unicode strings only...
        if self.serialized_input:
#            if form in self.form2morph:
#                return [x for x in self.form2morph[form]]
            uform = form.decode('utf-8')
            if uform in self.form2morph:
                return [x.encode('utf-8') for x in self.form2morph[uform]]
        elif form in self.form2morph:
                return self.form2morph[form]
        return None

    def form_is_known(self, form):
        return self.get_morphs(form) <> None

    def get_lefff_features(self, morph, knowncat, ftb4cat=None):
        """ interprets the lefff codes, and sets the relevant features,
        returns a dict of features """
        (lemma, code, cat) = re.split('\+',morph,2)
        if cat <> knowncat:
            return None
        feats = { 'lemma': lemma}
        # codes
        m = re.match('([PFSTIJGYWKC]*)([123]*)([mf]?)([ps]?)(_P[123][sp])?$', code)
        if m <> None:
            if m.group(1) <> '':
                c = m.group(1)[0]
                if c in self.moods_and_tenses:
                    (mood, tense) = self.moods_and_tenses[c]
                    feats['mood'] = mood
                    feats['tense'] = tense
                else:
                    print "PB pour ce code:", code, lemma, morph
                if m.group(2) <> '':
                    # last person (if '13' => 3)
                    feats['pers'] = m.group(2)[-1]
            if m.group(3) <> '':
                feats['gen'] = m.group(3)
            if m.group(4) <> '':
                feats['num'] = m.group(4)
        # rem : lefff contains verbal lemmas with cat adj for participles employed as adjs
        # some are redundant with adjectival entries
        # (adaptée => adaptée, adapté, adj + adaptée, adapter, past part + adaptée, adapter, adj
        # => we keep them
        # if 'mood' in feats and feats['mood'] == 'part' and cat == 'A':
        #    return None
        # todo : codes for other categories

        return feats
            
    def plural2sing(self, morph):
        nform = None
        (lemma, code, cat) = re.split('\+',morph,2)
#        (lemma, code) = re.split('\+',morph,1)

        ocode = code
        # if code ends with p
        m = re.match('^(.*?)([mf]?)p$',code)
        if m <> None:
            pref = m.group(1)
            gender = m.group(2)
            if not gender:
                # try masc + sing
                nform = self.from_morph2form(lemma, pref+'ms')
                if nform <> None : return nform
            code = pref + gender + 's'

        # for possessive p_P1p, ms_P2s
        m = re.match('([mf]?)[ps](_.*)$', code)
        if m <> None:
            pref = m.group(1)
            suff = m.group(2)
            # feminine set to masc already here ...
            if pref:
                pref = 'm'
            code = pref +'s'+suff
        if code <> ocode:
            nform = self.from_morph2form(lemma, code)
        return nform

    def fem2masc(self, morph):
        nform = None
        (lemma, code, cat) = re.split('\+',morph,2)
#        (lemma, code) = re.split('\+',morph,1)
        #print "fem2masc:", morph
        m = re.match('^(.*)f([sp]?)$', code)
        if m <> None:
            code = m.group(1)+'m'+m.group(2)
            nform = self.from_morph2form(lemma, code)
            # if unknown try without number (lyonnaise (fs) => lyonnais (m))
            if nform == None:
                code = m.group(1)+'m'
                nform = self.from_morph2form(lemma, code)
                
        return nform

    def desinflectverbs(self,morph):
        nform = None
        (lemma, code, cat) = re.split('\+',morph,2)
#        (lemma, code) = re.split('\+',morph,1)
        # si verbe, et verbe conjugué (a une personne)
        # en fait pas de vrai test sur la cat : 
        # matche aussi les possessifs mais peu importe
        if re.search('[123]', code) <> None:
            nform = self.from_morph2form(lemma, 'P2p')
            if nform == None:
                nform = self.from_morph2form(lemma, 'P3s')
        return nform

    def desinflect_simple_form(self, form, isnotfirst=1):
        # d'=>de n'=>ne qu'=>que etc...
        if form.endswith("'"):
            form = form[0:-1] + 'e'
        if form in self.poncts: return form
        # a few hacks...
        if form in HackedDfl: return HackedDfl[form]

        # get the different morphological analysis for this form
        morphs = self.get_morphs(form)

        # if form is unknown in dictionary
        if morphs == None:
            # if first word of sentence and word is capitalized
            # => try to uncap
            if isnotfirst==0:
                # hack : si premier mot = 'A' => sans doute = "à"
                if (form == 'A'):
                    return "à"
                l = form.lower()
                if l <> form:
                    nl = self.desinflect_simple_form(l, isnotfirst=1) # for the lower form, isnotfirst=0 is useless
                    # example : La -> la -> le
                    if nl <> l:
                        return nl
                    # example : Le -> le -> le
                    if self.get_morphs(l):
                        return l
                    # else, unkwnon word even when lowered => return the original capitalized word
                    return form
            # unknown in dic, and islower => nothing to do
            return form

        nform = None
        for morph in morphs:
            nnform = self.plural2sing(morph)
            # stop if any of the forms cannot undergo plural->sing
            #         or if different codes result in different forms
            if nnform == None or (nnform <> nform and nform <> None):
                break
            nform = nnform
        # if all resulting forms are the same form (i.e. no break)
        else:
            form = nform
            morphs = self.get_morphs(form)

        nform = None
        if morphs == None:
            return form
        for morph in morphs:
            nnform = self.fem2masc(morph)
            # stop if any of the codes cannot undergo fem->masc
            #         or if different codes result in different forms
            if nnform == None or (nnform <> nform and nform <> None):
                break
            nform = nnform
        # if all resulting forms are the same form
        else:
            form = nform
            morphs = self.get_morphs(form)

        nform = None
        if morphs == None:
            return form
        for morph in morphs:
            nnform = self.desinflectverbs(morph)
            # stop if any of the codes cannot undergo verb desinflection
            #         or if different codes result in different forms
            if nnform == None or (nnform <> nform and nform <> None):
                break
            nform = nnform
        # if all resulting forms are the same form
        else:
            form = nform
            morphs = self.get_morphs(form)

        return form

    def desinflect(self, form, isnotfirst=1):
        """ desinflection of a token 
        If isnotfirst is False (first token) and token unknown in lexicon, 
        it will also try to desinflect the corresponding low case token"""
        if not self.lowerfirstword: isnotfirst = 1 
        if self.clusternum:
            m = self.numre.search(form)
            if m:
                return 'NUMEXPR'
        # normalisation of compounds
        if form.endswith("_des"): form = form[0:-2] + 'u'    
        if form.endswith("_aux"): form = form[0:-1]
        # desinflect components of compounds
        components = form.split('_')
        # only first component may be decapitalized during desinflection, if isnotfirst==0
        n = [ self.desinflect_simple_form(components[0], isnotfirst) ]
        # other components are not
        for component in components[1:]:
            n.append(self.desinflect_simple_form(component))
        return '_'.join(n)

    def get_desinflected_form(self, token, isnotfirst=1):
        """ desinflection of a token
        Searches in self.form2dflform whether desinflected form was stored
        otherwise computes and stores the desinflected form """
        # if token is not first word, or token is all in low case
        l = token.lower()
        if isnotfirst > 0 or l==token:
            # try to get an already disinflected form
            if token in self.form2dflform:
                ntoken = self.form2dflform[token]
            else:
                ntoken = self.desinflect(token,isnotfirst)
                self.form2dflform[token] = ntoken
        # if token is the first word, and token is capitalized 
        else:
            # try whether the low case token is known already
            if l in self.form2dflform:
                ntoken = self.form2dflform[l]
            # otherwise just compute the desinflect form, but don't store it
            # (never store desinflection of a non-low case first token, since it is contextually dependent :
                # different result whether first token or not
            else:
                ntoken = self.desinflect(token,isnotfirst=0)
        return ntoken
        
    def dump_form2dflform(self, outfile):
        try:
            outstream = open(outfile, 'w')
        except IOError:
            sys.stderr.write("Impossible d'ouvrir "+outfile+'\n')
            return
        for token in sorted(self.form2dflform.keys()):
            ntoken = self.form2dflform[token]
            outstream.write(token + '\t' + ntoken + '\t' + ('**changed**' if ntoken <> token else '') + '\n')
        outstream.close()

    def normalise_compounds(self,form, ftb4cat):
        """ from ftb-like compound forms to ftb-like compound lemma """ 
        if form[0:2] == 'A_':
            form = 'à_' + form[2:]
        form = form.replace('_-_', '-')
        # si '_ présent dans la chaine : il s'agit d'un composé => si non NPP on minusculise
        # (après on aura perdu l'information que c'était un composé...)
        if form.find('\'_') > -1 and ftb4cat not in ['NPP','ET']:
            form = form.lower()
        form = form.replace('\'_', '\'')
        form = form.replace('_',' ')
        if form[0] == ' ': form = '_'+form[1:]
        if form[-1] == ' ': form = form[:-1]+'_' 
        return form

    def get_lefff_lemma_and_feats(self, form, ftb4cat, priorlemma=None, isfirst=False, ftbmincat=None, usepriorlemma=False):
        """ Returns the longest matching lefff lemma, if one exists that matches the given category """
        if (form, ftb4cat) in self.form2lemmafeats:
            return self.form2lemmafeats[(form, ftb4cat)]

        form = self.normalise_compounds(form,ftb4cat)
        feats = {}
        lemma = None # found lemma
        clemma = None # candidate default lemma

        # get feats imposed by the ftb4cat 
        if ftbmincat == None:
            (ftbmincat, featlist) = self.fixer.map_tag(ftb4cat)
            feats = dict(featlist)
        # some fixed results
        if ftb4cat == 'CLS': 
            self.form2lemmafeats[ (form, ftb4cat) ] = ('cln',feats, None)
            return ('cln',feats, None)
        key = None
        if (form,ftb4cat) in self.somelemmas:
            key = (form,ftb4cat)
        elif form in self.somelemmas:
            key = form
        if key <> None:
            if isinstance(self.somelemmas[key],str):
                self.form2lemmafeats[ (form, ftb4cat) ] = (self.somelemmas[key], feats, None)
                return (self.somelemmas[key], feats, None)
            else:
                nfeats = self.somelemmas[key][1]
                lemma = self.somelemmas[key][0]
                feats.update(nfeats)
                self.form2lemmafeats[ (form, ftb4cat) ] = (lemma, feats, None)
                return (lemma, feats, None)

        if ftb4cat == 'PONCT':
            lemma = form
        # lookup in the lefff
        if lemma == None:
            morphs = self.get_morphs(form)
            candidates = []
            if morphs <> None:
                feats_alt = []
                for morph in morphs:
#                if form in ['ne pas','eÃ»t']:
#                    print form, morph
                    cfeats = self.get_lefff_features(morph, ftbmincat, ftb4cat=ftb4cat)
#                if form in ['ne pas','eÃ»t']:
#                    print form, morph, feats
                    if cfeats <> None and cfeats['lemma'] not in self.lemmastoplist and self.are_unifiable(feats,cfeats):
                        candidates.append(cfeats)
                if len(candidates) > 0:
                # for several possible lemmas, take the longest
                    n = sorted(candidates, lambda x,y: cmp(len(y['lemma'].decode('utf-8')),len(x['lemma'].decode('utf-8'))))
                    lemma = n[0]['lemma']
                    del n[0]['lemma']
                    feats.update(n[0])
        # for tokens starting with uppercase :
        #  -- either any such token appearing at the beginning of the sent
        #  -- or any that is non NPP (we trust here the tagging info...)
            if lemma == None and (isfirst or (ftb4cat <> 'NPP')) and form.lower() <> form:
                (lemma, nfeats,clemma) = self.get_lefff_lemma_and_feats(form.lower(), ftb4cat, priorlemma=priorlemma, isfirst=False,ftbmincat=None,usepriorlemma=False)
                if lemma <> None:
                    feats.update(nfeats)
                elif form[0] == 'E':
                    (lemma, nfeats,clemma) = self.get_lefff_lemma_and_feats('é'+form[1:].lower(), ftb4cat, priorlemma=priorlemma, isfirst=False, ftbmincat=None,usepriorlemma=False)
                    if lemma <> None:  feats.update(nfeats)
            if lemma == None and form[0] == 'E':
                (lemma, nfeats,clemma) = self.get_lefff_lemma_and_feats('É'+form[1:], ftb4cat, priorlemma=priorlemma, isfirst=False, ftbmincat=None,usepriorlemma=False)
                if lemma <> None:  feats.update(nfeats)
        # correct Morfette's lemma for NPPs 
        if lemma == None and priorlemma <> None:
            if ftb4cat == 'NPP' and form[0].isupper() and not priorlemma[0].isupper():
                lemma = form

        if lemma == None:
            # lemmas for compounds : normalisations
            nform = form
            if form.find(' ') > -1 and form.endswith("'"):
                nform = form[0:-1] + 'e'
        # grâce_aux => grâce_à (meme s'il est mal taggé : P au lieu de P+D...)
            elif form.endswith(" aux"):
                nform = form[0:-3] + 'à'
            elif form.endswith(" au"):
                nform = form[0:-2] + 'à'
        # en_dépit_des => en_dépit_de
            elif form.endswith(" des"):
                nform = form[0:-1]
            elif form.endswith(" du"):
                nform = form[0:-1] + 'e'
        # if it's a compound and not an NPP => take lower form as lemma
            if form.find(' ',0,-1) > -1 and form[0].isupper() and ftb4cat not in ['NPP','ET']:
#        if form[0].isupper() and ftb4cat not in ['NPP','ET']:
                nform = nform.lower()
            if form <> nform:
                lemma = nform

        # subcat feature :
        # Default subcat according to lemma
        if lemma in self.somesubcats and not 'subcat' in feats:
            feats['subcat'] = self.somesubcats[lemma]

        # cardinal and ordinal subcats
        if 'subcat' not in feats:
            m = self.numre.search(form)
            n = self.alphanumre.search(form)
            if m or n:
                feats['subcat'] = 'card'
            else:
                m = self.ordinalre.search(form)
                if m:
                    feats['subcat'] = 'ord'
                elif ftbmincat == 'A':
                    m = self.ambigordinalre.search(form)
                    if m:
                        feats['subcat'] = 'ord'
        # Default subcat according to cat
        if 'subcat' not in feats and ftb4cat in self.defaultsubcats:
            feats['subcat'] = self.defaultsubcats[ftb4cat]

        if lemma == None:
            if usepriorlemma:
                clemma = priorlemma
            else:
#                clemma = 'UNKL'+self.guess_unk_lemma(form,ftb4cat,feats,isfirst)
                clemma = self.guess_unk_lemma(form,ftb4cat,feats,isfirst)

        if lemma <> None:
            lemma = lemma.replace(' ','_')
        if clemma <> None:
            clemma = clemma.replace(' ','_')
        # pb ambiguity in lefff
        if form in ['l\'','L\''] and 'gender' in feats:
            del feats[gender]
        self.form2lemmafeats[ (form, ftb4cat) ] = (lemma, feats, clemma)
        return (lemma, feats, clemma)

    def are_unifiable(self, feats1, feats2):
        for key in feats1:
            if key in feats2 and feats1[key] <> feats2[key]:
                return False
        return True

    def guess_unk_lemma(self, form, ftb4cat, feats, isfirst):
        """ very very qucik lemma guesser without any other information than cat...."""
        # pbs : compounds : only last component is lemmatized
        # pbs : gras => gra : imposer suppression du s si prÃ©cÃ©dent = e,i,u ou consonne?
        if 'subcat' in feats and feats['subcat'] == 'card':
            return form
        if isfirst and ftb4cat not in ['ET','NPP'] and form[0].isupper():
            form = form.lower()
        if len(form) < 4: return form
        if ftb4cat == 'NC':
            if form.endswith('aux'): return form[0:-2] + 'l'
            if form.endswith('ais'): return form
            if form.endswith('s'): return form[0:-1]
        elif ftb4cat in ['ADJ','VPP']:
            if form.endswith('aux'): return form[0:-2] + 'l'
            if form.endswith('aises'): return form[0:-2]
            if form.endswith('s'): form = form[0:-1]
            if form.endswith('euse'): return form[0:-2] + 'r'            
            if form.endswith('enne'): return form[0:-2]            
            if form.endswith('elle'): return form[0:-2]            
            if form.endswith('ive'): return form[0:-2] + 'f'            
            # !!! utf-8 é => 2 characters ????!!!
            if form.endswith('ière'): return form[0:-4] + 'er'            
            if form.endswith('le') and form[-3] not in ['b','p']: return form[0:-1]            
            if form.endswith('te') and form[-3] <> 's': return form[0:-1]            
            if form.endswith('ne'): return form[0:-1]            
            if form.endswith('se'): return form[0:-1]            
            if form.endswith('de'): return form[0:-1]            
            if form.endswith('ue') and form[-3] <> 'q': return form[0:-1]            
            if form.endswith('ée'): return form[0:-1]
        #return 'UNKNOTG'+form
        return form

    def formatfeats(self,featdict):
        """ Builds a string out of a dict of features, as in ftb conll format ( | separated list of features, where each attribute is the first character of the ftb attribute) """ 
        a = [ key[0]+'='+str(featdict[key]) for key in sorted(featdict.keys()) if key <> 'lemma' and featdict[key] <> None]
        if len(a) > 0:
            return '|'.join(a)
        return '_'

def load_serialized_lefff(serial_file):
    p = open(serial_file, 'r')
    return pickle.load(p)

if __name__ == "__main__":
    from optparse import OptionParser
    usage = """
    """+sys.argv[0]+""" LEFFFDIR
    Loads the Lefff lexicon in a python structure, and dumps it on STDOUT
    """
    parser=OptionParser(usage=usage.decode('utf-8'))
    parser.add_option("--member", dest="member", default='form2morph', help='Member of Lefff instance to dump. Default=form2morph')
    (opts,args) = parser.parse_args()
    if (len(args) > 0):
        lefffdir = args[0]
    else:
        exit(usage+'\nMissing LEFFFDIR argument!\n')
    member = str(opts.member)
    lefff = Lefff(lefffdir)
    # neither json or cjson allows to dump sets => we simply convert them to lists
    if member == 'form2morph':
        for k in lefff.form2morph:
            lefff.form2morph[k] = list(lefff.form2morph[k])
    # json or cjson dump in unicode_escape ...
    #json.dump(eval('lefff.'+member), sys.stdout, encoding='utf-8')
    sys.stdout.write(cjson.encode(eval('lefff.'+member)))

# Liste cat du lefff
# :GA
# :GN
# :GP
# :GR
# :NV
# :PV
# GA:
# GN:
# GP:
# GR:
# NV:
# PV:
# adj
# adjPref
# adv
# advPref
# advm
# advneg
# advp
# auxAvoir
# auxEtre
# caimp
# ce
# cf
# cfi
# cla
# clar
# cld
# cldr
# clg
# cll
# cln
# clneg
# clr
# coo
# csu
# det
# epsilon
# etr
# ilimp
# meta
# nc
# np
# parentf
# parento
# poncts
# ponctw
# prel
# prep
# pres
# pri
# pro
# que
# que_restr
# sbound
# suffAdj
# v
