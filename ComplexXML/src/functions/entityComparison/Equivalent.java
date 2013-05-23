package functions.entityComparison;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLEntity;

import utility.Attributes;
import de.unima.alcomox.mapping.Alignment;
import de.unima.alcomox.mapping.Correspondence;
import de.unima.alcomox.mapping.SemanticRelation;

/**
 *
 * @author Dominique Ritze
 *
 * Class which can compute whether two entites are equivalent (by reference
 * mapping) or not.
 *
 */
public class Equivalent implements EntityComparison{

        /**
         * Checks if two entites are equivalent with help of the reference
         * alignment.
         *
         * @param e1
         * @param e2
         * @return True if the entities are equivalent, false otherwise.
         */
	public boolean compute(OWLEntity e1, OWLEntity e2) {

                    //get reference alignment
                    Alignment reference = Attributes.alignment.getAlignment();

                    //get all correspondences
                    ArrayList<Correspondence> corres = reference.getCorrespondences();
                    for(Correspondence c : corres) {
                        //check if the current two entities are in a given correspondence
                        if(c.getSourceEntityUri().toString().equals(e1.getIRI().toString()) &&
                                c.getTargetEntityUri().toString().equals(e2.getIRI().toString()) &&
                                c.getRelation().getType() == SemanticRelation.EQUIV) {
                            return true;
                        }

                        if(c.getSourceEntityUri().toString().equals(e2.getIRI().toString()) &&
                                c.getTargetEntityUri().toString().equals(e1.getIRI().toString()) &&
                                c.getRelation().getType() == SemanticRelation.EQUIV) {
                            return true;
                        }
                    }
		
		return false;
	}

}
