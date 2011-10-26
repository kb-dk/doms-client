package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.client.datastreams.ExternalDatastream;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExternalDatastreamImpl extends AbstractDatastream implements ExternalDatastream {
    private String url;

    private String originalURL;


    public ExternalDatastreamImpl(DatastreamProfile datastreamProfile, DigitalObject digitalObject,
                                  CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
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
    }

    @Override
    public void postSave() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void undoSave() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
