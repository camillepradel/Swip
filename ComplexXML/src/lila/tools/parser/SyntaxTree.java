
package lila.tools.parser;

import java.io.*;
import java.util.*;

import lila.util.Settings;
import lila.util.Parameter;
import lila.syntax.*;

import edu.stanford.nlp.trees.*;


public class SyntaxTree {

	private Tree m_parse;
	
	private Set<TypedDependency> m_deps;
	
	
	public SyntaxTree( Tree parse ){
		m_parse = parse;
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure( m_parse );
		m_deps = new HashSet<TypedDependency>();
		Iterator iter = gs.typedDependencies().iterator();
		while( iter.hasNext() ){
			m_deps.add( (TypedDependency)iter.next() );
		}
	}
	
	public String[] getModifiers( String sWord ){
		Set<TypedDependency> deps = getDependencies( sWord.toString(), null, ".*mod" );
		String words[] = new String[deps.size()];
		int i=0;
		for( TypedDependency dep: deps ){
			words[i++] = dep.dep().yield().toString();
		}
		return words;
	}
	
	public String[] getObjects( String sVerb ){
		Set<TypedDependency> deps = getDependencies( sVerb.toString(), null, ".*obj" );
		String words[] = new String[deps.size()];
		int i=0;
		for( TypedDependency dep: deps ){
			words[i++] = dep.dep().yield().toString();
		}
		return words;
	}
	
	public String[] getSubjects( String sVerb ){
		Set<TypedDependency> deps = getDependencies( sVerb.toString(), null, ".*subj" );
		String words[] = new String[deps.size()];
		int i=0;
		for( TypedDependency dep: deps ){
			words[i++] = dep.dep().yield().toString();
		}
		return words;
	}
	
	public String[] getVerbs( String sNoun ){
		Set<TypedDependency> deps = getDependencies( null, sNoun.toString(), "(.*subj|.*obj)" );
		String words[] = new String[deps.size()];
		int i=0;
		for( TypedDependency dep: deps ){
			words[i++] = dep.gov().yield().toString();
		}
		return words;
	}

	/*
	 * TODO: coordinating conjunctions
	 */
	private Set<TypedDependency> getDependencies( String sGov, String sDep, String sTypePattern ){
		HashSet<TypedDependency> tds = new HashSet<TypedDependency>();
		for( TypedDependency td: m_deps )
		{
			TreeGraphNode gov = td.gov();
			TreeGraphNode dep = td.dep();
			boolean bAdd = (
				   ( sTypePattern == null || td.reln().getShortName().matches( sTypePattern ) )
				&& ( sGov == null || gov.yield().toString().equals( sGov ) )
				&& ( sDep == null || dep.yield().toString().equals( sDep ) )
			);			
			if( bAdd ){
				tds.add( td );
			}
		}
		return tds;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		// tree
		sb.append( "syntax tree:\n\n" );
		TreePrint tp = new TreePrint( "penn,typedDependenciesCollapsed" );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		tp.setPrintWriter( new PrintWriter( out ) );
		tp.printTree( m_parse );
		sb.append( out.toString() );
		// dependencies
		sb.append( "\n\ndependencies:\n\n" );
		Iterator iter = m_deps.iterator();
		while( iter.hasNext() )
		{
			TypedDependency dep = (TypedDependency)iter.next();
			sb.append( dep.toString() +"\n" );
		}
		return sb.toString();
	}
}
