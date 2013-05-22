package functions.entityIdentifier;

import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import utility.Attributes;

/**
 *
 * @author Dominique Ritze
 *
 * Represents the label of an entity.
 *
 */
public class Label implements EntityIdentifier{

        /**
         * Compute the label of the given entity.
         *
         * @param e
         * @return The label of e.
         */
	public String compute(OWLEntity e) {
		
		String label = "";
		
		OWLOntology onto;
		if(Attributes.firstOntology.getEntities().contains(e)) {
			onto = Attributes.firstOntology.getOntology();
		}
		else {
			onto = Attributes.secondOntology.getOntology();
		}
		
		//count the labels and number of language statements if existent
		int numberOflabels = 0, numberOfLanguages = 0;
		//get all labels
		for (OWLAnnotation<OWLObject> annotation : e.getAnnotations(onto, OWLRDFVocabulary.RDFS_LABEL.getURI())) {
			if (annotation.isAnnotationByConstant()) {
				OWLConstant val = annotation.getAnnotationValueAsConstant();
				//increase the label counter
				numberOflabels++;
				//the label is not annotated with a typ
				if (!val.isTyped()) {
					//label has a language statement
					if (val.asOWLUntypedConstant().hasLang()) {
						//increase the language counter
						numberOfLanguages++;
					}
				}
			}
		}
		
		for (OWLAnnotation<OWLObject> annotation : e.getAnnotations(onto, OWLRDFVocabulary.RDFS_LABEL.getURI())) {
			if (annotation.isAnnotationByConstant()) {        				
				OWLConstant val = annotation.getAnnotationValueAsConstant();   
				//only one label
				if(numberOflabels == 1) {
					label = val.getLiteral();
				}
				else {
					//there are several labels and all of them have a language statement then choose the english one       					
					if (numberOfLanguages == numberOflabels && !val.isTyped()) {               						
						if (val.asOWLUntypedConstant().hasLang("en")) 
							label = val.getLiteral();
					}        					
				} 
			}
		}
		return label;
	}
}
