package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.ValidationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.impl.datastreams.ExternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.datastreams.InternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.datastreams.SaveableDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.relations.LiteralRelationImpl;
import dk.statsbiblioteket.doms.client.impl.relations.ObjectRelationImpl;
import dk.statsbiblioteket.doms.client.objects.CollectionObject;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.relations.LiteralRelation;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.utils.Constants;

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

    private Constants.FedoraState state;
    private Constants.FedoraState stateOriginal;

    private Date lastModified;
    private Date created;

    protected Set<SaveableDatastreamImpl> deletedDSs, addedDSs;
    protected Set<Datastream> datastreams;


    protected TreeSet<dk.statsbiblioteket.doms.client.relations.Relation> relations;
    private TreeSet<dk.statsbiblioteket.doms.client.relations.Relation> removedRelations;
    private TreeSet<dk.statsbiblioteket.doms.client.relations.Relation> addedRelations;

    private List<ObjectRelation> inverseRelations;

    private boolean cmloaded = false;
    private boolean relsloaded = false;
    private boolean invrelsloaded = false;
    private boolean profileloaded = false;
    private boolean statePreSaved = false;


    public AbstractDigitalObject(String pid,
                                 CentralWebservice api,
                                 DigitalObjectFactory factory) throws ServerOperationFailed{

        this.pid = pid;
        this.api = api;
        this.factory = factory;
        type = new ArrayList<ContentModelObject>();
        datastreams = new HashSet<Datastream>();
        addedDSs = new HashSet<SaveableDatastreamImpl>();
        deletedDSs = new HashSet<SaveableDatastreamImpl>();

        relations = new TreeSet<dk.statsbiblioteket.doms.client.relations.Relation>();
        removedRelations = new TreeSet<dk.statsbiblioteket.doms.client.relations.Relation>();
        addedRelations = new TreeSet<dk.statsbiblioteket.doms.client.relations.Relation>();

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
    public Constants.FedoraState getState() throws ServerOperationFailed {
        loadProfile();
        return state;
    }

    @Override
    public void setState(Constants.FedoraState state) throws ServerOperationFailed {
        loadProfile();
        this.state = state;
    }

    @Override
    public void setState(Constants.FedoraState state, String viewAngle) throws ServerOperationFailed {
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
        return Collections.unmodifiableList(new ArrayList<Datastream>(datastreams));
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
        if (addition instanceof SaveableDatastreamImpl) {
            SaveableDatastreamImpl saveableDatastream = (SaveableDatastreamImpl) addition;
            addedDSs.add(saveableDatastream);
        }
        datastreams.add(addition);
    }

    @Override
    public void removeDatastream(Datastream deleted) throws ServerOperationFailed {
        loadProfile();
        if (deleted instanceof SaveableDatastreamImpl) {
            SaveableDatastreamImpl saveableDatastream = (SaveableDatastreamImpl) deleted;
            deletedDSs.add(saveableDatastream);
        }

        datastreams.remove(deleted);
    }

    @Override
    public List<dk.statsbiblioteket.doms.client.relations.Relation> getRelations() throws ServerOperationFailed {
        loadRelations();
        TreeSet<dk.statsbiblioteket.doms.client.relations.Relation> totalSet =
                new TreeSet<dk.statsbiblioteket.doms.client.relations.Relation>(relations);
        totalSet.addAll(addedRelations);
        totalSet.removeAll(removedRelations);
        return Collections.unmodifiableList(new ArrayList<dk.statsbiblioteket.doms.client.relations.Relation>(totalSet));
    }

    @Override
    public void removeRelation(dk.statsbiblioteket.doms.client.relations.Relation relation) {
        if (relation.getSubjectPid().equals(this.getPid())){
            privateRemoveRelation(relation);
        }
    }

    @Override
    public List<ObjectRelation> getInverseRelations() throws ServerOperationFailed {
        loadInverseRelations();
        return Collections.unmodifiableList(inverseRelations);
    }

    @Override
    public List<ObjectRelation> getInverseRelations(String predicate) throws ServerOperationFailed {
        List<Relation> frelations;
        try {
            frelations = api.getInverseRelations(pid);
        } catch (Exception e) {
            throw new ServerOperationFailed("Failed to load inverse relations", e);
        }
        List<ObjectRelation> result = new ArrayList<ObjectRelation>();
        for (dk.statsbiblioteket.doms.central.Relation frelation : frelations) {
            if (frelation.getPredicate().equals(predicate)){ //TODO do not request unneeded relations from server
                result.add(new ObjectRelationImpl(frelation.getSubject(), frelation.getPredicate(),
                                                  frelation.getObject(),
                                                  factory));
            }
        }
        return result;
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
                relations.add(new LiteralRelationImpl(this.getPid(), frelation.getPredicate(), frelation.getObject(),factory));
            } else {
                relations.add(new ObjectRelationImpl(this.getPid(),frelation.getPredicate(),frelation.getObject(), factory));
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
            inverseRelations.add(new ObjectRelationImpl(frelation.getSubject(), frelation.getPredicate(),
                                                        frelation.getObject(),
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
            if (cm_object instanceof ContentModelObject) {
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

        state = Constants.FedoraState.fromString(profile.getState());
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
                    children.add(objectRelation.getObject());
                }
            }
        }
        return children;
    }




    private void preSaveDatastreams()
            throws ServerOperationFailed, XMLParseException {
        for (SaveableDatastreamImpl deletedDS : deletedDSs) {
            deletedDS.markAsDeleted();
        }
        for (SaveableDatastreamImpl addedDS : addedDSs) {
            addedDS.create();
        }
        for (Datastream datastream : datastreams) {
            if (datastream instanceof SaveableDatastreamImpl) {
                SaveableDatastreamImpl saveableDatastream = (SaveableDatastreamImpl) datastream;
                saveableDatastream.preSave();
            }
        }
    }




    private void preSaveState()
            throws ServerOperationFailed {
        if (state.equals(stateOriginal)){
            return;
        }
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
            String message = e.getMessage();
            if (message.contains("dk.statsbiblioteket.doms.ecm.fedoravalidatorhook.ValidationFailedException")){
                int begin = message.indexOf("<validation");
                int end = message.indexOf("</validation>");
                if (end > begin  && begin > 0){
                    end += "</validation>".length();
                    String xmlMessage = message.substring(begin, end);
                    throw new ValidationFailed(xmlMessage,e);
                }
            }
            throw new ServerOperationFailed(e.getMessage(),e);
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


    protected void preSave(String viewAngle) throws ServerOperationFailed, XMLParseException {

        List<AbstractDigitalObject> saved = new ArrayList<AbstractDigitalObject>();
        try {
            Set<DigitalObject> children = getChildObjects(viewAngle);
            for (DigitalObject child : children) {
                if (child instanceof AbstractDigitalObject) {
                    AbstractDigitalObject abstractChild = (AbstractDigitalObject) child;
                    abstractChild.preSave(viewAngle);
                }
            }
            if(!getState().equals(Constants.FedoraState.Active)){
                preSaveState();
                preSaveTitle();
                preSaveDatastreams();
                preSaveRelations();
            } else {
                preSaveDatastreams();
                preSaveRelations();
                preSaveTitle();
                preSaveState();
            }

        } catch (ServerOperationFailed e){
            for (AbstractDigitalObject digitalObject : saved) {
                digitalObject.undoSave(viewAngle);
            }
            try {
                undoSave(viewAngle);
            } catch (Exception e2){
                e2.printStackTrace();
            }
            throw new ServerOperationFailed(e.getMessage(),e);
        } catch (XMLParseException e) {
            for (AbstractDigitalObject digitalObject : saved) {
                digitalObject.undoSave(viewAngle);
            }
            try {
                undoSave(viewAngle);
            } catch (Exception e2){
                e2.printStackTrace();//TODO log
            }
            throw new XMLParseException(e.getMessage(),e);
        }


    }

    private void preSaveTitle() throws ServerOperationFailed {
        if (title.equals(titleOriginal)){
            return;
        }
        try {
            api.setObjectLabel(this.getPid(),title,"Changing the object label");
        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
            throw new ServerOperationFailed(e);
        }
    }

    protected  void preSaveRelations() throws ServerOperationFailed {
        try {

            for (dk.statsbiblioteket.doms.client.relations.Relation addedRelation : addedRelations) {
                Relation apiRelation = toApiRelation(addedRelation);
                api.addRelation(this.getPid(),apiRelation,"Added a relation from the Doms Client");
            }
            for (dk.statsbiblioteket.doms.client.relations.Relation removedRelation : removedRelations) {
                Relation apiRelation = toApiRelation(removedRelation);
                api.deleteRelation(this.getPid(), apiRelation, "Added a relation from the Doms Client");
            }

        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
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
        postSaveDatastreams();
        postSaveRelations();
        postSaveTitle();
        postSaveState();
    }

    private void postSaveTitle() {
        titleOriginal = title;
    }

    private void postSaveRelations() {
        for (dk.statsbiblioteket.doms.client.relations.Relation addedRelation : addedRelations) {
            relations.add(addedRelation);
        }
        for (dk.statsbiblioteket.doms.client.relations.Relation removedRelation : removedRelations) {
            relations.remove(removedRelation);
        }
    }

    protected void postSaveDatastreams(){
        for (Datastream datastream : datastreams) {
            if (datastream instanceof SaveableDatastreamImpl) {
                SaveableDatastreamImpl saveableDatastream = (SaveableDatastreamImpl) datastream;
                saveableDatastream.postSave();
            }
        }

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
        if (getState().equals(Constants.FedoraState.Active)){
            undoSaveState();
            undoSaveDatastreams();
            undoSaveRelations();
            undoSaveTitle();
        } else {
            undoSaveDatastreams();
            undoSaveRelations();
            undoSaveTitle();
            undoSaveState();
        }

    }

    private void undoSaveTitle() throws ServerOperationFailed {
        if (title.equals(titleOriginal)){
            return;
        }
        try {
            api.setObjectLabel(this.getPid(),titleOriginal,"Undoing change of object label");
        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
            throw new ServerOperationFailed(e);
        }
    }

    private void undoSaveRelations() throws ServerOperationFailed {
        try {
            for (dk.statsbiblioteket.doms.client.relations.Relation removedRelation : removedRelations) {
                Relation apiRelation = toApiRelation(removedRelation);
                api.addRelation(this.getPid(),apiRelation,"Added a relation from the Doms Client");
            }
            for (dk.statsbiblioteket.doms.client.relations.Relation addedRelation : addedRelations) {
                Relation apiRelation = toApiRelation(addedRelation);
                api.deleteRelation(this.getPid(), apiRelation, "Added a relation from the Doms Client");
            }
        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
            throw new ServerOperationFailed(e);
        }


    }

    private Relation toApiRelation(dk.statsbiblioteket.doms.client.relations.Relation addedRelation) {
        Relation apiRelation = new Relation();

        if (addedRelation instanceof ObjectRelation) {
            ObjectRelation relation = (ObjectRelation) addedRelation;
            apiRelation.setLiteral(false);
            apiRelation.setObject(Constants.ensurePID(relation.getObjectPid()));
            apiRelation.setSubject(this.getPid());
            apiRelation.setPredicate(relation.getPredicate());
        } else if (addedRelation instanceof LiteralRelation) {
            LiteralRelation relation = (LiteralRelation) addedRelation;
            apiRelation.setLiteral(true);
            apiRelation.setObject(relation.getObject());
            apiRelation.setPredicate(relation.getPredicate());
            apiRelation.setSubject(this.getPid());
        }
        return apiRelation;
    }

    private void undoSaveDatastreams() throws ServerOperationFailed {
        for (Datastream datastream : datastreams) {
            if (datastream instanceof SaveableDatastreamImpl) {
                SaveableDatastreamImpl saveableDatastream = (SaveableDatastreamImpl) datastream;
                saveableDatastream.undoSave();
            }
        }
    }


    public void save(String viewAngle) throws ServerOperationFailed, XMLParseException {
        this.preSave(viewAngle);
        this.postSave(viewAngle);
    }



    public void save() throws ServerOperationFailed, XMLParseException {
        save("UNUSEDVIEWANGLE");
    }

    @Override
    public String toString() {
        return pid;
    }

    @Override
    public ObjectRelation addObjectRelation(String predicate, DigitalObject object) throws ServerOperationFailed {
        ObjectRelation rel = new ObjectRelationImpl(this.getPid(),predicate,object.getPid(),  factory);
        privateAddRelation(rel);
        return rel;

    }

    @Override
    public LiteralRelation addLiteralRelation(String predicate, String value) {
        LiteralRelation rel = new LiteralRelationImpl(this.getPid(), predicate, value,factory);
        privateAddRelation(rel);
        return rel;
    }

    @Override
    public Set<CollectionObject> getCollections() throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void addToCollection(CollectionObject collection) throws ServerOperationFailed {
        collection.addObject(this);
    }

    private void privateAddRelation(dk.statsbiblioteket.doms.client.relations.Relation relation){
        boolean isOriginal = relations.contains(relation);
        boolean isAlreadyRemoved = removedRelations.contains(relation);
        boolean isAdded = addedRelations.contains(relation);

        if (isAlreadyRemoved){
            removedRelations.remove(relation);
            return;
        }
        if (!isAdded && !isOriginal){
            addedRelations.add(relation);
        }
    }

    private void privateRemoveRelation(dk.statsbiblioteket.doms.client.relations.Relation relation){
        boolean isOriginal = relations.contains(relation);
        boolean isAlreadyRemoved = removedRelations.contains(relation);
        boolean isAdded = addedRelations.contains(relation);
        if (!(isOriginal ||  isAdded)){//is this anywhere?
            return;//if not present, return
        }

        if (isAdded){
            addedRelations.remove(relation);
            return;
        }
        if (!isAlreadyRemoved && isOriginal){
            removedRelations.add(relation);
        }

    }


}
