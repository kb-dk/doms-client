package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.ServerOperationFailed;
import dk.statsbiblioteket.doms.searchClient.SearchClient;
import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/5/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GuiClient extends SearchClient {

     /**
     * Get the XML content of the datastream identified by
     * <code>datastreamID</code> of the DOMS object identified by
     * <code>objectPID</code>.
     *
     * @param objectPID    ID of the DOMS object to retrieve the datastream contents of.
     * @param datastreamID ID of the datastream to get the contents of.
     * @return <code>Document</code> containing the datastream XML contents.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *          if the datastream contents cannot be retrieved.
     */
    Document getDataStream(String objectPID, String datastreamID)
            throws ServerOperationFailed;
}
