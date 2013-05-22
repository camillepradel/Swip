
package lila.syntax;

import java.util.*;

import lila.tools.OpenNLP;
import lila.tools.wordnet.Category;
import lila.tools.parser.*;


public class Phrase {
	
	protected String m_text;
	
	protected Word m_head;
	
	protected Word[] m_modifiers;
	
	protected Word[] m_words;
	
	protected String m_type;
	
	protected SyntaxTree m_parse;
	
	
	public Phrase( String text ){
		m_text = text;
	}
	
	/* tokenization and part-of-speech tagging */
	public Word[] getWords() throws Exception {
		if( m_words == null )
		{
			String tokens[] = OpenNLP.instance().getTokens( m_text );
			String pos[] = OpenNLP.instance().getPartOfSpeech( tokens );
			m_words = new Word[tokens.length];
			for( int i=0; i<tokens.length; i++ ){
				m_words[i] = Word.create( tokens[i], pos[i] );
			}
		}
		return m_words;
	}
	
	public Phrase[] getNounPhrases() throws Exception {
		String nps[] = OpenNLP.instance().getNounPhrases( m_text );
		Phrase phrases[] = new Phrase[nps.length];
		for( int i=0; i<nps.length; i++ ){
			phrases[i] = new Phrase( nps[i] );
		}
		return phrases;
	}
	
	public Word[] getModifiers( Word word ) throws Exception {
		SyntaxTree tree = getSyntaxTree();
		String mods[] = tree.getModifiers( word.toString() );
		List<String> other = new ArrayList<String>();
		if( word.isNoun() )
		{
			for( String sVerb: tree.getVerbs( word.toString() ) )
			{
				Verb verb = new Verb( sVerb );
				String sPOS = verb.getPartOfSpeech();
				if( sPOS.equals( "VBN" ) || sPOS.equals( "VBG" ) ){
					other.add( sVerb );
				}
			}
		}
		Word words[] = new Word[mods.length + other.size()];
		for( int i=0; i<mods.length; i++ ){
			words[i] = getWord( mods[i] );
		}
		for( int i=0; i<other.size(); i++ ){
			words[i] = getWord( other.get(i) );
		}
		return words;
	}
	
	public Word[] getObjects( Verb verb ) throws Exception {
		SyntaxTree tree = getSyntaxTree();
		String objs[] = tree.getObjects( verb.toString() );
		Word words[] = new Word[objs.length];
		for( int i=0; i<objs.length; i++ ){
			words[i] = getWord( objs[i] );
		}
		return words;
	}
	
	public Word[] getSubjects( Verb verb ) throws Exception {
		SyntaxTree tree = getSyntaxTree();
		String subjs[] = tree.getSubjects( verb.toString() );
		Word words[] = new Word[subjs.length];
		for( int i=0; i<subjs.length; i++ ){
			words[i] = getWord( subjs[i] );
		}
		return words;
	}
	
	public SyntaxTree getSyntaxTree() throws Exception {
		if( m_parse == null ){
			m_parse = StanfordParser.instance().parse( m_text );
		}
		return m_parse;
	}
	
	/* example: [ student, papers ] -> [ NN, NNS ] */
	public String[] getPartsOfSpeech() throws Exception {
		return OpenNLP.instance().getPartOfSpeech( m_text );
	}
	
	/* example: student papers -> papers */
	public Word getHead() throws Exception {
		if( m_head == null )
		{
			String sHead = OpenNLP.instance().getHead( m_text );
			if( sHead != null ){ 
				m_head = getWord( sHead );
			}
		}
		return m_head;
	}
	
	private Word getWord( String sWord ) throws Exception {
		/* TODO (bad hack): 
		   how to get the index of a word in the parse tree? */
		for( Word word: getWords() )
		{
			if( word.toString().equals( sWord ) ){
				return word;
			}
		}
		return new Word( sWord );
	}
	
	public String getType() throws Exception {
		return getHead().getCategory() +"_phrase";
	}
	
	public boolean isNounPhrase() throws Exception {
		return getHead().getCategory().equals( Category.NOUN );
	}
	
	public boolean isVerbPhrase() throws Exception {
		return getHead().getCategory().equals( Category.VERB );
	}
	
	public String toString(){
		return m_text;
	}
}