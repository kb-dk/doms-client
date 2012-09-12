package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CollectionObject extends DataObject{

    public Set<TemplateObject> getEntryTemplates(String viewangle) throws ServerOperationFailed;

    public Set<ContentModelObject> getEntryContentModels(String viewangle) throws ServerOperationFailed;
    
    public Set<ContentModelObject> getContentModels() throws ServerOperationFailed;

    public void removeObject(DigitalObject object) throws ServerOperationFailed;

    public void addObject(DigitalObject object) throws ServerOperationFailed;
}
