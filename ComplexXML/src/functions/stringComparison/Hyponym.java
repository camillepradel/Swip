
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
 * Implementation of the hyponym condition.
 * A word w1 is a hyponym of w1 if w1 is a refinement of w1, e.g.
 * house is a hyponym of building.
 * Hyponyms are the opposite of hypernyms.
 *
 */
public class Hyponym implements StringComparison {

    public boolean compute(String s1, String s2) {
        
        try {
            LiLA l = new LiLA();
            l.setContext(new HashSet<String>());
            Phrase p1 = Normalizer.normalize(s1);
            Phrase p2 = Normalizer.normalize(s2);


            if (p1.getWords().length == 1 && p2.getWords().length == 1) {
                Word word = p1.getWords()[0];
                Sense hyponyms[] = null;

                if(word.getSenses().length == 0) {
                    return false;
                }

                //check the category and determine the hyponyms
                if( word.getCategory().equals("NOUN")) {
                    hyponyms = ((NounSense)word.getSense()).getHyponyms();
		}
                else if( word.getCategory().equals("VERB") ) {
                    hyponyms = ((VerbSense)word.getSense()).getHyponyms();
		}
                else if( word.getCategory().equals("UNKNOWN") ) {
                    return false;
		}

                if(hyponyms == null || hyponyms.length == 0) {
                    return false;
                }

                for( int i=0; i<hyponyms.length; i++ ){
                    if(hyponyms[i].getWords()[0].equals(p2.getWords()[0].toString())) {
                        return true;
                    }
                }
            }
            return false;

        } catch (Exception ex) {
            throw new RuntimeException(new ComplexMappingException
                    (ComplexMappingException.ExceptionType.LILA_EXCEPTION, "Could not get the " +
                    "words of a phrase to check hyponym. " + s1 + " " +s2, ex));
        }
    }
}
