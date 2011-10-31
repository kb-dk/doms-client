package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.datastreams.InternalDatastream;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.util.xml.DOM;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/28/11
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatastreamTest extends TestBase{

    public DatastreamTest() throws MalformedURLException {
        super();
    }


    @Test
    public void testDatastreamModel() throws Exception {
        ContentModelObject cmProgram = (ContentModelObject)
                factory.getDigitalObject("doms:ContentModel_Program");
        assertTrue(cmProgram instanceof ContentModelObject);
        if (cmProgram instanceof ContentModelObject){
            DatastreamModel dsModel = cmProgram.getDsModel();
            assertTrue(dsModel.getDatastreamDeclarations().size() > 0);
            assertNotNull(dsModel.getMimeType());
            assertNotNull(dsModel.getFormatURI());
            DatastreamDeclaration dsDcl = dsModel.getDatastreamDeclarations().get(0);
        }
    }

    @Test
    public void testDatastreamModel2() throws Exception {
        DigitalObject template =
                factory.getDigitalObject("doms:Template_Program");
        Set<DatastreamDeclaration> declarations = template.getDatastream("PBCORE").getDeclarations();
        assertTrue(declarations.size() > 0);
        for (DatastreamDeclaration declaration : declarations) {
            assertEquals(declaration.getName(),"PBCORE");
            assertTrue(declaration.getPresentation() == Constants.GuiRepresentation.editable);
            Datastream pbcoreSchema = declaration.getSchema();
            if (pbcoreSchema != null){
                assertNotNull(pbcoreSchema.getContents());
            } else {
                fail();
            }
        }
    }


    private void changeField(SDOParsedXmlElement doc, String field, String newvalue){
        ArrayList<SDOParsedXmlElement> children = doc.getChildren();
        for (SDOParsedXmlElement child : children) {
            if (child.isLeaf()){
                if (child.getLabel().equals(field)){
                    child.setValue(newvalue);
                }
            } else {
                changeField(child, field, newvalue);
            }
        }
    }

    private void emptymize(SDOParsedXmlElement doc){
        ArrayList<SDOParsedXmlElement> children = doc.getChildren();
        for (SDOParsedXmlElement child : children) {
            if (child.isLeaf()){

                if (child.getValue() == null){
                    child.setValue("");
                }
            } else {
                emptymize(child);
            }
        }
    }

    @org.junit.Test
    @Ignore
    public void testSaveDatastream() throws Exception {


        String destroyed = "uuid:503d1582-0df8-4397-9e33-86c097111513";
        DigitalObject object = factory.getDigitalObject(destroyed);
        Datastream datastream = object.getDatastream("PBCORE");
        SDOParsedXmlDocument doc = datastream.getSDOParsedDocument();
        object.setState(Constants.FedoraState.Inactive);
        object.save();
        String originaldoc = doc.dumpToString();

        changeField(doc.getRootSDOParsedXmlElement(),"test", "testvalue");


        String unchangeddoc = doc.dumpToString();

        assertEquals(originaldoc,unchangeddoc);

        changeField(doc.getRootSDOParsedXmlElement(),"Subject", "test of change: "+Math.random());


        String changeddoc = doc.dumpToString();

        assertNotSame(originaldoc, unchangeddoc);


        if (datastream instanceof InternalDatastream) {
            InternalDatastream internalDatastream = (InternalDatastream) datastream;
            internalDatastream.replace(doc.dumpToString());
        }
        object.save();

        setUp();
        DigitalObject object2 = factory.getDigitalObject(destroyed);
        SDOParsedXmlDocument doc2 = object2.getDatastream("PBCORE").getSDOParsedDocument();
        String rereaddoc = doc2.dumpToString();


        //TODO make this comparison work, they are xml alike
        assertEquals(changeddoc, rereaddoc);

        object.setState(Constants.FedoraState.Active);

    }

}
