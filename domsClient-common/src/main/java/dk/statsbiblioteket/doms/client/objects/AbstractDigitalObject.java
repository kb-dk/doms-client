package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.relations.*;

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


    public AbstractDigitalObject(ObjectProfile profile,
                                 CentralWebservice api,
                                 DigitalObjectFactory factory) throws ServerOperationFailed {
        this.profile = profile;
        this.api = api;
        this.factory = factory;

        //Load directly from
        type = new ArrayList<ContentModelObject>();
        datastreams = new ArrayList<Datastream>();
        relations = new ArrayList<dk.statsbiblioteket.doms.client.relations.Relation>();
        inverseRelations = new ArrayList<ObjectRelation>();

        pid = profile.getPid();
        state = FedoraState.fromString(profile.getState());
        stateOriginal = state;
        created = new Date(profile.getCreatedDate());
        lastModified = new Date(profile.getModifiedDate());
        title = profile.getTitle();
        titleOriginal = title;

        for (DatastreamProfile datastreamProfile : profile.getDatastreams()) {
            datastreams.add(new Datastream(datastreamProfile,this));
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
        List<dk.statsbiblioteket.doms.client.relations.Relation> rels = new ArrayList<dk.statsbiblioteket.doms.client.relations.Relation>();
        for (dk.statsbiblioteket.doms.client.relations.Relation inRelation : relations) {
            rels.add(inRelation);
        }
        rels.addAll(addedRelations);
        rels.removeAll(removedRelations);//TODO what if something was added and removed multiple times?
        return rels;
    }


    @Override
    public List<ObjectRelation> getInverseRelations() {
        return inverseRelations;
    }

    /**
     * Do not call this.
     * @throws ServerOperationFailed
     */
    public synchronized void loadRelations() throws ServerOperationFailed {
        if (relsloaded) return;
        relsloaded = true;

        List<dk.statsbiblioteket.doms.central.Relation> frelations = profile.getRelations();

        for (dk.statsbiblioteket.doms.central.Relation frelation : frelations) {
            if (frelation.isLiteral()){
                relations.add(new LiteralRelation(frelation.getPredicate(),this,frelation.getSubject()));
            } else {
                relations.add(new ObjectRelation(frelation.getPredicate(),this,factory.getDigitalObject(
                        frelation.getSubject())));
            }
        }
    }

    /**
     * Do not call this.
     * @throws ServerOperationFailed
     */
    public synchronized void loadContentModels() throws ServerOperationFailed {
        if (cmloaded) return;

        cmloaded = true;

        for (String contentModel : profile.getContentmodels()) {
            DigitalObject cm_object = factory.getDigitalObject(contentModel);
            if (cm_object instanceof ContentModelObject) {
                ContentModelObject object = (ContentModelObject) cm_object;
                type.add(object);
            } else {
                throw new ServerOperationFailed("Object '"+pid+"' has the content model '"+contentModel+"' declared, but this is not a content model");
            }
        }
    }



}
