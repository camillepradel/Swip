package org.swip.pivotToMappings.model.query.subquery;

import java.util.LinkedList;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;

/**
 * subquery of type Q3: made of three elements
 */
public class Q3 extends Subquery {

    QueryElement e1;
    QueryElement e2;
    QueryElement e3;

    public Q3(QueryElement e1, QueryElement e2, QueryElement e3) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    @Override
    Iterable<QueryElement> getQueryElements() {
        LinkedList<QueryElement> result = new LinkedList<QueryElement>();
        result.add(e1);
        result.add(e2);
        result.add(e3);
        return result;
    }

    @Override
    public String toString() {
        return "Q3: " + e1 + "\n    " + e2 + "\n    " + e3;
    }

}
