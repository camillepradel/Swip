
package lila.tools.wordnet;

import java.util.*;

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.Dictionary;


public class PrintUtils {

	public static String toString( String strings[] ){
		StringBuffer sb = new StringBuffer();
		sb.append( "[ " );
		for( int i=0; i<strings.length; i++ )
		{
			sb.append( strings[i] );
			if( i<strings.length-1 ){
				sb.append( ", " );
			}
		}
		sb.append( " ]" );
		return sb.toString();
	}
	
	public static String toString( Collection<String> strings ){
		StringBuffer sb = new StringBuffer();
		Iterator iter = strings.iterator();
		sb.append( "[ " );
		while( iter.hasNext() )
		{
			sb.append( iter.next() );
			if( iter.hasNext() ){
				sb.append( ", " );
			}
		}
		sb.append( " ]" );
		return sb.toString();
	}
	
	public static String line(){
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<100; i++ ){
			sb.append( "-" );
		}
		return sb.toString();
	}
	
	/* words */
	
	public static String toString( Word words[] ){
		String s[] = new String[words.length];
		for( int i=0; i<words.length; i++ ){
			s[i] = words[i].getLemma();
		}
		return toString(s);
	}
	
	/* synsets */

	public static String toString( Synset synset ) throws JWNLException {
		StringBuffer sb = new StringBuffer();
		sb.append( "synset: offset="+ synset.getOffset() );
		sb.append( "\nwords: "+ toString( synset.getWords() ) );
		sb.append( "\ngloss: "+ synset.getGloss() );
		sb.append( "\nlexical relations:\n" );
		Iterator iter = PointerType.getAllPointerTypesForPOS( synset.getPOS() ).iterator();
		while( iter.hasNext() )
		{
			PointerType type = (PointerType) iter.next();
			Pointer pointers[] = synset.getPointers( type );
			if( pointers.length > 0 ){
				sb.append( toString( pointers )+"\n" );
			}
		}
		return sb.toString();
	}

	public static String toString( Synset[] synsets ) throws JWNLException {
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<synsets.length; i++ )
		{
			sb.append( "("+ i +") "+ toString( synsets[i] ) );
			if( i<synsets.length-1 ){
				sb.append( "\n" );
			}
		}
		return sb.toString();
	}
	
	/* senses */
	
	public static String toString( Sense[] senses ){
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<senses.length; i++ )
		{
			sb.append( /* "("+ i +") "+ */ senses[i].toString() );
			if( i<senses.length-1 ){
				sb.append( "\n" );
			}
		}
		return sb.toString();
	}
	
	public static String toShortString( Sense[] senses ){
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<senses.length; i++ )
		{
			sb.append( /* "("+ i +") "+ */ senses[i].toShortString() );
			if( i<senses.length-1 ){
				sb.append( "\n" );
			}
		}
		return sb.toString();
	}
	
	/* pointers */
	
	public static String toString( Pointer pointer ) throws JWNLException {
		StringBuffer sb = new StringBuffer();
		sb.append( pointer.getType().getLabel() +": " );
		sb.append( toString( pointer.getTarget() ) );
		return sb.toString();
	}

	public static String toString( PointerTarget target ) throws JWNLException {
		if( target instanceof Synset ){
			return toString( ((Synset)target).getWords() );
		}
		else if( target instanceof Word ){
			return ( (Word)target ).getLemma();
		}
		else {
			return target.toString();
		}
	}
	
	public static String toString( PointerTarget[] targets ) throws JWNLException {
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<targets.length; i++ )
		{
			sb.append( "("+ i +") "+ toString( targets[i] ) );
			/* if( i<targets.length-1 ){
				sb.append( "\n" );
			}*/
		}
		return sb.toString();
	}

	public static String toString( Pointer pointers[] ) throws JWNLException {
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<pointers.length; i++ )
		{
			sb.append( /* "("+ i +") " */ "\t"+ toString( pointers[i] ) );
			if( i<pointers.length-1 ){
				sb.append( "\n" );
			}
		}
		return sb.toString();
	}
}