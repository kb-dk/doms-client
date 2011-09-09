package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;

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

    public AbstractDomsClient(URL domsWSAPIEndpoint, String userName,
                               String password) {
        domsAPI = new CentralWebserviceService(domsWSAPIEndpoint,
                                               CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI)
                .getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    public List<Relation> listObjectRelations(String objectPID, String relationType)
            throws ServerOperationFailed {
        try {
            List<dk.statsbiblioteket.doms.central.Relation> domsRelations =
                    domsAPI.getNamedRelations(objectPID, relationType);
            DigitalObjectFactory dof = new DigitalObjectFactory();
            ArrayList<Relation> clientRelations = new ArrayList<Relation>();
            for (dk.statsbiblioteket.doms.central.Relation domsRelation : domsRelations) {
                clientRelations.add(new LiteralRelation(
                                                 domsRelation.getPredicate(),
                                                 dof.getDigitalObject(domsRelation.getObject()),
                                                 domsRelation.getSubject()));
            }
            return clientRelations;

        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed listing object relations (type: " + relationType
                    + ") from the source object (PID: " + objectPID
                    + ")",
                    exception);
        }
    }

    public FedoraState getState(String pid) throws ServerOperationFailed {
        // TODO: Uncomment when implemented in DOMS Central
        // return domsAPI.getState(pid);
        return FedoraState.Active;
    }

    public String getLabel(String uuid) {
        ArrayList lst = new ArrayList();
        lst.add(uuid);
        return getLabels(lst).get(0);
    }

    public List<String> getLabels(List<String> uuids) {
        // domsAPI.getLabels(uuids);
        // TODO Implement the get label method on the server side.
        return uuids;
    }

    public InputStream getDatastreamContent(String pid, String ds) throws ServerOperationFailed{

        try {
            return new ByteArrayInputStream(domsAPI.getDatastreamContents(pid, ds).getBytes());
        } catch (Exception exception) {
            throw new ServerOperationFailed("Failed getting datastream ('"
                                            + ds + "') on DOMS object (PID = " + pid
                                            + ")." , exception);
        }
    }
}
