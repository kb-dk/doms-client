package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.ontology.OWLObjectProperty;

import java.util.Set;

/**
 * The relation interface definition
 */
public interface Relation {
    DigitalObject getSubject() throws ServerOperationFailed;

    String getSubjectPid();

    String getPredicate();

    void remove() throws ServerOperationFailed;

    Set<RelationDeclaration> getDeclarations() throws ServerOperationFailed;


}
