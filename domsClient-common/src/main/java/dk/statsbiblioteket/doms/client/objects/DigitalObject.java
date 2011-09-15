package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.relations.AbstractRelation;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;

import java.util.Date;
import java.util.List;

/**
 * The digital Object
 */
public interface DigitalObject {

    /**
     * @return the object pid
     */
    String getPid();

    /**
     * @return the object's content models
     */
    List<ContentModelObject> getType() throws ServerOperationFailed;

    /**
     * @return the object title
     */
    String getTitle() throws ServerOperationFailed;

    /**
     * Set the object title. Will not save, yet
     * @param title the title
     */
    void setTitle(String title) throws ServerOperationFailed;

    /**
     * @return the Object State
     */
    FedoraState getState() throws ServerOperationFailed;

    void setState(FedoraState state) throws ServerOperationFailed;

    /**
     * @return the lastModified date for the object
     */
    Date getLastModified() throws ServerOperationFailed;

    /**
     * @return the createdDate for the object
     */
    Date getCreated() throws ServerOperationFailed;


    /**
     * @return The list of datastreams in the object
     */
    List<Datastream> getDatastreams() throws ServerOperationFailed;

    /**
     * Not implemented
     * @param addition
     */
    void addDatastream(Datastream addition) throws ServerOperationFailed;

    /**
     * Not implemented
     * @param deleted
     */
    void removeDatastream(Datastream deleted) throws ServerOperationFailed;


    /**
     * The list of relations in the object.
     * @return
     */
    List<Relation> getRelations() throws ServerOperationFailed;


    /**
     * The list of inverse relations. TODO implement
     * @return
     */
    List<ObjectRelation> getInverseRelations() throws ServerOperationFailed;


    /**
     * Remove a relation from this object
     * @param relation the relation to remove
     */
    void removeRelation(Relation relation);
}
