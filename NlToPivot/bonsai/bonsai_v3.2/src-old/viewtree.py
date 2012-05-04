#!/usr/bin/env python
# -*- coding: iso-8859-1 -*-

import sys
import nltk
from nltk.draw.tree import *
from Tkinter import *
from nltk import tree
from nltk.draw import *
from frequency import *
from PennTreeBankReader import *
from concord import *

#A viewer for a treebank 
class TreeView:

    def __init__(self,parent,instream):
        self.tree_list=[]
        sys.stderr.write("Reading treebank\n")
        (self.treebank,self.current_treebank) = self.read_treebank(instream)
        sys.stderr.write("Computing stats")
        freqs = FrequencyTable() 
        freqs.do_counts(self.treebank)
        self.pcfg = freqs.get_sorted_rev_dico(freqs.pcfg_dico)
        self.sym = freqs.get_sorted_rev_dico(freqs.sym_dico)
        self.do_top_window(parent)
        #Concordancer
        self.conc = Tgrep(self.current_treebank)

    def do_top_window(self,top):
        top.title("TreeViewer")
        listframe = Frame(top)
        scrollbar = Scrollbar(listframe,orient=VERTICAL)
        self.listbox = Listbox(listframe,height=36, width=45,yscrollcommand=scrollbar.set)
        scrollbar.config(command=self.listbox.yview)
        self.listbox.bind("<ButtonRelease-1>", self.display_tree_callback)
        self.fill_treebank_list(self.listbox, self.current_treebank)
        listframe.pack(side=LEFT)
        scrollbar.pack(side=RIGHT, fill=Y)     
        self.listbox.pack(side=LEFT, fill=BOTH, expand=1)

        self.cf = CanvasFrame(top,width=1000, height=600, closeenough=2) 
        xcoord = 10
        yindent = 5
        ydepth = 10
        self.tc = TreeWidget(self.cf.canvas(), self.ftb2nltk(self.current_treebank[0]), draggable=1, 
                        node_font=('helvetica', -12, 'bold'),
                        leaf_font=('helvetica', -10, 'italic'),
                        roof_fill='white', roof_color='black',
                        leaf_color='green4', node_color='blue2')
        self.cf.add_widget(self.tc,xcoord,ydepth)
        self.tree_list.append(self.tc)
        ydepth = self.tc.height() + yindent
        self.cf.pack(side=RIGHT)       
        #Menu related stuff
        menu = Menu(top)
        top.config(menu=menu)
        filemenu = Menu(menu)
        menu.add_cascade(label="File", menu=filemenu)
        filemenu.add_command(label="Save Postscript", command=self.do_save)
        filemenu.add_separator()
        filemenu.add_command(label="Exit", command=self.do_exit)
        concmenu=Menu(menu)
        menu.add_cascade(label="Concordance", menu=concmenu)
        concmenu.add_command(label="PCFG...",command=self.do_pcfg_window)
        concmenu.add_command(label="Categories...",command=self.do_sym_window)
        concmenu.add_separator()
        concmenu.add_command(label="Reset Concordancer",command=self.reset_viewer)

    def reset_viewer(self):
        self.current_treebank=self.treebank
        self.empty_treebank_list(self.listbox)
        self.fill_treebank_list(self.listbox,self.treebank)
        self.conc.set_treebank(self.treebank)

    def do_pcfg_window(self):
        self.pcfg_dialog = Toplevel()
        self.pcfg_dialog.title("PCFG")
        pcfglistframe = Frame(self.pcfg_dialog)
        pcfgscrollbar = Scrollbar(pcfglistframe,orient=VERTICAL)
        self.pcfglistbox = Listbox(pcfglistframe,height=40, width=35,yscrollcommand=pcfgscrollbar.set)
        pcfgscrollbar.config(command=self.pcfglistbox.yview)
        self.pcfglistbox.bind("<ButtonRelease-1>", self.display_pcfg_conc_callback)
        threshold = 1
        i = 0
        while i in range(len(self.pcfg)) and self.pcfg[i][0] >= threshold:
            self.pcfglistbox.insert(END,str(self.pcfg[i][0])+'  '+self.pcfg[i][1])
            i = i + 1
	    
        pcfglistframe.pack(side=LEFT)
        pcfgscrollbar.pack(side=RIGHT, fill=Y)     
        self.pcfglistbox.pack(side=LEFT, fill=BOTH, expand=1)
                          
    def do_sym_window(self):
        self.sym_dialog = Toplevel()
        self.sym_dialog.title("Non Terminal Symbols")
        symlistframe = Frame(self.sym_dialog)
        symscrollbar = Scrollbar(symlistframe,orient=VERTICAL)
        self.symlistbox = Listbox(symlistframe,height=20, width=35,yscrollcommand=symscrollbar.set)
        symscrollbar.config(command=self.symlistbox.yview)
        self.symlistbox.bind("<ButtonRelease-1>", self.display_sym_conc_callback)
        threshold = 1
        i = 0
        while i in range(len(self.sym)) and self.sym[i][0] >= threshold:
            self.symlistbox.insert(END,str(self.sym[i][0])+'  '+self.sym[i][1])
            i = i + 1	    
        symlistframe.pack(side=LEFT)
        symscrollbar.pack(side=RIGHT, fill=Y)     
        self.symlistbox.pack(side=LEFT, fill=BOTH, expand=1)

    def read_treebank(self,instream):
        reader = PtbReader()
        treebank = reader.read_mrg(sys.stdin)
        return (treebank,treebank)

    def do_save(self):
        idxes = self.listbox.curselection()
        # marie : stockage sous le home du user
        outfile = os.path.join( os.path.expanduser('~'), self.listbox.get(int(idxes[0]))+'.ps')
        #self.cf.print_to_file("/Users/Benoit/"+self.listbox.get(int(idxes[0]))+'.ps')
        #print "file written as /Users/Benoit/"+self.listbox.get(int(idxes[0]))+'.ps'
        self.cf.print_to_file(outfile)
        print "file written as "+outfile

    def do_exit(self):
        sys.exit(0)

    def display_tree_callback(self,event):
        sel_idxes = self.listbox.curselection()
        #Destroys the previouslys displayed stuff
        for tree in self.tree_list:
            self.cf.destroy_widget(tree)
            self.tree_list.remove(tree)
        for idx in sel_idxes:
            self.tc = TreeWidget(self.cf.canvas(), self.ftb2nltk(self.current_treebank[int(idx)]), draggable=1, 
                            node_font=('helvetica', -12, 'bold'),
                            leaf_font=('helvetica', -10, 'italic'),
                            roof_fill='white', roof_color='black',
                            leaf_color='green4', node_color='blue2')
            self.cf.add_widget(self.tc,10,10)
            #self.cf.scale(0.75,0.75,0,0)
            self.tree_list.append(self.tc)

    def display_pcfg_conc_callback(self,event):
        sel_idxes = self.pcfglistbox.curselection()
        pcfgrule = self.pcfglistbox.get(sel_idxes)
        rule = self.list_item2rule(pcfgrule)
        newtreebank = self.conc.search_treebank(rule)
        if len(newtreebank) > 0:
            self.conc.set_treebank(newtreebank)
            self.current_treebank = newtreebank
            self.empty_treebank_list(self.listbox)
            self.fill_treebank_list(self.listbox, newtreebank)
        else:
            tkMessageBox.showinfo("Concordance results","No match")
                           
    def display_sym_conc_callback(self,event):
        sel_idxes = self.symlistbox.curselection()
        symitem = self.symlistbox.get(sel_idxes)
        sym = self.list_item2sym(symitem)
        newtreebank = self.conc.ssearch_treebank(sym)
        if len(newtreebank) > 0:
            self.conc.set_treebank(newtreebank)
            self.current_treebank = newtreebank
            self.empty_treebank_list(self.listbox)
            self.fill_treebank_list(self.listbox, newtreebank)
        else:
            tkMessageBox.showinfo("Concordance results","No match")

    def fill_treebank_list(self,listobject, treelist):
        for tree in treelist:
            listobject.insert(END,tree.printf())

    def empty_treebank_list(self,listobject):
        listobject.delete(0, END)


#A hacky function that parses a list item in the GUI and turns it to a rule
    def list_item2rule(self,listitem):
        rule = re.split("\s+",listitem)
        rule = [rule[1]]+rule[3:]
        return rule

    def list_item2sym(self,listitem):
        sym = re.split("\s+",listitem)
        return sym[1]

    def ftb2nltk(self,ftbtree):
        nltk_tree = nltk.bracket_parse(ftbtree.printf())   
        return nltk_tree


#Main
# Compute main informations
root = Tk()
viewer = TreeView(root,sys.stdin)
root.mainloop()
