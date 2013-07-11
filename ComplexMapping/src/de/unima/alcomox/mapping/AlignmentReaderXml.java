package de.unima.alcomox.mapping;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import de.unima.alcomox.exceptions.CorrespondenceException;
import de.unima.alcomox.exceptions.MappingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
* A mapping reader for rdf/xml format reads mappings from rdf-files. The format supported is the
* one described in the Alignment API. But notice that not all kinds of specifications are supported.
* More precise it should work with any level 0 mapping, where the relations =, < and > are used
* to state correspodences between concepts or properties.
*/
public class AlignmentReaderXml extends DefaultHandler implements AlignmentReader {
    
	
	private String entity1;
	private String entity2;
	
	private String relation;
	private boolean readRelation;
	private String confidence;
	private boolean readConfidence;
	private File xmlFile;
	private ArrayList<Correspondence> correspondences;
	private MappingException internalException = null;
	
	
	public AlignmentReaderXml() {
		this.confidence = "";
	}

	public Alignment getMapping(String filepath) throws MappingException {
		this.readRelation = false;
		this.readConfidence = false;
		this.xmlFile = new File(filepath);
		this.correspondences = new ArrayList<Correspondence>();   
		Alignment mapping = new Alignment();
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(this.xmlFile, this);
		}
		catch (ParserConfigurationException e) {
			throw new MappingException(
				MappingException.IO_ERROR,
				"caused by parsing " + this.xmlFile.toString() + " (ParserConfigurationException)",
				e
			);
		}
		catch (SAXException e) {
			throw new MappingException(
				MappingException.IO_ERROR,
				"caused by parsing " + this.xmlFile.toString() + " (SAXException)",
				e
			);
		}
		catch (IOException e) {
			throw new MappingException(
				MappingException.IO_ERROR,
				"caused by parsing " + this.xmlFile.toString(),
				e
			);
		}
		if (this.internalException != null) { throw internalException; }
		mapping.setCorrespondences(correspondences);
		return mapping;
	}

    public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
    	if (qName.toLowerCase().equals("entity1")) {
    		this.entity1 = attrs.getValue("rdf:resource");
    	}
    	if (qName.toLowerCase().equals("entity2")) {
    		this.entity2 = attrs.getValue("rdf:resource");
    	}
    	if (qName.toLowerCase().equals("relation")) {
    		this.readRelation = true;
    	}
    	if (qName.toLowerCase().equals("measure")) {
    		this.readConfidence = true;
    	}
    }
    
    public void characters(char buf[], int offset, int len) throws SAXException {
        if (this.readRelation) {
        	this.relation = new String(buf, offset, len);	
        }
        if (this.readConfidence) {
        	this.confidence += new String(buf, offset, len);
        }        
        
    }    

    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
    	if (qName.toLowerCase().equals("cell")) {
    		float sim = (float)Double.parseDouble(this.confidence);
    		if (sim < 0.0 || sim > 1.0) {
    			BigDecimal d = new BigDecimal(this.confidence);
    			sim = d.floatValue();
    		}
    		this.confidence = "";
    		Correspondence correspondence;
    		
    		int relationType = SemanticRelation.NA;
    		if (this.relation == null) { relationType = SemanticRelation.EQUIV; }
    		else {
	    		if (this.relation.equals("=")) { relationType = SemanticRelation.EQUIV; }
	    		if (this.relation.equals("<")) { relationType = SemanticRelation.SUB; }
	    		if (this.relation.equals(">")) { relationType = SemanticRelation.SUPER; }
    		}
    		SemanticRelation semanticRelation;
			try {
				semanticRelation = new SemanticRelation(relationType);
				correspondence = new Correspondence(this.entity1 , this.entity2, semanticRelation, (double)sim);
				correspondences.add(correspondence);
			}
			catch (CorrespondenceException e) {
				this.internalException = new MappingException(
					MappingException.IO_ERROR,
					"caused by parsing " + this.xmlFile.toString() + " (SAXException)",
					e
				);
			}


    		
    	}
    	this.readConfidence = false;
    	this.readRelation = false;
    }
    
    


}
