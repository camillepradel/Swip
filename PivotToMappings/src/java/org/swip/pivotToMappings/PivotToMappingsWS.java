/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.pivotToMappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.json.JSONObject;
import org.swip.pivotToMappings.controller.Controller;
import org.swip.pivotToMappings.model.AbstractClass;
import org.swip.pivotToMappings.model.Class1;
import org.swip.pivotToMappings.model.Class2;
import org.swip.pivotToMappings.model.ClassToBeReturned;
import org.swip.pivotToMappings.model.NestedClassToBeReturned;
import org.swip.pivotToMappings.model.patterns.mapping.KbElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.LiteralElementMapping;
import org.swip.pivotToMappings.model.patterns.mapping.PatternToQueryMapping;
import org.swip.pivotToMappings.model.patterns.patternElement.ClassPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.LiteralPatternElement;
import org.swip.pivotToMappings.model.patterns.patternElement.PropertyPatternElement;
import org.swip.pivotToMappings.model.patterns.subpattern.PatternTriple;
import org.swip.pivotToMappings.model.patterns.subpattern.SubpatternCollection;
import org.swip.pivotToMappings.model.query.queryElement.Keyword;
import org.swip.pivotToMappings.model.query.queryElement.Literal;

@Path("/rest/")
public class PivotToMappingsWS {

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
                    ArrayList<ArrayList<JSONObject>> generalizations = new ArrayList<ArrayList<JSONObject>>();
                    
                    String descSent = ptqm.getSentence().trim().replaceAll("\\s+", " ");
                    if(descSent.charAt(descSent.length() - 1) == ',')
                        descSent = descSent.substring(0, descSent.length() - 1);
                    
                    descSentJSon.put("genNb", ptqm.getGeneralizations().size());
                    descSentJSon.put("string", descSent);
                    descSentJSon.put("gen", ptqm.getGeneralizations());
                    query.put("descriptiveSentence", descSentJSon);

                    query.put("mappingDescription", ptqm.getStringDescription());
                    query.put("relevanceMark", String.valueOf(ptqm.getRelevanceMark()));
                    query.put("sparqlQuery", ptqm.getSparqlQuery());

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
}