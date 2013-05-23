package ontology;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.unima.ki.mmatch.Setting;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;

/**
 * 
 * @author Dominique Ritze
 * 
 * The class Ontology contains the classes, properties and some ontology characteristics like the filepath.  
 *
 */
public class Ontology {
	
	private boolean[] delimiter = new boolean[3];
	private Set<OWLEntity> entities = new HashSet<OWLEntity>();
	private Set<OWLClass> classes = new HashSet<OWLClass>();
	private Set<OWLObjectProperty> objectProperties = new HashSet<OWLObjectProperty>();
	private Set<OWLDataProperty> datatypeProperties = new HashSet<OWLDataProperty>();
	private OWLOntology ontology;
	private URI path;
        private boolean taggerLoaded = false;

        /**
         * Constructor for an ontology represented by an URI.
         *
         * @param path
         * @throws ComplexMappingException
         */
	public Ontology(URI path) throws ComplexMappingException {
		this.path = path;
		createOntology();
	}

        /**
         * Constructor for an ontology represented by a local filepath.
         *
         * @param filepath
         * @throws ComplexMappingException
         */
	public Ontology(String filepath) throws ComplexMappingException {
		File file = new File(filepath);
		this.path = file.toURI();
		createOntology();
	}
	
	/**
	 * Create an ontology out of the filepath and build all important structures
         * (like set of classes or properties).
	 * 
	 * @param filepath
	 * @throws ComplexMappingException 
	 */
	public void createOntology() throws ComplexMappingException {
		
		this.ontology = buildOWLOntology();		
		//add all classes except Thing and Nothing to the set classes
		for(OWLEntity e : ontology.getClassesInSignature(true)) {
			if(e.getIRI().getFragment() != null && (e.getIRI().getFragment().equals("Thing") || e.getIRI().getFragment().equals("Nothing"))) {
				continue;				
			} 
			classes.add((OWLClass) e);
		}
		
		this.entities = ontology.getSignature(true);
		this.objectProperties = ontology.getObjectPropertiesInSignature(true);
		this.datatypeProperties = ontology.getDataPropertiesInSignature(true);
		this.delimiter = getDelimiter(ontology);
		
	}
		
	/**
	 * Compute the delimiter which occur in the ontology.
	 * 
	 * @param ontology
	 * @return Array with the real delimiter, first entry: hyphen, second: blank, third: underscore. The entry is true if the
	 * character should be a real delimiter.
	 */
		public static boolean[] getDelimiter(OWLOntology ontology) {
		int countHyphen = 0;
		int countUnderscore = 0;
		int countBlank = 0;	
		int size = ontology.getClassesInSignature().size();
		
		/*
		 * The following three loops check which delimiters are used in the ontology. 
		 * If occurence delimiter devided by number of all delimiters is less than 0.05
		 * the delimiter is not considered as a real delimiter.
		 */
		//iterate through all classes of the ontology and count the hypens, the blanks and the underscores
		for(OWLClass cls : ontology.getClassesInSignature(true)) {
			if(cls.getIRI().toString().contains("-")) {
				countHyphen++;
			}
			if(cls.getIRI().toString().contains(" ")) {
				countBlank++;
			}
			if(cls.getIRI().toString().contains("_")) {
				countUnderscore++;
			}
		}
        	
		//iterate through all data properties and count hypens, blanks, underscores
		for(OWLDataProperty dataProp : ontology.getDataPropertiesInSignature()) {
			if(dataProp.getIRI().toString().contains("-")) {
				countHyphen++;
			}
			if(dataProp.getIRI().toString().contains(" ")) {
				countBlank++;
			}
			if(dataProp.getIRI().toString().contains("_")) {
				countUnderscore++;
			}
		}
        	
		//iterate through all object properties and count hypens, blanks, underscores
		for(OWLObjectProperty objectProp : ontology.getObjectPropertiesInSignature()) {  
			if(objectProp.getIRI().toString().contains("-")) {
				countHyphen++;
			}
			if(objectProp.getIRI().toString().contains(" ")) {
				countBlank++;
			}
			if(objectProp.getIRI().toString().contains("_")) {
				countUnderscore++;
			} 
		}
		boolean[] delimiter = new boolean[]{false, false, false};
		if((double)countHyphen/(double)size > Setting.getMinimumOccurenceSeparator()) {
			delimiter[0] = true;
		}
		if((double)countBlank/(double)size > Setting.getMinimumOccurenceSeparator()) {
			delimiter[1] = true;
		}
		if((double)countUnderscore/(double)size > Setting.getMinimumOccurenceSeparator()) {
			delimiter[2] = true; 
		} 
		return delimiter;
	}  
	
	/**
	 * Build an OWLOntology out of a filepath with the help of
         * the OWL API methods.
	 * 
	 * @param filepath
	 * @return
	 * @throws ComplexMappingException 
	 */
	public OWLOntology buildOWLOntology() throws ComplexMappingException {
		// create the OWLOntologies out of the path
		OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
		try {
			// return manager1.loadOntologyFromPhysicalURI(path);
			return manager1.loadOntology(IRI.create(path));
		} catch (OWLOntologyCreationException e) {
			throw new ComplexMappingException(ExceptionType.CREATION_EXCEPTION,
					"Could not create an OWLOntology in Ontology.", e);
		}
	}

        /**
         * An array containing the delimiter (index 0:hyphen, 1: blank, 2:underscore).
         *
         * @return
         */
	public boolean[] getDelimiter() {
		return delimiter;
	}

        /**
         * Get all entities of the ontology.
         *
         * @return
         */
	public Set<OWLEntity> getEntities() {
		return entities;
	}

        /**
         * Get all classes of the ontology.
         *
         * @return
         */
	public Set<OWLClass> getClasses() {
		return classes;
	}

        /**
         * Get all object properties of the ontology.
         *
         * @return
         */
	public Set<OWLObjectProperty> getObjectProperties() {
		return objectProperties;
	}

        /**
         * Get all datatype properties of the ontology.
         *
         * @return
         */
	public Set<OWLDataProperty> getDatatypeProperties() {
		return datatypeProperties;
	}

        /**
         * Get the ontology itself as OWLOntology.
         *
         * @return
         */
	public OWLOntology getOntology() {
		return ontology;
	}

        /**
         * Get the path of the ontology as URI.
         *
         * @return
         */
	public URI getFilepath() {
		return path;
	}

        /**
         * Get the names of all entities in the ontology.
         * Used to set the context for wordnet.
         *
         * @return
         */
        public Set<String> getEntityNames() {
            Set<String> names = new HashSet<String>();
            for(OWLEntity e : this.getEntities()) {
                names.add(e.getIRI().getFragment());
            }
            return names;
        }

        public void executeTagger(String ontologyPath) throws ComplexMappingException  {
            taggerLoaded = true;
            List<String> resultingTerm = new ArrayList<String>();
            resultingTerm.add("java");
            resultingTerm.add("-jar");
            resultingTerm.add("-Xms1500m");
            resultingTerm.add("-Xmx1500m");
            resultingTerm.add("Tagger.jar");
            resultingTerm.add("\""+ontologyPath+"\"");
            File directory = new File("tagger/Tagger.jar");
            ProcessBuilder p = new ProcessBuilder(resultingTerm);
            p.redirectErrorStream(true);
            p.directory(new File(directory.getParent()));
            try {
                final Process x = p.start();

                //scan whatever the matcher outputs on the command line
                //if any error occurs, this can be discovered by the output
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                public void run() {
                    Scanner scanner = new Scanner(x.getInputStream());
                        while (scanner.hasNextLine()) {
                            System.out.println(scanner.nextLine());
                        }
                        scanner.close();
                    }
                });

                //process has been terminated
                x.waitFor();
                executorService.shutdown();
            } catch(Exception e) {
            throw new ComplexMappingException(ExceptionType.TAGGER_EXCEPTION, "Cannot create semantic relation file.", e);
        }
    }

    /**
     * @return the taggerLoaded
     */
    public boolean isTaggerLoaded() {
        return taggerLoaded;
    }
}