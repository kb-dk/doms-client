package dk.statsbiblioteket.doms.client.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 10/24/11
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyXMLWriteException extends Exception {
    public MyXMLWriteException(String message) {
        super(message);
    }

    public MyXMLWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
