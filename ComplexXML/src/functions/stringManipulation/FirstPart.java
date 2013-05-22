package functions.stringManipulation;

import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.descriptions.Term;
import exception.ComplexMappingException;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the string manipulation first part, whereby the first
 * part is just the first term of a string of the whole string
 * if the string just consists of one term.
 *
 */
public class FirstPart implements StringManipulation{

        /**
         * Determines the first part of a string and returns it.
         * If the string consists of one term, the whole string is returned.
         *
         * @param s The original string.
         * @return The first part of the string.
         */
	public String compute(String s) throws ComplexMappingException{
				
		Term t;
                //check the delimiter, they must be removed
                boolean[] delimiter = new boolean[]{true, true, true};

                //compute first part with help of Term, because in Term the string is splitted up according to the delimiter
                try {
                   t = new Term(s, delimiter);
                }
                catch(MMatchException e) {
                    throw new ComplexMappingException(ComplexMappingException.ExceptionType.MMATCH_EXCEPTION,
                            "Could not create term while computing the first part.", e);
                }
            return t.getTokens()[0];
	}
}
