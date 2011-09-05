package dk.statsbiblioteket.doms.searchClient;

import dk.statsbiblioteket.doms.client.SearchResult;
import dk.statsbiblioteket.doms.client.ServerOperationFailed;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/5/11
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SearchClient {

    /**
     * @param   query       the search string.
     * @param   offset      the first result of the search
     * @param   pageLength  the max number of results
     * @return  A list of SearchResult objects
     */
    List<SearchResult> search(String query, int offset, int pageLength) throws ServerOperationFailed;
}
