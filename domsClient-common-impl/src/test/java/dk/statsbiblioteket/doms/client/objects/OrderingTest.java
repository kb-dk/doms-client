package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.impl.sdo.SDOParsedXmlDocumentImpl;
import dk.statsbiblioteket.doms.client.objects.stubs.DatastreamDeclarationStub;
import dk.statsbiblioteket.doms.client.objects.stubs.DatastreamStub;
import dk.statsbiblioteket.util.Strings;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Tests that the ordering of elements presented in the GUI is the same as that in the input file.
 */
public class OrderingTest {

    /**
     * Tests a simple schema reordering where elements <el5></el5><el6></el6><el5></el5> in the original should be presented in the
     * GUI in that order.
     * @throws dk.statsbiblioteket.doms.client.exceptions.XMLParseException
     * @throws dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     */
    @Test
    public void testSimpleSchemaOrdering() throws XMLParseException, ServerOperationFailed, IOException, SAXException {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                        try {
                            return Strings.flush(Thread.currentThread().getContextClassLoader()
                                    .getResourceAsStream("ordering/simple_schema.xsd"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            }
        };
        final String modsDatastreamContent = Strings.flush(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("ordering/test2.xml"));

        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);

        Validator validator = new Validator(modsDatastreamContent);
        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(new ByteArrayInputStream(modsSchemaDatastreamDeclaration.getSchema().getContents().getBytes()));
        assertTrue(validator.toString(),validator.isValid());


        String sdodocString = SdoUtils.parseDoc(sdodoc);
        assertTrue(modsDatastreamContent + "\n" + sdodocString, sdodocString.matches("(?s)^.*El1.*El2.*El3.*El4.*El5.*El6.*El5.*"));
        String xmlFinal = sdodoc.dumpToString();
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        assertTrue(modsDatastreamContent + "\n" + xmlFinal, diff.similar());
    }

    /**
     * Test that the reordering algorithm works when there are identical elements (same name and content) in the
     * sequence.
     * @throws XMLParseException
     * @throws ServerOperationFailed
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public void testReorderingWithIdenticalElements() throws XMLParseException, ServerOperationFailed, IOException, SAXException {
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                        try {
                            return Strings.flush(Thread.currentThread().getContextClassLoader()
                                    .getResourceAsStream("ordering/simple_schema.xsd"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            }
        };
        final String modsDatastreamContent = Strings.flush(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("ordering/test3.xml"));

        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);

        Validator validator = new Validator(modsDatastreamContent);
        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(new ByteArrayInputStream(modsSchemaDatastreamDeclaration.getSchema().getContents().getBytes()));
        assertTrue(validator.toString(),validator.isValid());


        String sdodocString = SdoUtils.parseDoc(sdodoc);
        assertTrue(modsDatastreamContent + "\n" + sdodocString, sdodocString.matches("(?s)^.*El1.*El2.*El3.*El4.*El5.*El6.*El5.*El6.*"));
        String xmlFinal = sdodoc.dumpToString();
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        assertTrue(modsDatastreamContent + "\n" + xmlFinal, diff.similar());
    }

    /**
     * Test that a realistic xml-mods document is presented in the GUI with the same ordering of elements as
     * in the original document.
     * @throws Exception
     */
    @Test
    public void testRealisticMods() throws Exception {
        final String schemaString = SdoUtils.getStringFromFileOnClasspath("MODS.xsd");
        final String modsFilePath = "authority/aktuelt1-socialisten.xml";
        final DatastreamDeclaration modsSchemaDatastreamDeclaration = new DatastreamDeclarationStub() {
            public Datastream getSchema() {
                return new DatastreamStub() {
                    @Override
                    public String getContents() throws ServerOperationFailed {
                        return schemaString;
                    }
                };
            }
        };
        final String modsDatastreamContent = Strings.flush(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(modsFilePath));

        final Datastream modsDatastream = new DatastreamStub() {
            @Override
            public String getContents() throws ServerOperationFailed {
                return modsDatastreamContent;
            }
        };
        Validator validator = new Validator(modsDatastreamContent);
        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(new ByteArrayInputStream(schemaString.getBytes()));
        assertTrue(validator.toString(),validator.isValid());
        SDOParsedXmlDocumentImpl sdodoc = new SDOParsedXmlDocumentImpl(
                modsSchemaDatastreamDeclaration, modsDatastream);
        String xmlFinal = sdodoc.dumpToString();
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        Diff diff = XMLUnit.compareXML(modsDatastreamContent, xmlFinal);
        String parseDoc = SdoUtils.parseDoc(sdodoc);
        String message = "SDO-DOC:\n" + parseDoc + "Original Content:\n" + modsDatastreamContent + "Final Content:\n" + xmlFinal;
        assertTrue(message,  diff.similar());
        String matchingPatternString = "(?s)^.*mods.*titleinfo.*titleinfo.*origininfo" +
                ".*place.*placeterm.*dateissued.*dateissued.*issuance" +
                ".*abstract.*note.*genre.*typeofresource" +
                ".*identifier.*identifier.*physicaldescription" +
                ".*accesscondition.*accesscondition.*";
        assertTrue(message, parseDoc.toLowerCase().matches(matchingPatternString));

    }


}
