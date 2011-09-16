package dk.statsbiblioteket.doms.client.util;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.IOException;

public class StringUtils {
	
	/**
	 * @param names Strengen som skal splittes op.
	 * @param delimiter Den streng som bruges til markering af delstrenge.
	 */
	public static String[] split(String names, String delimiter) {
		if (names == null)
			return null;
		StringTokenizer tokens = new StringTokenizer( names, delimiter );
		String[] tokenName = new String[ tokens.countTokens()];
		for( int i=0;tokens.hasMoreTokens(); i++ ) {
			tokenName[i] = tokens.nextToken();
		}
		return tokenName;
	}

	/**
	 * Method to escape backslashs in a string.
	 * @param str
	 */
	public static String escapeSlashes(String str) {
		String[] res = split(str,"\\");
		String rv = "";
		boolean firsttime = true;
		for (int i=0; i < res.length; i++) {
			if (firsttime) { 
				rv = res[i];
				firsttime = false;
			} else
				rv += "\\\\"+res[i];
		}
		return rv;
	}

	/**
	 * Method to read an integer from a string.
	 * @param str
	 */
	public static int extractInt(String str)
	{
		int res = 0;
		try {
			res = Integer.parseInt(str);
		}
		catch (NumberFormatException numberFormatEx) {
			res = 0;
		}
		return res;
	}
	
	public static int extractInt(String str, int defaultValue)
	{
		int res = defaultValue;
		try {
			res = Integer.parseInt(str);
		}
		catch (NumberFormatException numberFormatEx) {
			res = defaultValue;
		}
		return res;
	}

    public static String readStream(InputStream is) throws IOException {
        if (is != null) {

            try {
                StringBuilder buf = new StringBuilder();
                int i = is.read();
                while (i!=-1)
                {
                    buf.append((char)i);
                    i = is.read();
                }
                return buf.toString();
            }
            finally {
                is.close();
            }
        }
        return null;

    }
}
