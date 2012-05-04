#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

import dgraph
import copy

# Marie Candito
# Graph Modification specification 

class LabelModifs:

    def __init__(self):
        self.label_map = {}
        
    def add_modif(self, old_label, new_label, conditions=[]):
        self.label_map[old_label] = [new_label, conditions]
    
    def get_modif(self, old_label):
        if old_label in self.label_map:
            return (self.label_map[old_label][0],
                    self.label_map[old_label][1])
        return (None,None)

    # try to modify the edge label
    #     according to label mappings, and conditions
    # CAUTION : conditions assume the edge variable is 'edge'
    def modify_edge(self, edge):
        mod = False
        loop = True
        while loop:
            print "MODIFIED BEFORE:", edge.print_parc700()
            loop = False
            (new_label, conditions) = self.get_modif(edge.label)
            if new_label <> None:
                for condition in conditions:
                    if not eval(condition):
                        break
                else:
                    loop = True
                    edge.label = new_label
                    print "MODIFIED AFTER:", edge.print_parc700()
                    mod = True
        return mod

    # marie 
    # Returns a copy of a dgraph,
    # where edge labels are changed according to a LabelModifs object
    def apply_modifs(self, dgraph):
        newdgraph = copy.deepcopy(dgraph)

        for edge in newdgraph.edges:
            self.modify_edge(edge)
        return newdgraph

        

# apauvrissement du corpus de référence p7-107
# 
P7107LABELMODIFS = LabelModifs() 
# old_label , new_label, conditions
# attention : le old_label est une clé
# TODO faire old_label => (new_lab1/conditions1), (new_lab2/conditions2) ...
P7107LABELMODIFS.add_modif('mod_app','mod')
P7107LABELMODIFS.add_modif('mod_loc','mod')
P7107LABELMODIFS.add_modif('mod_quant','mod')
P7107LABELMODIFS.add_modif('p_obj_loc','p_obj')
P7107LABELMODIFS.add_modif('p_obj_agt','p_obj')
P7107LABELMODIFS.add_modif('suj_impers','suj')
P7107LABELMODIFS.add_modif('aff_moyen','aff')
P7107LABELMODIFS.add_modif('mod#','mod')
P7107LABELMODIFS.add_modif('mod_cleft','mod')
P7107LABELMODIFS.add_modif('a_obj#','a_obj')
P7107LABELMODIFS.add_modif('de_obj#','de_obj')
P7107LABELMODIFS.add_modif('suj_impers','suj')
P7107LABELMODIFS.add_modif('mod_comp','mod')
# pas de gestion de la sous-catégorisation pour les tetes non verbales
# "destruction du temple"
# "attentif à la sécurité"
P7107LABELMODIFS.add_modif('de_obj','dep',['edge.orig.feats["pos"] in ["A","N","PRO","ADV"]'])
P7107LABELMODIFS.add_modif('a_obj','dep',['edge.orig.feats["pos"] in ["A","N","PRO","ADV"]'])
P7107LABELMODIFS.add_modif('p_obj','dep',['edge.orig.feats["pos"] in ["A","N","PRO","ADV"]'])
# pour les modifieurs de tete non verbales, on ecrase dans le cas d'un dependant prep (mais pas adj)
P7107LABELMODIFS.add_modif('mod','dep',['edge.orig.feats["pos"] in ["N","A","PRO","ADV"]','edge.dest.feats["pos"] in ["P","P+D"]' ])

            
