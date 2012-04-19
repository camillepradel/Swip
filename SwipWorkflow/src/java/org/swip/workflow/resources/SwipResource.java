/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.workflow.resources;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.swip.workflow.model.QueryInterpretation;

/**
 *
 * @author camille
 */
@Path("/mappings/")
public class SwipResource {

    public SwipResource() {
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{plquery}")
    public List<QueryInterpretation> processQuery(
            @QueryParam("plquery") @DefaultValue("") String plquery) {
//        List<QueryInterpretation> queryResults = new ArrayList<QueryInterpretation>();
//        queryResults.add(new QueryInterpretation("0.75",
//                                "sentence2", "sparql query2", "mapping description2", 0));
//        return queryResults;

        // NL to pivot query
//        String pivotQuery = plquery;
        String pivotQuery = translateQuery(plquery);

        // pivot to formal query
        List<QueryInterpretation> queryResults = null;
        if (!pivotQuery.equals("")) {
            List<org.swip.pivottomappings.PatternToQueryMapping> ptqms = generateBestMappings(pivotQuery, 50);

            if (ptqms != null) {
                queryResults = new ArrayList<QueryInterpretation>();
                for (org.swip.pivottomappings.PatternToQueryMapping ptqm : ptqms) {
                    queryResults.add(new QueryInterpretation(String.valueOf(ptqm.getRelevanceMark()),
                            ptqm.getSentence(), ptqm.getSparqlQuery(), ptqm.getStringDescription(), 0));
                }
                return queryResults;
            }
        }
        
        return queryResults;
    }

    private static java.util.List<org.swip.pivottomappings.PatternToQueryMapping> generateBestMappings(java.lang.String pivotQueryString, int numMappings) {
        org.swip.pivottomappings.PivotToMappingsWS_Service service = new org.swip.pivottomappings.PivotToMappingsWS_Service();
        org.swip.pivottomappings.PivotToMappingsWS port = service.getPivotToMappingsWSPort();
        return port.generateBestMappings(pivotQueryString, numMappings);
    }

    private static String translateQuery(java.lang.String nlQuery) {
        org.swip.nltopivot.NlToPivotWS_Service service = new org.swip.nltopivot.NlToPivotWS_Service();
        org.swip.nltopivot.NlToPivotWS port = service.getNlToPivotWSPort();
        return port.translateQuery(nlQuery);
    }
}