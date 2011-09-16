/**
 * 
 */
package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.util.Constants;
import dk.statsbiblioteket.doms.client.util.DOMSXMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * 
 */
public class DOMSDataStreamViewGui {
	public ArrayList<Node> relations = new ArrayList<Node>();
	public ArrayList<Node> inverse_relations = new ArrayList<Node>();
	
	public DOMSDataStreamViewGui()
	{
		
	}

    /**
	 * Reads a digital object from an XML DOM Node.
	 *
	 * @param node the XML Dom Node.
	 */
	public void parse(Node node) {

        /*
	<v:views xmlns:v="http://ecm.sourceforge.net/types/view/0/2/#">
  <v:viewangle name="SummaVisible">
    <v:relations xmlns:doms="http://doms.statsbiblioteket.dk/relations/default/0/1/#">
      <doms:hasShard></doms:hasShard>
    </v:relations>
    <v:inverseRelations></v:inverseRelations>
  </v:viewangle>
</v:views>
*/
		if (node == null)
			return;

		if (node.getNodeType() != Node.ELEMENT_NODE)
			return;

		if (DOMSXMLUtils.doesNodeMatch(node, "viewangle", Constants.VIEWS_NAMESPACE))
		{
			NodeList childNodes = node.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				switch (childNode.getNodeType()) {
				case Node.ELEMENT_NODE:
					if (DOMSXMLUtils.doesNodeMatch(childNode, "relations", Constants.VIEWS_NAMESPACE)) {
						parseRelations(childNode);
					} 
					else if (DOMSXMLUtils.doesNodeMatch(childNode, "inverse-relations",
                                                        Constants.VIEWS_NAMESPACE)) {
						//TODO parse inverse relations						
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
				relations.add(childNode);
			}
		}
	}
	

	
}
