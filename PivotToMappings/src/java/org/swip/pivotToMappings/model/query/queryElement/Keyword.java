package org.swip.pivotToMappings.model.query.queryElement;

import com.hp.hpl.jena.query.QuerySolution;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;
import org.swip.pivotToMappings.model.KbTypeEnum;
import org.swip.pivotToMappings.model.patterns.patternElement.KbPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.LiteralPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PatternElementMatching;
import org.swip.utils.sparql.SparqlServer;
import org.swip.pivotToMappings.stemmer.SnowballStemmer;
import org.swip.pivotToMappings.stemmer.englishStemmer;

public class Keyword extends QueryElement {

    private static final Logger logger = Logger.getLogger(Keyword.class);
    public static final Map<String, String> jokerTypeProperties = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put("http://purl.org/ontology/mo/release_type", "http://purl.org/ontology/mo/ReleaseType");
        }
    });
    public static final String jokerTypePropertiesString = generate(jokerTypeProperties);
    private static String generate(Map<String, String> jokerTypeProperties) {
        String result = "(";
        for (String prop : jokerTypeProperties.keySet()) {
            result += "<" + prop + ">|";
        }
        result = result.substring(0, result.length()-1) + ")";
        return result;
    }
    public static final String jokerTypePropertiesStringWithType = generateWithType(jokerTypeProperties);
    private static String generateWithType(Map<String, String> jokerTypeProperties) {
        String result = "( rdf:type | ";
        for (String prop : jokerTypeProperties.keySet()) {
            result += "<" + prop + ">|";
        }
        result = result.substring(0, result.length()-1) + ")";
        return result;
    }
    public static Map<String, String> pseudoClasses = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            // http://purl.org/ontology/mo/release_type considered harmful
            put("http://purl.org/ontology/mo/album", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/audiobook", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/compilation", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/ep", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/interview", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/live", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/remix", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/single", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/soundtrack", "http://purl.org/ontology/mo/MusicalManifestation");
            put("http://purl.org/ontology/mo/spokenword", "http://purl.org/ontology/mo/MusicalManifestation");
        }
    });
    int id = 0;
    String keywordValue = null;
    String aggregat = "";
    String cond = "";
    boolean isAggregate = false;

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

    @Override
    public String getVarName() {
        return "?" + (this.keywordValue.replaceAll(" ", "_"));
    }

    public void setKeywordValue(String keywordValue) {
        this.keywordValue = keywordValue;
    }

    public void setCond(String agg, String cond) {
        this.isAggregate = true;
        this.aggregat = agg;
        this.cond = cond;
    }

    @Override
    public void match(SparqlServer serv) {
        long time = System.currentTimeMillis();

        HashMap<String, String> labelsMap = new HashMap<String, String>();
        HashMap<String, Float> scoresMap = new HashMap<String, Float>();

        String stringToMatch = this.getKeywordValue().replace("_", " ");
//        StringTokenizer st2 = new StringTokenizer(this.keywordValue, " \t\n\r\f-_");
//        String stringToMatch = "";
//        while (st2.hasMoreTokens()) {
//            // remove plural form if any (done manually because Lucene score doesn't handle this)
//            // FIXME: is it possible to configure it in Lucene?
//            String nextToken = st2.nextToken();
//            if (nextToken.endsWith("ies")) {
//                nextToken = nextToken.substring(0, nextToken.length() - 3) + "y";
//            } else if (nextToken.endsWith("s")) {
//                nextToken = nextToken.substring(0, nextToken.length() - 1);
//            }
//            stringToMatch += nextToken + " ";
//        }

        String query = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "  PREFIX dc:  <http://purl.org/dc/elements/1.1/> "
                + "  PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "  PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#> "
                + "SELECT ?subj ?label ?score "
                + "WHERE { "
                + "       (?label ?score ) pf:textMatch ('" + stringToMatch.trim() + "' 0.6 20). "
                + "       ?subj (dc:title|rdfs:label|foaf:name) ?label. "
                + "} ";

        Iterable<QuerySolution> results = serv.select(query);

        // first add some important matchings
        // TODO: a more generic approach: favour matchings to ontology's entities over other KB entitites
        if (this.keywordValue.equalsIgnoreCase("person")) {
            String uri = "http://xmlns.com/foaf/0.1/Person";
            labelsMap.put(uri, "person");
            scoresMap.put(uri, 20.0f);
        } else if (this.keywordValue.equalsIgnoreCase("album")) {
            String uri = "http://purl.org/ontology/mo/album"; // *type property considered harmful
            labelsMap.put(uri, "album");
            scoresMap.put(uri, 20.0f);
        } else if (this.keywordValue.equalsIgnoreCase("produce")) {
            String uri = "http://purl.org/ontology/mo/producer";
            labelsMap.put(uri, "producer");
            scoresMap.put(uri, 20.0f);
        } else if (this.keywordValue.equalsIgnoreCase("single")) {
            String uri = "http://purl.org/ontology/mo/single";
            labelsMap.put(uri, "single");
            scoresMap.put(uri, 20.0f);
        } else if (this.keywordValue.equalsIgnoreCase("release")) {
            String uri = "http://xmlns.com/foaf/0.1/maker";
            labelsMap.put(uri, "made by");
            scoresMap.put(uri, 20.0f);
        } else if (this.keywordValue.equalsIgnoreCase("band")) {
            String uri = "http://purl.org/ontology/mo/MusicGroup";
            labelsMap.put(uri, "band");
            scoresMap.put(uri, 20.0f);
        } else if (this.keywordValue.equalsIgnoreCase("found")) {
            String uri = "http://purl.org/vocab/bio/0.1/Birth";
            labelsMap.put(uri, "foundation");
            scoresMap.put(uri, 20.0f);
        } else if (this.keywordValue.equalsIgnoreCase("compose")) {
            String uri = "http://purl.org/ontology/mo/composer";
            labelsMap.put(uri, "composer");
            scoresMap.put(uri, 20.0f);
        }



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
                String label = qs.get("label").toString();
                labelsMap.put(uri, label);
                scoresMap.put(uri, score);
            }
        }

        logger.info(this.keywordValue + " matches with " + labelsMap.size() + " resources:");
        PriorityQueue<Match> matches = new PriorityQueue<Match>();
        for (String uri : labelsMap.keySet()) {

            float bestTrustMark = scoresMap.get(uri);
            String bestLabel = labelsMap.get(uri);
            boolean isClass = false;
            boolean isPseudoClass = false;
            boolean isIndividual = false;
            boolean isProperty = false;
            boolean isNumericDataProperty = false;
            boolean isDataProperty = false;

            List<String> types = serv.listTypes(uri);
            if (serv.isClass(types)) {
                isClass = true;
                logger.info(" (o) class " + uri);
            } else if (pseudoClasses.containsKey(uri)) {
                isPseudoClass = true;
                logger.info(" (o) pseudo class " + uri);
            } else if (serv.isDataProperty(types)) {
                if (serv.isNumericDataProperty(uri)) {
                    isNumericDataProperty = true;
                    logger.info(" (o) numeric data property " + uri);
                } else {
                    isDataProperty = true;
                    logger.info(" (o) data property " + uri);
                }
                //isProperty = true;
            } else if (serv.isProperty(types)) {
                isProperty = true;
                logger.info(" (o) property " + uri);
            } else {
                isIndividual = true;
                String typeString = "";
                for (String type : types) {
                    typeString += type + ", ";
                }
                logger.info(" (o) individual " + uri);
                if (this.queried) {
                    bestTrustMark *= trustMarkDiminutionWhenQueriedInstance;
                    logger.info("     (queried instance)");
                } else {
                    bestTrustMark *= trustMarkDiminutionWhenInstance;
                }
                logger.info("  --  type: " + (typeString.length() > 1 ? typeString.substring(0, typeString.length() - 2) : "NOT FOUND"));
            }
            if (((isClass || isPseudoClass || isIndividual) && (roles.contains(QeRole.E2Q3)))
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
            } else if (isClass) {
                this.addMatch(uri, bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.CLASS);
            } else if (isPseudoClass) {
                logger.info("addMatch(" + pseudoClasses.get(uri) + ", " + bestTrustMark + ", " + uri + ", " + bestLabel + ", " + false + ", ...");
                this.addMatch(pseudoClasses.get(uri), bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.CLASS);
            } else if (isDataProperty) {
                this.addMatch(uri, bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.DATAPROP);
            } else if (isNumericDataProperty) {
                this.addMatch(uri, bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.NUMDATAPROP);
            } else if (isProperty) {
                this.addMatch(uri, bestTrustMark, uri, bestLabel, false, matches, KbTypeEnum.PROP);
                List<String> inverses = serv.listInverses(uri);
                if (inverses.size() > 0) {
                    String firstInverse = inverses.get(0);
                    String inverseLabel = serv.getALabel(firstInverse);
                    logger.info("  --> also matching inverse property: " + firstInverse + " (label: " + inverseLabel + ")");
                    this.addMatch(firstInverse, bestTrustMark, firstInverse, inverseLabel, false, matches, KbTypeEnum.PROP);
                }
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

        @Override
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
    public String getAggregate() {
        String ret = null;
        if (this.aggregat != null) {
            ret = "(" + this.aggregat + "(" + this.getVarName() + ")" + this.cond + ")";
        }
        return ret;
    }

    public String getCond() {
        return this.cond;
    }

    @Override
    public boolean isAggregate() {
        return this.isAggregate;
    }

    @Override
    public String toString() {
        return "Keyword{\"" + keywordValue + "\" - queried=" + queried + " - id=" + id + " - varName = " + this.getVarName() + " - cond = " + this.aggregat + "(" + this.getVarName() + ")" + this.cond + "}";
    }

    @Override
    public String getStringRepresentation(String lang, boolean isNumericDataProperty, int id, boolean generalized) {
        //return (this.queried ? "?" : "") + (this.id > 0 ? "$" + this.id : "") + this.keywordValue;
        String ret = "";

        if (this.isAggregate) {
            String s1 = "", s2 = "", s3 = "", count = "", sum = "", min = "", max = "", avg = "";
            if (lang.compareTo("en") == 0) {
                s1 = "wich";
                s2 = "must be";
                s3 = "of";
                count = "the number";
                sum = "the sum";
                min = "the minimum";
                max = "the maximum";
                avg = "the average";
            } else if (lang.compareTo("fr") == 0) {
                s1 = "dont";
                s2 = "doit être";
                s3 = "de";
                count = "le nombre";
                sum = "la somme";
                min = "le minimum";
                max = "le maximum";
                avg = "la moyenne";
            }



            ret += s1 + " ";
            if (this.cond.compareTo("") != 0 && !isNumericDataProperty) {
                if (this.aggregat.compareTo("COUNT") == 0) {
                    ret += count + " " + s3 + " ";
                } else if (this.aggregat.compareTo("SUM") == 0) {
                    ret += sum + " " + s3 + " ";
                } else if (this.aggregat.compareTo("MIN") == 0) {
                    ret += min + " " + s3 + " ";
                } else if (this.aggregat.compareTo("MAX") == 0) {
                    ret += max + " " + s3 + " ";
                } else if (this.aggregat.compareTo("AVG") == 0) {
                    ret += avg + " " + s3 + " ";
                }
            }
            String subject = "";
            if (generalized) {
                subject = "_assoc" + id + "_";
            } else {
                subject = this.keywordValue;
            }
            ret += subject + " " + s2 + " ";
            if (this.cond.compareTo("") != 0) {
                ret += this.cond;
            } else if (this.aggregat.compareTo("MAX") == 0) {
                ret += max;
            } else if (this.aggregat.compareTo("MIN") == 0) {
                ret += min;
            }

        }

        return ret;
    }

    @Override
    public String getStringValue() {
        return this.keywordValue;
    }
}
