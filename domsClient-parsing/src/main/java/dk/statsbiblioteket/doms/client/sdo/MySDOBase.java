package dk.statsbiblioteket.doms.client.sdo;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

public class MySDOBase {

	public void somethingUnexpectedHasHappened(Exception e) {
	    System.out.println(e.getMessage());
	  }
	
	public HelperContext createScopeForTypes() {
	    /*
	        "All MetaData for SDO types can be viewed as being scoped within an instance of HelperContext\n" +
	        "The Helper Context instance provides access to a collection of other helpers\n" +
	        "that you will see exercised in the SDO samples\n" +
	        "All the Helpers related to a given helper context instance know about the same set of types\n\n" +
	        "The SDO specification doesn't state how an SDO implementation should create a HelperContext\n" +
	        "So we use a Tuscany specific API to do this ...\n\n" +
	        "HelperContext scope = SDOUtil.createHelperContext();",
	        
	        "Creating a new HelperContext scope for types for the next sample run as we did in previous samples"
	    */
	        
	    HelperContext scope = SDOUtil.createHelperContext(true);
	    return scope;
	  }
	
	public HelperContext useDefaultScopeForTypes() {
	    /*
	        "All MetaData for SDO types can be viewed as being held in an instance of HelperContext\n" +
	        "The Helper Context instance provides access to a collection of other helpers\n" +
	        "that you will see exercised in the SDO samples\n" +
	        "All the Helpers related to a given helper context instance know about the same set of types\n\n" +
	        "For most cases it's best to take control of the type scope by creating a new HelperContext,\n" +
	        "but a default helper context is provided and can be accessed using ...\n\n" +
	        "HelperContext scope = HelperProvider.getDefaultContext();\n\n" +
	        "A case in point where the default scope must be used is when using some of the DataGraph APIs\n" +
	        "which don't all support the type scoping extensions which were introduced in the SDO 2.1 specification",
	        
	        "Retrieving the default HelperContext scope for types for the next sample run as we saw in a previous sample"
	    */
	        
	    HelperContext scope = HelperProvider.getDefaultContext();
	    return scope;
	  }
	
	public List<Type> loadTypesFromXMLSchemaFile(HelperContext scope, InputStream is, String schemaURL) 
	{
		
		List<Type> types = null;
		XSDHelper xsdHelper = scope.getXSDHelper();

		try {
			types = xsdHelper.define(is, schemaURL);
		} catch (Exception e) {
			somethingUnexpectedHasHappened(e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				somethingUnexpectedHasHappened(e);
			}
		}
		return types;
	}
	
	public List<Type> loadTypesFromXMLSchemaFile(HelperContext scope, String fileName) 
	{
		
		List<Type> types = null;
		XSDHelper xsdHelper = scope.getXSDHelper();

		InputStream is = null;
		try {

			URL url = new URL(fileName);
			is = url.openStream();
			types = xsdHelper.define(is, url.toString());
		} catch (Exception e) {
			somethingUnexpectedHasHappened(e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				somethingUnexpectedHasHappened(e);
			}
		}
		return types;
	}
	
	public DataObject getDataObjectFromFile(HelperContext scope, String filename)
			throws Exception {

		XMLDocument xmlDoc = getXMLDocumentFromFile(scope, filename);
		/*
				"An XMLDocument instance provides a wrapper for the root DataObject of a data graph\n"
						+ "along with other aspects of the XML nature of the document\n\n"
						+ "DataObject result = xmlDoc.getRootObject();",

				"Getting the root object from an XMLDocument as seen in previous samples"*/
		
		DataObject result = xmlDoc.getRootObject();

		return result;
	}
	
	public XMLDocument getXMLDocumentFromFile(HelperContext scope,
			String filename) throws Exception {

		XMLDocument result = null;
		InputStream is = null;

		try {
			/*
			    "The XMLHelper can be used to create an SDO XMLDocument instance from a file\n\n"+
			    "inputStream = ClassLoader.getSystemResourceAsStream(filename);\n"+
			    "result = scope.getXMLHelper().load(is);",
			
			    "Getting an XMLDocument instance from an XML file as seen in previous samples"
			 */
			//is = ClassLoader.getSystemResourceAsStream(filename);
			URL url = new URL(filename);
	        is = url.openStream();
			result = scope.getXMLHelper().load(is);

		} catch (Exception e) {
			somethingUnexpectedHasHappened(e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				somethingUnexpectedHasHappened(e);
			}
		}

		return result;
	}
	
	List<Type> typesToPrint;
	public void printTypesInSchema(List<Type> types, String targetNamespace, XSDHelper xsdHelper)
	{
		typesToPrint = new ArrayList<Type>();
		for (Type type : types) 
        {
			if (type.getName().equals("DocumentRoot"))
			{
				if ((targetNamespace==null) || (type.getURI().equals(targetNamespace)))
	        	{
					collectTypes(type, xsdHelper);
	        	}
			}
        }
		
		for (Type type : typesToPrint) 
		{
			printType(type, xsdHelper);
		}
	}
	
	private void collectTypes(Type type, XSDHelper xsdHelper)
	{
		if (!typesToPrint.contains(type))
		{
			typesToPrint.add(type);
			for (Property property : (List<Property>)type.getProperties())
	    	{
				collectTypes(property.getType(), xsdHelper);
	    	}
		}
	}
	
	/*
	 * type.isSequenced()==trye => the Type specifies Sequenced DataObjects.
	 * 
	 * XML elements with complex types are mapped to containment properties.
	 */
	
	private void printType(Type type, XSDHelper xsdHelper)
	{
			
		System.out.println("\n\ntype.getName = " + type.getName());
    	System.out.println("     getURI = " + type.getURI());
    	System.out.println("     isDataType = " + type.isDataType());
    	System.out.println("     isAbstract = " + type.isAbstract());
    	
    	System.out.println("     isOpen = " + type.isOpen());
    	System.out.println("     isSequenced = " + type.isSequenced());
    	System.out.println("     Properties of this type begin ");
    	for (Property property : (List<Property>)type.getProperties())
    	{ 
    		System.out.println("       property.getName: " + property.getName());
    		System.out.println("           getContainingType().getName: " + property.getContainingType().getName());
    		System.out.println("           isContainment: " + property.isContainment());
    		System.out.println("           property.isMany(): " + property.isMany());
    		System.out.println("           minOccurs: " + SDOUtil.getLowerBound(property));
    		System.out.println("           maxOccurs: " + SDOUtil.getUpperBound(property));
    		System.out.println("           getType().getName: " + property.getType().getName());
    		System.out.println("           getType().isDataType: " + property.getType().isDataType());
    		System.out.println("           getType().isOpen: " + property.getType().isOpen());
    		System.out.println("           getType().getProperties().size() = " + property.getType().getProperties().size()); 
    		if (xsdHelper.isAttribute(property))
    		{
    			System.out.println("           This is an attribute.");
    		}
    		else
    		{
    			System.out.println("           This is an element.");
    		}
    	}
    	System.out.println("     Properties of this type end. ");
    	if (type.getProperties().size()==0)
    	{
    		System.out.println("No properties in type.");
    	}
	}
	
	protected XMLDocument getXMLDocumentFromString(HelperContext scope, String xmlDoc) throws IOException {
	    XMLDocument result = null;
	    InputStream is = null;

	      /*
	          "The XMLHelper can be used to create an SDO XMLDocument instance from an\n\n"+
	          "inputStream = new ByteArrayInputStream(xmlDoc.getBytes());\n"+
	          "result = scope.getXMLHelper().load(is);",
	      
	          "Getting an XMLDocument instance from an XML file as seen in previous samples"
	      */
	      
	      is = new ByteArrayInputStream(xmlDoc.getBytes());
	      result = scope.getXMLHelper().load(is);
	      
	      return result;
	  }
	
	protected void saveXMLDocumentToFile(HelperContext scope, String fileName, XMLDocument xmlDocument) 
	{
		try
		{
			XSDHelper xsdHelper = scope.getXSDHelper();
			FileOutputStream fos = new FileOutputStream("c:/temp/domsgui/dcEx1.xml");
			scope.getXMLHelper().save(xmlDocument, fos, null);
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
}
