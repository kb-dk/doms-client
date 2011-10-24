package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
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

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/24/11
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SdoTest {


    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");
    private URL domsWSAPIEndpoint;
    private String userName = "fedoraAdmin";
    private String password = "fedoraAdminPass";
    private DigitalObjectFactory factory;

    public SdoTest() throws MalformedURLException {
        domsWSAPIEndpoint = new URL("http://alhena:7880/centralWebservice-service/central/");
    }


    @org.junit.Before
    public void setUp() throws Exception {
        CentralWebservice domsAPI = new CentralWebserviceService(domsWSAPIEndpoint,
                                                                 CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI)
                .getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
        factory = new DigitalObjectFactoryImpl(domsAPI);

    }


    @Test
    public void testSdoPBCore()
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject("uuid:f8f1b607-1394-418a-a90e-e65d1b4bf91f");
        SDOParsedXmlElement doc = program.getDatastream("PBCORE").getSDOParsedDocument();

        parseTree(doc, "");

    }

    @Test
    public void testSdoDC()
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject("uuid:f8f1b607-1394-418a-a90e-e65d1b4bf91f");

        SDOParsedXmlElement doc = program.getDatastream("DC").getSDOParsedDocument();

        parseTree(doc, "");


    }


    @Test
    public void testSdoRelsExt()
            throws ServerOperationFailed, NotFoundException, IOException, MyXMLWriteException, MyXMLReadException {
        DigitalObject program = factory.getDigitalObject("uuid:f8f1b607-1394-418a-a90e-e65d1b4bf91f");


        try {
            SDOParsedXmlElement doc = program.getDatastream("RELS-EXT").getSDOParsedDocument();
            Assert.fail();
        } catch (Exception e){

        }


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
