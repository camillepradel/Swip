package functions.stringComparison;

import exception.ComplexMappingException;
import functions.Function;

/**
 *
 * @author Dominique Ritze
 *
 * Interface for all classes which implement a string comparision, e.g.
 * similarity or same.
 *
 */
public interface StringComparison extends Function{

        /**
         * In the compute method, the comparison of two strings
         * should be implemented.
         *
         * @param s1
         * @param s2
         * @return
         * @throws ComplexMappingException
         */
	public boolean compute(String s1, String s2) throws ComplexMappingException;

}
