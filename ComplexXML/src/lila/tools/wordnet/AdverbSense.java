
package lila.tools.wordnet;

import java.util.*;

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.Dictionary;


public class AdverbSense extends Sense {
	
	protected AdverbSense( int id, Synset synset ){
		m_id = id;
		m_synset = synset;
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
		
	/* antonyms */
	
	public Sense[] getAntonyms() throws JWNLException { 
		return getSenses( PointerUtils.getInstance().getAntonyms( m_synset ) );
	}
		
	/* derived */
	
	public Sense[] getAttributes() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getDerived( m_synset ) ); 
	}
}