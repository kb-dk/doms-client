package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.relations.RelationDeclaration;
import dk.statsbiblioteket.doms.client.relations.RelationModel;
import dk.statsbiblioteket.doms.client.utils.Constants;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectionTest extends TestBase {

    public CollectionTest() throws MalformedURLException {
        super();
    }

    @Test
    public void testCollection1() throws ServerOperationFailed, XMLParseException, NotFoundException {
        boolean createdProgram = false;
        boolean createdShard = false;
        boolean createdRelation = false;
        boolean saved = false;

        DigitalObject newProgram = null;
        DigitalObject shard = null;

        DigitalObject object = factory.getDigitalObject("doms:RadioTV_Collection");
        if (object instanceof CollectionObject) {
            CollectionObject collectionObject = (CollectionObject) object;
            Set<TemplateObject> entryTemplates = collectionObject.getEntryTemplates("GUI");
            for (TemplateObject entryTemplate : entryTemplates) {
                if (entryTemplate.getPid().equals("doms:Template_Program")) {

                    newProgram = entryTemplate.clone();
                    createdProgram = true;
                    for (ContentModelObject contentModelObject : newProgram.getType()) {
                        RelationModel relModel = contentModelObject.getRelationModel();
                        for (RelationDeclaration relationDeclaration : relModel.getRelationDeclarations()) {
                            if (relationDeclaration.getViewAngles().contains("GUI")) {
                                Set<ContentModelObject> firstLevelObjects = relationDeclaration.getFirstLevelModels();
                                for (ContentModelObject firstLevelObject : firstLevelObjects) {
                                    Set<TemplateObject> templateDeep = firstLevelObject.getTemplates();
                                    if (templateDeep.size() > 0) {

                                        shard = templateDeep.iterator().next().clone();
                                        createdShard = true;
                                        newProgram.addObjectRelation(relationDeclaration.getPredicate(), shard);
                                        createdRelation = true;
                                        newProgram.save("GUI");
                                        saved = true;
                                    }
                                }
                                break;
                            }
                        }
                    }

                }
            }
        } else {

            fail();
        }
        if (newProgram != null) {
            newProgram.setState(Constants.FedoraState.Deleted);
            newProgram.save();
        }
        if (shard != null) {
            shard.setState(Constants.FedoraState.Deleted);
            shard.save();
        }
        assertTrue(createdProgram);
        assertTrue(createdShard);
        assertTrue(createdRelation);
        assertTrue(saved);
    }
}
