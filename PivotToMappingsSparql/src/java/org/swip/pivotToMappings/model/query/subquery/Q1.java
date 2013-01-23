package org.swip.pivotToMappings.model.query.subquery;

import java.util.LinkedList;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;

/**
 * subquery of type Q1: made of one element
 */
public class Q1 extends Subquery {

    QueryElement e1;

    public QueryElement getE1() {
        return e1;
    }

    public Q1(QueryElement e1){
        this.e1 = e1;
    }

    @Override
    Iterable<QueryElement> getQueryElements() {
        LinkedList<QueryElement> result = new LinkedList<QueryElement>();
        result.add(e1);
        return result;
    }

    @Override
    public String toString() {
        return "Q1: " + e1;
    }

}
