package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;


public abstract class SaveableDatastreamImpl extends AbstractDatastream{


    private CentralWebservice api;

    public SaveableDatastreamImpl(DatastreamProfile datastreamProfile, DigitalObject digitalObject,
                                  CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
        this.api = api;
    }

    public abstract void preSave() throws ServerOperationFailed;

    public abstract void postSave();

    public abstract void undoSave() throws ServerOperationFailed;


    public  void markAsDeleted(){

    }

    public void create(){

    }
}
