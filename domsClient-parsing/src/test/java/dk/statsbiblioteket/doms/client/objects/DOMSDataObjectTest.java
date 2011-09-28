package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.DomsClient;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.AbstractDomsClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/21/11
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class DOMSDataObjectTest {
    DOMSDataObject ddo = null;
    DomsClient dc = null;

    @Before
    public void setUp() throws IOException, ServerOperationFailed, ServiceException {
        Properties prop = new Properties();
        prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("alhena.properties"));
        dc = new AbstractDomsClient(new URL(prop.getProperty("WSAPIEndpoint")),
                prop.getProperty("UserName"), prop.getProperty("Password")){};
        ddo = new DOMSDataObject("uuid:70ac3f7f-ab4a-43d6-b815-26a543453578", dc.getFactory());
    }

    @After
    public void tearDown(){

    }

    @org.junit.Test
    public void testLoadAll() throws Exception {

    }

    @org.junit.Test
    public void testLoad() throws Exception {
        ddo.load();
    }


}
