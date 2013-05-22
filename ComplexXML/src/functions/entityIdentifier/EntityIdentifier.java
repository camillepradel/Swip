package functions.entityIdentifier;

import functions.Function;
import org.semanticweb.owl.model.OWLEntity;

/**
 *
 * @author Dominique Rite
 *
 * Interface which are implemented by all classes which
 * are some entity identifiers like name or label.
 *
 */
public interface EntityIdentifier extends Function{

        /**
         * Method to get the identifier of
         * an entity.
         *
         * @param e
         * @return The computed identifier.
         */
	public String compute(OWLEntity e);

}
