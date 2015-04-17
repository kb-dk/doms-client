package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Integration test for SDO
 */
public class SdoIntegrationTest {
    public static final String victimProgram = "uuid:fafda919-cd27-4f7b-bc6d-cdedb95e85a7";
    public static  final String victimShard = "uuid:c171df65-9ffb-4011-9fae-4f6dccad9b9c";
    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/", "CentralWebserviceService");
    public DigitalObjectFactory factory;
    private URL domsWSAPIEndpoint;
    private String userName = "fedoraAdmin";
    private String password = "fedoraAdminPass";

    public SdoIntegrationTest() throws MalformedURLException {
        domsWSAPIEndpoint = new URL("http://alhena:7880/centralWebservice-service/central/");
    }

    @Before
    public void setUp() throws Exception {

        CentralWebservice domsAPI = new CentralWebserviceService(
                domsWSAPIEndpoint, CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
        factory = new DigitalObjectFactoryImpl(domsAPI);
    }

    @Test
    public void testSdoPBCore() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        SDOParsedXmlDocument doc = program.getDatastream("PBCORE").getSDOParsedDocument();

        SdoUtils.parseDoc(doc);

    }

    @Test
    public void testDatastreamAdd() throws Exception {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        Datastream testStream = program.addInternalDatastream("ANNOTATIONS");
        SdoUtils.parseDoc(testStream.getSDOParsedDocument());
        System.out.println(testStream.getSDOParsedDocument().dumpToString());
    }


    @Test
    public void testSdoDC() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);

        SDOParsedXmlDocument doc = program.getDatastream("DC").getSDOParsedDocument();

        SdoUtils.parseDoc(doc);


    }

    @Test
    @Ignore("we do not have shards anymore")
    public void testSdoSHARD() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimShard);

        SDOParsedXmlDocument doc = program.getDatastream("SHARD_METADATA").getSDOParsedDocument();

        SdoUtils.parseDoc(doc);


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
    @Ignore("We cannot parse Gallup schema with the current SDO logic")
    public void testSdoGallup() throws ServerOperationFailed, NotFoundException, IOException, XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        SDOParsedXmlDocument doc = program.getDatastream("GALLUP_ORIGINAL").getSDOParsedDocument();
        SdoUtils.parseDoc(doc);
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
