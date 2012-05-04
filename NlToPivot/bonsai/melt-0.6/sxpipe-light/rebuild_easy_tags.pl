#!/usr/bin/perl
# $Id: rebuild_easy_tags.pl 2034 2008-08-21 13:59:32Z sagot $

$| = 1;

$e=0;

# Construction des commentaires au format XML issu de Easy, i.e. { <F id=\"E$iF$j\">token</F> }
# En entrée, chaque mot peut ne pas avoir de commentaire, ou avoir un commentaire non-XML: {token} mot
# Il peut y avoir plusieurs tokens dans un même commentaire : { <F id=\"E$iF$j\">token1</F> <F id=\"E$iF$j+1\">token2</F> <F id=\"E$iF$j+2\">token3</F> }
# Les étapes précédentes ont pu laisser des instructions spéciales, _REGLUE_ et _UNSPLIT_, utilisées ainsi: a _(REGLUE|UNSPLIT)_b
# 1) _REGLUE_ a pour but que les commentaires de a et b (qui sont, s'ils n'en ont pas encore, respectivement a et b) soient collés pour former un token commun:
#            a _REGLUE_b   ==>  { <F id="E1F1">ab</F> } a { <F id="E1F1">ab</F> } b
#            {c} a _REGLUE_b   ==>  { <F id="E1F1">cb</F> } a { <F id="E1F1">cb</F> } b
#            {c} a _REGLUE_b _REGLUE_d  ==>  { <F id="E1F1">cbd</F> } a { <F id="E1F1">cbd</F> } b { <F id="E1F1">cbd</F> } d
# 2) _UNSPLIT_ a pour but de donner à b le même commentaire que celui de a:
#            a _UNSPLIT_b   ==>  { <F id="E1F1">a</F> } a { <F id="E1F1">a</F> } b
#            {c} a _UNSPLIT_b   ==>  { <F id="E1F1">c</F> } a { <F id="E1F1">c</F> } b
#            {c} a _UNSPLIT_b _UNSPLIT_d  ==>  { <F id="E1F1">c</F> } a { <F id="E1F1">c</F> } b { <F id="E1F1">c</F> } d
# 3) exemples mixtes:
#            {a} b _UNSPLIT_c _REGLUE_d ==> { <F id="E1F1">ad</F> } b { <F id="E1F1">ad</F> } c { <F id="E1F1">ad</F> } d
#            {a} b _REGLUE_c _UNSPLIT_d ==> { <F id="E1F1">ac</F> } b { <F id="E1F1">ac</F> } c { <F id="E1F1">ac</F> } d
# Il n'est pas prévu que _(REGLUE|UNSPLIT)_b ait déjà un commentaire à lui. Si c'est le cas, la sortie est incorrecte.

while (<>) {
  chomp;
  if (/ _XML *$/) {
    if (/{\s*<\s*E\s+id\s*=\s*\"E([0-9]+)\"\s*>\s*}/) {
      $e=-$1;
    }
#     s/\>/\&gt;/g;
#     s/\</\&lt;/g;
#     s/^\s*{/{<F id=\"E${e}F1\">/;
#     s/} _XML/<\/F>} _XML/;
    print "$_\n";
    next;
  } elsif (/ (_MS_ANNOTATION|_PAR_BOUND) *$/) {
    print "$_\n";
    next;
  } elsif ($e<0) { # i.e. on vient de lire une ligne _XML qui nous a donné le vrai $e
    $e=-$e;
  } else {
    $e++;
  }

  s/\>/\&gt;/g;
  s/\</\&lt;/g;

  # ajoute un commentaire aux mots non reconnus par les grammaires locales
  s/^\s*([^\{\s][^\s]*)/\{$1\} $1/g;
  while (s/(\}[^\{\}]+\s)([^\{\s][^\s\}]*)/$1\{$2\} $2/g) {
  }

  # supprime les _ (i.e. les séparateurs de easy-sous-mots) à l'extérieur des commentaires
  while (s/(\}\s*[^\{_\s][^\{_]*[^\s\{])\_UNDERSCORE/$1 /g
	 || s/(\}\s*[^\s\{])\_UNDERSCORE/$1 /g) {
  }

  my $f=0;
  my $commentaire=1;
  # remplace les commentaires par les tags easy correspondants
  $tobeprinted="";
  $lasteasytag="";
  $lasteasytagset="";
  for (split(/[\{\}]/,$_)) {
    $commentaire=1-$commentaire;
    s/^ +//g;
    s/ +$//g;
    if ($commentaire) {
      s/_UNDERSCORE/\_/g;
    }
    if ($commentaire && $_!~/^(_REGLUE_|_UNSPLIT_)/) {
      print "$tobeprinted";
      $tobeprinted="";
    }
    if ($commentaire && $_!~/^(_SENT_BOUND$|_REGLUE_|_UNSPLIT_)/) {
      $lasteasytagset="";
      for (split(/(?<=\s)/,$_)) {
	$tab=" ";
	s/ +$//;
	if (s/(?<=[^\s])\s*\t\s*$//) {
	  $tab="\t";
	}
	if (s/^_REGLUE_//) {
	  $lasteasytagset=~s/(<\/F>)(\s)$2$/$_$1$tab/g;
	  $lasteasytag=~s/(<\/F>)(\s)$/$_$1$tab/g;
#était:	  $lasteasytag=~s/(<\/F>)(\s)$/$2$_$1$tab/g;
	} elsif (s/^_UNSPLIT_//) {
	} else {
	  $f++;
	  $lasteasytag="<F id=\"E${e}F$f\">$_</F>$tab";
	  $lasteasytagset.=$lasteasytag;
	}
      }
      if ($lasteasytagset ne "") {
	$tobeprinted="{ $lasteasytagset}";
      }
    } elsif ($commentaire && /^_SENT_BOUND$/) {
      $tobeprinted.="{ $lasteasytag}";
    } elsif ($commentaire) {
	$tab=" ";
	s/ +$//;
	if (s/(?<=[^\s])\s*\t\s*$//) {
	  $tab="\t";
	}
    } elsif (!$commentaire && s/^_REGLUE_//) {
      $tobeprinted=~s/(<\/F>)(\s+)\}/$_$1$tab\}/g;
      $lasteasytag=~s/(<\/F>)(\s+)$/$_$1$tab/g;
      $lasteasytagset=~s/(<\/F>)(\s+)$/$_$1$tab/g;
      $tobeprinted.="{ $lasteasytag} $_ ";
    } elsif (!$commentaire && s/^_UNSPLIT_//) {
      $tobeprinted.="{ $lasteasytagset} $_ ";
    } elsif (!$commentaire) {
      s/ / \{ $lasteasytag\} /g;
      $tobeprinted.=" $_ ";
    }
    }
    print "$tobeprinted\n";
}
