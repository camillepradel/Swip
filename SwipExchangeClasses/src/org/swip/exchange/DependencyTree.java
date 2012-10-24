package org.swip.exchange;

import java.text.ParseException;
import org.apache.log4j.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;
import org.json.JSONObject;

@XmlRootElement
public class DependencyTree {
    
    private static final Logger logger = Logger.getLogger(DependencyTree.class);
    int sentenceHeadId = 0;
    MyDependencyNode[] dependencyNodes = null;

    public DependencyTree() {
    }

    public DependencyTree(String json) throws ParseException {
        JSONObject jo = new JSONObject(json);
        this.sentenceHeadId = jo.getInt("sentenceHeadId");
        JSONArray array = jo.getJSONArray("dependencyNodes");
        int length = array.length();
        this.dependencyNodes = new MyDependencyNode[length];
        for (int i=0; i<length; i++) {
            this.dependencyNodes[i] = new MyDependencyNode(array.getString(i));
        }
    }

    public DependencyTree(int sentenceHeadId, MyDependencyNode[] dependencyNodes) {
        this.sentenceHeadId = sentenceHeadId;
        this.dependencyNodes = dependencyNodes;
    }

    @Override
    public String toString() {
        String result = "DependencyTree{\n\tsentenceHeadId=" + sentenceHeadId + "\n\tdependencyNodes=";
        int i = 0;
        for (MyDependencyNode dn : this.dependencyNodes) {
            result += "\n\t\t- " + i++ + ") " + dn.toString();
        }
        result += "\n}";
        return result;
    }

    public MyDependencyNode[] getDependencyNodes() {
        return dependencyNodes;
    }

    public void setDependencyNodes(MyDependencyNode[] dependencyNodes) {
        this.dependencyNodes = dependencyNodes;
    }

    public int getSentenceHeadId() {
        return sentenceHeadId;
    }

    public void setSentenceHeadId(int sentenceHeadId) {
        this.sentenceHeadId = sentenceHeadId;
    }
}
