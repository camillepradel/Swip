#################################################################################
## Copyright (C) 2009 Pascal Denis and Benoit Sagot
## 
## This library is free software; you can redistribute it and#or
## modify it under the terms of the GNU Lesser General Public
## License as published by the Free Software Foundation; either
## version 3.0 of the License, or (at your option) any later version.
## 
## This library is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
## Lesser General Public License for more details.
## 
## You should have received a copy of the GNU Lesser General Public
## License along with this library; if not, write to the Free Software
## Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#################################################################################

''' Some useful functions '''

from melt.corpus_reader import BrownReader
from collections import defaultdict
import codecs
try:
    from cjson import decode as loads, encode as dumps
except ImportError:
    try:
        from simplejson import dumps, loads
    except ImportError:
        from json import dumps, loads



def tag_dict(file_path):
    tag_dict = defaultdict(dict)
    for s in BrownReader(file_path,encoding="utf-8"):
        for wd,tag in s:
            tag_dict[wd][tag] = 1
    return tag_dict



def word_list(file_path,t=5):
    word_ct = {}
    for s in BrownReader(file_path,encoding="utf-8"):
        for wd,tag in s:
            word_ct[wd] =  word_ct.get(wd,0) + 1
    filtered_wd_list = {} 
    for w in word_ct:
        ct = word_ct[w]
        if ct >= t:
            filtered_wd_list[w] = ct
    return filtered_wd_list



def unserialize(filepath, encoding="utf-8"):
    _file = codecs.open( filepath, 'r', encoding=encoding )
    datastruct = loads( _file.read() )
    _file.close()
    return datastruct



def serialize(datastruct, filepath, encoding="utf-8"):
    _file = codecs.open( filepath, 'w', encoding=encoding )
    _file.write( dumps( datastruct ) )
    _file.close()
    return 


