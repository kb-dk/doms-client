package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DOMSDigitalObject;
import dk.statsbiblioteket.doms.client.util.Constants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



import java.util.*;

/**
 * This is the DS-COMPOSITE-MODEL datastream in a content model. It is a map of
 * the described type models.
 */
public class DOMSDataStreamCompositeModel extends DOMSDataStream {

    private Map<String, DOMSDataStreamCompositeModelTypeModel> models;



    /**
     * Constructor. Sets the id of this datastream to Constants.DS_COMPOSITE_MODEL_ID and
     * sets the content model to myDomsContentModel
     * @param myDomsContentModel the content model to associate with this datastream
     */
    public DOMSDataStreamCompositeModel(DOMSDigitalObject myDomsContentModel)
    {
        super(myDomsContentModel);
        setId(Constants.DS_COMPOSITE_MODEL_ID);
        models = new HashMap<String, DOMSDataStreamCompositeModelTypeModel>();
    }


    /**
     * Merges the given ds comp datastream with this one. As this method have
     * void return, the merging updates this object, not the given one.
     * <p>
     * The merging process basically runs through each Type model defined in
     * parentModels. If this do not have a type model with the same id, the
     * one from parentModels are copied. If this do have one, then it is assumed
     * to overwrite the one from parent id.
     * <p> For extensions, if this have the type model, but without the extension
     * the extension is copied from the type model in parent models. If this do
     * have the extension, it is assumed to overwrite.
     *
     * @param parentModels The datastream to merge from
     */
    public void mergeCompositeModels(DOMSDataStreamCompositeModel parentModels)
    {
        //for each datastream defined in the parents
        for (DOMSDataStreamCompositeModelTypeModel parentModel : parentModels.values()){

            //find our own version
            DOMSDataStreamCompositeModelTypeModel model = this.get(parentModel.getId());
            if (model==null){//we do not have our own version
                models.put(parentModel.getId(),parentModel); //so get the one from the parents
            } else { //we have a version ourselves
                //So ours are used, but do we have all the extensions defined?

                //the extensions from the parent
                List<DOMSDataStreamCompositeModelTypeModelExtension> extensions = parentModel.getExtensions();

                for (DOMSDataStreamCompositeModelTypeModelExtension ext: extensions){//for each of these
                    DOMSDataStreamCompositeModelTypeModelExtension ext_local = model.getExtensionByID(ext.getId());
                    if (ext_local == null){//we do not have this extension
                        model.put(ext.getId(),ext);
                    }

                }
            }
        }
    }


    /**
     * Reads a binding from an XML DOM Node.
     *
     * @param node the XML Dom Node.
     */
    public void parse(Node node) throws ServerOperationFailed {
        if (node == null)
            return;

        if (node.getNodeType() != Node.ELEMENT_NODE)
            return;

        if (node.getLocalName().equals("dsCompositeModel")) {

            NodeList childNodes = node.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                switch (childNode.getNodeType()) {
                    case Node.ELEMENT_NODE:
                        String childNodeName = childNode.getLocalName();
                        if (childNodeName.equals("dsTypeModel")) {
                            DOMSDataStreamCompositeModelTypeModel dsTypeModel =
                                    new DOMSDataStreamCompositeModelTypeModel(this.getOwningDigitalObject().getPid());
                            dsTypeModel.parse(childNode);
                            models.put(dsTypeModel.getId(),dsTypeModel);
                        }
                }
            }
        }
    }


    public int size() {
        return models.size();
    }

    public boolean isEmpty() {
        return models.isEmpty();
    }

    public boolean containsKey(Object key) {
        return models.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return models.containsValue(value);
    }

    public DOMSDataStreamCompositeModelTypeModel get(Object key) {
        return models.get(key);
    }

    public DOMSDataStreamCompositeModelTypeModel put(String key, DOMSDataStreamCompositeModelTypeModel value) {
        return models.put(key, value);
    }

    public DOMSDataStreamCompositeModelTypeModel remove(Object key) {
        return models.remove(key);
    }

    public void putAll(Map<? extends String, ? extends DOMSDataStreamCompositeModelTypeModel> m) {
        models.putAll(m);
    }

    public void clear() {
        models.clear();
    }

    public Set<String> keySet() {
        return models.keySet();
    }

    public Collection<DOMSDataStreamCompositeModelTypeModel> values() {
        return models.values();
    }

}
