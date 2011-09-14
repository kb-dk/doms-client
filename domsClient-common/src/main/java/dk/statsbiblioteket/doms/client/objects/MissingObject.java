package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/14/11
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MissingObject implements DigitalObject {
    @Override
    public String getPid() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ContentModelObject> getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTitle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTitle(String title) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FedoraState getState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setState(FedoraState state) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getLastModified() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getCreated() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Datastream> getDatastreams() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addDatastream(Datastream addition) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeDatastream(Datastream deleted) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Relation> getRelations() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ObjectRelation> getInverseRelations() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void load()  {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
