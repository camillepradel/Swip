package operators;

import java.util.ArrayList;

import pattern.Pattern;

import utility.Tuple;

/**
 *
 * @author Dominique Ritze
 *
 * The operator interface which should be implemented by every operator, for example
 * the boolean operators AND, OR and NOT.
 * Implementing this interface should also guarantee that the operator implements
 * a method to join a set of tuples.
 */
public interface Operator {

        /**
         * This method get a pattern which contains all information about the
         * correspondence pattern like the conditions and several lists of
         * tuples(from several conditions) which should be joined so that
         * a list of joined tuples can be returned.
         *
         * @param p Pattern containing the conditions and additional information.
         * @param tuples All lists of tuples to join.
         * @return A joined list of tuples.
         */
	public ArrayList<Tuple> joinTuples(Pattern p, ArrayList<ArrayList<Tuple>> tuples);
}
