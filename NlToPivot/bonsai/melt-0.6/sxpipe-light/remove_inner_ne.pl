#!/usr/bin/perl
# $Id: remove_inner_ne.pl 2034 2008-08-21 13:59:32Z sagot $

$| = 1;

while (<>) {
  chomp;
  if (/ (_XML|_MS_ANNOTATION|_PAR_BOUND) *$/) {
    print "$_\n";
    next;
  }

  s/\} +/\}/g;
  @c=split(//,$_);
  $depth=0;
  $ok=0;
  $output="";
  for (@c) {
    if ($_ eq '{') {
      if ($depth==0) {
	$t .= "{";
      }
      $ok=1;
      $depth++;
    } elsif ($_ eq '}') {
      $depth--;
      if ($depth==0) {
	$t .= "} ";
	$t=~s/ \} $/\} /;
      }
      $output .= $t;
      $ok=0;
      $t=""; # changement ; étaiit $t=" ";
    } elsif ($_=~/^\s$/) {
      if ($depth==0) {
	$output .= $_;
      } elsif ($t!~/^\s*$/ || $ok==1) {
	$t .= $_;
      } else {
	$t .= $_; # ajout
	$ok=1;
      }
    } elsif ($depth==0) {
      $output .= $_;
    } elsif ($t!~/^\s*$/ || $ok==1) {
      $t .= $_;
    }
  }
  $output=~s/ +/ /g;
  $output=~s/^ //g;
  $output=~s/ $//g;
  print "$output\n";
}
