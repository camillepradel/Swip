package org.swip.patterns.sentence;

public class StaticStringInTemplate extends SubsentenceTemplate {
    
    private String stringValue = null;

    public StaticStringInTemplate(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return "\"" + this.stringValue + "\" ";
    }    
}
