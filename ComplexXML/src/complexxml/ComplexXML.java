
package complexxml;

import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.Setting;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;
import java.io.File;
import parser.SchemaValidator;
import parser.XMLParser;
import pattern.Pattern;
import utility.Attributes;

/**
 *
 * @author Dominique Ritze
 */
public class ComplexXML {

    /**
     * Method to generate a complex alignment without graphical
     * user interface.
     * First parameter should be the path to the xml-file in which
     * the pattern is described.
     * Optional the third and fourth parameter specify the ontologies.
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            //first parameter is the xml-file
            if(args.length >= 2) {
                utility.Attributes.xmlFile = new File(args[0]);
                utility.Attributes.outputFile = new File(args[1]);
            }
            //additional the ontologies
            else if (args.length == 4) {
                utility.Attributes.sourcePath = args[2];
                utility.Attributes.targetPath = args[3];
            }
            else {
                System.err.println("Wrong parameters.");
            }


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
            //validate the xml-file and create the alignment
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
            ex.printStackTrace();
        }
    }

}
