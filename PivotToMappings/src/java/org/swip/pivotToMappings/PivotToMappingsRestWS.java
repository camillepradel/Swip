/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.pivotToMappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.json.JSONObject;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.model.patterns.Pattern;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;

/**
 * REST Web Service
 *
 * @author Murloc
 */
@Path("rest")
public class PivotToMappingsRestWS {

    @Context
    private UriInfo context;

    /** Creates a new instance of PivotToMappingsRestWS */
    public PivotToMappingsRestWS() {
    }


    @GET
    @Produces("application/json")
    @Path("generateBestMappings/{pivotQueryString}/{numMappings}")
    public String generateBestMappings(@PathParam("pivotQueryString") String pivotQueryString, @PathParam("numMappings") int numMappings) 
    {
        JSONObject response = new JSONObject();

        if(numMappings > 50) numMappings = 50;  // avoid to request the server with an infinite result
        List<PatternToQueryMapping> bestMappings = Controller.getInstance().getBestMappings(pivotQueryString, numMappings);
	
	if(bestMappings != null)
        {
	        Collections.reverse(bestMappings);
                
                ArrayList<JSONObject> queryResults = new ArrayList();
                
                for(PatternToQueryMapping ptqm : bestMappings)
                {
                    JSONObject query = new JSONObject();
                    query.put("descriptiveSentence", ptqm.getSentence());
                    query.put("mappingDescription", ptqm.getStringDescription());
                    query.put("relevanceMark", String.valueOf(ptqm.getRelevanceMark()));
                    query.put("sparqlQuery", ptqm.getSparqlQuery());
                    //query.put("validate", 0);

                    queryResults.add(query);
                }
                
                response.put("content", queryResults);
        }
        
	return response.toString();
    }
    
    @GET
    @Produces("application/json")
    @Path("getPatterns")
    public String getPatterns()
    {
        java.util.List<Pattern> patterns = Controller.getInstance().getPatterns();
        ArrayList<String> retList = new ArrayList<String>();
        for(Pattern pat : patterns)
        {
            retList.add(pat.toString());
        }
        
        JSONObject ret = new JSONObject();
        ret.put("patterns", retList);
        return ret.toString();
    }

    /**
     * PUT method for updating or creating an instance of PivotToMappingsRestWS
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    /*@PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }*/
}
