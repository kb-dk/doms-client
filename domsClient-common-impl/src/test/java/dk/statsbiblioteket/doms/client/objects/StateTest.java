package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.utils.Constants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Set;

import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/28/11
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class StateTest extends TestBase {
    public StateTest() throws MalformedURLException {
        super();
    }

    @Before
    public void makeActive() throws Exception {
        super.setUp();
        DigitalObject object = factory.getDigitalObject(victimProgram);
        //Set the object and all subobjects to Inactive
        object.setState(Constants.FedoraState.Active, "SummaVisible");
        object.save("SummaVisible");
    }

    @Test
    @Ignore("Alhena test disabled due to alhena not using java 8")
    public void testSaveState() throws Exception {

        int i = 0;


        //Load the object, and assert that everything is Active
        DigitalObject object = factory.getDigitalObject(victimProgram);
        Set<DigitalObject> children = object.getChildObjects("GUI");

        assertTrue(object.getState() == Constants.FedoraState.Active);
        for (DigitalObject child : children) {
            assertTrue(child.getState() == Constants.FedoraState.Active);
            i++;
        }
        assertTrue(i > 0);
        i = 0;

        //Set the object and all subobjects to Inactive
        object.setState(Constants.FedoraState.Inactive, "GUI");
        object.save("GUI");
        for (DigitalObject child : children) {
            assertTrue(child.getState() == Constants.FedoraState.Inactive);
            i++;
        }
        assertTrue(i > 0);
        i = 0;


        //To be sure that nothing is in cache, make a new factory
        setUp();

        //Load the object, and check that everything is now in Inactive
        DigitalObject object2 = factory.getDigitalObject(victimProgram);
        assertTrue(object2.getState() == Constants.FedoraState.Inactive);
        Set<DigitalObject> children2 = object2.getChildObjects("GUI");
        for (DigitalObject child : children2) {
            assertTrue(child.getState() == Constants.FedoraState.Inactive);
            i++;
        }
        assertTrue(i > 0);
        i = 0;

        //Then set everything to Active again
        object2.setState(Constants.FedoraState.Active, "GUI");
        object2.save("GUI");
        assertTrue(object2.getState() == Constants.FedoraState.Active);
        for (DigitalObject child : children2) {
            assertTrue(child.getState() == Constants.FedoraState.Active);
            i++;
        }
        assertTrue(i > 0);
        i = 0;

        setUp();

        DigitalObject object3 = factory.getDigitalObject(victimProgram);
        Set<DigitalObject> children3 = object3.getChildObjects("GUI");
        for (DigitalObject child : children3) {
            assertTrue(child.getState() == Constants.FedoraState.Active);
            i++;
        }
        assertTrue(i > 0);
        i = 0;

    }


}
