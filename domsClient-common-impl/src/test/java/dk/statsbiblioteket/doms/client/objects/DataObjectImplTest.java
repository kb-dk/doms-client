package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.Checksum;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.Link;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.links.LinkPattern;

import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        //Setup test fixture
        CentralWebservice centralWebservice = mock(CentralWebservice.class);
        DigitalObjectFactory factory = new DigitalObjectFactoryImpl(centralWebservice);
        when(centralWebservice.getObjectProfile("uuid:fafda919-cd27-4f7b-bc6d-cdedb95e85a7"))
                .thenReturn(createProgramObjectProfile());
        when(centralWebservice.getObjectLinks("uuid:fafda919-cd27-4f7b-bc6d-cdedb95e85a7", -1l))
                .thenReturn(createLinkList());

        //1. GET OBJECT

        //Call method
        DigitalObject object = factory.getDigitalObject("uuid:fafda919-cd27-4f7b-bc6d-cdedb95e85a7");
        List<LinkPattern> linkPatterns = object.getLinkPatterns();

        //Assert expected calls
        //Should read object
        verify(centralWebservice).getObjectProfile("uuid:fafda919-cd27-4f7b-bc6d-cdedb95e85a7");
        //Should get link patterns
        verify(centralWebservice).getObjectLinks("uuid:fafda919-cd27-4f7b-bc6d-cdedb95e85a7", -1l);
        verifyNoMoreInteractions(centralWebservice);

        //Check result
        assertEquals("Should have expected results", 2, linkPatterns.size());
        Iterator<LinkPattern> iterator = linkPatterns.iterator();
        LinkPattern linkPattern1 = iterator.next();
        LinkPattern linkPattern2 = iterator.next();
        if (!linkPattern1.getName().equals("Name")) {
            LinkPattern temp = linkPattern1;
            linkPattern1 = linkPattern2;
            linkPattern2 = temp;
        }
        assertEquals("Name", linkPattern1.getName());
        assertEquals("Value", linkPattern1.getValue());
        assertEquals("Description", linkPattern1.getDescription());
        assertEquals("Name2", linkPattern2.getName());
        assertEquals("Value2", linkPattern2.getValue());
        assertEquals("Description2", linkPattern2.getDescription());
    }

    private ObjectProfile createProgramObjectProfile() {
        ObjectProfile profile = new ObjectProfile();

        profile.setState("A");
        profile.setPid("uuid:fafda919-cd27-4f7b-bc6d-cdedb95e85a7");

        return profile;
    }

    private List<Link> createLinkList() {
        Link link = new Link();
        link.setName("Name");
        link.setValue("Value");
        link.setDescription("Description");
        Link link2 = new Link();
        link2.setName("Name2");
        link2.setValue("Value2");
        link2.setDescription("Description2");
        return Arrays.asList(link, link2);
    }
}
