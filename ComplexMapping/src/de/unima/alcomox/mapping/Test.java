package de.unima.alcomox.mapping;

import de.unima.alcomox.exceptions.AlcomoException;

public class Test {
	
	public static void main(String[] args) throws AlcomoException {
		
		
		AlignmentReader mr = new AlignmentReaderXml();
		Alignment m = mr.getMapping("C:/Dokumente und Einstellungen/Schlumpf/Desktop/alignments-xml-onlyequiv/asmov-cmt-confOf.rdf");
		
		for (Correspondence c : m) {
			System.out.println(c);
		}
		
		m.applyThreshhold(0.1f);
		
		AlignmentWriter mw = new AlignmentWriterXml();
		mw.writeMapping("D:/xxx.rdf", m);
		
		//System.out.println(m.getMetaDescription());
		Alignment ma = new Alignment();
		SemanticRelation s = new SemanticRelation(SemanticRelation.EQUIV);
	    Correspondence c = new Correspondence("http://xyz", "scsal", s, 0.7);
		ma.push(c);
		
	}

}
