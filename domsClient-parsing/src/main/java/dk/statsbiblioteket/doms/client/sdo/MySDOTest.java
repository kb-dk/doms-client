package dk.statsbiblioteket.doms.client.sdo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.tuscany.sdo.api.SDOUtil;
import org.apache.tuscany.sdo.generate.XSD2JavaGenerator;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

public class MySDOTest extends MySDOBase {

	private static final String sdoApiUri = "commonj.sdo";

	private static final String peopleURI = "www.example.org/people";

	private static final String medicalURI = "www.example.org/MedicalTest";
	  
	StringBuffer buf = null;
	HelperContext scope = HelperProvider.getDefaultContext();

	private int indent;

	private int indentIncrement = 2;

	public MySDOTest() 
	{
		buf = new StringBuffer();
	}
	
	public void test1() {
		try
		{
		    HelperContext scope = SDOUtil.createHelperContext();
	
		    createTypesViaAPI(scope);
		    
			DataFactory dataFactory = scope.getDataFactory();
		    DataObject person1 = dataFactory.create("www.example.org/people", "Person");
	
		    person1.setString("id", "1");
		    person1.setString("name", "Joe Johnson Snr.");
		    person1.setString("gender", "male");
	
		    System.out.println("person1: " + person1);
		}
		catch(Exception ex)
		{
			System.out.println("test1. " + ex.getMessage());
		}
	}
	
	private void createTypesViaAPI(HelperContext scope) throws Exception {

		List typeDeclarations = new ArrayList();

	    TypeHelper typeHelper = scope.getTypeHelper();

	    Type stringType = typeHelper.getType(sdoApiUri, "String");
	    Type dateType = typeHelper.getType(sdoApiUri, "Date");
	    Type booleanType = typeHelper.getType(sdoApiUri, "Boolean");

	    // <complexType name="Person">
	    // <sequence>
	    // <element name="dob" type="date"/>
	    // <element name="relative" maxOccurs="unbounded" type="tns:Relative"/>
	    // <any namespace="##other" processContents="lax" maxOccurs="unbounded"/>
	    // </sequence>
	    // <attribute name="id" type="ID"/>
	    // <attribute name="name" type="string"/>
	    // <attribute name="gender" type = "tns:Gender"/>
	    // </complexType>

	    DataObject personTypeDesc = createTypeDescription(scope, peopleURI,
	        "Person");
	    typeDeclarations.add(personTypeDesc);

	    addPropertyDescription(personTypeDesc, stringType, "name");
	    addPropertyDescription(personTypeDesc, dateType, "dob");
	    addPropertyDescription(personTypeDesc, stringType, "id"); // set to unique
	    // identifier?
	    addPropertyDescription(personTypeDesc, stringType, "gender"); // restrict?

	    DataObject relativeType = createTypeDescription(scope, peopleURI,
	        "Relative"); // forward declare the Relative type
	    typeDeclarations.add(relativeType);

	    DataObject rp = addPropertyDescription(personTypeDesc, relativeType,
	        "relative");
	    rp.setBoolean("many", true);
	    personTypeDesc.set("open", Boolean.TRUE);

	    // <complexType name="Relative">
	    // <attribute name="target" type="IDREF" sdoxml:propertyType="tns:Person"
	    // use="required"/>
	    // <attribute name="relationship" type="string" />
	    // <attribute name="genetic" use="optional" type="boolean"/>
	    // </complexType>

	    addPropertyDescription(relativeType, stringType, "relationship");
	    addPropertyDescription(relativeType, booleanType, "genetic");
	    DataObject targetPersonProp = addPropertyDescription(relativeType,
	        personTypeDesc, "target");
	    targetPersonProp.setBoolean("containment", false);

	    // <complexType name="PersonSet">
	    // <sequence>
	    // <element name="person" type="tns:Person" maxOccurs="unbounded"/>
	    // </sequence>
	    // </complexType>

	    DataObject pSet = createTypeDescription(scope, peopleURI, "PersonSet");
	    typeDeclarations.add(pSet);
	    DataObject pSetProperty = addPropertyDescription(pSet, personTypeDesc,
	        "person");
	    pSetProperty.setBoolean("many", true);

	    // <complexType name="Condition">
	    // <sequence>
	    // <element name="diagnosed" type="date" />
	    // </sequence>
	    // <attribute name="name" type="tns:ConditionName" />
	    // </complexType>

	    DataObject condition = createTypeDescription(scope, medicalURI, "Condition");
	    typeDeclarations.add(condition);
	    addPropertyDescription(condition, booleanType, "diagnosed");
	    addPropertyDescription(condition, stringType, "name"); // constrain?

	    // <complexType name="Test">
	    // <sequence>
	    // <element name="referrals" type="people:PersonSet" />
	    // <element name="patients" type="people:PersonSet" />
	    // <element name="relatives" type="people:PersonSet" />
	    // </sequence>
	    // </complexType>

	    DataObject testType = createTypeDescription(scope, medicalURI, "Test");
	    typeDeclarations.add(testType);
	    addPropertyDescription(testType, pSet, "referrals");
	    addPropertyDescription(testType, pSet, "patients");
	    addPropertyDescription(testType, pSet, "relatives");

	    List types = typeHelper.define(typeDeclarations);

	    DataObject p = scope.getDataFactory().create("commonj.sdo", "Property");
	    p.set("type", typeHelper.getType(medicalURI, "Condition"));
	    p.set("name", "condition");
	    p.setBoolean("many", true);
	    p.setBoolean("containment", true); // why is this not the default?

	    typeHelper.defineOpenContentProperty(medicalURI, p);

	  }
	
	private DataObject createTypeDescription(HelperContext scope, String uri,
		      String name) {
		    DataObject typeDesc = scope.getDataFactory().create(sdoApiUri, "Type");
		    typeDesc.set("name", name);
		    typeDesc.set("uri", uri);
		    return typeDesc;
		  }
	
	private DataObject addPropertyDescription(
		      DataObject containerTypeDescription, Object propertyType,
		      String propertyName) {
		    DataObject property = containerTypeDescription.createDataObject("property");
		    property.set("type", propertyType);
		    property.setString("name", propertyName);
		    property.setBoolean("containment", true);
		    return property;
		  }

	
	public void printToScreen(String schemaURL, String targetNamespace, String dcDocURL)
	{
		try
		{
			HelperContext scope = createScopeForTypes();
			
			//String schemaURL = "http://doms-gui:8080/fedora/get/doms:Schema_OAIDublinCore/SCHEMA";
			//String schemaURL = "http://doms-gui:8080/fedora/get/doms:Schema_QualifiedDublinCore/REELTAPE_SCHEMA";
			//String schemaURL = "http://doms-gui:8080/fedora/get/doms:Schema_ReelTapeSide/SCHEMA";
			//String schemaURL = "http://doms-gui:8080/fedora/get/doms:Schema_PremisImage/SCHEMA";
			//String schemaURL = "http://localhost/domsgui/po.xsd";
			//String schemaURL = "http://doms-gui:8080/fedora/get/doms:Schema_PremisAudioRecording/SCHEMA";
			//String schemaURL = "http://sigyn/domsgui/premisAudioRecording.xsd";
			//String schemaURL = "http://doms-gui:8080/fedora/get/doms:ContentModel_ReelTapeBWFa/PRONOMID_SCHEMA";
			//String schemaURL = "http://localhost:8080/fedora/get/doms:Schema_Premis/SCHEMA";
			
			List<Type> types = loadTypesFromXMLSchemaFile(scope, schemaURL);
			//printTypesInSchema(types, "http://doms.statsbiblioteket.dk/types/ReelTapeSide/0/1/#", scope.getXSDHelper());
			//printTypesInSchema(types, "http://www.loc.gov/standards/premis/v1", scope.getXSDHelper());
			
			printTypesInSchema(types, targetNamespace, scope.getXSDHelper());
			
			System.out.println("\nprintTypesInSchema done\n");
			
			//String dcDocURL = "http://sigyn/domsgui/bookStore.xml";
			//String dcDocURL = "http://sigyn/domsgui/qualifieddc1.xml";
			//String dcDocURL = "http://sigyn/domsgui/side-metadata1.xml";
			//String dcDocURL = "http://sigyn/domsgui/imagepremis.xml";
			//String dcDocURL = "http://sigyn/domsgui/view.xml";
			
			//String dcDocURL = "http://sigyn/domsgui/premisAudioRecording1.xml";
			
			if (dcDocURL!=null) {
				XMLDocument dcDoc = getXMLDocumentFromFile(scope, dcDocURL);
				
				printXMLDocument(dcDoc);
				System.out.println(getBuf().toString());
			}
			
			/*DataObject rootDataObject = dcDoc.getRootObject();
			if (rootDataObject!=null)
			{
				System.out.println("rootDataObject = " + rootDataObject.getType().getName());
				DataObject mainobject =
					rootDataObject.getDataObject("view[name='GUI']");
				if (mainobject!=null)
				{
					System.out.println("mainobject = " + mainobject.getType().getName() + ". mainobject.getType().isDataType() = " + mainobject.getType().isDataType());
					
					Object p = mainobject.get("mainobject");
					if (p!=null)
					{
						System.out.println("Found mainobject. Value = " + p + ". p.class = " + p.getClass());
					}
				}
			}
			*/
			
			//saveCopy(dcDoc);
			
			/*loadTypesFromXMLSchemaFile(scope, schemaURL);
		    DataObject dcDocObj = getDataObjectFromFile(scope, dcDocURL);

		    reset();
		    print(dcDocObj);
		    System.out.println(getBuf().toString());
		    */
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	public void reset() 
	{
		indent = 0;
	    buf = new StringBuffer();
	}
	
	public void print(Object sdoObject) throws Exception {

	    if (sdoObject instanceof XMLDocument) {
	      printXMLDocument((XMLDocument) sdoObject);
	    } else if (sdoObject instanceof DataObject) {
	      printDataObject((DataObject) sdoObject);
	    }

	}
	public void saveCopy(XMLDocument xmlDocument) {
		DataObject dataObject = xmlDocument.getRootObject();
		findSimpleValues(dataObject);
		saveXMLDocumentToFile(getScope(), "", xmlDocument);
	}
	
	public void findSimpleValues(DataObject dataObject) {
		XSDHelper xsdHelper = getScope().getXSDHelper();
		
		if (dataObject.getType().isSequenced()) 
		{
			Sequence seq = dataObject.getSequence();
			for (int i = 0; i < seq.size(); i++) 
			{
				Property p = seq.getProperty(i);
				if (p == null) {
					//buf.append("text: ").append(seq.getValue(i));
				} 
				else if (!xsdHelper.isAttribute(p)) 
				{
					String name = p.getName();
					System.out.println("p.getName= " + p.getName());
					traversePropertyValuePair(p, seq.getValue(i));
					
					//Object value = "hello world"; 
					//seq.setValue(i, value);
				}
			}
		}
		else
		{
			for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) 
			{
				Property p = (Property) dataObject.getInstanceProperties().get(i);
		        String name = p.getName();
		        System.out.println("p.getName= " + p.getName());
		        changePropety(dataObject, p);
		    }
		}
		
	}
	
	private void traversePropertyValuePair(Property p, Object value) {

	    if(p.getType().isDataType()) 
	    {
	    	System.out.println("Property: " + p.getName() + " Object value = " + value);
	    	System.out.println("  Property type = " + p.getType().getName());
	      lineBreak();
	    } else {
	      if(p.isContainment()) {
	    	  findSimpleValues((DataObject)value);
	      } else {
	        //printReferencedDataObject((DataObject)value);
	      }
	    }

	    
	  }
	
	private void changePropety(DataObject dataObject, Property property) {
		if (dataObject.isSet(property)) {
			if (property.getType().isDataType()) {
				if (property.isMany()) {
					printSimpleValues(dataObject.getList(property));
				} else {
					//printSimpleValue(dataObject.get(property));
					if (property.getType().getName().equals("String"))
					{
						dataObject.setString(property, "hello world");
					}
				}
			} else {
				if (property.isContainment()) {
					incrementIndent();
					if (property.isMany()) {
						printDataObjects(dataObject.getList(property));
					} else {
						printDataObject(dataObject.getDataObject(property));
					}
					decrementIndent();
				} else {
					if (property.isMany()) {
						printReferencedDataObjects(dataObject.getList(property));
					} else {
						printReferencedDataObject(dataObject.getDataObject(property));
					}
				}
			}
		} else {
			buf.append(" is not set");
		}
	}
	
	public void printXMLDocument(XMLDocument xmlDocument) 
	{

	    /*
		 * "We are going to traverse a data graph that has been wrapped in an
		 * instance of XMLDocument\n" + "Amongst other things, the XMLDocument
		 * instance provides access to the root element name\n" + "and the root
		 * DataObject of the data graph.\n\n" +
		 * "xmlDocument.getRootElementName();\n" +
		 * "xmlDocument.getRootObject();",
		 * 
		 * "Accessing another graph via an XMLDocument instance as we saw
		 * previously ...\n" + "xmlDocument.getRootElementName();\n" +
		 * "xmlDocument.getRootObject();"
		 */

	    buf.append("XMLDocument: ").append(xmlDocument.getRootElementName());
	    lineBreak();
	    incrementIndent();
	    printDataObject(xmlDocument.getRootObject());
	    decrementIndent();
	}
	
	
	public void printDataObject(DataObject dataObject) {

	    if (dataObject.getContainer() == null) {
	      /*
	          "We begin traversing the data graph by examining the root object of the graph's containment hierarchy,\n"
	              + "making a record of the values of its Properties. As we inspect the values of the Properties of this object\n"
	              + "if we encounter contained DataObjects, then we will recurs through the containment hierarchy of the\n"
	              + "data graph in a depth first fashion, and create a text representation of the graph that we'll print\n"
	              + "out after the graph traversal has been completed.",

	          "We are beginning to traverse another data graph from its root object, in the same way that we saw previously"
	          */
	    } else {
	      /*
	          "We have arrived at a contained dataObject in the graph, and will inspect its Property values,\n"
	              + "recursing deeper if necessary",

	          "Inspecting another contained dataObject"*/
	    }

	    lineBreak();
	    indent();
	    buf.append("DataObject: ");
	    Type type = dataObject.getType();
	    buf.append("Type: ").append(type.getURI()).append('#').append(
	        type.getName());
	    lineBreak();

	    System.out.println(buf);
	    if (dataObject.getType().isSequenced()) {

	      /*
	          "We've encountered a DataObject in the graph for which the Type is 'Sequenced'\n"
	              + "That is to say that the order of addition of Property values to the DataObject instance\n"
	              + "is important,  and is preserved by the DataObject\n\n"
	              + "dataObject.getType().isSequenced();",

	          "We've encountered another sequenced DataObject instance, and so will traverse the Property\n"
	              + "values in the order preerved by the instance, as we saw before\n\n"
	              + "dataObject.getType().isSequenced();"
	              */
	      
	      /*
	          "There's a subtlety here which we must deal with if this sample code is to\n" +
	      		"handle both Type systems that derive from XML schema, and those that come from elsewhere,\n" +
	      		"e.g. using the SDO API.  If a Sequenced DataObject has a Type that comes from XML schema\n" +
	      		"then its Properties that derive from XML attributes are not ordered, whereas those that\n" +
	      		"derive from XML elements are ordered.  The SDO specification doesn't say whether\n" +
	      		"the attribute related Properties should appear at the start of a Sequence or not.\n" +
	      		"Currently in Tuscany we leave them out of the Sequence;  other SDO implementations may\n" +
	      		"include the XML attributes in the Sequence.  This sample code is written to deal with\n" +
	      		"either approach\n." +
	      		"We use the XSDHelper.isAttribute(Property) and isElement(Property) methods to distinguish\n" +
	      		"between the two kinds of Property",
	      		
	      		"Examining the xml attributes and elements of a Sequenced DataObject again."
	      		*/
	      
	      XSDHelper xsdHelper = getScope().getXSDHelper();
	      incrementIndent();
	      for(Iterator it=dataObject.getInstanceProperties().iterator(); it.hasNext();) {
	        Property property = (Property)it.next();
	        if (xsdHelper.isAttribute(property)) {
	          indent();
	          buf.append("Property (XML Attribute): ").append(property.getName()).append(" - ").append(dataObject.get(property));
	          lineBreak();
	        }

	      }
	      decrementIndent();
	      Sequence seq = dataObject.getSequence();

	      /*
	          "The Property/Value pairs of a Sequence can be accessed via the getProperty(int) and getValue(int)\n"
	              + "accessor methods of the Sequence interface.  The size() method of the Sequence tells us how many there are.\n"
	              + "If the getProperty(int) method returns null,  then the value is text.  These text values may be encountered\n"
	              + "when the DataObject's type is 'mixed' (dataObject.getType().isMixed() == true). A typical example of this\n"
	              + "is when the data graph represents a form letter.",
	      
	          "Inspecting the Property/Value pairs of another Sequence"
	          */
	      
	      incrementIndent();
	      indent();
	      buf.append("Sequence: {\n");
	      
	      incrementIndent();
	      for (int i = 0; i < seq.size(); i++) {
	        Property p = seq.getProperty(i);
	        if (p == null) {
	          indent();
	          buf.append("text: ").append(seq.getValue(i));
	          lineBreak();
	        } else if(!xsdHelper.isAttribute(p)){
	          printPropertyValuePair(p, seq.getValue(i));
	       }
	      }
	      decrementIndent();
	      
	      indent();
	      buf.append("}\n");
	      decrementIndent();

	    } else {
	      incrementIndent();

	      /*
	          "We access the Property values of this DataObject by first getting the list of 'Instance Properties'\n"
	              + "from the DataObject.  For many DataObjects, this will be the same set of Properties that are defined\n"
	              + "by the DataObject's Type.  However, if the DataObject's type is 'Open' then an instance of that Type\n"
	              + "may contain more Properties than the type itself.  The list of Instance Properties will always include\n"
	              + "the Properties defined in the Type,  but will also include any Properties that the instance has values for\n"
	              + "by virtue of it's type being 'Open'\n\n"
	              + "for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) {\n"
	              + "  Property p = (Property) dataObject.getInstanceProperties().get(i);",

	          "Traversing the instance Properties of this DataObject\n"
	              + "for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) {\n"
	              + "  Property p = (Property) dataObject.getInstanceProperties().get(i);"

	      */

	      for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) {
	        Property p = (Property) dataObject.getInstanceProperties().get(i);
	        indent();
	        printValueOfProperty(dataObject, p);
	      }

	      decrementIndent();
	    }

	  }

	private void printPropertyValuePair(Property p, Object value) {

	    indent();
	    buf.append("Property: ").append(p.getName()).append(": ");
	    if(p.getType().isDataType()) {
	      printSimpleValue(value);
	      lineBreak();
	    } else {
	      if(p.isContainment()) {
	        incrementIndent();
	        printDataObject((DataObject)value);
	        decrementIndent();
	      } else {
	        printReferencedDataObject((DataObject)value);
	      }
	    }

	    
	  }
	
	private void printValueOfProperty(DataObject dataObject, Property p) {

	    /*
	        "We are about to inspect the value of a Property,  but we must\n"
	            + "consider the nature of that Property in order to deal with it appropriately.\n"
	            + "Firstly we see if the Property value has been set (dataObject.isSet(property))\n"
	            + "Then we see if the Property is simple valued (property.isDataType() == true)\n"
	            + "--if not then we know it's a DataObject and we must recurs deeper into the graph's\n"
	            + "containment hierarchy\n"
	            + "Whether or not the property value is a DataObject,  is may be single or multi-valued\n"
	            + "so we must either use one of the DataObject's get*(Property) accessors for single\n"
	            + "valued Properties or the getList() method for multi-valued properties.\n"
	            + "Another thing we must deal with when the Property is a DataObject, is whether or not the\n"
	            + "Property is a 'containment' Property.  If it isn't, then here we simply record the fact that\n"
	            + "we have encountered this non-containment relationship,  and move on to the next Property",

	        "Inspecting another property to determine how to access its value,  as we saw before"
	        */



	    buf.append("Property ").append(p.getName()).append(": ").append(" - ");

	    if (dataObject.isSet(p)) {
	      if (p.getType().isDataType()) {
	        if (p.isMany()) {
	          printSimpleValues(dataObject.getList(p));
	        } else {
	          printSimpleValue(dataObject.get(p));
	        }
	      } else {
	        if (p.isContainment()) {
	          incrementIndent();
	          if (p.isMany()) {
	            printDataObjects(dataObject.getList(p));
	          } else {
	            printDataObject(dataObject.getDataObject(p));
	          }
	          decrementIndent();
	        } else {
	          if (p.isMany()) {
	            printReferencedDataObjects(dataObject.getList(p));
	          } else {
	            printReferencedDataObject(dataObject.getDataObject(p));
	          }
	        }
	      }
	    } else {
	      buf.append(" is not set");
	    }

	    lineBreak();

	  }
	
	private void printReferencedDataObject(DataObject dataObject) {

	    /*
	        "We have encounted a non-containment reference to a DataObject, and so\n"
	            + "we know that this DataObject will be fully inspected when encountered by the\n"
	            + "traversal of the data graph's containment hierarchy.\n"
	            + "We therefore record the fact that this association has been encountered by\n"
	            + "establishing the path from the root object of the data graph to the referenced\n"
	            + "DataObject",

	        "Recording the fact that we have encountered another non-containment reference"
	        */

	    List path = new ArrayList();
	    DataObject current = dataObject;
	    while (current != null) {
	      Property containmentProperty = current.getContainmentProperty();
	      if(containmentProperty != null) {
	        if(containmentProperty.isMany()) {
	          List pValues = current.getContainer().getList(containmentProperty);
	          int index = pValues.indexOf(current)+1;
	          path.add("["+index+"]");
	        }
	        path.add("/"+current.getContainmentProperty().getName());
	      }
	      current = current.getContainer();
	    }
	    buf.append("reference to: ");
	    for (ListIterator i = path.listIterator(path.size()); i.hasPrevious();) {
	      buf.append(i.previous());
	    }
	  }
	
	private void printReferencedDataObjects(List list) {

	    /*
	        "Traversing a list of DataObjects which represent the values of a multi-valued non-containment Property"
	        */

	    indent();
	    buf.append('[');
	    for (Iterator i = list.iterator(); i.hasNext();) {
	      printReferencedDataObject((DataObject) i.next());
	    }
	    indent();
	    buf.append(']');
	  }

	  private void printDataObjects(List list) {

		/*
		 * "Traversing a list of DataObjects which represent the values of a
		 * multi-valued containment Property"
		 */
		lineBreak();
		indent();
		buf.append("[");
		incrementIndent();
		for (Iterator i = list.iterator(); i.hasNext();) {
			printDataObject((DataObject) i.next());
		}
		decrementIndent();
		indent();
		buf.append(']');
	}

	private void printSimpleValue(Object object) {
		buf.append(object);
	}

	private void printSimpleValues(List values) {
		buf.append('[');
		for (Iterator i = values.iterator(); i.hasNext();) {
			printSimpleValue(i.next());
			if (i.hasNext()) {
				buf.append(',');
			}
		}
		buf.append(']');

	}
	
	// ///////////////////
	private void decrementIndent() {
	    indent -= indentIncrement;

	  }

	  private void incrementIndent() {
	    indent += indentIncrement;

	  }

	  private void indent() {
	    for (int i = 0; i < indent; i++) {
	      buf.append(' ');
	    }
	  }

	  private void lineBreak() {
	    buf.append('\n');
	  }

	  public StringBuffer getBuf() {
	    return buf;
	  }

	  public void setBuf(StringBuffer b) {
	    buf = b;
	  }

	  public HelperContext getScope() {
	    return scope;
	  }

	  public void setScope(HelperContext scope) {
	    this.scope = scope;
	  }
}
