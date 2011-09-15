package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.client.datastreams.InternalDatastream;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class InternalDatastreamImpl extends AbstractDatastream implements InternalDatastream {
    public InternalDatastreamImpl(DatastreamProfile datastreamProfile, DigitalObject digitalObject,
                                  CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
    }

    @Override
    public void replace(String content) {
        throw new IllegalAccessError("Not implemented yet");
    }
}
