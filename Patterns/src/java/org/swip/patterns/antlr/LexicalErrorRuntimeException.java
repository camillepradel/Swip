/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.patterns.antlr;

/**
 *
 * @author camille
 */
public class LexicalErrorRuntimeException extends RuntimeException {

    /**
     * Creates a new instance of <code>LexicalErrorRuntimeException</code> without detail message.
     */
    public LexicalErrorRuntimeException() {
    }


    /**
     * Constructs an instance of <code>LexicalErrorRuntimeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public LexicalErrorRuntimeException(String msg) {
        super(msg);
    }
}
