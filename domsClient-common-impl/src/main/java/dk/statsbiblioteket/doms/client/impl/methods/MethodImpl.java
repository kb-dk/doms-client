package dk.statsbiblioteket.doms.client.impl.methods;

import dk.statsbiblioteket.doms.client.methods.Method;
import dk.statsbiblioteket.doms.client.methods.Parameter;
import dk.statsbiblioteket.doms.client.methods.ParameterType;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/12/12
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodImpl implements Method {

    private ContentModelObject contentModelObject;
    private String name;
    private boolean parsed = false;
    private Set<ParameterImpl> parameters;


    public MethodImpl(ContentModelObject contentModelObject, String name) {
        this.contentModelObject = contentModelObject;
        this.name = name;
        parseMethodDef();
    }

    private synchronized void parseMethodDef(){
        if (parsed){
            return;
        }
        parameters = new HashSet<ParameterImpl>();
        //TODO mockup from here
        parameters.add(new ParameterImpl("file", ParameterType.ServerFile, "", true, false, "/home/fedora/"));
        parameters.add(new ParameterImpl("channelID", ParameterType.Text, "", true, false, ""));
        parameters.add(new ParameterImpl("startTime", ParameterType.Datetime,"",false,false,""));
        parameters.add(new ParameterImpl("endTime", ParameterType.Datetime,"",false,false,""));
        parameters.add(new ParameterImpl("vhsLabel", ParameterType.TextBox,"",false,true,""));
        parameters.add(new ParameterImpl("recorder", ParameterType.Text,"",true,false,""));
        parameters.add(new ParameterImpl("quality", ParameterType.Integer,"",true,false,""));

        parsed = true;

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
    public String invoke(Set<Parameter> parameters) {
        return "uuid:"+UUID.randomUUID().toString();
    }

    @Override
    public Set<Parameter> getParameters() {
        HashSet<Parameter> result = new HashSet<Parameter>();
        for (ParameterImpl parameter : parameters) {
            result.add(parameter.clone());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodImpl)) return false;

        MethodImpl method = (MethodImpl) o;

        if (parsed != method.parsed) return false;
        if (!contentModelObject.equals(method.contentModelObject)) return false;
        if (!name.equals(method.name)) return false;
        if (!parameters.equals(method.parameters)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contentModelObject.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (parsed ? 1 : 0);
        result = 31 * result + parameters.hashCode();
        return result;
    }
}
