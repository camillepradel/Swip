#!/bin/sh

# make sure your BONSAI environment variable is set to the directory bonsai_vXXX (depending on the version)
#       and      MSTPARSER_DIR is set to your local path to the mst parser

# Parsing using :
# -- MElt tagger (https://gforge.inria.fr/projects/lingwb) Denis & Sagot, PACLIC 2009
# -- MST parser (http://mstparser.sourceforge.net/) McDonald Ph.D. thesis, Upenn, 2006

# input = raw text
# - segmentation into sentences / tokenisation / multi-word expressions
# - melt tagging
# - MST parsing
# output = labeled dependencies (conll)

# Marie Candito

MSTFRMODEL_DIR=$BONSAI/resources/mst

USAGE="\n Script to parse a French UTF-8 text file, using the MST Parser\n\n
Usage : $0 [-h] [-n] INFILE \n
  will output labeled dependencies into INFILE.outmst\n
[-n] Use this option to disable tokenization, segmentation into sentences and compound recognition (for already tokenized text). Don't use this option in order to process raw text.\n
"

PREPROCBOOL=" "
while getopts hn option
do	case "$option" in

	h)   echo -e $USAGE
	    exit 1;;
	n) PREPROCBOOL='-n ';;
	[?])	echo -e $USAGE
		exit 1;;
	esac
done
# shift arguments for the options alreay read
shift $((OPTIND - 1))

cat $1 | $BONSAI/bin/bonsai_preproc_for_malt_or_mst.sh -p mst $PREPROCBOOL > $1.inmst
java -classpath "$MSTPARSER_DIR:$MSTPARSER_DIR/lib/trove.jar" -Xmx1800m mstparser.DependencyParser test model-name:$MSTFRMODEL_DIR/ftb1-wflem-featred-cut50000-lbit10.model test-file:$1.inmst output-file:$1.outmsttmp decode-type:proj order:2 format:CONLL
paste $1.inmst $1.outmsttmp|awk 'BEGIN {OFS="\t"} {print $1,$11,$14,$15,$16,$17,$18,$19,$20,$21}' > $1.outmst
rm $1.outmsttmp