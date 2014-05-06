package dk.statsbiblioteket.doms.client.impl.ontology;


import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.doms.client.utils.DOMSXMLUtils;
import dk.statsbiblioteket.util.xml.DOM;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParsedOwlOntologyImpl implements ParsedOwlOntology {

    private String rdfBase = null;
    private List<OWLObjectProperty> owlObjectProperties = new ArrayList<OWLObjectProperty>();
    private OWLClass owlClass;

    public ParsedOwlOntologyImpl(Datastream ontologyStream) throws ServerOperationFailed {
        Document ontologyDOM = DOM.stringToDOM(ontologyStream.getContents(), true);
        parseRdf(ontologyDOM.getFirstChild());
    }

    /**
     * @return the rdfBase
     */
    @Override
    public String getRdfBase() {
        return rdfBase;
    }

    /**
     * @param rdfBase the rdfBase to set
     */
    public void setRdfBase(String rdfBase) {
        this.rdfBase = rdfBase;
    }

    /**
     * @return the owlObjectProperties
     */
    @Override
    public List<OWLObjectProperty> getOwlObjectProperties() {
        return Collections.unmodifiableList(owlObjectProperties);
    }


    /**
     * @return the owlClass
     */
    @Override
    public OWLClass getOwlClass() {
        return owlClass;
    }

    @Override
    public OWLObjectProperty getOWLObjectProperty(String id) {
        OWLObjectProperty owlObjectProperty = null;
        for (int i = 0; i < owlObjectProperties.size(); i++) {
            OWLObjectProperty temp = owlObjectProperties.get(i);
            if ((temp.getMappingId() != null) && (temp.getMappingId().equals(id))) {
                return temp;
            }
        }

        return owlObjectProperty;
    }

    private void parseRdf(Node node) {
        if (DOMSXMLUtils.doesNodeMatch(node, "RDF", Constants.RDF_NAMESPACE)) {
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
                        if (DOMSXMLUtils.doesNodeMatch(childNode, "Class", Constants.OWL_NAMESPACE)) {
                            owlClass = new OWLClassImpl(childNode);
                        } else if (DOMSXMLUtils.doesNodeMatch(childNode, "ObjectProperty", Constants.OWL_NAMESPACE)) {
                            OWLObjectProperty objProp = new OWLObjectPropertyImpl(this, childNode);
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

    private void assignRestrictions(OWLObjectProperty objProp) {
        if (owlClass != null) {
            for (int i = 0; i < owlClass.getOwlRestrictions().size(); i++) {
                OWLRestriction restriction = owlClass.getOwlRestrictions().get(i);
                String tempId = objProp.getMappingId();
                String tempOnProperty = restriction.getOnProperty();
                if (getRdfBase() != null) {
                    tempOnProperty = getRdfBase() + tempOnProperty;
                    //TODO hackfix
                    tempOnProperty = tempOnProperty.replace("##", "#");
                }
                if ((tempId != null) && (tempId.equals(tempOnProperty))) {
                    objProp.addOwlRestriction(restriction);
                }
            }
        }
    }

}
