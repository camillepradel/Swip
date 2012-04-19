package org.swip.pivotToMappings.model.query.subquery;

import java.util.LinkedList;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;

/**
 * subquery of type Q2: made of two elements
 */
public class Q2 extends Subquery {

    QueryElement e1;
    QueryElement e2;

    public Q2(QueryElement e1, QueryElement e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    Iterable<QueryElement> getQueryElements() {
        LinkedList<QueryElement> result = new LinkedList<QueryElement>();
        result.add(e1);
        result.add(e2);
        return result;
    }

    @Override
    public String toString() {
        return "Q2: " + e1 + "\n    " + e2;
    }
}
