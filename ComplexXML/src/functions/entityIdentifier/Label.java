package functions.entityIdentifier;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import utility.Attributes;

/**
 * 
 * @author Dominique Ritze
 * 
 *         Represents the label of an entity.
 * 
 */
public class Label implements EntityIdentifier {

	/**
	 * Compute the label of the given entity.
	 * 
	 * @param e
	 * @return The label of e.
	 */
	public String compute(OWLEntity e) {

		String label = "";

		OWLOntology onto;
		if (Attributes.firstOntology.getEntities().contains(e)) {
			onto = Attributes.firstOntology.getOntology();
		} else {
			onto = Attributes.secondOntology.getOntology();
		}

		// count the labels and number of language statements if existent
		int numberOflabels = 0, numberOfLanguages = 0;
		// get all labels
		
		
		
		
		
//		for (OWLAnnotation annotation : e.getAnnotations(onto,
//				OWLRDFVocabulary.RDFS_LABEL.getURI())) {
		
		IRI iri = IRI.create(OWLRDFVocabulary.RDFS_LABEL.getURI());
		OWLAnnotationProperty prop = new OWLAnnotationPropertyImpl(iri);
		
		for (OWLAnnotation annotation : e.getAnnotations(onto, prop)) {
		
			// if (annotation.isAnnotationByConstant()) {
			// OWLLiteral val = annotation.getAnnotationValueAsConstant();

			OWLAnnotationValue obj = annotation.getValue();
			if (obj instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) obj;

				// increase the label counter
				numberOflabels++;
				// the label is not annotated with a typ
				// if (!val.isTyped()) {
				if (val.isRDFPlainLiteral()) {

					// label has a language statement
					if (val/* .asOWLUntypedConstant() */.hasLang()) {
						// increase the language counter
						numberOfLanguages++;
					}
				}
			}
		}

		// for (OWLAnnotation annotation : e.getAnnotations(onto,
		// OWLRDFVocabulary.RDFS_LABEL.getURI())) {
			
			for (OWLAnnotation annotation : e.getAnnotations(onto, prop)) {	
			
			
			// if (annotation.isAnnotationByConstant()) {
			// OWLLiteral val = annotation.getAnnotationValueAsConstant();

			OWLAnnotationValue obj = annotation.getValue();
			if (obj instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) obj;

				// only one label
				if (numberOflabels == 1) {
					label = val.getLiteral();
				} else {
					// there are several labels and all of them have a language
					// statement then choose the english one
					if (numberOfLanguages == numberOflabels
							&& val.isRDFPlainLiteral() /* && !val.isTyped() */) {
						if (val/* .asOWLUntypedConstant() */.hasLang("en"))
							label = val.getLiteral();
					}
				}
			}
		}
		return label;
	}
}
