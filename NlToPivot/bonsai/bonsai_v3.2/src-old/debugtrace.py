#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import sys
import linecache


# Djame : code de deboggage, ajout fonction trace à la prolog
# man, I hate python so muchhhhhhhhh, wtf ? no { } ????? putain...
# see http://www.dalkescientific.com/writings/diary/archive/2005/04/20/tracing_python_code.html
# pure cut'n'paste, meme pas honte...

def traceit(frame, event, arg):
    if event == "line":
      lineno = frame.f_lineno
      filename = frame.f_globals["__file__"]
      if (filename.endswith(".pyc") or filename.endswith(".pyo")):
          filename = filename[:-1]
      name = frame.f_globals["__name__"]
      line = linecache.getline(filename, lineno)
      print "%s:%s: %s" % (name, lineno, line.rstrip())
    return traceit