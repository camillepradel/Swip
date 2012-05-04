#!/usr/bin/perl

# Marie Candito

use Getopt::Long;
$help = 0 ;
$outsep='space' ;
$dumpregexp = 0 ;
GetOptions("notnum!" => \$notnum,
	   "cmpdfile:s" => \$cmpdfile,
	   "dumpregexp!" => \$dumpregexp,
           "help!" => \$help) or die (&usage) ;

die(&usage) unless ($outsep eq 'newline' or $outsep eq 'space') ;
die(&usage) if ($help) ;

# DEBUG TODO : rajouter 'au_nombre_de'

sub usage
{
    return "Matches compounds in a tokenized text.
For recognized compounds, the space that separates the components is replaced by a _
Compounds are taken from a built-in list, or from a file
And numerical expressions are recognized as compounds (200 000 => 200_000)
$0 
[-notnum] do not transform 200 000 into 200_000 (Default=none)
[-cmpdfile = FILE containing normalised forms of compounds. If set, compounds are loaded from this file, instead of from the default compound list. Default=none]
[-dumpregexp] If set, the script compiles and dump the regexp used for compound recognition. Default=false
[ -help ]\n" ;
}

if ($dumpregexp)
{
    # use Regexp::List only when dumping the regexp
    # => otherwise Regexp::List needs not be installed
    eval("use Regexp::List") ;

    # -------------------------------------
    # default compounds
    @Compounds = (
	'a fortiori',
	'a posteriori',
	'a priori',
	'à fortiori',
	'à posteriori',
	'à priori',
	'afin de',
	'afin que',
	'ainsi de suite',
	'ainsi que',
	'alors même que',
	'alors que',
	'après que',
	'au bout de',
	'au contraire',
	'au cours de',
	'au détriment de',
	'au fil de',
	'au fur et à mesure',
	'au fur et à mesure que',
	'au jour le jour',
	'au lieu de',
	'au moins',
	'au moment où',
	'au niveau de',
	'au nom de',
	'au prix de',
	'au profit de',
	'au sein de',
	'au total',
	'au vu de',
	'au-delà de',
	'au-dessous de',
	'au-dessus de',
	'au delà de',
	'au dessous de',
	'au dessus de',
	'aujourd\' hui',
	'auprès de',
	'autour de',
	'autrement dit',
	'aux alentours de',
	'avant de',
	'avant même',
	'avant que',
	'bel et bien',
	'bien que',
	'bien sûr',
	'bon an mal an',
	'c\' est pourquoi',
	'c\' est-à-dire',
	'comme si',
	'compte tenu de',
	'contrairement à',
	'd\' abord',
	'd\' accord',
	'd\' ailleurs',
	'd\' après',
	'd\' autant',
	'd\' autant plus',
	'd\' autant plus que',
	'd\' autant que',
	'd\' autre part',
	'd\' autres',
	'd\' emblée',
	'd\' entre',
	'd\' ici',
	'd\' ici à',
	'd\' ores et déjà',
	'd\' où',
	'd\' une part',
	'dans ces conditions',
	'dans l\' immédiat',
	'dans la mesure où',
	'dans le cadre de',
	'dans le domaine de',
	'dans le passé',
	'dans son ensemble',
	'de base',
	'de facto',
	'de fait',
	'de l\' ordre de',
	'de la part de',
	'de loin',
	'de longue date',
	'de moins en moins',
	'de même que',
	'de nature à',
	'de nouveau',
	'de part et d\' autre',
	'de plus en plus',
	'de surcroît',
	'depuis peu',
	'depuis que',
	'du coup',
	'du fait de',
	'du moins',
	'dès lors',
	'dès lors que',
	'dès que',
	'en baisse',
	'en cas de',
	'en cause',
	'en ce qui concerne',
	'en ce sens',
	'en commun',
	'en compte',
	'en cours de',
	'en dehors de',
	'en dépit de',
	'en faveur de',
	'en fonction de',
	'en grande partie',
	'en général',
	'en hausse',
	'en jeu',
	'en l\' occurrence',
	'en la matière',
	'en matière de',
	'en moyenne',
	'en même temps',
	'en même temps que',
	'en outre',
	'en particulier',
	'en partie',
	'en place',
	'en plus',
	'en principe',
	'en question',
	'en raison de',
	'en revanche',
	'en réalité',
	'en tant que',
	'en termes de',
	'en tout cas',
	'en train de',
	'en vertu de',
	'en vigueur',
	'en voie de',
	'en vue de',
	'entre autres',
	'entre autres choses',
	'et même',
	'et puis',
	'ex aequo',
	'face à',
	'grosso modo',
	'grâce à',
	'hors de',
	'il est vrai',
	'il y a',
	'in extremis',
	'jusqu\' alors',
	'jusqu\' en',
	'jusqu\' ici',
	'jusqu\' à',
	'jusqu\' à présent',
	'la plupart',
	'le cas échéant',
	'le plus souvent',
	'loin de',
	'lors de',
	'mot à mot',
	'même si',
	'ne pas',
	'ne plus',
	'nombre de',
	'non plus',
	'non seulement',
	'nulle part',
	'ou bien',
	'par ailleurs',
	'par exemple',
	'par la suite',
	'par rapport à',
	'parce que',
	'pas encore',
	'pendant que',
	'petit à petit',
	'peu à peu',
	'plus ou moins',
	'plus tard',
	'plutôt que',
	'pour autant',
	'pour de bon',
	'pour l\' heure',
	'pour l\' instant',
	'pour la première fois',
	'pour que',
	'près de',
	'quant à',
	'quelqu\' un',
	'quelque chose',
	'quelque peu',
	'qui plus est',
	'quitte à',
	'sans doute',
	'sans que',
	'sous forme de',
	'sur place',
	'tandis que',
	'tour à tour',
	'tout au long de',
	'tout d\' abord',
	'tout de même',
	'tout de suite',
	'tout à coup',
	'tout à fait',
	'tout à l\' heure',
	'un petit peu',
	'un peu',
	'un peu plus',
	'un tant soit peu',
	'une fois',
	'une sorte de',
	'vis-à-vis de',
	'y compris',
	'à cause de',
	'à ce sujet',
	'à cet égard',
	'à compter de',
	'à condition que',
	'à court terme',
	'à hauteur de',
	'à l\' encontre de',
	'à l\' exception de',
	'à l\' issue de',
	'à l\' occasion de',
	'à l\' égard de',
	'à l\' étranger',
	'à la fois',
	'à la suite de',
	'à la tête de',
	'à long terme',
	'à mesure que',
	'à moitié',
	'à moyen terme',
	'à nouveau',
	'à partir de',
	'à peine',
	'à propos de',
	'à terme',
	'à travers',
	'à venir',
	'à vrai dire',
	'ça et là ',
	) ;

# normalise compounds
    @Compounds = map
    {
	if (/(.+?) que$/)
	{
	    ($1.' que', $1.' qu\'') ;
	}
	elsif (/(.+?) de$/)
	{
	    ($1.' de', $1.' d\'', $1.' du', $1.' des') ;
	}
	elsif (/(.+?) si$/)
	{
	    ($1.' si', $1.' s'."'") ;
	}
	elsif (/(.+?) à$/)
	{
	    ($1.' à', $1.' au', $1.' aux') ;
	}
	else
	{
	    $_ ;
	}
	#s/ que$/ qu[e\']/g ;
	#s/ de$/ d(?:es?|u|\')/g ;
	#s/ si$/ s[i\']/g ;
    } @Compounds ;
    
# build a regexp for these compounds
# TODO => handle compounds that are substrings of others ...


# an optimized regexp 
    $o  = Regexp::List->new;
    $reCompounds = $o->set(modifiers => 'i')->list2re(@Compounds) ;
    print "$reCompounds\n" ;
}
else
{
    $reCompounds = qr/(?-xism:(?i:(?=[\à\çabcdefghijlmnopqstuvy])(?:a(?:\ (?:p(?:oste)?riori|fortiori)|fin\ (?:d(?:es?|[\'u])|qu[e\'])|insi\ (?:qu[e\']|de\ suite)|lors\ (?:m\ême\ qu[e\']|qu[e\'])|pr\ès\ qu[e\']|u(?:\ (?:bout\ d(?:es?|[\'u])|co(?:urs\ d(?:es?|[\'u])|ntraire)|d(?:\étriment\ d(?:es?|[\'u])|e(?:l\à\ d(?:es?|[\'u])|ss(?:ous\ d(?:es?|[\'u])|us\ d(?:es?|[\'u]))))|f(?:il\ d(?:es?|[\'u])|ur\ et\ \à\ mesure(?:\ qu[e\'])?)|lieu\ d(?:es?|[\'u])|mo(?:ins|ment\ o\ù)|n(?:iveau\ d(?:es?|[\'u])|om\ d(?:es?|[\'u]))|pr(?:ix\ d(?:es?|[\'u])|ofit\ d(?:es?|[\'u]))|sein\ d(?:es?|[\'u])|vu\ d(?:es?|[\'u])|jour\ le\ jour|total)|\-de(?:l\à\ d(?:es?|[\'u])|ss(?:ous\ d(?:es?|[\'u])|us\ d(?:es?|[\'u])))|pr\ès\ d(?:es?|[\'u])|t(?:our\ d(?:es?|[\'u])|rement\ dit)|x\ alentours\ d(?:es?|[\'u])|jourd\'\ hui)|vant\ (?:d(?:es?|[\'u])|qu[e\']|m\ême))|\à\ (?:p(?:r(?:opos\ d(?:es?|[\'u])|iori)|artir\ d(?:es?|[\'u])|osteriori|eine)|c(?:ause\ d(?:es?|[\'u])|e(?:\ sujet|t\ \égard)|o(?:mpter\ d(?:es?|[\'u])|ndition\ qu[e\']|urt\ terme))|hauteur\ d(?:es?|[\'u])|l(?:\'\ (?:e(?:ncontre\ d(?:es?|[\'u])|xception\ d(?:es?|[\'u]))|issue\ d(?:es?|[\'u])|occasion\ d(?:es?|[\'u])|\é(?:gard\ d(?:es?|[\'u])|tranger))|a\ (?:suite\ d(?:es?|[\'u])|t\ête\ d(?:es?|[\'u])|fois)|ong\ terme)|m(?:esure\ qu[e\']|o(?:iti\é|yen\ terme))|t(?:erme|ravers)|v(?:enir|rai\ dire)|fortiori|nouveau)|b(?:ien\ (?:qu[e\']|s\ûr)|(?:el\ et\ bie|on\ an\ mal\ a)n)|c(?:\'\ est(?:\ pourquoi|\-\à\-dire)|o(?:m(?:me\ s[i\']|pte\ tenu\ d(?:es?|[\'u]))|ntrairement\ (?:aux?|\à)))|d(?:\'\ (?:a(?:ut(?:ant(?:\ (?:plus(?:\ qu[e\'])?|qu[e\']))?|re(?:\ part|s))|(?:b|cc)ord|(?:illeur|pr\è)s)|e(?:mbl\é|ntr)e|ici(?:\ (?:aux?|\à))?|o(?:res\ et\ d\éj\à|\ù)|une\ part)|ans\ (?:l(?:e\ (?:cadre\ d(?:es?|[\'u])|domaine\ d(?:es?|[\'u])|pass\é)|\'\ imm\édiat|a\ mesure\ o\ù)|ces\ conditions|son\ ensemble)|e(?:\ (?:fa(?:cto|it)|l(?:\'\ ordre\ d(?:es?|[\'u])|a\ part\ d(?:es?|[\'u])|o(?:in|ngue\ date))|m(?:\ême\ qu[e\']|oins\ en\ moins)|n(?:ature\ (?:aux?|\à)|ouveau)|p(?:art\ et\ d\'\ autre|lus\ en\ plus)|base|surcro\ît)|puis\ (?:qu[e\']|peu))|u\ (?:fait\ d(?:es?|[\'u])|coup|moins)|\ès\ (?:lors(?:\ qu[e\'])?|qu[e\']))|e(?:n(?:\ (?:c(?:a(?:s\ d(?:es?|[\'u])|use)|e\ (?:qui\ concerne|sens)|o(?:m(?:mun|pte)|urs\ d(?:es?|[\'u])))|d(?:ehors\ d(?:es?|[\'u])|\épit\ d(?:es?|[\'u]))|f(?:aveur\ d(?:es?|[\'u])|onction\ d(?:es?|[\'u]))|g(?:rande\ partie|\én\éral)|l(?:\'\ occurrenc|a\ mati\èr)e|m(?:ati\ère\ d(?:es?|[\'u])|\ême\ temps(?:\ qu[e\'])?|oyenne)|p(?:arti(?:culier|e)|l(?:ace|us)|rincipe)|r(?:aison\ d(?:es?|[\'u])|evanche|\éalit\é)|t(?:ant\ qu[e\']|ermes\ d(?:es?|[\'u])|rain\ d(?:es?|[\'u])|out\ cas)|v(?:ertu\ d(?:es?|[\'u])|oie\ d(?:es?|[\'u])|ue\ d(?:es?|[\'u])|igueur)|(?:(?:bai|hau)ss|outr)e|jeu|question)|tre\ autres(?:\ choses)?)|t\ (?:m\ême|puis)|x\ aequo)|face\ (?:aux?|\à)|gr(?:\âce\ (?:aux?|\à)|osso\ modo)|hors\ d(?:es?|[\'u])|i(?:l\ (?:est\ vrai|y\ a)|n\ extremis)|jusqu\'\ (?:a(?:ux?|lors)|\à(?:\ pr\ésent)?|en|ici)|l(?:e\ (?:cas\ \éch\éa|plus\ souve)nt|o(?:in\ d(?:es?|[\'u])|rs\ d(?:es?|[\'u]))|a\ plupart)|m(?:\ême\ s[i\']|ot\ \à\ mot)|n(?:e\ p(?:a|lu)s|o(?:mbre\ d(?:es?|[\'u])|n\ (?:plus|seulement))|ulle\ part)|p(?:a(?:r(?:\ (?:rapport\ (?:aux?|\à)|ailleurs|(?:exempl|la\ suit)e)|ce\ qu[e\'])|s\ encore)|e(?:ndant\ qu[e\']|tit\ \à\ petit|u\ \à\ peu)|lu(?:s\ (?:ou\ moins|tard)|t\ôt\ qu[e\'])|our\ (?:l(?:\'\ (?:heure|instant)|a\ premi\ère\ fois)|qu[e\']|autant|de\ bon)|r\ès\ d(?:es?|[\'u]))|qu(?:ant\ (?:aux?|\à)|elqu(?:e\ (?:chose|peu)|\'\ un)|i(?:tte\ (?:aux?|\à)|\ plus\ est))|s(?:ans\ (?:qu[e\']|doute)|ous\ forme\ d(?:es?|[\'u])|ur\ place)|t(?:andis\ qu[e\']|ou(?:t\ (?:au\ long\ d(?:es?|[\'u])|d(?:e\ (?:m\êm|suit)e|\'\ abord)|\à\ (?:coup|fait|l\'\ heure))|r\ \à\ tour))|un(?:\ (?:pe(?:u(?:\ plus)?|tit\ peu)|tant\ soit\ peu)|e\ (?:sorte\ d(?:es?|[\'u])|fois))|vis\-\à\-vis\ d(?:es?|[\'u])|ou\ bien|y\ compris|\ça\ et\ l\à\ )))/ ;
}

# add numerical exp.
$reCompounds .= '|[0-9]+(?: [0-9]+)+' unless ($notnum) ;

# -------------------------------------
while(<>) 
{
    chomp ;

    $current = $_ ;
    # TODO : faire un mathc avec /g et pos (plus efficace)
    # CAUTION : very much faster if $reCompounds searched only after blanks
    while ($current =~ /(^|.*? )($reCompounds)( .*|$)/i)#(?> |$)(.*)$/i)
    {
	print $1 ;
	$c = $2 ;
	$current = $3 ;
	#print "FOUND:$c:" ;
	$c =~ s/ /_/go ;
	# hack pour normaliser au (delà/dessous/dessus) avec ou sans tiret
	$c =~ s/^au_de/au-de/go ;
	$c =~ s/^à_(priori|fortiori|posteriori)/a_$1/go ;
	print $c ;
    }
    print $current."\n" ;
}
