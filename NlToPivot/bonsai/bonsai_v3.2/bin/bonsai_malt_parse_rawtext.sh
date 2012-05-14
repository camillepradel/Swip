#!/bin/sh

# make sure your BONSAI environment variable is set to the directory bonsai_vXXX (depending on the version)
#       and      MALT_DIR is set to your local path to the mst parser

# Parsing using :
# -- MElt tagger (https://gforge.inria.fr/projects/lingwb) Pascal Denis & Benoit Sagot, PACLIC 2009
# -- Malt parser (http://www.maltparser.org), developed by Johan Hall, Jens Nilsson and Joakim Nivre at Växjö University and Uppsala University, Sweden.

# input = raw text
# - segmentation into sentences / tokenisation / multi-word expressions
# - melt tagging
# - MST parsing
# output = labeled dependencies (conll)

# Marie Candito

MALTFRMODEL_DIR=$BONSAI/resources/malt

USAGE="\n Script to parse a French UTF-8 text file, using the Malt Parser\n\n
Usage : $0 [-h] [-n] INFILE \n
  will output labeled dependencies into INFILE.outmalt\n
[-n] Use this option to disable tokenization, segmentation into sentences and compound recognition (for already tokenized text). Don't use this option in order to process raw text.\n
"

PREPROCBOOL=" "
while getopts hn option
do	case "$option" in

	h) echo -e $USAGE
	   exit 1;;
	n) PREPROCBOOL='-n ';;
	[?])	echo -e $USAGE
		exit 1;;
	esac
done
# shift arguments for the options alreay read
shift $((OPTIND - 1))


cat $1 | $BONSAI/bin/bonsai_preproc_for_malt_or_mst.sh -p malt $PREPROCBOOL > $1.inmalt
#set -x
java -jar -Xmx2048m $MALT_DIR/malt.jar -c ftb-all -w $MALTFRMODEL_DIR -i $1.inmalt -v debug -m parse -o $1.outmalt -of $MALTFRMODEL_DIR/cformat.xml -if $MALTFRMODEL_DIR/cformat.xml 
