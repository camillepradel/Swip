# Process this file with automake to produce Makefile.in

PYTHONPATH=$(top_srcdir)/src

AUTOMAKE_OPTIONS = foreign

pkgdata_DATA = \
	$(srcdir)/fr/lexicon.json	\
	$(srcdir)/fr/tag_dict.json	\
	$(srcdir)/fr/classes.json		\
	$(srcdir)/fr/feature_map.json		\
	$(srcdir)/fr/weights.npy		\
	$(srcdir)/fr/bias_weights.npy

EXTRA_DIST = \
	$(srcdir)/fr/lexicon.json	\
	$(srcdir)/fr/tag_dict.json		\
	$(srcdir)/fr/classes.json		\
	$(srcdir)/fr/feature_map.json		\
	$(srcdir)/fr/weights.npy		\
	$(srcdir)/fr/bias_weights.npy


LC_COLLATE = C
LC_CTYPE = C
LC_ALL = C
LANG = C

update_lefff_data: $(alexinadir)/fr
	cat $(alexinadir)/fr/*.lex | perl lex2ftb4cats.pl
	cat lefff_24ms | $(top_srcdir)/bin/unfactorize_lexicon.pl | sort -u > $$$$; mv $$$$ lefff_24ms
	cat lefff_24 | sort -u > $$$$; mv $$$$ lefff_24
	cat lefff_13 | sort -u > $$$$; mv $$$$ lefff_13
	cat lefff_o | sort -u > $$$$; mv $$$$ lefff_o
	cat lefff_g | sort -u > $$$$; mv $$$$ lefff_g

%.MElt: %.tok
	cat $< | MElt > $@

%.locc: %.MElt
	cat $< | $(top_builddir)/bin/lemmatizer.pl | perl -pe "s/ /\n/g" | perl -pe "s/^.*?\/(.*)\/(.*)/\\2__\\1/; s/\|/\n/g" | sort | uniq -c | sort -n |perl -pe "s/^ *//; s/ /\t/" > $@



