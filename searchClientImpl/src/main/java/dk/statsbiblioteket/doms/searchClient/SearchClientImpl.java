package dk.statsbiblioteket.doms.searchClient;

import dk.statsbiblioteket.doms.client.DomsWSClient;
import dk.statsbiblioteket.doms.client.DomsWSClientImpl;
import dk.statsbiblioteket.doms.client.SearchResult;
import dk.statsbiblioteket.doms.client.ServerOperationFailed;

import java.net.URL;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/5/11
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchClientImpl implements SearchClient{


    private DomsWSClient domsclient;

    public SearchClientImpl(URL url, String username, String password){//TODO
        domsclient = new DomsWSClientImpl();
        domsclient.setCredentials(url,username,password);
    }

    protected DomsWSClient getDomsclient() {
        return domsclient;
    }

    @Override
    public List<SearchResult> search(String query, int offset, int pageLength) throws ServerOperationFailed {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
