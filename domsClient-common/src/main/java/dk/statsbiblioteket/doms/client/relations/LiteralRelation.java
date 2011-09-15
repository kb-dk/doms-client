package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.relations.Relation;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LiteralRelation extends Relation {
    String getSubject();

    void setSubject(String subject);
}
