package org.swip.pivotToMappings.model.patterns;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.patterns.subpattern.Subpattern;
import org.swip.pivotToMappings.model.patterns.subpattern.SubpatternCollection;
import org.swip.pivotToMappings.model.query.Query;
import org.swip.pivotToMappings.sparql.SparqlServer;

/**
 * class representing a query pattern
 */
public class Pattern {
    
    private static Logger logger = Logger.getLogger(Pattern.class);

    private String name = null;
    private List<Subpattern> subpatterns = null;
    /**
     * the set of pattern elements present in the query pattern
     * a same pattern element appears just once, even if it is repeated in the pattern
     */
//    private List<PatternElement> elements = null;
    /**
     * number of possible mappings of the pattern to user queries
     */
    private int numMappingsCombinations = 0;
    /**
     * the template used to generate the sentence presented to the user to explain the result of the mapping
     */
    private String sentenceTemplate = null;

    public Pattern() {
    }

    public Pattern(String name, List<Subpattern> subpatterns, String sentenceTemplate) {
        this.name = name;
        this.subpatterns = subpatterns;
        this.sentenceTemplate = sentenceTemplate;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the subpatterns
     */
    public List<Subpattern> getSubpatterns() {
        return subpatterns;
    }

    /**
     * @param subpatterns the subpatterns to set
     */
    public void setSubpatterns(List<Subpattern> subpatterns) {
        this.subpatterns = subpatterns;
    }

//    public List<PatternElement> getElements() {
//        return elements;
//    }

    /**
     * @param elements the elements to set
     */
//    public void setElements(List<PatternElement> elements) {
//        this.elements = elements;
//    }

    public int getNumMappingsCombinations() {
        if (numMappingsCombinations <= 0) {
            generateNumMappingsCombinations();
        }
        return numMappingsCombinations;
    }

    /**
     * @param numMappingsCombinations the numMappingsCombinations to set
     */
    public void setNumMappingsCombinations(int numMappingsCombinations) {
        this.numMappingsCombinations = numMappingsCombinations;
    }

    public String getSentenceTemplate() {
        return sentenceTemplate;
    }

    /**
     * @param sentenceTemplate the sentenceTemplate to set
     */
    public void setSentenceTemplate(String sentenceTemplate) {
        this.sentenceTemplate = sentenceTemplate;
    }

    public void finalizeMappings(SparqlServer serv) {
        for (Subpattern sp : this.getSubpatterns()) {
            sp.finalizeMapping(serv, this);
        }
    }

    private void generateNumMappingsCombinations() {
        int result = 1;
        for (PatternElement pe : Controller.getInstance().getPatternElementsForPattern(this)) {
            result *= pe.calculateNumMappingsCombinations();
        }
        this.numMappingsCombinations = result;
    }

    @Override
    public String toString() {
        String result = "pattern " + this.getName() + ":\n";
        for (Subpattern sp : this.getSubpatterns()) {
            result += sp.toString() + "\n";
        }
        result += "sentence template: " + this.getSentenceTemplate();
        return result;
    }

    public String toStringWithMapping(PatternToQueryMapping ptqm) {
        String result = "";
        for (Subpattern sp : this.getSubpatterns()) {
            result += sp.toStringWithMapping(ptqm) + "\n";
        }
        return result.substring(0, result.length() - 1);
    }

    public void printElementMappings() {
        logger.info(" + pattern " + this.getName() + "(" + this.getNumMappingsCombinations() + " posible query mappings)");
        for (PatternElement pe : Controller.getInstance().getPatternElementsForPattern(this)) {
            pe.printElementMappings();
        }
    }

    public String modifySentence(String sentence, PatternToQueryMapping ptqm, SparqlServer sparqlServer) {
        for (Subpattern sp : this.getSubpatterns()) {
            if (sp instanceof SubpatternCollection) {
                sentence = ((SubpatternCollection) sp).modifySentence(sentence, ptqm, sparqlServer);
            }
        }
        return sentence;
    }

    public Iterable<PatternToQueryMapping> getMappingsIterable(final Query q) {
        return new Iterable<PatternToQueryMapping>() {

            @Override
            public Iterator<PatternToQueryMapping> iterator() {
                return new Iterator<PatternToQueryMapping>() {
                    List<PatternElement> pe = Controller.getInstance().getPatternElementsForPattern(Pattern.this);
                    final int numElements = pe.size();
                    long[] numMappings = new long[numElements];
                    int currentMapping = 0;
                    PatternToQueryMapping next;

                    {
                        // instance initializer
                        for (int i = 0; i < numElements; i++) {
                            numMappings[i] = pe.get(i).calculateNumMappingsCombinations();
                        }
                        for (int i = numElements - 2; i >= 0; i--) {
                            numMappings[i] *= numMappings[i + 1];
                        }
                        next = nextForNext();
                    }

                    @Override
                    public boolean hasNext() {
//                        return (currentMapping < numMappings[0]);
                        return (next != null);
                    }

                    @Override
                    public PatternToQueryMapping next() {
                        PatternToQueryMapping buffer = next;
                        next = nextForNext();
                        return buffer;
                    }

                    private PatternToQueryMapping nextForNext() {
                        PatternToQueryMapping ptqm = null;
                        while (ptqm == null && currentMapping < numMappings[0]) {
                            ptqm = new PatternToQueryMapping(Pattern.this);
                            long localCurrentMapping = currentMapping;
                            for (int i = 0; i < numElements; i++) {
                                PatternElement pe = Controller.getInstance().getPatternElementsForPattern(Pattern.this).get(i);
                                long numInPe = (i < numElements - 1) ? localCurrentMapping / numMappings[i + 1] : localCurrentMapping;
                                ptqm.addElementMappings(pe.getElementMappings(numInPe));
                                localCurrentMapping = (i < numElements - 1) ? localCurrentMapping % numMappings[i + 1] : 0;
                            }
                            currentMapping++;
                            if (ptqm.isRedundant()) {
                                ptqm = null;
                            }
                        }
                        return ptqm;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove unsupported");
                    }
                };
            }
        };
    }

    public PatternElement getPatternElementById(int id) {
        for (PatternElement pe : Controller.getInstance().getPatternElementsForPattern(this)) {
            if (pe.getId() == id) {
                return pe;
            }
        }
        return null;
    }
}
