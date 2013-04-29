package org.swip.pivotToMappings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.model.query.Query;
import org.swip.pivotToMappings.model.query.antlr.KeywordRuntimeException;
import org.swip.pivotToMappings.model.query.antlr.LexicalErrorRuntimeException;
import org.swip.pivotToMappings.model.query.antlr.LiteralRuntimeException;
import org.swip.pivotToMappings.model.query.antlr.QueryParsingException;
import org.swip.pivotToMappings.model.query.antlr.SyntaxErrorRuntimeException;
import org.swip.pivotToMappings.model.query.antlr.userQueryGrammarLexer;
import org.swip.pivotToMappings.model.query.antlr.userQueryGrammarParser;
import org.swip.pivotToMappings.model.query.queryElement.Keyword;
import org.swip.pivotToMappings.model.query.queryElement.Literal;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.pivotToMappings.model.query.subquery.Q1;
import org.swip.pivotToMappings.model.query.subquery.Q2;
import org.swip.pivotToMappings.model.query.subquery.Q3;
import org.swip.pivotToMappings.model.query.subquery.Subquery;
import org.swip.utils.sparql.SparqlClient;

public class Controller {

    private static final Logger logger = Logger.getLogger(Controller.class);
    static Controller staticController = null;
    int nbQuery = 0;

    public Controller() {
    }

    public static Controller getInstance() {
        if (staticController == null) {
            staticController = new Controller();
        }
        return staticController;
    }

    String processQuery(String pivotQueryString, String sparqlEndpointUri, boolean useFederatedSparql, boolean useLarq, String kbLocation, String queriesNamedGraphUri, String patternsNamedGraphUri, int numMappings) {

        logger.info("Process query");

        // send me an email with the pivot query
        // Mail.sendEmail("[SWIP] getBestMappings query " + nbQuery++, "pivot query: " + pivotQueryString);

        logger.info("pivotQueryString : " + pivotQueryString);
        logger.info("sparqlEndpointUri : " + sparqlEndpointUri);
        logger.info("useFederatedSparql : " + useFederatedSparql);
        logger.info("useLarq : " + useLarq);
        logger.info("kbLocation : " + kbLocation);
        logger.info("queriesNamedGraphUri : " + queriesNamedGraphUri);
        logger.info("patternsNamedGraphUri : " + patternsNamedGraphUri);
        logger.info("numMappings : " + numMappings);
        SparqlClient sparqlClient = new SparqlClient(sparqlEndpointUri);
        try {
            logger.info("Parsing pivot query : " + pivotQueryString);
            final Query userQuery = createQuery(pivotQueryString);
            logger.info("parsed query: " + userQuery.toString() + "\n");
            String queryUri = generateQueryUri(userQuery, queriesNamedGraphUri);
            logger.info("query uri: " + queryUri + "\n");
            if (queryAlreadyExists(queryUri, sparqlClient, queriesNamedGraphUri)) {
                // TODO: update query metadata
                logger.info("query already exists: " + queryUri);
                return queryUri;
            } else {
                logger.info("================================================================\n");
                logger.info("Commit query into SPARQL endpoint:");
                logger.info("---------------------------------\n");
                commitQuery(queryUri, userQuery, queriesNamedGraphUri, sparqlClient);
                // change query processing state
                changeQueryProcessingState(queryUri, sparqlClient, queriesNamedGraphUri, "NotBegun");
                
                // perform query interpretation in a new thread
                QueryInterpreter qi = new QueryInterpreter(queryUri, sparqlClient, useFederatedSparql, useLarq, kbLocation, queriesNamedGraphUri, patternsNamedGraphUri, numMappings);
                qi.start();
                
                return queryUri;
            }
        } catch (QueryParsingException ex) {
            logger.info("An error occured while parsing query: " + pivotQueryString + "\n" + ex.getMessage());
            logger.info("Query process aborted");
            logger.error(ex.toString());
        }
        return null;
    }

    /**
     * create query object by parsing the pivot query string representation
     * @param userQuery
     * @return the query object
     * @throws QueryParsingException 
     */
    private Query createQuery(String userQuery) throws QueryParsingException {
        try {
            ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(userQuery.getBytes()));
            userQueryGrammarLexer lexer = new userQueryGrammarLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            userQueryGrammarParser parser = new userQueryGrammarParser(tokens);
            return parser.query();
        } catch (RecognitionException ex) {
            throw new QueryParsingException("RecognitionException: " + ex.getMessage());
        } catch (IOException ex) {
            throw new QueryParsingException("IOException: " + ex.getMessage());
        } catch (KeywordRuntimeException ex) {
            throw new QueryParsingException(ex.getMessage());
        } catch (LiteralRuntimeException ex) {
            throw new QueryParsingException(ex.getMessage());
        } catch (SyntaxErrorRuntimeException ex) {
            throw new QueryParsingException("Syntax error at " + ex.getMessage());
        } catch (LexicalErrorRuntimeException ex) {
            throw new QueryParsingException("Syntax error at " + ex.getMessage());
        }
    }

    /**
     * 
     * @param userQuery
     * @return 
     */
    private String generateQueryUri(Query userQuery, String queriesNamedGraphUri) {
        List<String> subqueryStrings = new LinkedList<String>();
        String result = queriesNamedGraphUri + "#";
        for (Subquery sq : userQuery.getSubqueries()) {
            subqueryStrings.add(getStringForSubquery(sq));
        }
        while (subqueryStrings.size() > 0) {
            String nextSqString = subqueryStrings.get(0);
            for (String sqString : subqueryStrings) {
                if (sqString.compareTo(nextSqString) < 0) {
                    nextSqString = sqString;
                }
            }
            result += nextSqString + "-p-";
            subqueryStrings.remove(nextSqString);
        }
        return result;
    }

    private String getStringForSubquery(Subquery sq) {
        if (sq instanceof Q1) {
            return ((Q1) sq).getE1().getStringForQueryUri();
        } else if (sq instanceof Q2) {
            return ((Q2) sq).getE1().getStringForQueryUri() + "-c-" + ((Q2) sq).getE2().getStringForQueryUri();
        } else if (sq instanceof Q3) {
            return ((Q3) sq).getE1().getStringForQueryUri() + "-c-" + ((Q3) sq).getE2().getStringForQueryUri() + "-e-" + ((Q3) sq).getE3().getStringForQueryUri();
        } else {
            return null;
        }
    }

    private boolean queryAlreadyExists(String queryUri, SparqlClient sparqlServer, String queriesUri) {
        String ask = "ASK\n"
                + "{\n"
                + "  GRAPH <" + queriesUri + ">\n"
                + "  {\n"
                + "    <" + queryUri + "> ?a ?b.\n"
                + "  }\n"
                + "}\n";
        return sparqlServer.ask(ask);
    }

    private void commitQuery(String queryUri, Query userQuery, String queriesNamedGraphUri, SparqlClient sparqlServer) {
        String updateGraph = getGraphForQuery(queryUri, userQuery, queriesNamedGraphUri, sparqlServer);
        String query = "# commit query "+ queryUri + "\n"
                + "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
                + "INSERT DATA\n"
                + "{\n"
                + "  GRAPH <" + queriesNamedGraphUri + ">\n"
                + "  {\n"
                + updateGraph
                + "  }\n"
                + "}\n";
//        logger.info(query);
        sparqlServer.update(query);
    }

//    void deleteQuery(String pivotQueryString, String sparqlEndpointUri) {
//        try {
//
//            logger.info("Process query");
//            logger.info("pivotQueryString : " + pivotQueryString);
//            logger.info("sparqlEndpointUri : " + sparqlEndpointUri);
//            SparqlClient sparqlClient = new SparqlClient(sparqlEndpointUri);
//            final Query userQuery = createQuery(pivotQueryString);
//            logger.info("parsed query: " + userQuery.toString() + "\n");
//            String queryLocalName = generateQueryUri(userQuery, sparqlClient);
//            String queryUri = sparqlClient.getEndpointUri() + queryLocalName;
//            logger.info("query local name: " + queryLocalName + "\n");
//            logger.info("query uri: " + queryUri + "\n");
//
//            String queryGraph = getGraphForQuery(queryUri, userQuery, sparqlClient);
//            String query = "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
//                    + "PREFIX graph:   <" + kbUri + ">\n"
//                    + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n"
//                    + "DELETE WHERE\n"
//                    + "{\n"
//                    + "  GRAPH <" + queriesUri + ">\n"
//                    + "  {\n"
//                    + queryGraph
//                    + "  }\n"
//                    + "}";
//            logger.info(query);
//            sparqlClient.update(query);
//        } catch (QueryParsingException ex) {
//            java.util.logging.Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    private String getGraphForQuery(String queryUri, Query userQuery, String queriesNamedGraphUri, SparqlClient sparqlServer) {
        String queryGraph = "    <" + queryUri + "> a queries:PivotQuery.\n";
        if (userQuery.isCount()) {
            queryGraph += "    <" + queryUri + "> a queries:CountPivotQuery.\n";
        } else if (userQuery.isAsk()) {
            queryGraph += "    <" + queryUri + "> a queries:AskPivotQuery.\n";
        }
        for (QueryElement qe : userQuery.getQueryElements()) {
            String qeUri = qe.getStringUri(queryUri, queriesNamedGraphUri);
            if (qe instanceof Keyword) {
                queryGraph += "    <" + qeUri + "> a queries:KeywordQueryElement.\n";
            } else if (qe instanceof Literal) {
                queryGraph += "    <" + qeUri + "> a queries:LiteralQueryElement.\n";
                queryGraph += "    <" + qeUri + "> queries:literalQueryElementHasType \"" + ((Literal)qe).getStringType() + "\".\n";
            }
            queryGraph += "    <" + qeUri + "> queries:queryElementHasValue \"" + qe.getStringValue().replaceAll("_", " ") + "\".\n";
            queryGraph += "    <" + queryUri + "> queries:queryHasQueryElement <" + qeUri + ">.\n";
            if (qe.isQueried()) {
                queryGraph += "    <" + qeUri + "> queries:queryElementIsQueried \"true\"^^xsd:boolean.\n";
            }
        }
        int i = 1;
        for (Subquery sq : userQuery.getSubqueries()) {
            String sqUri = queryUri + "/sq" + i++;
            if (sq instanceof Q1) {
                queryGraph += "    <" + sqUri + "> a queries:Q1.\n";
                queryGraph += "    <" + queryUri + "> queries:queryHasSubquery <" + sqUri + ">.\n";
                queryGraph += "    <" + sqUri + "> queries:subqueryHasE1 <" + ((Q1) sq).getE1().getStringUri(queryUri, sparqlServer.getEndpointUri()) + ">.\n";
            } else if (sq instanceof Q2) {
                queryGraph += "    <" + sqUri + "> a queries:Q2.\n";
                queryGraph += "    <" + queryUri + "> queries:queryHasSubquery <" + sqUri + ">.\n";
                queryGraph += "    <" + sqUri + "> queries:subqueryHasE1 <" + ((Q2) sq).getE1().getStringUri(queryUri, sparqlServer.getEndpointUri()) + ">.\n";
                queryGraph += "    <" + sqUri + "> queries:subqueryHasE2 <" + ((Q2) sq).getE2().getStringUri(queryUri, sparqlServer.getEndpointUri()) + ">.\n";
            } else if (sq instanceof Q3) {
                queryGraph += "    <" + sqUri + "> a queries:Q3.\n";
                queryGraph += "    <" + queryUri + "> queries:queryHasSubquery <" + sqUri + ">.\n";
                queryGraph += "    <" + sqUri + "> queries:subqueryHasE1 <" + ((Q3) sq).getE1().getStringUri(queryUri, sparqlServer.getEndpointUri()) + ">.\n";
                queryGraph += "    <" + sqUri + "> queries:subqueryHasE2 <" + ((Q3) sq).getE2().getStringUri(queryUri, sparqlServer.getEndpointUri()) + ">.\n";
                queryGraph += "    <" + sqUri + "> queries:subqueryHasE3 <" + ((Q3) sq).getE3().getStringUri(queryUri, sparqlServer.getEndpointUri()) + ">.\n";
            }
        }
        return queryGraph;
    }

    void changeQueryProcessingState(String queryUri, SparqlClient sparqlServer, String queriesUri, String stateLocalName) {
        String query = "PREFIX queries:   <http://swip.univ-tlse2.fr/ontologies/Queries#>\n"
                + "DELETE WHERE\n"
                + "{\n"
                + "  GRAPH <" + queriesUri + ">\n"
                + "  {\n"
                + "    <" + queryUri + "> queries:queryHasProcessingState ?state.\n"
                + "  }\n"
                + "};\n"
                + "INSERT DATA\n"
                + "{\n"
                + "  GRAPH <" + queriesUri + ">\n"
                + "  {\n"
                + "    <" + queryUri + "> queries:queryHasProcessingState queries:" + stateLocalName + ".\n"
                + "  }\n"
                + "}";
//        logger.info(query);
        sparqlServer.update(query);
    }
}
