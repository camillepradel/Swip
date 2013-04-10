package org.swip.pivotToMappings.model.query.queryElement;

import org.apache.log4j.Logger;
import org.swip.pivotToMappings.model.query.antlr.LiteralException;

public class Literal extends QueryElement {

//    public static final float literalMatchingTrustMark = 1;
    private static final Logger logger = Logger.getLogger(QueryElement.class);
    static int uriId = 0;

    String stringType = null;
    String stringValue = null;
    String stringUri = null;

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


    public String getStringType() {
        return stringType;
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String getStringForQueryUri() {
        return (stringValue.equals("?")? "qm" : stringValue) + "_" + stringType;
    }

    @Override
    public String getStringUri(String queryUri, String queriesNamedGraphUri) {
        if (this.stringUri == null) {
            this.stringUri = queryUri + "/literal" + (uriId++);
        }
        return stringUri;
    }

    public void setStringType(String stringType) {
        this.stringType = stringType;
    }

    @Override
    public String toString() {
        return "Literal{" + "queried=" + queried + " - type=" + stringType + " - value=" + stringValue + '}';
    }
}
