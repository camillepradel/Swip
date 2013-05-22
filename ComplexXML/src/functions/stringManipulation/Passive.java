
package functions.stringManipulation;

import exception.ComplexMappingException;
import lila.syntax.Phrase;
import lila.syntax.Verb;
import utility.Normalizer;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the passive condition.
 * For example registered is the passive form of register.
 *
 */
public class Passive implements StringManipulation{

    public String compute(String s) {
        try {
            Phrase phrase = Normalizer.normalize(s);
            if(phrase.getWords().length == 1) {
                //check whether the word is a verb
                if(phrase.getWords()[0].isVerb() ){
                    //return the passive form of the word
                    return ((Verb)phrase.getWords()[0]).getPassive().toString();
                }
                else {
                    return "";
                }
            }
            return "";
        } catch (Exception ex) {
            throw new RuntimeException("Cannot determine passive form of word " + s,
                    new ComplexMappingException(ComplexMappingException.ExceptionType.LILA_EXCEPTION, "", ex));
        }
    }
}
