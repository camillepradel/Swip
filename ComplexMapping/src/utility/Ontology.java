package utility;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import complexMapping.ComplexMappingException;

import correspondenceComputations.ComputationSetting;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.descriptions.Term;

/**
 * 
 * @author Dominique Ritze
 * 
 * The class Ontology contains the classes, properties and some ontology characteristics like the filepath.  
 *
 */
public class Ontology {
	
	private boolean[] delimiter = new boolean[3];
	private Set<OWLClass> classes = new HashSet<OWLClass>();
	private Set<OWLObjectProperty> objectProperties = new HashSet<OWLObjectProperty>();
	private Set<OWLDataProperty> datatypeProperties = new HashSet<OWLDataProperty>();
	private OWLOntology ontology;
	private URI filepath;
	private HashMap<OWLClass, Term> termClasses = new HashMap<OWLClass, Term>();
	private HashMap<OWLObjectProperty, Term> termObjectProperties = new HashMap<OWLObjectProperty, Term>();
	private HashMap<OWLDataProperty, Term> termDatatypeProperties = new HashMap<OWLDataProperty, Term>();
	
	/**
	 * Create an ontology out of the filepath and build all important structures (like set of classes or properties).
	 * 
	 * @param filepath
	 * @throws ComplexMappingException 
	 */
	public Ontology(String filepath) throws ComplexMappingException {
		
		this.ontology = buildOntology(filepath);		
		// this.classes = ontology.getReferencedClasses();
		// this.objectProperties = ontology.getReferencedObjectProperties();
		// this.datatypeProperties = ontology.getReferencedDataProperties();
		
		this.classes = ontology.getClassesInSignature(true);
		this.objectProperties = ontology.getObjectPropertiesInSignature(true);
		this.datatypeProperties = ontology.getDataPropertiesInSignature(true);
		
		this.delimiter = getDelimiter(ontology);
		File file = new File(filepath);
		this.filepath = file.toURI();
		
		try {
			//build a hash map with OWLClasses and the corresponding terms
			for(OWLClass cls : classes) {
				if(cls.getIRI().getFragment()!= null) {
					termClasses.put(cls, new Term(cls.getIRI().getFragment(), delimiter));
				}
				//avoid some problems with URIs which does not contain a #
				else {
					String[] realName = cls.getIRI().toString().split("/");
					termClasses.put(cls, new Term(realName[realName.length-1], delimiter));
				}
			}
			//build the object property - term map
			for(OWLObjectProperty prop : objectProperties) {
				if(prop.getIRI().getFragment() != null) {
					termObjectProperties.put(prop, new Term(prop.getIRI().getFragment(), delimiter, true));
				}
				else {
					String[] realName = prop.getIRI().toString().split("/");
					termObjectProperties.put(prop, new Term(realName[realName.length-1], delimiter, true));
				}
			}
			//build the datatype property - term map
			for(OWLDataProperty prop : datatypeProperties) {
				if(prop.getIRI().getFragment() !=null) {
					termDatatypeProperties.put(prop, new Term(prop.getIRI().getFragment(), delimiter, true));
				}	
				else {
					String[] realName = prop.getIRI().toString().split("/");
					termDatatypeProperties.put(prop, new Term(realName[realName.length-1], delimiter, true));
				}
			}
		} catch(MMatchException e) {
			throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not create maps with terms in ontology.", e);
		}
		
	}
	
	/**
	 * Compute the delimiter which occur in the ontology.
	 * 
	 * @param ontology
	 * @return Array with the real delimiter, first entry: hyphen, second: blank, third: underscore. The entry is true if the
	 * character should be a real delimiter.
	 */
	public static boolean[] getDelimiter(OWLOntology ontology) {
		int countHyphen = 0;
		int countUnderscore = 0;
		int countBlank = 0;	
		int size = ontology.getClassesInSignature(true).size();
		
		/*
		 * The following three loops check which delimiters are used in the ontology. 
		 * If occurence delimiter devided by number of all delimiters is less than 0.05
		 * the delimiter is not considered as a real delimiter.
		 */
		//iterate through all classes of the ontology and count the hypens, the blanks and the underscores
		for(OWLClass cls : ontology.getClassesInSignature(true)) {
			if(cls.toString().contains("-")) {
				countHyphen++;
			}
			if(cls.toString().contains(" ")) {
				countBlank++;
			}
			if(cls.toString().contains("_")) {
				countUnderscore++;
			}
		}
        	
		//iterate through all data properties and count hypens, blanks, underscores
		for(OWLDataProperty dataProp : ontology.getDataPropertiesInSignature(true)) {
			if(dataProp.toString().contains("-")) {
				countHyphen++;
			}
			if(dataProp.toString().contains(" ")) {
				countBlank++;
			}
			if(dataProp.toString().contains("_")) {
				countUnderscore++;
			}
		}
        	
		//iterate through all object properties and count hypens, blanks, underscores
		for(OWLObjectProperty objectProp : ontology.getObjectPropertiesInSignature(true)) {  
			if(objectProp.toString().contains("-")) {
				countHyphen++;
			}
			if(objectProp.toString().contains(" ")) {
				countBlank++;
			}
			if(objectProp.toString().contains("_")) {
				countUnderscore++;
			} 
		}
		boolean[] delimiter = new boolean[]{false, false, false};
		if((double)countHyphen/(double)size > ComputationSetting.getDelimiterPercent()) {
			delimiter[0] = true;
		}
		if((double)countBlank/(double)size > ComputationSetting.getDelimiterPercent()) {
			delimiter[1] = true;
		}
		if((double)countUnderscore/(double)size > ComputationSetting.getDelimiterPercent()) {
			delimiter[2] = true;
		}
		return delimiter;
	}
	
	/**
	 * Build an OWLOntology out of a filepath.
	 * 
	 * @param filepath
	 * @return
	 * @throws ComplexMappingException 
	 */
	public static OWLOntology buildOntology(String filepath) throws ComplexMappingException {
		//create the OWLOntologies out of the filepaths
		OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();    	
    	File ontologyFile = new File(filepath);        
    	try {
			// return
			// manager1.loadOntologyFromPhysicalURI(URI.create(ontologyFile.toURI().toString())); 
    		
			return manager1.loadOntology(IRI.create(ontologyFile.toURI()));
    		
    	} catch(OWLOntologyCreationException e) {
    		throw new ComplexMappingException(ComplexMappingException.CREATION_EXCEPTION, "Could not create an OWLOntology in Ontology.", e);
    	}
    	
    	   	
	}

	public boolean[] getDelimiter() {
		return delimiter;
	}

	public Set<OWLClass> getClasses() {
		return classes;
	}

	public Set<OWLObjectProperty> getObjectProperties() {
		return objectProperties;
	}

	public Set<OWLDataProperty> getDatatypeProperties() {
		return datatypeProperties;
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public URI getFilepath() {
		return filepath;
	}

	public HashMap<OWLClass, Term> getTermClasses() {
		return termClasses;
	}

	public HashMap<OWLObjectProperty, Term> getTermObjectProperties() {
		return termObjectProperties;
	}

	public HashMap<OWLDataProperty, Term> getTermDatatypeProperties() {
		return termDatatypeProperties;
	}
}
