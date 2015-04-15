package dk.statsbiblioteket.doms.client.objects;

import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.Checksum;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.impl.datastreams.InternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.objects.DataObjectImpl;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.impl.ontology.OWLClass;
import dk.statsbiblioteket.doms.client.impl.ontology.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.impl.ontology.OWLRestriction;
import dk.statsbiblioteket.doms.client.impl.ontology.ParsedOwlOntology;
import dk.statsbiblioteket.doms.client.impl.ontology.ParsedOwlOntologyImpl;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.relations.RelationDeclaration;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.util.Strings;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test ontology operations.
 */
public class OntologyTest {

    private static final String COLLECTION_REL
            = "http://doms.statsbiblioteket.dk/relations/default/0/1/#isPartOfCollection";
    private DigitalObjectFactory factory;
    private CentralWebservice domsAPI;

    public OntologyTest() throws MalformedURLException {
        super();
    }

    @Before
    public void setUp() throws Exception {
        domsAPI = mock(CentralWebservice.class);
        when(domsAPI.getDatastreamContents("doms:ContentModel_DOMS", "ONTOLOGY")).thenReturn(Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("ONTOLOGY.xml")));
        when(domsAPI.getObjectProfile("uuid:XXX")).thenReturn(createObjectProfile());
        when(domsAPI.getObjectProfile("doms:ContentModel_DOMS")).thenReturn(createCMDOMSObjectProfile());
        when(domsAPI.getObjectProfile("doms:ContentModel_License")).thenReturn(createCMLicenseObjectProfile());
        when(domsAPI.getObjectProfile("doms:ContentModel_Collection")).thenReturn(createCMCollectionObjectProfile());
        when(domsAPI.getObjectProfile("fedora-system:ContentModel-3.0")).thenThrow(new InvalidResourceException("Not found", "Not found"));
        factory = new DigitalObjectFactoryImpl(domsAPI);
    }

    @Test
    public void testParseOntologyWithoutExceptions() throws Exception {
        //Parse ontology
        ParsedOwlOntology parsedOntology = new ParsedOwlOntologyImpl(createOntologyDatastream());
        //Ensure no exceptions are thrown and the result is not null
        assertNotNull(parsedOntology);
    }

    @Test
    public void testParseOntology() throws Exception {
        //Parse ontology
        ParsedOwlOntology parsedOntology = new ParsedOwlOntologyImpl(createOntologyDatastream());

        //Check result
        //Check it is an owl document about the right class.
        OWLClass owlclass = parsedOntology.getOwlClass();
        assertEquals("Should be an OWL document about the expected class",
                     "info:fedora/doms:ContentModel_DOMS#class", owlclass.getName());

        //Check it has a restriction on collections with expected cardinality and value restriction.
        List<OWLRestriction> restrictions = owlclass.getOwlRestrictions();
        String allValuesFrom = null;
        int minCardinality = -2;
        for (OWLRestriction restriction : restrictions) {
            if (restriction.getOnProperty().equals(COLLECTION_REL)) {
                if (restriction.getAllValuesFrom() != null) {
                    allValuesFrom = restriction.getAllValuesFrom();
                } else if (restriction.getMinCardinality() != -2) {
                    minCardinality = restriction.getMinCardinality();
                }
            }
        }
        assertEquals("Should have expected value restriction",
                     "info:fedora/doms:ContentModel_Collection#class", allValuesFrom);
        assertEquals("Should have expected cardinality",
                     minCardinality, 1);

        //Check that you can look up a specific relation.
        OWLObjectProperty collectionRelProp = parsedOntology.getOWLObjectProperty(COLLECTION_REL);
        assertNotNull(collectionRelProp);
    }

    @Test
    public void testParseOntologyFromDataObject() throws Exception {
        //Read an object
        DigitalObject object = factory.getDigitalObject("uuid:XXX");

        //Verify calls
        //Should read object and its content model
        verify(domsAPI).getObjectProfile("uuid:XXX");
        verify(domsAPI).getObjectProfile("doms:ContentModel_DOMS");
        verifyNoMoreInteractions(domsAPI);

        //Find the collection relation
        List<Relation> relations = object.getRelations();
        Relation collectionRel = null;
        for (Relation relation : relations) {
            if (relation.getPredicate().equals(COLLECTION_REL)) {
                collectionRel = relation;
            }
        }

        //Should not call DOMS
        verifyNoMoreInteractions(domsAPI);

        //Get the OWL restrictions on said relation
        Set<RelationDeclaration> restrictions = collectionRel.getDeclarations();

        //Verify calls
        //Should read the relation and all directly referred content models
        verify(domsAPI).getDatastreamContents("doms:ContentModel_DOMS", "ONTOLOGY");
        verify(domsAPI).getObjectProfile("doms:ContentModel_License");
        verify(domsAPI).getObjectProfile("doms:ContentModel_Collection");
        verify(domsAPI).getObjectProfile("fedora-system:ContentModel-3.0");
        verifyNoMoreInteractions(domsAPI);
        assertNotNull(collectionRel);

        //Check that the restrictions are the expected cardinality and value restrictions
        String allValuesFrom = null;
        int minCardinality = -2;
        for (RelationDeclaration restriction : restrictions) {
            if (restriction.getPredicate().equals(collectionRel.getPredicate())) {
                if (!restriction.getFirstLevelModels().isEmpty()) {
                    allValuesFrom = restriction.getFirstLevelModels().iterator().next().toString();
                }
                if (restriction.getMinCardinality() != -2) {
                    minCardinality = restriction.getMinCardinality();
                }
            }
        }

        assertEquals("doms:ContentModel_Collection", allValuesFrom);
        assertEquals(1, minCardinality);

        //Should not call doms
        verifyNoMoreInteractions(domsAPI);
    }

    private Datastream createOntologyDatastream() throws Exception {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("doms:ContentModel_DOMS");
        profile.setState("A");
        DigitalObject digitalObject = new DataObjectImpl(profile, domsAPI, factory);
        DatastreamProfile datastreamProfile = new DatastreamProfile();
        Checksum checksum = new Checksum();
        checksum.setType("DISABLED");
        datastreamProfile.setChecksum(checksum);
        datastreamProfile.setId("ONTOLOGY");
        Datastream datastream = new InternalDatastreamImpl(datastreamProfile, digitalObject, domsAPI, false);
        return datastream;
    }

    private ObjectProfile createObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("uuid:XXX");
        profile.setState("A");
        dk.statsbiblioteket.doms.central.Relation relation = new dk.statsbiblioteket.doms.central.Relation();
        relation.setSubject("info:fedora/uuid:XXX");
        relation.setPredicate(COLLECTION_REL);
        relation.setObject("info:fedora/doms:BaseCollection");
        relation.setLiteral(false);
        profile.getRelations().add(relation);
        profile.getContentmodels().add("doms:ContentModel_DOMS");
        return profile;
    }


    private ObjectProfile createCMDOMSObjectProfile() {
        ObjectProfile profile = createCMObjectProfile("doms:ContentModel_DOMS");
        DatastreamProfile datastreamProfile = new DatastreamProfile();
        Checksum checksum = new Checksum();
        checksum.setType("DISABLED");
        datastreamProfile.setChecksum(checksum);
        datastreamProfile.setId("ONTOLOGY");
        profile.getDatastreams().add(datastreamProfile);
        return profile;
    }

    private ObjectProfile createCMCollectionObjectProfile() {
        return createCMObjectProfile("doms:ContentModel_Collection");
    }

    private ObjectProfile createCMLicenseObjectProfile() {
        return createCMObjectProfile("doms:ContentModel_License");
    }

    private ObjectProfile createCMObjectProfile(String pid) {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid(pid);
        profile.setState("A");
        profile.setType("ContentModel");
        return profile;
    }
}
