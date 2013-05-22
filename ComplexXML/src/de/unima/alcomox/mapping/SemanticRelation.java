package de.unima.alcomox.mapping;

import de.unima.alcomox.exceptions.CorrespondenceException;

/**
* A semantic relation can be used to express equivalence, subsumption or disjointness 
* between entities of different ontologies.   
*/
public class SemanticRelation {
	
	/**
	* Relation thats not available.
	*/	
	public static final int NA = 0;	
	
	/**
	* Relation of equivalence, e.g. A = B.
	*/
	public static final int EQUIV = 1;
	
	/**
	* Relation of subsumption, e.g. A < B.
	*/
	public static final int SUB = 2;
	
	/**
	* Relation of supsumption, e.g. A > B.
	*/
	public static final int SUPER = 3;	
	
	/**
	* Relation of disjointness, e.g. A != B.
	*/	
	public static final int DIS = 4;
	

	
	private int type;
	
	/**
	* Constructs a semantic relation of a certain type.
	* 
	* @param type The type of the semantic relation.
	* @throws CorrespondenceException Thrown, if a not existing type of relation is chosen.
	*/
	public SemanticRelation(int type) throws CorrespondenceException {
		if ((type != EQUIV) && (type != SUB) && (type != SUPER) && (type != DIS)) {
			throw new CorrespondenceException(CorrespondenceException.INVALID_SEMANTIC_RELATION);
		}
		else {
			this.type = type;
		}
	}
	
	/**
	* 
	* @return The type of this semantic relation.
	*/
	public int getType() {
		return this.type;
	}
	
	/**
	* Returns the string representation of this semantic relation.
	* 
	* @return Representation as string.
	*/
	public String toString() {
		switch (this.type) {
		case SemanticRelation.EQUIV:
			return "=";
		case SemanticRelation.SUB:
			return "<";
		case SemanticRelation.SUPER:
			return ">";
		case SemanticRelation.DIS:
			return "!=";
		default:
			// will never occur, hopefully
			return "?";
		}
	}
	
	/**
	* Checks equality of this and that semantic relation.
	* 
	* @param that That semantic relation.
	* @return True, if this and that are of the same type.
	*/
	public boolean equals(Object that) {
		return this.type == ((SemanticRelation)that).type;	
	}
	
	public int hashCode() {
		return this.type;
	}

	/**
	* Returns the inverse semantic relation of this semantic relation.
	* 
	* @return The inverted semantic relation
	*/
	public SemanticRelation getInverse() {
		switch (this.type) {
		case SemanticRelation.SUB:
			try { return new SemanticRelation(SemanticRelation.SUPER); }
			catch (CorrespondenceException e) { }
		case SemanticRelation.SUPER:
			try { return new SemanticRelation(SemanticRelation.SUB); }
			catch (CorrespondenceException e) { }
		default:
			return this;
		}
	}
	
	
}
