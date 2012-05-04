# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.59)
AC_INIT(MElt, 0.6, Pascal.Denis@inria.fr and Benoit.Sagot@inria.fr)
AM_INIT_AUTOMAKE

# Checks for programs.
AM_PATH_PYTHON

AC_PATH_PROG(PERL, perl)
if test "x$PERL" = x; then
	AC_MSG_ERROR(perl not found)
fi

# look for dicos
AC_PATH_ALEXINA
# AC_PATH_LEFFF
# AM_CONDITIONAL(HAVE_LEFFF, test -n "$lefffdir")

AC_CONFIG_FILES(	data/Makefile
			src/Makefile
			src/melt/Makefile
			bin/Makefile
			sxpipe-light/Makefile
			Makefile
		)
AC_OUTPUT
