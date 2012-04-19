package org.swip.pivotToMappings.sparql;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.QuerySolution;
import java.util.LinkedList;
import java.util.List;

public class LocalSparqlServer extends SparqlServer {

    Dataset dataset = null;

    public LocalSparqlServer(List<String> uris) {
        this.dataset = DatasetFactory.create(uris);
    }

    @Override
    public Iterable<QuerySolution> select(String queryString) {
//        System.out.println(queryString + "\n\n");
        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, dataset);
        ResultSet results = null;
        LinkedList<QuerySolution> resultList = null;
        try {
            results = qExec.execSelect();
            resultList = new LinkedList<QuerySolution>();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                resultList.add(soln);
            }
        } finally {
            qExec.close();
        }
        return resultList;
    }

    @Override
    public boolean ask(String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        boolean result = qexec.execAsk();
        qexec.close();
        return result;
    }
}
