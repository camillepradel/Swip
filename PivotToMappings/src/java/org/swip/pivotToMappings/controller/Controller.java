package org.swip.pivotToMappings.controller;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;
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

    // vive les loutres! *2 plus plus pour essayer
    
    private static final Logger logger = Logger.getLogger(Controller.class);
    static final boolean remote = true;
    static Controller staticController = null;
    private SparqlServer sparqlServer = null;
    private List<Pattern> patterns = null;
    private Map<Pattern, List<PatternElement>> patternElements = new HashMap<Pattern, List<PatternElement>>();
    private Map<PatternElement, List<ElementMapping>> elementMappings = new HashMap<PatternElement, List<ElementMapping>>();

    public Controller() {
        this.sparqlServer = null;
        this.patterns = null;
    }

    public static Controller getInstance() {
        if (staticController == null) {
            staticController = new Controller();
            staticController.createSparqlServer(remote);
            staticController.loadPatterns();
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

    private void createSparqlServer(boolean remote) {
        logger.info("Creating sparql server:");
        logger.info("----------------------\n");
        long time = System.currentTimeMillis();
        if (remote) {
            this.sparqlServer = new RemoteSparqlServer("http://localhost:2020/music");
        } else {
            List<String> uris = new LinkedList<String>();
            uris.add("D:/QALDworkshop/musicbrainz/musicbrainz.owl");
            uris.add("D:/QALDworkshop/musicbrainz/artists.rdf");
            uris.add("D:/QALDworkshop/musicbrainz/albums.rdf");
//            uris.add("D:/QALDworkshop/musicbrainz/tracks.rdf");
            uris.add("D:/QALDworkshop/musicbrainz/relations_artist_to_artist.rdf");
            uris.add("D:/QALDworkshop/musicbrainz/relations_album_to_artist.rdf");
//            uris.add("D:/QALDworkshop/musicbrainz/artists-verysmall.rdf");
//            uris.add("D:/QALDworkshop/musicbrainz/albums-verysmall.rdf");
            uris.add("D:/QALDworkshop/musicbrainz/tracks-verysmall.rdf");
            uris.add("D:/QALDworkshop/musicbrainz/complements.rdf");
            this.sparqlServer = new LocalSparqlServer(uris);
        }
        logger.info("Sparql server created");
        logger.info("time for creating sparql server: " + (System.currentTimeMillis() - time) + "ms");
        logger.info("\n================================================================\n");
    }

    void loadPatterns() {
        if (this.sparqlServer == null) {
            logger.info("There is no defined sparql server");
            return;
        } else {
            this.patterns = new LinkedList<Pattern>();
            try {
                logger.info("Loading patterns:");
                logger.info("----------------\n");
                long time = System.currentTimeMillis();
                // read patterns on file and instantiate them
                try {
                    ANTLRInputStream input = new ANTLRInputStream(this.getClass().getClassLoader().getResourceAsStream("../../patterns-musicbrainz.txt"));
                    patternsDefinitionGrammarLexer lexer = new patternsDefinitionGrammarLexer(input);
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    patternsDefinitionGrammarParser parser = new patternsDefinitionGrammarParser(tokens);
                    this.patterns = parser.patterns();
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
                for (Pattern pattern : this.patterns) {
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
                        pe.preprocess(this.sparqlServer);
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
        }
    }

    public List<PatternToQueryMapping> getBestMappings(String pivotQueryString, int numMappings) {
//        PropertyConfigurator.configure(projectPath+"/resources/log4j.properties");
        logger.info("new log file");
        if (this.sparqlServer == null) {
            logger.info("There is no defined sparql server");
        } else if (this.patterns == null) {
            logger.info("There is no loaded patterns");
        } else {
            try {
                logger.info("Parsing pivot query:");
                logger.info("-------------------\n");
                final Query userQuery = createQuery(pivotQueryString);
                logger.info("parsed query: " + userQuery.toString() + "\n");
                logger.info("\n================================================================\n");
                logger.info("Matching query elements to knowledge base and mapping pattern elements:");
                logger.info("----------------------------------------------------------------------\n");
                matchQueryElements(userQuery);
                logger.info("\n================================================================\n");
                logger.info("Possible element mappings are:");
                logger.info("-----------------------------\n");
                for (Pattern pattern : this.patterns) {
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
                for (Pattern p : patterns) {
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
                        stringToDisplay += " - Sentence: " + nextBestMapping.getSentence(sparqlServer, userQuery) + "\n\n";
                    }
                    if (printMappings) {
                        stringToDisplay += " - Mapping: " + nextBestMapping.toString() + "\n\n";
                    }
                    if (printSparql) {
                        stringToDisplay += " - Sparql query:\n" + nextBestMapping.getSparqlQuery(sparqlServer, userQuery) + "\n\n";
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

    private void matchQueryElements(Query userQuery) {
        // first clear previous mappings
        for (List<PatternElement> pes : this.patternElements.values()) {
            for (PatternElement pe : pes) {
                pe.resetForNewQuery();
                this.elementMappings.put(pe, new LinkedList<ElementMapping>());
            }
        }
        // matching step
        userQuery.matchElements(this.sparqlServer);
        // mapping
        for (Pattern p : this.patterns) {
            p.finalizeMappings(this.sparqlServer);
        }
    }

    public List<Pattern> getPatterns() {
        return this.patterns;
    }
}
