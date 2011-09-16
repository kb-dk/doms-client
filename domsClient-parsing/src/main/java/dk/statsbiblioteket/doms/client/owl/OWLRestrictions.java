package dk.statsbiblioteket.doms.client.owl;

import java.util.ArrayList;
import java.util.Iterator;

public class OWLRestrictions implements Iterable<OWLRestriction> {
	private ArrayList<OWLRestriction> restrictions = new ArrayList<OWLRestriction>();

	public OWLRestrictions()
	{
	}
	
	/**
	 * 
	 * @param restrictions
	 */
	public void setRestrictions(ArrayList<OWLRestriction> restrictions) {
		this.restrictions = restrictions;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<OWLRestriction> getRestrictions() {
		return restrictions;
	}
	
	/**
	 * 
	 * @param restriction
	 */
	public void add(OWLRestriction restriction)
	{
		restrictions.add(restriction);
	}
	
	/**
	 * 
	 * @param newRestrictions
	 */
	public void addAll(OWLRestrictions newRestrictions) {
		for (int i = 0; i < newRestrictions.size(); i++)
			this.restrictions.add(newRestrictions.get(i));
	}
	
	/**
	 * 
	 * @return the number of <code>OWLRestriction</code> objects in the list.
	 */
	public int size() {
		return restrictions.size();
	}
	
	/**
	 * @param index 
	 * @return <code>OWLRestriction</code> object at the given index
	 */
	public OWLRestriction get(int index) {
		return restrictions.get(index);
	}
	
	public Iterator<OWLRestriction> iterator() {
		return restrictions.iterator();
	}
}
