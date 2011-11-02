package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.CollectionObject;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.TemplateObject;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.lang.String;
import java.util.HashSet;
import java.util.List;
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
    public Set<TemplateObject> getEntryTemplates(String viewangle) throws ServerOperationFailed {
        Set<ContentModelObject> models = getContentModels();
        Set<TemplateObject> result = new HashSet<TemplateObject>();
        for (ContentModelObject model : models) {
            if (model.getEntryViewAngles().contains(viewangle)){
                result.addAll(model.getTemplates());
            }
        }
        return result;
    }

    @Override
    public Set<ContentModelObject> getContentModels() throws ServerOperationFailed {
        try {
            List<String> pids = api.getObjectsInCollection(this.getPid(), Constants.CM_CM_PID);
            Set<ContentModelObject> result = new HashSet<ContentModelObject>();
            for (String pid : pids) {
                DigitalObject object = factory.getDigitalObject(pid);
                if (object instanceof ContentModelObject) {
                    ContentModelObject contentModelObject = (ContentModelObject) object;
                    result.add(contentModelObject);
                }
            }
            return result;
        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
            throw new ServerOperationFailed(e);
        }
    }



    @Override
    public void removeObject(DigitalObject object) throws ServerOperationFailed {
        for (Relation relation : object.getRelations()) {
            if (relation.getPredicate().equals(Constants.IS_PART_OF_COLLECTION_PREDICATE)){
                if (relation instanceof ObjectRelation) {
                    ObjectRelation objectRelation = (ObjectRelation) relation;
                    if (objectRelation.getObjectPid().equals(this.getPid())){
                        relation.remove();
                    }
                }
            }
        }
    }

    @Override
    public void addObject(DigitalObject object) throws ServerOperationFailed {
        object.addObjectRelation(Constants.IS_PART_OF_COLLECTION_PREDICATE,this);
    }
}
