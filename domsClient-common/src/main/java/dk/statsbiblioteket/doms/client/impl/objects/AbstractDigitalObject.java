package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.datastreams.ExternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.datastreams.InternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.relations.LiteralRelationImpl;
import dk.statsbiblioteket.doms.client.impl.relations.ObjectRelationImpl;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.FedoraState;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;

import javax.swing.*;
import java.lang.String;
import java.util.*;

/**
 * The common functionality of a digital object is implemented here.
 */
public abstract class AbstractDigitalObject implements DigitalObject {


    protected ObjectProfile profile;
    protected CentralWebservice api;

    private String pid;
    protected DigitalObjectFactory factory;

    private List<ContentModelObject> type;

    private String title;
    private String titleOriginal;

    private FedoraState state;
    private FedoraState stateOriginal;

    private Date lastModified;
    private Date created;

    protected List<Datastream> datastreams;


    private List<dk.statsbiblioteket.doms.client.relations.Relation> relations;
    private List<dk.statsbiblioteket.doms.client.relations.Relation> removedRelations;
    private List<dk.statsbiblioteket.doms.client.relations.Relation> addedRelations;

    private List<ObjectRelation> inverseRelations;

    private boolean cmloaded = false;
    private boolean relsloaded = false;
    private boolean invrelsloaded = false;
    private boolean profileloaded = false;
    private boolean statePreSaved;


    public AbstractDigitalObject(String pid,
                                 CentralWebservice api,
                                 DigitalObjectFactory factory) throws ServerOperationFailed{

        this.pid = pid;
        this.api = api;
        this.factory = factory;
        type = new ArrayList<ContentModelObject>();
        datastreams = new ArrayList<Datastream>();
        relations = new ArrayList<dk.statsbiblioteket.doms.client.relations.Relation>();
        inverseRelations = new ArrayList<ObjectRelation>();

    }

    public AbstractDigitalObject(ObjectProfile profile,
                                 CentralWebservice api,
                                 DigitalObjectFactory factory) throws ServerOperationFailed {
        this(profile.getPid(),api,factory);
        this.profile = profile;
        loadProfile();
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public List<ContentModelObject> getType() throws ServerOperationFailed {
        loadContentModels();
        return Collections.unmodifiableList(type);
    }

    @Override
    public String getTitle() throws ServerOperationFailed {
        loadProfile();
        return title;
    }

    @Override
    public void setTitle(String title) throws ServerOperationFailed {
        loadProfile();
        this.title = title;
    }

    @Override
    public FedoraState getState() throws ServerOperationFailed {
        loadProfile();
        return state;
    }

    @Override
    public void setState(FedoraState state) throws ServerOperationFailed {
        loadProfile();
        this.state = state;
    }

    @Override
    public void setState(FedoraState state, String viewAngle) throws ServerOperationFailed {
        setState(state);
        Set<DigitalObject> children = getChildObjects(viewAngle);
        for (DigitalObject child : children) {
            child.setState(state);
        }
    }


    @Override
    public Date getLastModified() throws ServerOperationFailed {
        loadProfile();
        return lastModified;
    }

    @Override
    public Date getCreated() throws ServerOperationFailed {
        loadProfile();
        return created;
    }

    @Override
    public List<Datastream> getDatastreams() throws ServerOperationFailed {
        loadProfile();
        return Collections.unmodifiableList(datastreams);
    }

    @Override
    public Datastream getDatastream(String id) throws ServerOperationFailed, NotFoundException {
        loadProfile();
        for (Datastream datastream : datastreams) {
            if (datastream.getId().equals(id)) return datastream;
        }
        throw new NotFoundException("Datastream not found. Id: "+ id);
    }

    @Override
    public void addDatastream(Datastream addition) throws ServerOperationFailed {
        loadProfile();
        datastreams.add(addition);
    }

    @Override
    public void removeDatastream(Datastream deleted) throws ServerOperationFailed {
        loadProfile();
        datastreams.remove(deleted);
    }

    @Override
    public List<dk.statsbiblioteket.doms.client.relations.Relation> getRelations() throws ServerOperationFailed {
        loadRelations();
        return Collections.unmodifiableList(relations);
/*
        List<dk.statsbiblioteket.doms.client.relations.Relation> rels =
                new ArrayList<dk.statsbiblioteket.doms.client.relations.Relation>();
        for (dk.statsbiblioteket.doms.client.relations.Relation inRelation : relations) {
            rels.add(inRelation);
        }
        rels.addAll(addedRelations);
        rels.removeAll(removedRelations);//TODO what if something was added and removed multiple times?
        return rels;
*/
    }

    @Override
    public void removeRelation(dk.statsbiblioteket.doms.client.relations.Relation relation) {
        throw new IllegalAccessError("Not implemented yet");
    }

    @Override
    public List<ObjectRelation> getInverseRelations() throws ServerOperationFailed {
        loadInverseRelations();
        return Collections.unmodifiableList(inverseRelations);
    }

    /**
     * Do not call this.
     *
     * @throws ServerOperationFailed
     */
    protected synchronized void loadRelations() throws ServerOperationFailed {
        if (relsloaded) {
            return;
        }
        relsloaded = true;

        List<dk.statsbiblioteket.doms.central.Relation> frelations = profile.getRelations();

        for (dk.statsbiblioteket.doms.central.Relation frelation : frelations) {
            if (frelation.isLiteral()) {
                relations.add(new LiteralRelationImpl(frelation.getPredicate(), this.getPid(), frelation.getObject()));
            } else {
                relations.add(new ObjectRelationImpl(frelation.getPredicate(), this.getPid(), frelation.getObject(), factory));
            }
        }
    }


    /**
     * Do not call this.
     *
     * @throws ServerOperationFailed
     */
    protected synchronized void loadInverseRelations() throws ServerOperationFailed {
        if (invrelsloaded) {
            return;
        }
        invrelsloaded = true;

        List<Relation> frelations;
        try {
            frelations = api.getInverseRelations(pid);
        } catch (Exception e) {
            throw new ServerOperationFailed("Failed to load inverse relations", e);
        }

        for (dk.statsbiblioteket.doms.central.Relation frelation : frelations) {
            inverseRelations.add(new ObjectRelationImpl(frelation.getPredicate(),
                                                        frelation.getObject(),
                                                        frelation.getSubject(),
                                                        factory));
        }
    }

    /**
     * Do not call this.
     *
     * @throws ServerOperationFailed
     */
    protected synchronized void loadContentModels() throws ServerOperationFailed {
        if (cmloaded) {
            return;
        }

        cmloaded = true;

        for (String contentModel : profile.getContentmodels()) {
            DigitalObject cm_object = factory.getDigitalObject(contentModel);
            if (cm_object instanceof ContentModelObjectImpl) {
                ContentModelObject object = (ContentModelObject) cm_object;
                type.add(object);
            } else {
                throw new ServerOperationFailed("Object '" + pid + "' has the content model '" + contentModel +
                                                "' declared, but this is not a content model");
            }
        }
    }

    protected synchronized void loadProfile() throws ServerOperationFailed {
        if (profileloaded) return;
        profileloaded = true;

        if (profile == null){
            try {
                profile = api.getObjectProfile(pid);
            } catch (Exception e) {
                throw new ServerOperationFailed("Failed to retrieve Profile",e);
            }
        }

        state = FedoraState.fromString(profile.getState());
        stateOriginal = state;
        created = new Date(profile.getCreatedDate());
        lastModified = new Date(profile.getModifiedDate());
        title = profile.getTitle();
        titleOriginal = title;

        loadDatastreams();

    }

    protected void loadDatastreams() throws ServerOperationFailed {
        for (DatastreamProfile datastreamProfile : profile.getDatastreams()) {
            if (datastreamProfile.isInternal()){
                datastreams.add(new InternalDatastreamImpl(datastreamProfile, this, api));
            } else {
                datastreams.add(new ExternalDatastreamImpl(datastreamProfile, this, api));
            }
        }
    }

    @Override
    public Set<DigitalObject> getChildObjects(String viewAngle) throws ServerOperationFailed {
        Set<String> viewRelationNames = new HashSet<String>();
        Set<DigitalObject> children = new HashSet<DigitalObject>();
        for (ContentModelObject contentModelObject : getType()) {
            try {
            List<String> theseRels = contentModelObject.getRelationsWithViewAngle(viewAngle);
            if (theseRels!= null){
                viewRelationNames.addAll(theseRels);
            }
            } catch (ServerOperationFailed e){
                //pass quietly
            }
        }
        for (dk.statsbiblioteket.doms.client.relations.Relation rel : getRelations()) {
            if (viewRelationNames.contains(rel.getPredicate())){
                if (rel instanceof ObjectRelation) {
                    ObjectRelation objectRelation = (ObjectRelation) rel;
                    children.add(objectRelation.getSubject());
                }
            }
        }
        return children;
    }








    private void preSaveState()
            throws ServerOperationFailed {
        try {
            List<String> pid_list = new ArrayList<String>(1);
            pid_list.add(pid);
            switch (state){
                case Active:
                    api.markPublishedObject(pid_list, "Object '" + pid + "' marked as active");
                    break;
                case Deleted:
                    api.deleteObject(pid_list, "Object '"+pid+"' marked as deleted");
                    break;
                case Inactive:
                    api.markInProgressObject(pid_list, "Object '"+pid+"' marked as inactive");
            }
            statePreSaved = true;
        } catch (Exception e){
            throw new ServerOperationFailed(e);
        }

    }

    private void postSaveState(){
        stateOriginal = state;
        statePreSaved = false;
    }

    private void undoSaveState() throws ServerOperationFailed {
        try {
            if (statePreSaved){
                List<String> pid_list = new ArrayList<String>(1);
                pid_list.add(pid);
                switch (stateOriginal){
                    case Active:
                        api.markPublishedObject(pid_list, "Object '" + pid + "' marked as active");
                        break;
                    case Deleted:
                        api.deleteObject(pid_list, "Object '"+pid+"' marked as deleted");
                        break;
                    case Inactive:
                        api.markInProgressObject(pid_list, "Object '"+pid+"' marked as inactive");
                }
            }
            state = stateOriginal;
            statePreSaved = false;


        } catch (Exception e){
            throw new ServerOperationFailed(e);
        }
    }


    protected void preSave(String viewAngle) throws ServerOperationFailed{

        List<AbstractDigitalObject> saved = new ArrayList<AbstractDigitalObject>();
        try {
            Set<DigitalObject> children = getChildObjects(viewAngle);
            for (DigitalObject child : children) {
                if (child instanceof AbstractDigitalObject) {
                    AbstractDigitalObject abstractChild = (AbstractDigitalObject) child;
                    abstractChild.preSave(viewAngle);
                }
            }
            preSaveState();
        } catch (ServerOperationFailed e){
            for (AbstractDigitalObject digitalObject : saved) {
                digitalObject.undoSave(viewAngle);
            }
            undoSave(viewAngle);
            throw new ServerOperationFailed(e);
        }


    }

    protected void postSave(String viewAngle) throws ServerOperationFailed{
        Set<DigitalObject> children = getChildObjects(viewAngle);
        for (DigitalObject child : children) {
            if (child instanceof AbstractDigitalObject) {
                AbstractDigitalObject abstractChild = (AbstractDigitalObject) child;
                abstractChild.postSave(viewAngle);
            }
        }
        postSaveState();
    }

    protected void undoSave(String viewAngle)
            throws ServerOperationFailed {
        Set<DigitalObject> children = getChildObjects(viewAngle);
        for (DigitalObject child : children) {
            if (child instanceof AbstractDigitalObject) {
                AbstractDigitalObject abstractChild = (AbstractDigitalObject) child;
                abstractChild.undoSave(viewAngle);
            }
        }
        undoSaveState();
    }



    public void save(String viewAngle) throws ServerOperationFailed {
        this.preSave(viewAngle);
        this.postSave(viewAngle);
    }



    public void save() throws ServerOperationFailed {
        save("UNUSEDVIEWANGLE");
    }

}
