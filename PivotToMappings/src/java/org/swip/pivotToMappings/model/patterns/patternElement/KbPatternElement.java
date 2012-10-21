package org.swip.pivotToMappings.model.patterns.patternElement;

import com.hp.hpl.jena.query.QuerySolution;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.exceptions.PatternsParsingException;
import org.swip.pivotToMappings.model.KbTypeEnum;
import org.swip.pivotToMappings.model.patterns.mapping.ElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.KbElementMapping;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.utils.sparql.SparqlServer;

public abstract class KbPatternElement extends PatternElement {

    private String uri;
    private static float ancestorCoef = (float) 0.5;
    private static float descendantCoef = (float) 0.9;
//    private List<KbElementMapping> mappings = new LinkedList<KbElementMapping>();

    public KbPatternElement() {
    }

    public KbPatternElement(String uri, boolean qualifying) {
        this.uri = uri;
        this.qualifying = qualifying;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

//    /**
//     * @return the ancestorCoef
//     */
//    public static float getAncestorCoef() {
//        return ancestorCoef;
//    }
//
//    /**
//     * @param aAncestorCoef the ancestorCoef to set
//     */
//    public static void setAncestorCoef(float aAncestorCoef) {
//        ancestorCoef = aAncestorCoef;
//    }
//
//    /**
//     * @return the descendantCoef
//     */
//    public static float getDescendantCoef() {
//        return descendantCoef;
//    }
//
//    /**
//     * @param aDescendantCoef the descendantCoef to set
//     */
//    public static void setDescendantCoef(float aDescendantCoef) {
//        descendantCoef = aDescendantCoef;
//    }

    /**
     * @return the mappings
     */
//    public List<KbElementMapping> getMappings() {
//        return mappings;
//    }

//    @Override
//    public List<? extends ElementMapping> getElementMappings() {
//        return getMappings();
//    }

    /**
     * @param mappings the mappings to set
     */
//    public void setMappings(List<KbElementMapping> mappings) {
//        this.mappings = mappings;
//    }

//    /**
//     * @return the logger
//     */
//    public static Logger getLogger() {
//        return logger;
//    }
//
//    /**
//     * @param aLogger the logger to set
//     */
//    public static void setLogger(Logger aLogger) {
//        logger = aLogger;
//    }

    @Override
    public void preprocess(SparqlServer sparqlServer) throws PatternsParsingException {
        if (qualifying) {
            addPatternElementMatching(getUri(), new PatternElementMatching(this, 1));
//            addPatternElementMatchingAncestorsSparql(getUri(), 1, sparqlServer);
            addPatternElementMatchingDescendantsSparql(getUri(), 1, sparqlServer);
        }
    }

    void addPatternElementMatchingAncestorsSparql(String uri, float childrenMark, SparqlServer sparqlServer) throws PatternsParsingException {
        float mark = childrenMark * KbPatternElement.ancestorCoef;
        Iterable<QuerySolution> results = null;
        try {
            String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                    + "SELECT ?parent "
                    + "WHERE { "
                    + "       <" + uri + "> rdfs:subClassOf ?parent. "
                    + "      } ";
            results = sparqlServer.select(query);
        } catch (Exception e) {
            throw new PatternsParsingException("Error occured while querying sparql endpoint:\n" + e.getMessage());
        }
        for (QuerySolution sol : results) {
            String superClassString = sol.getResource("parent").getURI();
            addPatternElementMatching(superClassString, new PatternElementMatching(this, mark));
            addPatternElementMatchingAncestorsSparql(superClassString, mark, sparqlServer);
        }
    }

    void addPatternElementMatchingDescendantsSparql(String uri, float childrenMark, SparqlServer sparqlServer) throws PatternsParsingException {
        float mark = childrenMark * KbPatternElement.descendantCoef;
        Iterable<QuerySolution> results = null;
        try {
            String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                    + "SELECT ?child "
                    + "WHERE { "
                    + "       ?child rdfs:subClassOf <" + uri + ">. "
                    + "      } ";
            results = sparqlServer.select(query);
        } catch (Exception e) {
            throw new PatternsParsingException("Error occured while querying sparql endpoint:\n" + e.getMessage());
        }
        for (QuerySolution sol : results) {
            String subClassString = sol.getResource("child").getURI();
            addPatternElementMatching(subClassString, new PatternElementMatching(this, mark));
            addPatternElementMatchingDescendantsSparql(subClassString, mark, sparqlServer);
        }
    }

    public void addKbMapping(QueryElement qe, float trustMark, String firstlyMatched, String bestLabel, ElementMapping impliedBy, KbTypeEnum kbType) {
        for (ElementMapping em : Controller.getInstance().getElementMappingsForPatternElement(this)) {
            KbElementMapping kbem = (KbElementMapping)em;
            if (kbem.getQueryElement() == qe && kbem.getFirstlyMatchedOntResourceUri().equals(firstlyMatched)) {
                if (trustMark > kbem.getTrustMark()) {
                    kbem.changeValues(trustMark, bestLabel, kbType);
                }
                return;
            }
        }
        Controller.getInstance().addElementMappingForPatternElement(new KbElementMapping(this, qe, trustMark, firstlyMatched, bestLabel, impliedBy, kbType), this);
    }

    @Override
    public void resetForNewQuery() {
//        setMappings(new LinkedList<KbElementMapping>());
        this.mappingIsCompulsory = false;
    }

    @Override
    public String toString() {
        return "[KbPatternElement]" + getUri() + " - id=" + id + " - qualifying=" + qualifying;
    }
}
