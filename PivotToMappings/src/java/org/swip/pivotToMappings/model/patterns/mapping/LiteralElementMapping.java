package org.swip.pivotToMappings.model.patterns.mapping;

import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.query.queryElement.Literal;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.pivotToMappings.sparql.SparqlServer;


public class LiteralElementMapping extends ElementMapping {

    public LiteralElementMapping() {
    }

    public LiteralElementMapping(PatternElement pe, QueryElement qe, float trustMark, ElementMapping impliedBy) {
        super(pe, qe, trustMark, impliedBy);
    }

    @Override
    public String getStringForSentence(SparqlServer sparqlServer, String lang) {
        if (this.queryElement.isQueried()) {
            return this.patternElement.getDefaultStringForSentence(sparqlServer);
        }
        String s = this.queryElement.getStringValue();
        System.out.println("String for sentence LEM : "+s);
        return s;
    }
}
