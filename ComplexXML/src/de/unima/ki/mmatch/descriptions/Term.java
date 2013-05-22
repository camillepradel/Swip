package de.unima.ki.mmatch.descriptions;


import org.tartarus.snowball.SnowballStemmer;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.Setting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author Dominique Ritze
 * 
 * To compare the names of the entities not just strings are used. 
 * Therefore the name as string is separated into parts, based on
 * specified delimiters, and normalized. Also information about the head noun is saved.
 * 
 * This class gives access to the parts and the additional information.
 *
 */
public class Term{
		
	private String content;
	private String[] tokens;
	private int headNounIndex;	
	private String[] normalizedTokens;
	private String[] normalizedTokensWithoutSpecialTokens;
	private String[] tokensWithoutSpecialTokens;
	private String[] normalizedTokensPropertyWithoutIgnore;
	private boolean hyphenDelimiter, blankDelimiter, underscoreDelimiter;
		
	
	/**
	 * Constructor for concepts
	 * @param content
	 * @param counter
	 * @throws MMatchException
	 */
	public Term(String content, boolean[] delimiter) throws MMatchException{		
		setDelimiter(delimiter);
		this.content = content;
		this.split();
		this.normalizeTokens();		
		this.normalizedTokensWithoutSpecialTokens = normalizedTokensWithoutSpecialTokens(getNormalizedTokens());
		this.normalizedTokensPropertyWithoutIgnore = getTokens();
		this.tokensWithoutSpecialTokens = normalizedTokensWithoutSpecialTokens(getTokens());		
	}	
	
	/**
	 * Constructor for properties (if a tokens is contained in the file ignore.txt it should not be influence the similarity value)
	 * @param content
	 * @param property 
	 * @throws MMatchException
	 */
	public Term(String content,  boolean[] delimiter, boolean property) throws MMatchException{		
		setDelimiter(delimiter);
		this.content = content;
		this.split();
		this.normalizeTokens();			
		this.normalizedTokensWithoutSpecialTokens = normalizedTokensWithoutSpecialTokens(getNormalizedTokens());
		this.tokensWithoutSpecialTokens = normalizedTokensWithoutSpecialTokens(getTokens());
		this.normalizedTokensPropertyWithoutIgnore = normalizedTokensPropertyWithoutIgnore(tokens);
		this.normalizedTokensWithoutSpecialTokens = normalizedTokensPropertyWithoutIgnore(normalizedTokensWithoutSpecialTokens);
		this.tokensWithoutSpecialTokens = normalizedTokensPropertyWithoutIgnore(tokensWithoutSpecialTokens);		
	}
	
	/**
	 * 
	 * @param values The number of hyphens, blanks and underscores which occur in the ontology.
	 */
	public void setDelimiter(boolean[] delimiter) {
		this.hyphenDelimiter = delimiter[0];
		this.blankDelimiter = delimiter[1];
		this.underscoreDelimiter = delimiter[2];
	}	
	
	/**
	 *  
	 * @return The array with non-normalized tokens and with the special tokens.
	 */
	public String[] getTokens() {
		return tokens;
	} 
	
	/**
	 * 
	 * @return The array with non-normalized tokens and without the special tokens.
	 */
	public String[] getTokensWithoutSpecialTokens() {
		return tokensWithoutSpecialTokens;
	}
	
	/**
	 * 
	 * @param normalized True if the number of the normalized tokens without special tokens is required, false if 
	 * the number of non-normalized tokens without special tokens is required.
	 * @return The number of normalized/non-normalized tokens.
	 */
	public int getNumberOfTokensWithoutSpecialTokens(boolean normalized) {
		if(normalized) {
			return normalizedTokensWithoutSpecialTokens.length;
		}
		else {
			return tokensWithoutSpecialTokens.length;
		}		
	}
	
	/**
	 * This method checks if one token of the array is contained in the ignore file. 
	 * If such a token is found this token is eliminated.
	 * @param usedTokens The array of the normalized or non-normalized tokens.
	 * @return An array without the words contained in ignore.txt.
	 */
	private String[] normalizedTokensPropertyWithoutIgnore(String[] usedTokens) {
		String[] normalizedTokensWithoutSpecialTokensBefore = new String[usedTokens.length];
		String[] tokens;
		Set<Integer> wrongWords = new HashSet<Integer>();
		for(int i=0; i<usedTokens.length; i++) {			
			
			if(Setting.ignoreWords.contains(usedTokens[i].toLowerCase()) || Setting.inverseWords.contains(usedTokens[i].toLowerCase())) {
				wrongWords.add(i);
				continue;							
			}		
			
			normalizedTokensWithoutSpecialTokensBefore[i]= usedTokens[i].toLowerCase();	
		}		
		//check if a word out of the files has been in the name and remove it
		if(wrongWords.size() > 0) {
			int index = 0;
			tokens = new String[usedTokens.length-wrongWords.size()];
            for(int i=0; i<usedTokens.length; i++) {  
            	if(wrongWords.contains(i)) {
            		continue;
            	}
            	else {
            		tokens[index] = normalizedTokensWithoutSpecialTokensBefore[i];	
            		index++;
            	}        		            		
			}
		}
		else{
			tokens = new String[usedTokens.length];
			for(int i=0; i<usedTokens.length; i++) {
				tokens[i] = normalizedTokensWithoutSpecialTokensBefore[i];				
			}
		}		
		return tokens;
	}
	
	/**
	 * 
	 * @param normalized True if the method should return the array of normalized tokens without special tokens, false 
	 * if the method should return the array of non-normalized tokens without special tokens.
	 * @return The requested array.
	 */
	public String[] getTokensWithoutSpecialTokensNormalizedOrNot(boolean normalized) {
		if(normalized) {
			return normalizedTokensWithoutSpecialTokens;			
		}
		else {
			return tokensWithoutSpecialTokens;
		}
	}
	
	/**
	 * The words which the files strange and anti contains should not influence the similarity of two terms.
	 * This method deletes such words and built a new vector which contains the term without them.
	 * 
	 * @return The normalized tokens without special words like of, and, or. 
	 */	
	private String[] normalizedTokensWithoutSpecialTokens(String[] usedTokens) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i=0; i<usedTokens.length; i++) {
			list.add( usedTokens[i] );
		}
		
		for (int i=0; i<list.size(); i++) {
			String word = list.get( i );
			if(Setting.antiWords.contains( word ) || Setting.strangeWords.contains( word )) {
				list.remove( word );
			}
		}
		
		String[] tokens = new String[ list.size() ];
		
		for (int i=0; i<tokens.length; i++) {
			tokens[i] = list.get( i );
		}
		
		return tokens;
	}
		
	
	/**
	 * 
	 * @return The array with normalized tokens and special tokens.
	 */
	public String[] getNormalizedTokens() {
		return normalizedTokens;
	}
	
	/**
	 * Fills the token array with the tokens.
	 */
	private void split() {		
		int start =0;
		String current = new String();
		
		
		for (int i=1; i<content.length(); i++) {

			if ( i == content.length()-1 ) {
				if(content.charAt(i) == content.toUpperCase().charAt(i) && 
						!(content.toUpperCase().charAt(i) == content.toLowerCase().charAt(i)) &&
						!(content.toUpperCase().charAt(i-1) == content.charAt(i-1))) {
					current = current + content.substring(start,i) +"$" + content.substring(i);
				}
				else{
					current = current + content.substring(start);
				}				
				break;
			}			
			
			//small letter
			if ( content.charAt(i-1) == content.toLowerCase().charAt(i-1) &&
					!(content.toLowerCase().charAt(i-1) == content.toUpperCase().charAt(i-1))){
			//capital letter
			  if ( content.charAt(i) == content.toUpperCase().charAt(i) && 
					!(content.toUpperCase().charAt(i) == content.toLowerCase().charAt(i))) {				
					current = current + content.substring(start, i) + "$";				
					start = i;				
			  }			
			
			}
			else{
				//capital letter
				if(content.charAt(i-1) == content.toUpperCase().charAt(i-1) && 
						!(content.toUpperCase().charAt(i-1) == content.toLowerCase().charAt(i-1))){
					//capital letter
					if(content.charAt(i) == content.toUpperCase().charAt(i) &&
							!(content.toLowerCase().charAt(i) == content.toUpperCase().charAt(i))){
						//small letter
						if(content.charAt(i+1) == content.toLowerCase().charAt(i+1) &&
								!(content.toLowerCase().charAt(i+1) == content.toUpperCase().charAt(i+1))){
							current = current + content.substring(start,i) + "$" + content.substring(i,i+1);
							start =i+1;
						}
						
					}
					
				}
				
			}				
			
		}	
		//problems with ISBN, URL and so on!!!
/*		int i=1;
		//successive capital letters are seperated
		while(i<current.length()){
			if((current.charAt(i-1) == current.toUpperCase().charAt(i-1) && !(current.toUpperCase().charAt(i-1) == current.toLowerCase().charAt(i-1))
					&& !(current.substring(i-1,i).equals("$"))) && (current.charAt(i) == current.toUpperCase().charAt(i) && 
					!(current.toUpperCase().charAt(i) == current.toLowerCase().charAt(i)) &&
					!(current.substring(i,i+1).equals("$")))){
				current = current.substring(0,i).toLowerCase() + "$" + current.substring(i).toLowerCase();
				i=0;				
			}
			i++;			
		} */
        
		if(hyphenDelimiter) {
			current = current.replace("-", "$"); 
		}
		if(underscoreDelimiter) {
			current = current.replace("_", "$");
		}
		if(blankDelimiter) {
			current = current.replace(" ", "$");
		}		        

		//several $'s are combined
		Pattern p = Pattern.compile("\\$+");
		Matcher m = p.matcher(current);	
		current = m.replaceAll("\\$");
		
        //splitting the string at the $'s and writing the tokens into the array  		
		this.tokens = current.split("\\$");
	}
	
	/**
	 * Fills the normalizedTokens array with normalized tokens.
	 * 
	 * @throws MMatchException
	 */
	private void normalizeTokens() throws MMatchException{
	    
		try{
			normalizedTokens = new String[tokens.length];
	    	//String test = content;
	        String algorithm = new String();
	        //one can choose the algorithm (german or english)
	        algorithm= "English";
	    
	        //stemmer is provided
	     	Class stemClass = Class.forName("org.tartarus.snowball.ext." +
						algorithm + "Stemmer");
	        SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();

	        for (int i=0; i<tokens.length;i++){    	  
	        	stemmer.setCurrent(tokens[i]);
	        	stemmer.stem();
	        	normalizedTokens[i] = stemmer.getCurrent().toLowerCase();
	        }	
		} catch(Exception e){
			throw new MMatchException(
					MMatchException.STEMMER_ERROR, 
					"Could not stemm the word.",
					e			
			);
		}
			
		           	
  }
	
	/**
	* 	
	* @return The number of tokens which this term consists of.
	*/
	public int getNumberOfTokens() {
		return tokens.length;
	}
	
	/**
	 * 
	 * @return Index of the head noun.
	 * @throws MMatchException
	 */	
	public int getHeadNounIndex() throws MMatchException{		
		if(normalizedTokensPropertyWithoutIgnore.length == 1){
			return 0;
		}else
		{  				
			for(int i=1; i<normalizedTokensPropertyWithoutIgnore.length; i++){				
				if(Setting.antiWords.contains(getTokens()[i].toLowerCase())){	
					return i-1;
				}
			}				  


			for(int i=0; i<normalizedTokensPropertyWithoutIgnore.length; i++){
				if(Setting.strangeWords.contains(getTokens()[i])){						   
					return -1;
				}
			}

		}
		
		return getNumberOfTokensWithoutSpecialTokens(true)-1;
	}
	/**
	 * @return A string with the normalized tokens and the head noun
	 * format: content: normalizedToken[0], normalizedToken[1],...
	 * if normalizedToken == head noun: normalizedToken[2] (!) 
	 * @throws MMatchException
	 */
	public String tokensToString() throws MMatchException {
		String words = content + ": ";
				
		if (headNounIndex != -1){			
			for(int i=0; i<getNumberOfTokensWithoutSpecialTokens(true); i++){					
				if(i == getHeadNounIndex()){
					words += "(!) ";
				}
				words += (i == getNumberOfTokens()-1) ? getTokensWithoutSpecialTokensNormalizedOrNot(true)[i] : getTokensWithoutSpecialTokensNormalizedOrNot(true)[i] +", ";
			}
		} 
		else {
			for(int i=0; i<getNumberOfTokens(); i++){
				words += (i == getNumberOfTokens()-1) ? getTokensWithoutSpecialTokensNormalizedOrNot(true)[i] : getTokensWithoutSpecialTokensNormalizedOrNot(true)[i] +", ";				
			  }				
		}
			
		return words;		
	}
}