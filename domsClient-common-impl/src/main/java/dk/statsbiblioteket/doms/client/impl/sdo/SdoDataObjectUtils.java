package dk.statsbiblioteket.doms.client.impl.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XSDHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class that is used to remove empty DataObjects from a XMLDocument.
 *
 */
public class SdoDataObjectUtils {
	
	private ArrayList<DataObject> dataObjectsToDelete;
	
	public SdoDataObjectUtils()
	{
		dataObjectsToDelete = new ArrayList<DataObject>();
	}
	
	/**
	 * Performs the actual deletion of the DataObjects
	 */
	public void doDelete()
	{
		for (DataObject dob: dataObjectsToDelete) {
			dob.delete();
		}
	}
	
	/**
	 * Add a data object to the list of objects that must be deleted.
	 * 
	 * @param obj the object to delete
	 */
	private void addObjectToDelete(DataObject obj)
	{
		for (DataObject curObj : dataObjectsToDelete) {
			if (curObj.equals(obj)) {
				return;
			}
		}
		dataObjectsToDelete.add(obj);
	}
	
	/**
	 * Traverses the hierarchy of DataObjects that can be reached from dataObject. If a data object is empty it is deleted.
	 * @param helperContext
	 * @param parent
	 * @param dataObject
	 * @param parentProperty
	 * @return true if dataObject is empty.
	 */
	public boolean  handleDataObject(HelperContext helperContext, final DataObject parent, final DataObject dataObject, final Property parentProperty) 
	{
		boolean isEmpty = true;
		boolean temp= false;
		
		if (dataObject.getType().isSequenced()) 
	    {
	    	XSDHelper xsdHelper = helperContext.getXSDHelper();
	    	
	    	Sequence seq = dataObject.getSequence();

	    	for (int i = 0; i < seq.size(); i++) {
	    		Property p = seq.getProperty(i);
	    		if (p == null) 
	    		{
	    			if (seq.getValue(i)==null) {
	    				temp = true;
	    			}
	    			else {
	    				temp = (seq.getValue(i).toString().length()==0);
	    			}
	    			if (temp) {
	    				addObjectToDelete(dataObject);
	    			}
	    			isEmpty = (isEmpty && temp);
	    		} 
	    		else if(!xsdHelper.isAttribute(p))
	    		{
	    			temp = handlePropertyValuePair(helperContext, parent, dataObject, parentProperty, p, seq.getValue(i));
	    			isEmpty = (isEmpty && temp);
	    		}
	    	}
	    }
	    else 
	    {
	      for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) {
	    	  
	    		Property p = (Property) dataObject.getInstanceProperties().get(i);
	    		temp = handleValueOfProperty(helperContext, parent, dataObject, p);
	    		isEmpty = (isEmpty && temp);
	    		if (temp) {
	    			//dataObject.unset(p);
	    		}
	    	}
	    }
	    if (isEmpty)
	    {
	    	if (parent!=null) {
	    		addObjectToDelete(dataObject);
	    	}
	    }
	    return isEmpty;
	  }
	
	
	private boolean handlePropertyValuePair(HelperContext helperContext,
			final DataObject parent, DataObject dataObject,
			Property parentProperty, Property p, Object value) {
		boolean isEmpty = false;

		if (p.getType().isDataType()) {
			isEmpty = handleSimpleValue(helperContext, parent, dataObject, p, value);
		} else {
			if (p.isContainment()) {
				isEmpty = handleDataObject(helperContext, dataObject,
						(DataObject) value, p);
				if (isEmpty) {
					addObjectToDelete((DataObject) value);
				}
			}
		}
		return isEmpty;
	}
	
	private boolean handleValueOfProperty(HelperContext helperContext, final DataObject parent, DataObject dataObject, 
			Property p) 
	{
		boolean isEmpty = false;
		if (dataObject.isSet(p)) 
	    {
			if (p.getType().isDataType()) {
				if (p.isMany()) {
					isEmpty = handleSimpleValues(helperContext, parent, dataObject, p, dataObject.getList(p));
				} else {
					isEmpty = handleSimpleValue(helperContext, parent, dataObject, p, dataObject.get(p));
				}
			} else {
				if (p.isContainment()) {
					if (p.isMany()) {
						isEmpty = handleDataObjects(helperContext, dataObject, dataObject.getList(p), p);
					} else {
						isEmpty = handleDataObject(helperContext, dataObject, dataObject.getDataObject(p), p);
					}
				}
			}
		}
		else {
			isEmpty = true;
		}
		return isEmpty;
	}
	
	private boolean handleDataObjects(HelperContext helperContext, final DataObject parent, List list, final Property parentProperty) {
		/*
		 * "Traversing a list of DataObjects which represent the values of a
		 * multi-valued containment Property"
		 */
		boolean isEmpty = true;
		boolean temp;
		for (Iterator i = list.iterator(); i.hasNext();) {
			temp = handleDataObject(helperContext, parent, (DataObject) i.next(), parentProperty);
			isEmpty = (isEmpty && temp);
		}
		
		return isEmpty;
	}
 
	private boolean handleSimpleValue(HelperContext helperContext, final DataObject parent, DataObject dataObject, 
			Property property, Object value) {
		boolean isEmpty = true;
		if (value!=null) {
			isEmpty = (value.toString().length()==0);
		}
		
		return isEmpty;
	}

	private boolean handleSimpleValues(HelperContext helperContext, final DataObject parent, DataObject dataObject, 
			Property property, List values) 
	{
		boolean isEmpty = true;
		boolean temp;
		for (Iterator i = values.iterator(); i.hasNext();) {
			temp = handleSimpleValue(helperContext, parent, dataObject, property, i.next());
			isEmpty = (isEmpty && temp);
		}
		return isEmpty;
	}


}
