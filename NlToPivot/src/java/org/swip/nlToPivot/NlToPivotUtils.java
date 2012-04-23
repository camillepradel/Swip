/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.nlToPivot;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Murloc
 */
public class NlToPivotUtils 
{
     private static String WithSeparator(long number) {
        if (number < 0) {
            return "-" + WithSeparator(-number);
        }
        if (number / 1000L > 0) {
            return WithSeparator(number / 1000L) + ","
                    + String.format("%1$03d", number % 1000L);
        } else {
            return String.format("%1$d", number);
        }
    }
    private static String[] numerals = { "zero", "one", "two",
            "three", "four", "five", "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "ninteen", "twenty", "thirty", "forty",
            "fifty", "sixty", "seventy", "eighty", "ninety", "hundred" };

    private static long[] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 30, 40, 50, 60, 70,
            80, 90, 100 };

    private static ArrayList<String> list = new ArrayList<String>(
            Arrays.asList(numerals));
    private static long parseNumerals(String text) throws Exception {
        long value = 0;
        String[] words = text.replaceAll(" and ", " ").split("\\s");
        for (String word : words) {
            if (!list.contains(word)) {
                throw new Exception("Unknown token : " + word);
            }

            long subval = getValueOf(word);
            if (subval == 100) {
                if (value == 0)
                    value = 100;
                else
                    value *= 100;
            } else
                value += subval;
        }

        return value;
    }

    private static long getValueOf(String word) {
        return values[list.indexOf(word)];
    }
    private static String[] words = { "trillion", "billion", "million", "thousand" };

    private static long[] digits = { 1000000000000L, 1000000000L,1000000L, 1000L };

    private static long parse(String text) throws Exception {
        text = text.toLowerCase().replaceAll("[\\-,]", " ").replaceAll(" and "," ");
        long totalValue = 0;
        boolean processed = false;
        for (int n = 0; n < words.length; n++) {
            int index = text.indexOf(words[n]);
            if (index >= 0) {
                String text1 = text.substring(0, index).trim();
                String text2 = text.substring(index + words[n].length()).trim();

                if (text1.equals(""))
                    text1 = "one";

                if (text2.equals(""))
                    text2 = "zero";

                totalValue = parseNumerals(text1) * digits[n]+ parse(text2);
                processed = true;
                break;
            }
        }

        if (processed)
            return totalValue;
        else
            return parseNumerals(text);
    }
    
    public static int longToInt(long l)
    {
        int ret = 0;
        if(l < Integer.MIN_VALUE)
        {
            ret = Integer.MIN_VALUE;
        }
        else if (l > Integer.MAX_VALUE)
        {
            ret = Integer.MAX_VALUE;
        }
        else
        {
            try
            {
                ret = (int)l;
            }
            catch(Exception e)
            {
                ret = 0;
            }
        }
        
        return ret;
    }
    
    public static String wordsToDecimalNum(String q)
    {
        System.out.println("Traitement : \""+q+"\" ...");
        q = q.trim().replaceAll("  ", " ");
        String ret = "";
        String[] split = q.split(" ");
        ArrayList<String> sepWords = new ArrayList(Arrays.asList(split));
        int scope = sepWords.size();
        int i = 0;
         
         while(!sepWords.isEmpty() && scope > 0)
         {
             boolean found = false;

             while(!found && scope > 0)
             {
                 while(!found && i + scope <= sepWords.size())
                 {
                     String toSearch = "";
                     for(int j = 0; j < scope; j++)
                     {
                         if(j != 0)
                             toSearch += " ";
                         try{
                         toSearch += sepWords.get(j+i);     
                         }catch(Exception e)
                         {
                             System.err.println("ERREUR ICI !!!! ("+sepWords.toString()+" || "+(i+j));
                         }
                     }

                     //System.out.println("To search : " + toSearch);
                     int val = 0;
                     try
                     {
                         long valLong = parse(toSearch);
                         System.out.println("TROUVE !! "+toSearch+" -> "+valLong);
                         val = longToInt(valLong);
                         found = true;
                     }
                     catch(Exception e)
                     {found = false;}

                     if(found)
                     {
                         for(int j = 0; j < scope; j++)
                         {
                             sepWords.remove(j+i);
                             if(j == scope-1)
                                 sepWords.add(j+i, ""+val);
                             i--;
                         }
                     }

                     i++;
                 }

                 if(!found)
                 {
                     scope--;
                     i = 0;
                 }
             }
         }
        for(String s : sepWords)
        {
            ret += s+" ";
        }
        return ret;
    }
}
