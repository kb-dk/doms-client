package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * This class represents a datastream. TODO implement
 */
public class Datastream {


    private DigitalObject digitalObject;

    public Datastream(DatastreamProfile datastreamProfile, DigitalObject digitalObject) {
        this.digitalObject = digitalObject;
    }
}
