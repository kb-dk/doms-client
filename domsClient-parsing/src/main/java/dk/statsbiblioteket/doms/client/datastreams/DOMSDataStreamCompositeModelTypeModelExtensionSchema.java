package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.sdo.DOMSXmlDocument;
import dk.statsbiblioteket.doms.client.util.Constants;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class representing the SCHEMA extension to the DS-COMPOSITE-MODEL datastream.
 */
public class DOMSDataStreamCompositeModelTypeModelExtensionSchema extends DOMSDataStreamCompositeModelTypeModelExtension {


    /**
     * The type of schema. Always "datastream" for now
     */
    private String type;

    /**
     * The datastream containing the schema
     */
    private String value;






    private DOMSXmlDocument domsXmlDocument;
    private String object;

    /**
     *
     * @param contentModelpid
     */
    public DOMSDataStreamCompositeModelTypeModelExtensionSchema(String contentModelpid)
    {
        setObject(contentModelpid);
        setId(Constants.EXTENSIONS_SCHEMA);
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }



    public void parse(Node node) {

        NodeList childNodes = node.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            switch (childNode.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if(childNode.getLocalName().equals("reference")){
                        NamedNodeMap attrs = childNode.getAttributes();
                        if (attrs != null) {
                            Node attr;
                            attr = attrs.getNamedItem("type");
                            if (attr != null) {
                                setType(attr.getNodeValue());
                            }
                            attr = attrs.getNamedItem("value");
                            if (attr != null) {
                                setValue(attr.getNodeValue());
                            }
                        }
                    }
            }
        }
        setParsed(true);
    }


    public DOMSXmlDocument getDomsXmlDocument() {
        return domsXmlDocument;
    }

    public void setDomsXmlDocument(DOMSXmlDocument domsXmlDocument) {
        this.domsXmlDocument = domsXmlDocument;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getObject() {
        return object;
    }
}
