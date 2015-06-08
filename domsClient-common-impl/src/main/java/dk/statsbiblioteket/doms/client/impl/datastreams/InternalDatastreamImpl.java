package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.client.datastreams.InternalDatastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class InternalDatastreamImpl extends SaveableDatastreamImpl implements InternalDatastream {

    static Logger logger = Logger.getLogger(InternalDatastreamImpl.class.getName());

    private String contents;

    private String originalContents;
    private CentralWebservice api;

    private boolean virtual;

    public InternalDatastreamImpl(DatastreamProfile datastreamProfile, DigitalObject digitalObject,
                                  CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
        this.api = api;
        virtual = false;
    }

    public InternalDatastreamImpl(DatastreamProfile datastreamProfile, DigitalObject digitalObject,
                                  CentralWebservice api, boolean virtual) {
        this(datastreamProfile, digitalObject, api);
        this.virtual = virtual;
    }


    @Override
    public synchronized void replace(String content) {
        setContent(content);
    }

    @Override
    public void setContent(String content) {
        contents = content;
    }

    @Override
    public synchronized String getContents() throws ServerOperationFailed {
        if (contents != null) {
            return contents;
        }
        if (!isVirtual()) {
            contents = super.getContents();
            originalContents = contents;
            return contents;
        }
        return "<placeholder/>";
    }

    @Override
    public void preSave() throws ServerOperationFailed, XMLParseException {
        logger.info("In preSave() for " + getId());
        if (hasBeenSDOparsed()) {
            logger.info("Saving to datastream for " + getSDOParsedDocument());
            getSDOParsedDocument().saveToDatastream();

        }
        if (contents == null || (!isVirtual() && originalContents == null)) {
            logger.info("Null contents, saving nothing for " + getId());
            return;
        }
        if (contents.equals(originalContents)) {
            logger.info("Contents unchanged. Not saving " + getId());
            return;
        }
        try {
            api.modifyDatastream(getDigitalObject().getPid(), getId(), contents, "Save from GUI");
            setVirtual(false);
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
        if (contents == null || originalContents == null) {
            return;
        }
        if (contents.equals(originalContents)) {
            return;
        }
        try {
            api.modifyDatastream(getDigitalObject().getPid(), getId(), originalContents, "Save from GUI");
        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
            throw new ServerOperationFailed(e);
        }
        contents = originalContents;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    @Override
    public void create() throws XMLParseException, ServerOperationFailed {
        preSave();
    }
}
