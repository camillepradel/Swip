package org.swip.pivotToMappings.model.patterns.patternElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.exceptions.PatternsParsingException;
import org.swip.pivotToMappings.model.patterns.mapping.ElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;
import org.swip.pivotToMappings.model.query.Query;
import org.swip.pivotToMappings.sparql.SparqlServer;
import org.swip.pivotToMappings.utils.CombinationGenerator;

/**
 * abstract class representing a pattern element
 */
public abstract class PatternElement {

    private static final Logger logger = Logger.getLogger(PatternElement.class);
    int id = 0;
    /**
     * greater than one when pattern element is pivot element
     */
    int maxOccurrences = 1;
    /**
     * setting mappingIsCompulsory to true allows to forbid the generation of query mappings considering
     * that this pattern element is not mapped. This is done in order to prevent redundant patterns (for
     * instance when a query element matched a KB element directly designated by a KB pattern element (neither
     * a descendant, nor ancestor, nor a instance)
     */
    boolean mappingIsCompulsory = false;
    String mappingCondition = null;
    boolean qualifying;
    public static HashMap<String, Collection<PatternElementMatching>> patternElementMatchings = new HashMap<String, Collection<PatternElementMatching>>();

    abstract public void preprocess(SparqlServer sparqlServer) throws PatternsParsingException;

    public abstract void resetForNewQuery();

    static void addPatternElementMatching(String s, PatternElementMatching pem) {
        Collection<PatternElementMatching> pems = patternElementMatchings.get(s);
        if (pems == null) {
            pems = new LinkedList<PatternElementMatching>();
            patternElementMatchings.put(s, pems);
        }
        pems.add(pem);
    }

    public int getId() {
        return id;
    }

    public String getMappingCondition() {
        return mappingCondition;
    }

    public void setMappingCondition(String mappingCondition) {
        this.mappingCondition = mappingCondition;
    }

    public boolean isQualifying() {
        return qualifying;
    }

    public int calculateNumMappingsCombinations() {
        long result = mappingIsCompulsory ? 0 : 1;
        int localMaxOccurrences = determineLocalMaxOccurrences();
        for (int i = 1; i <= localMaxOccurrences; i++) {
            List<ElementMapping> ems = Controller.getInstance().getElementMappingsForPatternElement(this);
            result += CombinationGenerator.binomialCoefficient(ems.size(), i);
        }
        return (int) result;
    }

    /**
     * returns the real number of authorized mappings for that pattern element
     * @return
     */
    private int determineLocalMaxOccurrences() {
        List<ElementMapping> ems = Controller.getInstance().getElementMappingsForPatternElement(this);
        return (ems == null) ? 0
                : ((this.maxOccurrences == -1) ? ems.size() : Math.min(this.maxOccurrences, ems.size()));
    }

//    abstract List<? extends ElementMapping> getElementMappings();
    public List<ElementMapping> getElementMappings(long numInPe) {
        List<ElementMapping> elementMappings = new LinkedList<ElementMapping>();
        if (!this.mappingIsCompulsory) {
            if (numInPe == 0) {
                return elementMappings;
            }
            numInPe--;
        }
        int localMaxOccurrences = determineLocalMaxOccurrences();
        List<ElementMapping> ems = Controller.getInstance().getElementMappingsForPatternElement(this);
        for (int i = 1; i <= localMaxOccurrences; i++) {
            long bc = CombinationGenerator.binomialCoefficient(ems.size(), i);
            if (numInPe > bc) {
                numInPe -= bc;
            } else {
                CombinationGenerator cg = new CombinationGenerator(ems.size(), i);
                int[] indices = null;
                while (numInPe >= 0 && cg.hasMore()) {
                    indices = cg.getNext();
                    numInPe--;
                }
                for (int j = 0; j < indices.length; j++) {
                    elementMappings.add(ems.get(indices[j]));
                }
            }
        }

        return elementMappings;
    }

    public List<PatternToQueryMapping> mapToQuery(Query userQuery, Collection<PatternToQueryMapping> previousMappings) {
        List<PatternToQueryMapping> result = null;
        // TODO: set mappingIsCompulsory to true when necessary
        //       set it back to false for each new query (done in resetForNewQuery)
        if (mappingIsCompulsory) {
            result = new LinkedList<PatternToQueryMapping>();
        } else {
            result = new LinkedList<PatternToQueryMapping>(previousMappings);
        }
        int localMaxOccurrences = determineLocalMaxOccurrences();

        List<ElementMapping> ems = Controller.getInstance().getElementMappingsForPatternElement(this);
        for (int i = 1; i <= localMaxOccurrences; i++) {
            CombinationGenerator cg = new CombinationGenerator(ems.size(), i);
            while (cg.hasMore()) {
                int[] indices = cg.getNext();
                for (PatternToQueryMapping mapping : previousMappings) {
                    PatternToQueryMapping newMapping = (PatternToQueryMapping) mapping.clone();
                    for (int j = 0; j < indices.length; j++) {
                        newMapping.addElementMapping(ems.get(indices[j]));
                    }
                    result.add(newMapping);
                }
            }
        }
        return result;
    }

    abstract public String getDefaultStringForSentence(SparqlServer sparqlServer);

    public void printElementMappings() {
        logger.info("   - " + this + " mappable to:");
        for (ElementMapping mapping : Controller.getInstance().getElementMappingsForPatternElement(this)) {
            logger.info(mapping.toString());
        }
    }

    public static void printPatternElementMatchings() {
        for (String key : patternElementMatchings.keySet()) {
            logger.info(" + " + key);
            Collection<PatternElementMatching> pems = patternElementMatchings.get(key);
            for (PatternElementMatching pem : pems) {
                logger.info("   - " + pem.getPatternElement() + " - trust note factor = " + pem.getTrustMarkFactor());
            }
        }
        logger.info("\n");
    }

    public String toStringWithMapping(PatternToQueryMapping ptqm) {
        String result = this.toString();
        Collection<ElementMapping> ems = ptqm.getElementMappings(this);
        for (ElementMapping em : ems) {
            result += "\n       -> " + em.getQueryElement() + " - trust mark=" + em.getTrustMark();
        }
        return result;
    }
}
