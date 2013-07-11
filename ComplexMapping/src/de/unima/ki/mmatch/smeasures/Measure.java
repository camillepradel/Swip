/**
 * @author Dominique Ritze
 * @date 03.07.08
 */

package de.unima.ki.mmatch.smeasures;

import java.util.HashMap;
import uk.ac.shef.wit.simmetrics.similaritymetrics.*;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.Setting;
import de.unima.ki.mmatch.descriptions.Term;

public class Measure {	
	
	/**
	 * 
	 * @param t1 The first term.
	 * @param t2 The second term.
	 * @return The similarity value of the terms: similarity value of the normalized terms and similarity value of the non-normalized terms.
	 * @throws MMatchException	 
	 */
	
	public Double compare(Term t1, Term t2) throws MMatchException {		
	    Double valueNotStemmed = compareTerms(t1,t2,false);
		Double valueStemmed = compareTerms(t1,t2,true);	
		
		if(valueNotStemmed == null || valueStemmed == null) {
			return null;
		} 
		else {
			//weighted sum of the two value - stemmed and not stemmed
			return (((1-Setting.getStemWeight())*valueNotStemmed) + (Setting.getStemWeight()*valueStemmed)); 
		}		
	}	
	
	/**
	 * This Method compares two terms and declares the similarity between them, based on the Levenshtein measure.
	 * If the two terms differs too much in the length, the similarity is lesser.
	 * The similarity of the HeadNouns is more important than the similarity of the other words.
	 * The distance between two words in the term is also important.
	 * 
	 * @param t1 The first term
	 * @param t2 The second term	 
	 * @param normalized True if the similarity value of the normalized terms is required.
	 * @return Similarity (Levenshtein) between the first and the second term
	 * @throws MMatchException
	 */
	public Double compareTerms(Term t1, Term t2, boolean normalized) throws MMatchException {
		//one or both terms are null
		if(t1 == null || t2 == null) {
			return null;
		}
		//both terms consists only of one word
		if((t1.getNumberOfTokensWithoutSpecialTokens(normalized) == t2.getNumberOfTokensWithoutSpecialTokens(normalized)) && t1.getNumberOfTokensWithoutSpecialTokens(normalized) ==1){
			if(!(compareValue(t1,t2,0,0,true, normalized) < 0.0 || compareValue(t1,t2,0,0,true,normalized) > 1.0)) {
				return compareValue(t1,t2,0,0,true, normalized);
			}
			else {
				throw new MMatchException(
						MMatchException.COMPARE_VALUE_OUT_OF_BOUNDS, 
						"Compare value <0 or >1."						
						);
			}
			
		}
		//more than one word
		else {			
			int shorterLength;
			int tallerLength;
			boolean t1Shorter=false;
			
			//identifies the shorter and the larger Term 
			if(t1.getNumberOfTokensWithoutSpecialTokens(normalized)<=t2.getNumberOfTokensWithoutSpecialTokens(normalized)){
				shorterLength = t1.getNumberOfTokensWithoutSpecialTokens(normalized);
				tallerLength = t2.getNumberOfTokensWithoutSpecialTokens(normalized);
				t1Shorter=true;
			}
			else{
				shorterLength = t2.getNumberOfTokensWithoutSpecialTokens(normalized);
				tallerLength = t1.getNumberOfTokensWithoutSpecialTokens(normalized);
			}
			
			double [][] similarityValues = new double [shorterLength][tallerLength];			
			double [] similarityTallerTerm = new double[tallerLength];
			double [] similarityShorterTerm = new double[shorterLength];
			double sumSimilarityTaller = 0.0;
			double sumSimilarityShorter = 0.0;
			double multiplierWord=1.0;
			double returnValue =0.0;
			//create a matrix with the similarityvalues
			for(int i=0; i<shorterLength; i++){
				for(int j=0; j<tallerLength; j++){					
						similarityValues[i][j] = compareValue(t1,t2,i,j,t1Shorter,normalized);							
				}										
			}
			//create a vector for each row with the similarities and find the highest value			
			similarityShorterTerm =createVector(similarityValues, shorterLength, tallerLength, false, t1, t2, normalized);
            //create a vector for each column with the similarities and find the highest value
			similarityTallerTerm =createVector(similarityValues, tallerLength, shorterLength, true, t1, t2, normalized);			
			
			//sum all maxValues of each column			
			sumSimilarityTaller = sum(tallerLength, similarityTallerTerm, t1Shorter, tallerLength, shorterLength, t1, t2, normalized);
			//sum all maxValues of each row			
			sumSimilarityShorter = sum(shorterLength, similarityShorterTerm, t1Shorter, tallerLength, shorterLength, t1, t2, normalized);
			
			//if terms differ too much in the length
			if(3.0*(double)shorterLength<=(double)tallerLength) {
				multiplierWord=2.0/3.0;				
			}			
			
			//check if the words are really, but one ontology used delimiters
			String tmp1 = "", tmp2 = "";
			for(int i=0; i<tallerLength; i++) {
				if(!t1Shorter) {
					tmp1 += t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[i].toLowerCase();
				}
				else {
					tmp1 += t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[i].toLowerCase();
				}
			}
			for(int i=0; i<shorterLength; i++) {
				if(t1Shorter) {
					tmp2 += t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[i].toLowerCase();
				}
				else {
					tmp2 += t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[i].toLowerCase();
				}
			}
			
			if(tmp1.equalsIgnoreCase(tmp2)) {
				return 1.0;
			}
			
			
			//normalize
			returnValue  = multiplierWord*(((sumSimilarityTaller/tallerLength)+(sumSimilarityShorter/shorterLength))/2.0);			
			//if the return value = 1.0 check if the terms are really the same
		    if(returnValue == 1.0) {
				for(int i=0; i<shorterLength; i++) {
					if(!(t1.getNormalizedTokens()[i].equals(t2.getNormalizedTokens()[i]))) {
						returnValue = returnValue -0.01;
					}
					
				}
			}
			/*
			 * bonus identical tokens
			 * Compute the number of identical tokens and multiply 1-similarity value with this number and normalize the value.
			 * If the two terms have many identical tokens, the similarity value rises.   
			 */		    
			returnValue = returnValue + ((1-returnValue)*((double)identicalTokens(t1, t2, normalized)/(double)tallerLength));
			if(!(returnValue < 0.0 || returnValue > 1.0)) {
				return returnValue;
			}
			else {
				throw new MMatchException(
						MMatchException.COMPARE_VALUE_OUT_OF_BOUNDS, 
						"Compare value <0 or >1. return=" + returnValue +" 1. wort: " + t1.tokensToString()
						+ " 2. Wort: " + t2.tokensToString()
						);
			}
			
		}		
	}	
	
	/**
	 * Creates a wigthed sum for all the entries in a vector, which are the maximum values of a term.
	 * 
	 * @param length
	 * @param similarityTerm A vector with the maximum value of each row/column. 
	 * @param t1Shorter True if the first term is the shorter one.
	 * @param tallerLength 
	 * @param shorterLength 
	 * @param t1 The first term.
	 * @param t2 The second term. 
	 * @param normalized If the similarity value of the normalized terms is required.
	 * @return The sum of the similarityTerm entries.
	 * @throws MMatchException
	 */
	private double sum(int length, double[] similarityTerm, boolean t1Shorter, int tallerLength, int shorterLength, Term t1, Term t2, boolean normalized)throws MMatchException{
		double sum = 0.0;		
		double multiplierHeadNoun= Setting.getHeadNounWeight();
		double multiplierNotHeadNoun= 1-Setting.getHeadNounWeight();
		double multiplier = 1.0;
	
		//if the current part of the term is not the headnoun, the multiplier is <1, else >1
		for(int i=0; i<length; i++){
			if(length > 1 ){
				//t1 shorter
				if(t1Shorter== true){
					//true if the sum of the columns is required
					if(length == tallerLength){			
						//the current index is the headnoun index
						if(i== t2.getHeadNounIndex()){				
							//the weight is not set by the user
							if(Setting.getHeadNounWeight() == -1.0 ) {
								multiplierHeadNoun = (1.0+1.0/t2.getNumberOfTokensWithoutSpecialTokens(normalized));								
							}
							else {
								//weight headnoun is the property multiplied by the length
								multiplierHeadNoun = Setting.getHeadNounWeight()*t2.getNumberOfTokensWithoutSpecialTokens(normalized);
							}
							multiplier=multiplierHeadNoun;
						}
						//the current index is not the headnoun index
						else {
							if(Setting.getHeadNounWeight() == -1.0) {
								multiplierNotHeadNoun =  ((t2.getNumberOfTokensWithoutSpecialTokens(normalized)
										-(1.0+1.0/t2.getNumberOfTokensWithoutSpecialTokens(normalized)))/(t2.getNumberOfTokensWithoutSpecialTokens(normalized)-1));
							}
							else {
								//weight not headnoun is 1-property multiplied by the length divided by length-1 (number of the not headnouns)
								multiplierNotHeadNoun = (1-Setting.getHeadNounWeight())*t2.getNumberOfTokensWithoutSpecialTokens(normalized)/
								(t2.getNumberOfTokensWithoutSpecialTokens(normalized)-1);								
							}
							multiplier=multiplierNotHeadNoun;							
						}
					}
					//the sum of the rows is required
					else {
						//the current index is the headnoun index
						if(i==t1.getHeadNounIndex()) {
							if(Setting.getHeadNounWeight() == -1.0) {
								multiplierHeadNoun = (1.0+1.0/t1.getNumberOfTokensWithoutSpecialTokens(normalized));
							}
							else {
								multiplierHeadNoun = Setting.getHeadNounWeight()*t1.getNumberOfTokensWithoutSpecialTokens(normalized);
							}
							multiplier=multiplierHeadNoun;								
						}
						//the current index is not the headnoun index
						else {
							if(Setting.getHeadNounWeight() == -1.0) {
								multiplierNotHeadNoun =((t1.getNumberOfTokensWithoutSpecialTokens(normalized)
										-(1.0+1.0/t1.getNumberOfTokensWithoutSpecialTokens(normalized)))/(t1.getNumberOfTokensWithoutSpecialTokens(normalized)-1));
							}
							else {
								multiplierNotHeadNoun = (1.0-Setting.getHeadNounWeight())*t1.getNumberOfTokensWithoutSpecialTokens(normalized)/
								(t1.getNumberOfTokensWithoutSpecialTokens(normalized)-1);								
							}							
							multiplier=multiplierNotHeadNoun;							
						}
					}					
						
				}
				//t2 is the shorter term
				else {					
					if(length == tallerLength) {
						if(i== t1.getHeadNounIndex()) {			
							if(Setting.getHeadNounWeight() == -1.0) {
								multiplierHeadNoun = (1.0+1.0/t1.getNumberOfTokensWithoutSpecialTokens(normalized));
							}
							else {
								multiplierHeadNoun = Setting.getHeadNounWeight()*t1.getNumberOfTokensWithoutSpecialTokens(normalized);
							}
							
							multiplier=multiplierHeadNoun;
						}
						else {
							if(Setting.getHeadNounWeight() == -1.0) {
								multiplierNotHeadNoun = ((t1.getNumberOfTokensWithoutSpecialTokens(normalized)
										-(1.0+1.0/t1.getNumberOfTokensWithoutSpecialTokens(normalized)))/(t1.getNumberOfTokensWithoutSpecialTokens(normalized)-1));
							}
							else {
								multiplierNotHeadNoun = (1-Setting.getHeadNounWeight())*t1.getNumberOfTokensWithoutSpecialTokens(normalized)/
								(t1.getNumberOfTokensWithoutSpecialTokens(normalized)-1);								
							}
							
							multiplier=multiplierNotHeadNoun;
						}
					}
					else {
						if(i==t2.getHeadNounIndex()) {
							if(Setting.getHeadNounWeight() == -1.0) {
								multiplierHeadNoun = (1.0+1.0/t2.getNumberOfTokensWithoutSpecialTokens(normalized));
							}
							else {
								multiplierHeadNoun = Setting.getHeadNounWeight()*t2.getNumberOfTokensWithoutSpecialTokens(normalized);
							}
							
							multiplier=multiplierHeadNoun;
						}
						else {
							if(Setting.getHeadNounWeight() == -1.0) {
								multiplierNotHeadNoun = ((t2.getNumberOfTokensWithoutSpecialTokens(normalized)
										-(1.0+1.0/t2.getNumberOfTokensWithoutSpecialTokens(normalized)))/(t2.getNumberOfTokensWithoutSpecialTokens(normalized)-1));
							}
							else {
								multiplierNotHeadNoun = (1-Setting.getHeadNounWeight())*t2.getNumberOfTokensWithoutSpecialTokens(normalized)/
								(t2.getNumberOfTokensWithoutSpecialTokens(normalized)-1);								
							}
							
							multiplier=multiplierNotHeadNoun;
						}
					}
					
				}
			}			
			//the compare value should not be greater than 1
			for(int k=0; k<similarityTerm.length; k++) {
				if(similarityTerm[k] > 1.0) {
					multiplier = 1.0;
				}
			}
			
			//sum the vector entries			
			sum = sum + multiplier*similarityTerm[i];							
		}		
		return sum;
	}	
	
	/**
	 * Creates a vector for a term which contains the maximum similarity value of each part of the term.
	 * 
	 * @param similarityValue Matrix with all values
	 * @param firstParameter Outer loop count
	 * @param secondParameter Interior loop count
	 * @param row True if creating the vector for the rows
	 * @param t1 first term
	 * @param t2 second term
	 * @param normalized true if the similarity value of the normalized terms is required.
	 * @return Vector with the highest value of each row/column	. 
	 */
	private double [] createVector(double similarityValue[][],int firstParameter, int secondParameter, boolean row,Term t1, Term t2, boolean normalized) {
		HashMap<Integer, Integer> similarTokens =  similarTokens(t1, t2, normalized);
		double [] vector = new double[Math.max(secondParameter, firstParameter)];
		double [] similarityTerm = new double[Math.max(firstParameter,secondParameter)];		
		for(int i=0; i<firstParameter; i++) {
			for(int j=0; j<secondParameter; j++) {
				if(row == false) {
					//in the same column or row is not a 1.0 value
					if(!(similarTokens.containsKey(i)) && !(similarTokens.containsValue(j))) {
						vector[j]=similarityValue[i][j];						
					}
					//in the same row is a 1.0 value but it is not this entry
					if(!(similarTokens.containsKey(i)) && similarTokens.containsValue(j)) {
						vector[j]=0.0;						
					}
					//the entry has the value 1.0 
					if(similarTokens.containsKey(i) && similarTokens.get(i) == j) {						
						vector[j]=similarityValue[i][j];						
					} 
					//in the same columne is a 1.0 value but it is not this entry
					if(similarTokens.containsKey(i) && similarTokens.get(i) != j) {						
						vector[j]=0.0;
					} 
				}				
				else {
					if(!(similarTokens.containsKey(j)) && !(similarTokens.containsValue(i))) {
						vector[j]=similarityValue[j][i];
					}
					if((!(similarTokens.containsKey(j))) && (similarTokens.containsValue(i))) {
						vector[j]=0.0;
					}	
					if(similarTokens.containsKey(j) && similarTokens.get(j) == i) {						
						vector[j]=similarityValue[j][i];
					} 
					if(similarTokens.containsKey(j) && similarTokens.get(j) != i) {						
						vector[j]=0.0;
					} 
				}
			}
			similarityTerm[i]=maxValue(vector);
		}
		return similarityTerm;
	}
	
	/**
	 * Computes the maximum similarity value of a row oder a column
	 * 
	 * @param similarities
	 * @return The maximum of all entries in the vector.
	 */
	private double maxValue(double similarities []) {		
	    double currentMaximum= 0.0;	    
		for(int i=0; i<similarities.length-1; i++){			
			if(currentMaximum< Math.max(similarities[i], similarities[i+1])) {				
				currentMaximum = Math.max(similarities[i], similarities[i+1]);
			}	
		}		return currentMaximum;
	}
	
	/**
	 * Computes the multiplier for the distance. If the distance is large the multiplier is small (<1).
	 * 
	 * @param t1 The first Term
	 * @param t2 The second Term
	 * @param smallIndex Index of the first word which should be matched
	 * @param bigIndex Index of the second word which should be matched
	 * @param shorterTerm True if t1 is the smaller Term
	 * @param normalized True if the similarity value between the normalized terms is required.
	 * @return Multiplier based on the distance
	 */
	private double distance(Term t1, Term t2, int smallIndex, int bigIndex, boolean shorterTerm, boolean normalized){
		double shorterLength, firstPart, secondPart; 
		if(shorterTerm == true){
			shorterLength = t1.getNumberOfTokensWithoutSpecialTokens(normalized);				
		}
		else{
			shorterLength = t2.getNumberOfTokensWithoutSpecialTokens(normalized);				
		}
		//define the radiuses
		firstPart = shorterLength/3.0;
		secondPart = 2.0*firstPart;
		
		
		if(((smallIndex-firstPart)<=bigIndex && bigIndex<=(smallIndex+firstPart)) || firstPart<1){
			return 1.0;
		}
		if((smallIndex-secondPart)<=bigIndex && bigIndex<=(smallIndex+secondPart)){
			return 2.0/3.0;
		}		
		return 1.0/3.0;
	}
	
    /**
     * 
     * @param t1 The first term
     * @param t2 The second term
     * @param smallIndex Loop index of the smaller term
     * @param bigIndex Loop index of the larger term
     * @param shorterTerm 
     * @param normalized True if the similarity value between the normalized terms is required.
     * @return Similarity value of the terms t1 and t2
     * @throws MMatchException
     */ 	
	private double compareValue(Term t1, Term t2, int smallIndex, int bigIndex, boolean shorterTerm, boolean normalized) throws MMatchException{		
		AbstractStringMetric STRING_METRIC;
		if(Setting.getStringMetric().equals("Levenshtein")) {
			STRING_METRIC = new Levenshtein();
		}
		else {
			throw new MMatchException(MMatchException.NOT_YET_IMPLEMENTED, "Only Levenshtein metric implemented yet.");
		}
		//compute the multiplier caused by the distance
		double multiplier = distance(t1, t2, smallIndex, bigIndex, shorterTerm, normalized);	
		double multiplierHead = 1.0;		
		//both terms only consits of one word
		if(t1.getNumberOfTokensWithoutSpecialTokens(normalized)==1 && t2.getNumberOfTokensWithoutSpecialTokens(normalized)==1){
			return (STRING_METRIC.getSimilarity(t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[bigIndex]
			          , t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[smallIndex]));
		}
		//both terms consits of more than one word and t1 is the smaller one
		if(shorterTerm == true && t1.getNumberOfTokensWithoutSpecialTokens(normalized)!=1){	
			multiplierHead = multiplierHead(t1, t2, smallIndex, bigIndex, normalized);
			return multiplierHead*multiplier*(STRING_METRIC.getSimilarity(t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[smallIndex]
			                                 , t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[bigIndex]));
		}
		//both terms consits of more than one word and t2 is the smaller one
		if(shorterTerm == false && t2.getNumberOfTokensWithoutSpecialTokens(normalized)!=1){
			multiplierHead = multiplierHead(t2,t1,smallIndex, bigIndex, normalized);
			return multiplierHead*multiplier*(STRING_METRIC.getSimilarity(t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[bigIndex]
			                                  , t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[smallIndex]));
		}
		//the shorter term only consits of one word
        if(shorterTerm == false && t2.getNumberOfTokensWithoutSpecialTokens(normalized)==1){
        	return (STRING_METRIC.getSimilarity(t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[bigIndex]
        	                                  , t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[smallIndex]));	
        }
        if(shorterTerm == true && t1.getNumberOfTokensWithoutSpecialTokens(normalized)==1){
        	return (STRING_METRIC.getSimilarity(t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[smallIndex]
        	                                  , t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[bigIndex]));	
        }
		return 0.0;
	}
	
	/**
	 * Computes the multiplier for the headnoun. If the two headnouns are matched the similarity is higher.
	 * 
	 * @param shorterTerm The shorter term
	 * @param largerTerm The larger term
	 * @param smallIndex
	 * @param bigIndex
	 * @return The multiplier for the headnoun
	 * @throws MMatchException
	 */
	private double multiplierHead(Term shorterTerm, Term largerTerm, int smallIndex, int bigIndex, boolean normalized)throws MMatchException{
		double multiplierHead = 0.0;
		if(smallIndex == shorterTerm.getHeadNounIndex() && bigIndex== largerTerm.getHeadNounIndex()){
			//weight not set by the user
			if(Setting.getHeadNounWeight() == -1.0) {
				multiplierHead = (1.0+1.0/shorterTerm.getNumberOfTokensWithoutSpecialTokens(normalized));
			}
			else {
				multiplierHead = Setting.getHeadNounWeight()*shorterTerm.getNumberOfTokensWithoutSpecialTokens(normalized);
			}
							
		}
		else{
			if(Setting.getHeadNounWeight() == -1.0) {
				multiplierHead = ((shorterTerm.getNumberOfTokensWithoutSpecialTokens(normalized)
						-(1.0+1.0/shorterTerm.getNumberOfTokensWithoutSpecialTokens(normalized)))/(shorterTerm.getNumberOfTokensWithoutSpecialTokens(normalized)-1));
			}
			else {
				multiplierHead = (1-Setting.getHeadNounWeight())*shorterTerm.getNumberOfTokensWithoutSpecialTokens(normalized)/
				(shorterTerm.getNumberOfTokensWithoutSpecialTokens(normalized)-1);				
			}
			
		}
		return multiplierHead;
	}
	
	/**
	 * Computes a hash map with an entry in this map if two tokens are identical. 
	 * The index of the smaller term is the key and the index of the larger term is the value.
	 * If two tokens are identical, the other tokens in the same row/column shoud not be matched on this tokens.
	 * 
	 * @param t1
	 * @param t2
	 * @param normalized
	 * @return
	 */
	private HashMap<Integer,Integer> similarTokens(Term t1, Term t2, boolean normalized) {			
		HashMap<Integer,Integer> notAvailable = new HashMap<Integer, Integer>();		
		int smaller=0, larger=0;
		if(t1.getNumberOfTokensWithoutSpecialTokens(normalized)<= t2.getNumberOfTokensWithoutSpecialTokens(normalized)) {
			smaller = t1.getNumberOfTokensWithoutSpecialTokens(normalized);
			larger = t2.getNumberOfTokensWithoutSpecialTokens(normalized);			
		} 
		else {
			smaller = t2.getNumberOfTokensWithoutSpecialTokens(normalized);
			larger = t1.getNumberOfTokensWithoutSpecialTokens(normalized);			
		}	
		
		if(smaller == t1.getNumberOfTokensWithoutSpecialTokens(normalized)) {
			for(int i=0; i<smaller; i++) {
				for(int j=0; j<larger; j++) {
					if(t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[i].equals(
							t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[j])) {							
						notAvailable.put(i,j);							
					}					
				}
			}
		}
		if(smaller == t2.getNumberOfTokensWithoutSpecialTokens(normalized)) {
			for(int i=0; i<smaller; i++) {
				for(int j=0; j<larger; j++) {
					if(t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[j].equals(
							t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized)[i])) {							
						notAvailable.put(i,j);											
					}					
				}
			}
		}				
		return notAvailable;
	}
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param tokensT1
	 * @param tokensT2
	 * @param normalized
	 * @return The number of identical tokens in the terms.
	 */
	private int identicalTokens(Term t1, Term t2, boolean normalized) {
		String[] firstTerm = t1.getTokensWithoutSpecialTokensNormalizedOrNot(normalized);
		String[] secondTerm = t2.getTokensWithoutSpecialTokensNormalizedOrNot(normalized);
		int smaller=0, larger =0, identicalTokens=0;
		if(firstTerm.length<= secondTerm.length) {
			smaller = firstTerm.length;
			larger = secondTerm.length;			
		} 
		else {
			smaller = secondTerm.length;
			larger = firstTerm.length;			
		}		
		if(smaller == firstTerm.length) {
			for(int i=0; i<smaller; i++) {
				for(int j=0; j<larger; j++) {
					if(firstTerm[i].equals(secondTerm[j])) {	
						identicalTokens++;												
					}					
				}
			}
		}
		else {
			for(int i=0; i<smaller; i++) {
				for(int j=0; j<larger; j++) {
					if(firstTerm[j].equals(secondTerm[i])) {		
						identicalTokens++;																	
					}					
				}
			}
		}
		if(identicalTokens > smaller) {
			identicalTokens = smaller;
		}
		return identicalTokens;
	}
}
