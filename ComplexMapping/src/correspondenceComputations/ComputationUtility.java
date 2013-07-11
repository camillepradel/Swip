package correspondenceComputations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import reasoner.Reasoner;
import reasoner.ReasonerOneOntology;
import utility.OntologyAlignment;

import complexMapping.ComplexMappingException;

import de.unima.alcomox.exceptions.MappingException;
import de.unima.alcomox.mapping.Alignment;
import de.unima.alcomox.mapping.AlignmentFormatReader;
import de.unima.alcomox.mapping.AlignmentReader;
import de.unima.alcomox.mapping.AlignmentReaderTxt;
import de.unima.alcomox.mapping.Correspondence;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.descriptions.Term;
import de.unima.ki.mmatch.smeasures.Measure;

/**
 * 
 * @author Dominique Ritze
 * 
 * The class Computation contains some methods which are used by the classes in the same package.
 *
 */
public class ComputationUtility {
	
	/**
	 * Get the range of an obejct property using Pellet.
	 * 
	 * @param reasonerRange
	 * @param ontology
	 * @param property
	 * @return
	 * @throws ComplexMappingException
	 */
	public static Set<OWLClass> getObjectRange(Reasoner reasonerRange, OWLOntology ontology, OWLObjectProperty property) throws ComplexMappingException {
		Set<OWLClass> range = new HashSet<OWLClass>();				
		for(OWLClassExpression allPropRange : property.getRanges(ontology)) {
			for(OWLClassExpression subRange : ((ReasonerOneOntology)reasonerRange).getTypeClasses(allPropRange, Reasoner.DESCENDANT)) {
				range.add((OWLClass)subRange);
				if(allPropRange instanceof OWLClass) {
					range.add((OWLClass)allPropRange);
				}
			}
		}		
		return range;		
	}
	
	/**
	 * Get the range of a datatype property.
	 * 
	 * @param ontology
	 * @param property
	 * @return
	 */
	public static Set<String> getDataRange(OWLOntology ontology, OWLDataProperty property) {	
		Set<String> range = new HashSet<String>();
		//iterate through all ranges and add them to the set
		for(OWLDataRange allRange : property.getRanges(ontology)) {
			if(allRange.isDatatype()) {
				range.add(((OWLDatatype)allRange).getIRI().toString());
			}
			else {
				for(OWLLiteral con :((OWLDataOneOf)allRange).getValues()) {
					range.add(con.toString());
				}
			}
		}
		return range;
	}
	
	/**
	 * Get the domain of a datatype property using Pellet.
	 * 
	 * @param ontology
	 * @param property
	 * @return
	 * @throws ComplexMappingException 
	 */
	public static Set<OWLClass> getDataDomain(Reasoner reasonerDomain, OWLOntology ontology, OWLDataProperty property) throws ComplexMappingException {
		Set<OWLClass> domain = new HashSet<OWLClass>();
		int size = 0;
		
		//iterate through the domain of the property and get all descendants of all possible domains
		for(OWLClassExpression allPropDomain : property.getDomains(ontology)) {
			size++;
			for(OWLClassExpression subDomain : ((ReasonerOneOntology)reasonerDomain).getTypeClasses(allPropDomain, Reasoner.DESCENDANT)) {
				domain.add((OWLClass)subDomain);
				if(allPropDomain instanceof OWLClass) {
					domain.add((OWLClass)allPropDomain);
				}
			}
		}	
		//if there exists no domain, the domain was not specified in the ontology and can be every class
		if(size == 0) {
			for(OWLClassExpression entity : ontology.getClassesInSignature(true)) {
				domain.add((OWLClass)entity);
			}
		}
		return domain;		
	}
	
	/**
	 * Get the domain of an object property using Pellet.
	 * 
	 * @param ontology
	 * @param property
	 * @return
	 * @throws ComplexMappingException 
	 */
	public static Set<OWLClass> getObjectDomain(Reasoner reasonerDomain, OWLOntology ontology, OWLObjectProperty property) throws ComplexMappingException {
		Set<OWLClass> domain = new HashSet<OWLClass>();
		int size = 0;
		//iterate through all domains of the given property and also get all descendant
		for(OWLClassExpression allPropDomain : property.getDomains(ontology)) {
			size++;
			for(OWLClassExpression subDomain : ((ReasonerOneOntology)reasonerDomain).getTypeClasses(allPropDomain, Reasoner.DESCENDANT)) {
				domain.add((OWLClass)subDomain);
				if(allPropDomain instanceof OWLClass) {
					domain.add((OWLClass)allPropDomain);
				}
			}
		}
		//the domain is not specified so it can be every class
		if(size == 0) {			
			for(OWLClassExpression entity : ontology.getClassesInSignature(true)) {
				domain.add((OWLClass)entity);
			}
		}
		return domain;		
	}
	
	/**
	 * Get all disjoint classes of a given class.
	 * 
	 * @param reasonerDisjoint
	 * @param ontology
	 * @param cls
	 * @return
	 * @throws ComplexMappingException
	 */
	public static Set<OWLClassExpression> getDisjointClasses(Reasoner reasonerDisjoint, OWLOntology ontology, OWLClass cls) throws ComplexMappingException {
		Set<OWLClassExpression> disjointClasses = new HashSet<OWLClassExpression>();
		//iterate through all disjoint classes and get every subclass of a disjoint class
		for(OWLClassExpression disjoint : cls.getDisjointClasses(ontology)) {
			for(OWLClass subClasses : ((ReasonerOneOntology)reasonerDisjoint).getTypeClasses(disjoint, Reasoner.DESCENDANT)) {
				disjointClasses.add(subClasses);
			}			
			disjointClasses.add(disjoint);
		}
		return disjointClasses;
	}
	
	/**
	 * Delete the headnoun of all class names.
	 * 
	 * @param oldClasses
	 * @param delimiter
	 * @return
	 * @throws ComplexMappingException 
	 */
	public static HashMap<OWLClass, Term> deleteHeadnoun(Set<OWLClass> oldClasses, boolean[] delimiter) throws ComplexMappingException {
		HashMap<OWLClass, Term> newClasses = new HashMap<OWLClass, Term>();
		String tmp = "";
		Term name;
		try {
			for(OWLClass cls : oldClasses) {
				tmp = "";
				if(cls.getIRI().getFragment() != null) {
					name = new Term(cls.getIRI().getFragment(), delimiter);
				} 
				else {
					String[] realName = cls.getIRI().toString().split("/");
					name = new Term(realName[realName.length-1], delimiter);
					
				}
				//only delete headnoun if the concepts name consists of more than one word, else the new concept is the old one
				if(name.getNumberOfTokensWithoutSpecialTokens(true) != 1) {
					for(int i=0; i<name.getTokensWithoutSpecialTokensNormalizedOrNot(false).length; i++) {
						//delete the headnoun and build a new string without the headnoun
						if(i != name.getHeadNounIndex()) {			
							//the first letter should be a capital letter to seperate the words later
							tmp = tmp + name.getTokensWithoutSpecialTokensNormalizedOrNot(false)[i].substring(0,1).toUpperCase() + 
							name.getTokensWithoutSpecialTokensNormalizedOrNot(false)[i].substring(1);
						}				
					}		
					//create a new concept and put it in the hashmap
					newClasses.put(cls, new Term(tmp, delimiter));
				}
				else {
					newClasses.put(cls, name);
				}  			
			}
		} catch(MMatchException e) {
			throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not delete the headnoun of a term." , e);
		}
		
		return newClasses;
	}
	
	/**
	 * Get the reduced name of a term, that means the string without the word at the i-th position.
	 * 
	 * @param t
	 * @param i
	 * @return
	 */
	public static String getReducedName(Term t, int i) {	
		String tmp = "";
		for(int j=0; j<t.getTokensWithoutSpecialTokens().length; j++) {
			//if this word should be deleted
			if(i!=j) {
				if(j==t.getTokensWithoutSpecialTokens().length-1 || (j==t.getTokensWithoutSpecialTokens().length-2 &&
						i==t.getTokensWithoutSpecialTokens().length-1)) {
					tmp += t.getTokensWithoutSpecialTokens()[j];
				}
				else {
					//insert $ as delimiter
					tmp += t.getTokensWithoutSpecialTokens()[j] + "$";
				}				
			}			
		}
		String name = "";
		int index = 0;
		for(int j=0; j<tmp.length(); j++) {			
			//delete $ and build a camel case string where the upper case letter is the letter after $
			if(tmp.substring(j,j+1).equals("$")) {
				if(j<tmp.length()-1) {
					name += tmp.substring(index,j) + tmp.substring(j+1, j+2).toUpperCase();
					index = j+2;
				}					
			}
		}
		name += tmp.substring(index);
		return name;
	}	
	
	
	/**
	 * Get the similarity value of two terms.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 * @throws ComplexMappingException 
	 */
	public static double getSimilarValue(Term t1, Term t2) throws ComplexMappingException {
		try {
			Measure m = new Measure();
			if(t1 != null && t2 != null) {
				return m.compare(t1, t2);
			}
			else {
				return 0.0;
			}
		} catch(MMatchException e) {
			throw new ComplexMappingException(ComplexMappingException.MMATCH_EXCEPTION, "Could not compare term.", e);
		}
		
	}
	
	/**
	 * Check if a correspondence is contained in the conventional alignment.
	 * 
	 * @param entity
	 * @param ali
	 * @return
	 * @throws ComplexMappingException
	 */
	public static boolean alreadyInAlignment(OWLEntity entity, OntologyAlignment ali) throws ComplexMappingException {
		ArrayList<Correspondence> correspondencesAlignment;
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
		correspondencesAlignment = reference.getCorrespondences();	
		
		for(Correspondence corres : correspondencesAlignment) {
			if(entity.getIRI().toString().equals(corres.getSourceEntityUri())) {
				//properties should not be considered if they are in the alignment or not to avoid problems with property chain
				if(!(entity instanceof OWLDataProperty || entity instanceof OWLObjectProperty)) {
					return true;
				}				
			}
			else if(entity.getIRI().toString().equals(corres.getTargetEntityUri())) {
				if(!(entity instanceof OWLDataProperty || entity instanceof OWLObjectProperty)) {
					return true;
				}        						
			}
		}
		return false;
		
	}

}
