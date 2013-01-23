/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.pivotToMappings.model.query.antlr;

/**
 *
 * @author camille
 */
public class SyntaxErrorRuntimeException extends RuntimeException {

    /**
     * Creates a new instance of <code>SyntaxErrorRuntimeException</code> without detail message.
     */
    public SyntaxErrorRuntimeException() {
    }


    /**
     * Constructs an instance of <code>SyntaxErrorRuntimeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SyntaxErrorRuntimeException(String msg) {
        super(msg);
    }
}
