package org.swip.pivotToMappings;

import org.apache.log4j.Logger;
import org.swip.utils.sparql.SparqlClient;

public class QueryInterpreter extends Thread {

    private static final Logger logger = Logger.getLogger(QueryInterpreter.class);
    String queryUri = null;
    SparqlClient sparqlClient = null;
    boolean useFederatedSparql = false;
    boolean useLarq = true;
    String larqParams = null;
    String kbLocation = null;
    String queriesNamedGraphUri = null;
    String patternsNamedGraphUri = null;
    int numMappings = 0;

    public QueryInterpreter(String queryUri, SparqlClient sparqlServer, boolean useFederatedSparql, boolean useLarq, String larqParams, String kbLocation, String queriesUri, String patternsUri, int numMappings) {
        this.queryUri = queryUri;
        this.sparqlClient = sparqlServer;
        this.useFederatedSparql = useFederatedSparql;
        this.useLarq = useLarq;
        this.larqParams = (larqParams.equals("")? "8.0" : larqParams);
        this.kbLocation = kbLocation;
        this.queriesNamedGraphUri = queriesUri;
        this.patternsNamedGraphUri = patternsUri;
        this.numMappings = numMappings;
    }

    @Override
    public void run() {
        boolean commitUpdate = true;
        boolean logQuery = false;
        logger.info("================================================================");
        logger.info("Matching query elements to knowledge base elements:");
        logger.info("---------------------------------------------------\n");
        performMatching(queryUri, sparqlClient, commitUpdate, logQuery);
//        commitUpdate = false;
        logger.info("================================================================");
        logger.info("Mapping patterns elements:");
        logger.info("--------------------------\n");
        performElementMapping(queryUri, sparqlClient, commitUpdate, logQuery);
        logger.info("================================================================");
        logger.info("Mapping subpattern collections:");
        logger.info("------------------------------\n");
        performSpCollectionMapping(queryUri, sparqlClient, commitUpdate, logQuery);
        logger.info("================================================================");
        logger.info("Mapping patterns:");
        logger.info("----------------\n");
        performPatternMapping(queryUri, sparqlClient, commitUpdate, logQuery);
        logger.info("================================================================");
        logger.info("Ranking pattern mappings:");
        logger.info("------------------------\n");
        performMappingRanking(queryUri, sparqlClient, commitUpdate, logQuery);
        logger.info("================================================================");
        logger.info("Clearing bad mappings:");
        logger.info("---------------------\n");
        clearMappings(queryUri, sparqlClient, commitUpdate, logQuery);
        logger.info("================================================================");
        logger.info("Clearing KB:");
        logger.info("-----------\n");
        clearKB(queryUri, sparqlClient, commitUpdate, logQuery);
        logger.info("================================================================");
        logger.info("Generate descriptive sentences:");
        logger.info("------------------------------\n");
        generateDescriptiveSentences(queryUri, sparqlClient, commitUpdate, logQuery);
        logger.info("================================================================");
        logger.info("Generate SPARQL queries:");
        logger.info("-----------------------\n");
        generateSparqlQueries(queryUri, sparqlClient, commitUpdate, logQuery);
        logger.info("================================================================");
        logger.info("Done!");
        logger.info("-----\n");
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlClient, queriesNamedGraphUri, "QueryProcessed");
    }

    private void performMatching(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingMatching");

        String query = "# matching keywords to KB labels\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX pf:   <http://jena.hpl.hp.com/ARQ/property#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX bif:  <http://bif.org/>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
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
                + "  {\n"
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        <" + queryUri + "> queries:queryHasQueryElement ?keyword.\n"
                + "        ?keyword a queries:KeywordQueryElement;\n"
                + "                 queries:queryElementHasValue ?keywordValue.\n"
                + "        FILTER NOT EXISTS { ?keyword queries:keywordAlreadyMatched \"true\"^^xsd:boolean. }\n"
                + "      }\n"
//                + "      {\n"
//                + "    # subquery with DISTINCT to solve unidentified problem apparently due to larq\n"
//                + "    SELECT DISTINCT * WHERE\n"
//                + "    {\n"
                + "        " + ((useFederatedSparql) ? "SERVICE" : "GRAPH") + " <" + kbLocation + ">\n";
        if (useLarq) {
            query += "        {\n"
                    + "          (?l ?score) pf:textMatch (?keywordValue " + larqParams + ").\n"
                    + "          ?r rdfs:label ?l.\n"
                    + "          #FILTER(REGEX(?l, ?keywordValue, 'i'))\n"
                    + "        }\n"
                    + "        BIND ((?score * 1) AS ?s)\n";
        } else {
            query += "        {\n"
                    + "          ?r rdfs:label ?l.\n"
                    + "          #FILTER (CONTAINS(STR(?l), STR(?keywordValue)))\n"
                    + "          FILTER(REGEX(?l, ?keywordValue, 'i'))\n"
                    + "          #FILTER(bif:contains(?l, '\"beatles with\"'))\n"
                    + "        }\n"
                    + "        BIND (10 AS ?s)\n";
        }
        query += "      }\n"
//                + "    }\n"
//                + "  }\n"
                + "  BIND (IRI(CONCAT(\"" + queriesNamedGraphUri + "#matching-\", REPLACE(?keywordValue, \" \", \"_\", \"i\"), \"-to-\", REPLACE(?l, \" \", \"_\", \"i\"))) AS ?matchUri)\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    /**
     * this step could be simply done using a reasoner and property chain axioms
     * @param queryUri
     * @param sparqlServer 
     */
    private void performElementMapping(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingElementMapping");
        logger.info(" - mappingKbPeToKeywords");
        mappingKbPeToKeywords(queryUri, sparqlServer, commitUpdate, logQuery);
        logger.info(" - removeRedundantEm");
        removeRedundantEm(queryUri, sparqlServer, commitUpdate, logQuery);
        logger.info(" - findOutInstanceClassMappings");
        findOutInstanceClassMappings(queryUri, sparqlServer, commitUpdate, logQuery);
        logger.info(" - addEmptyEm");
        addEmptyEm(queryUri, sparqlServer, commitUpdate, logQuery);
        logger.info(" - mappingLiterals");
        mappingLiterals(queryUri, sparqlServer, commitUpdate, logQuery);
    }

    private void mappingKbPeToKeywords(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

//        // mapping KB pattern elements to keywords
//        String query = "# mapping KB pattern elements to keywords\n"
//                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
//                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
//                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
//                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
//                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n"
//                + "INSERT\n"
//                + "{\n"
//                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
//                + "  {\n"
//                + "    ?emUri a queries:KeywordMapping;\n"
//                + "           a queries:ElementMapping;\n" // we don't use any reasonner in queries graph
//                + "           queries:emHasPatternElement ?pe;\n"
//                + "           queries:mappingHasPatternConstituent ?pe;\n" // we don't use any reasonner in queries graph
//                + "           queries:emHasMatching ?matching;\n"
//                + "           queries:emHasQueryElement ?keyword;\n"
//                + "           queries:emHasKeyword ?keyword;\n" // we don't use any reasonner in queries graph
//                + "           queries:emHasMatchedLabel ?l;\n"
//                + "           queries:emHasResource ?r;\n"
//                + "           queries:emHasScore ?score;\n"
//                + "           queries:mappingHasQuery <" + queryUri + ">.\n"
//                + "    # generated NL\n"
//                + "    ?emUri queries:hasDescriptiveSubsentence ?descSubsentUri.\n"
//                + "    ?descSubsentUri a queries:DescriptiveSubsentence;\n"
//                + "                   queries:dsIsQueried ?queried;\n"
//                + "                   queries:hasStringValue ?stringValue.\n"
//                + "    # generated SPARQL\n"
//                + "    ?emUri queries:hasTriple ?tripleUri1, ?tripleUri2.\n"
//                + "     ?tripleUri1 sp:subject  ?s1 ;\n"
//                + "                sp:predicate ?p1 ;\n"
//                + "                sp:object    ?o1 .\n"
//                + "     ?tripleUri2 sp:subject  ?s2 ;\n"
//                + "                sp:predicate ?p2 ;\n"
//                + "                sp:object    ?o2 .\n"
//                + "  }\n"
//                + "}\n"
//                + "WHERE\n"
//                + "{\n"
//                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
//                + "  {\n"
//                + "    <" + queryUri + "> queries:queryHasQueryElement ?keyword.\n"
//                + "    ?matching queries:matchingHasKeyword ?keyword;\n"
//                + "              queries:matchingHasResource ?r;\n"
//                + "              queries:matchingHasScore ?score;\n"
//                + "              queries:matchingHasMatchedLabel ?l.\n"
//                + "    OPTIONAL\n"
//                + "    {\n"
//                + "      ?keyword queries:queryElementIsQueried ?queried.\n"
//                + "    }\n"
//                + "  }\n"
//                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
//                + "  {\n"
//                + "    ?p patterns:patternHasPatternElement ?pe.\n"
//                + "    ?pe patterns:isQualifying \"true\"^^xsd:boolean;\n"
//                + "        patterns:targetsKBElement ?kbe.\n"
//                + "  }\n"
//                + "  BIND (UUID() AS ?emUri) # needed in the following\n"
//                + "  # class\n"
//                + "  {\n"
//                + "    GRAPH <" + kbLocation + ">\n"
//                + "    {\n"
//                + "      {?r a owl:Class.} UNION {?r a rdfs:Class.}\n"
//                + "      ?r rdfs:subClassOf* ?kbe.\n"
//                + "      BIND (\"some \" AS ?stringValuePrefix)\n"
//                + "    }\n"
//                + "  }\n"
//                + "  UNION\n"
//                + "  # pseudo class (*Type properties)\n"
//                + "  {\n"
//                + "    GRAPH <" + queriesNamedGraphUri + ">\n"
//                + "    {\n"
//                + "      ?jtp a queries:JokerTypeProperty;\n"
//                + "           queries:concernsProperty ?prop;\n"
//                + "           queries:hasClass ?kbe;\n"
//                + "           queries:hasPseudoClass ?r.\n"
//                + "      BIND (\"*some \" AS ?stringValuePrefix)\n"
//                + "    }\n"
//                + "  }\n"
//                + "  UNION\n"
//                + "  # property\n"
//                + "  {\n"
//                + "    GRAPH <" + kbLocation + ">\n"
//                + "    {\n"
//                + "      {?r a owl:ObjectProperty.} UNION {?r a owl:DatatypeProperty.}\n"
//                + "      ?r rdfs:subPropertyOf* ?kbe.\n"
//                + "      BIND (\"\" AS ?stringValuePrefix)\n"
//                + "    }\n"
//                + "  }\n"
//                + "  UNION\n"
//                + "  # instance\n"
//                + "  {\n"
//                + "    GRAPH <" + kbLocation + ">\n"
//                + "    {\n"
//                + "      ?r a ?c.\n"
//                + "      ?c rdfs:subClassOf* ?kbe.\n"
//                + "      # generated NL\n"
//                + "      BIND (\"\" AS ?stringValuePrefix)\n"
//                + "      # generated SPARQL\n"
//                + "      BIND (IRI(CONCAT(str(?emUri), \"_impliedTriple1\")) AS ?tripleUri1)\n"
//                + "      BIND (IRI(CONCAT(str(?emUri), \"_impliedTriple2\")) AS ?tripleUri2)\n"
//                + "      BIND (CONCAT (\"?\", REPLACE(?kwValue, \" \", \"_\", \"i\")) AS ?rep)\n"
//                + "      BIND (?rep AS ?s1)\n"
//                + "      BIND (rdfs:label AS ?p1)\n"
//                + "      BIND (?l AS ?o1)\n"
//                + "      BIND (?rep AS ?s2)\n"
//                + "      BIND (rdf:type AS ?p2)\n"
//                + "      BIND (?targetedClass AS ?o2)\n"
//                
//                + "    }\n"
//                + "  }\n"
//                + "  BIND (IRI(CONCAT(str(?emUri), \"_descSubsent\")) AS ?descSubsentUri)\n"
//                + "  BIND (if (BOUND(?stringValuePrefix), CONCAT(?stringValuePrefix, ?l), STR(?l)) AS ?stringValue)\n"
//                + "}";
//
//        if (logQuery) {
//            logger.info(query);
//        }
//        if (commitUpdate) {
//            sparqlServer.update(query);
//        }
        String queryFrame = "# mapping KB pattern elements to keywords\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n"
                + "prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix sp:  <http://spinrdf.org/sp>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?emUri a queries:KeywordMapping;\n"
                + "           a queries:ElementMapping;\n" // we don't use any reasonner in queries graph
                + "           a ?type;\n"
                + "           queries:emHasPatternElement ?pe;\n"
                + "           queries:mappingHasPatternConstituent ?pe;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasMatching ?matching;\n"
                + "           queries:emHasQueryElement ?keyword;\n"
                + "           queries:emHasKeyword ?keyword;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasMatchedLabel ?l;\n"
                + "           queries:emHasResource ?r;\n"
                + "           queries:emHasScore ?score;\n"
                + "           queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    # generated NL\n"
                + "    ?emUri queries:hasDescriptiveSubsentence ?descSubsentUri.\n"
                + "    ?descSubsentUri a queries:DescriptiveSubsentence;\n"
                + "                   queries:dsIsQueried ?queried;\n"
                + "                   queries:hasStringValue ?stringValue.\n"
                + "    # generated SPARQL\n"
                + "    ?emUri queries:hasTriple ?tripleUri1, ?tripleUri2;\n"
                + "           queries:hasSparqlRepresentation ?rep.\n"
                + "    ?tripleUri1 sp:subject  ?s1 ;\n"
                + "                sp:predicate ?p1 ;\n"
                + "                sp:object    ?o1 .\n"
                + "    ?tripleUri2 sp:subject  ?s2 ;\n"
                + "                sp:predicate ?p2 ;\n"
                + "                sp:object    ?o2 .\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    <" + queryUri + "> queries:queryHasQueryElement ?keyword.\n"
                + "    ?matching queries:matchingHasKeyword ?keyword;\n"
                + "              queries:matchingHasResource ?r;\n"
                + "              queries:matchingHasScore ?score;\n"
                + "              queries:matchingHasMatchedLabel ?l.\n"
                + "    ?keyword queries:queryElementHasValue ?kwValue.\n"
                + "    OPTIONAL\n"
                + "    {\n"
                + "      ?keyword queries:queryElementIsQueried ?queried.\n"
                + "    }\n"
                + "  }\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?p patterns:patternHasPatternElement ?pe.\n"
                + "    ?pe patterns:isQualifying \"true\"^^xsd:boolean;\n"
                + "        patterns:targetsKBElement ?kbe.\n"
                + "  }\n"
                + "  BIND (UUID() AS ?emUri) # needed in query inner\n"
                + "[QUERY_INNER]"
                + "  BIND (IRI(CONCAT(str(?emUri), \"_descSubsent\")) AS ?descSubsentUri)\n"
                + "  BIND (if (BOUND(?stringValuePrefix), CONCAT(?stringValuePrefix, ?l), STR(?l)) AS ?stringValue)\n"
                + "}";

        String queryInner = "  # -- class\n"
                + "    GRAPH <" + kbLocation + ">\n"
                + "    {\n"
                + "      {?r a owl:Class.} UNION {?r a rdfs:Class.}\n"
                + "      ?r rdfs:subClassOf* ?kbe.\n"
                + "    }\n"
                + "    BIND (queries:ClassElementMapping AS ?type)\n"
                + "    # generated NL\n"
                + "    BIND (\"some \" AS ?stringValuePrefix)\n"
                + "    # generated SPARQL\n"
                + "    BIND (IRI(CONCAT(str(?emUri), \"_impliedTriple\")) AS ?tripleUri1)\n"
                + "    BIND (CONCAT (\"?\", REPLACE(?kwValue, \" \", \"_\", \"i\")) AS ?rep)\n"
                + "    BIND (?rep AS ?s1) BIND (rdf:type AS ?p1) BIND (?r AS ?o1)\n"
                + "    # --\n";

        String query = "# classes\n" + queryFrame.replace("[QUERY_INNER]", queryInner);

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);

        queryInner = "  # -- pseudo class (*Type properties)\n"
                + "    GRAPH <" + queriesNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?jtp a queries:JokerTypeProperty;\n"
                + "           queries:concernsProperty ?prop;\n"
                + "           queries:hasPseudoClass ?r;\n"
                + "           queries:hasClass ?class.\n"
                + "    }\n"
                + "    GRAPH <" + kbLocation + ">\n"
                + "    {\n"
                + "      ?kbe rdfs:subClassOf* ?class.\n"
                + "    }\n"
                + "    BIND (queries:PseudoClassElementMapping AS ?type)\n"
                + "    # generated NL\n"
                + "    BIND (\"*some \" AS ?stringValuePrefix)\n"
                + "    # generated SPARQL\n"
                + "    BIND (IRI(CONCAT(str(?emUri), \"_impliedTriple\")) AS ?tripleUri1)\n"
                + "    BIND (CONCAT (\"?\", REPLACE(?kwValue, \" \", \"_\", \"i\")) AS ?rep)\n"
                + "    BIND (?rep AS ?s1) BIND (?prop AS ?p1) BIND (?r AS ?o1)\n"
                + "    # --\n";

        query = "# pseudo classes\n" + queryFrame.replace("[QUERY_INNER]", queryInner);

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);

        queryInner = "  # -- property\n"
                + "    GRAPH <" + patternsNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?pe a patterns:PropertyPatternElement.\n"
                + "    }\n"
                + "    GRAPH <" + kbLocation + ">\n"
                + "    {\n"
                + "      # devious way to check that we deal with a property because KB often forget to express that properties are properties\n"
                + "      #{\n"
                + "      #  SELECT ?subject WHERE{\n"
                + "          # to be considered as a property, a resource must either be declared or used as such\n"
                + "          # FIXME: the first two subqueries are made unusefull by following FILTER\n"
                + "          #      {?r a owl:ObjectProperty. BIND (\"plop\" AS ?subject)}\n"
                + "          #UNION {?r a owl:DatatypeProperty. BIND (\"plop\" AS ?subject)}\n"
                + "          #UNION {?subject ?r ?object}\n"
                + "      #  } LIMIT 1\n"
                + "      #}\n"
                + "      #FILTER ( BOUND(?subject) )\n"
                + "      #{{?r a owl:ObjectProperty.} UNION {?r a owl:DatatypeProperty.}\n"
                + "      #?r rdfs:subPropertyOf* ?kbe.}\n"
                + "      #UNION\n"
                + "      #{ ?r rdfs:subPropertyOf* ?kbe.\n"
                + "      #FILTER (sameTerm(?r, <http://purl.org/NET/c4dm/timeline.owl#duration>)) }\n"
                + "      ?r rdfs:subPropertyOf* ?kbe.\n"
                + "    }\n"
                + "    BIND (queries:PropertyElementMapping AS ?type)\n"
                + "    # generated NL\n"
                + "    BIND (\"\" AS ?stringValuePrefix)\n"
                + "    # generated SPARQL\n"
                + "    BIND (?r AS ?rep)\n"
                + "    # --\n";

        query = "# properties\n" + queryFrame.replace("[QUERY_INNER]", queryInner);

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);

        queryInner = "  # -- instance\n"
                + "    GRAPH <" + kbLocation + ">\n"
                + "    {\n"
                + "      ?r a ?c.\n"
                + "      ?c rdfs:subClassOf* ?kbe.\n"
                + "    }\n"
                + "    BIND (queries:InstanceElementMapping AS ?type)\n"
                + "    # generated NL\n"
                + "    BIND (\"\" AS ?stringValuePrefix)\n"
                + "    # generated SPARQL\n"
                + "    BIND (IRI(CONCAT(str(?emUri), \"_impliedTriple1\")) AS ?tripleUri1)\n"
                + "    BIND (IRI(CONCAT(str(?emUri), \"_impliedTriple2\")) AS ?tripleUri2)\n"
                + "    BIND (CONCAT (\"?\", REPLACE(?kwValue, \" \", \"_\", \"i\")) AS ?rep)\n"
                + "    BIND (?rep AS ?s1) BIND (rdfs:label AS ?p1) BIND (?l AS ?o1)\n"
                + "    BIND (?rep AS ?s2) BIND (rdf:type AS ?p2) BIND (?c AS ?o2)\n"
                + "    # --\n";

        query = "# instances\n" + queryFrame.replace("[QUERY_INNER]", queryInner);

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void removeRedundantEm(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        // remove "redundant" element mappings, i.e. mappings of same type having a same pattern element and distinct KB resources with same matched label
        // FIXME: only element mappings of te same type (class, pseudo-class, property, instance), or we have to keep each mapped resource, or something like that + delete generated SPARQL
        String query = "# remove \"redundant\" element mappings, i.e. mappings having a same pattern element and distinct KB resources with same matched label\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?em2 ?a ?b.\n"
                + "    ?descSubsentUri ?c ?d;\n"
                + "    # TODO: also delete generated SPARQL\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?em1 queries:emHasMatching ?matching;\n"
                + "         queries:emHasResource ?r.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?em1 a ?type;\n"
                + "         queries:mappingHasQuery <" + queryUri + ">;\n"
                + "         queries:emHasPatternElement ?pe;\n"
                + "         queries:emHasKeyword ?keyword;\n"
                + "         queries:emHasMatchedLabel ?l.\n"
                + "    FILTER (?type IN (queries:ClassElementMapping, queries:PseudoClassElementMapping, queries:PropertyElementMapping, queries:InstanceElementMapping))\n"
                + "    ?em2 a ?type;\n"
                + "         queries:mappingHasQuery <" + queryUri + ">;\n"
                + "         queries:emHasPatternElement ?pe;\n"
                + "         queries:emHasKeyword ?keyword;\n"
                + "         queries:emHasMatchedLabel ?l.\n"
                + "    FILTER (str(?em1) < str(?em2))\n"
                + "    # to add to em1\n"
                + "    ?em2 queries:emHasMatching ?matching;\n"
                + "         queries:emHasResource ?r.\n"
                + "    # to delete\n"
                + "    ?em2 ?a ?b;\n"
                + "         queries:hasDescriptiveSubsentence ?descSubsentUri.\n"
                + "    ?descSubsentUri ?c ?d;\n"
                + "    # TODO: also delete generated SPARQL\n"
                + "  }\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void findOutInstanceClassMappings(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        // find out instance/class mappings
        String query = "# find out instance/class mappings\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix sp:  <http://spinrdf.org/sp>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?emUri a queries:InstanceClassMapping;\n"
                + "           a queries:ElementMapping;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasPatternElement ?pe;\n"
                + "           queries:mappingHasPatternConstituent ?pe;\n" // we don't use any reasonner in queries graph
                + "           queries:mappingHasQuery <" + queryUri + ">;\n"
                + "           queries:hasInstanceMapping ?im;\n"
                + "           queries:hasClassMapping ?cm;\n"
                + "           queries:emHasScore ?score;\n"
                + "           queries:emHasKeyword ?iKeyword, ?cKeyword;\n"
                + "           queries:emHasQueryElement ?iKeyword, ?cKeyword;\n" // we don't use any reasonner in queries graph
                + "           queries:hasDescriptiveSubsentence ?descSubsentUri.\n"
                + "    ?descSubsentUri a queries:DescriptiveSubsentence;\n"
                + "                    queries:hasStringValue ?descSubsentValue.\n"
                + "    # generated SPARQL\n"
                + "    ?emUri queries:hasTriple ?tripleUri1, ?tripleUri2;\n"
                + "           queries:hasSparqlRepresentation ?rep.\n"
                + "    ?tripleUri1 sp:subject  ?s1 ;\n"
                + "                sp:predicate ?p1 ;\n"
                + "                sp:object    ?o1 .\n"
                + "    ?tripleUri2 sp:subject  ?s2 ;\n"
                + "                sp:predicate ?p2 ;\n"
                + "                sp:object    ?o2 .\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  {\n"
                + "    # subquery with distinct needed in case several resources were attached to an element mapping in removeRedundantEm\n"
                + "    # this subquery symply selects couple of elements mappings which can be combined to build an i/c mapping\n"
                + "    SELECT ?im ?cm ?pe ?p2 (SAMPLE(?cr) AS ?o2)\n"
                + "    {\n"
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        # instance mapping\n"
                + "        ?im a queries:ElementMapping;\n"
                + "            queries:mappingHasQuery <" + queryUri + ">;\n"
                + "            queries:emHasPatternElement ?pe;\n"
                + "            queries:emHasResource ?ir.\n"
                + "        # class mapping\n"
                + "        ?cm a queries:ElementMapping;\n"
                + "            queries:mappingHasQuery <" + queryUri + ">;\n"
                + "            queries:emHasPatternElement ?pe;\n"
                + "            queries:emHasResource ?cr.\n"
                + "      }\n"
                + "      {\n"
                + "        GRAPH <" + kbLocation + ">\n"
                + "        {\n"
                + "          ?ir a ?cr.\n"
                + "          BIND (rdf:type AS ?p2).\n"
                + "          #BIND (?cr AS ?o2).\n"
                + "        }\n"
                + "      }\n"
                + "      UNION\n"
                + "      {\n"
                + "        GRAPH <" + queriesNamedGraphUri + ">\n"
                + "        {\n"
                + "          ?jtp a queries:JokerTypeProperty;\n"
                + "               queries:concernsProperty ?pseudoType;\n"
                + "               queries:hasPseudoClass ?cr.\n"
                + "        }\n"
                + "        GRAPH <" + kbLocation + ">\n"
                + "        {\n"
                + "          ?ir ?pseudoType ?cr.\n"
                + "          BIND (?pseudoType AS ?p2).\n"
                + "          #BIND (?cr AS ?o2).\n"
                + "        }\n"
                + "      }\n"
                + "    } GROUP BY ?im ?cm ?pe ?p2\n"
                + "  }\n"
                + "  # the rest of the query gets some properties of the previously selected mappings\n"
                + "  # and build some variables usefull for the INSERT clause\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    # instance mapping\n"
                + "    ?im queries:emHasScore ?iScore;\n"
                + "        queries:emHasKeyword ?iKeyword;\n"
                + "        queries:emHasMatchedLabel ?l;\n"
                + "        (queries:hasDescriptiveSubsentence/queries:hasStringValue) ?iDescSubsentValue.\n"
                + "    ?iKeyword queries:queryElementHasValue ?kwValue.\n"
                + "    # class mapping\n"
                + "    ?cm queries:emHasScore ?cScore;\n"
                + "        queries:emHasKeyword ?cKeyword;\n"
                + "        queries:emHasMatchedLabel ?cMatchedLabel.\n"
                + "  }\n"
                + "  BIND (UUID() AS ?emUri)\n"
                + "  BIND (?iScore + ?cScore AS ?score)\n"
                + "  # generated NL\n"
                + "  BIND (IRI(CONCAT(str(?emUri), \"_descSubsent\")) AS ?descSubsentUri)\n"
                + "  BIND (CONCAT( ?iDescSubsentValue, \" (\", ?cMatchedLabel, \")\") AS ?descSubsentValue)\n"
                + "  # generated SPARQL\n"
                + "  BIND (IRI(CONCAT(str(?emUri), \"_impliedTriple1\")) AS ?tripleUri1)\n"
                + "  BIND (IRI(CONCAT(str(?emUri), \"_impliedTriple2\")) AS ?tripleUri2)\n"
                + "  BIND (CONCAT (\"?\", REPLACE(?kwValue, \" \", \"_\", \"i\")) AS ?rep)\n"
                + "  BIND (?rep AS ?s1) BIND (rdfs:label AS ?p1) BIND (?l AS ?o1)\n"
                + "  BIND (?rep AS ?s2)\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void addEmptyEm(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        // add an empty mapping to all qualifying pattern elements
        String query = "# add an empty mapping to all pattern elements\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix sp:  <http://spinrdf.org/sp>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?emUri a queries:EmptyElementMapping;\n"
                + "           a queries:ElementMapping;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasPatternElement ?pe;\n"
                + "           queries:mappingHasPatternConstituent ?pe;\n" // we don't use any reasonner in queries graph
                + "           queries:mappingHasQuery <" + queryUri + ">;\n"
                + "           queries:emHasScore \"0\"^^xsd:float;\n"
                + "           queries:hasDescriptiveSubsentence ?descSubsentUri.\n"
                + "    ?pe queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    # generated NL\n"
                + "    ?descSubsentUri a queries:DescriptiveSubsentence;\n"
                + "                   queries:hasStringValue ?l.\n"
                + "    # generated SPARQL\n"
                + "    ?emUri queries:hasSparqlRepresentation ?rep.\n"
                + "    ?tripleUri sp:subject   ?s ;\n"
                + "               sp:predicate ?p ;\n"
                + "               sp:object    ?o .\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pe a patterns:PatternElement;\n"
                + "        patterns:isQualifying \"true\"^^xsd:boolean;\n"
                + "        patterns:targetsKBElement ?kbe.\n"
                + "    OPTIONAL { ?kbe rdfs:label ?label. }\n"
                + "    # generated SPARQL\n"
                + "    {\n"
                + "      ?pe a patterns:ClassPatternElement.\n"
                + "  BIND (CONCAT (\"?cpe_\", REPLACE(STRUUID(), \"-\", \"\", \"i\")) AS ?rep)\n"
                + "      BIND (?rep AS ?s) BIND (rdf:type AS ?p) BIND (?kbe AS ?o)\n"
                + "    }\n"
                + "    UNION {\n"
                + "      ?pe a patterns:LiteralPatternElement.\n"
                + "  BIND (CONCAT (\"?lpe_\", REPLACE(STRUUID(), \"-\", \"\", \"i\")) AS ?rep)\n"
                + "    }\n"
                + "    UNION {\n"
                + "      ?pe a patterns:PropertyPatternElement;\n"
                + "          patterns:targetsKBElement ?rep.\n"
                + "    }\n"
                + "  }\n"
                + "  BIND (UUID() AS ?emUri)\n"
                + "  BIND (IRI(CONCAT(str(?emUri), \"_descSubsent\")) AS ?descSubsentUri)\n"
                + "  BIND (IF( BOUND(?label), CONCAT( \"(a/an)\", ?label), CONCAT( \"[\", STR(?kbe), \"]\")) AS ?l)\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void mappingLiterals(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        // mapping literals
        String query = "# mapping literals\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?emUri a queries:LiteralElementMapping;\n"
                + "           a queries:ElementMapping;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasPatternElement ?pe;\n"
                + "           queries:mappingHasPatternConstituent ?pe;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasLiteralQe ?literalQe;\n"
                + "           queries:emHasQueryElement ?literalQe;\n" // we don't use any reasonner in queries graph
                + "           queries:emHasScore \"15\"^^xsd:float;\n"
                + "           queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    # generated SPARQL\n"
                + "    ?emUri queries:hasDescriptiveSubsentence ?descSubsentUri.\n"
                + "    ?descSubsentUri a queries:DescriptiveSubsentence;\n"
                + "                    queries:dsIsQueried ?queried;\n"
                + "                    queries:hasStringValue ?stringValue.\n"
                + "    # generated SPARQL\n"
                + "    ?emUri queries:hasSparqlRepresentation ?rep;\n"
                + "           queries:hasFilter ?filter.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    <" + queryUri + "> queries:queryHasQueryElement ?literalQe.\n"
                + "    ?literalQe a queries:LiteralQueryElement;\n"
                + "               queries:literalQueryElementHasType ?swipTypeString;\n"
                + "               queries:queryElementHasValue ?literalValue.\n"
                + "    ?lmr a queries:LiteralMappingRule;\n"
                + "         queries:hasSwipTypeString ?swipTypeString;\n"
                + "         queries:hasLiteralType ?literalType.\n"
                + "    OPTIONAL\n"
                + "    {\n"
                + "      ?literalQe queries:queryElementIsQueried ?queried.\n"
                + "    }\n"
                + "  }\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?p patterns:patternHasPatternElement ?pe.\n"
                + "    ?pe patterns:targetsLiteralType ?literalType.\n"
                + "  }\n"
                + "  BIND (UUID() AS ?emUri)\n"
                + "  # generated NL\n"
                + "  BIND (IRI(CONCAT(str(?emUri), \"_descSubsent\")) AS ?descSubsentUri)\n"
                + "  BIND ( IF (?literalValue=\"?\", CONCAT (\"some \", ?swipTypeString), ?literalValue) AS ?stringValue)\n"
                + "  # generated SPARQL\n"
                + "  BIND (CONCAT (\"?literal_\", REPLACE(STRUUID(), \"-\", \"\", \"i\")) AS ?rep)\n"
                + "  BIND (CONCAT (\"FILTER ( regex (\", ?rep, \", '\", ?literalValue, \"') )\") AS ?filter)\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void performSpCollectionMapping(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingSpCollectionMapping");

        initializeSpCollectionMapping(queryUri, sparqlServer, commitUpdate, logQuery);

        int iteration = 0;

        if (commitUpdate) {
            do {
                logger.info("iteration " + ++iteration);
                performSpCollectionMappingLoop(queryUri, sparqlServer, commitUpdate, logQuery, iteration);
            } while (!allPatternsAreMapped(queryUri, sparqlServer, logQuery));
        } else {
            for (int i = 0; i < 2; i++) {
                logger.info("iteration " + ++iteration);
                performSpCollectionMappingLoop(queryUri, sparqlServer, commitUpdate, logQuery, iteration);
            }
        }

    }

    private void initializeSpCollectionMapping(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        // specify for all subpatterns that they have not yet been mapped to the current query
        String query = "# specify for all subpatterns that they have not yet been mapped to the current query\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc queries:isNotMappedToQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc a patterns:SubpatternCollection.\n"
                + "  }\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void performSpCollectionMappingLoop(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery, int iteration) {
        if (iteration > 1) {
            removeMappingsOfEmptyMappings(queryUri, sparqlServer, commitUpdate, logQuery);
            combineMappings(queryUri, sparqlServer, commitUpdate, logQuery);
            addEmptyMapping(queryUri, sparqlServer, commitUpdate, logQuery);
        }
        startMapping(queryUri, sparqlServer, commitUpdate, logQuery);
        preventFrom(queryUri, sparqlServer, commitUpdate, logQuery);
//            commitUpdate = false;
        for (int j = 0; j < 12; j++) {
            makeProgress(queryUri, sparqlServer, commitUpdate, logQuery);
        }
        validateMappings(queryUri, sparqlServer, commitUpdate, logQuery);
    }

    private boolean allPatternsAreMapped(String queryUri, SparqlClient sparqlServer, boolean logQuery) {
        String query = "# are all patterns mapped to the current query?\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "ASK\n"
                + "{\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?p a patterns:Pattern.\n"
                + "  }\n"
                + "  FILTER NOT EXISTS\n"
                + "  {\n"
                + "    GRAPH <" + queriesNamedGraphUri + "> { ?p queries:toConsiderInMappingQuery <" + queryUri + ">. }\n"
                + "  }\n"
                + "}";

        if (logQuery) {
            logger.info(query);
        }
        boolean result = !sparqlServer.ask(query);
        logger.info("-> " + result);
        return result;
    }

    private void removeMappingsOfEmptyMappings(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        String query = "# remove mappings of contingent subpattern collections containing only empty mappings\n"
                + "# an empty mapping for the contingent subpattern collection itself will be added later\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?m ?a ?b.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc patterns:hasCardinalityMin \"0\"^^xsd:int.\n"
                + "  }\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    ?m queries:mappingHasQuery <" + queryUri + ">;\n"
                + "       queries:mappingHasPatternConstituent ?spc;\n"
                + "       ?a ?b.\n"
                + "    FILTER NOT EXISTS\n"
                + "    {\n"
                + "      ?m queries:mappingContainsMapping+ ?em.\n"
                + "      ?em queries:emHasScore ?score.\n"
                + "      FILTER (?score > 0)\n"
                + "    }\n"
                + "  }\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    /**
     * combine mappings of repeatable subpattern collections
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void combineMappings(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        String query = "# combine mappings of repeatable subpattern collections\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?mappingUri a queries:SubpatternCollectionMapping;\n"
                + "                a queries:CombinationMapping;\n"
                + "                queries:mappingHasPatternConstituent ?spc;\n"
                + "                queries:mappingContainsMapping ?m1, ?m2;\n"
                + "                queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    ?spc queries:allreadyCombinedForQuery <" + queryUri + ">.\n"
                //                + "    # process the descriptive sentence\n"
                //                + "    ?mappingUri queries:hasDescriptiveSubsentence ?descSubsent.\n"
                //                + "    ?descSubsent a queries:DescriptiveSubsentence;\n"
                //                + "                 queries:isMadeUpOfList ?list;\n"
                //                + "                 queries:isMadeUpOfList-for ?listFor1, ?listFor2.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    SELECT DISTINCT ?spc WHERE\n"
                + "    {\n"
                + "      ?spc patterns:hasCardinalityMax \"2\"^^xsd:int.\n"
                + "    }\n"
                + "  }\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    FILTER NOT EXISTS\n"
                + "    {\n"
                + "      ?spc queries:allreadyCombinedForQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "    ?m1 queries:mappingHasPatternConstituent ?spc.\n"
                + "    ?m2 queries:mappingHasPatternConstituent ?spc.\n"
                + "    FILTER ( STR(?m1) < STR(?m2) )\n"
                //                + "    # process the descriptive sentence\n"
                //                + "    ?m1 (queries:hasDescriptiveSubsentence/queries:isMadeUpOfList) ?list.\n"
                //                + "    ?m1 (queries:hasDescriptiveSubsentence/queries:isMadeUpOfList-for) ?listFor1.\n"
                //                + "    ?m2 (queries:hasDescriptiveSubsentence/queries:isMadeUpOfList-for) ?listFor2.\n"
                + "  }\n"
                + "  BIND (UUID() AS ?mappingUri)\n"
                + "  BIND (UUID() AS ?descSubsent)\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    /**
     * add an empty mapping to ContingentSubpatternCollections to be considered in next mappings
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void addEmptyMapping(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        String query = "# add an empty mapping to ContingentSubpatternCollections to be considered in next mappings\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?mappingUri a queries:EmptySubpatternCollectionMapping;\n"
                + "                a queries:SubpatternCollectionMapping;\n" // we don't use any reasonner in queries graph
                + "                queries:mappingHasPatternConstituent ?spc;\n"
                + "                queries:mappingHasQuery <" + queryUri + ">;\n"
                //                + "                queries:hasDescriptiveSubsentence ?descSentUri.\n"
                //                + "    ?descSentUri a queries:DescriptiveSubsentence;\n"
                //                + "                   queries:hasStringValue \"(EmptySubpatternCollectionMapping)\".\n"
                + "  }\n"
                + "}\n"
                + "WHERE # select all ContingentSubpatternCollections which don't have yet an EmptySubpatternCollectionMapping\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    FILTER NOT EXISTS\n"
                + "    {\n"
                + "      ?mapping a queries:EmptySubpatternCollectionMapping;\n"
                + "                queries:mappingHasPatternConstituent ?spc;\n"
                + "                queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "  }\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    SELECT DISTINCT ?spc WHERE\n"
                + "    {\n"
                + "      ?spc a patterns:SubpatternCollection;\n"
                + "           patterns:hasCardinalityMin \"0\"^^xsd:int.\n"
                + "    }\n"
                + "  }\n"
                + "  BIND (UUID() AS ?mappingUri)\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    /**
     * start mapping of SubpatternCollections whose all contained components are already mapped
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void startMapping(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        String query = "# start mapping of SubpatternCollections whose all contained components are already mapped\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc queries:isNotMappedToQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
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
                + "      GRAPH <" + patternsNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?spc a patterns:SubpatternCollection.\n"
                + "      }\n"
                + "      # which are not mapped\n"
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?spc queries:isNotMappedToQuery <" + queryUri + ">.\n"
                + "      }\n"
                + "      # whose contained SubpatternCollections are all 'to be considered'\n"
                + "      MINUS\n"
                + "      {\n"
                + "        # get all SubpatternCollections whose at least one contained SubpatternCollection is being mapped not mapped\n"
                + "        SELECT ?spc WHERE\n"
                + "        {\n"
                + "          GRAPH <" + patternsNamedGraphUri + ">\n"
                + "          {\n"
                + "            ?spc a patterns:SubpatternCollection;\n"
                + "                 patterns:hasDirectSubpattern ?spc2.\n"
                + "            ?spc2 a patterns:SubpatternCollection;\n"
                + "          }\n"
                + "          GRAPH <" + queriesNamedGraphUri + ">\n"
                + "          {\n"
                + "            ?spc2 (queries:isNotMappedToQuery | queries:isBeingMappedToQuery) <" + queryUri + ">.\n"
                + "          }\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    /**
     * prevent from ...
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void preventFrom(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        String query = "# prevent from adding in a subpattern collection mapping a element mapping\n"
                + "# relative to a pattern element appearing outside of this subpattern collection\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pc queries:toConsiderNextStepInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  SELECT DISTINCT ?pc WHERE\n"
                + "  {\n"
                + "    GRAPH <" + queriesNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "      ?pc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "    GRAPH <" + patternsNamedGraphUri + ">\n"
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

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    /**
     * make progress the mappings of currently processed SubpatternCollections
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void makeProgress(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        String query = "# make progress the mappings of currently processed SubpatternCollections\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    ?pc queries:allreadyCombinedForQuery <" + queryUri + ">.\n"
                + "    # to not consider these mappings anymore\n"
                + "    ?mPc queries:mappingHasPatternConstituent ?pc.\n"
                + "    ?mSpc queries:mappingHasPatternConstituent ?spc.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pc queries:isMappedToQuery <" + queryUri + ">.\n"
                + "    ?mappingUri a queries:SubpatternCollectionMapping;\n"
                + "                queries:mappingHasPatternConstituent ?spc;\n"
                + "                queries:mappingContainsMapping ?mSpc, ?mPc;\n"
                + "                queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    # used for descriptive sentence generation\n"
                + "    ?mPc queries:mappingHadPatternConstituent ?pc.\n"
                + "    ?mSpc queries:mappingHadPatternConstituent ?spc.\n"
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
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?pc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "        ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "      }\n"
                + "      GRAPH <" + patternsNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?spc patterns:isMadeUpOf ?pc.\n"
                + "      }\n"
                + "    } ORDER BY ?spc ?pc LIMIT 1\n"
                + "  }\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
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

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    /**
     * validate mappings of SubpatternCollections whose...
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void validateMappings(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {

        String query = "# validate mappings of SubpatternCollections which are being mapped and whose contained SubpatternCollections are all mapped or to be mapped in next step\n"
                + "# change the status of toConsiderNextStepInMappingQuery SubpatternCollections into toConsiderInMappingQuery\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "    ?comp queries:toConsiderNextStepInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spc queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "    ?comp queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                //                + "    # process the descriptive sentence\n"
                //                + "    ?spcm queries:hasDescriptiveSubsentence ?descSubsent.\n"
                //                + "    ?descSubsent a queries:DescriptiveSubsentence;\n"
                //                + "                 ?madeUpRelation ?listSerialized .\n"
                //                + "    ?oneListSerialized rdf:first ?oneElementSerializedSsit ;\n"
                //                + "                       rdf:first ?oneElementSerializedFlit ;\n"
                //                + "                       rdf:first ?oneElementSerializedOther ;\n"
                //                + "                       rdf:firsttt ?plop ;\n"
                //                + "                       rdf:rest ?nextListSerialized .\n"
                //                + "    ?oneElementSerializedSsit queries:hasStringValue ?ssitValue.\n"
                //                + "    ?oneElementSerializedFlit queries:insertForSubsentenceOf ?spc.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  {\n"
                + "    # get all SubpatternCollections which are being mapped and whose contained SubpatternCollections are all mapped or to be mapped in next step\n"
                + "    SELECT ?spc WHERE\n"
                + "    {\n"
                + "      # all SubpatternCollections\n"
                + "      GRAPH <" + patternsNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?spc a patterns:SubpatternCollection.\n"
                + "      }\n"
                + "      # which are being mapped\n"
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?spc queries:isBeingMappedToQuery <" + queryUri + ">.\n"
                + "      }\n"
                + "      # whose contained SubpatternCollections are all mapped or to be mapped in next step\n"
                + "      MINUS\n"
                + "      {\n"
                + "        # get all SubpatternCollections whose at least one contained component is not mapped\n"
                + "        SELECT DISTINCT ?spc WHERE\n"
                + "        {\n"
                + "          GRAPH <" + patternsNamedGraphUri + ">\n"
                + "          {\n"
                + "            ?spc a patterns:SubpatternCollection;\n"
                + "                 patterns:isMadeUpOf ?comp.\n"
                + "          }\n"
                + "          GRAPH <" + queriesNamedGraphUri + ">\n"
                + "          {\n"
                + "            ?comp queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "          }\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "  # change the status of toConsiderNextStepInMappingQuery SubpatternCollections into toConsiderInMappingQuery\n"
                + "  OPTIONAL\n"
                + "  {\n"
                + "    GRAPH <" + patternsNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?spc patterns:isMadeUpOf ?comp.\n"
                + "    }\n"
                + "    GRAPH <" + queriesNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?comp queries:toConsiderNextStepInMappingQuery <" + queryUri + ">.\n"
                + "    }\n"
                + "  }\n"
                //                + "  # process the descriptive sentence\n"
                //                + "  GRAPH <" + queriesUri + ">\n"
                //                + "  {\n"
                //                + "    ?spcm queries:mappingHasPatternConstituent ?spc.\n"
                //                + "  }\n"
                //                + "  GRAPH <" + patternsUri + ">\n"
                //                + "  {\n"
                //                + "    ?sst (patterns:sstTargetsPc|^patterns:hasSentenceTemplate) ?spc ;\n"
                //                + "         patterns:isMadeUpOfList ?list ;\n"
                //                
                //                
                //                + "    # when dealing with spc containing a for loop, two subsentences are generated (one for the inner for loop, one for the whole spc) \n"
                //                + "    OPTIONAL {\n"
                //                + "      ?sst a patterns:ForLoopInTemplate.\n"
                //                + "      BIND (queries:isMadeUpOfList-for AS ?madeUpRelation).\n"
                //                + "    }\n"
                //                + "    OPTIONAL {\n"
                //                + "      ?sst a patterns:SpcInTemplate.\n"
                //                + "      BIND (queries:isMadeUpOfList AS ?madeUpRelation).\n"
                //                + "    }\n"
                //                
                //                
                //                + "    ?list rdf:rest* ?oneList .\n"
                //                + "    ?oneList rdf:first ?oneElement ;\n"
                //                + "             rdf:rest ?nextList .\n"
                //                + "  }\n"
                //                + "  # oneElement is a static string\n"
                //                + "  OPTIONAL\n"
                //                + "  {\n"
                //                + "    GRAPH <" + patternsUri + "> { ?oneElement patterns:ssitHasValue ?ssitValue. \n"
                //                + "                           BIND (UUID() AS ?oneElementSerializedSsit) }\n"
                //                + "  }\n"
                //                + "  # oneElement is a ForLoopInTemplate\n"
                //                + "  OPTIONAL\n"
                //                + "  {\n"
                //                + "    GRAPH <" + patternsUri + "> { ?oneElement a patterns:ForLoopInTemplate. \n"
                //                + "                           BIND (UUID() AS ?oneElementSerializedFlit) }\n"
                //                + "  }\n"
                //                + "  # oneElement is a PeInTemplate or a SpcInTemplate\n"
                //                + "  OPTIONAL\n"
                //                + "  {\n"
                //                + "    GRAPH <" + patternsUri + "> { {?oneElement a patterns:PeInTemplate.} UNION {?oneElement a patterns:SpcInTemplate.}\n"
                //                + "                           ?oneElement patterns:sstTargetsPc ?pc. }\n"
                //                + "    GRAPH <" + queriesUri + "> { ?spcm queries:mappingContainsMapping+ ?sspcm. \n"
                //                + "                          ?sspcm queries:mappingHadPatternConstituent ?pc; \n"
                //                + "                                 queries:hasDescriptiveSubsentence ?oneElementSerializedOther.\n"
                //                + "                          FILTER NOT EXISTS # because of combined mappings which maps same spc as their contained mappings \n"
                //                + "                          # FIXME: this part makes the query very slow (for something that looks simple) \n"
                //                + "                          {\n"
                //                + "                            ?spcm queries:mappingContainsMapping+ ?sspcm2.\n"
                //                + "                            ?sspcm2 queries:mappingHadPatternConstituent ?pc;\n"
                //                + "                                    queries:mappingContainsMapping ?sspcm.\n"
                //                + "                          }}\n"
                //                + "    BIND (<SpcInTemplate> AS ?plop)\n"
                //                + "  }\n"
                //                + "  BIND (IRI(CONCAT(str(?spcm), \"_descSubsent\")) AS ?descSubsent)\n"
                //                + "  BIND (IRI(CONCAT(str(?spcm), str(?list))) AS ?listSerialized)\n"
                //                + "  BIND (IRI(CONCAT(str(?spcm), str(?oneList))) AS ?oneListSerialized)\n"
                //                + "  BIND (IRI(CONCAT(str(?spcm), str(?nextList))) AS ?nextListSerialized)\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    /**
     * 
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void performPatternMapping(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingPatternMapping");

        logger.info(" - perform pattern mapping");
        String query = "# perform pattern mapping\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?p queries:toConsiderInMappingQuery <" + queryUri + ">.\n"
                + "  }\n"
                + "}\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?p queries:isMappedToQuery <" + queryUri + ">.\n"
                + "    ?pm a queries:PatternMapping.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:mappingHasPatternConstituent ?p;\n"
                + "        queries:mappingHasQuery <" + queryUri + ">.\n"
                + "    FILTER NOT EXISTS {?pm2 queries:mappingContainsMapping ?pm.}\n"
                + "    # reject mappings which don't involve any triple (this can occure when all subpatterns are optional)\n"
                + "    # 'proper' mappings must contain at least two distinct element mappings\n"
                + "    ?pm queries:mappingContainsMapping+ ?em1.\n"
                + "    ?em1 a queries:ElementMapping.\n"
                + "    ?pm queries:mappingContainsMapping+ ?em2.\n"
                + "    ?em2 a queries:ElementMapping.\n"
                + "    FILTER (!sameTerm(?em1, ?em2))\n"
                + "  }\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?p a patterns:Pattern.\n"
                + "  }\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);

        logger.info(" - save the number of generated mappings");
        query = "# save the number of generated mappings\n"
                + "PREFIX queries: <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    <" + queryUri + "> queries:hasNumPatternMappings ?nb.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  SELECT (count(?pm) AS ?nb) where\n"
                + "  {\n"
                + "    GRAPH <" + queriesNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?pm a queries:PatternMapping;\n"
                + "          queries:mappingHasQuery <" + queryUri + "> .\n"
                + "    }\n"
                + "  }\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void performMappingRanking(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingMappingRanking");

        logger.info("element mapping relevance mark");
        String query = "# process the element mapping relevance mark\n"
                + "# step 1: process the average score of involved element mappings\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:hasEmAvg ?avgscore;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  SELECT ?pm (AVG(?score) AS ?avgscore) WHERE\n"
                + "    {\n"
                + "    GRAPH <" + queriesNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?pm a queries:PatternMapping;\n"
                + "          queries:mappingHasQuery <" + queryUri + ">;\n"
                + "          queries:mappingContainsMapping+ ?em.\n"
                + "      ?em queries:emHasScore ?score.\n"
                + "    }\n"
                + "  } GROUP BY ?pm\n"
                + "};\n"
                + "# step 2: disavantage query mappings involving several times a same keyword\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:hasEmFactor ?factor;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  SELECT ?pm (COUNT(distinct ?kw) / COUNT(distinct ?em) AS ?factor ) WHERE\n"
                + "  {\n"
                + "    GRAPH <" + queriesNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?pm a queries:PatternMapping;\n"
                + "          queries:mappingHasQuery <" + queryUri + ">;\n"
                + "          (queries:mappingContainsMapping+ / (queries:hasInstanceMapping | queries:hasClassMapping)? ) ?em.\n"
                + "      FILTER NOT EXISTS {?em a queries:EmptyElementMapping.}\n"
                + "      ?em queries:emHasKeyword ?kw.\n"
                + "    }\n"
                + "  } GROUP BY ?pm\n"
                + "};\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:hasEmrMark ?emrmark;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:hasEmAvg ?avgscore.\n"
                + "    ?pm queries:hasEmFactor ?factor.\n"
                + "    BIND (?factor * ?avgscore AS ?emrmark)\n"
                + "  }\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);

        logger.info("query coverage relevance mark");
        query = "# process the query coverage relevance mark\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:hasQcrMark ?qcrmark;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  {\n"
                + "    SELECT (COUNT(?qe) AS ?nbqe) WHERE\n"
                + "    {\n"
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        <" + queryUri + "> queries:queryHasQueryElement ?qe.\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "  {\n"
                + "    SELECT ?pm (COUNT(DISTINCT ?qe) AS ?nbmappedqe) WHERE\n"
                + "    {\n"
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?pm a queries:PatternMapping;\n"
                + "            queries:mappingHasQuery <" + queryUri + ">;\n"
                //                + "            (queries:mappingContainsMapping+/queries:hasInstanceMapping*/queries:hasClassMapping*) ?em.\n"
                //                + "        ?em (queries:emHasMatching / queries:matchingHasKeyword) ?qe.\n"
                + "            queries:mappingContainsMapping+ ?em.\n"
                + "        ?em queries:emHasQueryElement ?qe.\n"
                + "      }\n"
                + "    } GROUP BY ?pm\n"
                + "  }\n"
                + "  BIND ( ?nbmappedqe / ?nbqe AS ?qcrmark )\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);

        logger.info("pattern coverage relevance mark");
        query = "# process the pattern coverage relevance mark\n"
                + "# in two step probably because of a bug in ARQ\n"
                + "# step 1: find out for each query mapping the number of element mapping with distinct pattern element\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:numberOfSignificantElementMappings ?nbmappedpe;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  select ?pm (count(distinct ?pe) as ?nbmappedpe) where\n"
                + "  {\n"
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?pm a queries:PatternMapping;\n"
                + "            queries:mappingHasQuery <" + queryUri + ">;\n"
                + "            queries:mappingHasPatternConstituent ?p;\n"
                + "            queries:mappingContainsMapping+ ?em.\n"
                + "        ?em queries:emHasPatternElement ?pe.\n"
                + "        FILTER NOT EXISTS { ?em a queries:EmptyElementMapping. }\n"
                + "      }\n"
                + "  } group by ?pm\n"
                + "};\n"
                + "# step 2: find out for each query mapping the total number of element mappings\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:numberOfElementMappings ?nbmappedpe;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  select ?pm (count(distinct ?pe) as ?nbmappedpe) where\n"
                + "  {\n"
                + "      GRAPH <" + queriesNamedGraphUri + ">\n"
                + "      {\n"
                + "        ?pm a queries:PatternMapping;\n"
                + "            queries:mappingHasQuery <" + queryUri + ">;\n"
                + "            queries:mappingHasPatternConstituent ?p;\n"
                + "            queries:mappingContainsMapping+ ?em.\n"
                + "        ?em queries:emHasPatternElement ?pe.\n"
                + "      }\n"
                + "  } group by ?pm\n"
                + "};\n"
                + "# step 3: \n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:hasPcrMark ?pcrmark;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:mappingHasQuery <" + queryUri + ">;\n"
                + "        queries:numberOfElementMappings ?nbem;\n"
                + "        queries:numberOfSignificantElementMappings ?nbsem.\n"
                + "  }\n"
                + "  BIND (?nbsem / ?nbem AS ?pcrmark)\n"
                + "}";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);

        logger.info("final relevance mark");
        query = "# process final relevance mark\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:hasRelevanceMark ?rmark;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm a queries:PatternMapping;\n"
                + "        queries:mappingHasQuery <" + queryUri + ">;\n"
                + "        queries:hasEmrMark ?emrmark;\n"
                + "        queries:hasQcrMark ?qcrmark;\n"
                + "        queries:hasPcrMark ?pcrmark.\n"
                + "  }\n"
                + "  BIND ( ?emrmark * ?qcrmark * ?pcrmark AS ?rmark )\n"
                //                + "  BIND ( ?qcrmark * ?pcrmark AS ?rmark )\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);


    }

    /**
     * TODO: check that everything that must be cleared is actually cleared
     * @param queryUri
     * @param sparqlServer
     * @param commitUpdate 
     */
    private void clearMappings(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingMappingsClearing");

        // in two steps for efficiency reasons

        // step one
        String query = "# clear all mappings whose rank is over a given threshold\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "DELETE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm ?c ?d .\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    {\n"
                + "      SELECT * WHERE \n"
                + "      {\n"
                + "        ?pm queries:mappingHasQuery <" + queryUri + ">;\n"
                + "            queries:hasRelevanceMark ?rmark.\n"
                + "        \n"
                + "      } ORDER BY DESC(?rmark) ?pm OFFSET " + this.numMappings + "\n"
                + "    }\n"
                + "    ?pm ?c ?d .\n"
                + "  }\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void generateDescriptiveSentences(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingSentenceGeneration");

        String query = "# generate descriptive sentences\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "# step 1: add an empty descriptive sentence to each subpattern collection\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "     ?spcm queries:hasDescriptiveSubsentence ?descSubsent.\n"
                + "     ?descSubsent a queries:DescriptiveSubsentence;\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spcm a queries:SubpatternCollectionMapping ;\n"
                + "          queries:mappingHasQuery <" + queryUri + "> ;\n"
                + "    FILTER NOT EXISTS { ?spcm a queries:EmptySubpatternCollectionMapping . }\n"
                + "    BIND (IRI(CONCAT(str(?spcm), \"_descSubsent\")) AS ?descSubsent) \n"
                + "  }\n"
                + "};\n"
                + "# step 2: build for each spc a list linking to children subsentences\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?descSubsent ?madeUpRelation ?listSerialized .\n"
                + "    ?oneListSerialized rdf:first ?oneElementSerializedSsit ;\n"
                + "                       rdf:first ?oneElementSerializedFlit ;\n"
                + "                       rdf:first ?oneElementSerializedOther ;\n"
                + "                       rdf:rest ?nextListSerialized .\n"
                + "    ?oneElementSerializedSsit queries:hasStringValue ?ssitValue.\n"
                + "    ?oneElementSerializedFlit queries:insertForSubsentenceOf ?spc.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?spcm queries:hasDescriptiveSubsentence ?descSubsent;\n"
                + "          (queries:mappingHasPatternConstituent|queries:mappingHadPatternConstituent) ?spc.\n"
                + "  }\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?sst (patterns:sstTargetsPc|^patterns:hasSentenceTemplate) ?spc ;\n"
                + "         patterns:isMadeUpOfList ?list .\n"
                + "    # when dealing with spc containing a for loop, two subsentences are generated (one for the inner for loop, one for the whole spc) \n"
                + "    OPTIONAL {\n"
                + "      ?sst a patterns:ForLoopInTemplate.\n"
                + "      BIND (queries:isMadeUpOfList-for AS ?madeUpRelation).\n"
                + "    }\n"
                + "    OPTIONAL {\n"
                + "      ?sst a patterns:SpcInTemplate.\n"
                + "      BIND (queries:isMadeUpOfList AS ?madeUpRelation).\n"
                + "    }\n"
                + "    ?list rdf:rest* ?oneList .\n"
                + "    ?oneList rdf:first ?oneElement ;\n"
                + "             rdf:rest ?nextList .\n"
                + "  }\n"
                + "  # oneElement is a static string\n"
                + "  OPTIONAL\n"
                + "  {\n"
                + "    GRAPH <" + patternsNamedGraphUri + "> { ?oneElement patterns:ssitHasValue ?ssitValue. \n"
                + "                           BIND (UUID() AS ?oneElementSerializedSsit) }\n"
                + "  }\n"
                + "  # oneElement is a ForLoopInTemplate\n"
                + "  OPTIONAL\n"
                + "  {\n"
                + "    GRAPH <" + patternsNamedGraphUri + "> { ?oneElement a patterns:ForLoopInTemplate. \n"
                + "                           BIND (UUID() AS ?oneElementSerializedFlit) }\n"
                + "  }\n"
                + "  # oneElement is a PeInTemplate or a SpcInTemplate\n"
                + "  OPTIONAL\n"
                + "  {\n"
                + "    GRAPH <" + patternsNamedGraphUri + "> { {?oneElement a patterns:PeInTemplate.} UNION {?oneElement a patterns:SpcInTemplate.}\n"
                + "                           ?oneElement patterns:sstTargetsPc ?pc. }\n"
                + "    GRAPH <" + queriesNamedGraphUri + "> { ?spcm queries:mappingContainsMapping+ ?sspcm. \n"
                + "                          ?sspcm queries:mappingHadPatternConstituent ?pc; \n"
                + "                                 queries:hasDescriptiveSubsentence ?oneElementSerializedOther.\n"
                + "                          FILTER NOT EXISTS # because of combined mappings which maps same spc as their contained mappings \n"
                + "                          # FIXME: this part makes the query very slow (for something that looks simple) \n"
                + "                          {\n"
                + "                            ?spcm queries:mappingContainsMapping+ ?sspcm2.\n"
                + "                            ?sspcm2 queries:mappingHadPatternConstituent ?pc;\n"
                + "                                    queries:mappingContainsMapping ?sspcm.\n"
                + "                          }}\n"
                + "  }\n"
                //                + "  BIND (IRI(CONCAT(str(?spcm), \"_descSubsent\")) AS ?descSubsent)\n"
                + "  BIND (IRI(CONCAT(str(?spcm), str(?list))) AS ?listSerialized)\n"
                + "  BIND (IRI(CONCAT(str(?spcm), str(?oneList))) AS ?oneListSerialized)\n"
                + "  BIND (IRI(CONCAT(str(?spcm), str(?nextList))) AS ?nextListSerialized)\n"
                //                + "};\n"        
                //        
                //                + "# step 3: handle the case of mappings issued from combinations\n"
                //                + "INSERT\n"
                //                + "{\n"
                //                + "  GRAPH <" + queriesUri + ">\n"
                //                + "  {\n"
                //                + "    ?descSubsent a queries:DescriptiveSubsentence;\n"
                //                + "                 queries:isMadeUpOfList ?list;\n"
                //                + "                 queries:isMadeUpOfList-for ?listFor1, ?listFor2.\n"
                //                + "  }\n"
                //                + "}\n"
                //                + "WHERE\n"
                //                + "{\n"
                //                + "  GRAPH <" + queriesUri + ">\n"
                //                + "  {\n"
                //                + "    ?m a queries:CombinationMapping;\n"
                //                + "       queries:mappingContainsMapping ?m1, ?m2;\n"
                //                + "       queries:hasDescriptiveSubsentence ?descSubsent.\n"
                //                + "    ?m1 (queries:hasDescriptiveSubsentence/queries:isMadeUpOfList) ?list.\n"
                //                + "    ?m1 (queries:hasDescriptiveSubsentence/queries:isMadeUpOfList-for) ?listFor1.\n"
                //                + "    ?m2 (queries:hasDescriptiveSubsentence/queries:isMadeUpOfList-for) ?listFor2.\n"
                //                + "  }\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void generateSparqlQueries(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingSparqlQueryGeneration");

        String query = "# generate SPARQL queries\n"
                + "PREFIX patterns:   <http://swip.univ-tlse2.fr/ontologies/Patterns#>\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "prefix sp:  <http://spinrdf.org/sp>\n"
                + "# step 1: sparql representation and implied triples of each element mapping\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "     ?pm queries:hasSparqlQuery ?sq.\n"
                + "     ?sq queries:hasTriple ?tripleUri;\n"
                + "         queries:hasFilter ?filter.\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm a queries:PatternMapping ;\n"
                + "        queries:mappingHasQuery <" + queryUri + "> ;\n"
                + "        queries:mappingContainsMapping+ ?em .\n"
                + "    {?em queries:hasTriple ?tripleUri.}\n"
                + "    UNION\n"
                + "    {?em queries:hasFilter ?filter.}\n"
                + "  }\n"
                + "  BIND (IRI(CONCAT(str(?pm), \"_sparql\")) AS ?sq) \n"
                + "};\n"
                + "# step 2: adding triples of the query patterns\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "     ?pm queries:hasSparqlQuery ?sq.\n"
                //                + "     ?sq a sp:Select _:list.\n"
                + "     ?sq queries:hasTriple ?tripleUri."
                + "     ?tripleUri sp:subject   ?repS ;\n"
                + "                sp:predicate ?repP ;\n"
                + "                sp:object    ?repO .\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + patternsNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?p patterns:patternHasSubpattern ?pt .\n"
                + "    ?pt patterns:hasSubject ?subj ;\n"
                + "        patterns:hasProperty ?prop ;\n"
                + "        patterns:hasObject ?obj .\n"
                + "  }\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm a queries:PatternMapping ;\n"
                + "        queries:mappingHasPatternConstituent ?p ;\n"
                + "        queries:mappingHasQuery <" + queryUri + "> .\n"
                + "  }\n"
                + "  # subject of pattern triple\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:mappingContainsMapping+ ?emS .\n"
                + "    ?emS queries:emHasPatternElement ?subj ;\n"
                + "         queries:hasSparqlRepresentation ?repS.\n"
                + "  }\n"
                + "  # property of pattern triple\n"
                + "  {\n"
                + "    GRAPH <" + queriesNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?pm queries:mappingContainsMapping+ ?emP .\n"
                + "      ?emP queries:emHasPatternElement ?prop ;\n"
                + "           queries:hasSparqlRepresentation ?repP.\n"
                + "    }\n"
                + "  }\n"
                + "  UNION\n"
                + "  {\n"
                + "    GRAPH <" + patternsNamedGraphUri + ">\n"
                + "    {\n"
                + "      ?prop patterns:isQualifying \"false\"^^xsd:boolean ;\n"
                + "            patterns:targetsProperty ?repP .\n"
                + "    }\n"
                + "  }\n"
                + "  # object of pattern triple\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm queries:mappingContainsMapping+ ?emO .\n"
                + "    ?emO queries:emHasPatternElement ?obj ;\n"
                + "         queries:hasSparqlRepresentation ?repO.\n"
                + "  }\n"
                + "  BIND (IRI(CONCAT(str(?pm), \"_sparql\")) AS ?sq) \n"
                + "  BIND (UUID() AS ?tripleUri)\n"
                + "};\n"
                + "# step 3: select variable (projection atribute)\n"
                + "INSERT\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "     ?sq queries:hasSelectVar ?rep .\n"
                + "  }\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + "    ?pm a queries:PatternMapping ;\n"
                + "        queries:mappingHasPatternConstituent ?p ;\n"
                + "        queries:mappingHasQuery <" + queryUri + "> ;\n"
                + "        queries:mappingContainsMapping+ ?em ;\n"
                + "        queries:hasSparqlQuery ?sq.\n"
                + "    <" + queryUri + "> queries:queryHasQueryElement ?qe.\n"
                + "    ?qe queries:queryElementIsQueried \"true\"^^xsd:boolean.\n"
                + "    ?em queries:emHasQueryElement ?qe;\n"
                + "        queries:hasSparqlRepresentation ?rep.\n"
                + "  }\n"
                + "}\n";

        logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
    }

    private void clearKB(String queryUri, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        // change query processing state
        Controller.getInstance().changeQueryProcessingState(queryUri, sparqlServer, queriesNamedGraphUri, "PerformingKbClearing");

//        String query = "# clear spc mappings which are not contained in any query mapping\n"
//                + "DELETE\n"
//                + "{\n"
//                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
//                + "  {\n"
//                + "    ?spcm ?a ?b .\n"
//                + "  }\n"
//                + "}\n"
//                + "WHERE\n"
//                + "{\n"
//                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
//                + "  {\n"
//                + "    ?spcm a queries:SubpatternCollectionMapping .\n"
//                + "    FILTER NOT EXISTS { ?pm a queries:PatternMapping; \n"
//                + "                            queries:mappingContainsMapping* ?spcm. }\n"
//                + "    ?spcm ?a ?b .\n"
//                + "  }\n"
//                + "}\n";

        for (int i = 1; i <= 6; i++) {
            logger.info("iteration " + i);
            String query = "# clear spc mappings which are not contained in any query mapping\n"
                    + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                    + "DELETE\n"
                    + "{\n"
                    + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                    + "  {\n"
                    + "    ?spcm ?a ?b .\n"
                    + "  }\n"
                    + "}\n"
                    + "WHERE\n"
                    + "{\n"
                    + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                    + "  {\n"
                    + "    ?spcm a queries:SubpatternCollectionMapping .\n"
                    + "    FILTER NOT EXISTS { ?spcm a queries:PatternMapping . }\n"
                    + "    FILTER NOT EXISTS { ?pm queries:mappingContainsMapping ?spcm . }\n"
                    + "    ?spcm ?a ?b .\n"
                    + "  }\n"
                    + "}\n";
            logAndCommitQuery(query, sparqlServer, commitUpdate, logQuery);
        }

    }

    private void logAndCommitQuery(String query, SparqlClient sparqlServer, boolean commitUpdate, boolean logQuery) {
        if (logQuery) {
            logger.info(query);
        }
        if (commitUpdate) {
            sparqlServer.update(query);
        }
    }
}
