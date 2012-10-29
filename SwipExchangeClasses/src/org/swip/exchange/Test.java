package org.swip.exchange;

import java.text.ParseException;

public class Test {

    public static void main(String[] args) throws ParseException {
        DependencyTree dt = new DependencyTree("{'sentenceHeadId':3,'dependencyNodes':[{'form':'Was','lemma':'be','postag':'VBD','headEdge':{'headNodeId':3,'dependentNodeId':0,'deprel':'cop'},'leftDependentEdges':[],'rightDependentEdges':[]},{'form':'Keith_Richards','lemma':'Keith_Richards','postag':'RB','headEdge':{'headNodeId':3,'dependentNodeId':1,'deprel':'advmod'},'leftDependentEdges':[],'rightDependentEdges':[]},{'form':'a','lemma':'a','postag':'DT','headEdge':{'headNodeId':3,'dependentNodeId':2,'deprel':'det'},'leftDependentEdges':[],'rightDependentEdges':[]},{'form':'member','lemma':'member','postag':'NN','headEdge':{'headNodeId':-1,'dependentNodeId':3,'deprel':'null'},'leftDependentEdges':[{'headNodeId':3,'dependentNodeId':0,'deprel':'cop'},{'headNodeId':3,'dependentNodeId':1,'deprel':'advmod'},{'headNodeId':3,'dependentNodeId':2,'deprel':'det'}],'rightDependentEdges':[{'headNodeId':3,'dependentNodeId':4,'deprel':'prep'},{'headNodeId':3,'dependentNodeId':6,'deprel':'punct'}]},{'form':'of','lemma':'of','postag':'IN','headEdge':{'headNodeId':3,'dependentNodeId':4,'deprel':'prep'},'leftDependentEdges':[],'rightDependentEdges':[{'headNodeId':4,'dependentNodeId':5,'deprel':'pobj'}]},{'form':'The_Rolling_Stones','lemma':'The_Rolling_Stones','postag':'NP','headEdge':{'headNodeId':4,'dependentNodeId':5,'deprel':'pobj'},'leftDependentEdges':[],'rightDependentEdges':[]},{'form':'?','lemma':'?','postag':'SENT','headEdge':{'headNodeId':3,'dependentNodeId':6,'deprel':'punct'},'leftDependentEdges':[],'rightDependentEdges':[]}]}");
        System.out.println(dt.toString());
    }
}
