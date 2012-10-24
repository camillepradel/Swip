package org.swip.workflow;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;
import org.swip.exchange.DependencyTree;
import org.swip.exchange.NlToPivotResult;

@Path("/rest/")
public class WorkflowWS {

    private static final Logger logger = Logger.getLogger(WorkflowWS.class);
    private final static String serverIP = "http://localhost:8080/";
//    private final static serverIP = "http://swip.univ-tlse2.fr/";

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("nlToPivot")
    public NlToPivotResult nlToPivot(
            @QueryParam("nlQuery") @DefaultValue("") String nlQuery,
            @QueryParam("kb") @DefaultValue("") String kb,
            @QueryParam("lang") @DefaultValue("fr") String lang,
            @QueryParam("pos") @DefaultValue("treeTagger") String posTagger,
            @QueryParam("dep") @DefaultValue("malt") String depParser) {

        logger.info("User nl query: " + nlQuery);
        logger.info("Target knowledge base: " + kb);
        logger.info("Query language: " + lang);
        logger.info("POS tagger: " + posTagger);
        logger.info("Dependency parser: " + depParser);

        NlToPivotResult result = new NlToPivotResult();
        
        String gazetteedQuery = "Was Keith_Richards a member of The_Rolling_Stones?";
//        String gazetteedQuery = new NlToPivotGazetteerWS_JerseyClient().gatherNamedEntities(nlQuery, "false");
        result.setGazetteedQuery(gazetteedQuery);
        
        DependencyTree dependencyTree = new NlToPivotParserWS_JerseyClient().nlToDependenciesJson(lang, gazetteedQuery, depParser, posTagger);
        result.setDependencyTree(dependencyTree);
        
        String pivotQuery = new NlToPivotRulesWS_JerseyClient().dependenciesToPivotFromApp(dependencyTree);
        result.setPivotQuery(pivotQuery);
        
        return result;
    }

    static class NlToPivotGazetteerWS_JerseyClient {

        private WebResource webResource;
        private Client client;
        private static final String BASE_URI = serverIP + "NlToPivotGazetteer/resources";

        public NlToPivotGazetteerWS_JerseyClient() {
            com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
            client = Client.create(config);
            webResource = client.resource(BASE_URI).path("rest");
        }

        public String gatherNamedEntities(String text, String tagWithClass) throws UniformInterfaceException {
            WebResource resource = webResource;
            if (tagWithClass != null) {
                resource = resource.queryParam("tagWithClass", tagWithClass);
            }
            if (text != null) {
                resource = resource.queryParam("text", text);
            }
            resource = resource.path("gatherNamedEntities");
            return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
        }

        public void close() {
            client.destroy();
        }
    }

    static class NlToPivotParserWS_JerseyClient {

        private WebResource webResource;
        private Client client;
        private static final String BASE_URI = serverIP + "NlToPivotParser/resources";

        public NlToPivotParserWS_JerseyClient() {
            com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
            client = Client.create(config);
            webResource = client.resource(BASE_URI).path("rest");
        }

        public DependencyTree nlToDependenciesJson(String lang, String nlQuery, String dep, String pos) throws UniformInterfaceException {
            WebResource resource = webResource;
            if (lang != null) {
                resource = resource.queryParam("lang", lang);
            }
            if (nlQuery != null) {
                resource = resource.queryParam("nlQuery", nlQuery);
            }
            if (dep != null) {
                resource = resource.queryParam("dep", dep);
            }
            if (pos != null) {
                resource = resource.queryParam("pos", pos);
            }
            resource = resource.path("nlToDependenciesJson");
            return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(DependencyTree.class);
        }

        public void close() {
            client.destroy();
        }
    }

    static class NlToPivotRulesWS_JerseyClient {

        private WebResource webResource;
        private Client client;
        private static final String BASE_URI = serverIP + "NlToPivotRules/resources";

        public NlToPivotRulesWS_JerseyClient() {
            com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
            client = Client.create(config);
            webResource = client.resource(BASE_URI).path("rest");
        }

        public String dependenciesToPivotFromApp(DependencyTree dependencyTree) throws UniformInterfaceException {
//            Map<String, Object> params = new HashMap<String, Object>();
            MultivaluedMap params = new MultivaluedMapImpl();

            params.put("dependencyTree", dependencyTree);
            
            return webResource.path("dependenciesToPivotFromApp").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(String.class, params);
        }

        public String dependenciesToPivotFromForm() throws UniformInterfaceException {
            return webResource.path("dependenciesToPivotFromForm").type(javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED).post(String.class);
        }

        public void close() {
            client.destroy();
        }
    }
    
}
