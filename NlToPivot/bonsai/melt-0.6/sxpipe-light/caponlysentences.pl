#!/usr/bin/perl
# $Id: caponlysentences.pl 2904 2009-12-04 00:11:25Z sagot $

use locale;
binmode STDIN, ":encoding(iso-8859-1)";

$| = 1;

while (1) {
    $_=shift;
    if (/^$/) {last;}
}

while (<>) {
    # formattage
    chomp;

    if (/ (_XML|_MS_ANNOTATION) *$/) {
	print "$_\n";
	next;
    }

    $orig = $_;
    $min=0; $maj=0;
    $decap='';
    $inside_comment = 0;
    $inside_token = 0;
    while (s/(.)//) {
	$c = $1;
	$orc = $c;
	if ($inside_comment) {
	  if ($c eq "<") {
	    $inside_token = 0;
	  } elsif ($c eq ">") {
	    $inside_token = 1;
	  } elsif ($c eq "}") {
	    $inside_comment = 0;
	  }
	  if ($inside_token == 0 || $inside_comment == 0) {
	    $decap.=$c;
	    next;
	  }
	}
	if ($c eq "{") {$inside_comment = 1; $inside_token = 1; $decap.=$c; $inside_named_entity_id = 0; next;}
	if ($c eq " ") {$trans++; $inside_named_entity_id = 0}
	if ($c eq "_" && $decap =~ /[} ]$/) {$inside_named_entity_id = 1}
	if ($inside_named_entity_id == 1) {$decap.=$c; next}
	if ($c !~ /([^\W_0-9-])/) {$decap.=$c; next;}
	$c = lc($c);
	if ($orc eq $c) {$min++} else {$maj++}
	$decap.=$c;
    }
    $_ = $orig;
    if (5*$min < $maj && $trans > 1 && $maj > 10) {
#        $decap =~ s/(?<=[ }])_([^ {]+)/"_".uc($1)/ge;
	print "$decap\n";
    } else {
	print "$_\n";
    }
}
