package dk.statsbiblioteket.doms.client.objects.stubs;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;

import java.util.Set;

public class DatastreamStub implements Datastream {
    @Override
    public DigitalObject getDigitalObject() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getChecksumType() {
        return null;
    }

    @Override
    public String getChecksumValue() {
        return null;
    }

    @Override
    public String getFormatURI() {
        return null;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getContents() throws ServerOperationFailed {
        return null;
    }

    @Override
    public Set<DatastreamDeclaration> getDeclarations() throws ServerOperationFailed {
        return null;
    }

    @Override
    public SDOParsedXmlDocument getSDOParsedDocument() throws ServerOperationFailed, XMLParseException {
        return null;
    }
}
