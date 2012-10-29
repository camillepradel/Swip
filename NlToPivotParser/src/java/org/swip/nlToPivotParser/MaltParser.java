package org.swip.nlToPivotParser;

import org.swip.exchange.MyDependencyNode;
import org.swip.exchange.MyEdge;
import org.swip.exchange.DependencyTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import org.apache.log4j.Logger;
import org.maltparser.MaltParserService;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.syntaxgraph.DependencyStructure;
import org.maltparser.core.syntaxgraph.edge.Edge;
import org.maltparser.core.syntaxgraph.node.DependencyNode;
import org.maltparser.core.syntaxgraph.node.Root;
import org.maltparser.core.syntaxgraph.node.Token;

public class MaltParser {

    private static final Logger logger = Logger.getLogger(MaltParser.class);
    MaltParserService service = null;
    boolean ASK = false;
    boolean COUNT = false;
    Set<Integer> visitedNodes = null;
    Set<MyDependencyNode> visitedNodes2 = null;
    String pivotQuery = null;
    String nextElementRole = null;
    String subjectOfNextQ2 = null;
    boolean inverseQ3 = false;
    String queryObject = null;
    Token firstTokenToBrowse = null;
    MyDependencyNode firstNodeToBrowse = null;
    List<String> ignoredRelList = new ArrayList<String>(Arrays.asList(new String[]{"prep", "det", "punct", "auxpass"}));
    List<String> ignoredRoots = new ArrayList<String>(Arrays.asList(new String[]{"?"}));
    final List<String> ignoredPostags = new ArrayList<String>(Arrays.asList(new String[]{"DET", "WRB", "WDT", "IN", "IN/that", "WP", "PP", "PP$", "DT", "SENT", "RB", "RBR", "JJR", "VHP", "VHZ"}));
    final List<String> ignoredLemmas = new ArrayList<String>(Arrays.asList(new String[]{"do", "be", "show", "give", "list", "call", "all", "many"}));
    
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

//    public String dependenciesToPivot(DependencyTree dt, String lang) throws MaltChainedException {
//        logger.info("----------------------------------");
//        logger.info("+ Interpreting dependency graph...");
//        firstTokenToBrowse = null;
//        queryObject = null;
//        visitedNodes2 = new HashSet<MyDependencyNode>();
//        ASK = false;
//        COUNT = false;
//        pivotQuery = "";
//        nextElementRole = "e1q3";
//        inverseQ3 = false;
//
//        // determine if the considered query is a "When"/"Who"/"COUNT" query
//        MyDependencyNode firstTokenInSentence = dt.getDependencyNodes()[0];
//        MyDependencyNode secondTokenInSentence = dt.getDependencyNodes()[1];
//        String firstTokenLemma = firstTokenInSentence.getLemma();
//        String secondTokenLemma = secondTokenInSentence.getLemma();
//        if (firstTokenLemma.equals("who")) {
//            firstNodeToBrowse = firstTokenInSentence;
//            pivotQuery += "person";
//            queryObject = "person";
//            nextElementRole = "e2q3";
//        } else if (firstTokenLemma.equals("when")) {
//            firstNodeToBrowse = firstTokenInSentence;
//            pivotQuery += "date";
//            queryObject = "date";
//            nextElementRole = "e2q3";
//            inverseQ3 = true;
//        } else if (firstTokenLemma.equals("How") && secondTokenLemma.equals("many")) {
//            COUNT = true;
//            firstNodeToBrowse = firstTokenInSentence;
//        } else {
//            // look for query object
//            searchQueryObject(dt);
//
//            if (queryObject == null) {
//                ASK = true;
//                firstNodeToBrowse = dt.getDependencyNodes()[dt.getSentenceHeadId()];
//            }
//        }
//
//        // browse dependency tree
//        browseNode(dt, firstNodeToBrowse);
//        pivotQuery += ".";
//        if (COUNT) {
//            pivotQuery += " COUNT.";
//        } else if (ASK) {
//            pivotQuery += " ASK.";
//        } else {
//            if (pivotQuery.contains(queryObject)) {
//                pivotQuery = pivotQuery.replaceAll(queryObject, "?" + queryObject);
//            } else {
//                pivotQuery += "?" + queryObject + ".";
//            }
//        }
//
//        return pivotQuery;
//    }
//
//    private void searchQueryObject(DependencyTree dt) {
//        for (MyDependencyNode node : dt.getDependencyNodes()) {
//            int headId = node.getHeadEdge().getHeadNodeId();
//            if (headId >= 0) {
//                MyDependencyNode head = dt.getDependencyNodes()[headId];
//                // "which... ?" case
//                String headEdgeDeprel = node.getHeadEdge().getDeprel();
//                if (node.getLemma().equals("which") && node.getPostag().equals("WDT") && headEdgeDeprel.equals("det")) {
//                    // case [LEMMA=which,POSTAG=WDT] <--(DEPREL=det)-- [query object]
//                    firstNodeToBrowse = node;
//                    queryObject = head.getLemma();
//                    return;
//                }
//                // all "give me..." cases
//                int headId2 = head.getHeadEdge().getHeadNodeId();
//                if (headId2 >= 0) {
//                    MyDependencyNode head2 = dt.getDependencyNodes()[headId2];
//                    String head2EdgeDeprel = head.getHeadEdge().getDeprel();
//                    if (head.getLemma().equals("me")
//                            && (headEdgeDeprel.equals("dobj") || headEdgeDeprel.equals("null"))
//                            && head2.getLemma().equals("give")
//                            && (head2EdgeDeprel.equals("dep") || head2EdgeDeprel.equals("null"))) {
//                        // case [LEMMA=give] --(DEPREL=dep/null)--> [LEMMA=me] --(DEPREL=dobj/null)-->  [query object]
//                        firstNodeToBrowse = head2;
//                        queryObject = node.getLemma();
//                        return;
//                    }
//                    if (head.getPostag().equals("SENT")
//                            && headEdgeDeprel.equals("nsubj")
//                            && head2.getLemma().equals("give")
//                            && head2EdgeDeprel.equals("null")) {
//                        // case [LEMMA=give] --(DEPREL=null)--> [POSTAG=SENT] --(DEPREL=punct)-->  [LEMMA=me]
//                        //                                                    --(DEPREL=nsubj)-->  [query object]
//                        firstNodeToBrowse = head2;
//                        queryObject = node.getLemma();
//                        return;
//                    }
//                    if (node.getLemma().equals("give")
//                            && headEdgeDeprel.equals("nsubj")
//                            && head2.getLemma().equals("give")
//                            && head2EdgeDeprel.equals("null")) {
//                        // case [LEMMA=give] --(DEPREL=null)--> [POSTAG=SENT] --(DEPREL=nsubj)-->  [query object]
//                        //                                                    --(DEPREL=punct)-->  [LEMMA=me] (pas pris en compte)
//                        firstNodeToBrowse = head2;
//                        queryObject = node.getLemma();
//                        return;
//                    }
//                }
//                if (node.getLemma().equals("give")
//                        && (headEdgeDeprel.equals("dep") || headEdgeDeprel.equals("nsubj"))
//                        && headId2 < 0) {
//                    // case [LEMMA=give] <--(DEPREL=dep/nsubj)-- [query object] <--(DEPREL=null)-->  [ROOT]
//                    //                                                          <--(DEPREL=dep/predet)-- [LEMMA=give] (pas pris en compte)
//                    firstNodeToBrowse = node;
//                    queryObject = head.getLemma();
//                    return;
//                }
//                if (head.getLemma().equals("How") && headEdgeDeprel.equals("advmod")) {
//                    // case [LEMMA=How] --(DEPREL=advmod)--> [query object]
//                    firstNodeToBrowse = head;
//                    queryObject = node.getLemma();
//                    return;
//                }
//            }
//        }
//    }
//
//    void browseNode(DependencyTree dt, MyDependencyNode node) {
//        logger.info("browsing " + node);
//        // process token
//        String headEdgeDeprel = "";
//        String headLemma = "";
//        int headId = node.getHeadEdge().getHeadNodeId();
//        if (headId >= 0) {
//            headEdgeDeprel = node.getHeadEdge().getDeprel();
//            headLemma = dt.getDependencyNodes()[headId].getLemma();
//        }
//        if (!ignoredPostags.contains(node.getPostag()) && !ignoredLemmas.contains(node.getLemma())) {
//            if (headEdgeDeprel.equals("prt")) {
//                logger.info("token is part of its head");
//                pivotQuery = pivotQuery.replaceAll(headLemma, headLemma + " " + node.getLemma());
//            } else if (headEdgeDeprel.equals("nsubj") || headEdgeDeprel.equals("nn")) {
//                logger.info("token is maybe the type of its head...");
//
//                String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
//                        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
//                        + "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
//                        + "PREFIX pf:   <http://jena.hpl.hp.com/ARQ/property#>"
//                        + "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
//                        + "ASK"
//                        + "{"
//                        + "    ?resource rdf:type owl:Class."
//                        + "    ?resource dc:title|rdfs:label ?label."
//                        + "    (?label ?score) pf:textMatch \"" + node.getLemma() + "\"."
//                        // valeur du score à revoir (nouvelle version de larq)
//                        + "    FILTER (?score > 0.6)"
//                        + "}";
//                logger.info(query);
////                    if (sparqlServer.ask(query)) {
//                if (false) {
//                    logger.info("    and has been successfully matched to a KB class");
//                    pivotQuery = pivotQuery.replaceAll(headLemma, headLemma + "(" + node.getLemma() + ")");
//                } else {
//                    logger.info("    but has not been successfully matched to a KB class");
//                    addElementToPivotQuery(node.getLemma());
//                }
//            } else {
//                logger.info("added to pivot query");
//                addElementToPivotQuery(node.getLemma());
//            }
//        } else {
//            logger.info("ignored");
//        }
//        visitedNodes2.add(node);
//        // process children
//        for (MyEdge childEdge : node.getLeftDependentEdges()) {
//            MyDependencyNode candidateNode = dt.getDependencyNodes()[childEdge.getDependentNodeId()];
//            if (!visitedNodes2.contains(candidateNode)) {
//                browseNode(dt, candidateNode);
//            }
//        }
//        for (MyEdge childEdge : node.getRightDependentEdges()) {
//            MyDependencyNode candidateNode = dt.getDependencyNodes()[childEdge.getDependentNodeId()];
//            if (!visitedNodes2.contains(candidateNode)) {
//                browseNode(dt, candidateNode);
//            }
//        }
//        // process head
//        if (headId >= 0) {
//            MyDependencyNode candidateNode = dt.getDependencyNodes()[headId];
//            if (!visitedNodes2.contains(candidateNode)) {
//                browseNode(dt, candidateNode);
//            }
//        }
//    }
//
//    private void addElementToPivotQuery(String element) {
//        if (inverseQ3) {
//            if (nextElementRole.equals("e2q3")) {
//                pivotQuery = element + ": " + pivotQuery;
//                nextElementRole = "e1q3";
//            } else if (nextElementRole.equals("e1q3")) {
//                pivotQuery = pivotQuery.replaceFirst(":", "=");
//                pivotQuery = element + ": " + pivotQuery;
//                nextElementRole = "e2q2";
//                subjectOfNextQ2 = element;
//                inverseQ3 = false;
//            }
//        } else {
//            if (nextElementRole.equals("e1q3")) {
//                pivotQuery += element;
//                nextElementRole = "e2q3";
//            } else if (nextElementRole.equals("e2q3")) {
//                pivotQuery += ": " + element;
//                nextElementRole = "e3q3";
//            } else if (nextElementRole.equals("e3q3")) {
//                pivotQuery += "= " + element;
//                nextElementRole = "e2q2";
//                subjectOfNextQ2 = element;
//            } else if (nextElementRole.equals("e2q2")) {
//                pivotQuery += ". " + subjectOfNextQ2 + ": " + element;
//                nextElementRole = "e2q2";
//            }
//        }
//    }

    static DependencyTree changeInDependencyTree(DependencyStructure graph) throws MaltChainedException {

        int sentenceHeadId = 0;
        MyDependencyNode[] dependencyNodes = null;

        SortedSet<Integer> indices = graph.getDependencyIndices();
        dependencyNodes = new MyDependencyNode[indices.size() - 1];
        Map<Integer, Integer> indicesMap = new HashMap<Integer, Integer>();

        int i2 = 0; // index in nodes of DependencyTree
        for (int i1 = 0; i1 < indices.size(); i1++) { // index nodes of DependencyStructure
            DependencyNode node = graph.getDependencyNode(i1);
            if (node instanceof Root) {
                logger.info("root ignoré pour i=" + i1);
            } else if (node instanceof Token) {
                Token token = (Token) node;
                if (graph.getDependencyRoot().getRightmostDependent() == node) {
                    sentenceHeadId = i2;
                    logger.info("head pour i=" + i1 + " et i2=" + i2);
                }

                dependencyNodes[i2] = changeInMyDependencyNode(token);
                indicesMap.put(i1, i2);
                i2++;
            }
        }
        for (int i1 = 0; i1 < indices.size(); i1++) {
            DependencyNode node = graph.getDependencyNode(i1);
            if (node instanceof Token) {
                Token token = (Token) node;
                i2 = indicesMap.get(i1);
                // head edge
                Edge headEdge = token.getHeadEdge();
                dependencyNodes[i2].setHeadEdge(changeInMyEdge(headEdge, graph, indicesMap));
                // left dependency edges
                SortedSet<DependencyNode> dependents = token.getLeftDependents();
                MyEdge[] edges = new MyEdge[dependents.size()];
                int i = 0;
                for (DependencyNode dependent : dependents) {
                    edges[i] = changeInMyEdge(dependent.getHeadEdge(), graph, indicesMap);
                    i++;
                }
                dependencyNodes[i2].setLeftDependentEdges(edges);
                // right dependency edges
                dependents = token.getRightDependents();
                edges = new MyEdge[dependents.size()];
                i = 0;
                for (DependencyNode dependent : dependents) {
                    edges[i] = changeInMyEdge(dependent.getHeadEdge(), graph, indicesMap);
                    i++;
                }
                dependencyNodes[i2].setRightDependentEdges(edges);
            }
        }
        return new DependencyTree(sentenceHeadId, dependencyNodes);
    }

    static MyDependencyNode changeInMyDependencyNode(Token token) {
        String form = extractForm(token);
        String lemma = extractLemma(token);
        String postag = extractPostag(token);
        return new MyDependencyNode(form, lemma, postag);
    }

    static MyEdge changeInMyEdge(Edge edge, DependencyStructure graph, Map<Integer, Integer> indicesMap) throws MaltChainedException {
        int headNodeId = -1;
        int dependentNodeId = -1;
        String deprel = null;

        SortedSet<Integer> indices = graph.getDependencyIndices();

        for (int i1 = 0; i1 < indices.size(); i1++) {
            DependencyNode node = graph.getDependencyNode(i1);
            if (node instanceof Token) {
                if (node == edge.getSource()) {
                    headNodeId = indicesMap.get(i1);
                }
                if (node == edge.getTarget()) {
                    dependentNodeId = indicesMap.get(i1);
                }
            }
        }
        deprel = extractDeprel(edge);
        
        return new MyEdge(headNodeId, dependentNodeId, deprel);
    }

    static String extractForm(Token node) {
        String s = node.toString();
        int beginIndex = s.indexOf("FORM:") + 5;
        int endIndex = s.indexOf(" ", beginIndex);
        return s.substring(beginIndex, endIndex);
    }

    static String extractLemma(Token node) {
        String s = node.toString();
        int beginIndex = s.indexOf("LEMMA:") + 6;
        int endIndex = s.indexOf(" ", beginIndex);
        return s.substring(beginIndex, endIndex);
    }

    static String extractPostag(Token node) {
        String s = node.toString();
        int beginIndex = s.indexOf("POSTAG:") + 7;
        int endIndex = s.indexOf(" ", beginIndex);
        return s.substring(beginIndex, endIndex);
    }

    static String extractDeprel(Edge edge) {
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
