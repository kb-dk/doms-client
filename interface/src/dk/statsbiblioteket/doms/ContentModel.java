package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;

import java.util.List;

/**
 * TODO abr forgot to document this class
 */
public interface ContentModel extends DigitalObject{


    /**
     * Get the list of content models that this content model inherit from
     * @return a list of content models
     * @throws FedoraConnectionException
     */
    public List<ContentModel> getInheritedContentModels()
            throws FedoraConnectionException;


    /**
     * Get the list of content models that inherit from this content model
     * @return a list of content models
     * @throws IllegalStateException
     * @throws FedoraConnectionException
     * @throws FedoraIllegalContentException
     */
    public List<ContentModel> getInheritingContentModels()
            throws FedoraConnectionException;

    /**
     * Get the list of template objects for this content model
     * @return a list of template objects
     * @throws FedoraConnectionException
     */
    public List<Template> getTemplatesFor()
            throws FedoraConnectionException;

    /**
     * Get the (presumably) long list of subscribing data objects
     * @param status the status that the objects must have
     * @return A list of data objects
     * @throws FedoraConnectionException
     */
    public <E extends DataObject> List<E> getSubscribingObjects(State status)
            throws FedoraConnectionException;

    /**
     * Get the list of datastreams defined in this content model
     * @return the list of datastream definitions
     */
    public List<DatastreamDefinition> getDefinedDatastreams();

    /**
     * Get the list of relations defined for this content model class
     * @return a list of defined relations
     */
    public List<RelationDefinition> getDefinedRelations();


    /**
     * Merge this content model with it's ancestors, into a CompoundContentModel.
     * A CompoundContentModel is a meta object with all the information from the
     * content models it consist of, but not corresponding to any single object
     * in the repository.
     * @return a Compound Content Model
     */
    public CompoundContentModel mergeWithAncestors();
}
