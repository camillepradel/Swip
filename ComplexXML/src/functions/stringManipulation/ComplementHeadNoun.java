package functions.stringManipulation;

import utility.Attributes;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the string manipulation complement head noun, whereby
 * the complement of the head noun is the string remaining when removing
 * the head noun.
 *
 */
public class ComplementHeadNoun implements StringManipulation{

        /**
         * Delete the head noun of string s and return
         * the resulting string.
         *
         * @param s
         * @return
         */
	public String compute(String s) {

                //compute the headnoun
		Head hn = new Head();
		String head = hn.compute(s);
                s = s.toLowerCase();
                if(s.indexOf(head) == -1) {
                    return s;
                }

                int length = s.indexOf(head) + 1 + head.length();

                //remove the headnoun
                String complement = s.substring(0,s.indexOf(head));
                if(length < s.length()) {
                    complement += s.substring(s.indexOf(head)+ head.length());
                }
                
                complement = complement.trim();                

                //check the delimiter, they must be removed
                boolean[] delimiter = new boolean[]{true, true, true};

                if(Attributes.classNamesFirst == null) {
                    Attributes.initializeNames();
                }

                if(Attributes.propertyNamesFirst.contains(s) || Attributes.classNamesFirst.contains(s)) {
			delimiter = Attributes.firstOntology.getDelimiter();
		}
		if(Attributes.propertyNamesSecond.contains(s) || Attributes.classNamesSecond.contains(s)) {
			delimiter = Attributes.secondOntology.getDelimiter();
		}

                if(complement.endsWith("-") || complement.endsWith("_") ) {
                    int complLength = complement.length();
                    if(delimiter[0]) {                        
                        complement = complement.substring(0, complLength-1);
                    }
                    if(delimiter[1]) {
                        complement = complement.substring(0, complLength-1);
                    }
                     if(delimiter[2]) {
                        complement = complement.substring(0, complLength-1);
                    }
                }
                return complement;
	}

}
