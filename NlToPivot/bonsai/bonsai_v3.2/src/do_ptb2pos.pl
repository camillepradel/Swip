#!/usr/bin/perl

# From PTB bracketed format to tagged text
# outputs space-separated sequence of couples token+tag, separated by // or $1 if it exists


use Getopt::Long;

$outsep = '/' ;

GetOptions("outsep:s" => \$outsep,
	   "onetokenperline!" => \$onetokenperline,
	   "help!" => \$help
    ) or die(&usage) ;

die (&usage) if ($help) ;
sub usage
{
    return "
Reads from STDIN a corpus in penn treebank format
and outputs the corresponding tagged text.
$0
--outsep = separator between token and tag. Default /
--onetokenperline = If set, outputs one token+tag per line, separated by a tab.
" 
}

if ($onetokenperline)
{
    $outsep = "\t" ;
    $betweentokens = "\n" ;
}
else
{
    $betweentokens = " " ;
}
while(<>)
{ 
    while(/\( *([^\)\( ]+) +([^\) ]*) *\)(.*)$/) 
    { 
	$_ = $3 ;
	$token = $2 ; 
	$tag = $1 ; 
	$token = '(' if ($token eq '-LRB-' or $token eq '<LBR>' or $token eq '--LBR--') ;
	$token = ')' if ($token eq '-RRB-' or $token eq '<RBR>' or $token eq '--RBR--') ;
	print "$token$outsep$tag$betweentokens" ; 
    } 
    print "\n" ; 
}
