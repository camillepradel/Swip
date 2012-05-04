#!/usr/bin/perl
# $Id: gl_smiley.pl 2123 2008-10-09 21:50:23Z sagot $
$|=1;

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
    s/(\s)([;:]\s*-\s*(?:\(\s*\(|\)\s*\)|\)\s*\)\s*\)|\)\s*\)\s*\)\s*\)|\(|\)|\[|\]|D)|lol+|-\s*\)|:\s*[\)\(]|\^\s(?:_UNDERSCORE\s?){2,5}\s\^|\-\s(?:_UNDERSCORE\s?){2,5}\s\-)(\s)/$1\{$2}_SMILEY$3/go;

    # correction de sur-reconnaissance
    s/^\s*(\([^\)]*){([^{}]*\))}_SMILEY([\s\.]*)$/\1\2\3/go;

    # sortie
    s/^ //o;
    s/ $//o;
    print "$_\n";
}
