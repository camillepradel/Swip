#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
# Enrique Henestroza Anguiano : 
# PassageDoc meant to contain the PASSAGE representation at the document, 
# sentence, relation, group, word, and token levels. 
#
# Supports the complete Passage DTD 2.1, but ignores:
#    - Traits morpho-syntaxiques: <MSTAG> tag, 'mstag' attribute for <W> tag.
#    - Pose de rèperes: <M> tag.
#    - Entités nomméés: <NE> tag.
# 
# -- printing methods into XML format for PASSAGE or Easy evaluations.
# -- XML reading utility for documents in PASSAGE or Easy formats.
#
"""

import xml.dom.minidom
from operator import itemgetter

class Token:
    def __init__(self, id, start, end, form):
        """ Token with associated form, and start/end indices

        Members:
        * id : a unique string
        * start : the starting index of the token, a nonnegative integer string
	* end : the ending index of the token, a nonnegative integer string
	* form : string for the word / token

        @param id
        @param start
        @param end
        @param form
        """

        self.id = id
        self.start = start
        self.end = end
        self.form = form

    ## Printing Functions

    def strPassage(self):
        return '    <T id=\"'+self.id+'\" start=\"'+self.start+'\" end=\"'+self.end+'\">'+escapeChars(self.form)+'</T>\n'

class Word:
    def __init__(self, id, tokens, pos, lemma, form, head):
        """ Word composed of one or more tokens, with associated pos and lemma

        Members:
        * id : a unique string
        * tokens : a list of integer ids of tokens contained in the word
        * pos : part-of-speech label for the word
	* lemma : lemma for the word
	* form : form for the word, consists of partial or multiple tokens(s)
        * head : 'true' or 'false' for whether this is a head (within a group)

        @param id
        @param tokens
        @param pos
        @param lemma
        @param form
        @param head
        """

        self.id = id
        self.tokens = tokens
        self.pos = pos
        self.lemma = lemma
        self.form = form
        self.head = head

    ## Printing Functions

    def strPassage(self):
        return '    <W id=\"'+self.id+'\" tokens=\"'+' '.join(map(str,self.tokens))+'\" pos=\"'+self.pos+'\" lemma=\"'+escapeChars(self.lemma)+'\" form=\"'+escapeChars(self.form)+'\" head=\"'+self.head+'\"/>\n'

    def strEasy(self):
        return '      <F id=\"'+self.id+'\">'+escapeChars(self.form)+'</F>\n'


class Group:
    def __init__(self, id, type, wlist):
        """ Low-level multi-word syntagmatic group

        Members:
        * id : a unique string
        * type : a PASSAGE syntagmatic type, e.g. "GN"
        * wlist : a list of word objects within the group (order may matter)

        @param id
        @param type
        @param wlist
        """

        self.id = id
        self.type = type
        self.wlist = wlist

    ## Printing Functions

    def strPassage(self):
        gs = '  <G id=\"'+str(self.id)+'\" type=\"'+self.type+'\">\n'
        for word in self.wlist:
            gs += word.strPassage()
        return gs+'  </G>\n'

    def strEasy(self):
        gs = '    <Groupe id=\"'+str(self.id)+'\" type=\"'+self.type+'\">\n'
        for word in self.wlist:
            gs += word.strEasy()
        return gs+'    </Groupe>\n'


class Relation:
    def __init__(self, id, type, refstoroles, atbstovals):
        """ Relation between two words/groups

        Members:
        * id : a unique string
        * type : one of the PASSAGE relation tags, e.g. "SUJ_V"
        * refstoroles : dictionary of gov/dpt ids mapped to corresponding 
        *     PASSAGE role tags, e.g. "sujet" or "verbe"
        * atbstovals : dictionary of any other attributes to corresponding
        *     values, e.g. 's-o' to 'sujet' or 'objet'

        @param id
        @param type
        @param refstoroles
        @param atbstovals
        """

        self.id = id
        self.type = type
        self.refstoroles = refstoroles
        self.atbstovals = atbstovals

    ## Printing Functions

    def strPassage(self):
        rs = '  <R id=\"'+str(self.id)+'\" type=\"'+self.type+'\">\n'
        for (ref, role) in sorted(self.refstoroles.items(), key=itemgetter(1)):
            rs += '      <'+role+' ref=\"'+ref+'\"/>\n'
        for (atb, val) in sorted(self.atbstovals.items()):
            rs += '      <'+atb+' valeur=\"'+val+'\"/>\n'
        rs += '  </R>\n'
        return rs

    def strEasy(self):
        rs = '    <relation id=\"'+str(self.id)+'\" type=\"'+self.type+'\" xlink:type=\"extended\">\n'
        for (ref, role) in sorted(self.refstoroles.items(), key=itemgetter(1)):
            rs += '      <'+role+' xlink:type=\"locator\" xlink:href=\"'+ref+'\"/>\n'
        for (atb, val) in sorted(self.atbstovals.items()):
            rs += '      <'+atb+' valeur=\"'+val+'\"/>\n'
        rs += '    </relation>\n'
        return rs


class Sentence:
    def __init__(self, id, tlist, wglist, rlist):
        """ Sentence with a list of token/word/group and a list of relations

        Members:
        * id : a unique string
        * tlist : a list of Token objects (order may matter)
        * wglist : a list of Word/Group objects (order may matter)
        * rlist : a list of Relation objects

        @param id
        @param tlist
        @param wglist
        @param rlist
        """

        self.id = id
        self.tlist = tlist
        self.wglist = wglist
        self.rlist = rlist

    ## Printing Functions

    def strPassage(self):
        ss = '<Sentence id=\"'+self.id+'\">\n'
        for t in self.tlist:
            ss += t.strPassage()
        for wg in self.wglist:
            ss += wg.strPassage()
        for r in self.rlist:
            ss += r.strPassage()
        return ss+'</Sentence>\n'

    def strEasy(self):
        ss = '<E id=\"'+self.id+'\">\n  <constituants>\n'
        for wg in self.wglist:
            ss += wg.strEasy()
        ss += '  </constituants>\n  <relations>\n'
        for r in self.rlist:
            ss += r.strEasy()
        return ss+'  </relations>\n</E>\n'


class PassageDoc:
    def __init__(self, name, slist):
        """ Document with a given name and containing a list of sentences

        Members:
        * Name : a string, title of the document
        * slist : a list of Sentence objects

        @param name
        @param slist
        """

        self.name = name
        self.slist = slist

    ## Printing Functions

    def strPassage(self):
        ds = '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document dtdVersion=\"2.1\" file=\"'+self.name+'\">\n'
        for sentence in self.slist:
            ds += sentence.strPassage()
        return ds+'</Document>\n'

    def strEasy(self):
        ds = '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<DOCUMENT dtdVersion=\"1.1\" fichier=\"'+self.name+'\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n'
        for sentence in self.slist:
            ds += sentence.strEasy()
        return ds+'</DOCUMENT>\n'
        

## Easy XML Reading Functions

def readEasyXML(filename):
    filestring = open(filename, 'r').read().decode("utf-8").encode("utf-8")
    document = xml.dom.minidom.parseString(filestring)
    dname = document.getElementsByTagName("DOCUMENT")[0].getAttribute("fichier")
    dslist = []
    for sentence in document.getElementsByTagName("E"):
        dslist.append(getEasySentence(sentence))
    return PassageDoc(dname, dslist)

def getEasySentence(sentence):
    sid = sentence.getAttribute("id")
    stlist = []
    swglist = []
    srlist = []
    cons_node = False
    rels_node = False
    for node in sentence.childNodes:
        if node.nodeType == node.ELEMENT_NODE:
            if node.tagName == "constituants":
                for subnode in node.childNodes:
                    if subnode.nodeType == node.ELEMENT_NODE:
                        if subnode.tagName == "F":
                            swglist.append(getEasyWord(subnode))
                        elif subnode.tagName == "Groupe":
                            swglist.append(getEasyGroup(subnode))
            elif node.tagName == "relations":
                for subnode in node.childNodes:
                    if subnode.nodeType == node.ELEMENT_NODE:
                        if subnode.tagName == "relation":
                            srlist.append(getEasyRelation(subnode))
            # Doesn't match the Easy DTD, but ELDA files do this.
            elif node.tagName == "F":
                swglist.append(getEasyWord(node))
            elif node.tagName == "Groupe":
                swglist.append(getEasyGroup(node))
    # Derive tokens from words and groups.
    for wg in swglist:
        if isinstance(wg, Word):
            stlist.append(Token("tok"+wg.id, "", "", wg.form))
            wg.tokens.append("tok"+wg.id)
        elif isinstance(wg, Group):
            for w in wg.wlist:
                stlist.append(Token("tok"+w.id, "", "", w.form))
                w.tokens.append("tok"+w.id)
    return Sentence(sid, stlist, swglist, srlist)

def getEasyWord(word):
    wid = word.getAttribute("id")
    wform = getText(word.childNodes)
    return Word(wid, [], "", "", wform, "")

def getEasyGroup(group):
    gid = group.getAttribute("id")
    gtype = group.getAttribute("type")
    gwlist = []
    for node in group.childNodes:
        if node.nodeType == node.ELEMENT_NODE and node.tagName == "F":
            gwlist.append(getEasyWord(node))
    return Group(gid, gtype, gwlist)

def getEasyRelation(relation):
    rid = relation.getAttribute("id")
    rtype = relation.getAttribute("type")
    rrefstoroles = {}
    ratbstovals = {}
    for node in relation.childNodes:
        if node.nodeType == node.ELEMENT_NODE:
            if node.hasAttribute("xlink:href"):
                rrefstoroles[node.getAttribute("xlink:href")] = node.tagName
            elif node.hasAttribute("valeur"):
                ratbstovals[node.tagName] = node.getAttribute("valeur")
    return Relation(rid, rtype, rrefstoroles, ratbstovals)

def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc).decode("utf-8").encode("utf-8")


## Passage XML Reading Functions

def readPassageXML(filename):
    document = xml.dom.minidom.parse(filename)
    dname = document.getElementsByTagName("Document")[0].getAttribute("file")
    dslist = []
    for sentence in document.getElementsByTagName("Sentence"):
        dslist.append(getPassageSentence(sentence))
    return PassageDoc(dname, dslist)

def getPassageSentence(sentence):
    sid = sentence.getAttribute("id")
    stlist = []
    swglist = []
    srlist = []
    for node in sentence.childNodes:
        if node.nodeType == node.ELEMENT_NODE:
            if node.tagName == "T":
                stlist.append(getPassageToken(node))
            elif node.tagName == "W":
                swglist.append(getPassageWord(node))
            elif node.tagName == "G":
                swglist.append(getPassageGroup(node))
            elif node.tagName == "R":
                srlist.append(getPassageRelation(node))
    # Derive word forms from tokens.
    for wg in swglist:
        if isinstance(wg, Word):
            if wg.form == "":
                wgforms = []
                for t in stlist:
                    if t.id in wg.tokens:
                        wgforms.append(t.form)
                if len(wgforms) > 0:
                    wg.form = ' '.join(wgforms)
        elif isinstance(wg, Group):
            for w in wg.wlist:
                if w.form == "":
                    wforms = []
                    for t in stlist:
                        if t.id in w.tokens:
                            wforms.append(t.form)
                    if len(wforms) > 0:
                        w.form = ' '.join(wforms)
    return Sentence(sid, stlist, swglist, srlist)

def getPassageToken(token):
    tid = token.getAttribute("id")
    tstart = token.getAttribute("start")
    tend = token.getAttribute("end")
    tform = getText(token.childNodes)
    return Token(tid, tstart, tend, tform)

def getPassageWord(word):
    wid = word.getAttribute("id")
    wtokens = word.getAttribute("tokens").split(' ')
    wpos = word.getAttribute("pos")
    wlemma = word.getAttribute("lemma")
    wform = word.getAttribute("form")
    whead = word.getAttribute("head")
    return Word(wid, wtokens, wpos, wlemma, wform, whead)

def getPassageGroup(group):
    gid = group.getAttribute("id")
    gtype = group.getAttribute("type")
    gwlist = []
    for node in group.childNodes:
        if node.nodeType == node.ELEMENT_NODE and node.tagName == "W":
            gwlist.append(getPassageWord(node))
    return Group(gid, gtype, gwlist)

def getPassageRelation(relation):
    rid = relation.getAttribute("id")
    rtype = relation.getAttribute("type")
    rrefstoroles = {}
    ratbstovals = {}
    for node in relation.childNodes:
        if node.nodeType == node.ELEMENT_NODE:
            if node.hasAttribute("ref"):
                rrefstoroles[node.getAttribute("ref")] = node.tagName
            elif node.hasAttribute("valeur"):
                ratbstovals[node.tagName] = node.getAttribute("valeur")
    return Relation(rid, rtype, rrefstoroles, ratbstovals)

def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)

def escapeChars(s):
    return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("\'","&apos;")


## Demo

#def passage_demo():
    #d = readPassageXML("/Users/enrique/Projects/passage/CPCv3/bnc.xml")
    #d = readEasyXML("/Users/enrique/Projects/passage/CPCv3/lemonde2006_01_1.xml")
    #print d.strPassage().encode("utf-8")
    #print d.strEasy().encode("utf-8")

