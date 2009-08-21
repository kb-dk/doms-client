package dk.statsbiblioteket.doms;

import javax.xml.transform.Source;
import java.net.URI;
import java.util.List;

/**
 * TODO abr forgot to document this class
 */
public interface DatastreamDefinition {

    public String getDefinedName();

    public List<String> getMimeType();

    public List<URI> getFormatURI();

    public GuiRepresentation getGuiRepresentation();

    public List<Source> getSchemas();
}
