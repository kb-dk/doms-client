package dk.statsbiblioteket.doms;

import java.util.List;
import java.net.URI;

/**
 * TODO abr forgot to document this class
 */
public interface Datastream {



    public List<DatastreamVersion> getVersions();

    public boolean isVersionable();

    public String getDatastreamID();

    public DatastreamVersion getCurrentVersion();

    public String getMimeType();

    public URI getFormatURI();



}
