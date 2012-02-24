package dk.statsbiblioteket.doms.ingestclient;

import dk.statsbiblioteket.doms.client.impl.AbstractDomsClient;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/5/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class IngestClientImpl extends AbstractDomsClient implements
        IngestClient {
    public IngestClientImpl(URL url, String username, String password){
        super(url, username, password);
    }
}
