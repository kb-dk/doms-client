package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * This is a relation between two objects.
 */
public class ObjectRelation extends AbstractRelation {
    private DigitalObject subject;

    public DigitalObject getSubject() {
        return subject;
    }

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
    public ObjectRelation(String predicate, DigitalObject object,
                          DigitalObject subject) {
        super(predicate, object);
        this.subject = subject;
    }
}
