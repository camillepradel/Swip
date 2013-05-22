
package test;

import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.Setting;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;
import java.io.File;
import java.util.ArrayList;
import parser.SchemaValidator;
import parser.XMLParser;
import pattern.Pattern;
import utility.Attributes;

/**
 *
 * @author Dominique Ritze
 *
 * Class to run all tests.
 *
 */
public class Test {

    static ArrayList<File> Ontologies = new ArrayList<File>();
    static File directory = new File("exp/Ontologies");

    public static void main(String args[]) {
        
        for(File ontology : directory.listFiles()) {
            Ontologies.add(ontology);
        }
//        utility.Attributes.xmlFile = new File("exp/Pattern/ICAT.xml");
//        utility.Attributes.xmlFile = new File("exp/Pattern/CAT.xml");
//        utility.Attributes.xmlFile = new File("exp/Pattern/CAV_P.xml");
//        utility.Attributes.xmlFile = new File("exp/Pattern/CAV_N.xml");
//        utility.Attributes.xmlFile = new File("exp/Pattern/CAT.xml");
         utility.Attributes.xmlFile = new File("exp/Pattern/ICAT.xml");

        for(File firstOntology : Ontologies) {
            compute(firstOntology);
        }
    }

    private static void compute(File firstOntology) {
        for(File secondOntology : Ontologies) {

            System.out.println(firstOntology.getName() + " - " + secondOntology.getName());
                if(firstOntology.equals(secondOntology)) {
                    continue;
                }
                Attributes.reset();
                utility.Attributes.sourcePath = firstOntology.getAbsolutePath();
                utility.Attributes.targetPath = secondOntology.getAbsolutePath();
                String nameFirstOnto = firstOntology.getName().split("\\.")[0];
                String nameSecondOnto = secondOntology.getName().split("\\.")[0];
                utility.Attributes.outputFile = new File("exp/Out/ICAT/"+ nameFirstOnto + "-" +
                        nameSecondOnto + ".txt");

                try {

                    try {
                        Setting.load();
                    } catch (MMatchException e) {
                        try {
                            throw new ComplexMappingException(ExceptionType.MMATCH_EXCEPTION, "Cannot load MMatch Settings.", e);
                        } catch (ComplexMappingException ex) {
                            System.err.println(ex);
                        }
                    }

                    if(utility.Attributes.xmlFile == null) {
                        throw new ComplexMappingException(ExceptionType.BAD_METHOD_CALL, "No XML-file given.");
                    }
                    if(utility.Attributes.outputFile == null) {
                        throw new ComplexMappingException(ExceptionType.BAD_METHOD_CALL, "No output filepath given.");
                    }
                    SchemaValidator sv = new SchemaValidator(utility.Attributes.xmlFile.getAbsolutePath());
                    sv.validate();
                    XMLParser xml = new XMLParser();
                    xml.parseFile(utility.Attributes.xmlFile.getAbsolutePath());
                    for(Pattern p : Attributes.correspondencePattern) {
                        p.computeCorrespondences();
                        p.writeOutput();
                    }
                    System.out.println("Alignment created!");
            } catch (Exception ex) {
                System.err.println(ex);
            }

            }
    }
}
