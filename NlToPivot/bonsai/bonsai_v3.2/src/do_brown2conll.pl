#!/usr/bin/perl

# Marie Candito
# IN  = tagged file in Brown format, tagset FTB4
# OUT = conll format, fine-grained column   = input POS
#                     coarse-grained column = French Treebank coarser tagset 
#                     (N, V, A, D, P, ADV, ET, I, PONCT, CL, C, PRO)
 
%ftb4_to_ftbmin = (
    'V'=>'V',
    'VIMP'=>'V',
    'VS'=>'V',
    'VPP'=>'V',
    'VPR'=>'V',
    'VINF'=>'V',
    'NC'=>'N',
    'NPP'=>'N',
    'CS'=>'C',
    'CC'=>'C',
    'CLS'=>'CL',
    'CLO'=>'CL',
    'CLR'=>'CL',
    'CLS'=>'CL',
    'ADJ'=>'A',
    'ADJWH'=>'A',
    'ADV'=>'ADV',
    'ADVWH'=>'ADV',
    'PRO'=>'PRO',
    'PROWH'=>'PRO',
    'PROREL'=>'PRO',
    'DET'=>'D',
    'DETWH'=>'D',
    'P+D'=>'P+D',
    'P+PRO'=>'P+PRO',
    ) ;

while(<>)
{
    chomp ;
    @tokens = split(' ') ;
    $c = 0 ;
    map 
    { 
	$i = rindex($_,'/') ;
	$form = substr($_,0,$i) ;
	$ftag = substr($_,$i+1) ;
	$c++;
	$ctag = (defined($ftb4_to_ftbmin{$ftag})) ? $ftb4_to_ftbmin{$ftag} : $ftag ;

	print join("\t", ($c,$form,'_',$ctag,$ftag,'_','_','_','_','_'))."\n" ; 
    } @tokens ;
    print "\n" ;
}
