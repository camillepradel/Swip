/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.pivotToMappings.stemmer;

/**
 *
 * @author camille
 */
public class testStemmer {

    
    public static void main(String args[]) {
        SnowballStemmer stemmer = new englishStemmer();
        stemmer.setCurrent("driving");
        stemmer.stem();
        System.out.println(stemmer.getCurrent());
    }
}
