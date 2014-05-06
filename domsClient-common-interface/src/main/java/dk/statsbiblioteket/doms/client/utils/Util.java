package dk.statsbiblioteket.doms.client.utils;

public class Util {


    /**
     * Extract an integer from a string.
     *
     * @param str The string from which to extract the integer.
     */
    public static int extractInt(String str) {
        int res = 0;
        if (str != null) {
            try {
                res = Integer.parseInt(str);
            } catch (NumberFormatException numberFormatEx) {
                res = 0;
            }
        }
        return res;
    }


}
