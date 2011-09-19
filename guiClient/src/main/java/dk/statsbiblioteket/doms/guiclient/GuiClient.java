package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.*;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;

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
    List<SearchResult> search(String query, int offset, int pageLength) throws ServerOperationFailed;

    String getPasswordForUser(String username, List<String> roles) throws ServerOperationFailed;

}
