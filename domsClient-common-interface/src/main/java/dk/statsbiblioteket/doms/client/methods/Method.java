package dk.statsbiblioteket.doms.client.methods;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;

import java.util.Set;

/** This class represents a doms method */
public interface Method {

    /**
     * Get the name of this method
     *
     * @return
     */
    public String getName();

    /**
     * Invoke the method with a set of parameters
     *
     * @param parameters a set of parameters, for the invocation.
     *
     * @return The result as a string.
     * @see #getParameters()
     */
    public String invoke(Set<Parameter> parameters) throws ServerOperationFailed;

    public Set<Parameter> getParameters();


}
