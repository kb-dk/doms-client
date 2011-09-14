package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * Barebones relation, predicate and object. No subject
 */
public abstract class AbstractRelation implements Relation {
    private String predicate;   // relation name
    private DigitalObject object;

    /**
     * This constructor must be extended to complete the notion of triples
     * representing connections in the object graph.
     * @param predicate
     * @param object
     */
    public AbstractRelation(String predicate, DigitalObject object) {
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public DigitalObject getObject() {
        return object;
    }

    @Override
    public String getPredicate() {
        return predicate;
    }


}
