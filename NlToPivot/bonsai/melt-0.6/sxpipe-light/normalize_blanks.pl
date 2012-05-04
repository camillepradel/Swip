#!/usr/bin/perl
# $Id: normalize_blanks.pl 2034 2008-08-21 13:59:32Z sagot $

$| = 1;

while (<>) {
    chomp;
    if (/ (_XML|_MS_ANNOTATION|_PAR_BOUND) *$/) {
	print "$_\n";
	next;
    }
    s/  */ /g;
    s/{ /{/g;
    s/ }/}/g;
    s/\\/\\\\/g;
    s/%/\\%/g;
    print $_."\n";
}
