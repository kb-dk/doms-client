package dk.statsbiblioteket.doms.client.methods;

public interface Parameter {

	String getName();
	
	ParameterType getType();

    String getValue();

    void setValue(String value);

    boolean isRequired();

    boolean isRepeatable();

    /**
     * Returns the config value, if any, for this parameter
     * @return
     */
    String getConfig();
}
