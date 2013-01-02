package org.swip.patterns;

import java.util.List;
import org.apache.log4j.Logger;

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

    @Override
    public String toString() {
        String result = "pattern " + this.getName() + ":\n";
        for (Subpattern sp : this.getSubpatterns()) {
            result += sp.toString() + "\n";
        }
        result += "sentence template: " + this.getSentenceTemplate();
        return result;
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
