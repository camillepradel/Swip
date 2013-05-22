package condition;


/**
 *
 * @author Dominique Ritze
 *
 * A string condition is used within a condition which compares strings, e.g.
 * similarity or same. They can be applied to strings or enties.
 *
 */
public class StringCondition {
    

        public enum StringType {
            HEADNOUN, COMP_HEADNOUN, FIRST_PART, COMP_FIRST_PART, LABEL, NAME,
            VERB, PASSIVE, ACTIVE, WORD_CLASS, EMPTY, MODIFIER
        }
	
	private StringCondition stringManipulations;
	private String entityId;
	private StringType type;
        private boolean stringFirstMember = false;

        /**
         * Constructor for a string condition which just contains an
         * entity id as content.
         *
         * @param type
         * @param entityId
         */
	public StringCondition(StringType type,String entityId, boolean stringFirstMember) {
		this.entityId = entityId;
		this.type = type;
                this.stringFirstMember = stringFirstMember;
	}

        /**
         * Constructor for a string condition which consists of further
         * string conditions.
         *
         * @param type
         * @param stringManipulations
         */
	public StringCondition(StringType type, StringCondition stringManipulations) {
		this.stringManipulations = stringManipulations;
		this.type = type;
	}

        /**
         *
         * @return The id of the entity, if its a string manipulation
         * containing an entity.
         */
	public String getEntityId() {
		return this.entityId;
	}

        /**
         *
         * @return Further string manipulation.
         */
	public StringCondition getManipulations() {
		return this.stringManipulations;
	}

        /**
         * 
         * @return
         */
	public StringType getType() {
		return this.type;
	}

        /**
        * @return the stringFirstMember
        */
        public boolean isStringFirstMember() {
            return stringFirstMember;
        }

}
