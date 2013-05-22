
package lila.tools;

import java.util.*;

import lila.util.Settings;
import lila.util.Parameter;

import opennlp.tools.lang.english.*; 
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.parser.ParserME;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.*;
import opennlp.tools.coref.*;
import opennlp.tools.coref.mention.*;


public class OpenNLP {
	
	private String MODEL_DIR = "/Volumes/Dev/lski-intern/software/labels/res/opennlp/";
	
	private Tokenizer m_tokenizer;
	
	private PosTagger m_tagger;
	
	private ParserME m_parser;
	
	private HeadRules m_headRules;
	
	private static OpenNLP m_instance;
	

	public static void main( String args[] ) throws Exception {
		OpenNLP test = OpenNLP.instance();
		test.printAnalysis( args[0].replaceAll( "_", " " ) );
	}
	
	public static OpenNLP instance() throws Exception {
		return instance( null );
	}
	
	public static OpenNLP instance( String sConfig ) throws Exception {
		if( m_instance == null ){
			m_instance = new OpenNLP( sConfig );
		}
		return m_instance;
	}
	
	private OpenNLP( String sConfig ) throws Exception {
		if( sConfig == null ){
			Settings.load();
		}
		else {
			Settings.load( sConfig );
		}
		init();
	}
	
	private void init() throws Exception {
		String sModelDir = Settings.getString( Parameter.LILA_HOME )+"res/opennlp/";
		System.out.println( "\nOpenNLP.init: "+ sModelDir );
		System.out.println( "loading tokenizer ..." );
		m_tokenizer = new Tokenizer( sModelDir +"EnglishTok.bin.gz" );
		System.out.println( "loading part-of-speech tagger ..." );
		m_tagger = new PosTagger( sModelDir +"tag.bin.gz", new POSDictionary( sModelDir +"tagdict" ) );
		System.out.println( "loading parser ..." );
		m_parser = TreebankParser.getParser( sModelDir );
		System.out.println( "loading head rules ..." );
		m_headRules = new HeadRules( sModelDir +"head_rules" );
	}
	
	public String[] getTokens( String sPhrase ){
		return m_tokenizer.tokenize( sPhrase );
	}
	
	public String[] getPartOfSpeech( String sPhrase ){
		return getPartOfSpeech( getTokens( sPhrase ) );
	}
	
	public String[] getPartOfSpeech( String[] sTokens ){
		return m_tagger.tag( sTokens );
	}
	
	public String getHead( String sPhrase ){
		Parse parse = getParse( sPhrase );
		return getHead( parse ).toString();
	}
	
	private Parse getHead( Parse parse ){
		parse.updateHeads( m_headRules );
		return parse.getHead();
	}
	
	// TODO: caching of parse trees?
	
	public String getSyntaxTree( String sPhrase ){
		StringBuffer sb = new StringBuffer();
		Parse parse = getParse( sPhrase );
		parse.show(sb);
		return sb.toString();
	}
	
	public String[] getNounPhrases( String sPhrase ){
		DefaultParse parse = new DefaultParse( getParse( sPhrase ), 0 );
		java.util.List nps = parse.getNounPhrases();
		String phrases[] = new String[nps.size()];
		for( int i=0; i<nps.size(); i++ ){
			phrases[i] = ((DefaultParse)nps.get(i)).toString();
		}
		return phrases;
	}
	
	public String getType( String sPhrase ){
		Parse parse = getParse( sPhrase );
		return parse.getType();
	}
	
	private Parse getParse( String sPhrase ){
		String[] sTokens = getTokens( sPhrase );
		Parse p = new Parse( sPhrase, new Span( 0, sPhrase.length() ), /* "INC" */ "NML", 1, null );
		int start = 0;
		for( String sToken: sTokens )
		{
			p.insert( new Parse( sPhrase, new Span( start, start + sToken.length() ), ParserME.TOK_NODE, 0 ) );
			start += sToken.length() + 1;
		}
		// compute only one parse
		Parse[] parses = m_parser.parse(p,1);
		Parse parse = parses[0];
		return parse;
	}
	
	public void printAnalysis( String sPhrase ){
		System.out.println( "\nOpenNLP.printInfo: "+ sPhrase );
		System.out.println( "Tokens: "+ toString( getTokens( sPhrase ) ) );
		System.out.println( "PartOfSpeech: "+ toString( getPartOfSpeech( sPhrase ) ) );
		System.out.println( "Head: "+ getHead( sPhrase ) );
	}
	
	private String toString( String[] strings ){
		StringBuffer sb = new StringBuffer();
		sb.append( "[ " );
		for( int i=0; i<strings.length; i++ ){
			sb.append( strings[i] );
			if( i < strings.length-1 ){
				sb.append( "," );
			}
			sb.append( " " );
		}
		sb.append( "]" );
		return sb.toString();
	}
}