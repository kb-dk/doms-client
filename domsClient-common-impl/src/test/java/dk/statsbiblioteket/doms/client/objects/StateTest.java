package dk.statsbiblioteket.doms.client.objects;

import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.Checksum;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test state changes.
 */
public class StateTest {

    private static final String PID = "uuid:XXX";
    private static final String PID2 = "uuid:YYY";
    private static final String PID3 = "uuid:ZZZ";
    private static final String PIDCM = "doms:ContentModelProgram";
    private DigitalObjectFactory factory;
    private CentralWebservice domsAPI;

    public StateTest() throws MalformedURLException {
        super();
    }

    @Before
    public void setUp() throws Exception {
        domsAPI = mock(CentralWebservice.class);
        when(domsAPI.getObjectProfile(PID)).thenReturn(createObjectProfile());
        when(domsAPI.getObjectProfile(PID2)).thenReturn(createObject2Profile());
        when(domsAPI.getObjectProfile(PIDCM)).thenReturn(createCMProfile());
        when(domsAPI.getDatastreamContents(PIDCM, "VIEW")).thenReturn(createView());
        factory = new DigitalObjectFactoryImpl(domsAPI);
    }

    @Test
    public void testSaveStateInactive() throws Exception {
        //Load the object
        DigitalObject object = factory.getDigitalObject(PID);

        //should call doms
        verify(domsAPI).getObjectProfile(PID);
        verify(domsAPI).getObjectProfile(PIDCM);
        verifyNoMoreInteractions(domsAPI);

        //Get child objects for a view
        Set<DigitalObject> children = object.getChildObjects("GUI");

        //This should read the VIEW datastream
        verify(domsAPI).getDatastreamContents(PIDCM, "VIEW");
        verify(domsAPI).getObjectProfile(PID2);
        verifyNoMoreInteractions(domsAPI);

        //Check that expected childobject was found
        assertEquals(1, children.size());
        assertEquals(children.iterator().next().getPid(), PID2);

        //Set the object and all subobjects to Inactive
        object.setState(Constants.FedoraState.Inactive, "GUI");

        //No effect should be visible until save
        verifyNoMoreInteractions(domsAPI);

        //Save changes
        object.save("GUI");

        //This should call DOMS with the expected objects
        verify(domsAPI).markInProgressObject((List<String>) argThat(containsInAnyOrder(PID)), anyString());
        verify(domsAPI).markInProgressObject((List<String>) argThat(containsInAnyOrder(PID2)), anyString());
        verifyNoMoreInteractions(domsAPI);
    }

    @Test
    public void testSaveStateActiveNoOp() throws Exception {
        //Load the object
        DigitalObject object = factory.getDigitalObject(PID);

        //should call doms
        verify(domsAPI).getObjectProfile(PID);
        verify(domsAPI).getObjectProfile(PIDCM);
        verifyNoMoreInteractions(domsAPI);

        //Get child objects for a view
        Set<DigitalObject> children = object.getChildObjects("GUI");

        //This should read the VIEW datastream
        verify(domsAPI).getDatastreamContents(PIDCM, "VIEW");
        verify(domsAPI).getObjectProfile(PID2);
        verifyNoMoreInteractions(domsAPI);

        //Check that expected childobject was found
        assertEquals(1, children.size());
        assertEquals(children.iterator().next().getPid(), PID2);

        //Set the object and all subobjects to Inactive
        object.setState(Constants.FedoraState.Active, "GUI");

        //No effect should be visible until save
        verifyNoMoreInteractions(domsAPI);

        //Save changes
        object.save("GUI");

        //Objects are already active - nothing should happen.
        verifyNoMoreInteractions(domsAPI);
    }

    private ObjectProfile createObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid(PID);
        profile.setState("A");
        profile.setTitle("Object");
        profile.getContentmodels().add(PIDCM);
        profile.getRelations().addAll(createPID1relations());
        return profile;
    }

    private ObjectProfile createObject2Profile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid(PID2);
        profile.setState("A");
        profile.setTitle("Object");
        profile.getContentmodels().add(PIDCM);
        return profile;
    }

    private ObjectProfile createCMProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid(PIDCM);
        profile.setState("A");
        profile.setTitle("Object");
        profile.setType("ContentModel");
        DatastreamProfile datastream = new DatastreamProfile();
        datastream.setId("VIEW");
        Checksum checksum = new Checksum();
        checksum.setType("DISABLED");
        datastream.setChecksum(checksum);
        profile.getDatastreams().add(datastream);
        return profile;
    }

    private String createView() {
        return "<v:views xmlns:v=\"http://doms.statsbiblioteket.dk/types/view/default/0/1/#\">\n"
                + "    <v:viewangle name=\"GUI\">\n"
                + "        <v:relations xmlns:doms=\"http://doms.statsbiblioteket.dk/relations/default/0/1/#\">\n"
                + "            <doms:hasFile/>\n"
                + "        </v:relations>\n"
                + "        <v:inverseRelations/>\n"
                + "    </v:viewangle>\n"
                + "</v:views>\n"
                + "\n";
    }

    private List<Relation> createPID1relations() {
        List<Relation> result = new ArrayList<Relation>();
        Relation relation = new Relation();
        relation.setSubject(PID);
        relation.setPredicate("http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile");
        relation.setObject(PID2);
        relation.setLiteral(false);
        result.add(relation);
        Relation relation2 = new Relation();
        relation2.setSubject(PID);
        relation2.setPredicate("http://example.com/#otherRelation");
        relation2.setObject(PID3);
        relation2.setLiteral(false);
        result.add(relation2);
        return result;
    }


}
