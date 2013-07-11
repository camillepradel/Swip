package de.unima.alcomox.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import de.unima.alcomox.exceptions.AlcomoException;
import de.unima.alcomox.exceptions.CorrespondenceException;
import de.unima.alcomox.exceptions.PCFException;


/**
* A mapping is a ordered list of correspondences. It provided several set operations on the correspondences.
* As well as ordering and several operations like normalization and thresholding.
* 
*/
public class Alignment implements Iterable<Correspondence>, Comparable<Alignment>   {
	
	private ArrayList<Correspondence> correspondences = null;
	
	
	protected boolean[] idPattern = null;


	/**
	* Constructs a mapping with an empty list of correspondences.
	*/
	public Alignment() {
		this(new ArrayList<Correspondence>());
	}
	
	/**
	* Constructs a mapping with the given list of correspondences.
	* 
	* @param correspondences The correspondences of the mapping as list.
	*/
	public Alignment(ArrayList<Correspondence> correspondences) {
		this.setCorrespondences(correspondences);
	}
	
	/**
	* Constructs a mapping with the given set of correspondences.
	* 
	* @param correspondences The correspondences of the mapping as set.
	*/
	public Alignment(Set<Correspondence> correspondencesAsSet) {
		this.setCorrespondences(correspondencesAsSet);
	}


	
	public String toString() {
		return this.toSomeString(true);
	}	
	
	public String toShortString() {
		return this.toSomeString(false);
	}
	
	/**
	* Returns different kinds of meta information about this mapping.
	* 
	* @return Information about this mapping.
	*/
	public String getMetaDescription() {
		StringBuffer sb = new StringBuffer();

		HashSet<SemanticRelation> relations = new HashSet<SemanticRelation>();
		HashSet<String> sourceNamespaces = new HashSet<String>();
		HashSet<String> targetNamespaces = new HashSet<String>();
		
		double lowerBound = this.getConfidenceLowerBound();
		double upperBound = this.getConfidenceUpperBound();
		for (Correspondence c : this.getCorrespondences()) {
			relations.add(c.getRelation());
			sourceNamespaces.add(c.getSourceNamespace());
			targetNamespaces.add(c.getTargetNamespace());
		}		
		sb.append("NUMBER OF CORRESPONDENCES: " +  this.size() + "\n");
		sb.append("SEMANTIC RELATIONS: " + relations + "\n");
		sb.append("NAMESPACES FOR SOURCE ENTITIES: " + sourceNamespaces + "\n");
		sb.append("NAMESPACES FOR TARGET ENTITIES: " + targetNamespaces + "\n");
		sb.append("CONFIDENCE RANGE: [" + lowerBound + ", " + upperBound + "]\n");
		sb.append("IS UNIQUE: " + this.isUnique() + "\n");
		return sb.toString();
	}
	
	public double getConfidenceLowerBound() {
		double lowerBound = Double.MAX_VALUE;
		for (Correspondence c : this.getCorrespondences()) {
			lowerBound = c.getConfidence() < lowerBound ? c.getConfidence() : lowerBound;
		}
		return lowerBound;
	}
	
	public double getConfidenceUpperBound() {
		double upperBound = Double.MIN_VALUE;
		for (Correspondence c : this.getCorrespondences()) {
			upperBound = c.getConfidence() > upperBound ? c.getConfidence() : upperBound;
		}
		return upperBound;
	}	
	
	public boolean isUnique() {
		for (int i = 0; i < this.size(); i++) {
			for (int j = i + 1; j < this.size(); j++) {
				if (this.getCorrespondences().get(i).equals(this.getCorrespondences().get(j))) {
					return false;
				}
			}
		}
		return true;
	}	
	
	// --- SET OPERATIONS ---
	
	/**
	* Returns the set difference between this mapping and that mapping.
	* 
	* @param that The mapping to be compared to this mapping.
	* @return The set difference this without that.
	*/	
	public Alignment getDifference(Alignment that) {
		HashSet<Correspondence> thisCSet = this.getCorrespondencesAsSet();
		HashSet<Correspondence> thatCSet = that.getCorrespondencesAsSet();
		thisCSet.removeAll(thatCSet);
		return (new Alignment(thisCSet));
	}

	/**
	* Returns the intersection between this mapping and that.
	* 
	* @param that The mapping to be intersected to this mapping.
	* @return The intersection bewteen this and that.
	*/	
	public Alignment getIntersection(Alignment that) {
		HashSet<Correspondence> thisCSet = this.getCorrespondencesAsSet();
		HashSet<Correspondence> thatCSet = that.getCorrespondencesAsSet();
		thisCSet.retainAll(thatCSet);
		return (new Alignment(thisCSet));
	}
	
	/**
	* Returns the union of this mapping and that.
	* 
	* @param that The mapping to be unioned with this mapping.
	* @return The union of this and that.
	*/	
	public Alignment getUnion(Alignment that) {
		HashSet<Correspondence> thisCSet = this.getCorrespondencesAsSet();
		HashSet<Correspondence> thatCSet = that.getCorrespondencesAsSet();
		thisCSet.addAll(thatCSet);
		return (new Alignment(thisCSet));
	}
	
	/**
	* Creates and returns a copy of this mapping. Te copy has ist own list of correspondences,
	* but contains references to the same correspondences as are referred to be this mapping. 
	* 
	* @return A copy of this mapping.
	*/
	public Alignment getCopy() {
		Alignment copy = new Alignment();
		for (Correspondence c : this) {
			copy.push(c);
		}
		if (this.idPattern != null) {
			copy.idPattern = new boolean[this.idPattern.length];
			for (int i = 0; i < this.idPattern.length; i++) {
				copy.idPattern[i] = this.idPattern[i];
			}
		}
		return copy;
	}
	
	// --- CHANGING THE MAPPING --- 

	/**
	* Applies a threshold on the mapping by removing every
	* correspondence with a confidence below the threhold.
	* 
	* @param threshhold The threshold.
	* @return The number of correspondences that have been removed.
	*/
	public int applyThreshhold(float threshhold) {
		ArrayList<Correspondence> thresholdedCorrespondences = new ArrayList<Correspondence>();
		int numOfRemovedCorrespondences = 0;
		for (Correspondence c : this.correspondences) {
			if (c.getConfidence() >= threshhold) { thresholdedCorrespondences.add(c); }
			else { numOfRemovedCorrespondences++; }
		}
		this.correspondences = thresholdedCorrespondences;
		return numOfRemovedCorrespondences;
	}
	
	public void normalize(double normConfidence) throws AlcomoException {
		for (Correspondence c : this.correspondences) {
			c.setConfidence(normConfidence);
		}
		
	}
	
	/**
	* Normalizes the confidences of the correspondences to the range [0.0, 1.0].
	* 
	* @param minBound The lower bound (inclusive) of the range.
	* @param maxBound The upper bound (inclusive) of the range.
	*/
	public void normalize() {
		this.normalize(0.0, 1.0);
	}
	
	/**
	* Normalizes the confidences of the correspondences to a given range.
	* If all correspondences have the same confidence, all of them
	* are set to the upper bound of the range.
	* 
	* @param minBound The lower bound (inclusive) of the range.
	* @param maxBound The upper bound (inclusive) of the range.
	*/
	public void normalize(double minBound, double maxBound) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		// store the lowest and greates value of the correspondences.
		for (Correspondence c : this.correspondences) {
			min = (c.getConfidence() < min) ? c.getConfidence() : min;
			max = (c.getConfidence() > max) ? c.getConfidence() : max;
		}
		if (this.correspondences.size() > 0) {
			// if all correspondences had the same confidence, set them to the upper bound
			if (min == max) {
				for (int i = 0; i < this.correspondences.size(); i++) {
					try {
						this.correspondences.get(i).setConfidence(maxBound);
					}
					catch(CorrespondenceException ce) {
						// can never happen in this method
					}
				}			
			}
			// the normal case
			else {
				double sim;
				for (int i = 0; i < this.correspondences.size(); i++) {
					sim = this.correspondences.get(i).getConfidence();
					sim = ((sim - min) * ((maxBound - minBound)  / (max - min))) + minBound;
					sim = (sim > maxBound) ? maxBound : sim;
					sim = (sim < minBound) ? minBound : sim;
					// System.out.println(sim);
					try {
						this.correspondences.get(i).setConfidence(sim);
					}
					catch(CorrespondenceException ce) {
						// can never happen in this method
					}
				}
			}
		}
	}	
	
	/**
	* Inverts this mapping. This operation changes source and target entities of the
	* correspondences as well as replaces the semantic relations with inverse relations.
	*
	*/
	public void invert() {
		String sourceEntity;
		String targetEntity;
		SemanticRelation semanticRelation;		
		for (Correspondence c : this.correspondences) {
			sourceEntity = c.getSourceEntityUri();
			targetEntity = c.getTargetEntityUri();
			semanticRelation = c.getRelation();
			try {
				c.setSourceEntityUri(targetEntity);
				c.setTargetEntityUri(sourceEntity);
			} catch (CorrespondenceException e) {
				// cannot occur in this context
			}
			
			c.setRelation(semanticRelation.getInverse());			
		}
	}
	
	// --- GETTER AND SETTER ---
	

	
	/**
	* Sets a list of correspondences as correspondences of the mapping. 
	*/
	public void setCorrespondences(ArrayList<Correspondence> correspondences) {
		this.correspondences = new ArrayList<Correspondence>();
		HashSet<Correspondence> correspondencesAsSet = new HashSet<Correspondence>();
		correspondencesAsSet.addAll(correspondences);
		this.correspondences.addAll(correspondencesAsSet);
	}
	
	/**
	* Sets a set of correspondences as correspondences of the mapping. 
	*/
	public void setCorrespondences(Set<Correspondence> correspondencesAsSet) {
		this.correspondences = new ArrayList<Correspondence>();
		this.correspondences.addAll(correspondencesAsSet);
	}	

	/**
	* Returns the correspondences of this mapping as list.
	* 
	* @return The internally stored list of correspondences.
	*/
	public ArrayList<Correspondence> getCorrespondences() {
		return correspondences;
	}

	/**
	* Returns the correspondences of this mapping as set.
	* 
	* @return The set of correspondences.
	*/
	public HashSet<Correspondence> getCorrespondencesAsSet() {
		HashSet<Correspondence> correspondencesAsSet = new HashSet<Correspondence>();
		correspondencesAsSet.addAll(this.getCorrespondences());
		return correspondencesAsSet;
	}	
	
	/**
	* Returns the size of the mapping.
	* 
	* @return The size of the mapping in number of correspondences.
	*/
	public int size() {
		return this.correspondences.size();
	}
	


	public Correspondence get(int index) {
		if (index < this.size()) { return this.correspondences.get(index); }
		else { return null; }
	}

	public void push(Correspondence correspondence) {
		this.correspondences.add(correspondence);
	}
	
	public Correspondence pop() {
		return this.correspondences.remove(this.correspondences.size() - 1);
	}
	
	public Correspondence remove(int index) {
		return this.correspondences.remove(index);
	}
	



	// TODO fix this
	public boolean equals(Object object) {
		// System.out.println("This: " +  this.size() + "\nThat: ?");
		try {
			// Mapping that = (Mapping)object;
			return false;
			// up to you
		}
		catch(ClassCastException e) {
			return false;
		}
	}
	
	
	public int hashCode() {
		if (this.idPattern == null) {
			return 1;
		}		
		int hashcode = 0;
		int size = (this.idPattern.length < 32) ? this.idPattern.length : 32;
		for (int i = 0 ; i < size; i++) {
			if (this.idPattern[i]) { hashcode += 1; }
			hashcode = hashcode << 1;
		}
		return hashcode;
	}
	
	public Iterator<Correspondence> iterator() {
		return this.correspondences.iterator();
	}

	public void sortDescending() {
		Collections.sort(this.correspondences);
		Collections.reverse(this.correspondences);		
	}





	/**
	* Remove all correspondences from the mapping that express a different semantic relation than
	* equivalence.
	*
	*/
	public void reduceToEquivalenceCorrespondences() {
		int i = 0;
		while (i < this.size()) {
			if (this.get(i).getRelation().getType() == SemanticRelation.EQUIV) { i++; }
			else { this.correspondences.remove(i); }
		}
	}

	// does this make sense ?
	public void modifyToEquivalenceCorrespondences() {
		int i = 0;
		while (i < this.size()) {
			if (this.get(i).getRelation().getType() != SemanticRelation.EQUIV) {
				try {
					this.get(i).setRelation(new SemanticRelation(SemanticRelation.EQUIV));
				}
				catch (CorrespondenceException e) {
					// will never happen in this situation
				}
			}
			i++;
		}
		
	}
	
	public static Alignment getJoinedMapping(Alignment[] mappings) throws PCFException, CorrespondenceException {
		double[] weights = new double[mappings.length];
		double weight = 1.0 / (double)weights.length;
		for (int i = 0; i < weights.length; i++) {
			weights[i] = weight;
		}
		return getJoinedMapping(mappings, weights);
	}
	
	public static Alignment getJoinedMapping(Alignment[] mappings, double[] weights) throws PCFException, CorrespondenceException {
		if (mappings.length != weights.length) {
			throw new PCFException(
					PCFException.INVALID_PARAM_COMBINATION,
					"in a mapping join operation you need the same number of mappings and weights"
			);
		}
		HashMap <Correspondence, Double> hashedConfidences = new HashMap<Correspondence, Double>();
		for (int m = 0; m < mappings.length; m++) {
			double weight = weights[m];
			for (Correspondence c : mappings[m]) {
				if (hashedConfidences.containsKey(c)) {
					hashedConfidences.put(c, c.getConfidence() * weight + hashedConfidences.get(c));
				}
				else {
					hashedConfidences.put(c, c.getConfidence() * weight);
				}
			}
		}
		Alignment joinedMapping = new Alignment();
		for (Correspondence c : hashedConfidences.keySet()) {
			c.setConfidence(hashedConfidences.get(c));
			joinedMapping.push(c);
		}
		return joinedMapping;
	}
	

	public double getConfidenceTotal() {
		double total = 0.0d;
		for (Correspondence c : this) {
			total += c.getConfidence();
		}
		return total;
	}
	
	public void removeCorrespondencesWithNSPrefix(String ns) {
		ArrayList<Correspondence> filteredCorrespondences = new ArrayList<Correspondence>();
		for (Correspondence c : this) {
			if (!(c.getSourceNamespace().startsWith(ns) || c.getTargetNamespace().startsWith(ns))) {
				filteredCorrespondences.add(c);
				
			}
		}
		this.setCorrespondences(filteredCorrespondences);
	}

	/**
	* @return -1 if this is less than that which is the case if trust of this
	* is greater than trust of that
	*/
	public int compareTo(Alignment that) {
		double thisTrust = this.getConfidenceTotal();
		double thatTrust = that.getConfidenceTotal();
		if (thisTrust < thatTrust) { return 1; }
		else if (thisTrust == thatTrust) { return 0; }
		else { return -1; }
	}
	
	/**
	* Returns a string representation of this mapping. 
	*/
	private String toSomeString(boolean verbose) {
		if (this.size() == 0) { return "[empty mapping]\n"; }
		StringBuffer sb = new StringBuffer();
		ArrayList<Correspondence> sortedCorrespondences = this.getCorrespondences();
		Collections.sort(sortedCorrespondences);
		Collections.reverse(sortedCorrespondences);	
		// sb.append("Mapping of size " + this.correspondences.size() + "\n");
		
		for (Correspondence c : sortedCorrespondences) {
			if (verbose) { sb.append(c.toString() + "\n"); }
			else { sb.append(c.toShortString() + "\n"); }
		}
		return sb.toString();
	}
	




}
