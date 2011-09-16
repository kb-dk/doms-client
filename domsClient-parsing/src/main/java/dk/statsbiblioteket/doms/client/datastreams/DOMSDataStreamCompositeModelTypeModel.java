package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.Parsable;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.MyXMLParseException;
import dk.statsbiblioteket.doms.client.objects.MyXMLWriteException;
import dk.statsbiblioteket.doms.client.sdo.DOMSXmlDocument;
import dk.statsbiblioteket.doms.client.util.Constants;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.io.IOException;


/**
 * Represents one entry in the ds-comp datastream. One entry specified the
 * existence of one datastream, and possible the mimetype and format URI.
 * In addition, a number of extensions can be defined.
 * <p> This class is in fact a map of these named extensions.
 */
public class DOMSDataStreamCompositeModelTypeModel extends Parsable {



    /**
     * The name of the datastream
     */
    private String id;

    /**
     * The allowed mimetypes for the datastream.
     */
    private List<String> mimetypes;

    /**
     * The allowed formatURIs for the datastream
     */
    private List<String> formatURIs;

    private Map<String, DOMSDataStreamCompositeModelTypeModelExtension> extensions;
    private String contentModelpid;

    public DOMSDataStreamCompositeModelTypeModel(String contentModelpid)
    {
        this.contentModelpid = contentModelpid;

        mimetypes = new ArrayList<String>();
        formatURIs = new ArrayList<String>();
        extensions = new HashMap<String, DOMSDataStreamCompositeModelTypeModelExtension>();
    }

    public List<DOMSDataStreamCompositeModelTypeModelExtension> getExtensions(){
        return new ArrayList<DOMSDataStreamCompositeModelTypeModelExtension>(extensions.values());
    }

    public DOMSDataStreamCompositeModelTypeModelExtension getExtensionByID(String id){
        return extensions.get(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMimetypes() {
        return mimetypes;
    }

    public void setMimetypes(List<String> mimetypes) {
        this.mimetypes = mimetypes;
    }

    public List<String> getFormatURIs() {
        return formatURIs;
    }

    public void setFormatURIs(List<String> formatURIs) {
        this.formatURIs = formatURIs;
    }

    /**
     * Reads a dsTypeModel from an XML DOM Node.
     *
     * @param node the XML Dom Node.
     */
    public void parse(Node node) throws ServerOperationFailed {
        if (node == null)
            return;

        if (node.getNodeType() != Node.ELEMENT_NODE)
            return;

        if (node.getLocalName().equals("dsTypeModel")) {

            NamedNodeMap attrs;

            attrs = node.getAttributes();
            if (attrs != null) {
                Node attr;
                attr = attrs.getNamedItem("ID");
                if (attr != null) {
                    setId(attr.getNodeValue());
                }
            }

            NodeList childNodes = node.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap childattrs = childNode.getAttributes();

                    String childNodeName = childNode.getNodeName();

                    if (childNodeName.equals("form")) {
                        parseForm(childNode);
                    }
                    else if (childNodeName.equals("extension")) {
                        attrs = childNode.getAttributes();
                        if (attrs != null) {
                            Node attr;
                            attr = attrs.getNamedItem("name");
                            if (attr != null) {
                                if (attr.getNodeValue().equals(Constants.EXTENSIONS_GUI))
                                {
                                    DOMSDataStreamCompositeModelTypeModelExtension ext = new DOMSDataStreamCompositeModelTypeModelExtensionGui();
                                    ext.parse(childNode);
                                    extensions.put(Constants.EXTENSIONS_GUI,ext);

                                }
                                else if (attr.getNodeValue().equals(Constants.EXTENSIONS_SCHEMA))
                                {
                                    DOMSDataStreamCompositeModelTypeModelExtensionSchema ext = new DOMSDataStreamCompositeModelTypeModelExtensionSchema(contentModelpid);
                                    ext.parse(childNode);

                                    ext.setDomsXmlDocument(new DOMSXmlDocument());
                                    try { //BIG TODO
                                        //this changes the xml document in ext. Looks insane, but works
                                        ext.getDomsXmlDocument().generate(ext); //only invocation of this url
                                    } catch (IOException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    } catch (MyXMLWriteException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    } catch (MyXMLParseException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }

                                    extensions.put(Constants.EXTENSIONS_SCHEMA,ext);

                                }
                            }
                        }
                    }
                }
            }
        }
        setParsed(true);
    }


    /**
     * Reads a binding from an XML DOM Node.
     *
     * @param node the XML Dom Node.
     */
    private void parseForm(Node node) {
        if (node == null)
            return;

        if (node.getNodeType() != Node.ELEMENT_NODE)
            return;

        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {
            Node attr = attrs.getNamedItem("MIME");
            if (attr != null) {
                mimetypes.add(attr.getNodeValue());

            }
            attr = attrs.getNamedItem("FORMAT_URI");
            if (attr != null) {
                formatURIs.add(attr.getNodeValue());
            }
        }
    }

    public int size() {
        return extensions.size();
    }

    public boolean isEmpty() {
        return extensions.isEmpty();
    }

    public boolean containsKey(Object key) {
        return extensions.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return extensions.containsValue(value);
    }

    public DOMSDataStreamCompositeModelTypeModelExtension get(Object key) {
        return extensions.get(key);
    }

    public DOMSDataStreamCompositeModelTypeModelExtension put(String key, DOMSDataStreamCompositeModelTypeModelExtension value) {
        return extensions.put(key, value);
    }

    public DOMSDataStreamCompositeModelTypeModelExtension remove(Object key) {
        return extensions.remove(key);
    }

    public void putAll(Map<? extends String, ? extends DOMSDataStreamCompositeModelTypeModelExtension> m) {
        extensions.putAll(m);
    }

    public void clear() {
        extensions.clear();
    }

    public Set<String> keySet() {
        return extensions.keySet();
    }

    public Collection<DOMSDataStreamCompositeModelTypeModelExtension> values() {
        return extensions.values();
    }


    public boolean equals(Object o) {
        return extensions.equals(o);
    }

    public int hashCode() {
        return extensions.hashCode();
    }
}
