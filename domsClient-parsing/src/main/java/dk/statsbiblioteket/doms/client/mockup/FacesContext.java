package dk.statsbiblioteket.doms.client.mockup;

import dk.statsbiblioteket.doms.client.util.Util;

import javax.xml.stream.events.EntityDeclaration;
import java.beans.Expression;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/19/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class FacesContext {
    private static Util currentInstance;
    private ExpressionFactory application;
    private EntityDeclaration userPrincipal;
    private Object ELContext;

    public FacesContext getCurrentInstance() {
        return this;
    }

    public ExpressionFactory getApplication() {
        return application;
    }

    public FacesContext getExternalContext() {
        return new FacesContext();
    }

    public String getInitParameter(String paramName) {
        return paramName;  //To change body of created methods use File | Settings | File Templates.
    }

    public EntityDeclaration getUserPrincipal() {
        return userPrincipal;
    }

    public Object getELContext() {
        return ELContext;
    }
}
