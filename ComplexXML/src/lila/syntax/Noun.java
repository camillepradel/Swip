
package lila.syntax;

import lila.tools.MorphAdorner;
import lila.tools.wordnet.Category;


public class Noun extends Word {
	
	public Noun( String text ){
		super( text );
	}
	
	public Noun( String text, String pos ){
		super( text );
		m_pos = pos;
	}
	
	public String getCategory(){
		return Category.NOUN;
	}
	
	public Noun getSingular() throws Exception {
		return new Noun( getLemma(), m_pos );
	}
	
	public Noun getPlural() throws Exception {
		return new Noun( MorphAdorner.instance().getPlural( getLemma() ), m_pos );
	}
}