package dk.statsbiblioteket.doms.client.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.impl.objects.CollectionObjectImpl;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test of Collection objects using our achernar test server.
 */
public class CollectionObjectImplIT {
    private static final String TEST_SERVER_URL = "http://achernar:7880/centralWebservice-service/central/";
    private static final String TEST_SERVER_USERNAME = "fedoraAdmin";
    private static final String TEST_SERVER_PASSWORD = "fedoraAdminPass";
    private CollectionObjectImpl collection;
    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName("http://central.doms.statsbiblioteket.dk/",
                                                                      "CentralWebserviceService");


    @Before
    public void setUp() throws Exception {
        //Common fixture: The Newspaper Collection object from our achernar test server.
        URL domsWSAPIEndpoint = new URL(TEST_SERVER_URL);
        CentralWebservice centralWebservice = new CentralWebserviceService(domsWSAPIEndpoint,
                                                                           CENTRAL_WEBSERVICE_SERVICE)
                .getCentralWebservicePort();
        Map<String, Object> domsAPILogin = ((BindingProvider) centralWebservice).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, TEST_SERVER_USERNAME);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, TEST_SERVER_PASSWORD);
        collection = new CollectionObjectImpl(centralWebservice.getObjectProfile("doms:Newspaper_Collection"),
                                              centralWebservice, new DigitalObjectFactoryImpl(centralWebservice));
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test getting entry content models.
     *
     * @throws Exception
     */
    @Test
    public void testGetEntryContentModels() throws Exception {
        Set<ContentModelObject> entryContentModels = collection.getEntryContentModels("GUI");

        assertEquals("There should be two entry content models", 2, entryContentModels.size());
        Iterator<ContentModelObject> iterator = entryContentModels.iterator();
        String pid1 = iterator.next().getPid();
        String pid2 = iterator.next().getPid();
        assertTrue(pid1.equals("doms:ContentModel_Newspaper") || pid1.equals("doms:ContentModel_Edition"));
        assertTrue(pid2.equals("doms:ContentModel_Newspaper") || pid2.equals("doms:ContentModel_Edition"));
    }
}