package de.unima.alcomox.mapping;


import java.net.URI;

import de.unima.alcomox.exceptions.CorrespondenceException;



/**
* A correspondence represents a (simple) semantic relation between two
* entities belonging to different ontologies. The enties are first only
* referred to by their uris (unbound correspondence), when the container
* mapping is bound to the ontologies the entities attributed are set to
* the internal entity representation.
*/
public class Correspondence implements Comparable<Correspondence>  {
	
	// private static int idCounter = 0;
	
	// private int id;
	private String sourceEntityUri;
	private String targetEntityUri;
	private SemanticRelation relation;
	private double confidence;	
	private String explanation = null;


	/**
	* Constructs an unbound correspondence with confidence value set to 1.0.
	* 
	* @param sourceEntityUri Uri of the source entity.
	* @param targetEntityUri Uri of the target entity.
	* @param relation Semantic relation between two entities.
	* @throws CorrespondenceException Thrown if the uris are not wellformed (in a weak sense).
	* Checking wether the uri reference can be resolved does not occur in this context.
	*/
	public Correspondence(String sourceEntityUri, String targetEntityUri, SemanticRelation relation) throws CorrespondenceException {
		// this.setSourceEntityUri(sourceEntityUri);
		// this.setTargetEntityUri(targetEntityUri);
		
		this.sourceEntityUri = sourceEntityUri;
		this.targetEntityUri = targetEntityUri;
		this.setRelation(relation);
		this.setConfidence(1.0);
	}
	
	/**
	* Constructs an unbound correspondence with confidence value set to 1.0.
	* 
	* @param sourceEntityUri Uri of the source entity.
	* @param targetEntityUri Uri of the target entity.
	* @param relation Semantic relation between two entities.
	* @param confidence Confidence value of this correspondence.
	* @throws CorrespondenceException Thrown if the uris are not wellformed (in a weak sense) and
	* if the confidence value is not in the range from 0.0 to 1.0.
	* Checking wether the uri reference can be resolved does not occur in this context.
	*/
	public Correspondence(String sourceConcept, String targetConcept, SemanticRelation relation, double confidence) throws CorrespondenceException {
		this(sourceConcept, targetConcept, relation);
		this.setConfidence(confidence);
	}
	
	public Correspondence(String sourceConcept, String targetConcept, SemanticRelation relation, double confidence, String expl) throws CorrespondenceException {
		this(sourceConcept, targetConcept, relation, confidence);
		this.setExplanation(expl);
	}
	
	/**
	* @return String representation of this correspondence. 
	*/
	public String toString() {	
		String rep = "";
		rep += sourceEntityUri + " " + relation + " " + targetEntityUri + " | " + confidence;
		if (explanation != null) {
			rep += "\n"  + this.getExplanation() + "\n";
		}
		return rep;
	}
	
	/**
	* @return Short string representation of this correspondence. 
	*/	
	public String toShortString() {
		return  this.getFragement(sourceEntityUri) + " " + relation + " " + this.getFragement(targetEntityUri) + " | " + confidence;
	}

	// equality and hashcode
	
	/**
	*  @return True if uri of source entity, uri of target entity and semantic relation are the same,
	*  false otherwise. Equality is not affected by the confidence value. This means that two
	*  correspondences can be equal if they make the same statement with different trust in the statement.
	*  Input mappings that contain such correspondences are regarded as incorrect.
	*/
	public boolean equals(Object thatObject) {
		Correspondence that = (Correspondence)thatObject;
		String c1 = this.sourceEntityUri + this.relation + this.targetEntityUri;
		String c2 = that.sourceEntityUri + that.relation + that.targetEntityUri;
		if (c1.equals(c2)) { return true; }
		else { return false; }
	}
	


	public int hashCode() {
		return (this.sourceEntityUri + this.relation + this.targetEntityUri).hashCode();
	}
	
	// related to ordering correspondences
	
	public int compareTo(Correspondence correspondence) {
		Correspondence that = (Correspondence)correspondence;
		if (this.getConfidence() > that.getConfidence()) { return 1; }	
		else if (this.getConfidence() < that.getConfidence()) { return -1; }
		else {
			if (this.getSourceEntityUri().compareTo(that.getSourceEntityUri()) > 0) { return 1; }	
			else if	(this.getSourceEntityUri().compareTo(that.getSourceEntityUri()) < 0) { return -1; }	
			else {
				if (this.getTargetEntityUri().compareTo(that.getTargetEntityUri()) > 0) { return 1; }	
				else if	(this.getTargetEntityUri().compareTo(that.getTargetEntityUri()) < 0) { return -1; }	
				else { return 0; }
			}
		}
	}	
	
	// getter 
	
	public String getSourceEntityUri() {
		return this.sourceEntityUri;
	}

	public String getTargetEntityUri() {
		return this.targetEntityUri;
	}

	public SemanticRelation getRelation() {
		return this.relation;
	}
	
	public double getConfidence() {
		return this.confidence;
	}	
	
	
	public String getSourceNamespace() {
		return this.getNamespace(this.getSourceEntityUri());
	}
	
	public String getTargetNamespace() {
		return this.getNamespace(this.getTargetEntityUri());
	}
	

	
	
	// setter
	
	public void setSourceEntityUri(String sourceEntity) throws CorrespondenceException {
		this.sourceEntityUri = sourceEntity;
		if (!(isFullname(this.sourceEntityUri))) {
			throw new CorrespondenceException(
					CorrespondenceException.MISSING_NAMESPACE,
					"occured with respect to '" + this.toString() + "'"
			);				
		}
	}

	public void setTargetEntityUri(String targetEntity) throws CorrespondenceException {
		this.targetEntityUri = targetEntity;
		if (!(isFullname(this.targetEntityUri))) {
			throw new CorrespondenceException(
					CorrespondenceException.MISSING_NAMESPACE,
					"occured with respect to '" + this.toString() + "'"
			);				
		}
	}
	
	public void setConfidence(double confidence) throws CorrespondenceException {
		this.confidence = confidence;
		if (this.confidence < 0.0 || this.confidence > 1.0) {
			throw new CorrespondenceException(
				CorrespondenceException.INVALID_CONFIDENCE_VALUE,
				"occured with respect to '" + this.toString() + "'"
			);
		}
	}
	
	public void setRelation(SemanticRelation relation) {
		this.relation = relation;
	}
	
	

	// private
	
	private boolean isFullname(String fullname) {
		if (fullname.contains("#")) { return true; }
		else { return false; }	
	}
	
	private String getNamespace(String uri) {
		String[] parts = uri.split("#");
		return parts[0];
	}
	
	private String getFragement(String uri) {
		String[] parts = uri.split("#");
		return parts[1];
	}


	public String getExplanation() {
		return explanation;
	}


	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

}
