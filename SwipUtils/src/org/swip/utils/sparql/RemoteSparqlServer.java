package org.swip.utils.sparql;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class RemoteSparqlServer {

    String endpointUri = null;

    public RemoteSparqlServer(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public String getEndpointUri() {
        return endpointUri;
    }

    public void setEndpointUri(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public Iterable<QuerySolution> select(String queryString, String endpointUri) {
        ResultSet results = null;
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpointUri, queryString);
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

    public boolean ask(String queryString) {
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpointUri + "sparql", queryString);
            System.out.println(endpointUri + "sparql");
            boolean result = false;
            try {
                result = qexec.execAsk();
            } finally {
                qexec.close();
            }
            return result;
    }

    public String update(String queryString) {
        String status = "";
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(endpointUri + "update");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("update", queryString));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response2 = httpclient.execute(httpPost);

            try {
                status = response2.getStatusLine().toString();
                HttpEntity entity2 = response2.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2);
            } finally {
                httpPost.releaseConnection();
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteSparqlServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    public static void main(String args[]) {
        RemoteSparqlServer serv = new RemoteSparqlServer("http://swip.univ-tlse2.fr:8080/musicbrainz/");

        String query = "ASK WHERE { ?s ?ppp ?o }";
        if (serv.ask(query)) {
            System.out.println("service is UP");
        } else {
            System.out.println("service is DOWN");
        }

//        query = "PREFIX swip:   <http://swip.univ-tlse2.fr#>\n"
//                + "PREFIX pref:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
//                + "INSERT DATA\n"
//                + "{\n"
//                + "  GRAPH pref:queries\n"
//                + "  {\n"
//                + "    pref:Coast_To_Coast-c-song-p-person-c-compose-e-Coast_To_Coast-p- a swip:PivotQuery.\n"
//                + "  }\n"
//                + "}\n";
//        serv.update(query);

        query = "PREFIX swip:   <http://swip.univ-tlse2.fr#>\n"
                + "ASK\n"
                + "{\n"
                + "    GRAPH ?g { <http://swip.univ-tlse2.fr:8080/musicbrainz/sparql/Coast_To_Coast-c-song-p-person-c-compose-e-Coast_To_Coast-p-> ?a ?b. }\n"
                + "}\n";
        System.out.println(serv.ask(query));
    }
}
