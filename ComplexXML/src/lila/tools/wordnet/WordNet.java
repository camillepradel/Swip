
package lila.tools.wordnet;

import lila.util.Settings;
import lila.util.Parameter;

import java.io.*;
import java.util.*;

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.Dictionary;
 

public class WordNet {
	
	private static WordNet m_instance;
	
	private static WSD m_wsd;
	

	/* arguments: label, category ("noun", "verb", "adjective", "adverb" ), [sense] */
	public static void main( String args[] ) throws Exception {
		WordNet test = WordNet.instance();
		test.printAnalysis( args[0], args[1] );
	}
	
	public static WordNet instance() throws Exception {
		return instance( null );
	}
	
	public static WordNet instance( String sConfig ) throws Exception {
		if( m_instance == null ){
			m_instance = new WordNet( sConfig );
		}
		return m_instance;
	}
	
	private WordNet( String sConfig ) throws Exception {
		if( sConfig == null ){
			Settings.load();
		}
		else {
			Settings.load( sConfig );
		}
		init();
	}
	
	private void init() throws Exception {
		String sFile = Settings.getString( Parameter.LILA_HOME )+"res/file_properties.xml";
		System.out.println( "WordNet.init: "+ sFile );
		try {
			JWNL.initialize( new FileInputStream( sFile ) );
		}
		catch( Exception ex ){
			ex.printStackTrace();
			System.exit( -1 );
		}
		System.out.println( "loading WSD ..." );
		m_wsd = new WSD( this );
	}
	
	public void setContext( Set<String> context ) throws Exception {
		m_wsd.setContext( context );
	}
	
	public Sense[] getSenses( String sWord ) throws Exception {
		List<Sense> all = new ArrayList<Sense>();
		for( Sense s: getSenses( sWord, Category.NOUN ) ){
			all.add(s);
		}
		for( Sense s: getSenses( sWord, Category.VERB ) ){
			all.add(s);
		}
		for( Sense s: getSenses( sWord, Category.ADJECTIVE ) ){
			all.add(s);
		}
		for( Sense s: getSenses( sWord, Category.ADVERB ) ){
			all.add(s);
		}
		Sense senses[] = new Sense[all.size()];
		for( int i=0; i<all.size(); i++ ){
			senses[i] = all.get(i);
		}
		return senses;
	}
	
	public Sense[] getSenses( String sWord, String sCategory ) throws JWNLException {
		if( sCategory.equals( Category.UNKNOWN ) ){
			return new Sense[0];
		}
		Synset synsets[] = getSynsets( sWord, POS.getPOSForLabel( sCategory ) );
		Sense senses[] = new Sense[synsets.length];
		for( int i=0; i<synsets.length; i++ ){
			senses[i] = Sense.create( i, synsets[i] );
		}
		return senses;
	}
	
	public Sense getSense( String sWord ) throws Exception {
		return m_wsd.getSense( sWord );
	}
	
	public Sense getSense( String sWord, String sCategory ) throws Exception {
		return m_wsd.getSense( sWord, sCategory );
	}
	
	public Sense getSense( String sWord, String sCategory, int iSense ) throws JWNLException {
		Sense senses[] = getSenses( sWord, sCategory );
		if( senses.length > iSense ){
			return senses[iSense];
		}
		return null;
	}
	
	/************************************************** basics *****************************************************************/
	
	private Synset[] getSynsets( String sWord, POS pos ) throws JWNLException {
		IndexWord indexword = Dictionary.getInstance().getIndexWord( pos, sWord );
		if( indexword != null )
		{
			Synset[] synsets = indexword.getSenses();
			return synsets;
		}
		return new Synset[0];
	}
		 
	/******************************************************* print ***************************************************************/
	
	public void printAnalysis( String sWord, String sCategory ) throws JWNLException {
		Sense senses[] = getSenses( sWord, sCategory );
		for( Sense sense: senses )
		{
			System.out.println( PrintUtils.line() );
			printAnalysis( sense );
		}
	}
	
	public void printAnalysis( Sense sense ) throws JWNLException {
		System.out.println( "\n"+ sense.toString() );
	}
}
