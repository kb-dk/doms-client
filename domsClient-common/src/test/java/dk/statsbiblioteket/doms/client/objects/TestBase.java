package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.impl.AbstractDomsClient;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestBase {

    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");
    private URL domsWSAPIEndpoint;
    private String userName = "fedoraAdmin";
    private String password = "fedoraAdminPass";
    public DigitalObjectFactory factory;

    public TestBase() throws MalformedURLException {
        domsWSAPIEndpoint = new URL("http://alhena:7880/centralWebservice-service/central/");
    }


    @org.junit.Before
    public void setUp() throws Exception {

        CentralWebservice domsAPI = new CentralWebserviceService(domsWSAPIEndpoint,
                                                                 CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI)
                .getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
        factory = new DigitalObjectFactoryImpl(domsAPI);
    }
    @Test
    public void emptyTest(){

    }

}
