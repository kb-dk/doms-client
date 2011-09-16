package dk.statsbiblioteket.doms.client.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/16/11
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
