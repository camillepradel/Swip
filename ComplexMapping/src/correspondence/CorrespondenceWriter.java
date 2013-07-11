package correspondence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import complexMapping.ComplexMappingException;

/**
 * 
 * @author Dominique Ritze
 * 
 * Class to write the complex correspondences into a file. 
 *
 */
public class CorrespondenceWriter {
	
	//set with all correspondences
	Set<Correspondence> corres;
	
	/**
	 * Constructor
	 * 
	 * @param corres
	 */
	public CorrespondenceWriter(Set<Correspondence> corres) {
		this.corres = corres;
	}
	
	/**
	 * Write the alignment into the file given by the file path.
	 * 
	 * @param filepath
	 * @throws ComplexMappingException
	 */
	public void writeFileWithCorrespondences(String filepath) throws ComplexMappingException {
		File file = new File(filepath);
		try {
			FileWriter writer = new FileWriter(file);
			BufferedWriter writeFile = new BufferedWriter(writer);
			
			for(Correspondence c : this.corres) {
				writeFile.write(c.toShortString());
				writeFile.newLine();
			}
			
			writeFile.flush();
			writer.close();
			
		} catch(IOException ioe) {
			throw new ComplexMappingException(ComplexMappingException.IO_EXCEPTION,"Error writing alignment", ioe);
		}		
		
	}

}
