package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/8/11
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
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
