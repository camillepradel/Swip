/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.nlToPivot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Murloc
 */
public class NlToPivotPreParser 
{
    
    private static String tokensPreParser = "/Users/Murloc/Documents/IRIT/svn/swip/NlToPivot/resources/preParser/";
    
    private boolean startMaximum = false;
    private boolean startMinimum = false;
    private boolean startAverage = false;
    private boolean startSum = false;
    private boolean count = false;
    private boolean maximum = false;
    private boolean minimum = false;
    private boolean average = false;
    private boolean moreThan = false;
    private boolean lessThan = false;
    
    private String query;
    private String adaptedQuery;
    
    
    public NlToPivotPreParser(String query)
    {
        this.query = query.trim();
        this.adaptedQuery = query;
        boolean aggBeginFound = false;
        boolean aggFound = false;
        
        //System.out.println("PreParser : "+this.query);
        
        
        File f = new File(NlToPivotPreParser.tokensPreParser+"list");
        this.parseToken(f, "", true);
        
        if(!aggBeginFound)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"count");
            aggBeginFound = this.count = this.parseToken(f, "", true);
        }
        
        if(!aggBeginFound)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"maximum");
            aggBeginFound = this.startMaximum = this.parseToken(f, "", true);
        }
        
        if(!aggBeginFound)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"minimum");
            aggBeginFound = this.startMinimum = this.parseToken(f, "", true);
        }
        
        if(!aggBeginFound)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"average");
            aggBeginFound = this.startAverage = this.parseToken(f, "", true);
        }
        
        if(!aggBeginFound)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"sum");
            aggBeginFound = this.startSum = this.parseToken(f, "", true);
        }
        
        if(!aggFound)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"maximum");
            aggFound = this.maximum = this.parseToken(f, "");
        }
        
        if(!aggFound)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"minimum");
            aggFound = this.minimum = this.parseToken(f, "");
        }
        
        if(!aggFound)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"average");
            aggFound = this.average = this.parseToken(f, "");
        }
        
        f = new File(NlToPivotPreParser.tokensPreParser+"moreThan");
        this.moreThan = this.parseToken(f, "");

        if(!this.moreThan)
        {
            f = new File(NlToPivotPreParser.tokensPreParser+"lessThan");
            this.lessThan = this.parseToken(f, "");
        }
        
        
        f = new File(NlToPivotPreParser.tokensPreParser+"with");
        this.parseToken(f, "with");
        
        f = new File(NlToPivotPreParser.tokensPreParser+"stopList");
        this.parseToken(f, "");
    }
    
    private boolean parseToken(File f, String replace)
    {
        return this.parseToken(f, replace, false);
    }
    
    private boolean parseToken(File f, String replace, boolean begin)
    {
        boolean encore = true;
        try 
        {
            Scanner s = new Scanner(f);
            while(s.hasNext() && encore)
            {
                String token = (begin? "": " ")+s.nextLine()+" ";
                //System.out.println("TEST : "+token+" == "+this.adaptedQuery+"  || "+begin);
                if((!begin && this.adaptedQuery.contains(token)) || (begin && this.adaptedQuery.startsWith(token)))
                {
                    this.adaptedQuery = this.adaptedQuery.replaceAll(token, replace);
                    encore = false;
                }
            }
        } 
        catch (FileNotFoundException ex) 
        {
            System.err.println("Erreur lecture fichier");
        }
        return !encore;
    }
    
    public boolean getCount()
    {
        return this.count;
    }
    
    public boolean getMoreThan()
    {
        return this.moreThan;
    }
    
    public boolean getLessThan()
    {
        return this.lessThan;
    }
    
    public boolean getMaximum()
    {
        return this.maximum;
    }
    
    public boolean getMinimum()
    {
        return this.minimum;
    }
    public boolean getAverage()
    {
        return this.average;
    }
    
    
    public boolean getStartMaximum()
    {
        return this.startMaximum;
    }
    
    public boolean getStartMinimum()
    {
        return this.startMinimum;
    }
    public boolean getStartAverage()
    {
        return this.startAverage;
    }
    
    public boolean getStartSum()
    {
        return this.startSum;
    }
    
    public String getAdaptedQuery()
    {
        String ret = this.adaptedQuery.replaceAll("\\.", "");
        return NlToPivotUtils.wordsToDecimalNum(ret);
    }
}
