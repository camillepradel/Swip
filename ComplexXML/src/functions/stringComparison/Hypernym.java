
package functions.stringComparison;

import exception.ComplexMappingException;
import java.util.HashSet;
import lila.LiLA;
import lila.syntax.Phrase;
import lila.syntax.Word;
import lila.tools.wordnet.NounSense;
import lila.tools.wordnet.Sense;
import lila.tools.wordnet.VerbSense;
import utility.Normalizer;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the hypernym condition.
 * Word w1 is a hypernym of word w2 if w1 is a generalization of w2,
 * e.g. animal is a hypernym of dog.
 *
 */
public class Hypernym implements StringComparison{

    public boolean compute(String s1, String s2) {

        try {
            //initialize LiLA
            LiLA l = new LiLA();
            l.setContext(new HashSet<String>());
            Phrase p1 = Normalizer.normalize(s1);
            Phrase p2 = Normalizer.normalize(s1);

            //assume that only one word (not a whole phrase) can be hypernym
            if (p1.getWords().length == 1 && p2.getWords().length == 1) {
                Word word = p1.getWords()[0];
                Sense hypernyms[] = null;

                if(word.getSenses().length == 0) {
                    return false;
                }
                //check the category and determine the hypernyms
                if( word.getCategory().equals("NOUN")) {
                    hypernyms = ((NounSense)word.getSense()).getHypernyms();
		}
                else if( word.getCategory().equals("VERB") ) {
                    hypernyms = ((VerbSense)word.getSense()).getHypernyms();
		}
                else if( word.getCategory().equals("UNKNOWN") ) {
                    return false;
		}

                if(hypernyms == null || hypernyms.length == 0) {
                    return false;
                }

                for( int i=0; i<hypernyms.length; i++ ){
                    if(hypernyms[i].getWords()[0].equals(p2.getWords()[0].toString())) {
                        return true;
                    }
                }
            }
            return false;

        } catch (Exception ex) {
            throw new RuntimeException(new ComplexMappingException
                    (ComplexMappingException.ExceptionType.LILA_EXCEPTION, "Could not get the " +
                    "words of a phrase to check hypernym. " + s1 + " " +s2, ex));
        }
    }

}
