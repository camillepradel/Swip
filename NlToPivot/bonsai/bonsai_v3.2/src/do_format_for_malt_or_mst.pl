#!/usr/bin/perl

use Getopt::Long;
$minocc = 50000 ;
$nbdigit = 10 ;
# either malt or mst
$parsertype = 'malt' ;
GetOptions("minocc:i" => \$minocc,
	   "nbdigit:i" => \$nbdigit,
	   "parsertype:s" => \$parsertype,
           "help!" => \$help) or die (&usage) ;

$parsertype = lc($parsertype) ;

die(&usage) if ($parsertype ne 'malt' && ($parsertype ne 'mst')) ;

sub usage
{
    return "" ;
}
while(<>)
{
    chomp; 
    
    if ($_ eq '') { print "\n"; next ;}
    # UNKC
    s/UNKC_num/num/g ; 
    s/UNKC_(_([a-z]*)|prep_)?/UNKC/g ; 
    s/\t([01]+)(_[a-z]+|prep_)(\t[0-9]+)$/\t$1$3/g ; 

    # filter out features that are redundant with the POS, to shrink model
    @cols = split(/\t/) ;
    $occ = $cols[12] ;

    # get the first nbdigit digits of the clusterid
    $clusterid = substr($cols[11], 0, $nbdigit) ;
    $cat = $cols[4] ;

    # filter out some of the features for mst
    if ($parsertype eq 'mst')
    {
	$featuresstr = $cols[5] ;
	$featuresstr = '' if ($featuresstr eq '_') ;
	@features = map {
	    ($feat,$val) = split(/=/, $_, 2) ;
	    if (($feat eq 'm') or ($feat eq 's' and ($val =~/^(c|obj|p|suj)$/)) or ($cat eq 'PROREL' and (/^s=rel/)) or ($cat eq 'CS' and (/^s=s/)) or ($cat eq 'CLR' and (/^s=ref/)))
	    {
		() ;
	    }
	    else {  $_ ;}
	} split(/\|/,$featuresstr) ;
	# add cluster feature is occ >= minocc
	if ($occ >= $minocc)
	{
	    $features[$#features + 1] = 'lc='.$clusterid ;
	}
	# reconstruct features string
	$cols[5] = join('|', @features) ;
	$cols[5] = '_' if ($cols[5] eq '') ;

	# get rid of desinflected form, clusterid and occ columns
	pop(@cols) ;
	pop(@cols) ;
	pop(@cols) ;

	# lemma replace word forms, word forms kept as extra column
	$cols[$#cols + 1] = $cols[1] ;
	$cols[1] = $cols[2] ;
	$cols[2] = '_' ;

	$cols[6] = '0' ;
	$cols[7] = 'd' ;

    }
    # format for malt
    else
    {
	# add cluster feature is occ >= minocc
	$clusterid = '_' unless ($occ >= $minocc) ;
	$cols[5] = $cols[5]."\t$clusterid" ;
	# get rid of desinflected form, clusterid and occ columns
	pop(@cols) ;
	pop(@cols) ;
	pop(@cols) ;

    }

    print join("\t", @cols). "\n" ;

}

# original filter method (enrique)
#def shouldReduce(featstr, cat):
#    featval = featstr.split("=")
#    feat = featval[0]
#    val = featval[1]
#    if feat == "m" or (feat == "s" and (val == "c" or val == "obj" or val == "p" or val == "suj" or (val == "refl" and cat == "CLR") or (val == "rel" and cat == "PROREL") or (val == "s" and cat == "CS"))):
#        return True
#    return False


