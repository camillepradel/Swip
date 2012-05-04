# -*- coding: utf-8 -*-

import os
import sys
import codecs
import optparse
from collections import defaultdict
from melt.utils import serialize


usage = "usage: %prog [options] <input_file>"
parser = optparse.OptionParser(usage=usage)
parser.add_option("-p", "--path", action="store", help="path for dump", default="./lefff_dict.json")
(options, args) = parser.parse_args()


lefff_file = codecs.open( args[0], 'r', encoding="utf8" )


lefff_dict = defaultdict(dict)
for line in lefff_file:
    line = line.strip()
    if not line:
        continue
    wd, tag, lemma = line.split('\t')
    lefff_dict[wd][tag] = 1

serialize(lefff_dict,options.path)
