package de.unima.ki.mmatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;


public class Setting {		
	
   // ***************************** constants *****************************
	
	/**
	 * The number to identify which matrix should be created.
	 * 100 stands for the concept matrix. 
	 */
	public static final int CONCEPTS = 100;
	
	/**
	 * The number to identify which matrix should be created. 
	 * 101 stands for the property matrix.
	 */
	public static final int PROPERTIES = 101;

    
	// ***************************** join constants, variables and methods *****************************
	
	// ***************************** join constants *****************************
	
	/**
	 * Choose the standard deviation of a matrix as the join factor.
	 */
	public static final int STANDARD_DEVIATION = 1;
	
	/**
	 * Choose the variance of a matrix as the join factor.
	 */
	public static final int VARIANCE = 2;
	
	/**
	 * Choose the entropy of a matrix as the join factor.
	 */
	public static final int ENTROPY = 3;
	
	/**
	 * Choose the maximum of the values to create a new matrix.
	 */
	public static final int MAXIMUM = 4;
	
	/**
	 * Choose fixed constants as the join factor.
	 */
	public static final int CONSTANTS_AS_WEIGHT = 5;
	
	/**
	 * Defines into how many parts the interval 0 to 1 should be seperated, only needed if 
	 * the entropy of a matrix should be computed.
	 */
	public static final int NUMBER_OF_INTERVALS = 1000; 
	
	
	// ***************************** join variables and methods *****************************
	
	/**
	 * The syntactic equivalence join factor if the factors should be constants.
	 */
	private static double syntacticEquivalenceJoinFactor = 8.0;
	
	/**
	 * The wordnet equivalence join factor if the factors should be constants.
	 */
	private static double wordnetEquivalenceJoinFactor = 7.0;
    
	/**
	 * 
	 * @return
	 */
	public static double getSyntacticEquivalenceJoinFactor() {
		return syntacticEquivalenceJoinFactor;
	}
    
	/**
	 * 
	 * @return 
	 */
	public static double getWordnetEquivalenceJoinFactor() {
		return wordnetEquivalenceJoinFactor;
	}
	
	/**
	 * The syntactic join factor defines how the syntactic equivalence and the wordnet equivalence matrix should be joined.
	 * If this variable is not set by the user, the maximum is used.
	 */
	private static int syntacticJoinFactor = Setting.MAXIMUM;
	
	/**
	 * Set the syntactic join factor.
	 * 
	 * @param type The type of the factor.
	 * @param values The values if the factors should be constants.
	 * @throws MMatchException
	 */
	public static void setSyntacticJoinFactor(int type, double... values) throws MMatchException {
		//throw an exception if values are given and the type is not constants as weight
		if(type != Setting.CONSTANTS_AS_WEIGHT && values.length >0) {
			throw new MMatchException(MMatchException.BAD_PARAMETER, "Cannot set constants as syntactic join factors if constants as weight not selected.");
		}
		//get the type
		syntacticJoinFactor = getType(type);
		//check the number of parameters
		if(type == CONSTANTS_AS_WEIGHT) {
			if(Setting.wordnetActivated == false && values.length > 1) {
				throw new MMatchException(MMatchException.BAD_PARAMETER, "Too many constants for the syntactic join factor (wordnet not activated).");
			}
			if(values.length > 2) {
				throw new MMatchException(MMatchException.BAD_PARAMETER, "Too many constants for the syntactic join factor.");
			}
			//set the values if everything is right
			if(values.length > 0) {
				syntacticEquivalenceJoinFactor = values[0];
			}
			if(values.length > 1) {
				wordnetEquivalenceJoinFactor = values[1];
			}			
		}	
	}
	
	/**
	 * 
	 * @return The syntactic join factor.
	 */
	public static int getSyntacticJoinFactor() {
		return syntacticJoinFactor;
	}
	
	/**
	 * The concept syntactic-wordnet join factor if the concept matrix is created (constants as weight).
	 */
	private static double conceptSyntacticWordnetJoinFactor = 8.0;
	
	/**
	 *  The concept relation join factor if the concept matrix is created (constants as weight).
	 */
	private static double conceptRelationJoinFactor = 5.0;
	
	/**
	 * The concept join factor defines the type of the matrix join.
	 */
	private static int conceptJoinFactor = Setting.CONSTANTS_AS_WEIGHT;
	
	/**
	 * 
	 * @param type
	 * @param values
	 * @throws MMatchException
	 */
	public static void setConceptJoinFactor(int type, double... values) throws MMatchException {
		if(type != Setting.CONSTANTS_AS_WEIGHT && values.length >0) {
			throw new MMatchException(MMatchException.BAD_PARAMETER, "Cannot set constants as syntactic join factors if constants as weight not selected.");
		}
		Setting.conceptJoinFactor = getType(type);
		if(type == CONSTANTS_AS_WEIGHT) {
			if(values.length > 2) {
				throw new MMatchException(MMatchException.BAD_PARAMETER, "Too many constants for the concept join factor.");
			}
			if(values.length > 0) {
				conceptSyntacticWordnetJoinFactor = values[0];
			}
			if(values.length > 1) {
				conceptRelationJoinFactor = values[1];
			}			
		}				
	}
	
	/**
	 * 
	 * @return
	 */
	public static double getConceptSyntacticWordnetJoinFactor() {
		return conceptSyntacticWordnetJoinFactor;
	}
    
	/**
	 * 
	 * @return
	 */
	public static double getConceptRelationJoinFactor() {
		return conceptRelationJoinFactor;
	}
    
	/**
	 * 
	 * @return
	 */
	public static int getConceptJoinFactor() {
		return Setting.conceptJoinFactor;
	}

	/**
	 * The property syntactic-wordnet join factor if the property matrix is created (constants as weight).
	 */
	private static double propertySyntacticWordnetJoinFactor = 8.0;
	
	/**
	 * The property relation join factor if the property matrix is created (constants as weight).
	 */
	private static double propertyRelationJoinFactor = 4.0;
	
	/**
	 * The property logic join factor if the property matrix is created (constants as weight).
	 */
	private static double propertyLogicJoinFactor = 3.0;
	
	/**
	 * The property factor used to create the property matrix out of three matrices.
	 */
	private static int propertyJoinFactor = Setting.CONSTANTS_AS_WEIGHT;
	
	/**
	 * 
	 * @param type
	 * @param values
	 * @throws MMatchException
	 */
	public static void setPropertyJoinFactor(int type, double... values) throws MMatchException {
		if(type != Setting.CONSTANTS_AS_WEIGHT && values.length >0) {
			throw new MMatchException(MMatchException.BAD_PARAMETER, "Cannot set constants as syntactic join factors if constants as weight not selected.");
		}
		propertyJoinFactor = getType(type);
		if(type == CONSTANTS_AS_WEIGHT) {
			if(values.length < 3) {
				throw new MMatchException(MMatchException.BAD_PARAMETER, "Too many constants for the concept join factor.");
			}
			if(values.length > 0) {
				propertySyntacticWordnetJoinFactor = values[0];
			}
			if(values.length >1) {
				propertyRelationJoinFactor = values[1];	
			}
			if(values.length > 2) {
				propertyLogicJoinFactor = values[2];
			}			
		}		
	}
	
    /**
     * 
     * @return
     */
	public static double getPropertySyntacticWordnetJoinFactor() {
		return propertySyntacticWordnetJoinFactor;
	}
    
	/**
	 * 
	 * @return
	 */
	public static double getPropertyRelationJoinFactor() {
		return propertyRelationJoinFactor;
	}
    
	/**
	 * 
	 * @return
	 */
	public static double getPropertyLogicJoinFactor() {
		return propertyLogicJoinFactor;
	}
    
	/**
	 * 
	 * @return
	 */
	public static int getPropertyJoinFactor() {
		return propertyJoinFactor;
	}
	
	/**
	 * Auxiliary method to compute the factor type.
	 * 
	 * @param type
	 * @return
	 * @throws MMatchException
	 */
	private static int getType(int type) throws MMatchException {
		switch(type) {
		case STANDARD_DEVIATION:
			return Setting.STANDARD_DEVIATION;
		case VARIANCE:
			return Setting.VARIANCE;
		case ENTROPY:
			return Setting.ENTROPY;
		case MAXIMUM:
			return Setting.MAXIMUM;
		case CONSTANTS_AS_WEIGHT:
			return Setting.CONSTANTS_AS_WEIGHT;
		default:
			throw new MMatchException(MMatchException.BAD_PARAMETER, "Wrong syntactic join factor.");
		}
	}
		
	//***************************** general variables and their setter/getter *****************************
	
	
	/**
     * True if the wordnet matrices should be created.
     */
	private static boolean wordnetActivated = false;
	
	/**
	 * 
	 * @param wordnetActivated
	 */
	public static void setWordnetActivated(boolean wordnetActivated) {
		Setting.wordnetActivated = wordnetActivated;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean getWordnetActivated() {
		return wordnetActivated;
	}
	
	
	/**
	 * If a seperator does not occur very often, less than minumum occurence seperator*100 per cent, in an ontology, it is often not 
	 * used as a seperator and should not be accepted as one. 
	 */
	private static double minimumOccurenceSeperator = 0.01;
	
	/**
	 * 
	 * @param minimumOccurenceSeperator
	 */
	public static void setMinimumOccurenceSeperator(double minimumOccurenceSeperator) {
		Setting.minimumOccurenceSeperator = minimumOccurenceSeperator; 
	}
	
	/**
	 * 
	 * @return
	 */
	public static double getMinimumOccurenceSeperator() {
		return minimumOccurenceSeperator;	
	}	
	
		
	/**
	 * The weight of stemming. A STEM_WEIGHT of 0.5 means that the similarity value of
	 * two terms is the similarity value of the stemmed or normalized terms plus the
	 * similarity value of the non-normalized terms. 
	 * If the STEM_WEIGHT is high, the similarity value is greater if
	 * the normalized terms are similar and the non-normalized terms are not as similar.
	 */
	private static double stemWeight = 0.1;
	
	/**
	 * 
	 * @param stemWeight
	 */
	public static void setStemWeight(double stemWeight) {
		Setting.stemWeight = stemWeight;
	}
	
	/**
	 * 
	 * @return
	 */
	public static double getStemWeight() {
		return stemWeight;
	}
	
	
	/**
	 * The metric which should be used to compue the similarities.
	 * All metrics in the simmetrics-package are available.
	 */
	private static String stringMetric = "Levenshtein";
	
	/**
	 * 
	 * @return
	 */
	public static String getStringMetric() {
		return stringMetric;
	}
    
	/**
	 * 
	 * @param stringMetric
	 * @throws MMatchException 
	 */
	public static void setStringMetric(String stringMetric) throws MMatchException {
		if(!stringMetric.equalsIgnoreCase("Levenshtein")) {
			throw new MMatchException(MMatchException.NOT_YET_IMPLEMENTED, "Only Levenshtein metric implemented yet.");
		}
		Setting.stringMetric = stringMetric;
	}
	
	
	/**
	* Threshold finally applied on concept and property matrix before generating the alignment. 
	*/
	private static double generalThreshold = 0.65;
	
	/**
	 * 
	 * @return
	 */
	public static double getGeneralThreshold() {
		return generalThreshold;
	}
	
    /**
     * 
     * @param generalThreshold
     */
	public static void setGeneralThreshold(double generalThreshold) {
		Setting.generalThreshold = generalThreshold;
	}
	
	/**
	* Alignment is restricted to top k matching partners computed in greedy top down approach.
	* Might extend the specified number to force symmetric results. A value
	* of 0 means strict one to one alignments.
	*/
	private static int topK = 0;
	
	/**
	 * 
	 * @return 
	 */
	public static int getTopK() {
		return topK;
	}
    
	/**
	 * 
	 * @param topK
	 */
	public static void setTopK(int topK) {
		Setting.topK = topK;
	}
	
	/**
	 * The entries of the wordnet matrix are "rootalized" number of roots times to get realistic values.
	 * If number of roots = 2 the entries are the sqaure root of the result.
	 */
	private static int numberOfRoot = 2;
	
	/**
	 * 
	 * @return The number of root.
	 */
	public static int getNumberOfRoot() {
		return numberOfRoot;
	}
    
	/**
	 * 
	 * @param numberOfRoot
	 * @throws MMatchException
	 */
	public static void setNumberOfRoot(int numberOfRoot) throws MMatchException {
		if(Setting.wordnetActivated == false) {
			throw new MMatchException(MMatchException.BAD_PARAMETER, "Could not compute a root of a matrix which does not" +
					"exists (computation wordnet matrix disabled)");
		}
		Setting.numberOfRoot = numberOfRoot;
	}
	
	/**
	 *  The weight of the headnoun, used to compute the string similarity.
	 *  If the headNounWeight is -1.0, the weight of the headnoun is computed as follows:
	 *  1+1/length shorter term
	 */
	private static double headNounWeight = -1.0;
    
	/**
	 * 
	 */
	public static double getHeadNounWeight() {
		return headNounWeight;
	}
    
	/**
	 * 
	 * @param headNounWeight
	 * @throws MMatchException 
	 */
	public static void setHeadNounWeight(double headNounWeight) throws MMatchException {
		if(headNounWeight <0.0 || headNounWeight>1.0) {
			throw new MMatchException(MMatchException.BAD_PARAMETER, "Headnoun wight has to be a value between 0 and 1.");
		}
		Setting.headNounWeight = headNounWeight;
	}
	
	/**
	 * Path where the alignment should be build
	 */
	private static String alignmentPath = null;
	
	public static void setAlignmentPath(String path) {
		Setting.alignmentPath = path;
	}
	
	public static String getAlignmentPath() {
		return Setting.alignmentPath;
	}
	
	/**
	 * The variable createExplanations can be set if the explanations should not be created.
	 * If there are some memory problems turn off the creation of the explanations can help to solve them.
	 */
	private static boolean createExplanations = true;
	
	public static boolean getCreateExplanations() {
		return createExplanations;
	}

	public static void setCreateExplanations(boolean createExplanations) {
		Setting.createExplanations = createExplanations;
	}
	
	
		
	//***************************** Hash sets with all the words of the files *****************************
	
	public static HashSet<String> antiWords = new HashSet<String>();
	public static HashSet<String> strangeWords = new HashSet<String>();
	public static HashSet<String> ignoreWords = new HashSet<String>();
	public static HashSet<String> inverseWords = new HashSet<String>();
	
	
	
	
	/**
	 * Load all files which are needed.
	 * @throws MMatchException
	 */
	public static void load() throws MMatchException {		

		File anti = new File("anti.txt");
		File strange = new File("strange.txt");		
		File ignore = new File("ignore.txt");
		File inverse = new File("inverse.txt");
		try {
			FileReader fileAnti = new FileReader(anti);
			FileReader fileStrange = new FileReader(strange);
			FileReader fileIgnore = new FileReader(ignore);
			FileReader fileInverse = new FileReader(inverse);
			BufferedReader readAnti = new BufferedReader(fileAnti);
			BufferedReader readStrange = new BufferedReader(fileStrange);
			BufferedReader readIgnore = new BufferedReader(fileIgnore);
			BufferedReader readInverse = new BufferedReader(fileInverse);
			String readLineAnti = new String();
			String readLineStrange = new String();
			String readLineIgnore = new String();
			String readLineInverse = new String();
			
			while( (readLineAnti = readAnti.readLine()) != null) {		
				antiWords.add(readLineAnti);				
			}
			
			while( (readLineStrange = readStrange.readLine()) != null) {
				strangeWords.add(readLineStrange);	
			}			   				
			
			while( (readLineIgnore = readIgnore.readLine()) != null) {
				ignoreWords.add(readLineIgnore);
			}
			
			while( (readLineInverse = readInverse.readLine()) != null) {
				inverseWords.add(readLineInverse);
			}
			readAnti.close();
			readStrange.close();
			readIgnore.close();
			readInverse.close();
			
		} catch (IOException e) {
			throw new MMatchException(
					MMatchException.IO_ERROR, 
					"Could not access the anti- or strangeword file.",
					e			
			);
		}
		
	}
	
	/**
	 * Load the wordnet dictonary which is used to create the wordnet matrices. 
	 * The place of the dictonary is specified in the file_properties.xml.
	 * 
	 * @throws MMatchException
	 */
	public static void loadWordnet() throws MMatchException {
		String propsFile = "lib/file_properties.xml";
		
		//initialize the database
        try {
            JWNL.initialize(new FileInputStream(propsFile));
        } catch (FileNotFoundException e) {
        	throw new MMatchException(
					MMatchException.IO_ERROR, 
					"Could not find the JWNL property file.",
					e);
        } catch (JWNLException e) {
        	throw new MMatchException(
					MMatchException.JWNL_ERROR, 
					"Could not initialize the database which JWNL required.",
					e); 
        }
	}
	
}
