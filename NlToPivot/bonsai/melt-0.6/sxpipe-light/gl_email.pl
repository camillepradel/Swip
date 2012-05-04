#!/usr/bin/perl
# $Id: gl_email.pl 1853 2008-05-22 11:43:15Z sagot $


# language independant

$| = 1;

while (1) {
    $_=shift;
    if (/^$/) {last;}
    elsif (/^-no_sw$/ || /^-no_split-words$/i) {$no_sw=1;}
}

while (<>) {
    # formattage
    chomp;
    if (/ (_XML|_MS_ANNOTATION) *$/) {
	print "$_\n";
	next;
    }

    s/^\s*//o;
    s/\s$//o;

    # variables
    $an          = qr/[0-9a-zàâäéêèëîïöôùûüÿçµ\»¹¾³¶¼¿±A-ZÀÉÈÊËÂÄÔÖÛÜÇ¥\«©®£¦¬¯¡]/o;

    # correction
    if (!$no_sw) {
	s/\s+\@/\@/o;
	s/\@\s+/\@/o;
	s/(\@$an+),\s/$1./go;
	s/(\@$an+\.)\s/$1/go;
	s/(\@$an+\.$an+\.)\s([a-z]{2,})\b/$1$2/go;
    }

    # reconnaissance
    s/\b([\w\.-]+\s*\@\s*(?:[\w-]+\.)+\s?$an{2,4})\b/{$1}_EMAIL/go;

    s/(^|\s)(\&lt;\s*){([^}]*)}_EMAIL(\s*\&gt;)(\s|$)/$1\{$2$3$4\}_EMAIL$5/go;
    
    if ($no_sw) {
	s/(_EMAIL)([^\s])/$1 _REGLUE_$2/g;
	s/(^|\s)([^\s]+){([^{]*)}_EMAIL/$1\{$2$3\} $2 _REGLUE__EMAIL/g;
    } else {
	s/(_EMAIL)([^\s])/$1 $2/g;
    }

    # sortie
    print "$_\n";
}
