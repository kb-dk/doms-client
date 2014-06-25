package dk.statsbiblioteket.doms.client.impl.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.HelperContext;
import dk.statsbiblioteket.doms.client.exceptions.XMLParseException;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import org.apache.tuscany.sdo.api.SDOUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SDOParsedXmlElementImpl implements SDOParsedXmlElement {

    /**
     * Generally, we remove empty elements and attributes from the SDO tree before we write them back to DOMS. We
     * also, however, need to be able to remove attributes by setting previously non-empty values to empty in the
     * GUI. These two placeholders enable this behaviour as follows.
     *
     * When an instance of this class is created, it is flagged with "originallySetNonEmpty" and with "originallySet".
     * This occurs in SDOParsedXmlDocumentImpl.addLeaf() .
     * When the element comes back from the GUI, if it is empty and was originally empty then it is set to
     * PLACEHOLDER_FOR_EMPTY_STRING . If it is empty having previously been non-empty, then it is set to
     * PLACEHOLDER_FOR_NOW_EMPTY_STRING. If it empty having originally been absent then it is left empty.
     *
     * The logic in SdoDataObjectRemovalUtil.handleSimpleValue() has then sufficient information as to whether
     * the object is to be removed.
     */
    static final String PLACEHOLDER_FOR_EMPTY_STRING = "@UNIQUE_VALUE@92d2bd82bb836e0fc21a44b3cee7bcc0";
    static final String PLACEHOLDER_FOR_NOW_EMPTY_STRING = "@UNIQUE_VALUE@92d2bd82bb836e0fc21a44b3cee7bcc0@NOW";

    protected ArrayList<SDOParsedXmlElement> children = new ArrayList<SDOParsedXmlElement>();
    private SDOParsedXmlDocumentImpl myDocument;
    private Property property;
    private DataObject dataobject;
    private SDOParsedXmlElement parent;
    private String label;
    private Object value;
    private boolean originallySet;
    private boolean originallySetNonEmpty;
    private boolean isHasNonEmptyDescendant;
    private int maxOccurence = -1;
    private int minOccurence = -1;
    /**
     * If the dataobject is sequenced this is the sequence index.
     * If the property is multivalued this is the index in the list.
     */
    private int index = -1;
    private String id = "_" + UUID.randomUUID().toString();
    private GuiType guiType = GuiType.NA;
    private List<String> valueEnum;

    public SDOParsedXmlElementImpl(SDOParsedXmlDocumentImpl myDocument, SDOParsedXmlElement parent,
                                   DataObject dataobject, Property property) {
        this.setDataobject(dataobject);
        this.setProperty(property);
        this.setParent(parent);
        this.setLabel(property.getName());
        this.myDocument = myDocument;
    }

    public SDOParsedXmlElementImpl(SDOParsedXmlDocumentImpl myDocument, SDOParsedXmlElement parent,
                                   DataObject dataobject, Property property, int parentIndex) {
        this.setDataobject(dataobject);
        this.setProperty(property);
        this.setParent(parent);
        this.setLabel(property.getName());
        if (parentIndex < 0) {
            this.getParent().getChildren().add(this);
        } else {
            this.getParent().getChildren().add(parentIndex, this);
        }
        this.myDocument = myDocument;
    }

    public static String getAppInfo(Property property, String source) {
        String appinfo = null;
        org.eclipse.emf.ecore.EModelElement eModelElement;
        org.eclipse.emf.ecore.EAnnotation annotation;
        eModelElement = (org.eclipse.emf.ecore.EModelElement) property;
        annotation = eModelElement.getEAnnotation(source);
        if (annotation != null) {
            appinfo = (String) annotation.getDetails().get("appinfo");
        }
        return appinfo;
    }

    /**
     * @return the helperContext
     */
    public HelperContext getHelperContext() {
        return myDocument.getSdoContext();
    }

    @Override
    public SDOParsedXmlDocument getDocument() {
        return myDocument;
    }

    @Override
    public boolean isEnum() {
        return valueEnum != null;
    }

    @Override
    public boolean isLeaf() {
        if (property.isContainment() && !property.getType().isDataType()) {
            commonj.sdo.Type propType = property.getType();
            if (propType.getProperties() != null) {
                boolean onlyAttributes = true;
                for (Iterator<Property> i = propType.getProperties().iterator(); i.hasNext(); ) {
                    Property childProperty = i.next();
                    if (!getHelperContext().getXSDHelper().isAttribute(childProperty)) {
                        onlyAttributes = false;
                        break;
                    }
                }
                return !((propType.getProperties().size() > 0) && (!onlyAttributes));
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public int getNumberOfOccurences() {

        if (parent == null) {
            return 1;
        }
        int counter = 0;
        List<SDOParsedXmlElement> elems = parent.getChildren();
        for (SDOParsedXmlElement ele : elems) {
            if (ele.getProperty().equals(this.property)) {
                counter++;
            }

        }
        return counter;
    }

    @Override
    public boolean getAddable() {
        //You can create new elements of this type if your property specifies it,
        //and there are less occurences than the maximum specified.
        int amount = getNumberOfOccurences();
        if (this.maxOccurence >= 0 && amount >= this.maxOccurence) {
            //We already have the maximum amount of elements
            return false;
        }
        //true if we are many and we can occur more than one time, or '-1' times, meaning unbounded
        return (this.property.isMany() && (this.maxOccurence > 1 || this.maxOccurence < 0));
    }

    @Override
    public boolean getRemovable() {
        //You can be removed if you are not the only occurence of this type
        //and if there are more occurences than the minimumoccurence.
        //You can NOT be removed if you are the 'original' element
        int amount = getNumberOfOccurences();
        if (amount > 1) {
            if (amount > this.minOccurence) {
                return (!isOriginalElement());
            }
        }
        return false;
    }

    public boolean isOriginallySet() {
        return originallySet;
    }

    public void setOriginallySet(boolean originallySet) {
        this.originallySet = originallySet;
    }

    public boolean isOriginallySetNonEmpty() {
        return originallySetNonEmpty;
    }

    public void setOriginallySetNonEmpty(boolean originallySetNonEmpty) {
        this.originallySetNonEmpty = originallySetNonEmpty;
    }

    @Override
    public boolean isRequired() {
        return this.minOccurence > 0;
    }

    @Override
    public boolean isOriginalElement() {
        if (parent == null) {
            return true;
        }
        List<SDOParsedXmlElement> elems = parent.getChildren();
        for (SDOParsedXmlElement ele : elems) {
            if (ele.getProperty().equals(this.property)) {
                return (ele.getId() == this.id);

            }

        }
        return true;
    }

    @Override
    public boolean isGuiType(String typeName) {
        if (getGuiTypeAsString().equals(typeName)) {
            return true;
        }
        return false;
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public GuiType getGuiType() {
        if (guiType.equals(GuiType.NA)) {
            String source
                    = "http://doms.statsbiblioteket.dk/gui"; // FacesContext.getCurrentInstance().getExternalContext().getInitParameter("appInfoSource");
            String appinfo = getAppInfo(property, source);
            if (appinfo != null) {
                if (appinfo.equals(SDOParsedXmlElementImpl.GuiType.inputfield.toString())) {
                    guiType = SDOParsedXmlElementImpl.GuiType.inputfield;
                } else if (appinfo.equals(SDOParsedXmlElementImpl.GuiType.textarea.toString())) {
                    guiType = SDOParsedXmlElementImpl.GuiType.textarea;
                } else if (appinfo.equals(SDOParsedXmlElementImpl.GuiType.uneditable.toString())) {
                    guiType = SDOParsedXmlElementImpl.GuiType.uneditable;
                } else if (appinfo.equals(SDOParsedXmlElementImpl.GuiType.invisible.toString())) {
                    guiType = SDOParsedXmlElementImpl.GuiType.invisible;
                }

            }
        }
        if ((guiType.equals(GuiType.NA)) && (parent != null)) {
            setGuiType(parent.getGuiType());
        }
        return this.guiType;
    }

    /**
     * @param guiType the guiType to set
     */
    public void setGuiType(GuiType guiType) {
        this.guiType = guiType;
    }

    /**
     * @return the guiType
     */
    @Override
    public String getGuiTypeAsString() {

        if (guiType.equals(GuiType.NA)) {
            getGuiType();
        }
        if ((guiType.equals(GuiType.NA)) && (parent != null)) {
            setGuiType(parent.getGuiType());
        }
        if (guiType.equals(GuiType.NA)) {
            return GuiType.inputfield.toString();
        }
        return guiType.toString();
    }

    /**
     * @return the property
     */
    @Override
    public Property getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(Property property) {
        this.property = property;

        this.minOccurence = SDOUtil.getLowerBound(property);
        this.maxOccurence = SDOUtil.getUpperBound(property);
    }

    /**
     * @return the dataobject
     */
    @Override
    public DataObject getDataobject() {
        return dataobject;
    }

    /**
     * @param dataobject the dataobject to set
     */
    public void setDataobject(DataObject dataobject) {
        this.dataobject = dataobject;
    }

    /**
     * @return the parent
     */
    @Override
    public SDOParsedXmlElement getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(SDOParsedXmlElement parent) {
        this.parent = parent;
    }

    @Override
    public SDOParsedXmlElement create() {
        SDOParsedXmlElementImpl myElem;
        if ((this.property.isMany()) && this.property.getType().isDataType()) {
            List values = this.getDataobject().getList(this.getProperty());
            myElem = new SDOParsedXmlElementImpl(
                    this.myDocument,
                    this.parent,
                    this.getDataobject(),
                    this.property,
                    parent.getChildren().indexOf(this) + 1);
            myElem.setIndex(values.size());
            values.add(null);
        } else {
            DataObject myDo = getDataobject().getContainer().createDataObject(
                    getProperty().getName());
            myElem = new SDOParsedXmlElementImpl(
                    this.myDocument, this.parent, myDo, this.property, parent.getChildren().indexOf(this) + 1);

            if (!isLeaf()) {
                createChildren(myElem);
            }
        }

        return myElem;
    }

    private void createChildren(SDOParsedXmlElement element) {
        for (Object o : element.getDataobject().getType().getProperties()) {
            Property p = (Property) o;
            if (!getHelperContext().getXSDHelper().isAttribute(p)) {
                DataObject childDo;
                if (!p.isContainment()) {
                    childDo = element.getDataobject();
                } else {
                    childDo = element.getDataobject().createDataObject(p);
                }
                SDOParsedXmlElement childElement = new SDOParsedXmlElementImpl(this.myDocument, element, childDo, p);
                element.add(childElement);
                if (!childElement.isLeaf()) {
                    createChildren(childElement);
                }
            }
        }

    }

    @Override
    public void delete() {
        if ((this.property.isMany()) && this.property.getType().isDataType()) {
            List values = this.getDataobject().getList(this.getProperty());
            values.remove(this.getIndex());
            parent.getChildren().remove(this);
            for (SDOParsedXmlElement element : parent.getChildren()) {
                if (element.getIndex() > this.getIndex()) {
                    element.setIndex(element.getIndex() - 1);
                }
            }
        } else {
            parent.getChildren().remove(this);
            dataobject.delete();
        }
    }

    /**
     * @return the label
     */
    @Override
    public String getLabel() {
        if (label != null) {
            return label;
        } else {
            return "Untitled";
        }
    }

    @Override
    public void setLabel(String label) {
        this.label = label.toLowerCase()
                          .replaceFirst(String.valueOf(label.charAt(0)), String.valueOf(label.charAt(0)).toUpperCase());

    }

    /**
     * @return the value
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    @Override
    public void setValue(Object value) {
        this.value = value;
        if (value == null) {
            originallySet = true;
        }

    }

    @Override
    public String getStringValue() {
        if (this.getValue() instanceof String) {
            return (String) this.getValue();
        } else if (this.getValue() instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) this.getValue();
            return bd.toString();
        }

        return null;
    }

    /**
     * @return the index
     */
    @Override
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the children
     */
    @Override
    public ArrayList<SDOParsedXmlElement> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(ArrayList<SDOParsedXmlElement> children) {
        this.children = children;
    }

    @Override
    public void add(SDOParsedXmlElement xmlElement) {
        children.add(xmlElement);
    }

    @Override
    public void remove(SDOParsedXmlElement xmlElement) {
        children.remove(xmlElement);
    }

    @Override
    public boolean isHasNonEmptyDescendant() {
        return isHasNonEmptyDescendant;
    }

    @Override
    public void setIsHasNonEmptyDescendant(boolean b) {
        isHasNonEmptyDescendant = b;
    }

    /**
     * ??? Recursively traverses the tree of sdo elements starting with the current element, filling in the
     * values of all the lead elements. ???
     * @param context
     * @throws XMLParseException
     */
    public void submit(HelperContext context) throws XMLParseException {
        if (isLeaf()) {
            if (this.getValue() != null) {
                if (getProperty().getType().isSequenced()) {
                    if (context.getXSDHelper().isMixed(getProperty().getType())) {
                        Sequence seq = getDataobject().getSequence();
                        if (seq != null) {
                            if (seq.size() == 0) {
                                if (getValue() != null) {
                                    seq.addText((String) getValue());
                                }
                            } else {
                                for (int i = 0; i < seq.size(); i++) {
                                    Property p = seq.getProperty(i);
                                    if (p == null) {
                                        seq.setValue(i, valueToSDOType(context));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (getProperty().getType().getInstanceClass() != null && !getProperty().getType().isAbstract()) {
                        Object value;
                        try {
                            if (this.property.getType().isAbstract()) {
                                value = null;
                            } else  {
                                value = SDOUtil.createFromString(this.getProperty().getType(), valueToSDOType(context));
                            }

                        } catch (IllegalArgumentException e) {
                            if (this.getValue().toString().isEmpty() && !isRequired()) {
                                value = "";
                            } else {
                                throw new XMLParseException(
                                        "Failed to parse the value '" + this.getValue() + "' of field " + this.getLabel() + " as a " + this
                                                .getProperty()
                                                .getType()
                                                .getName(), e
                                );
                            }
                        }
                        if (value.toString().isEmpty()) {
                            if (this.isOriginallySet()) {
                                if (this.isOriginallySetNonEmpty()) {
                                    value = PLACEHOLDER_FOR_NOW_EMPTY_STRING;
                                } else {
                                    value = PLACEHOLDER_FOR_EMPTY_STRING;
                                }
                            }
                        }

                        if (this.getProperty().isMany()) {
                            //TODO here is the list bug. Please find out how to correctly add to lists
                            getDataobject().getList(this.getProperty());
                            List values = this.getDataobject().getList(this.getProperty());
                            if (getIndex() + 1 > values.size()) {
                                values.add(value);
                            } else {
                                values.set(getIndex(), value);
                            }
                        } else {
                            if (value.toString().isEmpty() && !isRequired()) {
                                this.getDataobject().unset(this.getProperty());
                            } else {
                                try {
                                    this.getDataobject().set(this.getProperty(), value);
                                } catch (ClassCastException e) {
                                    throw new XMLParseException(
                                            "Failed to parse the value '" + this.getValue() + "' of field " + this.getLabel() + " as a " + this
                                                    .getProperty()
                                                    .getType()
                                                    .getName(), e
                                    );
                                }
                            }
                        }
                    }
                }

            }
        } else {// end isLeaf
            for (SDOParsedXmlElement xmlElement : children) {
                if (xmlElement instanceof SDOParsedXmlElementImpl) {
                    SDOParsedXmlElementImpl element = (SDOParsedXmlElementImpl) xmlElement;
                    element.submit(context);
                }
            }
        }
    }

    @Override
    public String toString() {
        return getLabel();
    }

    private String valueToSDOType(HelperContext context) {
        String result = "";
        String typeName = this.property.getType().getName();
        if (typeName.equals("YearMonthDay")) {
            if (this.getValue() instanceof String) {
                result = (String) this.getValue();
            } else if (this.getValue() instanceof Date) {
                result = context.getDataHelper().toYearMonthDay((Date) this.getValue());
            }
        } else {
            result = getValue().toString();
        }

        return result;
    }

    public int getMaxOccurence() {
        return maxOccurence;
    }

    public void setMaxOccurence(int maxOccurence) {
        this.maxOccurence = maxOccurence;
    }

    public int getMinOccurence() {
        return minOccurence;
    }

    public void setMinOccurence(int minOccurence) {
        this.minOccurence = minOccurence;
    }

    public List<String> getValueEnum() {
        return valueEnum;
    }

    public void setValueEnum(List<String> valueEnum) {
        this.guiType = GuiType.enumeration;
        this.valueEnum = valueEnum;
    }

}
