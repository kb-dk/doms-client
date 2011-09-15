package dk.statsbiblioteket.doms.client.impl;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.DomsClient;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.objects.MissingObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.FedoraState;
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
        factory = DigitalObjectFactory.getInstance(domsAPI);
    }

    /**
     * Get the factory to read the objects
     * @return
     */
    public DigitalObjectFactory getFactory() {
        return factory;
    }
}
