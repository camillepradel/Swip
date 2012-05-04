#!/usr/bin/env python

from PennTreeBankReader import *
from optparse import OptionParser
import sys

#Turns a tree into a list of tuples
def tree2tuples(tree):
    (tuples,i,j) = tree2tuplesRec(tree,0)
    return tuples

def tree2tuplesRec(tree,min):
    lidx = min
    tuples = []
    if not (len(tree.children) == 1 and tree.children[0].is_leaf()):     
        for child in tree.children:
            (ctuples,i,j) = tree2tuplesRec(child,lidx)
            lidx = j
            tuples = tuples + ctuples
        if tree.label <> '':
            tuples.append((tree.label,min,lidx))
    else :
        lidx = lidx + 1
        #tuples.append((tree.label,min,lidx))  ==> do not take preterminals and terminals into account in the diff op
    return (tuples,min,lidx)    

#diff the tuples (tlist 1 is the test, tlist2 is the reference)
def diff_tuples(tlist1,tlist2):
    eqlist = []
    t1diff = []
    for t in tlist1:
        if t in tlist2:
            eqlist.append(t)
        else:
            t1diff.append(t)
    return (eqlist,t1diff)

#These are raw eval scores (!= evalb scores)
def precision(correctfound,found):
    if found == None or len(found) == 0:
        return 0
    else:
        return float(len(correctfound))/float(len(found))

def recall(correctfound,correct):
    if correct== None or len(correct) == 0:
        return 0
    else:
        return float(len(correctfound))/float(len(correct))

def f1_score(precision,recall):
    if precision == 0 and recall == 0:
        return 0
    else:
        return 2*precision*recall/(precision+recall)

#try to recode this stuff cleanly...
def diff_treebanks(treebank_test,treebank_gold,verbose=True):
    if (len(treebank_test) <> len(treebank_gold)):
        sys.stderr.write("ERROR :: treebanks have different lengths, diff aborted...")
        sys.exit(1)
    else:
        pdic = {}
        ptotdic = {}
        rdic = {}
        rtotdic = {}
        ptot = 0
        rtot = 0
        ftot = 0
        for i in range(len(treebank_test)):
            if treebank_test[i].label <> 'ROOT_FAILURE' and treebank_test[i].label <> 'ROOT_FAILURE':
                tuples_test = tree2tuples(treebank_test[i])
                tuples_ref = tree2tuples(treebank_gold[i])
                (eq,tdiff) = diff_tuples(tuples_test,tuples_ref)
                (eq,rdiff) = diff_tuples(tuples_ref,tuples_test)
                fill_dicos(pdic,ptotdic,eq,tuples_test)
                fill_dicos(rdic,rtotdic,eq,tuples_ref)
                pval = precision(eq,tuples_test)
                rval = recall(eq,tuples_ref)
                fval = f1_score(pval,rval)
                ptot = ptot + pval
                rtot = rtot + rval
                ftot = ftot + fval
                print "Precision => "+str(pval)+'\tRecall => '+str(rval)+'\tF-Score => '+str(fval)
                if (verbose):
                    tree = diff_annotate_tree(treebank_test[i],tdiff)
                    print tree.pprint()
                    # tree = diff_annotate_tree(treebank_gold[i],rdiff)
                print "---------------------------------------------------------------------------"   
        print "Summary:"
        print "-----------"
        print "Precision => "+str(ptot/float(len(treebank_test)))+'\tRecall => '+str(rtot/float(len(treebank_test)))+'\tF-Score => '+ str(ftot/float(len(treebank_test)))+'\n'
        print "\n\nPrecision (Details)"
        print "---------------------"
        for key in ptotdic.keys():
            val = float(pdic[key])/float(ptotdic[key])
            print "  "+ key +" ==> "+str(val)
        print "\nRecall (Details)"
        print "---------------------"
        for key in rtotdic.keys():
            val = float(rdic[key])/float(rtotdic[key])
            print "  "+ key +" ==> "+str(val)
        print "\nF-Measure (Details)"
        print "---------------------"
        for key in set(ptotdic.keys()).union(set(rtotdic.keys())):
            labs.append(key)
            if(not ptotdic.has_key(key)):
                pval = 0
            else:
                pval = float(pdic[key])/float(ptotdic[key])
            if(not rtotdic.has_key(key)):
                rval = 0
            else:
                rval =  float(rdic[key])/float(rtotdic[key])
            if pval == 0 and rval == 0:
                print "  "+ key +" ==> 0"         
            else:
                print "  "+ key +" ==> "+str(f1_score(pval,rval))


#Generates an R script for displaying an F-Score like graph
def r_fscore(vals,labels,title="F-Score by category", xtitle="Category",ytitle="F-Score",rdev="quartz"):
    lgth = len(vals)
    rstr = rdev+"()\n"
    rstr += "x <- c("  + ",".join(vals) + ")\n"
    rstr += 'y <- c("'  + '","'.join(labels)+'")\n'
    rstr += 'm <- cbind(x,y)\n'
    rstr += 'm <- m[order(m[,2]),]\n'
    rstr += 'plot(m[,1],type="h",xlab=" '+xtitle+' ",ylab="'+ytitle+'",col=rainbow('+str(lgth)+'),lwd=15,ylim = c(0,100), lend="square",main="'+title+'",xaxt="n")\n'
    rstr += 'axis(1,1:'+str(lgth)+',m[,2])\n'
    rstr += 'sleep(10000)\n'
    return rstr



def fill_dicos(dico,dico_tot,eq,tuples):
    for elt in tuples:
        (cat,i,j) = elt
        add_count(dico_tot,cat)
        if elt in eq:
            add_count(dico,cat)
        else:
            init_count(dico,cat)

def add_count(maptable,att):
    if maptable.has_key(att):
        maptable[att] = maptable[att] + 1
    else:
        maptable[att] = 1

def init_count(maptable,att):
    if not maptable.has_key(att):
        maptable[att] = 0


def diff_annotate_tree(tree,tupdiff):
    (i,j) = annotate_Rec(tree,tupdiff,0)
    return tree

def annotate_Rec(tree,tuples,min):
    lidx = min
    if not tree.is_leaf():     
        for child in tree.children:
            (i,j) = annotate_Rec(child,tuples,lidx)
            lidx = j
    else :
        lidx = lidx + 1
    if ((tree.label,min,lidx) in tuples and not tree.is_leaf() and tree.label <> ''):
        tree.label = '*'+tree.label+'*'
    return (min,lidx)        

# def diff_print_rec(tree):
#      indent = ' '
#      str = self.print_node()
#      if (not self.is_leaf()):
#          for child in self.children:
#              str = str + indent + child.printr(indent+'  ')+'\n'
#          str = str[:-1]+')'
#      else:
#          str = str + self.print_node()+'\n'
#      retu
#     rn str

usage = """This is a tool for analysing differences on two treebank
           %prog [options] FILE_TEST FILE_GOLD

           where FILE_TEST is a penn treebank encoded corpus to be compared with a corpus of exactly the same size being the reference (or gold) corpus. The tool outputs a detailed report on the differences observed between the 2 corpora

Comments and improvements are welcome, please send them to bcrabbe@linguist.jussieu.fr"""
parser=OptionParser(usage=usage)
(opts,args) = parser.parse_args()

treader = PtbReader()
rreader = PtbReader()
test = treader.read_mrg(open(args[0]))
gold = rreader.read_mrg(open(args[1]))
diff_treebanks(test,gold)
