
package lila.tools.wordnet;

import java.util.*;

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.Dictionary;


public class AdjectiveSense extends Sense {
	
	protected AdjectiveSense( int id, Synset synset ){
		m_id = id;
		m_synset = synset;
	}

	/* see also */
	
	private Sense[] getSeeAlso() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getAlsoSees( m_synset ) ); 
	}
	
	/* category, usage, region */
	
	public Sense[] getCategories() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.CATEGORY ) );
	}
	
	public Sense[] getRegions() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.REGION ) );
	}
	
	public Sense[] getUsages() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.USAGE ) );
	}
	
	/* pertainym */
	
	private Sense[] getPertainym() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.PERTAINYM ) ); 
	} 
	
	/* antonyms */
	
	public Sense[] getAntonyms() throws JWNLException { 
		return getSenses( PointerUtils.getInstance().getAntonyms( m_synset ) );
	}
		
	/* similar */
	
	private Sense[] getSimilar() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.SIMILAR_TO ) ); 
	} 
	
	/* participle */
	
	private Sense[] getParticipleOf() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.PARTICIPLE_OF ) ); 
	} 
	
	/* attributes */
	
	public Sense[] getAttributes() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getAttributes( m_synset ) ); 
	}
}