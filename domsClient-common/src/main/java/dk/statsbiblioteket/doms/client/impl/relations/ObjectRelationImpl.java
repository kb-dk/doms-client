package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;

import java.lang.ref.SoftReference;

/**
 * This is a relation between two objects.
 */
public class ObjectRelationImpl extends AbstractRelation implements ObjectRelation {
    private SoftReference<DigitalObject> subject = new SoftReference<DigitalObject>(null);
    private String pid;


    @Override
    public synchronized DigitalObject getSubject() throws ServerOperationFailed {
        DigitalObject result = subject.get();
        if (result == null){
            result = getFactory().getDigitalObject(pid);
            setSubject(result);
        }
        return result;
    }

    @Override
    public void setSubject(DigitalObject subject) {
        this.subject = new SoftReference<DigitalObject>(subject);
    }

    public String getSubjectPid() {
        return pid;
    }

    public ObjectRelationImpl(String predicate,
                              String objectPid,
                              String subjectPid,
                              DigitalObjectFactory factory) {
        super(predicate, objectPid, factory);
        this.pid = subjectPid;
    }


}
