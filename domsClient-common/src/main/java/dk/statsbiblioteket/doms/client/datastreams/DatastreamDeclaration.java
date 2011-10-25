package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/28/11
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DatastreamDeclaration {
    List<String> getDsMimeTypes();

    List<String> getDsFormatUris();

    Constants.GuiRepresentation getPresentation();

    String getName();


    Datastream getSchema();

    void setSchema(Datastream schema);



}
