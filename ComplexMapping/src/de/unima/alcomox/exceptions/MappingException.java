package de.unima.alcomox.exceptions;


/**
* A mapping exception handles every type of problems related to mappings and
* catches internally thrown correspondence exceptions. It might also be thrown,
* whenever some reading or writing operation formapping files result in problems.
*/
public class MappingException extends AlcomoException {

	public static final int CORRESPONDENCE_PROBLEM = 1;
	public static final int IO_ERROR = 2;
	public static final int INVALID_FORMAT = 3;

	public MappingException(int generalDescriptionId, String specificDescription, Exception e) {
		this(generalDescriptionId, specificDescription);
		this.catchedException = e;
	}	
	
	public MappingException(int generalDescriptionId, String specificDescription) {
		this(generalDescriptionId);
		this.specificDescription = specificDescription;
	}
	
	public MappingException(int generalDescriptionId) {
		super("Mapping-Exception");
		switch (generalDescriptionId) {
		case CORRESPONDENCE_PROBLEM:
			this.generalDescription = "problem with a correspondence occured";
			break;
		case IO_ERROR:
			this.generalDescription = "IO-operation caused an error";
			break;
		case INVALID_FORMAT:
			this.generalDescription = "invalid format in mapping file";
			break;
		default:
			this.generalDescription = "general description is missing";
			break;
		}
	}
	
	private static final long serialVersionUID = 1L;


	
}
