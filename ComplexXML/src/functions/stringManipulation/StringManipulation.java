package functions.stringManipulation;

import exception.ComplexMappingException;
import functions.Function;

/**
 *
 * @author Dominique Ritze
 *
 * Interface for all conditions which are string manipulations,
 * that means they get a term and return a modified one.
 *
 */
public interface StringManipulation extends Function{

        /**
         * Method to extract the modified string from the original one.
         *
         * @param s The original string.
         * @return Some modified string.
         */
	public String compute(String s) throws ComplexMappingException;

}
