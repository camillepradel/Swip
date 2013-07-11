package org.swip.addPredicates;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.log4j.*;

import java.io.*;
import java.util.regex.*;
import java.util.Scanner;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;

/**
 * AddPredicates is used in order to add missing predicates in a given knowledge base.
 * <p>
 * The first execution of this program will generate a configuration file in which
 * you can configure its behavior.
 * </p><p>
 * Two directories are made: one where you place knowledge base files, and an other containing the
 * generated files.
 * </p>
 * 
 * @author Guillaume PEYET
 */
public class AddPredicates {
	/**
	 * Read the configuration file at instancing and keep it in memory.
	 */
	private static Conf config;
	
	/**
	 * Used to log messages in console.
	 */
	private static final Logger logger = Logger.getLogger(AddPredicates.class);

	/**
	 * Isolates resources which have not the searched predicate (defined in the configuration file).
	 * 
	 * @param model The ontology model in which that kind of resources are
	 * 				searched (instance of OntModel).
	 * @return An ArrayList of isolated Resources.
	 */
	public static ArrayList<Resource> getSearchedResources(OntModel model){
		/* First, it makes a list of all resources which have
		 * the searched predicate.
		 */
		ArrayList<RDFNode> labeledResources = new ArrayList<RDFNode>();
		ResIterator iterLR = model.listSubjectsWithProperty(model.getProperty("http://www.w3.org/2000/01/rdf-schema#label"));
		while (iterLR.hasNext()) {
			labeledResources.add(iterLR.nextResource());
		}
		
		/* Then it browses all statements in model and for each one,
		 * it determines if the subject, the predicate and the object are
		 * in the list created above.
		 * If they are not in this list, the resource concerned has not the
		 * searched predicate ; so we add it in the returned list.
		 */
		ArrayList<Resource> unLabeledResources = new ArrayList<Resource>();
		
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			
			Resource subject = stmt.getSubject();
			Property predicate = stmt.getPredicate();
			RDFNode object = stmt.getObject();
			
			// select subjects which any searched predicate is defined.
			if (!labeledResources.contains(subject)
					&& !unLabeledResources.contains(subject)
					&& !config.getIgnoredPrefixes().contains(subject.getNameSpace())) {
				unLabeledResources.add(subject);
			}
			
			// select predicates which any searched predicate is define.
			if (!labeledResources.contains(predicate)
					&& !unLabeledResources.contains(predicate)
					&& !config.getIgnoredPrefixes().contains(predicate.getNameSpace())) {
				unLabeledResources.add(predicate);
			}
			
			// select objects resources which any searched predicate is define.
			if (object instanceof Resource
					&& !labeledResources.contains(object)
					&& !unLabeledResources.contains(object)
					&& !config.getIgnoredPrefixes().contains(((Resource)object).getNameSpace())) {
				unLabeledResources.add((Resource)object);
			}
		}
		
		return unLabeledResources; // return selected resources
	}
	
	/**
	 * Extracts the part of the uri <code>URIstring</code> which is after the last sharp or slash character.
	 * 
	 * @param URIstring The string representing the URI of a resource.
	 * @return The local name of <code>URIstring</code>.
	 */
	public static String getLocalName(String URIstring) {
		URI uri = null;
		String name = null;
		try {
			// transform the string in an instance of URI
			uri = new URI(URIstring);
			
			// get the name of the resource.
			name = uri.getFragment();
			if (name == null) {
				name = uri.getPath();
				String[] name_splited = name.split("/");
				name = name_splited[name_splited.length - 1];
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * Separates by a space the different words in the string <code>str</code> according to
	 * the CamelCase standard and replaces all underscores by spaces.
	 * 
	 * @param str The string splited.
	 * @return A new string containing the result of splitting <code>str</code>,
	 * 		   or null if <code>str</code> is empty.
	 */
	public static String splitCases(String str) {
		if (str == null || str == "")
			return null;
		else {
			// separates with CamelCase regex
			String label = str.replaceAll(
				      String.format("%s|%s|%s",
				         "(?<=[A-Z])(?=[A-Z][a-z])",
				         "(?<=[^A-Z_])(?=[A-Z])",
				         "_|\\.",
				         "(?<=[A-Za-z])(?=[^A-Za-z])"
				         
				      ),
				      " "
				   );
			
			/* puts the first letter of each words extract from the string
			 * in lower case, only if the first letter of the initial string
			 * is in lower case. 
			 */
			if (Character.isLowerCase(label.charAt(0))) {
				String[] label_splited = label.split(" ");
				int nbrWords = label_splited.length;
				int i = 0;
				label = "";
				
				for (String mot : label_splited) {
					i++;
					
					if (mot.length() >= 2
							&& Character.isUpperCase(mot.charAt(0))
							&& Character.isLowerCase(mot.charAt(1))) {
						label += Character.toLowerCase(mot.charAt(0));
						label += mot.substring(1);
					} else {
						label += mot;
					}
					
					if (i < nbrWords)
						label += " ";
				}
			}
			
			return label;
		}
	}

	/**
	 * Creates a label based on parsing given URI.
	 * 
	 * @param URIstring The string from which is extracted the resource's label.
	 * @return The label generated.
	 */
	public static String labelFromURI (String URIstring) {
		// Extract the name of a resource from its URI
		String name = getLocalName(URIstring);
		
		// separates by a space the different words in the resource's name.
		String label = splitCases(name);
		
		return label;
	}
	
	/**
	 * Creates a new model which defines a predicate, with its value,
	 * for each unlabeled resource of model.
	 * <ul><li>
	 * If you are running this program in AUTO mode, the value of each predicate
	 * added are generated from the resource's URI with the function <code>labelFromURI()</code>.
	 * </li><li>
	 * If you are running this program in SEMIAUTO mode, the value of each predicate
	 * added are generated in the same way as in AUTO mode, but the program will ask you
	 * if you want to keep this default value (by pressing enter key) or if you want to
	 * enter a new value manually.
	 * </li><li>
	 * If you are running this program in MAN mode, you must enter manually the value
	 * of each predicate added.
	 * </li></ul>
	 * <p>
	 * When you are in SEMIAUTO and MAN mode, you can enter 0 instead of the predicate's
	 * value in order to impede the definition of the predicate for the resource concerned.
	 * </p>
	 * 
	 * @param model The ontology model in which the program executes its searches.
	 * @return The new model generated.
	 */
	public static OntModel createPredicates(OntModel model) {
		// First, it lists any resources which has not searched predicate.
		ArrayList<Resource> listResources = getSearchedResources(model);
		
		/* Then we construct a new model describing the new predicates of each
		 * resources listed of the initial model.
		 */
		OntModel labels = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
		
		String mode = config.getMode();
		
		for (Resource resource : listResources) {
			String label = "";
			boolean add = true;
			
			if (mode.equals("AUTO")) {
				label = labelFromURI(resource.getURI());// generate a label with the resource's URI
			} else if (mode.equals("SEMIAUTO")) {
				Scanner scan = new Scanner(System.in);
				label = labelFromURI(resource.getURI());
				System.out.println("\nRessource's URI: " + resource.getURI());
				System.out.println("Name extracted: " + label);
				System.out.println("Predicate added : " + config.getAddedPredicate());
				System.out.print("Enter final name (press enter to keep the default name, 0 in order not to add it): ");
				String in = scan.nextLine();
				if (!in.equals("")) {
					label = in;
				} else if (in.equals("0")) {
					add = false;
				}
			} else if (mode.equals("MAN")) {
				Scanner scan = new Scanner(System.in);
				System.out.println("\nRessource's URI: " + resource.getURI());
				System.out.println("Predicate added : " + config.getAddedPredicate());
				System.out.print("Enter property's value (0 in order not to add it): ");
				label = scan.nextLine();
				if (label.equals("0")) {
					add = false;
				}
			}
			
			if (add) {
				Resource res = labels.createResource(resource.getURI());
				Property prop = labels.createProperty(config.getAddedPredicate());
				if (!config.getLanguageTag().equals("null")) {
					Literal lit = labels.createLiteral(label, config.getLanguageTag());
					labels.addLiteral(res, prop, lit);
				} else {
					labels.add(res, prop, label);
				}
			}
		}
		
		return labels;
	}
	
	/**
	 * Executes the program.
	 * @param args This application does not need parameters : they are not taken into account.
	 */
	public static void main(String[] args) {
		// simple configuration that logs on the console.
		BasicConfigurator.configure();
		
		config = new Conf();
		logger.info("Configuration file loaded.");
		// for each files in the KB_PATH
		int countFiles = 0;
		File currentFile = null;
		File folder = new File(config.getKbPath());
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if (listOfFiles[i].isFile()) 
			{
				countFiles++;
				currentFile = listOfFiles[i];
				String currentName = currentFile.getName();
				String currentPath = currentFile.getPath();
				
				logger.info("File processing of " + currentName + "...");
				
				// determine the language used in the RDF serialization file
				String extention = null;
				String lang = null;
				
				Pattern pattern = Pattern.compile(".+\\.([a-z]+)$");
		        Matcher matcher = pattern.matcher(currentFile.getName());
		        if(matcher.matches()) {
		            extention = matcher.group(1);
		            
			        if (extention.toLowerCase().equals("owl"))
			        	lang = "RDF/XML";
			        else if (extention.toLowerCase().equals("nt"))
			        	lang = "N-TRIPLE";
			        else if (extention.toLowerCase().equals("ttl"))
			        	lang = "TURTLE";
		        } else {
		        	logger.error(currentName + " :: Impossible to determine the language.");
		        }
		        
		        if (lang != null) {
		        	logger.info(currentName + " :: written in " + lang + ".");
		        	
		        	// Read the file(s) to construct the ontolgy model.
		        	OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
		    		InputStream in = null;
		    		
		    		try {
		    			in = new FileInputStream(currentPath);
		    		} catch (FileNotFoundException e) {
		    			e.printStackTrace();
		    		}
		    		
		    		if (in == null) {
		    			logger.error("File : " + currentPath + " not found.");
		    		}
		    		
		    		model.read(in, null, lang);
		    		logger.info(currentName + " :: Ontology Model constructed.");
		    		
		    		// generate the ontology model that describes the new predicates
		    		OntModel labels = createPredicates(model);
		    		logger.info(currentName + " :: predicates '" + getLocalName(config.getAddedPredicate()) + "' added.");
		    		
		    		// serialize this model into the file at path GENERATED_FILE_PATH
		    		java.io.File swipLabelsFile = new java.io.File(config.getGeneratedPath() + getLocalName(config.getAddedPredicate()) + "_" + currentFile.getName());
		    		java.io.FileOutputStream swipLabelsStream = null;
		    		try {
		    			swipLabelsFile.createNewFile();
		    			swipLabelsStream = new java.io.FileOutputStream(swipLabelsFile);
		    			labels.write(swipLabelsStream);
		    			swipLabelsStream.close();
		    		} catch (FileNotFoundException e) { 
		    			e.printStackTrace();
		    		} catch (IOException e) { 
		    			e.printStackTrace();
		    		}
		    		logger.info(currentName + " :: processed.");
		        } else {
		        	logger.error("The language of the file " + currentFile.getName() + " is not supported...");
		        }
			}
		}
		
		if (countFiles == 0)
			logger.info("There is no file to process in the directory '" + config.getKbPath() + "'...");
		else
			logger.info("All files processed (results in '" + config.getGeneratedPath() + "' directory).");
	}
}
