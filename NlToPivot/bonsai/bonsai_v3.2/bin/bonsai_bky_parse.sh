#!/bin/sh

# make sure your BONSAI environment variable is set to the directory bonsai_vXXX (depending on the version)

# Parsing of raw UTF-8 text file using Slav Petrov's Berkeley parser 
# (http://nlp.cs.berkeley.edu/Main.html#Parsing)

# input = raw text
# - segmentation into sentences / tokenisation / multi-word expressions
# - Berkeley parsing
# - optional:functional role labelling
# - optional:conversion to labeled dependencies


# Marie Candito

# TODO : switch to bky newer version, and get everything in utf8....
# ----------------------------------------------
# external resources
# ----------------------------------------------
# Berkeley Jar and trained grammar
BKYJAR=$BONSAI/resources/bkyjar/berkeleyParser-V1_0-fr.jar
BKYGRAMMAR=$BONSAI/resources/bkygram/gram-ftbuc-v6
# megam model for functional role labelling (old version...)
FLMODEL=$BONSAI/resources/flabelling/ftb1pw.v0

# ----------------------------------------------
BIN=$BONSAI/src
OLDBIN=$BONSAI/src-old

USAGE="\n Script to parse French UTF-8 text, read on STDIN, using the Berkeley Parser\n\n
By default, the input is raw text. Use -n option if input is already tokenized (one sentence per line, tokens separated by space)\n\n
Usage : $0 [OPTIONS] \n
[-n] Use this option to disable tokenization, segmentation into sentences and compound recognition (for already tokenized text).\n
[-f [const|udep|ldep]] Output format (default=const): \n
\t-f const will output French-Treebank-like constituents,\n
\t-f udep  will output unlabeled surface dependencies (conll)\n
\t-f ldep  will output labeled surface dependencies (conll)
"
OUTFORMAT=const
PREPROCESS=y

while getopts f:nh option
do	case "$option" in

	f)      OUTFORMAT="$OPTARG";;
	n)      PREPROCESS='n';;
	h)   echo $USAGE
	    exit 1;;
	[?])	echo $USAGE
		exit 1;;
	esac
done
# shift arguments for the options alreay read
shift $((OPTIND - 1))



if test $OUTFORMAT = 'ldep'
then
#-------------------------------------------------
# *** parsing to labeled dependencies ****
#-------------------------------------------------
# -- parse with BKY
# -- functional labelling (*** new version to come soon... ***)
# -- conversion to labeled dependencies
if test $PREPROCESS = 'y'
then
$BONSAI/bin/bonsai_raw_2_tok.sh | recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | python $OLDBIN/Const2dep.py --input-format=ptb --para=$FLMODEL --ctree | perl -n -e 's/CLS-([A-Z_]+)/CLS-SUJ/g; print;' | recode latin1..utf8 | python $BIN/do_ptb2dep.py --labeled
else
recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | python $OLDBIN/Const2dep.py --input-format=ptb --para=$FLMODEL --ctree | perl -n -e 's/CLS-([A-Z_]+)/CLS-SUJ/g; print;' | recode latin1..utf8 | python $BIN/do_ptb2dep.py --labeled
fi

else # outformat
if test $OUTFORMAT = 'udep'
then
#-------------------------------------------------
# *** parsing to unlabeled dependencies ****
#-------------------------------------------------
# -- parse with BKY
# -- conversion to unlabeled dependencies
if test $PREPROCESS = 'y'
then
$BONSAI/bin/bonsai_raw_2_tok.sh | recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | recode latin1..utf8 | python $BIN/do_ptb2dep.py
else
recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | recode latin1..utf8 | python $BIN/do_ptb2dep.py
fi

else # outformat=const
#-------------------------------------------------
# *** parsing to French-Treebank-like constituents
#-------------------------------------------------
# -- parse with BKY
if test $PREPROCESS = 'y'
then
$BONSAI/bin/bonsai_raw_2_tok.sh | recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | recode latin1..utf8
else
recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | recode latin1..utf8
fi

fi
fi
