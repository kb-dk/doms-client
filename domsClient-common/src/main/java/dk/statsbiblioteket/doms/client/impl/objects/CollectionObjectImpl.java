package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.CollectionObject;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.TemplateObject;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class CollectionObjectImpl extends DataObjectImpl implements CollectionObject{
    public CollectionObjectImpl(ObjectProfile profile, CentralWebservice api,
                                DigitalObjectFactoryImpl digitalObjectFactory) throws ServerOperationFailed {
        super(profile,api,digitalObjectFactory);
    }

    @Override
    public Set<TemplateObject> getTemplates() throws ServerOperationFailed {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<TemplateObject> getEntryTemplates(String viewangle) throws ServerOperationFailed {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<ContentModelObject> getContentModels() throws ServerOperationFailed {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    @Override
    public void removeObject(DigitalObject object) throws ServerOperationFailed {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addObject(DigitalObject object) throws ServerOperationFailed {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
