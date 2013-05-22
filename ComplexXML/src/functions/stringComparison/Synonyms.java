
package functions.stringComparison;

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
 * Implementation of the synonyms condition.
 * Two words are synonyms if one word can be replaced with the other
 * word without changing the meaning of the statement.
 *
 */
public class Synonyms implements StringComparison {

    public boolean compute(String s1, String s2) {

        try {
            LiLA l = new LiLA();
            l.setContext(new HashSet<String>());
            Phrase p1 = Normalizer.normalize(s1);
            Phrase p2 = Normalizer.normalize(s2);

        
            //assume that only a word can have synonyms and not a whole phrase
            if (p1.getWords().length > 1 || p2.getWords().length > 1
                    || p2.getWords().length == 0 || p1.getWords().length == 0) {
                return false;
            }
            else {
                for(Word w : p1.getWords()[0].getSynonyms()) {
                    if(w.equals(p2.getWords()[0])) {
                        return true;
                    }
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(new ComplexMappingException
                    (ComplexMappingException.ExceptionType.LILA_EXCEPTION, "Could not get the" +
                    "words of a phrase to check synonyms. ", ex));
        }

        return false;
    }
}
