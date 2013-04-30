package com.xyz;

import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owl.align.Relation;

import fr.inrialpes.exmo.align.impl.BasicCell;

public class BasicCellDecorator extends BasicCell {

	public BasicCellDecorator(String id, Object ob1, Object ob2,
			Relation rel, double m) throws AlignmentException {
		super(id, ob1, ob2, rel, m);
		// TODO Auto-generated constructor stub
	}

	public BasicCellDecorator(Cell c) throws AlignmentException {
		super(c.getId(), c.getObject1(), c.getObject2(), c.getRelation(), c
				.getStrength());
	}

	public boolean equals(Object c) {
		if (c != null && c instanceof Cell)
			return equals((Cell) c);
		else
			return false;
	}

	public boolean equals(Cell c) {
		if (c == null)
			return false;
		else
			return (object1.equals(((BasicCell) c).getObject1()) && object2
					.equals(((BasicCell) c).getObject2())
			/*
			 * && strength == ((BasicCell) c).getStrength() && (relation
			 * .equals(((BasicCell) c).getRelation()))
			 */);
	}

	public int hashCode() {
		return 17 + 7 * object1.hashCode() + 11 * object2.hashCode()
		/* + relation.hashCode() + (int) (strength * 150.) */;
	}
}