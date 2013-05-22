
package org.tartarus.snowball;

public class TestApp{

    public static void main(String [] args) throws Throwable {

    	String test = new String();
        test ="saws";
        String algorithmus = new String();
        algorithmus = "english";
    	
	Class stemClass = Class.forName("org.tartarus.snowball.ext." +
					algorithmus + "Stemmer");
        SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();

        
	StringBuffer input = new StringBuffer();


	int repeat = 1;


	Object [] emptyArgs = new Object[0];
	int character;
	int j = 0;
	char[] testen = test.toCharArray();
	while (j<testen.length) {
		character = testen[j];
	    char ch = (char) character;
	//    if (Character.isWhitespace((char) ch)) {
		if (input.length() > 0) {
		    stemmer.setCurrent(input.toString());
		    for (int i = repeat; i != 0; i--) {
			stemmer.stem();
		    }
		    System.out.print(stemmer.getCurrent());
		    input.delete(0, input.length());
		}
	   // } else {
		input.append(Character.toLowerCase(ch));
	   // }
	    j++;
	}

    }
}