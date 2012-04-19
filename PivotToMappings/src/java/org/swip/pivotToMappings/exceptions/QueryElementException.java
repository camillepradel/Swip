/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.pivotToMappings.exceptions;

/**
 *
 * @author camille
 */
public class QueryElementException extends Exception {

    /**
     * Creates a new instance of <code>QueryElementException</code> without detail message.
     */
    public QueryElementException() {
    }


    /**
     * Constructs an instance of <code>QueryElementException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public QueryElementException(String msg) {
        super(msg);
    }
}
