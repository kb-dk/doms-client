package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.objects.DOMSContentModel;
import dk.statsbiblioteket.doms.client.owl.OWLClass;
import dk.statsbiblioteket.doms.client.owl.OWLObjectProperties;
import dk.statsbiblioteket.doms.client.owl.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.owl.OWLRestriction;
import dk.statsbiblioteket.doms.client.util.Constants;
import dk.statsbiblioteket.doms.client.util.DOMSXMLUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DOMSDataStreamOntology extends DOMSDataStream {

	private String rdfBase = null;
	private OWLObjectProperties owlObjectProperties = new OWLObjectProperties();
	private OWLClass owlClass;
	
	public DOMSDataStreamOntology()
	{
		setId(Constants.ONTOLOGY);
	}
	
	public DOMSDataStreamOntology(DOMSContentModel myDomsContentModel)
	{
		super(myDomsContentModel);
		setId(Constants.ONTOLOGY);
	}

    /**
	 * @param rdfBase the rdfBase to set
	 */
	public void setRdfBase(String rdfBase) {
		this.rdfBase = rdfBase;
	}

	/**
	 * @return the rdfBase
	 */
	public String getRdfBase() {
		return rdfBase;
	}

	/**
	 * @param owlObjectProperties the owlObjectProperties to set
	 */
	public void setOwlObjectProperties(OWLObjectProperties owlObjectProperties) {
		this.owlObjectProperties = owlObjectProperties;
	}

	/**
	 * @return the owlObjectProperties
	 */
	public OWLObjectProperties getOwlObjectProperties() {
		return owlObjectProperties;
	}

	/**
	 * @param owlClass the owlClass to set
	 */
	public void setOwlClass(OWLClass owlClass) {
		this.owlClass = owlClass;
	}

	/**
	 * @return the owlClass
	 */
	public OWLClass getOwlClass() {
		return owlClass;
	}
	
	public OWLObjectProperty getOWLObjectProperty(String id)
	{
		OWLObjectProperty owlObjectProperty = null;
		for (int i = 0; i < owlObjectProperties.size(); i++) {
			OWLObjectProperty temp = owlObjectProperties.get(i);
			if ((temp.getMappingId()!=null) && (temp.getMappingId().equals(id)))
			{
				return temp;
			}
		}
		
		return owlObjectProperty;
	}
	
	public void addOWLObjectPropertiesDoNotOverwrite(DOMSDataStreamOntology parentOntologyDs)
	{
		for (int i = 0; i < parentOntologyDs.getOwlObjectProperties().size(); i++) {
			OWLObjectProperty temp = parentOntologyDs.getOwlObjectProperties().get(i);
			addOWLObjectPropertyDoNotOverwrite(parentOntologyDs, temp);
		}
		
	}
	
	private void addOWLObjectPropertyDoNotOverwrite(DOMSDataStreamOntology parentOntologyDs, OWLObjectProperty parentOWLProperty)
	{
		for (int i = 0; i < owlObjectProperties.size(); i++) {
			OWLObjectProperty temp = owlObjectProperties.get(i);
			if ((temp.getMappingId()==null) || (temp.getMappingId().equals(parentOWLProperty.getMappingId())))
			{
				assignParentRestrictions(parentOntologyDs, temp, parentOWLProperty);
				return;
			}
		}
		owlObjectProperties.add(parentOWLProperty);
	}
	
	public void parseRdf(Node node)
	{
		if (DOMSXMLUtils.doesNodeMatch(node, "RDF", Constants.RDF_NAMESPACE))
		{
			NamedNodeMap attrs = node.getAttributes();
			if (attrs != null) { 
				Node attr;
				attr = attrs.getNamedItem("xml:base");
				if (attr != null) {
					setRdfBase(attr.getNodeValue());
				}
			}
			
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				
				switch (childNode.getNodeType()) {
				case Node.ELEMENT_NODE:
					if (DOMSXMLUtils.doesNodeMatch(childNode, "Class", Constants.OWL_NAMESPACE))
					{
						setOwlClass(new OWLClass());
						getOwlClass().parse(childNode);
					}
					else if (DOMSXMLUtils.doesNodeMatch(childNode, "ObjectProperty", Constants.OWL_NAMESPACE))
					{
						OWLObjectProperty objProp = new OWLObjectProperty();
						objProp.setMyDataStream(this);
						objProp.parse(childNode);
						owlObjectProperties.add(objProp);
					}
				}
			}
			//Match object properties with restrictions
			for (int i = 0; i < owlObjectProperties.size(); i++) {
				OWLObjectProperty objProp = owlObjectProperties.get(i);
				assignRestrictions(objProp);
			}
		}
	}

	private void assignRestrictions(OWLObjectProperty objProp)
	{
		if (owlClass!=null)
		{
			for (int i = 0; i < owlClass.getOwlRestrictions().size(); i++) {
				OWLRestriction restriction = owlClass.getOwlRestrictions().get(i);
				String tempId = objProp.getMappingId();
				String tempOnProperty = restriction.getOnProperty();
				if (getRdfBase()!=null)
				{
					tempOnProperty = getRdfBase() + tempOnProperty;
                    //TODO hackfix
                    tempOnProperty = tempOnProperty.replace("##","#");
				}
				if ((tempId!=null) && (tempId.equals(tempOnProperty)))
				{
					objProp.getOwlRestrictions().add(restriction);
				}
			}
		}
	}
	
	private void assignParentRestrictions(DOMSDataStreamOntology parentOntologyDs, OWLObjectProperty owlProperty, OWLObjectProperty parentOWLProperty)
	{
		if (parentOWLProperty.getOwlRestrictions()!=null)
		{
			/*System.out.println("1. parentOWLProperty.getOwlRestrictions().size() = " + parentOWLProperty.getOwlRestrictions().size());
			System.out.println("this.getMyDomsContentModel().getPid()" + this.getMyDomsContentModel().getPid());
			System.out.println("parentOntologyDs.getMyDomsContentModel().getPid() = " + parentOntologyDs.getMyDomsContentModel().getPid());
			*/
			for (int i = 0; i < parentOWLProperty.getOwlRestrictions().size(); i++) {
				//System.out.println("2. parentOWLProperty.getOwlRestrictions().size() = " + parentOWLProperty.getOwlRestrictions().size());
				OWLRestriction parentRestriction = parentOWLProperty.getOwlRestrictions().get(i);
				String owlPropertyId = owlProperty.getMappingId();
				String tempOnProperty = parentRestriction.getOnProperty();
				if (parentOntologyDs.getRdfBase()!=null)
				{
					tempOnProperty = parentOntologyDs.getRdfBase() + tempOnProperty;
				}
				if ((owlPropertyId!=null) && (owlPropertyId.equals(tempOnProperty)))
				{
					//owlProperty.getOwlRestrictions().add(parentRestriction);
					//System.out.println("Adding parent restriction: " + parentRestriction.toString());
				}
			}
		}
	}
}
