package dk.statsbiblioteket.doms.client.methods;

public interface Parameter {

	String getName();
	
	ParameterType getType();

    String getValue();

    void setValue();

    boolean getRequired();

    boolean getRepeatable();

    /**
     * Returns the config value, if any, for this parameter
     * @return
     */
    String getConfig();
}
