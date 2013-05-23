package pattern;

import utility.Tuple;

/**
 * 
 * @author Dominique Ritze
 *
 * Class to represent a correspondence which satisfies
 * a certain pattern.
 *
 */
public class ComplexCorrespondence {
	
    private Tuple entities;
    private Pattern pattern;

    /**
     * Constructor.
     *
     * @param pattern The pattern which is satisfied by the correspondence.
     * @param entities The tuple which contains the entities.
     */
    public ComplexCorrespondence(Pattern pattern, Tuple entities) {
        this.entities = entities;
        this.pattern = pattern;
        entities = new Tuple(pattern);
    }

    /**
     *
     * @return A representation of the correspondences like it is
     * specified in the XML file. 
     */
    @Override
    public String toString() {
        String output = pattern.getOutputFormat().getCorrespondenceOutput();
        for(String s : pattern.getAssignment().keySet()) {
            output = output.replace(s, entities.getEntries()[pattern.getAssignment().get(s)].getIRI().toString());
            output = output.replace("&amp;", "&");
        }
        return output;
    }

    /**
     *
     * @param o
     * @return True if the entities of the correspondence are the same.
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof ComplexCorrespondence) {

            int partsCorrespondence = 0;
            for(int i=0; i<this.entities.getEntries().length; i++) {
                if(this.entities.getValue(i).equals(((ComplexCorrespondence)o).entities.getValue(i))) {
                    for(String id : this.pattern.getPartsOfCorrespondence()) {
                        if(this.pattern.getAssignment().get(id) == i) {
                            partsCorrespondence++;
                        }
                    }
                }
            }
            if(partsCorrespondence == this.pattern.getPartsOfCorrespondence().size()) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.entities != null ? this.entities.hashCode() : 0);
        hash = 31 * hash + (this.pattern != null ? this.pattern.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @return The corresponding pattern.
     */
    public Pattern getPattern() {
        return this.pattern;
    }

    /**
     *
     * @return The corresponding tuple.
     */
    public Tuple getTuple() {
        return this.entities;
    }

}
