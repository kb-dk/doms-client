package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DataObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;

/**
 * Data objects are the objects that actually holds the data in DOMS. TODO implement
 */
public class DataObjectImpl extends AbstractDigitalObject implements DataObject {


    public DataObjectImpl(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
