package org.swip.patterns;

import org.apache.log4j.Logger;

public abstract class KbPatternElement extends PatternElement {

    private static final Logger logger = Logger.getLogger(KbPatternElement.class);
    private String uri;
    private static float ancestorCoef = (float) 0.5;
    private static float descendantCoef = (float) 0.9;
//    private List<KbElementMapping> mappings = new LinkedList<KbElementMapping>();

    public KbPatternElement() {
    }

    public KbPatternElement(String uri, boolean qualifying) {
        this.uri = uri;
        this.qualifying = qualifying;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "[KbPatternElement]" + getUri() + " - id=" + id + " - qualifying=" + qualifying;
    }
}
