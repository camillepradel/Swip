package org.swip.pivotToMappings.model.patterns.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import org.swip.pivotToMappings.model.KbTypeEnum;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.pivotToMappings.sparql.SparqlServer;
import com.hp.hpl.jena.query.QuerySolution;
import org.apache.log4j.Logger;


public class KbElementMapping extends ElementMapping {

    private static final Logger logger = Logger.getLogger(KbElementMapping.class);

    String firstlyMatchedOntResourceUri = null;
    String bestLabel = null;
    KbTypeEnum kbType;

    public KbElementMapping() {
    }

    public KbElementMapping(PatternElement pe, QueryElement qe, float trustMark, String firstlyMatchedOntResource, String bestLabel, ElementMapping impliedBy, KbTypeEnum kbType) {
        super(pe, qe, trustMark, impliedBy);
        this.firstlyMatchedOntResourceUri = firstlyMatchedOntResource;
        this.bestLabel = bestLabel;
        this.kbType = kbType;
    }
    
    public KbTypeEnum getKbType() {
        return this.kbType;
    }

    public String getBestLabel() {
        return bestLabel;
    }

    public void setBestLabel(String bestLabel) {
        this.bestLabel = bestLabel;
    }

    public String getFirstlyMatchedOntResourceUri() {
        return firstlyMatchedOntResourceUri;
    }

    public void setFirstlyMatchedOntResourceUri(String firstlyMatchedOntResourceUri) {
        this.firstlyMatchedOntResourceUri = firstlyMatchedOntResourceUri;
    }

    @Override
    public String getStringForSentence(SparqlServer sparqlServer) {
        String ret;

        if(this.kbType  == KbTypeEnum.CLASS) {
            String gen = this.generateGeneralizedLabels(sparqlServer, this.generalizeClass(sparqlServer, this.getFirstlyMatchedOntResourceUri()));
            if(gen == null)
                ret = "un(e) " + this.bestLabel;
            else
                ret = "_selBeg_un(e) " + this.bestLabel + gen + "_selEnd_";
        } else if(this.kbType  == KbTypeEnum.IND) {
            String gen = this.generateGeneralizedLabels(sparqlServer, this.generalizeInd(sparqlServer, this.getFirstlyMatchedOntResourceUri()));
            if(gen == null)
                ret = this.bestLabel;
            else
                ret = "_selBeg_" + this.bestLabel + gen + "_selEnd_";
        } else if(this.kbType  == KbTypeEnum.PROP) {
            String gen = this.generateGeneralizedLabels(sparqlServer, this.generalizeProp(sparqlServer, this.getFirstlyMatchedOntResourceUri()));
            if(gen == null)
                ret = this.bestLabel;
            else
                ret = "_selBeg_" + this.bestLabel + gen + "_selEnd_";
        } else {
            ret = this.bestLabel;
        }

        return ret;
    }

    private Iterable<QuerySolution> generalizeClass(SparqlServer sparqlServer, String c) {
        ArrayList<String> ret = new ArrayList<String>();

        String sparqlQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
        sparqlQuery += "SELECT ?classes WHERE { <" + c + "> rdfs:subClassOf ?classes } LIMIT 5";
        
        logger.info("Generalizing " + c + "...");

        return sparqlServer.select(sparqlQuery);
    }

    private Iterable<QuerySolution> generalizeInd(SparqlServer sparqlServer, String i) {
        ArrayList<String> ret = new ArrayList<String>();

        String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
        sparqlQuery += "SELECT ?type WHERE { <" + i + "> rdf:type ?type } LIMIT 5";
        
        logger.info("Generalizing " + i + "...");

        return sparqlServer.select(sparqlQuery);
    }

    private Iterable<QuerySolution> generalizeProp(SparqlServer sparqlServer, String p) {
        ArrayList<String> ret = new ArrayList<String>();

        String sparqlQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
        sparqlQuery += "SELECT ?classes WHERE { <" + p + "> rdfs:subPropertyOf ?classes } LIMIT 5";
        
        logger.info("Generalizing " + p + "...");

        return sparqlServer.select(sparqlQuery);
    }

    private String generateGeneralizedLabels(SparqlServer sparqlServer, Iterable<QuerySolution> sols) {
        String ret = null;
        boolean hasBeenGeneralized = false;
        ArrayList<String> labels = new ArrayList<String>();

        for(QuerySolution sol : sols) {
            Iterator<String> varNames = sol.varNames();
            while(varNames.hasNext()) {
                String varName = varNames.next();
                String labelGen = sparqlServer.getALabel(sol.get(varName).toString());

                if(labelGen != null) {
                    labels.add(labelGen);
                    hasBeenGeneralized = true;
                }
            }
        }

        if(hasBeenGeneralized) {
            String article = "";
            if(this.kbType == KbTypeEnum.CLASS)
                article = "un(e) ";

            for(String s : labels) {
                ret += "_selSep_" + article + s;
            }
        }

        return ret;
    }

    public void changeValues(float trustMark, String bestLabel, KbTypeEnum kbType) {
        this.trustMark = trustMark;
        this.bestLabel = bestLabel;
        this.kbType = kbType;
    }
    
    public boolean isClass() {
        return ((this.kbType == KbTypeEnum.CLASS) ? true : false);
    }
    
    public boolean isInd() {
        return ((this.kbType == KbTypeEnum.IND) ? true : false);
    }
    
    public boolean isProp() {
        return ((this.kbType == KbTypeEnum.PROP) ? true : false);
    }

    @Override
    public String toString() {
        return "[KbElementMapping]" + patternElement + " -> " + queryElement + " - trust mark=" + trustMark + " - matched = " + firstlyMatchedOntResourceUri + " - label = " + bestLabel;
    }
    
}
