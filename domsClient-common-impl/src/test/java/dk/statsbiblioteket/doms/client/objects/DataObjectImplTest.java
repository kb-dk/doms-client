package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.links.LinkPattern;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 3/13/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataObjectImplTest extends TestBase {

    public DataObjectImplTest() throws MalformedURLException {
    }

    @Test
    public void testGetLinkPatterns() throws Exception {
        DigitalObject object = factory.getDigitalObject(victimProgram);

        if (object instanceof DataObject) {
            DataObject dataObject = (DataObject) object;
            List<LinkPattern> linkPatterns = dataObject.getLinkPatterns();
            assertTrue(linkPatterns.size() > 0);
            for (LinkPattern linkPattern : linkPatterns) {
                System.out.println(linkPattern.getValue());
            }
        }
    }
}
