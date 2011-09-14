package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.*;

import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/8/11
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
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


    private List<Relation> relations;
    private List<Relation> removedRelations;
    private List<Relation> addedRelations;

    private List<ObjectRelation> inverseRelations;

    public AbstractDigitalObject(ObjectProfile profile,
                                 CentralWebservice api,
                                 DigitalObjectFactory factory){
        this.profile = profile;
        this.api = api;
        this.factory = factory;
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
    public List<Relation> getRelations() {
        List<Relation> rels = new ArrayList<Relation>();
        for (Relation inRelation : relations) {
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

    //TODO hide these exceptions
    public void load() throws ServerOperationFailed {
        type = new ArrayList<ContentModelObject>();
        datastreams = new ArrayList<Datastream>();
        relations = new ArrayList<Relation>();
        inverseRelations = new ArrayList<ObjectRelation>();

        pid = profile.getPid();
        state = FedoraState.fromString(profile.getState());
        stateOriginal = state;
        created = new Date(profile.getCreatedDate());
        lastModified = new Date(profile.getModifiedDate());
        title = profile.getTitle();
        titleOriginal = title;
        List<dk.statsbiblioteket.doms.central.Relation> frelations = profile.getRelations();

        for (dk.statsbiblioteket.doms.central.Relation frelation : frelations) {
            if (frelation.isLiteral()){
                relations.add(new LiteralRelation(frelation.getPredicate(),this,frelation.getSubject()));
            } else {
                relations.add(new ObjectRelation(frelation.getPredicate(),this,factory.getDigitalObject(
                        frelation.getSubject())));
            }
        }

        for (String contentModel : profile.getContentmodels()) {
            DigitalObject cm_object = factory.getDigitalObject(contentModel);
            if (cm_object instanceof ContentModelObject) {
                ContentModelObject object = (ContentModelObject) cm_object;
                type.add(object);
            } else {
                throw new ServerOperationFailed("Object '"+pid+"' has the content model '"+contentModel+"' declared, but this is not a content model");
            }
        }

        for (DatastreamProfile datastreamProfile : profile.getDatastreams()) {
            datastreams.add(new Datastream(datastreamProfile,this));
        }
    }

}
