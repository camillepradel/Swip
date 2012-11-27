package org.swip.pivotToMappings.model.patterns.mapping;

import org.apache.log4j.Logger;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.utils.sparql.SparqlServer;

public class InstanceAndClassElementMapping extends ElementMapping {

    private static final Logger logger = Logger.getLogger(InstanceAndClassElementMapping.class);
    
    // queryElement field from ElementMapping is the mapped instance
    // mapped class
    QueryElement queryElementClass = null;

    String firstlyMatchedInstance = null;
    String firstlyMatchedClass = null;
    String bestLabelInstance = null;
    String bestLabelClass = null;

    public InstanceAndClassElementMapping() {
    }

    public InstanceAndClassElementMapping(PatternElement pe, QueryElement qeInst, QueryElement qeClass, float trustMark, String firstlyMatchedInstance, String firstlyMatchedClass, String bestLabelInstance, String bestLabelClass, ElementMapping impliedBy) {
        super(pe, qeInst, trustMark, impliedBy);
        this.queryElementClass = qeClass;
        this.firstlyMatchedInstance = firstlyMatchedInstance;
        this.firstlyMatchedClass = firstlyMatchedClass;
        this.bestLabelInstance = bestLabelInstance;
        this.bestLabelClass = bestLabelClass;
    }

    public String getFirstlyMatchedClass() {
        return firstlyMatchedClass;
    }

    public String getBestLabelInstance() {
        return bestLabelInstance;
    }

    @Override
    public String getStringForSentence(SparqlServer sparqlServer, String lang) {
        return bestLabelInstance + "(" + bestLabelClass + ")";
    }

    @Override
    public String toString() {
        return "[InstanceAndClassElementMapping]" + patternElement + " -> " + queryElement + " - trust mark=" + trustMark + " - matched = (" + firstlyMatchedInstance + ", " + firstlyMatchedClass + ") - label = (" + bestLabelInstance + ", " + bestLabelClass + ")";
    }
    
}
