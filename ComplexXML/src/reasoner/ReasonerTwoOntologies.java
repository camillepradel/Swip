package reasoner;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ontology.OntologyAlignment;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import exception.ComplexMappingException;
import de.unima.alcomox.mapping.Correspondence;
import exception.ComplexMappingException.ExceptionType;

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
			this.ontology2 = manager.loadOntologyFromPhysicalURI(onto2);
		} catch(OWLOntologyCreationException e) {
			throw new ComplexMappingException(ExceptionType.CREATION_EXCEPTION,
                                "Could not create second ontolgy in ReasonerTwoOntologies.", e);
		}		
		this.correspondences = ali.getAlignment().getCorrespondences();
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
    public Set<OWLClass> getTypeClassesBothOntologies(OWLDescription con, int type) throws ComplexMappingException {
    	
    	Set<OWLClass> all = new HashSet<OWLClass>();
    	
    	try {
			HashMap<String,OWLClass> classesOnt2 = new HashMap<String, OWLClass>();
			
			//Hashmap with all classes of the second ontology, later used to get the classes if only the url of a class is given
			for(OWLClass cls : this.ontology2.getReferencedClasses()) {
				classesOnt2.put(cls.getURI().toString(), cls);
			}

                        //add all sub/super/descendant/ancestor classes
			Set<Set<OWLClass>> subClsSets = getClasses(type, this.reasoner1, con);
                        Set<OWLClass> conClass = new HashSet<OWLClass>();
                        //also add the class itself
                        conClass.add(((OWLClass) con));
                        subClsSets.add(conClass);
			
			//Set for the equivalent classes in the other ontology
			Set<OWLClass> equivalentClassesOtherOntotology = new HashSet<OWLClass>();
			    		
			//iterate through all sub/super classes and check if the reference contains this class, add the class to the classes which will be returned
			for(Set<OWLClass> set : subClsSets) {    					
				for(OWLClass cls : set) {    				
					//iterate through all correspondences of the reference mapping
					for(Correspondence corres : this.correspondences) {
						if(cls.getURI().toString().equals(corres.getSourceEntityUri()) && corres.getRelation().toString().equals("=")) {
							//save the class which is the target class of the correspondence into a set
							equivalentClassesOtherOntotology.add(classesOnt2.get(corres.getTargetEntityUri()));
						}
						
						if(cls.getURI().toString().equals(corres.getTargetEntityUri()) && corres.getRelation().toString().equals("=")) {
							equivalentClassesOtherOntotology.add(classesOnt2.get(corres.getSourceEntityUri()));  								
						}          					
  					}					
					all.add(cls); 						
				}
			}
						
			subClsSets.clear();
			
			for(OWLClass cls : equivalentClassesOtherOntotology) { 
				if(cls != null) {
					subClsSets = getClasses(type, this.reasoner2, cls);
				}			
				
				all.add(cls);
    					
				for(Set<OWLClass> set : subClsSets) {    
					for(OWLClass cls2 : set) {  
						all.add(cls2);
					}    					
				}
				subClsSets.clear();
			}          			              
    
    	}catch(UnsupportedOperationException exception) {
    		throw new ComplexMappingException(ExceptionType.BAD_METHOD_CALL,
    				"Unsupported opertation ", exception);
    	}
    	
    	return all;      
    }

}
