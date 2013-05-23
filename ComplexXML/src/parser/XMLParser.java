package parser;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ontology.Ontology;
import ontology.OntologyAlignment;

import org.semanticweb.owlapi.model.OWLEntity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pattern.Pattern;
import condition.Condition;
import condition.Condition.Type;
import condition.StringCondition;
import condition.StringCondition.StringType;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;

/**
 *
 * @auth Dominique Ritze
 *
 * Parses a XML-file used to define complex correspondence types with the help of a SAX-Parser.
 * Creates a Condition at the end which contains all information.
 *
 */
public class XMLParser extends DefaultHandler {


    //boolean variables are needed to check which tags are currently opened
    private static boolean define = false;
    private static boolean load = false;
    private static boolean domain = false;
    private static boolean range = false;
    private static boolean equivalent = false;
    private static boolean compatible = false;
    private static boolean subclass = false, superclass = false;
    private static boolean string = false;
    private static boolean datatypeRange = false;
    private static boolean category = false;
    //variables to check how many tags, especially all tags which can be nested like the string manipulations, are opened
    private int headnoun = 0, compHeadnoun = 0, firstPart = 0, compFirstPart = 0, label = 0, 
            name = 0, passive = 0, active = 0, wordclass = 0, empty = 0, verb = 0,
            modifier = 0;
    private static int numberOfOpenedOperandTags = 0;
    //variables to save additional information like free characters
    private String id = "";
    private String someString = "";
    private String datatype = "";

    private static ArrayList<String> conditionEntries = new ArrayList<String>();
    private static ArrayList<Set<Condition>> operandConditions = new ArrayList<Set<Condition>>();
    private static ArrayList<Set<StringCondition>> stringManipulations = new ArrayList<Set<StringCondition>>();
    private Condition finalCondition;
    private static ArrayList<StringCondition> finalStringManipulation = new ArrayList<StringCondition>();

    private double similarityValue = -1.0;
    private String file;
    private HashMap<String, Set<? extends OWLEntity>> ident = new HashMap<String, Set<? extends OWLEntity>>();
    private ArrayList<String> partsOfCorrespondence = new ArrayList<String>();
    private boolean selectAll = false;
    private boolean stringFirstMember = false;
    private boolean outpoutFormat = false;


    /**
     * Regard all the starting tags.
     */
    @Override
    public void startElement(String namespaceURI, String localName,
       String qName, Attributes atts) throws SAXException {

        if(qName.toLowerCase().equals("outputformat")) {
            outpoutFormat = true;
        }

        //in the define-tag, the ontologies are defined
        if(qName.toLowerCase().equals("define")) {
           define = true;
        }
        //get and set the first ontology path
        if(qName.toLowerCase().equals("first") && define) {
            try {
                try {
                    URI pathURI;
                    if(atts.getValue(0).equals("$source")) {
                        pathURI = URI.create(utility.Attributes.sourcePath);
                    }
                    else {
                        pathURI = URI.create(atts.getValue(0));
                    }
                        utility.Attributes.firstOntology = new Ontology(pathURI);
                } catch(IllegalArgumentException ie) {
                    if(atts.getValue(0).equals("$source")) {
                        utility.Attributes.firstOntology = new Ontology(utility.Attributes.sourcePath);
                    }
                    else {
                        utility.Attributes.firstOntology = new Ontology(atts.getValue(0));
                    }
                }
            } catch (ComplexMappingException e) {
                throw new RuntimeException(e);
            }
        }
        //get and set the second ontology path
        if(qName.toLowerCase().equals("second") && define) {
            try {
                try {
                    URI pathURI;
                    if(atts.getValue(0).equals("$target")) {
                        pathURI = URI.create(utility.Attributes.targetPath);
                    }
                    else {
                        pathURI = URI.create(atts.getValue(0));
                    }
                        utility.Attributes.secondOntology = new Ontology(pathURI);
                } catch(IllegalArgumentException ie) {
                    if(atts.getValue(0).equals("$target")) {
                        utility.Attributes.secondOntology = new Ontology(utility.Attributes.targetPath);
                    }
                    else {
                        utility.Attributes.secondOntology= new Ontology(atts.getValue(0));
                    }
                }
            } catch (ComplexMappingException e) {
                throw new RuntimeException(e);
            }
        }
        //get and set the alignment
        if(qName.toLowerCase().equals("alignment") && define) {
            try {
                URI pathURI = URI.create(atts.getValue(0));
                utility.Attributes.alignment = new OntologyAlignment(pathURI);
                try {
                    if(!utility.Attributes.alignment.isValid()) {
                        throw new ComplexMappingException(ExceptionType.INVALID_REF_ALIGNMENT,"");
                    }
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            } catch(IllegalArgumentException ie) {
                utility.Attributes.alignment = new OntologyAlignment(atts.getValue(0));
                try {
                    if(!utility.Attributes.alignment.isValid()) {
                        throw new ComplexMappingException(ExceptionType.INVALID_REF_ALIGNMENT,"");
                    }
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //in load the used variables are defined
        if(qName.toLowerCase().equals("load")) {
            load = true;
        }

        //for a concept with an id, the id with contemplable concepts are saved in a map
        if(qName.toLowerCase().equals("concept") && load) {
            if(atts.getValue(0).equals("first")) {
                ident.put(atts.getValue(1).toString(),utility.Attributes.firstOntology.getClasses());
            }
            else if(atts.getValue(0).equals("second")) {
                ident.put(atts.getValue(1).toString(),utility.Attributes.secondOntology.getClasses());
            }
            //check if this concept is part of the resulting correspondence or just an auxiliary concept to find a correspondence
            if(atts.getValue(2)!= null && atts.getValue(2).toString().equals("yes")) {
                partsOfCorrespondence.add(atts.getValue(1));
            }
        }

        //the same for object property with id
        if(qName.toLowerCase().equals("objectproperty") && load) {
            if(atts.getValue(0).equals("first")) {
                ident.put(atts.getValue(1).toString(),utility.Attributes.firstOntology.getObjectProperties());
            }
            else if(atts.getValue(0).equals("second")) {
                ident.put(atts.getValue(1).toString(),utility.Attributes.secondOntology.getObjectProperties());
            }
            if(atts.getValue(2)!= null && atts.getValue(2).toString().equals("yes")) {
                partsOfCorrespondence.add(atts.getValue(1));
            }
        }

        //same for datatype property
        if(qName.toLowerCase().equals("datatypeproperty") && load) {
            if(atts.getValue(0).equals("first")) {
                ident.put(atts.getValue(1).toString(),utility.Attributes.firstOntology.getDatatypeProperties());
            }
            else if(atts.getValue(0).equals("second")) {
                ident.put(atts.getValue(1).toString(),utility.Attributes.secondOntology.getDatatypeProperties());
            }
            if(atts.getValue(2)!= null && atts.getValue(2).toString().equals("yes")) {
                partsOfCorrespondence.add(atts.getValue(1));
            }
        }

        if(qName.toLowerCase().equals("select")) {
            if(atts.getValue(0).equals("all")) {
                selectAll = true;
            }
        }

        //the variable "and" is incremented to know how many and-tags are opened and
        //create a set for every and-tag which will contain the conditions
        if(qName.toLowerCase().equals("and")) {
            numberOfOpenedOperandTags++;
        }

        if(qName.toLowerCase().equals("or")) {
            numberOfOpenedOperandTags++;
        }

        if(qName.toLowerCase().equals("not")) {
            numberOfOpenedOperandTags++;
        }

        if(qName.toLowerCase().equals("and") || qName.toLowerCase().equals("or") || qName.toLowerCase().equals("not")) {
            operandConditions.add(new HashSet<Condition>());
        }

        if(qName.toLowerCase().equals("string")) {
            string = true;
        }
        if(qName.toLowerCase().equals("category")) {
            category = true;
        }
        if(qName.toLowerCase().equals("similarityabove")) {
            similarityValue = new Double(atts.getValue(0));
        }
        if(qName.toLowerCase().equals("label")) {
            label++;
            stringManipulations.add(new HashSet<StringCondition>());
        }
        if(qName.toLowerCase().equals("name")) {
            name++;
            stringManipulations.add(new HashSet<StringCondition>());
        }
        if(qName.toLowerCase().equals("entity")) {

            //save the corresponding entity-id
            if(domain || compatible || range) {
                conditionEntries.add(atts.getValue(0));
                return;
            }

            if(name != 0 || label != 0) {
                id = atts.getValue(0);
            }

            //if an entity tag occured and its not included in a name/label
            //or equivalent/subclass/superclass tag, create an artificifial
            //tag empty which will determine the string to use for an entity later
            if(name == 0 && label == 0 && getCurrentSum()>0) {
                id = atts.getValue(0);
                empty++;
                stringManipulations.add(new HashSet<StringCondition>());
            }
            if(equivalent || subclass || superclass) {
                conditionEntries.add(atts.getValue(0));
            }
            else if(name == 0 && label == 0 && getCurrentSum() == 0) {
                id = atts.getValue(0);
                empty++;
                stringManipulations.add(new HashSet<StringCondition>());
            }
        }

        if(qName.toLowerCase().equals("head")) {
            headnoun++;
            stringManipulations.add(new HashSet<StringCondition>());
        }

        if(qName.toLowerCase().equals("passive")) {
            passive++;
            stringManipulations.add(new HashSet<StringCondition>());
        }
        if(qName.toLowerCase().equals("wordclass")) {
            wordclass++;
            stringManipulations.add(new HashSet<StringCondition>());
        }
        if(qName.toLowerCase().equals("active")) {
            active++;
            stringManipulations.add(new HashSet<StringCondition>());
        }
        if(qName.toLowerCase().equals("modifier")) {
            modifier++;
            stringManipulations.add(new HashSet<StringCondition>());
        }
        if(qName.toLowerCase().equals("verb")) {
            verb++;
            stringManipulations.add(new HashSet<StringCondition>());
        }
        if(qName.toLowerCase().equals("complementhead")) {
            compHeadnoun++;
            stringManipulations.add(new HashSet<StringCondition>());
        }

        if(qName.toLowerCase().equals("firstpart")) {
            firstPart++;
            stringManipulations.add(new HashSet<StringCondition>());
        }

        if(qName.toLowerCase().equals("complementfirstpart")) {
            compFirstPart++;
            stringManipulations.add(new HashSet<StringCondition>());
        }

        if(qName.toLowerCase().equals("equivalent")) {
            equivalent = true;
        }

        if(qName.toLowerCase().equals("compatible")) {
            compatible = true;
        }

        if(qName.toLowerCase().equals("isdomainof")) {
            domain = true;
        }

        if(qName.toLowerCase().equals("israngeof")) {
            range = true;
        }

        if(qName.toLowerCase().equals("datatype")) {
            datatypeRange = true;
        }

        if(qName.toLowerCase().equals("issubclassof")) {
            subclass = true;
        }

        if(qName.toLowerCase().equals("issuperclassof")) {
            superclass = true;
        }
   }


    @SuppressWarnings("unchecked")
    /**
    * Regard all end-tags.
    */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) {


        if(qName.toLowerCase().equals("outputformat")) {
            outpoutFormat = false;
        }

        if(qName.toLowerCase().equals("define")) {
            define = false;
        }
        if(qName.toLowerCase().equals("load")) {
            load = false;
        }
        if(qName.toLowerCase().equals("similarityabove")) {

            //create a similarityAbove-Condition which contains the condition entries (label, entity,...) which
            //have been created before closing this tag
            if(!finalStringManipulation.isEmpty()) {
                operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.SIMILARITY, finalStringManipulation, ""+similarityValue, someString));
                finalStringManipulation = new ArrayList<StringCondition>();
                similarityValue = -1.0;
            }
            else{
                operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.SIMILARITY, (ArrayList<String>)conditionEntries.clone()));
                conditionEntries.clear();
                similarityValue = -1.0;
            }
        }

        //empty is not a real tag, but used if name/label not specified
        if(empty > 0) {
            empty--;
            computeStringManipulation(StringType.EMPTY);            
        }

        if(qName.toLowerCase().equals("label")) {
            label--;
            computeStringManipulation(StringType.LABEL);
        }
        if(qName.toLowerCase().equals("name")) {
            name--;
            computeStringManipulation(StringType.NAME);
        }

        if(qName.toLowerCase().equals("and")) {
            buildOperatorCondition(Type.AND);
        }

        if(qName.toLowerCase().equals("or")) {
            buildOperatorCondition(Type.OR);
        }

        if(qName.toLowerCase().equals("not")) {
            buildOperatorCondition(Type.NOT);
        }

        if(qName.toLowerCase().equals("head")) {
            headnoun--;
            computeStringManipulation(StringType.HEADNOUN);
        }
        if(qName.toLowerCase().equals("passive")) {
            passive--;
            computeStringManipulation(StringType.PASSIVE);
        }
        if(qName.toLowerCase().equals("active")) {
            active--;
            computeStringManipulation(StringType.ACTIVE);
        }
        if(qName.toLowerCase().equals("modifier")) {
            modifier--;
            computeStringManipulation(StringType.MODIFIER);
        }
        if(qName.toLowerCase().equals("verb")) {
            verb--;
            computeStringManipulation(StringType.VERB);
        }
        if(qName.toLowerCase().equals("wordclass")) {
            wordclass--;
            computeStringManipulation(StringType.WORD_CLASS);
        }
        if(qName.toLowerCase().equals("complementhead")) {
            compHeadnoun--;
            computeStringManipulation(StringType.COMP_HEADNOUN);
        }

        if(qName.toLowerCase().equals("firstpart")) {
            firstPart--;
            computeStringManipulation(StringType.FIRST_PART);
        }

        if(qName.toLowerCase().equals("complementfirstpart")) {
            compFirstPart--;
            computeStringManipulation(StringType.COMP_FIRST_PART);
        }

        if(qName.toLowerCase().equals("equal") || qName.toLowerCase().equals("synonyms") || qName.toLowerCase().equals("antonyms") ||
            qName.toLowerCase().equals("hypernym") || qName.toLowerCase().equals("hyponym") ||
            qName.toLowerCase().equals("nominalization")  || qName.toLowerCase().equals("iscategorizedas")) {
            if(!finalStringManipulation.isEmpty()) {
                if(qName.toLowerCase().equals("equal")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.EQUAL, finalStringManipulation, someString));
                }
                if(qName.toLowerCase().equals("synonyms")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.SYNONYM, finalStringManipulation, someString));
                }
                if(qName.toLowerCase().equals("antonyms")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.ANTONYMS, finalStringManipulation, someString));
                }
                if(qName.toLowerCase().equals("hypernym")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.HYPERNYM, finalStringManipulation, someString));
                }
                if(qName.toLowerCase().equals("hyponym")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.HYPONYM, finalStringManipulation, someString));
                }
                if(qName.toLowerCase().equals("nominalization")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.NOMINALIZATION, finalStringManipulation, someString));
                }
                if(qName.toLowerCase().equals("iscategorizedas")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.CATEGORY, finalStringManipulation, someString));
                }
                finalStringManipulation = new ArrayList<StringCondition>();
                someString = "";
            }
            else{
                if(qName.toLowerCase().equals("equal")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.EQUAL, (ArrayList<String>)conditionEntries.clone()));
                }
                if(qName.toLowerCase().equals("synonyms")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.SYNONYM, (ArrayList<String>)conditionEntries.clone()));
                }
                if(qName.toLowerCase().equals("antonyms")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.ANTONYMS, (ArrayList<String>)conditionEntries.clone()));
                }
                if(qName.toLowerCase().equals("hypernym")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.HYPERNYM, (ArrayList<String>)conditionEntries.clone()));
                }
                if(qName.toLowerCase().equals("hyponym")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.HYPONYM, (ArrayList<String>)conditionEntries.clone()));
                }
                if(qName.toLowerCase().equals("nominalization")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.NOMINALIZATION, (ArrayList<String>)conditionEntries.clone()));
                }
                if(qName.toLowerCase().equals("iscategorizedas")) {
                    operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.CATEGORY, (ArrayList<String>)conditionEntries.clone()));
                }
                conditionEntries.clear();
                someString = "";
             }
        }

        if(qName.toLowerCase().equals("equivalent")) {
            addCondition(Type.EQUIVALENT);
        }

        if(qName.toLowerCase().equals("compatible")) {
            addCondition(Type.COMPATIBLE);
        }

        if(qName.toLowerCase().equals("isdomainof")) {
            addCondition(Type.DOMAIN);
        }

        if(qName.toLowerCase().equals("israngeof")) {
            if(datatype.equals("")) {
                addCondition(Type.OBJECTRANGE);
            }
            else {
                operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(Type.DATATYPERANGE, (ArrayList<String>)conditionEntries.clone(), datatype));
                conditionEntries.clear();
                range = false;
                datatype = "";
            }
        }

        if(qName.toLowerCase().equals("datatype")) {
            datatypeRange = false;
        }
        if(qName.toLowerCase().equals("string")) {
            string = false;
        }
        if(qName.toLowerCase().equals("category")) {
            category = false;
        }
        if(qName.toLowerCase().equals("issubclassof")) {
            addCondition(Type.SUBCLASS);
        }

        if(qName.toLowerCase().equals("issuperclassof")) {
            addCondition(Type.SUPERCLASS);
        }

        if(qName.toLowerCase().equals("correspondence") ||
            qName.toLowerCase().equals("anchorcorrespondence")) {
            try {
                if(qName.toLowerCase().equals("correspondence")) {
                    //build the resulting pattern
                    Pattern p = new Pattern((Condition)finalCondition, new OutputFormat(new File(file)),
                        (HashMap<String, Set<? extends OWLEntity>>)ident.clone(),
                        (ArrayList<String>)partsOfCorrespondence.clone(), selectAll);
                    utility.Attributes.correspondencePattern.add(p);
                }
                //a correspondence anchor
                else {
                    Pattern p = new Pattern((Condition)finalCondition,
                        (HashMap<String, Set<? extends OWLEntity>>)ident.clone(),
                        (ArrayList<String>)partsOfCorrespondence.clone(), selectAll);
                    //first anchor
                    if(utility.Attributes.alignment == null) {
                        utility.Attributes.alignment = new OntologyAlignment(p);
                        System.out.println("WTF");
                    }
                    //another anchor
                    else {
                        utility.Attributes.alignment.joinAlignments(p);
                    }
                }

                clear();

            } catch (ComplexMappingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void clear() {
        //clear everything for a new pattern
        finalCondition = null;
        ident.clear();
        stringManipulations.clear();
        finalStringManipulation.clear();
        conditionEntries.clear();
        operandConditions.clear();
        selectAll = false;
        partsOfCorrespondence.clear();

        string = false;
        category = false;
        datatypeRange = false;
        id = "";
        someString = "";
        datatype = "";

        similarityValue = -1.0;
        stringFirstMember = false;

    }

    /**
     * Create a new string manipulation containing all necessary information.
     * String manipulations are: head, complement head, first part, complement first part, label, name.
     * Because of the possible arbitrary nesting, the string manipulation must also be added to the
     * overlying one.
     *
     * @param type
     */
    private void computeStringManipulation(StringType type) {
        //check if it is the last string manipulation, that means if this condition results in a string
        if(headnoun == 0 && compHeadnoun == 0 && firstPart == 0 && compFirstPart == 0 && label == 0 && name == 0
            && passive == 0 && active == 0 && wordclass == 0 && empty == 0 && verb == 0
            && modifier == 00)  {
            if(!id.isEmpty()) {
                finalStringManipulation.add(new StringCondition(type, id, stringFirstMember));
            }
            else {
                finalStringManipulation.add(new StringCondition(type, stringManipulations.get(0).iterator().next()));
                stringManipulations.clear();
            }
        }
        else {
            StringCondition currentManipulation;
            //check if an id (of entity) or a string is given
            if(!id.isEmpty() || !someString.isEmpty()) {
                //id given
                if(!id.isEmpty()) {
                    //create new string manipulation with the given type and the corresponding id
                    currentManipulation = new StringCondition(type, id, stringFirstMember);
                    id="";
                }
                //string given and not id
                else {
                    currentManipulation = new StringCondition(type, someString, stringFirstMember);
                    someString="";
                }
            }
            //if neither id or string is given, the content of the string manipulation is the string manipulation "below" (the embedded one)
            else {
                currentManipulation =
                new StringCondition(type, stringManipulations.get(getCurrentSum()).iterator().next());

            }
            //add the created string manipulation to the string manipulation in which the current one is embedded
            stringManipulations.get(getCurrentSum()-1).add(currentManipulation);
            stringFirstMember = false;
        }
        id="";
    }

    private int getCurrentSum() {
        return headnoun+compHeadnoun+firstPart+compFirstPart+name+label+passive
                +active+wordclass+empty+verb+modifier;
    }


    /**
     * Check the characters in tags.
     * At the moment only a string can occur as free characters.
     */
    @Override
    public void characters(char ch[], int start, int length) {
        String s = new String(ch,start,length).trim();
        if(!s.isEmpty() && !outpoutFormat) {
            if(category) {
                someString = s;
            }
            else if(string) {
                someString = s;
                stringFirstMember = true;
            } else if(datatypeRange) {
                datatype = s;
            }
            else {
                //free characters should only occur in string-tags
                throw new RuntimeException(new ComplexMappingException(ExceptionType.PARSING_EXCEPTION,
                    "Free characters found but not in string tag."));
            }
	}
    }

    /**
    * Parse the XML-file.
    *
    * @param filepath
    * @return
    */
    public void parseFile(String filepath) {
        file = filepath;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser;
            saxParser = factory.newSAXParser();
            saxParser.parse(new File(filepath).getAbsolutePath(), this);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
    * Add the current condition to the list of conditions.
    *
    * @param condition
    * @param conditionType
    */
    @SuppressWarnings("unchecked")
    private void addCondition(Type conditionType) {
        //set the boolean condition variable to false because the tag is closed.
        switch(conditionType) {
            case EQUIVALENT:
                equivalent = false;
                break;
            case COMPATIBLE:
                compatible = false;
                break;
            case DOMAIN:
                domain = false;
                break;
            case OBJECTRANGE:
                range = false;
                break;
            case SUBCLASS:
                subclass = false;
                break;
            case SUPERCLASS:
                superclass = false;
                break;
            }
        //add a new condition of the corresponding type to the list of conditions
        operandConditions.get(numberOfOpenedOperandTags-1).add(Condition.getInstance(conditionType, (ArrayList<String>)conditionEntries.clone()));
        conditionEntries.clear();
    }

    /**
    * Add the current operator condition (and, or, not) to the list of conditions.
    *
    * @param conditionType
    */
    private void buildOperatorCondition(Type conditionType) {
        //decrement the variable to indicate that a tag has been closed.
        numberOfOpenedOperandTags--;

        //If it was the last operation-tag (and, or) the last condition is reached.
        if(numberOfOpenedOperandTags == 0)  {
            finalCondition = Condition.getInstance(conditionType, operandConditions.get(0));
        }
        else {
            //create a condition which contains all conditions which are connected by this and-tag
            Condition currentCondition = Condition.getInstance(conditionType, operandConditions.get(numberOfOpenedOperandTags));
            operandConditions.remove(numberOfOpenedOperandTags);
            operandConditions.get(numberOfOpenedOperandTags-1).add(currentCondition);
        }
    }
}