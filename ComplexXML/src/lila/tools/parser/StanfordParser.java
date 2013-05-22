
package lila.tools.parser;

import java.util.*;

import lila.util.Settings;
import lila.util.Parameter;

import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;


public class StanfordParser {
	
	private static StanfordParser m_instance;
	
	private LexicalizedParser m_parser;
	
	
	public static void main( String args[] ) throws Exception {
		StanfordParser test = StanfordParser.instance();
		test.printAnalysis( args[0] );
	}
	
	public static StanfordParser instance() throws Exception {
		return instance( null );
	}
	
	public static StanfordParser instance( String sConfig ) throws Exception {
		if( m_instance == null ){
			m_instance = new StanfordParser( sConfig );
		}
		return m_instance;
	}
	
	private StanfordParser( String sConfig ) throws Exception {
		if( sConfig == null ){
			Settings.load();
		}
		else {
			Settings.load( sConfig );
		}
		init();
	}
	
	public void init(){
		String sModelDir = Settings.getString( Parameter.LILA_HOME )+"res/stanford/";
		System.out.println( "\nStanfordParser.init: "+ sModelDir );
		m_parser = new LexicalizedParser( sModelDir +"englishPCFG.ser.gz" );
		m_parser.setOptionFlags( new String[]{ "-maxLength", "80", "-retainTmpSubcategories" } );
	}
	
	public List<String> tokenize( String sentence ){
		StringTokenizer st = new StringTokenizer( sentence, ".,;!? ", true );
		List<String> tokens = new ArrayList<String>();
		while( st.hasMoreTokens() )
		{
			String sToken = st.nextToken();
			if( sToken.trim().length() > 0 ){
				tokens.add( sToken );
			}
		}
		return tokens;
	}
	
	public SyntaxTree parse( String sPhrase ){
		List<String> tokens = tokenize( sPhrase );
		Tree parse = (Tree) m_parser.apply( tokens );
		// parse.percolateHeads( new CollinsHeadFinder() );
		if( parse != null ){
			return new SyntaxTree( parse );
		}
		return null;
	}

	public void printAnalysis( String sPhrase ){
		SyntaxTree parse = parse( sPhrase );
		System.out.println(parse.toString());
	}
}
