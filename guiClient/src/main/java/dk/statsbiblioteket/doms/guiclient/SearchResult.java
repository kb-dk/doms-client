package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.FedoraState;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A search result, with links to the digital object system.
 */
public class SearchResult {

	private String pid;
	private List<String> type;
	private String title;
	private FedoraState state;
    private Date lastModified;
    private Date created;
    private DigitalObjectFactory factory;


    public SearchResult(String pid, List<String> type, String title, FedoraState state, Date lastModified, Date created,
                        DigitalObjectFactory factory) {
        this.pid = pid;
        this.type = type;
        this.title = title;
        this.state = state;
        this.lastModified = lastModified;
        this.created = created;
        this.factory = factory;
    }

    public String getPid() {
        return pid;
    }

    public List<String> getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public FedoraState getState() {
        return state;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Date getCreated() {
        return created;
    }

    public String getTypeString(){
        String retString = "";
        for (String t : type){
            retString += t;
        }
        return retString;
    }

    public String getLastModifiedString() {
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        return df.format(lastModified);
    }

    /**
     * Retrieve the object and inflate it in the object system
     * @return a proper digital object.
     * @throws ServerOperationFailed
     */
    public DigitalObject getObject() throws ServerOperationFailed {
        return factory.getDigitalObject(pid);
    }
}

