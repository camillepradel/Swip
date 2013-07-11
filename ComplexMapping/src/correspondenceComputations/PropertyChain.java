package correspondenceComputations;


import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

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
 * This class computes the complex correspondence of type property chain. An example for this correspondence:
 * Ontology 1: hasJournal (datatype property) domain: Entry range: string
 * Ontology 2: journal (object property) domain: Article range: Journal  name (datatype property) domain: OWL:Thing range: string
 * 
 * First the algorithm collects all pairs of datatype property and object property with a high similarity value.
 * Afterwards it is checked if the domain of the datatype property is a superclass of the domain of the object property or vice versa.
 * If there is also a datatype property in the ontology which contains the object property and the domain of the datatype property is
 * the range of the object property, the range is the same range as the range of the other datatype property and the name is
 * "name" or contained in the name of the other datatype property the three properties are chosen.   
 *
 */
public class PropertyChain{
	
	private static Set<Correspondence> correspondences;
	
	/**
	 * Compute the set of correspondences of the type property chain where a datatype property is equivalent to a object property
	 * and a datatype property.
	 * 
	 * @param filepath1
	 * @param filepath2
	 * @param alignmentPath
	 * @return
	 * @throws ComplexMappingException
	 */
	public static Set<Correspondence> computePropertyChain(Ontology ont1, Ontology ont2, OntologyAlignment ali) throws ComplexMappingException {
		
		 correspondences = new HashSet<Correspondence>();
		
    	//compute the correspondences 
		computeCorrespondences(ont1, ont2, ali);
		computeCorrespondences(ont2, ont1, ali);					
		
		return correspondences;
	}
	
	/**
	 * Auxiliary method to compute the correspondences.
	 * 
	 * @param ontology1
	 * @param ontology2
	 * @param onto1
	 * @param onto2
	 * @param ali
	 * @throws ComplexMappingException
	 */
	private static void computeCorrespondences(Ontology ontology1, Ontology ontology2, OntologyAlignment ali) throws ComplexMappingException {
		//build the reasoner 
		ReasonerTwoOntologies reasonerBoth = new ReasonerTwoOntologies(ontology1.getFilepath(), ontology2.getFilepath(), ali);
		ReasonerOneOntology reasonerOnt1 = new ReasonerOneOntology(ontology1.getFilepath());
		ReasonerOneOntology reasonerOnt2 = new ReasonerOneOntology(ontology2.getFilepath());
		
		String additionalName = "";	
		Set<OWLDataProperty> secondProperties = new HashSet<OWLDataProperty>();
		double similaritySum = 0.0;
		
		//iterate through the datatype properties of the first ontology
		for(OWLDataProperty dataProp1 : ontology1.getDatatypeProperties()) {
			//iterate through the object properties of the second ontology
			for(OWLObjectProperty objectProp2 : ontology2.getObjectProperties()) {			
				//the names of the datatype property and the object property are similar
				Term firstName = ontology1.getTermDatatypeProperties().get(dataProp1);
				Term secondName = ontology2.getTermObjectProperties().get(objectProp2);
				if(ComputationUtility.getSimilarValue(firstName, secondName) > ComputationSetting.getPropertyChainDatatypeObjectName()) {
					try {
						additionalName = firstName.getTokensWithoutSpecialTokensNormalizedOrNot(true)[firstName.getHeadNounIndex()];
						if(additionalName.equals(secondName.getTokensWithoutSpecialTokensNormalizedOrNot(true)[secondName.getHeadNounIndex()])) {
							additionalName = "name";
						}
					} catch(MMatchException e) {
						throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not get headnoun index." + e);
					}
					
					//get the domain of the datatype property out of ontology 1
					for(OWLClassExpression domainOnto1 : ComputationUtility.getDataDomain(reasonerOnt1, ontology1.getOntology(), dataProp1)) {
						//all subclasses contained in both ontologies of the domain
						for(OWLClassExpression subClassesDomainOnto1 : reasonerBoth.getTypeClassesBothOntologies(domainOnto1, Reasoner.DESCENDANT)) {
							//domain of the object property
							for(OWLClassExpression domainOnto2 : ComputationUtility.getObjectDomain(reasonerOnt2, ontology2.getOntology(), objectProp2)) {
								//the domain of the object property is also domain of the datatype property								
								if(subClassesDomainOnto1 != null && domainOnto2 != null &&
										((OWLClass)subClassesDomainOnto1).getIRI().equals(((OWLClass)domainOnto2).getIRI())) {								
									//iterate thorugh all datatype properties of ontology 2
									for(OWLDataProperty dataProp2 : ontology2.getDatatypeProperties()) {
										//range of the object property
										for(OWLClassExpression rangeOnto2 : ComputationUtility.getObjectRange(reasonerOnt2, ontology2.getOntology(), objectProp2)) {			
											//domain of the datatype property
											for(OWLClassExpression domainDataOnt2 : ComputationUtility.getDataDomain(reasonerOnt2, ontology2.getOntology(), dataProp2)) {
												//if the range of the object property is the domain of the datatype property
												if(((OWLClass)domainDataOnt2).getIRI().equals(((OWLClass)rangeOnto2).getIRI()) && !domainDataOnt2.isOWLNothing()) {
													//compare the ranges of both datatype properties, string is compatible to every other range
													for(String rangeDataOnt2 : ComputationUtility.getDataRange(ontology2.getOntology(), dataProp2)) {
														for(String rangeDataOnt1 : ComputationUtility.getDataRange(ontology1.getOntology(), dataProp1)) {
															if(rangeDataOnt2.equals(rangeDataOnt1) || rangeDataOnt1.contains("string") || rangeDataOnt2.contains("string")) {
																//if the second datatype property is name or contains the name of the first datatype property																																
																if(dataProp1.getIRI().getFragment().contains(dataProp2.getIRI().getFragment()) ||
																		dataProp2.getIRI().getFragment().contains(dataProp1.getIRI().getFragment())) {
																	secondProperties.add(dataProp2);															
																}
																else if(dataProp2.getIRI().getFragment().contains(additionalName)){
																	secondProperties.add(dataProp2);
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
				//if only one correspondence is found, create it
				if(secondProperties.size() == 1) {
					if(!ComputationUtility.alreadyInAlignment(dataProp1, ali)) {
						similaritySum = ComputationUtility.getSimilarValue(firstName, secondName);
						//add the new correspondence to the set of correspondences
						correspondences.add(new Correspondence(dataProp1, objectProp2, secondProperties.iterator().next(),
					dataProp1.getIRI().toString()+objectProp2.getIRI().toString()+secondProperties.iterator().next().getIRI().toString(), Correspondence.PROPERTY_CHAIN, similaritySum));
					similaritySum = 0.0;
					}					
				}
				//if more than one correspondences are found, the one with name is usually not right
				if(secondProperties.size() > 1) {
					for(OWLDataProperty prop : secondProperties) {
						if(additionalName != "name")  {
							if(!ComputationUtility.alreadyInAlignment(dataProp1, ali)) {
								similaritySum = ComputationUtility.getSimilarValue(firstName, secondName);
								//add the new correspondence to the set of correspondences
								correspondences.add(new Correspondence(dataProp1, objectProp2, prop,
							dataProp1.getIRI().toString()+objectProp2.getIRI().toString()+ prop.getIRI().toString(), Correspondence.PROPERTY_CHAIN, similaritySum));
							similaritySum = 0.0;
							}							
						}
					}
				}
				secondProperties.clear();			
			}	
		}
	}

}
