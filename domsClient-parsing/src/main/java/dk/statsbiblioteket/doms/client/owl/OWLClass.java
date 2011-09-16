package dk.statsbiblioteket.doms.client.owl;

import dk.statsbiblioteket.doms.client.owl.OWLRestrictions;
import dk.statsbiblioteket.doms.client.util.Constants;
import dk.statsbiblioteket.doms.client.util.DOMSXMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class OWLClass {

	private OWLRestrictions owlRestrictions = new OWLRestrictions();
	
	public OWLClass()
	{
	}

	/**
	 * @param owlRestrictions the owlRestrictions to set
	 */
	public void setOwlRestrictions(OWLRestrictions owlRestrictions) {
		this.owlRestrictions = owlRestrictions;
	}

	/**
	 * @return the owlRestrictions
	 */
	public OWLRestrictions getOwlRestrictions() {
		return owlRestrictions;
	}
	
	/**
	 * Reads a OWL Class from an XML DOM Node.
	 *
	 * @param node the XML DOM Node.
	 */
	public boolean parse(Node node) {
		if (node == null)
			return false;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return false;

		if (DOMSXMLUtils.doesNodeMatch(node, "Class", Constants.OWL_NAMESPACE))
		{
			NodeList childNodes = node.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				switch (childNode.getNodeType()) {
				case Node.ELEMENT_NODE:
					if (DOMSXMLUtils.doesNodeMatch(childNode, "subClassOf", Constants.RDFS_NAMESPACE))
					{
						parseRestrictions(childNode);
					} 
				}
			}
		}
		
		return true;
	}
	
	private void parseRestrictions(Node node)
	{
		NodeList childNodes = node.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			switch (childNode.getNodeType()) {
			case Node.ELEMENT_NODE:
				if (DOMSXMLUtils
                        .doesNodeMatch(childNode, "Restriction", Constants.OWL_NAMESPACE))
				{
					OWLRestriction owlRestriction = new OWLRestriction();
					owlRestriction.parse(childNode);
					owlRestrictions.add(owlRestriction);
				} 
			}
		}
	}
}
