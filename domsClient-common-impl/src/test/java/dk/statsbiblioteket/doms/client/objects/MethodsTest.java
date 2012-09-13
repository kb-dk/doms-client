package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.methods.Method;
import dk.statsbiblioteket.doms.client.methods.Parameter;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/12/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodsTest extends TestBase{

    public MethodsTest() throws MalformedURLException {
        super();
    }

    @Test
    public void methodsTest1() throws ServerOperationFailed {
        ContentModelObject contentModel = (ContentModelObject) factory.getDigitalObject("doms:ContentModel_VHSFile");
        Set<Method> methods = contentModel.getMethods();
        assertTrue("No methods defined",methods.size() > 0);
        for (Method method : methods) {
            assertEquals("Name of method wrong","Ole_Import",method.getName());
            Set<Parameter> parameters = method.getParameters();
            assertTrue("No params defined",parameters.size() > 0);
            for (Parameter parameter : parameters) {
                parameter.setValue("test");
            }
            String result = method.invoke(parameters);
            System.out.println(result);
            assertTrue("result wrong",result.length() > 3);

        }

    }

}
