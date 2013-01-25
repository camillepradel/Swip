package org.swip.pivotToMappings;

import org.apache.log4j.Logger;
import org.swip.utils.sparql.RemoteSparqlServer;

public class QueryInterpreter extends Thread {

    private static final Logger logger = Logger.getLogger(QueryInterpreter.class);
    String queryUri = null;
    RemoteSparqlServer sparqlServer = null;
    int numMappings = 0;

    public QueryInterpreter(String queryUri, RemoteSparqlServer sparqlServer, int numMappings) {
        this.queryUri = queryUri;
        this.sparqlServer = sparqlServer;
        this.numMappings = numMappings;
    }

    @Override
    public void run() {
        boolean commitUpdate = true;
        logger.info("================================================================");
        logger.info("Initializing query interpretation:");
        logger.info("---------------------------------------------------\n");
        initializeQueryInterpretation(queryUri, sparqlServer, commitUpdate);
        logger.info("================================================================");
        logger.info("Matching query elements to knowledge base elements:");
        logger.info("---------------------------------------------------\n");
        performMatching(queryUri, sparqlServer, commitUpdate);
        logger.info("================================================================");
        logger.info("Mapping patterns elements:");
        logger.info("--------------------------\n");
        performElementMapping(queryUri, sparqlServer, commitUpdate);
//        logger.info("================================================================");
//        logger.info("Mapping pattern triples:");
//        logger.info("------------------------\n");
//        performTripleMapping(queryUri, sparqlServer, commitUpdate);
        logger.info("================================================================");
        logger.info("Mapping subpattern collections:");
        logger.info("------------------------\n");
        performSpCollectionMapping(queryUri, sparqlServer, commitUpdate);
        logger.info("================================================================");
        logger.info("Mapping patterns:");
        logger.info("------------------------\n");
        performPatternMapping(queryUri, sparqlServer, commitUpdate);
        logger.info("================================================================");
        logger.info("Ranking pattern mappings:");
        logger.info("------------------------\n");
        performMappingRanking(queryUri, sparqlServer, commitUpdate);
        logger.info("================================================================");
        logger.info("Generating and evaluating possible mappings");
        logger.info("--------------------------------------\n");
        // TODO
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, "QueryProcessed");
    }

    private void initializeQueryInterpretation(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, "InitializingQueryInterpretation");

        // specify for all subpatterns that they have not yet been mapped to the current query
        String query = "# specify for all subpatterns that they have not yet been mapped to the current query\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?spc queries:isNotMappedToQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH graph:patterns\n"
                + "  {\n"
                + "    ?spc a patterns:SubpatternCollection.\n"
                + "  }\n"
                + "}";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }

        // specify for all pattern elements that they have not yet been mapped to the current query
        query = "# specify for all pattern elements that they have not yet been mapped to the current query\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pe queries:isNotMappedToQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH graph:patterns\n"
                + "  {\n"
                + "    ?pe a patterns:PatternElement.\n"
                + "  }\n"
                + "}";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }

    private void performMatching(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, "PerformingMatching");

        // matching keywords

        // could be done at the same time on ontologies and instances with the following query (not tested)
        // but the execution is too long. that's why the keyword matching is performed in two steps
//        String query = "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
//                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
//                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
//                + "PREFIX pf:   <http://jena.hpl.hp.com/ARQ/property#>\n"
//                + "INSERT\n"
//                + "{\n"
//                + "  GRAPH graph:queries\n"
//                + "  {\n"
//                + "    ?keyword queries:queryElementHasMatching _:matching.\n"
//                + "    _:matching a queries:Matching;\n"
//                + "               queries:matchingHasResource ?r;\n"
//                + "               queries:matchingHasScore ?s;\n"
//                + "               queries:matchingHasMatchedLabel ?l.\n"
//                + "  }\n"
//                + "}\n"
//                + "WHERE\n"
//                + "{\n"
//                + "  GRAPH graph:queries\n"
//                + "  {\n"
//                + "    <" + queryUri + "> queries:queryHasQueryElement ?keyword.\n"
//                + "    ?keyword a queries:KeywordQueryElement.\n"
//                + "    ?keyword queries:queryElementHasValue ?keywordValue.\n"
//                + "  }\n"
//                + "  OPTIONAL\n"
//                + "  {\n"
//                + "    GRAPH graph:ontologies\n"
//                + "    {\n"
//                + "      (?l ?s2) pf:textMatch (?keywordValue 0.6 5).\n"
//                + "      ?r rdfs:label ?l.\n"
//                + "      BIND ((?s2 * 4) AS ?s)\n"
//                + "    }\n"
//                + "  }\n"
//                + "  OPTIONAL\n"
//                + "  {\n"
//                + "    GRAPH graph:instances\n"
//                + "    {\n"
//                + "      (?l ?s) pf:textMatch (?keywordValue 0.6 10).\n"
//                + "      ?r rdfs:label ?l.\n"
//                + "    }\n"
//                + "  }\n"
//                + "  FILTER (bound(?s))\n"
//                + "  ?r rdfs:label ?l.\n"
//                + "}";

        // matching keywords to ontology's labels
        String query = "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX pf:   <http://jena.hpl.hp.com/ARQ/property#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?matchUri a queries:Matching;\n"
                + "               queries:matchingHasKeyword ?keyword;\n"
                + "               queries:matchingHasResource ?r;\n"
                + "               queries:matchingHasScore ?s;\n"
                + "               queries:matchingHasMatchedLabel ?l.\n"
                + "   ?keyword queries:keywordAlreadyMatched \"true\"^^xsd:boolean.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "{\n"
                + "SELECT DISTINCT * WHERE\n" // subquery with DISTINCT to solve unidentified problem
                + "{\n" // apparently due to larq
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    <" + queryUri + "> queries:queryHasQueryElement ?keyword.\n"
                + "    ?keyword a queries:KeywordQueryElement;\n"
                + "             queries:queryElementHasValue ?keywordValue.\n"
                + "    FILTER NOT EXISTS { ?keyword queries:keywordAlreadyMatched \"true\"^^xsd:boolean. }\n"
                + "  }\n"
                + "  GRAPH <<GRAPH>>\n"
                + "  {\n"
                + "    (?l ?score) pf:textMatch (?keywordValue 6.0 5).\n"
                + "    ?r rdfs:label ?l.\n"
                + "    BIND ((?score * <<COEF>>) AS ?s)\n"
                + "  }\n"
                + "  }\n"
                + "  }\n"
                + "  BIND (UUID() AS ?matchUri)\n"
                //                + "  BIND (URI(CONCAT(\""
                //                    + queryUri + "\",\"/MATCHING_\", "
                //                    + "replace("
                //                        + "replace("
                //                            + "concat( STR(?keyword), \"_\", STR(?r)), "
                //                            + "\"http://swip.univ-tlse2.fr:8080/musicbrainz/\","
                //                            + " \"\"), "
                //                        + "\"http://\", " 
                //                        + " \"\"))) "
                //                        + "AS ?matchUri)\n"
                + "}";

        String ontQuery = query.replaceAll("<<GRAPH>>", "graph:ontologies").replaceAll("<<COEF>>", "4");
        logger.info(ontQuery);
        if (commitUpdate) {
            sparqlServer.update(ontQuery);
        }

        // matching keywords to instances's labels
        String instQuery = query.replaceAll("<<GRAPH>>", "graph:instances").replaceAll("<<COEF>>", "1");
        logger.info(instQuery);
        if (commitUpdate) {
            sparqlServer.update(instQuery);
        }

        // matching literals
        // TODO
    }

    /**
     * this step could be simply done using a reasoner and property chain axioms
     * @param queryUri
     * @param sparqlServer 
     */
    private void performElementMapping(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, "PerformingElementMapping");

        // mapping KB pattern elements to keywords
        String query = "# mapping KB pattern elements to keywords\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?emUri a queries:ElementMapping;\n"
                + "           queries:emHasPatternElement ?pe;\n"
                + "           queries:mappingHasPatternConstituent ?pe;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasMatching ?matching;\n"
                + "           queries:mappingHasQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    <" + queryUri + "> queries:queryHasQueryElement ?keyword.\n"
                + "    ?matching queries:matchingHasKeyword ?keyword;\n"
                + "              queries:matchingHasResource ?r.\n"
                + "  }\n"
                + "  GRAPH graph:patterns\n"
                + "  {\n"
                + "    ?p patterns:patternHasPatternElement ?pe.\n"
                + "    ?pe patterns:targetsKBElement ?kbe.\n"
                + "  }\n"
                + "  {\n"
                + "    GRAPH graph:ontologies\n"
                + "    {\n"
                + "      ?r (rdfs:subClassOf | rdfs:subPropertyOf) ?kbe.\n"
                + "    }\n"
                + "  }\n"
                + "  UNION\n"
                + "  {\n"
                + "    GRAPH graph:instances\n"
                + "    {\n"
                + "      ?r a ?c.\n"
                + "    }\n"
                + "    GRAPH graph:ontologies\n"
                + "    {\n"
                + "      ?c rdfs:subClassOf ?kbe.\n"
                + "    }\n"
                + "  }\n"
                + "  BIND (UUID() AS ?emUri)\n"
                //                + "  BIND (URI(CONCAT(\""
                //                    + queryUri + "\",\"/ELEMENT_MAPPING_\", "
                //                    + "replace("
                //                        + "replace("
                //                            + "concat( STR(?pe), \"_\", STR(?matching)), "
                //                            + "\"http://swip.univ-tlse2.fr:8080/musicbrainz/\","
                //                            + " \"\"), "
                //                        + "\"http://\", " 
                //                        + " \"\"))) "
                //                        + "AS ?emUri)\n"
                + "}";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }

        // add an empty mapping to all pattern elements
        query = "# add an empty mapping to all pattern elements\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pe queries:isNotMappedToQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?emUri a queries:EmptyElementMapping;\n"
                + "           a queries:ElementMapping;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasPatternElement ?pe;\n"
                + "           queries:mappingHasPatternConstituent ?pe;\n" // we don't use any reasonner in queries graph
                + "           queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    ?pe queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH graph:patterns\n"
                + "  {\n"
                + "    ?pe a patterns:PatternElement.\n"
                + "  }\n"
                + "  BIND (UUID() AS ?emUri)\n"
                //                + "  BIND (URI(CONCAT(\""
                //                    + queryUri + "\",\"/EMPTY_ELEMENT_MAPPING_\", "
                //                    + "replace("
                //                        + "replace("
                //                            + "STR(?pe), "
                //                            + "\"http://swip.univ-tlse2.fr:8080/musicbrainz/\","
                //                            + " \"\"), "
                //                        + "\"http://\", " 
                //                        + " \"\"))) "
                //                        + "AS ?emUri)\n"
                + "}";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }

        // TODO: handle literals

    }

    private void performTripleMapping(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, "PerformingPatternTripleInstanciation");

        // 
//        String query = "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
//                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
//                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
//                + "DELETE\n"
//                + "{\n"
//                + "  GRAPH graph:queries\n"
//                + "  {\n"
//                + "    ?pt queries:notYetMappedToQuery <" + queryUri + ">.\n"
//                + "  }\n"
//                + "}\n"
//                + "INSERT\n"
//                + "{\n"
//                + "  GRAPH graph:queries\n"
//                + "  {\n"
//                + "    _:ptm a queries:PatternTripleMapping;\n"
//                + "          queries:hasElementMapping ?emSubj, ?emObj, ?emProp;\n"
//                + "          queries:hasPatternTriple ?pt;\n"
//                + "          queries:hasQuery <" + queryUri + ">.\n"
//                + "  }\n"
//                + "}\n"
//                + "WHERE\n"
//                + "{\n"
//                + "  GRAPH graph:patterns\n"
//                + "  {\n"
//                + "    ?pt a patterns:PatternTriple;\n"
//                + "        patterns:hasSubject ?subj;\n"
//                + "        patterns:hasObject ?obj;\n"
//                + "        patterns:hasProperty ?prop.\n"
//                + "  }\n"
////                + "  OPTIONAL\n"
////                + "  {\n"
//                + "    GRAPH graph:queries\n"
//                + "    {\n"
//                + "      ?emSubj queries:emHasQuery <" + queryUri + ">;\n"
//                + "              queries:emHasPatternElement ?subj.\n"
//                + "    }\n"
////                + "  }\n"
////                + "  OPTIONAL\n"
////                + "  {\n"
//                + "    GRAPH graph:queries\n"
//                + "    {\n"
//                + "      ?emObj queries:emHasQuery <" + queryUri + ">;\n"
//                + "             queries:emHasPatternElement ?obj.\n"
//                + "    }\n"
////                + "  }\n"
////                + "  OPTIONAL\n"
////                + "  {\n"
//                + "    GRAPH graph:queries\n"
//                + "    {\n"
//                + "      ?emProp queries:emHasQuery <" + queryUri + ">;\n"
//                + "              queries:emHasPatternElement ?prop.\n"
//                + "    }\n"
////                + "  }\n"
//                + "}";
//
//        logger.info(query);
//        if (commitUpdate) {
//            sparqlServer.update(query);
//        }
    }

    private void performSpCollectionMapping(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, "PerformingSpCollectionMapping");

        int iteration = 0;
        while (!allPatternsAreMapped(queryUri, sparqlServer)) {
//        commitUpdate = false;
//        for (int i = 0; i < 2; i++) {
            logger.info("iteration " + ++iteration);
            if (iteration > 1) {
                combineMappings(queryUri, sparqlServer, commitUpdate);
                addEmptyMapping(queryUri, sparqlServer, commitUpdate);
            }
            startMapping(queryUri, sparqlServer, commitUpdate);
            preventFrom(queryUri, sparqlServer, commitUpdate);
            for (int j = 0; j < 5; j++) {
                makeProgress(queryUri, sparqlServer, commitUpdate);
            }
            validateMappings(queryUri, sparqlServer, commitUpdate);
        }

    }

    private boolean allPatternsAreMapped(String queryUri, RemoteSparqlServer sparqlServer) {
        logger.info("are all patterns mapped to the current query?");
        String query = "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "ASK\n"
                + "{\n"
                + "  GRAPH graph:patterns\n"
                + "  {\n"
                + "    ?p a patterns:Pattern.\n"
                + "  }\n"
                + "  FILTER NOT EXISTS\n"
                + "  {\n"
                + "    GRAPH graph:queries { ?p queries:toConsiderInMappingQuery <" + queryUri + ">. }\n"
                + "  }\n"
                + "}";

        logger.info(query);
        boolean result = !sparqlServer.ask(query);
        logger.info("-> " + result);
        return result;
    }

    /**
     * combine mappings of repeatable subpattern collections
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void combineMappings(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {

        String query = "# combine mappings of repeatable subpattern collections\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?mappingUri a queries:SubpatternCollectionMapping;\n"
                + "                queries:mappingHasPatternConstituent ?spc;\n"
                + "                queries:mappingContainsMapping ?m1, ?m2;\n"
                + "                queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    ?spc queries:allreadyCombinedForQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH graph:patterns\n"
                + "  {\n"
                + "    SELECT DISTINCT ?spc WHERE\n"
                + "    {\n"
                + "      ?spc patterns:hasCardinalityMax \"2\"^^xsd:int.\n"
                + "    }\n"
                + "  }\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?spc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    FILTER NOT EXISTS\n"
                + "    {\n"
                + "      ?spc queries:allreadyCombinedForQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "    ?m1 queries:mappingHasPatternConstituent ?spc.\n"
                + "    ?m2 queries:mappingHasPatternConstituent ?spc.\n"
                + "    FILTER ( STR(?m1) < STR(?m2) )\n"
                + "  }\n"
                + "  BIND (UUID() AS ?mappingUri)\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }

    /**
     * add an empty mapping to ContingentSubpatternCollections to be considered in next mappings
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void addEmptyMapping(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {

        String query = "# add an empty mapping to ContingentSubpatternCollections to be considered in next mappings\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?mappingUri a queries:EmptySubpatternCollectionMapping;\n"
                + "                a queries:SubpatternCollectionMapping;\n" // we don't use any reasonner in queries graph
                + "                queries:mappingHasPatternConstituent ?spc;\n"
                + "                queries:mappingHasQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE # select all ContingentSubpatternCollections which don't have yet an EmptySubpatternCollectionMapping\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?spc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    FILTER NOT EXISTS\n"
                + "    {\n"
                + "      ?mapping a queries:EmptySubpatternCollectionMapping;\n"
                + "                queries:mappingHasPatternConstituent ?spc;\n"
                + "                queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "  }\n"
                + "  GRAPH graph:patterns\n"
                + "  {\n"
                + "    SELECT DISTINCT ?spc WHERE\n"
                + "    {\n"
                + "      ?spc a patterns:SubpatternCollection;\n"
                + "           patterns:hasCardinalityMin \"0\"^^xsd:int.\n"
                + "    }\n"
                + "  }\n"
                + "  BIND (UUID() AS ?mappingUri)\n"
                + "}";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }

    /**
     * start mapping of SubpatternCollections whose all contained components are already mapped
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void startMapping(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {

        String query = "# start mapping of SubpatternCollections whose all contained components are already mapped\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?spc queries:isNotMappedToQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  {\n"
                + "    # get all SubpatternCollections which are not mapped but whose contained SubpatternCollections are 'to be considered'\n"
                + "    SELECT ?spc WHERE\n"
                + "    {\n"
                + "      # all SubpatternCollections\n"
                + "      GRAPH graph:patterns\n"
                + "      {\n"
                + "        ?spc a patterns:SubpatternCollection.\n"
                + "      }\n"
                + "      # which are not mapped\n"
                + "      GRAPH graph:queries\n"
                + "      {\n"
                + "        ?spc queries:isNotMappedToQuery <" + queryUri + ">.\n"
                + "      }\n"
                + "      # whose contained SubpatternCollections are all 'to be considered'\n"
                + "      MINUS\n"
                + "      {\n"
                + "        # get all SubpatternCollections whose at least one contained SubpatternCollection is being mapped not mapped\n"
                + "        SELECT ?spc WHERE\n"
                + "        {\n"
                + "          GRAPH graph:patterns\n"
                + "          {\n"
                + "            ?spc a patterns:SubpatternCollection;\n"
                + "                 patterns:hasDirectSubpattern ?spc2.\n"
                + "            ?spc2 a patterns:SubpatternCollection;\n"
                + "          }\n"
                + "          GRAPH graph:queries\n"
                + "          {\n"
                + "            ?spc2 (queries:isNotMappedToQuery | queries:isBeingMappedToQuery) <" + queryUri + ">.\n"
                + "          }\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }

    /**
     * prevent from ...
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void preventFrom(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {

        String query = "# prevent from ...\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pc queries:toConsiderNextStepInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  SELECT DISTINCT ?pc WHERE\n"
                + "  {\n"
                + "    GRAPH graph:queries\n"
                + "    {\n"
                + "      ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "      ?pc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "    GRAPH graph:patterns\n"
                + "    {\n"
                + "      ?spc patterns:isMadeUpOf ?pc.\n"
                + "      OPTIONAL\n"
                + "      {\n"
                + "        ?spc2 patterns:isMadeUpOf ?pc.\n"
                + "        FILTER ( !sameTerm(?spc, ?spc2) )\n"
                + "      }\n"
                + "      FILTER NOT EXISTS\n"
                + "      {\n"
                + "        { ?spc patterns:isMadeUpOf ?spc2. }\n"
                + "        UNION\n"
                + "        { ?spc2 patterns:isMadeUpOf ?spc. }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }

    /**
     * make progress the mappings of currently processed SubpatternCollections
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void makeProgress(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {

        String query = "# make progress the mappings of currently processed SubpatternCollections\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    ?mPc queries:mappingHasPatternConstituent ?pc.\n"
                + "    ?mSpc queries:mappingHasPatternConstituent ?spc.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pc queries:isMappedToQuery <" + queryUri + ">.\n"
                + "    ?mappingUri a queries:SubpatternCollectionMapping;\n"
                + "                queries:mappingHasPatternConstituent ?spc;\n"
                + "                queries:mappingContainsMapping ?mSpc, ?mPc;\n"
                + "                queries:mappingHasQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  {\n"
                + "    # select a pair (?spc, ?pc) such as\n"
                + "    #  - ?spc is a SubpatternCollection made of ?pc\n"
                + "    #  - ?spc is being mapped\n"
                + "    #  - ?pc is to be considered\n"
                + "    SELECT ?spc ?pc WHERE\n"
                + "    {\n"
                + "      GRAPH graph:queries\n"
                + "      {\n"
                + "        ?pc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "        ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "      }\n"
                + "      GRAPH graph:patterns\n"
                + "      {\n"
                + "        ?spc patterns:isMadeUpOf ?pc.\n"
                + "      }\n"
                + "    } ORDER BY ?spc ?pc LIMIT 1\n"
                + "  }\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    OPTIONAL\n"
                + "    {\n"
                + "      ?mSpc queries:mappingHasPatternConstituent ?spc;\n"
                + "            queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "    ?mPc queries:mappingHasPatternConstituent ?pc;\n"
                + "         queries:mappingHasQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "  BIND (UUID() AS ?mappingUri)\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }

    /**
     * validate mappings of SubpatternCollections whose...
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void validateMappings(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {
        // 
        String query = "# validate mappings of SubpatternCollections whose...\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "    ?comp queries:toConsiderNextStepInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?spc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    ?comp queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  {\n"
                + "    # get all SubpatternCollections which are being mapped and whose contained SubpatternCollections are all mapped or to be mapped in next step\n"
                + "    SELECT ?spc WHERE\n"
                + "    {\n"
                + "      # all SubpatternCollections\n"
                + "      GRAPH graph:patterns\n"
                + "      {\n"
                + "        ?spc a patterns:SubpatternCollection.\n"
                + "      }\n"
                + "      # which are being mapped\n"
                + "      GRAPH graph:queries\n"
                + "      {\n"
                + "        ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "      }\n"
                + "      # whose contained SubpatternCollections are all mapped\n"
                + "      MINUS\n"
                + "      {\n"
                + "        # get all SubpatternCollections whose at least one contained component is not mapped\n"
                + "        SELECT DISTINCT ?spc WHERE\n"
                + "        {\n"
                + "          GRAPH graph:patterns\n"
                + "          {\n"
                + "            ?spc a patterns:SubpatternCollection;\n"
                + "                 patterns:isMadeUpOf ?comp.\n"
                + "          }\n"
                + "          GRAPH graph:queries\n"
                + "          {\n"
                + "            ?comp queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "          }\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "  OPTIONAL\n"
                + "  {\n"
                + "    GRAPH graph:patterns\n"
                + "    {\n"
                + "      ?spc patterns:isMadeUpOf ?comp.\n"
                + "    }\n"
                + "    GRAPH graph:queries\n"
                + "    {\n"
                + "      ?comp queries:toConsiderNextStepInMappingQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "  }\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }

    /**
     * 
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void performPatternMapping(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, "PerformingPatternMapping");

        String query = "# perform pattern mapping\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?p queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?p queries:isMappedToQuery <" + queryUri + ">.\n"
                + "    ?pm a queries:PatternMapping.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pm queries:mappingHasPatternConstituent ?p;\n"
                + "        queries:mappingHasQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "  GRAPH graph:patterns\n"
                + "  {\n"
                + "    ?p a patterns:Pattern.\n"
                + "  }\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }

    private void performMappingRanking(String queryUri, RemoteSparqlServer sparqlServer, boolean commitUpdate) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, "PerformingMappingRanking");

        String query = "# process the element mapping relevance mark\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pm queries:hasEmrMark ?avgscore;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  SELECT ?pm (AVG(?score) AS ?avgscore) WHERE\n"
                + "    {\n"
                + "    GRAPH graph:queries\n"
                + "    {\n"
                + "      ?pm a queries:PatternMapping;\n"
                + "          queries:mappingHasQuery <" + queryUri + ">;\n"
                + "          queries:mappingContainsMapping+ ?em.\n"
                + "      ?em (queries:emHasMatching / queries:matchingHasScore) ?score.\n"
                + "    }\n"
                + "  } GROUP BY ?pm\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }

        query = "# process the query coverage relevance mark\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pm queries:hasQcrMark ?qcrmark;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  {\n"
                + "    SELECT (COUNT(?qe) AS ?nbqe) WHERE\n"
                + "    {\n"
                + "      GRAPH graph:queries\n"
                + "      {\n"
                + "        <" + queryUri + "> queries:queryHasQueryElement ?qe.\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "  {\n"
                + "    SELECT ?pm (COUNT(DISTINCT ?qe) AS ?nbmappedqe) WHERE\n"
                + "    {\n"
                + "      GRAPH graph:queries\n"
                + "      {\n"
                + "        ?pm a queries:PatternMapping;\n"
                + "            queries:mappingHasQuery <" + queryUri + ">;\n"
                + "            queries:mappingContainsMapping+ ?em.\n"
                + "        ?em (queries:emHasMatching / queries:matchingHasKeyword) ?qe.\n"
                + "      }\n"
                + "    } GROUP BY ?pm\n"
                + "  }\n"
                + "  BIND ( ?nbmappedqe / ?nbqe AS ?qcrmark )\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }

        query = "# process the pattern coverage relevance mark\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pm queries:hasPcrMark ?pcrmark;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  {\n"
                + "    SELECT ?p (COUNT(DISTINCT ?pe) AS ?nbpe) WHERE\n"
                + "    {\n"
                + "      GRAPH graph:patterns\n"
                + "      {\n"
                + "        ?p a patterns:Pattern;\n"
                + "           patterns:patternHasPatternElement ?pe\n"
                + "      }\n"
                + "    } GROUP BY ?p \n"
                + "  }\n"
                + "  {\n"
                + "    SELECT ?pm ?p (COUNT(DISTINCT ?pe) AS ?nbmappedpe) WHERE\n"
                + "    {\n"
                + "      GRAPH graph:queries\n"
                + "      {\n"
                + "        ?pm a queries:PatternMapping;\n"
                + "            queries:mappingHasQuery <" + queryUri + ">;\n"
                + "            queries:mappingHasPatternConstituent ?p;\n"
                + "            queries:mappingContainsMapping+ ?em.\n"
                + "        ?em queries:emHasPatternElement ?pe.\n"
                + "      }\n"
                + "    } GROUP BY ?p ?pm\n"
                + "  }\n"
                + "  BIND ( ?nbmappedpe / ?nbpe AS ?pcrmark )\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }

        query = "# process final relevance mark\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX graph:   <http://swip.univ-tlse2.fr:8080/musicbrainz/>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pm queries:hasRelevanceMark ?rmark;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH graph:queries\n"
                + "  {\n"
                + "    ?pm a queries:PatternMapping;\n"
                + "        queries:mappingHasQuery <" + queryUri + ">;\n"
                + "        queries:hasEmrMark ?emrmark;\n"
                + "        queries:hasQcrMark ?qcrmark;\n"
                + "        queries:hasPcrMark ?pcrmark.\n"
                + "  }\n"
                + "  BIND ( ?emrmark * ?qcrmark * ?pcrmark AS ?rmark )\n"
                + "}\n";

        logger.info(query);
        if (commitUpdate) {
            sparqlServer.update(query);
        }
        
        
    }
}
