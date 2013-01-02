/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.patterns.antlr;

/**
 *
 * @author camille
 */
public class PatternRuntimeException extends RuntimeException {

    /**
     * Creates a new instance of <code>PatternRuntimeException</code> without detail message.
     */
    public PatternRuntimeException() {
    }


    /**
     * Constructs an instance of <code>PatternRuntimeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PatternRuntimeException(String msg) {
        super(msg);
    }
}
