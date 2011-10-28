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
    private SoftReference<DigitalObject> subject = new SoftReference<DigitalObject>(null);
    private String pid;


    @Override
    public synchronized DigitalObject getObject() throws ServerOperationFailed {
        DigitalObject result = subject.get();
        if (result == null){
            result = getFactory().getDigitalObject(pid);
            setObject(result);
        }
        return result;
    }

    @Override
    public void setObject(DigitalObject subject) {
        this.subject = new SoftReference<DigitalObject>(subject);
    }

    public String getObjectPid() {
        return pid;
    }

    public ObjectRelationImpl(String predicate,
                              String objectPid,
                              String subjectPid,
                              DigitalObjectFactory factory) {
        super(predicate, objectPid, factory);
        this.pid = Constants.ensurePID(subjectPid);
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

        if (!pid.equals(that.pid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + pid.hashCode();
        return result;
    }
}
