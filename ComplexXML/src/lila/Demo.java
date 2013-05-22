
package lila;

import java.util.*;

import lila.syntax.*;
import lila.tools.wordnet.*;


public class Demo {
	
	private LiLA m_lila;
	
	
	public static void main( String args[] ) throws Exception {
		Demo demo = new Demo();
		//demo.phrase( "paper_written_by_clever_students" );
		// demo.phrase( "rejected_paper" );
		/* demo.phrase( "student_paper" );
		demo.phrase( "accepted_paper" );
		demo.phrase( "student_papers" );
		demo.phrase( "wrote_paper" );
		demo.phrase( "written_by" );
		demo.word( "paper" );
		demo.word( "write" ); */

	}
	
	public Demo() throws Exception {
		m_lila = new LiLA();
		m_lila.setContext( createContext() );
	}
	
	private Set<String> createContext(){
		Set<String> example = new HashSet<String>();
		String s[] = new String[]{ "conference", "publication", "scientific", "research", "participant", "course" };
		for( int i=0; i<s.length; i++ ){
			example.add( s[i] );
		}
		return example;
	}
	
	/************************************************************************************/
	
	public void phrase( String sLabel ) throws Exception {
		String sPhrase = m_lila.normalize( sLabel );
		Phrase phrase = new Phrase( sPhrase );
		Word head = phrase.getHead();
		StringBuffer sb = new StringBuffer();
		sb.append( "\nphrase: "+ sPhrase +"\n" );
		sb.append( "head="+ head +" " );
		sb.append( "modifiers="+ toString( phrase.getModifiers( head ) )+"\n" );
		/* list words */
		int i=0;
		for( Word word: phrase.getWords() )
		{
			sb.append( "\n\t("+ (i++) +") "+ toString( word ) );
			if( word.isVerb() )
			{
				sb.append( "\tsubjects="+ toString( phrase.getSubjects( (Verb)word ) ) );
				sb.append( " objects="+ toString( phrase.getObjects( (Verb)word ) )+"\n" );
			}
		}
		sb.append( "\n"+ phrase.getSyntaxTree() );
		System.out.println( sb.toString() );
	}
	
	public String toString( Word word ) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append( "word="+ word +" pos="+ word.getPartOfSpeech() +" category="+ word.getCategory() +" lemma="+ word.getLemma() +"\n" );
		sb.append( "\t-> synonyms="+ toString( word.getSynonyms() )+"\n" );
		if( word.isNoun() ){
			sb.append( "\t-> singular="+ ((Noun)word).getSingular() +" plural="+ ((Noun)word).getPlural()+ "\n" );
		}
		else if( word.isVerb() ){
			sb.append( "\t-> active="+ ((Verb)word).getActive() +" passive="+ ((Verb)word).getPassive() +" by\n" );
		}
		return sb.toString();
	}
	
	public String toString( Word words[] ){
		StringBuffer sb = new StringBuffer();
		sb.append( "[ " );
		for( int i=0; i<words.length; i++ ){
			sb.append( words[i] );
			if( i<words.length-1 ){
				sb.append( ", " );
			}
		}
		sb.append( " ]" );
		return sb.toString();
	}
	
	/************************************************************************************/
	
	public void word( String sLabel ) throws Exception {
		Word word = new Word( sLabel );
		System.out.println( "\n\nword="+ sLabel +" category="+ word.getCategory() );
		System.out.println( "senses:\n"+ PrintUtils.toShortString( word.getSenses() ) ); 
		print( word.getSense() );
	}
	
	public void verb( String sLabel ) throws Exception {
		word( new Verb( sLabel ) );
	}
	
	public void noun( String sLabel ) throws Exception {
		word( new Noun( sLabel ) );
	}
	
	public void word( Word word ) throws Exception {
		System.out.println( "\nword="+ word +" category="+ word.getCategory() );
		if( !word.exists() )
		{
			Word correct = word.checkSpelling();
			print( correct.getSense() );
			return;
		}
		print( word.getSense() );
	}
	
	public void print( Sense sense ) throws Exception {
		if( sense == null ){
			System.out.println( "\nno sense!" );
			return;
		}
		System.out.println( "\n"+ sense.toString() );
		if( sense instanceof NounSense ){
			NounSense noun = (NounSense)sense;
			System.out.println( "all hypernyms: "+ PrintUtils.toShortString( noun.getHypernyms() ) );
			System.out.println( "\nall hyponyms: "+ PrintUtils.toShortString( noun.getHyponyms() ) );
		}
	}
}