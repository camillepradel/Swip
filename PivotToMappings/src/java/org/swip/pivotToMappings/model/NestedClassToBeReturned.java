/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.pivotToMappings.model;

/**
 *
 * @author camille
 */
public class NestedClassToBeReturned {
    
    private String string3 = null;
    private int int2 = 0;

    public NestedClassToBeReturned(String string3, int int2) {
        this.string3 = string3;
        this.int2 = int2;
    }

    public NestedClassToBeReturned() {
    }

    /**
     * @return the string3
     */
    public String getString3() {
        return string3;
    }

    /**
     * @param string3 the string3 to set
     */
    public void setString3(String string3) {
        this.string3 = string3;
    }

    /**
     * @return the int2
     */
    public int getInt2() {
        return int2;
    }

    /**
     * @param int2 the int2 to set
     */
    public void setInt2(int int2) {
        this.int2 = int2;
    }
    
}
