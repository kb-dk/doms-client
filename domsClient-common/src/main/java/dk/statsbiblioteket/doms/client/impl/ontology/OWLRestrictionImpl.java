package dk.statsbiblioteket.doms.client.impl.ontology;

import dk.statsbiblioteket.doms.client.ontology.OWLRestriction;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.doms.client.utils.DOMSXMLUtils;
import dk.statsbiblioteket.doms.client.utils.Util;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OWLRestrictionImpl implements OWLRestriction {

	private int minCardinality;
	private int maxCardinality;
	private int cardinality;
	private String onProperty;
	private String allValuesFrom;
	
	public OWLRestrictionImpl(Node node)
	{
		allValuesFrom = null;
		minCardinality = -2; //means it has not been specified
		maxCardinality = -2; //means it has not been specified
		cardinality = -2; //means it has not been specified
        parse(node);
	}

	/**
	 * @param minCardinality the minCardinality to set
	 */
	public void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}

	/**
	 * @return the minCardinality
	 */
	@Override
    public int getMinCardinality() {
		return minCardinality;
	}

	/**
	 * @param maxCardinality the maxCardinality to set
	 */
	public void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	/**
	 * @return the maxCardinality
	 */
	@Override
    public int getMaxCardinality() {
		return maxCardinality;
	}

	/**
	 * @param cardinality the cardinality to set
	 */
	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}

	/**
	 * @return the cardinality
	 */
	@Override
    public int getCardinality() {
		return cardinality;
	}

	/**
	 * @param onProperty the onProperty to set
	 */
	public void setOnProperty(String onProperty) {
		this.onProperty = onProperty;
	}

	/**
	 * @return the onProperty
	 */
	@Override
    public String getOnProperty() {
		return onProperty;
	}
	
	/**
	 * @param allValuesFrom the allValuesFrom to set
	 */
	public void setAllValuesFrom(String allValuesFrom) {
		this.allValuesFrom = allValuesFrom;
	}

	/**
	 * @return the allValuesFrom
	 */
	@Override
    public String getAllValuesFrom() {
		return allValuesFrom;
	}

	/**
	 * Reads a OWL Restriction from an XML DOM Node.
	 *
	 * @param node the XML DOM Node.
	 */
	private boolean parse(Node node) {
		if (node == null)
			return false;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return false;

		if (DOMSXMLUtils.doesNodeMatch(node, "Restriction", Constants.OWL_NAMESPACE))
		{
			NodeList childNodes = node.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				switch (childNode.getNodeType()) {
				case Node.ELEMENT_NODE:
					if (DOMSXMLUtils.doesNodeMatch(childNode, "minCardinality", Constants.OWL_NAMESPACE)) {
						setMinCardinality(Util.extractInt(DOMSXMLUtils.xmlGetFirstTextValue(childNode)));
					}
					else if (DOMSXMLUtils.doesNodeMatch(childNode, "maxCardinality", Constants.OWL_NAMESPACE)) {
						setMaxCardinality(Util.extractInt(DOMSXMLUtils.xmlGetFirstTextValue(childNode)));
					}
					else if (DOMSXMLUtils.doesNodeMatch(childNode, "cardinality", Constants.OWL_NAMESPACE)) {
						setCardinality(Util.extractInt(DOMSXMLUtils.xmlGetFirstTextValue(childNode)));
					}
					else if (DOMSXMLUtils.doesNodeMatch(childNode, "onProperty", Constants.OWL_NAMESPACE)) {
						setOnProperty(DOMSXMLUtils.xmlGetAttribute(childNode, "rdf:resource"));
					} 
					else if (DOMSXMLUtils.doesNodeMatch(childNode, "allValuesFrom", Constants.OWL_NAMESPACE)) {
						setAllValuesFrom(DOMSXMLUtils.xmlGetAttribute(childNode, "rdf:resource"));
					}
				}
			}
		}
		
		return true;
	}
	
	public String toString()
	{
		String result = "\nOWLRestriction:";
		result = "\nminCardinality = " + minCardinality;
		result += "\nmaxCardinality = " + maxCardinality;
		result += "\ncardinality = " + cardinality;
		result += "\nonProperty = " + onProperty;
		result += "\nallValuesFrom = " + allValuesFrom;
		
		return result;
	}
	
}
