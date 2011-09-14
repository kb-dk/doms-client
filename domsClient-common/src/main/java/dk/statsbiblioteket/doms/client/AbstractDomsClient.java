package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.FedoraState;
import dk.statsbiblioteket.doms.client.objects.MissingObject;
import dk.statsbiblioteket.doms.client.relations.LiteralRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/6/11
 * Time: 9:01 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDomsClient implements DomsClient {

    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");
    /**
     * Reference to the active DOMS webservice client instance.
     */
    protected CentralWebservice domsAPI;
    private DigitalObjectFactory factory;

    public AbstractDomsClient(URL domsWSAPIEndpoint, String userName,
                              String password) {
        domsAPI = new CentralWebserviceService(domsWSAPIEndpoint,
                                               CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI)
                .getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
        factory = new DigitalObjectFactory(domsAPI);
    }

    /**
     *
     * @param pid    ID of the object housing the relation.
     * @param relationType AbstractRelation type ID which is valid according the the content
     *                     model for the source object.
     * @return
     * @throws ServerOperationFailed
     * @deprecated use the objects instead
     */
    public List<Relation> listObjectRelations(String pid, String relationType)
            throws ServerOperationFailed {
        DigitalObject object = getFactory().getDigitalObject(pid);
        if (object instanceof MissingObject){
            return null;
        } else {
            List<Relation> relations = object.getRelations();
            List<Relation> result = new ArrayList<Relation>();
            for (Relation relation : relations) {
                if (relation.getPredicate().equals(relationType)){
                    result.add(relation);
                }
            }
            return result;
        }
    }

    /**
     *
     * @param pid       The PID identifying the object of intrest.
     * @return
     * @throws ServerOperationFailed
     * @deprecated Use the objects instead
     */
    public FedoraState getState(String pid) throws ServerOperationFailed {
        DigitalObject object = getFactory().getDigitalObject(pid);
        if (object instanceof MissingObject){
            return null;
        } else {
            return object.getState();
        }
    }

    /**
     *
     * @param pid the uuid for which to get the label
     * @return
     * @deprecated Use the objects instead
     */
    public String getLabel(String pid) throws ServerOperationFailed {
        DigitalObject object = getFactory().getDigitalObject(pid);
        if (object instanceof MissingObject){
            return null;
        } else {
            return object.getTitle();
        }
    }

    /**
     *
     * @param uuids the list of uuid's for which to get the label
     * @return
     * @deprecated Do not use
     */
    public List<String> getLabels(List<String> uuids) {
        // domsAPI.getLabels(uuids);
        // TODO Implement the get label method on the server side.
        return uuids;
    }

    /**
     *
     * @param pid the persistent identifier of the object of intrest.
     * @param ds identifies the datastream of intrest.
     * @return
     * @throws ServerOperationFailed
     * @deprecated TODO implement the datastream methods instead
     */
    public InputStream getDatastreamContent(String pid, String ds) throws ServerOperationFailed{

        try {
            return new ByteArrayInputStream(domsAPI.getDatastreamContents(pid, ds).getBytes());
        } catch (Exception exception) {
            throw new ServerOperationFailed("Failed getting datastream ('"
                                            + ds + "') on DOMS object (PID = " + pid
                                            + ")." , exception);
        }
    }

    /**
     * Get the factory to read the objects
     * @return
     */
    public DigitalObjectFactory getFactory() {
        return factory;
    }
}
