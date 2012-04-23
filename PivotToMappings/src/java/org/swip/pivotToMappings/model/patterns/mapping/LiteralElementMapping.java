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
    public String getStringForSentence(SparqlServer sparqlServer) {
        if (this.queryElement.isQueried()) {
            return this.patternElement.getDefaultStringForSentence(sparqlServer);
        }
        return this.queryElement.getStringValue();
    }
}
