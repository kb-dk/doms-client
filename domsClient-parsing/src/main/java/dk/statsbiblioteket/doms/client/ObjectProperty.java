package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.client.sdo.DOMSXMLData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;




/**
 * Class representing a name-value pair. ObjectProperties are arbitrary properties
 * that can be set for any objects. Contain a method to parse its contents
 * from foxml
 *
 * @see #parse(Node)
 * @see dk.statsbiblioteket.doms.model.object.DOMSDigitalObject
 * @see dk.statsbiblioteket.doms.model.ObjectProperties
 */
public class ObjectProperty extends DOMSXMLData {

	private String name;
	private String value;

    /**
     * No effect constructor
     */
	public ObjectProperty(){
    }


    /**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Reads a property from an XML DOM Node. The nodename must be "foxml:property".
     * Populates the name and value of this object. 
	 *
	 * @param node the XML Dom Node.
	 */
	public void parse(Node node) {
		if (node == null)
			return;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return;

		if (node.getNodeName().equals("foxml:property")) {
			NamedNodeMap attrs = node.getAttributes();
			if (attrs != null) {
				Node attr = attrs.getNamedItem("NAME");
				if (attr != null) {
					setName(attr.getNodeValue());
				}
				attr = attrs.getNamedItem("VALUE");
				if (attr != null) {
					setValue(attr.getNodeValue());
				}
			}
		}
	}
	
	public String toHTML() {
		String resultHTML = "";
		
		resultHTML += "<div>" + name + ": " + value + "</div>";
		
		return resultHTML;
	}
}
