package org.swip.pivotToMappings.model.query.queryElement;

import java.util.HashMap;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.model.query.antlr.LiteralException;
import org.swip.pivotToMappings.model.query.antlr.LiteralRuntimeException;

public class Literal extends QueryElement {

    public static final float literalMatchingTrustMark = 1;
    static HashMap<String, LiteralTypeElements> literalTypes = buildLiteralTypes();
    private static final Logger logger = Logger.getLogger(QueryElement.class);

    static HashMap<String, LiteralTypeElements> buildLiteralTypes() {
        HashMap<String, LiteralTypeElements> localLiteralTypes = new HashMap<String, LiteralTypeElements>();
        localLiteralTypes.put("date", new LiteralTypeElements("date", "some date", "http://www.w3.org/2001/XMLSchema#dateTime"));
        localLiteralTypes.put("decimal", new LiteralTypeElements("decimal", "some decimal", "http://www.w3.org/2001/XMLSchema#decimal"));
        return localLiteralTypes;
    }

    public static String getUriFromType(String type) throws LiteralRuntimeException {
        LiteralTypeElements lte = literalTypes.get(type);
        if (lte == null) {
            throw new LiteralRuntimeException("unsupported literal: " + type);
        }
        return lte.typeUri;
    }

    String stringType = null;
    String stringValue = null;

    public Literal() {
        super();
    }

    public Literal(String stringType, String stringValue) throws LiteralException {
        super();
        if (stringValue.equals("?")) {
            super.queried = true;
        }
        this.stringType = stringType;
        this.stringValue = stringValue;
    }

    static int varNameId = 0;
    @Override
     public String getVarName()
    {
//        return "?"+(this.stringValue.replaceAll(" ", "_"));
        return "?literal" + varNameId++;
    }
      
    public static HashMap<String, LiteralTypeElements> getLiteralTypes() {
        return literalTypes;
    }

    public static void setLiteralTypes(HashMap<String, LiteralTypeElements> literalTypes) {
        Literal.literalTypes = literalTypes;
    }

    public String getStringType() {
        return stringType;
    }

    public void setStringType(String stringType) {
        this.stringType = stringType;
    }

    static private class LiteralTypeElements {

        String name = null;
        String defaultStringForSentence = null;
        String typeUri = null;

        public LiteralTypeElements(String name, String defaultStringForSentence, String typeUri) {
            this.name = name;
            this.defaultStringForSentence = defaultStringForSentence;
            this.typeUri = typeUri;
        }
    }

    @Override
    public String toString() {
        return "Literal{" + "queried=" + queried + " - type=" + stringType + " - value=" + stringValue + '}';
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }
}
