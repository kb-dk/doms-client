package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.objects.DatastreamDeclaration;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/27/11
 * Time: 8:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DatastreamModel extends Datastream {
    public List<DatastreamDeclaration> getDatastreamDeclarations() throws ServerOperationFailed;
}
