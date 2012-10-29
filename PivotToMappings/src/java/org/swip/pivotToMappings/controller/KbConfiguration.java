package org.swip.pivotToMappings.controller;

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
