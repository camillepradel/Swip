#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import sys
import re 
import os

# Marie : lexicon loading
# Lefff only for now

class Lefff:

    def __init__(self, lefffloc, clusternum=False, ignoreP2=True, lowerfirstword=True):
        self.form2morph = {}
        self.morph2form = {}
        # dictionary for already encountered forms -> disinflected forms
        self.form2dflform = {}
        # reg exp for numerical expresssions
        self.numre = re.compile("^[0-9][0-9\.\-\,_\/]*$")
        self.clusternum = clusternum
        # whether to ignore second person verb forms
        self.ignoreP2 = ignoreP2
        # whether to try lowering first word of sentence, if unknown in dict
        self.lowerfirstword = lowerfirstword

        self.read_dir_or_file(lefffloc)

        self.moods_and_tenses = {'P': ('indicatif','present'),
                                 'I': ('indicatif','imparfait'),
                                 'J': ('indicatif','passe-simple'),
                                 'F': ('indicatif','futur'),
                                 'S': ('subjonctif','present'),
                                 'T': ('subjonctif','passe'),
                                 'K': ('participe','passe'),
                                 'G': ('participe','present'),
                                 'Y': ('imperatif','present'),
                                 'W': ('infinitif','*'),
                                 }
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
        # caramba! ça marche pas
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

        m = re.match('^(.+?)_+[0-9]+$', lemma)
        if m <> None:
            lemma = m.group(1)
        key = lemma+'+'+morphcode #+'+'+cat
        if form not in self.form2morph:
            self.form2morph[form] = set([ key ])
        else:
            self.form2morph[form].add(key)

#        elif morph not in self.form2morph[form]:
#            self.form2morph[form][morph] = [lemma]
#        else:
#            self.form2morph[form][morph].append = lemma

        if lemma not in self.morph2form:
            self.morph2form[lemma] = { morphcode : form }
        #elif morphcode not in self.morph2form[lemma]:
        else:
            self.morph2form[lemma][morphcode] = form

    # get a form from lemma+morphcode+cat
    def from_morph2form(self, lemma, morphcode):
        if lemma in self.morph2form and morphcode in self.morph2form[lemma]:
            return self.morph2form[lemma][morphcode]
        return None

    def get_morphs(self, form):
        if form in self.form2morph:
            return self.form2morph[form]
        return None

    def form_is_known(self, form):
        return self.get_morphs(form) <> None

    def get_features(self, morph):
        """ interpret the lefff codes, and set the relevant features,
        returns a dict """
        (lemma, code) = re.split('\+',morph)
        feats = { 'lemma': lemma}
        # verbal codes
        m = re.match('([PFSTIJGYWKC]+)([123]*)([mf]?)([ps]?)$', code)
        if m <> None:
            (mood, tense) = self.moods_and_tenses[m.group(1)[0]]
            feats['mood'] = mood
            feats['tense'] = tense
            if m.group(2) <> '':
                # last person (if '13' => 3)
                feats['pers'] = m.group(2)[-1]
            if m.group(3) <> '':
                feats['gender'] = m.group(3)
            if m.group(4) <> '':
                feats['num'] = m.group(4)
        # todo : codes for other categories
        return feats
            
    def plural2sing(self, morph):
        nform = None
        (lemma, code) = re.split('\+',morph)
        #print "plural2sing:"+morph+":"+lemma+':'+code
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
        (lemma, code) = re.split('\+',morph)
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
        (lemma, code) = re.split('\+',morph)
        # si verbe, et verbe conjugué (a une personne)
        # en fait pas de vrai test sur la cat : 
        # matche aussi les possessifs mais peu importe
        if re.search('[123]', code) <> None:
            nform = self.from_morph2form(lemma, 'P2p')
            if nform == None:
                nform = self.from_morph2form(lemma, 'P3s')
        return nform

    def desinflect_simple_form(self,form,isnotfirst=1):
        # d'=>de n'=>ne qu'=>que etc...
        if form.endswith("'"):
            form = form[0:-1] + 'e'
        # arg!!
        if form == 'des': return form
        if form == 'À': return 'à'

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
                    nl = self.desinflect_simple_form(l, isnotfirst=1)
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

    def desinflect(self,form, isnotfirst=1):
        if not self.lowerfirstword: isnotfirst = 1 
        if self.clusternum:
            m = self.numre.search(form)
            if m:
                return 'NUMEXPR'
        # desinflect components of compounds
        components = form.split('_')
        # only first component may be decapitalized during desinflection, if isnotfirst==0
        n = [ self.desinflect_simple_form(components[0],isnotfirst) ]
        #if not isnotfirst: print "NOT ISNOTFIRST", n
        # other components are not
        for component in components[1:]:
            n.append(self.desinflect_simple_form(component))
        return '_'.join(n)

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
             
        
            
