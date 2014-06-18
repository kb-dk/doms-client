package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.impl.sdo.SDOParsedXmlDocumentImpl;
import dk.statsbiblioteket.doms.client.objects.stubs.DatastreamDeclarationStub;
import dk.statsbiblioteket.doms.client.objects.stubs.DatastreamStub;
import dk.statsbiblioteket.doms.client.objects.stubs.ModsHelper;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.util.Strings;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XMLUtil;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/24/11
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SdoTest extends TestBase {


    public SdoTest() throws MalformedURLException {
        super();
    }

    @Test
    public void testSdoPBCore() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        SDOParsedXmlDocument doc = program.getDatastream("PBCORE").getSDOParsedDocument();

        parseDoc(doc);

    }

    /**
     * Tests that parsing works correctly when the "INVALID" attribute is absent - ie it remains absent.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoMods() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {

        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {

                    @Override
                    public String getContents() throws ServerOperationFailed {
                        try {
                            return Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("MODS.xsd"));
                        } catch (IOException e) {
                            fail(e.getMessage());
                            return null;
                        }
                    }
                };
            }
        };

        ModsHelper modsHelper = new ModsHelper();
        modsHelper.setInvalidAttributeString("");
        final String modsDatastreamContent = modsHelper.getModsString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };

        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);

        String xmlOriginal = modsDatastream.getContents();
        String xmlFinal = sdodoc.dumpToString();

        Document finalDocument = DOM.stringToDOM(xmlFinal, true);
        XPathSelector MODS_XPATH_SELECTOR = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");

        //Unfortunately this util code cannot distinguish between the case of an absent attribute and an
        //empty string so an extra assert is needed aftwerwards
        String invalidAttributeValue = MODS_XPATH_SELECTOR.selectString(finalDocument, "mods:modsDefinition/mods:relatedItem/mods:identifier[@type='reel number']/@invalid");
        assertEquals("", invalidAttributeValue);
        assertFalse(xmlFinal.contains("invalid"));
    }

    /**
     * Tests that parsing works correctly when the "INVALID" attribute is present but empty. (This is not actually
     * consistent with the schema, but we aren't worried about that here.)
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoModsEmptyAttribute() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {

        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {

                    @Override
                    public String getContents() throws ServerOperationFailed {
                        try {
                            return Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("MODS.xsd"));
                        } catch (IOException e) {
                            fail(e.getMessage());
                            return null;
                        }
                    }
                };
            }
        };

        ModsHelper modsHelper = new ModsHelper();
        modsHelper.setInvalidAttributeString("invalid=\"\"");
        final String modsDatastreamContent = modsHelper.getModsString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };

        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);

        String xmlOriginal = modsDatastream.getContents();
        String xmlFinal = sdodoc.dumpToString();

        Document finalDocument = DOM.stringToDOM(xmlFinal, true);
        XPathSelector MODS_XPATH_SELECTOR = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");

        //Unfortunately this util code cannot distinguish between the case of an absent attribute and an
        //empty string so an extra assert is needed aftwerwards
        String invalidAttributeValue = MODS_XPATH_SELECTOR.selectString(finalDocument, "mods:modsDefinition/mods:relatedItem/mods:identifier[@type='reel number']/@invalid");
        assertEquals("", invalidAttributeValue);
        assertTrue(xmlFinal.contains("invalid"));
    }

    /**
     * Tests that parsing works correctly when the "inavlid" attribute is present and non-empty.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoModsWithAttribute() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {

        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {

                    @Override
                    public String getContents() throws ServerOperationFailed {
                        try {
                            return Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("MODS.xsd"));
                        } catch (IOException e) {
                            fail(e.getMessage());
                            return null;
                        }
                    }
                };
            }
        };

        ModsHelper modsHelper = new ModsHelper();
        modsHelper.setInvalidAttributeString("invalid=\"yes\"");
        final String modsDatastreamContent = modsHelper.getModsString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };

        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);

        String xmlOriginal = modsDatastream.getContents();
        String xmlFinal = sdodoc.dumpToString();

        Document finalDocument = DOM.stringToDOM(xmlFinal, true);
        XPathSelector MODS_XPATH_SELECTOR = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");

        String invalidAttributeValue = MODS_XPATH_SELECTOR.selectString(finalDocument, "mods:modsDefinition/mods:relatedItem/mods:identifier[@type='reel number']/@invalid");
        assertEquals("yes", invalidAttributeValue);
    }



    @Test
    public void testDatastreamAdd() throws Exception {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        Datastream testStream = program.addInternalDatastream("ANNOTATIONS");
        parseDoc(testStream.getSDOParsedDocument());
        System.out.println(testStream.getSDOParsedDocument().dumpToString());
    }


    @Test
    public void testSdoDC() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);

        SDOParsedXmlDocument doc = program.getDatastream("DC").getSDOParsedDocument();

        parseDoc(doc);


    }

    @Test
    @Ignore("we do not have shards anymore")
    public void testSdoSHARD() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimShard);

        SDOParsedXmlDocument doc = program.getDatastream("SHARD_METADATA").getSDOParsedDocument();

        parseDoc(doc);


    }


    @Test
    public void testSdoRitzau() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        try {
            SDOParsedXmlDocument doc = program.getDatastream("RITZAU_ORIGINAL").getSDOParsedDocument();
            // The ritzau schema is not serializable to SDO and expected to throw an exception.
            fail();
        } catch (Exception e) {

        }


    }

    @Test
    @Ignore("Fails for unenriched programs, please fix ASAP")
    public void testSdoGallup() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        SDOParsedXmlDocument doc = program.getDatastream("GALLUP_ORIGINAL").getSDOParsedDocument();
        parseDoc(doc);
    }


    @Test
    public void testSdoRelsExt() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);


        try {
            SDOParsedXmlDocument doc = program.getDatastream("RELS-EXT").getSDOParsedDocument();
            //Note: Changes in reflection causes different behaviour in Java 6/7 (NPE or Null document). Both are okay,
            //the RELS-EXT schema is not supported.
            assertNull(doc);
        } catch (RuntimeException e) {
            //Expected. Current SDO implementation does not support the RELS-EXT schema.
        }


    }


}
