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
from melt.corpus_reader import BrownReader


class ResultSink:
    pass


class AccuracySink(ResultSink):
    """Result sink that collects accuracy statistics"""

    def __init__(self):
        self.total = 0
        self.total_knowns = 0
        self.total_unknowns = 0
        self.correct = 0
        self.correct_knowns = 0
        self.correct_unknowns = 0
        self.correct_by_tag = {}
        self.pred_by_tag = {}
        self.total_by_tag = {}
        self.matrix = {}
        self.tags = {}
	self.model_predictions = []
        return

    def update(self,wd,gold_cl,pred_cl,know_words):
        self.total += 1
        self.total_by_tag[gold_cl] = self.total_by_tag.get(gold_cl,0) + 1
        if wd in know_words or wd.lower() in  know_words:
            self.total_knowns += 1
        else:
            self.total_unknowns += 1
        self.pred_by_tag[pred_cl] = self.pred_by_tag.get(pred_cl,0) + 1
        if (gold_cl == pred_cl):
            self.correct += 1
            self.correct_by_tag[pred_cl] = self.correct_by_tag.get(pred_cl,0) + 1
            if wd in know_words or wd.lower() in  know_words:
                self.correct_knowns += 1
            else:
                self.correct_unknowns += 1
        self.tags[gold_cl] = 1
        self.matrix[(gold_cl,pred_cl)] = self.matrix.get((gold_cl,pred_cl),0) + 1
	self.model_predictions.append((gold_cl,pred_cl))
        return


    def score(self):
        return self._score(self.correct, total=self.total)

    def score_knowns(self):
        return self._score(self.correct_knowns, self.total_knowns)

    def score_unknowns(self):
        return self._score(self.correct_unknowns, self.total_unknowns)

    def _score(self, correct, total, rounding=4 ):
        """Returns accuracy so far"""
        try:
            acc = correct / float(total)
            acc = round( acc, rounding)
        except ZeroDivisionError:
            acc = 0.0
        return acc

    def confusion(self):
        """Print confusion matrix"""
        print "Confusion matrix"
        print
        print '\tas'
        print 'is\t',"\t".join(self.tags.keys())
        for tag1 in self.tags.keys():
            print tag1,'\t',
            for tag2 in self.tags.keys():
                print self.matrix.get((tag1,tag2),0),'\t',
            print            
        return
        
    def predictions(self):
        truth_and_predicted = [str(x)+"\t"+y for (x,y) in self.model_predictions]
	print "====== Model predictions on test data ======"
        print "True\tPredicted"
	print "\n".join(truth_and_predicted)
	print "============================================"

    def rpf(self):
        """Print R-P-F scores by class labels"""
        print "Recall/Precision/F1 by class labels"
        print
        print "-"*80
        print "%10s | %10s %10s %10s | %10s %10s %10s" %("Label", "Recall", "Precison", "F1", "Correct", "Predicted", "Gold")
        print "-"*80
        for tag in self.tags:
            corr = self.correct_by_tag.get(tag,0)
            pred = self.pred_by_tag.get(tag,0)
            total = self.total_by_tag.get(tag,0)
            r = corr/float(total)
            p = 0.0
            if pred:
                p = corr/float(pred)
            f = 0.0
            if r+p:
                f = (2*r*p)/(r+p)
            print "%10s | %10s %10s %10s | %10s %10s %10s" %(tag, round(r,3), round(p,3), round(f,3), corr, pred, total)
        print "-"*80
        return
    


def compare_files( gold_file, pred_file, sink, known_words={}, quiet=True):
    gold = BrownReader( gold_file )
    pred = BrownReader( pred_file )
    s_ct = 0
    for gold_s in gold:
        s_ct += 1
        pred_s = pred.next()
        # compare tags
        for i in range(len(gold_s)):
            gwd,gtag = gold_s[i]
            try:
                pwd,ptag = pred_s[i]
            except IndexError:
                ptag = "UNK"
                print >> sys.stderr, "Warning: Missing prediction for Sentence #%s" %s_ct
            # update sinks
            sink.update(gwd,gtag,ptag,known_words)
            # errors
            if not quiet:
                if ptag<>gtag:
                    print >> sys.stderr, "%s: %s <==> *%s" %(gwd,gtag,ptag) 
            

    return




if __name__ == "__main__":

    ref_file, sys_file = sys.argv[1:]
    sink = AccuracySink()
    compare_files( ref_file, sys_file, sink )
    print "Acc: %s (%s/%s)" %(sink.score(),sink.correct,sink.total)
    
