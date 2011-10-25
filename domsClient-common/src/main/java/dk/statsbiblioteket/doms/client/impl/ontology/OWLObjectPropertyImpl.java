package dk.statsbiblioteket.doms.client.impl.ontology;

import dk.statsbiblioteket.doms.client.ontology.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.ontology.OWLRestriction;
import dk.statsbiblioteket.doms.client.ontology.ParsedOwlOntology;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.doms.client.utils.DOMSXMLUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OWLObjectPropertyImpl implements OWLObjectProperty {
	
	private String id;
	private String about;
	private List<OWLRestriction> owlRestrictions = new ArrayList<OWLRestriction>();
	private ParsedOwlOntology myDataStream;
	
    public OWLObjectPropertyImpl(ParsedOwlOntology parsedOwlOntology, Node childNode) {
        setMyDataStream(parsedOwlOntology);
        parse(childNode);
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
	@Override
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
	@Override
    public String getAbout() {
		return about;
	}
	
	/**
	 * @return the minCardinality
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	
	@Override
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
	 * @return the owlRestrictions
	 */
	@Override
    public List<OWLRestriction> getOwlRestrictions() {
		return Collections.unmodifiableList(owlRestrictions);
	}
	
	/**
	 * @param myDataStream the myDataStream to set
	 */
	public void setMyDataStream(ParsedOwlOntology myDataStream) {
		this.myDataStream = myDataStream;
	}

	/**
	 * @return the myDataStream
	 */
	@Override
    public ParsedOwlOntology getMyDataStream() {
		return myDataStream;
	}

    public void addOwlRestriction(OWLRestriction restriction) {
        owlRestrictions.add(restriction);
    }


	@Override
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
	private boolean parse(Node node) {
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
			return namespace+ DOMSXMLUtils.xmlGetLocalName(attr.getNodeValue());
		}
	}

}
