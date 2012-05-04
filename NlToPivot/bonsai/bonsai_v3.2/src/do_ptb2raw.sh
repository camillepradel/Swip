#!/bin/sh
# From PTB bracketed format to raw format : outputs space-separated sequence of terminals 
perl -n -e 's/[^\)]*?( [^\)\( ]+)[ \)]+/$1/g; s/^ //; print;'