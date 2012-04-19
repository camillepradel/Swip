package org.swip.pivotToMappings.sparql;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import java.util.LinkedList;

public class RemoteSparqlServer extends SparqlServer {

    String service = null;

    public RemoteSparqlServer(String service) {
        this.service = service;
    }

    @Override
    public Iterable<QuerySolution> select(String queryString) {
//        System.out.println(queryString + "\n\n");
        ResultSet results = null;
        QueryExecution qexec = QueryExecutionFactory.sparqlService(service, queryString);
        LinkedList<QuerySolution> resultList = null;
        try {
            results = qexec.execSelect();
            resultList = new LinkedList<QuerySolution>();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                resultList.add(soln);
            }
        } finally {
            qexec.close();
        }
        return resultList;
    }

    @Override
    public boolean ask(String queryString) {
//        System.out.println(queryString + "\n\n");
        QueryExecution qexec = QueryExecutionFactory.sparqlService(service, queryString);
        boolean result = false;
        try {
            result = qexec.execAsk();
        } finally {
            qexec.close();
        }
        return result;
    }

    public static void main(String args[]) {
        RemoteSparqlServer serv = new RemoteSparqlServer("http://localhost:2020/music");

        String query = "ASK WHERE { ?s ?p ?o }";
        if (serv.ask(query))  {
            System.out.println("service is UP");
        } else {
            System.out.println("service is DOWN");
        }
    }
}
