package functions.entityIdentifier;

import org.semanticweb.owlapi.model.OWLEntity;

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
            if(e.getIRI().getFragment() != null) {
                return e.getIRI().getFragment().toString();
            }
            else {
                //some problems if the URI does not contain a # to indicate the fragment
                return e.getIRI().toString().substring(e.getIRI().toString().lastIndexOf("/")+1);
            }

	}

}
