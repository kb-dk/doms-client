package dk.statsbiblioteket.doms.client.objects.stubs;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.util.List;

public class DatastreamDeclarationStub implements DatastreamDeclaration {
    @Override
    public List<String> getDsMimeTypes() {
        return null;
    }

    @Override
    public List<String> getDsFormatUris() {
        return null;
    }

    @Override
    public Constants.GuiRepresentation getPresentation() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Datastream getSchema() {
        return null;
    }

    @Override
    public void setSchema(Datastream schema) {

    }
}
