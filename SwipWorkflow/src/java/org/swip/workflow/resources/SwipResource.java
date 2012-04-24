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
import org.json.JSONObject;

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
    public String processQuery(
            @QueryParam("plquery") @DefaultValue("") String plquery,
            @QueryParam("start") @DefaultValue("") String strStart,
            @QueryParam("limit") @DefaultValue("") String limit) {
        
        int providedResults = Integer.parseInt(limit);
        int start = Integer.parseInt(strStart);
        int totalResults = 0;
        int maxResults = 50;
        
        String pivotQuery = translateQuery(plquery);
       
         System.out.println("\n--------PIVOT QUERY---------- \n "+pivotQuery+"\n------------------\n");
        
        JSONObject response = new JSONObject();
        ArrayList<JSONObject> queryResults = new ArrayList();

        // pivot to formal query
        
        if (!pivotQuery.equals("")) {
            List<org.swip.pivottomappings.PatternToQueryMapping> ptqms = generateBestMappings(pivotQuery, maxResults);
            System.out.println("Pivot To Mappings : "+ptqms.size());
            if (ptqms != null) {
                totalResults = ptqms.size();
                
                int i = 0;
                while(i < totalResults) {
                    if(i >= start && i < start + providedResults) {
                        org.swip.pivottomappings.PatternToQueryMapping ptqm = ptqms.get(i);
                        
                        JSONObject query = new JSONObject();
                        query.put("descriptiveSentence", ptqm.getSentence());
                        query.put("mappingDescription", ptqm.getStringDescription());
                        query.put("relevanceMark", String.valueOf(ptqm.getRelevanceMark()));
                        query.put("sparqlQuery", ptqm.getSparqlQuery());
                        query.put("validate", 0);

                        queryResults.add(query);
                    }
                    i++;
                }
                
                response.put("result", queryResults);
                response.put("totalCount", totalResults);
                
                return response.toString();
            }
        }

        return response.toString();
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