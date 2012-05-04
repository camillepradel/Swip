#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#
# data_tables.py
#
# Manages data tables for classifiers in Quinlan's c.4.5 format
#
# Author : Benoit Crabbé
# Date : Feb 2007
#

import re

class DataTable:

    def __init__(self,path_to_data): 
        (self.classes,self.attributes,self.att_vals) = self.parse_c45_names_file(path_to_data+".names")
        (self.datasize,self.counts) = self.parse_c45_data_file(path_to_data+".data")

    #Shows the classes 
    def show_classes(self):
        for target in self.classes:
            print target

    #Returns the legal values of a given feature
    def attribute_values(self,attname):
        if attname in self.att_vals.keys():
            return self.att_vals[attname]
        return None

    #Returns the number of occurrences of a given target class in the training set
    def class_counts(self,classname):
        if ('-CLASS-','-CLASS-',classname) in self.counts.keys():
            return self.counts[('-CLASS-','-CLASS-',classname)]
        else: #error case
            return 0

    #Returns the observed number of times the attribute has value v for a given class
    def att_val_counts(self,targetclass,attname,value):
        if (targetclass,attname,value) in self.counts.keys():
            return self.counts[(targetclass,attname,value)]
        else:
            return 0


    #Returns the observed number of different values for a given attribute given a class
    #TODO : There's a mistake out there, please check it out !!!
        #FOUND : the mistake concerns continuous values !
        #whose declared values are declared continuous
        #NO mistake here should be adressed somewhere else (discretise continuous domains before !)
    def att_counts(self,attname,classname):
        att_set = set(self.attribute_values(attname))
        count = 0
        for elt in att_set:
            if (classname,attname,elt) in self.counts.keys():
                count = count + 1
        return count

    #Returns the number of lines in the data
    def data_size():
        return self.data_size

    #Returns the names of the features
    def attribute_list(self):
        for feature in self.att_vals.keys():
            s = feature +" : " 
            for val in self.att_vals[feature]:
                s = s + val +" "
            print s

    def parse_c45_names_file(self,filepath):
        f = open(filepath)
        lines = f.readlines()
        first = True
        classes = []
        attribute_val = {}
        attlist = [] #ordered list of attributes
        for line in lines:
            #1. Throw away whitelines
            if(re.match("^\s*$",line)):
                continue
            #2. Throw away comments and trailing dots
            parts = re.split("\s*\|",line[:-2])
            wline = parts[0]
            if first:
                classes = re.split("\s*,\s*",wline)
                first = False
            else:
                (attribute,values) = re.split("\s*:\s*",wline)
                values = re.split("\s*,\s*",values)
                attribute_val[attribute]=values
                attlist.append(attribute)
        return (classes,attlist,attribute_val)

    def parse_c45_data_file(self,filepath):
        f = open(filepath)
        lines = f.readlines()
        counts = {} 
        for line in lines:
            wline = line[:-1]#chop it !
            vals = re.split(",",wline)
            targetclass = vals[-1]
            i = 0
            for i in range(len(vals)):
                if(i == len(vals)-1):
                    att = '-CLASS-'
                    table_key = (att,att,vals[i])
                else:
                    att = self.attributes[i]
                    table_key = (targetclass,att,vals[i])
                if table_key in counts.keys():
                    counts[table_key] = counts[table_key] + 1
                else:
                    counts[table_key] = 1
                i = i + 1
        return (len(lines),counts)


# dt = DataTable("/Users/Benoit/tools/dtrees/Data/functions")
# dt.show_classes()
# print dt.attribute_values("head-word")
# print dt.counts
# print dt.class_counts("SUBJ")
# print dt.att_val_counts("SUBJ","head-word","manger")
