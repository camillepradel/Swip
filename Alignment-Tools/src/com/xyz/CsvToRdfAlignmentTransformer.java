package com.xyz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Relation;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.rel.EquivRelation;
import fr.inrialpes.exmo.align.impl.rel.SubsumeRelation;
import fr.inrialpes.exmo.align.impl.rel.SubsumedRelation;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.ontowrap.BasicOntology;
import fr.inrialpes.exmo.ontowrap.Ontology;

public class CsvToRdfAlignmentTransformer {

	private static Logger LOGGER = Logger
			.getLogger("CsvToRdfAlignmentTransformer");

	/**
	 * @param args
	 * @throws AlignmentException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws AlignmentException,
			IOException, URISyntaxException {

		String csvFile = "E:/pgillet/Stage/Data/LOD-Reference-Alignment/music_dbpedia.csv";
		String out = "E:/pgillet/Stage/Data/LOD-Reference-Alignment/music_dbpedia.rdf";

		String baseUri1 = "http://purl.org/ontology/mo/";
		String baseUri2 = "http://dbpedia.org/ontology/";

		final double strength = 1.0f;

		BasicAlignment alignment = new BasicAlignment();
		
		Ontology o1 = new BasicOntology/*<?>*/();
		o1.setURI(new URI(baseUri1));
		
		Ontology o2 = new BasicOntology/*<?>*/();
		o2.setURI(new URI(baseUri2));

		alignment.setOntology1(o1);
		alignment.setOntology2(o2);

		FileReader fr = new FileReader(csvFile);
		BufferedReader br = new BufferedReader(fr);

		// Read the column titles
		br.readLine();

		String str = null;
		while ((str = br.readLine()) != null) {

			String[] tokens = str.split(",");

			String entity1 = baseUri1 + tokens[0];
			Relation relation = getRelation(tokens[1]);
			String entity2 = baseUri2 + tokens[2];

			if (relation != null) {
				try {
					alignment.addAlignCell(null, new URI(entity1), new URI(
							entity2), relation, strength);
					LOGGER.info(entity1 + " " + relation.getRelation() + " "
							+ entity2);

				} catch (AlignmentException e) {
					LOGGER.log(Level.WARNING, e.getMessage(), e);
				} catch (URISyntaxException e) {
					LOGGER.log(Level.WARNING, e.getMessage(), e);
				}
			}

		}
		br.close();

		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
				out)), true);
		AlignmentVisitor renderer = new RDFRendererVisitor(writer);
		alignment.render(renderer);
		writer.flush();
		writer.close();

		LOGGER.info("The job is done.");

	}

	public static Relation getRelation(String str) {

		Relation relation = null;

		switch (str) {
		case "=":
			relation = new EquivRelation();
			break;

		case ">":
			relation = new SubsumeRelation();
			break;

		case "<":
			relation = new SubsumedRelation();
			break;

		default:
			break;
		}

		return relation;
	}

}
