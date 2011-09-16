package dk.statsbiblioteket.doms.client.owl;

import dk.statsbiblioteket.doms.client.datastreams.DOMSDataStreamOntology;
import dk.statsbiblioteket.doms.client.util.Constants;
import dk.statsbiblioteket.doms.client.util.DOMSXMLUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;




public class OWLObjectProperty {
	
	private String id;
	private String about;
	private OWLRestrictions owlRestrictions = new OWLRestrictions();
	private DOMSDataStreamOntology myDataStream;
	
	public OWLObjectProperty()
	{
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(String about) {
		this.about = about;
	}

	/**
	 * @return the about
	 */
	public String getAbout() {
		return about;
	}
	
	/**
	 * @return the minCardinality
	 */
	public int getMinCardinality() {
		for (OWLRestriction restriction : owlRestrictions)
		{
			if (restriction.getMinCardinality()!=3)
			{
				return restriction.getMinCardinality();
			}
		}
		return 0;
	}

	/**
	 * @return the maxCardinality
	 */
	public int getMaxCardinality() {
		for (OWLRestriction restriction : owlRestrictions)
		{
			if (restriction.getMaxCardinality()!=3)
			{
				return restriction.getMaxCardinality();
			}
		}
		return 0;
	}

	/**
	 * @return the cardinality
	 */
	public int getCardinality() {
		for (OWLRestriction restriction : owlRestrictions)
		{
			if (restriction.getCardinality()!=3)
			{
				return restriction.getCardinality();
			}
		}
		return 0;
	}

	/**
	 * if the id is not null the id is returned
	 * otherwise the about is returned.
	 * @return the mapping id
	 */
	public String getMappingId()
	{
		String value = "";
		if (getMyDataStream().getRdfBase()!=null)
		{
			value = getMyDataStream().getRdfBase();
		}
		if (getId()!=null)
		{
			return value + getId();
		}
		else
		{
			return value + getAbout();
		}
	}
	
	public String getLabel()
	{
		if (getId()!=null)
		{
			return getId();
		}
		else
		{
			return getAbout();
		}
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
	 * @param myDataStream the myDataStream to set
	 */
	public void setMyDataStream(DOMSDataStreamOntology myDataStream) {
		this.myDataStream = myDataStream;
	}

	/**
	 * @return the myDataStream
	 */
	public DOMSDataStreamOntology getMyDataStream() {
		return myDataStream;
	}

	public String getAllValuesFrom()
	{
		for (OWLRestriction restriction : owlRestrictions)
		{
			if (restriction.getAllValuesFrom()!=null)
			{
				return restriction.getAllValuesFrom();
			}
		}
		return null;
	}

	/**
	 * Reads a OWL ObjectProperty from an XML DOM Node.
	 *
	 * @param node the XML DOM Node.
	 */
	public boolean parse(Node node) {
		if (node == null)
			return false;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return false;
		
		if (!DOMSXMLUtils.doesNodeMatch(node, "ObjectProperty", Constants.OWL_NAMESPACE))
			return false;
		
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null) { 
			Node attr;
			attr = attrs.getNamedItemNS(Constants.RDF_NAMESPACE, "ID");
			if (attr != null) {
				setId(getAttrValue(attr));
			}
			attr = attrs.getNamedItemNS(Constants.RDF_NAMESPACE, "about");
			if (attr != null) {
				setAbout(getAttrValue(attr));
			}
		}
		return true;
	}
	
	private String getAttrValue(Node attr)
	{
		String namespaceAlias = DOMSXMLUtils.xmlGetNamePrefix(attr.getNodeValue());
		String namespace = null;
		
		if (namespaceAlias!=null)
		{
			namespace= attr.lookupNamespaceURI(namespaceAlias);
		}
		
		if(namespace==null)
		{
			return attr.getNodeValue();
		}
		else
		{
			return namespace+DOMSXMLUtils.xmlGetLocalName(attr.getNodeValue());
		}
	}

}
