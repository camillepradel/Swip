package org.swip.patterns.sentence;

import org.swip.patterns.PatternElement;

public class PeInTemplate extends SubsentenceTemplate {
    
    PatternElement pe = null;

    public PeInTemplate(PatternElement pe) {
        this.pe = pe;
    }

    public PatternElement getPe() {
        return pe;
    }

    @Override
    public String toString() {
        return "-" + pe.getId() + "- ";
    }
}
