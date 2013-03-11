package dk.statsbiblioteket.doms.client.impl.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.InternalDatastream;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import dk.statsbiblioteket.util.xml.DOM;
import org.apache.tuscany.sdo.api.SDOUtil;
import org.apache.tuscany.sdo.impl.DataObjectImpl;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SDOParsedXmlDocumentImpl implements SDOParsedXmlDocument {


    private XMLDocument sdoXmlDocument = null;
    private SDOParsedXmlElementImpl rootSDOParsedXmlElement = null;
    private boolean isValid = false;
    private boolean isAbstract = false;

    private HelperContext sdoContext;

    private Datastream datastream;

    //The SDO types based on the XML Schema. Build in the ContentModel. The DataObjects use these types when parsing an
    //XML document that is an instance of the XML Schema.
    private List<Type> sdoTypes = new ArrayList<Type>();

    public SDOParsedXmlDocumentImpl(DatastreamDeclaration next,Datastream datastream)
            throws ServerOperationFailed, XMLParseException {
        generate(next);
        load(datastream);
        this.datastream = datastream;

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
     * @param rootSDOParsedXmlElement the rootSDOParsedXmlElement to set
     */
    public void setRootSDOParsedXmlElement(SDOParsedXmlElementImpl rootSDOParsedXmlElement) {
        this.rootSDOParsedXmlElement = rootSDOParsedXmlElement;
    }

    /**
     * @return the rootSDOParsedXmlElement
     */
    @Override
    public SDOParsedXmlElementImpl getRootSDOParsedXmlElement() {
        return rootSDOParsedXmlElement;
    }

    /**
     * @return the isValid
     */
    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * @return the isAbstract
     */
    @Override
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

    private void generate(DatastreamDeclaration compositeSchema)
            throws  ServerOperationFailed, XMLParseException {
        if (sdoContext == null){
            sdoContext = SDOUtil.createHelperContext(true);
        }

        if (compositeSchema.getSchema() != null){
            String is2 = compositeSchema.getSchema().getContents();


            //Since we do not have an instance of the XML Schema we do not know the type of the root element.
            //So we first read the schema manually to find out the targetNamespace.
            String targetNamespace = null;
            XmlSchemaWithResolver doc = new XmlSchemaWithResolver();
            if (doc.load(new ByteArrayInputStream(is2.getBytes()))) {
                if (doc.getDocNode() != null) {
                    targetNamespace = getSchemaTargetNamespace(doc.getDocNode());
                }
            }

/*
            if (targetNamespace == null) {
                return;
            }
*/

            //Define types based on the XML Schema
            try {
                setSdoTypes(defineTypes(sdoContext,  doc));
            } catch (TransformerException e) {
                throw new XMLParseException("Failed to parse the types",e);
            }
            if (getSdoTypes() != null) {
                Property rootProperty = getRootProperty(getSdoTypes(), null, targetNamespace, sdoContext.getXSDHelper());
                Type rootType = rootProperty.getType();
                isAbstract = rootType.isAbstract();
                isValid = !isAbstract;
            }
        }


    }

    private void load(Datastream datastream) throws ServerOperationFailed {



        //For XSD validation. Does not seem to work
        //Map options = new HashMap();
        //options.put(SDOHelper.XMLOptions.XML_LOAD_SCHEMA, Boolean.TRUE);
        //String schemaurl = compositeSchema.getObject() + "/" + compositeSchema.getDatastream();


        //Load the XML document


        String contents = datastream.getContents();


        setSdoXmlDocument(sdoContext.getXMLHelper().load(datastream.getContents()));
        //if we want to use XSD validation. setSdoXmlDocument(sdoContext.getXMLHelper().load(is, schemaurl, options));



        //Get the root data object


        DataObject rootDataObject = getSdoXmlDocument().getRootObject();

        Type rootType = rootDataObject.getType();
        String targetNamespace = rootType.getURI();

        isAbstract = rootType.isAbstract();

        Property rootProperty = getRootProperty(sdoTypes,
                rootType,
                targetNamespace,
                sdoContext.getXSDHelper());

        if (rootProperty == null) {
            for (Type sdoType : sdoTypes) {
                if (sdoType.getName().equals("DocumentRoot")){
                    targetNamespace = sdoType.getURI();
                    //First element declaration
                    rootProperty = (Property) sdoType.getDeclaredProperties().get(0);
                    rootDataObject = sdoContext.getDataFactory().create(targetNamespace,rootProperty.getName());

                }
            }
            if (rootProperty == null){
                throw new ServerOperationFailed("Could not get a valid SDO root DataObject for Datastream: '" + datastream.getId() + "'.");
            }
        }

        setSdoXmlDocument(sdoContext.getXMLHelper().createDocument(rootDataObject, targetNamespace, rootProperty.getName()));

        SDOParsedXmlElementImpl root = new SDOParsedXmlElementImpl(this, null, rootDataObject, rootProperty);
        root.setLabel(rootProperty.getName());

        setRootSDOParsedXmlElement(root);
        List containedProps = rootProperty.getType().getDeclaredProperties();
        for (Iterator i = containedProps.iterator(); i.hasNext(); ) {
            Object tmp = i.next();
            if (tmp instanceof Property) {
                Property property = (Property) tmp;
                handleProperty(sdoContext, getRootSDOParsedXmlElement(), rootDataObject, property);
            }
        }


    }

    @Override
    public String dumpToString() throws XMLParseException {
        if ((getRootSDOParsedXmlElement() != null) && (getSdoXmlDocument() != null) && (sdoContext != null)) {
            Writer writer = new StringWriter();
            getRootSDOParsedXmlElement().submit(sdoContext);

            //Before we serialize the xml document we make a copy of the xml documennt and deletes empty data
            //objects from the copy before serializing the copy.
            DataObject rootCopy = sdoContext.getCopyHelper().copy(getSdoXmlDocument().getRootObject());
            Type rootType = rootCopy.getType();
            Property rootProperty = getRootProperty(sdoTypes, rootType, rootType.getURI(), sdoContext.getXSDHelper());


            SdoDataObjectUtils utils = new SdoDataObjectUtils();
            utils.handleDataObject(sdoContext, null, rootCopy, rootProperty);
            utils.doDelete();


            try {
                XMLDocument docCopy = sdoContext.getXMLHelper().createDocument(rootCopy, rootType.getURI(), rootType.getName());
                sdoContext.getXMLHelper().save(docCopy, writer, null);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException("String writer failed to write...",e);
            }

            return writer.toString();

        }
        return null;
    }

    @Override
    public void saveToDatastream() throws XMLParseException {
        if (datastream instanceof InternalDatastream) {
            InternalDatastream internalDatastream = (InternalDatastream) datastream;
            internalDatastream.replace(dumpToString());
        } else {
            throw new IllegalAccessError("You should not attempt to save SDO to an external datastream");
        }
    }

    @Override
    public Datastream getDatastream() {
        return datastream;
    }


    /**
     * This method parse the given compositeSchema to determine the sdo type list of the
     * schema reference. It assumed a structure somewhat like
     * <datastream object="pid" value="DS_SCHEMA"/>
     *
     * @param context
     * @param compositeSchema
     * @return
     * @throws IOException
     * @throws MethodFailedException
     * @throws InvalidResourceException
     */
    private List<Type> defineTypes(HelperContext context,

                                   XmlSchemaWithResolver compositeSchema) throws ServerOperationFailed, TransformerException {
        List<Type> types;

        types = context.getXSDHelper().define(DOM.domToString(compositeSchema.getDocNode()));

        return types;
    }

    private void handleProperty(HelperContext helperContext,
                                final SDOParsedXmlElement parent, final DataObject dataObject,
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
                    SDOParsedXmlElement child = new SDOParsedXmlElementImpl(this, parent, childObject, property);
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
                    SDOParsedXmlElement leafRoot = null;
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
                SDOParsedXmlElement leafRoot = null;
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

    private SDOParsedXmlElement generateLeaf(
            final HelperContext helperContext, SDOParsedXmlElement parent,
            final DataObject dataObject, final Property property, Object value, int sequenceIndex) {
        SDOParsedXmlElementImpl newLeaf = new SDOParsedXmlElementImpl(this, parent, dataObject, property);
        newLeaf.setValue(value);
        newLeaf.setOriginallySet(value != null);
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

    private Property getRootProperty(List<Type> types, Type requiredType, String targetNamespace, XSDHelper xsdHelper) {
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
                        if (requiredType == null || property.getType().getName().equals(requiredType.getName())) {
                            rootProperty = property;
                            break;
                        }
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
