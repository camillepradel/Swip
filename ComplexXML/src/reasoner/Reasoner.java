package reasoner;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.mindswap.pellet.owlapi.PelletReasonerFactory;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;

/**
 * 
 * @author Dominique Ritze
 * 
 * A class which contains some methods used in the classes ReasonerOneOntology and ReasonerTwoOntologies.
 * One method create a reasoner: load the ontology, build a pellet reasoner and load the ontology into the reasoner.
 * Another method computes all things like sub-or superclasses of a given classs.
 *
 */
public class Reasoner {
	
	public static final int SUB_CLASS = 1;
	public static final int DESCENDANT = 2;
	public static final int SUPER_CLASS = 3;
	public static final int ANCESTOR = 4;  
	
	/**
	 * Create a pellet reasoner and load the ontology into the reasoner.
	 * 
	 * @param ont
	 * @return
	 * @throws ComplexMappingException
	 */
	public OWLReasoner createReasoner(URI ont) throws ComplexMappingException {
		
		try {	     		
	      	  
    		//create ontology manager 
    		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	   
    		//load both ontologies  
    		OWLOntology ont1 = manager.loadOntologyFromPhysicalURI(ont);
	        
    		//build the pellet reasoner
    		OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();    		
    		OWLReasoner reasoner = reasonerFactory.createReasoner(manager);    		    		
    				
	        //load the ontology into the reasoner      
			Set<OWLOntology> importsClosure = manager.getImportsClosure(ont1);
			reasoner.loadOntologies(importsClosure);
			reasoner.classify();
			
			return reasoner;
			
		}catch(UnsupportedOperationException exception) {
    		throw new ComplexMappingException(ExceptionType.BAD_METHOD_CALL,
    				"Unsupported opertation ", exception);
    	}
    	catch(OWLReasonerException ex) {    		
    		throw new ComplexMappingException(ExceptionType.REASONER_EXCEPTION,
    				"An error occured while reasoning the ontologies.", ex);
    	}
		catch(OWLOntologyCreationException e) {			
			throw new ComplexMappingException(ExceptionType.CREATION_EXCEPTION,
   				"Could not create the ontologies.", e);
    	}		
	}

     /**
     * Get all sub/super/ancestor/descendant classes of a given class.
     * 
     * @param type
     * @param reasoner
     * @param ontologyClass
     * @return
     * @throws ComplexMappingException
     */
    public static Set<Set<OWLClass>> getClasses(int type, OWLReasoner reasoner, OWLDescription ontologyClass) throws ComplexMappingException {
  	  Set<Set<OWLClass>> tmp = new HashSet<Set<OWLClass>>();
  	  try {
  		 switch(type) {
			case 1:
				tmp = reasoner.getSubClasses(ontologyClass);
				break;
			case 2:
				tmp = reasoner.getDescendantClasses(ontologyClass);
				break;
			case 3:
				tmp = reasoner.getSuperClasses(ontologyClass);
				break;
			case 4:
				tmp = reasoner.getAncestorClasses(ontologyClass); 
				break;	
			default:
				throw new ComplexMappingException(ExceptionType.BAD_PARAMETER,
						"Wrong type: " + type);
  		 }
  	  } catch(OWLReasonerException e) {
  		  throw new ComplexMappingException(ExceptionType.REASONER_EXCEPTION, "Could not get typed classes.", e);
  	  }
  	 
  	  return tmp;  	  
    }

}
