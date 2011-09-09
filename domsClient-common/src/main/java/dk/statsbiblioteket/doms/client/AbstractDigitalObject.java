package dk.statsbiblioteket.doms.client;

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

    private String pid;
	private List<ContentModelObject> type;
	private String title;
	private FedoraState state;
    private Date lastModified;
    private Date created;
    private List<String> datastreamTitles;
    private List<Relation> inRelations;
    private List<Relation> outRelations;

    public AbstractDigitalObject(String pid){

    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public List<ContentModelObject> getType() {
        return type;
    }

    @Override
    public void setType(List<ContentModelObject> type) {
        this.type = type;
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
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public List<String> getDatastreamTitles() {
        return datastreamTitles;
    }

    @Override
    public void setDatastreamTitles(List<String> datastreamTitles) {
        this.datastreamTitles = datastreamTitles;
    }

    @Override
    public List<Relation> getInRelations() {
        return inRelations;
    }

    @Override
    public void setInRelations(List<Relation> inRelations) {
        this.inRelations = inRelations;
    }

    @Override
    public List<Relation> getOutRelations() {
        return outRelations;
    }

    @Override
    public void setOutRelations(List<Relation> outRelations) {
        this.outRelations = outRelations;
    }
}
