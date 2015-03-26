package dk.statsbiblioteket.doms.client.objects;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.Checksum;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test working with datastreams.
 */
public class DatastreamTest {

    private CentralWebservice centralWebservice;
    private DigitalObjectFactory factory;

    public DatastreamTest() {
    }

    @Before
    public void setUp() throws Exception {
        centralWebservice = mock(CentralWebservice.class);
        when(centralWebservice.getObjectProfile("doms:ContentModel_Program"))
                .thenReturn(createContentModelProgramObjectProfile());
        when(centralWebservice.getObjectProfile("doms:Template_Program"))
                .thenReturn(createTemplateProgramObjectProfile());

        when(centralWebservice.getDatastreamContents("doms:ContentModel_Program", "DS-COMPOSITE-MODEL"))
                .thenReturn(createDSCompositeModel());
        when(centralWebservice.getDatastreamContents("doms:ContentModel_Program", "ACCESS_SCHEMA"))
                .thenReturn(createAccessSchema());
        when(centralWebservice.getDatastreamContents("doms:Template_Program", "ACCESS"))
                .thenReturn(createAccess());

        factory = new DigitalObjectFactoryImpl(centralWebservice);
    }

    /**
     * Test getting the datastream model.
     */
    @Test
    public void testDatastreamModel() throws Exception {
        //Call method
        ContentModelObject cmProgram = (ContentModelObject) factory.getDigitalObject("doms:ContentModel_Program");
        DatastreamModel dsModel = cmProgram.getDsModel();
        List<DatastreamDeclaration> datastreamDeclarations = dsModel.getDatastreamDeclarations();

        //Assert calls
        //Should read the content model profile
        verify(centralWebservice).getObjectProfile("doms:ContentModel_Program");
        //Should read the content model's datastream model
        verify(centralWebservice).getDatastreamContents("doms:ContentModel_Program", "DS-COMPOSITE-MODEL");
        verifyNoMoreInteractions(centralWebservice);

        assertEquals("Should get right mime type", "text/xml", dsModel.getMimeType());
        assertNotNull("Should get right format URI", dsModel.getFormatURI());
        assertEquals("Should find 1 declared datastream", 1, datastreamDeclarations.size());
        DatastreamDeclaration dsDcl = datastreamDeclarations.get(0);
        assertEquals("Should find the right declared datastream", "ACCESS", dsDcl.getName());
    }

    /**
     * Test getting reading a datastream.
     */
    @Test
    public void testReadDatastreamDefinition() throws Exception {
        //Call method
        DigitalObject template = factory.getDigitalObject("doms:Template_Program");
        Set<DatastreamDeclaration> declarations = template.getDatastream("ACCESS").getDeclarations();

        //Assert calls
        //Should read the object profile
        verify(centralWebservice).getObjectProfile("doms:Template_Program");
        //Should read the object's content model's profile
        verify(centralWebservice).getObjectProfile("doms:ContentModel_Program");
        //Should read the object's content model's datastream model
        verify(centralWebservice).getDatastreamContents("doms:ContentModel_Program", "DS-COMPOSITE-MODEL");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should get the one declared datastream", 1, declarations.size());
        DatastreamDeclaration declaration = declarations.iterator().next();
        assertEquals(declaration.getName(), "ACCESS");
        assertTrue(declaration.getPresentation() == Constants.GuiRepresentation.editable);

        //Call method
        Datastream accessSchema = declaration.getSchema();
        String contents = accessSchema.getContents();

        //Assert calls
        //Should read the schema for the datastream
        verify(centralWebservice).getDatastreamContents("doms:ContentModel_Program", "ACCESS_SCHEMA");
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertNotNull(contents);
    }

    /**
     * Test saving changes to a datastream.
     */
    @Test
    public void testSaveDatastream() throws Exception {
        //Call method
        String destroyed = "doms:Template_Program";
        DigitalObject object = factory.getDigitalObject(destroyed);
        Datastream datastream = object.getDatastream("ACCESS");
        SDOParsedXmlDocument doc = datastream.getSDOParsedDocument();

        //Verify expected calls
        //Should read the object profile
        verify(centralWebservice).getObjectProfile(destroyed);
        //Should read the object's content model's profile
        verify(centralWebservice).getObjectProfile("doms:ContentModel_Program");
        //Should read the object's content model's datastream model
        verify(centralWebservice).getDatastreamContents("doms:ContentModel_Program", "DS-COMPOSITE-MODEL");
        //Should read the object's content model's schema
        verify(centralWebservice).getDatastreamContents("doms:ContentModel_Program", "ACCESS_SCHEMA");
        //Should read the object's datastream
        verify(centralWebservice).getDatastreamContents(destroyed, "ACCESS");
        verifyNoMoreInteractions(centralWebservice);

        //Set the object inactive
        object.setState(Constants.FedoraState.Inactive);

        //Make a change to the document
        String originaldoc = doc.dumpToString();
        changeField(doc.getRootSDOParsedXmlElement(), "test", "testvalue");
        String unchangeddoc = doc.dumpToString();
        assertTrue(XMLUnit.compareXML(originaldoc, unchangeddoc).identical());
        changeField(doc.getRootSDOParsedXmlElement(), "Defekt", "test of change: " + Math.random());
        String changeddoc = doc.dumpToString();
        assertFalse(XMLUnit.compareXML(originaldoc, changeddoc).identical());

        //Save the document
        object.save();

        //Verify expected interactions
        //Set object inactive
        verify(centralWebservice).markInProgressObject(eq(Arrays.asList(destroyed)), anyString());
        //Modify datastream
        verify(centralWebservice).modifyDatastream(eq(destroyed), eq("ACCESS"), eq(changeddoc), anyString());
        verifyNoMoreInteractions(centralWebservice);

        //Try getting the datastream
        DigitalObject object2 = factory.getDigitalObject(destroyed);
        SDOParsedXmlDocument doc2 = object2.getDatastream("ACCESS").getSDOParsedDocument();
        String rereaddoc = doc2.dumpToString();

        //Should not reread it from DOMS
        verifyNoMoreInteractions(centralWebservice);

        //Should get the updated document
        assertTrue(XMLUnit.compareXML(changeddoc, rereaddoc).identical());
    }

    private void changeField(SDOParsedXmlElement doc, String field, String newvalue) {
        ArrayList<SDOParsedXmlElement> children = doc.getChildren();
        for (SDOParsedXmlElement child : children) {
            if (child.isLeaf()) {
                if (child.getLabel().equals(field)) {
                    child.setValue(newvalue);
                }
            } else {
                changeField(child, field, newvalue);
            }
        }
    }

    private ObjectProfile createContentModelProgramObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("doms:ContentModel_Program");
        profile.setState("A");
        profile.setType("ContentModel");

        DatastreamProfile datastreamProfile = new DatastreamProfile();
        datastreamProfile.setId("DS-COMPOSITE-MODEL");
        Checksum checksum = new Checksum();
        checksum.setType("DISABLED");
        datastreamProfile.setChecksum(checksum);
        datastreamProfile.setMimeType("text/xml");
        datastreamProfile.setFormatUri("info:fedora/fedora-system:FedoraDSCompositeModel-1.0");
        profile.getDatastreams().add(datastreamProfile);

        DatastreamProfile datastreamProfile2 = new DatastreamProfile();
        datastreamProfile2.setId("ACCESS_SCHEMA");
        Checksum checksum2 = new Checksum();
        checksum2.setType("DISABLED");
        datastreamProfile2.setChecksum(checksum2);
        profile.getDatastreams().add(datastreamProfile2);

        return profile;
    }

    private ObjectProfile createTemplateProgramObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setPid("doms:Template_Program");
        profile.setState("A");
        profile.setTitle("");
        profile.setType("TemplateObject");

        profile.getContentmodels().add("doms:ContentModel_Program");

        DatastreamProfile datastreamProfile = new DatastreamProfile();
        datastreamProfile.setId("ACCESS");
        Checksum checksum = new Checksum();
        checksum.setType("DISABLED");
        datastreamProfile.setChecksum(checksum);
        datastreamProfile.setInternal(true);
        profile.getDatastreams().add(datastreamProfile);

        return profile;
    }

    private String createDSCompositeModel() {
        return "<dsCompositeModel\n"
                + "        xmlns=\"info:fedora/fedora-system:def/dsCompositeModel#\">\n"
                + "    <dsTypeModel ID=\"ACCESS\">\n"
                + "        <form MIME=\"text/xml\"/>\n"
                + "        <extension name=\"GUI\">\n"
                + "            <presentAs type=\"editable\"/>\n"
                + "        </extension>\n"
                + "        <extension name=\"SCHEMA\">\n"
                + "            <reference type=\"datastream\" value=\"ACCESS_SCHEMA\"/>\n"
                + "        </extension>\n"
                + "    </dsTypeModel>\n"
                + "</dsCompositeModel>\n"
                + "\n";
    }

    private String createAccessSchema() {
        return "<xs:schema xmlns:tns=\"http://doms.statsbiblioteket.dk/types/access/0/1/#\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
                + "  version=\"1.0\" targetNamespace=\"http://doms.statsbiblioteket.dk/types/access/0/1/#\"\n"
                + "  attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\">\n"
                + "  <xs:element name=\"access\" type=\"tns:access\"/>\n" 
                + "\n"
                + "  <xs:complexType name=\"access\">\n" 
                + "    <xs:sequence>\n"
                + "      <xs:element name=\"individuelt_forbud\" type=\"tns:yesNoType\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
                + "      <xs:element name=\"klausuleret\" type=\"tns:yesNoType\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
                + "      <xs:element name=\"defekt\" type=\"tns:yesNoType\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
                + "      <xs:element name=\"kommentarer\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"1\"/>\n"
                + "    </xs:sequence>\n" 
                + "  </xs:complexType>\n"
                + "\n"
                + "  <xs:simpleType name=\"yesNoType\">\n"
                + "      <xs:restriction base=\"xs:string\">\n" 
                + "          <xs:enumeration value=\"Nej\"/>\n"
                + "          <xs:enumeration value=\"Ja\"/>\n" 
                + "      </xs:restriction>\n"
                + "  </xs:simpleType>\n"
                + "</xs:schema>\n";
    }

    private String createAccess() {
        return "<access xmlns=\"http://doms.statsbiblioteket.dk/types/access/0/1/#\">"
                + "</access>";
    }
}
