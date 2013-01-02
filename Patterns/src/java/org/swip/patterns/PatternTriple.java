package org.swip.patterns;

import org.apache.log4j.Logger;

/**
 * class representing a subpattern (triple e1, e2, e3)
 */
public class PatternTriple extends Subpattern {

    private static Logger logger = Logger.getLogger(PatternTriple.class);
    private ClassPatternElement e1 = null;
    private PropertyPatternElement e2 = null;
    private PatternElement e3 = null;

    public PatternTriple() {
    }

    public PatternTriple(ClassPatternElement pe1, PropertyPatternElement pe2, PatternElement pe3) {
        e1 = pe1;
        e2 = pe2;
        e3 = pe3;
    }

    public ClassPatternElement getE1() {
        return e1;
    }

    public void setE1(ClassPatternElement e1) {
        this.e1 = e1;
    }

    public PropertyPatternElement getE2() {
        return e2;
    }

    public void setE2(PropertyPatternElement e2) {
        this.e2 = e2;
    }

    public PatternElement getE3() {
        return e3;
    }

    public void setE3(PatternElement e3) {
        this.e3 = e3;
    }

    @Override
    public String toString() {
        return " + - " + e1.toString() + "\n   - " + e2.toString() + "\n   - " + e3.toString();
    }

    public boolean contains(PatternElement pe) {
        return (this.e1 == pe || this.e2 == pe || this.e3 == pe);
    }
}