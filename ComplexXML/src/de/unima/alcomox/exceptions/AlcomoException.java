package de.unima.alcomox.exceptions;

/**
* Every problem that might occur and cannot be solved is thrown as
* instance of this class. Also any exception thrown internally is captured
* and transformed into an alcomo exception.
*/
public abstract class AlcomoException extends Exception {
	
	protected String mainDescription;
	protected String generalDescription;
	protected String specificDescription;
	protected Exception catchedException;

	public AlcomoException(String mainDescription) {
		this.mainDescription = mainDescription;
	}



	/**
	* Returns a human understandable string representation.
	* 
	* @return A string representation of this exception. 
	*/
	public String toString() {
		String repr = "\n";
		repr += "Alcomo-Exception caused by " + this.mainDescription + "\n";
		repr += "General: " + this.generalDescription + "\n";
		if (this.specificDescription != null) {
			repr += "Specific: " + this.specificDescription + "\n";
		}
		if (this.catchedException != null) {
			repr += "Caught exception: " + this.catchedException + ".\n";
		}
		return repr;
	}


	
	private static final long serialVersionUID = 1L;
	
}
