package functions.stringComparison;

/**
 *
 * @author Dominique Ritze
 *
 * Implementation of the condition contained between two strings.
 *
 */
public class Contained implements StringComparison{

        /**
         * Check if s1 is contained in s2.
         *
         * @param s1
         * @param s2
         * @return True if s1 is contained in s2, false otherwise.
         */
	public boolean compute(String s1, String s2) {
		if(s2.contains(s1)) {
			return true;
		}
		else {
			return false;
		}
	}

}
