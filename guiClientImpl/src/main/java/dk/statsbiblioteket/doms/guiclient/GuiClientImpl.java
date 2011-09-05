package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.DomsWSClient;
import dk.statsbiblioteket.doms.client.DomsWSClientImpl;
import dk.statsbiblioteket.doms.client.SearchResult;
import dk.statsbiblioteket.doms.client.ServerOperationFailed;
import dk.statsbiblioteket.doms.searchClient.SearchClient;
import dk.statsbiblioteket.doms.searchClient.SearchClientImpl;
import org.w3c.dom.Document;

import java.net.URL;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/5/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuiClientImpl extends SearchClientImpl implements GuiClient{


    public GuiClientImpl(URL url, String username, String password){
        super(url,username,password);//TODO
    }

    @Override
    public Document getDataStream(String objectPID, String datastreamID) throws ServerOperationFailed {
        return getDomsclient().getDataStream(objectPID,datastreamID);
    }

}
