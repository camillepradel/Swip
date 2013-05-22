
package functions.stringManipulation;

import exception.ComplexMappingException;
import lila.syntax.Phrase;
import lila.syntax.Verb;
import utility.Normalizer;

/**
 *
 * @author Dominique Ritze
 *
 * Implements the active condition, e.g. register is the active form of registered.
 *
 */
public class Active implements StringManipulation {

    public String compute(String s) {
        
        try {
            Phrase phrase = Normalizer.normalize(s);            
            if(phrase.getWords().length == 1) {
                //check whether the string is a verb
                if(phrase.getWords()[0].isVerb() ){
                    //return the active form
                    return ((Verb)phrase.getWords()[0]).getActive().toString();
                }
            }
            return "";
        } catch (Exception ex) {
            throw new RuntimeException("Cannot determine active form of word " + s,
                    new ComplexMappingException(ComplexMappingException.ExceptionType.LILA_EXCEPTION, "", ex));
        }
    }
}
