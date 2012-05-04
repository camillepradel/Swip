#!/usr/bin/perl
# $Id: segmenteur.pl 2917 2009-12-22 17:20:55Z sagot $


# Principe général
# ----------------

# On procède en deux temps
# 1. tokenisation (on insère ou supprime des blancs:
#    à l'issue, un blanc est une frontière de tokens)
#    Ceci n'est pas fait si -no_sw
# 2. segmentation (les tokens constitués de points ou 
#    d'autres ponctuations sont susceptibles d'être des 
#    frontières de phrases
# L'option -no_s permet de ne faire que la partie 1 
# (les frontières de phrases restent de simples blancs)

# Spécificités:
# - on utilise le fichier passé en paramètres (-af=xxx) 
#   pour ne pas traiter comme un cas normal les points 
#   terminant des abréviations connues
# - on essaye d'être fin sur les points frontières de 
#   phrases et les autres
# - on essaye de ne pas mettre de frontière de phrase 
#   au milieu d'une citation bien balancée
# - l'option -p=[rp] permet de privilégier le rappel 
#   (les phrases peuvent sans problème commencer par 
#   une minuscule) ou la précision (les phrases ne 
#   commencent pas par une minuscule)




$| = 1;

$lang = "fr";
$toksent=1;
$no_af=0;
$no_sw=-1;
$cut_on_apos=0;
$sent_bound="\n";
$print_par_bound = 0;
$weak_sbound = 1;
$affixes = 0; # car normalement géré par text2dag
#$split_before_ne=0;
my $best_recall=0;

if ($lang eq "fr" || $lang eq "en") {
  $min=qr/(?:[a-zàâäéêèëîïöôùûüÿç])/;
  $maj=qr/(?:[A-ZÀÉÈÊËÂÄÔÖÛÜÇ])/;
  $l=qr/(?:[àâäéêèëîïöôùûüÿça-zA-ZÀÉÈÊËÂÄÔÖÛÜÇ])/;
  $nonl=qr/(?:[^a-zàâäéêèëîïöôùûüÿçA-ZÀÉÈÊËÂÄÔÖÛÜÇ])/;
} else {
  $min=qr/(?:[a-záä±æèïéêìëíåµ³ñòóôöàø¶¹\»úùüı¼¿])/;
  $maj=qr/(?:[A-ZÁÄ¡ÆÈÏÉÊÌËÍÅ£¥ÑÒÓÔÖÀØ¦©\«ÚÙÜİ¬¯])/;
  $l=qr/(?:[a-záä±æèïéêìëíåµ³ñòóôöàø¶¹\»úùüı¼¿A-ZÁÄ¡ÆÈÏÉÊÌËÍÅ£¥ÑÒÓÔÖÀØ¦©\«ÚÙÜİ¬¯])/;
  $nonl=qr/(?:[^a-záä±æèïéêìëíåµ³ñòóôöàø¶¹\»úùüı¼¿A-ZÁÄ¡ÆÈÏÉÊÌËÍÅ£¥ÑÒÓÔÖÀØ¦©\«ÚÙÜİ¬¯])/;
}
$initialclass=$maj;

while (1) {
    $_=shift;
    if (/^$/) {last;}
    elsif (/^-s$/ || /^-split-sentences?$/i) {$toksent=1;}
    elsif (/^-no_s$/ || /^-no_split-sentences?$/i) {$toksent=0;}
    elsif (/^-sb=(.*)$/ || /^-sentences?-boundary=(.*)$/i) {$sent_bound=$1;}
    elsif (/^-af=(.*)$/ || /^-abbrev-file=(.*)$/i) {$abrfilename=$1;}
    elsif (/^-no_af$/ || /^-no_abbrev-file$/i) {$no_af=1;}
    elsif (/^-sw$/ || /^-no_split-words$/i) {$no_sw=0;}
    elsif (/^-no_sw$/ || /^-no_split-words$/i) {$no_sw=1;}
    elsif (/^-ca$/ || /^-cut_on_apos$/i) {$cut_on_apos=1;}
    elsif (/^-a$/ || /^-affixes$/i) {$affixes=1;}
    elsif (/^-r/ || /^-p=r$/ || /^-best_sbound_recall$/i) {$initialclass=$l; $best_recall=1}
    elsif (/^-n/ || /^-p=p$/ || /^-best_sbound_precision$/i) {$initialclass=$maj;}
    elsif (/^-p/) {$initialclass=$maj; $weak_sbound = 0}
    elsif (/^-ppb$/ || /^-print_par_bound$/i) {$print_par_bound = 1;}
    elsif (/^-l=(.*)$/ || /^-lang=(.*)$/i) {$lang=$1;}
}

if ($no_sw == -1) {
  $no_sw = 0;
  if ($lang =~ /^(ja|tw|zh|th|ko)$/) {
    $no_sw = 1;
  }
}

$sent_bound =~ s/\\n/\n/g;
$sent_bound =~ s/\\r/\r/g;
$sent_bound =~ s/\\t/\t/g;
$sent_bound =~ s/^"(.*)"$/\1/;

my $cut_on_apos_re = "";
if ($cut_on_apos) {
  if ($lang eq "fr") {
    $cut_on_apos_re = join('|', qw/c m n j s t aujourd d l qu puisqu lorsqu quelqu presqu prud quoiqu jusqu/);
    $cut_on_apos_re = qr/(?:$cut_on_apos_re)/i;
  }
}

@abr=();
@abrp=();
my $temp;
my $temp2;
if ($abrfilename=~/\w/) {
    open (ABR,"<$abrfilename") || die ("Could not open dot-including abbreviations file not found ($abrfilename)\n");
    while (<ABR>) {
	if ((/^\"..+\"$/ || /^[^\"].+$/) && /^[^\#]/) {
	    chomp;
	    s/_/ /g;
	    s/^(.*[^\.].*\.)\.$/$1\_FINABR/; # peuvent être des abréviations finissant par point et finissant une phrase (type etc.)
	    s/^\"//;
	    s/\"$//;
	    $rhs = $_;
#	    $rhs=~s/ /_/g;
	    $rhs_nospace = $rhs;
	    $rhs_nospace=~s/_FINABR//;
	    $rhs_nospace=~s/ //g;
	    $rhs_nospace2rhs{$rhs_nospace}=$rhs;
	    s/([\.\[\]\(\)\*\+])/\\$1/g; # échappement des caractères spéciaux
	    s/^\\\. */\\\. \*/g;
	    s/(?<=.)\\\. */ \*\\\. \*/g;
#	    s/ /\\s/g;

# 	    s/ \*$/)( \+\[\^ \]/ ||
# 	      s/ \*_FINABR$/)( \+\[\^ \]\|\$/; # on ne reconnaît les abr à la fin de la phrase que si elles ont le droit d'y être
# 	    s/\_$//;
# 	    s/^/(/;
# 	    s/$/)/;    
# 	    s/ /\\s/g;
	    if (s/ \*_FINABR$//) {
	      push(@abr_fin,$rhs);
	      push(@abrp_fin,$_);
	    } else {
	      s/ \*$//;
	      push(@abr,$rhs);
	      push(@abrp,$_);
	    }
	}
    }
    close (ABR);
    $abrp_re = join("|",sort {length($b) <=> length($a)} @abrp);
    $abrp_re = qr/($abrp_re)/o;
    $abrp_fin_re = join("|",sort {length($b) <=> length($a)} @abrp_fin);
    $abrp_fin_re = qr/($abrp_fin_re)/o;
} elsif (!$no_af) {
    print STDERR "No dot-including abbreviations given\n";
    $no_af = 1;
}

$par_bound = 0;

while (<STDIN>) {
    chomp;
    if (/ (_XML|_MS_ANNOTATION) *$/) {
	print "$_\n";
	$par_bound = 0;
	next;
    } else {
      $par_bound = 1;
    }

    if ($par_bound && $print_par_bound) {
      print " _PAR_BOUND\n";
    }

    s/^/ /; s/$/ /;
    if (!$no_sw && $lang !~ /^(ja|zh|tw|th)$/) {                               # si on peut tokeniser soi-même...
	s/(?<=[^\.\t])\.+(\s\s+)/ \.$1/g;                    # si suivi de deux blancs (ou TABs) ou plus, point+ est un token
	s/(?<=\t)\.+(\s\s+)/\.$1/g;                          # idem
	s/ +/ /g;                                            # ceci étant exploité, on normalise les espaces
	s/\t+/\t/g;
	s/ +\t/\t/g; s/\t +/\t/g;

	s/(_(?:UNSPLIT|REGLUE|SPECWORD)_[^\s{]+)/\1_PROTECT_/g; # on protège de la segmentation des tokens déjà découpés et identifiés par _UNSPLIT_ ou _REGLUE_
	if ($lang =~ /^(fr|es|it)$/) {
	  s/[ \(\[]'([^ '][^']*?[^ '])'(?=[ ,;?\!:\"\)\(\*\#<>\[\]\%\/\=\+\«\»\½\&\`\.])/ ' \1 ' /g;          # les apostrophes peuvent servir à quoter...
	  s/  / /g;
	  s/(?<=[^\'\s_])\'\s*/\'/g;                            # par défaut, on supprime les blancs suivant les guillemets sauf après " '" et "''"
	  s/(\s)\'(?=[^ _])/$1\' /g;                            # en français, les autres guillemets sont détachés
	} else {
	  s/[ \(\[]'([^ '][^']*?[^ '])'(?=[ ,;?\!:\"\)\(\*\#<>\[\]\%\/\=\+\«\»\½\&\`\.])/ ' \1 ' /g;          # les apostrophes peuvent servir à quoter...
	  s/  / /g;
	}

	while (s/({[^}]*) ([^}]*}\s*_(?:EMAIL|EPSILON|URL|META_TEXTUAL_PONCT|SENT_BOUND|ETR))/$1\_SPACE$2/g) {} # on protège les espaces déjà présents dans ces entités nommées (cf ci dessous)
	if ($lang !~ /^(?:fr|en|es|it|nl|de|pt)$/) {
	  s/ *([,;?\!:\"\)\(\*\#<>\[\]\%\/\=\+\«\»\½\&\`]) */ $1 /g;# on isole toutes les ponctuations (et assimilées) sauf le point (½ est un guillement en l2)
	} else {
	  s/ *([,;?\!:\"\)\(\*\#<>\[\]\%\/\=\+\«\»\&\`]) */ $1 /g;# on isole toutes les ponctuations (et assimilées) sauf le point
	}
	s/  +/ /g;
	while (s/({[^}]*) ([^}]*}\s*_(?:EMAIL|EPSILON|URL|META_TEXTUAL_PONCT|SENT_BOUND|ETR))/$1$2/g) {} # les espaces qu'on vient d'insérer sont mauvais dans 
	                                                                      # ces entités nommées: on les enlève...
	s/_SPACE/ /g;                                                         # ...et on restaure ceux qui y étaient avant
	while (s/(_(?:REGLUE|UNSPLIT|SPECWORD)_[^\s{]+)(?<!_PROTECT_)\s+/\1/g) {}
	s/_PROTECT_//g;

	s/ *(_UNDERSCORE|_ACC_O|_ACC_F) */ $1 /g;            # on isole aussi les ponctuations échappées

	if ($lang eq "fr") {
	  s/($nonl)et\s*\/\s*ou($nonl)/$1 et\/ou $2/g;         # cas particulier: cas particulier pour et/ou
	} elsif ($lang eq "en") {
	  s/($nonl)and\s*\/\s*or($nonl)/$1 and\/or $2/g;         # cas particulier: cas particulier pour and/or
	}
	s/\&\s*(amp|quot|lt|gt)\s*;/\&$1;/g;                       # cas particulier: entités XML
	s/ &lt; -/&lt;-/g;                                     # cas particulier: flèches
	s/- &gt; /-&gt; /g;                                     # cas particulier: flèches

	s/ +\t/\t/g; s/\t +/\t/g;
	s/(\.\.*) *( |$)/$1$2/g;                             # un seul blanc derrière 2 points ou plus

	# les entnom sont des tokens
	s/(\} *_[A-Za-z_]+)/$1 /g;

	# POINT après une minuscule
	while (s/($min)(\.\.*\s*)([\(\[\"\)\]\?\!\'\/\_\½][^{}]*|(?:$l)[^{}]*)?(\{|$)/$1 $2 $3$4/g) {} # avant certaines poncts ou une lettre ou retour-chariot ou {, point+ est un token, mais pas dans les commentaires...
	s/($min)(\.\.+\s*)([^ ])/$1 $2 $3/g;                 # avant qqch d'autre (y compris un chiffre), il faut 2 points pour cela
	s/ +\t/\t/g; s/\t +/\t/g;

	# POINT après une majuscule
	s/\b($maj\.)($maj$min)/$1 $2/g;                      # insertion d'un blanc entre maj-point et maj-min
	s/($maj{3,})\./$1 \./g;                              # 3 majuscules de suite puis point -> le point est une ponct
	s/\.($maj{3,})/\. $1/g;                              # point puis 3 majuscules de suite -> le point est une ponct

	# POINT après un chiffre
	s/(\d)(\.+)([^0-9])/$1 $2 $3/g;                      # chiffre point non-chiffre -> le point est une ponct
	
        # TIRETS et slashes
	s/(\d)(\s*\-\s*)(\d)/$1 $2 $3/g;                     # le tiret entre 2 chiffres est une ponct
	s/([\(\[\"\)\]\,\;\%\½])\-/$1 -/g;                   # le tiret après une ponct autre que point en est séparé
	s/\-([\(\[\"\)\]\,\;\%\½])/- $1/g;                   # le tiret avant une ponct autre que point en est séparé
	s/($l)([\/\-])\s*($l)/$1$2$3/g;                      # recollages de tirets ou de slashes
	s/($l)\s*([\/\-])($l)/$1$2$3/g;
	s/ +\t/\t/g; s/\t +/\t/g;

	s/ *$//g;
	s/\n/\#\n/g;                                         # on marque d'un "#" les fins de ligne dans le texte brut

	# toilettage final
	s/ +\t/\t/g; s/\t +/\t/g;
# BS: lignes suivantes douteuses
#	s/\. +([\-\{])/ . $1/g;
#	s/([^\{])\{/$1 \{/g;

	if ($lang =~ /^(fr|es|it)$/) {
	  s/(\s)\'(?=[^ _])/$1\' /g;                            # rebelotte, car des nouveaux blancs ont pu être insérés
	}
	if ($cut_on_apos && $cut_on_apos_re ne "") {
	  s/ (${cut_on_apos_re})'([^ ])/ \1' \2/g;
	}
	s/(\s[\-\/\.])($l)/$1 $2/g;
	s/^ *([\-\/\.])($l)/$1 $2/g;
	s/(\s\.)\-/$1 -/g;
	s/\.(\-\s)/. $1/g;
    } else {
	s/(?<=[^\s])([\"\*\%\«\»\½]\s)/ _REGLUE_$1/g; # prudence
	s/(\s[\"\*\%\«\»\½])([^\s]+)/$1 _REGLUE_$2/g; # prudence
	s/(?<=_REGLUE_)\s+_REGLUE_//g;
	s/(?<=_UNSPLIT_)\s+_REGLUE_//g;
    }
    s/^/  /;
    s/$/  /;

    # Recollages particuliers
    s/(?<=[^\}]) (R \& [Dd]) / {$1} R\&D /go;
    if ($lang eq "fr") {      # ATTENTION : blanc non convertis en \s
      s/(?<=[^\}]) (C ?\. N ?\. R ?\. S ?\.) / {$1} C.N.R.S. /go;
      s/(?<=[^\}]) (S ?\. A ?\. R ?\. L ?\.) / {$1} S.A.R.L. /go;
      s/(?<=[^\}]) (S ?\. A ?\.) / {$1} S.A. /go;
      s/(?<=[^\}]) M +\. / {M .} M. /go;
      s/(?<=[^\}]) ([tT][eéEÉ][Ll]) +\. / {\1 .} \1. /go;
      s/(?<=[^\}]) \+ \+ / {+ +} ++ /go;
      s/(?<=[^\}]) \+ \/ \- / {+ \/ -} +\/- /go;
      #    s/([àâäéêèëîïöôùûüÿça-zA-ZÀÉÈÊËÂÄÔÖÛÜÇ]) (\*|_UNDERSCORE|_) ([^ \{\}]+)/$1 {$2 $3} $3/go;
      
    }

    if ($lang eq "en") {
      if ($expand_contractions) {
	s/(?<=[^\}]) ([aA])in't / {\1in't} \1re not /goi;      
	s/(?<=[^\}]) ([cC]a)n't / {\1n't} \1n not /goi;      
	s/(?<=[^\}]) ([Ww])on't / {\1on't} \1ill not /goi;      
	s/(?<=[^\}]) ([^ ]+)n't / {\1n't} \1 not /goi;      
	s/(?<=[^\}]) ([Ii])'m / {\1'm} I am /goi;      
	s/(?<=[^\}]) ([Yy]ou|[Ww]e)'re / {\1're} \1 are /goi;      
	s/(?<=[^\}]) (I|you|we|they|should|would)'(ve) / {\1've} \1 have /goi;      
	s/(?<=[^\}]) (I|you|he|she|we|they)'(d) / {\1'd} \1 would /goi;      
	s/(?<=[^\}]) (I|you|he|she|we|they)'(ll) / {\1'll} \1 will /goi;      
	s/(?<=[^\}]) (they)'(re) / {\1're} \1 are /goi;      
	s/(?<=[^\}]) ([^ ]+)'s / {\1's} \1 's /goi;      
	s/(?<=[^\}]) ([^ ]+)'/ {\1'} \1 's /goi;      
      } else {
	s/(?<=[^\}]) ([aA])in't / {\1in't} \1re n't /goi;      
	s/(?<=[^\}]) ([cC]a)n't / {\1n't} \1n n't /goi;      
	s/(?<=[^\}]) ([Ww])on't / {\1on't} \1ill n't /goi;
	s/(?<=[^\}]) ([^ ]+)n't / {\1n't} \1 n't /goi;      
	s/(?<=[^\}]) ([Ii])'m / {\1'm} I 'm /goi;      
	s/(?<=[^\}]) ([Yy]ou|[Ww]e)'re / {\1're} \1 're /goi;      
	s/(?<=[^\}]) (I|you|we|they|should|would)'(ve) / {\1've} \1 've /goi;      
	s/(?<=[^\}]) (I|you|he|she|we|they)'(d) / {\1'd} \1 'd /goi;      
	s/(?<=[^\}]) (I|you|he|she|we|they)'(ll) / {\1'll} \1 'll /goi;      
	s/(?<=[^\}]) (they)'(re) / {\1're} \1 're /goi;      
	s/(?<=[^\}]) ([^ ]*[^ s])'s / {\1's} \1 's /goi;      
	s/(?<=[^\}]) ([^ ]*s)'(?!s |\}.)/ {\1'} \1 's /goi;
      }
    } elsif ($lang eq "fr") {
      s/(?<=[^\}]) ([Ss]) ' (\S+)/ {\1 ' \2} \1'\2/goi;      
    }

    if ($affixes) {
      # ATTENTION:  normalement, ce travail est fait plus tard, par text2dag. Ceci ne devrait être utilisé que si l'on utilise sxpipe comme tokenizer pur (i.e., sans faire de vraie différence entre token et forme)
      if ($lang eq "fr") {
	# hyphenated suffixes
	# doit-elle => doit -elle
	# ira-t-elle => ira -t-elle
	# ira - t -elle => ira => -t-elle
	s/(- ?t ?)?-(ce|elles?|ils?|en|on|je|la|les?|leur|lui|[mt]oi|[vn]ous|tu|y)(?![\wéèêîôûëïüäù])/ $1-$2/go ;
	# donne-m'en => donne -m'en
	s/(- ?t ?)?-([mlt]\')/ $1-$2/go ;
	
	# hyphenated prefixes
	# anti-bush => anti- bush
	# franco-canadien => franco- canadien
	s/((?:anti|non|o)-)/$1 /igo ;
      }
    }

    s/(?<=[^\}])(\s+)(\( \.\.\. \))(\s+)/$1\{$2} (...)$3/go;
    s/(?<=[^\}])(\s+)(\[ \.\.\. \])(\s+)/$1\{$2} (...)$3/go;
    s/(?<=[^\}])(\s+)(\! \?)(\s+)/$1\{$2} !?$3/go;
    s/(?<=[^\}])(\s+)(\? \!)(\s+)/$1\{$2} ?!$3/go;
    s/(?<=[^\}])(\s+)(\!(?: \!)+)(\s+)/$1\{$2} !!!$3/go;
    s/(?<=[^\}])(\s+)(\?(?: \?)+)(\s+)/$1\{$2} ???$3/go;
    s/(?<=[^\}])(\s+)(\^\s+\^\s+)([^ \{\}]+)/$1\{$2$3\} $3 /go;
    s/(?<=[^\}]) ([^ \{\}\(\[]+) \^ ([^ \{\}]+)/{$1 ^ $2} $1\^$2/go;
    s/^\s+([^\s\{\}\(\[]+)(\s\^\s)([^\s\{\}]+)/\{$1$2$3\} $1\^$2/go;
    s/((?:_UNDERSCORE\s?)+_UNDERSCORE)([^_]|$)/ {$1} _UNDERSCORE $2/go;
    s/((?:_ACC_O\s?)+_ACC_O)(\s|$)/ {$1} _ACC_O$2/go;
    s/((?:_ACC_F\s?)+_ACC_F)(\s|$)/ {$1} _ACC_F$2/go;
    
    s/(?<=[^\}]) (turn over) / {$1} turn-over /g;
    s/(?<=[^\}]) (check liste?) / {$1} check-list /g;
    if ($lang eq "fr") {
      #abréviations courantes
      s/(?<=[^\}])([- ])([Qq])qfois /$1\{$2qfois} $2elquefois /go;
      s/(?<=[^\}])([- ])([Ee])xple /$1\{$2xple} $2xemple /go;
      s/(?<=[^\}])([- ])([Bb])cp /$1\{$2cp} $2eaucoup /go;
      s/(?<=[^\}])([- ])([Dd])s /$1\{$2s} $2ans /go;
      s/(?<=[^\}])([- ])([Mm])(gm?t) /$1\{$2$3} $2anagement /go;
      s/(?<=[^\}])([- ])([Nn])s /$1\{$2s} $2ous /go;
      s/(?<=[^\}])([- ])([Nn])b /$1\{$2b} $2ombre /go;
      s/(?<=[^\}])([- ])([Tt])ps /$1\{$2ps} $2emps /go;
      s/(?<=[^\}])([- ])([Tt])(jr?s) /$1\{$2$3} $2oujours /go;
      s/(?<=[^\}])([- ])([Qq])que(s?) /$1\{$2ue$3} $2uelque$3 /go;
      s/(?<=[^\}])([- ])([Qq])n /$1\{$2n} $2uelqu'un /go;
      s/(?<=[^\}])([- ])([Cc])(\.?-?[aà]\.?-?d\.?) /$1\{$2$3} $2'est-à-dire /go;
      s/(?<=[^\}])([- ])([Nn])breu(x|ses?) /$1\{$2breu$3} $2ombreu$3 /go;
      s/(?<=[^\}])([- ])([^ ]+t)([º°]) /$1\{$2$3} $2ion(s) /go;
      s/(?<=[^\}])([- ])([Ss])(n?t) /$1\{$2$3} $2ont /go;
      s/(?<=[^\}])([- ])(le|du|les|ce) ([wW][Ee]) /$1$2 \{$3} week-end /go;
      
      # fautes courantes
      s/(?<=[^\}]) (avant gardiste) / {$1} avant-gardiste /g;
      s/(?<=[^\}])([- ])à (fortiori|priori|posteriori|contrario) /$1\{à} a $2 /go;
      s/(?<=[^\}])([- ])pa /$1\{pa} pas /go;
      s/(?<=[^\}])([- ])er /$1\{er} et /go;
      s/(?<=[^\}])([- ])([Qq])uant ([^aà])/$1\{$2uant} $2and $3/go;
      s/(?<=[^\}])([- ])QUANT ([^AÀ])/$1\{QUANT} QUAND $2/go;
      s/(?<=[^\}])([- ])([Cc]) (est|était) /$1\{$2} $2' $3 /go;
      s/(?<=[^\}])([- ])(Etats[- ][Uu]nis) /$1\{$2} États-Unis /go;
      s/(?<=[^\}])([- ])([Rr])([eé]num[eé]ration) /$1\{$2$3} $2émunération /go;
      s/(?<=[^\}])([- ])c (est|ets) /$1\{c} c' \{$2} est /go;
    } elsif ($lang eq "en") {
      #abréviations courantes
      s/(?<=[^\}])([- ])(acct(?: ?\.)?) /$1\{$2} account /g;
      s/(?<=[^\}])([- ])(addl(?: ?\.)?) /$1\{$2} additional /g;
      s/(?<=[^\}])([- ])(amt(?: ?\.)?) /$1\{$2} amount /g;
      s/(?<=[^\}])([- ])(approx(?: ?\.)?) /$1\{$2} approximately /g;
      s/(?<=[^\}])([- ])(assoc(?: ?\.)?) /$1\{$2} associate /g;
      s/(?<=[^\}])([- ])(avg(?: ?\.)?) /$1\{$2} average /g;
      s/(?<=[^\}])([- ])(bldg(?: ?\.)?) /$1\{$2} building /g;
      s/(?<=[^\}])([- ])(incl(?: ?\.)?) /$1\{$2} including /g;
      s/(?<=[^\}])([- ])(intl(?: ?\.)?) /$1\{$2} international /g;
      s/(?<=[^\}])([- ])(jan(?: ?\.)?) /$1\{$2} January /g;
      s/(?<=[^\}])([- ])(feb(?: ?\.)?) /$1\{$2} February /g;
      s/(?<=[^\}])([- ])(apr(?: ?\.)?) /$1\{$2} April /g;
      s/(?<=[^\}])([- ])(aug(?: ?\.)?) /$1\{$2} august /g;
      s/(?<=[^\}])([- ])(sep(?: ?\.)?) /$1\{$2} September /g;
      s/(?<=[^\}])([- ])(oct(?: ?\.)?) /$1\{$2} October /g;
      s/(?<=[^\}])([- ])(nov(?: ?\.)?) /$1\{$2} November /g;
      s/(?<=[^\}])([- ])(dec(?: ?\.)?) /$1\{$2} December /g;
      s/(?<=[^\}])([- ])(max(?: ?\.)?) /$1\{$2} maximum /g;
      s/(?<=[^\}])([- ])(mfg(?: ?\.)?) /$1\{$2} manufacturing /g;
      s/(?<=[^\}])([- ])(mgr(?: ?\.)?) /$1\{$2} manager /g;
      s/(?<=[^\}])([- ])(mgt(?: ?\.)?) /$1\{$2} management  /g;
      s/(?<=[^\}])([- ])(mgmt(?: ?\.)?) /$1\{$2} management /g;
      s/(?<=[^\}])([- ])(std(?: ?\.)?) /$1\{$2} standard /g;
      s/(?<=[^\}])([- ])(w \/ o) /$1\{$2} without /g;
      s/(?<=[^\}])([- ])(dept(?: ?\.)?) /$1\{$2} department /g;
      s/(?<=[^\}])([- ])(wk(?: ?\.)?) /$1\{$2} week /g;
      s/(?<=[^\}])([- ])(div(?: ?\.)?) /$1\{$2} division /g;
      s/(?<=[^\}])([- ])(asst(?: ?\.)?) /$1\{$2} assistant /g;
      s/(?<=[^\}])([- ])(av(?: ?\.)?) /$1\{$2} average /g;
      s/(?<=[^\}])([- ])(avg(?: ?\.)?) /$1\{$2} average /g;
      s/(?<=[^\}])([- ])(co(?: ?\.)?) /$1\{$2} company /g;
      s/(?<=[^\}])([- ])(hr(?: ?\.)?) /$1\{$2} hour /g;
      s/(?<=[^\}])([- ])(hrs(?: ?\.)?) /$1\{$2} hours /g;
      s/(?<=[^\}])([- ])(mo(?: ?\.)?) /$1\{$2} month /g;
      s/(?<=[^\}])([- ])(mon(?: ?\.)?) /$1\{$2} Monday /g;
      s/(?<=[^\}])([- ])(tue(?: ?\.)?) /$1\{$2} Tuesday /g;
      s/(?<=[^\}])([- ])(wed(?: ?\.)?) /$1\{$2} Wednesday /g;
      s/(?<=[^\}])([- ])(thu(?: ?\.)?) /$1\{$2} Thursday /g;
      s/(?<=[^\}])([- ])(fri(?: ?\.)?) /$1\{$2} Friday /g;
      s/(?<=[^\}])([- ])(sun(?: ?\.)?) /$1\{$2} Sunday /g;
      s/(?<=[^\}])([- ])(no ?\.) /$1\{$2} number /g;
      s/(?<=[^\}])([- ])(yr(?: ?\.)?) /$1\{$2} year /g;
      s/(?<=[^\}])([- ])(abt) /$1\{$2} about /g;
      s/(?<=[^\}])([- ])(jr(?: ?\.)?) /$1\{$2} junior /g;
      s/(?<=[^\}])([- ])(jnr(?: ?\.)?) /$1\{$2} junior /g;
      s/(?<=[^\}])([- ])(mo(?: ?\.)?) /$1\{$2} month /g;
      s/(?<=[^\}])([- ])(mos(?: ?\.)?) /$1\{$2} months /g;
      s/(?<=[^\}])([- ])(sr(?: ?\.)?) /$1\{$2} senior /g;
      s/(?<=[^\}])([- ])(co-op) /$1\{$2} cooperative  /g;
      s/(?<=[^\}])([- ])(co(?: ?\.)?) /$1\{$2} company /g;
      s/(?<=[^\}])([- ])(cond(?: ?\.)?) /$1\{$2} condition  /g;
      s/(?<=[^\}])([- ])(corp(?: ?\.)?) /$1\{$2} corporation  /g;
      s/(?<=[^\}])([- ])(dba(?: ?\.)?) /$1\{$2} doing {$2} business {$2} as /g;
      s/(?<=[^\}])([- ])(dbl(?: ?\.)?) /$1\{$2} double /g;
      s/(?<=[^\}])([- ])(ea(?: ?\.)?) /$1\{$2} each  /g;
      s/(?<=[^\}])([- ])(inc(?: ?\.)?) /$1\{$2} incorporated /g;
      s/(?<=[^\}])([- ])(int'l) /$1\{$2} international /g;
      s/(?<=[^\}])([- ])(ltd) /$1\{$2} limited  /g;
      s/(?<=[^\}])([- ])(m-f(?: ?\.)?) /$1\{$2} Monday {$2} through {$2} Friday  /g;
      s/(?<=[^\}])([- ])(misc(?: ?\.)?) /$1\{$2} miscellaneous  /g;
      s/(?<=[^\}])([- ])(msg(?: ?\.)?) /$1\{$2} message  /g;
      s/(?<=[^\}])([- ])(spd(?: ?\.)?) /$1\{$2} Speed /g;
      s/(?<=[^\}])([- ])(w(?: ?\. ?)?r(?: ?\. ?)?t(?: ?\.)?) /$1\{$2} with {$2} respect {$2} to /g;
      s/(?<=[^\}])([- ])(e(?: ?\. ?)?g(?: ?\.)?) /$1\{$2} e.g. /g;
      s/(?<=[^\}])([- ])(i(?: ?\. ?)?e(?: ?\.)?) /$1\{$2} i.e. /g;
      s/(?<=[^\}])([- ])(ibid(?: ?\.)?) /$1\{$2} ibidem /g;
      s/(?<=[^\}])([- ])(pb(?: ?\.)?) /$1\{$2} problem /g;

      # fautes courantes
      s/(?<=[^\}])([- ])(today)(s) /$1\{$2$3} $2 {$2$3} '$3 /goi;

      s/(?<=[^\}]) (i) / \{$1} I /go;

      s/(?<=[^\}])([- ])([Rr])(enum[ea]ration) /$1\{$2$3} $2muneration /go;
      s/(?<=[^\}])([- ])([Aa]t)(le?ast|most|all) /$1\{$2$3} $2 {$2$3} $3 /go;
      s/(?<=[^\}])([- ])(wright) /$1\{$2} right /go;
      s/(?<=[^\}])([- ])(Wright) /$1\{$2} Right /go;
      s/(?<=[^\}])([- ])(Objectie)(s?) /$1\{$2$3} Objective$3 /go;
      s/(?<=[^\}])([- ])(do) (note) /$1 $2 \{$3} ___not /go;
      s/ ___not (,|that|the|a) / note $1 /go;
      s/ ___not / not /go;
    }

    # re-collage des points appartenant à des abréviations
    if ($no_af != 1) {
      if ($lang eq "fr") {
	s/(?<=[^a-zàâäéêèëîïöôùûüÿçA-ZÀÉÈÊËÂÄÔÖÛÜÇ\_\-])$abrp_re(\s+[^\s])/{$1} get_normalized_pctabr($1).$2/ge;
	s/(?<=[^a-zàâäéêèëîïöôùûüÿçA-ZÀÉÈÊËÂÄÔÖÛÜÇ\_\-])$abrp_fin_re(\s|$)/{$1} get_normalized_pctabr($1).$2/ge;
      } elsif ($lang !~ /^(ja|zh|tw|th)$/) {
	s/(?<=[^a-záä±æèïéêìëíåµ³ñòóôöàø¶¹\»úùüı¼¿A-ZÁÄ¡ÆÈÏÉÊÌËÍÅ£¥ÑÒÓÔÖÀØ¦©\«ÚÙÜİ¬¯\_\-\s])$abrp_re(\s+[^\s])/{$1} get_normalized_pctabr($1).$2/ge;
	s/(?<=[^a-záä±æèïéêìëíåµ³ñòóôöàø¶¹\»úùüı¼¿A-ZÁÄ¡ÆÈÏÉÊÌËÍÅ£¥ÑÒÓÔÖÀØ¦©\«ÚÙÜİ¬¯\_\-\s])$abrp_fin_re(\s|$)/{$1} get_normalized_pctabr($1).$2/ge;
      }
    }

    if ($lang !~ /^(ja|zh|tw|th)$/) {
      # abréviations en point qui sont en fin de phrase
      # _UNSPLIT_ a pour effet que les tokens (dans les commentaires) associés
      # à la ponctuation finale seront les mêmes que ceux associés à la forme précédente,
      # i.e. à l'abréviation:
      # echo "adj." | sxpipe    donne:
      # {<F id="E1F1">adj</F> <F id="E1F2">.</F>} adj. {<F id="E1F1">adj</F> <F id="E1F2">.</F>} .
      # c'est tok2cc/rebuild_easy_tags.pl qui fait ce travail
      # on ne le fait que si l'abréviation concernée a le droit de terminer une phrase,
      # ce qui est indiqué dans le lexique par le fait qu'elle se termine par 2 points (!)
      s/(?<=[^\.\s\_])(\.\_FINABR\.*) *$/\. _UNSPLIT_$1/;

      # Il faut maintenant gérer les abrév reconnues dans une entnom (type "{{godz .} godz. 16} _TIME")
      if ($no_sw) {
	while (s/(\{[^\{\}]*)\{([^\{\}]*)\} [^ ]+/$1$2/g) {
	}
      } else {
	while (s/(\{[^\{\}]*)\{[^\{\}]*\} ([^ ]+)/$1$2/g) {
	}
      }
      s/  */ /g;
      s/^ //;
    }

    # Détachements particuliers
    if ($lang eq "pl") {
      if ($no_sw) {
	s/(^|\s)(przeze|na|za|do|ode|dla|we)(ñ)(\s|$)/$1\{$2$3\} $2 \{$2$3\} _$3$4/g;
      } else {
	s/(^|\s)(przeze|na|za|do|ode|dla|we)(ñ)(\s|$)/$1$2 \{$3\} _$3$4/g;
      }
    }

    # SEGMENTATION en phrases si pas d'option -no_s
    # ---------------------------------------------
    # on identifie de toute façon les frontières de phrases
    # - si -no_s, on les indique par un espace
    # - sinon, on en a de 2 types: 
    #     * celles repérées par #__# sont remplacées par un retour-chariot,
    #     * celles reprérées par #_# sont remplacées par $sent_bound, 
    #       qui vaut retour-chariot par défaut mais qui peut être redéfini 
    #       par -sb=XXX (souvent, XXX = _SENT_BOUND)
    s/ +\t/\t/g; s/\t +/\t/g;
    s/  +/ /g;
    s/\t\t+/\t/g;

    if ($lang !~ /^(ja|zh|tw|th)$/) {    
      s/([\.:;\?\!])\s*([\"\½]\s*(?:$maj|[\[_\{])[^\"\½]*[\.:;\?\!]\s*[\"\½])\s*(\s$maj|[\[\{])/$1\#\_\#$2\#\_\#$3/g; # détection de phrases entières entre dbl-quotes
      s/(?<=\s)(\.\s*_UNDERSCORE)/{$1} ./go; # ad hoc pour mondediplo et ses underscores de fin d'article (?)
      #    $special_split = ($split_before_ne) ? qr/[\{\[_]/ : qr/[\[_]/;
      $special_split = qr/[\{\[_]/;
      if (!$no_sw) {
	s/(?<=[^\.])(\.\.*)\s*(\(\.\.\.\))\s($maj|[\[_\{\.])/ $1\#\_\#$2\#\_\#$3/g;
	s/([^\.][0-9\}\s]\.\.*)\s*($initialclass|$special_split)/$1\#\_\#$2/g; # CAS STANDARD
	s/($l|[\!\?])(\s*\.\.\.)\s($l|$special_split)/$1$2\#\_\#$3/g;
	s/(\.\s+\.\.*)\s*($maj|$special_split)/$1\#\_\#$2/g;
	s/\_FINABR\s*($initialclass|$special_split)/ _UNSPLIT_.\#\_\#$1/g;
      } else {
	s/(?<=\s)(\.\.*)(\s+\[\.\.\.\])(\s+$maj|[\[_\{\.])/$1\#\_\#$2\#\_\#$3/g;
	s/([^\.]\s+\.\.*)(\s+$initialclass|$special_split)/$1\#\_\#$2/g; # CAS STANDARD
	s/($l|[\!\?])(\s*\.\.\.*)(\s+(?:$l|$special_split))/$1$2\#\_\#$3/g;
	s/\_FINABR(\s*$initialclass|$special_split)/ _UNSPLIT_.\#\_\#$1/g;
      }
      s/(\.\s*\.+)(\s+$initialclass|[\[_\{\-\«])/$1\#\_\#$2/g;	 # attention !!!
      s/([\?\!]\s*\.*)(\s+$initialclass|[\[_\{\-\«])/$1\#\_\#$2/g; # attention !!!
      s/([\.\?\!]\s*\.\.+)(\s+)/$1\#\_\#$2/g;			   # attention
      s/([\.\?\!,:])(\s+[\-\+\«])/$1\#\_\#$2/g;			   # attention
      if ($weak_sbound) {
	s/(:\s*\.*)(\s+$initialclass|[\[_\{\-\«])/$1\#\_\#$2/g; # attention !!!
	s/(:\s*\.\.+)(\s+)/$1\#\_\#$2/g;			# attention
      }
    }

    s/(?<!TA_TEXTUAL_PONCT|_META_TEXTUAL_GN)(\s+\{[^\}]*\} _META_TEXTUAL)/\#\_\#$1/g;	# attention

    if ($lang !~ /^(ja|zh|tw|th)$/) {    
      if ($best_recall) {
	s/(,)(\s+[\-\+])/$1\#\_\#$2/g; # attention
      }
      while (s/^((?:[^\"]*[\"\½][^\"]*[\"\½])*[^\"]*\.)(\s+[\"\½])/$1\#\_\#$2/g) {
      }											 # attention
      while (s/^([^\"]*[\"\½](?:[^\"]*[\"\½][^\"]*\")*[^\"]*\.\s+[\"\½])\s+/$1\#\_\#/g) {
      }				# attention

      if ($weak_sbound) {
	s/(\s+);(\s+)/$1;\#\_\#$2/g; # les points-virgules sont des frontières de phrases ($sent_bound à la sortie, qui peut être retour chariot)
      }
    }

    s/$/\#\__\#/; # tout retour chariot dans le source est une frontière de paragraphe (retour chariot à la sortie)

    if ($lang !~ /^(ja|zh|tw|th)$/) {    
      # si on a détecté une frontière de phrase, tout point+ qui la précède est à isoler
      if (!$no_sw) {
	s/([^\.\{_])(\.+\s*)\#\_\#/$1 $2\#_\#/g;
      } else {
	s/(\s\.+)\s*\#\_\#/$1\#_\#/g;
	s/([^\.\s\{_])(\.+)\s*\#\_\#/$1 _REGLUE_$2\#_\#/g;
      }
    }
    if ($toksent) {			    # si on nous demande de segmenter en phrases
      while (s/(\{[^\}]*)\#\_\#/$1 /g) {
      }		      # attention: ces lignes ne gèrent (pour l'instant) pas les profondeurs
      while (s/(\([^\)]*)\#\_\#/$1 /g) {
      }		       #   de parenthèses supérieures à 1. Si donc on a "( ( ) #_# )",
      #	while (s/(\[[^\]]*)\#\_\#/$1 /g) {} #   il y aura une erreur.
      s/\s*\#\_\#\s*/ $sent_bound /g;
      s/\s*\#\__\#\s*/\n/g;
    } else {
      s/\#\_+\#/ /g;
      s/$/\n/;
    }
    s/\_FINABR//g;
    s/ +/ /g;
    s/(^|\n) +/\1/g;
    s/ +$//;

    if (/( - .*){8,}/) { # à partir de 8 (choisi au plus juste), on va considérer qu'on est face à une liste
      s/ - /\n- /g;
    }

    # sortie
    if ($_!~/^ *$/) { # && $_!~/^-----*$/ && $_!~/^\d+MD\d+$/) {
        if (!$no_sw) {
            s/(\S){/\1 {/g;
	}        
	print "$_";
    }
}

sub get_normalized_pctabr {
  my $s = shift;
  $s =~ s/\s+//g;
  return $rhs_nospace2rhs{$s};
}
