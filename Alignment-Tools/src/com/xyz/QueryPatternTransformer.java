package com.xyz;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owl.align.Relation;
import org.xml.sax.ContentHandler;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.edoal.Comparator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class QueryPatternTransformer {

	public class Subject {

		private String baseURI;

		private String entity;

		public String getBaseURI() {
			return baseURI;
		}

		public String getEntity() {
			return entity;
		}

		public void setBaseURI(String baseURI) {
			this.baseURI = baseURI;
		}

		public void setEntity(String entity) {
			this.entity = entity;
		}

	}

	private static Logger LOGGER = Logger.getLogger("QueryPatternTransformer");

	public static void main(String[] args) throws AlignmentException,
			IOException {
		File f = new File(
				"E:/pgillet/Stage/Data/Alignements/Output - MusicBrainz - DBPedia - v3/dbpedia-music/merge.rdf");

		File patternFile = new File(
				"E:/pgillet/Stage/Data/Query Patterns/patterns-musicbrainz-qald2-50.txt");
		File out = new File("E:/pgillet/Stage/Data/Query Patterns/out.txt");

		QueryPatternTransformer transformer = new QueryPatternTransformer();
		transformer.loadPrefixes();

		// Parses the alignment file
		Map<URI, Set<Cell>> m = transformer.parseAlignment(f);

		// Reads the pattern file
		List<String> lines = transformer.readStringFile(patternFile);

		transformer.transform(m, lines);

		// now, write the file again with the changes
		transformer.writeStringFile(lines, out);

		LOGGER.info("The job is done");
	}

	private Cell askUserForCell(Set<Cell> cells) throws AlignmentException {
		Console c = System.console();
		if (c == null) {
			LOGGER.severe("No console.");
			return cells.iterator().next();
		}

		Cell[] cellsArray = cells.toArray(new Cell[cells.size()]);

		String src = cellsArray[0].getObject1AsURI().toString();

		c.format("-------------------------------------------------------------%n");
		c.format("Source entity = %s%n", src);

		for (int i = 0; i < cellsArray.length; i++) {
			Cell cell = cellsArray[i];

			String uri2 = "n/a";
			try {
				uri2 = cell.getObject2AsURI().toString();
			} catch (AlignmentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			c.format("%d: %s\t%s\t%s%n", i + 1, uri2, cell.getRelation()
					.getRelation(), cell.getStrength());
		}

		int index = -1;
		do {
			String choice = c.readLine("Please enter your choice [1 - %d]: ",
					cellsArray.length);
			try {
				index = Integer.parseInt(choice);
			} catch (NumberFormatException e) {
				// Does nothing
			}
		} while (index < 1 || index > cellsArray.length);

		return cellsArray[index-1];
	}

	private void transform(Map<URI, Set<Cell>> m, List<String> lines)
			throws AlignmentException {
		final String tail = "(\\(|;|\\s)";

		for (Entry<URI, Set<Cell>> entry : m.entrySet()) {

			Subject src = getSubject(entry.getKey().toString());

			Set<Cell> cells = entry.getValue();
			Cell cell = null;
			if (cells.size() > 1) {
				cell = askUserForCell(cells);
			} else {
				cell = cells.iterator().next();
			}

			if (cell != null) {
				Subject dest = getSubject(entry.getValue().iterator().next()
						.getObject2AsURI().toString());

				// Regular expression: "prefix:entity" followed by a open
				// parenthesis,
				// a semicolon or a space character.
				String regex = getPrefix(src.getBaseURI()) + ":"
						+ src.getEntity() + tail;
				Pattern p = Pattern.compile(regex);

				String replacement = getPrefix(dest.getBaseURI()) + ":"
						+ dest.getEntity();

				LOGGER.info("Replacing " + regex + " with " + replacement);

				for (int i = 0; i < lines.size(); i++) {
					String line = lines.get(i);

					Matcher matcher = p.matcher(line);
					StringBuffer sb = new StringBuffer();

					while (matcher.find()) {
						matcher.appendReplacement(sb,
								replacement + matcher.group(1));
					}
					matcher.appendTail(sb);

					if (!line.equals(sb.toString())) {
						LOGGER.info("New line = " + sb.toString());

						lines.set(i, sb.toString());
					}

				}
			}

		}
	}

	private Properties prefixes;

	Map<String, Set<String>> suggestions = new HashMap<String, Set<String>>();

	public String getPrefix(String uri) {
		Set<Entry<Object, Object>> entries = prefixes.entrySet();

		for (Entry entry : entries) {

			String value = (String) entry.getValue();
			if (value.equals(uri)) {
				String key = (String) entry.getKey();
				return key;
			}
		}

		return null;
	}

	public Subject getSubject(String uri) {
		int index = uri.lastIndexOf("#");
		if (index == -1) {
			index = uri.lastIndexOf("/");
		}

		String baseURI = null;
		String entity = null;
		if (index != -1) {
			baseURI = uri.substring(0, index + 1);
			entity = uri.substring(index + 1);
		}

		Subject subject = new Subject();
		subject.setEntity(entity);
		subject.setBaseURI(baseURI);

		return subject;
	}

	public URI getURI(String prefix) throws URISyntaxException {
		String str = prefixes.getProperty(prefix);
		URI result = null;
		if (str != null) {
			return new URI(str);
		}

		return result;
	}

	private void loadPrefixes() throws IOException {
		prefixes = new Properties();

		// Load prefixes
		final String name = "prefixes.properties";
		InputStream in = QueryPatternTransformer.class
				.getResourceAsStream(name);
		prefixes.load(in);
		in.close();
	}

	public Map<URI, Set<Cell>> parseAlignment(File alignFile)
			throws AlignmentException {

		LOGGER.info("Parsing " + alignFile.getPath());

		Map<URI, Set<Cell>> suggestions = new HashMap<URI, Set<Cell>>();

		AlignmentParser aparser = new AlignmentParser(0);
		BasicAlignment alignment = (BasicAlignment) aparser.parse(alignFile
				.toURI());

		Enumeration<Cell> cells = alignment.getElements();

		while (cells.hasMoreElements()) {
			Cell cell = cells.nextElement();

			URI uri1 = cell.getObject1AsURI();
			LOGGER.info("Entity1: " + uri1);

			URI uri2 = cell.getObject2AsURI();
			LOGGER.info("Entity2: " + uri2);

			LOGGER.info("Relation: " + cell.getRelation().getRelation());
			LOGGER.info("Strength: " + cell.getStrength());
			LOGGER.info("***");

			Set<Cell> value = suggestions.get(uri1);
			if (value == null) {
				value = new TreeSet<Cell>(/*new java.util.Comparator<Cell>() {

					@Override
					public int compare(Cell o1, Cell o2) {
						return Double.compare(o2.getStrength(),
								o1.getStrength());
					}
				}*/);
			}

			value.add(new CellDecorator(cell));
			suggestions.put(uri1, value);

		}

		return suggestions;
	}

	public List<String> readStringFile(File file) throws IOException {

		// we need to store all the lines
		List<String> lines = new ArrayList<String>();

		// first, read the file and store the changes
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();

		return lines;
	}

	public void writeStringFile(List<String> lines, File f)
			throws FileNotFoundException {
		PrintWriter out = new PrintWriter(f);
		for (String l : lines) {
			out.println(l);
		}
		out.close();
	}

	public class CellDecorator implements Cell {

		private Cell decorated;

		CellDecorator(Cell decorated) {
			this.decorated = decorated;
		}

		@Override
		public int compareTo(Cell o) {
			if (equals(o)) {
				return 0;
			}

			try {
				int res = compare(getObject1AsURI(), o.getObject1AsURI(), false);

				if (res != 0) { // Not sure about this
					return res;
				}

				return compare(getObject2AsURI(), o.getObject2AsURI(), false);

			} catch (AlignmentException e) {
				return 0; // The pitfall
			}
		}

		// Null safe comparator
		public int compare(Comparable c1, Comparable c2, boolean nullGreater) {
			if (c1 == c2) {
				return 0;
			} else if (c1 == null) {
				return (nullGreater ? 1 : -1);
			} else if (c2 == null) {
				return (nullGreater ? -1 : 1);
			}
			return c1.compareTo(c2);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			try {
				result = prime * result + getObject1AsURI().hashCode();
				result = prime * result + getObject2AsURI().hashCode();
			} catch (AlignmentException e) {
				// Ignore
				result = -1;
			}

			return result;
		}

		/**
		 * Two cells are considered equals if they have the same source and
		 * target URIs.
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CellDecorator other = (CellDecorator) obj;

			try {
				if (getObject1AsURI() == null) {
					if (other.getObject1AsURI() != null) {
						return false;
					}
				} else if (!getObject1AsURI().equals(other.getObject1AsURI()))
					return false;

				if (getObject2AsURI() == null) {
					if (other.getObject2AsURI() != null) {
						return false;
					}
				} else if (!getObject2AsURI().equals(other.getObject2AsURI())) {
					return false;
				}
			} catch (AlignmentException e) {
				return false;
			}

			return true;
		}

		@Override
		public void accept(AlignmentVisitor arg0) throws AlignmentException {
			decorated.accept(arg0);
		}

		@Override
		public Cell compose(Cell arg0) throws AlignmentException {
			return decorated.compose(arg0);
		}

		@Override
		public void dump(ContentHandler arg0) {
			decorated.dump(arg0);

		}

		@Override
		public boolean equals(Cell arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getExtension(String arg0, String arg1) {
			return decorated.getExtension(arg0, arg1);
		}

		@Override
		public Collection<String[]> getExtensions() {
			return decorated.getExtensions();
		}

		@Override
		public String getId() {
			return decorated.getId();
		}

		@Override
		public Object getObject1() {
			return decorated.getObject1();
		}

		@Override
		public URI getObject1AsURI() throws AlignmentException {
			return decorated.getObject1AsURI();
		}

		@Override
		public URI getObject1AsURI(Alignment arg0) throws AlignmentException {
			return decorated.getObject1AsURI(arg0);
		}

		@Override
		public Object getObject2() {
			return decorated.getObject2();
		}

		@Override
		public URI getObject2AsURI() throws AlignmentException {
			return decorated.getObject2AsURI();
		}

		@Override
		public URI getObject2AsURI(Alignment arg0) throws AlignmentException {
			return decorated.getObject1AsURI(arg0);
		}

		@Override
		public Relation getRelation() {
			return decorated.getRelation();
		}

		@Override
		public String getSemantics() {
			return decorated.getSemantics();
		}

		@Override
		public double getStrength() {
			return decorated.getStrength();
		}

		@Override
		public Cell inverse() throws AlignmentException {
			return decorated.inverse();
		}

		@Override
		public void setExtension(String arg0, String arg1, String arg2) {
			decorated.setExtension(arg0, arg1, arg2);
		}

		@Override
		public void setId(String arg0) {
			decorated.setId(arg0);

		}

		@Override
		public void setObject1(Object arg0) throws AlignmentException {
			decorated.setObject1(arg0);
		}

		@Override
		public void setObject2(Object arg0) throws AlignmentException {
			decorated.setObject2(arg0);
		}

		@Override
		public void setRelation(Relation arg0) {
			decorated.setRelation(arg0);
		}

		@Override
		public void setSemantics(String arg0) {
			decorated.setSemantics(arg0);
		}

		@Override
		public void setStrength(double arg0) {
			decorated.setStrength(arg0);
		}

		private QueryPatternTransformer getOuterType() {
			return QueryPatternTransformer.this;
		}

	}

}
