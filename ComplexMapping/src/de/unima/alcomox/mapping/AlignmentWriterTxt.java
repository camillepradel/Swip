package de.unima.alcomox.mapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import de.unima.alcomox.exceptions.AlcomoException;
import de.unima.alcomox.exceptions.MappingException;

/**
* A MappingWriterTxt writes Mappings to txt-files. 
*
*/
public class AlignmentWriterTxt  implements AlignmentWriter {
	
	public AlignmentWriterTxt() { }
	
	public void writeMapping(String filepath, Alignment mapping) throws AlcomoException {
		File file = new File(filepath);
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(mapping.toString());
			fw.flush();
			fw.close();
		}
		catch (IOException e) {
			throw new MappingException(MappingException.IO_ERROR, "could not create/access file " + file);
		}
		
	}
	
	
}