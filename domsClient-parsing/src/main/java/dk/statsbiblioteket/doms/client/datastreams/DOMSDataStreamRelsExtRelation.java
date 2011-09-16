package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.util.Constants;
import dk.statsbiblioteket.doms.client.util.DOMSXMLUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class DOMSDataStreamRelsExtRelation {

	private String name;
	private String prefix;
	private String localname;
	private String namespaceURI;
	private String xmlnsNamespace;
	private String resource;
	
	public DOMSDataStreamRelsExtRelation() {}

    public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setLocalname(String localname) {
		this.localname = localname;
	}

	public String getLocalname() {
		return localname;
	}

	/**
	 * @param namespaceURI the namespaceURI to set
	 */
	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	/**
	 * @return the namespaceURI
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setXmlnsNamespace(String namespace) {
		this.xmlnsNamespace = namespace;
	}

	public String getXmlnsNamespace() {
		return xmlnsNamespace;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getResource() {
		return Constants.ensurePID(resource);
	}



	/**
	 * Reads a RELS-EXT relation from an XML DOM Node.
	 *
	 * @param node the XML DOM Node.
	 */
	public boolean parse(Node node) {
		if (node == null)
			return false;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return false;

		setName(node.getNodeName());
		setPrefix(node.getPrefix());
		setLocalname(node.getLocalName());
		setNamespaceURI(node.getNamespaceURI());
		
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null) { 
			Node attr;
			attr = attrs.getNamedItemNS(Constants.RDF_NAMESPACE, "resource");
			if (attr != null) {
				setResource(getAttrValue(attr));
			}
			attr = attrs.getNamedItem("xmlns");
			if (attr != null) {
				setXmlnsNamespace(getAttrValue(attr));
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
	
	public String toString()
	{
		String result = "\nRelsExtRelation: ";
		result += "\n  Name: " + getName();
		result += "\n  Prefix: " + getPrefix();
		result += "\n  Localname: " + getLocalname();
		result += "\n  NamespaceURI: " + this.getNamespaceURI();
		result += "\n  XmlNsNamespace: " + getXmlnsNamespace();
		result += "\n  Resource: " + Constants.ensureURI(getResource());
		
		return result;
	}
}
