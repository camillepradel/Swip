/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.pivotToMappings.exceptions;

/**
 *
 * @author camille
 */
public class LiteralException extends Exception {

    /**
     * Creates a new instance of <code>SyntaxErrorException</code> without detail message.
     */
    public LiteralException() {
    }


    /**
     * Constructs an instance of <code>SyntaxErrorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public LiteralException(String msg) {
        super(msg);
    }
}
