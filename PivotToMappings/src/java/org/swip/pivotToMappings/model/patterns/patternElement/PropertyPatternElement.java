package org.swip.pivotToMappings.model.patterns.patternElement;

import java.util.LinkedList;
import java.util.List;
import org.swip.utils.sparql.SparqlServer;

public final class PropertyPatternElement extends KbPatternElement {

    private List<Integer> referedElements = new LinkedList<Integer>();

    public PropertyPatternElement() {
        super();
    }

    public PropertyPatternElement(int id, String uri, boolean qualifying,  List<Integer> referedElements) {
        super(uri, qualifying);
        this.id = id;
        this.referedElements = referedElements;
    }

    /**
     * @return the referedElements
     */
    public List<Integer> getReferedElements() {
        return referedElements;
    }

    /**
     * @param referedElements the referedElements to set
     */
    public void setReferedElements(List<Integer> referedElements) {
        this.referedElements = referedElements;
    }

    @Override
    public String getDefaultStringForSentence(SparqlServer sparqlServer) {
         String label = sparqlServer.getALabel(this.getUri());
        return (label == null ? "no label found" : label);
    }

}
