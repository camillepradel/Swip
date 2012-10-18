/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.nlToPivotParser;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author camille
 */
public class StanfordParser {

    private static final Logger logger = Logger.getLogger(StanfordParser.class);
    LexicalizedParser lp = null;
    GrammaticalStructureFactory gsf = null;
    boolean ASK = false;
    boolean COUNT = false;
    String pivotQuery = "";
    Set<String> queryObjects = new HashSet<String>();
    List<String> listList = new ArrayList<String>(Arrays.asList(new String[]{"List", "list", "Give", "give"}));
    List<String> whoList = new ArrayList<String>(Arrays.asList(new String[]{"Who", "who"}));
    List<String> depRelations = new ArrayList<String>(Arrays.asList(new String[]{"nn", "dobj", "prep_on", "prep_of", "nsubj", "dep"}));

    public StanfordParser() {
    }

    LexicalizedParser getLp() {
        if (lp == null) {
            logger.info("Initializing parser...");
            lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
            gsf = new PennTreebankLanguagePack().grammaticalStructureFactory();
        }
        return lp;
    }

    GrammaticalStructureFactory getGsf() {
        if (gsf == null) {
            gsf = new PennTreebankLanguagePack().grammaticalStructureFactory();
        }
        return gsf;
    }

    public String nlToPivot(String nlQuery) {
        pivotQuery = "";
        logger.info("Parsing query...");
        Tree parse = getLp().apply(nlQuery);
        logger.info(parse.pennString());
        logger.info("Analysing grammatical structure...");
        GrammaticalStructure gs = getGsf().newGrammaticalStructure(parse);
        Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed(true);
        logger.info(tdl);

        logger.info("Interpreting dependency graph...");
        queryObjects = new HashSet<String>();
        ASK = false;
        COUNT = false;
        for (TypedDependency td : tdl) {

            String reln = td.reln().toString();
            String gov = removeId(td.gov().toString());
            String dep = removeId(td.dep().toString());

            if (td.reln().toString().equals("root")) {
                if (dep.equals("Was")) {
                    ASK = true;
                } else if (listList.contains(dep)) {
                    // do nothing
                } else {
                    queryObjects.add(dep);
                }
            } else if (depRelations.contains(reln)) {
                processDependency(reln, gov, dep);
            }
        }

        if (ASK) {
            pivotQuery += " ASK.";
        } else if (COUNT) {
            pivotQuery += " COUNT.";
        } else {
            for (String queryObject : queryObjects) {
                if (!pivotQuery.contains(queryObject)) {
                    pivotQuery += "--" + queryObject + "--.";
                }
                pivotQuery = pivotQuery.replaceAll(queryObject, "?" + queryObject);
            }
            if (queryObjects.isEmpty()) {
                pivotQuery = "Error! - no query object: " + pivotQuery;
            } else if (queryObjects.size() >= 2) {
                pivotQuery = "Error! - several query objects: " + pivotQuery;
            }
        }
        return pivotQuery;
    }

    private void processDependency(String reln, String gov, String dep) {
        if (gov.equals("Was")) {
            // ASK already put to true
            pivotQuery += "E1: " + dep + ".\n";
        } else if (listList.contains(gov)) {
            pivotQuery += "E1: " + dep + ".\n";
            queryObjects.add(dep);
        } else if (whoList.contains(dep)) {
            pivotQuery += "E1: " + gov + ".\n";
        } else {
            pivotQuery += "E2: " + gov + ":" + dep + ".\n";
        }
    }

    private String removeId(String label) {
        return label.substring(0, label.lastIndexOf('-'));
    }
}
