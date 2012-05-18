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
    String lang;
    
    
    
    public KbConfiguration(String name, String urlSparql, String patternsPath, String lang)
    {
       this.name = name;
       this.urlSparql = urlSparql;
       this.patternsPath = patternsPath;
       this.lang = lang;
    }
    
}
