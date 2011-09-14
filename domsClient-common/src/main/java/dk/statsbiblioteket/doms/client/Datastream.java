package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.DatastreamProfile;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/14/11
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Datastream {


    private DigitalObject digitalObject;

    public Datastream(DatastreamProfile datastreamProfile, DigitalObject digitalObject) {
        this.digitalObject = digitalObject;
    }
}
