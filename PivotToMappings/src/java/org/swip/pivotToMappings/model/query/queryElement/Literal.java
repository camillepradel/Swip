package org.swip.pivotToMappings.model.query.queryElement;

import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.exceptions.LiteralException;
import org.swip.pivotToMappings.exceptions.LiteralRuntimeException;
import org.swip.pivotToMappings.model.patterns.patternElement.LiteralPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElementMatching;
import org.swip.utils.sparql.SparqlServer;

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
     
    @Override
      public boolean isAggregate()
    {
        return false;
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

    @Override
    public void match(SparqlServer server) {
        logger.info(this.stringType + "<" + this.stringValue + ">" + " matches with: ");
        logger.info(" (o) all literal pattern elements of type " + this.stringType + "\n");
        map(Literal.getUriFromType(this.stringType), 1);
    }

    void map(String type, float trustMark) {
        Collection<PatternElementMatching> concernedPEMs = PatternElement.patternElementMatchings.get(type);
        if (concernedPEMs != null) {
            for (PatternElementMatching pem : concernedPEMs) {
                float mapTrustMark = trustMark * pem.getTrustMarkFactor();
                ((LiteralPatternElement) pem.getPatternElement()).addLiteralMapping(this, mapTrustMark, null);
            }
        }
    }

    public String getStringForSparql(List<String> labelStrings) {
        String result = "";
        if (this.stringType.equals("date") && !this.stringValue.contains("-")) {
            //result = "?literal" + ++(Subpattern.varCount);
            result = this.getVarName();
            labelStrings.add("FILTER ( " + result + " <= '" + this.stringValue + "-12-31'^^xsd:date && " + result + " >= '" + this.stringValue + "-01-01'^^xsd:date )");
        } else {
            result = "\"" + this.stringValue + "\"^^xsd:" + this.stringType;
        }
        logger.info("result: " + result);
        return result;
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
    public String getStringRepresentation(String lang, boolean isNumerciDataProperty, int id, boolean generalized) {
        //return this.stringType + "<" + this.stringValue + ">";
        return this.stringValue;
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }
}
