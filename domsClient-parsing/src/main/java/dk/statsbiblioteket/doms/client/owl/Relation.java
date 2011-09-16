package dk.statsbiblioteket.doms.client.owl;

import dk.statsbiblioteket.doms.client.owl.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.util.Constants;

public class Relation {

	private OWLObjectProperty objProp;
	private String pid;
	
	public Relation(OWLObjectProperty objProp, String pid)
	{
		this.setObjProp(objProp);
		this.pid = pid;
	}

	/**
	 * @return the typePid
	 */
	public String getTypePid() {
		if (getObjProp().getAllValuesFrom()!=null)
		{
			return getObjProp().getAllValuesFrom().substring(Constants.INFO_FEDORA_URI_SCHEME.length(), getObjProp().getAllValuesFrom().length());
		}
		return null;
	}
	

	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param objProp the objProp to set
	 */
	public void setObjProp(OWLObjectProperty objProp) {
		this.objProp = objProp;
	}
	
	/**
	 * @return the minCardinality
	 */
	public int getMinCardinality() {
		return objProp.getMinCardinality();
	}

	/**
	 * @return the maxCardinality
	 */
	public int getMaxCardinality() {
		return objProp.getMaxCardinality();
	}

	/**
	 * @return the cardinality
	 */
	public int getCardinality() {
		return objProp.getCardinality();
	}


	/**
	 * @return the objProp
	 */
	public OWLObjectProperty getObjProp() {
		return objProp;
	}
	
	public String getLabel()
	{
		return objProp.getLabel();
	}
	
	@Override
	public String toString()
	{
		return pid;
	}
}
