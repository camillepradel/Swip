package org.swip.nlToPivotParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.syntaxgraph.DependencyStructure;

/**
 *
 * @author camille
 */
public class TestMaltParser {

    public static void main(String[] args) throws IOException, MaltChainedException {
        MaltParser mp = new MaltParser();
        TreeTagger tt = new TreeTagger();
        System.out.println("Entrez les pharses que vous voulez analyser avec Malt. Entrez \"quit\" pour quitter le programme");
        boolean quit = false;
        while (!quit) {
            System.out.println("\nvotre phrase");
            String sentence = readFromConsole();
            if (sentence.equals("quit")) {
                quit = true;
            } else {
                DependencyStructure graph = null;
                String[] tokens = tt.posTag(sentence, "en");
                graph = mp.posTaggedToDependecies(tokens, "en");
                System.out.println("Returned dependecy graph:");
                mp.displayDependencyTree(graph);
            }
        }
    }
    
    static String readFromConsole() {
        String s = null;
	try{
	    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    s = bufferRead.readLine(); 
	}
	catch(IOException e)
	{
            e.printStackTrace();
	}
	return s;
    }
}
