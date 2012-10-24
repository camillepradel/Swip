package org.swip.exchange;

import java.text.ParseException;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONObject;

@XmlRootElement
public class NlToPivotResult {
    String gazetteedQuery = null;
    DependencyTree dependencyTree = null;
    String pivotQuery = null;

    public NlToPivotResult() {
    }

    public NlToPivotResult(String json) throws ParseException {
        JSONObject jo = new JSONObject(json);
        this.gazetteedQuery = jo.getString("gazetteedQuery");
        this.dependencyTree = new DependencyTree(jo.getString("dependencyTree"));
        this.pivotQuery = jo.getString("pivotQuery");
    }

    public DependencyTree getDependencyTree() {
        return dependencyTree;
    }

    public void setDependencyTree(DependencyTree dependencyTree) {
        this.dependencyTree = dependencyTree;
    }

    public String getGazetteedQuery() {
        return gazetteedQuery;
    }

    public void setGazetteedQuery(String gazetteedQuery) {
        this.gazetteedQuery = gazetteedQuery;
    }

    public String getPivotQuery() {
        return pivotQuery;
    }

    public void setPivotQuery(String pivotQuery) {
        this.pivotQuery = pivotQuery;
    }
    
}
