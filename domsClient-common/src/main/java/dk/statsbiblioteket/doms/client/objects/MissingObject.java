package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.relations.LiteralRelation;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * For some purposes it is easier to return this, rather than throwing an exception, if an object cannot be found.
 */
public class MissingObject implements DigitalObject {
    @Override
    public void save() throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void save(String viewAngle) throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public String getPid() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public Set<CollectionObject> getCollections() throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void addToCollection(CollectionObject collection) throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void addToCollection(CollectionObject collection, String viewAngle) throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public List<ContentModelObject> getType() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public String getTitle() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void setTitle(String title) {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public Constants.FedoraState getState() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void setState(Constants.FedoraState state) {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void setState(Constants.FedoraState state, String viewAngle) throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public Date getLastModified() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public Date getCreated() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public List<Datastream> getDatastreams() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public Datastream getDatastream(String id) {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void addDatastream(Datastream addition) {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void removeDatastream(Datastream deleted) {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public List<Relation> getRelations() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public List<ObjectRelation> getInverseRelations() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public List<ObjectRelation> getInverseRelations(String predicate) throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void removeRelation(Relation relation) {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public ObjectRelation addObjectRelation(String predicate, DigitalObject object) throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public LiteralRelation addLiteralRelation(String predicate, String value) {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public Set<DigitalObject> getChildObjects(String viewAngle) throws ServerOperationFailed {
        throw new IllegalAccessError("Missing object");
    }

}
