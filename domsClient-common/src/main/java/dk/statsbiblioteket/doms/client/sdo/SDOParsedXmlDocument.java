package dk.statsbiblioteket.doms.client.sdo;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.impl.sdo.SDOParsedXmlElementImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SDOParsedXmlDocument {
    SDOParsedXmlElementImpl getRootSDOParsedXmlElement();

    boolean isValid();

    boolean isAbstract();

    String dumpToString() throws IOException;

    void saveToDatastream() throws IOException;

    Datastream getDatastream();
}
