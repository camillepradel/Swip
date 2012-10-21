package org.swip.pivotToMappings.model.patterns.subpattern;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.swip.pivotToMappings.model.patterns.Pattern;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.utils.sparql.SparqlServer;

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
    
    public abstract String toStringWithMapping(PatternToQueryMapping ptqm);

    public abstract String generateSparqlWhere(PatternToQueryMapping ptqm, SparqlServer sparqlServer, Map<PatternElement,String> pivotsNames, Set<String> selectElements, HashMap<String, String> numerciDataPropertyElements);

    abstract public void finalizeMapping(SparqlServer serv, Pattern p);
}
