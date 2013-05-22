package de.unima.alcomox.mapping;

import de.unima.alcomox.exceptions.MappingException;


/**
* The MappingReader defines the interface for its implementing classes that can be used to read
* Mappings from xml as well as txt-files. 
*
*/

public interface AlignmentReader {
	
	public static final int FORMAT_TXT = 1;
	public static final int FORMAT_RDF = 2;	
	
	public Alignment getMapping(String filepath) throws MappingException;	
}