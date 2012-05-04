#!/usr/bin/env python
#
#  perceptron.py
#  
# B. Crabbe
# March 2008
#
# This implements the multiclass "flat" perceptron as described in K.Crammer and Y.Singer 2003a
# This is an example of linear classifier with a gradient descent learning function.
# Warning : the learning algorithm will take hours if the number of features is really HUGE, try to avoid using more than 500 000 features
# However the classification algorithm is extra fast whatever the size
#
###############################################################################################################

import pickle
import sys
import classifier_generic
from data_tables import *
from random import *

class Perceptron:
    
    def __init__(self,path_to_data,learn=True):
	self.path_to_data = path_to_data
	self.data = DataTable(path_to_data)
	#insert here a function for attributes binarisation
	self.classes = self.data.classes
	self.attributes = self.data.attributes
	self.weight_matrix = self.init_weights(self.classes,self.attributes)
        self.learn(self.load_data(path_to_data))

    def load_data(self,path_to_data):
	return classifier_generic.load_test(self,path_to_data,suffix='.data')

    def init_weights(self,classes,attributes):
	matrix = []
	for elt in classes:
	    row = []
	    for att in attributes:
		#row.append(random()-0.5)
                row.append(0.0)
	    matrix.append(row)
        print matrix
	return matrix

    def inner_product(self,vecA,vecB):
	if (len(vecA) <> len(vecB)):
	    sys.stderr.write('FATAL ERROR : vector of different lengths in dot product --> aborting...\n')
	    print vecA
	    print vecB
	    sys.exit(1)
	i = 0
	inner_prod = 0.0
	for i in range(len(vecA)):
	    inner_prod = inner_prod + float(vecA[i]) * float(vecB[i])
	return inner_prod
	    
    def similarity_score(self,vecA,vecB):
	#could use a kernel here instead
	return self.inner_product(vecA,vecB)

    def classify(self,case_descr):
	max_score = -1e308
	argmax = -1
	for j in range(len(self.weight_matrix)):
	    row_score = self.similarity_score(self.weight_matrix[j],case_descr)
	    if row_score > max_score:
		max_score = row_score
		argmax = j
	return argmax

    def learn(self,data_table,verbose=True,maxIter=500):
	if(verbose):
	    sys.stderr.write('Multi Class Perceptron training reports : \n')
	epoch = 0
	acc = 0
	while epoch < maxIter and acc <> 1 :
	    succ = 0
	    for i in range(len(data_table)):
		correct_class = int(data_table[i][-1])
                print 'correct class : ',correct_class
                instance_vector = data_table[i][:-1]
		correct_score = self.similarity_score(self.weight_matrix[correct_class],instance_vector)
		error_rows = []
                max_score = -1e308 
                argmax = -1
		for j in range(len(self.weight_matrix)):
                    if j <> correct_class:
                        row_score = self.similarity_score(self.weight_matrix[j],instance_vector)
                        if row_score >= correct_score :
                            error_rows.append(j)
                        if row_score > max_score:
                            max_score = row_score
                            argmax = j
                print 'decision : ',argmax
		if len(error_rows) > 0: #ultraconservative updating
		    self.update_rows(correct_class,instance_vector,error_rows)
		else:
		    succ = succ + 1 #no update
                print instance_vector
                print len(error_rows)
                print self.weight_matrix
	    epoch = epoch + 1
	    acc = float(succ)/float(len(data_table))
	    if (verbose):
		sys.stderr.write('End of epoch num  '+str(epoch)+'\tOverall accurracy : '+str(acc)+'\n')
	    if (acc == 1):
		sys.stderr.write('I got the convergence !\n')
	    
    def update_rows(self,correct_class,instance_vector,error_vector_idxes,learning_rate=1):
        #Make the correct vector get closer to the instance 
	for i in range(len(self.weight_matrix[correct_class])):
	    self.weight_matrix[correct_class][i] =  self.weight_matrix[correct_class][i] + (learning_rate * float(instance_vector[i]))
        #Make the incorrect vectors get away from the instance
        corr_ratio = float(len(error_vector_idxes))
	for idx in error_vector_idxes:
	    for i in range(len(self.weight_matrix[idx])):
		self.weight_matrix[idx][i] = self.weight_matrix[idx][i] - learning_rate * (float(instance_vector[i])/corr_ratio)

    def save_model(self):
	f = open(self.path_to_data+".perceptron","w")
	pickle.dump(self.weight_matrix,f)
	f.close()

    def load_model(self):
	f = open(self.path_to_data+".perceptron","w")
	pickle.dump(self.weight_matrix,f)
	f.close()

    def load_test(self):
        return classifier_generic.load_test(self,self.path_to_data,suffix='.test')

    def run_eval(self,testtable,verbose=False):
        classifier_generic.run_eval(self,testtable,verbose)

    #BINARIZATION SECTION
    def binarize_features(self):
        new_atts = []
        for att in self.attributes:
            vals = self.data.att_vals[att]
            for val in vals:
                new_atts.append((att,val))
        return new_atts

    #Binarize case description (add the continuous + boolean + word info => type the attributes)
    def map_case_descr(self,case_descr,new_attributes):
        new_case_descr = []
        for i in range(len(new_attributes)):
            (att,val) = new_attributes[i]
            att_idx = self.get_idx(att) #this line is crappy (redo later)
            if case_descr[att_idx] == val:
                new_case_descr.append(1)
            else :
                new_case_descr.append(0)
        return new_case_descr

    def get_idx(self,att): #this is crap (redo later)
        for i in range(len(self.attributes)):
            if att == self.attributes[i]:
                return i
        return -1

learner = Perceptron("/Users/benoit/Desktop/sensor")
#data = learner.load_data("/Users/benoit/tools/dtrees/Data/monk3")
#print learner.weight_matrix,'\n'
#print data
test = learner.load_test()
learner.run_eval(test,verbose=True)
#atts = learner.binarize_features()
#case = learner.map_case_descr(['parler','v','a','p','s','-','continuous','rel','-'],atts)
#print atts
#print case
