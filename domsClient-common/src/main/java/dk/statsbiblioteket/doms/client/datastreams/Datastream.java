package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.exceptions.MyXMLReadException;
import dk.statsbiblioteket.doms.client.exceptions.MyXMLWriteException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;

import java.io.IOException;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Datastream {
    DigitalObject getDigitalObject();

    String getId();

    String getChecksumType();

    String getChecksumValue();

    String getFormatURI();

    String getMimeType();

    String getLabel();

    String getContents() throws ServerOperationFailed;

    Set<DatastreamDeclaration> getDeclarations() throws ServerOperationFailed;

    SDOParsedXmlElement getSDOParsedDocument()
            throws ServerOperationFailed, IOException, MyXMLWriteException, MyXMLReadException;
}
