package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.relations.LiteralRelation;

/**
 * This is a relation to a literal, ie, not another object.
 */
public class LiteralRelationImpl extends AbstractRelation implements LiteralRelation {
    private String subject;

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * This constructor must be extended to complete the notion of triples
     * representing connections in the object graph.
     *
     * @param predicate
     * @param object
     */
    public LiteralRelationImpl(String predicate, DigitalObject object, String subject) {
        super(predicate, object, null);
        this.subject = subject;
    }
}
