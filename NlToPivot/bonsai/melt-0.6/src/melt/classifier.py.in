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

import sys
import os
import math
import subprocess
from melt.utils import serialize, unserialize

try:
    import numpy as np
except ImportError:
    sys.exit('This module requires that numpy be installed') 



class MaxEntClassifier:

    def __init__( self ):
        self.classes = []
        self.feature2int = {}
        self.weights = np.zeros( (0,0) )
        self.bias_weights = np.zeros( (0,0) )
        return


    def load(self, dirpath ):
        print >> sys.stderr, "Loading model from %s" %dirpath
        self.classes = unserialize( os.path.join(dirpath, 'classes.json') )
        self.feature2int = unserialize( os.path.join(dirpath, 'feature_map.json') )
        self.weights = np.load( os.path.join(dirpath, 'weights.npy') )
        self.bias_weights = np.load( os.path.join(dirpath, 'bias_weights.npy') )
        print >> sys.stderr, "done."
        return


    def dump(self, dirpath):
        print >> sys.stderr, "Dumping model in %s..." %dirpath,
        serialize( self.classes, os.path.join(dirpath, 'classes.json') )
        serialize( self.feature2int, os.path.join(dirpath, 'feature_map.json') )
        self.weights.dump( os.path.join(dirpath, 'weights.npy') )
        self.bias_weights.dump( os.path.join(dirpath, 'bias_weights.npy') )
        print >> sys.stderr, "done."
        return

 

    def train_megam( self, datafile, prior_prec=1, repeat=5, maxit=100, bias=True ):
        
        try:
            megam_exec_path = os.environ.get("MEGAM_DIR",None)+"/megam.opt"
        except TypeError:
            sys.exit("Missing env variable for MEGAM_DIR. You need Megam to train models.")
        
        """ simple call to Megam executable for multiclass
        classification with some relevant options:
        
        -prior_prec: precision of Gaussian prior (megam default:1). It's
         the inverse variance. See http://www.cs.utah.edu/~hal/docs/daume04cg-bfgs.pdf.
        -repeat: repeat optimization <int> times (megam default:1)
        -maxit: max # of iterations (megam default:100)
        """        

        print >> sys.stderr, "Training Megam classifier..."        
        # build process command
        proc = [megam_exec_path, "-nc", "-repeat", repeat, "-lambda", prior_prec,"-maxi", maxit]
        if not bias:
            proc.append("-nobias") 
        proc.append("multiclass") # optimization type
        proc.append(datafile)
        proc = map(str,proc)
        # run process
        p = subprocess.Popen(proc, stdout=subprocess.PIPE)
        (outdata, errdata) = p.communicate()
        # check return code
        if p.returncode != 0: 
            print >> sys.stderr, errdata 
            raise OSError("Error while trying to execute "+" ".join(proc)) 
        # load model from megam output
        self.process_megam( outdata )
        # print basic model info
        print >> sys.stderr, "# of classes: %s" %len(self.classes)
        print >> sys.stderr, "# of features: %s" %len(self.feature2int)
        return 




    def process_megam( self, megam_str, encoding="utf-8" ):
        ''' process megam parameter file --- only supports multiclass
        named classes at the moment'''
        nc_str = "***NAMEDLABELSIDS***" 
        bias_str = "**BIAS**"
        lines = megam_str.strip().split('\n')
        # set classes
        line = lines[0]
        if line.startswith(nc_str):
            self.classes = map( str, line.split()[1:] )
            lines.pop(0)
        else:
            raise OSError("Error while reading Megam output: %s not class line" %line) 
        # bias weights
        line = lines[0]
        if line.startswith(bias_str):
            items = line.split()
            self.bias_weights = np.array( map(float, items[1:]) )
            lines.pop(0)
        else:
            self.bias_weights = np.zeros( (len(lines),len(self.classes)) ) 
        # set feature map and weight matrix
        self.weights = np.zeros( (len(lines),len(self.classes)) )            
        for i,line in enumerate(lines):
            items = line.strip().split()
            fname = items[0]
            self.feature2int[fname] = i
            self.weights[i] = np.array( map(float, items[1:]) )
        return 



    def categorize( self, features ):
        """ sum over feature weights and return class that receives
        highest overall weight 
        """
        weights = self.bias_weights
        for f in features:
            fint = self.feature2int.get(f,None)
            if not fint:
                continue
            fweights = self.weights[fint]
            # summing bias and fweights
            weights = weights+fweights
        # find highest weight sum
        best_weight = weights.max()
        # return class corresponding to highest weight sum
        best_cl_index = np.nonzero(weights == best_weight)[0][0] 
        return self.classes[best_cl_index]



    def class_distribution( self, features ):
        """ probability distribution over the different classes
        """
        # print >> sys.stderr, "event: %s" % features
        weights = self.bias_weights
        for f in features:
            fint = self.feature2int.get(f,None)
            if fint is None:
                continue
            fweights = self.weights[fint]
            # summing bias and fweights
            weights = weights+fweights
        # exponentiation of weight sum
        scores = map( math.exp, list(weights) )
        # compute normalization constant Z
        z = sum( scores )
        # compute probabilities
        probs = [ s/z for s in scores ]
        # return class/prob map
        return zip( self.classes, probs )





##################################
# tests


def test1():
    # train model from data file
    import sys
    datafile = sys.argv[1]
    classif = MaxEntClassifier()
    classif.train_megam( datafile )
    print "Classes:", classif.classes
    print "Feature map:", classif.feature2int
    print "Bias weights:", classif.bias_weights
    print "Weights:", classif.weights
    # test simple instance
    fv = ['Temperature=Hot', 'Humidity=Normal']
    print classif.class_distribution( fv )
    print classif.categorize( fv )
    # dump model
    classif.dump('.')
    return

def test2():
    classif = MaxEntClassifier()
    # load model
    classif.load('.')
    print "Classes:", classif.classes
    print "Feature map:", classif.feature2int
    print "Bias weights:", classif.bias_weights
    print "Weights:", classif.weights
    # test again
    fv = ['Temperature=Hot', 'Humidity=Normal']
    print classif.class_distribution( fv )
    print classif.categorize( fv )
    return
    


    
if __name__ == "__main__":
    test1()
    test2()
    
    

