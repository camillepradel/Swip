package org.swip.exchange;

import java.text.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

public class MyDependencyNode {

    String form = null;
    String lemma = null;
    String postag = null;
    MyEdge headEdge = null;
    MyEdge[] leftDependentEdges = null;
    MyEdge[] rightDependentEdges = null;

    public MyDependencyNode() {
    }

    public MyDependencyNode(String json) throws ParseException {
        JSONObject jo = new JSONObject(json);
        this.form = jo.getString("form");
        this.lemma = jo.getString("lemma");
        this.postag = jo.getString("postag");
        this.headEdge = new MyEdge(jo.getString("headEdge"));
        JSONArray array = jo.getJSONArray("leftDependentEdges");
        int length = array.length();
        this.leftDependentEdges = new MyEdge[length];
        for (int i=0; i<length; i++) {
            this.leftDependentEdges[i] = new MyEdge(array.getString(i));
        }
        array = jo.getJSONArray("rightDependentEdges");
        length = array.length();
        this.rightDependentEdges = new MyEdge[length];
        for (int i=0; i<length; i++) {
            this.rightDependentEdges[i] = new MyEdge(array.getString(i));
        }
    }

    public MyDependencyNode(String form, String lemma, String postag) {
        this.form = form;
        this.lemma = lemma;
        this.postag = postag;
    }

    public MyEdge[] getLeftDependentEdges() {
        return leftDependentEdges;
    }

    public void setLeftDependentEdges(MyEdge[] leftDependentEdges) {
        this.leftDependentEdges = leftDependentEdges;
    }

    public MyEdge[] getRightDependentEdges() {
        return rightDependentEdges;
    }

    public void setRightDependentEdges(MyEdge[] rightDependentEdges) {
        this.rightDependentEdges = rightDependentEdges;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public MyEdge getHeadEdge() {
        return headEdge;
    }

    public void setHeadEdge(MyEdge headEdge) {
        this.headEdge = headEdge;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getPostag() {
        return postag;
    }

    public void setPostag(String postag) {
        this.postag = postag;
    }

    @Override
    public String toString() {
        String result = "MyDependencyNode:\n\t\t\t- form=" + form + ", lemma=" + lemma + ", postag=" + postag;
        result += "\n\t\t\t- headEdge: " + headEdge;
        result += "\n\t\t\t- leftDependentEdges: ";
        for (MyEdge e : this.leftDependentEdges) {
            result += "\n\t\t\t\t- " + e;
        }
        result += "\n\t\t\t- rightDependentEdges: ";
        for (MyEdge e : this.rightDependentEdges) {
            result += "\n\t\t\t\t- " + e;
        }
        return result;
    }
}