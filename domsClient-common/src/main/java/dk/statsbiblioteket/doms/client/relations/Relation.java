package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.objects.DigitalObject;

/**
 * The relation interface definition
 */
public interface Relation {
    DigitalObject getObject();

    String getPredicate();

    void remove();
}
