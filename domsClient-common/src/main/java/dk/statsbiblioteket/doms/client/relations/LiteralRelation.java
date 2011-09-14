package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/8/11
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class LiteralRelation extends AbstractRelation {
    private String subject;

    public String getSubject() {
        return subject;
    }

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
    public LiteralRelation(String predicate, DigitalObject object, String subject) {
        super(predicate, object);
        this.subject = subject;
    }
}
