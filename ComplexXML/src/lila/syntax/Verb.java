
package lila.syntax;

import lila.tools.MorphAdorner;
import lila.tools.wordnet.Category;


public class Verb extends Word {
	
	public Verb( String text ){
		super( text );
	}
	
	public Verb( String text, String pos ){
		super( text );
		m_pos = pos;
	}
	
	public String getCategory(){
		return Category.VERB;
	}
	
	public Verb getActive() throws Exception {
		return new Verb( getLemma(), m_pos );
	}
	
	/* return past participle, preposition (e.g. "by" might have to be added explicitly) */
	public Verb getPassive() throws Exception {
		return new Verb( MorphAdorner.instance().getPastParticiple( getLemma() ), m_pos );
	}
}