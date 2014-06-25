package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.impl.sdo.SDOParsedXmlDocumentImpl;
import dk.statsbiblioteket.doms.client.objects.stubs.DatastreamDeclarationStub;
import dk.statsbiblioteket.doms.client.objects.stubs.DatastreamStub;
import dk.statsbiblioteket.doms.client.objects.stubs.ModsTestHelper;
import dk.statsbiblioteket.util.Strings;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class SdoTest  {

    private Log log = LogFactory.getLog(SdoTest.class);


    /**
     * Tests that parsing works correctly when a fixed-value attribute is absent - it remains absent.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoFixedValueAbsent() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException, SAXException {
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
        ModsTestHelper modsTestHelper = new ModsTestHelper();
        final String modsDatastreamContent = modsTestHelper.getModsSimpleString();
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

        final String sdoDocString = SdoUtils.parseDoc(sdodoc);
        String output = sdoDocString + "\n" + xmlFinal;
        assertEquals(output, "", invalidAttributeValue);
        assertTrue(output, sdoDocString.matches("(?s)^.*'Title'.*adresseavisen.*inputfield.*$"));
        assertFalse(output, sdoDocString.matches("(?s)^.*'Supplied':\\s'yes'.*$"));
        assertFalse(output, xmlFinal.contains("foobarFixedValueIsBar"));
        assertFalse(output, xmlFinal.contains("foobarFixedValueIsFoo"));

       XMLUnit.setIgnoreWhitespace(true);
       Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
       assertTrue(sdoDocString + "\n" + modsDatastreamContent + "\n" + xmlFinal,  diff.similar());
       log.info(output);
    }

    /**
     * Tests that parsing works correctly when a fixed-value attribute is present
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoFixedValuePresent() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException, SAXException {
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
        ModsTestHelper modsTestHelper = new ModsTestHelper();
        modsTestHelper.setAdditionalAttributeString("foobarFixedValueIsBar=\"bar\"");
        final String modsDatastreamContent = modsTestHelper.getModsSimpleString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);
        String xmlFinal = sdodoc.dumpToString();
        String sdodocString = SdoUtils.parseDoc(sdodoc);
        Document finalDocument = DOM.stringToDOM(xmlFinal, true);
        XPathSelector MODS_XPATH_SELECTOR = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");
        String invalidAttributeValue = MODS_XPATH_SELECTOR.selectString(finalDocument, "mods:mods/mods:part/mods:extent/@foobarFixedValueIsBar");
        assertEquals(xmlFinal, "bar", invalidAttributeValue);
        assertTrue(xmlFinal, xmlFinal.contains("foobarFixedValueIsBar"));
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        assertTrue(sdodocString + "\n" + modsDatastreamContent + "\n" + xmlFinal, diff.similar());
        log.info(sdodocString + "\n" + xmlFinal);
    }

    /**
     * Tests that parsing works correctly when a fixed-value attribute is present but empty.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testSdoFixedValueEmpty() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException, SAXException {
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
        ModsTestHelper modsTestHelper = new ModsTestHelper();
        modsTestHelper.setAdditionalAttributeString("foobarFixedValueIsBar=\"\"");
        final String modsDatastreamContent = modsTestHelper.getModsSimpleString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);
        String xmlFinal = sdodoc.dumpToString();
        String sdodocString = SdoUtils.parseDoc(sdodoc);
        Document finalDocument = DOM.stringToDOM(xmlFinal, true);
        XPathSelector MODS_XPATH_SELECTOR = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");
        String invalidAttributeValue = MODS_XPATH_SELECTOR.selectString(finalDocument, "mods:modsDefinition/mods:part/mods:extent/@foobarFixedValueIsBar");
        assertEquals(xmlFinal, "", invalidAttributeValue);
        assertTrue(xmlFinal, xmlFinal.contains("foobarFixedValueIsBar"));
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        assertTrue(sdodocString + "\n" + modsDatastreamContent + "\n" + xmlFinal, diff.similar());
        log.info(sdodocString + "\n" + xmlFinal);
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
    public void testLeafWithNoAttributes() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException, SAXException {
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
        String xmlFinal = sdodoc.dumpToString();
        assertTrue(doc, doc.matches("(?s)^.*'Title':.*adresseavisen.*inputfield.*$"));
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        assertTrue(doc + "\n" + modsDatastreamContent + "\n" + xmlFinal,  diff.similar());
        log.info(doc + "\n" + xmlFinal);
    }

    /**
     * If the type of a lead cannot be determined then it must simply be omitted. This example is based on Mods 3.1
     * where the type of a Title element is not specified.
     * @throws ServerOperationFailed
     * @throws NotFoundException
     * @throws IOException
     * @throws XMLParseException
     */
    @Test
    public void testAbstractLeaf() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException, SAXException {
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
        String xmlFinal = sdodoc.dumpToString();
        assertFalse(doc, doc.matches("(?s)^.*'Title':.*inputfield.*$"));
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        assertTrue(doc + "\n" + modsDatastreamContent + "\n" + xmlFinal,  diff.similar());
        log.info(doc + "\n" + xmlFinal);
    }

    /**
     * Tests the case like the Title element in Mods 3.5 where the leaf element also has attributes so is represented
     * in the sdo tree with a set of sdo properties which include a 'Value' element. This also tests that a schema
     * with recursion does not lead to an infinite-depth tree / stack-overflow.
     * @throws XMLParseException
     * @throws ServerOperationFailed
     */
    @Test
    public void testLeafWithAttributes() throws XMLParseException, ServerOperationFailed, IOException, SAXException {
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
        ModsTestHelper modsTestHelper = new ModsTestHelper();
        final String modsDatastreamContent = modsTestHelper.getModsSimpleString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);
        String doc = SdoUtils.parseDoc(sdodoc);
        String xmlFinal = sdodoc.dumpToString();
        assertTrue(doc, doc.matches("(?s).*'Title'[\\s+()]*\\n\\s*'Value'.*"));
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        assertTrue(doc + "\n" + modsDatastreamContent + "\n" + xmlFinal,  diff.similar());
        log.info(doc + "\n" + xmlFinal);
    }

    /**
     * Test with the real MODS 3.5 schema and a realistic metadata document. The test simply confirms that the XML
     * returned by the sdo processing is equivalent to that put in.
     * @throws XMLParseException
     * @throws ServerOperationFailed
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public void testRealisticSchema() throws XMLParseException, ServerOperationFailed, IOException, SAXException {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                       return SdoUtils.getStringFromFileOnClasspath("MODS.xsd");
                    }
                };
            }
        };
        ModsTestHelper modsTestHelper = new ModsTestHelper();
        final String modsDatastreamContent = modsTestHelper.getModsString();
        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);
        String xmlFinal = sdodoc.dumpToString();
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        assertTrue(SdoUtils.parseDoc(sdodoc) + "\n" + modsDatastreamContent + "\n" + xmlFinal,  diff.similar());
    }


}
