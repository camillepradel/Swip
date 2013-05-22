
package lila.tools.wordnet;

import java.util.*;

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.Dictionary;


public class VerbSense extends Sense {
	
	protected VerbSense( int id, Synset synset ){
		m_id = id;
		m_synset = synset;
	}
	
	public boolean isNominalization( NounSense noun ) throws JWNLException {
		for( Sense sense: getNominalizations() ){
			if( sense.equals( noun ) ) return true;
		}
		for( Sense sense: noun.getNominalizations() ){
			if( sense.equals( this ) ) return true;
		}
		return false;
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
	
	/* see also */
	
	public Sense[] getSeeAlso() throws JWNLException { 
		return getSenses( PointerUtils.getInstance().getAlsoSees( m_synset ) );
	}
	
	/* antonyms */
	
	public Sense[] getAntonyms() throws JWNLException { 
		return getSenses( PointerUtils.getInstance().getAntonyms( m_synset ) );
	}
	
	/* hyponyms, hypernyms */
	
	public Sense[] getHyponyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getHyponymTree( m_synset ) ); 
	} 
	
	public Sense[] getHypernyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getHypernymTree( m_synset ) ); 
	} 
	
	public Sense[] getDirectHyponyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getDirectHyponyms( m_synset ) ); 
	} 
	
	public Sense[] getDirectHypernyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getDirectHypernyms( m_synset ) ); 
	}
	
	public Sense[] getInstanceHypernyms() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.INSTANCE_HYPERNYM ) );
	}
	
	public Sense[] getInstanceHyponyms() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.INSTANCES_HYPONYM ) );
	}

	/* meronyms */

	public Sense[] getMeronyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getMeronyms( m_synset ) );
	}
	
	public Sense[] getMemberMeronyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getMemberMeronyms( m_synset ) );
	}
	
	public Sense[] getPartMeronyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getPartMeronyms( m_synset ) );
	}
	
	public Sense[] getSubstanceMeronyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getSubstanceMeronyms( m_synset ) );
	}
	
	/* holonyms */
	
	public Sense[] getHolonyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getHolonyms( m_synset ) );
	}
	
	public Sense[] getMemberHolonyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getMemberHolonyms( m_synset ) );
	}
	
	public Sense[] getPartHolonyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getPartHolonyms( m_synset ) );
	}
	
	public Sense[] getSubstanceHolonyms() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getSubstanceHolonyms( m_synset ) );
	}
	
	/* causes */
	
	public Sense[] getCauses() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getCauses( m_synset ) ); 
	}
	
	/* entailments */
	
	public Sense[] getEntailments() throws JWNLException { 
		return getSenses( PointerUtils.getInstance().getEntailments( m_synset ) );
	}
	
	public Sense[] getEntailedBy() throws JWNLException { 
		return getSenses( PointerUtils.getInstance().getEntailedBy( m_synset ) );
	}
	
	/* nominalization */
	
	public Sense[] getNominalizations() throws JWNLException {
		return getSenses( getPointerTargets( PointerType.NOMINALIZATION ) ); 
	}
	
	/* verb group */
	
	public Sense[] getVerbGroup() throws JWNLException {
		return getSenses( PointerUtils.getInstance().getVerbGroup( m_synset ) ); 
	}
}