package pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owl.model.OWLEntity;
import parser.OutputFormat;
import condition.Condition;
import condition.Condition.Type;
import condition.StringCondition;
import condition.StringCondition.StringType;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.Setting;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;
import functions.stringComparison.DatatypeRange;
import functions.entityComparison.Equivalent;
import functions.Function;
import functions.entityComparison.SubclassOf;
import functions.entityComparison.SuperclassOf;
import functions.stringComparison.StringComparison;
import functions.stringComparison.Compatible;
import functions.stringComparison.Contained;
import functions.stringComparison.Equal;
import functions.stringComparison.Similarity;
import functions.entityIdentifier.Label;
import functions.entityIdentifier.Name;
import functions.entityComparison.Domain;
import functions.entityComparison.EntityComparison;
import functions.entityComparison.ObjectRange;
import functions.entityIdentifier.Empty;
import functions.stringComparison.Antonym;
import functions.stringComparison.Category;
import functions.stringComparison.Hypernym;
import functions.stringComparison.Hyponym;
import functions.stringComparison.Nominalization;
import functions.stringManipulation.WordClass;
import functions.stringComparison.Synonyms;
import functions.stringManipulation.Active;
import functions.stringManipulation.ComplementFirstPart;
import functions.stringManipulation.ComplementHeadNoun;
import functions.stringManipulation.FirstPart;
import functions.stringManipulation.Head;
import functions.stringManipulation.Modifier;
import functions.stringManipulation.Passive;
import functions.stringManipulation.StringManipulation;
import functions.stringManipulation.Verb;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import operators.And;
import operators.Not;
import operators.Operator;
import operators.Or;
import org.semanticweb.owl.model.OWLDataProperty;
import utility.Tuple;

/**
 *
 * @author Dominique Ritze
 *
 * This class represents a complex correspondence pattern which contains
 * all information about the pattern like the conditions, the output format
 * and also the properties, e.g. 1:1 mapping or n:m.
 * Of course all the data can be achieved from other classes which need
 * the information.
 *
 */
public class Pattern {

	private Condition c;
	private OutputFormat of;
        private boolean selectAll = false;
        //assignment entity-id to the possible entities, for example a concept with id 1a out of ontology 1 is defined in
	//the xml-file, this set contains at key 1a all concepts of ontology 1
	private Map<String, Set<? extends OWLEntity>> ident = new HashMap<String, Set<? extends OWLEntity>>();

	//assignment entity-id to its position in the tuple
	private Map<String, Integer> assignment = new HashMap<String, Integer>();
        
        private ArrayList<String> partsOfCorrespondence = new ArrayList<String>();

        private ArrayList<ComplexCorrespondence> correspondences = new ArrayList<ComplexCorrespondence>();

        /**
         * 
         * @param c
         * @param of
         * @param ident
         * @param partsOfCorrespondence
         * @param selectAll
         */
        public Pattern(Condition c, OutputFormat of, Map<String, Set<? extends OWLEntity>> ident,
                ArrayList<String> partsOfCorrespondence, boolean selectAll) {
		this.c = c;
		this.of = of;
		this.ident = ident;
		this.partsOfCorrespondence = partsOfCorrespondence;
                this.selectAll = selectAll;
		createIdentifier();
	}

        /**
         *
         * For correspondence anchor.
         *
         * @param c
         * @param of
         * @param ident
         * @param partsOfCorrespondence
         * @param selectAll
         */
        public Pattern(Condition c, Map<String, Set<? extends OWLEntity>> ident,
                ArrayList<String> partsOfCorrespondence, boolean selectAll) {
		this.c = c;
		this.ident = ident;
		this.partsOfCorrespondence = partsOfCorrespondence;
                this.selectAll = selectAll;
		createIdentifier();
	}

        /**
         * Create the assignment identifier to the position in the tuple.
         */
	private void createIdentifier() {
		int j=0;
		//assign variables to their position in the tuple
		for(String identifier : this.ident.keySet()) {
			this.assignment.put(identifier, j);
			j++;
		}
	}

        /**
         *
         * @return True if all correspondence should be in the alignment
         * and not a 1:1 mapping is asked for.
         */
        public boolean getSelectAll() {
            return selectAll;
        }

        /**
         *
         * @return The assignment identifier to position in the tuples.
         */
	public Map<String, Integer> getAssignment() {
		return assignment;
	}

        /**
         *
         * @return A map which assigns the identifier to a set of
         * entities which are possible entities. For exmaple if an identifier
         * represents a concept of the first ontology, the set according to
         * this identifier contains all concepts of the first ontology.
         */
	public Map<String, Set<? extends OWLEntity>> getIdent() {
		return ident;
	}

        /**
         *
         * @return A list of identifiers which have been specified
         * as parts of the correspondence.
         */
	public ArrayList<String> getPartsOfCorrespondence() {
		return partsOfCorrespondence;
	}

        /**
         *
         * @return The upper condition of the pattern, containing further conditons.
         */
	public Condition getCondition() {
		return c;
	}

        /**
         *
         * @return The output format of the pattern.
         */
	public OutputFormat getOutputFormat() {
		return of;
	}

        /**
         * Create the correspondences which satisfy this pattern and
         * write them into the output file.
         *
         * @throws ComplexMappingException
         */
        public void computeCorrespondences() throws ComplexMappingException {            
            //create the tuples which comply all conditions
            ArrayList<Tuple> a = buildTuples(this.getCondition(), new ArrayList<Tuple>());

            //build correspondence of tuple, whereby correspondence only contains defined entities and not every one
            
            for(Tuple t : a) {
                ComplexCorrespondence con = new ComplexCorrespondence(this, t);
                if(!correspondences.contains(con)) {
                    correspondences.add(con);
                }
            }
	}

        /**
         *
         * @throws ComplexMappingException
         */
        public void writeOutput() throws ComplexMappingException {
            OutputWriter writer = new OutputWriter();
            writer.writeOutputfile(correspondences);
        }

        /**
         * 
         * @return
         */
        public ArrayList<ComplexCorrespondence> getCorrespondences() {
            return this.correspondences;
        }

	/**
	 *
	 * @param c
	 * @param possTuples
	 * @return
	 * @throws MMatchException
	 */
    	private ArrayList<Tuple> buildTuples(Condition c, ArrayList<Tuple> possTuples) throws ComplexMappingException {

		//check which condition-type the condition has
		switch(c.getType()) {

		case AND: {
			return joinTuples(c, possTuples, new And());
		}
		case OR: {
			return joinTuples(c, possTuples, new Or());
		}
		case NOT: {
			return joinTuples(c, possTuples, new Not());
		}
		case EQUAL: {
			return checkCondition(possTuples, c, new Equal());
		}
                case WORD_CLASS: {
			return checkCondition(possTuples, c, new WordClass());
		}
		case CONTAINED: {                    
			return checkCondition(possTuples, c, new Contained());
		}
		case SIMILARITY: {
			//first set the similarity value and delete it of the content list
			double simValue = Double.valueOf(c.getContent().get(0));
			ArrayList<String> newContent = new ArrayList<String>();
			for(int i=1; i<c.getContent().size(); i++) {
				newContent.add(c.getContent().get(i));
			}
                        try {
                            Setting.load();
                        } catch(MMatchException me) {
                            throw new ComplexMappingException(ExceptionType.MMATCH_EXCEPTION, "Could not" +
                                    "instantiate Setting files", me);
                        }

			c.setContent(newContent);
			return checkCondition(possTuples, c, new Similarity(simValue));
		}

		//not, compatible, domain, range

		case EQUIVALENT: {
			return checkCondition(possTuples, c, new Equivalent());
		}
		case SUBCLASS: {
			return checkCondition(possTuples, c, new SubclassOf());
		}
		case SUPERCLASS: {
			return checkCondition(possTuples, c, new SuperclassOf());
		}

		case COMPATIBLE: {
			return checkCondition(possTuples, c, new Compatible());
		}
		case OBJECTRANGE: {
			return checkCondition(possTuples, c, new ObjectRange());
		}

                case DATATYPERANGE: {
                    return checkCondition(possTuples, c, new DatatypeRange());
                }

		case DOMAIN: {
			return checkCondition(possTuples, c, new Domain());
		}
                case SYNONYM: {
                    return checkCondition(possTuples, c, new Synonyms());
                }
                case ANTONYMS: {
                    return checkCondition(possTuples, c, new Antonym());
                }
                case HYPERNYM: {
                    return checkCondition(possTuples, c, new Hypernym());
                }
                case HYPONYM: {
                    return checkCondition(possTuples, c, new Hyponym());
                }
                case NOMINALIZATION: {
                    return checkCondition(possTuples, c, new Nominalization());
                }
                case CATEGORY: {
                    return checkCondition(possTuples, c, new Category());
                }
		default: {
			throw new ComplexMappingException(ExceptionType.BAD_PARAMETER, "Unknown condition.");
		}

		}
	}

        /**
	 *
         * Join an amount of tuples according to the boolean operator.
	 *
	 * @param c
	 * @param existingTuples
	 * @param o
	 * @return
	 * @throws MMatchException
	 * @throws ComplexMappingException
	 */
	private ArrayList<Tuple> joinTuples(Condition c, ArrayList<Tuple> existingTuples, Operator o)throws ComplexMappingException {
		ArrayList<ArrayList<Tuple>> tuplesToJoin = new ArrayList<ArrayList<Tuple>>();
		for(Condition co : c.getConditions()) {
                    tuplesToJoin.add(buildTuples(co, existingTuples));
		}

                //create a comparator which is used to compare the
                //arraylists by their size in order to first join
                //the smallest lists
                Comparator comp = new Comparator() {

                    public int compare(Object o1, Object o2) {
                        if(o1 instanceof ArrayList && o2 instanceof ArrayList) {
                            return ((ArrayList)o1).size()-((ArrayList)o2).size();
                        }
                        else {
                            throw new RuntimeException(new ComplexMappingException(ExceptionType.BAD_PARAMETER,
                                    "Need two lists to compare their sizes."));
                        }
                    }
                };

                //sort the lists
                Collections.sort(tuplesToJoin, comp);

		return o.joinTuples(this, tuplesToJoin);
	}


	/**
	 * Get the underlying strings of a string condition.
	 * For example string condition: headnoun of entity id=2
	 * Then this method returns a headnoun of entity id=2 as string.
	 *
	 * @param m
	 * @param sm
	 * @param e
	 * @return
	 * @throws ComplexMappingException
	 */
	private String computeStringManipulation(StringCondition m, StringManipulation sm, OWLEntity e) throws ComplexMappingException {
		if(m.getManipulations() != null) {
			//get the underlying string manipulation
                        switch(m.getManipulations().getType()) {
                            case HEADNOUN:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new Head(), e));
                            case COMP_HEADNOUN:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new ComplementHeadNoun(), e));
                            case FIRST_PART:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new FirstPart(), e));
                            case COMP_FIRST_PART:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new ComplementFirstPart(), e));
                            case PASSIVE:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new Passive(), e));
                            case ACTIVE:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new Active(), e));
                            case MODIFIER:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new Modifier(), e));
                            case VERB:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new Verb(), e));
                            case WORD_CLASS:
                                return sm.compute(computeStringManipulation(m.getManipulations(), new WordClass(), e));
                            case NAME:
                                return sm.compute(new Name().compute(e));
                            case LABEL:
                                return sm.compute(new Label().compute(e));
                            case EMPTY:
                                return sm.compute(new Empty().compute(e));
                            default:
                                throw new ComplexMappingException(ExceptionType.BAD_PARAMETER,
                                        "Bad string manipulation.");
                        }
		}
		//if only name or label is required
		else {
			if(m.getType() == StringType.NAME) {
				return new Name().compute(e);
			}
			else if(m.getType() == StringType.LABEL) {
				return new Label().compute(e);
			}
                        else {
                            return new Empty().compute(e);
                        }
		}
	}


	/**
	 *
	 * @param existingTuples
	 * @param c
	 * @param f
	 * @return
	 * @throws ComplexMappingException
	 */
	private ArrayList<Tuple> checkCondition(ArrayList<Tuple> existingTuples, Condition c, Function f) throws ComplexMappingException{

            ArrayList<Tuple> possibleTuples = new ArrayList<Tuple>();
            boolean ontologyFunction = false;

            if(f instanceof EntityComparison) {
                ontologyFunction = true;
            }

            //if no tuple has been created before, this means we are at a leaf in the three
            if(existingTuples.size() == 0) {
                Set<? extends OWLEntity> first = new HashSet<OWLEntity>(), second = new HashSet<OWLEntity>();

                if(ontologyFunction) {
                    //these sets contain all entities which are possible for a certain position in the tuples
                    first = this.getIdent().get(c.getContent().get(0));
                    second = this.getIdent().get(c.getContent().get(1));
                }

                String firstString = "", secondString = "";
                //condition consists of string manipulation condition
                if(c.getStringManipulation() != null) {
                    if(c.getStringManipulation().size() != 0) {

                        if(c.getStringManipulation().size() == 1) {
                            if(c.getStringManipulation().get(0).isStringFirstMember()) {
                                secondString = getEntityStringManipulation(c.getStringManipulation().get(0));
                                second = this.getIdent().get(secondString);
                            }
                            else {
                                firstString = getEntityStringManipulation(c.getStringManipulation().get(0));
                                first = this.getIdent().get(firstString);
                            }
                        }
                        else {
                            firstString = getEntityStringManipulation(c.getStringManipulation().get(0));
                            first = this.getIdent().get(firstString);
                            //check if comparing two entity strings or just one entity string and one given string
                            if(c.getStringManipulation().size() > 1) {
                                secondString = getEntityStringManipulation(c.getStringManipulation().get(1));
                                second = this.getIdent().get(secondString);
                            }
                        }



  /*                      if(this.assignment.get(getEntityStringManipulation(c.getStringManipulation().get(0))) != null) {
                            firstString = getEntityStringManipulation(c.getStringManipulation().get(0));
                            first = this.getIdent().get(firstString);
                        }
                        else if(this.assignment.get(getEntityStringManipulation(c.getStringManipulation().get(1))) != null) {
                            secondString = getEntityStringManipulation(c.getStringManipulation().get(1));
                            second = this.getIdent().get(secondString);
                        }*/

                         
                    }
                }

                //if the condition has content, a string or id is given
                if(c.getContent().size() > 0) {
                    //check the content of the condition
                    for(String y : c.getContent()) {
                        //string/strings given
                        if(firstString.equals("") && !y.isEmpty()) {
                            firstString = y;
                        }
                        if(secondString.equals("") && !y.isEmpty()) {
                            //check if this string has already been assigned to first string
                            if(firstString.equals(y)) {
                                continue;
                            }
                            else {
                                secondString = y;
                            }
                        }
                    }
                    if(c.getType() == Type.COMPATIBLE) {
                        first = this.getIdent().get(firstString);
                        second = this.getIdent().get(secondString);
                    }
                    if(c.getType() == Type.DATATYPERANGE) {
                        second = this.getIdent().get(secondString);
                    }
                }

                //check if the first entity is an entity or a string, if it's an entity, the map does not return null
                if(this.getAssignment().get(firstString) != null) {
                    for(OWLEntity e1 : first) {
                        //same for the second entity
                        if(this.getAssignment().get(secondString) != null) {
                            for(OWLEntity e2 : second) {
                                if(c.getType() == Type.COMPATIBLE) {
                                    if(((StringComparison) f).compute(new DatatypeRange().getDatatypeRange((OWLDataProperty)e1)
                                            , new DatatypeRange().getDatatypeRange((OWLDataProperty) e2))) {
                                        possibleTuples.add(createTupleBothEntities(e1, e2, firstString, secondString));
                                    }
                                    continue;
                                }

                                if(ontologyFunction) {
                                    if(((EntityComparison)f).compute(e1, e2)) {
                                        possibleTuples.add(createTupleBothEntities(e1, e2, firstString, secondString));
                                    }
                                }
                                else {
                                    if((((StringComparison)f).compute(computeStringManipulation(c.getStringManipulation().get(0),
                                            getInstanceOfStringMani(c.getStringManipulation().get(0)) , e1),
                                            computeStringManipulation(c.getStringManipulation().get(1),
                                            getInstanceOfStringMani(c.getStringManipulation().get(1)) ,e2)))){
                                        if(c.getType() == Type.SIMILARITY) {
                                            possibleTuples.add(createTupleBothEntities(e1, e2, firstString, secondString, ((Similarity)f).getSimilarityValue()));
                                        }
                                        else {
                                            possibleTuples.add(createTupleBothEntities(e1, e2, firstString, secondString));
                                        }
                                    }                                    
                                }
                            }
                        }
                        //the first entity is an entity, the second a string
                        else {
                            if(((StringComparison)f).compute(computeStringManipulation(c.getStringManipulation().get(0),
                                    getInstanceOfStringMani(c.getStringManipulation().get(0)) , e1),
                                    secondString)) {
                                if(c.getType() == Type.SIMILARITY) {
                                    possibleTuples.add(createTupleOneEntity(e1, firstString, ((Similarity)f).getSimilarityValue()));
                                }
                                else {
                                    possibleTuples.add(createTupleOneEntity(e1, firstString));
                                }
                            }
                        }
                    }
                }
                //the first entity is a string, but not the second one
                else {
                    for(OWLEntity e2 : second) {
                        if(c.getType() == Type.DATATYPERANGE) {
                            if(((DatatypeRange)f).compute(new DatatypeRange().
                                    getDatatypeRange((OWLDataProperty)e2), firstString)) {
                                possibleTuples.add(createTupleOneEntity(e2, secondString));
                            }
                        }
                        else {
                            //they are equivalent
                            if(((StringComparison)f).compute(firstString,
                                    computeStringManipulation(c.getStringManipulation().get(0),
                                    getInstanceOfStringMani(c.getStringManipulation().get(0)) ,e2))) {
                                if(c.getType() == Type.SIMILARITY) {
                                    possibleTuples.add(createTupleOneEntity(e2, secondString, ((Similarity)f).getSimilarityValue()));
                                }
                                else {
                                    possibleTuples.add(createTupleOneEntity(e2, secondString));
                                }
                            }
                        }
                    }
                }
            }
            //there are existing tuples
            else {
                OWLEntity e1 = null;
                OWLEntity e2 = null;
                String stringToCompare;
                int counter = 0;

                for(Tuple tu : existingTuples) {
                    //one string manipulation and one string
                    if(c.getStringManipulation().size() == 1) {
                        //get the id of the string manipulation
                        String entityId = getEntityStringManipulation(c.getStringManipulation().get(0));
                        stringToCompare = c.getContent().get(0);
                        //check if position in tuple already used
                        if(tu.getValue(this.getAssignment().get(entityId)) != null) {
                            //get entity which is located at the corresponding position
                            OWLEntity e = tu.getValue(this.getAssignment().get(entityId));
                            //check if condition holds
                            if(((StringComparison)f).compute(computeStringManipulation(c.getStringManipulation().get(0),
                                    getInstanceOfStringMani(c.getStringManipulation().get(0)) , e),
                                    stringToCompare)) {
                                if(c.getType() == Type.SIMILARITY) {
                                        possibleTuples.add(updateValue(tu, ((Similarity)f).getSimilarityValue()));
                                    }
                                else {
                                    possibleTuples.add(tu);
                                }
                            }
                        }
                        //position not used
                        else {
                            Set<? extends OWLEntity> entities = this.getIdent().get(getEntityStringManipulation(c.getStringManipulation().get(0)));
                            for(OWLEntity ent : entities) {
                                if(((StringComparison)f).compute(computeStringManipulation(c.getStringManipulation().get(0),
                                        getInstanceOfStringMani(c.getStringManipulation().get(0)) , ent),
                                        stringToCompare)) {
                                    //then create a new tuple and set the entities
                                    if(c.getType() == Type.SIMILARITY) {
                                        possibleTuples.add(createTupleOneEntity(ent, entityId, ((Similarity)f).getSimilarityValue()));
                                    }
                                    else {
                                        possibleTuples.add(createTupleOneEntity(ent, entityId));
                                    }
                                }
                            }
                        }
                        continue;
                    }
                    //both are entity ids and not strings
                    if(c.getStringManipulation().size() == 2) {
                        String s1 = "", s2 = "";
                        for(StringCondition s : c.getStringManipulation()) {
                            String y = getEntityStringManipulation(s);
                            //on the position in the tuple a value already exists
                            if(tu.getValue(this.getAssignment().get(y)) != null) {
                            //save the entities on the positions
                                if(counter == 0) {
                                    e1 = tu.getValue(this.getAssignment().get(y));
                                    s1 = y;
                                }
                                else {
                                    e2 = tu.getValue(this.getAssignment().get(y));
                                    s2 = y;
                                }
                            }
                        }

                        counter++;
                        if(e1 == null) {
                            if(e2 == null) {
                                //both positions are free, that means we need to check all possible entities
                                Set<? extends OWLEntity> first = this.getIdent().get(getEntityStringManipulation(c.getStringManipulation().get(0)));
                                Set<? extends OWLEntity> second = this.getIdent().get(getEntityStringManipulation(c.getStringManipulation().get(1)));
                                for(OWLEntity ent1 : first) {
                                    for(OWLEntity ent2 : second) {
                                        //condition holds?
                                        if(((StringComparison)f).compute(computeStringManipulation(c.getStringManipulation().get(0),
                                                getInstanceOfStringMani(c.getStringManipulation().get(0)) , ent1),
                                                computeStringManipulation(c.getStringManipulation().get(1),
                                                getInstanceOfStringMani(c.getStringManipulation().get(1)) ,ent2))) {
                                            //then create a new tuple and set the entities
                                            if(c.getType() == Type.SIMILARITY) {
                                                possibleTuples.add(updateTupleBothEntities(tu, ent1, ent2, s1, s2, ((Similarity)f).getSimilarityValue()));
                                            }
                                            else {
                                                possibleTuples.add(updateTupleBothEntities(tu, ent1, ent2, s1, s2));
                                            }
                                            
                                        }
                                    }
                                }
                            }
                            else {
                                //on the position for e2, a value is already available
                                Set<? extends OWLEntity> first = this.getIdent().get(c.getContent().get(0));
                                for(OWLEntity ent1 : first) {
                                    if(((StringComparison)f).compute(computeStringManipulation(c.getStringManipulation().get(0),
                                            getInstanceOfStringMani(c.getStringManipulation().get(0)) , ent1),
                                            computeStringManipulation(c.getStringManipulation().get(1),
                                            getInstanceOfStringMani(c.getStringManipulation().get(1)) ,e2))) {
                                        if(c.getType() == Type.SIMILARITY) {
                                            possibleTuples.add(createTupleBothEntities( ent1, e2, s1, s2, ((Similarity)f).getSimilarityValue()));
                                        }
                                        else {
                                            possibleTuples.add(createTupleBothEntities(ent1, e2, s1, s2));
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if(e2 == null) {
                                //on the position for e1, a value is already available
                                Set<? extends OWLEntity> second = this.getIdent().get(c.getContent().get(1));
                                for(OWLEntity ent2 : second) {
                                    if(((StringComparison)f).compute(computeStringManipulation(c.getStringManipulation().get(0),
                                            getInstanceOfStringMani(c.getStringManipulation().get(0)) , e1),
                                            computeStringManipulation(c.getStringManipulation().get(1),
                                            getInstanceOfStringMani(c.getStringManipulation().get(1)) ,ent2))) {
                                        if(c.getType() == Type.SIMILARITY) {
                                            possibleTuples.add(createTupleBothEntities(e1, ent2, s1, s2, ((Similarity)f).getSimilarityValue()));
                                        }
                                        else {
                                            possibleTuples.add(createTupleBothEntities(e1, ent2, s1, s2));
                                        }
                                    }
                                }
                            }
                            else {
                                //both not null, just check if the condition holds between the existing entities on the positions
                                if(((StringComparison)f).compute(computeStringManipulation(c.getStringManipulation().get(0),
                                        getInstanceOfStringMani(c.getStringManipulation().get(0)) , e1),
                                        computeStringManipulation(c.getStringManipulation().get(1),
                                        getInstanceOfStringMani(c.getStringManipulation().get(1)) ,e2))) {
                                    if(c.getType() == Type.SIMILARITY) {
                                        possibleTuples.add(updateValue(tu, ((Similarity)f).getSimilarityValue()));
                                    }
                                    else {
                                        possibleTuples.add(tu);
                                    }
                                }
                            }
                        }
                    }
                    //two strings, datatype compability or ontology functions
                    if(c.getStringManipulation().size() == 0) {
                        String s1 = c.getContent().get(0), s2 = c.getContent().get(1);
                        //check if datatypes are compared
                        if(this.getAssignment().get(s1) != null) {
                            e1 = null;
                            e2 = null;
                            counter = 0;
                            for(String y : c.getContent()) {
                                //on the position in the tuple a value already exists
                                if(tu.getValue(this.getAssignment().get(y)) != null) {
                                    //the first entity is assigned to e1
                                    if(counter == 0) {
                                        e1 = tu.getValue(this.getAssignment().get(y));
                                    }
                                    else {
                                        e2 = tu.getValue(this.getAssignment().get(y));
                                    }
                                }
                            }
                            counter++;
                            //if e1 or e2 is null, at least one string is not occupied in the tuple
                            if(e1 == null) {
                                if(e2 == null) {
                                    //both positions are free, that means we need to check all possible entities
                                    Set<? extends OWLEntity> first = this.getIdent().get(c.getContent().get(0));
                                    Set<? extends OWLEntity> second = this.getIdent().get(c.getContent().get(1));
                                    for(OWLEntity ent1 : first) {
                                        for(OWLEntity ent2 : second) {
                                            if(ontologyFunction) {
                                                if(((EntityComparison)f).compute(ent1, ent2)) {
                                                    possibleTuples.add(updateTupleBothEntities(tu, ent1, ent2, s1, s2));
                                                }
                                            }
                                            else {
                                                //check compability of ranges
                                                if(((StringComparison)f).compute(new DatatypeRange().getDatatypeRange(ent1.asOWLDataProperty()),
                                                        new DatatypeRange().getDatatypeRange(ent2.asOWLDataProperty()))) {
                                                    //then create a new tuple and set the entities
                                                    if(c.getType() == Type.SIMILARITY) {
                                                        possibleTuples.add(updateTupleBothEntities(tu, ent1, ent2, s1, s2, ((Similarity)f).getSimilarityValue()));
                                                    }
                                                    else {
                                                        possibleTuples.add(updateTupleBothEntities(tu, ent1, ent2, s1, s2));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //one position allocated (for e2), one not (for e1)
                                else {
                                    //on the position for e2, a value is already available
                                    Set<? extends OWLEntity> first = this.getIdent().get(c.getContent().get(0));
                                    for(OWLEntity ent1 : first) {
                                        if(ontologyFunction) {
                                            if(((EntityComparison)f).compute(ent1, e2)) {
                                                possibleTuples.add(updateTupleBothEntities(tu, ent1, e2, s1, s2));
                                            }
                                        }
                                        else {
                                            if(((StringComparison)f).compute(new DatatypeRange().getDatatypeRange(ent1.asOWLDataProperty()),
                                                    new DatatypeRange().getDatatypeRange(e2.asOWLDataProperty()))) {
                                                if(c.getType() == Type.SIMILARITY) {
                                                    possibleTuples.add(updateTupleBothEntities(tu, ent1, e2, s1, s2, ((Similarity)f).getSimilarityValue()));
                                                }
                                                else {
                                                    possibleTuples.add(updateTupleBothEntities(tu, ent1, e2, s1, s2));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //position for e1 allocated
                            else {
                                if(e2 == null) {
                                    //on the position for e1, a value is already available
                                    Set<? extends OWLEntity> second = this.getIdent().get(c.getContent().get(1));
                                    for(OWLEntity ent2 : second) {
                                        if(ontologyFunction) {
                                            if(((EntityComparison)f).compute(e1, ent2)) {
                                                possibleTuples.add(updateTupleBothEntities(tu, e1, ent2, s1, s2));
                                            }
                                        }
                                        else {
                                            if(((StringComparison)f).compute(new DatatypeRange().getDatatypeRange(e1.asOWLDataProperty()),
                                                    new DatatypeRange().getDatatypeRange(ent2.asOWLDataProperty()))) {
                                                if(c.getType() == Type.SIMILARITY) {
                                                    possibleTuples.add(updateTupleBothEntities(tu, e1, ent2, s1, s2, ((Similarity)f).getSimilarityValue()));
                                                }
                                                else {
                                                    possibleTuples.add(updateTupleBothEntities(tu, e1, ent2, s1, s2));
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    if(ontologyFunction) {
                                        if(((EntityComparison)f).compute(e1, e2)) {
                                            possibleTuples.add(tu);
                                        }
                                    }
                                    else {
                                        //both not null, just check if they are compatible
                                        if(((StringComparison)f).compute(new DatatypeRange().getDatatypeRange(e1.asOWLDataProperty()),
                                                new DatatypeRange().getDatatypeRange(e2.asOWLDataProperty()))) {
                                            if(c.getType() == Type.SIMILARITY) {
                                                updateValue(tu, ((Similarity)f).getSimilarityValue());
                                            }
                                            else {
                                                possibleTuples.add(tu);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //two strings are given
                        else {
                            //fulfill condition?
                            if(((StringComparison)f).compute(s1, s2)) {
                                if(c.getType() == Type.SIMILARITY) {
                                    updateValue(tu, ((Similarity)f).getSimilarityValue());
                                }
                                else {
                                    possibleTuples.add(tu);
                                }
                            }
                        }
                    }
                }
            }
            return possibleTuples;
	}

	/**
	 * The the entity id of a string manipulation.
	 * Due to the nested structure, recursive calls might be necessary.
	 *
	 * @param s
	 * @return
	 */
	private String getEntityStringManipulation(StringCondition s) {
		if(s.getEntityId() == null) {
			return getEntityStringManipulation(s.getManipulations());
		}
		else {
			return s.getEntityId();
		}
	}

	/**
	 * Get an instance of the corresponding string condition.
	 *
	 * @param sm
	 * @return
	 */
	private StringManipulation getInstanceOfStringMani(StringCondition sm) {
		switch(sm.getType()) {
		case HEADNOUN:
			return new Head();
		case COMP_HEADNOUN:
			return new ComplementHeadNoun();
		case FIRST_PART:
			return new FirstPart();
		case COMP_FIRST_PART:
			return new ComplementFirstPart();
                case PASSIVE:
                        return new Passive();
                case ACTIVE:
                        return new Active();
                case MODIFIER:
                    return new Modifier();
                case VERB:
                        return new Verb();
                case WORD_CLASS:
                        return new WordClass();

		}
		return null;
	}

        private Tuple createTupleBothEntities(OWLEntity e1, OWLEntity e2, String firstString, String secondString, Double... simValue) {
            //then create a new tuple and set the entities
            Tuple tup = new Tuple(this);
            //check the assignment...
            tup.setValue(this.getAssignment().get(firstString), e1);
            tup.setValue(this.getAssignment().get(secondString), e2);
            if(simValue.length == 1) {
                tup.setSimilarityValue(simValue[0]);
            }
            return tup;
        }

        private Tuple createTupleOneEntity(OWLEntity e1,String firstString, Double... simValue) {
            //then create a new tuple and set the entities
            Tuple tup = new Tuple(this);
            //check the assignment...
            tup.setValue(this.getAssignment().get(firstString), e1);
            if(simValue.length == 1) {
                tup.setSimilarityValue(simValue[0]);
            }
            return tup;
        }

        private Tuple updateTupleBothEntities (Tuple t, OWLEntity e1, OWLEntity e2, String firstString, String secondString, Double... simValue) {
            //then create a new tuple and set the entities
            Tuple tup = t.getCopy();
            //check the assignment...
            tup.setValue(this.getAssignment().get(firstString), e1);
            tup.setValue(this.getAssignment().get(secondString), e2);
            if(simValue.length == 1) {
                tup.setSimilarityValue(simValue[0]);
            }
            return tup;
        }

        private Tuple updateValue (Tuple t, Double simValue) {
            t.setSimilarityValue(simValue);
            return t;
        }
}
