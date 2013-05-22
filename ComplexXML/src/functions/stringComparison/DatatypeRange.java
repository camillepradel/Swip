package functions.stringComparison;

import org.semanticweb.owl.model.OWLDataProperty;
import utility.Attributes;

/**
 * 
 * @author Dominique Ritze
 * 
 * This class implements the datatype range condition and all
 * also a method to determine the datatype range of a property.
 *
 */
public class DatatypeRange implements StringComparison{

        /**
         * Determine the datatype range of a property
         * which can be compared. Therefore some parts
         * must be removed from the range which
         * is returned by the OWL Api.
         *
         * @param prop
         * @return The range as comparable string.
         */
	public String getDatatypeRange(OWLDataProperty prop) {
		//check if the property is in the first ontology
		if(Attributes.firstOntology.getDatatypeProperties().contains(prop)){
			//get the range
			String range = prop.getRanges(Attributes.firstOntology.getOntology()).toString();
			//a string like [range] is returned -> delete [ and ] to get a normal string
			range = range.replace("[", "");
			range = range.replace("]", "");
			return range;
		}
		//property is in the second ontology
		else {
			String range = prop.getRanges(Attributes.secondOntology.getOntology()).toString();
			range = range.replace("[", "");
			range = range.replace("]", "");
			return range;
		}
			
	}

        /**
         * Check if the datatype range of the property equals
         * the given range.
         *
         * @param prop The range of the property as string.
         * @param range The compared range as string.
         * @return True if the ranges are the same, false otherwise.
         */
        public boolean compute(String prop, String range) {
            if(prop.toLowerCase().equals(range.toLowerCase())) {
                return true;
            }
            else{
                return false;
            }
        }

}
