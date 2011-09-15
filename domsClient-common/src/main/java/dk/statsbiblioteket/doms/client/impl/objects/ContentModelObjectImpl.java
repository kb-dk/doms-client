package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;

/**
 * Content Model objects are the objects that holds the structure of the objects in doms. TODO implement
 */
public class ContentModelObjectImpl extends AbstractDigitalObject implements ContentModelObject {


    public ContentModelObjectImpl(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
