package functions.entityComparison;


import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

import reasoner.Reasoner;
import reasoner.ReasonerOneOntology;
import utility.Attributes;
import exception.ComplexMappingException;

/**
 *
 * @author Dominique Ritze
 *
 * Class to compute domain relation between classes and properties.
 *
 */
public class Domain implements EntityComparison{

        /**
         * Check if the class ent1 is in the domain of property ent2.
         * If the domain of the property is a complex one,
         * the direct subclasses are h
         *
         * @param ent1 The class which is possibly in the domain.
         * @param ent2 The property.
         * @return True if ent1 is domain of ent2.
         */
	public boolean compute(OWLEntity ent1, OWLEntity ent2) throws ComplexMappingException{
		
		OWLClass c = (OWLClass) ent1;
		OWLProperty p = (OWLProperty) ent2;
		
		OWLOntology onto;
		boolean first = false;


		//check in which ontologies the property and class are
		if(p instanceof OWLObjectProperty && Attributes.firstOntology.getObjectProperties().
                        contains((OWLObjectProperty)p) || p instanceof OWLDataProperty
				&& Attributes.firstOntology.getDatatypeProperties().
                                contains((OWLDataProperty)p)) {
			onto = Attributes.firstOntology.getOntology();
			first = true;
		}
		else {
			onto = Attributes.secondOntology.getOntology();
		}
		
		Set<OWLClassExpression> domain;
		
		//get the domain described directly in the ontology
		if(p instanceof OWLObjectProperty) {
			domain = ((OWLObjectProperty) p).getDomains(onto);
		}	
		else {
			domain = ((OWLDataProperty) p).getDomains(onto);
		}
		
		//if the class is already in this set or the domain is thing (size = 0 only in domain is thing)
		if(domain.contains(c) || domain.size() == 0) {
			return true;
		}

                //check if the domain is anonymous, than every direct subclass
                //can be the domain
                for(OWLClassExpression someDomain : domain) {
                    if(someDomain.isAnonymous()) {

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

                        Set<OWLClass> subranges = reasoner.getTypeClasses(someDomain, Reasoner.SUB_CLASS);
                        //check if the class is in the domain which is obtained by the pellet reasoner
                        if(subranges.contains(c)) {
                            return true;
                        }
                    }
                }
            return false;
	}
}
