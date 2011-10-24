package dk.statsbiblioteket.doms.client.exceptions;

import javax.xml.transform.TransformerException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/24/11
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyXMLReadException extends Exception {

    public MyXMLReadException(String message) {
        super(message);
    }

    public MyXMLReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
