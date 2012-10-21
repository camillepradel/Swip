package org.swip.exchange.parser;

import java.text.ParseException;
import org.json.JSONObject;

public class MyEdge {

    int headNodeId = -1;
    int dependentNodeId = -1;
    String deprel = null;

    public MyEdge() {
    }

    public MyEdge(String json) throws ParseException {
        JSONObject jo = new JSONObject(json);
        this.headNodeId = jo.getInt("headNodeId");
        this.dependentNodeId = jo.getInt("dependentNodeId");
        this.deprel = jo.getString("deprel");

    }

    public MyEdge(int headNodeId, int dependentNodeId, String deprel) {
        this.headNodeId = headNodeId;
        this.dependentNodeId = dependentNodeId;
        this.deprel = deprel;
    }

    public int getDependentNodeId() {
        return dependentNodeId;
    }

    public void setDependentNodeId(int dependentNodeId) {
        this.dependentNodeId = dependentNodeId;
    }

    public String getDeprel() {
        return deprel;
    }

    public void setDeprel(String deprel) {
        this.deprel = deprel;
    }

    public int getHeadNodeId() {
        return headNodeId;
    }

    public void setHeadNodeId(int headNodeId) {
        this.headNodeId = headNodeId;
    }

    @Override
    public String toString() {
        return "MyEdge{" + headNodeId + "->" + dependentNodeId + " deprel=" + deprel + '}';
    }
}