package com.xyz;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;

import fr.inrialpes.exmo.align.impl.BasicAlignment;

public class BasicAlignmentDecorator extends BasicAlignment {

	public BasicAlignmentDecorator(BasicAlignment alignment)
			throws AlignmentException {
		setOntology1(alignment.getOntology1());
		setOntology2(alignment.getOntology2());
		setType(getType());
		setLevel(getLevel());
		setFile1(getFile1());
		setFile2(getFile2());
		setExtensions(convertExtension("cloned", this.getClass().getName()
				+ "#clone"));
		// for ( Enumeration e = namespaces.getNames() ; e.hasMoreElements(); ){
		// String label = (String)e.nextElement();
		for (String label : namespaces.stringPropertyNames()) {
			setXNamespace(label, getXNamespace(label));
		}
		super.ingest(alignment);
	}

	@Override
	public void ingest(Alignment other) throws AlignmentException {
		if (other != null) {
			for (Cell c : other) {

				Cell decorated = new BasicCellDecorator(c);
				addCell(decorated);
			}
		}
	}

}