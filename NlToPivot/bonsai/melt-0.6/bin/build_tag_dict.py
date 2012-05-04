#!/usr/bin/env python

import os
import sys
import optparse
from melt.mytoken import Token
from melt.utils import tag_dict, serialize

usage = "usage: %prog [options] <input_file>"
parser = optparse.OptionParser(usage=usage)
parser.add_option("-p", "--path", action="store", help="path for dump", default="./tag_dict.json")
(options, args) = parser.parse_args()

tdict = tag_dict( args[0] ) # e.g., ftb_1.pos

serialize(tdict,options.path)


print >> sys.stderr, "Tag dictionary dumped at: %s (%s entries)" %(options.path,len(tdict))



