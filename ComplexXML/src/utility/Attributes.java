package utility;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ontology.Ontology;
import ontology.OntologyAlignment;

import org.semanticweb.owlapi.model.OWLEntity;

import pattern.Pattern;
import reasoner.ReasonerOneOntology;
import reasoner.ReasonerTwoOntologies;

/**
 *
 * @author Dominique Ritze
 *
 * Class containing data which is used in many classes
 * and is necessary for every pattern.
 *
 */
public class Attributes {
	
	public static Ontology firstOntology;
	public static Ontology secondOntology;
	public static OntologyAlignment alignment;
		
	public static ArrayList<String> partsOfCorrespondence = new ArrayList<String>(); 
	
	public static ArrayList<Pattern> correspondencePattern = new ArrayList<Pattern>();
	
	public static ReasonerOneOntology reasonerFirst; 
	public static ReasonerOneOntology reasonerSecond;
	public static ReasonerTwoOntologies reasonerBoth;
	
	//sets used for similarity computation
	public static Set<String> classNamesFirst;
	public static Set<String> classNamesSecond;
	public static Set<String> propertyNamesFirst;
	public static Set<String> propertyNamesSecond;

        public static File xmlFile;
        public static File outputFile;
        public static String sourcePath;
        public static String targetPath;

        /**
         * Reset the whole data, for example if a XML file
         * has been entered but an error occured while creating
         * the alignment.
         */
	public static void reset() {
            firstOntology = null;
            secondOntology = null;
            alignment = null;
            partsOfCorrespondence = new ArrayList<String>();
            correspondencePattern = new ArrayList<Pattern>();
            reasonerFirst = null;
            reasonerSecond = null;
            reasonerBoth = null;
            classNamesFirst = null;
            classNamesSecond = null;
            propertyNamesFirst = null;
            propertyNamesSecond = null;

        }

        public static void initializeNames() {
            Attributes.classNamesFirst = new HashSet<String>();
            Attributes.classNamesSecond = new HashSet<String>();
            Attributes.propertyNamesFirst = new HashSet<String>();
            Attributes.propertyNamesSecond = new HashSet<String>();


            for(OWLEntity e : Attributes.firstOntology.getClasses()) {
                    Attributes.classNamesFirst.add(e.getIRI().getFragment());
            }
            for(OWLEntity e : Attributes.secondOntology.getClasses()) {
                    Attributes.classNamesSecond.add(e.getIRI().getFragment());
            }
            for(OWLEntity e : Attributes.firstOntology.getObjectProperties()) {
                    Attributes.propertyNamesFirst.add(e.getIRI().getFragment());
            }
            for(OWLEntity e : Attributes.secondOntology.getObjectProperties()) {
                    Attributes.propertyNamesSecond.add(e.getIRI().getFragment());
            }
            for(OWLEntity e : Attributes.firstOntology.getDatatypeProperties()) {
                    Attributes.propertyNamesFirst.add(e.getIRI().getFragment());
            }
            for(OWLEntity e : Attributes.secondOntology.getDatatypeProperties()) {
                    Attributes.propertyNamesSecond.add(e.getIRI().getFragment());
            }
        }
}
