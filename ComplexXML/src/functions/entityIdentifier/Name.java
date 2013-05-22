package functions.entityIdentifier;

import org.semanticweb.owl.model.OWLEntity;

/**
 *
 * @author Dominique Ritze
 *
 * Represents the name (the id) of an entity.
 *
 */
public class Name implements EntityIdentifier{

        /**
         * Get the name (id) of a given entity.
         *
         * @param e
         * @return The name of the entity.
         */
	public String compute(OWLEntity e) {
            if(e.getURI().getFragment() != null) {
                return e.getURI().getFragment().toString();
            }
            else {
                //some problems if the URI does not contain a # to indicate the fragment
                return e.getURI().toString().substring(e.getURI().toString().lastIndexOf("/")+1);
            }

	}

}
