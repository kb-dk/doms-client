package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DigitalObjectFactoryTest {

    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");
    private URL domsWSAPIEndpoint;
    private String userName = "fedoraAdmin";
    private String password = "fedoraAdminPass";
    private DigitalObjectFactory factory;

    public DigitalObjectFactoryTest() throws MalformedURLException {
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
        factory = new DigitalObjectFactory(domsAPI);

    }

    @org.junit.Test
    public void testGetDigitalObject() throws Exception {
        DigitalObject cmdoms = factory.getDigitalObject("doms:ContentModel_DOMS");
        assertEquals(cmdoms.getState(),FedoraState.Active);
        assertTrue(cmdoms instanceof ContentModelObject);
    }
}
