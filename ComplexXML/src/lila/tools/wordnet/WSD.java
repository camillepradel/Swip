
package lila.tools.wordnet;

import lila.util.Settings;
import lila.util.Parameter;
import lila.tools.MorphAdorner;

import java.io.*;
import java.util.*;

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.Dictionary;
 

public class WSD {
	
	private WordNet m_wordnet;
	
	private Set<String> m_context;
	
	
	protected WSD( WordNet wordnet ) throws Exception {
		this( wordnet, new HashSet<String>() );
	}
	
	protected WSD( WordNet wordnet, Set<String> context ) throws Exception {
		m_wordnet = wordnet;
		if( context.size() > 0 ){
			setContext( context );
		}
	}
	
	protected void setContext( Set<String> context ) throws Exception {
		m_context = normalize( context );
	//	System.out.println( "WSD.setContext: "+ toString( m_context ) );
	}
	
	/* example: accepted_papers -> accept, paper */
	private Set<String> normalize( Set<String> words ) throws Exception {
		Set<String> normalized = new HashSet<String>();
		for( String word: words )
		{
			String[] tokens = word.split( "_" );
			for( String token : tokens )
			{
				if( token.length() > 0 ){
					String sLemma = MorphAdorner.instance().getLemma( token );
					normalized.add( sLemma );
				}
			}
		}
		return normalized;
	}
	
	protected Sense getSense( String sWord ) throws Exception {
		return getSense( sWord, Category.UNKNOWN );
	}
	
	protected Sense getSense( String sWord, String sCategory ) throws Exception {
		Sense senses[] = null;
		if( sCategory.equals( Category.UNKNOWN ) ){
			senses = m_wordnet.getSenses( sWord );
		}
		else {
			senses = m_wordnet.getSenses( sWord, sCategory );
		}
		if( senses.length == 0 ){
			return null;
		}
		Sense maxSense = senses[0];
		double dMaxScore = 0.0;
		for( int i=0; i<senses.length; i++ )
		{
			Sense sense = senses[i];
			double dScore = getScore( sense );
			if( dScore > dMaxScore )
			{
				maxSense = sense;
				dMaxScore = dScore; 
			}
		}
		return maxSense;
	}
	
	private double getScore( Sense sense ) throws Exception {
		if( m_context.size() == 0 ){
			return 0.0;
		}
		Set<String> signature = getSignature( sense );
		int iCount = 0;
		for( String sign: signature )
		{
			if( contains( m_context, sign ) ){
				iCount++;
			}
		}
		double dScore = (double)iCount / (double)signature.size();
		// System.out.print( "\nWSD.getScore: sense="+ sense.getID() +" words="+ PrintUtils.toString( sense.getWords() ) );
		// System.out.print( " score="+ dScore +" signature="+ PrintUtils.toString( signature ) +"\n" );
		return dScore;
	}
	
	private Set<String> getSignature( Sense sense ) throws Exception {
		Set<String> signature = new HashSet<String>();
		signature.addAll( getGlossTerms( sense ) );
		Sense related[] = sense.getRelatedSenses();
		for( Sense other: related ){
			signature.addAll( getGlossTerms( other ) );
		}
		return normalize( signature );
	}
	
	private Set<String> getGlossTerms( Sense sense ){
		Set<String> terms = new HashSet<String>();
		String sGloss = sense.getGloss();
		StringTokenizer st = new  StringTokenizer( sGloss, " ,.?!'\"-;)(", false );
		while( st.hasMoreTokens() )
		{
			String sToken = st.nextToken();
			terms.add( sToken );
		}
		return terms;
	}
	
	private boolean contains( Set<String> set, String s ){
		for( String other: set )
		{
			if( other.equalsIgnoreCase(s) ){
				return true;
			}
		}
		return false;
	}
	
	private String toString( Set<String> set ){
		StringBuffer sb = new StringBuffer();
		Iterator iter = set.iterator();
		while( iter.hasNext() )
		{
			sb.append( iter.next() );
			if( iter.hasNext() ){
				sb.append( ", " );
			}
		}
		return sb.toString();
	}
}
