package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;

import java.util.Date;
import java.util.List;

/**
 * For some purposes it is easier to return this, rather than throwing an exception, if an object cannot be found.
 */
public class MissingObject implements DigitalObject {
    @Override
    public String getPid() {
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
    public FedoraState getState() {
        throw new IllegalAccessError("Missing object");
    }

    @Override
    public void setState(FedoraState state) {
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

}
