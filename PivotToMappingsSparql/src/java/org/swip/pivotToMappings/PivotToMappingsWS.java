package org.swip.pivotToMappings;

import com.sun.jersey.api.json.JSONWithPadding;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.json.JSONObject;


@Path("/rest/")
public class PivotToMappingsWS {

    private static final Logger logger = Logger.getLogger(PivotToMappingsWS.class);

    public PivotToMappingsWS() {
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("generateBestMappings")
    public String generateBestMappings(
            @QueryParam("pivotQuery") @DefaultValue("") String pivotQueryString,
            @QueryParam("sparqlEndpointUri") @DefaultValue("") String sparqlEndpointUri,
            @QueryParam("useFederatedSparql") @DefaultValue("false") boolean useFederatedSparql,
            @QueryParam("useLarq") @DefaultValue("true") boolean useLarq,
            @QueryParam("larqParams") @DefaultValue("") String larqParams,
            @QueryParam("kbLocation") @DefaultValue("") String kbLocation,
            @QueryParam("queriesNamedGraphUri") @DefaultValue("") String queriesNamedGraphUri,
            @QueryParam("patternsNamedGraphUri") @DefaultValue("") String patternsNamedGraphUri,
            @QueryParam("numMappings") @DefaultValue("50") int numMappings) {

        JSONObject response = new JSONObject();
        String queryUri = Controller.getInstance().processQuery(pivotQueryString, sparqlEndpointUri, useFederatedSparql, useLarq, larqParams, kbLocation, queriesNamedGraphUri, patternsNamedGraphUri, numMappings);
        response.put("queryUri", queryUri);
        return response.toString();
    }

    @GET
    @Produces({"application/x-javascript", MediaType.APPLICATION_JSON})
    @Path("generateBestMappingsJSONP")
    public JSONWithPadding generateBestMappingsJSONP(
            @QueryParam("pivotQuery") @DefaultValue("") String pivotQueryString,
            @QueryParam("sparqlEndpointUri") @DefaultValue("") String sparqlEndpointUri,
            @QueryParam("useFederatedSparql") @DefaultValue("false") boolean useFederatedSparql,
            @QueryParam("useLarq") @DefaultValue("true") boolean useLarq,
            @QueryParam("larqParams") @DefaultValue("") String larqParams,
            @QueryParam("kbLocation") @DefaultValue("") String kbLocation,
            @QueryParam("queriesNamedGraphUri") @DefaultValue("") String queriesNamedGraphUri,
            @QueryParam("patternsNamedGraphUri") @DefaultValue("") String patternsNamedGraphUri,
            @QueryParam("numMappings") @DefaultValue("50") int numMappings,
            @QueryParam("callback") @DefaultValue("fn") String callback) {

        return new JSONWithPadding(generateBestMappings(pivotQueryString, sparqlEndpointUri, useFederatedSparql, useLarq, larqParams, kbLocation, queriesNamedGraphUri, patternsNamedGraphUri, numMappings), callback);
    }

//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    @Path("deleteQuery")
//    public void deleteQuery(
//            @QueryParam("pivotQuery") @DefaultValue("") String pivotQueryString,
//            @QueryParam("sparqlEndpointUri") @DefaultValue("") String sparqlEndpointUri) {
//
//        Controller.getInstance().deleteQuery(pivotQueryString, sparqlEndpointUri);
//    }
}