/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.patterns;

/**
 *
 * @author camille
 */
public class PatternsParsingException extends Exception {

    /**
     * Creates a new instance of <code>PatternsParsingException</code> without detail message.
     */
    public PatternsParsingException() {
    }


    /**
     * Constructs an instance of <code>PatternsParsingException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PatternsParsingException(String msg) {
        super(msg);
    }
}
