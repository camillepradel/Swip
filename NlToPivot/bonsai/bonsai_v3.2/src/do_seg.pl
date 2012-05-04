#!/usr/bin/perl

# Marie Candito

use Getopt::Long;
$help = 0 ;
$outsep='space' ;
GetOptions("outsep:s" =>\$outsep,
           "help!" => \$help) or die (&usage) ;
 
die(&usage) unless (!$help and ($outsep eq 'newline' or $outsep eq 'space'))  ;

sub usage
{
    return "$0
Very naive sentence segmentation.
Use after tokenization (do_tok) 
[ -help ]\n" ;
}
$newline = "\n" ;
while(<>)
{
    # any period,?,! that was handled as a token separator, and not preceded by a digit
    $_ =~ s/(?<![0-9]) ([\.\?\!](?:\.\.)?) / $1$newline/g ;
    # any period,?,! that was handled as a token separator, and not followed by a digit
    $_ =~ s/ ([\.\?\!](?:\.\.)?) (?![0-9])/ $1$newline/g ;
    # any ';' that was handled as a token separator
    $_ =~ s/ ; / ;$newline/g ;
    $_ =~ s/$newline +/$newline/g ;
    print ;

}
