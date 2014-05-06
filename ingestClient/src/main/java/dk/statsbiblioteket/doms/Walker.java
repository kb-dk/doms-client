package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.Relation;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 11/10/11
 * Time: 7:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Walker {

    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/", "CentralWebserviceService");
    /**
     * Reference to the active DOMS webservice client instance.
     */
    protected CentralWebservice domsAPI;


    public Walker(URL domsWSAPIEndpoint, String userName, String password) {
        domsAPI = new CentralWebserviceService(
                domsWSAPIEndpoint, CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    public void walk() throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        List<Relation> relations = domsAPI.getInverseRelations("doms:ContentModel_Program");

        for (Relation relation : relations) {
            String programPid = relation.getObject();
            domsAPI.getDatastreamContents(programPid, "PBCORE");
        }
    }

}
