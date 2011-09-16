package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.datastreams.DOMSDataStreamCompositeModelTypeModelExtension;
import dk.statsbiblioteket.doms.client.util.Constants;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class representing the GUI extention to the DS-COMPOSITE-MODEL datastream in content models.
 * The only information is the presentAs enum.
 */
public class DOMSDataStreamCompositeModelTypeModelExtensionGui extends DOMSDataStreamCompositeModelTypeModelExtension {
    private Constants.GuiRepresentation presentAs;

    public DOMSDataStreamCompositeModelTypeModelExtensionGui() {
        setId(Constants.EXTENSIONS_GUI);
        presentAs = Constants.GuiRepresentation.invisible;
    }

    public void parse(Node node) {

        NodeList childNodes = node.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            switch (childNode.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if (childNode.getLocalName().equals("presentAs")){
                        NamedNodeMap attrs = childNode.getAttributes();
                        if (attrs != null) {
                            Node attr;
                            attr = attrs.getNamedItem("type");
                            if (attr != null) {
                                setPresentAs(Constants.GuiRepresentation.valueOf(attr.getNodeValue()));
                            }
                        }
                    }
            }
        }
        setParsed(true);
    }


    public void setPresentAs(Constants.GuiRepresentation presentAs) {
        this.presentAs = presentAs;
    }

    public Constants.GuiRepresentation getPresentAs() {
        return presentAs;
    }
}
