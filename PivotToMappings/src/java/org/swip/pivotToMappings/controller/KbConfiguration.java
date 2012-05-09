/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.pivotToMappings.controller;

/**
 *
 * @author Murloc
 */
public class KbConfiguration
{
    String name;
    String urlSparql;
    String patternsPath;
    
    
    
    public KbConfiguration(String name, String urlSparql, String patternsPath)
    {
       this.name = name;
       this.urlSparql = urlSparql;
       this.patternsPath = patternsPath;
    }
    
}
