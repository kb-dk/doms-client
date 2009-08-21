package dk.statsbiblioteket.doms;

/**
     * The standard way to represent a relation
 */
public class Relation {

    private String from,to,relation;

    /**
     * Create a new Relation object. This constructor should not be used
     * except in classes implementing FedoraConnector
     * @param from The object, ie the object wherein the relation originates
     * @param to the Subject, ie the target of the relation
     * @param relation the full name of the relation
     */
    public Relation(String from, String to, String relation) {
        this.from = from;
        this.to = to;
        this.relation = relation;
    }

    /**
     * From is the pid of the object that holds the relation.
     * Called object in rdf terminology
     * @return from
     */
    public String getFrom() {
        return from;
    }

    /**
     * To is the pid of the object pointed to, the target. Called subject
     * in rdf terminology
     *
     * @return to
     */
    public String getTo() {
        return to;
    }

    /**
     * The fully qualified name of the relation. Called Property in
     * rdf terminology
     * @return the relation
     */
    public String getRelation() {
        return relation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Relation) {
            Relation relation1 = (Relation) obj;
            return relation1.getTo().equals(this.getTo())
                   && relation1.getFrom().equals(this.getFrom())
                   && relation1.getRelation().equals(this.getRelation());
        } else{
            return false;
        }
    }
}
