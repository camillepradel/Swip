# Process this file with automake to produce Makefile.in

AUTOMAKE_OPTIONS = foreign

bin_SCRIPTS =	MElt		\
		MElt-train	\
		lemmatizer.pl	\
		serialize_lefff.py	\
		build_word_list.py	\
		build_tag_dict.py

EXTRA_DIST =	MElt.in		\
		MElt-train.in		\
		lemmatizer.pl.in	\
		serialize_lefff.py.in	\
		build_word_list.py	\
		build_tag_dict.py

CLEANFILES =	MElt MElt-train lemmatizer.pl serialize_lefff.py 


EDIT = perl -pe "s|\@pythondir\@|\$(pythondir)|g; 	\
		 s|\@datadir\@|\$(pkgdatadir)|g; 	\
		 s|\@libdir\@|\$(pkglibdir)|g; 		\
		 s|\@version\@|$(PACKAGE_VERSION)|g;	\
		 s|\@bindir\@|\$(bindir)|g; "

MElt: MElt.in
	$(EDIT) < $< > $@
	chmod +x $@

MElt-train: MElt-train.in
	$(EDIT) < $< > $@
	chmod +x $@

%.pl: %.pl.in
	$(EDIT) < $< > $@
	chmod +x $@

%.py: %.py.in
	$(EDIT) < $< > $@
	chmod +x $@
