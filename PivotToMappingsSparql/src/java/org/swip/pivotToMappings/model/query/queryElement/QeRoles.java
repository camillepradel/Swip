package org.swip.pivotToMappings.model.query.queryElement;

import java.util.LinkedList;
import java.util.List;
import org.swip.pivotToMappings.model.query.antlr.QueryElementException;

/**
 *
 * @author camille
 */
public class QeRoles {
    List<QeRole> roles = new LinkedList<QeRole>();

    public void addRole(QeRole role) throws QueryElementException {
        if (role == QeRole.E2Q3) {
            if (roles.contains(QeRole.E1Q1) || roles.contains(QeRole.E1Q23) || roles.contains(QeRole.E2Q2) || roles.contains(QeRole.E3Q3)) {
                throw new QueryElementException();
            }
        } else if (role == QeRole.E1Q1 || role == QeRole.E1Q23 || role == QeRole.E2Q2 || role == QeRole.E3Q3) {
            if (roles.contains(QeRole.E2Q3)) {
                throw new QueryElementException();
            }
        }
        this.roles.add(role);
    }

//    boolean compatibleWith(List<String> types) {
//        // FIXME: requêtes réalisées plusieurs fois
//        //        ajouter gestions des litéraux (si nécessaire)
//        if (types.contains("http://www.w3.org/2000/01/rdf-schema#Class")) {
//            if (!(roles.contains(QeRole.E1Q1) || roles.contains(QeRole.E1Q23) || roles.contains(QeRole.E2Q2) || roles.contains(QeRole.E3Q3))){
//                return true;
//            }
//        } else /*if (or.isClass() || or.isIndividual())*/ {
//            if (!(roles.contains(QeRole.E2Q3))){
//                return true;
//            }
//        } /*else if (or.isLiteral()) {
//            if (!(roles.contains(QeRole.Q3e2) || roles.contains(QeRole.Q1e1) || roles.contains(QeRole.Q2e1) || roles.contains(QeRole.Q3e1))){
//                return true;
//            }
//        }*/
//        return false;
//    }

    public boolean usedAsProperty() {
        for (QeRole role : roles) {
            if (role != QeRole.E2Q3)
                return false;
        }
        return true;
    }

    boolean contains (QeRole r) {
        return this.roles.contains(r);
    }
}
