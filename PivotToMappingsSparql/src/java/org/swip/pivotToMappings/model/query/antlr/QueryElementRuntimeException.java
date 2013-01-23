/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.pivotToMappings.model.query.antlr;

/**
 *
 * @author camille
 */
public class QueryElementRuntimeException extends RuntimeException {

    /**
     * Creates a new instance of <code>QueryElementRuntimeException</code> without detail message.
     */
    public QueryElementRuntimeException() {
    }


    /**
     * Constructs an instance of <code>QueryElementRuntimeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public QueryElementRuntimeException(String msg) {
        super(msg);
    }
}
