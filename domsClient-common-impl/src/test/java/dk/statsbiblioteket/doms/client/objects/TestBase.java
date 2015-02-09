package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import org.custommonkey.xmlunit.XMLUnit;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class TestBase {
    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/", "CentralWebserviceService");

    private URL domsWSAPIEndpoint;
    private String userName = "fedoraAdmin";
    private String password = "fedoraAdminPass";
    public DigitalObjectFactory factory;
    public static final String victimProgram = "uuid:fafda919-cd27-4f7b-bc6d-cdedb95e85a7";

    public static  final String victimShard = "uuid:c171df65-9ffb-4011-9fae-4f6dccad9b9c";

    public static  final String CMVHSFilePID = "doms:ContentModel_VHSFile";


    public TestBase() throws MalformedURLException {
        domsWSAPIEndpoint = new URL("http://alhena:7880/centralWebservice-service/central/");
    }


    @org.junit.Before
    public void setUp() throws Exception {

        CentralWebservice domsAPI = new CentralWebserviceService(               // TODO mock in each test, then remove
                domsWSAPIEndpoint, CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
        factory = new DigitalObjectFactoryImpl(domsAPI);

        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
    }

    protected void emptymize(SDOParsedXmlElement element) {
        ArrayList<SDOParsedXmlElement> children = element.getChildren();
        for (SDOParsedXmlElement child : children) {
            if (child.isLeaf()) {
                if (child.getValue() == null) {
                    child.setValue("");
                }

            } else {
                emptymize(child);
            }
        }

    }
}
