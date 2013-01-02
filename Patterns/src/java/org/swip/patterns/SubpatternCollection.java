package org.swip.patterns;

import java.util.Collection;
import java.util.LinkedList;
import org.apache.log4j.Logger;

public class SubpatternCollection extends Subpattern {

    private static Logger logger = Logger.getLogger(SubpatternCollection.class);
    Collection<Subpattern> subpatterns = new LinkedList<Subpattern>();
    String id = null;
    private int minOccurrences = 1;
    private int maxOccurrences = 1;
    private PatternElement pivotElement = null;

    public SubpatternCollection() {
    }

    public SubpatternCollection(Collection<Subpattern> subpatterns, String id, int minOccurrences, int maxOccurrences, PatternElement pivotElement) {
        this.subpatterns = subpatterns;
        this.id = id;
        this.minOccurrences = minOccurrences;
        this.maxOccurrences = maxOccurrences;
        this.pivotElement = pivotElement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMaxOccurrences() {
        return maxOccurrences;
    }

    public void setMaxOccurrences(int maxOccurrences) {
        this.maxOccurrences = maxOccurrences;
    }

    public int getMinOccurrences() {
        return minOccurrences;
    }

    public void setMinOccurrences(int minOccurrences) {
        this.minOccurrences = minOccurrences;
    }

    public PatternElement getPivotElement() {
        return pivotElement;
    }

    public void setPivotElement(PatternElement pivotElement) {
        this.pivotElement = pivotElement;
    }

    public Collection<Subpattern> getSubpatterns() {
        return subpatterns;
    }

    public void setSubpatterns(Collection<Subpattern> subpatterns) {
        this.subpatterns = subpatterns;
    }

    @Override
    public String toString() {
        String result = "[\n";
        for (Subpattern sp : this.subpatterns) {
            result += sp.toString() + "\n";
        }
        result += "] " + this.id + ": ";
        if (minOccurrences == maxOccurrences) {
            result += "exactly " + minOccurrences;
        } else {
            if (maxOccurrences == -1) {
                result += " " + minOccurrences + " or more";
            } else {
                result += "between " + minOccurrences + " and " + maxOccurrences;
            }
        }
        if (maxOccurrences > 1 || maxOccurrences == -1) {
            result += " through " + pivotElement.toString();
        }
        return result;
    }
}
