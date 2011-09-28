package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/27/11
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatastreamDeclaration {
    private String name;
    private Datastream ds;
    private List<String> dsMimeTypes;
    private List<String> dsFormatUris;
    private List<String> presentations;
    private HashMap<String, Datastream> compositeSchemas;

    public DatastreamDeclaration(String name, Datastream ds) {
        this.name = name;
        //To change body of created methods use File | Settings | File Templates.
        this.ds = ds;
        dsMimeTypes = new ArrayList<String>();
        dsFormatUris = new ArrayList<String>();
        presentations = new ArrayList<String>();
        compositeSchemas = new HashMap<String, Datastream>();
    }

    public void addMimeTypes(List<String> dsMimeTypes) {
        this.dsMimeTypes.addAll(dsMimeTypes);
    }

    public void addFormatUris(List<String> dsFormatUris) {

        this.dsFormatUris.addAll(dsFormatUris);
    }

    public void addPresentation(String gui, List<String> guiViewAngles) {

        this.presentations.addAll(guiViewAngles);
    }

    public List<String> getDsMimeTypes() {
        return dsMimeTypes;
    }

    public List<String> getDsFormatUris() {
        return dsFormatUris;
    }

    public List<String> getPresentations() {
        return presentations;
    }

    public String getName() {
        return name;
    }

    public Datastream getDs() {
        return ds;
    }

    public void addCompositeSchemas(HashMap<String, Datastream> compositeSchemas) {
        this.compositeSchemas.putAll(compositeSchemas);
    }

    public HashMap<String, Datastream> getCompositeSchemas(){
        return this.compositeSchemas;
    }
}
