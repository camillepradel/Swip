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

""" Corpus reader """

import sys
import codecs
import re

WD_TAG_RE = re.compile(r'^(.+)/(.+)$')


class CorpusReader:
    pass


class BrownReader(CorpusReader):

    """
    Data reader for corpus in the Brown format:

    Le/DET prix/NC de/P vente/NC du/P+D journal/NC n'/ADV a/V pas/ADV <E9>t<E9>/VPP divulgu<E9>/VPP ./PONCT 
    Le/DET cabinet/NC Arthur/NPP Andersen/NPP ,/PONCT administrateur/NC judiciaire/ADJ du/P+D titre/NC depuis/P l'/DET effondrement/NC de/P l'/DET empire/NC Maxwell/NPP en/P d<E9>cembre/NC 1991/NC ,/PO
    NCT a/V indiqu<E9>/VPP que/CS les/DET nouveaux/ADJ propri<E9>taires/NC allaient/V poursuivre/VINF la/DET publication/NC ./PONCT 

    """

    def __init__(self,infile, encoding='utf-8'):
        self.stream = codecs.open(infile, 'r', encoding)
        return

    def __iter__(self):
        return self

    def next(self):
        line = self.stream.readline().strip()
        if (line == ''):
            self.stream.seek(0)
            raise StopIteration
        token_list = []
        for item in line.split():
            wd,tag = WD_TAG_RE.match(item).groups()
            token_list.append( (wd,tag) )
        return token_list




if __name__ == "__main__":
    import sys 
    for s in BrownReader( sys.argv[1] ):
        print s
