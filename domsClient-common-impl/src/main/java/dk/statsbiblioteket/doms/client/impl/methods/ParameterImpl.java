package dk.statsbiblioteket.doms.client.impl.methods;

import dk.statsbiblioteket.doms.client.methods.Parameter;
import dk.statsbiblioteket.doms.client.methods.ParameterType;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/12/12
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterImpl implements Parameter {

    private String name;

    private ParameterType type;

    private String value;

    private boolean required;

    private boolean repeatable;

    private String config;

    public ParameterImpl(String name, ParameterType type, String value, boolean required, boolean repeatable,
                         String config) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.required = required;
        this.repeatable = repeatable;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public ParameterType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public String getConfig() {
        return config;
    }

    protected Parameter clone() {
        return new ParameterImpl(name, type, value, required, repeatable, config);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParameterImpl)) {
            return false;
        }

        ParameterImpl parameter = (ParameterImpl) o;

        if (repeatable != parameter.repeatable) {
            return false;
        }
        if (required != parameter.required) {
            return false;
        }
        if (config != null ? !config.equals(parameter.config) : parameter.config != null) {
            return false;
        }
        if (name != null ? !name.equals(parameter.name) : parameter.name != null) {
            return false;
        }
        if (type != parameter.type) {
            return false;
        }
        if (value != null ? !value.equals(parameter.value) : parameter.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (repeatable ? 1 : 0);
        result = 31 * result + (config != null ? config.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParameterImpl{" +
               "name='" + name + '\'' +
               ", type=" + type +
               ", value='" + value + '\'' +
               ", required=" + required +
               ", repeatable=" + repeatable +
               ", config='" + config + '\'' +
               '}';
    }
}
