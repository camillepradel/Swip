
package functions.stringManipulation;

import exception.ComplexMappingException;
import lila.syntax.Phrase;
import utility.Normalizer;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the word class condition.
 * The different part of speeches are noun, adjective,
 * verb, adverb or unknown.
 *
 */
public class WordClass implements StringManipulation{

    public String compute(String s1) {
        
        try {            
            Phrase p1 = Normalizer.normalize(s1);
            //determine the word type
            return p1.getType();

        } catch(Exception e) {
            throw new RuntimeException("Could not determine word class of " +s1, new
                    ComplexMappingException(ComplexMappingException.ExceptionType.LILA_EXCEPTION, "", e));
        }

    }

}
