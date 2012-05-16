package org.swip.pivotToMappings.model.patterns.patternElement;

import org.swip.pivotToMappings.sparql.SparqlServer;


public final class ClassPatternElement extends KbPatternElement {

    public ClassPatternElement() {
    }

    public ClassPatternElement(int id, String uri, boolean qualifying) {
        super(uri, qualifying);
        this.id = id;
    }

    @Override
    public String getDefaultStringForSentence(SparqlServer sparqlServer) {
        String label = sparqlServer.getALabel(this.getUri());
        return (label == null ? "no label found" : "un(e) " + label);
    }
}
