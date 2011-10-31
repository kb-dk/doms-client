package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TemplateObject extends DigitalObject {

    public DigitalObject clone(String... oldIDs) throws ServerOperationFailed;

    public Set<ContentModelObject> getTemplatedClasses() throws ServerOperationFailed;
}
