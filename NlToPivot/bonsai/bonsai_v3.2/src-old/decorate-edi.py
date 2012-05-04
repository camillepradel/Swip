#/usr/local/bin/python

from PennTreeBankReader import *
from LabelledTree import *



def decorate_tree(root):
    if not (root.is_leaf() or root.is_terminal_sym()):
        root.label = root.label+'*'
        for child in root.children:
            decorate_tree(child)


reader = PtbReader(drparser=False)
treebank = reader.read_mrg(sys.stdin)

for tree in treebank:
    decorate_tree(tree)
    print tree.printf()


