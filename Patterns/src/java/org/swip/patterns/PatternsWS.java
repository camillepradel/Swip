package org.swip.patterns;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;

@Path("/rest/")
public class PatternsWS {

    private static final Logger logger = Logger.getLogger(PatternsWS.class);

    public PatternsWS() {
    }
    
    @POST
    @Produces({MediaType.TEXT_XML})
    @Path("patternsTextToRdf")
    public String patternsTextToRdf(
            @FormParam("patterns") @DefaultValue("") String patterns,
            @FormParam("setName") @DefaultValue("") String setName,
            @FormParam("ontologyUri") @DefaultValue("http://ontologies.alwaysdata.net/cinema") String ontologyUri,
            @FormParam("authorUri") @DefaultValue("http://camillepradel.com/uris#me") String authorUri) {

        logger.info(setName);
        return PatternsTextToRdf.patternsTextToRdf(setName, authorUri, ontologyUri, patterns);
    }
}