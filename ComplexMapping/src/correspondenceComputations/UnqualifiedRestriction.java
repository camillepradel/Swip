package correspondenceComputations;


import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import reasoner.Reasoner;
import reasoner.ReasonerOneOntology;
import reasoner.ReasonerTwoOntologies;
import utility.Ontology;
import utility.OntologyAlignment;

import complexMapping.ComplexMappingException;

import correspondence.Correspondence;
import de.unima.alcomox.exceptions.MappingException;
import de.unima.ki.mmatch.MMatchException;

/**
 * 
 * @author Dominique Ritze
 * 
 * This class computes the complex correspondences between a class and a object property with restricted range.
 * Example:
 * Ontology 1: Organizator (class)
 * Ontology 2: organised_by (object property) with restricted range Person (not restricted: Person or Organisation)
 *
 * First the names of the classes and the object properties are compared. If two names are similar and the range of the 
 * property is a superclass of the class and the domain and all its subclasses are disjoint with the class, a correspondence is found.
 * 
 */
public class UnqualifiedRestriction{
	
	private static Set<Correspondence> correspondences;
	
	/**
	 * Compute the complex correspondences as described in the class description. 
	 * 
	 * @param filepath1
	 * @param filepath2
	 * @param alignmentPath
	 * @return
	 * @throws MappingException
	 * @throws MMatchException
	 * @throws ComplexMappingException
	 * @throws OWLOntologyCreationException
	 * @throws OWLReasonerException 
	 */
	public static Set<Correspondence> computeUnqualifiedRestriction(Ontology ont1, Ontology ont2, OntologyAlignment ali) throws ComplexMappingException {
		
		correspondences = new HashSet<Correspondence>();
    	
		//compute the correspondences
		computeCorrespondences(ont1, ont2, ali);
		computeCorrespondences(ont2, ont1, ali);
				
		return correspondences;
	}
	
	/**
	 * Auxiliary method to compute the correspondence.
	 * 
	 * @param conceptOntology
	 * @param propertyOntology
	 * @param ali
	 * @param filepath1
	 * @param filepath2
	 * @return
	 * @throws ComplexMappingException
	 */
	private static void computeCorrespondences(Ontology conceptOntology, Ontology propertyOntology, OntologyAlignment ali) throws ComplexMappingException {
	
		ReasonerTwoOntologies reasoner = new ReasonerTwoOntologies(conceptOntology.getFilepath(), propertyOntology.getFilepath(), ali);
		ReasonerOneOntology one = new ReasonerOneOntology(propertyOntology.getFilepath());
		double similaritySum = 0.0;
    	
		//iterate through the concepts of the ontology where the matched concept should be
		for(OWLClass cls : conceptOntology.getClasses()) {	
			//iterate through all properties of the ontology where the matched property should be
			for(OWLObjectProperty prop : propertyOntology.getObjectProperties()) {		
				//compare the name of the concept and of the property
				if(cls.getIRI().getFragment() != null && ComputationUtility.getSimilarValue(conceptOntology.getTermClasses().get(cls),
						propertyOntology.getTermObjectProperties().get(prop)) > ComputationSetting.getUnqualifiedRestrictionClassSimilarToPropertyName()) {
					//get all superclasses of the current concept, also out of the second ontology
					Set<OWLClass> superClassesConcept = reasoner.getTypeClassesBothOntologies(cls, Reasoner.ANCESTOR);
					for(OWLClass con : superClassesConcept) {
						//the name of the concept is similar to the name of the property and the property is an objectproperty
						//the range of the property should be a superclass of the class 
						for(OWLClassExpression range : ComputationUtility.getObjectRange(one, propertyOntology.getOntology(), prop)) {
							if(range != null && con != null && ((OWLClass)range).getIRI().equals(con.getIRI())) {
								//the class and the range are not (namely) the same
								if(!(cls.getIRI().getFragment().equals(((OWLClass)range).getIRI().getFragment()))) {
									for(OWLClass domain : ComputationUtility.getObjectDomain(one, propertyOntology.getOntology(), prop)) {
										for(OWLClassExpression disjoint : ComputationUtility.getDisjointClasses(one, propertyOntology.getOntology(), (OWLClass)domain)) {										
											//	((OWLClass)domain).getDisjointClasses(propertyOntology)) {
											//domain of the property is not a superclass of the class
											if(((OWLClass)disjoint).getIRI().equals(con.getIRI())) {												
												try {
													if(!conceptOntology.getTermClasses().get(cls).getTokens()
															[conceptOntology.getTermClasses().get(cls).getHeadNounIndex()].
															equals(((OWLClass)range).getIRI().getFragment().toLowerCase())) {	
														if(!ComputationUtility.alreadyInAlignment(cls, ali)) {
															similaritySum = ComputationUtility.getSimilarValue(conceptOntology.getTermClasses().get(cls),
																	propertyOntology.getTermObjectProperties().get(prop));
															correspondences.add(new Correspondence(cls, prop, con, 
																	cls.getIRI().toString()+prop.getIRI().toString()+con.getIRI().toString(), Correspondence.UNQUALIFIED_RESTRICTION, similaritySum));
															similaritySum = 0.0;
														}																																															
													}
												} catch(MMatchException e) {
													throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not get headnoun index", e);
												}
																									
											}													
										}
									}
								}										
							}
						}																		
					}					
				}				
			}			
		}
	}

}
