package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestBase {

    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");
    private URL domsWSAPIEndpoint;
    private String userName = "fedoraAdmin";
    private String password = "fedoraAdminPass";
    public DigitalObjectFactory factory;
    public static final String victimProgram = "uuid:8ec77383-4390-415a-a535-78126e419a50";

    public static  final String victimShard = "uuid:a4d10b2d-850e-4562-acea-28a2513b5418";


    public TestBase() throws MalformedURLException {
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

        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
    }
    @Test
    @Ignore
    public void emptyTest(){

    }


    protected void emptymize(SDOParsedXmlElement element    ){
        ArrayList<SDOParsedXmlElement> children = element.getChildren();
        for (SDOParsedXmlElement child : children) {
            if (child.isLeaf()){
                if (child.getValue() == null){
                    child.setValue("");
                }

            } else {
                emptymize(child);
            }
        }

    }

    protected void parseDoc(SDOParsedXmlDocument doc){
        parseTree(doc.getRootSDOParsedXmlElement(),"");
    }


    protected void parseTree(SDOParsedXmlElement doc, String indryk) {

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
