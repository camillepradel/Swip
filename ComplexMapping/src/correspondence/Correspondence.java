package correspondence;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import complexMapping.ComplexMappingException;

public class Correspondence {
	
	//constants to identify the type of a correspondence
	public static final int QUALIFIED_RESTRICTION = 0;
	public static final int UNQUALIFIED_RESTRICTION = 1;
	public static final int VALUE_RESTRICTION = 2;
	public static final int PROPERTY_CHAIN = 3;
	
	//variables to save information about the correspondence and its entities
	private OWLClass classOnt1;
	private OWLObjectProperty objectPropertyOnt2;
	private OWLClass classOnt2;
	private OWLDataProperty dataPropertyOnt2;
	private String data;
	private OWLDataProperty dataPropOnt1;
	private OWLObjectProperty objectProperty1Ont2;
	private OWLDataProperty dataProperty2Ont2;
	private volatile int hashCode;
	//identifier = all URIs and literals of the correspondence together in one string
	private String identifier;
	//one of the type defined above
	private int type;
	private double similaritySum;
	
	/**
	 * Constructor for complex correspondence of type Qualified and Unqualified Restriction. 
	 * 	
	 * @param classOnt1
	 * @param objectPropertyOnt2
	 * @param classOnt2
	 * @param identifier
	 * @param type
	 */
	public Correspondence(OWLClass classOnt1, OWLObjectProperty objectPropertyOnt2, OWLClass classOnt2, String identifier, int type, double similaritySum) {
		this.classOnt1 = classOnt1;
		this.objectPropertyOnt2 = objectPropertyOnt2;
		this.classOnt2 = classOnt2;
		this.identifier = identifier;		
		this.type = type;
		this.similaritySum = similaritySum;
	}
	
	/**
	 * Constructor for complex correspondence of type Value Restriction.
	 * 
	 * @param classOnt1
	 * @param dataPropertyOnt2
	 * @param data
	 * @param identifier
	 * @param type
	 */
	public Correspondence(OWLClass classOnt1, OWLDataProperty dataPropertyOnt2, String data, String identifier, int type) {
		this.classOnt1 = classOnt1;
		this.dataPropertyOnt2 = dataPropertyOnt2;
		this.data = data;
		this.identifier = identifier;
		this.type = type;
	}
	
	/**
	 * Constructor for complex correspondence of type Property Chain.
	 * 
	 * @param dataPropOnt1
	 * @param objectProperty1Ont2
	 * @param dataProperty2Ont2
	 * @param identifier
	 * @param type
	 */
	public Correspondence(OWLDataProperty dataPropOnt1, OWLObjectProperty objectProperty1Ont2, OWLDataProperty dataProperty2Ont2, String identifier, int type, double similaritySum) {
		this.dataPropOnt1 = dataPropOnt1;
		this.objectProperty1Ont2 = objectProperty1Ont2;
		this.dataProperty2Ont2 = dataProperty2Ont2;
		this.identifier = identifier;
		this.type = type;
		this.similaritySum = similaritySum;
	}
	
	/**
	 * Print a correspondence in fist-order-logic format with full URIs of the entities.
	 */
	public String toString(){
		switch(type) {
		case UNQUALIFIED_RESTRICTION: 
			return "Forall x ("+classOnt1.getIRI().toString() + "(x) <-> Exists y (" + objectPropertyOnt2.getIRI()+"(y,x) and " + classOnt2.getIRI() + "(x)))";
		case QUALIFIED_RESTRICTION:
			return "Forall x ("+classOnt1.getIRI().toString() + "(x) <-> Exists y (" + objectPropertyOnt2.getIRI()+"(x,y) and " + classOnt2.getIRI() + "(y)))";
		case VALUE_RESTRICTION:
			return "Forall x ("+classOnt1.getIRI() + "(x) <-> " + dataPropertyOnt2.getIRI() + "(x,"+data+"))";
		case PROPERTY_CHAIN:
			return "Forall x,z ("+ dataPropOnt1.getIRI()+ "(x,z) <-> Exists y (" + objectProperty1Ont2.getIRI() + "(x,y) and " + dataProperty2Ont2.getIRI() + "(y,z)))";
		default:	
			//bad parameter, throw exception (try because the method should overwrite the toString method and this method does not throw any exception)
			try {
					throw new ComplexMappingException(ComplexMappingException.BAD_PARAMETER, "Wrong correspondece type.");
				} catch (ComplexMappingException e) {
					e.printStackTrace();
				}
		}
		return "";
	}
	
	/** 
	 *  
	 *  
	 * @return The correspondence in string format without whole URIs, only with the important parts to identify the entity.
	 * @throws ComplexMappingException
	 */
	public String toShortString() throws ComplexMappingException {
		switch(type) {
		case UNQUALIFIED_RESTRICTION: 
			return "Forall x ("+ computeShortURI(classOnt1.getIRI().toString()) + "(x) <-> " +
					"Exists y (" + computeShortURI(objectPropertyOnt2.getIRI().toString())+"(y,x) and " + 
					computeShortURI(classOnt2.getIRI().toString()) + "(x)))";
		case QUALIFIED_RESTRICTION:
			return "Forall x ("+ computeShortURI(classOnt1.getIRI().toString()) + "(x) <-> " +
					"Exists y(" + computeShortURI(objectPropertyOnt2.getIRI().toString())+"(x,y) and " +
					computeShortURI(classOnt2.getIRI().toString()) + "(y)))";
		case VALUE_RESTRICTION:
			return "Forall x ("+ computeShortURI(classOnt1.getIRI().toString()) + "(x) <-> " +
			computeShortURI(dataPropertyOnt2.getIRI().toString()) + "(x,"+data+"))";
		case PROPERTY_CHAIN:
			return "Forall x,z ("+ computeShortURI(dataPropOnt1.getIRI().toString()) + "(x,z) <-> " +
					"Exists y (" + computeShortURI(objectProperty1Ont2.getIRI().toString()) +
					"(x,y) and " + computeShortURI(dataProperty2Ont2.getIRI().toString()) + "(y,z)))";
		default:	
			//bad parameter, throw exception 
			throw new ComplexMappingException(ComplexMappingException.BAD_PARAMETER, "Wrong correspondece type.");		
		}
	}
	
	/**
	 * Compute the short URI which only contains the important names and not the whole URI.
	 * 
	 * @param uri
	 * @return
	 */
	private String computeShortURI(String uri) {
		String[] splittedUri = uri.split("/");
		//in the benchmark ontologies the URI often contains the word onto as name of the ontology
		if(splittedUri[splittedUri.length-1].contains("onto")) {
			return splittedUri[splittedUri.length-2] + "/" + splittedUri[splittedUri.length-1];
		}
		//take the last part of the URI
		else {
			return splittedUri[splittedUri.length-1];
		}		
	}
	
	/**
	 * To be able to compare the correspondences, override the equals method.
	 */
	@Override
	public boolean equals(Object o) {
		if ( !(o instanceof Correspondence) ) {
			return false;
		}

		Correspondence c = (Correspondence) o;
		
		return this.identifier.equals(c.identifier);
	}
	
	/**
	 * Override the hash code method to match the correspondences right.
	 */
	@Override
	public int hashCode() {
	 int result = hashCode;

	 if ( result == 0 ) {
	  result = 17;
	  result = 31 * result + ( classOnt1 == null ? 0 : classOnt1.hashCode() );
	  result = 31 * result + ( objectPropertyOnt2 == null ? 0 : objectPropertyOnt2.hashCode() );
	  result = 31 * result + ( classOnt2 == null ? 0 : classOnt2.hashCode() );
	  result = 31 * result + ( dataPropertyOnt2 == null ? 0 : dataPropertyOnt2.hashCode() );
	  result = 31 * result + ( data == null ? 0 : data.hashCode() );
	  result = 31 * result + ( dataPropOnt1 == null ? 0 : dataPropOnt1.hashCode() );
	  result = 31 * result + ( objectProperty1Ont2 == null ? 0 : objectProperty1Ont2.hashCode() );
	  result = 31 * result + ( dataProperty2Ont2 == null ? 0 : dataProperty2Ont2.hashCode() );
	 }

	 return result;
	}

	public OWLClass getClassOnt1() {
		return classOnt1;
	}

	public OWLObjectProperty getObjectPropertyOnt2() {
		return objectPropertyOnt2;
	}

	public OWLClass getClassOnt2() {
		return classOnt2;
	}

	public OWLDataProperty getDataPropertyOnt2() {
		return dataPropertyOnt2;
	}

	public String getData() {
		return data;
	}

	public OWLDataProperty getDataPropOnt1() {
		return dataPropOnt1;
	}

	public OWLObjectProperty getObjectProperty1Ont2() {
		return objectProperty1Ont2;
	}

	public OWLDataProperty getDataProperty2Ont2() {
		return dataProperty2Ont2;
	}
	
	public int getType() {
		return type;
	}
	
	public double getSimilaritySum() {
		return similaritySum;
	}
	
	public String getIdentifier() {
		return identifier;
	}	

}
