package dk.statsbiblioteket.doms.client.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SDOParsedXmlElement {

    SDOParsedXmlDocument getDocument();

    boolean isEnum();

    boolean isLeaf();

    int getNumberOfOccurences();

    boolean getAddable();

    boolean getRemovable();

    boolean isRequired();

    boolean isOriginalElement();

    boolean isGuiType(String typeName);

    String getId();

    GuiType getGuiType();

    String getGuiTypeAsString();

    Property getProperty();

    DataObject getDataobject();

    SDOParsedXmlElement getParent();

    SDOParsedXmlElement create();

    void delete();

    void setLabel(String label);

    String getLabel();

    void setValue(Object value);

    Object getValue();

    String getStringValue();

    int getIndex();

    ArrayList<SDOParsedXmlElement> getChildren();

    void add(SDOParsedXmlElement xmlElement);

    void remove(SDOParsedXmlElement xmlElement);

    String toString();

    void setIndex(int i);

    public static enum GuiType {
        inputfield, textarea, uneditable, NA, enumeration, invisible
    }
}
