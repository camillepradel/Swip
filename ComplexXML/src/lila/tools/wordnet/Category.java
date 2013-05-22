
package lila.tools.wordnet;

import net.didion.jwnl.data.POS;

/*
 * wrapper for net.didion.jwnl.data.POS
 */
public abstract class Category {

	public final static String NOUN = POS.NOUN.getLabel();

	public final static String VERB = POS.VERB.getLabel();

	public final static String ADJECTIVE = POS.ADJECTIVE.getLabel();

	public final static String ADVERB = POS.ADVERB.getLabel();
	
	public final static String UNKNOWN = "unknown";
	
	
	public static String guess( String pos ){
		if( pos.startsWith( "N" ) ){
			return Category.NOUN;
		}
		else if( pos.startsWith( "V" ) ){
			return Category.VERB;
		}
		else if( pos.startsWith( "J" ) ){
			return Category.ADJECTIVE;
		}
		else if( pos.startsWith( "R" ) ){
			return Category.ADVERB;
		}
		return Category.UNKNOWN;
	}
}