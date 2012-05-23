package org.swip.pivotToMappings.model.patterns.subpattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.model.KbTypeEnum;
import org.swip.pivotToMappings.model.patterns.Pattern;
import org.swip.pivotToMappings.model.patterns.mapping.ElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.KbElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;
import org.swip.pivotToMappings.model.patterns.patternElement.ClassPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.KbPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.LiteralPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PropertyPatternElement;
import org.swip.pivotToMappings.model.query.queryElement.Literal;
import org.swip.pivotToMappings.sparql.SparqlServer;

/**
 * class representing a subpattern (triple e1, e2, e3)
 */
public class PatternTriple extends Subpattern {

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

    @Override
    public String toStringWithMapping(PatternToQueryMapping ptqm) {
        return "   + " + e1.toStringWithMapping(ptqm)
                + "\n     " + e2.toStringWithMapping(ptqm)
                + "\n     " + e3.toStringWithMapping(ptqm);
    }

    @Override
    public String generateSparqlWhere(PatternToQueryMapping ptqm, SparqlServer sparqlServer, Map<PatternElement, String> elementsStrings, Set<String> selectElements, HashMap<String, String> numerciDataPropertyElements) {
        LinkedList<String> typeStrings = new LinkedList<String>();
        HashMap<PatternElement, String> matchNumerciDataProperty = new HashMap<PatternElement, String>();
        String sparqlE1 = sparqlForElement(e1, typeStrings, ptqm, sparqlServer, elementsStrings, selectElements, matchNumerciDataProperty);
        String sparqlE2 = sparqlForElement(e2, typeStrings, ptqm, sparqlServer, elementsStrings, selectElements, matchNumerciDataProperty);
        String sparqlE3 = sparqlForElement(e3, typeStrings, ptqm, sparqlServer, elementsStrings, selectElements, matchNumerciDataProperty);
        String result = "       "
                + sparqlE1 + " "
                + sparqlE2 + " "
                + sparqlE3 + ".\n";
        
        if(!matchNumerciDataProperty.isEmpty())
            numerciDataPropertyElements.put(matchNumerciDataProperty.get(e2), sparqlE3);
        
        for (String typeString : typeStrings) {
            result += typeString;
        }

        return result;
    }

    private String sparqlForElement(PatternElement e, LinkedList<String> typeStrings, PatternToQueryMapping ptqm, SparqlServer sparqlServer, Map<PatternElement, String> elementsStrings, Set<String> selectElements, HashMap<PatternElement, String> numerciDataPropertyElements) {
        String elementString = elementsStrings.get(e);
        if (elementString == null) {
            List<ElementMapping> elementMappings = ptqm.getElementMappings(e);
            if (!elementMappings.isEmpty()) { // element mapped
                ElementMapping elementMapping = elementMappings.get(0);
                if (elementMapping instanceof KbElementMapping) {
                    KbElementMapping kbElementMapping = (KbElementMapping) elementMapping;
                    String varName = kbElementMapping.getQueryElement().getVarName();
                    String firstlyMatchedOntResource = kbElementMapping.getFirstlyMatchedOntResourceUri();

                    String toInsert = "";
                    if(kbElementMapping.isGeneralized())
                        toInsert = "_gen" + kbElementMapping.getPatternElement().getId() + "_";
                    else
                        toInsert = "<" + kbElementMapping.getFirstlyMatchedOntResourceUri() + ">";

                    if (sparqlServer.isClass(firstlyMatchedOntResource)) { // class
                        //elementString = "?var" + ++(Subpattern.varCount);
                        elementString = varName;
                        typeStrings.add("       " + elementString + " rdf:type " + toInsert + ".\n");
                        if (kbElementMapping.getQueryElement().isQueried()) {
                            selectElements.add(elementString);
                        }
                    } else if(kbElementMapping.getKbType() == KbTypeEnum.DATAPROPNUM) {
                        numerciDataPropertyElements.put(e, kbElementMapping.getQueryElement().getStringValue());
                        elementString = toInsert;
                    } else if (sparqlServer.isProperty(firstlyMatchedOntResource)) { // property
                        elementString = toInsert;
                    } else { // instance
                        //elementString = "?var" + ++(Subpattern.varCount);
                        elementString = varName;
                        List<String> types = sparqlServer.listTypes(firstlyMatchedOntResource);
                        for (String type : types) {
                            typeStrings.add("       " + elementString + " rdf:type " + toInsert + ".\n");
                        }
                        String matchedLabel = ((KbElementMapping) ptqm.getElementMappings(e).get(0)).getBestLabel();
                        /*typeStrings.add(
                                "       { " + elementString + " <http://purl.org/dc/elements/1.1/title> \"" + matchedLabel + "\". } "
                                + "       UNION "
                                + "       { " + elementString + " rdfs:label \"" + matchedLabel + "\". } ");*/
                        typeStrings.add("       " + elementString + " (<http://purl.org/dc/elements/1.1/title>|rdfs:label) \"" + matchedLabel + "\"@fr.\n");
                    }
                } else { // literal
                    String varName = elementMapping.getQueryElement().getVarName();
                    if (elementMapping.getQueryElement().isQueried()) {
                        //elementString = "?literal" + ++(Subpattern.varCount);
                         elementString = varName;
                        // TODO: eventuellemnt contraindre le type du literal avec FILTER (datatype...
                        selectElements.add(elementString);
                    } else {
                        elementString = ((Literal) elementMapping.getQueryElement()).getStringForSparql(typeStrings);
                    }
                }
            } else { // element not mapped
                if (e instanceof ClassPatternElement) {
                    elementString = "?var" + ++(Subpattern.varCount);
                    String type = ((ClassPatternElement) e).getUri();
                    if (!type.equals("BlankNode")) {
                        typeStrings.add("       " + elementString + " rdf:type <" + type + ">.");
                    }
                } else if (e instanceof PropertyPatternElement) {
                    elementString = "<" + ((PropertyPatternElement) e).getUri() + ">";
                } else { // literal
                    elementString = "?var" + ++(Subpattern.varCount);
                    // TODO: eventuellemnt contraindre le type du literal avec FILTER (datatype...
                }
            }
            elementsStrings.put(e, elementString);
        }
        return elementString;
    }

    public boolean contains(PatternElement pe) {
        return (this.e1 == pe || this.e2 == pe || this.e3 == pe);
    }

    @Override
    public void finalizeMapping(SparqlServer serv, Pattern p) {
        List<PatternElement> elementsToMap = new LinkedList<PatternElement>();
        for (int id : this.e2.getReferedElements()) {
            elementsToMap.add(p.getPatternElementById(id));
        }
        for (ElementMapping em2 : Controller.getInstance().getElementMappingsForPatternElement(this.e2)) {
            if (!em2.getQueryElement().getRoles().usedAsProperty()) {
                for (PatternElement elementToMap : elementsToMap) {
                    if (elementToMap instanceof KbPatternElement) {
                        KbPatternElement kbe = (KbPatternElement) elementToMap;
                        kbe.addKbMapping(em2.getQueryElement(), em2.getTrustMark(), kbe.getUri(), serv.getALabel(kbe.getUri()), em2, KbTypeEnum.CLASS);
                    } else if (elementToMap instanceof LiteralPatternElement) {
                        LiteralPatternElement le = (LiteralPatternElement) elementToMap;
                        le.addLiteralMapping(em2.getQueryElement(), em2.getTrustMark(), em2);
                    }
                }
            }
        }
    }
}