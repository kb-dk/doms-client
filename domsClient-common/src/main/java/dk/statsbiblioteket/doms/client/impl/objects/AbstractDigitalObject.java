package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.datastreams.AbstractDatastream;
import dk.statsbiblioteket.doms.client.impl.datastreams.ExternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.datastreams.InternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.relations.LiteralRelationImpl;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.impl.relations.ObjectRelationImpl;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.FedoraState;

import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The common functionality of a digital object is implemented here.
 */
public abstract class AbstractDigitalObject implements DigitalObject {


    private ObjectProfile profile;
    private CentralWebservice api;

    private String pid;
    private DigitalObjectFactory factory;

    private List<ContentModelObject> type;

    private String title;
    private String titleOriginal;

    private FedoraState state;
    private FedoraState stateOriginal;

    private Date lastModified;
    private Date created;

    private List<Datastream> datastreams;


    private List<dk.statsbiblioteket.doms.client.relations.Relation> relations;
    private List<dk.statsbiblioteket.doms.client.relations.Relation> removedRelations;
    private List<dk.statsbiblioteket.doms.client.relations.Relation> addedRelations;

    private List<ObjectRelation> inverseRelations;

    private boolean cmloaded = false;
    private boolean relsloaded = false;
    private boolean invrelsloaded = false;


    public AbstractDigitalObject(ObjectProfile profile,
                                 CentralWebservice api,
                                 DigitalObjectFactory factory) throws ServerOperationFailed {
        this.profile = profile;
        this.api = api;
        this.factory = factory;

        //Load directly from
        type = new ArrayList<ContentModelObjectImpl>();
        datastreams = new ArrayList<AbstractDatastream>();
        relations = new ArrayList<dk.statsbiblioteket.doms.client.relations.Relation>();
        inverseRelations = new ArrayList<ObjectRelationImpl>();

        pid = profile.getPid();
        state = FedoraState.fromString(profile.getState());
        stateOriginal = state;
        created = new Date(profile.getCreatedDate());
        lastModified = new Date(profile.getModifiedDate());
        title = profile.getTitle();
        titleOriginal = title;

        for (DatastreamProfile datastreamProfile : profile.getDatastreams()) {
            if (datastreamProfile.isInternal()){
                datastreams.add(new InternalDatastreamImpl(datastreamProfile, this, api));
            } else {
                datastreams.add(new ExternalDatastreamImpl(datastreamProfile, this, api));
            }
        }


    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public List<ContentModelObject> getType() {
        return Collections.unmodifiableList(type);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public FedoraState getState() {
        return state;
    }

    @Override
    public void setState(FedoraState state) {
        this.state = state;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public List<Datastream> getDatastreams() {
        return Collections.unmodifiableList(datastreams);
    }

    @Override
    public void addDatastream(Datastream addition) {
        datastreams.add(addition);
    }

    @Override
    public void removeDatastream(Datastream deleted) {
        datastreams.remove(deleted);
    }

    @Override
    public List<dk.statsbiblioteket.doms.client.relations.Relation> getRelations() throws ServerOperationFailed {
        loadRelations();
        List<dk.statsbiblioteket.doms.client.relations.Relation> rels =
                new ArrayList<dk.statsbiblioteket.doms.client.relations.Relation>();
        for (dk.statsbiblioteket.doms.client.relations.Relation inRelation : relations) {
            rels.add(inRelation);
        }
        rels.addAll(addedRelations);
        rels.removeAll(removedRelations);//TODO what if something was added and removed multiple times?
        return rels;
    }


    @Override
    public List<ObjectRelation> getInverseRelations() throws ServerOperationFailed {
        loadInverseRelations();
        return inverseRelations;
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
                relations.add(new LiteralRelationImpl(frelation.getPredicate(), this, frelation.getSubject()));
            } else {
                relations.add(new ObjectRelationImpl(frelation.getPredicate(), this, factory.getDigitalObject(
                        frelation.getSubject())));
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
                                                    factory.getDigitalObject(frelation.getSubject()),
                                                    this));
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


}
