#!/usr/bin/perl
# $Id$
$|=1;

binmode STDIN, ":encoding(iso-8859-1)";
binmode STDOUT, ":encoding(iso-8859-1)";
binmode STDERR, ":encoding(iso-8859-1)";

$lang="fr";

$a_star = qr/(?:[-'a-zA-ZÀÁÂÄÅÆÇÈÉÊËÎÏĞÒÔÕÖ×ØÙÚÛÜİßàáâãäåæçèéêëìíîïñòóôõöøùúûüşÿ]*)/o;
$not_a = qr/(?:[^-'a-zA-ZÀÁÂÄÅÆÇÈÉÊËÎÏĞÒÔÕÖ×ØÙÚÛÜİßàáâãäåæçèéêëìíîïñòóôõöøùúûüşÿ])/o;
$ent = qr/(?:\&\#\d+;)/o;
$w_with_ent = qr/(?:$a_star$ent(?:$a_star|$ent)*)/o;

$jap_num_bullets = qr/(?:\&#226;\&#145;\&#16[0-8];)/o;
#$jap_highdot = qr/(?:&#227;&#131;&#187;)/o;
$jap_bigbullet = qr/(?:&#226;&#151;&#143;)/o;
$jap_bullets = qr/(?:^$jap_bigbullet(?!$jap_bigbullet)|(?<!$jap_bigbullet)$jap_bigbullet$|(?<!$jap_bigbullet)$jap_bigbullet(?!$jap_bigbullet))/o;
$independent_entity = qr/(?:$jap_num_bullets|$jap_bullets)/o;

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
    # la regexp ci-dessous, quoique bonne, segfaulte sur un exemple en japonais.
    # On la remplace donc par les 4 regexp qui suivent, qui font le même travail par étapes.
    #s/($w_with_ent+(?:\s+$w_with_ent+)*)/{$1}_ETR/go;

    s/(?<=$not_a)($w_with_ent+)(?=$not_a)/\{\{$1\}\}/go;
    s/($independent_entity)/}}{_{\1}_} {{/go;
    s/{{}}//g;
    s/}} {{/ /g;
    s/{_?{/ {/g;
    s/}_?}/}_ETR /g;

#    s/_ETR([^ {]+)/_ETR _UNSPLIT_\1 /g;

    # sortie
    s/^ //o;
    s/ $//o;
    print "$_\n";
}
