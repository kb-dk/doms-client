package dk.statsbiblioteket.doms.client.objects;


import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.utils.Constants;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateTest extends TestBase {

    public TemplateTest() throws MalformedURLException {
        super();
    }

    @Test
    @Ignore("Alhena test disabled due to alhena not using java 8")
    public void testTemplateCreation() throws ServerOperationFailed {
        DigitalObject template = factory.getDigitalObject("doms:Template_Program");
        if (template instanceof TemplateObject) {
            TemplateObject template1 = (TemplateObject) template;
            DigitalObject newObject = template1.clone();
            assertTrue(newObject.getPid().startsWith("uuid:"));
            assertEquals(newObject.getState(), Constants.FedoraState.Inactive);
            assertTrue(newObject instanceof DataObject);
            newObject.setState(Constants.FedoraState.Deleted);
        } else {
            fail("Template is not template");
        }

    }
}
