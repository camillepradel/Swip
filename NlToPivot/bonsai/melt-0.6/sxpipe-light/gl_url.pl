#!/usr/bin/perl
# $Id: gl_url.pl 1853 2008-05-22 11:43:15Z sagot $

# ATTENTION: ‡ exÈcuter APRES gl_email.pl

$| = 1;

$lang="fr";

while (1) {
    $_=shift;
    if (/^$/) {last;}
    elsif (/^-no_sw$/ || /^-no_split-words$/i) {$no_sw=1;}
    elsif (/^-l(?:ang)?=(.*)$/) {$lang=$1;}
}

if ($lang eq "sk") {
    $safeExt     = qr/(?:com|org|edu|gov|net|mil|aero|biz|coop|info|museum|name|pro|int|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|dj|dk|dm|do|dz|ec|ee|eg|eh|er|es|fi|fj|fk|fm|fo|fr|fx|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|um|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw|bo|ci|co|in|im)/o;
    $unsafeExt   = qr/(?:by|do|je|ma|mu|my|na|no|tj|to|za)/o;
} elsif ($lang eq "pl") {
    $safeExt     = qr/(?:com|org|edu|gov|net|mil|aero|biz|coop|info|museum|name|pro|int|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|br|bs|bt|bv|bw|bz|ca|cc|cd|cf|cg|ch|ck|cl|cm|cn|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|dz|ec|ee|eg|eh|er|es|et|eu|fi|fj|fk|fm|fo|fr|fx|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|io|iq|ir|is|it|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mv|mw|mx|mz|nc|ne|nf|ng|ni|nl|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|sv|sy|sz|tc|td|tf|tg|th|tk|tm|tn|tp|tr|tt|tv|tw|tz|ua|ug|uk|um|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|zm|zw)/o;
    $unsafeExt   = qr/(?:bo|by|ci|co|do|im|in|je|ma|mu|my|na|no|tj|to|za)/o;
} else {
    $safeExt     = qr/(?:com|org|edu|gov|net|mil|aero|biz|coop|info|museum|name|pro|int|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|dj|dk|dm|do|dz|ec|ee|eg|eh|er|es|fi|fj|fk|fm|fo|fr|fx|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|um|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw)/o;
    $unsafeExt   = qr/(?:de|et|eu)/o;
}

$protocole = qr/http|ftp|telnet|gopher/o;
$a         = qr/[a-z‡‚‰ÈÍËÎÓÔˆÙ˘˚¸ˇÁµ\ªπæ≥∂ºø±A-Z¿…» À¬ƒ‘÷€‹«•\´©Æ£¶¨Ø°]/o;
$pre       = qr/(?<=[^\'\/\{\@\.\w‡‚‰ÈÍËÎÓÔˆÙ˘˚¸ˇµªπæ≥∂ºø±\-_<])/o;
$post      = qr/(?=[^\'\/\}\w-_‡‚‰ÈÍËÎÓÔˆÙ˘˚¸ˇµªπæ≥∂ºø±>])/o;
$dirname   = qr/\s*\/\s*(?:\s(?:_UNDERSCORE|_|\.|\~)|(?:_UNDERSCORE|_|\.|\~)\s|[\w-\.\~_])+/o;
$dom2      = qr/$a[\w\-_]{2,}\./o;
$domN      = qr/$a[\w\-_]+\.(?:[\w\-_]{2,}\.)+/o;
$POSTvar   = qr/(?:[a-zA-Z0-9\-\_]+\s*=(?:\s*[a-zA-Z0-9\-\_]+)?)/o;
$refOK     = qr/(?:$domN(?:$safeExt|$unsafeExt)|$dom2$safeExt)(?:$dirname)*(?:\s*\/)?(?:\s*\?\s*$POSTvar(?:\s*\&amp;\s*$POSTvar)*)?/o;
$op        = qr/(?:\<|\&lt;)/o;
$cl        = qr/(?:\>|\&gt;)/o;

while (<>) {
    # formattage
    chomp;
    if (/ (_XML|_MS_ANNOTATION) *$/) {
	print "$_\n";
	next;
    }

    s/^\s*/ /o;
    s/\s*$/ /o;
#    s/\&gt;/>/o;
#    s/\&lt;/</o;

    # correction
    s/($protocole):\/([^\/])/$1:\/\/$2/go;
    if (!$no_sw) {
	s/($protocole):\s+\/([^\/])/$1:\/\/$2/go;
    }
    s/($protocole)\/\//$1:\/\//go;
    s/([^h])ttp:\/\//$1http:\/\//go;
    if (!$no_sw) {
	s/($protocole)\s*:\s*\/\/\s*/$1:\/\//go;
	s/($protocole)(:\/\/$a+\.)\s/$1$2/go;
	s/($protocole)(:\/\/$a+\.$a+\.)\s([a-z]{2,})\b/$1$2$3/go;
	s/([\'\/\.\w‡‚‰ÈÍËÎÓÔˆÙ˘˚¸ˇ-])($protocole):\/\//$1 $2:\/\//go;
	s/\. *(org|net|com|fr)\b/.$1/go;
	s/((?:$dom2$safeExt|$domN(?:$safeExt|$unsafeExt))[\w-\/\~]*)\s([\w-]*(?:\/[\w-\/]*|[\w-\/]*\.html?))\b/$1$2/go;
    }
    s/($protocole)(:\/\/[^\s,;]*)[‚‡‰]([^\s,;]*\.$a)/$1$2a$3/go;
    s/($protocole)(:\/\/[^\s,;]*)[ÈËÍÎ]([^\s,;]*\.$a)/$1$2e$3/go;
    s/($protocole)(:\/\/[^\s,;]*)[ÓÔ]([^\s,;]*\.$a)/$1$2i$3/go;
    s/($protocole)(:\/\/[^\s,;]*)[Ùˆ]([^\s,;]*\.$a)/$1$2o$3/go;
    s/($protocole)(:\/\/[^\s,;]*)[˚¸]([^\s,;]*\.$a)/$1$2u$3/go;
    if (!$no_sw) {
	s/\btrad\.([^\s])/trad. $1/go;
    } else {
	s/\btrad\.([^\s])/trad. _REGLUE_$1/go;
    }

    # reconnaissance
    ### minitel
    s/(^|\s)(36-?1[567]\s[^\s]+)(\s|$)/$1\{$2} _URL$3/;
    ### urls faciles (deux points dans le nom de domaine ou alors extension autre que .et et .de)
    s/$pre($op?(?:(?:$protocole)\s*:\s*\/\/)?$refOK$cl?)$post/\{$1\}_URL/go;
    ### urls de type (http://)?nomsanspoint.(et|de)/deschosesobligatoirement
    s/$pre($op?(?:(?:$protocole)\s*:\s*\/\/)?$dom2$unsafeExt(?:$dirname)+\/?$cl?)$post/\{$1\}_URL/go;
    ### urls de type http://nomsanspoint.(et|de)(/deschosesfacultatives)?
    s/$pre($op?(?:(?:$protocole)\s*:\s*\/\/)$dom2$unsafeExt(?:$dirname)*\/?$cl?)$post/\{$1\}_URL/go;
    s/(\s)\.\}_URL/\}_URL$1\./go || s/\.\}_URL/\}_URL\./go;

    # sortie
    if ($no_sw) {
	s/(_URL)([^\s])/$1 _REGLUE_$2/g;
    } else {
	s/(_URL)([^\s])/$1 $2/g;
    }

    s/^\s*_<_\s*/_<_/go;
    s/\s*_<_\s*/ _<_/go;
    s/\s*_>_\s*/_>_ /go;
    s/^_\{/_/go;
    s/ _\{/ _/go;
    s/\}_[A-Z]+_ /_ /go;
    print "$_\n";
}
