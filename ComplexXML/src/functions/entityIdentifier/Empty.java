
package functions.entityIdentifier;

import org.semanticweb.owlapi.model.OWLEntity;

/**
 *
 * @author Dominique Ritze
 *
 * This class is used to determine the name of an entity (id or label)
 * if the type (id/label) has not been specified.
 */
public class Empty implements EntityIdentifier{

    /**
     *
     * If the label is not empty, the label will be the name.
     * Otherwise the id.
     *
     * @param e1
     * @return
     */
    public String compute(OWLEntity e1) {
        Label l = new Label();
        String annotation = l.compute(e1);
        if(!annotation.equals("")) {
            return annotation;
        }
        else {
            Name n = new Name();
            return n.compute(e1);
        }
    }

}
