package operators;

import java.util.ArrayList;

import pattern.Pattern;

import utility.Tuple;

/**
 *
 * @author Dominique Ritze
 *
 * The implementation of the boolean AND operator to join a set of tuples
 * by connecting the tuples according to the boolean AND.
 * For example t1 = (x,y,-) and t2 = (-,y,z) results in t = (x,y,z).
 *
 */
public class And implements Operator{

        /**
         * Method to join the tuples by applying the AND operator.
         *
         * @param p
         * @param tuples
         * @return
         */
	@SuppressWarnings("unchecked")
	public ArrayList<Tuple> joinTuples(Pattern p, ArrayList<ArrayList<Tuple>> tuples) {
		ArrayList<Tuple> allTuples = new ArrayList<Tuple>();
		Tuple currentTuple = new Tuple(p);
		
		//nothing to join, just one list
		if(tuples.size() == 1) {
			return tuples.get(0);
		}
		
		ArrayList<Tuple> nextList = tuples.get(0);
		
		int countAgreement = 0;
		for(int i=1; i<tuples.size(); i++) {
			//one list is empty -> because of and operation there cannot exist any correspondence at all
			if(tuples.get(i).size()==0) {
				allTuples = new ArrayList<Tuple>();
				return(allTuples);
			}
			
			for(Tuple t1 : tuples.get(i)) {
				//other list is empty
				if(nextList.size()==0) {
					allTuples = new ArrayList<Tuple>();
					return(allTuples);
				}
				
				for(Tuple t2 : nextList) {					
					for(int j=0; j<t1.getEntries().length; j++) {
						
						if(t1.getValue(j) == null && t2.getValue(j) == null) {
							countAgreement++;
							currentTuple.setValue(j, null);
							continue;
						}
						
						//if the values are different and one of them is not null, there is a conflict						
						if(t1.getValue(j) != t2.getValue(j)) {
							
							//if one value is not null, this one will be in the tuple
							if(t1.getValue(j) == null) {
								countAgreement++;
								currentTuple.setValue(j, t2.getValue(j));
							}
							if(t2.getValue(j) == null) {
								countAgreement++;
								currentTuple.setValue(j, t1.getValue(j));
							}
							if(t1.getValue(j) != null && t2.getValue(j) != null) {
								break;
							}
						}
						else {
							//set the value and increment the agreement counter
							countAgreement++;
							currentTuple.setValue(j, t1.getValue(j));								
						}						
					}
					//all values have been regarded and their are pairwise according
					if(countAgreement == t1.getEntries().length) {
						allTuples.add(currentTuple);						
					}
					currentTuple = new Tuple(p);
					countAgreement = 0;		
				}				
			}
			nextList = (ArrayList<Tuple>)allTuples.clone();
			if(i != tuples.size()-1) {
				allTuples.clear();
			}
			
		}
		return allTuples;
	}

}
