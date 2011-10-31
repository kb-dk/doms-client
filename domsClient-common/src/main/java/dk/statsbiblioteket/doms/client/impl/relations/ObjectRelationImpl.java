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


    @Override
    public synchronized DigitalObject getObject() throws ServerOperationFailed {
        DigitalObject result = object.get();
        if (result == null){
            result = getFactory().getDigitalObject(objectPid);
            setObject(result);
        }
        return result;
    }

    @Override
    public void setObject(DigitalObject subject) {
        this.object = new SoftReference<DigitalObject>(subject);
    }

    public String getObjectPid() {
        return objectPid;
    }

    public ObjectRelationImpl(String predicate,
                              String objectPid,
                              String subjectPid,
                              DigitalObjectFactory factory) {
        super(predicate, objectPid, factory);
        this.objectPid = Constants.ensurePID(subjectPid);
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
