#!/usr/bin/perl

open (LEFFF, "< /usr/local/share/melt/lefff.ftb4tags") || die "Could not open /usr/local/share/melt/lefff.ftb4tags: $!";
while (<LEFFF>) {
  chomp;
  s/ /_/g;
#  s/(\S)-(\S)/\1_-_\2/g;
  /^(.*?)\t(.*?)\t(.*?)$/ || next;
  $form = $1; $cat = $2; $lemma = $3;
  $qmlemma = quotemeta ($lemma);
  if (defined ($cat_form2lemma{$cat}) && defined ($cat_form2lemma{$cat}{$form}) && $cat_form2lemma{$cat}{$form} !~ /(^|\|)$qmlemma(\||$)/) {
    $cat_form2lemma{$cat}{$form} .= "|$lemma";
  } else {
    $cat_form2lemma{$cat}{$form} = "$lemma";
  }
}

%equiv = (
	  "--RBR--" => ")",
	  "--LBR--" => "(",
);

while (<>) {
  s/^ +//;
  s/ +$//;
  @result = ();
  for (split (/ +/, $_)) {
    /^(.*?)\/(.*?)$/ || die "Format error: $_";
    if (defined ($cat_form2lemma{$2}{$1})) {
      push @result, "$1/$2/".$cat_form2lemma{$2}{$1};
    } elsif (defined ($cat_form2lemma{$2}{lc($1)})) {
      push @result, "$1/$2/".$cat_form2lemma{$2}{lc($1)};
    } elsif (defined ($cat_form2lemma{$2}{$equiv{$1}})) {
      push @result, "$1/$2/".$cat_form2lemma{$2}{$equiv{$1}};
    } else {
      push @result, "$1/$2/*".$1;
#      print STDERR "$1\t$2\n";
    }
  }
  print join(" ",@result)."\n";
}
