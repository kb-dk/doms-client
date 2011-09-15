package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
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
    List<ContentModelObject> getType();

    /**
     * @return the object title
     */
    String getTitle();

    /**
     * Set the object title. Will not save, yet
     * @param title the title
     */
    void setTitle(String title);

    /**
     * @return the Object State
     */
    FedoraState getState();

    void setState(FedoraState state);

    /**
     * @return the lastModified date for the object
     */
    Date getLastModified();

    /**
     * @return the createdDate for the object
     */
    Date getCreated();


    /**
     * @return The list of datastreams in the object
     */
    List<Datastream> getDatastreams();

    /**
     * Not implemented
     * @param addition
     */
    void addDatastream(Datastream addition);

    /**
     * Not implemented
     * @param deleted
     */
    void removeDatastream(Datastream deleted);


    /**
     * The list of relations in the object.
     * @return
     */
    List<Relation> getRelations() throws ServerOperationFailed;


    /**
     * The list of inverse relations. TODO implement
     * @return
     */
    List<ObjectRelation> getInverseRelations();

    /**
     * Do not call. Used internally to load the object
     * @throws dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed
     */
    void load()
            throws ServerOperationFailed;

}
