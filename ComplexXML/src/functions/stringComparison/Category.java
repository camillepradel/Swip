
package functions.stringComparison;

import exception.ComplexMappingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import ontology.Ontology;
import utility.Attributes;

/**
 *
 * @author Dominique Ritze
 */
public class Category implements StringComparison {

    public boolean compute(String s1, String s2) throws ComplexMappingException {

        if(s1.isEmpty() || s2.isEmpty()) {
            return false;
        }

        try {

            Ontology usedOntology;

           if(Attributes.firstOntology.getEntityNames().contains(s1)) {
                usedOntology = Attributes.firstOntology;
            }
            else {
                usedOntology = Attributes.secondOntology;
            }
            if(!usedOntology.isTaggerLoaded()) {
                usedOntology.executeTagger(usedOntology.getFilepath().toString().substring(
                    usedOntology.getFilepath().toString().indexOf("/")+1));
            }            

            String ontologyName = usedOntology.getFilepath().toString().substring(
                    usedOntology.getFilepath().toString().lastIndexOf("/")+1,
                    usedOntology.getFilepath().toString().lastIndexOf("."));
            File taggerFile = new File("tagger/out/"+ ontologyName+".txt");
            BufferedReader reader = new BufferedReader(new FileReader(taggerFile));
            String next = reader.readLine();
            while(next != null) {
                if(next.contains(s1)) {
                    if(next.split("\\$")[1].equals(s2) && next.split("\\$")[0].equals(s1)) {
                        System.out.println(next);
                        System.out.println(s1 + " - " + s2);
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                next = reader.readLine();
            }

            reader.close();

        } catch(Exception e) {
            throw new ComplexMappingException(ComplexMappingException.ExceptionType.TAGGER_EXCEPTION, "Cannot determine semantic class.", e);
        }
        return false;
    }

}
