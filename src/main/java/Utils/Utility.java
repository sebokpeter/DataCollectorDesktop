package Utils;

/**
 * Contains utility methods
 *
 * @author Peter
 */
public class Utility {

    /**
     * Checks if the given string can be safely parsed to an integer.
     *
     * @param s The string that will be checked
     * @return True if the string can be parsed to an integer, false otherwise.
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}
