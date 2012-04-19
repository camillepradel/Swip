/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.pivotToMappings.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author camille
 */
public class ClassToBeReturned {
    
    private String string1 = null;
    private String string2 = null;
    private int int1 = 0;
    private List<String> strings = null;
    private NestedClassToBeReturned nestedObject = null;

    public ClassToBeReturned() {
    }

    /**
     * @return the string1
     */
    public String getString1() {
        return string1;
    }

    /**
     * @param string1 the string1 to set
     */
    public void setString1(String string1) {
        this.string1 = string1;
    }

    /**
     * @return the string2
     */
    public String getString2() {
        return string2;
    }

    /**
     * @param string2 the string2 to set
     */
    public void setString2(String string2) {
        this.string2 = string2;
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

    /**
     * @return the nestedObject
     */
    public NestedClassToBeReturned getNestedObject() {
        return nestedObject;
    }

    /**
     * @param nestedObject the nestedObject to set
     */
    public void setNestedObject(NestedClassToBeReturned nestedObject) {
        this.nestedObject = nestedObject;
    }

    /**
     * @return the strings
     */
    public List<String> getStrings() {
        return strings;
    }

    /**
     * @param strings the strings to set
     */
    public void setStrings(List<String> strings) {
        this.strings = strings;
    }
    
}
