
package lila.tools;

import java.io.*;
import java.util.*;

import lila.util.Settings;
import lila.util.Parameter;

import edu.northwestern.at.utils.corpuslinguistics.inflector.*;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.EnglishLemmatizer;
import edu.northwestern.at.utils.corpuslinguistics.inflector.pluralizer.EnglishPluralizer;
import edu.northwestern.at.utils.corpuslinguistics.stemmer.PorterStemmer;
import edu.northwestern.at.utils.spellcheck.*;


public class MorphAdorner {
	
	private EnglishInflector m_inflector;
	
	private EnglishLemmatizer m_lemmatizer;
	
	private EnglishPluralizer m_pluralizer;
	
	private PorterStemmer m_stemmer;
	
	private TernaryTrieSpellingDictionary m_dictionary;
	
	private static MorphAdorner m_instance;
	
	
	/* arguments: word, category ( "noun", "verb", "adjective", "adverb" ) */
	public static void main( String args[] ) throws Exception {
		MorphAdorner adorner = MorphAdorner.instance();
		adorner.printAnalysis( args[0], args[1] );
	}
	
	public static MorphAdorner instance() throws Exception {
		return instance( null );
	}
	
	public static MorphAdorner instance( String sConfig ) throws Exception {
		if( m_instance == null ){
			m_instance = new MorphAdorner( sConfig );
		}
		return m_instance;
	}
	
	private MorphAdorner( String sConfig ) throws Exception {
		if( sConfig != null ){
			Settings.load( sConfig );
		}
		else {
			Settings.load();
		}
		init();
	}
	
	private void init() throws Exception {
		String sModelDir = Settings.getString( Parameter.LILA_HOME )+"res/adorner/";
		System.out.println( "\nAdorner.init: "+ sModelDir );
		m_inflector = new EnglishInflector();
		m_lemmatizer = new EnglishLemmatizer();
		m_pluralizer = new EnglishPluralizer();
		m_stemmer = new PorterStemmer();
		m_dictionary = new TernaryTrieSpellingDictionary( read( sModelDir +"dictionary.txt" ) );
	}
	
	public boolean exists( String sWord ){
		return m_dictionary.lookupWord( sWord );
	}
	
	public String[] checkSpelling( String sWord ){
		Set<String> set = m_dictionary.findMostSimilarSet( sWord );
		String words[] = new String[set.size()];
		int i=0;
		for( String word: set ){
			words[i++] = word;
		}
//		System.out.println( "\nMorphAdorner.checkSpelling: "+ toString( words ) );
		return words;
	}
	
	public String getLemma( String sWord ){
		try {
			return m_lemmatizer.lemmatize( sWord );
		} 
		catch ( Exception e ){
			return sWord;
		}
	}
	
	public String getLemma( String sWord, String sCategory ){
		try {
			return m_lemmatizer.lemmatize( sWord, sCategory );
		}
		catch ( Exception e ){
			return sWord;
		}
	}
	
	public String getStem( String sWord ){
		return m_stemmer.stem( sWord );
	}
	
	public String getPastParticiple( String sLemma ){
		return m_inflector.conjugate( sLemma, VerbTense.PAST_PARTICIPLE, Person.THIRD_PERSON_SINGULAR );
	}
	
	public String getPlural( String sLemma ){
		return m_pluralizer.pluralize( sLemma );
	}
	
	public void printAnalysis( String sWord, String sCategory ){
		String sLemma = getLemma( sWord );
		if( sCategory.equalsIgnoreCase( "verb" ) ){
			System.out.println( "active: "+ sLemma );
			System.out.println( "passive: "+ getPastParticiple( sLemma ) );
		}
		else if( sCategory.equalsIgnoreCase( "noun" ) ){
			System.out.println( "singular: "+ sLemma );
			System.out.println( "plural: "+ getPlural( sLemma ) );
		}
	}
	
	private String toString( String strings[] ){
		StringBuffer sb = new StringBuffer();
		sb.append( "[ " );
		for( int i=0; i<strings.length; i++ ){
			sb.append( strings[i] );
			if( i < strings.length-1 ){
				sb.append( ", " );
			}
		}
		sb.append( " ]" );
		return sb.toString();
	}
	
	private static List<String> read( String sFile ) throws Exception {
		List<String> lines = new ArrayList<String>();
		File file = new File( sFile );
		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new FileReader( file ) );
			String sLine = null;
			while( ( sLine = reader.readLine() ) != null ){
				lines.add( sLine.trim() );
			}
		}
		finally {
			if( reader != null ) {
				reader.close();
			}
		}
		return lines;
	}
}