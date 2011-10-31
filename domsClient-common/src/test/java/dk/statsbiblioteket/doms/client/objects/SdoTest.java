package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import dk.statsbiblioteket.doms.client.exceptions.*;
import dk.statsbiblioteket.doms.client.exceptions.MyXMLReadException;
import dk.statsbiblioteket.doms.client.exceptions.MyXMLWriteException;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

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
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        SDOParsedXmlDocument doc = program.getDatastream("PBCORE").getSDOParsedDocument();

        parseDoc(doc);

    }

    @Test
    public void testSdoDC()
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject(victimProgram);

        SDOParsedXmlDocument doc = program.getDatastream("DC").getSDOParsedDocument();

        parseDoc(doc);


    }

    @Test
    public void testSdoSHARD()
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject(victimShard);

        SDOParsedXmlDocument doc = program.getDatastream("SHARD_METADATA").getSDOParsedDocument();

        parseDoc(doc);


    }


    @Test
    public void testSdoRitzau()
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        try {
            SDOParsedXmlDocument doc = program.getDatastream("RITZAU_ORIGINAL").getSDOParsedDocument();
            fail();
        } catch (Exception e){

        }


    }

    @Test
    public void testSdoGallup()
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject(victimProgram);
        try {
            SDOParsedXmlDocument doc = program.getDatastream("GALLUP_ORIGINAL").getSDOParsedDocument();
            fail();
        } catch (Exception e){

        }



    }



    @Test
    public void testSdoRelsExt()
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject(victimProgram);


        try {
            SDOParsedXmlDocument doc = program.getDatastream("RELS-EXT").getSDOParsedDocument();
            assertNull(doc);
        } catch (Exception e){

        }


    }


    private void parseDoc(SDOParsedXmlDocument doc){
        parseTree(doc.getRootSDOParsedXmlElement(),"");
    }

    private void parseTree(SDOParsedXmlElement doc, String indryk) {

        ArrayList<SDOParsedXmlElement> children = doc.getChildren();
        for (SDOParsedXmlElement child : children) {
            if (child.isLeaf()){

                System.out.print(indryk+"'"+child.getLabel()+"': '"+child.getValue()+"'");
                if (child.getProperty().isMany()){
                    if (child.getAddable()){
                        System.out.print(" (+)");
                    }
                    if (child.getRemovable()){
                        System.out.print("(-)");
                    }
                }
                System.out.print("  type="+child.getGuiTypeAsString());
                System.out.println();

            } else {
                System.out.print(indryk + "'"+child.getLabel()+"'");
                if (child.getProperty().isMany()){
                    if (child.getAddable()){
                        System.out.print(" (+)");
                    }
                    if (child.getRemovable()){
                        System.out.print("(-)");
                    }
                }
                System.out.println();

                parseTree(child, indryk+"    ");
            }
        }
    }

}
