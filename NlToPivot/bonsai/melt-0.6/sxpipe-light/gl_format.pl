#!/usr/bin/perl
# $Id: gl_format.pl 1853 2008-05-22 11:43:15Z sagot $

$| = 1;

$lang="fr";

while (<>) {
    # formattage
    chomp;
    if (/ (_XML|_MS_ANNOTATION) *$/) {
	print "$_\n";
	next;
    }

    s/^\s*/ /o;
    s/\s*$/ /o;

    # reconnaissance
    s/(\s)_UNDERSCORE([^\s_]+)_UNDERSCORE(\s)/$1\{_UNDERSCORE$2\_UNDERSCORE\} $2$3/o;
    s/(\s)\*([^ _]+)\*(\s)/$1\{*$2\*\} $1$3/o;

    # sortie
    s/^ //o;
    s/ $//o;
    print "$_\n";
}
