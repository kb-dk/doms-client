package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.Checksum;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.relations.RelationDeclaration;
import dk.statsbiblioteket.doms.client.relations.RelationModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test a common collection workflow.
 */
public class CollectionTest {

    static public final String DOMS_RELATIONS_NAMESPACE = "http://doms.statsbiblioteket.dk/relations/default/0/1/#";
    private CentralWebservice centralWebservice;
    private DigitalObjectFactory factory;

    public CollectionTest() throws MalformedURLException {
        super();
    }

    @Before
    public void setUp() throws Exception {
        //Set up fixture
        centralWebservice = mock(CentralWebservice.class);
        //Five previously known objects: One collection, Two content models, and Two templates
        when(centralWebservice.getObjectProfile("doms:RadioTV_Collection"))
                .thenReturn(createRadioTVCollectionObjectProfile());
        when(centralWebservice.getObjectProfile("doms:ContentModel_Program"))
                .thenReturn(createContentModelProgramObjectProfile());
        when(centralWebservice.getObjectProfile("doms:ContentModel_RadioTVFile"))
                .thenReturn(createContentModelRadioTVFileObjectProfile());
        when(centralWebservice.getObjectProfile("doms:Template_Program"))
                .thenReturn(createTemplateProgramObjectProfile());
        when(centralWebservice.getObjectProfile("doms:Template_RadioTVFile"))
                .thenReturn(createTemplateRadioTVFileObjectProfile());

        //Two known datastreams in the Program content model that define a relationship in the GUI view to the
        //RadioTVFile content model
        when(centralWebservice.getDatastreamContents("doms:ContentModel_Program", "ONTOLOGY"))
                .thenReturn(createOntology());
        when(centralWebservice.getDatastreamContents("doms:ContentModel_Program", "VIEW")).thenReturn(createView());

        //List content models for the Radio TV Collection
        when(centralWebservice.getContentModelsInCollection("doms:RadioTV_Collection"))
                .thenReturn(Arrays.asList("doms:ContentModel_Program", "doms:ContentModel_RadioTVFile"));

        //List template relations for the two content models
        when(centralWebservice.getInverseRelationsWithPredicate("doms:ContentModel_Program",
                                                                DOMS_RELATIONS_NAMESPACE + "isTemplateFor"))
                .thenReturn(createInverseRelationsForProgram());
        when(centralWebservice.getInverseRelationsWithPredicate("doms:ContentModel_RadioTVFile",
                                                                DOMS_RELATIONS_NAMESPACE + "isTemplateFor"))
                .thenReturn(createInverseRelationsForRadioTVFile());

        //Two new objects created in the workflow
        when(centralWebservice.newObject(eq("doms:Template_Program"), anyList(), anyString())).thenReturn("new:object");
        when(centralWebservice.newObject(eq("doms:Template_RadioTVFile"), anyList(), anyString()))
                .thenReturn("new:object2");
        when(centralWebservice.getObjectProfile("new:object")).thenReturn(createNewObjectObjectProfile());
        when(centralWebservice.getObjectProfile("new:object2")).thenReturn(createNewObject2ObjectProfile());

        factory = new DigitalObjectFactoryImpl(centralWebservice);
    }

    @Test
    /**
     * Test a common workflow: Find entry content models in collection, clone a template for that, find
     * related content model, clone a template for that, connect the cloned objects and save it.
     */
    public void testCollection1() throws Exception {
        //1. GET THE COLLECTION OBJECT

        //Call method
        DigitalObject object = factory.getDigitalObject("doms:RadioTV_Collection");

        //Assert expected calls
        //We should read the object profile
        verify(centralWebservice).getObjectProfile("doms:RadioTV_Collection");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertTrue("Should be a collection object", object instanceof CollectionObject);
        CollectionObject collectionObject = (CollectionObject) object;

        //2. GET ENTRY TEMPLATES

        //Call method
        Set<TemplateObject> entryTemplates = collectionObject.getEntryTemplates("GUI");

        //Assert expected calls
        //We should find content models
        verify(centralWebservice).getContentModelsInCollection("doms:RadioTV_Collection");
        //We should read all the content models
        verify(centralWebservice).getObjectProfile("doms:ContentModel_RadioTVFile");
        verify(centralWebservice).getObjectProfile("doms:ContentModel_Program");
        //We should look for templates in the entry content model
        verify(centralWebservice).getInverseRelationsWithPredicate("doms:ContentModel_Program",
                                                                   DOMS_RELATIONS_NAMESPACE + "isTemplateFor");
        //We should read the template object
        verify(centralWebservice).getObjectProfile("doms:Template_Program");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should have one result", 1, entryTemplates.size());
        TemplateObject entryTemplate = entryTemplates.iterator().next();
        assertEquals("Should find template object", "doms:Template_Program", entryTemplate.getPid());

        //3. CLONE THE TEMPLATE

        //Call method
        DigitalObject newProgram = entryTemplate.clone();

        //Assert expected calls
        //We should clone the template object
        verify(centralWebservice).newObject(eq("doms:Template_Program"), anyList(), anyString());
        //We should read the new object
        verify(centralWebservice).getObjectProfile("new:object");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should have right pid", "new:object", newProgram.getPid());
        assertTrue("Should be a data object", newProgram instanceof DataObject);
        assertEquals("Should have one content model", 1, newProgram.getType().size());
        assertEquals("Should be the right one", "doms:ContentModel_Program",
                     newProgram.getType().iterator().next().getPid());

        //4. GET THE RELATIONS MODEL

        //Call method
        ContentModelObject contentModelObject = newProgram.getType().iterator().next();
        RelationModel relModel = contentModelObject.getRelationModel();

        //Assert expected calls
        //We should read the ontology
        verify(centralWebservice).getDatastreamContents("doms:ContentModel_Program", "ONTOLOGY");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should be for right content model", "doms:ContentModel_Program",
                     relModel.getContentModel().getPid());

        //5. GET RELATED CONTENT MODELS

        //Call method
        Set<RelationDeclaration> relationDeclarations = relModel.getRelationDeclarations();

        //Assert expected calls
        //We should read the view
        verify(centralWebservice).getDatastreamContents("doms:ContentModel_Program", "VIEW");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should have one known relation", 1, relationDeclarations.size());
        RelationDeclaration relationDeclaration = relationDeclarations.iterator().next();
        assertEquals("Should have the right known relation", DOMS_RELATIONS_NAMESPACE + "hasFile",
                     relationDeclaration.getPredicate());

        assertTrue("Should be in GUI view", relationDeclaration.getViewAngles().contains("GUI"));

        Set<ContentModelObject> firstLevelObjects = relationDeclaration.getFirstLevelModels();
        assertEquals("Should have one content model", 1, firstLevelObjects.size());
        ContentModelObject firstLevelObject = firstLevelObjects.iterator().next();
        assertEquals("Should be right content model", "doms:ContentModel_RadioTVFile", firstLevelObject.getPid());

        //6. FIND TEMPLATES FOR SUB OBJECT

        //Call method
        Set<TemplateObject> templateDeep = firstLevelObject.getTemplates();

        //Assert expected calls
        //Should look for objects with template relations for RadioTVFile
        verify(centralWebservice).getInverseRelationsWithPredicate("doms:ContentModel_RadioTVFile", DOMS_RELATIONS_NAMESPACE + "isTemplateFor");
        //Should retrieve profile of found template
        verify(centralWebservice).getObjectProfile("doms:Template_RadioTVFile");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should have found one template", 1, templateDeep.size());
        TemplateObject templateObject = templateDeep.iterator().next();
        assertEquals("Should be the right template", "doms:Template_RadioTVFile", templateObject.getPid());

        //7. CLONE THAT TEMPLATE

        //Call method
        DigitalObject newFileObject = templateObject.clone();

        //Assert expected calls
        //We should clone the template object
        verify(centralWebservice).newObject(eq("doms:Template_RadioTVFile"), anyList(), anyString());
        //We should read the new object
        verify(centralWebservice).getObjectProfile("new:object2");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should have right PID", "new:object2", newFileObject.getPid());
        assertEquals("Should have right Content Model", "doms:ContentModel_RadioTVFile", newFileObject.getType().iterator().next().getPid());

        //8. ADD RELATION BETWEEN THE TWO OBJECTS

        //Call method
        newProgram.addObjectRelation(relationDeclaration.getPredicate(), newFileObject);

        //Assert expected calls
        verifyNoMoreInteractions(centralWebservice);

        //9. SAVE THE CHANGES

        //Call method
        newProgram.save("GUI");

        //Assert expected calls
        //Only the new relation is unsaved
        verify(centralWebservice).addRelation(eq("new:object"), isThisRelation("info:fedora/" + "new:object",
                                                                               DOMS_RELATIONS_NAMESPACE + "hasFile",
                                                                               "info:fedora/" + "new:object2", false), anyString());
        verifyNoMoreInteractions(centralWebservice);
    }

    private String createOntology() {
        return "<rdf:RDF\n"
                + "        xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "        xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "        xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "    <owl:Class rdf:about=\"info:fedora/doms:ContentModel_Program#class\">\n"
                + "        <rdfs:subClassOf>\n"
                + "            <owl:Restriction>\n"
                + "                <owl:onProperty\n"
                + "                        rdf:resource=\"http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile\"/>\n"
                + "                <owl:allValuesFrom\n"
                + "                        rdf:resource=\"info:fedora/doms:ContentModel_RadioTVFile#class\"/>\n"
                + "            </owl:Restriction>\n"
                + "        </rdfs:subClassOf>\n"
                + "    </owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile\"/>\n"
                + "</rdf:RDF>\n";
    }

    private String createView() {
        return "<v:views xmlns:v=\"http://doms.statsbiblioteket.dk/types/view/default/0/1/#\">\n"
                + "    <v:viewangle name=\"GUI\">\n"
                + "        <v:relations xmlns:doms=\"http://doms.statsbiblioteket.dk/relations/default/0/1/#\">\n"
                + "            <doms:hasFile/>\n"
                + "        </v:relations>\n"
                + "        <v:inverseRelations/>\n"
                + "    </v:viewangle>\n"
                + "</v:views>\n";
    }

    private List<Relation> createInverseRelationsForProgram() {
        Relation relation = new Relation();
        relation.setSubject("doms:Template_Program");
        relation.setPredicate(DOMS_RELATIONS_NAMESPACE + "isTemplateFor");
        relation.setObject("doms:ContentModel_Program");
        relation.setLiteral(false);
        return Arrays.asList(relation);
    }

    private List<Relation> createInverseRelationsForRadioTVFile() {
        Relation relation = new Relation();
        relation.setSubject("doms:Template_RadioTVFile");
        relation.setPredicate(DOMS_RELATIONS_NAMESPACE + "isTemplateFor");
        relation.setObject("doms:ContentModel_RadionTVFile");
        relation.setLiteral(false);
        return Arrays.asList(relation);
    }

    private ObjectProfile createNewObjectObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid("new:object");
        objectProfile.setType("DataObject");
        objectProfile.setState("I");
        objectProfile.setTitle("New Object");
        objectProfile.getContentmodels().add("doms:ContentModel_Program");
        return objectProfile;
    }

    private ObjectProfile createNewObject2ObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid("new:object2");
        objectProfile.setType("DataObject");
        objectProfile.setState("I");
        objectProfile.setTitle("New Object 2");
        objectProfile.getContentmodels().add("doms:ContentModel_RadioTVFile");
        return objectProfile;
    }

    private ObjectProfile createTemplateProgramObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid("doms:Template_Program");
        objectProfile.setType("TemplateObject");
        objectProfile.setState("I");

        return objectProfile;
    }

    private ObjectProfile createTemplateRadioTVFileObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid("doms:Template_RadioTVFile");
        objectProfile.setType("TemplateObject");
        objectProfile.setState("I");

        return objectProfile;
    }

    private ObjectProfile createContentModelRadioTVFileObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid("doms:ContentModel_RadioTVFile");
        objectProfile.setType("ContentModel");
        objectProfile.setState("A");

        return objectProfile;
    }

    private ObjectProfile createContentModelProgramObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid("doms:ContentModel_Program");
        objectProfile.setType("ContentModel");
        objectProfile.setState("A");

        Relation relation = new Relation();
        relation.setSubject("info:fedora/" + "doms:ContentModel_Program");
        relation.setPredicate(DOMS_RELATIONS_NAMESPACE + "isEntryForViewAngle");
        relation.setObject("GUI");
        relation.setLiteral(true);
        objectProfile.getRelations().add(relation);

        DatastreamProfile datastreamProfile = new DatastreamProfile();
        datastreamProfile.setId("ONTOLOGY");
        datastreamProfile.setInternal(true);
        Checksum checksum = new Checksum();
        checksum.setType("DISABLED");
        datastreamProfile.setChecksum(checksum);
        objectProfile.getDatastreams().add(datastreamProfile);

        DatastreamProfile datastreamProfile2 = new DatastreamProfile();
        datastreamProfile2.setId("VIEW");
        datastreamProfile2.setInternal(true);
        Checksum checksum2 = new Checksum();
        checksum2.setType("DISABLED");
        datastreamProfile2.setChecksum(checksum2);
        objectProfile.getDatastreams().add(datastreamProfile2);

        return objectProfile;
    }

    private ObjectProfile createRadioTVCollectionObjectProfile() {
        ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.setPid("doms:RadioTV_Collection");
        objectProfile.setType("CollectionObject");
        objectProfile.setState("A");

        return objectProfile;
    }

    private static Relation isThisRelation(String subject, String predicate, String object, boolean literal) {
        return argThat(new IsThisRelation(subject, predicate, object, literal));
    }

    private static class IsThisRelation extends ArgumentMatcher<Relation> {
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
            Relation that = (Relation) argument;
            return (clean(subject).equals(clean(that.getSubject()))
                    && predicate.equals(that.getPredicate())
                    && clean(object).equals(clean(that.getObject()))
                    && literal == that.isLiteral());
        }

        private String clean(String s) {
            return s.replaceAll("^info:fedora/", "");
        }

    }
}
