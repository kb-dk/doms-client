package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.TemplateObject;

/**
 * Template objects are objects that can be cloned to make new objects.
 */
public class TemplateObjectImpl extends AbstractDigitalObject implements TemplateObject {

    public TemplateObjectImpl(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);
    }


    @Override
    public DigitalObject clone() {
        throw new IllegalAccessError("Method not implemented");
    }
}
