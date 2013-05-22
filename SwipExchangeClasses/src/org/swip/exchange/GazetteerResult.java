package org.swip.exchange;

import java.text.ParseException;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONObject;

@XmlRootElement
public class GazetteerResult {
    String gazetteedQuery = null;

    public GazetteerResult() {
    }

    public GazetteerResult(String json) throws ParseException {
        JSONObject jo = new JSONObject(json);
        this.gazetteedQuery = jo.getString("gazetteedQuery");
    }

    public String getGazetteedQuery() {
        return gazetteedQuery;
    }

    public void setGazetteedQuery(String gazetteedQuery) {
        this.gazetteedQuery = gazetteedQuery;
    }
    
}
