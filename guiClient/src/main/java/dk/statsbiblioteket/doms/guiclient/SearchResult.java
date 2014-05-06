package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A search result, with links to the digital object system.
 */
public class SearchResult {

    private String pid;
    private String type;
    private String source;
    private String title;
    private String time;
    private String description;
    private Constants.FedoraState state;
    private Date lastModified;
    private Date created;
    private DigitalObjectFactory factory;


    public SearchResult(String pid, String type, String source, String title, String time, String description,
                        Constants.FedoraState state, Date lastModified, Date created, DigitalObjectFactory factory) {
        this.pid = pid;
        this.type = type;
        this.source = source;
        this.title = title;
        this.time = time;
        this.description = description;
        this.state = state;
        this.lastModified = lastModified;
        this.created = created;
        this.factory = factory;
    }

    public String getPid() {
        return pid;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public Constants.FedoraState getState() {
        return state;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Date getCreated() {
        return created;
    }

    public String getTypeString() {
        return getType();
    }

    public String getLastModifiedString() {
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        return df.format(lastModified);
    }

    /**
     * Retrieve the object and inflate it in the object system
     *
     * @return a proper digital object.
     * @throws ServerOperationFailed
     */
    public DigitalObject getObject() throws ServerOperationFailed {
        return factory.getDigitalObject(pid);
    }
}

