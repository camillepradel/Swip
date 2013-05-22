
package functions.stringComparison;

import exception.ComplexMappingException;
import java.util.HashSet;
import lila.LiLA;
import lila.syntax.Phrase;
import lila.syntax.Word;
import lila.tools.wordnet.AdjectiveSense;
import lila.tools.wordnet.AdverbSense;
import lila.tools.wordnet.NounSense;
import lila.tools.wordnet.Sense;
import lila.tools.wordnet.VerbSense;
import utility.Normalizer;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the antonym condition.
 * An example of an antonym: Rejection is an antonym of Acceptance.
 *
 */
public class Antonym implements StringComparison {

    /**
     * Determine whether s1 and s2 are antonyms or not.
     *
     * @param s1
     * @param s2
     * @return
     */
    public boolean compute(String s1, String s2) {
        try {
            //initiate LiLA stuff
            LiLA l = new LiLA();
            l.setContext(new HashSet<String>());
            Phrase p1 = Normalizer.normalize(s1);
            Phrase p2 = Normalizer.normalize(s2);

            //assume that antonmys consists of just one word
            if (p1.getWords().length == 1 && p2.getWords().length == 1) {
                Word word = p1.getWords()[0];
                Sense antonyms[] = null;

                if(word.getSenses().length == 0) {
                    return false;
                }

                //check the category because phrase must be casted
                if( word.getCategory().equals("NOUN")) {
                    antonyms = ((NounSense)word.getSense()).getAntonyms();                    
		}
                else if( word.getCategory().equals("VERB") ) {
                    antonyms = ((VerbSense)word.getSense()).getAntonyms();
		}
                else if( word.getCategory().equals("ADJECTIVE") ) {
                    antonyms = ((AdjectiveSense)word.getSense()).getAntonyms();
		}
                else if( word.getCategory().equals("ADVERB") ) {
                    antonyms = ((AdverbSense)word.getSense()).getAntonyms();
		}
                else if( word.getCategory().equals("UNKNOWN") ) {
                    return false;
		}

                if(antonyms == null || antonyms.length == 0) {
                    return false;
                }                

                //check whether the strings are antonyms or not
                for( int i=0; i<antonyms.length; i++ ){
                    if(antonyms[i].getWords()[0].equals(p2.getWords()[0].toString())) {
                        return true;
                    }
                }                
            }
            return false;

        } catch (Exception ex) {
            throw new RuntimeException(new ComplexMappingException
                    (ComplexMappingException.ExceptionType.LILA_EXCEPTION, "Could not get the " +
                    "words of a phrase to check antonyms. " + s1 + " " +s2, ex));
        }
    }
}
