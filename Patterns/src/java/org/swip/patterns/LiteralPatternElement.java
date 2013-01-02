package org.swip.patterns;

import org.apache.log4j.Logger;

public class LiteralPatternElement extends PatternElement {
    
    private static final Logger logger = Logger.getLogger(LiteralPatternElement.class);

    String type = null;
//    List<LiteralElementMapping> possibleMappings = null;

    public LiteralPatternElement() {
    }

    public LiteralPatternElement(int id, String typeString) {
//        this.possibleMappings = new LinkedList<LiteralElementMapping>();
        this.id = id;
        this.type = typeString;
        this.qualifying = true;
    }

//    public List<LiteralElementMapping> getPossibleMappings() {
//        return possibleMappings;
//    }

//    @Override
//    List<? extends ElementMapping> getElementMappings() {
//        return this.getPossibleMappings();
//    }

//    public void setPossibleMappings(List<LiteralElementMapping> possibleMappings) {
//        this.possibleMappings = possibleMappings;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "literal of type " + type.toString() + " - id=" + id;
    }
}
