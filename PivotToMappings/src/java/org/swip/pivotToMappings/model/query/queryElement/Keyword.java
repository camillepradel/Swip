package org.swip.pivotToMappings.model.query.queryElement;

import com.hp.hpl.jena.query.QuerySolution;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.model.KbTypeEnum;
import org.swip.pivotToMappings.model.patterns.patternElement.KbPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.LiteralPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElementMatching;
import org.swip.pivotToMappings.sparql.SparqlServer;
import org.swip.pivotToMappings.stemmer.SnowballStemmer;
import org.swip.pivotToMappings.stemmer.englishStemmer;

public class Keyword extends QueryElement {

    int id = 0;
    String keywordValue = null;
     String aggregat = "";
    String cond = "";
    boolean isAggregate = false;
    
    private static final Logger logger = Logger.getLogger(QueryElement.class);

    public Keyword() {
        super();
    }

    public Keyword(boolean queried, int id, String keywordValue) {
        super();
        super.queried = queried;
        this.id = id;
        this.keywordValue = keywordValue;
    }
    
    /*public Keyword(boolean queried, int id, String keywordValue, String aggregat) {
        super();
        if(aggregat != null)
        {
            super.queried = queried;
            this.id = id;
            this.keywordValue = keywordValue;
            this.aggregat = aggregat;
            this.isAggregate = true;
        }
        else
        {
            super.queried = queried;
            this.id = id;
            this.keywordValue = keywordValue;
        }
        
    }*/


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeywordValue() {
        return keywordValue;
    }

    public String getVarName()
    {
        return "?"+(this.keywordValue.replaceAll(" ", "_"));
    }

    public void setKeywordValue(String keywordValue) {
        this.keywordValue = keywordValue;
    }
    
    public void setCond(String agg, String cond)
    {
        this.isAggregate = true;
        this.aggregat = agg;
        this.cond = cond;
    }


    public void match(SparqlServer serv) {
        logger.info(this.keywordValue + " matches with: ");
        long time = System.currentTimeMillis();

        HashMap<String, String> labelsMap = new HashMap<String, String>();
        HashMap<String, Float> scoresMap = new HashMap<String, Float>();

        StringTokenizer st2 = new StringTokenizer(this.keywordValue, " \t\n\r\f-_");
        String stringToMatch = "";
        while (st2.hasMoreTokens()) {
            // remove plural form if any (done manually because Lucene score doesn't handle this)
            // FIXME: is it possible to configure it in Lucene?
            String nextToken = st2.nextToken();
            if (nextToken.endsWith("ies")) {
                nextToken = nextToken.substring(0, nextToken.length()-3) + "y";
            } else if (nextToken.endsWith("s")) {
                nextToken = nextToken.substring(0, nextToken.length()-1);
            }
            stringToMatch += nextToken + " ";
        }

        String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "  PREFIX dc:  <http://purl.org/dc/elements/1.1/>"
                + "  PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>"
                + "SELECT ?subj ?label ?score ?str "
                + "WHERE { "
                + "       (?label ?score ) pf:textMatch ('" + stringToMatch.trim() + "~' 0.6) "
                + "       { ?subj dc:title ?label. BIND(str(?label) AS ?str).} "
                + "                  UNION "
                + "       { ?subj rdfs:label ?label. BIND(str(?label) AS ?str).} "
                + "      } ";
 
        Iterable<QuerySolution> results = serv.select(query);
        for (QuerySolution qs : results) {
            String uri = qs.get("subj").toString();

            boolean changeMaps = false;
            float score = ((com.hp.hpl.jena.rdf.model.Literal) qs.get("score")).getFloat();
            if (scoresMap.containsKey(uri)) {
                if (score > scoresMap.get(uri)) {
                    changeMaps = true;
                }
            } else {
                changeMaps = true;
            }

            if (changeMaps) {
                String label = qs.get("str").toString();
                labelsMap.put(uri, label);
                scoresMap.put(uri, score);
            }
        }

        PriorityQueue<Match> matches = new PriorityQueue<Match>();
        for (String uri : labelsMap.keySet()) {            

            float bestTrustMark = scoresMap.get(uri);
            String bestLabel = labelsMap.get(uri);
            boolean isClass = false;
            boolean isIndividual = false;
            boolean isProperty = false;
            boolean isNumericDataProperty = false;

            List<String> types = serv.listTypes(uri);
            if (serv.isClass(types)) {
                isClass = true;
                logger.info(" (o) class " + uri);
            } 
            else if(serv.isDataProperty(types) && serv.isNumericDataProperty(uri)) {
                isNumericDataProperty = true;
                isProperty = true;
                logger.info(" (o) numeric data property " + uri);
            } else if (serv.isProperty(types)) {
                isProperty = true;
                logger.info(" (o) property " + uri);
            } else {
                isIndividual = true;
                logger.info(" (o) individual " + uri + "(type: ");
                for (String type : types) {
                    logger.info(type + ", ");
                }
                logger.info(")");
                if (this.queried) {
                    bestTrustMark *= trustMarkDiminutionWhenQueriedInstance;
                    logger.info(" (queried instance)");
                } else {
                    bestTrustMark *= trustMarkDiminutionWhenInstance;
                }
            }
            if (((isClass || isIndividual) && (roles.contains(QeRole.E2Q3)))
                    || ((isProperty) && (roles.contains(QeRole.E1Q1) || roles.contains(QeRole.E1Q23) || roles.contains(QeRole.E3Q3)))) {
                bestTrustMark *= trustMarkDiminutionWhenIncompatible;
                logger.info(" (incompatible type)");
            }
            
            logger.info("  --  matched label: " + bestLabel);
            logger.info("  --  trust mark = " + bestTrustMark);

            if (isIndividual) {
                for (String type : types) {
                    this.addMatch(type, bestTrustMark, uri, bestLabel, true, matches, KbTypeEnum.IND);
                }
            } else if(isClass) {
                this.addMatch(uri, bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.CLASS);
            } else if(isProperty) {
                if(isNumericDataProperty)
                {
                    this.addMatch(uri, bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.DATAPROPNUM);
                }
                else
                    this.addMatch(uri, bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.PROP);
            } else {
                this.addMatch(uri, bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.NONE);
                // if query element matches a property and has not a property role in query,
                // we add a mapping from the property's range to that query element, with the same trust mark
//                if (isProperty && !this.roles.usedAsProperty()) {
//                    log.println("       matches a property but has no property role in query -> matches property's range as well", 2);
//                    List<String> ranges = serv.listRanges(uri);
//                    for (String range : ranges) {
//                        log.println("       (o) class " + range + " -- trust mark = " + bestTrustMark, 2);
//                        this.addMatch(range, bestTrustMark, range, serv.getALabel(range), false, matches);
//                    }
//                }
                // TODO: inverse de ce qu'il y a au dessus: reporter un matching de classe vers les propriétés
                // qui ont cette classe en range
            }

        }
        map(matches, serv);

        long time2 = System.currentTimeMillis();
       logger.info("time: " + (double) (time2 - time) / 1000. + "s to match keyword " + this.keywordValue + "\n");
    }

    void addMatch(String resourceUri, float trustMark, String firstlyMatched, String label, boolean checkMappingCondition, PriorityQueue<Match> matches, KbTypeEnum kbType) {
        final int maxNumMatches = 50;
        if (matches.size() < maxNumMatches || trustMark > matches.peek().trustMark || trustMark >= 1.0) {
            matches.add(new Match(resourceUri, trustMark, firstlyMatched, label, checkMappingCondition, kbType));
            if (matches.size() > maxNumMatches && matches.peek().trustMark < 1.0) {
                matches.poll();
            }
        }
    }

    void map(PriorityQueue<Match> matches, SparqlServer serv) {
        for (Match match : matches) {
            Collection<PatternElementMatching> concernedPEMs = PatternElement.patternElementMatchings.get(match.resourceUri);
            if (concernedPEMs != null) {
                for (PatternElementMatching pem : concernedPEMs) {
                    boolean doMapping = true;
                    if (match.checkMappingCondition && pem.getPatternElement().getMappingCondition() != null) {
                        String askQuery = pem.getPatternElement().getMappingCondition().replace("__" + pem.getPatternElement().getId() + "__", "<" + match.firstlyMatched + ">");
                        doMapping = serv.ask("ASK {" + askQuery + "}");
                    }
                    if (doMapping) {
                        float mapTrustMark = match.trustMark * pem.getTrustMarkFactor();
                        PatternElement pe = pem.getPatternElement();
                        if (pe instanceof KbPatternElement) {
                            ((KbPatternElement) pe).addKbMapping(this, mapTrustMark, match.firstlyMatched, match.label, null, match.kbType);
                        } else if (pe instanceof LiteralPatternElement) {
                            ((LiteralPatternElement) pe).addLiteralMapping(this, mapTrustMark, null);
                        }
                    }
                }
            }
        }
    }

    private class Match implements Comparable<Match> {

        String resourceUri = null;
        float trustMark = 0;
        String firstlyMatched = null;
        String label = null;
        boolean checkMappingCondition;
        KbTypeEnum kbType;

        public Match(String resourceUri, float trustMark, String firstlyMatched, String label, boolean checkMappingCondition, KbTypeEnum kbType) {
            this.resourceUri = resourceUri;
            this.trustMark = trustMark;
            this.firstlyMatched = firstlyMatched;
            this.label = label;
            this.checkMappingCondition = checkMappingCondition;
            this.kbType = kbType;
        }

        public int compareTo(Match o) {
            if (this.trustMark < o.trustMark) {
                return -1;
            }
            if (this.trustMark == o.trustMark) {
                return 0;
            }
            return 1;
        }
    }

    private String stemTerm(String term) {
        if (term == null) {
            return null;
        }
        SnowballStemmer stemmer = englishStemmer.getStemmer();
        stemmer.setCurrent(term);
        stemmer.stem();
        return stemmer.getCurrent();
    }
    
    
    /*
     * return null if is not an aggregate
     */
    public String getAggregate()
    {
        String ret = null;
        if(this.aggregat!=null)
        {
            ret = "("+this.aggregat+"("+this.getVarName()+")"+this.cond+")";
        }
        return ret;
    }
    
    public String getCond()
    {
        return this.cond;
    }
    
    public boolean isAggregate()
    {
        return this.isAggregate;
    }
    

    @Override
    public String toString() {
        return "Keyword{\"" + keywordValue + "\" - queried=" + queried + " - id=" + id + " - varName = "+this.getVarName()+" - cond = "+this.aggregat+"("+this.getVarName()+")"+this.cond+"}";
    }

    @Override
    public String getStringRepresentation(String lang, boolean isNumericDataProperty) {
        //return (this.queried ? "?" : "") + (this.id > 0 ? "$" + this.id : "") + this.keywordValue;
        String ret = "";
        
        if(this.isAggregate)
        {
            String s1 = "", s2 = "", s3 = "", count = "", sum ="", min = "", max = "", avg = "";
            if(lang.compareTo("en") == 0)
            {
                s1 = "wich";
                s2 = "must be";
                s3 = "of";
                count = "the number";
                sum = "the sum";
                min = "the minimum";
                max = "the maximum";
                avg = "the average";
            }
            else if(lang.compareTo("fr") == 0)
            {
                s1 = "dont";
                s2 = "doit être";
                s3 = "de";
                count = "le nombre";
                sum = "la somme";
                min = "le minimum";
                max = "le maximum";
                avg = "la moyenne";
            }
            
            
            
            ret += s1+" ";
            if(this.cond.compareTo("") != 0 && !isNumericDataProperty)
            {
                if(this.aggregat.compareTo("COUNT") == 0)
                {
                    ret += count+" "+s3+" ";
                }
                else if(this.aggregat.compareTo("SUM") == 0)
                {
                    ret += sum+" "+s3+" ";
                }
                else if(this.aggregat.compareTo("MIN") == 0)
                {
                    ret += min+" "+s3+" ";
                }
                else if(this.aggregat.compareTo("MAX") == 0)
                {
                    ret += max+" "+s3+" ";
                }
                else if(this.aggregat.compareTo("AVG") == 0)
                {
                    ret += avg+" "+s3+" ";
                }
            }
            ret += this.keywordValue+" "+s2+" ";
            if(this.cond.compareTo("") != 0 )
            {
                ret += this.cond;
            }
            else if(this.aggregat.compareTo("MAX") == 0)
            {
                ret += max;
            }
            else if(this.aggregat.compareTo("MIN") == 0)
            {
                ret += min;
            }
            
        }
        
        return ret;
    }
        
     public String getStringValue()
    {
        return this.keywordValue;
    }
}
