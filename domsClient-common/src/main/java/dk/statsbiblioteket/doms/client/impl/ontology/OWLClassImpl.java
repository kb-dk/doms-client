package dk.statsbiblioteket.doms.client.impl.ontology;

import dk.statsbiblioteket.doms.client.ontology.OWLClass;
import dk.statsbiblioteket.doms.client.ontology.OWLRestriction;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.doms.client.utils.DOMSXMLUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OWLClassImpl implements OWLClass {

	private List<OWLRestriction> owlRestrictions = new ArrayList<OWLRestriction>();

    private String name;



    public OWLClassImpl(Node childNode) {
        parse(childNode);
    }


	/**
	 * @return the owlRestrictions
	 */
	@Override
    public List<OWLRestriction> getOwlRestrictions() {
		return Collections.unmodifiableList(owlRestrictions);
	}
	
	/**
	 * Reads a OWL Class from an XML DOM Node.
	 *
	 * @param node the XML DOM Node.
	 */
	private boolean parse(Node node) {
		if (node == null)
			return false;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return false;

		if (DOMSXMLUtils.doesNodeMatch(node, "Class", Constants.OWL_NAMESPACE))
		{
            NamedNodeMap attrs = node.getAttributes();
            Node nameNode = attrs.getNamedItemNS(Constants.RDF_NAMESPACE, "about");
            if (nameNode!= null){
                name = nameNode.getNodeValue();
            }
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
				if (DOMSXMLUtils.doesNodeMatch(childNode, "Restriction", Constants.OWL_NAMESPACE))
				{
					OWLRestriction owlRestriction = new OWLRestrictionImpl(childNode);

					owlRestrictions.add(owlRestriction);
				} 
			}
		}
	}

    @Override
    public String getName() {
        return name;
    }
}
