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
    private SoftReference<DigitalObject> subject;
    private String identifier;


    @Override
    public synchronized DigitalObject getSubject() throws ServerOperationFailed {
        DigitalObject result = subject.get();
        if (result == null){
            result = getFactory().getDigitalObject(identifier);
            setSubject(result);
        }
        return result;
    }

    @Override
    public void setSubject(DigitalObject subject) {
        this.subject = new SoftReference<DigitalObject>(subject);
    }

    /**
     * This constructor must be extended to complete the notion of triples
     * representing connections in the object graph.
     *
     * @param predicate
     * @param object
     */
    public ObjectRelationImpl(String predicate, DigitalObject object,
                              DigitalObject subject, DigitalObjectFactory factory) {
        super(predicate, object, factory);
        identifier = subject.getPid();//TODO if we will work on versions of objects
        this.subject = new SoftReference<DigitalObject>(subject);

    }
}
