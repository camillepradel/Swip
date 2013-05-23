package functions.entityComparison;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import reasoner.Reasoner;
import reasoner.ReasonerOneOntology;
import utility.Attributes;
import exception.ComplexMappingException;

/**
 *
 * @author Dominique Ritze
 *
 * Class to compute if a class is range of an object property.
 *
 */
public class ObjectRange implements EntityComparison{

        /**
         * Check if the class is a possible range of the object property, whereby
         * not only the range specified in the ontology is taken into account,
         * also all its subclasses but just of the same ontology.
         *
         * @param ent1 The possible range as class.
         * @param ent2 The object property.
         * @return
         */
	public boolean compute(OWLEntity ent1, OWLEntity ent2) throws ComplexMappingException{

		OWLClass c = (OWLClass)ent1;
		OWLObjectProperty p = (OWLObjectProperty) ent2;
			
		OWLOntology onto;
		boolean first = false;
		
		if(Attributes.firstOntology.getObjectProperties().contains(p)) {
                    onto = Attributes.firstOntology.getOntology();
                    first = true;
		}	
		else {
                    onto = Attributes.secondOntology.getOntology();
		}

                Set<? extends OWLClassExpression> range = ((OWLObjectProperty)p).getRanges(onto);

                //if the set is empty, every class can be range of the property
                if(range.size() == 0) {
                    return true;
                }

                if(range.contains(c)) {
                        return true;
                }
                //check if the range is anonymous
                //every direct subclass is then the range
                for(OWLClassExpression someRange : range) {
                    if(someRange.isAnonymous()) {

                        ReasonerOneOntology reasoner;

                        //ontology with entities is the first one
                        if(first) {
                         //check if the reasoner has been set to get better performance
                            if(Attributes.reasonerFirst == null) {
                                Attributes.reasonerFirst = new ReasonerOneOntology(Attributes.firstOntology.getFilepath());
                            }
                            reasoner = Attributes.reasonerFirst;
                        }
                        else {
                            if(Attributes.reasonerSecond == null) {
                                Attributes.reasonerSecond = new ReasonerOneOntology(Attributes.secondOntology.getFilepath());
                            }
                            reasoner = Attributes.reasonerSecond;
                        }

                        Set<OWLClass> subranges = reasoner.getTypeClasses(someRange, Reasoner.SUB_CLASS);
                        //check if the class is in the domain which is obtained by the pellet reasoner
                        if(subranges.contains(c)) {
                            return true;
                        }
                    }
                }
            return false;
	}	
	
}
