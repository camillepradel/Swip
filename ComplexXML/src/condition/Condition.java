package condition;

import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Dominique Ritze
 *
 * An instance of the class Condition represents a condition from a pattern.
 * Therefore the type, e.g. similarity or subclass, is saved as well as the
 * content of the condition (involved entites or further conditions).
 *
 */
public class Condition {
	
	/**
         * possible conditions:
	 * just with a set of conditions like the or/and conditions
	 * with a set of string manipulations, for example same(name(ent1), name(ent2))
	 * with string manipulation and a similarity value as first string in the content-list -> for similarity
	 * with string manipulation and additionally values in content, for example same(name(ent1), "person")
	 * with only values in content, for example same("a", "b")
         */

         public enum Type {
             SIMILARITY, EQUIVALENT, EQUAL, CONTAINED, COMPATIBLE, DOMAIN,
             DATATYPERANGE, OBJECTRANGE, SUBCLASS, SUPERCLASS, SYNONYM, AND, OR,
             NOT, WORD_CLASS, ANTONYMS, HYPERNYM, HYPONYM, NOMINALIZATION, CATEGORY
         }
	
	private Type type;
	private ArrayList<String> content = new ArrayList<String>();
	private Set<Condition> conditions = new HashSet<Condition>();
	private ArrayList<StringCondition> stringMani;
	
        /**
         * Constructor with type.
         *
         * @param type
         */
	private Condition(Type type) {
		this.type = type;
	}
	
        /**
         * Get an instance of the class with a special type,
         * a collection containing the further conditions/stringconditions
         * /entites and other necessary data.
         *
         * @param <T>
         * @param type The type of the condition.
         * @param col A collection containing the identifiers of entites or
         * further string conditions or conditions.
         * @param values
         * @return
         */
	@SuppressWarnings("unchecked")
	public static <T> Condition getInstance(Type type, Collection<T> col, String... values) {
		T t = col.iterator().next();
		
		Condition condition = new Condition(type);
		//check if additional information is given, like a double-value for the similarity
		//or a string which is used in some comparisons
		if(values.length > 0) {
                    for(int i=0; i<values.length; i++) {
                        condition.content.add(values[i]);
                    }
		}
		
		if (col instanceof List && t instanceof String) {
                        condition.content.addAll((ArrayList<String>)col);
		} else if (col instanceof List && t instanceof StringCondition) {
			condition.stringMani = (ArrayList<StringCondition>) col;
		} else if (col instanceof Set && t instanceof Condition) {
			condition.conditions = (HashSet<Condition>) col;
		} else {
			throw new RuntimeException(new ComplexMappingException(ExceptionType.BAD_PARAMETER,
                                "Cannot create condition instance."));
		}
		
		return condition;
	}

        /**
         *
         * @return The type of the condition.
         */
	public Type getType() {
		return this.type;
	}

        /**
         *
         * @return A list with the identifiers of the entities or strings
         * involved in the condition.
         */
	public ArrayList<String> getContent() {
		return content;
	}

        /**
         *
         * @return A set of further conditions (for AND, OR, NOT operators).
         */
	public Set<Condition> getConditions() {
		return conditions;
	}

	/**
         *
         * @return A list with string manipulations if any.
         */
	public ArrayList<StringCondition> getStringManipulation() {
		return stringMani;
	}

        /**
         * Update the content.
         *
         * @param newContent
         */
	public void setContent(ArrayList<String> newContent) {
		this.content = newContent;
	}
}
