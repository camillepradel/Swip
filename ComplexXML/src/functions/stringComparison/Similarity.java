package functions.stringComparison;

import utility.Attributes;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.descriptions.Term;
import de.unima.ki.mmatch.smeasures.Measure;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the similarity condition between strings.
 *
 */
public class Similarity implements StringComparison{
	
	private double threshold;
        private double similarityValue;
	
	/**
         * Constructor.
         *
         * @param value The threshold to determine whether two strings
         * are similar or not.
         */
	public Similarity(double value) {
		this.threshold = value;
	}

        /**
         * Check if the strings s1 and s2 are similar. 
         * Therefore the strings are splited into terms according to
         * the characters which have been selected as delimiters.
         * Afterwards the terms are compared based on levenshtein distance
         * such that a wighted sum is determined which finally indicates
         * a similarity value between the strings. If this value is above
         * the specified threshold, the strings are declared as similar.
         *
         * If two strings are empty, they are not declared as similar.
         *
         * @param s1
         * @param s2
         * @return True if the similarity value is above the specified threshold.
         * @throws ComplexMappingException
         */
	public boolean compute(String s1, String s2) throws ComplexMappingException{

		if (!utility.Attributes.correspondencePattern.isEmpty()) {
			utility.Attributes.correspondencePattern.get(0)
					.getCorrespondences().get(0).getTuple();
		}

                if(s1.equals("") && s2.equals("")) {
                    return false;
                }
		
		//create sets containing the entity names as strings
                //which is necessary for determining the delimiter
		if(Attributes.classNamesFirst == null) {
			Attributes.initializeNames();
		}

                //create the delimiter arrays
		boolean [] delimiterT1 = determineDelimiter(s1);
		boolean[] delimiterT2 = determineDelimiter(s2);
		boolean propertyT1 = determineProperty(s1);
		boolean propertyT2 = determineProperty(s2);
		
		try {
			Term t1, t2;
			//create the terms
			if(propertyT1) {
				t1 = new Term(s1, delimiterT1, propertyT1);
			}
			else {
				t1 = new Term(s1, delimiterT1);
			}
			if(propertyT2) {
				t2 = new Term(s2, delimiterT2, propertyT2);
			}
			else {
				t2 = new Term(s2, delimiterT2);
				
			}			

			Measure m = new Measure();
                        
                        //compare the terms
                        similarityValue = m.compare(t1, t2);
			if(similarityValue >= this.threshold) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(MMatchException e) {
			throw new ComplexMappingException(ExceptionType.MMATCH_EXCEPTION,
                                "Could not compare strings.", e);
		}	
	}
	
	/**
	 * Get the delimiter corresponding to the ontology in which the string is name of a entity.
	 * 
	 * @param s
	 * @return
	 */
	private boolean[] determineDelimiter(String s) {
		if(Attributes.propertyNamesFirst.contains(s) || Attributes.classNamesFirst.contains(s)) {
			return Attributes.firstOntology.getDelimiter();
		}
		if(Attributes.propertyNamesSecond.contains(s) || Attributes.classNamesSecond.contains(s)) {
			return Attributes.secondOntology.getDelimiter();
		}
		return new boolean[]{false, false, false};
	}
	
	/**
	 * Returns true if the current string is name of a property, false otherwise.
	 * 
	 * @param s
	 * @return
	 */
	private boolean determineProperty(String s) {
		if(Attributes.propertyNamesFirst.contains(s) || Attributes.propertyNamesSecond.contains(s)) {
			return true;
		}
		else {
			return false;
		}
	}

        public double getSimilarityValue() {
            return this.similarityValue;
        }
}
