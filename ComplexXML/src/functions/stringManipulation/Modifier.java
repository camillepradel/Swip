
package functions.stringManipulation;

import exception.ComplexMappingException;
import java.util.HashSet;
import lila.LiLA;
import lila.syntax.Phrase;
import lila.syntax.Word;
import utility.Normalizer;


/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the modifier condition.
 * It can be a adverbial oder adjective modifier of the head.
 * For example accepted is a modifier of accepted_paper.
 *
 */
public class Modifier implements StringManipulation {

    public String compute(String s) throws ComplexMappingException {
        if(s.isEmpty()) {
            return "";
        }
        Phrase phrase = Normalizer.normalize(s);
        Word head;
        try {
            if(phrase.getWords().length == 0) {
                return "";
            }
            LiLA lila = new LiLA();
            lila.setContext(new HashSet<String>());
            head = phrase.getHead();
            //TODO >1?
            //determine the modifier of the head
            if(phrase.getModifiers(head).length > 0) {
                return(phrase.getModifiers(head)[0].toString());
            }
            //if no modifier found, check whether the word contains
            //a delimiter and try to determine a modifier
            //after splitting the phrase
            else if(phrase.getModifiers(head).length == 0 && (s.contains("-") ||
                    s.contains(" ") || s.contains("_"))){
                phrase = Normalizer.normalize(s, true, true, true);
                head = phrase.getHead();
                if(phrase.getModifiers(head).length > 0) {
                    return(phrase.getModifiers(head)[0].toString());
                }
            }
        }
        catch(Exception e) {
            throw new ComplexMappingException(ComplexMappingException.ExceptionType.LILA_EXCEPTION,
                    "", e);
        }
        return "";
    }

}
