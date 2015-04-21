package dk.statsbiblioteket.doms.client.objects;

import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.Checksum;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.relations.LiteralRelation;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test relation operations
 */
public class RelationsTest {

    private static final String UUID = "uuid:XXX";
    private static final String CM_PID = "doms:ContentModel_Program";
    private DigitalObjectFactory factory;
    private CentralWebservice domsAPI;

    public RelationsTest() throws MalformedURLException {
        super();
    }

    @Before
    public void setUp() throws Exception {
        domsAPI = mock(CentralWebservice.class);
        factory = new DigitalObjectFactoryImpl(domsAPI);
        when(domsAPI.getObjectProfile(UUID)).thenReturn(createObjectProfile());
        when(domsAPI.getObjectProfile(CM_PID)).thenReturn(createCMObjectProfile());
        when(domsAPI.getDatastreamContents(CM_PID, "VIEW")).thenReturn(createView());
        when(domsAPI.getInverseRelations(CM_PID)).thenReturn(createInverseRelations());
    }

    /**
     * Test that relations are related to their content model view annotations.
     *
     * @throws Exception
     */
    @Test
    public void testViewRelations() throws Exception {
        //Read content model object
        DigitalObject cmdoms = factory.getDigitalObject(CM_PID);

        //This should call DOMS
        verify(domsAPI).getObjectProfile(CM_PID);
        verifyNoMoreInteractions(domsAPI);

        //Read view relations
        ContentModelObject cmo = (ContentModelObject) cmdoms;
        List<String> relationsWithViewAngle = cmo.getRelationsWithViewAngle("SummaVisible");

        //This should trigger a read of the VIEW relations
        verify(domsAPI).getDatastreamContents(CM_PID, "VIEW");
        verifyNoMoreInteractions(domsAPI);

        //A view relation should be found.
        assertNotNull(relationsWithViewAngle);
        assertEquals(1, relationsWithViewAngle.size());
        assertEquals("http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile", relationsWithViewAngle.get(0));
    }


    /**
     * Test getting inverse relations.
     * @throws Exception
     */
    @Test
    public void testInverseRelations() throws Exception {
        //Read content model object
        DigitalObject cmdoms = factory.getDigitalObject(CM_PID);

        //This should call DOMS
        verify(domsAPI).getObjectProfile(CM_PID);
        verifyNoMoreInteractions(domsAPI);

        //Read inverse relations
        List<ObjectRelation> inverseRelations = cmdoms.getInverseRelations();

        //This should call DOMS
        verify(domsAPI).getInverseRelations(CM_PID);
        verifyNoMoreInteractions(domsAPI);

        //Check results
        assertEquals(1, inverseRelations.size());
        assertEquals(inverseRelations.get(0).getObjectPid(), cmdoms.getPid());
        assertNotNull(inverseRelations.get(0).getSubjectPid());
        verifyNoMoreInteractions(domsAPI);

        //Check object can be read from relation
        DigitalObject subject = inverseRelations.get(0).getSubject();

        //Reads object from DOMS
        verify(domsAPI).getObjectProfile(UUID);

        //Is right object
        assertEquals(UUID, subject.getPid());
    }

    /**
     * Test adding literal relations.
     * @throws Exception
     */
    @Test
    public void testAddedRelationLiteral() throws Exception {
        //Read object
        DigitalObject object = factory.getDigitalObject(UUID);

        //This should call DOMS
        verify(domsAPI).getObjectProfile(UUID);
        verifyNoMoreInteractions(domsAPI);

        //Add a relation
        LiteralRelation newRel = object
                .addLiteralRelation("http://domclient.unittests/#testRelationPredicateLiteral", "literalValue");

        //This should not call DOMS until the object is saved.
        verifyNoMoreInteractions(domsAPI);

        //Save the change
        object.save();

        //This should call DOMS
        verify(domsAPI).addRelation(eq(UUID),
                                    isThisRelation(UUID, "http://domclient.unittests/#testRelationPredicateLiteral",
                                                   "literalValue", true), anyString());
        verifyNoMoreInteractions(domsAPI);
    }

    /**
     * Test removing literal relations.
     * @throws Exception
     */
    @Test
    public void testRemovedRelationLiteral() throws Exception {
        //Read object
        DigitalObject object = factory.getDigitalObject(UUID);

        //This should call DOMS
        verify(domsAPI).getObjectProfile(UUID);
        verifyNoMoreInteractions(domsAPI);

        //Find literal relation
        LiteralRelation literalRelation = null;
        for (Relation relation : object.getRelations()) {
            if (relation instanceof LiteralRelation) {
                literalRelation = (LiteralRelation) relation;
                break;
            }
        }
        assertNotNull("A literal relation should be found", literalRelation);

        //Remove literal relation
        literalRelation.remove();

        //This should not call DOMS until the object is saved.
        verifyNoMoreInteractions(domsAPI);

        //Save the change
        object.save();

        //This should call DOMS
        verify(domsAPI).deleteRelation(eq(UUID),
                                       isThisRelation(literalRelation.getSubjectPid(), literalRelation.getPredicate(),
                                                      literalRelation.getObject(), true), anyString());
        verifyNoMoreInteractions(domsAPI);
    }

    /**
     * Test adding object relations.
     * @throws Exception
     */
    @Test
    public void testAddedRelationObject() throws Exception {
        //Read object
        DigitalObject object = factory.getDigitalObject(UUID);

        //This should call DOMS
        verify(domsAPI).getObjectProfile(UUID);
        verifyNoMoreInteractions(domsAPI);

        //Add a relation
        object.addObjectRelation("http://domsclient.unittests/#testRelationPredicateObject", object);

        //This should not call DOMS until the object is saved.
        verifyNoMoreInteractions(domsAPI);

        //Save the change
        object.save();

        //This should call DOMS
        verify(domsAPI).addRelation(eq(UUID),
                                    isThisRelation(UUID, "http://domsclient.unittests/#testRelationPredicateObject",
                                                   UUID, false), anyString());
        verifyNoMoreInteractions(domsAPI);
    }

    /**
     * Test removing object relations.
     * @throws Exception
     */
    @Test
    public void testRemovedRelationObject() throws Exception {
        //Read object
        DigitalObject object = factory.getDigitalObject(UUID);

        //This should call DOMS
        verify(domsAPI).getObjectProfile(UUID);
        verifyNoMoreInteractions(domsAPI);

        //Find object relation
        ObjectRelation objectRelation = null;
        for (Relation relation : object.getRelations()) {
            if (relation instanceof ObjectRelation) {
                objectRelation = (ObjectRelation) relation;
                break;
            }
        }
        assertNotNull(objectRelation);

        //Remove object relation
        objectRelation.remove();

        //This should not call DOMS until the object is saved.
        verifyNoMoreInteractions(domsAPI);

        //Save the change
        object.save();

        //This should call DOMS
        verify(domsAPI).deleteRelation(eq(UUID),
                                       isThisRelation(objectRelation.getSubjectPid(), objectRelation.getPredicate(),
                                                      objectRelation.getObjectPid(), false), anyString());
        verifyNoMoreInteractions(domsAPI);

    }

    private ObjectProfile createObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid(UUID);
        objectProfile.setState("I");
        objectProfile.setTitle("Object");
        dk.statsbiblioteket.doms.central.Relation relation1 = new dk.statsbiblioteket.doms.central.Relation();
        relation1.setSubject("info:fedora/" + UUID);
        relation1.setPredicate("info:test/relation");
        relation1.setObject("Hello");
        relation1.setLiteral(true);
        dk.statsbiblioteket.doms.central.Relation relation2 = new dk.statsbiblioteket.doms.central.Relation();
        relation2.setSubject("info:fedora/" + UUID);
        relation2.setPredicate("info:test/relation2");
        relation2.setObject("info:fedora/doms:ContentModel_Program");
        relation2.setLiteral(false);
        objectProfile.getRelations().add(relation1);
        objectProfile.getRelations().add(relation2);
        return objectProfile;
    }

    private ObjectProfile createCMObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid(CM_PID);
        objectProfile.setType("ContentModel");
        objectProfile.setState("A");
        DatastreamProfile datastreamProfile = new DatastreamProfile();
        datastreamProfile.setId("VIEW");
        Checksum checksum = new Checksum();
        checksum.setType("DISABLED");
        datastreamProfile.setChecksum(checksum);
        objectProfile.getDatastreams().add(datastreamProfile);
        return objectProfile;
    }

    private String createView() {
        return "<v:views xmlns:v=\"http://doms.statsbiblioteket.dk/types/view/default/0/1/#\">\n"
                + "    <v:viewangle name=\"SummaVisible\">\n"
                + "        <v:relations xmlns:doms=\"http://doms.statsbiblioteket.dk/relations/default/0/1/#\">\n"
                + "            <doms:hasFile/>\n"
                + "        </v:relations>\n"
                + "        <v:inverseRelations/>\n"
                + "    </v:viewangle>\n"
                + "</v:views>\n";
    }

    private List<dk.statsbiblioteket.doms.central.Relation> createInverseRelations() {
        dk.statsbiblioteket.doms.central.Relation relation = new dk.statsbiblioteket.doms.central.Relation();
        relation.setSubject(UUID);
        relation.setPredicate("dk.statsbiblioteket.doms.client.objects.RelationsTest.testInverseRelations");
        relation.setObject(CM_PID);
        relation.setLiteral(false);

        return Arrays.asList(relation);
    }


    private static dk.statsbiblioteket.doms.central.Relation isThisRelation(String subject, String predicate, String object, boolean literal) {
        return argThat(new IsThisRelation(subject, predicate, object, literal));
    }

    private static class IsThisRelation extends ArgumentMatcher<dk.statsbiblioteket.doms.central.Relation> {
        private final String subject;
        private final String predicate;
        private final String object;
        private final boolean literal;

        public IsThisRelation(String subject, String predicate, String object, boolean literal) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            this.literal = literal;
        }

        @Override
        public boolean matches(Object argument) {
            dk.statsbiblioteket.doms.central.Relation that = (dk.statsbiblioteket.doms.central.Relation) argument;
            return (clean(subject).equals(clean(that.getSubject()))
                    && predicate.equals(that.getPredicate())
                    && clean(object).equals(clean(that.getObject()))
                    && literal == that.isLiteral());
        }

        private String clean(String s) {
            return s.replaceAll("^info:fedora/", "");
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue(
                    String.format("<relation: (%s, %s, %s), literal: %s>", subject, predicate, object, literal));
        }
    }
}
