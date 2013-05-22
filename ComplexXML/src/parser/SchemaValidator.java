package parser;

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;

/**
 * 
 * @author Dominique Ritze
 *
 * Validator for the XML file which checks if the XML file
 * is well-defined accoring to the specified XML schema.
 *
 */
public class SchemaValidator {
	
	private String filepath;

        /**
         *
         * @param filepath Path to the XML file.
         */
	public SchemaValidator(String filepath) {
		this.filepath = filepath;
	}

        /**
         * Validate the XML file.
         *
         * @return True if the XML file is well-defined according to
         * the XML schema.
         * @throws ComplexMappingException
         */
	public boolean validate() throws ComplexMappingException{
			   
	    try {
	    	// parse an XML document into a DOM tree
		    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    Document document = parser.parse(new File(filepath));

		    // create a SchemaFactory capable of understanding WXS schemas
		    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		    // load a WXS schema, represented by a Schema instance
		    Source schemaFile = new StreamSource(new File("complexSchema.xsd"));
		    Schema schema = factory.newSchema(schemaFile);

		    // create a Validator instance, which can be used to validate an instance document
		    Validator validator = schema.newValidator();
	    	
		    // validate the DOM tree
	        validator.validate(new DOMSource(document));
	        return true;
	        
	    } catch (SAXException e) {
	    	throw new ComplexMappingException(ExceptionType.VALIDATION_EXCEPTION, "XML-document invalid because it does" +
	    			" not ensure the according XML-schema.", e);
	    } catch (IOException io) {
	    	throw new ComplexMappingException(ExceptionType.IO_EXCEPTION, "Cannot open XML-document or schema.", io);
	    } catch (ParserConfigurationException pe) {
	    	throw new ComplexMappingException(ExceptionType.PARSING_EXCEPTION, "Error while trying to parse document/schema in the" +
	    			" schema validation", pe);
	    }
            catch (Exception e) {
                throw new ComplexMappingException(ExceptionType.BAD_METHOD_CALL, "No files given.", e);
            }

	}

}
