package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.DomsClient;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;

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
    SearchResultList search(String query, int offset, int pageLength) throws ServerOperationFailed;

    String getPasswordForUser(String username, List<String> roles) throws ServerOperationFailed;

}
