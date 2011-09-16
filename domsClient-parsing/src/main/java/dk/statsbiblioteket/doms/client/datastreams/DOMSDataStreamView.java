/**
 * 
 */
package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.objects.DOMSContentModel;
import dk.statsbiblioteket.doms.client.util.Constants;
import dk.statsbiblioteket.doms.client.util.DOMSXMLUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 *
 */
public class DOMSDataStreamView extends DOMSDataStream {
	public DOMSDataStreamViewGui guiView;
	
	public DOMSDataStreamView()
	{
		setId(Constants.VIEW_ID);
	}
	
	public DOMSDataStreamView(DOMSContentModel myDomsContentModel)
	{
		super(myDomsContentModel);
		setId(Constants.VIEW_ID);
	}

    public void parseGuiView(Node node)
	{/*
	<v:views xmlns:v="http://ecm.sourceforge.net/types/view/0/2/#">
  <v:viewangle name="SummaVisible">
    <v:relations xmlns:doms="http://doms.statsbiblioteket.dk/relations/default/0/1/#">
      <doms:hasShard></doms:hasShard>
    </v:relations>
    <v:inverseRelations></v:inverseRelations>
  </v:viewangle>
</v:views>
*/
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			switch (childNode.getNodeType()) {
			case Node.ELEMENT_NODE:
				if (DOMSXMLUtils
                        .doesNodeMatch(childNode, "viewangle", Constants.VIEWS_NAMESPACE))
				{
					NamedNodeMap attrs = childNode.getAttributes();
					if (attrs != null) {
						Node attr;
						attr = attrs.getNamedItem("name");
						if ((attr != null) && (attr.getNodeValue().equals("GUI")))
						{
							guiView = new DOMSDataStreamViewGui();
							guiView.parse(childNode);
							break;
						}
					}
				}
			}
		}
	}
	
	public void addRelationsDoNotOverwrite(DOMSDataStreamView otherViewDataStream)
	{
		if (guiView==null)
		{
			guiView = new DOMSDataStreamViewGui();
		}
		
		if ((guiView!=null) && (otherViewDataStream.guiView!=null))
		{
			for (Node rNode : otherViewDataStream.guiView.relations)
			{
				addRelationDoNotOverwrite(rNode);
			}
		}
	}
	
	public void addRelationDoNotOverwrite(Node otherNode)
	{
		for (Node rNode : guiView.relations)
		{
			if (rNode.getNodeName().equals(otherNode.getNodeName()))
			{
				return;
			}
		}
		guiView.relations.add(otherNode);
	}
	
}
