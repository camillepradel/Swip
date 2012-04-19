package org.swip.pivotToMappings.model.patterns.patternElement;

public class PatternElementMatching {

    private PatternElement patternElement = null;
    private float trustMarkFactor = 1;

    public PatternElementMatching( PatternElement patternElement, float trustNoteFactor) {
        this.patternElement = patternElement;
        this.trustMarkFactor = trustNoteFactor;
    }

    public PatternElement getPatternElement() {
        return patternElement;
    }

    public float getTrustMarkFactor() {
        return trustMarkFactor;
    }

}
