package reasoner;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

import utility.OntologyAlignment;

import complexMapping.ComplexMappingException;

import de.unima.alcomox.exceptions.MappingException;
import de.unima.alcomox.mapping.Alignment;
import de.unima.alcomox.mapping.AlignmentFormatReader;
import de.unima.alcomox.mapping.AlignmentReader;
import de.unima.alcomox.mapping.AlignmentReaderTxt;
import de.unima.alcomox.mapping.Correspondence;

/**
 * 
 * @author Dominique Ritze
 * 
 * This class is used to gather inferred information about an ontology and especially information about 
 * classes and properties in another ontolgy. Information about the other ontology can be collected through a given
 * alignment. If a class can be found in an alignment it is equivalent to a class in the other ontology.
 * All subclasses of the class are also subclasses of the equivalent in the other ontology.
 *
 */
public class ReasonerTwoOntologies extends Reasoner {
	
	private OWLReasoner reasoner1;
	private OWLReasoner reasoner2;
	private OWLOntology ontology2;
	private ArrayList<Correspondence> correspondences;
	
	/**
	 * Constructor to save all information.
	 * 
	 * @param onto1
	 * @param onto2
	 * @param ali
	 * @throws ComplexMappingException
	 */
	public ReasonerTwoOntologies(URI onto1, URI onto2, OntologyAlignment ali) throws ComplexMappingException {
		this.reasoner1 = super.createReasoner(onto1);
		this.reasoner2 = super.createReasoner(onto2);
		//create ontology manager 
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();   
		//load second ontology
		try {
			this.ontology2 = manager.loadOntology(IRI.create(onto2));
		} catch(OWLOntologyCreationException e) {
			throw new ComplexMappingException(ComplexMappingException.CREATION_EXCEPTION, "Could not create second ontolgy in ReasonerTwoOntologies.", e);
		}
			
		
		
		//get the reference alignment
		Alignment reference;
		AlignmentReader mr;
		
		//test which format is the right one, xml or txt.
		try {
			mr = new AlignmentFormatReader();
			reference = mr.getMapping(ali.getPath());
		} catch(MappingException e) {
			try {
				mr = new AlignmentReaderTxt();
				reference = mr.getMapping(ali.getPath());
			} catch(MappingException me) {
				throw new ComplexMappingException(ComplexMappingException.MAPPING_EXCEPTION, "Could not read alignment.", me);
			}
			
		}
		
		this.correspondences = reference.getCorrespondences();  
	}
	
	/**
	 * Get all typed(sub, super, ancestor, descendant) classes out of both ontologies (an alignment is required).
	 * 
	 * @param con
	 * @param onto1
	 * @param onto2
	 * @param ali
	 * @param type
	 * @return
	 * @throws ComplexMappingException
	 */
    public Set<OWLClass> getTypeClassesBothOntologies(OWLClassExpression con, int type) throws ComplexMappingException {
    	
    	Set<OWLClass> all = new HashSet<OWLClass>();
    	
    	try {
			HashMap<String,OWLClass> classesOnt2 = new HashMap<String, OWLClass>();
			
			//Hashmap with all classes of the second ontology, later used to get the classes if only the url of a class is given
			for(OWLClass cls : this.ontology2.getClassesInSignature(true)) {
				classesOnt2.put(cls.getIRI().toString(), cls);
			}
    					
			NodeSet<OWLClass> subClsSets = getClasses(type, this.reasoner1, con);
			
			//Set for the equivalent classes in the other ontology
			Set<OWLClass> equivalentClassesOtherOntotology = new HashSet<OWLClass>();
			    		
			//iterate through all sub/super classes and check if the reference contains this class, add the class to the classes which will be returned
			for(Node<OWLClass> set : subClsSets) {    					
				for(OWLClass cls : set) {    				
					//iterate through all correspondences of the reference mapping
					for(Correspondence corres : this.correspondences) {    	
						if(cls.getIRI().toString().equals(corres.getSourceEntityUri())) {
							//save the class which is the target class of the correspondence into a set
							equivalentClassesOtherOntotology.add(classesOnt2.get(corres.getTargetEntityUri()));
						}
						else if(cls.getIRI().toString().equals(corres.getTargetEntityUri())) {
							equivalentClassesOtherOntotology.add(classesOnt2.get(corres.getSourceEntityUri()));          						
						}          					
  					}
					all.add(cls); 
				}
			}
    		
			// subClsSets.clear();
			subClsSets = new OWLClassNodeSet();
			
			for(OWLClass cls : equivalentClassesOtherOntotology) { 	
				if(cls != null) {
					subClsSets = getClasses(type, this.reasoner2, cls);
				}			
				
				all.add(cls);
    					
				for(Node<OWLClass> set : subClsSets) {    
					for(OWLClass cls2 : set) {  
						all.add(cls2);
					}    					
				}
				
				// subClsSets.clear();
				subClsSets = new OWLClassNodeSet();
			}          			              
    
    	}catch(UnsupportedOperationException exception) {
    		throw new ComplexMappingException(ComplexMappingException.BAD_METHOD_CALL, 
    				"Unsupported opertation ", exception);
    	}
    	
    	return all;      
    }

}
