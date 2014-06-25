package dk.statsbiblioteket.doms.client.impl.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;
import org.apache.tuscany.sdo.impl.AnyTypeDataObjectImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility class that is used to remove empty DataObjects from a XMLDocument.
 */
public class SdoDataObjectRemovalUtil {

    private ArrayList<DataObject> dataObjectsToDelete;

    public SdoDataObjectRemovalUtil() {
        dataObjectsToDelete = new ArrayList<DataObject>();
    }

    /**
     * Performs the actual deletion of the DataObjects
     */
    public void doDelete() {
        for (DataObject dob : dataObjectsToDelete) {
            dob.delete();
        }
    }

    /**
     * Add a data object to the list of objects that must be deleted.
     *
     * @param obj the object to delete
     */
    private void addObjectToDelete(DataObject obj) {
        for (DataObject curObj : dataObjectsToDelete) {
            if (curObj.equals(obj)) {
                return;
            }
        }
        dataObjectsToDelete.add(obj);
    }

    /**
     * Traverses the hierarchy of DataObjects that can be reached from dataObject. If a data object is empty it is
     * deleted.
     *
     * @param helperContext
     * @param parent
     * @param dataObject
     * @return true if dataObject is empty.
     */
    public boolean handleDataObject(HelperContext helperContext, final DataObject parent, final DataObject dataObject) {
        boolean isEmpty = true;
        boolean temp = false;
        boolean isSequenced = dataObject.getType().isSequenced();
        //Determine if the current object is mixed. Mixed data objects are not removed.
        boolean isMixed = dataObject instanceof AnyTypeDataObjectImpl && ((AnyTypeDataObjectImpl) dataObject).getMixed().size() > 0;

        if (isSequenced) {
            List seq = dataObject.getType().getProperties();
            //We make a copy of this data because changing it while we iterate through it
            //causes surprising side-effects - specifically it can reorder the sequence
            //so we the iteration gets screwed up.
            Map<Property, Object> sequenceMapCopy = new HashMap<Property, Object>();
            for (int i = 0; i < seq.size(); i++) {
                sequenceMapCopy.put((Property) seq.get(i), dataObject.get((Property) seq.get(i)));
            }

            for (Map.Entry<Property, Object> entry: sequenceMapCopy.entrySet()) {
                Property p = entry.getKey();
                Object v = entry.getValue();
                if (p == null) {
                    if (v == null) {
                        temp = true;
                    } else {
                        temp = (v.toString().length() == 0);
                    }
                    if (temp && !isMixed) {
                        addObjectToDelete(dataObject);
                    }
                    isEmpty = (isEmpty && temp);
                } else  {
                    temp = handleValueOfProperty(
                            helperContext, parent, dataObject, p);
                    isEmpty = (isEmpty && temp);
                }
            }
        } else {
            for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) {
                Property p = (Property) dataObject.getInstanceProperties().get(i);
                temp = handleValueOfProperty(helperContext, parent, dataObject, p);
                isEmpty = (isEmpty && temp);
                if (temp) {
                    dataObject.unset(p);
                }
            }
        }
        if (isEmpty && !isMixed) {
            if (parent != null) {
                addObjectToDelete(dataObject);
            }
        }
        return isEmpty;
    }

    private boolean handleValueOfProperty(HelperContext helperContext, final DataObject parent, DataObject dataObject,
                                          Property p) {
        boolean isEmpty = false;
        if (dataObject.isSet(p)) {
            if (p.getType().isDataType()) {
                if (p.isMany()) {
                    isEmpty = handleSimpleValues(helperContext, parent, dataObject, p, dataObject.getList(p));
                } else {
                    isEmpty = handleSimpleValue(helperContext, parent, dataObject, p, dataObject.get(p));
                }
            } else {

                if (p.isContainment()) {
                    if (p.isMany()) {
                        isEmpty = handleDataObjects(helperContext, dataObject, dataObject.getList(p));
                    } else {
                        isEmpty = handleDataObject(helperContext, dataObject, dataObject.getDataObject(p));
                    }
                }
            }
        } else {
            isEmpty = true;
        }
        return isEmpty;
    }

    private boolean handleDataObjects(HelperContext helperContext, final DataObject parent, List list) {
        /*
           * "Traversing a list of DataObjects which represent the values of a
           * multi-valued containment Property"
           */
        boolean isEmpty = true;
        boolean temp;
        for (Iterator i = list.iterator(); i.hasNext(); ) {
            temp = handleDataObject(helperContext, parent, (DataObject) i.next());
            isEmpty = (isEmpty && temp);
        }

        return isEmpty;
    }

    private boolean handleSimpleValue(HelperContext helperContext, final DataObject parent, DataObject dataObject,
                                      Property property, Object value) {
        boolean manyValued = property.isMany();
        List emptyStringlList = new ArrayList();
        emptyStringlList.add("");
        boolean isEmpty = true;
        if (value != null) {
            if (value.equals(SDOParsedXmlElementImpl.PLACEHOLDER_FOR_EMPTY_STRING)) {
                isEmpty = false;
                if (!manyValued) {
                    dataObject.set(property, "");
                } else {
                    dataObject.set(property, emptyStringlList);
                }
            } else if (value.equals(SDOParsedXmlElementImpl.PLACEHOLDER_FOR_NOW_EMPTY_STRING)) {
                isEmpty = true;
            } else if (!value.toString().isEmpty()) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    private boolean handleSimpleValues(HelperContext helperContext, final DataObject parent, DataObject dataObject,
                                       Property property, List values) {
        boolean isEmpty = true;
        boolean temp;
        for (Iterator i = values.iterator(); i.hasNext(); ) {
            temp = handleSimpleValue(helperContext, parent, dataObject, property, i.next());
            isEmpty = (isEmpty && temp);
        }
        return isEmpty;
    }


}
