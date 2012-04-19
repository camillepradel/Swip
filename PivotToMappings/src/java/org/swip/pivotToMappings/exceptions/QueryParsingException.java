/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.swip.pivotToMappings.exceptions;

/**
 *
 * @author camille
 */
public class QueryParsingException extends Exception {

    /**
     * Creates a new instance of <code>QueryParsingException</code> without detail message.
     */
    public QueryParsingException() {
    }


    /**
     * Constructs an instance of <code>QueryParsingException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public QueryParsingException(String msg) {
        super(msg);
    }
}
