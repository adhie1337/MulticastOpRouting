package routing.util;

/**
 * A utility class that compares two nullable type variables.
 * @author PIAPAAI.ELTE
 */
public class CompareUtil {

    /**
     * General comparison.
     * @param a the first object
     * @param b the second object
     * @return true if they are the same.
     */
    public static Boolean compare(Object a, Object b) {
        if(a == null && b != null || a != null && b == null)
            return false;
        else if(a != null && b != null)
            return a.equals(b);

        return true;
    }
}
