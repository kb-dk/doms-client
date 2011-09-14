package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;

/**
 * Content Model objects are the objects that holds the structure of the objects in doms. TODO implement
 */
public class ContentModelObject extends AbstractDigitalObject {


    public ContentModelObject(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory) {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
