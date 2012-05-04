#!/usr/bin/perl

# For texts (such as WikiSource) where newlines appear within sentences
# => don't write newlines between two non-empty lines, if the second one start with a lower case char

$previous_is_empty_line = 0 ;
$first = 1 ;
while(<>)
{
    chomp ;
    s/^\s+// ;
    print (($previous_is_empty_line or !(/^[a-zיטךכאגהשûüמןפצ]/)) ? "\n" : (($first) ? '' : " ")) ;
    $previous_is_empty_line = ($_) ? 0 : 1 ;
    print ;
    $first = 0 ;
}
print "\n" unless ($first) ;
