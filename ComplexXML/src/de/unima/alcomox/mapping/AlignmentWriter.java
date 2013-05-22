package de.unima.alcomox.mapping;


import de.unima.alcomox.exceptions.AlcomoException;

/**
* The MappingReader defines the interface for its implementing classes that can be used to write
* Mappings to xml as well as txt-files. 
*
*/
public interface AlignmentWriter {
	
	public void writeMapping(String filepath, Alignment mapping) throws AlcomoException;
	
	
		
}