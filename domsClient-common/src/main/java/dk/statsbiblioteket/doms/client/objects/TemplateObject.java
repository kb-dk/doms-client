package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.AbstractDigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;

/**
 * Template objects are objects that can be cloned to make new objects.
 */
public class TemplateObject extends AbstractDigitalObject {

    public TemplateObject(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);
    }
}
