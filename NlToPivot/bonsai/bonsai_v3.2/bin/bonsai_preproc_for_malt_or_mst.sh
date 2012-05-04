#!/bin/bash


# Benchmarking of Statistical Dependency Parsers for French
# Candito, Nivre, Denis & Henestroza Anguiano, COLING 2010


# PREPROCESSING STEPS FOR BEST MODEL FOR MALT / FOR MST
#-------------------------------------------------


USAGE="
# INPUT read from STDIN = raw UTF-8 text\n
# OUTPUT printed to STDOUT = text in conll format, with predicted POS (via MElt tagging), predicted lemmas, predicted gender, number, and predicted unsupervised word cluster\n
\n
# processing includes\n
# - desinflection\n
# - substitution of desinflected forms  with unsupervised clusters\n
# - prediction of lemma and morpho features\n
\n
Usage : $0 [OPTIONS]\n
[-n] Use this option to disable tokenization, segmentation into sentences and compound recognition (for already tokenized text).\n
[-p [malt|mst]] Parser type (default=malt)\n
"
# ----------------------------------------------
# external resources
# ----------------------------------------------
# path to the Lefff lexicon
LEFFF=$BONSAI/resources/lefff/lefff
#cluster file : cluster id , tab, token 
CLUSTERFILE=$BONSAI/resources/clusters/EP.tcs.dfl-c1000-min20-v2.utf8
# default value 
PARSERTYPE=malt
PREPROCESS=y

while getopts nhp: option
do	case "$option" in

	p)     PARSERTYPE="$OPTARG";;
	n)     PREPROCESS='n';;
	h)  echo -e $USAGE
	    exit 1;;
	[?])	echo -e $USAGE
		exit 1;;
	esac
done
shift $((OPTIND - 1))
BIN=$BONSAI/src

# to lowercase
PARSERTYPE=`echo $PARSERTYPE|tr '[A-Z]' '[a-z]'`

if test $PREPROCESS = 'y'
then
PREPROCCMD="$BONSAI/bin/bonsai_raw_2_tok.sh | "
else
PREPROCCMD=" "
fi
 
# format for Malt Parser best model
if test $PARSERTYPE = 'malt'
then
NBDIGIT=7
MINOCC=600

eval $PREPROCCMD MElt | $BIN/do_brown2conll.pl | $BIN/do_lefff_lemmatisation.py --inputformat=conll --serializedlex $LEFFF | $BIN/do_desinflect.py --inputformat tabulated --token-rank 1 --append --serializedlex $LEFFF | $BIN/do_substitute_tokens.py --inputformat tabulated --token-rank 10 --ldelim '' --rdelim '' --append --no-cap-info $CLUSTERFILE | $BIN/do_format_for_malt_or_mst.pl --parsertype $PARSERTYPE --minocc=$MINOCC --nbdigit=$NBDIGIT 

else
# format for MST Parser best model
if test $PARSERTYPE = 'mst'
then
NBDIGIT=10
MINOCC=50000

eval $PREPROCCMD MElt | $BIN/do_brown2conll.pl | $BIN/do_lefff_lemmatisation.py --inputformat=conll --serializedlex $LEFFF | $BIN/do_desinflect.py --inputformat tabulated --token-rank 1 --append --serializedlex $LEFFF | $BIN/do_substitute_tokens.py --inputformat tabulated --token-rank 10 --ldelim '' --rdelim '' --append --no-cap-info $CLUSTERFILE | $BIN/do_format_for_malt_or_mst.pl --parsertype $PARSERTYPE --minocc=$MINOCC --nbdigit=$NBDIGIT 

else
echo $USAGE
exit 1
fi
fi

# ----------------------------------------------




