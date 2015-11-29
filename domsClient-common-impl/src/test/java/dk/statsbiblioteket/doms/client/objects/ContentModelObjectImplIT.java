package dk.statsbiblioteket.doms.client.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.impl.objects.ContentModelObjectImpl;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test using out achernar test server to test content model operations.
 */
public class ContentModelObjectImplIT {

    private static final String TEST_SERVER_URL = "http://achernar:7880/centralWebservice-service/central/";
    private static final String TEST_SERVER_USERNAME = "fedoraAdmin";
    private static final String TEST_SERVER_PASSWORD = "fedoraAdminPass";
    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName("http://central.doms.statsbiblioteket.dk/",
                                                                      "CentralWebserviceService");
    private ContentModelObjectImpl contentModel;

    @Before
    public void setUp() throws Exception {
        // Common fixture: The Content Model object for Program on our test server.
        URL domsWSAPIEndpoint = new URL(TEST_SERVER_URL);
        CentralWebservice centralWebservice = new CentralWebserviceService(domsWSAPIEndpoint,
                                                                           CENTRAL_WEBSERVICE_SERVICE)
                .getCentralWebservicePort();
        Map<String, Object> domsAPILogin = ((BindingProvider) centralWebservice).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, TEST_SERVER_USERNAME);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, TEST_SERVER_PASSWORD);
        contentModel = new ContentModelObjectImpl(centralWebservice.getObjectProfile("doms:ContentModel_Program"),
                                                  centralWebservice, new DigitalObjectFactoryImpl(centralWebservice));
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test getting templates.
     *
     * @throws Exception
     */
    @Test
    public void testGetTemplates() throws Exception {
        Set<TemplateObject> templates = contentModel.getTemplates();

        assertEquals("Should have one template", 1, templates.size());
        assertEquals("Should have one template", "doms:Template_Program", templates.iterator().next().getPid());
    }

    /**
     * Test getting entry view angles.
     *
     * @throws Exception
     */
    @Test
    public void testGetEntryViewAngles() throws Exception {
        Set<String> viewAngles = contentModel.getEntryViewAngles();

        assertEquals("Should have the two expected viewangles", Arrays.asList("GUI", "SummaVisible"), viewAngles);
    }
}