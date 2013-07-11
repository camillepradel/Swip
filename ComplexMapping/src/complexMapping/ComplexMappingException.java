package complexMapping;

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
	private int descriptionId;
	private Exception parentException;
	
	public static final int BAD_PARAMETER = 1;
	public static final int BAD_METHOD_CALL = 2;
	public static final int CREATION_EXCEPTION = 3;
	public static final int REASONER_EXCEPTION = 4;	
	public static final int IO_EXCEPTION = 5;
	public static final int MAPPING_EXCEPTION = 6;
	public static final int MMATCH_EXCEPTION = 7;
	
	/**
	 * Constructor with a parent exception.
	 * 
	 * @param descriptionId
	 * @param description
	 * @param parentException
	 */
	public ComplexMappingException(int descriptionId, String description, Exception parentException) {
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
	public ComplexMappingException(int descriptionId, String description) {
		this.descriptionId=descriptionId;
		this.description=description;		
	}
	
	/**
	 * Print the exception with additional information and the parent exception if available.
	 */
	public String toString() {
		String rep = "REASONEREXCEPTION: \n";
		switch(this.descriptionId) {
			case 1: rep += "Bad parameter!";
					break;
			case 2: rep += "Bad or illegal method call!";
					break;
			case 3: rep += "Ontology creation error.";		
					break;
			case 4: rep += "Reasoning error, could not start reasoning.";
					break;
			case 5: rep += "IO Exception while trying to create the alignment.";
					break;
			case 6: rep += "Mapping Exception (Alcomox)";
					break;
			case 7: rep += "MMatch Exception (MMatch)";
					break;
		}		
		rep += description +"\n";
		
		if(parentException != null) {
			rep += "Caused by: " + parentException.toString();
		}
        return rep;		        
        
	}

	private static final long serialVersionUID = 1L;

}
