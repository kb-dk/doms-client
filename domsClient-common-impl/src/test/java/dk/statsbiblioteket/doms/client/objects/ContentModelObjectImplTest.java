package dk.statsbiblioteket.doms.client.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.client.impl.objects.ContentModelObjectImpl;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test of ContentModelObject implementation.
 */
public class ContentModelObjectImplTest {

    static public final String DOMS_RELATIONS_NAMESPACE = "http://doms.statsbiblioteket.dk/relations/default/0/1/#";

    private CentralWebservice centralWebservice;
    private ContentModelObjectImpl contentModel;

    @Before
    public void setUp() throws Exception {
        // Common fixture: Content Model Object based on mock centralWebservice. Contains ContentModel and temple for a
        // program object.
        centralWebservice = mock(CentralWebservice.class);
        when(centralWebservice.getObjectProfile("doms:ContentModel_Program"))
                .thenReturn(createContentModelProgramObjectProfile());
        when(centralWebservice.getObjectProfile("doms:Template_Program"))
                .thenReturn(createTemplateProgramObjectProfile());
        when(centralWebservice.getInverseRelationsWithPredicate("doms:ContentModel_Program",
                                                                DOMS_RELATIONS_NAMESPACE + "isTemplateFor"))
                .thenReturn(createInverseTemplateProgramRelations());
        contentModel = new ContentModelObjectImpl(createContentModelProgramObjectProfile(), centralWebservice,
                                                  new DigitalObjectFactoryImpl(centralWebservice));
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test getting templates from a content model object.
     *
     * @throws Exception
     */
    @Test
    public void testGetTemplates() throws Exception {
        //Call method
        Set<TemplateObject> templates = contentModel.getTemplates();

        //Check calls
        verify(centralWebservice).getInverseRelationsWithPredicate("doms:ContentModel_Program", DOMS_RELATIONS_NAMESPACE + "isTemplateFor");
        verify(centralWebservice).getObjectProfile("doms:Template_Program");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should be a list containing the expected template", 1, templates.size());
        assertEquals("Should be a list containing the expected template",
                     "doms:Template_Program", templates.iterator().next().getPid());
    }

    /**
     * Test getting entry view angles for a content model.
     *
     * @throws Exception
     */
    @Test
    public void testGetEntryViewAngles() throws Exception {
        Set<String> viewAngles = contentModel.getEntryViewAngles();

        verifyNoMoreInteractions(centralWebservice);

        assertEquals("Should return the expected viewangle", new HashSet<String>(Collections.singletonList("GUI")), viewAngles);
    }

    /**
     * An object profile for a content model with one entry viewangle defined.
     *
     * @return The object profile.
     */
    private ObjectProfile createContentModelProgramObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("doms:ContentModel_Program");
        profile.setState("A");
        profile.setType("ContentModel");
        profile.setTitle("Program");
        Relation relation = new Relation();
        relation.setObject("info:fedora/doms:ContentModel_Program");
        relation.setPredicate(DOMS_RELATIONS_NAMESPACE + "isEntryForViewAngle");
        relation.setObject("GUI");
        relation.setLiteral(true);
        profile.getRelations().add(relation);

        return profile;
    }

    /**
     * An object profile for a template for the content model above.
     *
     * @return The object profile.
     */
    private ObjectProfile createTemplateProgramObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("doms:Template_Program");
        profile.setState("I");
        profile.setType("TemplateObject");
        profile.setTitle("Program");

        return profile;
    }

    /**
     * Get the relation between the two above objects.
     *
     * @return That relation.
     */
    private List<Relation> createInverseTemplateProgramRelations() {
        dk.statsbiblioteket.doms.central.Relation relation = new dk.statsbiblioteket.doms.central.Relation();
        relation.setSubject("info:fedora/doms:Template_Program");
        relation.setPredicate(DOMS_RELATIONS_NAMESPACE + "isTemplateFor");
        relation.setObject("info:fedora/doms:ContentModel_Program");
        relation.setLiteral(false);
        return Collections.singletonList(relation);
    }
}