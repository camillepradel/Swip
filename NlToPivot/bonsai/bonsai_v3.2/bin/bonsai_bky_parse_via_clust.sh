#!/bin/sh

# make sure your BONSAI environment variable is set to the directory bonsai_vXXX (depending on the version)

# Parsing of raw UTF-8 text file using Slav Petrov's Berkeley parser 
# (http://nlp.cs.berkeley.edu/Main.html#Parsing)

# input = raw text
# - segmentation into sentences / tokenisation / multi-word expressions
# - Berkeley parsing
# - substitution of tokens with morphological clusters (desinflection)
# - substitution of desinflected forms  with unsupervised clusters
# - Berkeley parsing
# - reinsertion of original tokens
# - optional:functional role labelling
# - optional:conversion to labeled dependencies


# Marie Candito

# ----------------------------------------------
# external resources
# ----------------------------------------------
# path to the Lefff lexicon
LEFFF=$BONSAI/resources/lefff/lefff
#cluster file : cluster id , tab, token 
CLUSTERFILE=$BONSAI/resources/clusters/EP.tcs.dfl-c1000-min20-v2.utf8
# Berkeley Jar and trained grammar
BKYJAR=$BONSAI/resources/bkyjar/berkeleyParser-V1_0-fr.jar
BKYGRAMMAR=$BONSAI/resources/bkygram/gram-ftbuc+dfl+clust0-200-v6
# megam model for functional role labelling (old version...)
FLMODEL=$BONSAI/resources/flabelling/ftb1pw.v0

# ----------------------------------------------
BIN=$BONSAI/src
OLDBIN=$BONSAI/src-old

USAGE="\n Script to parse a French UTF-8 text file, using the Berkeley Parser, and unsupervised clusters (tokens are replaced by clusters before parsing, and reinserted afterwards).\n\n
By default, the INFILE is a raw text. Use -n option if INFILE is already tokenized (one sentence per line, tokens separated by space)\n\n
Usage : $0 [OPTIONS] INFILE \n
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
	h)   echo -e $USAGE
	    exit 1;;
	[?])	echo -e $USAGE
		exit 1;;
	esac
done
# shift arguments for the options alreay read
shift $((OPTIND - 1))

# - preprocessing
if test $PREPROCESS = 'y'
then
cat $1 | $BONSAI/bin/bonsai_raw_2_tok.sh > $1.tcs
TOK=$1.tcs
else
TOK=$1
fi

if test $OUTFORMAT = 'ldep'
then
#-------------------------------------------------
# *** parsing to labeled dependencies ****
#-------------------------------------------------
# -- desinflect forms, substitute by clusters
# -- parse with BKY
# -- reinsert original tokens (that appear in $1 : this is why this script DOES NOT READ ON STDIN)
# -- functional labelling (*** new version to come soon... ***)
# -- conversion to labeled dependencies
cat $TOK | $BIN/do_desinflect.py --serializedlex --inputformat tok $LEFFF | tee $TOK.dfl | $BIN/do_substitute_tokens.py --inputformat tok --ldelim '-K' --rdelim 'K-' $CLUSTERFILE | recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | $BIN/do_reinsert_tokens.py $TOK | python $OLDBIN/Const2dep.py --input-format=ptb --para=$FLMODEL --ctree | perl -n -e 's/CLS-([A-Z_]+)/CLS-SUJ/g; print;' | python $BIN/do_ptb2dep.py --labeled | $BIN/do_lefff_lemmatisation.py --inputformat=conll --serializedlex $LEFFF
else
if test $OUTFORMAT = 'udep'
then
#-------------------------------------------------
# *** parsing to unlabeled dependencies ****
#-------------------------------------------------
# -- desinflect forms, substitute by clusters
# -- parse with BKY
# -- reinsert original tokens (that appear in $TOK : this is why this script DOES NOT READ ON STDIN)
# -- conversion to unlabeled dependencies
cat $TOK | $BIN/do_desinflect.py --serializedlex --inputformat tok $LEFFF | tee $TOK.dfl | $BIN/do_substitute_tokens.py --inputformat tok --ldelim '-K' --rdelim 'K-' $CLUSTERFILE | recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | $BIN/do_reinsert_tokens.py $TOK | python $BIN/do_ptb2dep.py | $BIN/do_lefff_lemmatisation.py --inputformat=conll --serializedlex $LEFFF
else
#-------------------------------------------------
# *** parsing to French-Treebank-like constituents
#-------------------------------------------------
# -- desinflect forms, substitute by clusters
# -- parse with BKY
# -- reinsert original tokens (that appear in $TOK : this is why this script DOES NOT READ ON STDIN)
cat $TOK | $BIN/do_desinflect.py --serializedlex --inputformat tok $LEFFF | tee $TOK.dfl | $BIN/do_substitute_tokens.py --inputformat tok --ldelim '-K' --rdelim 'K-' $CLUSTERFILE | recode utf8..latin1 | $BIN/do_prebky.pl | java -Xmx1800m -jar $BKYJAR -accurate -gr $BKYGRAMMAR | $BIN/do_reinsert_tokens.py $TOK 
fi
fi
