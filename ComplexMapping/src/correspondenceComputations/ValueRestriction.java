package correspondenceComputations;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;

import reasoner.Reasoner;
import reasoner.ReasonerOneOntology;
import reasoner.ReasonerTwoOntologies;
import utility.Ontology;
import utility.OntologyAlignment;

import complexMapping.ComplexMappingException;

import correspondence.Correspondence;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.descriptions.Term;

/**
 * 
 * @author Dominique Ritze
 * 
 * Class to compute complex correspondences where a class is equivalent to a datatype property and a special value.
 * 
 * Example:
 * ontology 1: Early-Registered_Participant (class)
 * ontology 2: earlyRegistered (datatype property with range boolean, domain participant) true
 * 
 * First the algorithm tries to find similar classes or parts of classes,
 * in this case the class Participant and the part Participant of Early-Registered_Participant will be found.
 * The other part of the class, here Early-Registered, is compared with the datatype properties of the other ontology.
 * If a high value is detected the algorithm proofs if the domain of the datatype property is a superclass of the class found before.
 * Afterwards the range of the datatype property is checked and if it is boolean and exactly two correspondences are found 
 * the class with the higher of the last comparison will be assigned to the datatype property and the value true, the other one
 * to the property and the value false. Other correspondences especially with other ranges are nor implemented yet.
 * 
 *
 */
public class ValueRestriction {
	
	private static Set<Correspondence> correspondences;
	
	/**
	 * Compute the complex correspondences of two given ontologies how it is recorded in the class description.
	 * 
	 * @param filepath1
	 * @param filepath2
	 * @param alignmentPath
	 * @return
	 * @throws ComplexMappingException
	 */
	public static Set<Correspondence> valueRestriction(Ontology ont1, Ontology ont2, OntologyAlignment ali) throws ComplexMappingException {
		
		correspondences = new HashSet<Correspondence>();
				
		HashMap<OWLClass, Term> bestConceptOnt1 = computeNewClasses(ont1, ont2);
		HashMap<OWLClass, Term> bestConceptOnt2 = computeNewClasses(ont2, ont1);
		
		//call the real computation method
		computeCorrespondences(ont1, ont2, bestConceptOnt1, ont2.getDatatypeProperties(), ali); 	
		computeCorrespondences(ont2, ont1, bestConceptOnt2, ont1.getDatatypeProperties(), ali);  
			
		return correspondences;
	}
	
	/**
	 * Auxiliary method to compute the range restriction similarity on a datatype property.
	 * 
	 * @param ont1
	 * @param ont2
	 * @param newConcepts
	 * @param properties
	 * @param ali
	 * @param filepath1
	 * @param filepath2
	 * @return
	 * @throws ComplexMappingException
	 */
	private static void computeCorrespondences(Ontology ont1, Ontology ont2, HashMap<OWLClass,Term> newConcepts, Set<OWLDataProperty> properties, OntologyAlignment ali) throws ComplexMappingException {
		//HashMap<Double, OWLClass> bestValues = new HashMap<Double, OWLClass>();
		//construct the reasoners which are used to get things like super classes
		//this reasoner can be used to get sub or super classes of a class in both ontologies, the ontologies are connected by the alignment		
		ReasonerTwoOntologies bothOntologiesReasoner = new ReasonerTwoOntologies(ont1.getFilepath(), ont2.getFilepath(), ali);
		//reasoner for just one ontology
		ReasonerOneOntology oneOntologyReasoner = new ReasonerOneOntology(ont2.getFilepath());
		
		//int numberOfCorrespondences = 0;	
		//iterate through the properties and the concepts
		for(OWLDataProperty p : properties) {			
			for(OWLClass c : newConcepts.keySet()) {	
				//compare the names 
				if(p.getIRI().getFragment() != null && ComputationUtility.getSimilarValue(newConcepts.get(c), ont2.getTermDatatypeProperties().get(p)) > ComputationSetting.getValueRestrictionPartOfClassSimilarToPropertyName()) {
					//the domain of the property should be a superclass of the current concept
					Set<OWLClass> superClasses = bothOntologiesReasoner.getTypeClassesBothOntologies(c, Reasoner.ANCESTOR);
					for(OWLClass con : superClasses) {
						for(OWLClass domain : ComputationUtility.getDataDomain(oneOntologyReasoner, ont2.getOntology(), p)) {					
							if((OWLClass)domain != null && con != null && ((OWLClass)domain).getIRI().equals(con.getIRI()) && 
									!((OWLClass)domain).getIRI().getFragment().contains("Thing")) { 										
								//add the correspondence with its according similarity value to a map
								//bestValues.put((Computation.getSimilarValue(newConcepts.get(c), ont2.getTermDatatypeProperties().get(p))), c);
								//count the correspondences which are found, this will be used later
								//numberOfCorrespondences++;									
								for(String range : ComputationUtility.getDataRange(ont2.getOntology(), p)) {
								//	System.out.println(range);
									//only boolean implemented yet
									if(range.contains("boolean")) {
										Term ahead;
										try {
											ahead = new Term(newConcepts.get(c).getTokensWithoutSpecialTokens()[0], ont1.getDelimiter());
										} catch(MMatchException e) {
											throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, 
													"Could not compute the first part of the name (RangeRestriction)", e);
										}
										
										if(ComputationUtility.getSimilarValue(ahead, ont2.getTermDatatypeProperties().get(p)) > 
										ComputationSetting.getValueRestrictionFirstPartClassToPropertyName()) {
										//	System.out.println("concept: " + c.getURI() + " prop: " + p.getURI());
											if(!ComputationUtility.alreadyInAlignment(c, ali)) {
												correspondences.add(new Correspondence(c, p, "true", 
														c.getIRI().toString()+p.getIRI().toString()+"true", Correspondence.VALUE_RESTRICTION));
											}											
										}
										else {
											if(!ComputationUtility.alreadyInAlignment(c, ali)) {
												correspondences.add(new Correspondence(c, p, "false", 
														c.getIRI().toString()+p.getIRI().toString()+"false", Correspondence.VALUE_RESTRICTION));
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
	
	/**
	 * Get the integer key of the greatest double value.
	 * 
	 * @param values
	 * @return
	 */
	private static int getMaximumValue(HashMap<Integer, Double> values) {
		double currentBest = 0.0; 
		int keyOfBestValue = -1;
		for(int key : values.keySet()) {
			if(values.get(key) > currentBest ) {
				currentBest = values.get(key);
				keyOfBestValue = key;
			}
		}
		return keyOfBestValue;
	}
	
	/**
	 * Delete the part of the class name of the first ontology which has
	 * a partner (class with a high similarity) in the second ontology.	 
	 * 
	 * @param ont1
	 * @param ont2
	 * @return A map with the new classes and the corresponding terms.
	 * @throws ComplexMappingException 
	 */
	private static HashMap<OWLClass, Term> computeNewClasses(Ontology ont1, Ontology ont2) throws ComplexMappingException {			
		HashMap<OWLClass, Term> bestConcepts = new HashMap<OWLClass, Term>();
		HashMap<Integer, Double> compareValues = new HashMap<Integer, Double>();
		Double[] maximumValues;
		
		//iterate through the classes of both ontologies
		for(OWLClass cls1 : ont1.getClasses()) {
			maximumValues = new Double[ont1.getTermClasses().get(cls1).getTokensWithoutSpecialTokens().length];
		 	for(int i=0; i<ont1.getTermClasses().get(cls1).getTokensWithoutSpecialTokens().length; i++) {
				maximumValues[i] = 0.0;
			}
			for(OWLClass cls2 : ont2.getClasses()) {				
				//delete the part of the class name which has a high similarity value with a class of the other ontology				
				if(ont1.getTermClasses().get(cls1).getTokensWithoutSpecialTokens().length > 1) {
					for(int i=0; i<ont1.getTermClasses().get(cls1).getTokensWithoutSpecialTokens().length; i++) {
						double value;
						try {
							value = ComputationUtility.getSimilarValue(new Term(ont1.getTermClasses().get(cls1).getTokensWithoutSpecialTokens()[i],
									ont1.getDelimiter()), ont2.getTermClasses().get(cls2));
						} catch(MMatchException e) {
							throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not compute the terms in compute" +
									"new classes (RangeRestriction)" + e);
						}
						
						if(value > maximumValues[i]) {
							maximumValues[i] = value;
						}												
					}
				}
			}
			for(int i=0; i<ont1.getTermClasses().get(cls1).getTokensWithoutSpecialTokens().length; i++) {
				compareValues.put(i,maximumValues[i]);
			}
		
			//assign the best value	and check if its bigger than the given border	 				
			if(getMaximumValue(compareValues)!= -1 && compareValues.get(getMaximumValue(compareValues))> ComputationSetting.getValueRestrictionClassSimilarToRangeName()) {							
				try {
					//add the reduced term where the best matching partner is deleted 
					bestConcepts.put(cls1, new Term(ComputationUtility.getReducedName(ont1.getTermClasses().get(cls1), getMaximumValue(compareValues)), ont1.getDelimiter()));								
						compareValues.clear();
				} catch(MMatchException e) {
					throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not add a reduced name to a set (RangeRestriction).", e);
				}
				
			}
		}
		return bestConcepts;
	}
	
			
} 
