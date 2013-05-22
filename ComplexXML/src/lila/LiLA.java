
package lila;

import java.util.*;

import lila.tools.wordnet.*;
import lila.tools.MorphAdorner;
import lila.tools.OpenNLP;
import lila.tools.parser.*;
import lila.syntax.*;
import lila.util.*;


public class LiLA {

	public LiLA() throws Exception {
		this( Settings.DEFAULT );
	}
	
	public LiLA( String sConfig ) throws Exception {
		if( sConfig == null ){
			Settings.load();
		}
		else {
			Settings.load( sConfig );
		}
		if( !Settings.getBoolean( Parameter.LAZY_INIT ) )
		{
			OpenNLP.instance();
			WordNet.instance();
			MorphAdorner.instance();
			// LingPipe.instance();
			StanfordParser.instance();
		}
	}
	
	public void setContext( Set<String> context ) throws Exception {
		WordNet.instance().setContext( context );
	}
	
	/* 
	 * TODO: camel-case and acronyms
	 * example: Student_Paper -> student paper 
	 */
	public String normalize( String sLabel ){
		return sLabel.replaceAll( "_", " " ).toLowerCase();
	}
	
	public String labelize( Phrase phrase ) throws Exception {
		Word words[] = phrase.getWords();
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<words.length; i++ )
		{
			sb.append( words[i].toString() );
			if( i<words.length-1 ){
				sb.append( "_" );
			}
		}
		return sb.toString();
	}
}