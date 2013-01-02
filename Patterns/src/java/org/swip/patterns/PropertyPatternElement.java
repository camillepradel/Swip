package org.swip.patterns;

import java.util.LinkedList;
import java.util.List;

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
}
