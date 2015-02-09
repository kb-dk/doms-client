package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.methods.Method;
import dk.statsbiblioteket.doms.client.methods.Parameter;
import org.junit.Test;

import java.lang.String;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/12/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodsTest extends TestBase {

    public MethodsTest() throws MalformedURLException {
        super();
    }

    @Test
    public void methodsTest1OLD() throws ServerOperationFailed {
        ContentModelObject contentModel = (ContentModelObject) factory.getDigitalObject("doms:ContentModel_VHSFile");
        Set<Method> methods = contentModel.getMethods();
        assertTrue("No methods defined", methods.size() > 0);
        for (Method method : methods) {
            assertEquals("Name of method wrong", "VHS import", method.getName());
            Set<Parameter> parameters = method.getParameters();
            assertTrue("No params defined", parameters.size() > 0);
            for (Parameter parameter : parameters) {
                parameter.setValue("test");
            }
            String result = method.invoke(parameters);
            System.out.println(result);
            assertTrue("result wrong", result.length() > 3);

        }
    }

    /**
     * Test that it is possible to invoke methods on Fedora objects
     *
     * @throws ServerOperationFailed
     */
    @Test
    public void methodsTest() throws ServerOperationFailed, MethodFailedException, InvalidResourceException,
            InvalidCredentialsException {
        // Setup fixture
        CentralWebservice domsAPIMock = mock(CentralWebservice.class);

        when(domsAPIMock.getObjectProfile(CMVHSFilePID)).thenReturn(createObjectProfile());
        when(domsAPIMock.getMethods(CMVHSFilePID)).thenReturn(Arrays.asList(createMethod()));
        when(domsAPIMock.invokeMethod(eq(CMVHSFilePID), eq("VHS import"), anyListOf(Pair.class))).thenReturn("method invoked");
        DigitalObjectFactory factory = new DigitalObjectFactoryImpl(domsAPIMock);

        // Test and verify
        ContentModelObject contentModel = (ContentModelObject) factory.getDigitalObject("doms:ContentModel_VHSFile");
        Set<Method> methods = contentModel.getMethods();

        assertTrue("No methods defined", methods.size() > 0);

        for (Method method : methods) {
            assertEquals("Name of method wrong", "VHS import", method.getName());

            Set<Parameter> parameters = method.getParameters();
            assertTrue("No params defined", parameters.size() > 0);

            for (Parameter parameter : parameters) {
                parameter.setValue("test");
            }
            String result = method.invoke(parameters);
            System.out.println(result); // TODO remove
            assertTrue("result wrong", result.length() > 3);
        }
    }

    private ObjectProfile createObjectProfile() {
        ObjectProfile CMVHSFileProfile = new ObjectProfile();
        CMVHSFileProfile.setPid(CMVHSFilePID);
        CMVHSFileProfile.setState("A");
        CMVHSFileProfile.setType("ContentModel");
        return CMVHSFileProfile;
    }

    private dk.statsbiblioteket.doms.central.Method createMethod() {
        dk.statsbiblioteket.doms.central.Method centralMethod = new dk.statsbiblioteket.doms.central.Method();
        centralMethod.setName("VHS import");
        centralMethod.setType("static");
        centralMethod.setParameters(createParameters());
        return centralMethod;
    }

    private Parameters createParameters() {
        Parameters centralMethodParameters = new Parameters();
        dk.statsbiblioteket.doms.central.Parameter param = new dk.statsbiblioteket.doms.central.Parameter();
        centralMethodParameters.getParameter().add(param);
        param.setName("param1");
        param.setType("Text");
        return centralMethodParameters;
    }
}
