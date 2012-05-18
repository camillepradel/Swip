package org.swip.pivotToMappings.model.patterns.subpattern;

import java.util.Map;
import java.util.Set;
import org.swip.pivotToMappings.model.patterns.Pattern;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.sparql.SparqlServer;

abstract public class Subpattern {

    public static int varCount = 0;
    
    private boolean hasNumericDataProperty;

    public Subpattern() {
        this.hasNumericDataProperty = false;
    }

    public static int getVarCount() {
        return varCount;
    }

    public static void setVarCount(int varCount) {
        Subpattern.varCount = varCount;
    }
    
    public boolean hasNumericDataProperty()
    {
        System.out.println("\n/!\\ get has numeric data property to : "+this.hasNumericDataProperty+" || "+this+" \n\n");
        return this.hasNumericDataProperty;
    }
    
    protected void setHasNumericDataProperty(boolean bool)
    {
        this.hasNumericDataProperty = bool;
        System.out.println("\n/!\\ set has numeric data property to : "+bool+"  || "+this+" \n\n");
    }

    public abstract String toStringWithMapping(PatternToQueryMapping ptqm);

    public abstract String generateSparqlWhere(PatternToQueryMapping ptqm, SparqlServer sparqlServer, Map<PatternElement,String> pivotsNames, Set<String> selectElements);

    abstract public void finalizeMapping(SparqlServer serv, Pattern p);
}
