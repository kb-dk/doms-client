package dk.statsbiblioteket.doms.client.datastreams;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.util.xml.DOM;
import org.apache.tuscany.sdo.api.SDOUtil;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DOMSXmlDocument {


    private XMLDocument sdoXmlDocument = null;
    private DOMSXmlElement rootDomsXmlElement = null;
    private boolean isValid = false;
    private boolean isAbstract = false;

    private HelperContext sdoContext;
    //The SDO types based on the XML Schema. Build in the ContentModel. The DataObjects use these types when parsing an
    //XML document that is an instance of the XML Schema.
    private List<Type> sdoTypes;

    public DOMSXmlDocument() {
    }

    /**
     * @param sdoXmlDocument the sdoXmlDocument to set
     */
    public void setSdoXmlDocument(XMLDocument sdoXmlDocument) {
        this.sdoXmlDocument = sdoXmlDocument;
    }

    /**
     * @return the sdoXmlDocument
     */
    public XMLDocument getSdoXmlDocument() {
        return sdoXmlDocument;
    }

    /**
     * @param rootDomsXmlElement the rootDomsXmlElement to set
     */
    public void setRootDomsXmlElement(DOMSXmlElement rootDomsXmlElement) {
        this.rootDomsXmlElement = rootDomsXmlElement;
    }

    /**
     * @return the rootDomsXmlElement
     */
    public DOMSXmlElement getRootDomsXmlElement() {
        return rootDomsXmlElement;
    }

    /**
     * @return the isValid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * @return the isAbstract
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * @param sdoContext the sdoContext to set
     */
    public void setSdoContext(HelperContext sdoContext) {
        this.sdoContext = sdoContext;
    }

    /**
     * @return the sdoContext
     */
    public HelperContext getSdoContext() {
        return sdoContext;
    }

    /**
     * @param sdoTypes the sdoTypes to set
     */
    public void setSdoTypes(List<Type> sdoTypes) {
        this.sdoTypes = sdoTypes;
    }

    /**
     * @return the sdoTypes
     */
    public List<Type> getSdoTypes() {
        return sdoTypes;
    }

    public void generate(DatastreamDeclaration compositeSchema)
            throws IOException, ServerOperationFailed, MyXMLWriteException {
        sdoContext = SDOUtil.createHelperContext(true);
        InputStream is = null;

        try {

                String is2 = compositeSchema.getSchema().getContents();


                //Since we do not have an instance of the XML Schema we do not know the type of the root element.
                //So we first read the schema manually to find out the targetNamespace.
                String targetNamespace = null;
                XmlSchemaWithResolver doc = new XmlSchemaWithResolver();
                if (doc.Load(is)) {
                    if (doc.getDocNode() != null) {
                        targetNamespace = getSchemaTargetNamespace(doc.getDocNode());
                    }
                }

                if (targetNamespace == null) {
                    return;
                }

                //Define types based on the XML Schema
                setSdoTypes(defineTypes(sdoContext,  doc));
                if (getSdoTypes() != null) {
                    Property rootProperty = getRootProperty(getSdoTypes(), targetNamespace, sdoContext.getXSDHelper());
                    Type rootType = rootProperty.getType();
                    isAbstract = rootType.isAbstract();
                    isValid = !isAbstract;
                }

        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                is.close();
            } catch (Exception e) {
               // logger.warn("DOMSXmlDocument.generate. Exception-3. " + e);
            }
        }

    }

    public void load(
                     InputStream is) throws IOException,  ServerOperationFailed {


        try {
            //For XSD validation. Does not seem to work
            //Map options = new HashMap();
            //options.put(SDOHelper.XMLOptions.XML_LOAD_SCHEMA, Boolean.TRUE);
            //String schemaurl = compositeSchema.getObject() + "/" + compositeSchema.getDatastream();


            //Load the XML document

            setSdoXmlDocument(sdoContext.getXMLHelper().load(is));
            //if we want to use XSD validation. setSdoXmlDocument(sdoContext.getXMLHelper().load(is, schemaurl, options));

            //Get the root data object
            DataObject rootDataObject = getSdoXmlDocument().getRootObject();

            Type rootType = rootDataObject.getType();
            String targetNamespace = rootType.getURI();

            isAbstract = rootType.isAbstract();

            Property rootProperty = getRootProperty(sdoTypes,
                    targetNamespace,
                    sdoContext.getXSDHelper());

            if (rootProperty == null) {
                throw new ServerOperationFailed("Could not get a valid SDO root DataObject for Input Stream: '" + is + "'.");
            }

            setSdoXmlDocument(sdoContext.getXMLHelper().createDocument(rootDataObject, targetNamespace, rootProperty.getName()));

            DOMSXmlElement root = new DOMSXmlElement(this, null, rootDataObject, rootProperty);
            root.setLabel(rootProperty.getName());

            setRootDomsXmlElement(root);
            List<Property> containedProps = rootType.getProperties();
            for (Iterator<Property> i = containedProps.iterator(); i.hasNext(); ) {
                Property property = (Property) i.next();
                handleProperty(sdoContext, getRootDomsXmlElement(), rootDataObject, property);
            }


        } finally {
            try {
                is.close();
            } catch (Exception e) {
                //logger.warn("DOMSXmlDocument.load. Exception-2 in DOMSXmlDocument.generate. " + e);
            }
        }
    }

    public ByteArrayOutputStream save() throws IOException {
        if ((getRootDomsXmlElement() != null) && (getSdoXmlDocument() != null) && (sdoContext != null)) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            getRootDomsXmlElement().submit(sdoContext);

            //Before we serialize the xml document we make a copy of the xml documennt and deletes empty data
            //objects from the copy before serializing the copy.
            DataObject rootCopy = sdoContext.getCopyHelper().copy(getSdoXmlDocument().getRootObject());
            Type rootType = rootCopy.getType();
            Property rootProperty = getRootProperty(sdoTypes, rootType.getURI(), sdoContext.getXSDHelper());
            XMLDocument docCopy = sdoContext.getXMLHelper().createDocument(rootCopy, rootType.getURI(), rootProperty.getName());

            SdoDataObjectUtils utils = new SdoDataObjectUtils();
            utils.handleDataObject(sdoContext, null, rootCopy, rootProperty);
            utils.doDelete();
            sdoContext.getXMLHelper().save(docCopy, os, null);

            return os;
        }
        return null;
    }

    /**
     * This method parse the given compositeSchema to determine the sdo type list of the
     * schema reference. It assumed a structure somewhat like
     * <datastream object="pid" value="DS_SCHEMA"/>
     *
     * @param context
     * @param compositeSchema
     * @return
     * @throws RemoteException
     * @throws IOException
     * @throws MethodFailedException
     * @throws InvalidResourceException
     */
    private List<Type> defineTypes(HelperContext context,

                                   XmlSchemaWithResolver compositeSchema) throws ServerOperationFailed, TransformerException {
        List<Type> types = null;

        types = context.getXSDHelper().define(DOM.domToString(compositeSchema.getDocNode()));

        return types;
    }

    private void handleProperty(HelperContext helperContext,
                                final DOMSXmlElement parent, final DataObject dataObject,
                                final Property property) {
        /*System.out.println("\n\nhandleProperty. property = " + property.getName() + ". dataObject.getType().getName() = "
                  + dataObject.getType().getName() + ". property.isOpenContent() = " + property.isOpenContent()
                  + ". dataObject.getType().isSequenced()" + dataObject.getType().isSequenced() + "."); */


        if (property.isContainment() && !property.getType().isDataType()) {
            List<DataObject> childObjects;
            if (dataObject.isSet(property)) {
                childObjects = new ArrayList<DataObject>(1);
                if (property.isMany()) {
                    List<DataObject> list = dataObject.getList(property);
                    if (list != null) {
                        for (Iterator<DataObject> i = list.iterator(); i.hasNext(); ) {
                            childObjects.add((DataObject) i.next());
                        }
                    }
                } else {
                    childObjects.add(dataObject.getDataObject(property));
                }
            } else {
                // if we are traversing hierarchy without an xml data instance
                // we create an empty placeholder
                childObjects = new ArrayList<DataObject>(1);
                childObjects.add(dataObject.createDataObject(property));
            }

            for (DataObject childObject : childObjects) {
                Type type = property.getType();
                List<Property> containedProps = type.getProperties();
                boolean onlyAttributes = true;
                for (Iterator<Property> i = containedProps.iterator(); i.hasNext(); ) {
                    Property childProperty = (Property) i.next();
                    if (!helperContext.getXSDHelper().isAttribute(childProperty)) {
                        onlyAttributes = false;
                        break;
                    }
                }
                if ((containedProps.size() > 0) && (!onlyAttributes)) {
                    DOMSXmlElement child = new DOMSXmlElement(this, parent, childObject, property);
                    child.setLabel(property.getName());
                    parent.add(child);
                    for (Iterator<Property> i = containedProps.iterator(); i.hasNext(); ) {
                        Property childProperty = (Property) i.next();
                        if (!helperContext.getXSDHelper().isAttribute(childProperty)) {
                            handleProperty(helperContext, child, childObject, childProperty);
                        }
                    }
                } else {
                    // Leaf xml element
                    DOMSXmlElement leafRoot = null;
                    Object value = null;
                    int sequenceIndex = -1;
                    if (dataObject.isSet(property)) {
                        if (property.getType().isSequenced()) {
                            if (helperContext.getXSDHelper().isMixed(property.getType())) {
                                Sequence seq = childObject.getSequence();
                                if (seq != null) {
                                    if (seq.size() > 1) {
                                        throw new RuntimeException("We have a sequenced with more than one value. What to do? Container property = "
                                                + property.getName());
                                    }
                                    for (int i = 0; i < seq.size(); i++) {

                                        Property p = seq.getProperty(i);
                                        if (p == null) {
                                            value = seq.getValue(i);
                                            sequenceIndex = i;
                                        } else {
                                            throw new RuntimeException("We have a sequenced dataobject with internal properties. What to do? Container property = "
                                                    + property.getName() + ". Internal property = " + p.getName() + ". Value = " + seq.getValue(i));

                                        }
                                    }
                                }
                            }
                        } else {
                            value = dataObject.get(property);
                        }
                    }
                    leafRoot = generateLeaf(helperContext, parent, childObject, property, value, sequenceIndex);
                    parent.add(leafRoot);
                }
            }
        } else {
            if (!helperContext.getXSDHelper().isAttribute(property)) {
                // Leaf xml element
                DOMSXmlElement leafRoot = null;
                Object value = null;
                if (property.isMany()) {
                    if (dataObject.isSet(property)) {
                        List values = dataObject.getList(property);
                        for (int v = 0, count = values.size(); v < count; v++) {
                            value = values.get(v);
                            leafRoot = generateLeaf(helperContext, parent, dataObject, property, value, v);
                            parent.add(leafRoot);
                        }
                    } else {
                        leafRoot = generateLeaf(helperContext, parent, dataObject, property, value, 0);
                        parent.add(leafRoot);
                    }
                } else {
                    if (dataObject.isSet(property)) {
                        value = dataObject.get(property);
                    }
                    leafRoot = generateLeaf(helperContext, parent, dataObject, property, value, -1);
                    parent.add(leafRoot);
                }
            }
        }
    }

    private DOMSXmlElement generateLeaf(
            final HelperContext helperContext, DOMSXmlElement parent,
            final DataObject dataObject, final Property property, Object value, int sequenceIndex) {
        DOMSXmlElement newLeaf = new DOMSXmlElement(this, parent, dataObject, property);
        newLeaf.setValue(value);
        newLeaf.setLabel(property.getName());
        newLeaf.setIndex(sequenceIndex);
        try {
            List<String> valueEnum = (List<String>) SDOUtil.getEnumerationFacet(property.getType());
            newLeaf.setValueEnum(valueEnum);
        } catch (NullPointerException e) {
            /* TODO: Is there a way to check that the property has an enumerationFacet?
                * There is an issue with SDO, were it will generate a nullpointer exception
                * if there is no enumeration for the property.
               */
        }

        return newLeaf;
    }

    private String getSchemaTargetNamespace(Node schema) {
        NamedNodeMap attrs = schema.getAttributes();
        if (attrs != null) {
            Node attr = attrs.getNamedItem("targetNamespace");
            if (attr != null) {
                return attr.getNodeValue();
            }
        }
        return null;
    }

    private Property getRootProperty(List<Type> types, String targetNamespace, XSDHelper xsdHelper) {
        List<Type> relevantTypes = new ArrayList<Type>();
        for (Type type : types) {
            if (type.getName().equals("DocumentRoot")) {
                if ((targetNamespace == null) || (type.getURI().equals(targetNamespace))) {
                    collectTypes(type, relevantTypes, xsdHelper);
                }
            }
        }

        Property rootProperty = null;

        for (Type type : relevantTypes) {
            if (type.getName().equals("DocumentRoot")) {
                for (Property property : (List<Property>) type.getProperties()) {
                    if (xsdHelper.isElement(property)) {
                        rootProperty = property;
                        break;
                    }
                }
                break;
            }
        }

        return rootProperty;
    }

    private void collectTypes(Type type, List<Type> types, XSDHelper xsdHelper) {
        if (!types.contains(type)) {
            types.add(type);
            for (Property property : (List<Property>) type.getProperties()) {
                collectTypes(property.getType(), types, xsdHelper);
            }
        }
    }


}
