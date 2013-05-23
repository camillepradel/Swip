
package utility;

import lila.syntax.Phrase;
import lila.syntax.Verb;
import lila.syntax.Word;
import ontology.Ontology;

import org.semanticweb.owlapi.model.OWLEntity;

import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.descriptions.Term;
import exception.ComplexMappingException;

/**
 *
 * @author Dominique Ritze
 *
 * Class containing all methods to normalize a string.
 * For example a spell checking is provided.
 *
 */
public class Normalizer {

    static Term t;

    /**
     * Normalize a string s and return the normalited phrase.
     * Includes error correction and spell checking.
     * Deletes all prepositions like "by".
     * The normalized string is castet to lower case and separeted by
     * blanks, this is the form a phrase requires a string.
     *
     * @param s
     * @param delimiters
     * @return
     * @throws ComplexMappingException
     */
    public static Phrase normalize(String s, boolean... delimiters) throws ComplexMappingException {

        String normalizedString = "";
        t = null;

        //set the delimiters if they have been passed or try to determine them
        if(delimiters.length == 3) {
            try {
                t = new Term(correctErrors(s), new boolean[]{delimiters[0], delimiters[1], delimiters[2]});
            }
            catch(MMatchException me) {
                throw new ComplexMappingException(ComplexMappingException.ExceptionType.MMATCH_EXCEPTION,
                    "Could not create term of string " +s);
            }
        }
        else  if(Attributes.firstOntology != null && Attributes.secondOntology != null) {
            checkString(s, Attributes.firstOntology);
            checkString(s, Attributes.secondOntology);
        }

        if(t == null) {
            try {
                t = new Term(correctErrors(s), new boolean[]{false, true, true});
            }
            catch(MMatchException me) {
                throw new ComplexMappingException(ComplexMappingException.ExceptionType.MMATCH_EXCEPTION,
                    "Could not create term of string " +s);
            }            
        }
        int counter = 0;
        for(String part : t.getTokens()) {
            counter++;
            if(part.isEmpty()) {
                continue;
            }
            Word w = new Word(part.toLowerCase());
            
            try {                

                if(w.isVerb() || w.getCategory().toLowerCase().equals("verb")) {
                    w = new Verb(part.toLowerCase());
                }

                //delete all prepositions
                if(w.exists() && w.getPartOfSpeech() != null && w.getPartOfSpeech().equals("IN")) {
                    continue;
                }
                //check whether a word has a sense, otherwise check the spelling
                if(w.getSenses().length == 0){
                    
                    Word wAsVerb = new Verb(part.toLowerCase());
                    if(wAsVerb.checkSpelling().exists()) {
                        w = wAsVerb.checkSpelling();
                    }
                    else {
                        w = w.checkSpelling();
                    }
                    normalizedString = normalizedString + w.toString() + " ";
                }
                else {
                    normalizedString = normalizedString + part.toLowerCase() + " ";
                }
            }
            //avoid some problems with nullpointer and just separate the
            //words by blanks
            catch(NullPointerException ne) {
                normalizedString = "";
                for(String token : t.getTokens()) {
                     normalizedString += token + " ";
                }
                if(normalizedString.endsWith(" ")) {
                    normalizedString = normalizedString.substring(0,normalizedString.length()-1);
                }
                
                return new Phrase(normalizedString);
            }
            catch(Exception e) {
                throw new ComplexMappingException(ComplexMappingException.ExceptionType.LILA_EXCEPTION,
                    "Could not determine part of speech of string " +s);
            }
        }
        if(normalizedString.endsWith(" ")) {
            normalizedString = normalizedString.substring(0,normalizedString.length()-1);
        }

        return new Phrase(normalizedString);
    }

    /**
     * Correct some errors which result from external tools like wordnet or
     * morphadorner.
     *
     * @param s
     * @return
     */
    private static String correctErrors(String s) {
        if(s.equals("registered")){
            return("registerred");
        } 
        return s;
    }

    /**
     * Create a term out of a string.
     * Therefore the delimiters must be determined.
     *
     * @param s
     * @param o
     * @throws ComplexMappingException
     */
    private static void checkString(String s, Ontology o) throws ComplexMappingException {
        for(OWLEntity c : o.getEntities()) {
            if(c.getIRI().getFragment().equals(s)) {
                try {
                    t = new Term(s, o.getDelimiter());
                }
                catch(MMatchException me) {
                    throw new ComplexMappingException(ComplexMappingException.ExceptionType.MMATCH_EXCEPTION,
                            "Could not create term of string " +s);
                }
            }
        }
    }
}
