package functions.stringComparison;

/**
 *
 * @author Dominique Ritze
 *
 * Implements the condition same between two strings.
 *
 */
public class Equal implements StringComparison{

        /**
         * Check if s1 is the same string as s2.
         *
         * @param s1
         * @param s2
         * @return True if they are the same.
         */
	public boolean compute(String s1, String s2) {
		if(s1.equals(s2) && !s1.isEmpty() && ! s2.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

}
