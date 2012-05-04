#!/usr/bin/perl
# $Id: gl_number.pl 2758 2009-09-22 12:41:38Z sagot $

# ‡ passer en dernier

$| = 1;

$lang="fr";

while (1) {
    $_=shift;
    if (/^$/) {last;}
    elsif (/^-no_sw$/ || /^-no_split-words$/i) {$no_sw=1;}
    elsif (/^-l(?:ang)?=(.*)$/) {$lang=$1;} elsif (/^-l(?:ang)?$/) {$lang=shift;}
}

$l        = qr/[^a-z‡‚‰ÈÍËÎÓÔˆÙ˘˚¸ˇÁµ\ªπæ≥∂ºø±A-Z¿…» À¬ƒ‘÷€‹«•\´©Æ£¶¨Ø°\.]/o;
$maj      =qr/[^ A-Z¿…» À¬ƒ‘÷€‹«•\´©Æ£¶¨Ø°]/o;
$numFR1   = qr/(?:\d{1,3}(?:\s+\d{3})*(?:\s*,\s*\d+)?)/o;
$numFR2   = qr/(?:\d{1,3}(?:\.\d{3})*(?:\s*,\s*\d+)?)/o;
$numNOSEP = qr/(?:\d+(?:\s*[,\.]\s*\d+)?)/o;
$num_ext = qr/(?:\d*(?:[Il]*\d+|\d+[Il])(?:\s*[,\.]\s*[\dIl]+)?)/o;
$numUS    = qr/(?:\d{1,3}(?:\s*,\s*\d{3})*(?:\s*\.\s*(?:[12]\s*\/\s*[234]|\d+))?)/o;
$rom1_10 = qr/(?:III?|I?V|VIII?|VI|I?X|I)/o;
$rom2_10 = qr/(?:III?|I?V|VIII?|VI|I?X)/o;
$rom10_maxrom = qr/(?:(?:M+(?:C?D)?)?(?:X{,4}|X?L|LX{1,4}|X?C{1,4}))/o;
$numROM    = qr/(?:$rom10_maxrom?$rom1_10)/o;
$numROMnotI    = qr/(?:$rom10_maxrom?$rom2_10|${rom10_maxrom}I)/o;
$num      = qr/(?:$numFR1|$numNOSEP|$numFR2|$numUS)/o;
$num_ext   = qr/(?:$numFR1|$numNOSEP|$numFR2|$numUS|$num_ext)/o;
$multinum = qr/$num(?:\s*[\-\/\.\≠]\s*$num)?/o;
$multinum_ext = qr/$num_ext(?:\s*[\-\/\.\≠]\s*$num_ext)?/o;
$multinumL = qr/$num[\-\≠]?[a-zA-Z](?:\s*[\-\/\.\≠]\s*$num[\-\≠]?[a-zA-Z])?/o;
$multinumROMnotI = qr/$numROMnotI(?:\s*[\-\/\.\≠]\s*(?:$numROM|$multinum))?/o;
$multinumROMI = qr/I\s*[\-\/\.\≠]\s*(?:$numROM|$multinum)/o;
$fraction = qr/(?:[1-9][0-9]*\s*\/\s*[1-9][0-9]*)/o;
$num_left_ctxt = qr/(?:[Aa]rt\s*\.?|nr\s*\.?|num\s*\.?|pkt\s*\.?|ust\s*\.?)/o;
if ($lang eq "sk") {
  $numsuff  = qr/(?:\.)(?=\s+[^\s])/io;
  $num_right_ctxt = qr/(?:rok[aoue]*)(?:[\s\"\Ω,;\.:\)]|$)/o;
} elsif ($lang eq "pl") {
  $numsuff  = qr/(?:\.)(?=\s+[^\s])/io;
  $num_right_ctxt = qr/(?:lata?|rok[aoue]*)(?:[\s\"\Ω,;\.:\)]|$)/o;
} elsif ($lang eq "en") {
  $numsuff  = qr/(?:th|st|[nr]d)(?=[\s\"\Ω,;\.:\)])/io;
  $num_right_ctxt = qr/(?:years?)(?:[\s\"\Ω,;\.:\)]|$)/o;
} else {
  $numsuff  = qr/(?:i?[eË]mes?|[eË]|ers?|[eË]res?|nds?|ndes?|aines?|e)(?=[\s\"\Ω,;\.:\)])/io;
  $num_right_ctxt = qr/(?:ans?)(?:[\s\"\Ω,;\.:\)]|$)/o;
}
$not_an     = qr/[^0-9a-z‡‚‰ÈÍËÎÓÔˆÙ˘˚¸ˇÁµ\ªπæ≥∂ºø±A-Z¿…» À¬ƒ‘÷€‹«•\´©Æ£¶¨Ø°{}]/o;

while (<>) {
    # formattage
    chomp;
    if (/ (_XML|_MS_ANNOTATION) *$/) {
	print "$_\n";
	next;
    }

    s/  +/ /g;
    s/^/  /;
    s/$/  /;
    s/([^0-9],)([0-9]+,[0-9])/$1 $2/g;

    if ($lang eq "en") {
      s/(?<=[^\}]) (\d+\+)( [^\d]|[\.,;:\?\!\(\)\[\]]|$)/ {\1} _NUM\2/go;
      s/(?<=[^\}]) (\d+(?:[km]|bn))( [^\d]|[\.,;:\?\!\(\)\[\]]|$)/ {\1} _NUM\2/go;
    }
    s/(?<=[^\}]) (\#\d+)([ \.,;:\?\!\(\)\[\]])/ {\1} _NUM\2/go;

    if ($lang eq "fr") {
      s/(?<=[^\}]) (M) (6)([ \.,;:\?\!\(\)\[\]])/ {$1 $2} $1_$2$3/go;
      s/(?<=[^\}]) (France) ([2345]|24)([ \.,;:\?\!\(\)\[\]])/ {$1 $2} $1_$2$3/go;
      s/(?<=[^\}]) (Europe) ([12])([ \.,;:\?\!\(\)\[\]])/ {$1 $2} $1_$2$3/go;
      s/(?<=[^\}]) (RTL) ([29])([ \.,;:\?\!\(\)\[\]])/ {$1 $2} $1_$2$3/go;
      s/(?<=[^\}]) (TF) (1)([ \.,;:\?\!\(\)\[\]])/ {$1 $2} $1_$2$3/go;
      s/(?<=[^\}]) (George) (V)([ \.,;:\?\!\(\)\[\]])/ {$1 $2} $1_$2$3/go;
      s/(?<=[^\}]) (20) (minutes)([ \.,;:\?\!\(\)\[\]])/ {$1 $2} $1_$2$3/go;
      s/(?<=[^\}]) (Seveso) ([12]|I|II)([ \.,;:\?\!\(\)\[\]])/ {$1 $2} $1_$2$3/go;
    }
    s/(?<=[^\}]) G ([78])([ \.,;:\?\!\(\)\[\]])/ {G $1} G$1$2/go;
    s/(?<=[^\}]) (4[xX]4)([ \.,;:\?\!\(\)\[\]])/ {\1} 4x4\2/go;
    s/(?<=[^\}]) (N)( ?)([+-])( ?)([0-9])([ \.,;:\?\!\(\)\[\]])/ {\1\2\3\4\5} \1\3\5/go;

    # security for m2, m3, km2 and others
    s/(\s)(k?m[23]|M6|G[789]|France[2345]|France24|TF1|\S+_\d\S*)([ \.,;:\?\!\(\)\[\]])/$1\__\{\{$2\}\}__$3/g;
    s/(\&#\d+;)/\__\{\{$1\}\}__/g;
    s/(moteurs?\s*)(V[0-9]+)([ \.,;:\?\!\(\)\[\]])/\1 __{{\2}}__\3/go;
    s/(?<=[^\}]) (i\. *e\.?|e\. *g\.?|d\. *h\.?|z\. *b\.?|o\. *‰\.?|m\. *e\.?|u\. *s\. *w\.?)([ \.,;:\?\!\(\)\[\]])/ __{{\1}}__\2/goi;
    if ($lang eq "fr") {
      s/(Ian)([ \.,;:\?\!\(\)\[\]])/__{{\1}}__ \2/go; # protection required
    }

    # reconnaissance
    $listnumid = qr/(?:$numROM|\d+)(?:\s*\.\s*(?:$numROM|\d+|[A-Za-z]))*(?:\s*\.)?(?=[^0-9])/o;
    $listnumidB = qr/(?:[A-Za-z])(?:\s*\.\s*(?:$numROM|\d+|[A-Za-z]))*(?:\s*\.)?(?=[^0-9])/o;
    $listnumprefix = qr/((?:^|[\.;:,]|_META_TEXTUAL_[^ ]+) +)/o;
    $listnumprefix2 = qr/((?:^|, |[\.;:] ?|_META_TEXTUAL_[^ ]+ ) *)/o;
    $listnumprefix3 = qr/((?:^|[;:,]|_META_TEXTUAL_[^ ]+) +)/o;

    s/^ +([\-\≠=]+\s*&gt;)/\{$1\}_META_TEXTUAL_PONCT/go; # [dÈbut de ligne] ->
    if ($no_sw) {
      s/$listnumprefix((?:$numROM|\d+)\s*[\.\-\≠]\s*$listnumid)(\s*[\.\)\]\/\]\-\≠])?(?=[^0-9}])/$1\{$2$3\}_META_TEXTUAL_GN/go; # [dÈbut de ligne] I.2 texte
      s/$listnumprefix([\-\≠])(\s+)($listnumid|$listnumidB)(\s+)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2\}_META_TEXTUAL_PONCT$3\{$4\}_META_TEXTUAL_GN$5\{$6\}_META_TEXTUAL_PONCT/go; # [dÈbut de ligne] - 1) texte
      s/$listnumprefix([\-\≠])(\s+)($listnumid|$listnumidB)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2\}_META_TEXTUAL_PONCT$3\{$4$5\}_META_TEXTUAL_GN/go; # [dÈbut de ligne] - 1) texte
      s/$listnumprefix([\-\≠])($listnumid|$listnumidB)(\s+)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2$3\}_META_TEXTUAL_GN$4\{$5\}_META_TEXTUAL_PONCT/go; # [dÈbut de ligne] -1 - texte
      s/$listnumprefix([\-\≠])($listnumid|$listnumidB)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2$3$4\}_META_TEXTUAL_GN/go; # [dÈbut de ligne] -1) texte
      s/$listnumprefix($listnumid|$listnumidB)(\s+)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2\}_META_TEXTUAL_GN$3\{$4\}_META_TEXTUAL_PONCT/go; # [dÈbut de ligne] 1 ) texte

      s/$listnumprefix($listnumid|$listnumidB)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2$3\}_META_TEXTUAL_GN/go; # [dÈbut de ligne] 1) texte
    } else {
      s/$listnumprefix2((?:$numROM|\d+)\s*[\.\-\≠]\s*$listnumid)(\s*[\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2$3\}_META_TEXTUAL_GN/go; # [dÈbut de ligne] I.2) texte
      s/$listnumprefix2((?:$numROM|\d+)\s*[\.\-\≠]\s*$listnumid) /$1\{$2\}_META_TEXTUAL_GN /go; # [dÈbut de ligne] I.2 texte
      s/$listnumprefix2([\-\≠])(\s*)($listnumid|$listnumidB)(\s*)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2\}_META_TEXTUAL_PONCT$3\{$4\}_META_TEXTUAL_GN$5\{$6\}_META_TEXTUAL_PONCT/go; # [dÈbut de ligne] - 1) texte
      s/$listnumprefix2($listnumid)(\s*)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2\}_META_TEXTUAL_GN$3\{$4\}_META_TEXTUAL_PONCT/go; # [dÈbut de ligne] 1) texte
      s/$listnumprefix($listnumidB)(\s*)([\.\)\]\/\]\-\≠])(?=[^0-9}])/$1\{$2\}_META_TEXTUAL_GN$3\{$4\}_META_TEXTUAL_PONCT/go; # [dÈbut de ligne] 1) texte
    }

    # rattrapage de sur-corrections
    if ($lang eq "fr") {
      s/$listnumprefix\{([Aa])\}_META_TEXTUAL_GN\{-\}_META_TEXTUAL_PONCTt-/\1\2-t-/g;
      s/$listnumprefix\{([G-Z…¿¬ €Œ‘ƒÀ‹œ÷«])\}_META_TEXTUAL_GN\{\.\}_META_TEXTUAL_PONCT/\1\2./g; # \1 est dans listnumprefix
      # ci-dessus = horreur
    }
    s/$listnumprefix\{(e\.g|i\.e)\}_META_TEXTUAL_GN\{\.\}_META_TEXTUAL_PONCT/\1\2./g;

    if ($lang eq "en") { # le mot "a" ne peut terminer une phrase... donc on va dire artificiellement que " a[.\)...]" dÈsigne tjs un _META_TEXTUAL_truc
      s/ ([Aa])([\.\)\]\/\]\-\≠])(?=[^0-9])/ \{$1\}_META_TEXTUAL_GN\{$2\}_META_TEXTUAL_PONCT/go; # a.
    }

    s/$listnumprefix([\-\≠\*])/$1\{$2\}_META_TEXTUAL_PONCT/go; # [dÈbut de ligne] - texte
    s/$listnumprefix\+(\s)/$1\{\+\}_META_TEXTUAL_PONCT$2/go; # [dÈbut de ligne] + texte
    s/$listnumprefix3\.(\s)/$1\{\.\}_META_TEXTUAL_PONCT$2/go; # [dÈbut de ligne] . texte

    # QQUES CAS TORDUS
    s/(?<=[^0-9\{])(\d+\s*(?:[,\.]\s*|\s+)[123]\s*\/\s*[234])([\s\"\Ω;:\)]|[\.,][^0-9]|$)/\{$1\}_NUM$2/go; #    9,1/4
    s/(?<=[^0-9\{])([A-Z\d]*\d[A-Z\d]*\s*[\/-]\s*[\dA-Z\/-\s]*[\dA-Z])([\s\"\Ω;:\)]|[\.,][^0-9]|$)/\{$1\}_NUM$2/go; #    (fractions)   2-F4      A3-249/93	0-190/93
    s/(?<=[^0-9\{])(\d+(?:[.,]\d+)?\s*x\s*\d+(?:[.,]\d+)?)([\s\"\Ω;:\)]|[\.,][^0-9]|$)/\{$1\}_NUM$2/go; # 130x180
#    s/(?<=[^0-9\{])($fraction)/\{$1\}_NUM/go; # 1/2

    s/(?<=$not_an)($multinumROMnotI)(?=(?:$l|\.\s+$maj))/\{$1\}_ROMNUM/go; # IV
    s/(?<=$not_an)($multinumROMI)(?=(?:$l|\.\s+$maj))/\{$1\}_ROMNUM/go; # I
    s/(?<=$not_an)($multinumROMnotI$numsuff)(?=$l)/\{$1\}_ROMNUM/go; # IVËme
    s/(?<=$not_an)($multinumROMI$numsuff)(?=$l)/\{$1\}_ROMNUM/go; # Ier

    s/(?<=[^0-9\{])($multinum\s*$numsuff)/\{$1\}_NUM/go; # 2Ëme
    if ($lang eq "pl") {
	s/(?<=[^0-9\{])($multinum_ext\s*(?:mld|tys|mln|bln)(?:\s*\.)?)/\{$1\}_NUM/go; # 2Ëme
    }
    s/(?<=[^0-9\{])($multinumL)(\s*)(?=[^0-9\-\≠\/,\.a-z‡‚‰ÈÍËÎÓÔˆÙ˘˚¸ˇÁµ\ªπæ≥∂ºø±A-Z¿…» À¬ƒ‘÷€‹«•\´©Æ£¶¨Ø°])/\{$1\}_NUM$2/go; # 2a

    s/($num_left_ctxt\s*)([A-Z])([\s\"\Ω,;\.:\)]|$)/$1\{$2\} _NUM$3/go;
    s/($num_left_ctxt\s*)([0-9]+\s*[A-Za-f])([\s\"\Ω,;\.:\)]|$)/$1\{$2\} _NUM$3/go;
    s/(?<=\s)([0-9]*[Il][0-9\.,\s]*[0-9][0-9\.,\s]*?)(\s*$num_right_ctxt)/\{$1\} _NUM$2/go;
    s/(?<=[^0-9\{])([0-9]*[0-9\.,\s]+[Il])(\s*$num_right_ctxt)/\{$1\} _NUM$2/go;

    while (s/(?<=[^0-9\{])($multinumL)(\s*[,\.\-\/\≠])([^0-9]|$)/\{$1\}_NUM\2\3/go) {} 
    s/(?<=[^0-9\{])($multinum)(\s*)(?=[^0-9\-\≠\/,\.\}])/\{$1\}_NUM$2/go; # 2
    while (s/(?<=[^0-9\{])($multinum)(\s*[,\.\-\≠\/])([^0-9]|\s+$)/\{$1\}_NUM\2\3/go) {} 
# REGROUPE AVEC LE PRECEDENT   s/(?<=[^0-9\{])($multinum) +$/\{$1\}_NUM/go; # 2 [fin de ligne]

    # sÈcuritÈs pour Èviter les _NUM emboitÈs et les reconnaissances de trucs dÈj‡ reconnus ‡ l'avance comme 4x4
    while (s/{([^{}]*){([^{}]*)}_NUM/{$1$2/g){}
    s/{([^}]+)}\s*{([^}]+)}_NUM/{$1} $2/g;

    if ($lang eq "fr") {
      s/(?<=[^0-9\{])(\{[^\}]+\}_NUM\s*virgule\s+\{[^\}]+\}_NUM)/\{$1\}_NUM/go; # 2 virgule 5
    }

    s/(?<=\s)([0-9]+)(?=\s)/\{$1\}_NUM/go; # sÈcuritÈ

    s/ \}/\}/go;

    # unlock protections
    s/__\{\{//g;
    s/\}\}__//g;

    if ($lang =~ /^(ja|zh|tw)$/) {
      s/({&#226;&#145;&#16[0-8];})\s*_ETR/\1_META_TEXTUAL_GN/g;
#      s/({&#227;&#131;&#187;})\s*_ETR/\1_META_TEXTUAL_PONCT/g;
      s/({&#226;&#151;&#143;})\s*_ETR/\1_META_TEXTUAL_PONCT/g;
    }
    s/({(?:&#226;&#128;&#162;|&#194;&#183;)})\s*_ETR/\1_META_TEXTUAL_PONCT/g; # diverses bullets unicode yarecodÈes

    # sortie
    $ne = qr/(?:_(?:ROM)?NUM|_META_TEXTUAL_PONCT|_META_TEXTUAL_GN)/o;
    if ($no_sw) {
	s/($ne)([^\s\}])/$1 _REGLUE_$2/g;
	#    print STDERR "1: $_\n\n";
	s/(^|\s)([^\s\{]+){([^{}]*)}($ne)/$1\{$2$3\} $2 _UNSPLIT_$4/g;
	if (s/{_REGLUE_.*?}//) {print STDERR "Warning: gl_number encoutered an unexpected situation (input data is approx: $_)\n"}
	#    print STDERR "2: $_\n\n";
    } else {
	s/($ne)([^\s])/$1 $2/g;
	s/(^|\s)([^\s\{]+){([^{}]*)}($ne)/$1$2 \{$3\}$4/g;
    }

    s/^(.*_META_TEXTUAL_[^ ]+)/\1 _UNSPLIT__SENT_BOUND/g;
    s/({[^}]*(?:{[^}]*(?:{[^}]*(?:{[^}]*}[^}]*)*}[^}]*)*}[^}]*)*})\s*{[^}]*}\s*($ne) _REGLUE_/$1$2 _UNSPLIT_/g;
    s/({[^}]*(?:{[^}]*(?:{[^}]*(?:{[^}]*}[^}]*)*}[^}]*)*}[^}]*)*})\s*{[^}]*}\s*($ne)/$1$2/g;

    s/^ //;
    s/ $//;
    print "$_\n";
}

