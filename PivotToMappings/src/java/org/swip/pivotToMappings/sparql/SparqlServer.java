package org.swip.pivotToMappings.sparql;

import com.hp.hpl.jena.query.QuerySolution;
import java.util.LinkedList;
import java.util.List;

public abstract class SparqlServer {

    abstract public Iterable<QuerySolution> select(String queryString);

//    abstract public Model construct(String queryString);

    abstract public boolean ask(String queryString);

    public boolean isClass(String resourceUri) {
//        String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
//                + "  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
//                + "ASK { \n"
//                + "        <" + resourceUri + "> rdf:type <http://www.w3.org/2000/01/rdf-schema#Class>.\n"
////                + "    UNION\n"
////                + "        <" + resourceUri + "> rdf:type <http://www.w3.org/2000/01/rdf-schema#Class>.\n"
//                + "    } ";
//        return ask(query);
        List<String> types = listTypes(resourceUri);
        for (String type : types) {
            if (type.endsWith("Class")) {
                return true;
            }
        }
        return false;
    }

    public boolean isClass(List<String> types) {
        for (String type : types) {
            if (type.endsWith("Class")) {
                return true;
            }
        }
        return false;
    }

    public boolean isProperty(String resourceUri) {
        List<String> types = listTypes(resourceUri);
        for (String type : types) {
            if (type.endsWith("Property")) {
                return true;
            }
        }
        return false;
    }

    public boolean isProperty(List<String> types) {
        for (String type : types) {
            if (type.endsWith("Property")) {
                return true;
            }
        }
        return false;
    }

//    public boolean isProperty(String resourceUri) {
//        String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
//                + "  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
//                + "ASK { \n"
//                + "        <" + resourceUri + "> rdf:type <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>\n"
////                + "    UNION\n"
////                + "        <" + resourceUri + "> rdf:type <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>\n"
//                + "    } ";
//        return ask(query);
//    }

    public List<String> listTypes(String resourceUri) {
        String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "SELECT ?type \n"
                + "WHERE { \n"
                + "    <" + resourceUri + "> rdf:type ?type \n"
                + "      } ";

        Iterable<QuerySolution> results = select(query);
        List<String> resultList = new LinkedList<String>();

        for (QuerySolution qs : results) {
            resultList.add(qs.get("type").toString());
        }

        return resultList;
    }

    public String getALabel(String resourceUri) {
        String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "  PREFIX dc: <http://purl.org/dc/elements/1.1/> \n"
                + "SELECT ?label \n"
                + "WHERE { \n"
                + "    {<" + resourceUri + "> dc:title ?label.} \n"
                + "    UNION \n"
                + "    {<" + resourceUri + "> rdfs:label ?label.} \n"
                + "      } ";

        Iterable<QuerySolution> results = select(query);

        for (QuerySolution qs : results) {
            return qs.get("label").toString();
        }
        return null;
    }

    public List<String> listRanges(String resourceUri) {
        String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "SELECT ?range \n"
                + "WHERE { \n"
                + "    <" + resourceUri + "> rdfs:range ?range \n"
                + "      } ";

        Iterable<QuerySolution> results = select(query);
        List<String> resultList = new LinkedList<String>();

        for (QuerySolution qs : results) {
            resultList.add(qs.get("range").toString());
        }

        return resultList;
    }
}
