package org.swip.pivotToMappings.model.patterns.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import org.swip.pivotToMappings.model.KbTypeEnum;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import com.hp.hpl.jena.query.QuerySolution;
import org.apache.log4j.Logger;
import org.swip.utils.sparql.SparqlServer;


public class KbElementMapping extends ElementMapping {

    private static final Logger logger = Logger.getLogger(KbElementMapping.class);

    private KbTypeEnum kbType;
    private boolean generalized;
    private ArrayList<String> generalizations;
    private ArrayList<String> uris;

    String firstlyMatchedOntResourceUri = null;
    String bestLabel = null;
    

    public KbElementMapping() {
        this.generalized = false;
        this.generalizations = new ArrayList<String>();
        this.uris = new ArrayList<String>();
    }

    public KbElementMapping(PatternElement pe, QueryElement qe, float trustMark, String firstlyMatchedOntResource, String bestLabel, ElementMapping impliedBy, KbTypeEnum kbType) {
        super(pe, qe, trustMark, impliedBy);
        this.firstlyMatchedOntResourceUri = firstlyMatchedOntResource;
        this.bestLabel = bestLabel;
        this.kbType = kbType;

        this.generalized = false;
        this.generalizations = new ArrayList<String>();
        this.uris = new ArrayList<String>();
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

    public ArrayList<String> getGeneralizations() {
        return this.generalizations;
    }

    public ArrayList<String> getUris() {
        return this.uris;
    }

    @Override
    public String getStringForSentence(SparqlServer sparqlServer, String lang) {
        return getStringForSentence(sparqlServer, -1, lang);
    }

    public String getStringForSentence(SparqlServer sparqlServer, int genId, String lang) {
        String ret;

        if(genId == -1)
            return null;

        if(this.kbType  == KbTypeEnum.CLASS) {
            if(this.generalized) {
                ret = "_gen" + genId + "_";
            } else {
                this.generateGeneralizedLabels(sparqlServer, this.generalizeClass(sparqlServer, this.getFirstlyMatchedOntResourceUri()));
                if(this.generalizations.size() <= 1)
                {
                    String s ="";
                    if(lang.compareTo("fr") == 0)
                        s = "un(e)";
                    else
                        s = "a";
                    ret = s+" "+ this.bestLabel;
                    this.generalizations.clear();
                }
                else {
                    ret = "_gen" + genId + "_";
                    this.generalized = true;
                }
            }
        } else if(this.kbType == KbTypeEnum.IND ) {
            if(this.generalized) {
                ret = "_gen" + genId + "_";
            } else {
                this.generateGeneralizedLabels(sparqlServer, this.generalizeInd(sparqlServer, this.getFirstlyMatchedOntResourceUri()));
                if(this.generalizations.size() <= 1)
                {
                    ret = this.bestLabel;
                    this.generalizations.clear();
                }
                else {
                    ret = "_gen" + genId + "_";
                    this.generalized = true;
                }
            }

        } else if(this.kbType == KbTypeEnum.PROP) {
            if(this.generalized) {
                ret = "_gen" + genId + "_";
            } else {
                this.generateGeneralizedLabels(sparqlServer, this.generalizeProp(sparqlServer, this.getFirstlyMatchedOntResourceUri()));
                if(this.generalizations.size() <= 1)
                {
                    ret = this.bestLabel;
                    this.generalizations.clear();
                }
                else {
                    ret = "_gen" + genId + "_";
                    this.generalized = true;
                }
            }
        }
        else {
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

    private void generateGeneralizedLabels(SparqlServer sparqlServer, Iterable<QuerySolution> sols) {
        System.out.println("==============================");
        System.out.println("Generalizing " + this.getFirstlyMatchedOntResourceUri());

        String article = "";
        if(this.kbType == KbTypeEnum.CLASS)
        {
            article = "un(e) ";
            this.uris.add("\"<" + this.getFirstlyMatchedOntResourceUri() + ">\"");
        }

        if(this.kbType != KbTypeEnum.PROP)
            this.generalizations.add("\"" + article + this.getBestLabel() + "\"");
            

        if(this.kbType == KbTypeEnum.IND)
            article = "un(e) ";

        for(QuerySolution sol : sols) {
            Iterator<String> varNames = sol.varNames();
            while(varNames.hasNext()) {
                String varName = varNames.next();
                String labelGen = sparqlServer.getALabel(sol.get(varName).toString());

                if(labelGen != null) {
                    System.out.println("Label found : " + labelGen);
                    this.generalizations.add("\"" + article + labelGen + "\"");
                    this.uris.add("\"<" + sol.get(varName).toString() + ">\"");

                    if(this.kbType == KbTypeEnum.IND && this.uris.size() == 1)
                        this.uris.add("\"<" + sol.get(varName).toString() + ">\"");
                }
            }
        }

        System.out.println("==============================");
    }

    public boolean isGeneralized() {
        return this.generalized;
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
        return ((this.kbType == KbTypeEnum.PROP ) ? true : false);
    }
    
    public boolean isNumericDataProperty(){
        return (this.kbType == KbTypeEnum.NUMDATAPROP);
    }

    @Override
    public String toString() {
        return "[KbElementMapping]" + patternElement + " -> " + queryElement + " - trust mark=" + trustMark + " - matched = " + firstlyMatchedOntResourceUri + " - label = " + bestLabel;
    }
    
}
