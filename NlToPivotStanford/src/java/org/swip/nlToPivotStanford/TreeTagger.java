/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.nlToPivotStanford;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.apache.log4j.Logger;

/**
 *
 * @author camille
 */
public class TreeTagger {

    private static final Logger logger = Logger.getLogger(TreeTagger.class);
    final String treeTaggerDir = "/mnt/data/treeTagger";
    TreeTaggerWrapper tt = null;
    List<String> tokens = null;

    public TreeTagger() {
        System.setProperty("treetagger.home", treeTaggerDir);
        tt = new TreeTaggerWrapper<String>();
    }

    public String[] posTag(String sentence, String lang) throws IOException {
        logger.info("------------");
        logger.info("+ treeTagger");
        tokens = new ArrayList<String>();
        if (lang.equals("en")) {
            tt.setModel(treeTaggerDir + "/lib/english.par");
        } else if (lang.equals("fr")) {
            tt.setModel(treeTaggerDir + "/lib/french.par");
        } else {
            //TODO: rise exception
        }
        
        tt.setHandler(new TokenHandler<String>() {
            public void token(String token, String pos, String lemma) {
                tokens.add(token + "\t" + pos + "\t" + lemma);
            }
        });
        
        try {
            List<String> tokenizedSentence = tokenize(sentence);
            logger.info(tokenizedSentence);
            tt.process(tokenize(sentence));
        } catch (TreeTaggerException ex) {
            logger.error(ex.getMessage());
        }
        
        logger.info("treeTagger result:");
        for (String token : tokens) {
            logger.info(" - " + token);
        }
        String[] tokensArray = tokens.toArray(new String[0]);
        return tokensArray;
    }

    // FIXME: use a good tokenizer
    // (this is taken from http://code.google.com/p/tt4j/wiki/SimpleTokenizer)
    public List<String> tokenize(final String aString) {
        List<String> tokens = new ArrayList<String>();
        BreakIterator bi = BreakIterator.getWordInstance();
        bi.setText(aString);
        int begin = bi.first();
        int end;
        for (end = bi.next(); end != BreakIterator.DONE; end = bi.next()) {
            String t = aString.substring(begin, end);
            if (t.trim().length() > 0) {
                tokens.add(aString.substring(begin, end));
            }
            begin = end;
        }
        if (end != -1) {
            tokens.add(aString.substring(end));
        }
        return tokens;
    }
}
