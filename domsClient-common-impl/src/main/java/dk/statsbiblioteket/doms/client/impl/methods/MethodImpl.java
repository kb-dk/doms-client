package dk.statsbiblioteket.doms.client.impl.methods;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.methods.Method;
import dk.statsbiblioteket.doms.client.methods.Parameter;
import dk.statsbiblioteket.doms.client.methods.ParameterType;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.lang.String;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/12/12
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodImpl implements Method {

    private CentralWebservice api;
    private ContentModelObject contentModelObject;
    private String name;
    private Set<Parameter> parameters;


    public MethodImpl(CentralWebservice api, ContentModelObject contentModelObject,String name, Set<Parameter> parameters) {
        this.api = api;
        this.contentModelObject = contentModelObject;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public ContentModelObject getContentModelObject() {
        return contentModelObject;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String invoke(Set<Parameter> parameters) throws ServerOperationFailed {
        List<Pair> pairs = new ArrayList<Pair>();
        for (Parameter parameter : parameters) {
            Pair soapPair = new Pair();
            soapPair.setName(parameter.getName());
            soapPair.setValue(parameter.getValue());
            pairs.add(soapPair);
        }
        try {
            String result = api.invokeMethod(contentModelObject.getPid(), name, pairs);
            return result;
        } catch (Exception e) {
            throw new ServerOperationFailed(e);
        }
    }

    @Override
    public Set<Parameter> getParameters() {
        HashSet<Parameter> result = new HashSet<Parameter>();
        for (Parameter parameter : parameters) {
            if (parameter instanceof ParameterImpl) {
                ParameterImpl parameter1 = (ParameterImpl) parameter;
                result.add(parameter1.clone());
            } else {
                result.add(parameter);
            }

        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodImpl)) return false;

        MethodImpl method = (MethodImpl) o;

        if (!contentModelObject.equals(method.contentModelObject)) return false;
        if (!name.equals(method.name)) return false;
        if (!parameters.equals(method.parameters)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contentModelObject.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }
}