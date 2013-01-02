package org.swip.patterns;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.log4j.Logger;

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
     * !! semble être tout le temps à false, donc sert à rien !!
     */
    boolean mappingIsCompulsory = false;
    String mappingCondition = null;
    boolean qualifying;
    public static HashMap<String, Collection<PatternElementMatching>> patternElementMatchings = new HashMap<String, Collection<PatternElementMatching>>();


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
}
