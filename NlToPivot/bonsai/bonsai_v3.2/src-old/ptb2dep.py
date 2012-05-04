#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

# Marie Candito : 
# extract dependencies from a ptb-like treebank (or parser output)
# + metrics for comparison between Gold and Test, if Gold is given

from LabelledTree import *
from PennTreeBankReader import *
from Dep import *
from DependencyTree import *
from optparse import OptionParser
import sys

def compare_deplists(gold_deplist, test_deplist, opts={}):
#    missings = wrongs = goods = discarded = unkingold = 0
    # suppose that both deplists are sorted according to 
    #                           linearid of the dependent
    # (cf. tree2deplist output)

    # but still easier to have one list indexed...
    test_lid2dep = dict( [(x.dependent.lid, x) for x in test_deplist.deps ])

    comparison = init_comparison()

    for gold_dep in gold_deplist.deps:
        gold_lid = gold_dep.dependent.lid 

        if gold_dep.governor.is_unknown():
            pref = preflabel = '?'
            comparkey = 'unkingold'          # governor unknown in gold : comparison is impossible
        elif 'deletetags' in opts and opts['deletetags'] <> [] and gold_dep.dependent.tag() in opts['deletetags']:
            pref = preflabel = '/'
            comparkey = 'discarded'          # dependency to discard according to deletetags
        elif 'deletelabels' in opts and opts['deletelabels'] <> [] and gold_dep.name in opts['deletelabels']:
            pref = preflabel = '/'
            comparkey = 'discarded'          # dependency to discard according to deletelabels
        elif gold_lid in test_lid2dep:
             test_dep = test_lid2dep[gold_lid]
             (l,c) = compare_Dep(gold_dep, test_dep, opts)
             #print "compare_Dep:", gold_dep.tostring()
             #print "compare_Dep:", test_dep.tostring()
             #print "compare_Dep:", l, c
             # prefix for good / wrong label
             preflabel = '-'
             if l == 0:
                 preflabel = '='
             if  c == 0 and l == 0:
                  pref = '='
                  comparkey = 'goods'        # correct governor
             elif c == -1:
                  pref = '0'
                  comparkey = 'missings'     # unknown governor in test (<=> missing dep)
                  gold_dep.wronggov = test_dep.governor
                  gold_dep.wronglab = test_dep.name
             else:
                  if c == 0:
                      pref = '='
                  else:
                      pref = '-'
                  comparkey = 'wrongs'       # wrong governor or label in test
                  gold_dep.wronggov = test_dep.governor
                  gold_dep.wronglab = test_dep.name
        else:
             comparkey = 'missings'          # missing dep in test
             pref = preflabel = '0'
        comparopts = {'catgov':gold_dep.governor.tag(),
                      'catdep':gold_dep.dependent.tag(), #}
                      'depname':gold_dep.name}
        # gold dependency with prefix indicating whether wrong/missing/unknown etc...
        gold_dep.name = preflabel + pref + gold_dep.name
        comparison['trace'].append(gold_dep)

        store_dep_comparison(comparison, comparkey, comparopts)
    return comparison

# TODO : en faire une classe...
def init_comparison():
    return { 'any': {}, 'trace':[] }

# 'catgov' --> 'NC' --> 'wrongs' -->number of wrongs having gold governor category == NC  
# 'any'    --> 'wrongs' number of wrongs 
def store_dep_comparison(comparison, comparkey, comparopts):
    comparison['any'][comparkey] = comparison['any'].get(comparkey, 0) + 1
    for typecompar in comparopts: # 'catgov', 'catdep' ...
        type = comparopts[typecompar]
        if typecompar in comparison:
            if type in comparison[typecompar]:
                comparison[typecompar][type][comparkey] = comparison[typecompar][type].get(comparkey, 0) + 1
            else: # TODO
                comparison[typecompar][type] = { comparkey : 1 } 
        else:
            comparison[typecompar] = { type : { comparkey : 1 } }
        
# recall : 
# goods / (missings+wrongs+goods)
def compute_recall(eval):
     den = eval['goods'] + eval['missings'] + eval['wrongs'] + 0.0
     if den == 0: return 0
     return 100 * eval['goods'] / den

# precision :
# goods / (wrongs+goods)
def compute_prec(eval):
     den = eval['goods'] + eval['wrongs'] + 0.0
     if den == 0: return 0
     return 100 * eval['goods'] / den

def fmeas(recall, precision):
    if recall == 0 or precision == 0:
        return 0
    return 2.0 / ( (1/recall) + (1/precision))

def add_eval(eval, total, length, cutoff_length):
    cokey = 'len<='+str(cutoff_length)
    for x in ['All',cokey]:
        if x not in total:
            total[x] = {}    
    # nb of words : sum of all keys
    # (also set appropriate defaults)
    eval['nbword'] = eval.setdefault('missings',0) + eval.setdefault('wrongs',0) + eval.setdefault('goods',0) + eval.setdefault('discarded',0) + eval.setdefault('unkingold',0)
    for key in ['nbword', 'missings', 'goods', 'wrongs', 'unkingold']:
        total['All'][key] = total['All'].setdefault(key, 0) + eval[key]
        if length <= cutoff_length:
            total[cokey][key] = total[cokey].setdefault(key, 0) + eval[key]
        elif key not in total[cokey]:
            total[cokey][key] = 0
    
# 'catgov' --> 'NC' --> 'wrongs' -->number of wrongs having gold governor category == NC  
# 'any'    --> 'wrongs' number of wrongs 
def compute_measures(comparisons, cutoff_length, opts={}, trace=False):
     total = init_comparison()

     print '  Sent.                        #Dep.'
     print ' ID  Len. Recal  Prec. Goods gold test unkgold'
     print '=============================================='
     for i, compar in enumerate(comparisons):
         # length of sentences : sum of all keys
         length = 0
         for k in ['missings', 'wrongs', 'goods', 'discarded', 'unkingold']:
             length += compar['any'].setdefault(k,0)

         for typecompar in filter(lambda x: x<>'trace', compar):
             if typecompar == 'any':
                 add_eval(compar['any'], total['any'], length, cutoff_length)
             else:
                 if typecompar not in total:
                     total[typecompar] = {}
                 for type in compar[typecompar]:
                     if type not in total[typecompar]:
#                         print 'typecompar:'+typecompar+"\ttype:"+str(type)
                         total[typecompar][type] = {}
                     add_eval(compar[typecompar][type], total[typecompar][type], length, cutoff_length)
 
          # donnees phrase par phrase
         any = compar['any']
         print "%4d %3d %6.2f %6.2f  %3d  %3d  %3d %3d" % (i, any['nbword'],
                                                           compute_recall(any), 
                                                           compute_prec(any), 
                                                           any['goods'], any['goods']+any['wrongs']+any['missings'], 
                                                           any['goods']+any['wrongs'],
                                                           any['unkingold'])
         if trace:
             for dep in compar['trace']:
                 print dep.tostring()
     print '=============================================\n'

     print '============================================='
     print '=== Summary ==='

     for k in ['len<='+str(cutoff_length),'All']:
         print '\n-- '+k+' --'
         for typecompar in sorted(total, lambda x,y: cmp(y,x)):
             if typecompar == 'any':
                 subtot = total['any'][k]
                 if subtot['nbword'] == 0:
                     r = -1
                 else:
                     r = 100 * (subtot['nbword'] - subtot['unkingold'] + 0.0) / subtot['nbword']
                 rec = compute_recall(subtot)
                 prec = compute_prec(subtot)
                 print k+'\tany\tDependency extraction in gold file : Recall = %5.2f' % r
                 print k+'\tany\tRecall     = %5.2f' % rec
                 print k+'\tany\tPrecision  = %5.2f' % prec
                 print k+'\tany\tFMeasure   = %5.2f' % fmeas(rec,prec)
             elif typecompar <> 'trace':
                 for type in sorted(total[typecompar]):
                     subtot = total[typecompar][type][k]
                     rec = compute_recall(subtot)
                     prec = compute_prec(subtot)
                     print k+'\t'+str(typecompar)+'-'+str(type)+'\tRecall     = %5.2f' % rec
                     print k+'\t'+str(typecompar)+'-'+str(type)+'\tPrecision  = %5.2f' % prec
                     print k+'\t'+str(typecompar)+'-'+str(type)+'\tFMeasure   = %5.2f' % fmeas(rec,prec)
     
usage = """This program takes as input a treebank
           -- in ptb-like format => then program extracts dependencies
           -- in dependency format => then program loads dependencies
           If a gold treebank is given, 
           -- the program either extracts/or load dependencies from gold file
           -- and compares input treebank with gold treebank
           %prog [options] TEST_FILE
"""
parser=OptionParser(usage=usage)
parser.add_option("--testformat", dest="testformat", default='ptb', help='Format of TEST_FILE. Either "ptb" or "dep". If set to "dep", .piv files in directory are loaded. Default=ptb')
parser.add_option("--gold", dest="gold", default=None, help='Gold file')
parser.add_option("--goldformat", dest="goldformat", default='ptb', help='Format of gold file. Either "ptb" or "dep". If set to "dep", .piv files in directory are loaded. Default=ptb')
parser.add_option("--headrules", dest="headrules", default='p7', help='Head rules. Default=p7')
parser.add_option("--deletetags", dest="deletetags", default='', help=':-separated list of tags to ignore : dependencies where the dependent has such a tag in gold are ignored. Default=\'\'. Use --deletetags=\'PONCT\' to ignore ponctuation dependents.')
parser.add_option("--deletelabels", dest="deletelabels", default='ponct', help=':-separated list of labels to ignore : dependencies having such label in gold are ignored. Default=\'\'')

parser.add_option("--cutoff", dest="cutoff_length", default=40, help='At the end of evaluation, statistics for sentences of length <= cutoff are also shown. Default=40')
parser.add_option("--labeled", action="store_true", dest="labeled", default=False, help='Either compare labeled or unlabeled dependencies (default False => unlabeled)')
parser.add_option("--flpara", dest="flpara", default='/Users/mcandito/Documents/OUTILS/probparse/statgram/parser/params/ftb1pw', help='Path to functional labeller parameters')
parser.add_option("--trace",action="store_true",dest="trace",default=False,help="Triggers trace output of missing / wrong / good dependencies")
(opts,args) = parser.parse_args()
#ARGS
test_file = None
if (len(args) > 0):
     test_file = args[0]
else:
    raise NameError, 'Missing test file argument'
#OPTIONS
#print opts
gold_file = str(opts.gold) if opts.gold <> None else None
test_format = str(opts.testformat)
gold_format = str(opts.goldformat)
trace = bool(opts.trace)
labeled = bool(opts.labeled)
flpara = str(opts.flpara)
cutoff_length = int(opts.cutoff_length)
headrules = str(opts.headrules)
deletelabels = [x for x in str(opts.deletelabels).split(':') if x <> '']
deletetags = [x for x in str(opts.deletetags).split(':') if x <> '']


compare_opts = { 'deletetags':deletetags,#['PONCT'], # modifs djame was :[],#['PONCT']
                 'deletelabels':deletelabels,#['ponct'],#['head','ponct'], 
                 'labeled':labeled,
                 'tagged':False
                 }

trace = True
if gold_file: trace = False
gold = None

if headrules == 'p7':
    ptable = sym4_table()
else:
    ptable = gettable(headrules)


#print ptable.display_table()
    
# load dependencies for test_file
# either from ptb format (read + functional labelling + dependencies extraction)
# or from pivot format (load dependencies only)
testdgraphs = []
testdeplists = []
if test_format == 'ptb':
    testreader = PtbReader()
    test = testreader.read_mrg(open(test_file))
    # functional labelling pertains only if labeled dependencies asked
    if labeled:
        testdgraphs = parsedtrees2depgraphs(test, ptable, flmethod='svm', flpara=flpara)
    else:
        testdgraphs = parsedtrees2depgraphs(test, ptable, flmethod=None)
else:
    if os.path.isdir(test_file):
        depsentlist = parse_triples_dir(test_file,'piv')
    else:
        depsentlist = parse_triples_file(test_file)
    for depsent in depsentlist:
        testdgraphs.append( (depsent.id, depsent.depforest) )
i = 1
for (id, dgraph) in testdgraphs:
    deplist = DepList(id=id, dgraph=dgraph, trace=trace)
    i = i+1
    if trace:
        deplist.robustprint('withcat')
    testdeplists.append(deplist)

# load dependencies for gold_file
# either from ptb format (read + dependencies extraction) (no functional labelling)
# or from pivot format (load dependencies only)
if gold_file:
    golddgraphs = []
    golddeplists = []
    if gold_format == 'ptb':
        goldreader = PtbReader()
        gold = goldreader.read_mrg(open(gold_file))
        # for gold files : no functional labelling
        golddgraphs = parsedtrees2depgraphs(gold, ptable, flmethod=None)
    else:
        if os.path.isdir(gold_file):
            depsentlist = parse_triples_dir(gold_file,'piv')
        else:
            depsentlist = parse_triples_file(gold_file)
        for depsent in depsentlist:
            # a debugger ! le parse_triples se plante si test_file est en format ptb, incomprehensible
            golddgraphs.append( (depsent.id, depsent.depforest))
    for (id, dgraph) in golddgraphs:
        deplist = DepList(id=id, dgraph=dgraph, trace=trace)
        if trace:
            deplist.robustprint('withcat')
        golddeplists.append(deplist)
        if len(testdeplists) == len(golddeplists):
            break

    # comparisons for each sentence, between test_dependencies and gold_dependencies    
    comparisons = []
    for i, gdeplist in enumerate(golddeplists):
        try:
            tdeplist = testdeplists[i]
        except IndexError:
            raise IndexError, 'Dependencies comparison : different sentence numbers for gold and test files!'
        comparisons.append(compare_deplists(gdeplist, tdeplist, compare_opts))
    print_deplists(golddeplists, 'withcat')

    # metrics for all sentences, and sentences of length < cutoff_length
    compute_measures(comparisons, cutoff_length, trace)

    print '\n=== Options ==='
    for opt in compare_opts:
         print opt+'('+str(compare_opts[opt])+')\t'



#print_deplists(testdeplists, 'withcat')

# todo : en perfect tagging pour avoir des stats par cat signifiantes
        
# TODO : faire arbre de dep, ou bien au moins une classe deplist
# TODO : gestion encodage !!!        
