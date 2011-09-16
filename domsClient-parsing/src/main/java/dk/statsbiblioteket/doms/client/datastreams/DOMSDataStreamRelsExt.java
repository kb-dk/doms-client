package dk.statsbiblioteket.doms.client.datastreams;

import java.util.ArrayList;
import java.util.Iterator;

import dk.statsbiblioteket.doms.client.objects.DOMSContentModel;
import dk.statsbiblioteket.doms.client.objects.DOMSDataObject;
import dk.statsbiblioteket.doms.client.util.Constants;
import dk.statsbiblioteket.doms.client.util.DOMSXMLUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class DOMSDataStreamRelsExt extends DOMSDataStream implements Iterable<DOMSDataStreamRelsExtRelation> {

	private ArrayList<DOMSDataStreamRelsExtRelation> relsExtRelations = new ArrayList<DOMSDataStreamRelsExtRelation>();
	
	
	public DOMSDataStreamRelsExt(DOMSContentModel myDomsContentModel)
	{
		super(myDomsContentModel);
		setId(Constants.RELS_EXT_ID);
	}
	
	public DOMSDataStreamRelsExt(DOMSDataObject myDomsContentModelInstance)
	{
		super(myDomsContentModelInstance.getContentModel(), myDomsContentModelInstance);
		setId(Constants.RELS_EXT_ID);
	}

    public void setRelsExtRelations(ArrayList<DOMSDataStreamRelsExtRelation> relsExtRelations) {
		this.relsExtRelations = relsExtRelations;
	}

	public ArrayList<DOMSDataStreamRelsExtRelation> getRelsExtRelations() {
		return relsExtRelations;
	}

	public Iterator<DOMSDataStreamRelsExtRelation> iterator() {
		return getRelsExtRelations().iterator();
	}
	
	public void parseRdf(Node node)
	{
		if (DOMSXMLUtils.doesNodeMatch(node, "RDF", Constants.RDF_NAMESPACE))
		{
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				
				switch (childNode.getNodeType()) {
				case Node.ELEMENT_NODE:
					
					if (DOMSXMLUtils.doesNodeMatch(childNode, "Description", Constants.RDF_NAMESPACE))
					{
						NamedNodeMap attrs = childNode.getAttributes();
						
						if (attrs != null) { 
							Node attr; 
							attr = attrs.getNamedItemNS(Constants.RDF_NAMESPACE, "about");
							
							if (attr != null) {
								String about = attr.getNodeValue();
								if (about.length()> Constants.INFO_FEDORA_URI_SCHEME.length())
								{
									String pid = about.substring(Constants.INFO_FEDORA_URI_SCHEME.length(), about.length());
									
									if (this.getOwningDigitalObject()!=null)
									{
										if (pid.equals(this.getOwningDigitalObject().getPid()))
										{
											parseRelations(childNode);
										}
									}
									else
									{
										if (pid.equals(this.getMyDomsContentModel().getPid()))
										{
											parseRelations(childNode);
										}
									}
								}
							}
						}
					}
				}
			}
			
		}
	}
	
	private void parseRelations(Node node)
	{
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			
			switch (childNode.getNodeType()) {
			case Node.ELEMENT_NODE:
				DOMSDataStreamRelsExtRelation rel = new DOMSDataStreamRelsExtRelation();
				rel.parse(childNode);
				getRelsExtRelations().add(rel);
			}
		}
	}
	
	private void printNodeInfo(Node node)
	{
		System.out.println();
		System.out.println("\nnode.getNodeName() = " + node.getNodeName());
		System.out.println("  node.getLocalName() = " + node.getLocalName());
		System.out.println("  node.getBaseURI() = " + node.getBaseURI());
		String prefix = node.getPrefix();
		System.out.println("  node.getPrefix() = " + prefix);
		String namespaceURI = node.getNamespaceURI();
		System.out.println("  node.getNamespaceURI() = " + namespaceURI);
		if (namespaceURI!=null)
		{
			System.out.println("  node.getPrefix() = " + node.lookupPrefix(namespaceURI));
		}
		if (prefix!=null)
		{
			System.out.println("node.lookupNamespaceURI(prefix) = " + node.lookupNamespaceURI(prefix));
		}
	}
}
