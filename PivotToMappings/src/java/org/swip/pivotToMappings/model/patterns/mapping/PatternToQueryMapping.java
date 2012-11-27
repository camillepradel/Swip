package org.swip.pivotToMappings.model.patterns.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.model.patterns.Pattern;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PropertyPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.LiteralPatternElement;
import org.swip.pivotToMappings.model.patterns.subpattern.PatternTriple;
import org.swip.pivotToMappings.model.patterns.subpattern.Subpattern;
import org.swip.pivotToMappings.model.patterns.subpattern.SubpatternCollection;
import org.swip.pivotToMappings.model.query.Query;
import org.swip.pivotToMappings.model.query.queryElement.Keyword;
import org.swip.pivotToMappings.model.query.queryElement.QueryElement;
import org.swip.utils.sparql.SparqlServer;

// bean! (mais pas les classes utilisees)
public class PatternToQueryMapping {
    
    private static Logger logger = Logger.getLogger(PatternToQueryMapping.class);

    private Pattern pattern = null;
    private List<ElementMapping> elementMappings = new LinkedList<ElementMapping>();
    // element mappings relevance mark
    private float rMap = -1;
    // query coverage relevance mark
    private float rQueryCov = -1;
    // pattern coverage relevance mark
    private float rPatternCov = -1;
    // respect of the number of queried elements relevance mark
    private float rNumQueried = -1;
    // final relevance mark
    private float relevanceMark = -1;
    private String sentence = null;
    private String sparqlQuery = null;
    // string description (result of toString) stored in order to display it in client application
    private String stringDescription = null;
    
    private HashMap<Integer, ArrayList<String> > generalizations;
    private HashMap<Integer, ArrayList<String> > uris;

    public PatternToQueryMapping() {
        this.generalizations = new HashMap<Integer, ArrayList<String>>();
        this.uris = new HashMap<Integer, ArrayList<String>>();
    }

    public PatternToQueryMapping(Pattern p) {
        this.pattern = p;
        this.generalizations = new HashMap<Integer, ArrayList<String>>();
        this.uris = new HashMap<Integer, ArrayList<String>>();
    }

    public PatternToQueryMapping(Pattern p, List<ElementMapping> ems) {
        this.pattern = p;
        this.elementMappings = ems;
        this.generalizations = new HashMap<Integer, ArrayList<String>>();
        this.uris = new HashMap<Integer, ArrayList<String>>();
    }

    /**
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the elementMappings
     */
    public List<ElementMapping> getElementMappings() {
        return elementMappings;
    }

    /**
     * @param elementMappings the elementMappings to set
     */
    public void setElementMappings(List<ElementMapping> elementMappings) {
        this.elementMappings = elementMappings;
    }

    /**
     * @return the rMap
     */
    public float getrMap() {
        return rMap;
    }

    public float getrMap(Query userQuery) {
        if (rMap < 0) {
            evaluateRelevanceMark(userQuery);
        }
        return rMap;
    }

    /**
     * @param rMap the rMap to set
     */
    public void setrMap(float rMap) {
        this.rMap = rMap;
    }

    /**
     * @return the rQueryCov
     */
    public float getrQueryCov() {
        return rQueryCov;
    }

    public float getrQueryCov(Query userQuery) {
        if (rQueryCov < 0) {
            evaluateRelevanceMark(userQuery);
        }
        return rQueryCov;
    }

    /**
     * @param rQueryCov the rQueryCov to set
     */
    public void setrQueryCov(float rQueryCov) {
        this.rQueryCov = rQueryCov;
    }

    /**
     * @return the rPatternCov
     */
    public float getrPatternCov() {
        return rPatternCov;
    }

    public float getrPatternCov(Query userQuery) {
        if (rPatternCov < 0) {
            evaluateRelevanceMark(userQuery);
        }
        return rPatternCov;
    }

    /**
     * @param rPatternCov the rPatternCov to set
     */
    public void setrPatternCov(float rPatternCov) {
        this.rPatternCov = rPatternCov;
    }

    /**
     * @return the rNumQueried
     */
    public float getrNumQueried() {
        return rNumQueried;
    }

    public float getrNumQueried(Query userQuery) {
        if (rNumQueried < 0) {
            evaluateRelevanceMark(userQuery);
        }
        return rNumQueried;
    }

    /**
     * @param rNumQueried the rNumQueried to set
     */
    public void setrNumQueried(float rNumQueried) {
        this.rNumQueried = rNumQueried;
    }

    /**
     * @return the relevanceMark
     */
    public float getRelevanceMark() {
        return relevanceMark;
    }

    public float getRelevanceMark(Query userQuery) {
        if (relevanceMark < 0) {
            evaluateRelevanceMark(userQuery);
        }
        return relevanceMark;
    }

    /**
     * @param relevanceMark the relevanceMark to set
     */
    public void setRelevanceMark(float relevanceMark) {
        this.relevanceMark = relevanceMark;
    }

    /**
     * @return the sentence
     */
    public String getSentence() {
        return sentence;
    }

    public String getSentence(SparqlServer server, Query userQuery, String lang) {
        if (this.sentence == null) {
            generateSentence(server, userQuery, lang);
        }
        return sentence;
    }

    /**
     * @param sentence the sentence to set
     */
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    /**
     * @return the sparqlQuery
     */
    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public String getSparqlQuery(SparqlServer sparqlServer, Query userQuery) {
        if (this.sparqlQuery == null) {
            generateSparqlQuery(sparqlServer, userQuery);
        }
        return this.sparqlQuery;
    }

    /**
     * @param sparqlQuery the sparqlQuery to set
     */
    public void setSparqlQuery(String sparqlQuery) {
        this.sparqlQuery = sparqlQuery;
    }

    public String getStringDescription() {
        return stringDescription;
    }

    public void setStringDescription(String stringDescription) {
        this.stringDescription = stringDescription;
    }
    
    public void addElementMapping(ElementMapping elementMapping) {
        this.getElementMappings().add(elementMapping);
    }

    public void addElementMappings(List<ElementMapping> elementMappings) {
        for (ElementMapping em : elementMappings) {
            this.getElementMappings().add(em);
        }
    }

    private boolean mapsQueryElement(QueryElement qe) {
        for (ElementMapping em : this.getElementMappings()) {
            if (em.queryElement == qe) {
                return true;
            }
        }
        return false;
    }

    private boolean mapsPatternElement(PatternElement pe) {
        for (ElementMapping em : this.getElementMappings()) {
            if (em.patternElement == pe) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object clone() {
        List<ElementMapping> clonedElementMappings = new LinkedList<ElementMapping>(this.getElementMappings());
        PatternToQueryMapping result = new PatternToQueryMapping();
        return new PatternToQueryMapping(this.getPattern(), clonedElementMappings);
    }

    @Override
    public String toString() {
        String result = "mapping - relevance mark = " + this.getRelevanceMark()
                + " ( query coverage = " + getrQueryCov()
                + " - pattern coverage = " + getrPatternCov()
                + " - element mappings = " + getrMap()
                + " - queried elements = " + getrNumQueried() + " )\n";
        result += getPattern().toStringWithMapping(this) + "\n";
        return result.substring(0, result.length() - 2);
    }

    // returns list because of arity of SubpatternsCollections
    public List<ElementMapping> getElementMappings(PatternElement pe) {
        List<ElementMapping> result = new LinkedList<ElementMapping>();
        for (ElementMapping petqem : this.getElementMappings()) {
            if (petqem.patternElement.equals(pe)) {
                result.add(petqem);
            }
        }
        return result;
    }

    private void evaluateRelevanceMark(Query userQuery) {

        // element mappings relevance mark
        // we assume here that all redundant query mappings have been removed (so we don't consider
        // element mappings that won't influence the final query)
        setrMap(0);
        for (ElementMapping em : this.getElementMappings()) {
            // penalize mapping of several pattern elements to one query element
            int numPe = 0;
            for (ElementMapping em2 : this.getElementMappings()) {
                /*if (em.patternElement instanceof PropertyPatternElement) {
                    PropertyPatternElement ppe = (PropertyPatternElement) em.patternElement;
                    if (!ppe.getReferedElements().contains(em2.patternElement.getId()) && em.queryElement == em2.queryElement) {
                        numPe++;
                    }
                } else if (em2.patternElement instanceof PropertyPatternElement) {
                    PropertyPatternElement ppe = (PropertyPatternElement) em2.patternElement;
                    if (!ppe.getReferedElements().contains(em.patternElement.getId()) && em2.queryElement == em.queryElement) {
                        numPe++;
                    }
                } else*/ if (em.queryElement == em2.queryElement && em.impliedBy != em2 && em2.impliedBy != em) {
                    numPe++;
                }
            }
            if (em.queryElement.isQueried()) {
                setrMap((float) (getrMap(userQuery) + em.trustMark / Math.pow(numPe, 3)));
            } else {
                setrMap((float) (getrMap(userQuery) + em.trustMark / Math.pow(numPe, 3)));
            }
        }
        if (!this.elementMappings.isEmpty()) {
            setrMap(getrMap(userQuery) / (float) (this.getElementMappings().size()));
        }

        // query coverage relevance mark
        int numMatchedElements = 0;
        for (QueryElement qe : userQuery.getQueryElements()) {
            if (this.mapsQueryElement(qe)) {
                numMatchedElements++;
            }
        }
        setrQueryCov((float) numMatchedElements / (float) userQuery.getQueryElements().size());

        // pattern coverage relevance mark
        numMatchedElements = 0;
        int numPeInFinalQuery = 0;
        for (PatternElement pe : Controller.getInstance().getPatternElementsForPattern(this.pattern)) {
            if (pe.isQualifying()) {
                if (this.mapsPatternElement(pe)) {
                    numMatchedElements++;
                }
                if (this.influencesFinalQuery(pe)) {
                    numPeInFinalQuery++;
                }
            }
        }
        setrPatternCov((float) numMatchedElements / (float) numPeInFinalQuery);

        // respect of the number of queried elements relevance mark
        int numQueriedInQuery = userQuery.getNumberOfQueriedElements();
        int numQueriedInMapping = 0;
        for (ElementMapping em : this.getElementMappings()) {
            if (em.queryElement.isQueried() && !(em.patternElement instanceof PropertyPatternElement)) {
                numQueriedInMapping++;
            }
        }
        if (numQueriedInQuery == 0) {
            setrNumQueried(1);
        } else {
            setrNumQueried(Math.min(numQueriedInQuery, numQueriedInMapping) / Math.max(numQueriedInQuery, numQueriedInMapping));
        }

        // TODO: structure respect relevance mark
        // sera utile par exemple pour: ?movie: director=josee dayan, adaptation of= $novel;
        //                              $novel: author= Fred Vargas

//        this.relevanceMark = (float) .3 * rMap + (float) .6 * rQueryCov + (float) .1 * rPatternCov;
        this.setRelevanceMark((float) (Math.pow(getrMap(userQuery), .25)
                * Math.pow(getrQueryCov(userQuery), .35)
                * Math.pow(getrPatternCov(userQuery), .25)
                * Math.pow(getrNumQueried(userQuery), .15)));
    }

    public boolean isRedundant() {
        for (ElementMapping em : this.getElementMappings()) {
            if (!influencesFinalQuery(em.getPatternElement())) {
//                System.out.println("redundant query mapping:\n" + this + "\n");
                return true;
            }
        }
        return false;
    }

    /**
     * checks wether an element mapping will influence the generation of the final graph query
     * (example of "useless" mapping: the mapping of a pattern element that appears only in a
     * subpattern collection that won't appear in the final query because its minOccurrences is 0
     * and its pivot element is not mapped)
     * @param em
     * @return
     */
    private boolean influencesFinalQuery(PatternElement pm) {
        for (Subpattern sp : this.getPattern().getSubpatterns()) {
            if (sp instanceof PatternTriple) {
                if (((PatternTriple) sp).contains(pm)) {
                    return true;
                }
            } else if (sp instanceof SubpatternCollection) {
                if (influencesFinalQueryInSubpatternCollection(pm, (SubpatternCollection) sp)) {
                    return true;
                }
            }
        }
//        System.out.println("pattern element does not influence final query:\n" + pm + "\n");
        return false;
    }

    private boolean influencesFinalQueryInSubpatternCollection(PatternElement pm, SubpatternCollection sc) {
        if (sc.getMinOccurrences() > 0 || this.mapsPatternElement(sc.getPivotElement())) {
            for (Subpattern sp : sc.getSubpatterns()) {
                if (sp instanceof PatternTriple) {
                    if (((PatternTriple) sp).contains(pm)) {
                        return true;
                    }
                } else if (sp instanceof SubpatternCollection) {
                    if (influencesFinalQueryInSubpatternCollection(pm, (SubpatternCollection) sp)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void generateSentence(SparqlServer sparqlServer, Query userQuery, String lang) {
        String localSentence = this.getPattern().getSentenceTemplate();
        String beginAggregateSentence = "";
        String queriedElements = "";
        String aggregateSentence = "";
        ArrayList<QueryElement> aggregateProcessed = new ArrayList<QueryElement>();

        logger.info("initial local sentence: " + localSentence);
        localSentence = this.getPattern().modifySentence(localSentence, this, sparqlServer);
        logger.info("modified sentence: " + localSentence);

        Collection<PatternElement> replacedPatternElements = new LinkedList<PatternElement>();
        boolean numericDataProperty = false;
        for(ElementMapping em : this.getElementMappings())
        {
            if(em instanceof KbElementMapping && !numericDataProperty) {
                numericDataProperty = ((KbElementMapping)em).isNumericDataProperty();
            }
        }
        if (userQuery.isCount()) {
            if(lang.compareTo("en") == 0)
                beginAggregateSentence = "The number of  ";
            else if(lang.compareTo("fr") == 0)
                beginAggregateSentence = "Le nombre de  ";
        }
        else if (userQuery.isAsk()) {
            if(lang.compareTo("en") == 0)
                beginAggregateSentence = "Is there";
            else if(lang.compareTo("fr") == 0)
                beginAggregateSentence = "Existe-t-il ";
        }
        else if (userQuery.isAvg()) {
            if(lang.compareTo("en") == 0)
                beginAggregateSentence = "The average of  ";
            else if(lang.compareTo("fr") == 0)
                beginAggregateSentence = "La moyenne de  ";
        }
        else if (userQuery.isMax()) {
            if(lang.compareTo("en") == 0)
                beginAggregateSentence = "The maximum of  ";
            else if(lang.compareTo("fr") == 0)
                beginAggregateSentence = "Le maximum de  ";
        }
        else if (userQuery.isMin()) {
            if(lang.compareTo("en") == 0)
                beginAggregateSentence = "The minimum of  ";
            else if(lang.compareTo("fr") == 0)
                beginAggregateSentence = "Le minimum de  ";
        }
        else if (userQuery.isSum()) {
            if(lang.compareTo("en") == 0)
                beginAggregateSentence = "The sum of  ";
            else if(lang.compareTo("fr") == 0)
                beginAggregateSentence = "La somme de  ";
        } // TODO formuler une jolie phrase  quand on a :    e1:2. AGG. 
        
        for (ElementMapping em : this.getElementMappings()) {
            QueryElement qe = em.queryElement;
            String queriedBegin = qe.isQueried() ? "<b>" : "";
            String queriedEnd = qe.isQueried() ? "</b>" : "";
            String elementSentence = "";
            boolean generalized = false;
            if(em instanceof KbElementMapping) {
                KbElementMapping kbem = (KbElementMapping) em;

                elementSentence = kbem.getStringForSentence(sparqlServer, em.patternElement.getId(), lang);

                if(kbem.isGeneralized())
                {
                    this.generalizations.put(em.patternElement.getId(), kbem.getGeneralizations());
                    this.uris.put(em.patternElement.getId(), kbem.getUris());
                    generalized = true;
                }
            } else if (em instanceof LiteralElementMapping) {
                LiteralPatternElement lpe = (LiteralPatternElement)em.getPatternElement();
                if((lpe.getType().compareTo("decimal")) == 0)
                {
                    String s = "an integer";
                    if(lang.compareTo("fr") == 0)
                        s = "un entier";
                    //localSentence = localSentence.replaceAll("__" + em.patternElement.getId() + "__", s);
                    elementSentence = s;
                }
                else
                {
                    //localSentence = localSentence.replaceAll("__" + em.patternElement.getId() + "__", queried + em.getStringForSentence(sparqlServer, lang) + queried);
                    elementSentence = em.getStringForSentence(sparqlServer, lang);
                }
            } else {
                //localSentence = localSentence.replaceAll("__" + em.patternElement.getId() + "__", queried + em.getStringForSentence(sparqlServer, lang) + queried);
                elementSentence = em.getStringForSentence(sparqlServer, lang);
            }
            
            localSentence = localSentence.replaceAll("__" + em.patternElement.getId() + "__", queriedBegin + elementSentence + queriedEnd);
            
            if(qe.isQueried() && userQuery.isSelectAggregate() && queriedElements.compareTo("") == 0)
            {
                if(generalized)
                    queriedElements = "_assoc"+em.getPatternElement().getId()+"_";
                else
                    queriedElements = elementSentence;
            }
            
            replacedPatternElements.add(em.patternElement);

            if(qe.isAggregate() && !aggregateProcessed.contains(qe))
            {
                aggregateSentence += qe.getStringRepresentation(lang, numericDataProperty, em.getPatternElement().getId(), generalized);
                aggregateProcessed.add(qe);
            }
        }
        for (PatternElement pe : Controller.getInstance().getPatternElementsForPattern(this.pattern)) {
            if (!replacedPatternElements.contains(pe)) {
                localSentence = localSentence.replaceAll("__" + pe.getId() + "__", pe.getDefaultStringForSentence(sparqlServer));
            }
        }
        

        System.out.println("PTQM : " + generalizations.toString());
        System.out.println("beginAggregate : " + beginAggregateSentence+ " || queriedElements : "+queriedElements);
        if(beginAggregateSentence.compareTo("")!=0 && queriedElements.compareTo("")!=0)
        {
            String s = "";
            if(lang.compareTo("fr") == 0)
                s = "tel que";
            else
                s = "such as";
            localSentence = beginAggregateSentence+" "+queriedElements+" "+s+", "+localSentence;
        }
        localSentence += aggregateSentence;
        this.sentence = localSentence;
    }

    public HashMap<Integer, ArrayList<String> > getGeneralizations() {
        return this.generalizations;
    }

    public HashMap<Integer, ArrayList<String> > getUris() {
        return this.uris;
    }

    private void generateSparqlQuery(SparqlServer sparqlServer, Query userQuery) {
        String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX mo: <http://purl.org/ontology/mo/>\n";
        Set<String> selectElements = new HashSet<String>();
        HashMap<String, String> numericDataPropertyElements = new HashMap<String, String>();
        String where = "";
        String query = "";
        Map<PatternElement, String> pivotsNames = new HashMap<PatternElement, String>();
        LinkedList<String> typeStrings = new LinkedList<String>();
        LinkedList<String> labelStrings = new LinkedList<String>();
        for (Subpattern sp : this.getPattern().getSubpatterns()) {
            where += sp.generateSparqlWhere(this, sparqlServer, pivotsNames, selectElements, numericDataPropertyElements, typeStrings, labelStrings);
        }
        
        for (String typeString : typeStrings) {
            where = typeString + where;
        }

        for (String labelString : labelStrings) {
            where = labelString + where;
        }
        
        String aggSelectFormat = "";
        
        /*if(!numericDataPropertyElements.isEmpty())
        {
            aggSelectFormat = "%s";
        }
        else*/ if (userQuery.isCount()) 
        {
            //select = "COUNT(" + select + " AS "+(select.replaceAll("?", "?Nb"))+")";
            aggSelectFormat = "(COUNT(%s) AS ?%sNb)";
        }
        else if(userQuery.isAvg())
        {
            aggSelectFormat = "(AVG(%s) AS ?%sAvg)";
        }
        else if(userQuery.isMax())
        {
            aggSelectFormat = "(MAX(%s) AS ?%sMax)";
        }
        else if(userQuery.isMin())
        {
            aggSelectFormat = "(MIN(%s) AS ?%sMin)";
        }
        else if(userQuery.isSum())
        {
            aggSelectFormat = "SUM(%s) AS ?%sSum";
        }
        else
            aggSelectFormat = "%s";
        
        String select = "";
        String varsSelect = "";
        for (String selectElement : selectElements) {
             varsSelect += selectElement + " ";
            select += (String.format(aggSelectFormat, selectElement, selectElement))+ " ";
        }
         if(select.compareTo("")==0)
        {
            varsSelect = "*";
            select = String.format(aggSelectFormat, "*", "All");
        }

         
        query  += prefixes+"\n"; 
        if (userQuery.isAsk()) {
            //this.setSparqlQuery();
            query += "ASK {\n";
        } else {
            //this.setSparqlQuery();
            query += "SELECT DISTINCT " + select + "\nWHERE {\n";
        }
        query += where;
        /*String aggregat = "";
        String aggregatSubject = "";
        String cond = "";*/
        boolean isAgg = false;
        String orderBy = "";
        String filter = "";
        String groupby = "";
        String having = "";
        for(QueryElement q : userQuery.getQueryElements())
        {
            if(q.isAggregate())
            {
                Keyword k = (Keyword)q;
                String aggregat = k.getAggregate();
                String aggregatSubject = k.getVarName();
                String cond = numericDataPropertyElements.get(k.getKeywordValue())+k.getCond();
                isAgg = true;

                System.out.println("aggregat : "+aggregat+" || aggregatSubject : "+aggregatSubject+" || cond : "+cond);
                
                if(!numericDataPropertyElements.isEmpty())
                {
                    if(cond == null || cond.compareTo(aggregatSubject) == 0)
                    {
                        if(orderBy.compareTo("") != 0)
                            orderBy += " AND ";
                        orderBy += aggregatSubject;
                    }
                    else
                    {
                        if(filter.compareTo("") !=0)
                            filter += " AND ";
                        filter += cond;
                    }
                }
                else if(select.compareTo("*") != 0 && cond.compareTo("null") != 0)
                {
                    if(groupby.compareTo("") != 0)
                    {
                        groupby += " AND ";
                        having += " AND ";
                    }
                    groupby += varsSelect;
                    having += aggregat;
                }
            }
        }
        if(filter.compareTo("") != 0)
        {
            query += "FILTER ("+filter+") \n }";
        }
        else if(orderBy.compareTo("") != 0)
        {
            query += "}\n";
            query += "ORDER BY DESC("+orderBy+")\n";
            query += "LIMIT 1"; // TODO possibilité d'augmenter le 1 par ex : les 10 plus grand ... */
        }
        else if(groupby.compareTo("") != 0)
        {
            query += "}\n";
            query += "GROUPBY "+groupby+" \n";
            query += "HAVING "+having+"\n";
        }
        else
            query += "}";
        
        
        this.setSparqlQuery(query);
    }
}
