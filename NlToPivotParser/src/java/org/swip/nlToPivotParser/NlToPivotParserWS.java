package org.swip.nlToPivotParser;

import org.swip.exchange.DependencyTree;
import java.io.IOException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.annolab.tt4j.TreeTaggerException;
import org.apache.log4j.Logger;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.syntaxgraph.DependencyStructure;

@Path("/rest/")
public class NlToPivotParserWS {

    private static final Logger logger = Logger.getLogger(NlToPivotParserWS.class);
    StanfordParser sp = null;
    TreeTagger tt = null;
    MaltParser mp = null;

    StanfordParser getSp() {
        if (sp == null) {
            sp = new StanfordParser();
        }
        return sp;
    }

    TreeTagger getTt() {
        if (tt == null) {
            tt = new TreeTagger();
        }
        return tt;
    }

    MaltParser getmp() {
        if (mp == null) {
            mp = new MaltParser();
        }
        return mp;
    }

    public NlToPivotParserWS() {
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("nlToDependenciesJson")
    public DependencyTree nlToDependenciesJson(@QueryParam("nlQuery") @DefaultValue("") String nlQuery,
            @QueryParam("lang") @DefaultValue("fr") String lang,
            @QueryParam("pos") @DefaultValue("treeTagger") String posTagger,
            @QueryParam("dep") @DefaultValue("malt") String depParser) throws IOException, InterruptedException, TreeTaggerException, MaltChainedException {

        logger.info("User nl query: " + nlQuery);
        logger.info("Query language: " + lang);
        logger.info("POS tagger: " + posTagger);
        logger.info("Dependency parser: " + depParser);

        if (posTagger.equals("treeTagger") && depParser.equals("malt")) {
            DependencyStructure graph = null;
            String[] tokens = getTt().posTag(nlQuery, lang);
            graph = getmp().posTaggedToDependecies(tokens, lang);
            logger.info("Returned dependecy graph:");
            getmp().displayDependencyTree(graph);
            DependencyTree dt = MaltParser.changeInDependencyTree(graph);
            logger.info(dt);
            return dt;
        } else if (posTagger.equals("stanfordParser") && depParser.equals("stanfordParser")) {
            return null;
        } else {
            return null;
        }
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("nlToDependencies")
    public String nlToDependencies(@QueryParam("nlQuery") @DefaultValue("") String nlQuery,
            @QueryParam("lang") @DefaultValue("fr") String lang,
            @QueryParam("pos") @DefaultValue("treeTagger") String posTagger,
            @QueryParam("dep") @DefaultValue("malt") String depParser) throws IOException, InterruptedException, TreeTaggerException, MaltChainedException {

        logger.info("User nl query: " + nlQuery);
        logger.info("Query language: " + lang);
        logger.info("POS tagger: " + posTagger);
        logger.info("Dependency parser: " + depParser);
        DependencyStructure graph = null;

        if (posTagger.equals("treeTagger") && depParser.equals("malt")) {
            String[] tokens = getTt().posTag(nlQuery, lang);
            graph = getmp().posTaggedToDependecies(tokens, lang);
        } else if (posTagger.equals("stanfordParser") && depParser.equals("stanfordParser")) {
            return "specified POS tagger and dependency parsers are not supported";
        } else {
            return "specified POS tagger and dependency parsers are not supported";
        }
        logger.info("Returned dependecy graph:");
        getmp().displayDependencyTree(graph);
        logger.info("\n\n\n");
        return graph.toString();
    }
}