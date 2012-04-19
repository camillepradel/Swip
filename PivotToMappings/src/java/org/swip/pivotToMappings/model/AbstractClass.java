/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.pivotToMappings.model;

import java.io.Serializable;

/**
 *
 * @author camille
 */
public abstract class AbstractClass implements  Serializable {
    
    int int1 = 1;

    public AbstractClass() {
    }

    /**
     * @return the int1
     */
    public int getInt1() {
        return int1;
    }

    /**
     * @param int1 the int1 to set
     */
    public void setInt1(int int1) {
        this.int1 = int1;
    }
    
}
