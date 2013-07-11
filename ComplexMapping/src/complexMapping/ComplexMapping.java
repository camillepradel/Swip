package complexMapping;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import reasoner.Reasoner;
import reasoner.ReasonerOneOntology;
import utility.Ontology;
import utility.OntologyAlignment;
import correspondence.Correspondence;
import correspondence.CorrespondenceWriter;
import correspondenceComputations.ComputationSetting;
import correspondenceComputations.ComputationUtility;
import correspondenceComputations.PropertyChain;
import correspondenceComputations.QualifiedRestriction;
import correspondenceComputations.UnqualifiedRestriction;
import correspondenceComputations.ValueRestriction;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.Setting;
import de.unima.ki.mmatch.descriptions.Term;

/**
 * 
 * @author Dominique Ritze
 * 
 * Create the complex mapping, print the correspondences and write the alignment. 
 *
 */
public class ComplexMapping {
	
	String filepath1;
	String filepath2;
	String alignmentPath;
	String filePathForWriting;
	Set<Correspondence> allCorrespondences = new HashSet<Correspondence>();
	Ontology ont1; 
	Ontology ont2; 	
	
	/**
	 * Main method for creating an alignment containing complex correspondences.
	 * The first two parameters (required) are the paths to both ontologies regardless of the order. 
	 * Third parameter (required) is the path to the conventional alignment.
	 * Last parameter (optional) is the path to the file in which the alignment should be written.  
	 * 
	 * @param args
	 * @throws ComplexMappingException
	 */
	public static void main(String args[]) throws ComplexMappingException {
		ComplexMapping mapping;
		//alignment should be written
		if(args.length == 4) {
			mapping = new ComplexMapping(args[0], args[1], args[2], args[3]);	
			mapping.createAlignment();
			mapping.writeAlignment(mapping.filePathForWriting);			
		}
		//no alignment should be written into a file
		if(args.length == 3) {
			mapping = new ComplexMapping(args[0], args[1], args[2]);
			mapping.createAlignment();
		}
		if(args.length != 4 && args.length != 3) {
			throw new ComplexMappingException(ComplexMappingException.BAD_METHOD_CALL, "Not the right number of parameters, only 3 or 4 are permitted.");
		}
	}
	
	/**
	 * Constructor (if alignment should be written into a file) to get all important files and to edit the settings.
	 * 
	 * @param filepath1
	 * @param filepath2
	 * @param alignmentPath
	 * @param writePath
	 * @throws ComplexMappingException
	 */
	public ComplexMapping(String filepath1, String filepath2, String alignmentPath, String writePath) throws ComplexMappingException {
		try {
			//load settings required for the matcher
			Setting.load();
		} catch(MMatchException e) {
			throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not load setting file MMatch.", e);
		}
		//blank, hyphen, camel case and underscore are delimiter
		ComputationSetting.setDelimiterPercent(0.0);
		//a stemmer is used while comparing two names and in a variable the percentage of the compare values of the stemmed names are saved
		//and can be set to a value
		Setting.setStemWeight(0.6);
		//save all given paths
		this.filepath1 = filepath1;
		this.filepath2 = filepath2;
		this.alignmentPath = alignmentPath;
		this.filePathForWriting = writePath;
			
	}
	
	/**
	 * Constructor to get all important files and to edit the settings.
	 * 
	 * @param filepath1
	 * @param filepath2
	 * @param alignmentPath
	 * @throws ComplexMappingException
	 */
	public ComplexMapping(String filepath1, String filepath2, String alignmentPath) throws ComplexMappingException {
		try {
			Setting.load();
		} catch(MMatchException e) {
			throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not load setting file MMatch.", e);
		}
		
		ComputationSetting.setDelimiterPercent(0.0);
		Setting.setStemWeight(0.6);
		this.filepath1 = filepath1;
		this.filepath2 = filepath2;
		this.alignmentPath = alignmentPath;
			
	}
	
	/**
	 * Print the correspondences and write the alignment (if desired).
	 * 
	 * @throws ComplexMappingException
	 */
	public void createAlignment() throws ComplexMappingException {
		
		ont1 = new Ontology(this.filepath1);
		ont2 = new Ontology(this.filepath2);
		OntologyAlignment ali = new OntologyAlignment(this.alignmentPath);
		
		System.out.println(filepath1 + "-" + filepath2 + ":");
		
		System.out.println("Qualified Restriction:");
		Set<Correspondence> similarRange = QualifiedRestriction.computeQualifiedRestriction(ont1, ont2, ali);
		for(Correspondence cor : similarRange) {
			if(!containsMoreGeneralCorrespondence(cor, similarRange) && highestSimilarity(cor, similarRange)) {
				System.out.println(cor.toShortString()+ "\n");
				allCorrespondences.add(cor);
			}			
		}
		System.out.println("Unqualified Restriction:");
		Set<Correspondence> similarProperty = UnqualifiedRestriction.computeUnqualifiedRestriction(ont1, ont2, ali);
		for(Correspondence cor : similarProperty) {
			if(!containsMoreGeneralCorrespondence(cor, similarProperty) && highestSimilarity(cor, similarProperty)) {
				System.out.println(cor.toShortString()+ "\n");
				allCorrespondences.add(cor);
			}			
		} 
		System.out.println("Value Restriction:");
		for(Correspondence cor : ValueRestriction.valueRestriction(ont1, ont2, ali)) {
			System.out.println(cor.toShortString()+ "\n");
			allCorrespondences.add(cor);
		}
		System.out.println("Property Chain:");
		Set<Correspondence> propertyChainCorres = PropertyChain.computePropertyChain(ont1, ont2, ali);
		for(Correspondence cor : propertyChainCorres) {
			if(highestSimilarity(cor, propertyChainCorres)) {
				System.out.println(cor.toShortString()+ "\n");
				allCorrespondences.add(cor);
			}			
		}		
	}
	
	/**
	 * Write the alignment into a file.
	 * 
	 * @param writePath
	 * @throws ComplexMappingException
	 */
	public void writeAlignment(String writePath) throws ComplexMappingException {
		CorrespondenceWriter write = new CorrespondenceWriter(allCorrespondences);
		write.writeFileWithCorrespondences(writePath);
	}
	
	/**
	 * If two complex correspondences exist and one is more general than the other, only the more general one should be in the alignment.
	 * For example imagine the two following correspondences:
	 * Forall x (ReviewedPaper(x) <-> Exists y (reviewOfPaper(x,y) and Paper(y))) 
	 * Forall x (ReviewedPaper(x) <-> Exists y (reviewOfPaper(x,y) and SubmittedPaper(y)))
	 * Now if SubmittedPaper is a subclass of Paper, the second correspondence is deleted because the first one is more general.
	 * 
	 * @param cor
	 * @param correspondences
	 * @return True if a more general correspondence is contained in the set of correspondences and the current correspondence can be deleted.
	 * @throws ComplexMappingException
	 */
	private boolean containsMoreGeneralCorrespondence(Correspondence cor, Set<Correspondence> correspondences) throws ComplexMappingException {		
		//reasoner for each ontology is required
		ReasonerOneOntology reasoner1 = new ReasonerOneOntology(ont1.getFilepath());
		ReasonerOneOntology reasoner2 = new ReasonerOneOntology(ont2.getFilepath());
		for(Correspondence corres2 : correspondences) {
			//only for Qualified and Unqualified Restriction because all other types does not contain such examples
			if(cor.getType() == Correspondence.QUALIFIED_RESTRICTION  || cor.getType() == Correspondence.UNQUALIFIED_RESTRICTION) {
				//two entities are the same
				if(cor.getClassOnt1() == corres2.getClassOnt1() && cor.getClassOnt2() != corres2.getClassOnt2()) {
					//check if the class is in the first ontology 
					if(ont1.getClasses().contains(cor.getClassOnt1())) {
						//regard the superclasses
						for(OWLClass cls : reasoner2.getTypeClasses(cor.getClassOnt2(), Reasoner.SUPER_CLASS)) {
							if(cls.getIRI().equals(corres2.getClassOnt2().getIRI())) {
								return true;
							}
						}
					}
					else {
						for(OWLClass cls : reasoner1.getTypeClasses(cor.getClassOnt2(), Reasoner.SUPER_CLASS)) {
							if(cls.getIRI().equals(corres2.getClassOnt2().getIRI())) {
								return true;
							}
						}
					}
					
				}
			}			
		}
		return false;
	}
	
	/**
	 * If an entity has several complex descriptions (is contained in more than one correspondence on the left side) and one of them
	 * has a higher sum of similarity values, this one is chosen and the other ones are deleted.
	 * 
	 * @param cor
	 * @param correspondences
	 * @return True if the current correspondence has the highest sum of similarity values and should be contained in the alignment.
	 * False if the current correspondence should be deleted.
	 * @throws ComplexMappingException
	 */
	private boolean highestSimilarity(Correspondence cor, Set<Correspondence> correspondences) throws ComplexMappingException {
		double maximum = 0.0;
		double sum = 0.0;
		Ontology firstOntology, secondOntology;
		
		//only Qualified and Unqualified Restriction
		if(cor.getType() == Correspondence.UNQUALIFIED_RESTRICTION || cor.getType() == Correspondence.QUALIFIED_RESTRICTION) {
			//determine to which ontology the current correspondence belongs
			if(ont1.getClasses().contains(cor.getClassOnt2())) {
				firstOntology = ont2;
				secondOntology = ont1;
			}
			else {
				firstOntology = ont1;
				secondOntology = ont2;
			}
			
			try {
				for(Correspondence corres2 : correspondences) {
					//which parts are the same and which are different
					if(cor.getClassOnt1() == corres2.getClassOnt1() && cor.getClassOnt2() != corres2.getClassOnt2() ||
							cor.getClassOnt1() == corres2.getClassOnt1() && cor.getObjectPropertyOnt2() != corres2.getObjectPropertyOnt2()) {
						//same correspondence
						if(cor.getIdentifier().equals(corres2.getIdentifier())) {
							continue;
						}								
						//compute the sum and add also similarity values of the whole entity names if they have not been taken into account
						//while trying to find correspondences
						sum = corres2.getSimilaritySum() + ComputationUtility.getSimilarValue(new Term(corres2.getClassOnt2().getIRI().getFragment(), secondOntology.getDelimiter()), 
								new Term(cor.getClassOnt1().getIRI().getFragment(), firstOntology.getDelimiter()));
						//sum is higher than all regarded ones before
						if(sum > maximum) {
							maximum = sum;
						}
					}
				}
			
				//check if the maximum belongs to the current correspondence
				if(cor.getSimilaritySum() + ComputationUtility.getSimilarValue(new Term(cor.getClassOnt2().getIRI().getFragment(), secondOntology.getDelimiter()), 
						new Term(cor.getClassOnt1().getIRI().getFragment(), firstOntology.getDelimiter())) > maximum) {
					return true;
				}
			
			} catch(MMatchException e) {
				throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION,"Cannot compare classes in complex mapping.",e);
			}	
		}
		//for type property chain
		if(cor.getType() == Correspondence.PROPERTY_CHAIN) {
			if(ont1.getDatatypeProperties().contains(cor.getDataPropOnt1())) {
				firstOntology = ont2;
				secondOntology = ont1;
			}
			else {
				firstOntology = ont1;
				secondOntology = ont2;
			}
			for(Correspondence corres2 : correspondences) {
				if(cor.getDataPropOnt1() == corres2.getDataPropOnt1() && cor.getObjectProperty1Ont2() != corres2.getObjectProperty1Ont2()) {
					if(cor.getIdentifier().equals(corres2.getIdentifier())) {
						continue;
					}
					sum = corres2.getSimilaritySum();
					if(sum > maximum) {
						maximum = sum;
					}
				}
			}
			if(cor.getSimilaritySum() > maximum) {
				return true;
			}		
			
		}
		return false;
			
	}

}
