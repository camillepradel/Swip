package functions.stringManipulation;

import exception.ComplexMappingException;

/**
 * 
 * @author Dominique Ritze
 * 
 * Implementation of the string manipulation complement first part,
 * whereby the complement of the first part of the string is just 
 * the string which remains when removing the first part.
 * If the string contains only of one term, the complement of the 
 * first part is empty.
 * 
 */
public class ComplementFirstPart implements StringManipulation{

        /**
         * Delete the first part of the string and return the
         * remaining parts if any.
         *
         * @param s The original string.
         * @return The string without first part.
         */
	public String compute(String s) throws ComplexMappingException{
		//compute the first part
		FirstPart f = new FirstPart();
		String first = f.compute(s);
		
		
		//delete the first part and the delimiter between the first part and the rest
		if(!s.equals(first)) {
			return s.substring(s.indexOf(first)+ 1 + first.length());
		}
		else {
			return "";
		}
	}

}
