package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.client.datastreams.ExternalDatastream;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExternalDatastreamImpl extends SaveableDatastreamImpl implements ExternalDatastream {
    private String url;
    Logger logger = Logger.getLogger(ExternalDatastreamImpl.class.getName());

    private String originalURL;
    private CentralWebservice api;


    public ExternalDatastreamImpl(DatastreamProfile datastreamProfile, DigitalObject digitalObject,
                                  CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
        this.api = api;
        url = datastreamProfile.getUrl();
        originalURL = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void preSave() {
        logger.warning("Not implemented: Cannot save external datastream " + getId());
        //TODO
    }

    @Override
    public void postSave() {
        //TODO
    }

    @Override
    public void undoSave() {
        //TODO
    }
}
