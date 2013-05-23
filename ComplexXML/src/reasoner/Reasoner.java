package reasoner;

import java.net.URI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

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

			// create ontology manager
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

			// load both ontologies
			// OWLOntology ont1 = manager.loadOntologyFromPhysicalURI(ont);
			OWLOntology ont1 = manager.loadOntologyFromOntologyDocument(IRI
					.create(ont));

			// build the pellet reasoner
			OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
			// OWLReasoner reasoner = reasonerFactory.createReasoner(manager);
			OWLReasoner reasoner = reasonerFactory.createReasoner(ont1);

			// load the ontology into the reasoner
			// Set<OWLOntology> importsClosure =
			// manager.getImportsClosure(ont1);
			// reasoner.loadOntologies(importsClosure);
			// reasoner.classify();

			return reasoner;

		} catch (UnsupportedOperationException exception) {
			throw new ComplexMappingException(ExceptionType.BAD_METHOD_CALL,
					"Unsupported opertation ", exception);
		}
		// catch(OWLReasonerException ex) {
		// throw new ComplexMappingException(ExceptionType.REASONER_EXCEPTION,
		// "An error occured while reasoning the ontologies.", ex);
		// }
		catch (OWLOntologyCreationException e) {
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
	public static NodeSet<OWLClass> getClasses(int type, OWLReasoner reasoner,
			OWLClassExpression ontologyClass) throws ComplexMappingException {
		NodeSet<OWLClass> tmp = null;
		// new HashSet<Set<OWLClass>>();
		// try {
		switch (type) {
		case 1:
			tmp = reasoner.getSubClasses(ontologyClass, true);
			break;
		case 2:
			tmp = reasoner.getSubClasses(ontologyClass, false);
			break;
		case 3:
			tmp = reasoner.getSuperClasses(ontologyClass, true);
			break;
		case 4:
			tmp = reasoner.getSuperClasses(ontologyClass, false);
			break;
		default:
			throw new ComplexMappingException(ExceptionType.BAD_PARAMETER,
					"Wrong type: " + type);
		}
		// } catch(OWLReasonerException e) {
		// throw new ComplexMappingException(ExceptionType.REASONER_EXCEPTION,
		// "Could not get typed classes.", e);
		// }

		return tmp;
	}

}
