package org.swip.pivotToMappings.controller;

import com.hp.hpl.jena.query.QuerySolution;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.swip.pivotToMappings.exceptions.KeywordRuntimeException;
import org.swip.pivotToMappings.exceptions.LexicalErrorRuntimeException;
import org.swip.pivotToMappings.exceptions.LiteralRuntimeException;
import org.swip.pivotToMappings.exceptions.PatternsParsingException;
import org.swip.pivotToMappings.exceptions.QueryParsingException;
import org.swip.pivotToMappings.exceptions.SyntaxErrorRuntimeException;
import org.swip.pivotToMappings.model.patterns.Pattern;
import org.swip.pivotToMappings.model.patterns.antlr.patternsDefinitionGrammarLexer;
import org.swip.pivotToMappings.model.patterns.antlr.patternsDefinitionGrammarParser;
import org.swip.pivotToMappings.model.patterns.mapping.ElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.KbElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.query.Query;
import org.swip.pivotToMappings.model.query.antlr.userQueryGrammarLexer;
import org.swip.pivotToMappings.model.query.antlr.userQueryGrammarParser;
import org.swip.pivotToMappings.model.query.queryElement.Keyword;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.pivotToMappings.sparql.LocalSparqlServer;
import org.swip.pivotToMappings.sparql.RemoteSparqlServer;
import org.swip.pivotToMappings.sparql.SparqlServer;

public class Controller {
    
    private static final Logger logger = Logger.getLogger(Controller.class);
    static Controller staticController = null;
    private final static ArrayList<KbConfiguration>  kbConfs = new ArrayList<KbConfiguration>();
    private HashMap<String, SparqlServer> sparqlServers = null;
    private HashMap<String, List<Pattern>> patternsMap = null;
    private HashMap<String, String> langs = null;
    private Map<Pattern, List<PatternElement>> patternElements = new HashMap<Pattern, List<PatternElement>>();
    private Map<PatternElement, List<ElementMapping>> elementMappings = new HashMap<PatternElement, List<ElementMapping>>();

    public Controller() {
        this.sparqlServers = null;
        this.patternsMap = null;
        
        this.kbConfs.add(new KbConfiguration("cinema", "http://localhost:2021/cinema", "patterns-cinema.txt", "fr"));
        this.kbConfs.add(new KbConfiguration("cinemaDist", "http://swipserver:2021/cinema", "patterns-cinema.txt", "fr"));
        this.kbConfs.add(new KbConfiguration("cinemaLocal", "http://localhost:2020/cinema", "patterns-cinema.txt", "fr"));
        this.kbConfs.add(new KbConfiguration("music", "http://localhost:2020/music", "patterns-musicbrainz.txt", "en"));
    }

    public static Controller getInstance() {
        if (staticController == null) {
            staticController = new Controller();
            staticController.createSparqlServer();
            staticController.loadPatterns();
            staticController.initLangs();
        }
        return staticController;
    }

    public List<PatternElement> getPatternElementsForPattern(Pattern p) {
        return this.patternElements.get(p);
    }

    public void setPatternElementsForPattern(List<PatternElement> pes, Pattern p) {
        this.patternElements.put(p, pes);
    }

    public List<ElementMapping> getElementMappingsForPatternElement(PatternElement pe) {
        return this.elementMappings.get(pe);
    }

    public void addElementMappingForPatternElement(ElementMapping em, PatternElement pe) {
        this.elementMappings.get(pe).add(em);
    }

    private void initLangs()
    {
        this.langs = new HashMap<String, String>();
        for(KbConfiguration kbConf : this.kbConfs)
        {
            this.langs.put(kbConf.name, kbConf.lang);
        }
    }
    
    private void createSparqlServer() {
        logger.info("Creating sparql server:");
        logger.info("----------------------\n");
        
        this.sparqlServers = new HashMap<String, SparqlServer>();
        
        long time = System.currentTimeMillis();
        for(KbConfiguration kbConf : this.kbConfs)
        {
            this.sparqlServers.put(kbConf.name, new RemoteSparqlServer(kbConf.urlSparql));
        }

        logger.info("Sparql server created");
        logger.info("time for creating sparql server: " + (System.currentTimeMillis() - time) + "ms");
        logger.info("\n================================================================\n");
    }

    void loadPatterns() {
        if (this.sparqlServers == null) {
            logger.info("There is no defined sparql server");
            return;
        } else {
            this.patternsMap = new HashMap<String, List<Pattern>>();
            for(KbConfiguration kbConf : this.kbConfs)
            {
                
                List<Pattern> l = new LinkedList<Pattern>();
                try {
                    logger.info("Loading patterns:");
                    logger.info("----------------\n");
                    long time = System.currentTimeMillis();
                    // read patterns on file and instantiate them
                    try {
                        ANTLRInputStream input = new ANTLRInputStream(this.getClass().getClassLoader().getResourceAsStream(kbConf.patternsPath));
                        patternsDefinitionGrammarLexer lexer = new patternsDefinitionGrammarLexer(input);
                        CommonTokenStream tokens = new CommonTokenStream(lexer);
                        patternsDefinitionGrammarParser parser = new patternsDefinitionGrammarParser(tokens);
                        l = parser.patterns();
                    } catch (RecognitionException ex) {
                        logger.error(ex);
                        throw new PatternsParsingException("RecognitionException: " + ex.getMessage());
                    } catch (IOException ex) {
                        logger.error(ex);
                        throw new PatternsParsingException("IOException: " + ex.getMessage());
                    } catch (SyntaxErrorRuntimeException ex) {
                        logger.error(ex);
                        throw new PatternsParsingException("Syntax error at " + ex.getMessage());
                    } catch (LexicalErrorRuntimeException ex) {
                        logger.error(ex);
                        throw new PatternsParsingException("Syntax error at " + ex.getMessage());
                    } catch (RuntimeException ex) {
                        logger.error(ex);
                        throw new PatternsParsingException(ex.getMessage());
                    }
                    // display loaded patterns
                    logger.info("Patterns:\n");
                    for (Pattern pattern : l) {
                        logger.info(pattern.toString());
                    }
                    logger.debug("Pattern elements:\n");
                    for (List<PatternElement> pes : this.patternElements.values()) {
                        for (PatternElement pe : pes) {
                            logger.debug(pe.toString());
                        }
                    }
                    logger.info("Pattern loaded");
                    logger.info("time for loading patterns: " + (System.currentTimeMillis() - time) + "ms");
                    logger.info("================================================================");
                    // preprocess patterns
                    logger.info("Preprocessing patterns:");
                    logger.info("----------------------\n");
                    time = System.currentTimeMillis();
                    for (List<PatternElement> pes : this.patternElements.values()) {
                        for (PatternElement pe : pes) {
                            pe.preprocess(this.sparqlServers.get(kbConf.name));
                        }
                    }
                    logger.info("\nPattern element matchings:\n");
                    PatternElement.printPatternElementMatchings();
                    logger.info("Pattern preprocessed");
                    logger.info("time for preprocessing patterns: " + (System.currentTimeMillis() - time) + "ms");
                    logger.info("\n================================================================\n");
                } catch (PatternsParsingException ex) {
                    logger.info("An error occured while parsing patterns:\n" + ex.getMessage());
                    logger.info("Patterns loading aborted");
                    logger.error(ex);
                }
                this.patternsMap.put(kbConf.name, l);
            }
        }
    }

    public List<PatternToQueryMapping> getBestMappings(String pivotQueryString, int numMappings, String kbName) {
//        PropertyConfigurator.configure(projectPath+"/resources/log4j.properties");
        logger.info("KbName : " + kbName);
        if (this.sparqlServers == null) {
            logger.info("There is no defined sparql server");
        } else if (this.patternsMap == null) {
            logger.info("There is no loaded patterns");
        } else {
            try {
                logger.info("Parsing pivot query : <"+pivotQueryString+"> \n");
                logger.info("-------------------\n");
                final Query userQuery = createQuery(pivotQueryString);
                logger.info("parsed query: " + userQuery.toString() + "\n");
                logger.info("\n================================================================\n");
                logger.info("Matching query elements to knowledge base and mapping pattern elements:");
                logger.info("----------------------------------------------------------------------\n");
                matchQueryElements(userQuery, kbName);
                logger.info("\n================================================================\n");
                logger.info("Possible element mappings are:");
                logger.info("-----------------------------\n");
                for (Pattern pattern : this.patternsMap.get(kbName)) {
                    pattern.printElementMappings();
                }
                logger.info("\n================================================================\n");
                logger.info("Generating and evaluating possible mappings");
                logger.info("--------------------------------------\n");
                // determine the numMappings best mappings
                PriorityQueue<PatternToQueryMapping> bestMappingsPQ = new PriorityQueue<PatternToQueryMapping>(numMappings, new Comparator<PatternToQueryMapping>() {

                    @Override
                    public int compare(PatternToQueryMapping o1, PatternToQueryMapping o2) {
                        if (o1.getRelevanceMark(userQuery) < o2.getRelevanceMark(userQuery)) {
                            return -1;
                        }
                        if (o1.getRelevanceMark(userQuery) == o2.getRelevanceMark(userQuery)) {
                            return 0;
                        }
                        return 1;
                    }
                });
                float lowestBestMappingMark = 0;
                int totalNumMappings = 0;
                for (Pattern p : this.patternsMap.get(kbName)) {
                    for (PatternToQueryMapping ptqm : p.getMappingsIterable(userQuery)) {
                        totalNumMappings++;
                        if (ptqm.getRelevanceMark(userQuery) > lowestBestMappingMark) {
                            bestMappingsPQ.add(ptqm);
                            if (bestMappingsPQ.size() > numMappings) {
                                bestMappingsPQ.poll();
                                lowestBestMappingMark = bestMappingsPQ.peek().getRelevanceMark(userQuery);
                            }
                        }
                    }
                }
                // tranfer best mappings from priority queue to list, store their string description and display them
                final boolean printSentences = true;
                final boolean printMappings = true;
                final boolean printSparql = true;
                String stringToDisplay = bestMappingsPQ.size() + " best mappings:\n";
                List<PatternToQueryMapping> bestMappingsList = new LinkedList<PatternToQueryMapping>();
                int numQuery = 1;
                while (!bestMappingsPQ.isEmpty()) {
                    PatternToQueryMapping nextBestMapping = bestMappingsPQ.poll();

                    // Generalization
                    
                    bestMappingsList.add(nextBestMapping);
                    // store the string description of each mapping in order to be able to display it in the client application
                    nextBestMapping.setStringDescription(nextBestMapping.toString());
                    stringToDisplay = numQuery++ + ")  trust mark = "
                            + nextBestMapping.getRelevanceMark(userQuery)
                            + "(rMap=" + nextBestMapping.getrMap(userQuery)
                            + " - rPatternCov=" + nextBestMapping.getrPatternCov(userQuery)
                            + " - rQueryCov=" + nextBestMapping.getrQueryCov(userQuery)
                            + " - rNumQueried=" + nextBestMapping.getrNumQueried(userQuery) + ")\n";
                    if (printSentences) {
                        stringToDisplay += " - Sentence: " + nextBestMapping.getSentence(this.sparqlServers.get(kbName), userQuery, this.langs.get(kbName)) + "\n\n";
                    }
                    if (printMappings) {
                        stringToDisplay += " - Mapping: " + nextBestMapping.toString() + "\n\n";
                    }
                    if (printSparql) {
                        stringToDisplay += " - Sparql query:\n" + nextBestMapping.getSparqlQuery(this.sparqlServers.get(kbName), userQuery) + "\n\n";
                    }
                    stringToDisplay += "\n";
                    logger.info(stringToDisplay);
                }
                logger.info("Query processed");
                
                return bestMappingsList;
            } catch (QueryParsingException ex) {
                logger.info("An error occured while parsing query: " + pivotQueryString + "\n" + ex.getMessage());
                logger.info("Query process aborted");
            }
        }
        return null;
    }

    /**
     * create query object by parsing the pivot query string representation
     * @param userQuery
     * @return
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

    private void matchQueryElements(Query userQuery, String kbName) {
        
        SparqlServer sparqlServer = this.sparqlServers.get(kbName);
        List<Pattern> patterns = this.patternsMap.get(kbName);
        
        // first clear previous mappings
        for (List<PatternElement> pes : this.patternElements.values()) {
            for (PatternElement pe : pes) {
                pe.resetForNewQuery();
                this.elementMappings.put(pe, new LinkedList<ElementMapping>());
            }
        }
        // matching step
        userQuery.matchElements(sparqlServer);
        // mapping
        for (Pattern p : patterns) {
            p.finalizeMappings(sparqlServer);
        }
    }

    /*public List<Pattern> getPatterns() {
        return this.patterns;
    }*/
    
    public String processQuery(String sparqlQuery, String kbName)
    {
        
        SparqlServer sparqlServer = this.sparqlServers.get(kbName);
        logger.info("process Query : "+kbName);
        logger.info("Query : "+sparqlQuery);
        JSONObject response = new JSONObject();
        ArrayList<JSONObject> queryResults = new ArrayList();
        logger.info("Waiting for sparql server response ... ");
        Iterable<QuerySolution> sols = sparqlServer.select(sparqlQuery);
        logger.info("Response received.");
        for(QuerySolution sol : sols)
        {
            JSONObject query = new JSONObject();
            Iterator<String> varNames = sol.varNames();
            while(varNames.hasNext())
            {
                String varName = varNames.next();
                query.put("res", "" + sol.get(varName) + "");
            }
            queryResults.add(query);
        }
        response.put("content", queryResults);
        logger.info("return to client : "+response.toString());
        return response.toString();
    }
}
