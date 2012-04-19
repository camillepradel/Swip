package org.swip.pivotToMappings.utils;

public class StopList {

//    final static String[] frenchStopListArray = {"a", "est", "le", "la", "des", "de", "pour", "comme", "une", "un", "se"};
//    final public static Collection<String> frenchStopList = new HashSet<String>(java.util.Arrays.asList(frenchStopListArray));

    final static String[] englishStopListArray = {"has", "is", "the", "of", "from", "for", "a", "an", "in"};
//    final public static Collection<String> englishStopList = new HashSet<String>(java.util.Arrays.asList(englishStopListArray));

    public static boolean stopEnglishWord(String word) {
        for (String s : englishStopListArray) {
            if (s.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

}
