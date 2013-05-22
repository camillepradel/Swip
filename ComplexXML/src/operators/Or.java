package operators;

import java.util.ArrayList;
import pattern.Pattern;
import utility.Tuple;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the OR operator to join several tuples.
 * Therefore just all tuples are returned because every tuple satisfies
 * at least one of the conditions which are connected by the OR operator,
 * otherwise the tuple would not be in a list to join.
 *
 */
public class Or implements Operator{

        /**
         * Join-method of the OR operator to join tuples.
         *
         * @param p
         * @param tuples
         * @return
         */
	public ArrayList<Tuple> joinTuples(Pattern p, ArrayList<ArrayList<Tuple>> tuples) {		
		ArrayList<Tuple> allTuples = new ArrayList<Tuple>();
		
		for(ArrayList<Tuple> tupleList : tuples) {
			for(Tuple t : tupleList) {
                            //just add the tuple if it is not contained already
				if(!allTuples.contains(t)) {
					allTuples.add(t);
				}				
			}
		}
			
  
		return allTuples;
	} 

}
