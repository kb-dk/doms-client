package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.ontology.*;
import dk.statsbiblioteket.doms.client.impl.ontology.OWLClass;
import dk.statsbiblioteket.doms.client.impl.ontology.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.impl.ontology.OWLRestriction;
import dk.statsbiblioteket.doms.client.impl.ontology.ParsedOwlOntology;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.relations.RelationDeclaration;
import dk.statsbiblioteket.doms.client.utils.Constants;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class OntologyTest extends TestBase{




    public OntologyTest() throws MalformedURLException {
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
    public void testParseOntologyWithoutExceptions() throws ServerOperationFailed, NotFoundException {
        DigitalObject cmdoms = factory.getDigitalObject("doms:ContentModel_DOMS");
        assertEquals(cmdoms.getState(), Constants.FedoraState.Active);
        assertTrue(cmdoms instanceof ContentModelObject);

        Datastream ontologyStream = cmdoms.getDatastream("ONTOLOGY");
        ParsedOwlOntology parsedOntology = new ParsedOwlOntologyImpl(ontologyStream);
    }

    @Test
    public void testParseOntology() throws ServerOperationFailed, NotFoundException {
        DigitalObject cmdoms = factory.getDigitalObject("doms:ContentModel_DOMS");
        assertEquals(cmdoms.getState(), Constants.FedoraState.Active);
        assertTrue(cmdoms instanceof ContentModelObject);

        Datastream ontologyStream = cmdoms.getDatastream("ONTOLOGY");
        ParsedOwlOntology parsedOntology = new ParsedOwlOntologyImpl(ontologyStream);
        OWLClass owlclass= parsedOntology.getOwlClass();
        assertEquals(owlclass.getName(),"info:fedora/doms:ContentModel_DOMS#class");
        List<OWLRestriction> restrictions = owlclass.getOwlRestrictions();
        String collectionRel = "http://doms.statsbiblioteket.dk/relations/default/0/1/#isPartOfCollection";
        String allValuesFrom = null;
        int minCardinality = -2;
        for (OWLRestriction restriction : restrictions) {
            if (restriction.getOnProperty().equals(collectionRel)){
                if (restriction.getAllValuesFrom() != null){
                    allValuesFrom = restriction.getAllValuesFrom();
                } else if (restriction.getMinCardinality() != -2){
                    minCardinality = restriction.getMinCardinality();
                }
            }
        }
        assertEquals(allValuesFrom,"info:fedora/doms:ContentModel_Collection#class");
        assertEquals(minCardinality,1);
        OWLObjectProperty collectionRelProp = parsedOntology.getOWLObjectProperty("http://doms.statsbiblioteket.dk/relations/default/0/1/#isPartOfCollection");
        assertNotNull(collectionRelProp);
    }


    @Test
    public void testParseOntologyFromDataObject() throws ServerOperationFailed, NotFoundException {
        DigitalObject programObject = factory.getDigitalObject(victimProgram);
        assertEquals(programObject.getState(), Constants.FedoraState.Active);
        assertTrue(programObject instanceof DataObject);
        List<Relation> relations = programObject.getRelations();
        Relation collectionRel = null;
        for (Relation relation : relations) {
            if (relation.getPredicate().equals("http://doms.statsbiblioteket.dk/relations/default/0/1/#isPartOfCollection")){
                collectionRel = relation;
            }
        }
        assertNotNull(collectionRel);

        Set<RelationDeclaration> restrictions = collectionRel.getDeclarations();


        String allValuesFrom = null;
        int minCardinality = -2;
        for (RelationDeclaration restriction : restrictions) {
            if (restriction.getPredicate().equals(collectionRel.getPredicate())){
                if (!restriction.getFirstLevelModels().isEmpty()){
                    allValuesFrom = restriction.getFirstLevelModels().iterator().next().toString();
                }
                if (restriction.getMinCardinality() != -2){
                    minCardinality = restriction.getMinCardinality();
                }
            }
        }
        assertEquals("doms:ContentModel_Collection",allValuesFrom);
        assertEquals(1,minCardinality);
    }


}
