package de.unima.ki.mmatch.descriptions;

import de.unima.ki.mmatch.MMatchException;

/**
 *
 * @author Dominique Ritze
 */
public class Text extends Term{	
	
	/**
	 * Create a new term for a concept.
	 * 
	 * @param content
	 * @param delimiter
	 * @throws MMatchException
	 */
	public Text(String content, boolean[] delimiter) throws MMatchException {
		super(content, delimiter);
	}
	
	/**
	 * Create a new term for a property.
	 * 
	 * @param content
	 * @param delimiter
	 * @param property
	 * @throws MMatchException
	 */
	public Text(String content, boolean[] delimiter, boolean property) throws MMatchException {
		super(content, delimiter, property); 
	}
}