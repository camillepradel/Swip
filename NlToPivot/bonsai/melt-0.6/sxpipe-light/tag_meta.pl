#!/usr/bin/perl
# $Id: tag_meta.pl 2215 2009-03-12 09:51:47Z sagot $
$| = 1;

while (<>) {
  chomp;
  s/^\s*(<[a-zA-Z]+(?: .*?)?>)\s*\n/{$1} _XML\n/g;
  s/^\s*(<\/[a-zA-Z]+>)\s*\n/{$1} _XML\n/g;
  if (/(^| )(_XML|_MS_ANNOTATION|_PAR_BOUND) *\n?$/) {
    print "$_\n";
    next;
  }
  s/_/_UNDERSCORE/g;
  s/{/_ACC_O/g;
  s/}/_ACC_F/g;
  s/</&lt;/g;
  s/>/&gt;/g;
  s/´/'/g;
  s/’/'/g;
  s/\t/   /g;
  s/&#227;&#128;&#130;([^ ]|$)/&#227;&#128;&#130; \1/g; # le point en japonais: 。 (sinon gl_entities ne passe pas sur les paragraphes trop longs)
  print "$_\n";
}
