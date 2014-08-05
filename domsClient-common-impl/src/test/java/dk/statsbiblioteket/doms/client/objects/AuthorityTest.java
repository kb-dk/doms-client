package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.impl.sdo.SDOParsedXmlDocumentImpl;
import dk.statsbiblioteket.doms.client.objects.stubs.DatastreamDeclarationStub;
import dk.statsbiblioteket.doms.client.objects.stubs.DatastreamStub;
import dk.statsbiblioteket.doms.client.objects.stubs.ModsTestHelper;
import dk.statsbiblioteket.util.Strings;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Tests that sample authority files can be correctly handled by the GUI back end.
 */
public class AuthorityTest {

    @Test
    public void test1() throws IOException, XMLParseException, ServerOperationFailed, SAXException {
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
        final String modsDatastreamContent = Strings.flush(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("authority/aktuelt1-socialisten.xml"));

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
        assertTrue("SDO-DOC:\n" + SdoUtils.parseDoc(sdodoc) + "Original Content:\n" + modsDatastreamContent + "Final Content:\n" + xmlFinal,  diff.similar());
    }

    @Test
    public void test1_simple() throws IOException, XMLParseException, ServerOperationFailed, SAXException {
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
        final String modsDatastreamContent = Strings.flush(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("authority/simple/aktuelt1-socialisten.xml"));

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
        assertTrue("SDO-DOC:\n" + SdoUtils.parseDoc(sdodoc) + "Original Content:\n" + modsDatastreamContent + "Final Content:\n" + xmlFinal,  diff.similar());
    }
}
