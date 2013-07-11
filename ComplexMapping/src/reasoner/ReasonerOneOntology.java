package reasoner;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import complexMapping.ComplexMappingException;

/**
 * 
 * @author Dominique Ritze
 * 
 * This class contains the methods which are used to get additional information about an ontology 
 * which can be deduced. All results are based on the results of the reasoner Pellet.
 * Only information about one ontology are collected and no information about other ontologies like
 * the class ReasonerTwoOntologies does. 
 *
 */
public class ReasonerOneOntology extends Reasoner{	
	
	
	private OWLReasoner reasoner;
	
	public ReasonerOneOntology(URI onto) throws ComplexMappingException {
		this.reasoner = super.createReasoner(onto);
		
	}
	
	/**
	 * Get all typed(super, sub, ancestor, descendant)classes of the given OWLClass.
	 * 
	 * @param con
	 * @param ont
	 * @param type
	 * @return
	 * @throws ComplexMappingException
	 */
	public Set<OWLClass> getTypeClasses(OWLClassExpression con, int type) throws ComplexMappingException {
		
		Set<OWLClass> all = new HashSet<OWLClass>();
		
		try {				
					
			NodeSet<OWLClass> subClsSets = super.getClasses(type, this.reasoner, con); 
			    		
			//iterate through all sub/super classes and check if the reference contains this class, add the class to the classes which will be returned
			for(Node<OWLClass> set : subClsSets) {    					
				for(OWLClass cls : set) {     
					all.add(cls);
				}					 
			}
		}catch(UnsupportedOperationException exception) {
    		throw new ComplexMappingException(ComplexMappingException.BAD_METHOD_CALL, 
    				"Unsupported opertation ", exception);
    	}    	
    	return all;   
	}	

}
