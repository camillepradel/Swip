package correspondenceComputations;

/**
 * 
 * @author Dominique Ritze
 * 
 * In this class all values are defined which can be changed to modify the result of the complex ontology matching.
 * Therefore also at lot of getters and setters are contained.
 *
 */
public class ComputationSetting {
	
	/**
	 * If the similarity value of the datatype property and the object property is higher than this
	 * variable, the algorithm continues the computation else it will terminate.
	 */
	public static double propertyChainDatatypeObjectName = 0.6; 
	
	/**
	 * All classes which have a similarity value higher than this variable to a object property will be collected.
	 */
	public static double unqualifiedRestrictionClassSimilarToPropertyName = 0.6; 
	
	/**
	 * A class will be chosen for further computations if the similarity to the name of a range is higher than this variable.
	 */
	public static double valueRestrictionClassSimilarToRangeName = 0.6;
	
	/**
	 * The similarity value of the class name and the property name has to be higher than this variable 
	 * if there is a complex correspondence of the type value restriction on a datatype property.
	 */
	public static double valueRestrictionPartOfClassSimilarToPropertyName = 0.6; 
	
	/**
	 * If the first part of the class name is similar, a similarity value higher than this variable, 
	 * to the property name, a correspondence with the class and the property + true is found.
	 */
	public static double valueRestrictionFirstPartClassToPropertyName = 0.6; 
	
	/**
	 * If the class name and the name of the range have a higher similarity value than the variable
	 * there might be a correspondence between them.
	 */
	public static double qualifiedRestrictionClassRangeName = 0.7; 
	
	/**
	 * All classes where the similarity value with the superclass of a property is higher than 
	 * this variable will be chosen for further computation.
	 */
	public static double qualifiedRestrictionPropertySuperclass = 0.7; 
	
	/**
	 * A character is a delimiter if the character occurs more than delimiterProcent times in the ontology.
	 */
	public static double delimiterPercent = 0.05;

	public static double getPropertyChainDatatypeObjectName() {
		return propertyChainDatatypeObjectName;
	}

	public static double getUnqualifiedRestrictionClassSimilarToPropertyName() {
		return unqualifiedRestrictionClassSimilarToPropertyName;
	}

	public static double getValueRestrictionClassSimilarToRangeName() {
		return valueRestrictionClassSimilarToRangeName;
	}

	public static double getValueRestrictionPartOfClassSimilarToPropertyName() {
		return valueRestrictionPartOfClassSimilarToPropertyName;
	}

	public static double getValueRestrictionFirstPartClassToPropertyName() {
		return valueRestrictionFirstPartClassToPropertyName;
	}

	public static double getQualifiedRestrictionClassRangeName() {
		return qualifiedRestrictionClassRangeName;
	}

	public static double getQualifiedRestrictionPropertySuperclass() {
		return qualifiedRestrictionPropertySuperclass;
	}

	public static double getDelimiterPercent() {
		return delimiterPercent;
	}

	public static void setDelimiterPercent(double delimiterProcent) {
		ComputationSetting.delimiterPercent = delimiterProcent;
	}

	public static void setPropertyChainDatatypeObjectName(double compoundPropertyDatatypeObjectName) {
		ComputationSetting.propertyChainDatatypeObjectName = compoundPropertyDatatypeObjectName;
	}

	public static void setUnqualifiedRestrictionClassSimilarToPropertyName(double classSimilarToPropertyName) {
		ComputationSetting.unqualifiedRestrictionClassSimilarToPropertyName = classSimilarToPropertyName;
	}

	public static void setValueRestrictionClassSimilarToRangeName(double rangeRestrictionClassSimilarToRangeName) {
		ComputationSetting.valueRestrictionClassSimilarToRangeName = rangeRestrictionClassSimilarToRangeName;
	}

	public static void setValueRestrictionPartOfClassSimilarToPropertyName(double rangeRestrictionPartOfClassSimilarToPropertyName) {
		ComputationSetting.valueRestrictionPartOfClassSimilarToPropertyName = rangeRestrictionPartOfClassSimilarToPropertyName;
	}

	public static void setValueRestrictionFirstPartClassToPropertyName(double rangeRestrictionFirstPartClassToPropertyName) {
		ComputationSetting.valueRestrictionFirstPartClassToPropertyName = rangeRestrictionFirstPartClassToPropertyName;
	}

	public static void setQualifiedRestrictionClassRangeName(double classSimilarToRangeClassRangeName) {
		ComputationSetting.qualifiedRestrictionClassRangeName = classSimilarToRangeClassRangeName;
	}

	public static void setQualifiedRestrictionPropertySuperclass(double classSsimiarToRangePropertySuperclass) {
		ComputationSetting.qualifiedRestrictionPropertySuperclass = classSsimiarToRangePropertySuperclass;
	}
	
}
