package org.swip.nlToPivotParser;

import org.swip.exchange.MyDependencyNode;
import org.swip.exchange.MyEdge;
import org.swip.exchange.DependencyTree;
import java.util.HashMap;
import java.util.Map;
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
    MaltParserService serviceFr = null;
    MaltParserService serviceEn = null;
    
    public MaltParser() {
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

        MaltParserService serviceToUse = null;
        if (lang.equals("en")) {
            if (serviceEn == null) {
                try {
                    serviceEn = new MaltParserService();
                    // on mirail server
                    serviceEn.initializeParserModel("-c engmalt.linear-1.7 -m parse -w /home/cpradel/apache-tomcat-7.0.32/webapps/NlToPivotParser/WEB-INF/classes/ -lfi parser.log");
                    // on irit server
//                    serviceEn.initializeParserModel("-c engmalt.linear-1.7 -m parse -w /usr/local/WWW/recherches/MELODI/swip/WEB-INF/classes/ -lfi parser.log");
                    // on my computer
//                     serviceEn.initializeParserModel("-c engmalt.linear-1.7 -m parse -w /mnt/data/gitSwip/NlToPivotParser/build/web/WEB-INF/classes/ -lfi parser.log");
                } catch (MaltChainedException ex) {
                    logger.error(ex.getMessage());
                }
            }
            serviceToUse = serviceEn;
        } else if (lang.equals("fr")) {
            if (serviceFr == null) {
                try {
                    serviceFr = new MaltParserService();
                    // on mirail server
                    serviceFr.initializeParserModel("-c fremalt-1.7 -m parse -w /home/cprade/apache-tomcat-7.0.32/webapps/NlToPivotParser/WEB-INF/classes/ -lfi parser.log");
                    // on irit server
//                    serviceFr.initializeParserModel("-c fremalt-1.7 -m parse -w /usr/local/WWW/recherches/MELODI/swip/WEB-INF/classes/ -lfi parser.log");
                    // on my computer
                    // serviceFr.initializeParserModel("-c fremalt-1.7 -m parse -w /mnt/data/gitSwip/NlToPivotParser/build/web/WEB-INF/classes/ -lfi parser.log");
                } catch (MaltChainedException ex) {
                    logger.error(ex.getMessage());
                }
            }
            serviceToUse = serviceFr;
        } else {
            // Todo: rise exception
        }

        DependencyStructure graph = serviceToUse.parse(formatedTokens);
        logger.info("MaltParser result:");
        displayDependencyTree(graph);
        return graph;
    }
    
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
