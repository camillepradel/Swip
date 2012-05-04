#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

from optparse import OptionParser
import XmlReader
from SetCompounds import *
import os

usage = """
This converts the FTB in xml format into Penn Tree bank format (bracketed).
It outputs one sentence per line, each line containing the id of the sentence, a tab, the sentence in ptb-like format.

The original trees are modified : empty lexical nodes are removed.
and depending on the options, they undergo various transformations :
on the trees' tagset, on the compounds, etc...
 
            %prog [options] FTB_FILE_OR_DIR

"""                    
parser=OptionParser(usage=usage)
parser.add_option("--add-dummy",action="store_true",dest="add_dummy",default=False,help="If set, a dummy root is added to every tree. Used for full Penn TreeBank compliance.")
parser.add_option("--merge-cpd",action="store_true",dest="merge_cpd",default=False,help="If set, compounds are merged to yield a single token.")
parser.add_option("--merge-num",action="store_true",dest="merge_num",default=False,help="If set, digital numbers are merged to yield a single token.")
parser.add_option("--clitics-down",action="store_true", dest="clitics_down", default=False, help="If set, puts the functional annotations on the VN on the clitics")
parser.add_option("--output-function",action="store_true",dest="output_function",default=False,help="If set, available functional information is appended to categories.")
parser.add_option("--output-features",action="store_true",dest="output_features",default=False,help="If set, available features (gender, nb, mood...) are appended to categories.")
parser.add_option("--tagset", dest="tagset", default='ftb4', help='Tagset to turn the base categories of the FTB, plus features into complex categories. Set to None to disable. Default=ftb4')
parser.add_option("--set-cpd",action="store_true", dest="set_cpd", default=False, help="If set, compounds having a regular paattern are undone, and mandatory compounds are set. See UndoCompounds.py")
parser.add_option("--lefffloc",dest="lefffloc",default=None,help="Pertains only if --set-cpd is set. Lefff location : either a .lex lefff file to load, or a directory, in which all .lex files are to load",metavar='LEFFF_FILE_OR_DIR')
parser.add_option("--subcatfile",dest="subcatfile",default=None,help="Pertains only if --set-cpd is set. File containing known subcats : triples cat, subcat, form, separated by a tab. Used to infer subcats for components of undone compounds.", metavar="SUBCATFILE")
parser.add_option("--cc",action="store_true", dest="cc", default=False, help="If set, options are set as to output the treebank in CC format : merge-num, merge-cpd, add-dummy, tagset=ftb4")
parser.add_option("--serializedlex", action="store_true", dest="serializedlex", default=False, help='Whether the lefff is in serialized version')

(opts,args) = parser.parse_args()
#ARGS
if (len(args) > 0):
    ftbloc = args[0]
    if os.path.isdir(ftbloc):
        get_trees_function = XmlReader.read_xml_dir
    else:
        get_trees_function = XmlReader.read_xml_file

#OPTIONS
add_dummy = bool(opts.add_dummy)
tagset = str(opts.tagset)
merge_num = bool(opts.merge_num)
merge_cpd = bool(opts.merge_cpd)
clitics_down = bool(opts.clitics_down)
output_function = bool(opts.output_function)
output_features = bool(opts.output_features)
set_cpd = bool(opts.set_cpd)
lefffloc = str(opts.lefffloc) if opts.lefffloc <> None else None
subcatfile = str(opts.subcatfile)
cc = bool(opts.cc)
serializedlex = bool(opts.serializedlex)

if cc:
    merge_cpd = True
    merge_num = True
    add_dummy = True
    tagset = 'ftb4'

if tagset <> None:
    # call the ftb4_fixer function 
    tagfixer = eval(tagset+'_fixer')()
    tagfixer.build_inverse_mapping()
else:
    tagfixer = None

if set_cpd:
    setter = SetCompounds(lefffdir=lefffloc, subcatfile=subcatfile, serialized_input=serializedlex)

# call XmlReader on the xml trees, without any transformations (normalised = false)
for ttree in get_trees_function(ftbloc,normalised=False):
    # rm_trace
    # merge_num
    # Lowers down clitic annotations on the VN to the clitic tags
    # don't merge cpd yet, if compounds are to be modified (set_cpd)
    XmlReader.normalise_ftb_tree(ttree.tree, merge_num=merge_num, merge_cpd=False, rm_traces=True)
    
    # undo / redo compounds
    if set_cpd:
        setter.undo_compounds(ttree.tree)
        setter.do_compounds(ttree.tree)
    # merge compounds
    if merge_cpd:
        XmlReader.normalise_ftb_tree(ttree.tree, merge_num=False, merge_cpd=True, rm_traces=False)

    # build complex tagset
    if tagfixer <> None:
        XmlReader.do_complex_cats(ttree.tree,tagfixer)

    # add dummy root to trees (as in strict PTB format)
    if add_dummy:
        ttree.tree = add_dummy_root(ttree.tree)

    print ttree.to_db_string(identifier=True,display_features=output_features,display_functions=output_function)

