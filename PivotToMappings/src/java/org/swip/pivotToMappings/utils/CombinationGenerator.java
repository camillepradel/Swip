package org.swip.pivotToMappings.utils;

//--------------------------------------
// Systematically generate combinations.
// blindly copied from http://www.merriampark.com/comb.htm
//--------------------------------------
import java.math.BigInteger;

public class CombinationGenerator {

    private int[] a;
    private int n;
    private int r;
    private BigInteger numLeft;
    private BigInteger total;

    //------------
    // Constructor
    //------------
    public CombinationGenerator(int n, int r) {
        if (r > n) {
            throw new IllegalArgumentException();
        }
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.r = r;
        a = new int[r];
        BigInteger nFact = getFactorial(n);
        BigInteger rFact = getFactorial(r);
        BigInteger nminusrFact = getFactorial(n - r);
        total = nFact.divide(rFact.multiply(nminusrFact));
        reset();
    }

    //------
    // Reset
    //------
    public void reset() {
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }
        numLeft = new BigInteger(total.toString());
    }

    //------------------------------------------------
    // Return number of combinations not yet generated
    //------------------------------------------------
    public BigInteger getNumLeft() {
        return numLeft;
    }

    //-----------------------------
    // Are there more combinations?
    //-----------------------------
    public boolean hasMore() {
        return numLeft.compareTo(BigInteger.ZERO) == 1;
    }

    //------------------------------------
    // Return total number of combinations
    //------------------------------------
    public BigInteger getTotal() {
        return total;
    }

    //------------------
    // Compute factorial
    //------------------
    private static BigInteger getFactorial(int n) {
        BigInteger fact = BigInteger.ONE;
        for (int i = n; i > 1; i--) {
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        }
        return fact;
    }

    //--------------------------------------------------------
    // Generate next combination (algorithm from Rosen p. 286)
    //--------------------------------------------------------
    public int[] getNext() {

        if (numLeft.equals(total)) {
            numLeft = numLeft.subtract(BigInteger.ONE);
            return a;
        }

        int i = r - 1;
        while (a[i] == n - r + i) {
            i--;
        }
        a[i] = a[i] + 1;
        for (int j = i + 1; j < r; j++) {
            a[j] = a[i] + j - i;
        }

        numLeft = numLeft.subtract(BigInteger.ONE);
        return a;

    }

    // retourne k parmi n
    public static long binomialCoefficient(int n, int k) {
        if (n - k == 1 || k == 1) {
            return n;
        }
        long[][] b = new long[n + 1][n - k + 1];
        b[0][0] = 1;
        for (int i = 1; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                if (i == j || j == 0) {
                    b[i][j] = 1;
                } else if (j == 1 || i - j == 1) {
                    b[i][j] = i;
                } else {
                    b[i][j] = b[i - 1][j - 1] + b[i - 1][j];
                }
            }
        }
        return b[n][n - k];
    }

    public static void main(String args[]) {
        CombinationGenerator cg = new CombinationGenerator(5, 3);
        while (cg.hasMore()) {
            int[] indices = cg.getNext();
            for (int i : indices) {
                System.out.print(i + " - ");
            }
            System.out.println();
        }
    }
}
