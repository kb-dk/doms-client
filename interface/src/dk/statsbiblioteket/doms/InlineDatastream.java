package dk.statsbiblioteket.doms;

import org.w3c.dom.Document;

/**
 * TODO abr forgot to document this class
 */
public interface InlineDatastream extends Datastream{

    public boolean modifyDatastream(Document newXmlContent);

    public Document getDatastreamAsDocument();
}
