package org.swip.pivotToMappings.model.patterns.mapping;

import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.pivotToMappings.sparql.SparqlServer;


public abstract class ElementMapping {

    PatternElement patternElement = null;
    QueryElement queryElement = null;
    float trustMark;
    ElementMapping impliedBy = null;

    public ElementMapping() {
    }

    public ElementMapping(PatternElement patternElement, QueryElement queryElement, float trustMark, ElementMapping impliedBy) {
        this.patternElement = patternElement;
        this.queryElement = queryElement;
        this.trustMark = trustMark;
        this.impliedBy = impliedBy;
    }

    public ElementMapping getImpliedBy() {
        return impliedBy;
    }

    public void setImpliedBy(ElementMapping impliedBy) {
        this.impliedBy = impliedBy;
    }

    public PatternElement getPatternElement() {
        return patternElement;
    }

    public void setPatternElement(PatternElement patternElement) {
        this.patternElement = patternElement;
    }

    public QueryElement getQueryElement() {
        return queryElement;
    }

    public void setQueryElement(QueryElement queryElement) {
        this.queryElement = queryElement;
    }

    public float getTrustMark() {
        return trustMark;
    }

    public void setTrustMark(float trustMark) {
        this.trustMark = trustMark;
    }

    abstract public String getStringForSentence(SparqlServer sparqlServer);

    @Override
    public String toString() {
        return patternElement + " -> " + queryElement + " - trust mark=" + trustMark;
    }
}
