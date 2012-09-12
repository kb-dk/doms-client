package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.methods.Method;
import dk.statsbiblioteket.doms.client.relations.RelationModel;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ContentModelObject extends DigitalObject {

    public List<String> getRelationsWithViewAngle(String viewAngle) throws ServerOperationFailed;

    public List<String> getInverseRelationsWithViewAngle(String viewAngle) throws ServerOperationFailed;

    public Set<String> getDeclaredViewAngles() throws  ServerOperationFailed;

    public RelationModel getRelationModel() throws ServerOperationFailed;

    public DatastreamModel getDsModel() throws ServerOperationFailed;

    public Set<TemplateObject> getTemplates() throws ServerOperationFailed;

    public Set<String> getEntryViewAngles() throws ServerOperationFailed;

    public Set<DigitalObject> getSubscribingObjects() throws ServerOperationFailed;

    public Set<ContentModelObject> getParents() throws ServerOperationFailed;

    public Set<ContentModelObject> getDescendants() throws ServerOperationFailed;
    
    public List<Method> listMethods() throws ServerOperationFailed;
}
