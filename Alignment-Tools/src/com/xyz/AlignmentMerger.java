package com.xyz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class AlignmentMerger {

	private static Logger LOGGER = Logger.getLogger("AlignmentMerger");

	private static final class RdfFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			final String ext = ".rdf";
			return (pathname.getName().endsWith(ext));
		}
	}

	/**
	 * 
	 * @throws TransformerException
	 * @throws AlignmentException
	 * @throws IOException
	 */
	public static void main(String[] args) throws AlignmentException,
			IOException {

		File basedir = new File(
				"E:/pgillet/Stage/Data/Alignements/Output - MusicBrainz - DBPedia - v3/dbpedia-music");

		File[] files = basedir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		BasicAlignmentDecorator alignment = null;
		AlignmentParser aparser = new AlignmentParser(0);

		FileFilter filter = new RdfFileFilter();

		for (File matchingToolDir : files) {
			File[] rdfFiles = matchingToolDir.listFiles(filter);

			File rdfFile = (rdfFiles.length > 0) ? rdfFiles[0] : null;

			// Load the reference alignment
			if (rdfFile != null) {
				if (alignment == null) {
					LOGGER.info("First alignment from = "
							+ rdfFile.getParentFile().getName());
					alignment = new BasicAlignmentDecorator(
							(BasicAlignment) aparser.parse(rdfFile.toURI()));
				} else {

					Alignment other = aparser.parse(rdfFile.toURI());
					LOGGER.info("Merging with = "
							+ rdfFile.getParentFile().getName());

					alignment.ingest(other);
				}
			}

		}

		// Displays it as OWL Rules
		File out = new File(basedir, "merge.rdf");
		LOGGER.info("Writing the final result to = " + out);
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
				out /*
					 * , "UTF-8"
					 */)), true);
		AlignmentVisitor renderer = new RDFRendererVisitor(writer);
		alignment.render(renderer);
		writer.flush();
		writer.close();

	}

}
