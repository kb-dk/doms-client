package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.lang.ref.SoftReference;

/**
 * This is a relation between two objects.
 */
public class ObjectRelationImpl extends AbstractRelation implements ObjectRelation {
    private SoftReference<DigitalObject> object = new SoftReference<DigitalObject>(null);
    private String objectPid;


    public ObjectRelationImpl(String subjectPid, String predicate, String objectPid, DigitalObjectFactory factory) {
        super(subjectPid, predicate, factory);
        this.objectPid = Constants.ensurePID(objectPid);
    }

    @Override
    public synchronized DigitalObject getObject() throws ServerOperationFailed {
        DigitalObject result = object.get();
        if (result == null) {
            result = getFactory().getDigitalObject(objectPid);
            object = new SoftReference<DigitalObject>(result);
        }
        return result;
    }

    public String getObjectPid() {
        return objectPid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectRelationImpl)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ObjectRelationImpl that = (ObjectRelationImpl) o;

        if (!objectPid.equals(that.objectPid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + objectPid.hashCode();
        return result;
    }
}
