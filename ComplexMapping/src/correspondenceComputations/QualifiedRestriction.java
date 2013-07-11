package correspondenceComputations;


import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import reasoner.Reasoner;
import reasoner.ReasonerOneOntology;
import reasoner.ReasonerTwoOntologies;
import utility.Ontology;
import utility.OntologyAlignment;

import complexMapping.ComplexMappingException;

import correspondence.Correspondence;
import de.unima.ki.mmatch.descriptions.Term;

/**
 * 
 * @author Dominique Ritze
 *  
 * This class contains the method to compute a complex correspondence between a class and a property with its range.
 * Example:
 * Ontology 1: Accepted_Paper (class)
 * Ontology 2: hasDecision (object property) with the restricted range Acceptance
 * 
 * The algorithm searches similar classes especially a high similarity value between the head noun of a class name and another class name.
 * All object properties are examined and checked if the range of an object property is a superclass of the class which has a high value
 * with a concept out of the same ontology. Also the domain of the property is considered and has to be a superclass of the class of
 * the ontology which does not contain the property. Finally name of the property and the name of its range should be similar.
 *
 */
public class QualifiedRestriction{
	
	private static Set<Correspondence> correspondences;
	
	/**
	 * Compute the correspondences which consists of an object property out of one ontology and an
	 * object property plus a restricted range out of the other ontology.
	 * 
	 * @param filepath1
	 * @param filepath2
	 * @param alignmentPath
	 * @return
	 * @throws ComplexMappingException
	 */
	public static Set<Correspondence> computeQualifiedRestriction(Ontology ont1, Ontology ont2, OntologyAlignment ali) throws ComplexMappingException {
		correspondences = new HashSet<Correspondence>();	
    	
    	//create a map for each ontology which contains the OWLClasses of the ontology and its term
    	HashMap<OWLClass, Term> classesOnt1 = ont1.getTermClasses(); 
    	HashMap<OWLClass, Term> classesOnt2 = ont2.getTermClasses();
    	
    	//maps with the OWLClasses and reduced terms, terms without the headnoun
    	HashMap<OWLClass, Term> reducedClassesOnt1 = ComputationUtility.deleteHeadnoun(classesOnt1.keySet(), ont1.getDelimiter());
    	HashMap<OWLClass, Term> reducedClassesOnt2 = ComputationUtility.deleteHeadnoun(classesOnt2.keySet(), ont2.getDelimiter());
    	
    	computeCorrespondence(ont1, ont2, reducedClassesOnt1, reducedClassesOnt2, ont1.getFilepath(), ont2.getFilepath(), ali);
    	computeCorrespondence(ont2, ont1, reducedClassesOnt2, reducedClassesOnt1, ont2.getFilepath(), ont1.getFilepath(), ali);		
		
		return correspondences;    	
	}	
	
	/**
	 * Method to avoid code duplication, just for the computation.
	 * 
	 * @param ontology1
	 * @param ontology2
	 * @param reducedClassesOnt1
	 * @param reducedClassesOnt2
	 * @param reasonerOnt1
	 * @param reasonerOnt2
	 * @param ali
	 * @throws ComplexMappingException
	 */
	private static void computeCorrespondence(Ontology ontology1, Ontology ontology2, HashMap<OWLClass, Term> reducedClassesOnt1, HashMap<OWLClass, Term> reducedClassesOnt2,
		URI onto1, URI onto2, OntologyAlignment ali) throws ComplexMappingException {
		ReasonerOneOntology reasonerOnlyOntology1 = new ReasonerOneOntology(onto1);
    	ReasonerTwoOntologies reasonerOntologies = new ReasonerTwoOntologies(onto2, onto1, ali);
    	double similaritySum = 0.0;
		
		//iterate through all concepts
		for(OWLClass class1 : reducedClassesOnt1.keySet()) {			
			for(OWLClass class2 : reducedClassesOnt2.keySet()) {
				//not the headnoun of the concept should match the range of the property		
				if(class1.getIRI().getFragment() != null && class2.getIRI().getFragment()!= null &&
						!class1.getIRI().getFragment().equals(class2.getIRI().getFragment())) {
					//compute the similarity value of the two concepts 
					if(ComputationUtility.getSimilarValue(reducedClassesOnt1.get(class1), reducedClassesOnt2.get(class2)) > ComputationSetting.getQualifiedRestrictionClassRangeName()) {
						//if the names of the classes are equal and they should not occur as a result
						//	&& !class1.getURI().getFragment().toLowerCase().equals(class2.getURI().getFragment().toLowerCase())) {
						//the name of the concept should contain of more than one word
						//if(classesOnt2.get(class2).getNumberOfTokens() > 1) {
						//iterate through the properties of the second ontology
						for(OWLObjectProperty property : ontology1.getObjectProperties()) {							 
							//check if there exist some superclass of the first concept which is the range of the current property
							//get the super classes of the current concept of ontology 1
							Set<OWLClass> superClassesConcept1 = reasonerOnlyOntology1.getTypeClasses(class1, Reasoner.ANCESTOR);							
							for(OWLClass con : superClassesConcept1) {	
								//compare the name of the superclass and the name of the property 
								if(con.getIRI().getFragment() != null && property.getIRI().getFragment() != null && 
										ComputationUtility.getSimilarValue(ontology1.getTermClasses().get(con), 
									ontology1.getTermObjectProperties().get(property)) > ComputationSetting.qualifiedRestrictionPropertySuperclass) {
								//iterate through all ranges of the property and check if some of them equals a superclass					
								for(OWLClassExpression range : ComputationUtility.getObjectRange(reasonerOnlyOntology1, ontology1.getOntology(), property)) {
									if(range != null && con != null && ((OWLClass)range).getIRI().equals(con.getIRI())) {
										Set<OWLClass> superClassesConcept2 = reasonerOntologies.getTypeClassesBothOntologies(class2, Reasoner.ANCESTOR);
										for(OWLClass con2 : superClassesConcept2) {
											for(OWLClass domain : ComputationUtility.getObjectDomain(reasonerOnlyOntology1, ontology1.getOntology(), property)) {
												//the domain of the property should be a superclass of the current class
												if(domain != null && con2 != null && ((OWLClass)domain).getIRI().equals(con2.getIRI())){													
													if(!ComputationUtility.alreadyInAlignment(class2, ali)) {														
														similaritySum = ComputationUtility.getSimilarValue(reducedClassesOnt1.get(class1), reducedClassesOnt2.get(class2)) +
															ComputationUtility.getSimilarValue(ontology1.getTermClasses().get(con), 
																ontology1.getTermObjectProperties().get(property));
														
														
														//add the correspondence
														correspondences.add(new Correspondence(class2, property, class1,
																class2.getIRI().toString()+property.getIRI().toString()+class1.getIRI().toString(), Correspondence.QUALIFIED_RESTRICTION, similaritySum));
														similaritySum = 0.0;
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
				//	}	
				} 
			}
		}
	}

}
