package dk.statsbiblioteket.doms.client.exceptions;

import javax.xml.transform.TransformerException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/24/11
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLParseException extends Exception {

    public XMLParseException(String message) {
        super(message);
    }

    public XMLParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
