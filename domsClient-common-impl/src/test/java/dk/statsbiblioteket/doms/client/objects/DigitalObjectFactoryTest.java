package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.datastreams.InternalDatastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import dk.statsbiblioteket.doms.client.utils.Constants;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DigitalObjectFactoryTest extends TestBase{


    public DigitalObjectFactoryTest() throws MalformedURLException {
        super();
    }

    @org.junit.Test
    public void testLoadTime() throws ServerOperationFailed {
        long before = System.currentTimeMillis();
        DigitalObject object = factory.getDigitalObject(victimProgram);
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
        assertEquals(cmdoms.getState(), Constants.FedoraState.Active);
        assertTrue(cmdoms instanceof ContentModelObject);
    }




    @org.junit.Test
    public void testGetDigitalObject2() throws Exception {
        DigitalObject cmdoms = factory.getDigitalObject("doms:Root_Collection");
        assertEquals(cmdoms.getState(), Constants.FedoraState.Active);
        List<ObjectRelation> inverseRels = cmdoms.getInverseRelations();
        assertNotNull(inverseRels);
        assertTrue(inverseRels.size() > 3);

    }

    @Test
    public void testMostSpecificCM() throws ServerOperationFailed {
        DigitalObject object = factory.getDigitalObject(victimProgram);
        if (object instanceof DataObject) {
            DataObject dataObject = (DataObject) object;
            String cmTitle = dataObject.getContentmodelTitle();
            assertEquals(cmTitle,"Radio/TV Program");

        }   else {
            fail();
        }

    }


}
