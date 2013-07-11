package de.unima.ki.mmatch;

public class MMatchException extends Exception {
	
	private String specificDescription;
	private int descriptionId;
	private Exception parentException;

	public static final int IO_ERROR = 1;	
	public static final int COMPARE_VALUE_OUT_OF_BOUNDS = 2;
	public static final int STEMMER_ERROR =3;
	public static final int OWL_ONTOLOGY_CREATION_ERROR = 4;
	
	/**
	* Bad parameter for method call.
	*/
	public static final int BAD_PARAMETER = 5;
	public static final int JWNL_ERROR = 6;
	public static final int NOT_YET_IMPLEMENTED = 7;
	
	public MMatchException(int descriptionId, String specificDescription, Exception parentException) {
		this(descriptionId, specificDescription);
		this.parentException=parentException;
	}
	
	public MMatchException(int descriptionId, String specificDescription) {
		this.parentException = null;
		this.descriptionId=descriptionId;
		this.specificDescription=specificDescription;		
	}
	
	public String toString() {
		String rep = "MMATCHEXCEPTION: \n";
		switch(this.descriptionId) {
		case IO_ERROR: 
			rep += "An IO-Error occured. \n";
			break;		
			
		case COMPARE_VALUE_OUT_OF_BOUNDS:
			rep += "The compare value is lower 0 or higher 1 \n";
			break;
		
		case STEMMER_ERROR:
			rep += "A Stemmer-Error occured. \n";
			break;
		
		case OWL_ONTOLOGY_CREATION_ERROR:
			rep += "Could not create an internal ontology out of the given ontology \n";
			break;
		
		case BAD_PARAMETER:
			rep += "Bad parameter for method call \n";
			break;
		
		case JWNL_ERROR:
			rep+= "A JWNL-error occured. \n ";
			break;
			
		case NOT_YET_IMPLEMENTED:
			rep+= "Upps, not yet implemented. \n ";
			break;
			
		}		
		rep += specificDescription +"\n";
		
		if(parentException != null) {
			rep += "Caused by: " + parentException.toString();
		}
        return rep;		        
        
	}


	private static final long serialVersionUID = 1L;
	
	

}
