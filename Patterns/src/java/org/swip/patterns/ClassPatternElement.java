package org.swip.patterns;

import org.apache.log4j.Logger;


public final class ClassPatternElement extends KbPatternElement {

    private static Logger logger = Logger.getLogger(ClassPatternElement.class);

    public ClassPatternElement() {
    }

    public ClassPatternElement(int id, String uri, boolean qualifying) {
        super(uri, qualifying);
        this.id = id;
    }
}
