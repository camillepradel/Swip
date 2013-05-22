

package functions.stringManipulation;

import exception.ComplexMappingException;
import lila.syntax.Phrase;
import lila.syntax.Word;
import utility.Normalizer;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the verb condition.
 * For example registered is the verb phrase of the phrase early_registered.
 *
 */
public class Verb implements StringManipulation{

    public String compute(String s) throws ComplexMappingException {

        Phrase p = Normalizer.normalize(s, true, true, true);

        try {
            for(Word w : p.getWords()) {
                //return the first word with category verb
                if(w.getCategory().toLowerCase().equals("verb")) {
                    return w.toString();
                }
            }

            return "";

        } catch(Exception e) {
            throw new RuntimeException("Cannot determine verb of phrase " + s,
                    new ComplexMappingException(ComplexMappingException.ExceptionType.LILA_EXCEPTION, "", e));
        }
    }

}
