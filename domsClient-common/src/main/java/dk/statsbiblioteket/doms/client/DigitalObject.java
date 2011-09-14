package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;

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
public interface DigitalObject {
    String getPid();


    List<ContentModelObject> getType();


    String getTitle();

    void setTitle(String title);

    FedoraState getState();

    void setState(FedoraState state);

    Date getLastModified();


    Date getCreated();



    List<Datastream> getDatastreams();

    void addDatastream(Datastream addition);

    void removeDatastream(Datastream deleted);

    List<Relation> getRelations();


    List<ObjectRelation> getInverseRelations();

    void load()
            throws ServerOperationFailed;

}
