/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.nlToPivot;

import java.io.ByteArrayInputStream;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.FileWriter;

/**
 *
 * @author camille
 */
@WebService(serviceName = "NlToPivotWS")
public class NlToPivotWS {

    private static final Logger logger = Logger.getLogger(NlToPivotWS.class);
    static int nbQueryFr = 0;

    /**
     * Web service operation
     */
    @SuppressWarnings("unchecked")
    @WebMethod(operationName = "translateQuery")
    public String translateQuery(@WebParam(name = "nlQuery") String nlQuery, @WebParam(name = "lang") String lang) {
        logger.info("User nl query: " + nlQuery);
        logger.info("Query language: " + lang);
        String pivotQuery = "";

        if (lang.equals("en")) {
            // identify type of query using the begining of the query,
            // and adapting the query string for an easier parsing

            String adaptedQuery = null;

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
            pivotQuery = pqg.generatePivotQuery(adaptedQuery);

            if (pp.getCount()) {
                pivotQuery += " COUNT.";
            } else if (pp.getStartMaximum()) {
                pivotQuery += " MAX.";
            } else if (pp.getStartMinimum()) {
                pivotQuery += " MIN.";
            } else if (pp.getStartAverage()) {
                pivotQuery += " AVG.";
            } else if (pp.getStartSum()) {
                pivotQuery += " SUM.";
            }

            pivotQuery = pivotQuery.trim();
            pivotQuery = pivotQuery.replaceAll("\\s*([:;\\(\\)<>=\\.\\?])\\s*", "$1");

            logger.info("Generated pivot query: " + pivotQuery);
        } else if (lang.equals("fr")) {
            try {
                // setting environment
                String bonsaiDir = "/home/camille/bonsai";
                String bonsai32Dir = bonsaiDir + "/bonsai_v3.2";
                String bonsai32binDir = bonsai32Dir + "/bin";
                String[] env = new String[3];
                env[0] = "BONSAI=" + bonsai32Dir;
                env[1] = "MALT_DIR=" + bonsaiDir + "/malt-1.3";
                env[2] = "MALTPARSERHOME=" + bonsaiDir + "/malt-1.3";

                BufferedWriter out = new BufferedWriter(new FileWriter(bonsaiDir + "/query" + nbQueryFr));
                logger.info("query" + nbQueryFr);
                out.write(nlQuery);
                out.close();

                String cmd = bonsai32binDir + "/bonsai_malt_parse_rawtext.sh " + bonsaiDir + "/query" + nbQueryFr;
                logger.info(cmd);
                Process proc = Runtime.getRuntime().exec(cmd, env);
                proc.waitFor();

                cmd = "cat " + bonsaiDir + "/query" + nbQueryFr + ".outmalt | /usr/bin/python " + bonsai32Dir + "/my_src/outmalt_to_pivot.py";
                logger.info(cmd);
                proc = Runtime.getRuntime().exec(cmd);
                proc.waitFor();
                BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line = null;
                while ((line = buf.readLine()) != null) {
                    pivotQuery += line;
                    logger.info(line);
                }

                nbQueryFr++;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pivotQuery = "language not supported";
        }
        return pivotQuery;
    }

    private static String getSuppleSemanticAnnotations(java.lang.String adaptedNlQuery) {
        org.swip.nltopivotgatepipeline.NlToPivotGatePipeline service = new org.swip.nltopivotgatepipeline.NlToPivotGatePipeline();
        org.swip.nltopivotgatepipeline.NlToPivotGatePipelineWS port = service.getNlToPivotGatePipelineWSPort();
        return port.getSuppleSemanticAnnotations(adaptedNlQuery);
    }
}
