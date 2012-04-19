/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.nlToPivotGatePipeline;

import gate.*;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.persistence.PersistenceManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.apache.log4j.Logger;

/**
 *
 * @author camille
 */
@WebService(serviceName = "NlToPivotGatePipeline")
public class NlToPivotGatePipelineWS {

    /** The Corpus Pipeline application */
    static private CorpusController corpusController = null;
    private static final Logger logger = Logger.getLogger(NlToPivotGatePipelineWS.class);

    private CorpusController getCorpusController() {
        //logger.info("la");
        if (corpusController == null) {
            // initialise the GATE library
            try {
                logger.info("Initialising GATE...");
                
                //Gate.setGateHome(new File("/home/sam/Documents/Stage/Swip/swip/NlToPivotGatePipeline/web/WEB-INF"));
                File userGateFile = new File(this.getClass().getClassLoader().getResource("/../user-gate.xml").getPath());
                File gateHomeFile = userGateFile.getParentFile();
                
                Gate.setGateHome(gateHomeFile);
                Gate.setUserConfigFile(userGateFile);
                
                /*logger.info("USER GATE : "+userGateFile.getAbsolutePath());
                logger.info("GATE HOME : "+gateHomeFile.getAbsolutePath());*/
                
                
                Gate.init();
                
                //logger.info("OKKKKK");
                
                logger.debug("default user config file: " + Gate.getDefaultUserConfigFileName());
                logger.debug("default user session file: " + Gate.getDefaultUserSessionFileName());
                logger.debug("user config file: " + Gate.getUserConfigFile());
                logger.debug("user session file: " + Gate.getUserSessionFile());
                logger.debug("site config file: " + Gate.getSiteConfigFile());
                logger.debug("plugins home: " + Gate.getPluginsHome());
                logger.debug("original user config: " + Gate.getOriginalUserConfig());
                logger.debug("known plugins: " + Gate.getKnownPlugins());
                logger.debug("built in creole dir: " + Gate.getBuiltinCreoleDir());
                logger.info("Loading Gate process pipeline...");
                //FIXME: not the good way to reference static files on a server
                corpusController = (CorpusController) PersistenceManager.loadObjectFromFile(new File(this.getClass().getClassLoader().getResource("../NlToPivotSupple.gapp").getPath()));
                logger.info("Process pipeline loaded");
            } catch (Exception ex) {
                logger.error(ex.getStackTrace());
            }
        }
        logger.info("2");
        return corpusController;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getSuppleSemanticAnnotations")
    public String getSuppleSemanticAnnotations(@WebParam(name = "adaptedNlQuery") String adaptedNlQuery) {
        logger.info("adapted query for Gate pipeline: " + adaptedNlQuery);
        try {
            //logger.info("ici");
            CorpusController cont = this.getCorpusController();
            Corpus corpus = (Corpus) Factory.createResource("gate.corpora.CorpusImpl");
            Document doc = (Document) Factory.newDocument(adaptedNlQuery);
            ((List<Document>) corpus).add(doc);
            cont.setCorpus(corpus);
            cont.execute();
            AnnotationSet annotSet = doc.getAnnotations();
            AnnotationSet semanticsAnnotSet = annotSet.get("semantics");
            String suppleSemanticOutput = "";
            int numSemanticAnnotation = 0;
            for (Annotation semanticAnnot : semanticsAnnotSet) {
                suppleSemanticOutput += "semantic annotation " + ++numSemanticAnnotation + "\n";
                FeatureMap features = semanticAnnot.getFeatures();
                ArrayList<String> qlfFeatures = (ArrayList<String>) features.get("qlf");
                for (String qlfFeature : qlfFeatures) {
                    if (!qlfFeature.substring(0, 4).equals("name") && !qlfFeature.substring(0, 3).equals("det")) {
                        suppleSemanticOutput += qlfFeature + "\n";
                    }
                }
            }
            logger.info("supple semantic output:\n" + suppleSemanticOutput);
            return suppleSemanticOutput;
        } catch (ExecutionException ex) {
            logger.error(ex);
        } catch (ResourceInstantiationException ex) {
            logger.error(ex);
        }
        return "fail to parse adapted nl query";
    }
}