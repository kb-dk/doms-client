package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.relations.LiteralRelation;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.utils.Constants;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/28/11
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class RelationsTest extends TestBase {

    public RelationsTest() throws MalformedURLException {
        super();
    }

    @Test
    public void testViewRelations() throws Exception {
        DigitalObject cmdoms = factory.getDigitalObject("doms:ContentModel_Program");
        assertTrue(cmdoms instanceof ContentModelObject);
        if (cmdoms instanceof ContentModelObject) {
            ContentModelObject cmo = (ContentModelObject) cmdoms;
            assertNotNull(cmo.getRelationsWithViewAngle("SummaVisible"));
        }
    }

    @Test
    @Ignore //Will run for ever
    public void testInverseRelations() throws Exception {
        DigitalObject cmdoms = factory.getDigitalObject("doms:ContentModel_Program");
        assertTrue(cmdoms instanceof ContentModelObject);
        List<ObjectRelation> inverseRelations = cmdoms.getInverseRelations();
        for (ObjectRelation inverseRelation : inverseRelations) {
            assertEquals(inverseRelation.getObjectPid(), cmdoms.getPid());
            assertNotNull(inverseRelation.getSubject());
        }
    }

    @Test
    public void testAddedRelationLiteral() throws Exception {
        DigitalObject object = factory.getDigitalObject(victimProgram);

        object.setState(Constants.FedoraState.Inactive); //To open it up for changes
        object.save();

        LiteralRelation newRel = object.addLiteralRelation(
                "http://domclient.unittests/#testRelationPredicateLiteral",
                "literalValue");
        object.save();

        setUp();
        object = factory.getDigitalObject(victimProgram);
        boolean present = false;
        for (Relation relation : object.getRelations()) {
            if (relation.equals(newRel)) {
                present = true;
                newRel = (LiteralRelation) relation;
                break;
            }
        }
        Assert.assertTrue(present);
        newRel.remove();
        present = false;
        for (Relation relation : object.getRelations()) {
            if (relation.equals(newRel)) {
                present = true;
                fail("Relation should have been removed");
            }
        }
        object.save();

        setUp();
        object = factory.getDigitalObject(victimProgram);
        for (Relation relation : object.getRelations()) {
            if (relation.equals(newRel)) {
                present = true;
                fail("Relation should have been removed");
            }
        }
        object.setState(Constants.FedoraState.Active); //To open it up for changes
        object.save();


    }

    @Test
    public void testAddedRelationObject() throws Exception {
        DigitalObject object = factory.getDigitalObject(victimProgram);

        object.setState(Constants.FedoraState.Inactive); //To open it up for changes
        object.save();

        Relation newRel = object.addObjectRelation(
                "http://domclient.unittests/#testRelationPredicate",
                object.getType().get(0));
        object.save();

        setUp();
        object = factory.getDigitalObject(victimProgram);
        boolean present = false;
        for (Relation relation : object.getRelations()) {
            if (relation.equals(newRel)) {
                present = true;
                newRel = relation;
                break;
            }
        }
        Assert.assertTrue(present);
        newRel.remove();
        present = false;
        for (Relation relation : object.getRelations()) {
            if (relation.equals(newRel)) {
                present = true;
                fail("Relation should have been removed");
            }
        }
        object.save();

        setUp();
        object = factory.getDigitalObject(victimProgram);
        for (Relation relation : object.getRelations()) {
            if (relation.equals(newRel)) {
                present = true;
                fail("Relation should have been removed");
            }
        }


    }

}
