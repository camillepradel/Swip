package de.unima.alcomox.mapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import de.unima.alcomox.exceptions.MappingException;

/**
* A MappingWriterXml writes Mappings to xml-files. 
*
*/
public class AlignmentWriterXml implements AlignmentWriter {
	
	public AlignmentWriterXml() { }
	
	public void writeMapping(String filepath, Alignment mapping) throws MappingException {
		File file = new File(filepath);
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(getXMLString(mapping));
			fw.flush();
			fw.close();
		}
		catch (IOException e) {
			throw new MappingException(MappingException.IO_ERROR, "could not create/access file " + file);
		}
		
	}
	
	private String getXMLString(Alignment mapping) {
		StringBuffer mappingXML = new StringBuffer();
		ArrayList<Correspondence> correspondences = mapping.getCorrespondences();
		mappingXML.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		// mappingXML.append("<!DOCTYPE rdf:RDF SYSTEM \"align.dtd\">\n");
		mappingXML.append("<rdf:RDF xmlns=\"http://knowledgeweb.semanticweb.org/heterogeneity/alignment\" ");
		mappingXML.append("\n\t xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" ");
		mappingXML.append("\n\t xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n\n");		
		mappingXML.append("<Alignment>\n");
		mappingXML.append("<xml>yes</xml>\n");
		mappingXML.append("<level>0</level>\n");
		mappingXML.append("<type>??</type>\n\n");
		for (Correspondence c : correspondences) {
			mappingXML.append(getMappedCell(c));
		}
		mappingXML.append("\n</Alignment>\n");
		mappingXML.append("</rdf:RDF>\n");
		return mappingXML.toString();
	}
	
	private StringBuffer getMappedCell(Correspondence correspondence) {
		StringBuffer mappedCell = new StringBuffer();
		mappedCell.append("<map>\n");
		mappedCell.append("\t<Cell>\n");
		mappedCell.append("\t\t<entity1 rdf:resource=\"" + correspondence.getSourceEntityUri() + "\"/>\n");
		mappedCell.append("\t\t<entity2 rdf:resource=\"" + correspondence.getTargetEntityUri() + "\"/>\n");
		mappedCell.append("\t\t<measure rdf:datatype=\"xsd:float\">" + correspondence.getConfidence() + "</measure>\n");
        mappedCell.append("\t\t<relation>" + correspondence.getRelation() + "</relation>\n");
		mappedCell.append("\t</Cell>\n");
		mappedCell.append("</map>\n");
		return mappedCell;
	}

}

