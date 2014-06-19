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
import dk.statsbiblioteket.util.Strings;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.IOException;

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
     * Tests that parsing works correctly when a fixed-value attribute is absent - it remains absent.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoFixedValueAbsent() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                       return SdoUtils.getStringFromFileOnClasspath("MODS35_SIMPLE.xsd");
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
        String xmlFinal = sdodoc.dumpToString();
        Document finalDocument = DOM.stringToDOM(xmlFinal, true);
        XPathSelector MODS_XPATH_SELECTOR = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");
        String invalidAttributeValue = MODS_XPATH_SELECTOR.selectString(finalDocument, "mods:modsDefinition/mods:part/mods:extent/@foobarFixedValueIsBar");
        assertEquals("", invalidAttributeValue);
        assertFalse(xmlFinal.contains("foobarFixedValueIsBar"));
    }

    /**
     * Tests that parsing works correctly when a fixed-value attribute is present
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoFixedValuePresent() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                        return SdoUtils.getStringFromFileOnClasspath("MODS35_SIMPLE.xsd");
                    }
                };
            }
        };
        ModsHelper modsHelper = new ModsHelper();
        modsHelper.setAdditionalAttributeString("foobarFixedValueIsBar=\"bar\"");
        final String modsDatastreamContent = modsHelper.getModsSimpleString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);
        String xmlFinal = sdodoc.dumpToString();
        Document finalDocument = DOM.stringToDOM(xmlFinal, true);
        XPathSelector MODS_XPATH_SELECTOR = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");
        String invalidAttributeValue = MODS_XPATH_SELECTOR.selectString(finalDocument, "mods:modsDefinition/mods:part/mods:extent/@foobarFixedValueIsBar");
        assertEquals("bar", invalidAttributeValue);
        assertTrue(xmlFinal.contains("foobarFixedValueIsBar"));
    }

    /**
     * Tests that parsing works correctly when a fixed-value attribute is present but empty.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoFixedValueEmpty() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                        return SdoUtils.getStringFromFileOnClasspath("MODS35_SIMPLE.xsd");
                    }
                };
            }
        };
        ModsHelper modsHelper = new ModsHelper();
        modsHelper.setAdditionalAttributeString("foobarFixedValueIsBar=\"\"");
        final String modsDatastreamContent = modsHelper.getModsSimpleString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);
        String xmlFinal = sdodoc.dumpToString();
        Document finalDocument = DOM.stringToDOM(xmlFinal, true);
        XPathSelector MODS_XPATH_SELECTOR = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");
        String invalidAttributeValue = MODS_XPATH_SELECTOR.selectString(finalDocument, "mods:modsDefinition/mods:part/mods:extent/@foobarFixedValueIsBar");
        assertEquals("", invalidAttributeValue);
        assertTrue(xmlFinal.contains("foobarFixedValueIsBar"));
    }

    /**
     * A leaf element with no attributes is presented as an input field. This example is based on the Title field
     * in Mods 3.1.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testLeafWithNoAttributes() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                        return SdoUtils.getStringFromFileOnClasspath("MODS31_SIMPLE.xsd");
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
        String doc = SdoUtils.parseDoc(sdodoc);
        assertTrue(doc.matches("(?s)^.*'Title':.*inputfield.*$"));
    }

    /**
     * A leaf element with no attributes is presented as an input field. This example is based on the Title field
     * in Mods 3.1. In this case there is no "Title" element in the input field so an empty one is created in the
     * sdo tree. This checks that it is created and has an input field where we can enter its value.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testNewLeafWithNoAttributes() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                        return SdoUtils.getStringFromFileOnClasspath("MODS31_SIMPLE.xsd");
                    }
                };
            }
        };
        final String modsDatastreamContent = Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream("MODS31_EVEN_SIMPLER.xml"));
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);
        String doc = SdoUtils.parseDoc(sdodoc);
        assertTrue(doc, doc.matches("(?s)^.*'Title':.*inputfield.*$"));
    }

    /**
     * Tests the case like the Title element in Mods 3.5 where the leaf element also has attributes so is represented
     * in the sdo tree with a set of sdo properties which include a 'Value' element. This also tests that a schema
     * with recursion does not lead to an infinite-depth tree / stack-overflow.
     * @throws XMLParseException
     * @throws ServerOperationFailed
     */
    @Test
    public void testLeafWithAttributes() throws XMLParseException, ServerOperationFailed {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                       return SdoUtils.getStringFromFileOnClasspath("MODS35_SIMPLE.xsd");
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
        String doc = SdoUtils.parseDoc(sdodoc);
        assertTrue(doc.matches("(?s).*'Title'[\\s+()]*\\n\\s*'Value'.*"));
    }



}
