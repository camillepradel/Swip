package org.swip.patterns;

abstract public class Subpattern {

    public static int varCount = 0;    

    public Subpattern() {
    }

    public static int getVarCount() {
        return varCount;
    }

    public static void setVarCount(int varCount) {
        Subpattern.varCount = varCount;
    }
    
}
