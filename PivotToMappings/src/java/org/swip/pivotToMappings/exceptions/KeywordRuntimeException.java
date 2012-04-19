/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.pivotToMappings.exceptions;

/**
 *
 * @author camille
 */
public class KeywordRuntimeException extends RuntimeException {

    /**
     * Creates a new instance of <code>KeywordRuntimeException</code> without detail message.
     */
    public KeywordRuntimeException() {
    }


    /**
     * Constructs an instance of <code>KeywordRuntimeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public KeywordRuntimeException(String msg) {
        super(msg);
    }
}
