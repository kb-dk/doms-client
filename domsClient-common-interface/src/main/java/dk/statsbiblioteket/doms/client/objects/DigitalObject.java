package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.methods.Method;
import dk.statsbiblioteket.doms.client.relations.LiteralRelation;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The digital Object
 */
public interface DigitalObject {

    /**
     * Saves the digital object to the Server.
     * @throws ServerOperationFailed
     */
    void save() throws ServerOperationFailed, XMLParseException;

    /**
     * Saves the digital object to the Server.
     * @throws ServerOperationFailed
     */
    void save(String viewAngle) throws ServerOperationFailed, XMLParseException;


    /**
     * @return the object pid
     */
    String getPid();

    Set<CollectionObject> getCollections() throws ServerOperationFailed;

    public void addToCollection(CollectionObject collection) throws ServerOperationFailed;

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
    Constants.FedoraState getState() throws ServerOperationFailed;

    void setState(Constants.FedoraState state) throws ServerOperationFailed;

    public void setState(Constants.FedoraState state, String viewAngle) throws ServerOperationFailed;

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
     *
     * @param id datastream ID
     * @return The datastream
     * @throws ServerOperationFailed
     */
    Datastream getDatastream(String id) throws ServerOperationFailed, NotFoundException;
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
     * The list of inverse relations. TODO implement
     * @return
     */
    List<ObjectRelation> getInverseRelations(String predicate) throws ServerOperationFailed;



    /**
     * Remove a relation from this object
     * @param relation the relation to remove
     */
    void removeRelation(Relation relation);


    ObjectRelation addObjectRelation(String predicate,DigitalObject object) throws ServerOperationFailed;

    LiteralRelation addLiteralRelation(String predicate, String value);

    public Set<DigitalObject> getChildObjects(String viewAngle) throws ServerOperationFailed;

    public Set<Method> getMethods() throws ServerOperationFailed;
}
