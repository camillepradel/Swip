package functions.stringComparison;

import exception.ComplexMappingException;
import java.util.HashSet;
import lila.LiLA;
import lila.syntax.Phrase;
import lila.syntax.Word;
import lila.tools.wordnet.NounSense;
import lila.tools.wordnet.VerbSense;
import utility.Normalizer;

/**
 *
 * @author Dominique Ritze
 *
 * Implements the nominalization condition.
 * For example acceptance is the nominalization of accept.
 *
 *
 */
public class Nominalization implements StringComparison{

    public boolean compute(String s1, String s2) {

        try {

            if(s1.isEmpty() || s2.isEmpty()) {
                return false;
            }

            //initialize LiLA
            LiLA l = new LiLA();
            l.setContext(new HashSet<String>());
            Phrase p1 = Normalizer.normalize(s1);
            Phrase p2 = Normalizer.normalize(s2);

            //just one word can be nominalization
            if (p1.getWords().length == 1 && p2.getWords().length == 1) {
                Word firstWord = p1.getWords()[0];
                Word secondWord = p2.getWords()[0];

                if(firstWord.getSenses().length == 0 || secondWord.getSenses().length == 0) {
                    return false;
                }

                //check which word is noun and which the verb and if
                //the noun is nominalization of the verb
                if( firstWord.getCategory().toLowerCase().equals("noun") && secondWord.getCategory().toLowerCase().equals("verb")) {                    
                    if(((NounSense)firstWord.getSense()).isNominalization((VerbSense)secondWord.getSense())) {
                        return true;
                    }
                    else {
                        return false;
                    }
		}
                else if(firstWord.getCategory().toLowerCase().equals("verb") && secondWord.getCategory().toLowerCase().equals("noun")) {
                    if(((VerbSense)firstWord.getSense()).isNominalization((NounSense)secondWord.getSense())) {
                        return true;
                    }
                    else {
                        return false;
                    }
                } 
            }
            return false;

        } catch (Exception ex) {
            throw new RuntimeException(new ComplexMappingException
                    (ComplexMappingException.ExceptionType.LILA_EXCEPTION, "Could not get the " +
                    "words of a phrase to check nominalization. " + s1 + " " +s2, ex));
        }

    }

}
