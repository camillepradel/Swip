package functions.entityComparison;

import ontology.Ontology;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;

import reasoner.Reasoner;
import reasoner.ReasonerOneOntology;
import reasoner.ReasonerTwoOntologies;
import utility.Attributes;
import exception.ComplexMappingException;

/**
 *
 * @author Dominique Ritze
 *
 * Class to compute the superclass relations.
 *
 */
public class SuperclassOf implements EntityComparison{

        /**
         * Check if one class is the superclass of the second one.
         * Therefore also a reasoner is used and it is also possible
         * through the reference alignment that the classes are not
         * necessarily in the same ontology.
         * 
         * @param class1 The "superclass".
         * @param class2 The "subclass".
         * @return True if class1 is a superclass of class2.
         */
	public boolean compute(OWLEntity class1, OWLEntity class2) throws ComplexMappingException{
		
		boolean oneOntology = false;
		boolean first = false;
		
		Ontology ont1;
		Ontology ont2;

                //check which class contains to which ontology
		if(Attributes.firstOntology.getClasses().contains((OWLClass)class1)) {
			ont1 = Attributes.firstOntology;
			if(Attributes.firstOntology.getClasses().contains((OWLClass)class2)) {
				ont2 = Attributes.firstOntology;
				oneOntology = true;
				first = true;
			}
			else {
				ont2 = Attributes.secondOntology;
			}
		}
		else {
			ont1 = Attributes.secondOntology;
			if(Attributes.secondOntology.getClasses().contains((OWLClass)class2)) {
				ont2 = Attributes.secondOntology;
				oneOntology = true;				
			}
			else {
				ont2 = Attributes.firstOntology;
			}
		}
		
		ReasonerOneOntology reasonerOne; 
		ReasonerTwoOntologies reasonerBoth;

                //create the reasoner and get all superclasses with the help
                //of the reasoner
                if(oneOntology) {
                    if(first) {
                        //check if the reasoner has been set to get better performance
                        if(Attributes.reasonerFirst == null) {
                                Attributes.reasonerFirst = new ReasonerOneOntology
                                        (Attributes.firstOntology.getFilepath());
                        }
                        reasonerOne = Attributes.reasonerFirst;
                    }
                    else {
                        if(Attributes.reasonerSecond == null) {
                                Attributes.reasonerSecond = new ReasonerOneOntology
                                        (Attributes.secondOntology.getFilepath());
                        }
                        reasonerOne = Attributes.reasonerSecond;
                    }
				
                    if(reasonerOne.getTypeClasses((OWLClassExpression)class1, Reasoner.DESCENDANT)
                            .contains((OWLClass)class2)) {
                            return true;
                    }
				
                }
                else {
				
                    if(Attributes.reasonerBoth == null) {
                            Attributes.reasonerBoth = new ReasonerTwoOntologies
                                    (ont1.getFilepath(), ont2.getFilepath(), Attributes.alignment);
                    }
                    reasonerBoth = Attributes.reasonerBoth;
				

                    if(reasonerBoth.getTypeClassesBothOntologies((OWLClassExpression)
                            class1, Reasoner.DESCENDANT).contains((OWLClass)class2)) {
                            return true;
                    }
                }
		
            return false;
	}
}
