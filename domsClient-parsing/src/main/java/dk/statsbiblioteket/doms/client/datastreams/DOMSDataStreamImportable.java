package dk.statsbiblioteket.doms.client.datastreams;


import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;

import java.io.IOException;

/**
 * TODO abr forgot to document this class
 */
public class DOMSDataStreamImportable extends DOMSDataStreamReadOnly {
    public DOMSDataStreamImportable(String pid, String id) throws IOException, ServerOperationFailed {
        super(pid, id);
    }
}
