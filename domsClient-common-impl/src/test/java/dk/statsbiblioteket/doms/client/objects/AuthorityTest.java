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
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * Tests that sample authority files can be correctly handled by the GUI back end.
 */
@RunWith(value = Parameterized.class)
public class AuthorityTest {

    private String authorityModsFilepath;

    public AuthorityTest(String authorityModsFilepath) {
        this.authorityModsFilepath = authorityModsFilepath;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { {"authority/aktuelt1-socialisten.xml"},
                {"authority/aktuelt2-social-demokraten.xml"},
                {"authority/aktuelt3-aktuelt.xml"},
                {"authority/aktuelt4-Lands-avisen Aktuelt.xml"},
                {"authority/aktuelt5-Aktuelt.xml"},
                {"authority/aktuelt6-det fri Aktuelt.xml"},
                {"authority/aktuelt7-Aktuelt.xml"},
                {"authority/simple/aktuelt1-socialisten.xml"}
        };
        return Arrays.asList(data);
    }

    /**
     * Test that each of the sample template files can read and written by the DOMS GUI backend without
     * any resulting semantically meaningful changes in the content.
     * @throws IOException
     * @throws XMLParseException
     * @throws ServerOperationFailed
     * @throws SAXException
     */
    @Test
    public void test1() throws IOException, XMLParseException, ServerOperationFailed, SAXException {
        final String schemaString = SdoUtils.getStringFromFileOnClasspath("MODS.xsd");
        final String modsFilePath = authorityModsFilepath;
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
        String message = "SDO-DOC:\n" + SdoUtils.parseDoc(sdodoc) + "Original Content:\n" + modsDatastreamContent + "Final Content:\n" + xmlFinal;
        assertTrue(message,  diff.similar());
        System.out.println(message);

    }



}
