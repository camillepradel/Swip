#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

# Enrique Henestroza Anguiano : 

from depparser import *
from DepGraph import *
from PassageDoc import *
from optparse import OptionParser
import re
import copy

usage = """This program reads from STDIN a dependency treebank
           in CONLL format and outputs the treebank in PASSAGE format
"""

parser = OptionParser(usage=usage)
parser.add_option("--out-format", dest="out_format", default="passage", help='Valid options are passage (default), pivot, both, or demo')
parser.add_option("--out-name", dest="out_name", default="document", help='Desired document tag name for Passage XML output file')
parser.add_option("--token-file", dest="token_file", default="", help='.tcs tokenized file to compare agains CONLL format file')

(opts, args) = parser.parse_args()
out_format = str(opts.out_format)
out_name = str(opts.out_name)
token_file = str(opts.token_file)

# Simple POS tag sets
nouns = set(['CLO', 'CLR', 'CLS', 'NC', 'NPP', 'PRO', 'PROREL', 'PROWH', 'ET', 'I'])
verbs = set(['V', 'VIMP', 'VINF', 'VPP', 'VPR', 'VS'])
advbs = set(['ADV', 'ADVWH'])
adjvs = set(['ADJ', 'ADJWH'])
preps = set(['P', 'P+D', 'P+PRO'])
detrs = set(['DET', 'DETWH'])

# Dictionary mapping PASSAGE relation names to a 'governor'-first list of roles
rtype_to_roles = { 'SUJ-V' : ['verbe', 'sujet'], 'AUX-V' : ['verbe', 'auxiliaire'], 'COD-V' : ['verbe', 'cod'], 'CPL-V' : ['verbe', 'complement'], 'ATB-SO' : ['verbe', 'attribut'], 'MOD-V' : ['verbe', 'modifieur'], 'MOD-N' : ['nom', 'modifieur'], 'MOD-A' : ['adjectif', 'modifieur'], 'MOD-R' : ['adverbe', 'modifieur'], 'MOD-P' : ['preposition', 'modifieur'], 'COMP' : ['verbe', 'complementeur'], 'APPOS' : ['premier', 'appose'], 'JUXT' : ['premier', 'suivant'], 'COORD-G' : ['coordonnant', 'coord-g', 'coord-d'], 'COORD-D' : ['coordonnant', 'coord-d'], 'COORD-DU' : ['coordonnant', 'coord-d'], 'COORD-GU' : ['coordonnant', 'coord-g'], 'COMPR' : ['complementeur', 'nom']}

# Set of PASSAGE relation types. Used for membership queries.
rtype_set = set(rtype_to_roles.keys())

# Global counts for tokens, relations, and sentences for id management.
sidcount = 0
tidcount = 0
ridcount = 0

###############################################################################

#
# order_ids
#
# Input: A list of Sentence objects
# Output: none
#
# Function: Renames the Relation and Token ids within the Sentence objects
# in order to create <0, ..., n> style orderings across the entire document.
#
#def order_ids(slist):

#    tc = 0 # counter for token ids
#    rc = 0 # counter for relation ids

#    for sen in slist:
#        for tok in sen.tlist:
#            tok.id = "t"+str(tc)
#            tc += 1
#        for rel in sen.rlist:
#            rel.id = "r"+str(rc)
#            rc += 1

#
# depgraph_add_missing_tokens
#
# Input : A DepGraph object, a string of tokens
# Output : none
#
# Function: Whenever tokens are missing from the DepGraph, they are added in.
def depgraph_add_missing_tokens(dg, tokens):
    tokenlist = tokens.split(' ')

    if len(dg.lexnodes.keys()) < 1:
        dg.add_lexnode(LexicalNode('null', 0))
        for i in range(len(tokenlist)):
            newlex = LexicalNode(tokenlist[i].rstrip(), i+1)
            dg.add_lexnode(newlex, gov_lidx=0, label="root")

    elif len(dg.lexnodes.keys()) != len(tokenlist):
        print "Incorrect number of tokens!"

#
# depgraph_passage_mod
#
# Input: A DepGraph object
# Output: none
#
# Function: Traverses the DepGraph and inserts additional relations relevant
# to the PASSAGE format, all while remaining within the DepGraph formalism.
#
def depgraph_passage_mod(dg):
    transcategorization(dg)
    control_raising(dg)
    juxtaposition(dg)
    apposition(dg)
    verbal_objects(dg)
    coordination(dg)
    punctuation(dg)
    unamb_affixes(dg)
    determiners(dg)
    attributes(dg)
    auxiliaries(dg)
    prep_raising(dg)
    neg_adverbs(dg)
    complementizers(dg)
    amb_affixes(dg)
    mod_blanket(dg)

#
# depgraph_to_passage_sen
#
# Input: A DepGraph object, and a sentence id
# Output: A PassageDoc object
#
# Function:  Straightforward conversion of the DepGraph directly into PASSAGE
# Sentence format.
#
def depgraph_to_passage_sen(dg):
    global tidcount, ridcount, sidcount

    lexlist = map(dg.get_lexnode, sorted(dg.lexnodes.keys()))

    deplist = dg.sorted_dependencies()
    tid_to_tokens = {}
    tid_to_words = {}
    reflist = []

    for lex in lexlist:
        if lex.lidx < 0:
            continue

        if not lex.get_feature_value('lemma'):
            lex.set_feature('lemma',lex.form)
        tokenid = "t" + str(tidcount)
        wordid = "w" + str(tidcount)
        tidcount += 1

        tid_to_tokens[lex.lidx] = Token(tokenid, "0", "0", lex.form)
        tid_to_words[lex.lidx] = Word(wordid, [tokenid], lex.cat, lex.get_feature_value('lemma'), lex.form, "false")

    for dep in deplist:
        lab = dep.label

        if dep.governor.lidx < 0 or lab == "NONE":
            continue

        gword = tid_to_words[dep.governor.lidx]
        dword = tid_to_words[dep.dependent.lidx]
        reftorole = {}
        atbtoval = {}

        rlist = []
        if lab in rtype_set and lab != "COORD-D":
            rlist = rtype_to_roles[lab]
        elif lab == "NONE":
            continue
        else:
            continue
#           rlist = ["gov", "dpt"]

        # Handle reversed COMP
        if lab == 'COMPR':
            lab = 'COMP'

        # Merge coordination dependencies
        if dep.label == "COORD-G":
            for dep2 in dg.governor2deps[dep.governor.lidx]:
                if dep2.label == "COORD-D":
                    cc = gword.id
                    cg = dword.id
                    cd = tid_to_words[dep2.dependent.lidx].id
                    lab = 'COORD'
                    reftorole = {cc : rlist[0], cg : rlist[1], cd : rlist[2]}
                    break
            else:
                lab = 'COORD'
                reftorole = {gword.id : rlist[0], dword.id : rlist[1]}
        elif dep.label == "COORD-DU" or dep.label == "COORD-GU":
            lab = 'COORD'
            reftorole = {gword.id : rlist[0], dword.id : rlist[1]}

        # Handle subject/object attribute values
        elif dep.label == "ATB-SO":
            reftorole = {gword.id : rlist[0], dword.id : rlist[1]}
            atbtoval = {"s-o" : dep.atb}
        else:
            reftorole = {gword.id : rlist[0], dword.id : rlist[1]}

        reflist.append(Relation("r"+str(ridcount), lab, reftorole, atbtoval))
        ridcount += 1

    s = Sentence("s"+str(sidcount), tid_to_tokens.values(), tid_to_words.values(), reflist)
    sidcount += 1
    return s

###############################################################################

#
# Suite of functions to implement transformations outlined by Francois Guerin. 
# All take as input a DepGraph in Pivot format, and modify it (no output). Some
# functionality differs from the original transformation description.
#

# An adjective modified by a determiner is transformed into a noun.
def transcategorization(dg):
    for lexid in dg.lexnodes:
        lex = dg.lexnodes[lexid]
        
        if lex.cat in detrs and lexid in dg.dependent2deps:
            for dep in dg.dependent2deps[lexid]:
                if dep.governor.cat in adjvs:
                    dep.governor.cat = 'NC'
                    break
            else:
                for dep in dg.dependent2deps[lexid]:
                    if dep.governor.cat in nouns:
                        break
                else:
                    lex.cat = 'NC'

# Subject control raising for lower-level verbs.
def control_raising(dg):
    newdeps = []
#   rps = set(["en", "pour"])

    for lexid in dg.lexnodes:
        lex = dg.lexnodes[lexid]

        # Check if this node has a subject. If not then continue.
        suj = ""
        if lex.lidx in dg.governor2deps:
            for dep in dg.governor2deps[lex.lidx]:
                if dep.label == 'suj':
                    suj = dep.dependent
                    break
        if suj == "":
            continue

        # Go through dependents of the node and assign the subject to any
        # lower level verbs.
        for dep in dg.governor2deps[lex.lidx]:
            dpt = dep.dependent
            if dpt.cat in preps:
                if dpt.lidx in dg.governor2deps:
                    for dep2 in dg.governor2deps[dpt.lidx]:
                        if dep2.label == 'obj' and dep2.dependent.cat in verbs:
                            sverb = dep2.dependent
                            newdeps.append(Dep(sverb, suj, "suj"))
            elif dpt.cat in verbs and dep.label == 'obj':
                sverb = dpt
                newdeps.append(Dep(sverb, suj, "suj"))

    for dep in newdeps:
        dg.add_dep(dep)

# Finding and marking juxtapositions.
def juxtaposition(dg):
    newdeps = []
    juxtlabs = set(["mod", "dep", "unk", "arg"])

    for dep in dg.deps:
        gov = dep.governor
        dpt = dep.dependent

        # If the dep is potentially a juxt and both gov and dpt are verbs, then
        # label the dep as a juxt if there are any ponct marks in between.
        if dep.label in juxtlabs and dpt.cat in verbs and gov.cat in verbs:
            idmin = min([gov.lidx, dpt.lidx])
            idmax = max([gov.lidx, dpt.lidx])
            for i in range(idmin + 1, idmax):
                if dg.lexnodes[i].cat == "ponct":
                    jfirst = dg.lexnodes[idmin]
                    jsecond = dg.lexnodes[idmax]
                    newdeps.append(Dep(jfirst, jsecond, 'JUXT'))
                    break

    for dep in newdeps:
        dg.add_dep(dep)

# Find and marking appositions.
def apposition(dg):
    remdeps = []
    newdeps = []
    applabs = set(["mod", "dep", "unk", "arg"])
    appdivs = set (["(", "<D>", "<C>", "\,", "\:", "-"])

    for dep in dg.deps:
        gov = dep.governor
        dpt = dep.dependent

        # If the dep is potentially an appos and both gov and dpt are nouns,
        # then label the dep as an appos if there is one divider in between.
        if dep.label in applabs and dpt.cat in nouns and gov.cat in nouns:
            idmin = min([gov.lidx, dpt.lidx])
            idmax = max([gov.lidx, dpt.lidx])
            divcount = 0
            for i in range(idmin + 1, idmax):
                if dg.lexnodes[i].form in appdivs:
                    divcount += 1
            if divcount == 1:
                afirst = dg.lexnodes[idmin]
                asecond = dg.lexnodes[idmax]
                newdeps.append(Dep(afirst, asecond, 'APPOS'))
                remdeps.append(dep)

    for dep in remdeps:
        remove_dep(dg, dep)
    for dep in newdeps:
        dg.add_dep(dep)

# Adjust dependencies for verbal objects.
def verbal_objects(dg):
    cpllabs = set(["a_obj", "de_obj", "p_obj"])
    weirdlabs = set(["arg", "none", "unk"])
    compsim = set(["où", "comment", "pourquoi", "combien"])

    for dep in dg.deps:
        gov = dep.governor
        dpt = dep.dependent
        if gov.cat not in verbs and gov.cat != 'CC' and gov.cat != 'ponct':
            continue
        elif dep.label == 'suj':
            dep.label = 'SUJ-V'
        elif dep.label == 'obj':
            dep.label = 'COD-V'
        elif dep.label in cpllabs:
            dep.label = 'CPL-V'
        elif dep.label == 'mod':
            dep.label = 'MOD-V'
        elif dep.label in weirdlabs:
            if dpt.cat in preps or dpt.cat in compsim:
                dep.label = 'CPL-V'
#           elif dpt.cat in advbs:
#               dep.label = 'CPL-V'
            else:
                dep.label = 'MOD-V'

# Account for coordination.
def coordination(dg):
    newdeps = []
    remdeps = []
    coord_govs = {}

    for dep in dg.deps:
        if dep.label == 'coord' and dep.governor.lidx not in coord_govs:
            coord_govs[dep.governor.lidx] = 1
            coorddeps = []
            for dep2 in dg.governor2deps[dep.governor.lidx]:
                if dep2.label == 'coord':
                    coorddeps.append(dep2)

            firstitem = dep.governor
            lastconj = dep.dependent
            lastitem = dep.governor

            # Visit each subsequent coordinated item and set its left coord to
            # the previous item.
            for dep2 in coorddeps:
                if dep2.dependent.lidx in dg.governor2deps:
                    for dep3 in dg.governor2deps[dep2.dependent.lidx]:
                        gov3 = dep3.governor
                        if dep3.label == 'dep_coord' or dep3.label == 'comp':
                            dep3.label = 'COORD-D'
                            newdeps.append(Dep(gov3, lastitem, 'COORD-G'))
                            lastitem = dep3.dependent
                            lastconj = dep3.governor
                            break

                    # Otherwise just take the first dependent
                    else:
                        dep3 = dg.governor2deps[dep2.dependent.lidx][0]
                        dep3.label = 'COORD-D'
                        newdeps.append(Dep(gov3, lastitem, 'COORD-G'))
                        lastitem = dep3.dependent
                        lastconj = dep3.governor
 
                remdeps.append(dep2)

            # Checks for any relations involving the first coordinated item
            # since it's where DepGraph puts shared outside relations, and 
            # moves them to the last coordinating conjunction.
            if firstitem.lidx in dg.dependent2deps:
                for dep2 in dg.dependent2deps[firstitem.lidx]:
                    newdep = copy.copy(dep2)
                    newdep.dependent = lastconj
                    newdeps.append(newdep)
                    remdeps.append(dep2)

                    # Handle odd case of coordinated NPs introduced by same P
                    if dep2.governor.cat in preps:
                        newdeps.append(Dep(dep2.governor, lastitem, 'comp'))

    for dep in remdeps:
        remove_dep(dg, dep)
    for dep in newdeps:
        dg.add_dep(dep)

    # Handle any dangling dep_coord relations.
    for dep in dg.deps:
        if dep.label == 'dep_coord':
            if dep.governor.lidx > dep.dependent.lidx:
                dep.label = 'COORD-GU'
            else:
                dep.label = 'COORD-DU'

# Remove relations related to punctuation.
def punctuation(dg):
    hidedeps = []

    for dep in dg.deps:
        if dep.label == 'ponct':
            hidedeps.append(dep)
    for dep in hidedeps:
        hide_dep(dg, dep)

# Deal with unambiguous affixes.
def unamb_affixes(dg):
    hidedeps = []
    affsup = set(["se", "s\'"])
    affsuj = set(["je", "tu", "il", "elle", "ils", "elles", "on", "-je", "-tu", "-il", "-t-il", "-elle", "-t-elle", "-ils", "-t-ils", "-elles", "-t-elles", "-on", "-t-on"])
    affcod = (["le", "la", "les", "l\'", "-le", "-la", "-les"])
    affcpl = (["lui", "y", "en", "-lui", "-y", "-en"])

    for dep in dg.deps:
        if dep.label == 'aff':
            if dep.dependent.form in affsup:
                hidedeps.append(dep)
            elif dep.dependent.form in affsuj:
                dep.label = "SUJ-V"
            elif dep.dependent.form in affcod:
                dep.label = "COD-V"
            elif dep.dependent.form in affcpl:
                dep.label = "CPL-V"

    for dep in hidedeps:
       hide_dep(dg, dep)

# Deal with determiners.
def determiners(dg):
    hidedeps = []
    detsup = set(["le", "la", "les", "l\'", "un", "une", "des", "de", "d\'", "son", "sa", "ses", "mon", "ma", "mes", "ton", "ta", "tes", "notre", "nos", "votre", "vos", "leur", "ce", "cette", "ces"])

    for dep in dg.deps:
        if dep.label == 'det':
            if dep.dependent.form in detsup:
                hidedeps.append(dep)
            else:
                dep.label = "MOD-N"

    for dep in hidedeps:
        hide_dep(dg, dep)

# Deal with attributes. Note: Not described by Francois, but trivial.
def attributes(dg):
    for dep in dg.deps:
        if dep.label == 'ats':
            dep.label = 'ATB-SO'
            dep.atb = 'sujet'
        elif dep.label == 'ato':
            dep.label = 'ATB-SO'
            dep.atb = 'objet'

# Deal with auxiliaries by chaining dependencies left-to-right.
def auxiliaries(dg):
    newdeps = []
    remdeps = []
    auxlabs = set(["aux_tps", "aux_caus", "aux_pass"])

    for lexid in dg.governor2deps:
        if lexid < 0:
            continue

        lex = dg.lexnodes[lexid]
        sujdep = 0
        auxdeps = {}

        # Only care about verbs that have one or more auxiliaries.
        if lex.cat not in verbs:
            continue
        for vdep in dg.governor2deps[lexid]:
            if vdep.label == "SUJ-V":
                sujdep = vdep
            elif vdep.label in auxlabs:
                auxdeps[vdep.dependent.lidx] = vdep
        if len(auxdeps) == 0:
            continue

        # Chain dependencies from left to right, so the subject depends on the
        # first auxiliary which depends on the next auxiliary, etc. The last
        # auxiliary depends on the verb.
        verbedroigt = lex
        sortedauxids = auxdeps.keys()
        sortedauxids.sort()
        sortedauxids.reverse()
        for auxid in sortedauxids:
            auxdep = auxdeps[auxid]
            auxdep.label = 'AUX-V'
            newdep = copy.copy(auxdep)
            newdep.governor = verbedroigt
            newdeps.append(newdep)
            remdeps.append(auxdep)
            verbedroigt = auxdep.dependent
        if (sujdep != 0):
            newdep = copy.copy(sujdep)
            newdep.governor = verbedroigt
            newdeps.append(newdep)
            remdeps.append(sujdep)

    for dep in remdeps:
        remove_dep(dg, dep)
    for dep in newdeps:
        dg.add_dep(dep)

# Deal with preposition raising. TODO: account for multiple level raising.
def prep_raising(dg):
    newdeps = []
    hidedeps = []

    for dep in dg.deps:
        gov = dep.governor
        dpt = dep.dependent

        # Raise the object (complement) of the preposition so that it becomes
        # the dependent of any relations the preposition was a dependent in.
        if dpt.cat in preps and dpt.lidx in dg.governor2deps:
            for dep2 in dg.governor2deps[dpt.lidx]:
                if dep2.label == 'obj':
                    newdep = copy.copy(dep)
                    newdep.dependent = dep2.dependent
                    newdeps.append(newdep)
                    hidedeps.append(dep)
                    hidedeps.append(dep2)

    for dep in hidedeps:
        hide_dep(dg, dep)
    for dep in newdeps:
        dg.add_dep(dep)

# Deal with negation adverbs.
def neg_adverbs(dg):
    hidedeps = []
    negsup = set(["ne", "ni", "n\'"])

    # Hide all negation adverbs relations.
    for dep in dg.deps:
        if dep.label == 'MOD-V' and dep.dependent.form in negsup:
            hidedeps.append(dep)

    for dep in hidedeps:
        hide_dep(dg, dep)

# Deal with complementizers. Note: Different from Francois, check specifically
# for comparatives and do a heuristic to find some adverb governing the
# complementizer so that the dependent of the complementizer becomes MOD-R
# of the adverb.
def complementizers(dg):
    newdeps = []
    remdeps = []
    cmpr = set(["plus", "moins", "davantage", "aussi", "si", "autant", "tant"])

    for dep in dg.deps:
        gov = dep.governor
        dpt = dep.dependent
        lab = dep.label

        # Check that the dependent is a complementizer and has dependents.
        if dpt.cat == 'CS' and dpt.lidx in dg.governor2deps:
            for dep2 in dg.governor2deps[dpt.lidx]:

                if dep2.label == 'obj':

                    # Switch the direction of the complementizer relation.
                    dep2.label = 'COMP'
                    newdep = copy.copy(dep2)
                    newdep.dependent = dep2.governor
                    newdep.governor = dep2.dependent
                    newdeps.append(newdep)
                    remdeps.append(dep2)

                    # We're dealing with a potential comparative as long as the
                    # the governor is an adverb, adjective, or noun.
                    gcat = gov.cat
                    if dep.label == "comp" and dep.governor.cat not in verbs:
                        if gov.form in cmpr:
                            dep.label ='MOD-R'
                            newdep = copy.copy(dep)
                            newdep.dependent = dep2.dependent
                            newdeps.append(newdep)
                            remdeps.append(dep)
                        else:
                            for dep3 in dg.governor2deps[gov.lidx]:
                                if dep3.dependent.form in cmpr:
                                    dep.label = 'MOD-R'
                                    newdep = copy.copy(dep)
                                    newdep.dependent = dep3.dependent
                                    newdeps.append(newdep)
                                    remdeps.append(dep)
                                    break

                    # By default, do raising on the obj of the complementizer.
                    else:
                        newdep = copy.copy(dep)
                        newdep.dependent = dep2.dependent
                        newdeps.append(newdep)
                        remdeps.append(dep)

    # Check for any remaining or dangling comparatives.
    for dep in dg.deps:
        if dep.label == 'comp':
            dep.label = 'COMP'
            if dep.governor.cat == "CS" or dep.governor.cat in preps:
                dep.label = 'COMPR'
#                newdep = copy.copy(dep)
#                newdep.dependent = dep.governor
#                newdep.governor = dep.dependent
#                newdeps.append(newdep)
#                remdeps.append(dep)
                
    for dep in remdeps:
        remove_dep(dg, dep)
    for dep in newdeps:
        dg.add_dep(dep)

# Deal with ambiguous affixes.
def amb_affixes(dg):
    affsujcodcoi = set(["nous", "vous", "-nous", "-vous"])
    affcodcoi = set(["me", "m\'", "te", "t\'", "moi", "toi", "-moi", "-toi"])
    
    for dep in dg.deps:
        gov = dep.governor
        dpt = dep.dependent
        if dep.label == 'aff':
            if dpt.form not in (affsujcodcoi | affcodcoi):
                hide_dep(dg, dep)
            hassuj = 0
            hascod = 0
            for dep2 in dg.governor2deps[gov.lidx]:
                if dep2.label == 'SUJ-V':
                    hassuj = 1
                elif dep2.label == 'COD-V':
                    hascod = 1
            if dpt.form in affsujcodcoi:
                if hassuj == 1 and hascod == 1:
                    dep.label = 'CPL-V'
                elif hassuj == 1 and hascod == 0:
                    dep.label = 'COD-V'
                else:
                    dep.label = 'SUJ-V'
            elif dpt.form in affcodcoi:
                if hascod == 1:
                    dep.label = 'CPL-V'
                else:
                    dep.label = 'COD-V'

# Translation of other ambiguous or unexplained relations into 'mod' relations.
def mod_blanket(dg):
    remdeps = []
    newdeps = []

    weirdlabs = set(["mod", "mod_rel", "arg", "none", "unk", "dep"])
    normalcats = nouns | verbs | advbs | adjvs | preps
    for dep in dg.deps:
        lab = dep.label
        cat = dep.governor.cat

        if lab in rtype_set or lab == "NONE" or dep.governor.lidx == '0':
            continue

        # Do progressively rougher heuristics to ensure that any remaining
        # unconverted dependencies get assigned somewhere.

        if cat == "CC":
            for dep2 in dg.governor2deps[dep.governor.lidx]:
                if dep2.label == 'COORD-G' or dep2.label == 'COORD-D':
                    cat = dep2.dependent.cat
                    break

        if cat not in normalcats and dep.governor.lidx in dg.dependent2deps:
            for dep2 in dg.dependent2deps[dep.governor.lidx]:
                gov2 = dep2.governor
                if gov2.cat in normalcats:
                    #newdep = copy.copy(dep)
                    #newdep.governor = gov2
                    #newdeps.append(newdep)
                    #remdeps.append(dep)
                    cat = gov2.cat
                    break

        if cat not in normalcats:
            for dep2 in dg.governor2deps[dep.governor.lidx]:
                dpt2 = dep2.dependent
                if dep.dependent != dpt2 and dpt2.cat in normalcats:
                    #newdep = copy.copy(dep)
                    #newdep.governor = dpt2
                    #newdeps.append(newdep)
                    #remdeps.append(dep)
                    cat = dpt2.cat
                    break

        if cat in nouns:
            dep.label = 'MOD-N'
        elif cat in verbs:
            dep.label = 'MOD-V'
        elif cat in advbs:
            dep.label = 'MOD-R'
        elif cat in adjvs:
            dep.label = 'MOD-A'
        elif cat in preps:
            dep.label = 'MOD-P'

    for dep in remdeps:
        remove_dep(dg, dep)
    for dep in newdeps:
        dg.add_dep(dep)


#
# Helper functions for the transformation process
#

# Hides a relation within a DepGraph
def hide_dep(dg, dep):
    dep.label = "NONE"
    return

# Deletes a relation completely from a DepGraph
def remove_dep(dg, dep):
    if dg.deps.count(dep) < 1:
        return

    dptdeplist = dg.dependent2deps[dep.dependent.lidx]
    govdeplist = dg.governor2deps[dep.governor.lidx]
    dptdeplist.remove(dep)
    govdeplist.remove(dep)
    if len(dptdeplist) == 0:
        del dg.dependent2deps[dep.dependent.lidx]
    else:
        dg.dependent2deps[dep.dependent.lidx] = dptdeplist
    if len(govdeplist) == 0:
        del dg.governor2deps[dep.governor.lidx]
    else:
        dg.governor2deps[dep.governor.lidx] = govdeplist
    dg.deps.remove(dep)

###############################################################################

#
# Main script section: Reads CONLL formatted dependency data from standard
# input, then modifies the DepGraph structure to reflect PASSAGE-style
# relations, then converts the DepGraph object into PassageDoc representation.
#

if out_format == "demo":
    passage_demo()
else:
    sentencelist = []
    pivotout = ""
    tokf = ""
    tokenslist = []
    if token_file != "":
        tokf = open(token_file, 'r')
        tokenslist = tokf.readlines()

    for depparse in read_depparses_conll_stream(sys.stdin):
        
        if token_file != "":
            depgraph_add_missing_tokens(depparse.depgraph, tokenslist[sidcount])

        depgraph_passage_mod(depparse.depgraph)
        sentencelist.append(depgraph_to_passage_sen(depparse.depgraph))
        pivotout += 'sentence_form('+depparse.depgraph.get_yield_str(normalize=True)+')\nsurf_deps(\n'+'\n'.join([dep.to_string_pivot() for dep in depparse.depgraph.sorted_dependencies()])+'\n)\n\n'

    if token_file != "":
        tokf.close()

    relstring = re.sub("none.*\n|root.*\n", '', pivotout)
    #order_ids(sentencelist)
    pdoc = PassageDoc(out_name, sentencelist)

    if out_format == "both":
        f1 = open(out_name + '.passpiv', 'w')
        f1.write(relstring)
        f1.close()
        f2 = open(out_name + '.xml', 'w')
        f2.write(pdoc.strPassage())
        f2.close()
        
    elif out_format == "pivot":
        print relstring

    elif out_format == "passage":
        print pdoc.strPassage()

###############################################################################
