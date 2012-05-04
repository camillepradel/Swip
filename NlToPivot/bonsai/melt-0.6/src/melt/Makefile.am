# Process this file with automake to produce Makefile.in

AUTOMAKE_OPTIONS = foreign

pkgpython_PYTHON = 	__init__.py		\
			pos_tagger.py		\
			instance.py		\
			mytoken.py		\
			classifier.py		\
			result_sink.py 		\
			utils.py		\
			corpus_reader.py	\
			pos_tag.py

BUILT_SOURCES = 	__init__.py		\
			pos_tagger.py		\
			instance.py		\
			mytoken.py		\
			classifier.py		\
			result_sink.py 		\
			utils.py		\
			corpus_reader.py	\
			pos_tag.py

CLEANFILES = 		__init__.py		\
			pos_tagger.py		\
			instance.py		\
			mytoken.py		\
			classifier.py	\
			result_sink.py 		\
			utils.py		\
			corpus_reader.py	\
			pos_tag.py		\
			__init__.pyc		\
			pos_tagger.pyc		\
			instance.pyc		\
			mytoken.pyc		\
			classifier.pyc	\
			result_sink.pyc 	\
			utils.pyc		\
			corpus_reader.pyc	\
			pos_tag.pyc

EXTRA_DIST = $(srcdir)/*.py.in

EDIT = perl -pe "s|\@datadir\@|\$(pkgdatadir)|g; 	\
		 s|\@libdir\@|\$(pkglibdir)|g;		\
		 s|\@bindir\@|\$(bindir)|g; "

%.py: %.py.in
	$(EDIT) < $< > $@
