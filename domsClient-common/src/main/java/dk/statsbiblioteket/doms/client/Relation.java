package dk.statsbiblioteket.doms.client;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/15/11
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Relation {
    private String subject;
    private String predicate;
    private String object;

    public Relation(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public String getObject() {
        return object;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getSubject() {
        return subject;
    }
}
