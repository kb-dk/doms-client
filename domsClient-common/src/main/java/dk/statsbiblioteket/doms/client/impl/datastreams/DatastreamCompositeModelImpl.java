package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamCompositeModel;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/27/11
 * Time: 9:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatastreamCompositeModelImpl extends AbstractDatastream implements DatastreamCompositeModel{

    public DatastreamCompositeModelImpl(DatastreamProfile datastreamProfile, DigitalObject digitalObject, CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
    }

    
}
