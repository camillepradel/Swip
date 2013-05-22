
package lila.syntax;

import java.util.List;
import java.util.ArrayList;

import lila.tools.MorphAdorner;
import lila.tools.OpenNLP;
import lila.tools.wordnet.Category;
import lila.tools.wordnet.Sense;
import lila.tools.wordnet.WordNet;


public class Word extends Phrase {
	
	protected String m_pos;
	
	protected String m_lemma;
	
	// all possible senses of this word
	protected Sense[] m_senses;
	
	// result of automatic word sense disambiguation
	protected Sense m_sense;
	
	protected String m_category;
	
	
	public Word( String text ){
		super( text );
	}
	
	public Word( String text, String pos ){
		super( text );
		m_pos = pos;
	}
	
	public boolean exists() throws Exception {
		return MorphAdorner.instance().exists( getLemma() );
	}
	
	public void setCategory( String sCategory ){
		m_category = sCategory;
	}
	
	protected static Word create( String text, String pos ){
		String sCategory = Category.guess( pos );
		if( sCategory.equals( Category.NOUN ) ){
			return new Noun( text, pos );
		}
		else if( sCategory.equals( Category.VERB ) ){
			return new Verb( text, pos );
		}
		return new Word( text, pos );
	}
	
	public boolean isNoun() throws Exception {
		return getCategory().equals( Category.NOUN );
	}
	
	public boolean isVerb() throws Exception {
		return getCategory().equals( Category.VERB );
	}
	
	public String getCategory() throws Exception {
		if( m_category == null ){
			m_category = Category.guess( getPartOfSpeech() );
		}
		return m_category;
	}
	
	public Word checkSpelling() throws Exception {
		if( !exists() ){
			String correct[] = MorphAdorner.instance().checkSpelling( m_text );
			for( int i=0; i<correct.length; i++ )
			{
				Word word = new Word( correct[i] );
				// assume part-of-speech to be correct
				if( getCategory().equals( word.getCategory() ) ){
					return word;
				}
			}
		}
		return this;
	}
	
	/* example: papers -> NNS */
	public String getPartOfSpeech() throws Exception {
		if( m_pos == null ){
			return OpenNLP.instance().getPartOfSpeech( m_text )[0];
		}
		return m_pos;
	}
	
	/* example: papers -> paper */
	public String getLemma() throws Exception {
		if( m_lemma == null )
		{
			String sCategory = getCategory();
			if( !sCategory.equals( Category.UNKNOWN ) )
			{
				// if category is known, provide this information to the lemmatizer
				m_lemma = MorphAdorner.instance().getLemma( m_text, sCategory );
			}
			else {
				m_lemma = MorphAdorner.instance().getLemma( m_text );
			}
		}
		if( m_lemma == null ) return m_text;
		return m_lemma;
	}
	
	/* synonyms */
	
	public Word[] getSynonyms() throws Exception {
		// if sense is unknown, get synonyms for all senses
		Sense senses[] = getSenses();
		List<Word> all = new ArrayList<Word>();
		for( int i=0; i<senses.length; i++ )
		{
			Word syns[] = getSynonyms(i);
			for( Word syn: syns )
			{
				if( !all.contains( syn ) ){
					all.add( syn );
				}
			}
		}
		return all.toArray( new Word[all.size()] );
	}
	
	public Word[] getSynonyms( int iSense ) throws Exception {
		String sCategory = getCategory();
		Sense senses[] = getSenses( sCategory );
		if( senses.length > iSense )
		{
			Sense sense = senses[iSense];
			String syns[] = sense.getWords();
			Word words[] = new Word[syns.length];
			for( int i=0; i<syns.length; i++ )
			{
				words[i] = new Word( syns[i] );
				words[i].setCategory( sense.getCategory() );
			}
			return words;
		}
		return new Word[0];
	}
	
	/* senses */
	
	public Sense[] getSenses() throws Exception {
		if( m_senses == null ){
			m_senses = getSenses( getCategory() );
		}
		return m_senses;
	}
	
	public Sense[] getSenses( String sCategory ) throws Exception {
		if( m_senses == null )
		{
			String sLemma = getLemma();
			if( sCategory.equals( Category.UNKNOWN ) )
			{
				// if category is unknown, get senses for all categories
				m_senses = WordNet.instance().getSenses( sLemma );
			}
			else {
				m_senses = WordNet.instance().getSenses( sLemma, sCategory );
			}
		}
		return m_senses;
	}
	
	public Sense getSense() throws Exception {
		if( m_sense == null )
		{
			String sLemma = getLemma();
			String sCategory = getCategory();
			if( sCategory.equals( Category.UNKNOWN ) )
			{
				// if category is unknown, consider senses for all categories
				m_sense = WordNet.instance().getSense( sLemma );
			}
			else {
				m_sense = WordNet.instance().getSense( sLemma, sCategory );
			}

		}
		return m_sense;
	}
	
	/* verbs */
	
	public Word getActive() throws Exception {
		return new Word( getLemma(), m_pos );
	}
	
	/* return past participle, preposition (e.g. "by" might have to be added explicitly) */
	public Word getPassive() throws Exception {
		return new Word( MorphAdorner.instance().getPastParticiple( getLemma() ), m_pos );
	}
	
	/* nouns */
	
	public Word getSingular() throws Exception {
		return new Word( getLemma(), m_pos );
	}
	
	public Word getPlural() throws Exception {
		return new Word( MorphAdorner.instance().getPlural( getLemma() ), m_pos );
	}
	
	public boolean equals( Object object ){
		if( !( object instanceof Word ) ) return false;
		Word other = (Word)object;
		try {
			if( other.getLemma().equals( this.getLemma() ) 
			   /* && ( other.getPartOfSpeech().equals( this.getPartOfSpeech() ) ) */ ){
				return true;
			}
		}
		catch ( Exception e ) { /* nothing */ }
		return false;
	}
}