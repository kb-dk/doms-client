package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.FedoraState;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 8/15/11
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResult {

	private String pid;
	private List<String> type;
	private String title;
	private FedoraState state;
    private Date lastModified;
    private Date created;

    public SearchResult(String pid, List<String> type, String title, FedoraState state, Date lastModified, Date created) {
        this.pid = pid;
        this.type = type;
        this.title = title;
        this.state = state;
        this.lastModified = lastModified;
        this.created = created;
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
}

