package org.swip.pivotToMappings.utils;

import java.util.StringTokenizer;

public class StringManipulation {

    public static String removeBlankBorders(String element) {
        String result = element;
        while (result.length() > 0 &&
               (   result.charAt(0) == ' '
                || result.charAt(0) == '\n'
                || result.charAt(0) == '\t'
                || result.charAt(0) == '\r'
                || result.charAt(0) == '\f')) {
            result = result.substring(1);
        }
        while (result.length() > 0 &&
               (   result.charAt(result.length()-1) == ' '
                || result.charAt(result.length()-1) == '\n'
                || result.charAt(result.length()-1) == '\t'
                || result.charAt(result.length()-1) == '\r'
                || result.charAt(result.length()-1) == '\f')) {
            result = result.substring(0,result.length()-1);
        }
        return result;
    }

    public static String separateAndLowerCase(String keyword) {
        String result = "";
        StringTokenizer st = new StringTokenizer(keyword, " \t\n\r\f-");
        while (st.hasMoreTokens()) {
            result += st.nextToken().toLowerCase();
            if (st.hasMoreTokens()) {
                result += " ";
            }
        }
        return result;
    }

    public static String removeLanguageFromLabel(String label) {
        return label.split("@")[0];
    }

}
