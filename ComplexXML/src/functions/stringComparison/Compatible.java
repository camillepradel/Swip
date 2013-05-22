package functions.stringComparison;

/**
 *
 * @author Dominique Ritze
 *
 * Implements the compatible condition between two datatype ranges.
 *
 */
public class Compatible implements StringComparison{
	
	/**
         * Check if the two datatype ranges are compatible, for example
         * string is compatible with every other type but
         * boolean is not compatible with date.
         *
         * @param range1
         * @param range2
         * @return True if the ranges are compatible, false otherwise.
         */
	public boolean compute(String range1, String range2) {


                if(range1.endsWith("string") || range2.endsWith("string") || range1.equals(range2)) {
                    return true;
                }		
		
		//first range compatible with every string or positive integer (and every type which can be an integer)
		if(range1.endsWith("boolean") || range1.endsWith("base64Binary") || range1.endsWith("hexBinary")) {
			if(!range2.endsWith("normalizedString") || !range2.endsWith("string") || !range2.endsWith("token") ||
					!range2.endsWith("byte") || !range2.endsWith("decimal") || !range2.endsWith("int") ||
					!range2.endsWith("integer") || !range2.endsWith("long") || !range2.endsWith("nonNegativeInteger") ||
					!range2.endsWith("positiveInteger") || !range2.endsWith("short") || !range2.endsWith("unsignedByte") 
					|| !range2.endsWith("unsignedShort") || !range2.endsWith("unsignedLong") || !range2.endsWith("unsignedInt")) {
				return false;
			}
		}
		//first range only compatible with every string
		if(range1.endsWith("anyURI") || range1.endsWith("language")) {
			if(!range2.endsWith("normalizedString") || !range2.endsWith("string") || !range2.endsWith("token")) {
				return false;
			}
		}
		//first range = number
		if(range1.endsWith("byte") || range1.endsWith("decimal") || range1.endsWith("double") || range1.endsWith("float")
				|| range1.endsWith("int") || range1.endsWith("integer") || range1.endsWith("long") || range1.endsWith("negativeInteger")
				|| range1.endsWith("nonNegativeInteger") || range1.endsWith("positiveInteger") || range1.endsWith("nonPositiveInteger")
				|| range1.endsWith("short") || range1.endsWith("unsignedByte") || range1.endsWith("unsignedShort")|| range1.endsWith("unsignedLong")
				|| range1.endsWith("unsignedInt")) {
			if(range2.endsWith("anyURI") || range2.endsWith("language")) {
				return false;
			}
		}
		else {
			if(range1.endsWith("negativeInteger") || range1.endsWith("nonPositiveInteger")) {
				if(range2.endsWith("positiveInteger")) {
					return false;
				}
			}
			if(range1.endsWith("positiveInteger") || range1.endsWith("nonNegativeInteger")) {
				if(range2.endsWith("negativeInteger")) {
					return false;
				}
			}									
		}												
		//first range = calendar type
		if(range1.endsWith("date") || range1.endsWith("dateTime") || range1.endsWith("duration") || range1.endsWith("gDay")
				|| range1.endsWith("gMonth") || range1.endsWith("gMonthDay") || range1.endsWith("gYear") || range1.endsWith("gYearMonth")
				|| range1.endsWith("time")) {
			if(range2.endsWith("boolean") || range2.endsWith("anyURI") || range2.endsWith("language") || range2.endsWith("negativeInteger")
					|| range2.endsWith("nonPositiveInteger")) {
				return false;
			}
		}
		else {
			if(range1.endsWith("date")) {
				if(!range2.endsWith("dateTime") || !range2.endsWith("gDay") || !range2.endsWith("gMonthDay")) {
					return false;
				}									
			}
			if(range1.endsWith("dateTime")) {
				if(!range2.endsWith("date") || !range2.endsWith("gDay") || !range2.endsWith("gMonthDay")) {
					return false;
				}									
			}
			if(range1.endsWith("duration")) {
				if(!range2.endsWith("time")) {
					return false;
				}									
			}
			if(range1.endsWith("gDay")) {
				if(!range2.endsWith("dateTime") || !range2.endsWith("date") || !range2.endsWith("gMonthDay")) {
					return false;
				}									
			}
			if(range1.endsWith("gMonth")) {
				if(!range2.endsWith("gYearMonth") || !range2.endsWith("gMonthDay")) {
					return false;
				}									
			}
			if(range1.endsWith("gMonthDay")) {
				if(!range2.endsWith("dateTime") || !range2.endsWith("gDay") || !range2.endsWith("date")) {
					return false;
				}									
			}
			if(range1.endsWith("gYearMonth")) {
				if(!range2.endsWith("gMonth")) {
					return false;
				}									
			}
		}
		return true;
	}
}
