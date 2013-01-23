package org.swip.pivotToMappings.model.query.subquery;

import org.swip.pivotToMappings.model.query.queryElement.QueryElement;


/**
 * abstract class representing a user subquery
 */
public abstract class Subquery {

    abstract Iterable<QueryElement> getQueryElements();

}
