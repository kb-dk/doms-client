package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;

/**
 * This is a relation between two objects.
 */
public class ObjectRelationImpl extends AbstractRelation implements ObjectRelation {
    private DigitalObject subject;

    @Override
    public DigitalObject getSubject() {
        return subject;
    }

    @Override
    public void setSubject(DigitalObject subject) {
        this.subject = subject;
    }

    /**
     * This constructor must be extended to complete the notion of triples
     * representing connections in the object graph.
     *
     * @param predicate
     * @param object
     */
    public ObjectRelationImpl(String predicate, DigitalObject object,
                              DigitalObject subject) {
        super(predicate, object);
        this.subject = subject;
    }
}
