package org.swip.pivotToMappings.model.patterns.patternElement;

import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.exceptions.LiteralException;
import org.swip.pivotToMappings.exceptions.PatternsParsingException;
import org.swip.pivotToMappings.model.patterns.mapping.ElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.LiteralElementMapping;
import org.swip.pivotToMappings.model.query.queryElement.Literal;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.utils.sparql.SparqlServer;

public class LiteralPatternElement extends PatternElement {

    String type = null;
//    List<LiteralElementMapping> possibleMappings = null;

    public LiteralPatternElement() {
    }

    public LiteralPatternElement(int id, String typeString) throws LiteralException {
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
    public void preprocess(SparqlServer sparqlServer) throws PatternsParsingException {
        addPatternElementMatching(Literal.getUriFromType(type), new PatternElementMatching(this, 1));
    }

    public void addLiteralMapping(QueryElement qe, float trustMark, ElementMapping impliedBy) {
        Controller.getInstance().addElementMappingForPatternElement(new LiteralElementMapping(this, qe, trustMark, impliedBy), this);
    }

    @Override
    public String toString() {
        return "literal of type " + type.toString() + " - id=" + id;
    }

    @Override
    public String getDefaultStringForSentence(SparqlServer sparqlServer) {
        if (type.equals("date")) {
            return "some date";
        } else if (type.equals("year")) {
            return "some year";
        } else {
            return "some pouet";
        }
    }

    @Override
    public void resetForNewQuery() {
//        this.possibleMappings = new LinkedList<LiteralElementMapping>();
        this.mappingIsCompulsory = false;
    }
}
