package dk.statsbiblioteket.doms.client.owl;

import dk.statsbiblioteket.doms.client.owl.OWLObjectProperty;

import java.util.ArrayList;
import java.util.Iterator;

public class OWLObjectProperties implements Iterable<OWLObjectProperty> {
	private ArrayList<OWLObjectProperty> properties = new ArrayList<OWLObjectProperty>();
	
	public OWLObjectProperties()
	{
	}
	
	/**
	 * 
	 * @param properties
	 */
	public void setRestrictions(ArrayList<OWLObjectProperty> properties) {
		this.properties = properties;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<OWLObjectProperty> getRestrictions() {
		return properties;
	}
	
	/**
	 * 
	 * @param property
	 */
	public void add(OWLObjectProperty property)
	{
		properties.add(property);
	}
	
	/**
	 * @param newProperties
	 */
	public void addAll(OWLObjectProperties newProperties) {
		for (int i = 0; i < newProperties.size(); i++)
			this.properties.add(newProperties.get(i));
	}
	
	/**
	 * 
	 * @return the number of <code>OWLObjectProperty</code> objects in the list.
	 */
	public int size() {
		return properties.size();
	}
	
	/**
	 * @param index 
	 * @return <code>OWLObjectProperty</code> object at the given index
	 */
	public OWLObjectProperty get(int index) {
		return properties.get(index);
	}
	
	public Iterator<OWLObjectProperty> iterator() {
		return properties.iterator();
	}
}
