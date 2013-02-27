package org.swip.pivotToMappings.model.query.queryElement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class Keyword extends QueryElement {

    private static final Logger logger = Logger.getLogger(Keyword.class);
    public static final Map<String, String> jokerTypeProperties = Collections.unmodifiableMap(new HashMap<String, String>() {

        {
            put("http://purl.org/ontology/mo/release_type", "http://purl.org/ontology/mo/ReleaseType");
        }
    });
    public static final String jokerTypePropertiesString = generate(jokerTypeProperties);

    private static String generate(Map<String, String> jokerTypeProperties) {
        String result = "(";
        for (String prop : jokerTypeProperties.keySet()) {
            result += "<" + prop + ">|";
        }
        result = result.substring(0, result.length() - 1) + ")";
        return result;
    }
    public static final String jokerTypePropertiesStringWithType = generateWithType(jokerTypeProperties);

    private static String generateWithType(Map<String, String> jokerTypeProperties) {
        String result = "( rdf:type | ";
        for (String prop : jokerTypeProperties.keySet()) {
            result += "<" + prop + ">|";
        }
        result = result.substring(0, result.length() - 1) + ")";
        return result;
    }
    public static Map<String, String> pseudoClasses = Collections.unmodifiableMap(new HashMap<String, String>() {

        {
            // http://purl.org/ontology/mo/release_type considered harmful
            put("http://purl.org/ontology/mo/album", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/audiobook", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/compilation", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/ep", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/interview", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/live", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/remix", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/single", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/soundtrack", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/spokenword", "http://purl.org/ontology/mo/MusicalManifestation");
        }
    });
    int id = 0;
    String keywordValue = null;
    String aggregat = "";
    String cond = "";
    boolean isAggregate = false;
    String stringUri = null;

    public Keyword() {
        super();
    }

    public Keyword(boolean queried, int id, String keywordValue) {
        super();
        super.queried = queried;
        this.id = id;
        this.keywordValue = keywordValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeywordValue() {
        return keywordValue;
    }

//    @Override
//    public String getVarName() {
//        return "?" + (this.keywordValue.replaceAll(" ", "_"));
//    }

    public void setKeywordValue(String keywordValue) {
        this.keywordValue = keywordValue;
    }

    public void setCond(String agg, String cond) {
        this.isAggregate = true;
        this.aggregat = agg;
        this.cond = cond;
    }

    @Override
    public String toString() {
        return "Keyword{\"" + keywordValue + "\" - queried=" + queried + " - id=" + id + " - cond = " + this.aggregat + this.cond + "}";
    }

    @Override
    public String getStringValue() {
        return this.keywordValue;
    }

    @Override
    public String getStringForQueryUri() {
        return this.keywordValue;
    }

    @Override
    public String getStringUri(String queryUri, String sparqlServerUri) {
        if (this.stringUri == null) {
            this.stringUri = "http://" + sparqlServerUri + "/" + this.getStringValue();
        }
        return stringUri;
    }
}
