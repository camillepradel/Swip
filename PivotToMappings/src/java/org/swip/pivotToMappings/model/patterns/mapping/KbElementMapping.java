package org.swip.pivotToMappings.model.patterns.mapping;

import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.pivotToMappings.sparql.SparqlServer;


public class KbElementMapping extends ElementMapping {

    String firstlyMatchedOntResourceUri = null;
    String bestLabel = null;

    public KbElementMapping() {
    }

    public KbElementMapping(PatternElement pe, QueryElement qe, float trustMark, String firstlyMatchedOntResource, String bestLabel, ElementMapping impliedBy) {
        super(pe, qe, trustMark, impliedBy);
        this.firstlyMatchedOntResourceUri = firstlyMatchedOntResource;
        this.bestLabel = bestLabel;
    }

    public String getBestLabel() {
        return bestLabel;
    }

    public void setBestLabel(String bestLabel) {
        this.bestLabel = bestLabel;
    }

    public String getFirstlyMatchedOntResourceUri() {
        return firstlyMatchedOntResourceUri;
    }

    public void setFirstlyMatchedOntResourceUri(String firstlyMatchedOntResourceUri) {
        this.firstlyMatchedOntResourceUri = firstlyMatchedOntResourceUri;
    }

    @Override
    public String getStringForSentence(SparqlServer sparqlServer) {
        if (sparqlServer.isClass(this.firstlyMatchedOntResourceUri)) {
            return "a " + this.bestLabel;
        } else {
            return this.bestLabel;
        }
    }

    public void changeValues(float trustMark, String bestLabel) {
        this.trustMark = trustMark;
        this.bestLabel = bestLabel;
    }

    @Override
    public String toString() {
        return "[KbElementMapping]" + patternElement + " -> " + queryElement + " - trust mark=" + trustMark + " - matched = " + firstlyMatchedOntResourceUri + " - label = " + bestLabel;
    }
    
}
