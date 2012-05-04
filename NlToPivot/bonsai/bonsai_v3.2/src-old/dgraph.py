#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-

import datetime
import re
# mathieu
import os

DR_LABEL='null'
DR_EDGE ='head'

#BC Implementation note : due to the internal implementation of sets in python,
# I strongly recommend that vertices and edges are kept immutable once added to the graph.
# Since their identity (defined by their hashcode) is taken from the label and idx of the vertices,
# one may further modify other fields; modifying any of these two fields will inevitably lead to inconsistencies 

class DependencyGraph:

    def __init__(self):
        self.vertices = set([])
        self.edges = set([])
        #self.set_root(root)
        self.idx = -1# what is it for ?

	#FRANCOIS: une fonction ajoute un noeud MISSINGHEAD entre 'null' et la tete du graphe,
	# ce fix ce charge de supprimer ce noeud inutile (et de rétablir l'arc entre null et la tete)
    def FIX_missinghead(self):
    	nedges=set([])
    	null_head={}
    	for e in self.edges:
    		if e.orig.label == 'null':
    			null_head['null']=e.orig
    		elif e.orig.label == 'MISSINGHEAD':
    			null_head['head']=e.dest
    		else:
    			nedges.add(e)
    	if null_head.has_key('null') and null_head.has_key('head'):
    		nedges.add(DepEdge(null_head['null'],'head',null_head['head']))
    		self.edges=nedges
    	#rajouter les deux "else" (si jamais ilnya pas les deux slots d'activés)

    def translate_labels(self,dico):
		for e in self.edges:
			if dico.has_key(e.label.lower()):
				e.label = dico[e.label.lower()]

    def is_empty_graph(self):
        return len(self.vertices) == 0

    def is_parse_failure(self):
        return self.is_empty_graph()

    def add_dummy_root(self):
        dr = DepVertex(DR_LABEL,-1)
        roots = self.get_roots()
        for r in roots:
            self.add_edge(dr,DR_EDGE,r)

    def remove_dummy_root(self):
        dr = DepVertex(DR_LABEL,-1)
        self.vertices.remove(dr)
        nedges = set([])
        for e in self.edges:
            if not e.orig == dr:
                nedges.add(e)
        self.edges = nedges

    def set_root(self,root):
        self.root = root

    def add_vertex(self,label,id):
        vertex = DepVertex(label,id)
        self.vertices.add(vertex)

	#FRANCOIS: zont le meme nom, ce nest pas grave?
    def add_vertex(self,vertex):
        self.vertices.add(vertex)

    def add_edgeq(self,edge):
        self.add_edge(edge.orig,edge.label,edge.dest)

    def add_edge(self,orig,lbl,dest):
        #This fixes a super weird bug I dug out : need to ensure that pointers point in the right place, arf...
        # I made a verbose fix -> start here
        if orig in self.vertices:
            orig = self.get_vertex_ptr(orig)
        else:
            self.add_vertex(orig)
        if dest in self.vertices:
            dest = self.get_vertex_ptr(dest)
        else:
            self.add_vertex(dest)
        #eofix
        edge = DepEdge(orig,lbl,dest)
        self.edges.add(edge)


    # this sucks... can anyone find sth more efficient ??
    def get_vertex_ptr(self,vertex):
        for elt in self.vertices:
            if elt == vertex:
                return elt

    def set_feature(self,vertex,attr,val):
        if vertex in self.vertices:
            for v in self.vertices:
                if v == vertex:
                    v.set_feature(attr,val)
                    return

    #Returns a list of vertices ordered linearily
    def linear_order(self):
        vertices = sorted(self.vertices,lambda x,y: cmp(x.idx, y.idx))
        return vertices

    #Returns the sentence encoded in this graph as a string
    def sentence(self):
        vertices = self.linear_order()
        # marie : skip dummy nodes, i.e
        res = ''
        if len(vertices) > 0:
            if not vertices[0].is_dummy() : res = vertices[0].label
            for v in vertices[1:]:
                if not v.is_dummy():
                    res += ' '+v.label
            return res
        else:
            return ''

	#FRANCOIS
    def get_deps(self,index):
		nedges = set([])
		for ne in self.edges:
			#if ne.orig.idx == index and ne.label != "ponct":
			if ne.orig.idx == index:
				nedges.add(ne)
		return nedges
		
	#FRANCOIS
    def get_vertex(self,index):
		for v in self.vertices:
			if str(v.idx) == str(index):
				return v

#Graph connectivity section
#-----------------------------

    def next_vertices(self,vertex):
        next = set([])
        for elt in self.edges:
            (orig,lbl,dest) = elt.totuple()
            if orig.__eq__(vertex):
                next.add(dest)
        return next

    def reachable_nodes(self):
        agenda = []
        subset = set([])
        agenda.append(self.root)
        subset.add(self.root)
        while(len(agenda) > 0):
            current = agenda.pop()
            vertex_set = self.next_vertices(current)
            for elt in vertex_set:
                if not elt in subset:
                    agenda.append(elt)
                    subset.add(elt)
        return subset

    def is_connected(self):
        subset = self.reachable_nodes()
        if len(subset) == len(self.vertices):
            return True
        else:
            return False

    #Returns the roots of a dependency forest
    def get_roots(self):
        inset = set([])#vertices with 1 incoming edge set
        for edge in self.edges:
            inset.add(edge.dest)
        roots = self.vertices.difference(inset)
        return roots

    #Forces connectivity by linking a dependency forest roots to a new artificial root
    def repair_graph(self):
        roots = self.get_roots
        root = self.root
        for elt in roots:
            self.add_edge(root,"dep",elt)

    #Checks if the graph is projective
    def is_projective(self,root):
        pass

    # Marie
    # sort dep graph according to increasing id of dest (dependents)
    # i.e. according to linear order of original sentence
    def sort_dest(self):
        self.edges = sorted(self.edges,lambda x,y: cmp(x.dest.idx, y.dest.idx))

#Printing section
    def triples2string(self,sorted=False):
        if sorted:
            self.sort_dest()
        res = ''
        for elt in self.edges:
        	res = res+elt.print_parc700()+"\n"
        return res

    #Prints the features if featurelist is supplied, will print only the features specified in that list otherwise prints whatever feature found on the vertices
    def features2string(self,featurelist=[],sorted=False):
        vertices = self.vertices    
        if sorted:
            vertices = self.linear_order()
        res = ''
        if len(featurelist)>0:
            for f in featurelist:
                for v in vertices:
                    #if not v.is_dummy() and v.feats.has_key(f):
                    # on n'ecrit pas les None (???)
                    if not v.is_dummy() and v.feats.has_key(f) and v.feats[f] <> None:
                        try:
                            res+= str(f)+'('+v.__str__()+','+str(v.feats[f])+')\n'        
                        except UnicodeEncodeError:
                            # lemma and word not encoded in the same way, let python handle it
                            res += str(f) + '(' + v.__str__() 
                            res += ',' + v.feats[f] + ')\n'
        else:  
            for v in vertices:
                if not v.is_dummy():
                    for f in v.feats.keys():
                       	res+= str(f)+'('+v.__str__()+','+str(v.feats[f])+')\n'
        return res

    #converts a string into a suitable dot one
    def str2dot(self,str):
        return re.sub('"',"''",str)

    def graph2dot(self,features=['pos']):
        #Legend
        # latin1 encoding #res = 'digraph{\ngraph [ label = "'+self.str2dot(self.sentence())+'" ];\n'
        res = 'digraph{\ncharset=latin1\ngraph [ label = "'+self.str2dot(self.sentence())+'" ];\n'
        # sorts the nodes for a better display
        for v in self.linear_order():
             lbl = v.label
             if len(features) > 0:
                 for f in features:
                     if v.feats.has_key(f):
                         lbl=str(v.idx)+"   "+lbl+'-'+v.feats[f]
             if v.feats.has_key(f):
             	if v.feats[f] == "N":
             		res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'",color=orangered, style=filled ];\n'
             	elif v.feats[f] == "V":
             		res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'",color=green, style=filled ];\n'
             	elif v.feats[f] == "ADV":
             		res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'",color=orange, style=filled ];\n' 
             	elif v.feats[f] == "A":
             		res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'",color=violet, style=filled ];\n'
              	elif v.feats[f] == "P":
             		res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'",color=skyblue, style=filled ];\n'              	
              	elif v.feats[f] == "C":
             		res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'",color=grey, style=filled];\n'
              	elif v.feats[f] == "CL":
             		res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'",color=gold, style=filled];\n'  	
             	else:
             		res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'" ];\n'
             else:
             	res += str(v.idx) + ' '+'[label = "'+self.str2dot(lbl)+'" ];\n' 
        # output the edges
        for edge in self.edges:
            if edge.label.lower() == 'unk'  or edge.label == '':
                color = 'red'
            else:
                color = 'blue'
            res += str(edge.orig.idx) + ' -> ' + str(edge.dest.idx)+' [label = "'+self.str2dot(edge.label)+'",color='+color+'];\n' 
        res += '}\n'
        return res


    def graph2png(self,features=[]):
        pass
    # on verra plus tard... ne doit pas être localisé dans cette classe-ci...

     # marie : desactivation de la génération du png, cf. trop long
            #os.system('dot -T' + output + ' -Gcharset=latin1 ' + filename + '.dot > ' + filename + '.' + output)
            #os.remove(filename + '.dot')

    # marie : appauvrissement du tagset, pour conserver telle quelle les heuristiques de François pour conversion easy
    def set_poor_tags(self):
        for v in self.vertices:
            v.set_poor_tag()
        

        

  #   # mathieu : print the ParcPivot string ( function(governor,dependant) according to the linear order of the dependant.
#     # Can also print the pos and lemma if the input was a xml file (not if it was a Penn format file)
#  	#francois: copier coller de parcpivot au-dessus, mis a part quil nya pas d'argument
#  	# tout ce qui est treenum, date, file et validator sont des champs de DependencyGraph
#     def print_parcPivot(self, out_lemma):
#         # mathieu : ajout de la racine
#         # marie : ajout de la racine déplacé en amont, à la création du dgraph
#         #liste_elts.append(("head", "null", self.root.print_vertex(), self.root.idx))
#         # marie : tri déplacé en amont, avec méthode dgraph.sort_dest()

#         res = ""
#         res += "sentence(\n"
#         res += "id(" + str(self.treenum) + "," + self.file + ")\ndate("+str(self.date)+")\nvalidators(" + self.validator + ")\n"
#         if self.is_graph_failure():
#             res += "sentence_form(PARSE_FAILURE)\n"
#             res += 'surf_deps(\n'
#         else:
#             res += "sentence_form("+" ".join(self.sentence())+")\n"
#             res += "surf_deps(\n"

#             for elt in self.edges:
#                 res += elt.print_parcPivot()+'\n'
#             res += ")\n"
#             res += "features(\n"
            
#             for elt in self.edges:
#                 res += ("pos(" + elt.dest.label + "~" + str(elt.dest.idx) + "," + elt.dest.subcat + ")\n")
#             if out_lemma:
#                 for elt in self.edges:
#                     res += ("lemma(" + elt.dest.label + "~" + str(elt.dest.idx) + "," + elt.dest.lemma + ")\n")                
#             res += "))"
#         return res

#     # mathieu
#     # Convert a DependancyTree in a Pivot file : create a ParcPivot txt file
#     def Dgraph2ParcPivot(self, dir_name, validator, treenum, generation, out_lemma):
#         filename = os.path.join(dir_name,'P_'+str(treenum)) 
#         fichier_Pivot = open(filename+'.piv', 'w')
#         # marie propagation ajouts de ces champs a DependencyGraph
#         self.treenum = treenum
#         self.date = str(datetime.date.today())
#         self.validator = validator
#         # !! en fait c'est bien un dir ...
#         self.file = dir_name
#         fichier_Pivot.write(self.print_parcPivot(out_lemma).encode('ISO-8859-1'))
#         fichier_Pivot.close()
#         # todo : faire une fonction print_dot, à partir d'un DepGraph (et pas d'un fichier pivot...)
#         if generation:
#             output = 'png'
#             fichier_Pivot = open(filename + '.piv', 'r')
#             fichier_dot = open(filename + '.dot', 'w')
#             begin = False
#             end = False
#             for line in fichier_Pivot:
#                 if begin and not end:
#                     if line == ')\n':
#                         end = True
#                         break
#                     line = re.split('\)\n', line)
#                     line = re.sub('\(', ',', line[0], 1)
#                     line = re.split(',', line)
        
#                     if line[0] == 'none':
#                         couleur = "blue"
#                     else:
#                         couleur = "red"
            
#                     dependant_label = ""
#                     indice_trait_union = 0
#                     for j in range(len(line[2])-1,0,-1):
#                         if line[2][j] == '~': #marie '-':
#                             break
#                     for k in range(0,j):
#                         dependant_label += line[2][k]
#                     #marie : skip the "null -head-> root" edge
#                     if line[0] == 'head':
#                         fichier_dot.write(Traitement_RE(line[2]) + ' [ label = \" ' + Traitement_label(dependant_label) + '\" ] ;\n')
#                     else:
#                         fichier_dot.write('edge[color = ' + couleur + ', label = \"' + line[0] + '\" ] ;\n'
#                                           + Traitement_RE(line[1]) + ' -> ' + Traitement_RE(line[2]) + ';\n'
#                                           + Traitement_RE(line[2]) + ' [ label = \" ' + Traitement_label(dependant_label) + '\" ] ;\n')
#                 elif "sentence_form(" in line:
#                     line = re.split("sentence_form\(", line)
#                     line = re.split("\)\n", line[1])
#                     fichier_dot.write('digraph\n { graph [ label = \"' + Traitement_label(line[0]) + '\" ] ;\n')

#                 elif "surf_deps(" in line:
#                     begin = True
        
#             fichier_dot.write('}')
#             fichier_Pivot.close()
#             fichier_dot.close()

#             # marie : desactivation de la génération du png, cf. trop long
#             #os.system('dot -T' + output + ' -Gcharset=latin1 ' + filename + '.dot > ' + filename + '.' + output)
#             #os.remove(filename + '.dot')
    
#     # Convert a dgraph object to a Parc 700 or a ParcPivot txt file
#     # It is almost identical to the Dgraph2Pivot method : it could be merge into one easily
#     # (I though it was better to seperate them)
#     def Dgraph2Parc700(self, dir_name, validator, treenum, generation):
#         filename = os.path.join(dir_name,'P_'+str(treenum)) 
#         fichier_parc700 = open(filename+'.tpl', 'w')
#         # marie : print_parc700_dep_sorted devenu print_parc700
#         fichier_parc700.write(self.print_parc700(treenum, dir_name, validator).encode('ISO-8859-1'))
#         fichier_parc700.close()
#         if generation:
#             output = 'png'
#             fichier_700 = open(filename + '.tpl', 'r')
#             fichier_dot = open(filename + '.dot', 'w')
#             begin = False
#             end = False
#             for line in fichier_700:
#                 if begin and not end:
#                     if '))' in line:
#                         end = True
#                         break
#                     line = re.split('\)\n', line)
#                     line = re.sub('\(', ',', line[0], 1)
#                     line = re.split(',', line)
        
#                     if line[0] == 'none':
#                         couleur = "blue"
#                     else:
#                         couleur = "red"
            
#                     dependant_label = ""
#                     indice_trait_union = 0
#                     for j in range(len(line[2])-1,0,-1):
#                         if line[2][j] == '~': #'-':
#                             break
#                     for k in range(0,j):
#                         dependant_label += line[2][k]
            
#                     fichier_dot.write('edge[color = ' + couleur + ', label = \"' + line[0] + '\" ] ;\n'
#                           + Traitement_RE(line[1]) + ' -> ' + Traitement_RE(line[2]) + ';\n'
#                           + Traitement_RE(line[2]) + ' [ label = \" ' + Traitement_label(dependant_label) + '\" ] ;\n')
#                 elif "sentence_form(" in line:
#                     line = re.split("sentence_form\(", line)
#                     line = re.split("\)\n", line[1])
#                     fichier_dot.write('digraph\n { graph [ label = \"' + Traitement_label(line[0]) + '\" ] ;\n')

#                 elif "structure(" in line:
#                     begin = True
        
#             fichier_dot.write('}')
#             fichier_700.close()
#             fichier_dot.close()

#             # marie : desactivation de la génération du png, cf. trop long
#             #os.system('dot -T' + output + ' -Gcharset=latin1 ' + filename + '.dot > ' +filename+ "." + output)
#             #os.remove(filename + '.dot')

# def Traitement_RE(chaine):
#     if '\"' in chaine:
#         chaine = re.sub('\"', 'QUOTES_TYPO', chaine)
#     if '.' in chaine:
#         chaine = re.sub('\.', 'DOT_TYPO', chaine)
#     if '\'' in chaine:
#         chaine = re.sub('\'', 'BACKSLASH_TYPO', chaine)
#     #marie : ~ replaces - as separator
#     if '~' in chaine:
#         chaine = re.sub('~', 'TRAIT_TYPO', chaine)
#     if '-' in chaine:
#         chaine = re.sub('-', 'TRAIT_TYPO', chaine)
#     if chaine[0] == ',':
#         chaine = re.sub(',', 'COMMA_PONCT', chaine) 
#     if ',' in chaine and chaine[0] <> ',':
#         chaine = re.sub('\,', 'COMMA_MATH', chaine)                  
#     if chaine[0] == '.':
#         chaine = re.sub('\.', 'DOT_PONT', chaine)
     
#     if '%' in chaine:
#         chaine = re.sub('%', 'PERCENT_TYPO', chaine)
#     if chaine[0] == '+':
#         chaine = re.sub('\+', 'PLUS_TYPO', chaine)
#     if chaine[0].isdigit():
#         chaine = '_' + chaine
#     if chaine[0] == '(':
#         chaine = re.sub('\(', 'LBR_PONCT', chaine)
#     if chaine[0] == ')':
#         chaine = re.sub('\)', 'RBR_PONCT', chaine)
#     if chaine[0] == ';':
#         chaine = re.sub(';', 'SEMICOLON_PONCT', chaine)
#     if chaine[0] == ':':
#         chaine = re.sub(':', 'COLON_PONCT', chaine)
#     if chaine[0] == '?':
#         chaine = re.sub('\?', 'QMARK_PONCT', chaine)
#     if chaine[0] == '!':
#         chaine = re.sub('!', 'EMARK_PONCT>', chaine)
#     if chaine[0] == '[':
#         chaine = re.sub('\[', 'TLBR_PONCT>', chaine)
#     if chaine[0] == ']':
#         chaine = re.sub('\]', 'TRBR_PONCT>', chaine)
#     if '<' in chaine:
#         chaine = re.sub('<', 'INF_', chaine)
#     if '>' in chaine:
#         chaine = re.sub('>', '_', chaine)
    
#     # Cas tres particulier dus au langage DOT de graphviz
#     if chaine == 'strict':
#         chaine += '_token'
        
#     return chaine

# def Traitement_label(chaine):
#     if '\"' in chaine:
#         chaine = re.sub('\"', '\\"', chaine)
#     return chaine


    #Code for D. Duchier's xdag.sty
    def print_latex(self):
        res = "\begin{xdag}\n"
        pass
        res = res + "\end{xdag}\n"
        return res

# Creates an empty graph used to represent a parse failure (the sentence received no parse at all)

def generate_parse_failure():
    return DependencyGraph()

# marie : hack tempo pour appauvrissement du tagset éventuel, avant conversion easy, cf. toutes les heuristiques easy sont sur tagset pauvre
ToPoorTags = { 'NC' : 'N', 'NPP' : 'N', 
               'VIMP':'V', 'VINF' : 'V', 'VS' : 'V', 'VPP':'V', 'VPR':'V',
               'CS':'C', 'CC':'C',
               'CLS':'CL', 'CLO':'CL', 'CLR':'CL', 
               'ADJ':'A', 'ADJWH':'A',
               'ADVWH':'ADV',
               'PROREL':'PRO', 'PROWH':'PRO',
               'DET':'D', 'DETWH':'D'
               }



#def rank(self,node,rank=0):
#    ranknum = 1
#    for elt in self.edges:
        
class DepVertex:
    def __init__(self,label,idx):
        self.label = label
        self.idx = idx
        self.feats = {}

    #cat is an atomic category
    def set_cat(self,cat):
        self.feats['pos'] = cat

    def set_lemma(self,lemma):
        self.feats['lemma'] = lemma
        
             
    def __str__(self):
        return self.print_vertex()

    def print_vertex(self,print_pos=False):
        # marie : separator ~ between lex and idx
        #return self.GR_symbols(self.label)+"-"+str(self.idx)    
        vertex = self.GR_symbols(self.label)+"~"+str(self.idx)
        pos = self.get_feature('pos')
        if print_pos and pos <> None:
        	vertex += "/"+pos
        	pass
        return vertex

    def __eq__(self,other):
        return self.idx == other.idx and self.label == other.label

    def __ne__(self,other):
        return not self.__eq__(other)

    def __hash__(self):
        return (self.label+'-'+str(self.idx)).__hash__()

    #BC: features attached to the nodes
    def set_feature(self,attr,value):
        self.feats[attr] = value

    # marie
    def get_feature(self,attr):
        if attr in self.feats:
            return self.feats[attr]
        return None

    #does metasymbols for GR format
    def GR_symbols(self,word):
        if word == ",":
            return "<C>"
        elif word == "--LBR--":
            return "<LBR>"
        elif word == "--RBR--":
            return "<RBR>"
        else:
            word = re.sub(",","<C>",word)
            word = re.sub("-","<D>",word)
            return word
    #marie : dummy vertices are null or missinheads
        # they have negative idx by convention
    def is_dummy(self):
        return (self.idx < 0)
      
	#FRANCOIS
    def est_lemma(self,lemma):
		if self.feats.has_key('lemma'):
			return self.feats['lemma'] == lemma
		else:
			return False
	
	#FRANCOIS
    def get_lemma(self):
    	if self.feats.has_key('lemma'):
    		return self.feats['lemma']
    	else:
    		return "_NO_LEMMA_"
	
	#FRANCOIS		
    def est_categorie(self,cat):
		if self.feats.has_key('pos'):
			return re.match("^"+cat+"$",self.feats['pos'])
		else:
			return False
	
	#FRANCOIS		
    def get_categorie(self):
		if self.feats.has_key('pos'):
			return self.feats['pos']
		else:
			return "_NO_POS_"
    def set_poor_tag(self):
        cat = self.get_categorie()
        ncat = cat
        if cat in ToPoorTags:
            ncat = ToPoorTags[cat]
        self.set_cat(ncat)


class DepEdge:
    
    def __init__(self,orig,label,dest):
        self.orig = orig
        self.dest = dest
        self.label = label

    def __eq__(self,other):
        return self.orig.__eq__(other.orig) and  self.dest.__eq__(other.dest) and self.label == other.label

    def __hash__(self):
        return self.print_triple().__hash__()

    def __str__(self):
        return self.print_triple()
     
    #FRANCOIS   
    def est_label(fonction):
    	return self.label == fonction 

    def totuple(self):
        return (self.orig,self.label,self.dest)

    def print_triple(self):
        try:
        	return "("+self.orig.print_vertex()+","+self.label+","+self.dest.print_vertex()+')'
        except UnicodeDecodeError:
        	return "("+self.orig.print_vertex()+","+self.label+","+self.dest.print_vertex()+')'
        except UnicodeEncodeError:
        	return "("+self.orig.print_vertex()+","+self.label+","+self.dest.print_vertex()+')'

    def print_parc700(self):
        return self.label.lower()+"("+self.orig.print_vertex()+","+self.dest.print_vertex()+')'
    # marie : pour homogénéité
    def print_parcPivot(self):
        return self.print_parc700()
		
class DepSentence:

    def __init__(self,id,file,validator,date,depforest):
        self.date = date
        self.validator = validator
        self.id = id
        self.file = file
        self.depforest = depforest

    def set_id(self,id):
        self.idx = id
        
    def set_date(self,date):
        self.date = date
    
    def set_validator(self,validator):
        self.validator = validator
    
    def set_treenum(self,treenum):
        self.id = treenum

    def set_file(self,file):
        self.file = file

    #printing section (pivot)
    def id2str(self):
        return 'id('+str(self.id)+','+str(self.file)+')\n'
    def date2str(self):
        return 'date('+'-'.join(self.date)+')\n'
    def validator2str(self):
        if isinstance(self.validator, str):
            return 'validators('+self.validator+')\n'
        return 'validators('+','.join(self.validator)+')\n'
    def sentence_form(self,decode=False):
        if decode:
            return self.decode_pivot_metachars(self.depforest.sentence())
        else:
            return 'sentence_form('+self.depforest.sentence()+')\n'
    def surfdeps2str(self):
        return 'surf_deps(\n'+self.depforest.triples2string(sorted=True)+')\n'
    def feats2str(self,out_lemma):
    	if out_lemma:
	        return 'features(\n'+self.depforest.features2string(['pos','lemma'],sorted=True)+')\n'
    	else:
	        return 'features(\n'+self.depforest.features2string(['pos'],sorted=True)+')\n'	    	
    def sentence2pivot(self,out_lemma=False):
        return 'sentence(\n'+self.id2str()+self.date2str()+self.validator2str()+self.sentence_form()+self.surfdeps2str()+self.feats2str(out_lemma)+')'

    def encode_pivot_metachars(self,string):
        string = re.sub('-','<M>',string)
        string = re.sub(',','<C>',string)
        string = re.sub('(','--LBR--',string)
        string = re.sub(')','--RBR--',string)
        return string

    def decode_pivot_metachars(self,string):
        string = re.sub('<M>','-',string)
        string = re.sub('<C>',',',string)
        string = re.sub('<D>','-',string) #dunno what's this D
        string = re.sub('--LBR--','(',string)
        string = re.sub('--RBR--',')',string)
        return string

	#FRANCOIS:
	# renvoie le sommet associé à l'indice en argument
    def get_vertex(self,index):
		return self.depforest.get_vertex(index)
		
	#FRANCOIS:
	# les labels seront en minuscules
    def lower_labels(self):
		nedges = set([])
		for edge in self.depforest.edges:
			edge=DepEdge(edge.orig, edge.label.lower(), edge.dest)
			nedges.add(edge)
		self.depforest.edges = nedges
	
	#FRANCOIS:
	# ajoute au label la catégorie du gouverneur	
    def fine_types(self):
		nedges = set([])
		for edge in self.depforest.edges:
			orig = edge.orig
			cat="?"
			for v in self.depforest.vertices:
				if v.idx == orig.idx:
					if v.feats.has_key('pos'):
						cat=v.feats['pos']
					else:
						cat="@"
			edge=DepEdge(edge.orig, edge.label+"_"+cat, edge.dest)
			nedges.add(edge)
		self.depforest.edges=nedges


#Example:

# dg = DependencyGraph()
# r = DepVertex("dort",2)
# v1 = DepVertex("le",0)
# v2 = DepVertex("chat",1)
# v3 = DepVertex("sur",3)
# v4 = DepVertex("le",4)
# v5 = DepVertex("paillasson",5)
# dg.add_edge(r,"subj",v2)
# dg.add_edge(v2,"det",v1)
# dg.add_edge(v2,"foo",v4)
# dg.add_edge(v2,"foo",v5)
# dg.add_edge(r,"mod",v5)
# dg.add_edge(v5,"prep",v3)
# dg.add_edge(v5,"det",v4)
# #print dg.triples2string()
# #print dg.sentence()
# dg.add_dummy_root()
# ds = DepSentence('ftb-1','flmlxxx','auto','120677',dg)
# print ds.sentence2pivot()

# if dg.is_connected():
#     print "Yes."
# else:
#     dg.repair_graph()
#     print "No."
# print dg.print_parc700()

# #print dg.vertices




