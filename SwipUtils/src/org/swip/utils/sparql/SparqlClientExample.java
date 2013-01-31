package org.swip.utils.sparql;

import java.util.Map;

public class SparqlClientExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SparqlClient sparqlClient = new SparqlClient("192.168.250.91:8080/sparqluedo");

        String query = "ASK WHERE { ?s ?p ?o }";
        boolean serverIsUp = sparqlClient.ask(query);
        if (serverIsUp) {
            System.out.println("server is UP");

            nbPersonnesParPiece(sparqlClient);

            System.out.println("ajout d'une personne dans le bureau:");
            query = "PREFIX : <http://www.lamaisondumeurtre.fr#>\n"
                    + "PREFIX instances: <http://www.lamaisondumeurtre.fr/instances#>\n"
                    + "INSERT DATA\n"
                    + "{\n"
                    + "  instances:Bob :personneDansPiece instances:Bureau.\n"
                    + "}\n";
            sparqlClient.update(query);

            nbPersonnesParPiece(sparqlClient);

            System.out.println("suppression d'une personne du bureau:");
            query = "PREFIX : <http://www.lamaisondumeurtre.fr#>\n"
                    + "PREFIX instances: <http://www.lamaisondumeurtre.fr/instances#>\n"
                    + "DELETE DATA\n"
                    + "{\n"
                    + "  instances:Bob :personneDansPiece instances:Bureau.\n"
                    + "}\n";
            sparqlClient.update(query);
            
            nbPersonnesParPiece(sparqlClient);
            
        } else {
            System.out.println("service is DOWN");
        }
    }
    
    private static void nbPersonnesParPiece(SparqlClient sparqlClient) {
        String query = "PREFIX : <http://www.lamaisondumeurtre.fr#>\n"
                    + "SELECT ?piece (COUNT(?personne) AS ?nbPers) WHERE\n"
                    + "{\n"
                    + "    ?personne :personneDansPiece ?piece.\n"
                    + "}\n"
                    + "GROUP BY ?piece\n";
            Iterable<Map<String, String>> results = sparqlClient.select(query);
            System.out.println("nombre de personnes par pièce:");
            for (Map<String, String> result : results) {
                System.out.println(result.get("piece") + " : " + result.get("nbPers"));
            }
    }    
}
