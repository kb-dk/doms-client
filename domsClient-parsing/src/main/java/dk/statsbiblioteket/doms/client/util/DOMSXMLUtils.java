/**
 * 
 */
package dk.statsbiblioteket.doms.client.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML utilities
 */
public class DOMSXMLUtils {
	
	public static Boolean doesNodeMatch(Node node, String nodenameIn, String namespaceIn)
	{
		String nodeName = (node.getLocalName()!=null) ? node.getLocalName() : null; 
		String nodeNamespace = (node.getNamespaceURI()!=null) ? node.getNamespaceURI() : "";
		
		return ((nodeName.equals(nodenameIn)) && (nodeNamespace.equals(namespaceIn)));
	}

	/**
	 * Metode til at l&aelig;se v&aelig;rdien af den f&oslash;rste child tekst node
	 * i en DOM Node.
	 * @param node DOM Node fra hvilken v&aelig;rdien af den f&oslash;rste child tekst node skal l&aelig;ses.
	 */
	public static String xmlGetFirstTextValue(Node node) {
		String theValue = "";

		NodeList theNodeList = node.getChildNodes();
		if (theNodeList != null) {
			for (int i = 0; i < theNodeList.getLength(); i++) {
				Node textNode = theNodeList.item(i);
				if (textNode != null) {
					theValue = textNode.getNodeValue();
					if (theValue == null) {
						theValue = "";
					}
					break;
				}
			}
		}
		return theValue;
	}
	
	/**
	 * 
	 * @param root
	 * @param tagname
	 * @param attrKey
	 * @param attrValue
	 * @return
	 */
	public static Node getSpecificNode(Node root, String tagname, Boolean isLocalName, String attrKey, String attrValue)
	{
		Node result = null;
		if (root.getNodeType() != Node.ELEMENT_NODE)
		{
			return null;
		}
		
		NodeList childNodes = root.getChildNodes();
		
		if (childNodes!=null)
		{
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (result == null)
				{
					Node childNode = childNodes.item(i);
					Boolean match = false;
					if (isLocalName)
					{
						if (childNode.getLocalName()!=null)
						{
							match = childNode.getLocalName().equals(tagname);
						}
					}
					else
					{
						match = childNode.getNodeName().equals(tagname);
					}
					if (match) {
						if ((attrKey==null) && (attrValue==null))
						{
							return childNode;
						}
						else
						{
							NamedNodeMap attrs = childNode.getAttributes();
							if (attrs != null) {
								Node attr = attrs.getNamedItem(attrKey);
								if (attr != null) {
									if (attr.getNodeValue().equals(attrValue))
									{
										result = childNode;
										return result;
									}
								}
							}
						}
					}
					else
					{
						if (result == null)
						{
							result = getSpecificNode(childNode, tagname, isLocalName, attrKey, attrValue);
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Reads an attribut from a DOM Node.
	 * 
	 * @param node
	 *            The Node to read the attribute from.
	 * @param attrname
	 *            The name of the attribute to read.
	 */
	public static String xmlGetAttribute(Node node, String attrname) {
		String theValue = null;
		if (node == null)
			return null;

		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null) {
			Node attr = attrs.getNamedItem(attrname);
			if (attr != null) {
				return attr.getNodeValue();
			}
		}

		return theValue;
	}
	
	public static void xmlGetAttribute2(Node node, String attrname) {
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++)
			{
				Node attr = attrs.item(i);
				/*
				System.out.println("attr.getNodeName() = " + attr.getNodeName());
				System.out.println("attr.getLocalName() = " + attr.getLocalName());
				System.out.println("attr.getNodeValue() = " + attr.getNodeValue());
				*/
			}
		}
	}

	public static String xmlGetLocalName(String attrname) 
	{
		 if (attrname!=null)
		 {
			 int inx = attrname.lastIndexOf(':');
			 if (inx!=-1)
			 {
				 return attrname.substring(attrname.lastIndexOf(':') + 1);
			 }
			 else
			 {
				 return attrname;
			 }
		}
		return null;
	}
	 
	 public static String xmlGetNamePrefix(String attrname) 
	 {
		 String theValue = null;
		 if ((attrname!=null) && (attrname.lastIndexOf(':')!=-1))
		 {
			 return attrname.substring(0, attrname.lastIndexOf(':'));
		}
		return theValue;
	}
}
