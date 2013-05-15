package org.swip.nlToPivotRules;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.swip.exchange.DependencyTree;
import org.swip.exchange.MyDependencyNode;
import org.swip.exchange.MyEdge;

@Path("/rest/")
public class NlToPivotRulesWS {

    private static final Logger logger = Logger.getLogger(NlToPivotRulesWS.class);
    boolean ASK = false;
    boolean COUNT = false;
    boolean DURATION = false;
    Set<Integer> visitedNodes = null;
    Set<MyDependencyNode> visitedNodes2 = null;
    String pivotQuery = null;
    String nextElementRole = null;
    String subjectOfNextQ2 = null;
    boolean inverseQ3 = false;
    String queryObject = null;
    MyDependencyNode firstNodeToBrowse = null;
    List<String> ignoredRelList = new ArrayList<String>(Arrays.asList(new String[]{"prep", "det", "punct", "auxpass"}));
    List<String> ignoredRoots = new ArrayList<String>(Arrays.asList(new String[]{"?"}));
    final List<String> ignoredPostags = new ArrayList<String>(Arrays.asList(new String[]{"DET", "WRB", "WDT", "IN", "IN/that", "WP", "PP", "PP$", "DT", "SENT", "RB", "RBR", "JJR", "VHP", "VHZ"}));
    final List<String> ignoredLemmas = new ArrayList<String>(Arrays.asList(new String[]{"do", "be", "show", "give", "list", "call", "all", "many", "have"}));
    // sparql server
    //RemoteSparqlServer sparqlServer = new RemoteSparqlServer("http://swipserver:8080/joseki/musicbrainz");

    // must be POST because of parameters size
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("dependenciesToPivot")
    public String dependenciesToPivot(@FormParam("dependencyTree") @DefaultValue("") DependencyTree dependencyTree,
            @FormParam("lang") String lang,
            @FormParam("pos") @DefaultValue("treeTagger") String posTagger,
            @FormParam("dep") @DefaultValue("malt") String depParser) throws ParseException {

        logger.info("received dependency tree: " + dependencyTree);
        logger.info("Query language: " + lang);
        logger.info("POS tagger: " + posTagger);
        logger.info("Dependency parser: " + depParser);

        if (posTagger.equals("treeTagger") && depParser.equals("malt")) {
            pivotQuery = dependenciesToPivot(dependencyTree, lang);
        } else if (posTagger.equals("stanfordParser") && depParser.equals("stanfordParser")) {
            pivotQuery = "Parameters not supported!";
        } else {
            pivotQuery = "Parameters not supported!";
        }
        logger.info("Returned pivot query: " + pivotQuery);
        logger.info("");
        return pivotQuery;
    }

    private String dependenciesToPivot(DependencyTree dt, String lang) {
        logger.info("----------------------------------");
        logger.info("+ Interpreting dependency graph...");
        queryObject = null;
        visitedNodes2 = new HashSet<MyDependencyNode>();
        ASK = false;
        COUNT = false;
        pivotQuery = "";
        nextElementRole = "e1q3";
        inverseQ3 = false;

        // determine if the considered query is a "When"/"Who"/"COUNT" query
        MyDependencyNode firstTokenInSentence = dt.getDependencyNodes()[0];
        MyDependencyNode secondTokenInSentence = dt.getDependencyNodes()[1];
        String firstTokenLemma = firstTokenInSentence.getLemma();
        String secondTokenLemma = secondTokenInSentence.getLemma();
        if (firstTokenLemma.equals("who")) {
            firstNodeToBrowse = firstTokenInSentence;
            pivotQuery += "person";
            queryObject = "person";
            nextElementRole = "e2q3";
        } else if (firstTokenLemma.equals("when")) {
            firstNodeToBrowse = firstTokenInSentence;
            pivotQuery += "date<?>";
            queryObject = "date<?>";
            nextElementRole = "e2q3";
            inverseQ3 = true; // pour inverser le e1 avec le e3: que pour la date?
        } else if (firstTokenLemma.equals("How") && secondTokenLemma.equals("many")) {
            COUNT = true;
            firstNodeToBrowse = firstTokenInSentence;
        }  
        else {
            // look for query object
            searchQueryObject(dt);
            if (queryObject == null) {
                searchQueryObjectAccordingToWordsOrder(dt);
                firstNodeToBrowse = dt.getDependencyNodes()[dt.getSentenceHeadId()];
                if (queryObject == null) {
                    ASK = true;
                }
                
            }
        }

        // browse dependency tree
        browseNode(dt, firstNodeToBrowse);
        pivotQuery += ".";
        if (COUNT) {
            pivotQuery += " COUNT.";
        } else if (ASK) {
            pivotQuery += " ASK.";
        } 
       
            else {
              if (pivotQuery.contains(queryObject)) {
                  pivotQuery = pivotQuery.replaceAll(queryObject, "?" + queryObject);
              
              } else {
                  pivotQuery += "?" + queryObject + ".";
              }
        }

        return pivotQuery;
    }

    private void searchQueryObject(DependencyTree dt) {
        for (MyDependencyNode node : dt.getDependencyNodes()) {
            int headId = node.getHeadEdge().getHeadNodeId();
            if (headId >= 0) {
                MyDependencyNode head = dt.getDependencyNodes()[headId];
                // "which... ?" case
                String headEdgeDeprel = node.getHeadEdge().getDeprel();
                if (node.getLemma().equals("which") && node.getPostag().equals("WDT") && headEdgeDeprel.equals("det")) {
                    // case [LEMMA=which,POSTAG=WDT] <--(DEPREL=det)-- [query object]
                    firstNodeToBrowse = node;
                    queryObject = head.getLemma();
                    logger.info("query object : which me"+node.getLemma() +"--- first node"+firstNodeToBrowse.getLemma() );
                    return;
                }
                if (node.getLemma().equals("long") && head.getLemma().toLowerCase().equals("how")) {
                    DURATION=true;
                    MyDependencyNode verbe = dt.getDependencyNodes()[head.getHeadEdge().getHeadNodeId()];
                    //searching for the subject
                   // for (MyEdge edgeDep : verbe.getRightDependentEdges()){
                    
                   // }
                    queryObject = "duration";
                    nextElementRole = "e2q2";
                    subjectOfNextQ2 = "duration";
                    firstNodeToBrowse = verbe;
                    logger.info("query object : how long"+queryObject +"--- verbe"+verbe.getLemma() );
                    return;
                 } 
                // all "give me..." cases
                int headId2 = head.getHeadEdge().getHeadNodeId();
                 
                 
                if (headId2 >= 0) {
                    MyDependencyNode head2 = dt.getDependencyNodes()[headId2];
                   
                    
                    String head2EdgeDeprel = head.getHeadEdge().getDeprel();
                    
                    if (head.getLemma().equals("me")
                            && (headEdgeDeprel.equals("dobj") || headEdgeDeprel.equals("null"))
                            && head2.getLemma().equals("give")
                            && (head2EdgeDeprel.equals("dep") || head2EdgeDeprel.equals("null"))) {
                        // case [LEMMA=give] --(DEPREL=dep/null)--> [LEMMA=me] --(DEPREL=dobj/null)-->  [query object]
                        firstNodeToBrowse = head2;
                        queryObject = node.getLemma();
                        logger.info("query object : me-give"+node.getLemma() +"---first node"+firstNodeToBrowse.getLemma());
                        return;
                    }
                    if (head.getPostag().equals("SENT")
                            && headEdgeDeprel.equals("nsubj")
                            && head2.getLemma().equals("give")
                            && head2EdgeDeprel.equals("null")) {
                        // case [LEMMA=give] --(DEPREL=null)--> [POSTAG=SENT] --(DEPREL=punct)-->  [LEMMA=me]
                        //                                                    --(DEPREL=nsubj)-->  [query object]
                        firstNodeToBrowse = head2;
                        queryObject = node.getLemma();
                        logger.info("query object : give null"+node.getLemma() +"---first node"+firstNodeToBrowse.getLemma());
                        return;
                    }
                    if (node.getLemma().equals("give")
                            && headEdgeDeprel.equals("nsubj")
                            && head2.getLemma().equals("give")
                            && head2EdgeDeprel.equals("null")) {
                        // case [LEMMA=give] --(DEPREL=null)--> [POSTAG=SENT] --(DEPREL=nsubj)-->  [query object]
                        //                                                    --(DEPREL=punct)-->  [LEMMA=me] (pas pris en compte)
                        firstNodeToBrowse = head2;
                        queryObject = node.getLemma();
                        logger.info("query object : give nsubj"+node.getLemma() +"---first node"+firstNodeToBrowse.getLemma());
                        return;
                    }
                }
               
                if (node.getLemma().equals("give")
                        && (headEdgeDeprel.equals("dep") || headEdgeDeprel.equals("nsubj"))
                        && headId2 < 0 && ! head.getPostag().contains("V")) {
                    // case [LEMMA=give] <--(DEPREL=dep/nsubj)-- [query object] <--(DEPREL=null)-->  [ROOT]
                    //                                                          <--(DEPREL=dep/predet)-- [LEMMA=give] (pas pris en compte)
                    firstNodeToBrowse = node;
                    queryObject = head.getLemma();
                    logger.info("query object : give nsubj/dep"+head.getLemma() +"---first node"+firstNodeToBrowse.getLemma());
                   
                    return;
                }
                if (head.getLemma().equals("How") && headEdgeDeprel.equals("advmod")) {
                    // case [LEMMA=How] --(DEPREL=advmod)--> [query object]
                    firstNodeToBrowse = head;
                    queryObject = node.getLemma();
                    return;
                }
            }
        }
    }

    private void searchQueryObjectAccordingToWordsOrder(DependencyTree dt) {
        MyDependencyNode firstWord=dt.getDependencyNodes()[0];
        MyDependencyNode secondWord=dt.getDependencyNodes()[1];
        MyDependencyNode thirdWord=dt.getDependencyNodes()[2];
        // cas despérant de l arbre incorrect pour gve me all
        if (firstWord.getLemma().equals("give")
                        && secondWord.getLemma().equals("me")) {
                  
                    if ( thirdWord.getLemma().equals("all")) {
                        queryObject =dt.getDependencyNodes()[3].getLemma();
                    }
                    else queryObject = thirdWord.getLemma();
                    logger.info("query object : give me avec mauvais arbre"+queryObject);
                   
                    return;
                }
    }
                
    
    
    private void browseNode(DependencyTree dt, MyDependencyNode node) {
        logger.info("browsing " + node);
        // process token
        String headEdgeDeprel = "";
        String headLemma = "";
        String headPostag = "";
        String currentRole = "";
            if (node.getLemma().equals("bear")) {  //car treetagger lemmatise born en bear...
                logger.info("lemma " + node.getLemma()+"--");
                node.setLemma("be born");
                logger.info("lemma " + node.getLemma()+"--");
            }
            if (node.getLemma().equals("@card@")) {
                logger.info("lemma " + node.getLemma()+"--");
               node.setLemma(node.getForm()); //car treetagger met @card@ comme lemme aux valeurs num
                logger.info("lemma " + node.getLemma()+"--");
            }
            
        
        int headId = node.getHeadEdge().getHeadNodeId();
        if (headId >= 0) {
            headEdgeDeprel = node.getHeadEdge().getDeprel();
            headLemma = dt.getDependencyNodes()[headId].getLemma();
            
            headPostag = dt.getDependencyNodes()[headId].getPostag();
        }
        if (!ignoredPostags.contains(node.getPostag()) && !ignoredLemmas.contains(node.getLemma())) {
            if (headEdgeDeprel.equals("prt")) {
                logger.info("token is part of its head");
                pivotQuery = pivotQuery.replaceAll(headLemma, headLemma + " " + node.getLemma());
            } else if (((headEdgeDeprel.equals("nsubj") || headEdgeDeprel.equals("nn") || headEdgeDeprel.equals("amod")))&&(! (headPostag.equals("VVP") ||headPostag.equals("VVD") ||headPostag.equals("VV")) ) && pivotQuery.contains(headLemma) ) {
                logger.info("**token is maybe the type of its head...");

                if (true) {
                    logger.info("    and has lead to the creation of a Q2 -"+headLemma+" -:- "+ node.getLemma());
                    
                 
                    currentRole=nextElementRole; // on tombe sur un Q2 alors qu'on est au milieu d'un traitement de Q3
                 //   if (!nextElementRole.equals("e2q3")) //cas où on aurait détecté un type alors que faux
                        nextElementRole = "e2q2";
                    subjectOfNextQ2 = headLemma;
                    addElementToPivotQuery(node.getLemma());
                    nextElementRole=currentRole;
                    
                    
                } 
            } else {
//                logger.info("added to pivot query"+query);
                addElementToPivotQuery(node.getLemma());
            }
        } else {
            logger.info("ignored");
        }
        visitedNodes2.add(node);
        // process children
        for (MyEdge childEdge : node.getLeftDependentEdges()) {
            MyDependencyNode candidateNode = dt.getDependencyNodes()[childEdge.getDependentNodeId()];
            if (!visitedNodes2.contains(candidateNode)) {
                browseNode(dt, candidateNode);
            }
        }
        for (MyEdge childEdge : node.getRightDependentEdges()) {
            MyDependencyNode candidateNode = dt.getDependencyNodes()[childEdge.getDependentNodeId()];
            if (!visitedNodes2.contains(candidateNode)) {
                browseNode(dt, candidateNode);
            }
        }
        // process head
        if (headId >= 0) {
            MyDependencyNode candidateNode = dt.getDependencyNodes()[headId];
            if (!visitedNodes2.contains(candidateNode)) {
                browseNode(dt, candidateNode);
            }
        }
    }

    private void addElementToPivotQuery(String element) {
        logger.info("**pivot avant "+pivotQuery+" ajout de "+element+" en tant que "+nextElementRole);
        if (inverseQ3) {
            logger.info("**inverse");
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
                else if (nextElementRole.equals("e2q2")) {
               pivotQuery += ". "+subjectOfNextQ2 + ": " + element;
                nextElementRole = "e1q3";
                }
            
        } else {
            logger.info("**not inverse");
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
                pivotQuery =  subjectOfNextQ2 + ": " + element + ". "+pivotQuery;
                if (DURATION) {
                    DURATION=false;
                    subjectOfNextQ2=element;
                }
                //nextElementRole = "e2q2";
            }
        }
        logger.info("**pivot après "+pivotQuery+" nextElementRole "+nextElementRole+"subjectOfNextQ2 "+subjectOfNextQ2+" inverse "+inverseQ3 );
    }
}
