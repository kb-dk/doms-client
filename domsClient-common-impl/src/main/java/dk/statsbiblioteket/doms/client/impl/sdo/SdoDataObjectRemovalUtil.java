package dk.statsbiblioteket.doms.client.impl.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.helper.HelperContext;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

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
            //dob.delete();
            delete(dob);
        }
    }


    /**
     * This is a slightly more robust version of DataObjectUtil.delete()
     * @param dataObject
     */
    public static void delete(DataObject dataObject)
    {
        EObject eDataObject = (EObject)dataObject;
        EcoreUtil.remove(eDataObject);
        List contents = new ArrayList((eDataObject).eContents());
        for (int i = 0, size = contents.size(); i < size; ++i)
        {
            delete((DataObject) contents.get(i));
        }
        EClass eClass = eDataObject.eClass();
        for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i)
        {
            EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);
            if (eStructuralFeature instanceof Property && eStructuralFeature.isChangeable() && !eStructuralFeature.isDerived() && !((Property)eStructuralFeature).isReadOnly())
            {
                eDataObject.eUnset(eStructuralFeature);
            } else if (!(eStructuralFeature instanceof Property)) {
                eDataObject.eUnset(eStructuralFeature);
            }
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

        boolean sequencePropertiesAreEmpty = true;
        boolean instancePropertiesAreEmpty = true;

        if (dataObject.getType().isSequenced()) {
            Sequence sequence = dataObject.getSequence();
            for (int i = 0; i < sequence.size(); i++) {
                boolean thisSequenceElementIsEmpty = true;
                Object value = sequence.getValue(i);
                if (value instanceof DataObject) {
                    thisSequenceElementIsEmpty = handleDataObject(helperContext, dataObject, (DataObject) value);
                } else if (value != null && value instanceof String) {
                    thisSequenceElementIsEmpty =  ("".equals(value));
                }
                sequencePropertiesAreEmpty = sequencePropertiesAreEmpty && thisSequenceElementIsEmpty;
            }
        }

        for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) {
            Property p = (Property) dataObject.getInstanceProperties().get(i);
            boolean thisInstancePropertyIsEmpty = handleValueOfProperty(helperContext, parent, dataObject, p);
            instancePropertiesAreEmpty = thisInstancePropertyIsEmpty && instancePropertiesAreEmpty;
        }

        if (sequencePropertiesAreEmpty && instancePropertiesAreEmpty && parent != null) {
            addObjectToDelete(dataObject);
            return true;
        } else {
            return false;
        }
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
        if (isEmpty && helperContext.getXSDHelper().isAttribute(property)) {
            dataObject.unset(property);
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
