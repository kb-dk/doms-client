package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/27/11
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatastreamDeclarationImpl implements DatastreamDeclaration {
    private String name;
    private DatastreamModel model;
    private List<String> dsMimeTypes;
    private List<String> dsFormatUris;
    private Constants.GuiRepresentation presentation;
    private Datastream schema;

    public DatastreamDeclarationImpl(String name, DatastreamModel model) {
        this.name = name;
        this.model = model;
        //To change body of created methods use File | Settings | File Templates.

        dsMimeTypes = new ArrayList<String>();
        dsFormatUris = new ArrayList<String>();
        presentation = Constants.GuiRepresentation.undefined;
    }

    public void addMimeTypes(List<String> dsMimeTypes) {
        this.dsMimeTypes.addAll(dsMimeTypes);
    }

    public void addFormatUris(List<String> dsFormatUris) {

        this.dsFormatUris.addAll(dsFormatUris);
    }


    @Override
    public List<String> getDsMimeTypes() {
        return dsMimeTypes;
    }

    @Override
    public List<String> getDsFormatUris() {
        return dsFormatUris;
    }

    @Override
    public Constants.GuiRepresentation getPresentation() {
        return presentation;
    }

    public void setPresentation(Constants.GuiRepresentation presentation) {
        this.presentation = presentation;
    }

    @Override
    public String getName() {
        return name;
    }

    public Datastream getSchema() {
        return schema;
    }

    public void setSchema(Datastream schema) {
        this.schema = schema;
    }
}
