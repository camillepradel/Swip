/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.nlToPivot;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import java.io.ByteArrayInputStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

/**
 * REST Web Service
 *
 * @author Murloc
 */
@Path("rest")
public class NlToPivotRestWS {

    private static final Logger logger = Logger.getLogger(NlToPivotWS.class);
    
    @Context
    private UriInfo context;

    /** Creates a new instance of NlToPivotRestWS */
    public NlToPivotRestWS() {
    }

    @GET
    @Produces("application/json")
    @Path("translateQuery/{nlQuery}")
    public String translateQuery(@PathParam("nlQuery") String nlQuery) 
    {
        logger.info("User nl query: " + nlQuery);
        // identify type of query using the begining of the query,
        // and adapting the query string for an easier parsing
        //boolean count = false;
        
        String adaptedQuery = null;
        
        /*if (nlQuery.startsWith("Are there any ")) {
            adaptedQuery = nlQuery.substring(14);
        } else if (nlQuery.startsWith("Are there ")) {
            adaptedQuery = nlQuery.substring(10);
        } else if (nlQuery.startsWith("How many ")) {
            count = true;
            adaptedQuery = nlQuery.substring(9);
        } else if (nlQuery.startsWith("List all ")) {
            adaptedQuery = nlQuery.substring(9);
        } else if (nlQuery.startsWith("List ")) {
            adaptedQuery = nlQuery.substring(5);
        } else {
            adaptedQuery = nlQuery;
        }*/
        
        NlToPivotPreParser pp = new NlToPivotPreParser(nlQuery);
        adaptedQuery = pp.getAdaptedQuery();
        
        logger.info("Adapted query: " + adaptedQuery);

        // parsing the nl query with a Gate pipeline (using supple)
        String suppleSemanticOutput = getSuppleSemanticAnnotations(adaptedQuery);
        logger.info("Supple semantic output:\n" + suppleSemanticOutput);

        // tranlating supple semantic output into the pivot language
        PivotQueryGraph pqg = null;
        try {
            ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(suppleSemanticOutput.getBytes()));
            nlToPivotGrammarLexer lexer = new nlToPivotGrammarLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            nlToPivotGrammarParser parser = new nlToPivotGrammarParser(tokens);
            pqg = parser.qlfs();
        } catch (Exception ex) {
            logger.error(ex);
        }
        pqg.setOptions(pp);
        String pivotQuery = pqg.generatePivotQuery(adaptedQuery);
        
        if (pp.getCount()) {
            pivotQuery += " COUNT.";
        }
        else if(pp.getStartMaximum())
        {
            pivotQuery += " MAX.";
        }
        else if(pp.getStartMinimum())
        {
            pivotQuery += " MIN.";
        }
        else if(pp.getStartAverage())
        {
            pivotQuery += " AVG.";
        }
        else if(pp.getStartSum())
        {
            pivotQuery += " SUM.";
        }
        
        pivotQuery = pivotQuery.trim();
        pivotQuery = pivotQuery.replaceAll("\\s*([:;\\(\\)<>=\\.\\?])\\s*", "$1");
        
        System.out.println("Generated pivot query: " + pivotQuery);
        logger.info("Generated pivot query: " + pivotQuery);
        return "{\"pivotQuery\" : \""+pivotQuery+"\"}";
    }

    private static String getSuppleSemanticAnnotations(java.lang.String adaptedNlQuery) {
        org.swip.nltopivotgatepipeline.NlToPivotGatePipeline service = new org.swip.nltopivotgatepipeline.NlToPivotGatePipeline();
        org.swip.nltopivotgatepipeline.NlToPivotGatePipelineWS port = service.getNlToPivotGatePipelineWSPort();
        return port.getSuppleSemanticAnnotations(adaptedNlQuery);
    }

    
}
