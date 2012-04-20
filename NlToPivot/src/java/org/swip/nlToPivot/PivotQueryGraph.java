package org.swip.nlToPivot;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class PivotQueryGraph {

    Logger logger = Logger.getLogger(org.swip.nlToPivot.PivotQueryGraph.class);

    class QuerySubstring {

        int startId;
        int endId;
        String stringValue = null;

        public QuerySubstring(int startId, int endId) {
            logger.debug("QuerySubstring(" + startId + ", " + endId + ")");
            this.startId = startId;
            this.endId = endId;
        }

        String getStringValue() {
//            return (stringValue==null? nlQuery.substring(startId, endId) : stringValue);
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }

    abstract class Q {

        abstract String generateStringRepresentation(String nlQuery);

        abstract Collection<? extends String> getIdsList();
    }

    class Q2 extends Q {

        String id1;
        String id2;

        public Q2(String id1, String id2) {
            this.id1 = id1;
            this.id2 = id2;
        }

        @Override
        String generateStringRepresentation(String nlQuery) {
            String q2e1 = (id1.equals(PivotQueryGraph.this.queryObjectId) ? "?" : "")
                    + PivotQueryGraph.this.substrings.get(id1).getStringValue();
            String q2e2 = (id2.equals(PivotQueryGraph.this.queryObjectId) ? "?" : "")
                    + PivotQueryGraph.this.substrings.get(id2).getStringValue();
            return (q2e1.replace(q2e2, "").replace(" ", "_")
                    + ": "
                    + q2e2.replace(q2e1, "").replace(" ", "_")
                    + ".");
        }

        @Override
        Collection<? extends String> getIdsList() {
            List<String> result = new LinkedList<String>();
            result.add(this.id1);
            result.add(this.id2);
            return result;
        }
    }
    //TODO: use Vector instead of Map
    Map<String, QuerySubstring> substrings = new HashMap<String, QuerySubstring>();
    String queryObjectId = null;
    List<Q> qs = new LinkedList<Q>();
    List<String> countIds = new LinkedList<String>();
    Map<String, String> adjs = new HashMap<String, String>();

    void addQuerySubstring(String id, int startId, int endId) {
        substrings.put(id, new QuerySubstring(startId, endId));
    }

    void setASubstringAsCount(String id) {
        countIds.add(id);
    }

    void addAdjToSubstring(String id, String adjValue) {
        logger.debug("addAdjToSubstring(" + id + ", " + adjValue + ")");
        this.adjs.put(id, adjValue);
    }

//    void setqueryObjectId(String rootSubstringId) {
//        if (this.queryObjectId != null) {
//            logger.warn("Caution: several candidates for query object are detected (case not handled)");
//        }
//        this.queryObjectId = rootSubstringId;
//    }
    void addQ2(String id1, String id2) {
        if (id1.equals(id2)) {
            return;
        }
        for (Q q : this.qs) {
            if (q instanceof Q2) {
                Q2 q2 = (Q2) q;
                if (q2.id1.equals(id1)) {
                    if (q2.id2.equals(id2)) {
                        return;
                    }
                } else if (q2.id1.equals(id2)) {
                    if (q2.id2.equals(id1)) {
                        return;
                    }
                }
            }
        }
        this.qs.add(new Q2(id1, id2));
    }

    String getSubstringsList(String nlQuery) {
        String result = "queried object: " + this.queryObjectId;
        for (String id : substrings.keySet()) {
            result += ("\n" + (id.equals(this.queryObjectId) ? "->" : "") + id + ": " + substrings.get(id).getStringValue());
        }
        return result;
    }

//    void printQs(String nlQuery) {
//        for(Q q : this.qs) {
//            try {
//            logger.info(q.generateStringRepresentation(nlQuery));
//            } catch (NullPointerException ex) {}
//        }
//    }
    String generatePivotQuery(String nlQuery) {
        // determine string value for each element
        for (QuerySubstring qs1 : this.substrings.values()) {
            qs1.setStringValue(nlQuery.substring(qs1.startId, qs1.endId));
        }
        logger.debug(getSubstringsList(nlQuery));
        logger.debug("determine string value for each element");
        // separate nested substrings
        for (QuerySubstring qs1 : this.substrings.values()) {
            for (QuerySubstring qs2 : this.substrings.values()) {
                if ((qs1.startId <= qs2.startId && qs1.endId > qs2.endId) || (qs1.startId < qs2.startId && qs1.endId >= qs2.endId)) {
                    qs1.setStringValue(qs1.getStringValue().replace(qs2.getStringValue(), ""));
                }
            }
        }
        // remove adjs
        for (String id : this.adjs.keySet()) {
            QuerySubstring qs = this.substrings.get(id);
            qs.setStringValue(qs.getStringValue().replace(this.adjs.get(id), ""));
        }
        // add type information
        for (String countId : this.countIds) {
            QuerySubstring qs = this.substrings.get(countId);
            qs.setStringValue("COUNT<" + qs.getStringValue() + ">");
        }
        logger.debug(getSubstringsList(nlQuery));

        // determine query object
        // query object is the largest substring starting at the very begining of the sentence
        logger.debug("determine query object");
        int queryObjectStart = 1000;
        int queryObjectLength = 0;
        for (String qsid : this.substrings.keySet()) {
            QuerySubstring qs = this.substrings.get(qsid);
            if (qs.startId < queryObjectStart
                    || (qs.startId <= queryObjectStart && (qs.endId - qs.startId) > queryObjectLength)) {
                this.queryObjectId = qsid;
                queryObjectStart = qs.startId;
                queryObjectLength = qs.endId - qs.startId;
            }
        }
        logger.debug(getSubstringsList(nlQuery));

        String pivotQuery = "";
        Set<String> usedIds = new HashSet<String>();

        // generate pivot query parts for each subquery
        for (Q q : this.qs) {
            try {
                pivotQuery += q.generateStringRepresentation(nlQuery) + "  ";
                usedIds.addAll(q.getIdsList());
            } catch (NullPointerException ex) {
                logger.error(ex);
            }
        }

        // add query elements which were not involved in any subquery
        for (String id : substrings.keySet()) {
            if (!usedIds.contains(id)) {
                pivotQuery += (id.equals(this.queryObjectId) ? "?" : "")
                        + substrings.get(id).getStringValue()
                        + ".  ";
            }
        }

        return pivotQuery;
    }
}
