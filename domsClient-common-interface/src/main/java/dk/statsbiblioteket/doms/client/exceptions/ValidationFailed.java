package dk.statsbiblioteket.doms.client.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 11/1/11
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationFailed extends ServerOperationFailed {
    public ValidationFailed(String message) {
        super(message);
    }

    public ValidationFailed(String message, Exception e) {
        super(message, e);
    }
}
