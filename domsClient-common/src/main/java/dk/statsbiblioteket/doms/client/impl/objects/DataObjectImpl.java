package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DataObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.util.*;

/**
 * Data objects are the objects that actually holds the data in DOMS. TODO implement
 */
public class DataObjectImpl extends AbstractDigitalObject implements DataObject {


    private String contentModelTitle;

    public DataObjectImpl(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String getContentmodelTitle() throws ServerOperationFailed {

        if (contentModelTitle != null){
            return contentModelTitle;
        }
        List<ContentModelObject> tmp = getType();
        List<ContentModelObject> contentModels = new ArrayList<ContentModelObject>();
        for (ContentModelObject contentModel : tmp) {
            if (!contentModel.getPid().equals("fedora-system:FedoraObject-3.0")){
                contentModels.add(contentModel);
            }
        }
        Map<ContentModelObject, Integer> extendsCount = new HashMap<ContentModelObject, Integer>();

        for (ContentModelObject contentModel : contentModels) {
            extendsCount.put(contentModel,0);
        }
        for (ContentModelObject contentModel : contentModels) {
            List<ObjectRelation> childModels = contentModel.getInverseRelations(Constants.EXTENDSMODEL_PREDICATE);
            for (ObjectRelation childModel : childModels) {
                DigitalObject object = childModel.getObject();
                if (object instanceof ContentModelObject) {
                    ContentModelObject child = (ContentModelObject) object;
                    if (contentModels.contains(child)){
                        Integer extendscounter = extendsCount.get(child);
                        extendsCount.put(contentModel,extendscounter+1);
                    }
                }
            }
        }
        int bestCount = Integer.MAX_VALUE;
        ContentModelObject bestCM = null;
        for (ContentModelObject contentModelObject : extendsCount.keySet()) {
            Integer extendscounter = extendsCount.get(contentModelObject);
            if (extendscounter <= bestCount){
                bestCount = extendscounter;
                bestCM = contentModelObject;
            }
        }

        contentModelTitle = bestCM.getTitle();
        return contentModelTitle;
    }


}
