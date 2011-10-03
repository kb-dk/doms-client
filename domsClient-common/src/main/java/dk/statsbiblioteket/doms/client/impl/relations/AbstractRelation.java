package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.relations.Relation;

import java.lang.ref.SoftReference;

/**
 * Barebones relation, predicate and object. No subject
 */
public abstract class AbstractRelation implements Relation {
    private String predicate;   // relation name
    private SoftReference<DigitalObject> object = new SoftReference<DigitalObject>(null);
    private String pid;
    private DigitalObjectFactory factory;


    /**
     * This constructor must be extended to complete the notion of triples
     * representing connections in the object graph.
     * @param predicate
     * @param objectPid
     */
    public AbstractRelation(String predicate, String objectPid, DigitalObjectFactory factory) {
        this.predicate = predicate;
        this.factory = factory;
        this.pid = objectPid;
    }


    @Override
    public DigitalObject getObject() throws ServerOperationFailed {
        DigitalObject result = object.get();
        if (result == null){
            result = factory.getDigitalObject(pid);
            object = new SoftReference<DigitalObject>(result);
        }
        return result;
    }

    @Override
    public String getObjectPid() {
        return pid;
    }

    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public void remove() throws ServerOperationFailed {
        getObject().removeRelation(this);
    }

    protected DigitalObjectFactory getFactory() {
        return factory;
    }
}
