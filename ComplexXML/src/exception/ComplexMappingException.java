 package exception;

/**
 * 
 * @author Dominique Ritze
 * 
 * Class to create exceptions which can be thrown if an exception occurs during the
 * matching process.
 * Helps to get better information why an exception was thrown.
 *
 */
public class ComplexMappingException extends Exception{
	
	private String description;
	private ExceptionType descriptionId;
	private Exception parentException;

        public enum ExceptionType {
            BAD_PARAMETER, BAD_METHOD_CALL, CREATION_EXCEPTION, REASONER_EXCEPTION,
            IO_EXCEPTION, MAPPING_EXCEPTION, MMATCH_EXCEPTION, PARSING_EXCEPTION,
            VALIDATION_EXCEPTION, INVALID_REF_ALIGNMENT, LILA_EXCEPTION,
            TAGGER_EXCEPTION
        }
	
	/**
	 * Constructor with a parent exception.
	 * 
	 * @param descriptionId
	 * @param description
	 * @param parentException
	 */
	public ComplexMappingException(ExceptionType descriptionId, String description, Exception parentException) {
		this.descriptionId = descriptionId;
		this.description = description;
		this.parentException=parentException;
	}
	
	/**
	 * Constructor without a parent exception.
	 * 
	 * @param descriptionId
	 * @param description
	 */
	public ComplexMappingException(ExceptionType descriptionId, String description) {
		this.descriptionId=descriptionId;
		this.description=description;		
	}
	
	/**
	 * Print the exception with additional information and the parent exception if available.
	 */
    @Override
	public String toString() {
		String rep = "EXCEPTION: \n";
		switch(descriptionId) {
			case BAD_PARAMETER: rep += "Bad parameter!\n";
					break;
			case BAD_METHOD_CALL: rep += "Bad or illegal method call!\n";
					break;
			case CREATION_EXCEPTION: rep += "Ontology creation error.";
					break;
			case REASONER_EXCEPTION: rep += "Reasoning error, could not start reasoning.\n";
					break;
			case IO_EXCEPTION: rep += "IO Exception while trying to create the correspondences.\n";
					break;
			case MAPPING_EXCEPTION: rep += "Mapping Exception (Alcomox)\n";
					break;
			case MMATCH_EXCEPTION: rep += "MMatch Exception (MMatch)\n";
					break;
			case PARSING_EXCEPTION: rep += "Cannot parse XML-file or schema.\n";
					break;
			case VALIDATION_EXCEPTION: rep += "XML-file not valid to schema.\n";
                                        break;
                        case INVALID_REF_ALIGNMENT: rep+= "The reference alignment contains correspondences between object- and datatype " +
                                        "properties which is not permitted. Please refactor the reference alignment and retry.";
		}		
		rep += description +"\n";
		
		if(parentException != null) {
			rep += "Caused by: " + parentException.toString();
		}
        return rep;		        
        
	}

	private static final long serialVersionUID = 1L;

}
