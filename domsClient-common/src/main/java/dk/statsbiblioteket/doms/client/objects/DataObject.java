package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;

/**
 * Data objects are the objects that actually holds the data in DOMS. TODO implement
 */
public class DataObject extends AbstractDigitalObject {


    public DataObject(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory) {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
    }
}