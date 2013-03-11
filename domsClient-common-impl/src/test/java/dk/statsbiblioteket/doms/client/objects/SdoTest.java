package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/24/11
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SdoTest extends TestBase{


    public SdoTest() throws MalformedURLException {
        super();
    }

    @Test
    public void testSdoPBCore()
            throws ServerOperationFailed, NotFoundException, IOException,  XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        SDOParsedXmlDocument doc = program.getDatastream("PBCORE").getSDOParsedDocument();

        parseDoc(doc);

    }

    @Test
     public void testDatastreamAdd() throws Exception {
         DigitalObject program = factory.getDigitalObject(victimProgram);
         Datastream testStream = program.addInternalDatastream("ANNOTATIONS");
         parseDoc(testStream.getSDOParsedDocument());
        System.out.println(testStream.getSDOParsedDocument().dumpToString());
     }


    @Test
    public void testSdoDC()
            throws ServerOperationFailed, NotFoundException, IOException,  XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);

        SDOParsedXmlDocument doc = program.getDatastream("DC").getSDOParsedDocument();

        parseDoc(doc);


    }

    @Test
    @Ignore("we do not have shards anymore")
    public void testSdoSHARD()
            throws ServerOperationFailed, NotFoundException, IOException,  XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimShard);

        SDOParsedXmlDocument doc = program.getDatastream("SHARD_METADATA").getSDOParsedDocument();

        parseDoc(doc);


    }


    @Test
    public void testSdoRitzau()
            throws ServerOperationFailed, NotFoundException, IOException,  XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        try {
            SDOParsedXmlDocument doc = program.getDatastream("RITZAU_ORIGINAL").getSDOParsedDocument();
            // The ritzau schema is not serializable to SDO and expected to throw an exception.
            fail();
        } catch (Exception e){

        }


    }

    @Test
    public void testSdoGallup()
            throws ServerOperationFailed, NotFoundException, IOException,  XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        SDOParsedXmlDocument doc = program.getDatastream("GALLUP_ORIGINAL").getSDOParsedDocument();
        parseDoc(doc);
    }



    @Test
    public void testSdoRelsExt()
            throws ServerOperationFailed, NotFoundException, IOException,  XMLParseException {
        DigitalObject program = factory.getDigitalObject(victimProgram);


        try {
            SDOParsedXmlDocument doc = program.getDatastream("RELS-EXT").getSDOParsedDocument();
            //Note: Changes in reflection causes different behaviour in Java 6/7 (NPE or Null document). Both are okay,
            //the RELS-EXT schema is not supported.
            assertNull(doc);
        } catch (RuntimeException e){
            //Expected. Current SDO implementation does not support the RELS-EXT schema.
        }


    }




}
