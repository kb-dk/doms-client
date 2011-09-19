package dk.statsbiblioteket.doms.client.mockup;

import javax.xml.stream.events.EntityDeclaration;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/19/11
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExternalContext {
    private EntityDeclaration userPrincipal;

    public EntityDeclaration getUserPrincipal() {
        return userPrincipal;
    }

    public String getInitParameter(String filepath) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
