package operators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.semanticweb.owl.model.OWLEntity;
import pattern.Pattern;
import utility.Tuple;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the NOT operator to delete all tuples which
 * satisfy one or more conditions. Therefore it is necessary to
 * create all other tuples and to return them.
 *
 */
public class Not implements Operator{

        /**
         * Join tuples with the NOT operator means, that every tuple which satisfies
         * a condition is deleted and every other tuple returned.
         * This is only possible, if really all other tuples are created which
         * can be very expansive especially if the tuples consists of many entries.
         *
         * @param pattern
         * @param tuples
         * @return
         */
	public ArrayList<Tuple> joinTuples(Pattern pattern, ArrayList<ArrayList<Tuple>> tuples) {
		
		ArrayList<Tuple> joinedTuples = new ArrayList<Tuple>();
		Set<Integer> usedPositions = new HashSet<Integer>(); 
		
		for(Tuple t : tuples.get(0)) {
			for(int i=0; i<t.getEntries().length; i++) {
				if(t.getEntries()[i] != null) {
					//check which positions are used and which ones are empty
					usedPositions.add(i);
				}
			}
		}
		
		int numberOfTuple = 1;
		int maximumSize = 0;

                //determine the number of all tuples which must be created (regarding the used positions)
                //and the size of the biggest ontology
                for(String identifier : pattern.getIdent().keySet()) {
                    if(usedPositions.contains(pattern.getAssignment().get(identifier))) {
                        numberOfTuple = numberOfTuple*pattern.getIdent().get(identifier).size();
			if(pattern.getIdent().get(identifier).size() > maximumSize) {
				maximumSize = pattern.getIdent().get(identifier).size();
			}
                    }
                }
		
		//create all tuples but without values
		for(int i=0; i<numberOfTuple; i++) {
			joinedTuples.add(new Tuple(pattern));
		}
		
		//fill the created tuples with values according to the ids
		Iterator<? extends OWLEntity> it;		
		int sizeMemory = 1;
		for (String iden : pattern.getIdent().keySet()) {
			
			//check if the position is used -> in the tuples this position must be used too
			//or if no position is used at all -> create tuples which use all positions
			if(usedPositions.contains(pattern.getAssignment().get(iden)) || usedPositions.size() == 0) {
				if(pattern.getIdent().get(iden).size() != 0) {
					it = pattern.getIdent().get(iden).iterator();
					
					sizeMemory = sizeMemory * pattern.getIdent().get(iden).size();
					   
					   //check how many tuples have the same value on a position
					   int stepSize = joinedTuples.size() / sizeMemory;

					   int counter = 0;
					   OWLEntity ent = it.next();
					  	
					   
					   for (Tuple t : joinedTuples) {				   
						   if (counter == stepSize) {				   
							   if (!it.hasNext()) {
								   it = pattern.getIdent().get(iden).iterator();
							   }
							   ent = it.next();
							   counter = 0;
						   }
						   //set the values in the tuples
						   t.setValue(pattern.getAssignment().get(iden), ent);
						   counter++;
					   }
				} 
			}
			else {
				continue;
			}

                        //remove the tuples to "join"
			for(Tuple t : tuples.get(0)) {
				if(joinedTuples.contains(t)) {
					joinedTuples.remove(t);
				}
			}			
		}
		return joinedTuples;
	}

}
