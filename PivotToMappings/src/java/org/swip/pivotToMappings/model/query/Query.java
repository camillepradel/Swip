package org.swip.pivotToMappings.model.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.pivotToMappings.model.query.subquery.Subquery;
import org.swip.pivotToMappings.sparql.SparqlServer;

/**
 * class representing a user query
 */
public class Query {

    /**
     * subqueries (Q1, Q2, Q3) composing the query
     */
    private Collection<Subquery> subqueries = null;
    /**
     * the set of query elements present in the query
     * a same query element appears just once, even if it is repeated in the user query
     */
    private Set<QueryElement> queryElements = null;
    boolean count = false;
    boolean max = false;
    boolean min = false;
    boolean sum = false;
    boolean avg = false;
    boolean ask = false;

    public Query() {
        this.subqueries = new LinkedList<Subquery>();
        this.queryElements = new HashSet<QueryElement>();
    }

    public Set<QueryElement> getQueryElements() {
        return queryElements;
    }

    public void addQueryElement(QueryElement qe) {
        this.queryElements.add(qe);
    }

    public Collection<Subquery> getSubqueries() {
        return subqueries;
    }

    public void addSubquery(Subquery sq) {
        this.subqueries.add(sq);
    }

    public boolean isCount() {
        return count;
    }

    public boolean isMax() {
        return max;
    }
    
    public boolean isMin() {
        return min;
    }
    
    public boolean isAvg() {
        return avg;
    }
    
    public boolean isSum() {
        return sum;
    }

    
    public boolean isAsk() {
        return ask;
    }
    
    public boolean isSelectAggregate()
    {
        return (this.ask || this.avg || this.count || this.max || this.min || this.sum);
    }

    public void setCount(boolean count) {
        this.count = count;
    }
    
      public void setMax(boolean max) {
        this.max = max;
    }
    
    public void setMin(boolean min) {
        this.min = min;
    }
    
    public void setAvg(boolean avg) {
        this.avg = avg;
    }
    
    public void setSum (boolean sum) {
        this.sum = sum;
    }

    public void setAsk(boolean ask) {
        this.ask = ask;
    }

    @Override
    public String toString() {
        String s = (this.count == true) ? "COUNT\n" : "";
        for (Subquery sq : this.subqueries) {
            s += sq.toString() + "\n";
        }
        return s;
    }

    public void matchElements(SparqlServer server) {
        for (QueryElement qe : this.queryElements) {
            qe.match(server);
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
    }

    public int getNumberOfQueriedElements() {
        int result = 0;
        for (QueryElement qe : this.queryElements) {
            if (qe.isQueried()) {
                result++;
            }
        }
        return result;
    }
}
