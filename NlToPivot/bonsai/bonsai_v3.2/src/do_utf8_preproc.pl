#!/usr/bin/perl

# Punctuation not in latin1 are a problem...

# pour s'amuser...
# le iconv va convertir les caractères non iso-latin en 'x'
# mais on peut vouloir en amont maîtriser la conversion de certains caractères
while(<>)
{
    foreach $i ('В','М','С','О','А','К','ф','П','♇','Ⅹ','γ','Υ','Γ','Δ','Τ','Λ','Π')
    {
	s/$i/X/g ;
    }
#Inutile avec iconv (et --unicode-subst=x), utile avec recode
    foreach $i ('д','́','‎','б','н','к','р','м','я','ч','е','с','л','а','в','и','х','й','о','т')
    {
	s/$i/x/g ;
    }
# inutile avec recode, mais utile avec iconv
    s/€/EUR/g ;
    s/Œ/OE/g ;
    s/œ/oe/g ;
    s/—/-/g ;
    s/–/-/g ;
    s/’/'/g ;
    s/…/.../g ;
    s/‘/'/g ;
    s/“/"/g ;
    s/Š/S/g ;
    s/Č/C/g ;
    s/Ė/E/g ;
    s/Ž/Z/g ;
    s/ļ/l/g ;

    s/≈/=/g ;
    s/⁄/\//g ;
    s/•/./g ;
    s/╗/"/g ;
    s/▒/"/g ;
    s/█/"/g ;
    s/╩/"/g ;
    s/▄/"/g ;
    s/▓/"/g ;
    print ;
}
