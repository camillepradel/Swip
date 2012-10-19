package org.swip.nlToPivotParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.maltparser.MaltParserService;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.syntaxgraph.DependencyStructure;
import org.maltparser.core.syntaxgraph.edge.Edge;
import org.maltparser.core.syntaxgraph.node.DependencyNode;
import org.maltparser.core.syntaxgraph.node.Root;
import org.maltparser.core.syntaxgraph.node.Token;
import org.swip.utils.sparql.RemoteSparqlServer;

public class MaltParser {

    private static final Logger logger = Logger.getLogger(MaltParser.class);
    MaltParserService service = null;
    boolean ASK = false;
    boolean COUNT = false;
    Set<Integer> visitedNodes = null;
    String pivotQuery = null;
    String nextElementRole = null;
    String subjectOfNextQ2 = null;
    boolean inverseQ3 = false;
    String queryObject = null;
    Token firstTokenToBrowse = null;
    List<String> ignoredRelList = new ArrayList<String>(Arrays.asList(new String[]{"prep", "det", "punct", "auxpass"}));
    List<String> ignoredRoots = new ArrayList<String>(Arrays.asList(new String[]{"?"}));
    final List<String> ignoredPostags = new ArrayList<String>(Arrays.asList(new String[]{"DET", "WRB", "WDT", "IN", "IN/that", "WP", "PP", "PP$", "DT", "SENT", "RB", "RBR", "JJR", "VHP", "VHZ"}));
    final List<String> ignoredLemmas = new ArrayList<String>(Arrays.asList(new String[]{"do", "be", "show", "give", "list", "call", "all", "many"}));
    // sparql server
    RemoteSparqlServer sparqlServer = new RemoteSparqlServer("http://swipserver:8080/joseki/musicbrainz");

    public MaltParser() {
        try {
            service = new MaltParserService();
        } catch (MaltChainedException ex) {
            logger.error(ex.getMessage());
        }
    }

    public DependencyStructure posTaggedToDependecies(String[] tokens, String lang) throws MaltChainedException {
        logger.info("------------");
        logger.info("+ MaltParser");
        String[] formatedTokens = new String[tokens.length];
        int i = 0;
        for (String token : tokens) {
            String form = token.substring(0, token.indexOf("\t"));
            String lema = token.substring(token.lastIndexOf("\t") + 1);
            String postag = token.substring(token.indexOf("\t") + 1, token.lastIndexOf("\t"));
            formatedTokens[i] = (i + 1) + "\t" + form + "\t" + lema + "\t" + postag + "\t" + postag + "\t" + "_" + "\t" + "_" + "\t" + "_" + "\t" + "_";
            i++;
        }

        if (lang.equals("en")) {
            // on mirail server
            service.initializeParserModel("-c engmalt.linear-1.7 -m parse -w /home/operateur/apache-tomcat-7.0.32/webapps/NlToPivotParser/WEB-INF/classes/ -lfi parser.log");
            // on my computer
//            service.initializeParserModel("-c engmalt.linear-1.7 -m parse -w /mnt/data/gitSwip/NlToPivotParser/build/web/WEB-INF/classes/ -lfi parser.log");
        } else if (lang.equals("fr")) {
            // on mirail server
            service.initializeParserModel("-c fremalt-1.7 -m parse -w /home/operateur/apache-tomcat-7.0.32/webapps/NlToPivotParser/WEB-INF/classes/ -lfi parser.log");
            // on my computer
//            service.initializeParserModel("-c fremalt-1.7 -m parse -w /mnt/data/gitSwip/NlToPivotParser/build/web/WEB-INF/classes/ -lfi parser.log");
        } else {
            // Todo: rise exception
        }
        DependencyStructure graph = service.parse(formatedTokens);
        logger.info("MaltParser result:");
        displayDependencyTree(graph);
        return graph;
    }

    public String dependenciesToPivot(String[] tokens, DependencyStructure graph, String lang) throws MaltChainedException {
        logger.info("----------------------------------");
        logger.info("+ Interpreting dependency graph...");
        firstTokenToBrowse = null;
        queryObject = null;
        visitedNodes = new HashSet<Integer>();
        ASK = false;
        COUNT = false;
        pivotQuery = "";
        nextElementRole = "e1q3";
        inverseQ3 = false;

        // determine if the considered query is a "When"/"Who"/"COUNT" query
        Token firstTokenInSentence = (Token) graph.getDependencyNode(1);
        Token secondTokenInSentence = (Token) graph.getDependencyNode(2);
        String firstTokenLemma = getLemma(firstTokenInSentence);
        String secondTokenLemma = getLemma(secondTokenInSentence);
        if (firstTokenLemma.equals("who")) {
            firstTokenToBrowse = firstTokenInSentence;
            pivotQuery += "person";
            queryObject = "person";
            nextElementRole = "e2q3";
        } else if (firstTokenLemma.equals("when")) {
            firstTokenToBrowse = firstTokenInSentence;
            pivotQuery += "date";
            queryObject = "date";
            nextElementRole = "e2q3";
            inverseQ3 = true;
        } else if (firstTokenLemma.equals("How") && secondTokenLemma.equals("many")) {
            COUNT = true;
            firstTokenToBrowse = firstTokenInSentence;
        } else {
            // look for query object
            searchQueryObject(graph);

            if (queryObject == null) {
                ASK = true;
                firstTokenToBrowse = (Token) graph.getDependencyRoot().getRightmostDependent();
            }
        }

        // browse dependency tree
        browseNode(tokens, graph, firstTokenToBrowse);
        pivotQuery += ".";
        if (COUNT) {
            pivotQuery += " COUNT.";
        } else if (ASK) {
            pivotQuery += " ASK.";
        } else {
            if (pivotQuery.contains(queryObject)) {
                pivotQuery = pivotQuery.replaceAll(queryObject, "?" + queryObject);
            } else {
                pivotQuery += "?" + queryObject + ".";
            }
        }

        return pivotQuery;
    }

    private void searchQueryObject(DependencyStructure graph) {
        try {
            for (int idNode : graph.getDependencyIndices()) {
                DependencyNode node = graph.getDependencyNode(idNode);
                if (node instanceof Token) {
                    Token token = (Token) node;
                    String tokenLemma = getLemma(token);
                    String tokenPostag = getPostag(token);
                    DependencyNode head = token.getHead();
                    if (head instanceof Token) {
                        // "which... ?" case
                        Token headToken = (Token) head;
                        String headEdgeDeprel = getDeprel(token.getHeadEdge());
                        String headLemma = getLemma(headToken);
                        String headPostag = getPostag(headToken);
                        if (tokenLemma.equals("which") && tokenPostag.equals("WDT") && headEdgeDeprel.equals("det")) {
                            // case [LEMMA=which,POSTAG=WDT] <--(DEPREL=det)-- [query object]
                            firstTokenToBrowse = token;
                            queryObject = headLemma;
                            return;
                        }
                        // all "give me..." cases
                        DependencyNode head2 = head.getHead();
                        if (head2 instanceof Token) {
                            Token head2Token = (Token) head2;
                            String head2EdgeDeprel = getDeprel(head.getHeadEdge());
                            String head2Lemma = getLemma(head2Token);
                            if (headLemma.equals("me")
                                    && (headEdgeDeprel.equals("dobj") || headEdgeDeprel.equals("null"))
                                    && head2Lemma.equals("give")
                                    && (head2EdgeDeprel.equals("dep") || head2EdgeDeprel.equals("null"))) {
                                // case [LEMMA=give] --(DEPREL=dep/null)--> [LEMMA=me] --(DEPREL=dobj/null)-->  [query object]
                                firstTokenToBrowse = head2Token;
                                queryObject = tokenLemma;
                                return;
                            }
                            if (headPostag.equals("SENT")
                                    && headEdgeDeprel.equals("nsubj")
                                    && head2Lemma.equals("give")
                                    && head2EdgeDeprel.equals("null")) {
                                // case [LEMMA=give] --(DEPREL=null)--> [POSTAG=SENT] --(DEPREL=punct)-->  [LEMMA=me]
                                //                                                    --(DEPREL=nsubj)-->  [query object]
                                firstTokenToBrowse = head2Token;
                                queryObject = tokenLemma;
                                return;
                            }
                            if (tokenLemma.equals("give")
                                    && headEdgeDeprel.equals("nsubj")
                                    && head2Lemma.equals("give")
                                    && head2EdgeDeprel.equals("null")) {
                                // case [LEMMA=give] --(DEPREL=null)--> [POSTAG=SENT] --(DEPREL=nsubj)-->  [query object]
                                //                                                    --(DEPREL=punct)-->  [LEMMA=me] (pas pris en compte)
                                firstTokenToBrowse = head2Token;
                                queryObject = tokenLemma;
                                return;
                            }
                        }
                        if (tokenLemma.equals("give")
                                && (headEdgeDeprel.equals("dep") || headEdgeDeprel.equals("nsubj"))
                                && head2 instanceof Root) {
                            // case [LEMMA=give] <--(DEPREL=dep/nsubj)-- [query object] <--(DEPREL=null)-->  [ROOT]
                            //                                                          <--(DEPREL=dep/predet)-- [LEMMA=give] (pas pris en compte)
                            firstTokenToBrowse = token;
                            queryObject = headLemma;
                            return;
                        }
                        if (headLemma.equals("How") && headEdgeDeprel.equals("advmod")) {
                            // case [LEMMA=How] --(DEPREL=advmod)--> [query object]
                            firstTokenToBrowse = headToken;
                            queryObject = tokenLemma;
                            return;
                        }
                    }
                }
            }
        } catch (MaltChainedException ex) {
            logger.error(ex);
        }
    }

    void browseNode(String[] tokens, DependencyStructure graph, Token token) {
        try {
            logger.info("browsing " + token);
            // process token
            String tokenLemma = getLemma(token);
            String headEdgeDeprel = getDeprel(token.getHeadEdge());
            if (!ignoredPostags.contains(getPostag(token)) && !ignoredLemmas.contains(getLemma(token))) {
                if (headEdgeDeprel.equals("prt")) {
                    logger.info("token is part of its head");
                    String headLemma = getLemma((Token) token.getHead());
                    pivotQuery = pivotQuery.replaceAll(headLemma, headLemma + " " + tokenLemma);
                } else if (headEdgeDeprel.equals("nsubj") || headEdgeDeprel.equals("nn")) {
                    logger.info("token is maybe the type of its head...");

                    String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                            + "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
                            + "PREFIX pf:   <http://jena.hpl.hp.com/ARQ/property#>"
                            + "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                            + "ASK"
                            + "{"
                            + "    ?resource rdf:type owl:Class."
                            + "    ?resource dc:title|rdfs:label ?label."
                            + "    (?label ?score) pf:textMatch \"" + tokenLemma + "\"."
                            // valeur du score à revoir (nouvelle version de larq)
                            + "    FILTER (?score > 0.6)"
                            + "}";
                    logger.info(query);
//                    if (sparqlServer.ask(query)) {
                    if (false) {
                        logger.info("    and has been successfully matched to a KB class");
                        String headLemma = getLemma((Token) token.getHead());
                        pivotQuery = pivotQuery.replaceAll(headLemma, headLemma + "(" + tokenLemma + ")");
                    } else {
                        logger.info("    but has not been successfully matched to a KB class");
                        addElementToPivotQuery(tokenLemma);
                    }
                } else {
                    logger.info("added to pivot query");
                    addElementToPivotQuery(tokenLemma);
                }
            } else {
                logger.info("ignored");
            }
            visitedNodes.add(token.getIndex());
            // process children
            for (DependencyNode child : token.getLeftDependents()) {
                if (child instanceof Token && !visitedNodes.contains(child.getIndex())) {
                    browseNode(tokens, graph, (Token) child);
                }
            }
            for (DependencyNode child : token.getRightDependents()) {
                if (child instanceof Token && !visitedNodes.contains(child.getIndex())) {
                    browseNode(tokens, graph, (Token) child);
                }
            }
            // process head
            DependencyNode head = token.getHead();
            if (head instanceof Token && !visitedNodes.contains(head.getIndex())) {
                browseNode(tokens, graph, (Token) head);
            }
        } catch (MaltChainedException ex) {
            logger.error(ex);
        }
    }

    private void addElementToPivotQuery(String element) {
        if (inverseQ3) {
            if (nextElementRole.equals("e2q3")) {
                pivotQuery = element + ": " + pivotQuery;
                nextElementRole = "e1q3";
            } else if (nextElementRole.equals("e1q3")) {
                pivotQuery = pivotQuery.replaceFirst(":", "=");
                pivotQuery = element + ": " + pivotQuery;
                nextElementRole = "e2q2";
                subjectOfNextQ2 = element;
                inverseQ3 = false;
            }
        } else {
            if (nextElementRole.equals("e1q3")) {
                pivotQuery += element;
                nextElementRole = "e2q3";
            } else if (nextElementRole.equals("e2q3")) {
                pivotQuery += ": " + element;
                nextElementRole = "e3q3";
            } else if (nextElementRole.equals("e3q3")) {
                pivotQuery += "= " + element;
                nextElementRole = "e2q2";
                subjectOfNextQ2 = element;
            } else if (nextElementRole.equals("e2q2")) {
                pivotQuery += ". " + subjectOfNextQ2 + ": " + element;
                nextElementRole = "e2q2";
            }
        }
    }

    private String getLemma(Token node) {
        String s = node.toString();
        int beginIndex = s.indexOf("LEMMA:") + 6;
        int endIndex = s.indexOf(" ", beginIndex);
        return s.substring(beginIndex, endIndex);
    }

    private String getPostag(Token node) {
        String s = node.toString();
        int beginIndex = s.indexOf("POSTAG:") + 7;
        int endIndex = s.indexOf(" ", beginIndex);
        return s.substring(beginIndex, endIndex);
    }

    private String getDeprel(Edge edge) {
        String s = edge.toString();
        int beginIndex = s.indexOf("DEPREL:") + 7;
        int endIndex = s.indexOf(" ", beginIndex);
        return s.substring(beginIndex, endIndex);
    }

    public void displayDependencyTree(DependencyStructure graph) {
        try {
            for (int idNode : graph.getDependencyIndices()) {
                DependencyNode node = graph.getDependencyNode(idNode);
                logger.info(node.toString());
                logger.info("head edge: " + node.getHeadEdge());
            }
        } catch (MaltChainedException ex) {
            logger.error(ex);
        }
    }
}
