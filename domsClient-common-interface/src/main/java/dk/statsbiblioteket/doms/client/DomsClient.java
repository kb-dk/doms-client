package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/6/11
 * Time: 8:51 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DomsClient {


    /**
     * @return Profile The object factory
     */
    DigitalObjectFactory getFactory() throws ServerOperationFailed;


    boolean testLogin() throws ServerOperationFailed;

}
