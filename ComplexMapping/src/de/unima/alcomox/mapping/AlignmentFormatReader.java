package de.unima.alcomox.mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owl.align.Relation;

import de.unima.alcomox.exceptions.CorrespondenceException;
import de.unima.alcomox.exceptions.MappingException;
import fr.inrialpes.exmo.align.impl.rel.EquivRelation;
import fr.inrialpes.exmo.align.impl.rel.IncompatRelation;
import fr.inrialpes.exmo.align.impl.rel.SubsumeRelation;
import fr.inrialpes.exmo.align.impl.rel.SubsumedRelation;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class AlignmentFormatReader implements AlignmentReader {

	@Override
	public Alignment getMapping(String filepath) throws MappingException {

		AlignmentParser aparser = new AlignmentParser(0);
		Alignment mapping = new Alignment();
		try {
			org.semanticweb.owl.align.Alignment reference = aparser
					.parse(new File(filepath).toURI());

			Enumeration<Cell> cells = reference.getElements();
			ArrayList<Correspondence> correspondences = new ArrayList<Correspondence>();

			while (cells.hasMoreElements()) {
				Cell cell = cells.nextElement();

				// Semantic relation
				SemanticRelation sr = null;

				Relation rel = cell.getRelation();
				Correspondence correspondence = null;

				try {
					if (rel instanceof EquivRelation) {
						sr = new SemanticRelation(SemanticRelation.EQUIV);
					} else if (rel instanceof SubsumedRelation) {
						sr = new SemanticRelation(SemanticRelation.SUB);
					} else if (rel instanceof SubsumeRelation) {
						sr = new SemanticRelation(SemanticRelation.SUPER);
					} else if (rel instanceof IncompatRelation) {
						sr = new SemanticRelation(SemanticRelation.DIS);
					} else {
						// Last chance
						String line = rel.getRelation();
						if (line.contains("EquivRelation")) {
							sr = new SemanticRelation(SemanticRelation.EQUIV);
						} else {
							sr = new SemanticRelation(SemanticRelation.NA);
						}
					}

					correspondence = new Correspondence(
							cell.getObject1AsURI(null),
							cell.getObject2AsURI(null), sr, cell.getStrength());
				} catch (CorrespondenceException e) {
					e.printStackTrace();
				}
				correspondences.add(correspondence);
			}

			mapping.setCorrespondences(correspondences);

		} catch (AlignmentException e) {
			throw new MappingException(-1, null, e);
		}
		// catch (CorrespondenceException ce) {
		// throw new MappingException(MappingException.CORRESPONDENCE_PROBLEM,
		// "correspondences in " + filepath + " is invalid", ce);
		// }

		return mapping;
	}

}
