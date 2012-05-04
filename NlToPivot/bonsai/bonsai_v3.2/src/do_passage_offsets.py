#!/usr/bin/env python
# -*- coding: utf-8 -*-

# Enrique Henestroza Anguiano : 

from optparse import OptionParser
import sys
import re

usage = """This program reads from STDIN a Passage XML file, and outputs
           a file with correct offsets.
"""

parser = OptionParser(usage=usage)
parser.add_option("--out-name", dest="out_name", default="document", help='Desired document tag name for Passage XML output file')
parser.add_option("--raw-file", dest="raw_file", default="", help='.txt raw untokenized file to compare against Passage XML format file')

(opts, args) = parser.parse_args()
out_name = str(opts.out_name)
raw_file = str(opts.raw_file)

bigline = ""
rfile = open(raw_file, 'r')
for line in rfile:
    nline = line.rstrip()
    bigline += nline
    for i in range(len(line) - len(nline)):
        bigline += " "
rfile.close()

# print "Input length: " + str(len(bigline))

def deEscapeChars(s):
    return s.replace("&amp;","&").replace("&lt;","<").replace("&gt;",">").replace("&quot;","\"").replace("&apos;","\'")


# Simple approach, doesn't allow any missed tokens.
# Works well under the assumption that most tokens should find a match.
ofile = open(out_name, 'w')
lookahead = 20
offset = 1
consumedstring = ""
warned = 0
warnedlength = 0
for line in sys.stdin:
    m = re.search('>(.*)</T>', line)

    if m != None and warned < 20:
        token = deEscapeChars(m.group(1))

        # Replace underscores with spaces
        if len(token) > 1:
            tok = token.replace("_", " ").lstrip().rstrip()
            token = tok

        # Have a default set of values just in case the real token isn't found.
        mstart = warnedlength
        mend = warnedlength + len(token)

        # Cut any spaces before or after the word, due to slight misalignment.
        while bigline[mstart:mstart+1] == " ":
            mstart += 1
            if bigline[mend-1:mend] != " ":
                mend += 1
        while bigline[mend-1:mend] == " ":
            mend -= 1

        m2 = bigline.find(token, 0, lookahead + warnedlength + len(token))
        if m2 != -1:
            mstart = m2
            mend = m2 + len(token)
            warned = 0
            warnedlength = 0
            consumedstring = bigline[:mend]
            bigline = bigline[mend:]
        else:
            print "WARNING \'"+out_name+"\': Assigned token \'"+token+ "\' to raw \'"+bigline[mstart:mend]+"\'"
            warned += 1
            warnedlength += len(token) + 1

        line = re.sub('start=\".*?\"', 'start=\"'+str(offset+mstart)+'\"', line)
        offset += len(consumedstring.decode("utf-8"))

        line = re.sub('end=\".*?\"', 'end=\"'+str(offset)+'\"', line)



        if warned == 10:
            print "EMERGENCY: 10 consecutive mismatched tokens, widening lookahead."
        elif warned == 20:
            print "FAILURE: 20 consecutive mismatched tokens."

    ofile.write(line)

ofile.close()

