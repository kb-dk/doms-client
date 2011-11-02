package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 11/2/11
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DigitalObjectFactory {
    DigitalObject getDigitalObject(String pid) throws ServerOperationFailed;

    //CentralWebservice getApi();
}
