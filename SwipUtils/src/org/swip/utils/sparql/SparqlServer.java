package org.swip.utils.sparql;

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

    public boolean isDataProperty(List<String> types) {
        boolean ret = false;
        for (String type : types) {
            if (type.endsWith("DatatypeProperty")) {
                ret = true;
            }
        }
        return ret;
    }

    public boolean isDataProperty(String resourceUri) {
        List<String> types = listTypes(resourceUri);
        for (String type : types) {
            if (type.endsWith("DatatypeProperty")) {
                return true;
            }
        }
        return false;
    }

    public boolean isNumericDataProperty(String uri) {
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
                + "ASK "
                + "WHERE "
                + "{ <" + uri + "> rdfs:range xsd:decimal }";
        boolean ret = false;

        ret = ask(query);

        return ret;
    }

    public boolean isInstanceOfClass(String instanceUri, String classUri) {
        String query = "PREFIX mo:   <http://purl.org/ontology/mo/> "
                + "ASK "
                + "WHERE "
                + "{ <" + instanceUri + "> (a|mo:release_type) <" + classUri + "> }";
        return ask(query);
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
                + "     <" + resourceUri + "> rdf:type ?type.\n"
                + "     FILTER NOT EXISTS{\n"
                + "         ?subType rdfs:subClassOf ?type.\n"
                + "         <" + resourceUri + "> rdf:type ?subType.\n"
                + "         FILTER (!sameTerm(?type,?subType))\n"
                + "     }"
                + "}";

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
                + "  PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?str \n"
                + "WHERE { \n"
                + "    <" + resourceUri + "> (rdfs:label|dc:title|foaf:name) ?label. \n"
                + "    BIND(str(?label) AS ?str) \n"
                + "} ";

        try {
            Iterable<QuerySolution> results = select(query);
            for (QuerySolution qs : results) {
                return qs.get("str").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "LABEL_NOT_FOUND";
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

    public List<String> listInverses(String resourceUri) {
        // it is necessary to check owl:inverseOf (symmetric) property in both ways because we don't use an OWL reasoner for now
        String query = "  PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "SELECT ?inverse \n"
                + "WHERE { \n"
                + "    OPTIONAL {<" + resourceUri + "> owl:inverseOf ?inverse.} \n"
                + "    OPTIONAL {?inverse owl:inverseOf <" + resourceUri + ">.} \n"
                + "    FILTER (bound(?inverse)) \n"
                + "}";

        Iterable<QuerySolution> results = select(query);
        List<String> resultList = new LinkedList<String>();

        for (QuerySolution qs : results) {
            resultList.add(qs.get("inverse").toString());
        }

        return resultList;
    }
}
