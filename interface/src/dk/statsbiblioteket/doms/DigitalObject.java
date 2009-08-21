package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.exceptions.DatastreamNotFoundException;
import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;
import dk.statsbiblioteket.doms.exceptions.NotWritableException;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Interface corresponding to a Digital Object in a Fedora Repository.
 *
 * The object is assumed to be write through, in that any setter methods should
 * immediately call the repository to have the change written. In case the
 * object cannot be written due to state rules, or lack of authoritization, a
 * NotWritableException should be thrown.
 *
 * To reduce overhead, the object should be populated Just-In-Time, ie. the
 * implementations should load the nessesary information only when the user
 * requests this information.
 */
public interface DigitalObject {

    /**
     * Get the pid of the Object
     * @return
     */
    public String getPid();

    /**
     * Get the state of the object
     * @return
     */
    public State getState();

    /**
     * Set the new state of the object
     * @param state the new state
     * @throws NotWritableException if the object is not writable
     * @throws FedoraConnectionException
     */
    public void setState(State state)
        throws NotWritableException,
            FedoraConnectionException;
    /**
     * Test if the object is writable.
     * @return true, if the object can be written
     */
    public boolean isWritable()
            throws FedoraConnectionException;


    //------------------------Relation Methods--------------------------------

    /**
     * Add a relation to the object
     * @param relationname - The name of the relation
     * @param to the DigitalObject that the relation should be to
     * @return true if the relation was added, false if the relation was
     * already there
     * @throws FedoraConnectionException if there was a connection problem;
     * @throws dk.statsbiblioteket.doms.exceptions.NotWritableException if this
     * object is not writable
     */
    public boolean addRelation(String relationname, DigitalObject to)
            throws NotWritableException,
                   FedoraConnectionException;


    /**
     * Adds a literal relation the object
     * @param relation the name of the relation
     * @param value the value of the relation
     * @param datatype the datatype of the relation
     * @return true if the relation was added, false if the relation was
     * already there
     * @throws FedoraConnectionException if there was a connection problem;
     * @throws dk.statsbiblioteket.doms.exceptions.NotWritableException if this
     * object is not writable
     */
    public boolean addLiteralRelation(
                                      String relation,
                                      String value,
                                      String datatype)
            throws NotWritableException,
                   FedoraConnectionException;


    /**
     * Get all the relations for this object
     * @return a list of all the relations
     * @throws FedoraConnectionException if there was a connection problem;
     */
    public List<Relation> getRelations()
            throws FedoraConnectionException;


    /**
     * Get all the relations with this name for this object
     * @param relation the name of the relation
     * @return all the relations with the given name
     * @throws FedoraConnectionException if there was a connection problem;
     */
    public List<Relation> getRelations( String relation)
            throws FedoraConnectionException;



    public boolean removeRelation(Relation relation)
            throws FedoraConnectionException;






    public <D extends Datastream> D getDatastream()
            throws IllegalStateException,
                   FedoraConnectionException, DatastreamNotFoundException;


    public <D extends Datastream> List<D> listDatastreams()
            throws IllegalStateException,
                   FedoraConnectionException;


    public <D extends Datastream> boolean removeDatastream(D datastream)
            throws IllegalStateException,
                   FedoraConnectionException,
                   DatastreamNotFoundException,
                   NotWritableException;


    public Document getObjectXml()
            throws IllegalStateException,
                   FedoraConnectionException,
                   FedoraIllegalContentException;

   public List<ContentModel> getContentModels();

    public boolean hasContentModel(ContentModel contentmodel);

}
