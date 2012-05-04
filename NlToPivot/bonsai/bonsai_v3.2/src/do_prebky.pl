#!/usr/bin/perl

while(<>)
{
    # bky fails if input sentence begins with a white space...
    s/^\s+//g ;
    s/\(/-LRB-/g ; # caution : check consistency with bky grammar
    s/\)/-RRB-/g ;
    # these «» aren't in the FTB, so they cannot be analyzed properly...
    # REM : other ponct are missing in the FTB (_), and are wrongly analyzed...
    tr/«»/""/ ;
    
    print ;
}
	
