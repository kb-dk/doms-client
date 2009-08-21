package dk.statsbiblioteket.doms;

import java.net.URL;
import java.util.Date;

/**
 * TODO abr forgot to document this class
 */
public interface DatastreamVersion {

    public Date getLastModified();

    public URL getContentUrl();

    public String getContentAsString();
    
}
