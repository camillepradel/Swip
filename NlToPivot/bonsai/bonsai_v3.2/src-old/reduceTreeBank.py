#! /sw/bin/python -O
# -*- coding: iso-8859-15 -*-
#
# reduceTreeBank
# Author : Marie Candito
# Date : may 2008

# Out of a treebank
# produce a suite of reduced treebanks

import re
import sys
import os
from optparse import OptionParser

usage = """
           %prog [options] TREEBANKDIR
        """

optparser=OptionParser(usage=usage)
optparser.add_option("-n", "--nbparts", default=10, help="Nb de sous-corpus a' produire. Le plus petit aura la taille 100/nb % du total")
#optparser.add_option("-s", "--filesuffix", default='mrg', help="Suffix for treebank files")
(opts,args) = optparser.parse_args()
treebankdir = args[0]
nbparts = int(opts.nbparts)
# infiles are training set and dev set
infiles = [ 'ftb_1.mrg', 'ftb_2.mrg' ]

if treebankdir <> None:
    if os.path.isdir(treebankdir):
        for rk in range(1,nbparts):
            dirrk = treebankdir + '-' + str(rk)
            os.mkdir(dirrk)
            # copy test set (raw and mrg) "as is"
            os.system('cp '+treebankdir+'/*raw '+dirrk)
            os.system('cp '+treebankdir+'/ftb_3.mrg '+dirrk)
        # iterate on training set and dev set
        for file in infiles:
            instream = open(os.path.join(treebankdir, file))
            outstreams = [ open(os.path.join(treebankdir + '-' + str(rk), file), 'w') for rk in range(1,nbparts) ]
            count = 0
            for line in instream:
                count = count + 1
                if count == nbparts:
                    count = 0
                else:
                    for rk in range(count, nbparts):
                        outstreams[rk - 1].write(line)
            for x in outstreams: x.close()
            instream.close()


