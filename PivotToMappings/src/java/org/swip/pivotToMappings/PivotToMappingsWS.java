/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.pivotToMappings;

import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.swip.pivotToMappings.controller.Controller;

import org.swip.pivotToMappings.model.patterns.PatternsTextToRdf;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;


@Path("/rest/")
public class PivotToMappingsWS {
    
    private static final Logger logger = Logger.getLogger(PivotToMappingsWS.class);

    public PivotToMappingsWS() {

    }

   /* @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("getPatterns")
    public java.util.List<org.swip.pivotToMappings.model.patterns.Pattern> getPatterns() {
        return Controller.getInstance().getPatterns();
    }*/

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("generateBestMappings")
    public String generateBestMappings(
        @QueryParam("pivotQueryString") @DefaultValue("") String pivotQueryString, 
        @QueryParam("numMappings") @DefaultValue("0") int numMappings,
        @QueryParam("kbName") @DefaultValue("cinema") String kbName) {
        
        JSONObject response = new JSONObject();
        
        if(numMappings > 50) numMappings = 50;  // Limiting the maximum amount of answers
        List<PatternToQueryMapping> bestMappings = Controller.getInstance().getBestMappings(pivotQueryString, numMappings, kbName);
    
        if(bestMappings != null)
        {
            Collections.reverse(bestMappings);
                
                ArrayList<JSONObject> queryResults = new ArrayList();
                
                for(PatternToQueryMapping ptqm : bestMappings)
                {
                    JSONObject query = new JSONObject();
                    JSONObject descSentJSon = new JSONObject();
                    JSONObject generalizations = new JSONObject();
                    JSONObject sparqlQuery = new JSONObject();
                    JSONObject uris = new JSONObject();
                    
                    String descSent = ptqm.getSentence().trim().replaceAll("\\s+", " ");
                    if(descSent.charAt(descSent.length() - 1) == ',')
                        descSent = descSent.substring(0, descSent.length() - 1);
                    for(Map.Entry<Integer, ArrayList<String>> entry : ptqm.getGeneralizations().entrySet())
                        generalizations.put(String.valueOf(entry.getKey()), entry.getValue());
                    descSentJSon.put("string", descSent);
                    descSentJSon.put("gen", generalizations);
                    query.put("descriptiveSentence", descSentJSon);

                    for(Map.Entry<Integer, ArrayList<String>> entry : ptqm.getUris().entrySet())
                        uris.put(String.valueOf(entry.getKey()), entry.getValue());
                    sparqlQuery.put("string", ptqm.getSparqlQuery());
                    sparqlQuery.put("uris", uris);
                    query.put("sparqlQuery", sparqlQuery);

                    query.put("mappingDescription", ptqm.getStringDescription());
                    query.put("relevanceMark", String.valueOf(ptqm.getRelevanceMark()));

                    queryResults.add(query);
                }
                
                response.put("content", queryResults);
        }
        
        return response.toString();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("processQuery")
    public String processQuery(
        @QueryParam("query") @DefaultValue("") String query,
        @QueryParam("kbName") @DefaultValue("cinema") String kbName) {

        return Controller.getInstance().processQuery(query, kbName);
    }

    @POST
    @Produces({MediaType.TEXT_XML})
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("patternsTextToRdf")
    public String patternsTextToRdf(
        @FormParam("patterns") @DefaultValue("") String patterns,
        @FormParam("setName") @DefaultValue("") String setName,
        @FormParam("ontologyUri") @DefaultValue("http://ontologies.alwaysdata.net/cinema") String ontologyUri,
        @FormParam("authorUri") @DefaultValue("http://camillepradel.com/uris#me") String authorUri)  {

        logger.info(setName);
        return PatternsTextToRdf.patternsTextToRdf(setName, authorUri, ontologyUri, patterns);
    }
}