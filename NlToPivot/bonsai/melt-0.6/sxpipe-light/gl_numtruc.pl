#!/usr/bin/perl
# $Id: gl_numtruc.pl 2214 2009-03-12 09:50:26Z sagot $

## numtruc to detect expressions 4-mère  or 3-foliacé

use strict;

my $no_sw=0;
my $suffix_mark="";
my $lang="fr";

while (1) {
    $_=shift;
    if (/^$/) {last;}
    elsif (/^-no_sw$/ || /^-no_split-words$/i) {$no_sw=1;}
    elsif (/^-asm$/ || /^-add_suffix_mark$/i) {$suffix_mark="_-";}
}

$| = 1;


while (<>) {
    chomp;
    if (/ (_XML|_MS_ANNOTATION) *$/) {
	print "$_\n";
	next;
    }

    if ($no_sw) {
      s/\b(\s*)((?:(?:±\s*)?\d+[-­])+)(?=[a-zàâäéêèëîïöôùûüÿçµ\»¹¾³¶¼¿±A-ZÀÉÈÊËÂÄÔÖÛÜÇ¥\«©®£¦¬¯¡]{2,})/$1\{$2\}_NPREF/og;
    } else {
      s/\b(\s*)((?:(?:±\s*)?\d+[-­])+)(?=[a-zàâäéêèëîïöôùûüÿçµ\»¹¾³¶¼¿±A-ZÀÉÈÊËÂÄÔÖÛÜÇ¥\«©®£¦¬¯¡]{2,})/$1\{$2\}_NPREF/og;
#      s/\b(\s*)((?:(?:±\s*)?\d+[-­])+)(\s+)(?=[a-zàâäéêèëîïöôùûüÿçµ\»¹¾³¶¼¿±A-ZÀÉÈÊËÂÄÔÖÛÜÇ¥\«©®£¦¬¯¡]{2,})/$1\{$2\}_NPREF$3$suffix_mark/og;
    }


    # sortie
    if ($no_sw) {
	s/(_NPREF|_NUM)([^\s\}])/$1 _REGLUE_$suffix_mark$2/g;
	s/(^|\s)([^\s\{]+){([^{}]*)}(_NPREF|_NUM)/$1\{$2$3\} $2 _UNSPLIT_$4/g;
    } else {
	s/(_NPREF|_NUM)([^\s])/$1 $suffix_mark$2/g;
	s/(^|\s)([^\s\{]+){([^{}]*)}(_NPREF|_NUM)/$1$2 \{$3\}$4/g;
    }

    print $_."\n";
}
