package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.*;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/5/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GuiClient extends DomsClient {

    /**
     * @param   query       the search string.
     * @param   offset      the first result of the search
     * @param   pageLength  the max number of results
     * @return  A list of SearchResult objects
     */
    List<SearchResult> search(String query, int offset, int pageLength) throws ServerOperationFailed, InvalidCredentialsException;

    /**
     * Get the <code>FedoraState</code> for the DOMS object with the specified <code>PID</code>.
     *
     * @param pid       The PID identifying the object of intrest.
     * @return A FedoraState enum indicating the state of the object.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *          If the object cannot be found.
     */
    FedoraState getState(String pid) throws ServerOperationFailed;

    /**
     * @param  PID  The object PID
     * @return Profile The object profile
     */
    DigitalObjectProfile getProfile(String PID) throws ServerOperationFailed;
}
