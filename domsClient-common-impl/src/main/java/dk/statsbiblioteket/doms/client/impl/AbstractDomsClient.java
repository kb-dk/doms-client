package dk.statsbiblioteket.doms.client.impl;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.client.DomsClient;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.objects.AbstractDigitalObjectFactory;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/6/11
 * Time: 9:01 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDomsClient implements DomsClient {

    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/", "CentralWebserviceService");
    /**
     * Reference to the active DOMS webservice client instance.
     */
    protected CentralWebservice domsAPI;
    private AbstractDigitalObjectFactory factory;

    public AbstractDomsClient(URL domsWSAPIEndpoint, String userName, String password) {
        domsAPI = new CentralWebserviceService(
                domsWSAPIEndpoint, CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
        factory = new DigitalObjectFactoryImpl(domsAPI);
    }


    /**
     * Get the factory to read the objects
     *
     * @return
     */
    public DigitalObjectFactory getFactory() {
        return factory;
    }

    @Override
    public boolean testLogin() throws ServerOperationFailed {
        //attempts an operation on an object that does not exist. As policies are checked at the beginning, the
        //invalid credentials exception will be thrown even for an object that does not exist.
        try {
            domsAPI.markInProgressObject(Arrays.asList("doms:This_PID_CANNOT_EXIST"), "attempting login");
        } catch (InvalidCredentialsException e) {
            return false;
        } catch (InvalidResourceException e) {
            return true;
        } catch (MethodFailedException e) {
            return true;
        }
        return true;
    }
}
