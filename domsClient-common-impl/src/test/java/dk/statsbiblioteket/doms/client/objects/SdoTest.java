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
import dk.statsbiblioteket.util.Strings;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class SdoTest  {



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
        SdoUtils.parseDoc(sdodoc);
    }

    /**
     * Tests that parsing mods works correctly
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoMods31FromFile() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {

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

        final String modsDatastreamContent = Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("MODS31_SIMPLE.xml"));
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };

        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);

        SdoUtils.parseDoc(sdodoc);
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
        SdoUtils.parseDoc(sdodoc);
    }

    @Test
    public void testSimpleMods() throws XMLParseException, ServerOperationFailed {

        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {

                    @Override
                    public String getContents() throws ServerOperationFailed {
                        try {
                            return Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("MODS_SIMPLE.xsd"));
                        } catch (IOException e) {
                            fail(e.getMessage());
                            return null;
                        }
                    }
                };
            }
        };

        ModsHelper modsHelper = new ModsHelper();
        final String modsDatastreamContent = modsHelper.getModsSimpleString();
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

        SdoUtils.parseDoc(sdodoc);
        System.out.println(xmlFinal);
    }




}
