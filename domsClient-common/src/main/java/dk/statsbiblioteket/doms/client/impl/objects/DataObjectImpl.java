package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DataObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.relations.Relation;

import java.util.Set;

/**
 * Data objects are the objects that actually holds the data in DOMS. TODO implement
 */
public class DataObjectImpl extends AbstractDigitalObject implements DataObject {



    public DataObjectImpl(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Set<DigitalObject> getChildren() throws ServerOperationFailed {
        return getChildObjects("GUI");
    }


    @Override
    public String getContentmodelTitle() throws ServerOperationFailed {
        String contentmodels = "";
        boolean multiple = false;
        for (Relation relation : getRelations()) {
            if (relation.getPredicate().contains("ContentModel")){
                int startIndex = relation.getPredicate().indexOf("ContentModel");
                String str = relation.getPredicate().substring(startIndex);
                contentmodels.concat(str);
                if (multiple){
                    contentmodels.concat(" | ");
                }
                multiple = true;
            }
        }
        if (multiple){
            contentmodels = contentmodels.substring(0, contentmodels.length()-1);
        }
        return contentmodels;
    }


}
