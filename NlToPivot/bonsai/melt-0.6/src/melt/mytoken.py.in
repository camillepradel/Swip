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


''' Token class definition '''

import re
import sys


class Token:

    def __init__(self, string=None, pos=None, label=None, comment=None, label_pr_distrib=[]):
        self.string = string
        self.pos = pos
        self.label = label
        self.comment = comment
        self.label_pr_distrib = label_pr_distrib
        if (self.comment == None):
            self.comment = ""
        return

    def set_label(self, label):
        self.label = label
        return

    def __str__(self):
        return "%s%s/%s" %(self.comment,self.string,self.label)


