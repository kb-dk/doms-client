package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.objects.ContentModelObjectImpl;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
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
        factory = new DigitalObjectFactoryImpl(domsAPI);

    }

    @org.junit.Test
    public void testLoadTime() throws ServerOperationFailed {
        long before = System.currentTimeMillis();
        DigitalObject object = factory.getDigitalObject("uuid:f8f1b607-1394-418a-a90e-e65d1b4bf91f");
        long after = System.currentTimeMillis();
        System.out.println("Time to load one object="+(after-before)+"ms");

        before = System.currentTimeMillis();
        DigitalObject object2 = factory.getDigitalObject("uuid:26e3048b-824d-476b-8b1f-671d7906e28a");
        after = System.currentTimeMillis();
        System.out.println("Time to load next object="+(after-before)+"ms");
        long load1 = after - before;


        before = System.currentTimeMillis();
        List<Relation> relations = object.getRelations();
        after = System.currentTimeMillis();
        System.out.println("Time to resolve relations for object1="+(after-before)+"ms, as there was "+relations.size()+" relations");

        before = System.currentTimeMillis();
        List<Relation> relations2 = object2.getRelations();
        after = System.currentTimeMillis();
        System.out.println("Time to resolve relations for object2="+(after-before)+"ms, as there was "+relations2.size()+" relations");
        load1 += after-before;
        load1 /= 2;

        before = System.currentTimeMillis();
        List<ObjectRelation> invrelations = object.getInverseRelations();
        after = System.currentTimeMillis();
        System.out.println("Time to resolve inverrelations object="+(after-before)+"ms, as there was "+invrelations.size()+" relations");

        System.out.println("Time to load one object is about "+load1+"ms when all dependencies are already loaded");

        before = System.currentTimeMillis();
        int sum = 0;
        for (Datastream datastream : object.getDatastreams()) {
            String contents = datastream.getContents();
            sum += contents.length();
        }
        after = System.currentTimeMillis();
        System.out.println("Time to resolve all datastreams in object1="+(after-before)+"ms, as there was "+object.getDatastreams().size()+" datastreams");

    }


    @org.junit.Test
    public void testGetDigitalObject1() throws Exception {
        DigitalObject cmdoms = factory.getDigitalObject("doms:ContentModel_DOMS");
        assertEquals(cmdoms.getState(),FedoraState.Active);
        assertTrue(cmdoms instanceof ContentModelObjectImpl);
    }

    @org.junit.Test
    public void testGetDigitalObject2() throws Exception {
        DigitalObject cmdoms = factory.getDigitalObject("doms:Root_Collection");
        assertEquals(cmdoms.getState(),FedoraState.Active);
        List<ObjectRelation> inverseRels = cmdoms.getInverseRelations();
        assertNotNull(inverseRels);
        assertTrue(inverseRels.size() > 3);

    }

}
