package utility;

/**
 * 
 * @author Dominique Ritze
 * 
 * This class is used to set and get the path of an alignment.
 *
 */
public class OntologyAlignment {
	
	private String path;
	
	/**
	 * Constructor to set the path.
	 * 
	 * @param path
	 */
	public OntologyAlignment(String path) {
		this.path = path;
	} 
	
	/**
	 * Get the path.
	 * 
	 * @return
	 */
	public String getPath() {
		return this.path;
	}

}
