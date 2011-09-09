package dk.statsbiblioteket.doms.client;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/15/11
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
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
