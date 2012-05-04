nobase_pkgdata_SCRIPTS = \
	tag_meta.pl	\
	gl_entities.pl	\
	gl_email.pl	\
	gl_url.pl	\
	gl_mtponct.pl	\
	gl_numtruc.pl	\
	gl_number.pl	\
	gl_smiley.pl	\
	gl_format.pl	\
	segmenteur.pl	\
	remove_inner_ne.pl	\
	caponlysentences.pl	\
	rebuild_easy_tags.pl	\
	normalize_blanks.pl


bin_SCRIPTS = sxpipe-light

EXTRA_DIST = lingpipe $(nobase_pkgdata_SCRIPTS) pctabr sxpipe-light.conf.in

CLEANFILES = sxpipe-light.conf sxpipe-light

pkgdata_DATA = pctabr

EDIT = perl -pe "s|\@pkgdatadir\@|$(pkgdatadir)|g;\
		s|\@datadir\@|\$(pkgdatadir)|g; 	\
		s|\@libdir\@|\$(pkglibdir)|g; 	\
		s|\@bindir\@|\$(bindir)|g; "

sxpipe-light.conf: sxpipe-light.conf.in
	@echo "Building lingpipe configuration file $@";\
	$(EDIT) < $< > $@

sxpipe-light: sxpipe-light.conf $(srcdir)/lingpipe
	@echo "Building script $@";\
	$(srcdir)/lingpipe --config $< --mode create > $@
	chmod +x $@
