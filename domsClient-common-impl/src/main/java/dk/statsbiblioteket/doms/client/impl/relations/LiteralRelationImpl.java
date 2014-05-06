package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.relations.LiteralRelation;

/**
 * This is a relation to a literal, ie, not another object.
 */
public class LiteralRelationImpl extends AbstractRelation implements LiteralRelation {
    private String object;

    /**
     * This constructor must be extended to complete the notion of triples
     * representing connections in the object graph.
     *
     * @param subjectPid
     * @param predicate
     */
    public LiteralRelationImpl(String subjectPid, String predicate, String object, DigitalObjectFactory factory) {
        super(subjectPid, predicate, factory);
        setObject(object);
    }

    @Override
    public String getObject() {
        return object;
    }

    private void setObject(String object) {
        if (object.startsWith("\"") && object.endsWith("\"")) {
            object = object.substring(1, object.length() - 1);
        }
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LiteralRelationImpl)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        LiteralRelationImpl that = (LiteralRelationImpl) o;

        if (!object.equals(that.object)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + object.hashCode();
        return result;
    }
}
