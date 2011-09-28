package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

import java.io.InputStream;
import java.lang.String;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents a datastream. TODO implement
 */
public abstract class AbstractDatastream implements Datastream {


    private DigitalObject digitalObject;
    private CentralWebservice api;
    private String id;
    private String checksumType;
    private String checksumValue;
    private String formatURI;
    private String mimeType;
    private String label;


    public AbstractDatastream(DatastreamProfile datastreamProfile, DigitalObject digitalObject, CentralWebservice api) {
        this.digitalObject = digitalObject;
        this.api = api;
        id = datastreamProfile.getId();
        checksumType = datastreamProfile.getChecksum().getType();
        checksumValue = datastreamProfile.getChecksum().getValue();
        formatURI = datastreamProfile.getFormatUri();
        mimeType = datastreamProfile.getMimeType();
        label = datastreamProfile.getLabel();
    }

    @Override
    public DigitalObject getDigitalObject() {
        return digitalObject;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getChecksumType() {
        return checksumType;
    }

    @Override
    public String getChecksumValue() {
        return checksumValue;
    }

    @Override
    public String getFormatURI() {
        return formatURI;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getContents() throws ServerOperationFailed {
        try {
            return api.getDatastreamContents(digitalObject.getPid(),id);
        } catch (Exception e) {
            throw new ServerOperationFailed("Failed to load the datastream contents",e);
        }
    }

    @Override
    public Set<DatastreamDeclaration> getDeclarations() throws ServerOperationFailed {
        Set<DatastreamDeclaration> datastreamDeclarations = new HashSet<DatastreamDeclaration>();
        List<ContentModelObject> contentmodels = digitalObject.getType();
        for (ContentModelObject contentmodel : contentmodels) {
            DatastreamModel dsmodel = contentmodel.getDsModel();
            List<DatastreamDeclaration> declerations = dsmodel.getDatastreamDeclarations();
            for (DatastreamDeclaration decleration : declerations) {
                if (decleration.getName().equals(this.getId())){
                    datastreamDeclarations.add(decleration);
                }
            }
        }
        return datastreamDeclarations;
    }
}
