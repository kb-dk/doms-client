package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.client.sdo.DOMSXMLData;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;


/**
 * A wrapper for a list of ObjectProperty. Contain methods for parsing
 * the properties from a foxml object.
 * @see ObjectProperty
 * @see #parse(Node)
 */
public class ObjectProperties extends DOMSXMLData implements List<ObjectProperty> {


	private ArrayList<ObjectProperty> properties;

    /**
     * Zero argument, no code constructor.
     *
     * @see #parse(Node)
     */
	public ObjectProperties(){
        properties = new ArrayList<ObjectProperty>();

    }


	/**
	 * Reads a list of object properties from an XML DOM Node. The nodename
     * must be "foxml:objectProperties", ie from a foxml serilization of an
     * object.
     *
	 *
	 * @param node the XML Dom Node.
	 */
	public void parse(Node node) {
		if (node == null)
			return;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return;

		if (node.getNodeName().equals("foxml:objectProperties")) {

			NodeList childNodes = node.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				switch (childNode.getNodeType()) {
				case Node.ELEMENT_NODE:
					String childNodeName = childNode.getNodeName();
					if (childNodeName.equals("foxml:property")) {
						ObjectProperty property = new ObjectProperty();
						property.parse(childNode);
						properties.add(property);
					}
				}
			}
		}
	}





    public <T> T[] toArray(T[] a) {
        return properties.toArray(a);
    }

    public Object[] toArray() {
        return properties.toArray();
    }

    public int lastIndexOf(Object o) {
        return properties.lastIndexOf(o);
    }

    public int size() {
        return properties.size();
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public boolean contains(Object o) {
        return properties.contains(o);
    }

    public int indexOf(Object o) {
        return properties.indexOf(o);
    }

    public ObjectProperty get(int index) {
        return properties.get(index);
    }

    public ObjectProperty set(int index, ObjectProperty element) {
        return properties.set(index, element);
    }

    public boolean add(ObjectProperty objectProperty) {
        return properties.add(objectProperty);
    }

    public void add(int index, ObjectProperty element) {
        properties.add(index, element);
    }

    public ObjectProperty remove(int index) {
        return properties.remove(index);
    }

    public boolean remove(Object o) {
        return properties.remove(o);
    }

    public void clear() {
        properties.clear();
    }

    public boolean addAll(Collection<? extends ObjectProperty> c) {
        return properties.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends ObjectProperty> c) {
        return properties.addAll(index, c);
    }

    public Iterator<ObjectProperty> iterator() {
        return properties.iterator();
    }

    public ListIterator<ObjectProperty> listIterator() {
        return properties.listIterator();
    }

    public ListIterator<ObjectProperty> listIterator(int index) {
        return properties.listIterator(index);
    }

    public List<ObjectProperty> subList(int fromIndex, int toIndex) {
        return properties.subList(fromIndex, toIndex);
    }

    public boolean equals(Object o) {
        return properties.equals(o);
    }

    public int hashCode() {
        return properties.hashCode();
    }

    public boolean containsAll(Collection<?> c) {
        return properties.containsAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return properties.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return properties.retainAll(c);
    }

    public String toString() {
        return properties.toString();
    }





}
