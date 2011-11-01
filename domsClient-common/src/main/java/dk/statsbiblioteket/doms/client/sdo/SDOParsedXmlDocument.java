package dk.statsbiblioteket.doms.client.sdo;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;


/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SDOParsedXmlDocument {
    SDOParsedXmlElement getRootSDOParsedXmlElement();

    boolean isValid();

    boolean isAbstract();

    String dumpToString() throws XMLParseException;

    void saveToDatastream() throws XMLParseException;

    Datastream getDatastream();
}
