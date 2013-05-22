
package lila.tools.wordnet;

import java.util.*;

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.Dictionary;


public abstract class Sense {

	protected int m_id = -1;
	
	protected Synset m_synset;

	
	protected static Sense create( int id, Synset synset ){
		POS pos = synset.getPOS();
		if( pos == POS.NOUN ) return new NounSense( id, synset );
		else if( pos == POS.VERB ) return new VerbSense( id, synset );
		else if( pos == POS.ADJECTIVE ) return new AdjectiveSense( id, synset );
		else if( pos == POS.ADVERB ) return new AdverbSense( id, synset );
		else return null;
	}
	
	protected Synset getSynset(){
		return m_synset;
	}
	
	protected POS getPOS(){
		return m_synset.getPOS();
	}
	
	public String toString(){
		try {
			return "("+ m_id +") "+ PrintUtils.toString( m_synset );
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		return null;
	}
	
	public String toShortString(){
		try {
			return "("+ m_id +") "+ PrintUtils.toString( getWords() );
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean equals( Object object ){
		if( !( object instanceof Sense ) ){
			return false;
		}
		return this.getOffset() == ((Sense)object).getOffset();
	}
	
	/************************************************** interface ***************************************************************/
	
	public long getOffset(){
		return m_synset.getOffset();
	}
	
	public int getID(){
		return m_id;
	}
	
	public String getCategory(){
		return m_synset.getPOS().getLabel();
	}
	
	public String getGloss(){
		return m_synset.getGloss();
	}
	
	public String[] getWords() throws JWNLException { 
		Word words[] = m_synset.getWords();
		String sWords[] = new String[words.length];
		for( int i=0; i<words.length; i++ ){
			sWords[i] = words[i].getLemma(); 
		}
		return sWords;
	}
	
	/************************************************** basics *****************************************************************/
	
	protected Sense[] getSenses( Synset synsets[] ){
		Sense senses[] = new Sense[synsets.length];
		for( int i=0; i<synsets.length; i++ ){
			senses[i] = Sense.create( i, synsets[i] );
		}
		return senses;
	}
	
	protected Sense[] getSenses( PointerTargetTree tree ){
		return getSenses( getSynsets( tree ) );
	}
	
	protected Synset[] getSynsets( PointerTargetTree tree ){	
		List<Synset> synsets = new ArrayList<Synset>();
		Iterator iter = tree.toList().iterator();
		while( iter.hasNext() )
		{
			PointerTargetNodeList nodeList = (PointerTargetNodeList)iter.next();
			for( int i = 0; i < nodeList.size(); i++ )
			{
				PointerTargetNode node = (PointerTargetNode)nodeList.get( i );
				Synset synset = node.getSynset();
				if( !contains( synsets, synset ) ){
					synsets.add( synset );
				}
			}
		}
		return (Synset[])synsets.toArray( new Synset[]{} );
	}
	
	protected Sense[] getSenses( PointerTargetNodeList list ){
		return getSenses( getSynsets( list ) );
	}
	
	protected Synset[] getSynsets( PointerTargetNodeList list ){		
		List<Synset> synsets = new ArrayList<Synset>(); 
		for( int i = 0; i < list.size(); i++ )
		{
			PointerTargetNode node = (PointerTargetNode)list.get( i );
			synsets.add( node.getSynset() );
		}
		return (Synset[])synsets.toArray( new Synset[]{} );
	}
	
	protected Synset[] getPointerTargets( PointerType type ) throws JWNLException {
		Pointer pointers[] = m_synset.getPointers( type );
		Synset synsets[] = new Synset[pointers.length];
		for( int i=0; i<pointers.length; i++ ){
			synsets[i] = pointers[i].getTargetSynset();
		}
		return synsets;
	}
	
	protected PointerType[] getPointerTypes(){
		List all = PointerType.getAllPointerTypesForPOS( m_synset.getPOS() );
		List<PointerType> some = new ArrayList<PointerType>();
		Iterator allIter = all.iterator();
		while( allIter.hasNext() )
		{
			PointerType type = (PointerType) allIter.next();
			if( m_synset.getPointers( type ).length > 0 ){
				some.add( type );
			}
		}
		PointerType[] types = new PointerType[some.size()];
		for( int i=0; i<some.size(); i++ ){
			types[i] = (PointerType) some.get(i);
		}
		return types;
	}
	
	private boolean contains( List<Synset> synsets, Synset synset ){
		for( Synset other: synsets ){
			if( other.getOffset() == synset.getOffset() ){
				return true;
			}
		}
		return false;
	}
	
	protected Sense[] getRelatedSenses() throws JWNLException {
		List<Synset> related = new ArrayList<Synset>();
		for( PointerType type: getPointerTypes() )
		{
			Synset synsets[] = getPointerTargets( type );
			for( Synset synset: synsets )
			{
				if( !contains( related, synset ) && synset.getOffset() != this.getOffset() ){
					related.add( synset );
				}
			}
		}
		Synset rsynsets[] = (Synset[])related.toArray( new Synset[]{} );
		return getSenses( rsynsets );
	}
}