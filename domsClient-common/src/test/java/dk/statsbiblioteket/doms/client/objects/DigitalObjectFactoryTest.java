package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.datastreams.Presentation;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.*;

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
        assertTrue(cmdoms instanceof ContentModelObject);
    }

    @Test
    public void  testRelations() throws Exception {
        DigitalObject cmdoms = factory.getDigitalObject("doms:ContentModel_Program");
        assertTrue(cmdoms instanceof ContentModelObject);
        if (cmdoms instanceof ContentModelObject) {
            ContentModelObject cmo = (ContentModelObject) cmdoms;
            assertNotNull(cmo.getRelationsWithViewAngle("SummaVisible"));
        }
    }

    @Test
    public void testDatastreamModel() throws Exception {
        ContentModelObject cmProgram = (ContentModelObject)
                factory.getDigitalObject("doms:ContentModel_Program");
        assertTrue(cmProgram instanceof ContentModelObject);
        if (cmProgram instanceof ContentModelObject){
            DatastreamModel dsModel = cmProgram.getDsModel();
            assertTrue(dsModel.getDatastreamDeclarations().size() > 0);
            assertNotNull(dsModel.getMimeType());
            assertNotNull(dsModel.getFormatURI());
            DatastreamDeclaration dsDcl = dsModel.getDatastreamDeclarations().get(0);
        }
    }

     @Test
    public void testDatastreamModel2() throws Exception {
        DigitalObject template =
                factory.getDigitalObject("doms:Template_Program");
         Set<DatastreamDeclaration> declarations = template.getDatastream("PBCORE").getDeclarations();
         assertTrue(declarations.size() > 0);
         for (DatastreamDeclaration declaration : declarations) {
             assertEquals(declaration.getName(),"PBCORE");
             assertTrue(declaration.getPresentation() == Presentation.editable);
             Datastream pbcoreSchema = declaration.getSchema();
             if (pbcoreSchema != null){
                 assertNotNull(pbcoreSchema.getContents());
             } else {
                 fail();
             }
         }
    }

    @org.junit.Test
    public void testGetDigitalObject2() throws Exception {
        DigitalObject cmdoms = factory.getDigitalObject("doms:Root_Collection");
        assertEquals(cmdoms.getState(),FedoraState.Active);
        List<ObjectRelation> inverseRels = cmdoms.getInverseRelations();
        assertNotNull(inverseRels);
        assertTrue(inverseRels.size() > 3);

    }

    @org.junit.Test
    public void testSaveState() throws Exception {

        int i = 0;


        //Load the object, and assert that everything is Active
        DigitalObject object = factory.getDigitalObject("uuid:f8f1b607-1394-418a-a90e-e65d1b4bf91f");
        Set<DigitalObject> children = object.getChildObjects("SummaVisible");
        assertTrue(object.getState() == FedoraState.Active);
        for (DigitalObject child : children) {
            assertTrue(child.getState()==FedoraState.Active);
            i++;
        }
        assertTrue(i>0);
        i = 0;

        //Set the object and all subobjects to Inactive
        object.setState(FedoraState.Inactive, "SummaVisible");
        object.save("SummaVisible");
        for (DigitalObject child : children) {
            assertTrue(child.getState()==FedoraState.Inactive);
            i++;
        }
        assertTrue(i>0);
        i = 0;


        //To be sure that nothing is in cache, make a new factory
        setUp();

        //Load the object, and check that everything is now in Inactive
        DigitalObject object2 = factory.getDigitalObject("uuid:f8f1b607-1394-418a-a90e-e65d1b4bf91f");
        assertTrue(object2.getState() == FedoraState.Inactive);
        Set<DigitalObject> children2 = object2.getChildObjects("SummaVisible");
        for (DigitalObject child : children2) {
            assertTrue(child.getState()==FedoraState.Inactive);
            i++;
        }
        assertTrue(i>0);
        i = 0;

        //Then set everything to Active again
        object2.setState(FedoraState.Active, "SummaVisible");
        object2.save("SummaVisible");
        assertTrue(object2 .getState() == FedoraState.Inactive);
        for (DigitalObject child : children2) {
            assertTrue(child.getState()==FedoraState.Active);
            i++;
        }
        assertTrue(i>0);
        i = 0;

        setUp();

        DigitalObject object3 = factory.getDigitalObject("uuid:f8f1b607-1394-418a-a90e-e65d1b4bf91f");
        Set<DigitalObject> children3 = object3.getChildObjects("SummaVisible");
        for (DigitalObject child : children3) {
            assertTrue(child.getState()==FedoraState.Active);
            i++;
        }
        assertTrue(i>0);
        i = 0;

    }

}
