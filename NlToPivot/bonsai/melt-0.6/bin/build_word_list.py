#!/usr/bin/env python

import os
import sys
from melt.utils import word_list
import optparse

usage = "usage: %prog [options] <input_file>"
parser = optparse.OptionParser(usage=usage)
parser.add_option("-f", "--frequency", action="store", help="freq. threshold", type=int, default=5)
(options, args) = parser.parse_args()

n = options.frequency

wd_list = word_list( args[0], t=n )
for w,ct in wd_list.items():
    print w.encode('utf-8'),ct

