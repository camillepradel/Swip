package functions.stringManipulation;

import exception.ComplexMappingException;
import lila.syntax.Phrase;
import utility.Normalizer;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the head condition. A phrase can contain a
 * verb or noun as head, e.g. paper is the noun of accepted_paper.
 *
 */
public class Head implements StringManipulation{
	
	public String compute(String s) {

            try {
                Phrase phrase = Normalizer.normalize(s);
                //return the head of the phrase
                return phrase.getHead().toString();
            } catch(Exception e) {
                throw new RuntimeException("Could not determine the headnoun of " + s + " .", new
                        ComplexMappingException(ComplexMappingException.ExceptionType.LILA_EXCEPTION, "", e));
                        
            } 
	}
}
