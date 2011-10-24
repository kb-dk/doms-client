package dk.statsbiblioteket.doms.client.impl.relations;

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
     * @param objectPid
     */
    public LiteralRelationImpl(String predicate, String objectPid, String subject) {
        super(predicate, objectPid, null);
        this.subject = subject;
    }
}
