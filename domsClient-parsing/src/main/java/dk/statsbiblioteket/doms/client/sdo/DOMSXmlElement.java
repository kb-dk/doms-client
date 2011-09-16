package dk.statsbiblioteket.doms.client.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.HelperContext;
import org.apache.tuscany.sdo.api.SDOUtil;

import java.math.BigDecimal;
import java.util.*;

public class DOMSXmlElement {


	
	public static enum GuiType {
		inputfield, textarea, uneditable, NA, enumeration, invisible
	}

	private DOMSXmlDocument myDocument;
	private Property property;
	private DataObject dataobject;
	private DOMSXmlElement parent;

	private String label;
	private Object value;
	private int maxOccurence = -1;
	private int minOccurence = -1;
	private int index = -1; // If the dataobject is sequenced this is the sequence index. If the property is multivalued this is the index in the list.
	protected ArrayList<DOMSXmlElement> children = new ArrayList<DOMSXmlElement>();

	private String id = "_" + UUID.randomUUID().toString();
	
	private GuiType guiType = GuiType.NA;
	
	private List<String> valueEnum; 
	
	public DOMSXmlElement() {

	}

	public DOMSXmlElement(DOMSXmlDocument myDocument, DOMSXmlElement parent, DataObject dataobject,
			Property property) {
		this.setDataobject(dataobject);
		this.setProperty(property);
		this.setParent(parent);
		this.setLabel(property.getName());
		this.myDocument = myDocument;
	}

	public DOMSXmlElement(DOMSXmlDocument myDocument, DOMSXmlElement parent, DataObject dataobject,
			Property property, int parentIndex) {
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

	/**
	 * @return the helperContext
	 */
	public HelperContext getHelperContext() {
		return myDocument.getSdoContext();
	}
	
	public boolean isEnum() {
		return valueEnum != null;
	}

	public boolean isLeaf() {
		if (property.isContainment() && !property.getType().isDataType()) {
			commonj.sdo.Type propType = property.getType();
			if (propType.getProperties() != null) {
				boolean test;
				boolean onlyAttributes = true;
				for (Iterator<Property> i = propType.getProperties().iterator(); i.hasNext();) {
					Property childProperty = (Property) i.next();
					if (!getHelperContext().getXSDHelper().isAttribute(childProperty)) {
						onlyAttributes = false;
						break;
					} 
				}
				
				//return (propType.getProperties().size() == 0);
				if ((propType.getProperties().size() > 0) && (!onlyAttributes)) {
					test = false;
				}
				else {
					test = true;
				}
				
				return !((propType.getProperties().size() > 0) && (!onlyAttributes));
			} else {
				return true;
			}
		}
		return true;
	}
	
	public int getNumberOfOccurences(){

		if(parent == null) {
			return 1;
		}
		int counter = 0;
		List<DOMSXmlElement> elems = parent.getChildren();		
		for(DOMSXmlElement ele : elems) {
			if(ele.getProperty().equals(this.property)) {
				counter++;
			}
			
		}
		return counter;
	}
	
	public boolean getAddable(){
		//You can create new elements of this type if your property specifies it,
		//and there are less occurences than the maximum specified.
		int amount = getNumberOfOccurences();
		if(this.maxOccurence >=0 && amount >= this.maxOccurence) {
			//We already have the maximum amount of elements
			return false;
		}
		//true if we are many and we can occur more than one time, or '-1' times, meaning unbounded
		return (this.property.isMany() && (this.maxOccurence > 1 || this.maxOccurence < 0));		
	}
	
	public boolean getRemovable(){
		//You can be removed if you are not the only occurence of this type
		//and if there are more occurences than the minimumoccurence.
		//You can NOT be removed if you are the 'original' element
		int amount = getNumberOfOccurences();
		if(amount > 1) {
			if(amount > this.minOccurence) {
				return (!isOriginalElement());
			}
		}
		return false;
	}
	
	public boolean isRequired(){		
		return this.minOccurence > 0;
	}


	public boolean isOriginalElement() {
		if(parent == null) {
			return true;
		}			
		List<DOMSXmlElement> elems = parent.getChildren();		
		for(DOMSXmlElement ele : elems) {
			if(ele.getProperty().equals(this.property)) {
				return (ele.getId() == this.id);
				
			}
			
		}
		return true;
	}
	
	public boolean isGuiType(String typeName){
		if(getGuiTypeAsString().equals(typeName)){
			return true;
		}
		return false;
	}
		
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param guiType the guiType to set
	 */
	public void setGuiType(GuiType guiType) {
		this.guiType = guiType;
	}
	
	
	public GuiType getGuiType() {
		if (guiType.equals(GuiType.NA)) {
			String source = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("appInfoSource");
			String appinfo = DOMSSDOUtil.getAppInfo(property, source);
			if (appinfo!=null) {
				if (appinfo.equals(DOMSXmlElement.GuiType.inputfield.toString())) {
					guiType = DOMSXmlElement.GuiType.inputfield;
				}
				else if (appinfo.equals(DOMSXmlElement.GuiType.textarea.toString())) {
					guiType = DOMSXmlElement.GuiType.textarea;
				}
				else if (appinfo.equals(DOMSXmlElement.GuiType.uneditable.toString())) {
					guiType = DOMSXmlElement.GuiType.uneditable;
				}
                else if (appinfo.equals(DOMSXmlElement.GuiType.invisible.toString())) {
                    guiType = DOMSXmlElement.GuiType.invisible;
                }

			}
		}
		if ((guiType.equals(GuiType.NA)) && (parent!=null)) {
			setGuiType(parent.getGuiType());
		}
		return this.guiType;
	}
	
	/**
	 * @return the guiType
	 */
	public String getGuiTypeAsString() {
		
		if (guiType.equals(GuiType.NA)) {
			getGuiType();
		}
		if ((guiType.equals(GuiType.NA)) && (parent!=null)) {
			setGuiType(parent.getGuiType());
		}
		if (guiType.equals(GuiType.NA)) {
			return GuiType.inputfield.toString();
		}
		return guiType.toString();
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(Property property) {
		this.property = property;
		
		this.minOccurence = SDOUtil.getLowerBound(property);
		this.maxOccurence = SDOUtil.getUpperBound(property);
	}

	/**
	 * @return the property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @param dataobject
	 *            the dataobject to set
	 */
	public void setDataobject(DataObject dataobject) {
		this.dataobject = dataobject;
	}

	/**
	 * @return the dataobject
	 */
	public DataObject getDataobject() {
		return dataobject;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(DOMSXmlElement parent) {
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public DOMSXmlElement getParent() {
		return parent;
	}

	public DOMSXmlElement create() {
		DOMSXmlElement myElem;
		if ((this.property.isMany()) && this.property.getType().isDataType())
		{
			List values = this.getDataobject().getList(this.getProperty());
			myElem = new DOMSXmlElement(this.myDocument, this.parent, this.getDataobject(),
					this.property, parent.getChildren().indexOf(this) + 1);
			myElem.setIndex(values.size());
			values.add(null);
		}
		else {		
			DataObject myDo = getDataobject().getContainer().createDataObject(
					getProperty().getName());
			myElem = new DOMSXmlElement(this.myDocument, this.parent, myDo,
					this.property, parent.getChildren().indexOf(this) + 1);
		
			if (!isLeaf()) {
				createChildren(myElem);
			}
		}

		return myElem;
	}

	private void createChildren(DOMSXmlElement element) {
		for (Object o : element.getDataobject().getType().getProperties()) {
			Property p = (Property) o;
			if (!getHelperContext().getXSDHelper().isAttribute(p)) {
				DataObject childDo;	
				if (!p.isContainment()) {				
					childDo = element.getDataobject();
				} else {
					childDo = element.getDataobject().createDataObject(p);
				}			
				DOMSXmlElement childElement = new DOMSXmlElement(this.myDocument, element, childDo, p);			
				element.add(childElement);
				if (!childElement.isLeaf()) {
					createChildren(childElement);
				}
			}
		}

	}

	public void delete() {
		if ((this.property.isMany()) && this.property.getType().isDataType())
		{
			List values = this.getDataobject().getList(this.getProperty());
			values.remove(this.getIndex());
			parent.getChildren().remove(this);
			for (DOMSXmlElement element : parent.getChildren())
			{
				if (element.getIndex()>this.getIndex()) {
					element.setIndex(element.getIndex()-1);
				}
			}
		}
		else
		{
			parent.getChildren().remove(this);
			dataobject.delete();
		}
	}

	public void setLabel(String label) {		
		this.label = label.toLowerCase().replaceFirst(String.valueOf(label.charAt(0)), String.valueOf(label.charAt(0)).toUpperCase());
		
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		if (label != null) {
			return label;
		} else {
			return "Untitled";
		}
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

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
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}


	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(ArrayList<DOMSXmlElement> children) {
		this.children = children;
	}

	/**
	 * @return the children
	 */
	public ArrayList<DOMSXmlElement> getChildren() {
		return children;
	}

	public void add(DOMSXmlElement xmlElement) {
		children.add(xmlElement);
	}

	public void remove(DOMSXmlElement xmlElement) {
		children.remove(xmlElement);
	}

	public void submit(HelperContext context) {
		if (isLeaf()) {
			//System.out.println("LeafDOMSXmlElement.submit. property = " + getProperty().getName());
			
			if (this.getValue() != null) 
			{
				if (getProperty().getType().isSequenced()) 
				{
					if (context.getXSDHelper().isMixed(getProperty().getType())) {
						Sequence seq = getDataobject().getSequence();
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
				else 
				{
					if (getProperty().getType().getInstanceClass() != null) {
						
						Object value = SDOUtil.createFromString(this.getProperty().getType(), valueToSDOType(context));
						
						if (this.getProperty().isMany())
						{
							List values = this.getDataobject().getList(this.getProperty());
							if (value!=null) {
								if (this.getDataobject().isSet(this.getProperty())) {
									values.set(this.index, value);
								}
							}
						}
						else {
							this.getDataobject().set(this.getProperty(), value);
						}
					}
				}
				
			}
		} else {// end isLeaf
			for (DOMSXmlElement xmlElement : children) {				
					xmlElement.submit(context);
			}
		}
	}

	public String toString() {
		return getLabel();
	}
	
	private String valueToSDOType(HelperContext context)
	{
		String result = "";
		String typeName = this.property.getType().getName();
		if (typeName.equals("YearMonthDay")) {
			if (this.getValue() instanceof String) {
				result = (String)this.getValue();
			}
			else if (this.getValue() instanceof Date) {
				result = context.getDataHelper().toYearMonthDay((Date)this.getValue());
			}
		}
		else {
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
