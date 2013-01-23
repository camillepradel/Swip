/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.pivotToMappings.model.query.antlr;

/**
 *
 * @author camille
 */
public class LiteralRuntimeException extends RuntimeException {

    /**
     * Creates a new instance of <code>LiteralRuntimeException</code> without detail message.
     */
    public LiteralRuntimeException() {
    }


    /**
     * Constructs an instance of <code>LiteralRuntimeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public LiteralRuntimeException(String msg) {
        super(msg);
    }
}
