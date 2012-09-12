package dk.statsbiblioteket.doms.client.methods;

import java.util.Set;

public interface Method {

	public void callMe();
	
	public Set<Parameter> getParameters();
	
}
