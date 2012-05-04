#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-

# A wrapper allowing to use libsvm for functional labelling 
# Requires to install libsvm and set the PYTHONPATH to the python dir in the libsvm directory.

import sys
import pickle
from svm import *
from random import shuffle

# This class implements simple functions for finding parameters of an SVM with an RBF based kernel (params = C and Gamma) and for storing the resulting model

class SVMLearner:
    def __init__(self,data_path,binary_data_table,grid_search,log2c=7,log2g=-9):
        sys.stderr.write("SVM :: Scaling up data...\n")
        if grid_search:
            (self.train_set,self.test_set) = self.fold_data(self.scale_data(binary_data_table),20)
            sys.stderr.write("SVM :: Training model...\n")
            model = self.grid_search()
            self.save_svm(data_path,model)
        else:
            (self.train_set,self.test_set) = self.fold_data(self.scale_data(binary_data_table),20)
            sys.stderr.write("SVM :: Training model...\n")
            model = self.train_RBF_model(log2c,log2g)
            self.save_svm(data_path,model)
    
    def save_svm(self,data_path,model):
        model.save(data_path+'.svm')
        f = open(data_path+".sf","w")
        pickle.dump((self.sf,self.low),f)
        f.close()
        sys.stderr.write('Model saved as '+data_path+'.svm')
    
    #SCALING (std linear scaling)
    def col_factor(self,binary_data_table,idx,low,high):
        fstrow = binary_data_table[0]
        min = fstrow[idx]
        max =fstrow[idx]
        for row in binary_data_table[1:]:
            val = row[idx]
            if val < min :
                min = val
            if val > max:
                max = val
        ini_range = float(max)-float(min)
        if ini_range == 0:
            ini_range = 1
        factor = float(high-low)/float(ini_range)
        return factor

    def scale_factors(self,binary_data_table,l,u):
        fctors = []
        for idx in range(1,len(binary_data_table[0])):
            fctors.append(self.col_factor(binary_data_table,idx,l,u))
        return fctors

    def scale_data(self,binary_data_table,l=-1,u=1):
        sf = self.scale_factors(binary_data_table,l,u)
        self.sf = sf
        self.low = l
        for row in binary_data_table:
            for i in range(1,len(row)):#1 -> do not scale labels
                row[i] = scale_one(row[i],sf[i-1],l)
        return binary_data_table

    #FOLDING
    def fold_data(self,datalist,nfold):
        shuffle(datalist)
        flen = int(float(len(datalist))/float(nfold))
        test = datalist[0:flen]
        train = datalist[flen:]
        return(train,test)

    def fold_data_iter(self,train,test,nfold):
        datalist = train + test
        flen = int(float(len(datalist))/float(nfold))
        test = datalist[0:flen]
        train = datalist[flen:]
        return(train,test)

    #VALIDATION
    def do_classes_values(self,datalist):
        classes = []
        vals = []
        for elt in datalist:
            classes.append(elt[0])
            vals.append(elt[1:])
        return (classes,vals)

    def accy(self,labels,target):
        c = 0
        for i in range(len(labels)):
            if labels[i] == target[i]:
                c = c + 1
        return float(c)/float(len(labels))*100.0

    #FINDING PARAMETERS
    #Implements a grid search algorithm with cross-validation
    def grid_search(self,c_begin=3,c_end=19,c_step=2,g_begin=3,g_end=-15,g_step=-2,nfold=5,maxveclength=50000):
        sys.stderr.write("SVM :: Grid search (Takes a while... be patient)...\n")
        max_acc = 0.0
        maxparams = (c_begin,g_begin)
        (labels,samples) = self.do_classes_values(self.train_set[:maxveclength])
        for c in range(c_begin,c_end+1,c_step):
            for g in range(g_begin,g_end-1,g_step):
                param = svm_parameter(kernel_type = RBF,C=2**c,gamma=2**g)
                prob = svm_problem(labels,samples)
                target = cross_validation(prob, param, nfold) 
                acc = self.accy(labels,target)
                self.grid_info(acc,c,g)
                if acc > max_acc:
                    max_acc = acc
                    maxparams = (c,g)
        sys.stderr.write("Best parameters :\n")
        self.grid_info(max_acc,maxparams[0],maxparams[1])
        sys.stderr.write("SVM :: Training final model with best parameters log2(C) = "+str(maxparams[0])+" log2(Gamma) = "+str(maxparams[1])+'\n')
        prob = svm_problem(labels,samples)
        param = svm_parameter(kernel_type = RBF, C=2**c,gamma=2**g)
        model = svm_model(prob, param)
        self.test_model(self.test_set,model)
        sys.stderr.write("Done.\n")
        return model

    def train_RBF_model(self,log2c,log2g):
        (labels,samples) = self.do_classes_values(self.train_set)
        prob = svm_problem(labels,samples)
        param = svm_parameter(kernel_type = RBF, C=2**log2c,gamma=2**log2g)
        model = svm_model(prob, param)
        self.test_model(self.test_set,model)
        sys.stderr.write("Done.\n")
        return model

    def test_model(self,test_data,svm_model):
        sys.stderr.write('Testing final model :\n')
        target = []
        labels = []
        for elt in test_data:
            target.append(svm_model.predict(elt[1:]))
            labels.append(elt[0])
        acc = self.accy(labels,target)
        sys.stderr.write('Accuracy on test data : '+str(acc)+'\n')

    def grid_info(self,acc,c,g):
        sys.stderr.write('Accurracy : '+str(acc)+'\n'+ 'log2(C) : '+ str(c) + '(C = '+str(2**c)+')\n'+'log2(Gamma) : '+ str(g) + '(Gamma = '+str(2**g)+')\n----------------------\n')


#Functions shared by both classes
def scale_one(num,scale_factor,l=0):
    return l + float(num) * float(scale_factor) 


class SVMClassifier:
    def __init__(self,datapath):
        self.model = self.load_model(datapath)

    def load_model(self,datapath):
        model = svm_model(datapath+'.svm')
        f = open(datapath+".sf")
        (self.scale_factors,self.lowerbound) = pickle.load(f)
        f.close()
        return model
    
    #Note : this does not guarantee that the resulting values lie in the interval specified at learning
    def scale_features(self,features):
        scled = []
        for i in range(len(features)):
            scled.append(scale_one(features[i],self.scale_factors[i],self.lowerbound))
        return scled

    def classify(self,features):
        return self.model.predict(self.scale_features(features))


