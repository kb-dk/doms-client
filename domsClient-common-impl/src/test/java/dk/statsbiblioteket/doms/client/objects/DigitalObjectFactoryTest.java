package dk.statsbiblioteket.doms.client.objects;

import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Test creating digital objects
 */
public class DigitalObjectFactoryTest {
    static public final String DOMS_RELATIONS_NAMESPACE = "http://doms.statsbiblioteket.dk/relations/default/0/1/#";

    private CentralWebservice centralWebservice;
    private DigitalObjectFactory factory;

    public DigitalObjectFactoryTest() throws MalformedURLException {
        super();
    }

    @Before
    public void setUp() throws Exception {
        centralWebservice = mock(CentralWebservice.class);
        when(centralWebservice.getObjectProfile("doms:ContentModel_Program"))
                .thenReturn(createContentModelProgramObjectProfile());
        when(centralWebservice.getObjectProfile("doms:ContentModel_DOMS"))
                .thenReturn(createContentModelDOMSObjectProfile());
        when(centralWebservice.getObjectProfile("uuid:68081375-cc57-4e8c-a6b2-25dad34f4ad4"))
                .thenReturn(createDataObjectProfile());
        when(centralWebservice.getObjectProfile("doms:RadioTV_Collection"))
                .thenReturn(createRadioTVCollectionObjectProfile());
        when(centralWebservice.getInverseRelationsWithPredicate("doms:ContentModel_DOMS",
                                                                DOMS_RELATIONS_NAMESPACE + "extendsModel"))
                .thenReturn(createInverseContentModelDOMSRelations());
        when(centralWebservice.getInverseRelationsWithPredicate("doms:ContentModel_Program",
                                                                DOMS_RELATIONS_NAMESPACE + "extendsModel"))
                .thenReturn(createInverseContentModelProgramRelations());

        factory = new DigitalObjectFactoryImpl(centralWebservice);
    }

    /**
     * Test that we cache values when calling the server
     */
    @Test
    public void testCaching() throws Exception {
        //Call method once
        DigitalObject object = factory.getDigitalObject("uuid:68081375-cc57-4e8c-a6b2-25dad34f4ad4");

        //Assert expected calls
        verify(centralWebservice, times(1)).getObjectProfile("uuid:68081375-cc57-4e8c-a6b2-25dad34f4ad4");
        verify(centralWebservice, times(1)).getObjectProfile("doms:ContentModel_Program");
        verify(centralWebservice, times(1)).getObjectProfile("doms:ContentModel_DOMS");
        verifyNoMoreInteractions(centralWebservice);

        //Call method again
        DigitalObject object2 = factory.getDigitalObject("uuid:68081375-cc57-4e8c-a6b2-25dad34f4ad4");

        //Assert no calls
        verifyNoMoreInteractions(centralWebservice);

        //Loading relations should not require server contact
        List<Relation> relations = object.getRelations();

        //Assert expected calls
        verifyNoMoreInteractions(centralWebservice);

        //Loading inverse relations should call the server

        List<ObjectRelation> invrelations = object.getInverseRelations();

        //Assert expected call
        verify(centralWebservice, times(1)).getInverseRelations("uuid:68081375-cc57-4e8c-a6b2-25dad34f4ad4");
        verifyNoMoreInteractions(centralWebservice);

        //But only the first time. No calls expected the second time.

        List<ObjectRelation> invrelations2 = object2.getInverseRelations();
        verifyNoMoreInteractions(centralWebservice);
    }

    /**
     * Test that we can read a content model object
     */
    @Test
    public void testGetDigitalObject1() throws Exception {
        DigitalObject cmprogram = factory.getDigitalObject("doms:ContentModel_Program");

        //Verify calls
        verify(centralWebservice).getObjectProfile("doms:ContentModel_Program");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals(cmprogram.getState(), Constants.FedoraState.Active);
        assertTrue(cmprogram instanceof ContentModelObject);
    }

    /**
     * Test that we can read a collection object
     */
    @Test
    public void testGetDigitalObject2() throws Exception {
        DigitalObject collection = factory.getDigitalObject("doms:RadioTV_Collection");

        //Verify calls
        verify(centralWebservice).getObjectProfile("doms:RadioTV_Collection");
        verifyNoMoreInteractions(centralWebservice);

        //Check results
        assertEquals(collection.getState(), Constants.FedoraState.Active);
        assertTrue(collection instanceof CollectionObject);
    }

    /**
     * Check that when we ask for a content model, we get the most specific one.
     */
    @Test
    public void testMostSpecificCM() throws Exception {
        //Call method
        DigitalObject object = factory.getDigitalObject("uuid:68081375-cc57-4e8c-a6b2-25dad34f4ad4");

        //Verify calls
        verify(centralWebservice).getObjectProfile("uuid:68081375-cc57-4e8c-a6b2-25dad34f4ad4");
        verify(centralWebservice).getObjectProfile("doms:ContentModel_Program");
        verify(centralWebservice).getObjectProfile("doms:ContentModel_DOMS");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertTrue(object instanceof DataObject);
        DataObject dataObject = (DataObject) object;

        //Call method
        String objectTitle = dataObject.getContentmodelTitle();

        //Verify calls
        verify(centralWebservice).getInverseRelationsWithPredicate("doms:ContentModel_Program",
                                                                   DOMS_RELATIONS_NAMESPACE + "extendsModel");
        verify(centralWebservice).getInverseRelationsWithPredicate("doms:ContentModel_DOMS",
                                                                   DOMS_RELATIONS_NAMESPACE + "extendsModel");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals(objectTitle, "Program");
    }


    private ObjectProfile createContentModelProgramObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("doms:ContentModel_Program");
        profile.setState("A");
        profile.setType("ContentModel");
        profile.setTitle("Program");

        return profile;
    }

    private ObjectProfile createContentModelDOMSObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("doms:ContentModel_DOMS");
        profile.setState("A");
        profile.setType("ContentModel");
        profile.setTitle("DOMS");

        return profile;
    }

    private ObjectProfile createDataObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("uuid:68081375-cc57-4e8c-a6b2-25dad34f4ad4");
        profile.setState("A");
        profile.setTitle("Test program");
        profile.setType("DataObject");

        profile.getContentmodels().add("doms:ContentModel_DOMS");
        profile.getContentmodels().add("doms:ContentModel_Program");

        return profile;
    }

    private ObjectProfile createRadioTVCollectionObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("doms:RadioTVCollection");
        profile.setState("A");
        profile.setTitle("");
        profile.setType("CollectionObject");

        return profile;
    }

    private List<dk.statsbiblioteket.doms.central.Relation> createInverseContentModelDOMSRelations() {
        dk.statsbiblioteket.doms.central.Relation relation = new dk.statsbiblioteket.doms.central.Relation();
        relation.setObject("info:fedora/doms:ContentModel_DOMS");
        relation.setPredicate(DOMS_RELATIONS_NAMESPACE + "extendsModel");
        relation.setSubject("info:fedora/doms:ContentModel_Program");
        relation.setLiteral(false);
        return Collections.singletonList(relation);
    }

    private List<dk.statsbiblioteket.doms.central.Relation> createInverseContentModelProgramRelations() {
        return Collections.emptyList();
    }
}
