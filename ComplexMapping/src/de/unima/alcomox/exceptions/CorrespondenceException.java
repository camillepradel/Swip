package de.unima.alcomox.exceptions;


/**
* A correspondence exception handles every type of problems related to
* correspondences and their internal components.
*/
public class CorrespondenceException extends AlcomoException {
		
	public static final int INVALID_SEMANTIC_RELATION = 1;
	public static final int INVALID_CONFIDENCE_VALUE = 2;
	public static final int MISSING_NAMESPACE = 3;
	

	public CorrespondenceException(int generalDescriptionId, String specificDescription, Exception e) {
		this(generalDescriptionId, specificDescription);
		this.catchedException = e;
	}	
	
	public CorrespondenceException(int generalDescriptionId, String specificDescription) {
		this(generalDescriptionId);
		this.specificDescription = specificDescription;
	}
	
	public CorrespondenceException(int generalDescriptionId) {
		super("Correspondence-Exception");
		switch (generalDescriptionId) {
		case INVALID_SEMANTIC_RELATION:
			this.generalDescription = "An invalid (not available) semantic relation occured";
			break;
		case INVALID_CONFIDENCE_VALUE:
			this.generalDescription = "An invalid confidence value occured (must double in range 0.0 to 1.0)";
			break;
		case MISSING_NAMESPACE:
			this.generalDescription = "An entity reference without namespace occured";
			break;
		default:
			this.generalDescription = "General description is missing";
			break;
		}
	}
	
	private static final long serialVersionUID = 1L;
	
}
