package functions.entityComparison;

import org.semanticweb.owlapi.model.OWLEntity;

import exception.ComplexMappingException;

/**
 *
 * @author Dominique Ritze
 *
 * Computation of subclass relations.
 *
 */
public class SubclassOf implements EntityComparison{

        /**
         * Check if c1 is a subclass of c2, whereby also
         * a reasoner is used.
         *
         * @param c1 The "subclass".
         * @param c2 The "superclass".
         * @return True if c1 is the subclass of c2.
         */
	public boolean compute(OWLEntity c1, OWLEntity c2) throws ComplexMappingException{
		SuperclassOf sc = new SuperclassOf();
                boolean value = sc.compute(c2, c1);
		return value;
	}

}
