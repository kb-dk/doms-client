package dk.statsbiblioteket.doms.client;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/8/11
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DigitalObject extends java.io.Serializable {
        String getPid();

        void setPid(String pid);

        List<ContentModelObject> getType();

        void setType(List<ContentModelObject> type);

        String getTitle();

        void setTitle(String title);

        FedoraState getState();

        void setState(FedoraState state);

        Date getLastModified();

        void setLastModified(Date lastModified);

        Date getCreated();

        void setCreated(Date created);

        List<String> getDatastreamTitles();

        void setDatastreamTitles(List<String> datastreamTitles);

        List<Relation> getInRelations();

        void setInRelations(List<Relation> inRelations);

        List<Relation> getOutRelations();

        void setOutRelations(List<Relation> outRelations);
    }
