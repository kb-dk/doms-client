package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.datastreams.InternalDatastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

import java.lang.String;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class InternalDatastreamImpl extends AbstractDatastream implements InternalDatastream {

    private String contents;

    private String originalContents;
    private CentralWebservice api;

    public InternalDatastreamImpl(DatastreamProfile datastreamProfile, DigitalObject digitalObject,
                                  CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
        this.api = api;
    }


    @Override
    public synchronized void replace(String content) {
        contents = content;
    }

    @Override
    public synchronized String getContents() throws ServerOperationFailed {
        if (contents != null){
            return contents;
        }
        contents = super.getContents();
        originalContents = contents;
        return contents;
    }

    @Override
    public void preSave() throws ServerOperationFailed {
        try {
            api.modifyDatastream(getDigitalObject().getPid(),getId(),contents,"Save from GUI");
        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
            throw new ServerOperationFailed(e);
        }

    }

    @Override
    public void postSave() {
        originalContents = contents;
    }

    @Override
    public void undoSave() throws ServerOperationFailed {
         try {
            api.modifyDatastream(getDigitalObject().getPid(),getId(),originalContents,"Save from GUI");
        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
            throw new ServerOperationFailed(e);
        }
        contents = originalContents;
    }
}
